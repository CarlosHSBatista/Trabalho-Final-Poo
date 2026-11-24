package com.loja.usuario;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.Scanner;

public class VerificadorUsuario {
    //Aqui eu declaro minha lista usuarios do tipo list<Usuario>
    private List<Usuario> usuarios;

    //Aqui eu guardo a minha veferencia da minha lista na variavel usuario aqui dentro a minha lista nao muda continua sendo a mesma
    VerificadorUsuario(List<Usuario> usuarios){
        this.usuarios = usuarios;
    }

    //Aqui esse metodo confere se a senha esta batendo
    public void verificadorDeSenha(String senhaUsuario, String senhaDeConfirmacao, Scanner ler){

        //Aqui enquanto a minha senhaUsuario e a minha senhaDeConfirmacao nao baterem ela pergunta novamente ate que seja igual as duas
        while (!senhaUsuario.equals(senhaDeConfirmacao)){
            System.out.print("Senha diferente digite novamente: ");
            senhaDeConfirmacao = ler.nextLine().trim();
        }
    }

    //Aqui esse metodo valida meu email se tem um @ e um .
    public String verificadorDeEmail(String emailCadastro, Scanner ler){

        //Aqui a variavel do tipo EmailValidator esta guardando o objeto validator
        EmailValidator validador = EmailValidator.getInstance();

        //Aqui ele recebe o email pelo metodo isValid e devolve um tru ou falce e o While ja trata o erro
        //O isValid devolve true ou falce
        while (!validador.isValid(emailCadastro) || emailJaExixte(emailCadastro)){
            System.out.println("Email errado ou ja existe");
            System.out.print("Digite novamente: ");
            emailCadastro = ler.nextLine().trim();
        }

        //Aqui ele devolve o email ja validado
        return emailCadastro;
    }

    //Aqui esse metodo verifica meu email e senha se esta na minha lista usuarios
    public Usuario verificaLogin(String email, String senha) {

        //Aqui o usuario assume oque esta em usuarios na lista e a cada vez que repete ele asume o proximo
        for (Usuario usuario : usuarios) {
            //Aqui ele confere se a senha digitada e o email bate com a senha e o email do usuario dentro da lista
            if (email.equals(usuario.getEmailUsuario()) && senha.equals(usuario.getSenhaUsuario())) {
                return usuario;
            }
        }
        return null;
    }

    //Aqui esse metodo verifica se tem dois email igual
    public boolean emailJaExixte(String email){

        //Aqui a variavel usuario do tipo Usuario assume oque esta dentro da lista pelo usuarios e a cada repeticao vai seguindo a proximo da lista
        for(Usuario usuario : usuarios){
            //Aqui ele pega o email digitado e confere se e igual ao email do usuario
            if(email.equals(usuario.getEmailUsuario())){
                return true;
            }
        }

        return false;
    }
}
