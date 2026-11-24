package com.loja.repositorio;

import com.loja.model.CriadorProduto.Dados;
import com.loja.model.Produto;
import com.loja.model.ProdutoFactory;
import com.loja.model.TipoProduto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public final class ProdutoRepositorio {
    public int inserir(TipoProduto tipo, Produto produto, Dados dados) throws SQLException {
        try (Connection conexao = Postgres.conectar()) {
            conexao.setAutoCommit(false);
            try (var inserir = conexao.prepareStatement("""
                    INSERT INTO produto (tipo, nome, descricao, preco, estoque)
                    VALUES (?, ?, ?, ?, ?) RETURNING id
                    """)) {
                inserir.setString(1, tipo.name());
                inserir.setString(2, produto.getNome());
                inserir.setString(3, produto.getDescricao());
                inserir.setBigDecimal(4, dados.preco());
                if (tipo == TipoProduto.PRODUTOSIMPLES) {
                    inserir.setInt(5, dados.estoque());
                } else {
                    inserir.setNull(5, Types.INTEGER);
                }
                int id;
                try (var resultado = inserir.executeQuery()) {
                    resultado.next();
                    id = resultado.getInt(1);
                }
                if (tipo == TipoProduto.PRODUTOKIT) {
                    inserirComponentes(conexao, id, dados.componentes());
                }
                conexao.commit();
                return id;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        }
    }

    private void inserirComponentes(Connection conexao, int id, List<Produto> componentes)
            throws SQLException {
        try (var inserir = conexao.prepareStatement("""
                INSERT INTO kit_componente (kit_id, componente_id, posicao) VALUES (?, ?, ?)
                """)) {
            for (int posicao = 0; posicao < componentes.size(); posicao++) {
                inserir.setInt(1, id);
                inserir.setInt(2, componentes.get(posicao).getId());
                inserir.setInt(3, posicao);
                inserir.addBatch();
            }
            inserir.executeBatch();
        }
    }

    public List<Produto> buscar(String termo) throws SQLException {
        try (Connection conexao = Postgres.conectar()) {
            return buscar(conexao, termo);
        }
    }

    List<Produto> buscar(Connection conexao, String termo) throws SQLException {
        try (var consulta = conexao.prepareStatement("""
                     SELECT p.*, k.componente_id, position(lower(?) IN lower(p.nome)) > 0 AS corresponde
                     FROM produto p LEFT JOIN kit_componente k ON k.kit_id = p.id
                     ORDER BY p.id, k.posicao
                     """)) {
            consulta.setString(1, termo);
            Map<Integer, Registro> registros = new LinkedHashMap<>();
            try (var resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    int id = resultado.getInt("id");
                    Registro registro = registros.get(id);
                    if (registro == null) {
                        registro = new Registro(TipoProduto.valueOf(resultado.getString("tipo")),
                                resultado.getString("nome"), resultado.getString("descricao"),
                                resultado.getBigDecimal("preco"), resultado.getInt("estoque"),
                                resultado.getBoolean("corresponde"), new ArrayList<>());
                        registros.put(id, registro);
                    }
                    Integer componente = resultado.getObject("componente_id", Integer.class);
                    if (componente != null) {
                        registro.componentes().add(componente);
                    }
                }
            }

            Map<Integer, Produto> produtos = new LinkedHashMap<>();
            List<Produto> encontrados = new ArrayList<>();
            for (var entrada : registros.entrySet()) {
                if (entrada.getValue().corresponde()) {
                    encontrados.add(montar(entrada.getKey(), registros, produtos, new HashSet<>()));
                }
            }
            return encontrados;
        }
    }

    private Produto montar(int id, Map<Integer, Registro> registros, Map<Integer, Produto> produtos,
                           Set<Integer> caminho) throws SQLException {
        if (produtos.containsKey(id)) {
            return produtos.get(id);
        }
        if (!caminho.add(id) || !registros.containsKey(id)) {
            throw new SQLException("Composição inválida no produto " + id);
        }
        Registro registro = registros.get(id);
        List<Produto> componentes = new ArrayList<>();
        for (int componente : registro.componentes()) {
            componentes.add(montar(componente, registros, produtos, caminho));
        }
        Produto produto = ProdutoFactory.selecionar(registro.tipo()).montar(id,
                new Dados(registro.nome(), registro.descricao(), registro.preco(), registro.estoque(), componentes));
        produtos.put(id, produto);
        caminho.remove(id);
        return produto;
    }

    static void bloquearAlteracoes(Connection conexao) throws SQLException {
        try (var comando = conexao.createStatement()) {
            comando.execute("LOCK TABLE produto, kit_componente IN SHARE ROW EXCLUSIVE MODE");
        }
    }

    public void editar(int id, String nome, String descricao, BigDecimal preco, int estoque,
                       List<Integer> ids) throws SQLException {
        Postgres.transacao(conexao -> {
            bloquearAlteracoes(conexao);
            List<Produto> catalogo = buscar(conexao, "");
            Produto atual = localizar(catalogo, id);
            TipoProduto tipo = atual instanceof com.loja.model.ProdutoKit
                    ? TipoProduto.PRODUTOKIT : TipoProduto.PRODUTOSIMPLES;
            List<Produto> componentes = new ArrayList<>();
            for (int componente : ids) {
                Produto produto = localizar(catalogo, componente);
                if (contem(produto, id)) {
                    throw new IllegalArgumentException("O kit não pode conter a si mesmo, mesmo indiretamente.");
                }
                componentes.add(produto);
            }
            Produto editado = ProdutoFactory.selecionar(tipo).montar(id,
                    new Dados(nome, descricao, preco, estoque, componentes));
            try (var atualizar = conexao.prepareStatement("""
                    UPDATE produto SET nome = ?, descricao = ?, preco = ?, estoque = ? WHERE id = ?
                    """)) {
                atualizar.setString(1, editado.getNome());
                atualizar.setString(2, editado.getDescricao());
                atualizar.setBigDecimal(3, preco);
                if (tipo == TipoProduto.PRODUTOKIT) {
                    atualizar.setNull(4, Types.INTEGER);
                } else {
                    atualizar.setInt(4, estoque);
                }
                atualizar.setInt(5, id);
                atualizar.executeUpdate();
            }
            if (tipo == TipoProduto.PRODUTOKIT) {
                try (var remover = conexao.prepareStatement("DELETE FROM kit_componente WHERE kit_id = ?")) {
                    remover.setInt(1, id);
                    remover.executeUpdate();
                }
                inserirComponentes(conexao, id, componentes);
            }
            return null;
        });
    }

    public void remover(int id) throws SQLException {
        try (Connection conexao = Postgres.conectar();
             var remover = conexao.prepareStatement("DELETE FROM produto WHERE id = ?")) {
            remover.setInt(1, id);
            if (remover.executeUpdate() == 0) {
                throw new IllegalArgumentException("Produto não encontrado: " + id);
            }
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new IllegalArgumentException("Produto em uso em um kit ou carrinho.");
            }
            throw e;
        }
    }

    public static Produto localizar(List<Produto> produtos, int id) {
        return produtos.stream().filter(produto -> produto.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    private boolean contem(Produto produto, int id) {
        return produto.getId() == id || produto instanceof com.loja.model.ProdutoKit kit
                && kit.getComponentes().stream().anyMatch(componente -> contem(componente, id));
    }

    private record Registro(TipoProduto tipo, String nome, String descricao, BigDecimal preco,
                            int estoque, boolean corresponde, List<Integer> componentes) {
    }
}
