package com.loja.compra;

import com.loja.frete.EstrategiaFrete;
import com.loja.model.Produto;
import com.loja.model.ProdutoKit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ResumoCompra(BigDecimal subtotal, BigDecimal total, Map<Produto, Long> estoque) {
    public record Linha(Produto produto, int quantidade) {
        public BigDecimal precoUnitario() {
            if (produto instanceof ProdutoKit kit) {
                return kit.getComponentes().stream().map(p -> new Linha(p, 1).precoUnitario())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            return BigDecimal.valueOf(produto.calcularPreco()).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public static ResumoCompra calcular(List<Linha> linhas, EstrategiaFrete frete) {
        if (linhas.isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio.");
        }
        Map<Produto, Long> demanda = new LinkedHashMap<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Linha linha : linhas) {
            somarEstoque(linha.produto(), linha.quantidade(), demanda);
            subtotal = subtotal.add(linha.precoUnitario().multiply(BigDecimal.valueOf(linha.quantidade())));
        }
        for (var item : demanda.entrySet()) {
            if (item.getValue() > Integer.MAX_VALUE
                    || !item.getKey().verificarEstoque(item.getValue().intValue())) {
                throw new IllegalArgumentException("Estoque insuficiente: " + item.getKey().getNome());
            }
        }
        return new ResumoCompra(subtotal, calcularTotal(subtotal, frete), demanda);
    }

    public static BigDecimal calcularTotal(BigDecimal subtotal, EstrategiaFrete frete) {
        Pedido pedido = new Pedido(0, LocalDate.now(), 0, SituacaoPedido.AGUARDANDO_PAGAMENTO,
                subtotal.doubleValue(), frete);
        pedido.definirFrete(frete);
        return BigDecimal.valueOf(pedido.getTotal()).setScale(2, RoundingMode.HALF_UP);
    }

    private static void somarEstoque(Produto produto, int quantidade, Map<Produto, Long> demanda) {
        if (produto instanceof ProdutoKit kit) {
            kit.getComponentes().forEach(componente -> somarEstoque(componente, quantidade, demanda));
        } else {
            demanda.merge(produto, (long) quantidade, Long::sum);
        }
    }
}
