
import java.util.List;
import java.util.Scanner;

public class Login {

    private List<Usuario> usuarios;

    Login(List<Usuario> usuarios){
        this.usuarios = usuarios;
    }

    private void querContinuar(){
        System.out.println("Deseja continuar? ou prefere voltar para o menu? ");
        System.out.println("1 - para continuar");
        System.out.println("0 - para voltar");
    }

    public boolean fazerLogin(Scanner ler){

        querContinuar();
        String escolha = ler.nextLine().trim();

        while (!escolha.equals("0") && !escolha.equals("1")){
            System.out.print("Escolha errada! Escolha novamente: ");
            escolha = ler.nextLine().trim();
        }

        if(escolha.equals("0")){
            return false;
        }

        System.out.println("  --Login--");
        System.out.print("Digite seu email:");
        String emailLogin = ler.nextLine().trim();

        System.out.print("Digite sua senha: ");
        String senhaLogin = ler.nextLine().trim();


        VerificadorUsuario verifica = new VerificadorUsuario(usuarios);


        Usuario verificado = verifica.verificaLogin(emailLogin,senhaLogin);

        while (verificado == null){
            Utilitarios.limpar();

            System.out.println("Acesso negado tente novamente!");

            querContinuar();
            escolha = ler.nextLine().trim();

            while (!escolha.equals("0") && !escolha.equals("1")){
                System.out.print("Escolha errada! Escolha novamente: ");
                escolha = ler.nextLine().trim();
            }

            if(escolha.equals("0")){
                return false;
            }

            System.out.print("Digite seu email:");
            emailLogin = ler.nextLine().trim();

            System.out.print("Digite sua senha: ");
            senhaLogin = ler.nextLine().trim();

            verificado = verifica.verificaLogin(emailLogin,senhaLogin);

        }


        System.out.println("Login feito com sucesso!");

        return true;
    }


}
