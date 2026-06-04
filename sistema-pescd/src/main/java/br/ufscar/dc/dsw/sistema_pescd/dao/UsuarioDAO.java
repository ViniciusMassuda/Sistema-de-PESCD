// Arquivo: UsuarioDAO.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioDAO extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByRoleIn(List<Usuario.Role> roles);

    // NOVO: Buscar usuários por papel (role)
    List<Usuario> findByRole(Usuario.Role role);

    // NOVO: Buscar apenas professores (para o dropdown)
    @Query("SELECT u FROM Usuario u WHERE u.role = :role")
    List<Usuario> findProfessoresByRole(@Param("role") Usuario.Role role);
}


