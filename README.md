# UaiBoom — Loja de Produtos Online

Aplicação JavaFX para consultar o catálogo, comprar produtos avulsos ou kits e acompanhar pedidos. As contas, os carrinhos e os pedidos são gravados em PostgreSQL. O mesmo sistema também tem um menu de terminal.

## 1. Prepare o ambiente

- Instale um **JDK 17 ou superior**. Confira com `java -version`.
- Instale e abra o **Docker Desktop**. Confira com `docker compose version` e aguarde o Docker iniciar.
- Use **IntelliJ IDEA** com o projeto importado pelo Maven ou tenha o **Maven** disponível no terminal (`mvn -v`).

Abra um terminal na pasta do projeto, onde estão `pom.xml` e `compose.yaml`.

## 2. Suba o PostgreSQL e carregue o catálogo

```sh
docker compose up -d --wait
docker compose ps
```

O serviço `postgres` deve aparecer como `healthy`. Na primeira execução, o Docker pode demorar para baixar a imagem. Se o volume foi criado com uma versão anterior que só tinha o catálogo, aplique a atualização abaixo antes de abrir a loja:

```sh
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U loja -d loja -f /docker-entrypoint-initdb.d/02-compras.sql
```

**Em um catálogo vazio**, carregue os produtos de demonstração. Execute esta etapa uma vez por banco para evitar duplicatas:

```sh
docker compose cp db/exemplo.sql postgres:/tmp/loja-exemplo.sql
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U loja -d loja -f /tmp/loja-exemplo.sql
```

A loja deve listar **Mouse USB** (R$ 49,90), **Teclado USB** (R$ 79,90) e **Kit escritório** (R$ 129,80).

## 3. Abra a loja

**Pelo IntelliJ:** abra a pasta do projeto, importe o `pom.xml` como Maven e aguarde as dependências. Execute a classe [LauncherGrafico](src/main/java/com/loja/view/grafica/LauncherGrafico.java) pelo botão de execução ao lado de `main`.

**Pelo terminal:**

```sh
mvn javafx:run
```

## 4. Faça uma compra de teste

1. Na janela, clique em **Entrar → Criar uma conta**. Use um e-mail de teste e uma senha de pelo menos oito caracteres. Entre com essa conta.
2. Em **Catálogo**, busque por `Kit` no topo da janela ou use o filtro **Kits**. No cartão **Kit escritório**, clique em **Comprar** e confirme a quantidade `1`.
3. Abra **Meu carrinho**. O subtotal esperado é **R$ 129,80**. Para testar a persistência do carrinho, feche a aplicação, abra de novo e entre com a mesma conta. O kit deve continuar lá.
4. Selecione **Frete normal** e clique em **Finalizar pedido**. O total esperado é **R$ 136,29**. A aplicação abre **Meus pedidos**, onde aparecem o kit, a quantidade e o total. Em **Meu carrinho**, a lista deve estar vazia.
5. Feche e abra a aplicação novamente. Entre com a mesma conta e confira o pedido em **Meus pedidos**. Crie uma segunda conta para confirmar que ela começa com carrinho e histórico próprios.

## 5. Teste o painel de administrador

Crie uma conta separada com o e-mail `admin@example.com` pela própria loja. Em seguida, no terminal, promova a conta no banco:

```sh
docker compose exec -T postgres psql -v ON_ERROR_STOP=1 -U loja -d loja -c "UPDATE cliente SET admin = true WHERE email = 'admin@example.com';"
```

O comando deve retornar `UPDATE 1`. Saia da conta e entre novamente. No **Catálogo**, use **Novo produto** para cadastrar um avulso de teste e busque pelo nome. Use **Editar** e **Remover** no cartão do produto. Para criar um kit, marque **Kit de produtos** e informe os IDs de produtos existentes. **Todos os pedidos** mostra as compras registradas.

## Menu de terminal

Execute `com.loja.Main` no IntelliJ ou rode `mvn compile exec:java`. A **opção 1** abre a conta e as compras no PostgreSQL, a **opção 2** abre a demonstração em memória e a **opção 3** consulta o catálogo público.

## Se houver um problema

| Sintoma | Como resolver |
|---|---|
| O catálogo está vazio | Limpe a busca e clique em **Buscar**. Se a lista continuar vazia, carregue os dados da etapa 2. |
| A tela informa falha no PostgreSQL | Confira `docker compose ps`. Se o banco vem de uma versão anterior, aplique a atualização da etapa 2. |
| O IntelliJ não encontra JavaFX ou o driver | Recarregue o projeto Maven, confira se o SDK é um JDK 17 ou superior e execute `LauncherGrafico`. |
| A porta `55432` já está em uso | Informe outro valor para `LOJA_DB_PORT` no ambiente em que inicia o Compose e o Java. No IntelliJ, ponha a mesma variável na configuração de execução. |

O banco `loja` usa a porta local `55432` por padrão. Os dados ficam no volume Docker `loja_dados`. Se usar `LOJA_DB_PASSWORD` em um banco novo, use o mesmo valor no Compose e na aplicação. Para parar o banco e conservar os dados, rode `docker compose stop`.
