package com.loja.usuario;

public class Usuario {
    private int id;
    private String nomeUsuario;
    private String enderecoUsuario;
    private String emailUsuario;
    private String senhaUsuario;

    public String getNomeUsuario(){
        return nomeUsuario;
    }

    public String getEnderecoUsuario() {
        return enderecoUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public String getSenhaUsuario(){
        return senhaUsuario;
    }

    public void setNomeUsuario(String nomeUsuario){
        this.nomeUsuario = nomeUsuario;
    }

    public void setEnderecoUsuario(String enderecoUsuario){
        this.enderecoUsuario = enderecoUsuario;
    }

    public void setEmailUsuario(String emailUsuario){
        this.emailUsuario = emailUsuario;
    }
    public void setSenhaUsuario(String senhaUsuario){
        this.senhaUsuario = senhaUsuario;
    }
}
