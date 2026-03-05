ALTER TABLE items
    ADD COLUMN author   VARCHAR(255),
    ADD COLUMN synopsis TEXT;

COMMENT ON COLUMN items.author   IS 'Autor(es) do quadrinho/mangá';
COMMENT ON COLUMN items.synopsis IS 'Sinopse da obra';
