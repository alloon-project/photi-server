CREATE TABLE contact
(
    contact_id        BIGINT PRIMARY KEY,
    email             VARCHAR(100) NOT NULL,
    verification_code VARCHAR(6)   NOT NULL,
    verify_yn         BOOLEAN      NOT NULL,
    is_deleted        BOOLEAN      NOT NULL,
    create_date_time  TIMESTAMP(6) NOT NULL,
    update_date_time  TIMESTAMP(6) NOT NULL,
    deleted_date      TIMESTAMP(6) NULL,
    CONSTRAINT uq_contact UNIQUE (email)
);

CREATE TABLE users
(
    user_id               BIGINT PRIMARY KEY,
    username              VARCHAR(20)  NOT NULL,
    password              VARCHAR(255) NOT NULL,
    image_url             VARCHAR(500) NOT NULL,
    temporary_password_yn BOOLEAN      NOT NULL,
    create_date_time      TIMESTAMP(6) NOT NULL,
    update_date_time      TIMESTAMP(6) NOT NULL,
    contact_id            BIGINT       NOT NULL,
    feed_cnt              INT          NOT NULL,
    challenge_cnt         INT          NOT NULL,
    CONSTRAINT fk_user_contact FOREIGN KEY (contact_id) REFERENCES contact (contact_id),
    CONSTRAINT uq_users UNIQUE (username)
);

