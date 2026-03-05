CREATE TABLE wishlists (
    user_id      UUID           NOT NULL,
    item_id      UUID           NOT NULL,
    target_price NUMERIC(10, 2),
    added_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT wishlists_pk      PRIMARY KEY (user_id, item_id),
    CONSTRAINT wishlists_user_fk FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT wishlists_item_fk FOREIGN KEY (item_id) REFERENCES items(id)
);
