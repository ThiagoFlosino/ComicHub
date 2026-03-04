-- Task #001 – Migração inicial: tabela de itens do catálogo
CREATE TABLE items
(
    id          UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    isbn        VARCHAR(13)              NOT NULL,
    title       VARCHAR(500)             NOT NULL,
    publisher   VARCHAR(255),
    series      VARCHAR(255),
    volume      INTEGER,
    variant     VARCHAR(255),
    cover_image VARCHAR(500),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL     DEFAULT NOW(),

    CONSTRAINT items_isbn_unique UNIQUE (isbn)
);

COMMENT ON TABLE  items              IS 'Catálogo central de quadrinhos e mangás';
COMMENT ON COLUMN items.isbn        IS 'EAN-13 do item (barcode)';
COMMENT ON COLUMN items.cover_image IS 'Caminho S3 da capa otimizada em WebP';
