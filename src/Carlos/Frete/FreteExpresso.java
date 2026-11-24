package Carlos.Frete;

//Aqui e calculado 10% do valor total para o frete normal
public class FreteExpresso implements EstrategiaFrete {

    @Override
    public double calcular(double total){
        //Aqui ele soma a porcentagem do total e adiciona o resultado no total
        return (total * 0.10) + total;
    }
}
