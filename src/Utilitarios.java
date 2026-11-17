public class Utilitarios {

    public static void limpar(){
        for(int i = 0; i < 50; i++){
            System.out.println(" ");
        }
    }

    public static void tempo(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
