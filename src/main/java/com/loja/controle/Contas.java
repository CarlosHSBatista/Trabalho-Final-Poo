package com.loja.controle;

import com.loja.repositorio.ContaRepositorio;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.validator.routines.EmailValidator;

public final class Contas {
    public record Sessao(int id, String nome, boolean admin) {}

    private final ContaRepositorio repositorio = new ContaRepositorio();
    private final SecureRandom random = new SecureRandom();

    public void cadastrar(String nome, String email, String senha, String confirmacao) throws SQLException {
        email = normalizar(email);
        if (nome.isBlank() || nome.length() > 120 || email.length() > 254
                || !EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException("Informe um nome e um e-mail válidos.");
        }
        if (senha.length() < 8 || senha.length() > 128 || senha.isBlank() || !senha.equals(confirmacao)) {
            throw new IllegalArgumentException("Use de 8 a 128 caracteres e confirme a mesma senha.");
        }
        byte[] sal = new byte[16];
        random.nextBytes(sal);
        var base64 = Base64.getEncoder();
        repositorio.inserir(nome.strip(), email,
                base64.encodeToString(sal) + ":" + base64.encodeToString(hash(senha, sal)));
    }

    public Sessao entrar(String email, String senha) throws SQLException {
        var conta = repositorio.buscar(normalizar(email));
        if (conta == null || senha.length() > 128) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }
        String[] partes = conta.hash().split(":");
        var base64 = Base64.getDecoder();
        if (!MessageDigest.isEqual(base64.decode(partes[1]), hash(senha, base64.decode(partes[0])))) {
            throw new IllegalArgumentException("E-mail ou senha incorretos.");
        }
        return new Sessao(conta.id(), conta.nome(), conta.admin());
    }

    private static String normalizar(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private static byte[] hash(String senha, byte[] sal) {
        var chave = new PBEKeySpec(senha.toCharArray(), sal, 600_000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(chave).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao processar a senha.", e);
        } finally {
            chave.clearPassword();
        }
    }
}
