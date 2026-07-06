// Realiza as operacoes com os usuarios no banco de dados.

package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioDAO dao;
    private final PasswordEncoder encoder;

    // Filtra a tabela de usuários e retorna apenas quem possui o perfil de Professor.
    // É usado pelo SecretarioController para preencher o <select> do formulário de cadastro.
    @Override
    public List<Usuario> buscarProfessores() {
        return dao.findByRoleIn(
                List.of(Usuario.Role.PROFESSOR));
    }

    // OPERAÇÕES PADRÕES DE MANUTENÇÃO (CRUD)

    public void salvar(Usuario usuario) {
        dao.save(usuario);
    }

    public void excluir(Long id) {
        dao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarSecretariosEProfessores() {
        return dao.findByRoleIn(Arrays.asList(Usuario.Role.SECRETARIO, Usuario.Role.PROFESSOR));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return dao.findByUsername(username).orElse(null);
    }
}
