package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.*;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.dto.AcaoInscricaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EncerrarOfertaRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EstatisticasOfertaResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.InscricaoDTO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementacao do servico do Professor contendo toda a logica de negocio.
 */
@Service
@RequiredArgsConstructor
public class ProfessorServiceImpl implements IProfessorService {

    private final InscricaoDAO inscricaoDAO;
    private final OfertaDAO ofertaDAO;
    private final UsuarioDAO usuarioDAO;

    @Override
    @Transactional(readOnly = true)
    public List<InscricaoDTO> listarAlunosVinculados(String username) {
        Usuario professor = buscarProfessor(username);
        // Busca todas as inscrições onde este professor é o supervisor logado
        List<Inscricao> inscricoes = inscricaoDAO.findByProfessorVinculado(professor.getId());
        return inscricoes.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void aprovarPlano(AcaoInscricaoRequestDTO request, String username) {
        Inscricao inscricao = buscarInscricao(request.getInscricaoId());
        Usuario professor = buscarProfessor(username);

        // 1. Verifica se a oferta ainda está ativa e se o professor logado é o supervisor do aluno
        validarOfertaNaoEncerrada(inscricao.getOferta());
        validarSupervisor(inscricao, professor);
        
        // 2. Garante que o plano está na fase correta para ser aprovado
        validarStatus(inscricao, StatusAluno.PLANO_ENVIADO, "O plano precisa estar com status PLANO_ENVIADO.");

        // 3. Salva o parecer e avança o status do aluno para a próxima etapa
        inscricao.setParecerPlano(request.getParecer());
        inscricao.setStatus(StatusAluno.PLANO_APROVADO);
        inscricao.setDataAprovacaoPlano(LocalDateTime.now());
        inscricaoDAO.save(inscricao);
    }

    @Override
    @Transactional
    public void aprovarRelatorio(AcaoInscricaoRequestDTO request, String username) {
        Inscricao inscricao = buscarInscricao(request.getInscricaoId());
        Usuario professor = buscarProfessor(username);

        // 1. Validações de segurança e regras de negócio da oferta e do supervisor
        validarOfertaNaoEncerrada(inscricao.getOferta());
        validarSupervisor(inscricao, professor);
        validarStatus(inscricao, StatusAluno.RELATORIO_ENVIADO, "O relatório precisa estar com status RELATORIO_ENVIADO.");
        validarNotaEFrequencia(request);

        // 2. Salva o parecer, a nota preliminar e avança para a aprovação do responsável
        inscricao.setParecerRelatorioSupervisor(request.getParecer());
        inscricao.setFrequenciaSupervisor(request.getFrequencia());
        inscricao.setNotaSupervisor(request.getNota());
        inscricao.setStatus(StatusAluno.RELATORIO_APROVADO_SUPERVISOR);
        inscricaoDAO.save(inscricao);
    }

    @Override
    @Transactional
    public void concluirRelatorio(AcaoInscricaoRequestDTO request, String username) {
        Inscricao inscricao = buscarInscricao(request.getInscricaoId());
        Usuario professor = buscarProfessor(username);

        // 1. O responsável verifica se a oferta está ativa e se o relatório já passou pelo supervisor
        validarOfertaNaoEncerrada(inscricao.getOferta());
        validarResponsavel(inscricao, professor);
        validarStatus(inscricao, StatusAluno.RELATORIO_APROVADO_SUPERVISOR,
                "O relatório precisa estar com status RELATORIO_APROVADO_SUPERVISOR.");
        validarNotaEFrequencia(request);

        // 2. Consolida a nota e frequência finais e conclui a inscrição do aluno
        inscricao.setParecerRelatorioResponsavel(request.getParecer());
        inscricao.setFrequenciaResponsavel(request.getFrequencia());
        inscricao.setNotaResponsavel(request.getNota());
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoDAO.save(inscricao);
    }

    @Override
    @Transactional
    public void avaliarDocumentacao(AcaoInscricaoRequestDTO request, String username) {
        Inscricao inscricao = buscarInscricao(request.getInscricaoId());
        Usuario professor = buscarProfessor(username);

        // 1. Garante que é o professor responsável que está avaliando a documentação
        validarOfertaNaoEncerrada(inscricao.getOferta());
        validarResponsavel(inscricao, professor);
        validarStatus(inscricao, StatusAluno.DOCUMENTACAO_ENVIADA,
                "A documentação precisa estar com status DOCUMENTACAO_ENVIADA.");
        validarNotaEFrequencia(request);

        // 2. Aprova a dispensa e conclui a inscrição do aluno
        inscricao.setParecerRelatorioResponsavel(request.getParecer());
        inscricao.setFrequenciaResponsavel(request.getFrequencia());
        inscricao.setNotaResponsavel(request.getNota());
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoDAO.save(inscricao);
    }

    @Override
    @Transactional(readOnly = true)
    public EstatisticasOfertaResponseDTO buscarEstatisticasOferta(Long ofertaId, String username) {
        Oferta oferta = ofertaDAO.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
        Usuario professor = buscarProfessor(username);

        // 1. Verificações rígidas de estado da oferta para evitar encerramento duplicado
        if (!oferta.getProfessorResponsavel().getId().equals(professor.getId())) {
            throw new IllegalStateException("Apenas o professor responsável pode encerrar a oferta.");
        }
        if (oferta.isEncerradaSecretario()) {
            throw new IllegalStateException("Esta oferta já foi encerrada pelo secretário.");
        }
        if (oferta.isConcluidaProfessor()) {
            throw new IllegalStateException("Esta oferta já foi concluída pelo professor.");
        }

        // 2. Regra de Negócio: A oferta só pode ser encerrada se TODOS os alunos já estiverem concluídos
        List<Inscricao> inscricoes = inscricaoDAO.findByOfertaId(ofertaId);
        boolean todosConcluidos = inscricoes.stream()
                .allMatch(i -> i.getStatus() == StatusAluno.CONCLUIDO_RESPONSAVEL);
        if (!todosConcluidos) {
            throw new IllegalStateException("Nem todos os alunos estão com status CONCLUIDO_RESPONSAVEL.");
        }

        // 3. Processamento das estatísticas (Média de frequência e contagem de cada nota)
        double mediaFrequencia = inscricoes.stream()
                .filter(i -> i.getFrequenciaResponsavel() != null)
                .mapToInt(Inscricao::getFrequenciaResponsavel)
                .average().orElse(0.0);

        Map<String, Long> contagemNotas = new HashMap<>();
        for (String nota : List.of("A", "B", "C", "D", "E")) {
            long count = inscricoes.stream()
                    .filter(i -> nota.equals(i.getNotaResponsavel()))
                    .count();
            contagemNotas.put(nota, count);
        }

        long viaEstagio = inscricoes.stream().filter(i -> i.getPlanoTrabalho() != null).count();
        long viaDocumentacao = inscricoes.stream().filter(i -> i.getDocumentacaoComprobatoria() != null).count();

        // 4. Monta e retorna o DTO consolidado para a interface visualizar
        EstatisticasOfertaResponseDTO dto = new EstatisticasOfertaResponseDTO();
        dto.setOfertaId(oferta.getId());
        dto.setNomeOferta(oferta.getNome());
        dto.setSemestre(oferta.getSemestre());
        dto.setTotalAlunos(inscricoes.size());
        dto.setMediaFrequencia(mediaFrequencia);
        dto.setCreditosViaEstagio(viaEstagio);
        dto.setCreditosViaDocumentacao(viaDocumentacao);
        dto.setContagemNotas(contagemNotas);
        return dto;
    }

    @Override
    @Transactional
    public void encerrarOferta(EncerrarOfertaRequestDTO request, String username) {
        Oferta oferta = ofertaDAO.findById(request.getOfertaId())
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
        Usuario professor = buscarProfessor(username);

        // 1. Verificações de acesso e estado da oferta
        if (!oferta.getProfessorResponsavel().getId().equals(professor.getId())) {
            throw new IllegalStateException("Apenas o professor responsável pode encerrar a oferta.");
        }
        if (oferta.isEncerradaSecretario() || oferta.isConcluidaProfessor()) {
            throw new IllegalStateException("Esta oferta já foi encerrada ou concluída.");
        }

        // 2. Revalidação de segurança: garantir que nenhum aluno ficou pendente
        List<Inscricao> inscricoes = inscricaoDAO.findByOfertaId(request.getOfertaId());
        boolean todosConcluidos = inscricoes.stream()
                .allMatch(i -> i.getStatus() == StatusAluno.CONCLUIDO_RESPONSAVEL);
        if (!todosConcluidos) {
            throw new IllegalStateException("Nem todos os alunos estão com status CONCLUIDO_RESPONSAVEL.");
        }

        // 3. Salva a avaliação final da turma e marca como concluída pelo professor
        oferta.setLicoesAprendidas(request.getDescricaoLicoesAprendidas());
        oferta.setConcluidaProfessor(true);
        oferta.setDataConcluidaProfessor(LocalDateTime.now());
        ofertaDAO.save(oferta);
    }

    private Usuario buscarProfessor(String username) {
        return usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado: " + username));
    }

    private Inscricao buscarInscricao(Long inscricaoId) {
        return inscricaoDAO.findById(inscricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada: " + inscricaoId));
    }

    private void validarOfertaNaoEncerrada(Oferta oferta) {
        if (oferta.isEncerradaSecretario()) {
            throw new IllegalStateException("Operação negada: a oferta já foi encerrada pelo secretário.");
        }
    }

    private void validarSupervisor(Inscricao inscricao, Usuario professor) {
        if (inscricao.getPlanoTrabalho() == null ||
                !inscricao.getPlanoTrabalho().getProfessorSupervisor().getId().equals(professor.getId())) {
            throw new IllegalStateException("Acesso negado: você não é o supervisor deste aluno.");
        }
    }

    private void validarResponsavel(Inscricao inscricao, Usuario professor) {
        if (!inscricao.getOferta().getProfessorResponsavel().getId().equals(professor.getId())) {
            throw new IllegalStateException("Acesso negado: você não é o professor responsável desta oferta.");
        }
    }

    private void validarStatus(Inscricao inscricao, StatusAluno esperado, String mensagem) {
        if (inscricao.getStatus() != esperado) {
            throw new IllegalStateException("Status inválido. " + mensagem
                    + " Status atual: " + inscricao.getStatus().getDescricao());
        }
    }

    private void validarNotaEFrequencia(AcaoInscricaoRequestDTO request) {
        if (request.getFrequencia() == null) {
            throw new IllegalArgumentException("A frequência é obrigatória para esta operação.");
        }
        if (request.getNota() == null || request.getNota().isBlank()) {
            throw new IllegalArgumentException("A nota é obrigatória para esta operação.");
        }
        if (!List.of("A", "B", "C", "D", "E").contains(request.getNota().toUpperCase())) {
            throw new IllegalArgumentException("Nota inválida. Valores aceitos: A, B, C, D ou E.");
        }
    }

    // Converte a entidade de Inscricao para o DTO consolidado plano
    private InscricaoDTO toDTO(Inscricao i) {
        InscricaoDTO dto = new InscricaoDTO();
        dto.setInscricaoId(i.getId());
        dto.setStatus(i.getStatus().getDescricao());

        dto.setNomeAluno(i.getAluno().getNome());
        dto.setEmailAluno(i.getAluno().getUsername());

        dto.setOfertaId(i.getOferta().getId());
        dto.setNomeOferta(i.getOferta().getNome());
        dto.setSemestre(i.getOferta().getSemestre());
        dto.setProfessorResponsavelNome(i.getOferta().getProfessorResponsavel().getNome());

        if (i.getPlanoTrabalho() != null) {
            PlanoTrabalho p = i.getPlanoTrabalho();
            dto.setCodigoDisciplina(p.getCodigoDisciplina());
            dto.setNomeDisciplina(p.getNomeDisciplina());
            dto.setCursoDisciplina(p.getCursoDisciplina());
            dto.setProfessorSupervisorNome(p.getProfessorSupervisor().getNome());
            dto.setArquivoPlanoPath(p.getArquivoPdfPath());
            dto.setDataEnvioPlano(i.getDataEnvioPlano());
        }
        dto.setParecerPlano(i.getParecerPlano());
        dto.setDataAprovacaoPlano(i.getDataAprovacaoPlano());

        if (i.getRelatorioFinal() != null) {
            RelatorioFinal r = i.getRelatorioFinal();
            dto.setFrequenciaRelatorio(r.getFrequencia());
            dto.setArquivoRelatorioPath(r.getArquivoPdfPath());
            dto.setDataEnvioRelatorio(r.getDataEnvio());
        }

        dto.setParecerRelatorioSupervisor(i.getParecerRelatorioSupervisor());
        dto.setFrequenciaSupervisor(i.getFrequenciaSupervisor());
        dto.setNotaSupervisor(i.getNotaSupervisor());

        dto.setParecerRelatorioResponsavel(i.getParecerRelatorioResponsavel());
        dto.setFrequenciaResponsavel(i.getFrequenciaResponsavel());
        dto.setNotaResponsavel(i.getNotaResponsavel());

        if (i.getDocumentacaoComprobatoria() != null) {
            DocumentacaoComprobatoria d = i.getDocumentacaoComprobatoria();
            dto.setInstituicao(d.getInstituicao());
            dto.setNomeDisciplinaDocumentacao(d.getNomeDisciplina());
            dto.setCursoDisciplinaDocumentacao(d.getCursoDisciplina());
            dto.setCargaHoraria(d.getCargaHoraria());
            dto.setArquivoDocumentacaoPath(d.getArquivoPdfPath());
            dto.setDataEnvioDocumentacao(d.getDataEnvio());
        }

        return dto;
    }
}
