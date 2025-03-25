DROP TABLE IF EXISTS ver;
DROP TABLE IF EXISTS block;
DROP TABLE IF EXISTS suspension;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS report_category;
DROP TABLE IF EXISTS inquiry;
DROP TABLE IF EXISTS inquiry_category;
DROP TABLE IF EXISTS feed_like;
DROP TABLE IF EXISTS feed_comment;
DROP TABLE IF EXISTS feed;
DROP TABLE IF EXISTS challenge_member;
DROP TABLE IF EXISTS challenge_template_image;
DROP TABLE IF EXISTS challenge_hashtag;
DROP TABLE IF EXISTS challenge_rule;
DROP TABLE IF EXISTS challenge;
DROP TABLE IF EXISTS user_template_image;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS contact;

CREATE TABLE contact
(
    contact_id        BIGSERIAL PRIMARY KEY,
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
    user_id               BIGSERIAL PRIMARY KEY,
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
    user_role_id     BIGSERIAL PRIMARY KEY,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    role             VARCHAR(6)   NOT NULL,
    user_id          BIGINT       NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_template_image
(
    user_template_image_id SERIAL PRIMARY KEY,
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
    challenge_id       BIGSERIAL PRIMARY KEY,
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
    challenge_rule_id BIGSERIAL PRIMARY KEY,
    rule              VARCHAR(30)  NOT NULL,
    create_date_time  TIMESTAMP(6) NOT NULL,
    update_date_time  TIMESTAMP(6) NOT NULL,
    service_status    VARCHAR(10)  NOT NULL,
    challenge_id      BIGINT       NOT NULL,
    CONSTRAINT fk_challenge_rule_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);

CREATE TABLE challenge_hashtag
(
    challenge_hashtag_id BIGSERIAL PRIMARY KEY,
    hashtag              VARCHAR(6)   NOT NULL,
    create_date_time     TIMESTAMP(6) NOT NULL,
    update_date_time     TIMESTAMP(6) NOT NULL,
    service_status       VARCHAR(10)  NOT NULL,
    challenge_id         BIGINT       NOT NULL,
    CONSTRAINT fk_challenge_hashtag_challenge FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);

CREATE TABLE challenge_template_image
(
    challenge_template_image_id BIGSERIAL PRIMARY KEY,
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
    challenge_member_id BIGSERIAL PRIMARY KEY,
    is_creator          BOOLEAN      NOT NULL,
    status              VARCHAR(15)  NOT NULL,
    create_date_time    TIMESTAMP(6) NOT NULL,
    update_date_time    TIMESTAMP(6) NOT NULL,
    service_status      VARCHAR(10)  NOT NULL,
    challenge_id        BIGINT       NOT NULL,
    user_id             BIGINT       NOT NULL,
    goal                VARCHAR(16),
    CONSTRAINT fk_challenge_member_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_challenge_member_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE feed
(
    feed_id             BIGSERIAL PRIMARY KEY,
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
    feed_comment_id     BIGSERIAL PRIMARY KEY,
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
    feed_like_id        BIGSERIAL PRIMARY KEY,
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
    inquiry_category_id SERIAL PRIMARY KEY,
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
    inquiry_id       BIGSERIAL PRIMARY KEY,
    type             VARCHAR(15)  NOT NULL,
    content          VARCHAR(120) NOT NULL,
    create_date_time TIMESTAMP(6) NOT NULL,
    update_date_time TIMESTAMP(6) NOT NULL,
    user_id          BIGINT,
    CONSTRAINT fk_inquiry_category_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE report_category
(
    report_category_id SERIAL PRIMARY KEY,
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
    report_id        BIGSERIAL PRIMARY KEY,
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
    suspend_id       BIGSERIAL PRIMARY KEY,
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
    block_id         BIGSERIAL PRIMARY KEY,
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
    ver_id           SERIAL PRIMARY KEY,
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

-- contact 테이블
INSERT INTO contact(email, verification_code, verify_yn, is_deleted, create_date_time,
                    update_date_time, deleted_date)
SELECT 'user' || gs.i || '@example.com'              AS email,
       LPAD(FLOOR(RANDOM() * 1000000)::TEXT, 6, '0') AS verification_code,
       TRUE                                          AS verify_yn,
       FALSE                                         AS is_deleted,
       NOW()                                         AS create_date_time,
       NOW()                                         AS update_date_time,
       NULL                                          AS deleted_date
FROM generate_series(1, 30) AS gs(i);

-- user 테이블
INSERT INTO users (username, password, image_url, temporary_password_yn, create_date_time,
                   update_date_time, contact_id, feed_cnt, challenge_cnt)
VALUES ('user1', '$2a$10$KcNHY41JcvJd2OGnWmC0bek8qT6XiEE11LeHsOfElgj6bFLYOgGay',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        FALSE, NOW(), NOW(), 1, 16, 20),
       ('user2', '$2a$10$rnp2AMAt4gsvcdxGlGH4UeXLLE2chDTX4aSptgILLUBNaC1ZISGZK',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        FALSE, NOW(), NOW(), 2, 2, 20),
       ('user3', '$2a$10$VOCyMnl8u5sXLJDfgDGlvOoxcUesiULBDOkzebTRavhGXlY63B3qi',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        FALSE, NOW(), NOW(), 3, 10, 10),
       ('user4', '$2a$10$ttevy3H13u6UEbYCDjWOjOImZJKA6SDzNb1rcWcdc.CmQlY4qy8R.',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        FALSE, NOW(), NOW(), 4, 0, 15),
       ('user5', '$2a$10$TMgFmiij5j0NfpOcUyUjtOrbQmBrdKwI/dfzXWI2haHnVcvMD8Vfq',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        FALSE, NOW(), NOW(), 5, 8, 10);
INSERT INTO users (username, password, image_url, temporary_password_yn, create_date_time,
                   update_date_time, contact_id, feed_cnt, challenge_cnt)
SELECT 'user' || gs.i                                                AS username,
       '$2a$10$' || substr(md5(random()::TEXT), 1, 53)               AS password,
       image_urls[CEIL(RANDOM() * ARRAY_LENGTH(image_urls, 1))::INT] AS image_url,
       FALSE                                                         AS temporary_password_yn,
       NOW()                                                         AS create_date_time,
       NOW()                                                         AS update_date_time,
       gs.i                                                          AS contact_id,
       FLOOR(RANDOM() * 20)                                          AS feed_cnt,
       FLOOR(RANDOM() * 20)                                          AS challenge_cnt
FROM generate_series(6, 30) AS gs(i)
         CROSS JOIN LATERAL (
    SELECT ARRAY [
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_health.jpg'
               ] AS image_urls
    ) img_array;

-- user_role 테이블
INSERT INTO user_role (create_date_time, update_date_time, role, user_id)
SELECT NOW()  AS create_date_time,
       NOW()  AS update_date_time,
       'USER' AS role,
       gs.i   AS user_id
FROM generate_series(1, 30) AS gs(i);

-- challenge 테이블
INSERT INTO challenge (name, goal, prove_time, end_date, image_url, is_public,
                       start_date, current_member_cnt, visit_cnt, invitation_code,
                       create_date_time, update_date_time, service_status)
SELECT '챌린지 이름 ' || gs.i,
       '챌린지 목표 ' || gs.i,
       MAKE_TIME(FLOOR(RANDOM() * 24)::INTEGER, 0, 0),
       start_date + (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL,
       image_urls[ceil(random() * array_length(image_urls, 1))::INT],
       is_public_statuses[ceil(random() * array_length(is_public_statuses, 1))::INT],
       start_date,
       FLOOR(RANDOM() * 50),
       FLOOR(RANDOM() * 500),
       'ABC12',
       '2025-01-01 00:00:00'::TIMESTAMP + (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL,
       '2025-01-01 00:00:00'::TIMESTAMP
           + (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL
           + (FLOOR(RANDOM() * 7)::INTEGER || ' hours')::INTERVAL,
       service_statuses[ceil(random() * array_length(service_statuses, 1))::INT]
FROM generate_series(1, 500) AS gs(i),
     LATERAL (
         SELECT ARRAY [
                    'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
                    'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_health.jpg',
                    'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
                    'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
                    'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
                    'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg'
                    ]                                                 AS image_urls,
                ARRAY ['ACTIVE', 'END']                               AS service_statuses,
                ARRAY [TRUE, FALSE]                                   AS is_public_statuses,
                '2025-01-01'::DATE +
                (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL AS start_date
         ) AS arrays;

-- challenge_rule 테이블
INSERT INTO challenge_rule (rule, create_date_time, update_date_time, service_status, challenge_id)
SELECT '규칙 ' || gs.i,
       NOW(),
       NOW(),
       'ACTIVE',
       challenge_id
FROM challenge,
     LATERAL generate_series(1, 5) AS gs(i);

-- challenge_hashtag 테이블
INSERT INTO challenge_hashtag (hashtag, create_date_time, update_date_time, service_status,
                               challenge_id)
SELECT unnest(array(
        SELECT ARRAY [
                   hashtags[ceil(random() * array_length(hashtags, 1))::INT],
                   hashtags[ceil(random() * array_length(hashtags, 1))::INT],
                   hashtags[ceil(random() * array_length(hashtags, 1))::INT]
                   ]
              )),
       NOW(),
       NOW(),
       'ACTIVE',
       challenge_id
FROM challenge,
     LATERAL (SELECT ARRAY ['러닝', '건강식', '게임', '챌린지', '개발', '코틀린', 'iOS', '안드로이드', '스프링', '디자인'] AS hashtags);

-- challenge_member 테이블
WITH challenge_data AS (SELECT generate_series(1, 100) AS challenge_id),
     user_data AS (SELECT generate_series(2, 30) AS user_id),
     ranked_data AS (SELECT cd.challenge_id,
                            ud.user_id,
                            ROW_NUMBER()
                            OVER (PARTITION BY cd.challenge_id ORDER BY RANDOM()) AS rn,
                            'PROGRESS'                                            AS status,
                            NOW()                                                 AS create_date_time,
                            NOW()                                                 AS update_date_time,
                            'ACTIVE'                                              AS service_status,
                            '매일 ' || cd.challenge_id || 'km 달리기'                  AS goal
                     FROM challenge_data cd
                              CROSS JOIN user_data ud
                     WHERE RANDOM() < 0.4)
INSERT
INTO challenge_member (is_creator, status, create_date_time, update_date_time, service_status,
                       challenge_id, user_id, goal)
SELECT CASE WHEN rn = 1 THEN TRUE ELSE FALSE END AS is_creator,
       status,
       create_date_time,
       update_date_time,
       service_status,
       challenge_id,
       user_id,
       goal
FROM ranked_data
WHERE rn <= 20;

UPDATE challenge_member
SET user_id = 1
WHERE challenge_member_id BETWEEN 1 AND 20;

UPDATE challenge_member
SET challenge_id = challenge_member_id
WHERE challenge_member_id BETWEEN 1 AND 20;

-- feed 테이블
INSERT INTO feed (like_cnt, comment_cnt, image_url, create_date_time, update_date_time,
                  service_status, challenge_id, challenge_member_id)
SELECT FLOOR(RANDOM() * 101)                                         AS like_cnt,
       FLOOR(RANDOM() * 101)                                         AS comment_cnt,
       image_urls[CEIL(RANDOM() * ARRAY_LENGTH(image_urls, 1))::INT] AS image_url,
       '2025-02-24 12:00:00'::TIMESTAMP + INTERVAL '1 minute' *
                                          (challenge_member_id - 1)  AS create_date_time,
       '2025-02-24 12:00:00'::TIMESTAMP + INTERVAL '1 minute' *
                                          (challenge_member_id - 1)  AS update_date_time,
       'ACTIVE'                                                      AS service_status,
       challenge_member_id                                           AS challenge_id,
       challenge_member_id                                           AS challenge_member_id
FROM generate_series(1, 20) AS challenge_member_id
         CROSS JOIN LATERAL (
    SELECT ARRAY [
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg'
               ] AS image_urls
    ) img_array;

INSERT INTO feed (like_cnt, comment_cnt, image_url, create_date_time, update_date_time,
                  service_status, challenge_id, challenge_member_id)
VALUES (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        '2025-01-29 12:00:00', '2025-01-29 12:00:00', 'ACTIVE', 1, 1),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        '2025-01-29 12:01:00', '2025-01-29 12:01:00', 'ACTIVE', 2, 2),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        '2025-01-29 12:02:00', '2025-01-29 12:02:00', 'ACTIVE', 3, 3),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        '2025-01-29 12:03:00', '2025-01-29 12:03:00', 'ACTIVE', 4, 4),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        '2025-01-29 12:04:00', '2025-01-29 12:04:00', 'ACTIVE', 5, 5),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        '2025-01-29 12:05:00', '2025-01-29 12:05:00', 'ACTIVE', 6, 6),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        '2025-01-29 12:06:00', '2025-01-29 12:06:00', 'ACTIVE', 7, 7),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        '2025-01-29 12:07:00', '2025-01-29 12:07:00', 'ACTIVE', 8, 8),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        '2025-01-29 12:08:00', '2025-01-29 12:08:00', 'ACTIVE', 9, 9),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        '2025-01-29 12:09:00', '2025-01-29 12:09:00', 'ACTIVE', 10, 10),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        '2025-01-29 12:10:00', '2025-01-29 12:10:00', 'ACTIVE', 11, 11),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        '2025-01-29 12:11:00', '2025-01-29 12:11:00', 'ACTIVE', 12, 12),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        '2025-01-29 12:12:00', '2025-01-29 12:12:00', 'ACTIVE', 13, 13),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        '2025-01-29 12:13:00', '2025-01-29 12:13:00', 'ACTIVE', 14, 14);

