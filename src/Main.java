import java.util.Scanner;

import static java.lang.Thread.sleep;

void main() {
    //Cria o Scanner para ser distribuidos nas classes que utiliza o Scanner
    Scanner ler = new Scanner(System.in);

    //Declarando a classe MenuLogin
    MenuLogin menuLogin = new MenuLogin();

    //Aqui eu to crinado uma String com o valor null
    String escolha = null;

    do{
        //Aqui eu chamo meu metodo da classe MenuLogin e quardo o resultado na String escolha
        escolha = menuLogin.mostrarMenu(ler);

        Utilitarios.limpar();

        switch (escolha) {
            case "1":
                //To declarando a classe CadatrarUsuario
                CadatrarUsusario cadastro = new CadatrarUsusario();
                //Estou chamando o metodo que esta na classe CadatrarUsusario
                cadastro.mostrarCadastrar(ler);
                Utilitarios.tempo();
                Utilitarios.limpar();
                break;
            case "2":

            case "0":
                break;
        }
    //Minha condição de parada
    }while (!escolha.equals("0"));
}