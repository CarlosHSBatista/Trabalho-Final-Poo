package com.loja.model;


// Fábrica que cria o produto certo de acordo com o tipo simples ou kit
public class ProdutoFactory {

    //Metodo que vai criar o Produto
    public static Produto criarProduto(TipoProduto tipo,int id, String nome, String descricao, double preco, int estoque){

        // Se não informaram o tipo, não tem como saber qual produto criar
        if (tipo == null){
            throw new IllegalArgumentException("Erro ao criar produto");
        }


        // Decide qual classe criar de acordo com o tipo (padrão Factory)
        switch (tipo){
            case PRODUTOSIMPLES :
                return new ProdutoSimples(id, nome, descricao, preco, estoque);

            case PRODUTOKIT:
                return new ProdutoKit(id, nome, descricao);

            default:
                throw new IllegalArgumentException("Erro ao criar produto");
        }


    }

}
