package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
    // Busca um usuário no banco através do e-mail (username) para validar login ou cadastro
    Optional<Usuario> findByUsername(String username);
    // Filtra e traz uma lista de usuários com base nos seus papéis (usado para listar os professores)
    List<Usuario> findByRoleIn(List<Usuario.Role> roles);
    List<Usuario> findByRole(Usuario.Role role);
}


