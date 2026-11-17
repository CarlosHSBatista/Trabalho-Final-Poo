import java.util.Scanner;

public class CadatrarUsusario {

    public void mostrarCadastrar(Scanner ler){
        //Aqui to pedindo as informações tirando os espaços do começo e do final
        System.out.print("Qual seu nome? ");
        String nomeUsuario = ler.nextLine().trim();

        ler.nextLine();

        System.out.print("Digite sua senha: ");
        String senhaUsuario = ler.nextLine().trim();

        System.out.print("Confirme sua senha: ");
        String confirmacaoSenha = ler.nextLine().trim();

        //Aqui to verificando se a senha do usuario e a senha de confirmação são iguais
        while (!senhaUsuario.equals(confirmacaoSenha)){
            System.out.println("Senha diferente digite novamente: ");
            confirmacaoSenha = ler.nextLine().trim();
        }

        System.out.print("Digite seu email:");
        String email = ler.nextLine().trim();

        while (!email.contains("@")){

        }
    }
}
