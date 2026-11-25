# SistemaJava

Sistema acadêmico de gerenciamento desenvolvido em equipe, utilizando Java e MySQL. O projeto tem como objetivo aplicar conceitos de programação orientada a objetos, integração com banco de dados e organização de código em camadas.

## Sobre o projeto

O SistemaJava reúne funcionalidades de gerenciamento de produtos, estoque, movimentações, caixa e funcionários, com uma interface gráfica para interação com o usuário.

O desenvolvimento é realizado de forma colaborativa, como parte das atividades acadêmicas do curso de Análise e Desenvolvimento de Sistemas.

## Funcionalidades e módulos

- Gerenciamento de produtos.
- Controle e consulta de estoque.
- Registro de movimentações de estoque.
- Gerenciamento de caixa.
- Gerenciamento de funcionários.
- Integração com banco de dados MySQL.
- Interface gráfica para utilização do sistema.

*As funcionalidades estão sendo implementadas e aprimoradas ao longo do desenvolvimento.*

## Tecnologias utilizadas

- Java
- MySQL
- JDBC
- MySQL Connector/J

## Estrutura do projeto

O projeto está organizado em camadas para facilitar a manutenção e a separação de responsabilidades:

- `app/` — inicialização da aplicação.
- `dao/` — acesso e operações no banco de dados.
- `model/` — classes que representam as entidades do sistema.
- `view/` — interfaces gráficas.
- `resources/` — recursos utilizados pela aplicação.

## Como executar

1. Tenha o Java JDK instalado.
2. Configure o MySQL no ambiente.
3. Configure os dados de conexão no arquivo `Conexao.java`.
4. Verifique se o MySQL Connector/J está disponível no projeto.
5. Abra o projeto em uma IDE compatível com Java e execute a classe principal.

## Notificações de estoque em tempo real

O CoStock registra um alerta no MySQL quando uma venda confirmada faz a quantidade de um produto cair abaixo do estoque mínimo. O servidor repassa o aviso por TCP aos painéis de administração e gerência conectados. O painel carrega os 30 alertas mais recentes do banco ao abrir e atualiza a lista quando recebe um aviso.

Na configuração do banco, execute `resources/alertas_estoque.sql` no mesmo banco usado pelo CoStock. O script pode ser executado novamente sem apagar o histórico.

1. Execute a classe `notificacoes.ServidorNotificacoes` na IDE. Por padrão, ela escuta em `127.0.0.1:5050` e roda em um processo separado do CoStock.
2. Execute `app.CoStock` em duas instâncias. Entre como administrador ou gerente em uma delas e como caixa na outra.
3. No painel, aguarde o status **Alertas conectados**. Para testar, use um produto com estoque igual ao mínimo e com pelo menos uma unidade disponível.
4. Finalize a venda de uma unidade desse produto. O nome do produto, o saldo e o mínimo aparecem na barra de alertas. Outra venda do mesmo produto enquanto ele já estiver abaixo do mínimo não dispara um novo aviso.

O servidor atende conexões simultâneas com um pool de threads. Cada painel tem uma thread de escuta, e os caixas enviam os avisos em segundo plano. Os painéis se reconectam automaticamente e consultam o histórico para recuperar alertas gerados enquanto estavam desconectados.

Para alterar a porta, use a opção de JVM `-Dcostock.notificacoes.porta=5051` no servidor e nas instâncias do CoStock.

## Equipe de desenvolvimento

- Isabel Lopes
- Heitor
- Gustavo

## Status do projeto

Em desenvolvimento.

Projeto acadêmico desenvolvido em equipe como parte da formação em Análise e Desenvolvimento de Sistemas.
