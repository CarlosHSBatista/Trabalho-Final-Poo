package com.loja.view;

import com.loja.controle.Catalogo;
import com.loja.controle.Contas.Sessao;
import com.loja.model.Produto;
import com.loja.model.ProdutoKit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class MenuCatalogo {
    private final Catalogo catalogo;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));

    public MenuCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public void executar(Scanner entrada, Sessao sessao) {
        boolean admin = sessao != null && sessao.admin();
        while (true) {
            if (admin) {
                System.out.println("\n1 - Cadastrar produto simples   2 - Cadastrar kit");
                System.out.println("4 - Editar produto   5 - Remover produto");
            }
            System.out.println("\n3 - Buscar por nome   0 - Voltar");
            try {
                switch (ler(entrada, "Escolha: ")) {
                    case "1" -> { if (admin) cadastrarSimples(entrada, sessao); else acessoRestrito(); }
                    case "2" -> { if (admin) cadastrarKit(entrada, sessao); else acessoRestrito(); }
                    case "3" -> mostrar(catalogo.buscar(ler(entrada, "Nome (vazio para listar todos): ")));
                    case "4" -> { if (admin) editar(entrada, sessao); else acessoRestrito(); }
                    case "5" -> {
                        if (admin) {
                            catalogo.remover(sessao, lerInteiro(entrada, "ID do produto: "));
                            System.out.println("Produto removido.");
                        } else {
                            acessoRestrito();
                        }
                    }
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                return;
            } catch (NumberFormatException e) {
                System.out.println("Número inválido. Preço aceita vírgula ou ponto. Estoque e IDs são inteiros.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (SQLException e) {
                System.out.println("Falha no catálogo. Confira o PostgreSQL e a configuração da conexão.");
            }
        }
    }

    private void cadastrarSimples(Scanner entrada, Sessao sessao) throws SQLException {
        String nome = ler(entrada, "Nome: ");
        String descricao = ler(entrada, "Descrição: ");
        BigDecimal preco = lerPreco(entrada);
        int estoque = lerInteiro(entrada, "Estoque: ");
        confirmar(catalogo.cadastrarSimples(sessao, nome, descricao, preco, estoque));
    }

    private void cadastrarKit(Scanner entrada, Sessao sessao) throws SQLException {
        List<Produto> produtos = catalogo.buscar("");
        if (produtos.isEmpty()) {
            System.out.println("Cadastre um produto antes de montar um kit.");
            return;
        }
        mostrar(produtos);
        String nome = ler(entrada, "Nome do kit: ");
        String descricao = ler(entrada, "Descrição: ");
        confirmar(catalogo.cadastrarKit(sessao, nome, descricao, lerComponentes(entrada)));
    }

    private void editar(Scanner entrada, Sessao sessao) throws SQLException {
        Produto atual = catalogo.obter(lerInteiro(entrada, "ID do produto: "));
        String nome = ler(entrada, "Novo nome: ");
        String descricao = ler(entrada, "Nova descrição: ");
        if (atual instanceof ProdutoKit) {
            mostrar(catalogo.buscar(""));
            catalogo.editar(sessao, atual.getId(), nome, descricao, null, 0, lerComponentes(entrada));
        } else {
            catalogo.editar(sessao, atual.getId(), nome, descricao, lerPreco(entrada),
                    lerInteiro(entrada, "Novo estoque: "), List.of());
        }
        System.out.println("Produto atualizado.");
    }

    private static List<Integer> lerComponentes(Scanner entrada) {
        String componentes = ler(entrada, "IDs dos componentes, separados por espaço: ");
        if (componentes.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um componente do kit.");
        }
        return Arrays.stream(componentes.split("\\s+")).map(Integer::valueOf).toList();
    }

    private static BigDecimal lerPreco(Scanner entrada) {
        return new BigDecimal(ler(entrada, "Preço: ").replace(',', '.'));
    }

    private static int lerInteiro(Scanner entrada, String pergunta) {
        return Integer.parseInt(ler(entrada, pergunta));
    }

    private static void acessoRestrito() {
        System.out.println("Operação exclusiva do administrador.");
    }

    private void mostrar(List<Produto> produtos) {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
        }
        for (Produto produto : produtos) {
            System.out.printf("#%d | %s | %s | %s | %s%n", produto.getId(),
                    produto instanceof ProdutoKit ? "Kit" : "Simples", produto.getNome(),
                    moeda.format(produto.calcularPreco()), produto.getDescricao());
        }
    }

    private static void confirmar(int id) {
        System.out.println("Produto cadastrado. ID: " + id);
    }

    private static String ler(Scanner entrada, String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine().strip();
    }
}
