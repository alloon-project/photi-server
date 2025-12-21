ALTER TABLE users
    ADD COLUMN provider VARCHAR(15)  NULL,
    ADD COLUMN sub      VARCHAR(255) NULL,
    ALTER COLUMN authentication_code DROP NOT NULL;

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS uq_email;

ALTER TABLE users
    ADD CONSTRAINT uq_user_provider_sub
        UNIQUE (provider, sub);