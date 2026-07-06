package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.dao.*;
import br.ufscar.dc.dsw.sistema_pescd.domain.*;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.*;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.*;
import br.ufscar.dc.dsw.sistema_pescd.mapper.*;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import br.ufscar.dc.dsw.sistema_pescd.util.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlunoServiceImpl implements IAlunoService {
    private final InscricaoDAO inscricaoDAO;
    private final OfertaDAO ofertaDAO;
    private final UsuarioDAO usuarioDAO;
    private final PlanoTrabalhoDAO planoTrabalhoDAO;
    private final DocumentacaoDAO documentacaoDAO;
    private final DocumentacaoMapper documentacaoMapper;
    private final OfertaMapper ofertaMapper;
    private final PlanoTrabalhoMapper planoTrabalhoMapper;
    private final RelatorioDAO relatorioDAO;
    private final RelatorioMapper relatorioMapper;
    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno) {
        // Mapeia todas as ofertas do aluno e calcula o status descritivo (Em andamento, Atrasada) em tempo real
        return inscricaoDAO.findByAlunoId(aluno.getId()).stream()
                .map(i -> ofertaMapper.toDto(i.getOferta(), calcularStatus(i.getOferta()), i))
                .collect(Collectors.toList());
    }

    @Override
    public PlanoTrabalhoResponseDTO enviarPlanoTrabalho(Long oid, Usuario al, PlanoTrabalhoRequestDTO req) {
        Oferta oferta = ofertaDAO.findById(oid).orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
        
        // 1. Regra de Negócio: Não permite envio de arquivos se a oferta já foi encerrada pela secretaria
        if (oferta.isEncerradaSecretario()) {
            throw new IllegalStateException("Oferta já concluída. Apenas leitura permitida.");
        }

        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, oferta).orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));
        
        // 2. Garante que o aluno não reenvie um plano que já foi submetido anteriormente
        if (i.getStatus() != StatusAluno.NAO_ENVIADO) {
            throw new IllegalStateException("O plano já foi enviado anteriormente.");
        }
        
        Usuario supervisor = usuarioDAO.findById(req.getProfessorSupervisorId()).orElseThrow(() -> new IllegalArgumentException("Supervisor não encontrado."));

        // 3. Validação de Segurança: Blinda a aplicação checando os 'Magic Numbers' para garantir que é um PDF real
        FileValidationUtil.validarPdf(req.getArquivo());

        // 4. Salva o arquivo no disco, atualiza o status de inscrição e salva no banco de dados
        String path = salvar(req.getArquivo(), al.getId(), oid, "planos");
        PlanoTrabalho p = planoTrabalhoMapper.toEntity(req, supervisor, path);
        p = planoTrabalhoDAO.save(p);
        i.setPlanoTrabalho(p);
        i.setStatus(StatusAluno.PLANO_ENVIADO);
        i.setDataEnvioPlano(LocalDateTime.now());
        inscricaoDAO.save(i);

        return planoTrabalhoMapper.toResponseDTO(p, "Plano enviado com sucesso.");
    }

    @Override
    public boolean podeEnviarPlano(Long oid, Usuario al) {
        Oferta o = ofertaDAO.findById(oid).orElse(null);
        if (o == null || o.isEncerradaSecretario()) return false;
        
        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, o).orElse(null);
        return i != null && i.getStatus() == StatusAluno.NAO_ENVIADO;
    }

    @Override
    public DocumentacaoResponseDTO enviarDocumentacao(Long oid, Usuario al, DocumentacaoRequestDTO req) {
        Oferta oferta = ofertaDAO.findById(oid).orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
        
        // 1. Verifica se a oferta ainda está aberta para envio de documentos
        if (oferta.isEncerradaSecretario()) {
            throw new IllegalStateException("A oferta está encerrada.");
        }

        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, oferta).orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));

        // 2. Garante que o documento submetido não é um arquivo malicioso disfarçado de PDF
        FileValidationUtil.validarPdf(req.getArquivo());

        // 3. Persiste o PDF no servidor e muda o status da inscrição para indicar submissão
        String path = salvar(req.getArquivo(), al.getId(), oid, "documentacoes");
        DocumentacaoComprobatoria d = documentacaoMapper.toEntity(req, path);
        d = documentacaoDAO.save(d);
        i.setDocumentacaoComprobatoria(d);
        i.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoDAO.save(i);
        return documentacaoMapper.toResponseDTO(d, "Documentação enviada com sucesso.");
    }

    @Override
    public boolean podeEnviarDocumentacao(Long oid, Usuario al) {
        Oferta o = ofertaDAO.findById(oid).orElse(null);
        if (o == null || o.isEncerradaSecretario()) return false;
        
        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, o).orElse(null);
        return i != null && i.getStatus() == StatusAluno.NAO_ENVIADO;
    }

    @Override
    public RelatorioResponseDTO enviarRelatorio(Long oid, Usuario al, RelatorioRequestDTO req) {
        Oferta oferta = ofertaDAO.findById(oid).orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));
        
        // 1. Impede alteração de dados em ofertas que já foram arquivadas
        if (oferta.isEncerradaSecretario()) {
            throw new IllegalStateException("A oferta está concluída.");
        }

        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, oferta).orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada."));

        // 2. Protege o sistema injetando o FileValidationUtil para checar o cabeçalho do arquivo
        FileValidationUtil.validarPdf(req.getArquivo());

        // 3. Salva o relatório no disco local e informa ao banco de dados o novo status do aluno
        String path = salvar(req.getArquivo(), al.getId(), oid, "relatorios");
        RelatorioFinal r = relatorioMapper.toEntity(req, path);
        r = relatorioDAO.save(r);
        i.setRelatorioFinal(r);
        i.setStatus(StatusAluno.RELATORIO_ENVIADO);
        inscricaoDAO.save(i);
        return relatorioMapper.toResponseDTO(r, "Relatório enviado com sucesso.");
    }

    @Override
    public boolean podeEnviarRelatorio(Long oid, Usuario al) {
        Oferta o = ofertaDAO.findById(oid).orElse(null);
        if (o == null || o.isEncerradaSecretario()) return false;
        
        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, o).orElse(null);
        return i != null && i.getStatus() == StatusAluno.PLANO_APROVADO;
    }

    private String salvar(MultipartFile f, Long aid, Long oid, String sub) {
        try {
            Path p = Paths.get(uploadPath + "/" + sub + "/");
            if (!Files.exists(p)) Files.createDirectories(p);
            String name = sub + "_" + aid + "_" + oid + "_" + UUID.randomUUID() + ".pdf";
            Files.write(p.resolve(name), f.getBytes());
            return "/uploads/" + sub + "/" + name;
        } catch (IOException e) { throw new RuntimeException("Erro ao salvar o arquivo: " + e.getMessage(), e); }
    }

    private String calcularStatus(Oferta o) {
        LocalDate h = LocalDate.now();
        if (h.isBefore(o.getDataInicio())) return "Não iniciada";
        if (h.isAfter(o.getDataFim())) return "Atrasada";
        return "Em andamento";
    }
}
