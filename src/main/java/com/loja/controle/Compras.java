package com.loja.controle;

import com.loja.compra.ResumoCompra.Linha;
import com.loja.compra.ResumoCompra;
import com.loja.controle.Contas.Sessao;
import com.loja.frete.EstrategiaFrete;
import com.loja.frete.FreteExpresso;
import com.loja.frete.FreteNormal;
import com.loja.frete.RetiradaNaLoja;
import com.loja.repositorio.CompraRepositorio;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public final class Compras {
    public enum Modalidade {
        NORMAL(new FreteNormal()),
        EXPRESSO(new FreteExpresso()),
        RETIRADA_NA_LOJA(new RetiradaNaLoja());

        private final EstrategiaFrete estrategia;

        Modalidade(EstrategiaFrete estrategia) {
            this.estrategia = estrategia;
        }
    }

    private final CompraRepositorio repositorio;

    public Compras(CompraRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Linha> carrinho(Sessao sessao) throws SQLException {
        return repositorio.carrinho(sessao.id());
    }

    public void definirQuantidade(Sessao sessao, int produto, int quantidade) throws SQLException {
        repositorio.definirQuantidade(sessao.id(), produto, quantidade);
    }

    public CompraRepositorio.Confirmacao finalizar(Sessao sessao, Modalidade modalidade) throws SQLException {
        return repositorio.finalizar(sessao.id(), modalidade.name(), modalidade.estrategia);
    }

    public BigDecimal preverTotal(BigDecimal subtotal, Modalidade modalidade) {
        return ResumoCompra.calcularTotal(subtotal, modalidade.estrategia);
    }

    public List<CompraRepositorio.ItemHistorico> historico(Sessao sessao, boolean todos) throws SQLException {
        if (todos) {
            Catalogo.exigirAdmin(sessao);
            return repositorio.historico(null);
        }
        return repositorio.historico(sessao.id());
    }
}
