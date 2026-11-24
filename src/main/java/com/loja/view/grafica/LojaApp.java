package com.loja.view.grafica;

import com.loja.compra.ResumoCompra.Linha;
import com.loja.controle.Catalogo;
import com.loja.controle.Compras;
import com.loja.controle.Compras.Modalidade;
import com.loja.controle.Contas;
import com.loja.controle.Contas.Sessao;
import com.loja.model.Produto;
import com.loja.model.ProdutoKit;
import com.loja.repositorio.CompraRepositorio;
import com.loja.repositorio.CompraRepositorio.ItemHistorico;
import com.loja.repositorio.ProdutoRepositorio;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class LojaApp extends Application {
    private final ProdutoRepositorio produtos = new ProdutoRepositorio();
    private final Catalogo catalogo = new Catalogo(produtos);
    private final Contas contas = new Contas();
    private final Compras compras = new Compras(new CompraRepositorio(produtos));
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private final ExecutorService banco = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "loja-postgres");
        thread.setDaemon(true);
        return thread;
    });

    private final BorderPane raiz = new BorderPane();
    private final Label aviso = new Label();
    private Stage janela;
    private VBox corpo;
    private Sessao sessao;

    @Override
    public void start(Stage stage) {
        janela = stage;
        Scene cena = new Scene(raiz, 1050, 680);
        cena.getStylesheets().add(LojaApp.class.getResource("loja.css").toExternalForm());
        aviso.getStyleClass().add("notice");
        aviso.setWrapText(true);
        stage.setTitle("Loja Tema 4");
        stage.setMinWidth(860);
        stage.setMinHeight(590);
        stage.setScene(cena);
        stage.show();
        catalogo();
    }

    @Override
    public void stop() {
        banco.shutdownNow();
    }

    private void tela(String titulo, String descricao, String pagina) {
        Label marca = rotulo("Loja Tema 4", "brand");
        VBox menu = new VBox(marca, navegacao("Catálogo", pagina, this::catalogo));
        menu.getStyleClass().add("sidebar");
        menu.setPrefWidth(205);
        Region espaco = new Region();
        VBox.setVgrow(espaco, Priority.ALWAYS);
        if (sessao == null) {
            menu.getChildren().addAll(espaco, navegacao("Entrar", pagina, () -> conta(false)));
        } else {
            menu.getChildren().addAll(navegacao("Meu carrinho", pagina, this::carrinho),
                    navegacao("Meus pedidos", pagina, () -> pedidos(false)));
            if (sessao.admin()) {
                menu.getChildren().add(navegacao("Todos os pedidos", pagina, () -> pedidos(true)));
            }
            menu.getChildren().addAll(espaco, rotulo(sessao.nome(), "account"),
                    navegacao("Sair da conta", pagina, () -> {
                        sessao = null;
                        catalogo();
                    }));
        }
        if (aviso.getParent() instanceof VBox anterior) {
            anterior.getChildren().remove(aviso);
        }
        corpo = new VBox(rotulo(titulo, "title"), rotulo(descricao, "subtitle"));
        corpo.getStyleClass().add("content");
        VBox painel = new VBox(corpo, aviso);
        painel.setStyle("-fx-padding: 0 0 16 0;");
        aviso.setStyle("-fx-padding: 0 32 0 32;");
        VBox.setVgrow(corpo, Priority.ALWAYS);
        raiz.setLeft(menu);
        raiz.setCenter(painel);
        informar("", false);
    }

    private void catalogo() {
        tela("Catálogo", "Encontre produtos avulsos e kits pelo nome.", "Catálogo");
        TextField busca = campo("Buscar produto");
        HBox.setHgrow(busca, Priority.ALWAYS);
        TableView<Produto> tabela = tabela("Nenhum produto encontrado.");
        coluna(tabela, "ID", 65, p -> "#" + p.getId());
        coluna(tabela, "Produto", 185, Produto::getNome);
        coluna(tabela, "Tipo", 85, p -> p instanceof ProdutoKit ? "Kit" : "Avulso");
        coluna(tabela, "Descrição", 180, Produto::getDescricao);
        coluna(tabela, "Preço", 100, p -> moeda.format(p.calcularPreco()));
        Consumer<String> pesquisar = termo -> executar(() -> catalogo.buscar(termo),
                itens -> tabela.getItems().setAll(itens));
        busca.setOnAction(evento -> pesquisar.accept(busca.getText()));
        HBox filtros = linha(busca, botao("Buscar", true, () -> pesquisar.accept(busca.getText())));
        HBox acoes = linha(botao(sessao == null ? "Entrar para comprar" : "Definir quantidade no carrinho",
                true, () -> {
                    if (sessao == null) {
                        conta(false);
                    } else {
                        selecionar(tabela, produto -> definirQuantidade(produto, "1",
                                "Carrinho atualizado.", () -> {}));
                    }
                }));
        if (sessao != null && sessao.admin()) {
            acoes.getChildren().addAll(botao("Novo produto", false, () -> editar(null, pesquisar, busca)),
                    botao("Editar", false, () -> selecionar(tabela, p -> editar(p, pesquisar, busca))),
                    botao("Remover", false, () -> selecionar(tabela, p -> remover(p, pesquisar, busca))));
        }
        corpo.getChildren().addAll(filtros, tabela, acoes);
        pesquisar.accept("");
    }

    private void conta(boolean cadastro) {
        tela(cadastro ? "Criar conta" : "Entrar", "Use sua conta para acessar o carrinho e os pedidos.", "Entrar");
        TextField nome = campo("Nome");
        TextField email = campo("E-mail");
        PasswordField senha = senha("Senha");
        PasswordField confirmar = senha("Confirme a senha");
        VBox formulario = new VBox();
        formulario.setMaxWidth(480);
        formulario.getStyleClass().add("card");
        if (cadastro) {
            formulario.getChildren().addAll(new Label("Nome"), nome);
        }
        formulario.getChildren().addAll(new Label("E-mail"), email, new Label("Senha"), senha);
        if (cadastro) {
            formulario.getChildren().addAll(new Label("Confirme a senha"), confirmar);
        }
        Button enviar = botao(cadastro ? "Criar conta" : "Entrar na loja", true, () -> {
            String endereco = email.getText();
            String chave = senha.getText();
            if (cadastro) {
                String cliente = nome.getText();
                String confirmacao = confirmar.getText();
                executar(() -> {
                    contas.cadastrar(cliente, endereco, chave, confirmacao);
                    return null;
                }, resposta -> {
                    conta(false);
                    informar("Conta criada. Entre para continuar.", false);
                });
            } else {
                executar(() -> contas.entrar(endereco, chave), resposta -> {
                    sessao = resposta;
                    catalogo();
                });
            }
        });
        enviar.setDefaultButton(true);
        formulario.getChildren().add(linha(enviar, botao(cadastro ? "Voltar para entrar" : "Criar uma conta",
                false, () -> conta(!cadastro))));
        corpo.getChildren().add(formulario);
    }

    private void carrinho() {
        tela("Meu carrinho", "Revise os itens antes de finalizar a compra.", "Meu carrinho");
        TableView<Linha> tabela = tabela("Seu carrinho está vazio.");
        coluna(tabela, "Produto", 220, item -> item.produto().getNome());
        coluna(tabela, "Quantidade", 95, item -> String.valueOf(item.quantidade()));
        coluna(tabela, "Preço unitário", 120, item -> moeda.format(item.precoUnitario()));
        coluna(tabela, "Subtotal do item", 150, item -> moeda.format(total(item)));
        var valorSubtotal = Bindings.createObjectBinding(() -> tabela.getItems().stream()
                .map(LojaApp::total).reduce(BigDecimal.ZERO, BigDecimal::add), tabela.getItems());
        Label subtotal = new Label();
        subtotal.textProperty().bind(Bindings.createStringBinding(
                () -> "Subtotal dos itens   " + moeda.format(valorSubtotal.get()), valorSubtotal));
        Label totalPrevisto = rotulo("", "subtotal");
        ComboBox<Modalidade> modalidade = new ComboBox<>();
        modalidade.getItems().setAll(Modalidade.values());
        modalidade.setConverter(new StringConverter<>() {
            @Override public String toString(Modalidade valor) {
                return valor == null ? "" : nomeFrete(valor.name());
            }
            @Override public Modalidade fromString(String texto) { throw new UnsupportedOperationException(); }
        });
        modalidade.setValue(Modalidade.NORMAL);
        modalidade.setAccessibleText("Modalidade de entrega");
        totalPrevisto.textProperty().bind(Bindings.createStringBinding(
                () -> "Total com entrega   " + moeda.format(compras.preverTotal(
                        valorSubtotal.get(), modalidade.getValue())), valorSubtotal, modalidade.valueProperty()));
        Button finalizar = botao("Finalizar pedido", true, () -> {
            Modalidade escolha = modalidade.getValue();
            executar(() -> compras.finalizar(sessao, escolha), pedido -> {
                pedidos(false);
                informar("Pedido #" + pedido.id() + " confirmado. Total " + moeda.format(pedido.total()), false);
            });
        });
        finalizar.disableProperty().bind(Bindings.isEmpty(tabela.getItems()));
        corpo.getChildren().addAll(tabela, linha(
                botao("Alterar quantidade", false, () -> selecionar(tabela, item ->
                        definirQuantidade(item.produto(), String.valueOf(item.quantidade()),
                                "Quantidade atualizada.", this::carrinho))),
                botao("Remover item", false, () -> selecionar(tabela, item ->
                        salvarQuantidade(item.produto(), 0, "Item removido.", this::carrinho)))),
                linha(new Label("Entrega"), modalidade), subtotal, linha(totalPrevisto, finalizar));
        executar(() -> compras.carrinho(sessao), itens -> tabela.getItems().setAll(itens));
    }

    private void pedidos(boolean todos) {
        String titulo = todos ? "Todos os pedidos" : "Meus pedidos";
        tela(titulo, "Acompanhe os pedidos e os preços registrados na compra.", titulo);
        TableView<ItemHistorico> tabela = tabela("Nenhum pedido encontrado.");
        coluna(tabela, "Pedido", 65, item -> "#" + item.pedido());
        coluna(tabela, "Data", 100, item -> item.data().toLocalDate().toString());
        coluna(tabela, "Item", 155, ItemHistorico::produto);
        coluna(tabela, "Qtd.", 55, item -> String.valueOf(item.quantidade()));
        coluna(tabela, "Unitário", 85, item -> moeda.format(item.preco()));
        coluna(tabela, "Entrega", 105, item -> nomeFrete(item.modalidade()));
        coluna(tabela, "Total do pedido", 115, item -> moeda.format(item.total()));
        coluna(tabela, "Situação", 145, item -> item.situacao().replace('_', ' '));
        corpo.getChildren().add(tabela);
        executar(() -> compras.historico(sessao, todos), itens -> tabela.getItems().setAll(itens));
    }

    private void definirQuantidade(Produto produto, String atual, String mensagem, Runnable depois) {
        TextInputDialog dialogo = new TextInputDialog(atual);
        dialogo.initOwner(janela);
        dialogo.setTitle("Quantidade no carrinho");
        dialogo.setHeaderText(produto.getNome());
        dialogo.setContentText("Quantidade final (0 remove)");
        dialogo.showAndWait().ifPresent(texto -> {
            try {
                salvarQuantidade(produto, Integer.parseInt(texto.strip()), mensagem, depois);
            } catch (NumberFormatException e) {
                informar("Informe um número inteiro.", true);
            }
        });
    }

    private void salvarQuantidade(Produto produto, int quantidade, String mensagem, Runnable depois) {
        executar(() -> {
            compras.definirQuantidade(sessao, produto.getId(), quantidade);
            return null;
        }, resposta -> {
            depois.run();
            informar(mensagem, false);
        });
    }

    private static BigDecimal total(Linha linha) {
        return linha.precoUnitario().multiply(BigDecimal.valueOf(linha.quantidade()));
    }

    private static String nomeFrete(String modalidade) {
        return switch (modalidade) {
            case "NORMAL" -> "Frete normal";
            case "EXPRESSO" -> "Frete expresso";
            case "RETIRADA_NA_LOJA" -> "Retirada na loja";
            default -> modalidade;
        };
    }

    private void editar(Produto atual, Consumer<String> pesquisar, TextField busca) {
        executar(() -> catalogo.buscar(""), itens -> formularioProduto(atual, pesquisar, busca, itens));
    }

    private record DadosProduto(String nome, String descricao, boolean kit, BigDecimal preco,
                                int estoque, List<Integer> componentes) {}

    private void formularioProduto(Produto atual, Consumer<String> pesquisar, TextField busca,
                                  List<Produto> disponiveis) {
        Dialog<DadosProduto> dialogo = new Dialog<>();
        dialogo.initOwner(janela);
        dialogo.setTitle(atual == null ? "Cadastrar produto" : "Editar produto");
        ButtonType salvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);
        TextField nome = campo("Nome");
        TextField descricao = campo("Descrição");
        TextField preco = campo("Preço em reais");
        TextField estoque = campo("Estoque disponível");
        TextField componentes = campo("IDs separados por espaços");
        CheckBox kit = new CheckBox("Kit de produtos");
        ListView<String> lista = new ListView<>();
        lista.setPrefHeight(100);
        lista.getItems().setAll(disponiveis.stream().filter(p -> atual == null || p.getId() != atual.getId())
                .map(p -> "#" + p.getId() + "  " + p.getNome()).toList());
        Label erro = rotulo("", "notice");
        erro.setWrapText(true);
        if (atual != null) {
            nome.setText(atual.getNome());
            descricao.setText(atual.getDescricao());
            kit.setDisable(true);
            if (atual instanceof ProdutoKit produtoKit) {
                kit.setSelected(true);
                componentes.setText(produtoKit.getComponentes().stream()
                        .map(p -> String.valueOf(p.getId())).collect(Collectors.joining(" ")));
            } else {
                preco.setText(BigDecimal.valueOf(atual.calcularPreco()).toPlainString());
                // O formulário pede o estoque atual explicitamente ao salvar.
            }
        }
        VBox simples = new VBox(6, new Label("Preço em reais"), preco, new Label("Estoque disponível"), estoque);
        VBox composicao = new VBox(6, new Label("IDs dos componentes"), componentes, lista);
        simples.visibleProperty().bind(kit.selectedProperty().not());
        simples.managedProperty().bind(simples.visibleProperty());
        composicao.visibleProperty().bind(kit.selectedProperty());
        composicao.managedProperty().bind(composicao.visibleProperty());
        VBox campos = new VBox(8, new Label("Nome"), nome, new Label("Descrição"), descricao,
                kit, simples, composicao, erro);
        dialogo.getDialogPane().setPrefWidth(460);
        dialogo.getDialogPane().setContent(campos);
        DadosProduto[] dados = new DadosProduto[1];
        dialogo.getDialogPane().lookupButton(salvar).addEventFilter(javafx.event.ActionEvent.ACTION, evento -> {
            try {
                String ids = componentes.getText().strip();
                dados[0] = new DadosProduto(nome.getText(), descricao.getText(), kit.isSelected(),
                        kit.isSelected() ? null : new BigDecimal(preco.getText().strip().replace(',', '.')),
                        kit.isSelected() ? 0 : Integer.parseInt(estoque.getText().strip()),
                        kit.isSelected() && !ids.isEmpty()
                                ? Arrays.stream(ids.split("\\s+")).map(Integer::valueOf).toList() : List.of());
            } catch (NumberFormatException e) {
                erro.setText("Preencha preço e estoque ou informe IDs inteiros para os componentes.");
                evento.consume();
            }
        });
        dialogo.setResultConverter(acao -> acao == salvar ? dados[0] : null);
        dialogo.showAndWait().ifPresent(valor -> executar(() -> {
            if (atual != null) {
                catalogo.editar(sessao, atual.getId(), valor.nome(), valor.descricao(),
                        valor.preco(), valor.estoque(), valor.componentes());
            } else if (valor.kit()) {
                catalogo.cadastrarKit(sessao, valor.nome(), valor.descricao(), valor.componentes());
            } else {
                catalogo.cadastrarSimples(sessao, valor.nome(), valor.descricao(), valor.preco(), valor.estoque());
            }
            return null;
        }, resultado -> {
            pesquisar.accept(busca.getText());
            informar(atual == null ? "Produto cadastrado." : "Produto atualizado.", false);
        }));
    }

    private void remover(Produto produto, Consumer<String> pesquisar, TextField busca) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Remover " + produto.getNome() + "?",
                ButtonType.CANCEL, ButtonType.OK);
        confirmacao.initOwner(janela);
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            executar(() -> {
                catalogo.remover(sessao, produto.getId());
                return null;
            }, resultado -> {
                pesquisar.accept(busca.getText());
                informar("Produto removido.", false);
            });
        }
    }

    private <T> void selecionar(TableView<T> tabela, Consumer<T> acao) {
        T selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            informar("Selecione uma linha na tabela.", true);
        } else {
            acao.accept(selecionado);
        }
    }

    private <T> void executar(Callable<T> chamada, Consumer<T> concluir) {
        raiz.setDisable(true);
        informar("Consultando a loja…", false);
        Task<T> tarefa = new Task<>() {
            @Override protected T call() throws Exception { return chamada.call(); }
        };
        tarefa.setOnSucceeded(evento -> {
            raiz.setDisable(false);
            if (aviso.getText().equals("Consultando a loja…")) {
                informar("", false);
            }
            concluir.accept(tarefa.getValue());
        });
        tarefa.setOnFailed(evento -> {
            raiz.setDisable(false);
            Throwable falha = tarefa.getException();
            informar(falha instanceof IllegalArgumentException ? falha.getMessage()
                    : falha instanceof SQLException ? "Falha no PostgreSQL. Verifique se o banco está rodando."
                    : "Não foi possível concluir a operação.", true);
        });
        banco.execute(tarefa);
    }

    private void informar(String texto, boolean erro) {
        aviso.setText(texto);
        aviso.getStyleClass().remove("error");
        if (erro) {
            aviso.getStyleClass().add("error");
        }
    }

    private static Button navegacao(String texto, String pagina, Runnable acao) {
        Button botao = botao(texto, false, acao);
        botao.setMaxWidth(Double.MAX_VALUE);
        if (texto.equals(pagina)) {
            botao.getStyleClass().add("active");
        }
        return botao;
    }

    private static Button botao(String texto, boolean primario, Runnable acao) {
        Button botao = new Button(texto);
        if (primario) {
            botao.getStyleClass().add("primary");
        }
        botao.setOnAction(evento -> acao.run());
        return botao;
    }

    private static Label rotulo(String texto, String estilo) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().add(estilo);
        return rotulo;
    }

    private static HBox linha(javafx.scene.Node... elementos) {
        HBox caixa = new HBox(10, elementos);
        caixa.setAlignment(Pos.CENTER_LEFT);
        return caixa;
    }

    private static TextField campo(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.setAccessibleText(prompt);
        return campo;
    }

    private static PasswordField senha(String prompt) {
        PasswordField campo = new PasswordField();
        campo.setPromptText(prompt);
        campo.setAccessibleText(prompt);
        return campo;
    }

    private static <T> TableView<T> tabela(String vazia) {
        TableView<T> tabela = new TableView<>();
        tabela.setPlaceholder(new Label(vazia));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabela, Priority.ALWAYS);
        return tabela;
    }

    private static <T> void coluna(TableView<T> tabela, String nome, double largura,
                                  Function<T, String> valor) {
        TableColumn<T, String> coluna = new TableColumn<>(nome);
        coluna.setPrefWidth(largura);
        coluna.setCellValueFactory(dado -> new ReadOnlyStringWrapper(valor.apply(dado.getValue())));
        tabela.getColumns().add(coluna);
    }
}
