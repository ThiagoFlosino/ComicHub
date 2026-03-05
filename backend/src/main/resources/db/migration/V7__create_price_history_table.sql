CREATE TABLE price_history (
    id         UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id    UUID                     NOT NULL,
    store_id   VARCHAR(100)             NOT NULL,
    price      NUMERIC(10, 2)           NOT NULL,
    currency   VARCHAR(3)               NOT NULL DEFAULT 'USD',
    checked_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT price_history_item_fk FOREIGN KEY (item_id) REFERENCES items(id)
);
CREATE INDEX price_history_item_idx ON price_history(item_id, checked_at DESC);
