package com.loja.model;

// Classe mãe de todos os produtos
// No Composite ela é o componente e na Factory é o que a fábrica devolve
public abstract class Produto {
    protected int id;
    protected String nome;
    protected String descricao;

    //Construtor que ira armazenar os dados do Produto
    public Produto(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    //Metodos abstratos que irá ser usado para calcular o preco e mexer no estoque
    public abstract double calcularPreco();

    public abstract boolean verificarEstoque(int quantidade);

    public abstract void diminuirEstoque(int quantidade);

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

}
