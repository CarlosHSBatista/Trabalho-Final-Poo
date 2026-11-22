public class Utilitarios {

    //Aqui eu criei um metodo para limpar a tela ela escreve um print em 50 ou 51 linhas
    public static void limpar(){
        for(int i = 0; i < 50; i++){
            System.out.println(" ");
        }
    }

    //Aqui eu criei esse metodo para dar um tempo de 2 sengundo para que a proxima linha seja executada
    public static void tempo(){

        //Aqui o try catch esta aqui porque para que o sleep seja criado e obrigado a ter o try catch se nao nao roda
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
