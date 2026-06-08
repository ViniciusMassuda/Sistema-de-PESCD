package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.dao.*;
import br.ufscar.dc.dsw.sistema_pescd.domain.*;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.*;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.*;
import br.ufscar.dc.dsw.sistema_pescd.mapper.*;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlunoServiceImpl implements IAlunoService {
    @Autowired private InscricaoDAO inscricaoDAO;
    @Autowired private OfertaDAO ofertaDAO;
    @Autowired private UsuarioDAO usuarioDAO;
    @Autowired private PlanoTrabalhoDAO planoTrabalhoDAO;
    @Autowired private DocumentacaoDAO documentacaoDAO;
    @Autowired private DocumentacaoMapper documentacaoMapper;
    @Autowired private OfertaMapper ofertaMapper;
    @Autowired private PlanoTrabalhoMapper planoTrabalhoMapper;
    @Autowired private RelatorioDAO relatorioDAO;
    @Autowired private RelatorioMapper relatorioMapper;
    @Value("${upload.path}") private String uploadPath;

    @Override
    public List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno) {
        return inscricaoDAO.findByAlunoId(aluno.getId()).stream()
                .map(i -> ofertaMapper.toDto(i.getOferta(), calcularStatus(i.getOferta()), i))
                .collect(Collectors.toList());
    }

    @Override
    public PlanoTrabalhoResponseDTO enviarPlanoTrabalho(Long oid, Usuario al, PlanoTrabalhoRequestDTO req) {
        Oferta oferta = ofertaDAO.findById(oid).orElseThrow();
        // rng-5: impede envio em oferta concluida
        if (oferta.isEncerradaSecretario()) throw new RuntimeException("oferta ja concluida. apenas leitura permitida.");

        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, oferta).orElseThrow();
        if (i.getStatus() != StatusAluno.NAO_ENVIADO) throw new RuntimeException("plano ja enviado anteriormente.");
        
        Usuario supervisor = usuarioDAO.findById(req.getProfessorSupervisorId()).orElseThrow();

        MultipartFile arquivo = req.getArquivo();
        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("o arquivo pdf deve ter no maximo 5mb.");
        }

        String path = salvar(req.getArquivo(), al.getId(), oid, "planos");
        PlanoTrabalho p = planoTrabalhoMapper.toEntity(req, supervisor, path);
        p = planoTrabalhoDAO.save(p);
        i.setPlanoTrabalho(p);
        i.setStatus(StatusAluno.PLANO_ENVIADO);
        i.setDataEnvioPlano(LocalDateTime.now());
        inscricaoDAO.save(i);

        return planoTrabalhoMapper.toResponseDTO(p, "plano enviado com sucesso.");
    }

    @Override
    public boolean podeEnviarPlano(Long oid, Usuario al) {
        Oferta o = ofertaDAO.findById(oid).orElse(null);
        if (o == null || o.isEncerradaSecretario()) return false; // rng-5
        
        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, o).orElse(null);
        return i != null && i.getStatus() == StatusAluno.NAO_ENVIADO;
    }

    @Override
    public DocumentacaoResponseDTO enviarDocumentacao(Long oid, Usuario al, DocumentacaoRequestDTO req) {
        Oferta oferta = ofertaDAO.findById(oid).orElseThrow();
        // rng-5: trava para oferta encerrada
        if (oferta.isEncerradaSecretario()) throw new RuntimeException("oferta encerrada.");

        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, oferta).orElseThrow();

        MultipartFile arquivo = req.getArquivo();
        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("o arquivo pdf deve ter no maximo 5mb.");
        }

        String path = salvar(req.getArquivo(), al.getId(), oid, "documentacoes");
        DocumentacaoComprobatoria d = documentacaoMapper.toEntity(req, path);
        d = documentacaoDAO.save(d);
        i.setDocumentacaoComprobatoria(d);
        i.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoDAO.save(i);
        return documentacaoMapper.toResponseDTO(d, "documentacao enviada.");
    }

    @Override
    public boolean podeEnviarDocumentacao(Long oid, Usuario al) {
        Oferta o = ofertaDAO.findById(oid).orElse(null);
        if (o == null || o.isEncerradaSecretario()) return false; // rng-5
        
        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, o).orElse(null);
        return i != null && i.getStatus() == StatusAluno.NAO_ENVIADO;
    }

    @Override
    public RelatorioResponseDTO enviarRelatorio(Long oid, Usuario al, RelatorioRequestDTO req) {
        Oferta oferta = ofertaDAO.findById(oid).orElseThrow();
        // rng-5: impede alteracao de dados
        if (oferta.isEncerradaSecretario()) throw new RuntimeException("oferta concluida.");

        Inscricao i = inscricaoDAO.findByAlunoAndOferta(al, oferta).orElseThrow();

        MultipartFile arquivo = req.getArquivo();
        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("o arquivo pdf deve ter no maximo 5mb.");
        }

        String path = salvar(req.getArquivo(), al.getId(), oid, "relatorios");
        RelatorioFinal r = relatorioMapper.toEntity(req, path);
        r = relatorioDAO.save(r);
        i.setRelatorioFinal(r);
        i.setStatus(StatusAluno.RELATORIO_ENVIADO);
        inscricaoDAO.save(i);
        return relatorioMapper.toResponseDTO(r, "relatorio enviado.");
    }

    @Override
    public boolean podeEnviarRelatorio(Long oid, Usuario al) {
        Oferta o = ofertaDAO.findById(oid).orElse(null);
        if (o == null || o.isEncerradaSecretario()) return false; // rng-5
        
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
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    private String calcularStatus(Oferta o) {
        LocalDate h = LocalDate.now();
        if (h.isBefore(o.getDataInicio())) return "Nao iniciada";
        if (h.isAfter(o.getDataFim())) return "Atrasada";
        return "Em andamento";
    }
}
