BEGIN;
CREATE TABLE IF NOT EXISTS cliente (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome varchar(120) NOT NULL,
    email varchar(254) NOT NULL UNIQUE,
    senha_hash text NOT NULL,
    admin boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS carrinho_item (
    cliente_id integer NOT NULL REFERENCES cliente(id),
    produto_id integer NOT NULL REFERENCES produto(id),
    quantidade integer NOT NULL CHECK (quantidade > 0),
    PRIMARY KEY (cliente_id, produto_id)
);

CREATE TABLE IF NOT EXISTS pedido (
    id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id integer NOT NULL REFERENCES cliente(id),
    criado_em timestamptz NOT NULL DEFAULT now(),
    situacao varchar(40) NOT NULL,
    modalidade varchar(40) NOT NULL,
    subtotal numeric NOT NULL CHECK (subtotal >= 0),
    total numeric NOT NULL CHECK (total >= 0)
);

CREATE TABLE IF NOT EXISTS pedido_item (
    pedido_id integer NOT NULL REFERENCES pedido(id),
    produto_id integer NOT NULL,
    nome varchar(120) NOT NULL,
    quantidade integer NOT NULL CHECK (quantidade > 0),
    preco_unitario numeric NOT NULL CHECK (preco_unitario >= 0),
    PRIMARY KEY (pedido_id, produto_id)
);
COMMIT;
