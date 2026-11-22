package com.loja.model;

public class ProdutoSimples extends Produto{

    private double preco;
    private int estoque;

    //Construtor que coloca dados dentro das variaveis
    public ProdutoSimples(int id, String nome, String descricao, double preco, int estoque) {
        super(id, nome, descricao);
        this.preco = preco;
        this.estoque = estoque;
    }

    //metodos abstratos que irá ser usado para calcular o preco e mexer no estoque
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
