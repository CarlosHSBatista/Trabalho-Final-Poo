package com.loja.model;

import java.util.ArrayList;
import java.util.List;

public class ProdutoKit extends Produto {

    //Cria uma Lista pra guardar os produtos
    private List<Produto> produtos = new ArrayList<>();

    //Construtor que coloca dados dentro das variaveis
    public ProdutoKit(int id, String nome, String descricao) {
        super(id, nome, descricao);
    }

    //Adiciona o Produto
    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    //Remove o Produto
    public void removerProduto(Produto produto) {
        produtos.remove(produto);
    }

    @Override
    public double calcularPreco() {
        //Faz a soma dos itens que  estão dentro da Sacola
        double soma = 0;
        for (Produto p : produtos) {
            soma = soma + p.calcularPreco();
        }
        return soma;
    }

    @Override
    // Verifica se TODOS os produtos do kit têm estoque suficiente
    public boolean verificarEstoque(int quantidade) {
        // Se algum produto não tiver estoque, o kit inteiro fica indisponível
        for (Produto p : produtos) {
            if (!p.verificarEstoque(quantidade)) {
                return false;
            }
        }
        // Todos os produtos têm estoque
        return true;
    }

    @Override
    // Diminui o estoque de cada produto do kit, só se todos tiverem estoque
    public void diminuirEstoque(int quantidade) {
        if (verificarEstoque(quantidade)) {
            for (Produto p : produtos) {
                p.diminuirEstoque(quantidade);
            }
        }
    }
}
