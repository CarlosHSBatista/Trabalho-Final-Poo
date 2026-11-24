package com.loja.controle;

import com.loja.model.CriadorProduto.Dados;
import com.loja.model.Produto;
import com.loja.model.ProdutoFactory;
import com.loja.model.TipoProduto;
import com.loja.repositorio.ProdutoRepositorio;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Catalogo {
    private final ProdutoRepositorio repositorio;

    public Catalogo(ProdutoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public int cadastrarSimples(Contas.Sessao sessao, String nome, String descricao, BigDecimal preco, int estoque)
            throws SQLException {
        exigirAdmin(sessao);
        return cadastrar(TipoProduto.PRODUTOSIMPLES, new Dados(nome, descricao, preco, estoque, List.of()));
    }

    public int cadastrarKit(Contas.Sessao sessao, String nome, String descricao, List<Integer> ids) throws SQLException {
        exigirAdmin(sessao);
        List<Produto> produtos = buscar("");
        List<Produto> componentes = new ArrayList<>();
        for (int id : ids) {
            Produto componente = produtos.stream().filter(p -> p.getId() == id).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
            componentes.add(componente);
        }
        return cadastrar(TipoProduto.PRODUTOKIT, new Dados(nome, descricao, null, 0, componentes));
    }

    private int cadastrar(TipoProduto tipo, Dados dados) throws SQLException {
        Produto produto = ProdutoFactory.selecionar(tipo).montar(0, dados);
        return repositorio.inserir(tipo, produto, dados);
    }

    public List<Produto> buscar(String termo) throws SQLException {
        return repositorio.buscar(termo.strip());
    }

    public Produto obter(int id) throws SQLException {
        return ProdutoRepositorio.localizar(buscar(""), id);
    }

    public void editar(Contas.Sessao sessao, int id, String nome, String descricao,
                       BigDecimal preco, int estoque, List<Integer> componentes) throws SQLException {
        exigirAdmin(sessao);
        repositorio.editar(id, nome, descricao, preco, estoque, componentes);
    }

    public void remover(Contas.Sessao sessao, int id) throws SQLException {
        exigirAdmin(sessao);
        repositorio.remover(id);
    }

    static void exigirAdmin(Contas.Sessao sessao) {
        if (sessao == null || !sessao.admin()) {
            throw new IllegalArgumentException("Operação exclusiva do administrador.");
        }
    }
}
