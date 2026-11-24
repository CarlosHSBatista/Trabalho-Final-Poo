package com.loja.repositorio;

import java.sql.SQLException;

public final class ContaRepositorio {
    public record Conta(int id, String nome, String hash, boolean admin) {}

    public void inserir(String nome, String email, String hash) throws SQLException {
        try (var conexao = Postgres.conectar();
             var cmd = conexao.prepareStatement("""
                     INSERT INTO cliente (nome, email, senha_hash) VALUES (?, ?, ?)
                     ON CONFLICT (email) DO NOTHING
                     """)) {
            cmd.setString(1, nome);
            cmd.setString(2, email);
            cmd.setString(3, hash);
            if (cmd.executeUpdate() == 0) {
                throw new IllegalArgumentException("Já existe uma conta com esse e-mail.");
            }
        }
    }

    public Conta buscar(String email) throws SQLException {
        try (var conexao = Postgres.conectar();
             var cmd = conexao.prepareStatement("SELECT id, nome, senha_hash, admin FROM cliente WHERE email = ?")) {
            cmd.setString(1, email);
            try (var rs = cmd.executeQuery()) {
                return rs.next() ? new Conta(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBoolean(4))
                        : null;
            }
        }
    }
}
