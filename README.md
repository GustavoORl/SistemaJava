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

### 1. Preparar o ambiente

- Instale um JDK 17 ou superior e confirme no PowerShell que `java -version` e `javac -version` funcionam.
- Instale o MySQL Server 8.0 e o MySQL Workbench. Inicie o serviço do MySQL Server.
- Baixe ou clone o projeto e abra a pasta `SistemaJava`. O driver JDBC já está em `lib/mysql-connector-j-9.0.0.jar`.

### 2. Importar o banco no MySQL Workbench

1. Abra uma conexão com o MySQL local (`localhost:3306` na configuração padrão) usando uma conta com permissão para criar e alterar tabelas.
2. Vá em **File > Open SQL Script**, abra [resources/script.sql](resources/script.sql) e execute o arquivo inteiro pelo botão de raio, sem selecionar um trecho. Esse arquivo cria as 20 tabelas do banco `gestao_vendas`, incluindo `alertas_estoque`, e carrega os usuários, produtos, saldos e formas de pagamento para a demonstração. Use uma instância de testes, pois uma nova execução recria as tabelas e substitui seus dados, incluindo o histórico de alertas.
3. Atualize **Schemas** e confira se `gestao_vendas` contém as tabelas `usuarios`, `estoque` e `alertas_estoque`.

### 3. Configurar a conexão Java

Na pasta `dao`, crie o arquivo `Conexao.java` com o conteúdo abaixo. Substitua `SEU_USUARIO_MYSQL` e `SUA_SENHA_MYSQL` pelas credenciais usadas no Workbench. Se o MySQL usar outra porta, ajuste `3306` na URL. Esse arquivo fica na máquina de quem executa o projeto e é ignorado pelo Git.

```java
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/gestao_vendas",
                "SEU_USUARIO_MYSQL",
                "SUA_SENHA_MYSQL"
        );
    }
}
```

### 4. Compilar e iniciar o servidor de notificações

No PowerShell, a partir da pasta `SistemaJava` (onde fica este README), execute os comandos abaixo. Prossiga para o último comando depois de a compilação terminar sem erros.

```powershell
$fontes = (Get-ChildItem -Path app,dao,exception,model,notificacoes,services,view -Recurse -Filter '*.java' -File).FullName
javac -encoding UTF-8 -cp "lib/mysql-connector-j-9.0.0.jar" -d build $fontes
java -cp "build;lib/mysql-connector-j-9.0.0.jar;." notificacoes.ServidorNotificacoes
```

Aguarde a mensagem **Servidor de notificações ouvindo em /127.0.0.1:5050** e mantenha esse terminal aberto.

### 5. Abrir o CoStock

Abra outro PowerShell na mesma pasta e execute:

```powershell
java -cp "build;lib/mysql-connector-j-9.0.0.jar;." app.CoStock
```

Entre na tela de login com uma das contas carregadas pelo script:

| Perfil | E-mail | Senha |
| --- | --- | --- |
| Administrador | `admin@costock.com` | `123456` |
| Caixa | `caixa@costock.com` | `123456` |

Para usar os dois perfis ao mesmo tempo, repita o comando em um terceiro terminal e entre com a outra conta. Ao terminar, feche as janelas do CoStock e pressione **Ctrl+C** no terminal do servidor.

## Notificações de estoque em tempo real

O CoStock registra no MySQL o saldo de cada produto após uma venda, entrada, saída ou ajuste confirmado. O servidor repassa o aviso por TCP aos painéis de administração e gerência conectados. O painel carrega os 30 avisos mais recentes do banco ao abrir e atualiza a lista quando recebe um aviso. Uma saída que faz o saldo cruzar o mínimo aparece como **Estoque baixo**.

1. Com o servidor rodando, entre como administrador em uma janela e como caixa em outra. No painel do administrador, aguarde o status **Alertas conectados**.
2. Na janela do caixa, informe um valor inicial, como `100,00`, e clique em **Confirmar abertura**.
3. Em uma base recém-importada, busque o produto pelo código `TESTE000015` (Feijão Carioca 1kg). Adicione 4 unidades à venda, clique em **Finalizar venda**, selecione uma forma de pagamento e clique em **Confirmar venda**.
4. Confira se o painel do administrador recebe um aviso de **Estoque baixo**. A venda reduz o saldo de 18 para 14 unidades, abaixo do mínimo de 15.
5. No painel do administrador, abra **Movimentações de Estoque** e registre uma entrada de 20 unidades para o mesmo produto. Confira se o painel recebe a notificação com o novo saldo de 34 unidades.

O servidor atende conexões simultâneas com um pool de threads. Cada painel tem uma thread de escuta, e os avisos são enviados em segundo plano. Os painéis se reconectam automaticamente e consultam o histórico para recuperar alertas gerados enquanto estavam desconectados.

Para alterar a porta, use a opção de JVM `-Dcostock.notificacoes.porta=5051` no servidor e nas instâncias do CoStock.

## Equipe de desenvolvimento

- Isabel Lopes
- Heitor
- Gustavo

## Status do projeto

Em desenvolvimento.

Projeto acadêmico desenvolvido em equipe como parte da formação em Análise e Desenvolvimento de Sistemas.
