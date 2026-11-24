package com.loja.repositorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

final class Postgres {
    private Postgres() {
    }

    static Connection conectar() throws SQLException {
        String porta = System.getenv().getOrDefault("LOJA_DB_PORT", "55432");
        String senha = System.getenv().getOrDefault("LOJA_DB_PASSWORD", "loja_dev");
        return DriverManager.getConnection("jdbc:postgresql://127.0.0.1:" + porta
                + "/loja?connectTimeout=3", "loja", senha);
    }

    static <T> T transacao(Operacao<T> operacao) throws SQLException {
        try (Connection conexao = conectar()) {
            conexao.setAutoCommit(false);
            try {
                T resultado = operacao.executar(conexao);
                conexao.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                try {
                    conexao.rollback();
                } catch (SQLException falha) {
                    e.addSuppressed(falha);
                }
                throw e;
            }
        }
    }

    interface Operacao<T> {
        T executar(Connection conexao) throws SQLException;
    }
}
