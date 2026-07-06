// Gerencia as inscricoes dos alunos em ofertas.
// Conta a quantidade de alunos matriculados.

package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IInscricaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InscricaoService implements IInscricaoService {

    private final InscricaoDAO dao;
    private final OfertaDAO ofertaDAO;
    private final UsuarioDAO usuarioDAO;

    @Override
    public long contarPorOferta(Oferta oferta) {
        // Conta quantos alunos estao na oferta selecionada.
        return dao.countByOferta(oferta);
    }

    // ADICIONADO PARA A S.03: Busca todas as inscrições de uma determinada oferta
    @Override
    public List<Inscricao> buscarPorOferta(Oferta oferta) {
        return dao.findByOferta(oferta);
    }

    @Override
    public List<Inscricao> buscarPorProfessorVinculado(Long professorId) {
        return dao.findByProfessorVinculado(professorId);
    }

    @Override
    public Inscricao buscarPorId(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        dao.deleteById(id);
    }

    // Refatorado: Lógica de negócio da matrícula manual encapsulada sob transação
    @Override
    @Transactional
    public void matricularAlunoManual(Long ofertaId, String nome, String email, String senha) {
        Oferta oferta = ofertaDAO.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));

        // RN-2: Verifica se o usuário correspondente ao e-mail já existe no banco
        Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

        // Cria o aluno com perfil padrão caso ele ainda não exista no banco
        if (aluno == null) {
            // RN-1: Se não existir, realiza o cadastro prévio com perfil ALUNO
            aluno = new Usuario();
            aluno.setNome(nome);
            aluno.setUsername(email);
            aluno.setPassword(senha);
            aluno.setRole(Usuario.Role.ALUNO);
            usuarioDAO.save(aluno);
        }

        // RN-2: Evita duplicidade de matrícula do mesmo aluno na mesma oferta
        if (dao.existsByOfertaAndAluno(oferta, aluno)) {
            throw new IllegalStateException("Este aluno já está inscrito nesta oferta!");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setOferta(oferta);
        inscricao.setAluno(aluno);
        dao.save(inscricao);
    }

    // Refatorado: Processamento de stream e parseamento de CSV isolado na camada de serviço
    @Override
    @Transactional
    public void importarAlunosCsv(Long ofertaId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Por favor, selecione um arquivo CSV.");
        }

        try {
            Oferta oferta = ofertaDAO.findById(ofertaId)
                    .orElseThrow(() -> new IllegalArgumentException("Oferta não encontrada."));

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String cabecalho = reader.readLine(); // Descarta o cabeçalho descritivo do CSV

                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (linha.trim().isEmpty()) continue;

                    String[] colunas = linha.split(",");
                    String ra = colunas[0].trim();
                    String nomeCompleto = colunas[1].trim();
                    String email = colunas[2].trim();

                    // Procura pelo aluno usando o e-mail informado
                    Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

                    // Cria o aluno usando o RA como senha provisória se for um novo cadastro
                    if (aluno == null) {
                        // RN-3: Se não cadastrado, cria credenciais padrão (Username=Email, Senha=RA)
                        aluno = new Usuario();
                        aluno.setNome(nomeCompleto);
                        aluno.setUsername(email);
                        aluno.setPassword(ra);
                        aluno.setRole(Usuario.Role.ALUNO);
                        usuarioDAO.save(aluno);
                    }

                    // Inscreve o aluno na oferta caso o vínculo ainda não exista
                    if (!dao.existsByOfertaAndAluno(oferta, aluno)) {
                        Inscricao inscricao = new Inscricao();
                        inscricao.setOferta(oferta);
                        inscricao.setAluno(aluno);
                        dao.save(inscricao);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar o CSV: " + e.getMessage(), e);
        }
    }
}
