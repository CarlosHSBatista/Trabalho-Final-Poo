# Estado da entrega

Verificação em 24/11/2026, branch local `integration`. As classes citadas estão sob `src/main/java/com/loja`.

| Frente | Estado e evidência |
|---|---|
| Exibição do total com entrega | Verificada em 25/11 na janela JavaFX. O valor aparece em uma linha própria. No tamanho mínimo, conferi o total de R$ 398,79 e um valor longo sem corte. |
| Vitrine UaiBoom | Verificada em 25/11 no JavaFX. Consultei o catálogo local pelo cabeçalho, usei o filtro Kits, naveguei até o cadastro e conferi os controles administrativos. No tamanho mínimo, o resumo do carrinho exibiu o total do frete expresso e manteve o botão de finalizar dentro da janela. Os cartões não usam imagens. |
| Total do carrinho no JavaFX | Verificado em 24/11. O subtotal dos itens permanece visível e o total é recalculado ao trocar a modalidade. Na janela, R$ 129,80 resultou em R$ 136,29 no normal, R$ 142,78 no expresso e R$ 123,31 na retirada. Carrinho vazio exibiu R$ 0,00. |
| Interface JavaFX | Verificada em 24/11 com PostgreSQL de teste em porta separada. Abri a janela e executei cadastro, login, busca, seleção de produto, carrinho, pedido, histórico e logout. Como administrador, cadastrei, editei e removi um produto e consultei todos os pedidos. O terminal continua disponível. |
| Catálogo e Factory Method | Verificado pelo menu de administrador. Cadastrei produtos simples e kit, editei um produto, busquei e removi. `controle/Catalogo` e `model/CriadorProduto` delegam a criação conforme o tipo. |
| Composite | Verificado no fluxo de compra. Um kit com dois componentes custou R$ 150. A edição para incluir o próprio kit e a remoção de produto usado em kit foram recusadas. A leitura de componentes usa `model/ProdutoKit.getComponentes`. |
| Strategy | Verificado no pedido persistente. `compra/ResumoCompra` usa `Pedido.definirFrete` com as estratégias existentes. Frete normal levou R$ 150 a R$ 157,50. A retirada mantém o desconto original de 5%. |
| PostgreSQL e camadas | Verificado com Docker usando o volume que já tinha o catálogo. Apliquei `db/02-compras.sql` e carreguei `db/exemplo.sql`. A interface chama `controle` e o JDBC fica em `repositorio`. |
| Login e carrinho individual | Verificado com duas contas de cliente e uma de administrador. Confirmei hash gravado, login em outra execução, carrinho preservado e isolamento. Cliente recebeu recusa ao tentar remover produto do catálogo e consultar todos os pedidos. |
| Pedido, estoque e histórico | Verificado com mouse avulso mais kit usando o mesmo mouse. Estoque insuficiente conservou o carrinho; após remover o avulso, o checkout gravou pedido e itens, baixou o estoque e limpou o carrinho. Em outra execução, o histórico manteve o nome e preço anteriores à edição do kit e a segunda conta não viu esse pedido. |
| Documentação e apresentação | `README.md` inclui preparação, carga do catálogo de exemplo, roteiro de teste no JavaFX com valores esperados, verificação após reiniciar e teste de administrador. Falta produzir o PDF ou DOCX da disciplina com UML, camadas, justificativa dos padrões, divisão das tarefas e detalhes da entrega. |

Para apresentar, cadastre um avulso e um kit, entre com um cliente, finalize uma compra e reabra a aplicação para mostrar o pedido. Use outra conta para demonstrar o isolamento.
