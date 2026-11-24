package com.loja.repositorio;

import com.loja.compra.ResumoCompra;
import com.loja.compra.ResumoCompra.Linha;
import com.loja.compra.SituacaoPedido;
import com.loja.frete.EstrategiaFrete;
import com.loja.model.Produto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CompraRepositorio {
    public record Confirmacao(int id, BigDecimal total) {}
    public record ItemHistorico(int pedido, OffsetDateTime data, String situacao, String modalidade,
                                BigDecimal total, String produto, int quantidade, BigDecimal preco) {}

    private final ProdutoRepositorio produtos;

    public CompraRepositorio(ProdutoRepositorio produtos) {
        this.produtos = produtos;
    }

    private void bloquearCliente(Connection conexao, int cliente) throws SQLException {
        try (var cmd = conexao.prepareStatement("SELECT id FROM cliente WHERE id = ? FOR UPDATE")) {
            cmd.setInt(1, cliente);
            try (var rs = cmd.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Entre novamente na sua conta.");
                }
            }
        }
    }

    public void definirQuantidade(int cliente, int produto, int quantidade) throws SQLException {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Informe uma quantidade não negativa.");
        }
        Postgres.transacao(conexao -> {
            ProdutoRepositorio.bloquearAlteracoes(conexao);
            bloquearCliente(conexao, cliente);
            if (quantidade == 0) {
                try (var cmd = conexao.prepareStatement("""
                        DELETE FROM carrinho_item WHERE cliente_id = ? AND produto_id = ?
                        """)) {
                    cmd.setInt(1, cliente);
                    cmd.setInt(2, produto);
                    cmd.executeUpdate();
                }
                return null;
            }
            ProdutoRepositorio.localizar(produtos.buscar(conexao, ""), produto);
            try (var cmd = conexao.prepareStatement("""
                    INSERT INTO carrinho_item (cliente_id, produto_id, quantidade) VALUES (?, ?, ?)
                    ON CONFLICT (cliente_id, produto_id) DO UPDATE SET quantidade = excluded.quantidade
                    """)) {
                cmd.setInt(1, cliente);
                cmd.setInt(2, produto);
                cmd.setInt(3, quantidade);
                cmd.executeUpdate();
            }
            return null;
        });
    }

    public List<Linha> carrinho(int cliente) throws SQLException {
        return Postgres.transacao(conexao -> {
            conexao.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return carrinho(conexao, cliente);
        });
    }

    private List<Linha> carrinho(Connection conexao, int cliente) throws SQLException {
        var catalogo = produtos.buscar(conexao, "").stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));
        List<Linha> linhas = new ArrayList<>();
        try (var cmd = conexao.prepareStatement("""
                SELECT produto_id, quantidade FROM carrinho_item WHERE cliente_id = ? ORDER BY produto_id
                """)) {
            cmd.setInt(1, cliente);
            try (var rs = cmd.executeQuery()) {
                while (rs.next()) {
                    linhas.add(new Linha(catalogo.get(rs.getInt(1)), rs.getInt(2)));
                }
            }
        }
        return linhas;
    }

    public Confirmacao finalizar(int cliente, String modalidade, EstrategiaFrete frete) throws SQLException {
        return Postgres.transacao(conexao -> {
            ProdutoRepositorio.bloquearAlteracoes(conexao);
            bloquearCliente(conexao, cliente);
            List<Linha> linhas = carrinho(conexao, cliente);
            ResumoCompra resumo = ResumoCompra.calcular(linhas, frete);
            int pedido = gravarPedido(conexao, cliente, modalidade, resumo);
            gravarItens(conexao, pedido, linhas);
            try (var cmd = conexao.prepareStatement("UPDATE produto SET estoque = estoque - ? WHERE id = ?")) {
                for (var item : resumo.estoque().entrySet()) {
                    cmd.setInt(1, item.getValue().intValue());
                    cmd.setInt(2, item.getKey().getId());
                    cmd.addBatch();
                }
                cmd.executeBatch();
            }
            try (var cmd = conexao.prepareStatement("DELETE FROM carrinho_item WHERE cliente_id = ?")) {
                cmd.setInt(1, cliente);
                cmd.executeUpdate();
            }
            return new Confirmacao(pedido, resumo.total());
        });
    }

    private int gravarPedido(Connection conexao, int cliente, String modalidade, ResumoCompra resumo)
            throws SQLException {
        try (var cmd = conexao.prepareStatement("""
                INSERT INTO pedido (cliente_id, situacao, modalidade, subtotal, total)
                VALUES (?, ?, ?, ?, ?) RETURNING id
                """)) {
            cmd.setInt(1, cliente);
            cmd.setString(2, SituacaoPedido.AGUARDANDO_PAGAMENTO.name());
            cmd.setString(3, modalidade);
            cmd.setBigDecimal(4, resumo.subtotal());
            cmd.setBigDecimal(5, resumo.total());
            try (var rs = cmd.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void gravarItens(Connection conexao, int pedido, List<Linha> linhas) throws SQLException {
        try (var cmd = conexao.prepareStatement("""
                INSERT INTO pedido_item (pedido_id, produto_id, nome, quantidade, preco_unitario)
                VALUES (?, ?, ?, ?, ?)
                """)) {
            for (Linha linha : linhas) {
                cmd.setInt(1, pedido);
                cmd.setInt(2, linha.produto().getId());
                cmd.setString(3, linha.produto().getNome());
                cmd.setInt(4, linha.quantidade());
                cmd.setBigDecimal(5, linha.precoUnitario());
                cmd.addBatch();
            }
            cmd.executeBatch();
        }
    }

    public List<ItemHistorico> historico(Integer cliente) throws SQLException {
        String filtro = cliente == null ? "" : " WHERE p.cliente_id = ?";
        List<ItemHistorico> itens = new ArrayList<>();
        try (var conexao = Postgres.conectar();
             var cmd = conexao.prepareStatement("""
                     SELECT p.id, p.criado_em, p.situacao, p.modalidade, p.total,
                            i.nome, i.quantidade, i.preco_unitario
                     FROM pedido p JOIN pedido_item i ON i.pedido_id = p.id
                     """ + filtro + " ORDER BY p.id DESC, i.produto_id")) {
            if (cliente != null) {
                cmd.setInt(1, cliente);
            }
            try (var rs = cmd.executeQuery()) {
                while (rs.next()) {
                    itens.add(new ItemHistorico(rs.getInt(1), rs.getObject(2, OffsetDateTime.class),
                            rs.getString(3), rs.getString(4), rs.getBigDecimal(5), rs.getString(6),
                            rs.getInt(7), rs.getBigDecimal(8)));
                }
            }
        }
        return itens;
    }
}
