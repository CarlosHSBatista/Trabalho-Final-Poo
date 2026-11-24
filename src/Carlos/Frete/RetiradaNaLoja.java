package Carlos.Frete;

//Aqui ele retira 5% do valor total se for retirada na loja
public class RetiradaNaLoja implements EstrategiaFrete {

    @Override
    public double calcular(double total){
        //Aqui ele soma a porcentagem do total e retira o resultado do total
        return total - (total * 0.05);
    }
}
