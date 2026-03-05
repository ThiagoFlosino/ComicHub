-- Task #005 (complemento) – Adiciona coluna de status ao acervo
ALTER TABLE collections
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'OWNED';

COMMENT ON COLUMN collections.status IS 'Status do item: OWNED | READING | LENT | READ';
