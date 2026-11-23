package com.loja.model;

// Produto normal é a folha do Composite
// Tem preço e estoque próprio
public class ProdutoSimples extends Produto{

    private double preco;
    private int estoque;

    //Construtor que coloca dados dentro das variaveis
    public ProdutoSimples(int id, String nome, String descricao, double preco, int estoque) {
        super(id, nome, descricao);
        this.preco = preco;
        this.estoque = estoque;
    }

    //Metodos abstratos que irá ser usado para calcular o preco e mexer no estoque
    @Override
    public double calcularPreco() {
        return preco;
    }

    @Override
    public boolean verificarEstoque(int quantidade) {
        return estoque - quantidade >= 0;
    }

    @Override
    public void diminuirEstoque(int quantidade) {
        if (verificarEstoque(quantidade)) {
            estoque = estoque - quantidade;
        }
    }
}