INSERT INTO feed (like_cnt, comment_cnt, image_url, create_date_time, update_date_time,
                  service_status, challenge_id, challenge_member_id)
VALUES (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        '2025-01-02 12:00:00', '2025-01-02 12:00:00', 'ACTIVE', 1, 1),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        '2025-01-02 12:01:00', '2025-01-02 12:01:00', 'ACTIVE', 2, 2),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        '2025-01-02 12:02:00', '2025-01-02 12:02:00', 'ACTIVE', 3, 3),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        '2025-01-02 12:03:00', '2025-01-02 12:03:00', 'ACTIVE', 4, 4),
       (FLOOR(RANDOM() * 101), FLOOR(RANDOM() * 101),
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        '2025-01-02 12:04:00', '2025-01-02 12:04:00', 'ACTIVE', 5, 5);

INSERT INTO feed (like_cnt, comment_cnt, image_url, create_date_time, update_date_time,
                  service_status, challenge_id, challenge_member_id)
SELECT FLOOR(RANDOM() * 101)                                         AS like_cnt,
       FLOOR(RANDOM() * 101)                                         AS comment_cnt,
       image_urls[CEIL(RANDOM() * ARRAY_LENGTH(image_urls, 1))::INT] AS image_url,
       '2025-02-24 12:00:00'::TIMESTAMP + INTERVAL '1 minute' *
                                          (challenge_member_id - 1)  AS create_date_time,
       '2025-02-24 12:00:00'::TIMESTAMP + INTERVAL '1 minute' *
                                          (challenge_member_id - 1)  AS update_date_time,
       'ACTIVE'                                                      AS service_status,
       1                                                             AS challenge_id,
       challenge_member_id
