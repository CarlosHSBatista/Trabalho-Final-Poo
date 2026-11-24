package com.loja.model;

public final class ProdutoFactory {
    private ProdutoFactory() {
    }

    public static CriadorProduto selecionar(TipoProduto tipo) {
        return switch (tipo) {
            case PRODUTOSIMPLES -> new CriadorProdutoSimples();
            case PRODUTOKIT -> new CriadorProdutoKit();
        };
    }
}
