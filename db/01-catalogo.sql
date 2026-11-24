CREATE TABLE produto (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo varchar(20) NOT NULL,
    nome varchar(120) NOT NULL CHECK (btrim(nome) <> ''),
    descricao varchar(500) NOT NULL,
    preco numeric(12, 2),
    estoque integer,
    CHECK (
        (tipo = 'PRODUTOSIMPLES' AND preco IS NOT NULL AND preco >= 0
            AND estoque IS NOT NULL AND estoque >= 0)
        OR (tipo = 'PRODUTOKIT' AND preco IS NULL AND estoque IS NULL)
    )
);

CREATE TABLE kit_componente (
    kit_id integer NOT NULL REFERENCES produto(id) ON DELETE CASCADE,
    componente_id integer NOT NULL REFERENCES produto(id),
    posicao integer NOT NULL CHECK (posicao >= 0),
    PRIMARY KEY (kit_id, posicao),
    CHECK (kit_id <> componente_id)
);