CREATE TABLE user_role
(
    user_role_id     BIGINT PRIMARY KEY,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    role             VARCHAR(6)   NOT NULL,
    user_id          BIGINT       NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_template_image
(
    user_template_image_id BIGINT PRIMARY KEY,
    image_url              VARCHAR(500) NOT NULL,
    start_date_time        TIMESTAMP(6) NOT NULL,
    end_date_time          TIMESTAMP(6) NOT NULL,
    create_date_time       TIMESTAMP(6) NOT NULL,
    update_date_time       TIMESTAMP(6) NOT NULL,
    service_status         VARCHAR(10)  NOT NULL,
    admin_id               BIGINT,
    CONSTRAINT fk_user_template_image_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE challenge
(
    challenge_id       BIGINT PRIMARY KEY,
    name               VARCHAR(16)  NOT NULL,
    goal               VARCHAR(120) NOT NULL,
    prove_time         TIME         NOT NULL,
    end_date           DATE         NOT NULL,
    image_url          VARCHAR(500) NOT NULL,
    is_public          BOOLEAN      NOT NULL,
    start_date         DATE         NOT NULL,
    current_member_cnt INT          NOT NULL,
    visit_cnt          INT          NOT NULL,
    invitation_code    VARCHAR(5)   NOT NULL,
    create_date_time   TIMESTAMP(6) NOT NULL,
    update_date_time   TIMESTAMP(6) NOT NULL,
    service_status     VARCHAR(10)  NOT NULL
);

CREATE TABLE challenge_rule
(
    challenge_rule_id BIGINT PRIMARY KEY,
    rule              VARCHAR(30)  NOT NULL,
    create_date_time  TIMESTAMP(6) NOT NULL,
    update_date_time  TIMESTAMP(6) NOT NULL,
    service_status    VARCHAR(10)  NOT NULL,
    challenge_id      BIGINT       NOT NULL,
    CONSTRAINT fk_challenge_rule_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);

CREATE TABLE challenge_hashtag
(
    challenge_hashtag_id BIGINT PRIMARY KEY,
    hashtag              VARCHAR(6)   NOT NULL,
    create_date_time     TIMESTAMP(6) NOT NULL,
    update_date_time     TIMESTAMP(6) NOT NULL,
    service_status       VARCHAR(10)  NOT NULL,
    challenge_id         BIGINT       NOT NULL,
    CONSTRAINT fk_challenge_hashtag_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);

CREATE TABLE challenge_template_image
(
    challenge_template_image_id BIGINT PRIMARY KEY,
    image_url                   VARCHAR(500) NOT NULL,
    start_date_time             TIMESTAMP(6) NOT NULL,
    end_date_time               TIMESTAMP(6) NOT NULL,
    create_date_time            TIMESTAMP(6) NOT NULL,
    update_date_time            TIMESTAMP(6) NOT NULL,
    service_status              VARCHAR(10)  NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_challenge_image_template_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE challenge_member
(
    challenge_member_id BIGINT PRIMARY KEY,
    is_creator          BOOLEAN      NOT NULL,
    status              VARCHAR(15)  NOT NULL,
    create_date_time    TIMESTAMP(6) NOT NULL,
    update_date_time    TIMESTAMP(6) NOT NULL,
    service_status      VARCHAR(10)  NOT NULL,
    challenge_id        BIGINT       NOT NULL,
    user_id             BIGINT,
    goal                VARCHAR(16),
    CONSTRAINT fk_challenge_member_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_challenge_member_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE feed
(
    feed_id             BIGINT PRIMARY KEY,
    like_cnt            INT          NOT NULL,
    comment_cnt         INT          NOT NULL,
    image_url           VARCHAR(500) NOT NULL,
    create_date_time    TIMESTAMP(6) NOT NULL,
    update_date_time    TIMESTAMP(6) NOT NULL,
    service_status      VARCHAR(10)  NOT NULL,
    challenge_id        BIGINT       NOT NULL,
    challenge_member_id BIGINT       NOT NULL,
    CONSTRAINT fk_feed_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT fk_feed_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);

CREATE TABLE feed_comment
(
    feed_comment_id     BIGINT PRIMARY KEY,
    comment             VARCHAR(300),
    create_date_time    TIMESTAMP(6) NOT NULL,
    update_date_time    TIMESTAMP(6) NOT NULL,
    service_status      VARCHAR(10)  NOT NULL,
    feed_id             BIGINT       NOT NULL,
    challenge_member_id BIGINT       NOT NULL,
    CONSTRAINT fk_feed_comment_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_comment_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);

CREATE TABLE feed_like
(
    feed_like_id        BIGINT PRIMARY KEY,
    create_date_time    TIMESTAMP(6) NOT NULL,
    update_date_time    TIMESTAMP(6) NOT NULL,
    service_status      VARCHAR(10)  NOT NULL,
    feed_id             BIGINT       NOT NULL,
    challenge_member_id BIGINT       NOT NULL,
    CONSTRAINT fk_feed_like_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_like_challenge_member FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);

CREATE TABLE inquiry_category
(
    inquiry_category_id BIGINT PRIMARY KEY,
    description         VARCHAR(30)  NOT NULL,
    sort                INT          NOT NULL,
    create_date_time    TIMESTAMP(6) NOT NULL,
    update_date_time    TIMESTAMP(6) NOT NULL,
    service_status      VARCHAR(10)  NOT NULL,
    admin_id            BIGINT,
    CONSTRAINT fk_inquiry_category_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE inquiry
(
    inquiry_id       BIGINT PRIMARY KEY,
    type             VARCHAR(15)  NOT NULL,
    content          VARCHAR(120) NOT NULL,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    user_id          BIGINT,
    CONSTRAINT fk_inquiry_category_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE report_category
(
    report_category_id BIGINT PRIMARY KEY,
    type               VARCHAR(16)  NOT NULL,
    description        VARCHAR(30)  NOT NULL,
    sort               INT          NOT NULL,
    create_date_time   TIMESTAMP(6) NOT NULL,
    update_date_time   TIMESTAMP(6) NOT NULL,
    service_status     VARCHAR(10)  NOT NULL,
    admin_id           BIGINT,
    CONSTRAINT fk_report_category_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE report
(
    report_id        BIGINT PRIMARY KEY,
    reporter_id      BIGINT       NOT NULL,
    target_id        BIGINT       NOT NULL,
    category         VARCHAR(15)  NOT NULL,
    reason           VARCHAR(15)  NOT NULL,
    content          VARCHAR(120) NULL,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL
);

CREATE TABLE suspension
(
    suspend_id       BIGINT PRIMARY KEY,
    start_date       DATE         NOT NULL,
    end_date         DATE         NOT NULL,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    user_id          BIGINT,
    admin_id         BIGINT,
    CONSTRAINT fk_suspension_admin FOREIGN KEY (admin_id) REFERENCES users (user_id),
    CONSTRAINT fk_suspension_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE block
(
    block_id         BIGINT PRIMARY KEY,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    service_status   VARCHAR(10)  NOT NULL,
    user_id          BIGINT,
    blocker_id       BIGINT,
    CONSTRAINT fk_bock_blocker FOREIGN KEY (blocker_id) REFERENCES users (user_id),
    CONSTRAINT fk_bock_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE ver
(
    ver_id           BIGINT PRIMARY KEY,
    version          VARCHAR(10)  NOT NULL,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    admin_id         BIGINT,
    CONSTRAINT fk_app_version_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

-- Autogenerated: do not edit this file

CREATE TABLE BATCH_JOB_INSTANCE
(
    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,
    VERSION         BIGINT,
    JOB_NAME        VARCHAR(100) NOT NULL,
    JOB_KEY         VARCHAR(32)  NOT NULL,
    constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION
(
    JOB_EXECUTION_ID BIGINT    NOT NULL PRIMARY KEY,
    VERSION          BIGINT,
    JOB_INSTANCE_ID  BIGINT    NOT NULL,
    CREATE_TIME      TIMESTAMP NOT NULL,
    START_TIME       TIMESTAMP DEFAULT NULL,
    END_TIME         TIMESTAMP DEFAULT NULL,
    STATUS           VARCHAR(10),
    EXIT_CODE        VARCHAR(2500),
    EXIT_MESSAGE     VARCHAR(2500),
    LAST_UPDATED     TIMESTAMP,
    constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
        references BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    JOB_EXECUTION_ID BIGINT       NOT NULL,
    PARAMETER_NAME   VARCHAR(100) NOT NULL,
    PARAMETER_TYPE   VARCHAR(100) NOT NULL,
    PARAMETER_VALUE  VARCHAR(2500),
    IDENTIFYING      CHAR(1)      NOT NULL,
    constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION
(
    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,
    VERSION            BIGINT       NOT NULL,
    STEP_NAME          VARCHAR(100) NOT NULL,
    JOB_EXECUTION_ID   BIGINT       NOT NULL,
    CREATE_TIME        TIMESTAMP    NOT NULL,
    START_TIME         TIMESTAMP DEFAULT NULL,
    END_TIME           TIMESTAMP DEFAULT NULL,
    STATUS             VARCHAR(10),
    COMMIT_COUNT       BIGINT,
    READ_COUNT         BIGINT,
    FILTER_COUNT       BIGINT,
    WRITE_COUNT        BIGINT,
    READ_SKIP_COUNT    BIGINT,
    WRITE_SKIP_COUNT   BIGINT,
    PROCESS_SKIP_COUNT BIGINT,
    ROLLBACK_COUNT     BIGINT,
    EXIT_CODE          VARCHAR(2500),
    EXIT_MESSAGE       VARCHAR(2500),
    LAST_UPDATED       TIMESTAMP,
    constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
        references BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,
    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,
    SERIALIZED_CONTEXT TEXT,
    constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
        references BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
);

CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
CREATE SEQUENCE BATCH_JOB_SEQ MAXVALUE 9223372036854775807 NO CYCLE;
