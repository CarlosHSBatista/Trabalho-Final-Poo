import org.apache.commons.validator.routines.EmailValidator;

import java.util.List;
import java.util.Scanner;

public class VerificadorUsuario {
    private List<Usuario> usuarios;

    VerificadorUsuario(List<Usuario> usuarios){
        this.usuarios = usuarios;
    }

    public void verificadorDeSenha(String senhaUsuario, String senhaDeConfirmacao, Scanner ler){


        while (!senhaUsuario.equals(senhaDeConfirmacao)){
            System.out.print("Senha diferente digite novamente: ");
            senhaDeConfirmacao = ler.nextLine().trim();
        }
    }

    public String verificadorDeEmail(String emailCadastro, Scanner ler){

        //Aqui a variavel do tipo EmailValidator esta guardando o objeto validator
        EmailValidator validador = EmailValidator.getInstance();

        //Aqui ele recebe o email pelo metodo isValid e devolve um tru ou falce e o While ja trata o erro
        while (!validador.isValid(emailCadastro)){
            System.out.print("O email está invalido digite novamente: ");
            emailCadastro = ler.nextLine().trim();
        }

        return emailCadastro;
    }

    public Usuario verificaLogin(String email, String senha) {


        for (Usuario usuario : usuarios) {
            if (email.equals(usuario.getEmailUsuario()) && senha.equals(usuario.getSenhaUsuario())) {
                return usuario;
            }
        }
        return null;
    }
}
