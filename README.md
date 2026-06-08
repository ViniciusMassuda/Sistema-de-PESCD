# Sistema-de-PESCD

Salvatore:

1 - Usabilidade do sistema: O professor entra na sua lista de alunos, identifica quem enviou o plano, abre o formulário, lê o conteúdo enviado pelo aluno, insere um parecer de validação e confirma a aprovação.
O retorno do sistema: O sistema transforma o aluno de "inscrito" em "estagiário ativo", disparando a contagem do tempo de estágio até que o mesmo seja dado como encerrado em outra instancia.


2 - Usabilidade do sistema:O professor visualiza o relatório que o aluno submeteu, compara com o plano aprovado anteriormente, insere os dados de desempenho (escolhendo uma nota de A até E e a frequência) e finaliza sua etapa de supervisão.
O retorno do sistema: Garante o envio dos dados e submete uma requisição para a finalização da etapa no sistema.


3 - Usabilidade do sistema:O professor acessa a ficha técnica, valida a nota sugerida pelo supervisor ou faz uma alteração técnica se necessário, e encerra a participação do aluno mudando seu status para "concluído".
O retorno do sistema: Garante que nenhum aluno receba créditos sem uma validação do Supervisor + Responsável e envia para o sistema a submissão de encerramento do aluno alterando seu status.

Juliano:

1 - Usabilidade do sistema: O aluno acessa sua área principal e visualiza uma tabela com todas as disciplinas matriculadas. Para cada item, são exibidas informações como o nome da disciplina, semestre e o status atual (ex: "não enviado", "plano aprovado", "concluído"). Conforme o status, o sistema disponibiliza botões de ação para o aluno prosseguir, como "Enviar Plano", "Enviar Documentação" ou "Enviar Relatório".
O retorno do sistema: O sistema exibe a lista de disciplinas com seus respectivos status atualizados, permitindo que o aluno identifique facilmente quais processos estão pendentes e realize a ação necessária.


2 - Usabilidade do sistema: Na lista de ofertas, o aluno localiza a disciplina com status "não enviado" e clica em "Enviar Plano". O sistema exibe um formulário onde o aluno informa os dados da disciplina (código, nome, curso), seleciona o professor supervisor e anexa o arquivo PDF do plano. Após preencher, o aluno confirma o envio.
O retorno do sistema: O sistema valida o preenchimento, confirma o recebimento do arquivo, atualiza o status do estágio do aluno para "plano enviado" e exibe uma mensagem de sucesso, desabilitando o botão de envio para evitar duplicidade.


3 - Usabilidade do sistema: Na lista de ofertas, o aluno localiza a disciplina com status "não enviado" e clica em "Enviar Documentação". O sistema exibe um formulário onde o aluno informa a instituição onde ministrou aulas, a disciplina, carga horária e anexa o arquivo PDF comprobatório. Após preencher, o aluno confirma o envio.
O retorno do sistema: O sistema valida o envio, registra a solicitação, altera o status do estágio do aluno para "documentação enviada" e exibe uma mensagem de sucesso, encaminhando o processo para análise do professor responsável.

Vinicius:

1 - Usabilidade do sistema: O secretário acessa a tela de gerenciamento de ofertas, seleciona a opção "Nova Oferta", preenche as informações da oferta (nome, semestre, datas e professor responsável) e confirma o cadastro. Caso o nome não seja informado, o sistema gera automaticamente um nome padrão baseado no semestre. O sistema também registra automaticamente a data/hora de criação e o usuário responsável pelo cadastro.
O retorno do sistema: O sistema valida os dados informados, grava a oferta no banco de dados e disponibiliza a nova oferta na listagem de ofertas. Além disso, registra informações de auditoria, como o usuário criador e o momento exato do cadastro.


2 - Usabilidade do sistema: O secretário acessa uma oferta específica, abre a área de gerenciamento de alunos e informa os dados do aluno (nome, e-mail e senha). O sistema verifica se já existe um usuário cadastrado com o e-mail informado. Caso não exista, realiza automaticamente o cadastro do aluno. Em seguida, cria a matrícula do aluno na oferta selecionada.
O retorno do sistema: O sistema garante que um mesmo aluno não seja matriculado duas vezes na mesma oferta. Após a matrícula, o aluno passa a estar vinculado à oferta e aparece na lista de inscritos com o status inicial definido pelo sistema.


