package com.loja;

import com.loja.frete.EstrategiaFrete;
import com.loja.frete.FreteExpresso;
import com.loja.frete.FreteNormal;
import com.loja.frete.RetiradaNaLoja;
import com.loja.compra.Carrinho;
import com.loja.compra.Pedido;
import com.loja.model.Item;
import com.loja.model.Produto;
import com.loja.model.ProdutoFactory;
import com.loja.model.CriadorProduto.Dados;
import com.loja.controle.Catalogo;
import com.loja.controle.Compras;
import com.loja.controle.Contas;
import com.loja.repositorio.CompraRepositorio;
import com.loja.repositorio.ProdutoRepositorio;
import com.loja.view.MenuCatalogo;
import com.loja.view.MenuConta;
import java.math.BigDecimal;
import com.loja.model.TipoProduto;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ProdutoRepositorio produtos = new ProdutoRepositorio();
        MenuCatalogo menuCatalogo = new MenuCatalogo(new Catalogo(produtos));
        MenuConta menuConta = new MenuConta(new Contas(), new Compras(new CompraRepositorio(produtos)),
                menuCatalogo);
        var console = System.console();
        try (Scanner entrada = console == null ? new Scanner(System.in) : new Scanner(console.reader())) {
            while (true) {
                System.out.println("\n1 - Minha conta e compras");
                System.out.println("2 - Testar produtos, kit e carrinho (Pedro e Carlos)");
                System.out.println("3 - Catálogo (PostgreSQL)");
                System.out.println("0 - Sair");
                switch (escolher(entrada)) {
                    case "1" -> menuConta.executar(entrada);
                    case "2" -> testarCompra(entrada);
                    case "3" -> menuCatalogo.executar(entrada, null);
                    case "0" -> { return; }
                    default -> System.out.println("Opção inválida.");
                }
            }
        }
    }

    private static void testarCompra(Scanner entrada) {
        var simples = ProdutoFactory.selecionar(TipoProduto.PRODUTOSIMPLES);
        Produto mouse = simples.montar(1, new Dados("Mouse", "Mouse USB",
                BigDecimal.valueOf(50), 10, List.of()));
        Produto teclado = simples.montar(2, new Dados("Teclado", "Teclado USB",
                BigDecimal.valueOf(100), 10, List.of()));
        Produto kit = ProdutoFactory.selecionar(TipoProduto.PRODUTOKIT).montar(3,
                new Dados("Kit USB", "Mouse e teclado", null, 0, List.of(mouse, teclado)));
        Carrinho carrinho = new Carrinho();
        System.out.println("Novo teste em memória. Cada operação adiciona ou remove uma unidade.");
        while (true) {
            System.out.println("\n1 - Adicionar mouse (" + moeda(mouse.calcularPreco()) + ")");
            System.out.println("2 - Adicionar kit com mouse e teclado (" + moeda(kit.calcularPreco()) + ")");
            System.out.println("3 - Remover mouse   4 - Remover kit");
            System.out.println("5 - Simular preço de dois kits   6 - Finalizar pedido   0 - Voltar");
            System.out.println("Subtotal do carrinho: " + moeda(carrinho.calcularTotal()));
            switch (escolher(entrada)) {
                case "1" -> carrinho.adicionarItem(mouse, 1);
                case "2" -> carrinho.adicionarItem(kit, 1);
                case "3" -> carrinho.removerItem(mouse, 1);
                case "4" -> carrinho.removerItem(kit, 1);
                case "5" -> System.out.println("Dois kits: " + moeda(new Item(kit, 2).calcularSubtotal()));
                case "6" -> finalizarPedido(entrada, carrinho);
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void finalizarPedido(Scanner entrada, Carrinho carrinho) {
        System.out.println("1 - Frete normal   2 - Frete expresso   3 - Retirada na loja   0 - Voltar");
        EstrategiaFrete frete;
        switch (escolher(entrada)) {
            case "1" -> frete = new FreteNormal();
            case "2" -> frete = new FreteExpresso();
            case "3" -> frete = new RetiradaNaLoja();
            case "0" -> { return; }
            default -> { System.out.println("Opção inválida."); return; }
        }
        Pedido pedido = carrinho.finalizarCompra(frete);
        if (pedido != null) {
            System.out.printf("Pedido %d | %s | Total: %s%n",
                    pedido.getId(), pedido.getSituacao(), moeda(pedido.getTotal()));
        }
    }

    private static String escolher(Scanner entrada) {
        System.out.print("Escolha: ");
        return entrada.hasNextLine() ? entrada.nextLine().trim() : "0";
    }

    private static String moeda(double valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }
}
