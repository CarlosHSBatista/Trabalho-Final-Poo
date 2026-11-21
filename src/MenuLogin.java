import java.util.Scanner;

public class MenuLogin {

    public String mostrarMenu(Scanner ler){
        //Menu de escolhas
        System.out.println("        --Menu--");
        System.out.println("Escolha as opções abaixo:");
        System.out.println("1 - Cadastrar");
        System.out.println("2 - Logar");
        System.out.println("0 - Sair");

        //Escolha do usuario
        System.out.print("Escolha: ");
        String escolha = ler.nextLine().trim();

        //Verifica se a escolha e 1, 2 ou 3 para não ser letras que podera quebrar o código
        while (!escolha.equals("1") && !escolha.equals("2") && !escolha.equals("0")){
            System.out.print("Escolha errada! Escolha novamente: ");
            escolha = ler.nextLine().trim();
        }

        //Devolve a String verificada
        return escolha;
    }
}