FROM generate_series(2, 35) AS challenge_member_id
         CROSS JOIN LATERAL (
    SELECT ARRAY [
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
               'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg'
               ] AS image_urls
    ) img_array;

-- feed_comment 테이블
WITH feed_data AS (SELECT f.feed_id, f.challenge_member_id
                   FROM feed f),
     comment_data AS (SELECT fd.feed_id,
                             (SELECT challenge_member_id
                              FROM feed
                              ORDER BY RANDOM()
                              LIMIT 1)                                                 AS challenge_member_id,
                             comments[CEIL(RANDOM() * ARRAY_LENGTH(comments, 1))::INT] AS comment,
                             NOW()                                                     AS create_date_time,
                             NOW()                                                     AS update_date_time,
                             'ACTIVE'                                                  AS service_status
                      FROM feed_data fd
                               CROSS JOIN (SELECT generate_series(1, 30) AS n) AS count_series
                               CROSS JOIN LATERAL (
                          SELECT ARRAY [
                                     '와우', '와우와', '멋져요', '굳굳', '화이팅!', '짱짱', '최고!', '대단해요',
                                     '굳굳굳', '짱짱짱', '와아아아', '멋져요멋져요', '화이팅화이팅', '짱짱짱짱', '와우!',
                                     '굳굳굳굳', '와아아아!!~~', '최고최고!', '대박!', '굿굿!', '화이팅화이팅화이팅'
                                     ] AS comments
                          ) AS comment_array)
INSERT
INTO feed_comment (comment, create_date_time, update_date_time, service_status, feed_id,
                   challenge_member_id)
SELECT comment, create_date_time, update_date_time, service_status, feed_id, challenge_member_id
FROM comment_data;
