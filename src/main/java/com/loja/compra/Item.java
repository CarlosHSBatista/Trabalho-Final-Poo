package com.loja.compra;

import com.loja.model.Produto;

// Linha do carrinho com o produto, a quantidade e o preço
public class Item {

    private Produto produto;
    private int quantidade;
    private double precoUnitario;

    // Não deixa criar sem produto ou com quantidade 0 e guarda o preço da hora da compra
    public Item(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("O item precisa de um produto");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade precisa ser maior que zero");
        }
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.calcularPreco();
    }


    public double calcularSubtotal() {
        return precoUnitario * quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
}
