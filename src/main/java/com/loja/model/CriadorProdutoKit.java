package com.loja.model;

public final class CriadorProdutoKit extends CriadorProduto {
    @Override
    protected Produto criarProduto(int id, Dados dados) {
        if (dados.componentes().isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um componente do kit.");
        }
        ProdutoKit kit = new ProdutoKit(id, dados.nome(), dados.descricao());
        dados.componentes().forEach(kit::adicionarProduto);
        return kit;
    }
}
