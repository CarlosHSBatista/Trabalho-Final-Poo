import java.util.Scanner;

void main() {
    System.out.println("Escolha as opções abaixo:");
    System.out.println("1 - Cadastrar");
    System.out.println("2 - Logar");
    System.out.println("3 - Sair");

    Scanner ler = new Scanner(System.in);
    System.out.println("Escolha: ");
    int escolha = ler.nextInt();


    switch (escolha){
        case 1:
            System.out.println("Qual seu nome? ");
            String nomeUsuario = ler.nextLine().trim();

            System.out.println("Digite sua senha: ");
            String senhaUsuario =
        case 2:

        case 3:
            break;
    }
}