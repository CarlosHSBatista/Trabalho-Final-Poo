import java.util.List;
import java.util.Scanner;

public class CadatrarUsusario {
    private List<Usuario> usuarios;

    CadatrarUsusario(List<Usuario> usuarios){
        this.usuarios = usuarios;
    }

    public void mostrarCadastrar(Scanner ler){
        //Aqui to pedindo as informações tirando os espaços do começo e do final
        System.out.println("--Cadastro--");
        System.out.print("Qual seu nome? ");
        String nomeCadastro = ler.nextLine().trim();

        System.out.print("Digite sua senha: ");
        String senhaCadastro = ler.nextLine().trim();

        System.out.print("Confirme sua senha: ");
        String confirmacaoSenha = ler.nextLine().trim();


        VerificadorUsuario verifica = new VerificadorUsuario(usuarios);
        //Aqui to verificando se a senha do usuario e a senha de confirmação são iguais
        verifica.verificadorDeSenha(senhaCadastro,confirmacaoSenha, ler);

        System.out.print("Digite seu email: ");
        String emailCadastro = ler.nextLine().trim();

        //Aqui eu ferifico se o email e valido
        emailCadastro = verifica.verificadorDeEmail(emailCadastro, ler);

        //Aqui e o construtor da classe usuario
        Usuario pessoa = new Usuario();

        //Aqui estou guardando as informações do usuario
        pessoa.setNomeUsuario(nomeCadastro);
        pessoa.setSenhaUsuario(senhaCadastro);
        pessoa.setEmailUsuario(emailCadastro);

        usuarios.add(pessoa);


        System.out.println("Conta criada com sucesso!");
    }
}

