WITH simples AS (
    INSERT INTO produto (tipo, nome, descricao, preco, estoque) VALUES
        ('PRODUTOSIMPLES', 'Mouse USB', 'Mouse óptico', 49.90, 10),
        ('PRODUTOSIMPLES', 'Teclado USB', 'Teclado ABNT2', 79.90, 10)
    RETURNING id
), kit AS (
    INSERT INTO produto (tipo, nome, descricao) VALUES
        ('PRODUTOKIT', 'Kit escritório', 'Mouse e teclado USB')
    RETURNING id
)
INSERT INTO kit_componente (kit_id, componente_id, posicao)
SELECT kit.id, simples.id, (row_number() OVER (ORDER BY simples.id) - 1)::integer
FROM kit CROSS JOIN simples;
