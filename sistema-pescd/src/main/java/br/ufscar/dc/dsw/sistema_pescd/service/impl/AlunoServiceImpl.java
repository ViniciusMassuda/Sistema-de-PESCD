package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.PlanoTrabalhoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.domain.PlanoTrabalho;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.PlanoTrabalhoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.mapper.OfertaMapper;
import br.ufscar.dc.dsw.sistema_pescd.mapper.PlanoTrabalhoMapper;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlunoServiceImpl implements IAlunoService {

    @Autowired
    private InscricaoDAO inscricaoDAO;

    @Autowired
    private OfertaDAO ofertaDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private PlanoTrabalhoDAO planoTrabalhoDAO;

    @Autowired
    private OfertaMapper ofertaMapper;

    @Autowired
    private PlanoTrabalhoMapper planoTrabalhoMapper;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno) {
        List<Inscricao> inscricoes = inscricaoDAO.findByAlunoId(aluno.getId());

        return inscricoes.stream()
                .map(inscricao -> {
                    Oferta oferta = inscricao.getOferta();
                    String statusOferta = calcularStatusOferta(oferta);
                    return ofertaMapper.toDto(oferta, statusOferta, inscricao);
                })
                .collect(Collectors.toList());
    }

    @Override
    public PlanoTrabalhoResponseDTO enviarPlanoTrabalho(Long ofertaId, Usuario aluno,
                                                        PlanoTrabalhoRequestDTO request) {
        Oferta oferta = ofertaDAO.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta não encontrada"));

        Inscricao inscricao = inscricaoDAO.findByAlunoAndOferta(aluno, oferta)
                .orElseThrow(() -> new RuntimeException("Aluno não está inscrito nesta oferta"));

        String statusOferta = calcularStatusOferta(oferta);
        if (!"Em andamento".equals(statusOferta)) {
            throw new RuntimeException("Só é possível enviar plano para ofertas em andamento");
        }

        if (inscricao.getStatusAluno() != StatusAluno.NAO_ENVIADO) {
            throw new RuntimeException("Plano já foi enviado para esta oferta");
        }

        Usuario professorSupervisor = usuarioDAO.findById(request.getProfessorSupervisorId())
                .orElseThrow(() -> new RuntimeException("Professor supervisor não encontrado"));

        if (professorSupervisor.getRole() != Usuario.Role.PROFESSOR) {
            throw new RuntimeException("O supervisor deve ser um professor");
        }

        MultipartFile arquivo = request.getArquivo();
        if (arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo PDF é obrigatório");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("O arquivo deve ser um PDF");
        }

        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("O arquivo PDF deve ter no máximo 5MB. Tamanho atual: " +
                    (arquivo.getSize() / 1024 / 1024) + "MB");
        }

        String nomeArquivo = salvarArquivo(arquivo, aluno.getId(), ofertaId);

        PlanoTrabalho planoTrabalho = planoTrabalhoMapper.toEntity(request, professorSupervisor, nomeArquivo);
        planoTrabalho = planoTrabalhoDAO.save(planoTrabalho);

        inscricao.setPlanoTrabalho(planoTrabalho);
        inscricao.setStatusAluno(StatusAluno.PLANO_ENVIADO);
        inscricao.setDataEnvioPlano(LocalDateTime.now());
        inscricaoDAO.save(inscricao);

        return planoTrabalhoMapper.toResponseDTO(planoTrabalho, "Plano de trabalho enviado com sucesso!");
    }

    @Override
    public boolean podeEnviarPlano(Long ofertaId, Usuario aluno) {
        try {
            Oferta oferta = ofertaDAO.findById(ofertaId).orElse(null);
            if (oferta == null) return false;

            String statusOferta = calcularStatusOferta(oferta);
            if (!"Em andamento".equals(statusOferta)) return false;

            Inscricao inscricao = inscricaoDAO.findByAlunoAndOferta(aluno, oferta).orElse(null);
            if (inscricao == null) return false;

            return inscricao.getStatusAluno() == StatusAluno.NAO_ENVIADO;
        } catch (Exception e) {
            return false;
        }
    }

    private String salvarArquivo(MultipartFile arquivo, Long alunoId, Long ofertaId) {
        try {
            String uploadDir = uploadPath + "/planos/";
            Path uploadPathObj = Paths.get(uploadDir);

            if (!Files.exists(uploadPathObj)) {
                Files.createDirectories(uploadPathObj);
            }

            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            String nomeUnico = "aluno_" + alunoId + "_oferta_" + ofertaId + "_" +
                    UUID.randomUUID().toString() + extensao;

            Path filePath = uploadPathObj.resolve(nomeUnico);
            Files.write(filePath, arquivo.getBytes());

            return "/uploads/planos/" + nomeUnico;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    private String calcularStatusOferta(Oferta oferta) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = oferta.getDataInicio();
        LocalDate dataFim = oferta.getDataFim();

        if (hoje.isBefore(dataInicio)) {
            return "Não iniciada";
        } else if (hoje.isAfter(dataFim)) {
            return "Atrasada";
        } else {
            return "Em andamento";
        }
    }
}