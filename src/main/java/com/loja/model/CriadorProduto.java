package com.loja.model;

import java.math.BigDecimal;
import java.util.List;

public abstract class CriadorProduto {
    public record Dados(String nome, String descricao, BigDecimal preco, int estoque,
                        List<Produto> componentes) {
    }

    public final Produto montar(int id, Dados dados) {
        String nome = dados.nome().strip();
        String descricao = dados.descricao().strip();
        if (nome.isEmpty() || nome.length() > 120 || descricao.length() > 500) {
            throw new IllegalArgumentException("Informe um nome de 1 a 120 caracteres e uma descrição de até 500.");
        }
        return criarProduto(id, new Dados(nome, descricao, dados.preco(), dados.estoque(),
                dados.componentes()));
    }

    protected abstract Produto criarProduto(int id, Dados dados);
}
