DROP TABLE ver;

CREATE TABLE app_version
(
    app_version_id   BIGSERIAL PRIMARY KEY,
    os               VARCHAR(15)  NOT NULL,
    min_version      VARCHAR(10)  NOT NULL,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL
);