3 - Usabilidade do sistema: O secretário acessa a lista de ofertas cadastradas e seleciona uma oferta específica através da opção "Gerenciar Alunos". O sistema apresenta uma tela contendo as informações gerais da oferta, como semestre, professor responsável, datas de início e término e status atual da oferta. Além disso, o secretário visualiza a relação completa dos alunos inscritos e o status de andamento de cada um dentro do programa.
O retorno do sistema: O sistema recupera as informações da oferta e das inscrições associadas diretamente do banco de dados e apresenta uma visão consolidada da turma. Dessa forma, o secretário consegue acompanhar a situação da oferta, verificar quais alunos estão vinculados e consultar o status individual de cada inscrição, facilitando o controle e a gestão das atividades do PESCD.


Rafael:

1 - Visualizar Ofertas Disponíveis V.01
Descrição: Descreve os passos para um usuário não autenticado (visitante) acessar o sistema e visualizar as informações públicas sobre as ofertas do semestre.

Ator Principal: Visitante (Usuário não logado)

Pré-condições:
- O sistema deve estar online
- Deve existir pelo menos uma oferta cadastrada no banco de dados

Fluxo Principal:
- O Visitante acessa a URL raiz (Página Inicial/Index) do sistema PESCD
- O sistema carrega a interface pública
- O sistema busca no banco de dados todas as ofertas cadastradas e ativas
- O sistema exibe uma tabela com os detalhes das ofertas ordenadas da mais recente para a mais antiga (Nome da oferta, Semestre, Data de Início, Data de Fim, Professor Responsável e Total de Inscritos)
- O Visitante analisa as informações das ofertas disponíveis
- O caso de uso se encerra

2 - Autenticação e Redirecionamento por Perfil U.01
Descrição: Descreve o processo de login, onde o sistema autentica as credenciais e redireciona o usuário para o painel de controle correspondente ao seu nível de acesso.

Ator Principal: Usuário (Visitante que possui credenciais de Admin, Secretário, Professor ou Aluno)

Pré-condições:
- O usuário deve estar previamente cadastrado no banco de dados
- Deve possuir um e-mail (username) e senha válidos

Fluxo Principal:
- A partir da página inicial, o Usuário clica na opção "Ir para o Login" ou em um botão restrito da interface
- O sistema exibe o formulário de login pedindo Nome de Usuário (E-mail) e Senha
- O Usuário preenche suas credenciais e clica em "Entrar"
- O sistema valida as credenciais no banco de dados (Spring Security)
- O sistema identifica o perfil (Role) atrelado ao usuário: ADMIN, SECRETARIO, PROFESSOR ou ALUNO
- O sistema redireciona o Usuário automaticamente para a sua respectiva Home (ex: painel do administrador ou dashboard do aluno)
- O caso de uso se encerra

3 - Gerenciar Cadastro de Usuários AD.01
Descrição: Permite ao Administrador criar, editar, visualizar ou remover contas de Secretários e Professores para garantir o correto controle de acesso.

Ator Principal: Administrador

Pré-condições:
- O Administrador deve estar autenticado no sistema (ter passado com sucesso pelo Caso de Uso 2)
- Deve estar no Painel de Administração

Fluxo Principal (Criação de novo usuário):
- No menu principal de Administração, o Administrador seleciona "Gerenciar Secretários e Professores"
- O sistema exibe a lista de usuários atuais
- O Administrador clica no botão "Novo Usuário"
- O sistema apresenta um formulário solicitando Nome, E-mail (Username), Senha e Perfil
- O Administrador preenche os dados (ex: Perfil "Professor") e clica em "Salvar"
- O sistema valida se os dados não estão em branco e verifica se o e-mail fornecido já não existe no banco de dados
- O sistema salva o novo usuário no banco de dados
- O sistema redireciona para a lista de usuários exibindo a mensagem "Usuário salvo com sucesso"
