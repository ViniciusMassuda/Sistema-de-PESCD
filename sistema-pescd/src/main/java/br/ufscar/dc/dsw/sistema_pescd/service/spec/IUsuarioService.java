// Arquivo: IUsuarioService.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import java.util.List;

public interface IUsuarioService {
    void salvar(Usuario usuario);
    void excluir(Long id);
    Usuario buscarPorId(Long id);
    List<Usuario> buscarTodos();
    List<Usuario> buscarSecretariosEProfessores();
}


