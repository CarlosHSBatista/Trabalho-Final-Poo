package com.loja.compra;

import com.loja.frete.EstrategiaFrete;
import com.loja.model.Produto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Carrinho {
    private static int proximoId = 1;
    private List<Item> itens = new ArrayList<>();

    public void adicionarItem(Produto produto, int quantidade){
        if(quantidade <= 0){
            System.out.println("Quantidade inválida!");
            return;
        }

        for (Item item : itens){
            if(item.getProduto().getId() == produto.getId()){

                int total = item.getQuantidade() + quantidade;

                if(!produto.verificarEstoque(total)){
                    System.out.println("Não cabe no estoque!");
                    return;
                }else{
                    item.setQuantidade(total);
                }

                System.out.println("Produto adicionado com sucesso!");
                return;
            }
        }


        if(produto.verificarEstoque(quantidade)){
            Item novo = new Item(produto, quantidade);
            itens.add(novo);
            System.out.println("Produto adicionado com sucesso!");
        }else{
            System.out.println("Não cabe no estoque!");
            return;
        }
    }

    public void removerItem(Produto produto, int quantidade){
        if(quantidade <= 0){
            System.out.println("Quantidade inválida!");
            return;
        }

        for(Item item : itens){
            if(item.getProduto().getId() == produto.getId()){


                if(item.getQuantidade() < quantidade){
                    System.out.println("Você tem apenas " + item.getQuantidade() + " no carrinho!");
                    return;
                }

                if(item.getQuantidade() == quantidade){
                    itens.remove(item);
                    System.out.println("Item removido com sucesso!");
                    return;
                }

                if(item.getQuantidade() > quantidade){
                    int total = item.getQuantidade() - quantidade;
                    item.setQuantidade(total);
                    System.out.println("Removido com sucesso!");
                    return;
                }
            }
        }

        System.out.println("Esse produto não está no carrinho!");
    }

    public double calcularTotal(){
        double total = 0;

        for(Item item : itens){
            total = total + item.calcularSubtotal();
        }
        return total;
    }

    public Pedido finalizarCompra(EstrategiaFrete frete){
        //Primeiro vêm todas as verificações, nenhuma altera nada
        if(itens.isEmpty()){
            System.out.println("O carrinho está vazio!");
            return null;
        }

        if(frete == null){
            System.out.println("Frete inválido!");
            return null;
        }

        //Confere o estoque de todos os itens, porque ele pode ter mudado desde que entrou no carrinho
        for(Item item : itens){
            if(!item.getProduto().verificarEstoque(item.getQuantidade())){
                System.out.println("Não há estoque de " + item.getProduto().getNome() + "!");
                return null;
            }
        }

        //Só depois que tudo passou eu diminuo o estoque, em um laço separado
        for(Item item : itens){
            item.getProduto().diminuirEstoque(item.getQuantidade());
        }

        //Cria o pedido com os valores que o construtor atual pede e aplica o frete escolhido
        double subtotal = calcularTotal();

        Pedido pedido = new Pedido(proximoId++, LocalDate.now(), subtotal, SituacaoPedido.AGUARDANDO_PAGAMENTO, subtotal, null);
        pedido.definirFrete(frete);

        //Esvazia o carrinho depois de calcular o subtotal
        itens.clear();

        return pedido;
    }
}
