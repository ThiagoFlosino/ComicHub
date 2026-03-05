-- Task #005 – Tabela de coleções físicas do utilizador
CREATE TABLE collections
(
    user_id        UUID         NOT NULL,
    item_id        UUID         NOT NULL,
    shelf_location VARCHAR(255),
    added_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT collections_pk      PRIMARY KEY (user_id, item_id),
    CONSTRAINT collections_user_fk FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT collections_item_fk FOREIGN KEY (item_id) REFERENCES items(id)
);

COMMENT ON TABLE  collections                IS 'Acervo físico de cada utilizador';
COMMENT ON COLUMN collections.shelf_location IS 'Localização física (ex: Estante 1, Prateleira 2)';
