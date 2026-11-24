package com.loja.model;

import java.math.BigDecimal;

public final class CriadorProdutoSimples extends CriadorProduto {
    @Override
    protected Produto criarProduto(int id, Dados dados) {
        BigDecimal preco = dados.preco();
        if (preco == null || preco.signum() < 0 || preco.scale() > 2
                || preco.compareTo(new BigDecimal("9999999999.99")) > 0 || dados.estoque() < 0) {
            throw new IllegalArgumentException("Informe preço não negativo com até dez dígitos inteiros, duas casas decimais e estoque não negativo.");
        }
        return new ProdutoSimples(id, dados.nome(), dados.descricao(), preco.doubleValue(), dados.estoque());
    }
}
