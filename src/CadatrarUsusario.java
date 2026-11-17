import java.util.Scanner;
//biblioteca do EmailValidator
import org.apache.commons.validator.routines.EmailValidator;

public class CadatrarUsusario {

    public void mostrarCadastrar(Scanner ler){
        //Aqui to pedindo as informações tirando os espaços do começo e do final
        System.out.print("Qual seu nome? ");
        String nomeUsuario = ler.nextLine().trim();

        System.out.print("Digite sua senha: ");
        String senhaUsuario = ler.nextLine().trim();

        System.out.print("Confirme sua senha: ");
        String confirmacaoSenha = ler.nextLine().trim();

        //Aqui to verificando se a senha do usuario e a senha de confirmação são iguais
        while (!senhaUsuario.equals(confirmacaoSenha)){
            System.out.println("Senha diferente digite novamente: ");
            confirmacaoSenha = ler.nextLine().trim();
        }

        System.out.print("Digite seu email: ");
        String email = ler.nextLine().trim();

        //Aqui a variavel do tipo EmailValidator esta guardando o objeto validator
        EmailValidator validador = EmailValidator.getInstance();

        //Aqui ele recebe o email pelo metodo isValid e devolve um tru ou falce e o While ja trata o erro
        while (!validador.isValid(email)){
            System.out.print("O email está invalido digite novamente: ");
            email = ler.nextLine().trim();
        }

        System.out.println("Conta criada com susseso!");
    }
}

