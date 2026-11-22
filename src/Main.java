import java.util.Scanner;



void main() {
    //Cria o Scanner para ser distribuidos nas classes que utiliza o Scanner
    Scanner ler = new Scanner(System.in);

    //Declarando a classe MenuLogin
    MenuLogin menuLogin = new MenuLogin();

    //Aqui eu to crinado uma String com o valor null
    String escolha = null;

    //Aqui eu inicio a minha lista vazia
    ArrayList<Usuario> usuarios = new ArrayList<>();

    do{
        //Aqui eu chamo meu metodo da classe MenuLogin e quardo o resultado na String escolha
        escolha = menuLogin.mostrarMenu(ler);

        Utilitarios.limpar();

        switch (escolha) {
            case "1":
                //To declarando a classe CadatrarUsuario
                CadatrarUsusario cadastro = new CadatrarUsusario(usuarios);

                //Estou chamando o metodo que esta na classe CadatrarUsusario
                cadastro.mostrarCadastrar(ler);
                Utilitarios.tempo();
                Utilitarios.limpar();
                break;
            case "2":
                //Aqui eu declaro a classe login passando como parametro a minha lista para que ela poder acessar a lista
                Login logar = new Login(usuarios);

                //Aqui eu chamo meu metodo que esta na classe Login
                logar.fazerLogin(ler);
                Utilitarios.tempo();
                Utilitarios.limpar();
                break;
            case "0":
                break;
        }
    //Minha condição de parada enquanto for diferente de 0
    }while (!escolha.equals("0"));
}