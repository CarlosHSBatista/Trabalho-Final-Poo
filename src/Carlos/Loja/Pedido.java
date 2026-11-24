package Carlos.Loja;

import Carlos.Frete.EstrategiaFrete;

import java.time.LocalDate;

public class Pedido {

    private int id;
    private LocalDate data;
    private double total;
    private SituacaoPedido situacao;
    private double subtotal;
    private EstrategiaFrete estrategia;

    public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public double getTotal() {
        return total;
    }

    public SituacaoPedido getSituacao() {
        return situacao;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public EstrategiaFrete getEstrategia() {
        return estrategia;
    }

    public Pedido(int id, LocalDate data, double total, SituacaoPedido situacao, double subtotal, EstrategiaFrete estrategia){
        this.id = id;
        this.data = data;
        this.total = total;
        this.situacao = situacao;
        this.subtotal = subtotal;
        this.estrategia = estrategia;
    }

    public void definirFrete(EstrategiaFrete frete){
        if(frete == null){
            System.out.println("Frete inválido!");
            return;
        }
        estrategia = frete;

        total = frete.calcular(subtotal);
    }

    public double calcularFrete(){
        return total - subtotal;
    }

    public void atualizarSituacao(SituacaoPedido novaSituacao){
        //Aqui eu recuso se a nova situação for nula
        if(novaSituacao == null){
            System.out.println("Situação inválida!");
            return;
        }

        //Começa falso e só vira true se a mudança for permitida para a situação atual
        boolean permitido = false;

        switch (situacao){
            case AGUARDANDO_PAGAMENTO:
                permitido = novaSituacao == SituacaoPedido.PAGO
                        || novaSituacao == SituacaoPedido.CANCELADO;
                break;
            case PAGO:
                permitido = novaSituacao == SituacaoPedido.ENVIADO
                        || novaSituacao == SituacaoPedido.CANCELADO;
                break;
            case ENVIADO:
                permitido = novaSituacao == SituacaoPedido.ENTREGUE;
                break;
            case ENTREGUE:
            case CANCELADO:
                //Situações finais: nenhuma mudança é aceita
                break;
        }

        if(!permitido){
            System.out.println("Não é possível mudar de " + situacao + " para " + novaSituacao + "!");
            return;
        }

        //Se chegou aqui a mudança é válida, então grava e confirma
        situacao = novaSituacao;
        System.out.println("Situação atualizada para " + novaSituacao + "!");
    }
}
