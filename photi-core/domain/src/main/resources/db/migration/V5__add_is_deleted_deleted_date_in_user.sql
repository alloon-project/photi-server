ALTER TABLE users
    ADD COLUMN is_deleted   BOOLEAN      NOT NULL DEFAULT false,
    ADD COLUMN deleted_date TIMESTAMP(6) NULL;