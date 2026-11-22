
import java.util.List;
import java.util.Scanner;

public class Login {
    //Aqui eu estou eu declaro a minha lista usuarios que e do tipo list<Usuario>
    private List<Usuario> usuarios;

    //Aqui eu guardo a referencia da minha lista na minha variavel usuarios e minha lista nao muda continua sendo a mesma coisa aqui dentro
    Login(List<Usuario> usuarios){
        this.usuarios = usuarios;
    }

    //Aqui eu criei esse metodo para ser chamado uma pergunta que fala se o usuario deseja continuar ou se quer voltar para o menu
    private void querContinuar(){
        System.out.println("Deseja continuar? ou prefere voltar para o menu? ");
        System.out.println("1 - para continuar");
        System.out.println("0 - para voltar");
    }

    //Aqui esse metodo faz o  login e devolve um true quando o usuario esta logado e um false para quando nao tiver usuario
    public boolean fazerLogin(Scanner ler){
        //Aqui eu chamo meu metodo que faz a pergunta se quer continuar
        querContinuar();
        String escolha = ler.nextLine().trim();

        //Aqui ele repete ate o usuario digitar 1 ou 0 se for diferente o laco continua rodando
        while (!escolha.equals("0") && !escolha.equals("1")){
            System.out.print("Escolha errada! Escolha novamente: ");
            escolha = ler.nextLine().trim();
        }

        //Aqui se o usuario digitar um 0 ele retorna um false para que o metodo termine e ele volta para o menu
        if(escolha.equals("0")){
            return false;
        }

        //Aqui eu pergunto o login do usuario
        System.out.println("  --Login--");
        System.out.print("Digite seu email:");
        String emailLogin = ler.nextLine().trim();

        System.out.print("Digite sua senha: ");
        String senhaLogin = ler.nextLine().trim();

        //Aqui eu estou estanciando a minha classe verificador usuario passando a minha lista de usuario como parametro o parametro e para que a classe pode ter acesso a minha lista
        VerificadorUsuario verifica = new VerificadorUsuario(usuarios);

        //Aqui eu ferifico se o email e a senha bater ela devolve o Usuario se nao ela devolve um null falando que nao tem e o resultado esta sendo quardado na minha variavel verifica
        Usuario verificado = verifica.verificaLogin(emailLogin,senhaLogin);

        //Aqui o meu while pega oque esta dentro da minha variavel verifica e compara se for null era roda o laco
        while (verificado == null){
            //Aqui eu limpo a tela para aparecer oque esta em baixo
            Utilitarios.limpar();

            System.out.println("Acesso negado tente novamente!");

            //Aqui eu pergunto o usuario e ele quer continuar
            querContinuar();
            escolha = ler.nextLine().trim();

            //aqui eu ferifico se a resposta vai ser diferente de 0 ou 1 se for ele pergunta novamente ate que seja 0 ou 1
            while (!escolha.equals("0") && !escolha.equals("1")){
                System.out.print("Escolha errada! Escolha novamente: ");
                escolha = ler.nextLine().trim();
            }

            //Aqui se a resposta do usuario for 0 ele devolve um false para parar e volta para o menu
            if(escolha.equals("0")){
                return false;
            }

            //Aqui eu pergunto novamente o login do usuario
            System.out.print("Digite seu email:");
            emailLogin = ler.nextLine().trim();

            System.out.print("Digite sua senha: ");
            senhaLogin = ler.nextLine().trim();

            //aqui eu verifico novamnete o email e a senha do usuario se voltar um null o laco sera repetido novamente ate ser diferente de null
            verificado = verifica.verificaLogin(emailLogin,senhaLogin);

        }


        System.out.println("Login feito com sucesso!");

        return true;
    }


}
