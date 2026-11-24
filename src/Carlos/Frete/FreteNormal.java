package Carlos.Frete;

//Aqui e calculado 5% do valor total para o frete normal
public class FreteNormal implements EstrategiaFrete {

    @Override
    public  double calcular(double total){
        //Aqui ele soma a porcentagem do total e adiciona o resultado no total
        return (total * 0.05) + total;
    }
}
