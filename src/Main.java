import java.util.Scanner;

void main() {
    System.out.println("Escolha as opções abaixo:");
    System.out.println("1 - Cadastrar");
    System.out.println("2 - Logar");
    System.out.println("3 - Sair");

    Scanner ler = new Scanner(System.in);
    System.out.print("Escolha: ");
    int escolha = ler.nextInt();


    switch (escolha){
        case 1:
            System.out.print("Qual seu nome? ");
            String nomeUsuario = ler.nextLine().trim();

            ler.nextLine();

            System.out.print("Digite sua senha: ");
            String senhaUsuario = ler.nextLine().trim();


            System.out.print("Confirme sua senha: ");
            String confirmacaoSenha = ler.nextLine().trim();

            while (!senhaUsuario.equals(confirmacaoSenha)){
                System.out.println("Senha diferente digite novamente: ");
                confirmacaoSenha = ler.nextLine().trim();
            }

            System.out.print("Digite seu email:");
            String email = ler.nextLine().trim();

            while (!email.contains("@")){

            }
        case 2:

        case 3:
            break;
    }
}