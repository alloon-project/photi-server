CREATE TABLE contact
(
    contact_id	                BIGSERIAL PRIMARY KEY,
    email	                    VARCHAR(100)	          NOT NULL,
    verification_code	        VARCHAR(6)	              NOT NULL,
    verify_yn	                BOOLEAN	                  NOT NULL,
    create_date_time	        TIMESTAMP(6)	          NOT NULL,
    update_date_time	        TIMESTAMP(6)	          NOT NULL,
    CONSTRAINT uq_contact UNIQUE (email)
);

CREATE TABLE users
(
    user_id                     BIGSERIAL PRIMARY KEY,
    username                    VARCHAR(20)               NOT NULL,
    password                    VARCHAR(255)              NOT NULL,
    image_url                   VARCHAR(500)              NOT NULL,
    temporary_password_yn       BOOLEAN                   NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    contact_id                  BIGINT                    NOT NULL,
    feed_cnt                    INT                       NOT NULL,
    CONSTRAINT fk_user_contact FOREIGN KEY (contact_id) REFERENCES contact (contact_id),
    CONSTRAINT uq_users UNIQUE (username)
);

CREATE TABLE user_role
(
    user_role_id                BIGSERIAL PRIMARY KEY,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    role                        VARCHAR(6)                NOT NULL,
    user_id                     BIGINT                    NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_template_image
(
    user_template_image_id      SERIAL PRIMARY KEY,
    image_url                   VARCHAR(500)              NOT NULL,
    start_date_time             TIMESTAMP(6)              NOT NULL,
    end_date_time               TIMESTAMP(6)              NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_user_template_image_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE challenge
(
    challenge_id                BIGSERIAL PRIMARY KEY,
    name                        VARCHAR(16)               NOT NULL,
    goal                        VARCHAR(120)              NOT NULL,
    prove_time                  TIME                      NOT NULL,
    end_date                    DATE                      NOT NULL,
    image_url                   VARCHAR(500)              NOT NULL,
    is_public                   BOOLEAN                   NOT NULL,
    start_date                  DATE                      NOT NULL,
    current_member_cnt          INT                       NOT NULL,
    visit_cnt                   INT                       NOT NULL,
    is_recruit                  BOOLEAN                   NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    hashtags                    TEXT                      NOT NULL
);
CREATE INDEX idx_challenge_hashtags ON challenge USING GIN (to_tsvector('simple', hashtags));

CREATE TABLE challenge_rule
(
    challenge_rule_id           BIGSERIAL PRIMARY KEY,
    rule                        VARCHAR(30)               NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    challenge_id                BIGINT                    NOT NULL,
    CONSTRAINT fk_challenge_rule_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);

CREATE TABLE challenge_template_image
(
    challenge_template_image_id BIGSERIAL PRIMARY KEY,
    image_url                   VARCHAR(500)              NOT NULL,
    start_date_time             TIMESTAMP(6)              NOT NULL,
    end_date_time               TIMESTAMP(6)              NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_challenge_image_template_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE challenge_member
(
    challenge_member_id         BIGSERIAL PRIMARY KEY,
    is_creator                  BOOLEAN                   NOT NULL,
    status                      VARCHAR(15)               NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    challenge_id                BIGINT                    NOT NULL,
    user_id                     BIGINT,
    goal                        VARCHAR(16),
    CONSTRAINT fk_challenge_member_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_challenge_member_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE feed
(
    feed_id                     BIGSERIAL PRIMARY KEY,
    like_cnt                    INT                       NOT NULL,
    image_url                   VARCHAR(500)              NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    challenge_id                BIGINT                    NOT NULL,
    challenge_member_id         BIGINT                    NOT NULL,
    CONSTRAINT fk_feed_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT fk_feed_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);

CREATE TABLE feed_comment
(
    feed_comment_id             BIGSERIAL PRIMARY KEY,
    comment                     VARCHAR(300),
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    feed_id                     BIGINT                    NOT NULL,
    challenge_member_id         BIGINT                    NOT NULL,
    CONSTRAINT fk_feed_comment_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_comment_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);

CREATE TABLE feed_like
(
    feed_like_id                BIGSERIAL PRIMARY KEY,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    feed_id                     BIGINT                    NOT NULL,
    challenge_member_id         BIGINT                    NOT NULL,
    CONSTRAINT fk_feed_like_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_like_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);

CREATE TABLE inquiry_category
(
    inquiry_category_id         SERIAL PRIMARY KEY,
    description                 VARCHAR(30)               NOT NULL,
    sort                        INT                       NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_inquiry_category_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE inquiry
(
    inquiry_id                  BIGSERIAL PRIMARY KEY,
    content                     VARCHAR(100)              NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    inquiry_category_id         INT                       NOT NULL,
    user_id                     BIGINT,
    CONSTRAINT fk_inquiry_inquiry_category FOREIGN KEY (inquiry_category_id) REFERENCES inquiry_category (inquiry_category_id),
    CONSTRAINT fk_inquiry_category_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE report_category
(
    report_category_id          SERIAL PRIMARY KEY,
    type                        VARCHAR(16)               NOT NULL,
    description                 VARCHAR(30)               NOT NULL,
    sort                        INT                       NOT NULL,
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_report_category_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE report
(
    report_id                   BIGSERIAL PRIMARY KEY,
    reason                      VARCHAR(120),
    create_date_time            TIMESTAMP(6)              NOT NULL,
    update_date_time            TIMESTAMP(6)              NOT NULL,
    reporter_id                 BIGINT,
    challenge_member_id         BIGINT,
    challenge_id                BIGINT,
    feed_id                     BIGINT,
    report_category_id          INT                       NOT NULL,
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id),
    CONSTRAINT fk_report_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT fk_report_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_report_report_category FOREIGN KEY (report_category_id) REFERENCES report_category(report_category_id)
);