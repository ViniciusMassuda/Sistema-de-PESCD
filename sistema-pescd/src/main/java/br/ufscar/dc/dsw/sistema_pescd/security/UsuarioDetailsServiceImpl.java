package br.ufscar.dc.dsw.sistema_pescd.security;

import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioDAO dao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Tentando logar usuario: " + username);
        Usuario usuario = dao.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario nao encontrado no banco: " + username);
                    return new UsernameNotFoundException("Usuario nao encontrado: " + username);
                });
        
        System.out.println("Usuario encontrado: " + usuario.getUsername() + " com papel: " + usuario.getRole());
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()))
        );
    }
}
