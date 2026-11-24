package com.loja.view;

import com.loja.controle.Compras;
import com.loja.controle.Compras.Modalidade;
import com.loja.controle.Contas;
import com.loja.controle.Contas.Sessao;
import java.io.Console;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class MenuConta {
    private final Contas contas;
    private final Compras compras;
    private final MenuCatalogo catalogo;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public MenuConta(Contas contas, Compras compras, MenuCatalogo catalogo) {
        this.contas = contas;
        this.compras = compras;
        this.catalogo = catalogo;
    }

    public void executar(Scanner entrada) {
        while (true) {
            System.out.println("\n1 - Criar conta   2 - Entrar   0 - Voltar");
            try {
                switch (ler(entrada, "Escolha: ")) {
                    case "1" -> cadastrar(entrada);
                    case "2" -> {
                        String email = ler(entrada, "E-mail: ");
                        Sessao sessao = contas.entrar(email, senha(entrada, "Senha: "));
                        System.out.println("Login feito com sucesso, " + sessao.nome() + "!");
                        menuCliente(entrada, sessao);
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                return;
            } catch (IllegalArgumentException | SQLException e) {
                mostrarErro(e);
            }
        }
    }

    private void cadastrar(Scanner entrada) throws SQLException {
        String nome = ler(entrada, "Nome: ");
        String email = ler(entrada, "E-mail: ");
        String senha = senha(entrada, "Senha: ");
        contas.cadastrar(nome, email, senha, senha(entrada, "Confirme a senha: "));
        System.out.println("Conta criada.");
    }

    private void menuCliente(Scanner entrada, Sessao sessao) {
        while (true) {
            System.out.println("\n1 - Catálogo   2 - Meu carrinho   3 - Alterar item do carrinho");
            System.out.println("4 - Finalizar compra   5 - Meus pedidos   0 - Sair da conta");
            if (sessao.admin()) {
                System.out.println("6 - Todos os pedidos");
            }
            try {
                switch (ler(entrada, "Escolha: ")) {
                    case "1" -> catalogo.executar(entrada, sessao);
                    case "2" -> mostrarCarrinho(sessao);
                    case "3" -> {
                        int id = Integer.parseInt(ler(entrada, "ID do produto: "));
                        int quantidade = Integer.parseInt(ler(entrada, "Quantidade final (0 remove): "));
                        compras.definirQuantidade(sessao, id, quantidade);
                        System.out.println("Carrinho atualizado.");
                    }
                    case "4" -> finalizar(entrada, sessao);
                    case "5" -> mostrarPedidos(sessao, false);
                    case "6" -> mostrarPedidos(sessao, true);
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                return;
            } catch (IllegalArgumentException | SQLException e) {
                mostrarErro(e);
            }
        }
    }

    private void mostrarCarrinho(Sessao sessao) throws SQLException {
        var itens = compras.carrinho(sessao);
        if (itens.isEmpty()) {
            System.out.println("Carrinho vazio.");
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var item : itens) {
            BigDecimal preco = item.precoUnitario();
            subtotal = subtotal.add(preco.multiply(BigDecimal.valueOf(item.quantidade())));
            System.out.printf("#%d | %s | %d × %s%n", item.produto().getId(),
                    item.produto().getNome(), item.quantidade(), moeda.format(preco));
        }
        System.out.println("Subtotal: " + moeda.format(subtotal));
    }

    private void finalizar(Scanner entrada, Sessao sessao) throws SQLException {
        System.out.println("1 - Frete normal   2 - Frete expresso   3 - Retirada na loja   0 - Voltar");
        String opcao = ler(entrada, "Escolha: ");
        if (opcao.equals("0")) {
            return;
        }
        Modalidade modalidade = switch (opcao) {
            case "1" -> Modalidade.NORMAL;
            case "2" -> Modalidade.EXPRESSO;
            case "3" -> Modalidade.RETIRADA_NA_LOJA;
            default -> throw new IllegalArgumentException("Modalidade inválida.");
        };
        var pedido = compras.finalizar(sessao, modalidade);
        System.out.printf("Pedido #%d confirmado. Total: %s%n", pedido.id(), moeda.format(pedido.total()));
    }

    private void mostrarPedidos(Sessao sessao, boolean todos) throws SQLException {
        var itens = compras.historico(sessao, todos);
        if (itens.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
        }
        int anterior = 0;
        for (var item : itens) {
            if (item.pedido() != anterior) {
                System.out.printf("Pedido #%d | %s | %s | %s | %s%n", item.pedido(),
                        item.data().toLocalDate(), item.situacao(), item.modalidade(), moeda.format(item.total()));
                anterior = item.pedido();
            }
            System.out.printf("  %s | %d × %s%n", item.produto(), item.quantidade(), moeda.format(item.preco()));
        }
    }

    private static void mostrarErro(Exception e) {
        if (e instanceof SQLException) {
            System.out.println("Falha no PostgreSQL. Confira a conexão e o esquema do banco.");
        } else if (e instanceof NumberFormatException) {
            System.out.println("Informe um número inteiro válido.");
        } else {
            System.out.println(e.getMessage());
        }
    }

    private static String ler(Scanner entrada, String texto) {
        System.out.print(texto);
        return entrada.nextLine().strip();
    }

    private static String senha(Scanner entrada, String texto) {
        Console console = System.console();
        if (console == null) {
            System.out.print(texto);
            return entrada.nextLine();
        }
        char[] caracteres = console.readPassword("%s", texto);
        if (caracteres == null) {
            throw new NoSuchElementException();
        }
        try {
            return new String(caracteres);
        } finally {
            Arrays.fill(caracteres, '\0');
        }
    }
}
