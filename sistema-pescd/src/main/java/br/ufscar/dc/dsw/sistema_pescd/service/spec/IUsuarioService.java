// É a Interface que define o contrato para os serviços de Usuário.
// Ela dita as operações que o sistema pode realizar para buscar e gerenciar perfis.
package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import java.util.List;

public interface IUsuarioService {
    // Contrato para salvar ou atualizar um usuário no banco de dados
    void salvar(Usuario usuario);

    // Contrato para remover um usuário do sistema através do seu ID
    void excluir(Long id);

    // Contrato para buscar um usuário específico pelo ID
    Usuario buscarPorId(Long id);

    // Contrato para listar todos os usuários cadastrados
    List<Usuario> buscarTodos();

    // Contrato para buscar funcionários do sistema (Secretários e Professores)
    List<Usuario> buscarSecretariosEProfessores();

    // Contrato para o método da S.01 que filtra e traz apenas os Professores do sistema
    List<Usuario> buscarProfessores();

    Usuario buscarPorUsername(String username);
}


