---
name: loja-tema-4
description: Orienta implementações e revisões da loja do Tema 4 no repositório Trabalho-Final-Poo e acompanha as pendências de catálogo, carrinho, pedidos, padrões de projeto e entrega da disciplina.
---

# Loja do Tema 4

Antes de planejar ou implementar, leia o [estado da entrega](references/estado.md). Confira a branch atual, as alterações locais e os arquivos da funcionalidade. O quadro descreve a última verificação e pode ter ficado atrás do trabalho dos colegas.

## Decisões do projeto

- O enunciado aceita terminal. A loja também oferece JavaFX em `com.loja.view.grafica.LauncherGrafico`. O terminal continua em `com.loja.Main`. Ambos usam JDK 17 ou superior e compartilham os serviços e os dados.
- O tema exige catálogo, carrinho e pedidos. Para a experiência multiusuário escolhida, use a identidade obtida no login para localizar o carrinho e os pedidos do cliente.
- A persistência escolhida é PostgreSQL em Docker com volume. Mantenha o acesso JDBC nos repositórios, as operações da loja na camada de controle e as interações com o terminal na visualização.
- Para Factory Method, mostre o cadastro real delegando a construção aos criadores concretos de produto simples e kit. O enum pode selecionar o criador com um switch. O Composite deve permitir usar produtos avulsos e kits no mesmo carrinho. O Strategy adotado atua no frete do pedido.

## Manutenção a cada implementação

1. Localize no quadro a linha que a mudança atende. Use o critério de aceite para escolher uma verificação proporcional. Se a tarefa for apenas uma análise, relate as diferenças encontradas.
2. Ao implementar, exercite o caminho alterado pelo menu, pela tela ou por um teste da operação. Para persistência, encerre e abra outra instância da aplicação e consulte os dados. Para isolamento, use dois clientes distintos.
3. Na mesma alteração, atualize a linha afetada em `references/estado.md` com data, estado, classe ou arquivo relevante e resultado observado. Se só uma parte funciona, registre o que falta e a verificação ainda pendente. Marque como verificado apenas o comportamento demonstrado.
4. Revise este arquivo quando o grupo mudar uma decisão duradoura, como a interface ou o padrão escolhido. Mantenha o progresso cotidiano no quadro. Ao relatar a entrega, indique o que foi testado e a próxima lacuna relacionada.
