DROP TABLE IF EXISTS app_version;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS inquiry;
DROP TABLE IF EXISTS feed_history;
DROP TABLE IF EXISTS feed_like;
DROP TABLE IF EXISTS feed_comment;
DROP TABLE IF EXISTS feed;
DROP TABLE IF EXISTS challenge_history;
DROP TABLE IF EXISTS challenge_member;
DROP TABLE IF EXISTS challenge_example_image;
DROP TABLE IF EXISTS challenge_hashtag;
DROP TABLE IF EXISTS challenge_rule;
DROP TABLE IF EXISTS challenge;
DROP TABLE IF EXISTS user_challenge_history;
DROP TABLE IF EXISTS users;

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE users
(
    user_id                 BIGSERIAL PRIMARY KEY,
    email                   VARCHAR(100) NOT NULL,
    provider                VARCHAR(15)  NULL,
    sub                     VARCHAR(255) NULL,
    authentication_code     VARCHAR(6)   NULL,
    is_authenticated        BOOLEAN      NOT NULL,
    username                VARCHAR(20)  NULL,
    password                VARCHAR(255) NULL,
    image_url               VARCHAR(500) NULL,
    role                    VARCHAR(25)  NOT NULL,
    deleted_date            TIMESTAMP(6) NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    CONSTRAINT uq_user_provider_sub UNIQUE (provider, sub),
    CONSTRAINT uq_username UNIQUE (username)
);

CREATE TABLE user_challenge_history
(
    user_challenge_history_id BIGSERIAL PRIMARY KEY,
    user_id                   BIGINT       NOT NULL,
    challenge_count           INT          NOT NULL,
    ended_challenge_count     INT          NOT NULL,
    feed_count                INT          NOT NULL,
    created_date_time         TIMESTAMP(6) NULL,
    last_modified_date_time   TIMESTAMP(6) NULL,
    created_by                VARCHAR(255) NULL,
    last_modified_by          VARCHAR(255) NULL,
    CONSTRAINT fk_user_challenge_history_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT uq_user_challenge_history_user_id UNIQUE (user_id)
);

CREATE TABLE report
(
    report_id               BIGSERIAL PRIMARY KEY,
    reporter_id             BIGINT       NOT NULL,
    target_id               BIGINT       NOT NULL,
    category                VARCHAR(20)  NOT NULL,
    reason                  VARCHAR(15)  NOT NULL,
    content                 VARCHAR(120) NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE inquiry
(
    inquiry_id              BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    category                VARCHAR(15)  NOT NULL,
    content                 VARCHAR(120) NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_inquiry_user_id FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE challenge
(
    challenge_id            BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(16)  NOT NULL,
    is_public               BOOLEAN      NOT NULL,
    goal                    VARCHAR(120) NOT NULL,
    prove_time              TIME         NOT NULL,
    end_date                DATE         NOT NULL,
    image_url               VARCHAR(500) NOT NULL,
    invitation_code         VARCHAR(5)   NOT NULL,
    start_date              DATE         NOT NULL,
    status                  VARCHAR(15)  NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);
CREATE INDEX idx_challenge_status ON challenge (status);
CREATE INDEX idx_challenge_name_trgm ON challenge USING GIN (name gin_trgm_ops);

CREATE TABLE challenge_rule
(
    challenge_rule_id       BIGSERIAL PRIMARY KEY,
    challenge_id            BIGINT       NOT NULL,
    rule                    VARCHAR(30)  NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_challenge_rule_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);

CREATE TABLE challenge_hashtag
(
    challenge_hashtag_id    BIGSERIAL PRIMARY KEY,
    challenge_id            BIGINT       NOT NULL,
    hashtag                 VARCHAR(6)   NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_challenge_hashtag_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id) ON DELETE CASCADE
);
CREATE INDEX idx_challenge_hashtag_trgm ON challenge_hashtag USING GIN (hashtag gin_trgm_ops);

CREATE TABLE challenge_member
(
    challenge_member_id     BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    challenge_id            BIGINT       NOT NULL,
    goal                    VARCHAR(16)  NULL,
    is_creator              BOOLEAN      NOT NULL,
    status                  VARCHAR(15)  NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_challenge_member_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_challenge_member_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id)
);

CREATE TABLE challenge_example_image
(
    challenge_example_image_id BIGSERIAL PRIMARY KEY,
    image_url                  VARCHAR(500) NOT NULL
);

CREATE TABLE challenge_history
(
    challenge_history_id    BIGSERIAL PRIMARY KEY,
    challenge_id            BIGINT       NOT NULL,
    challenge_member_count  INT          NOT NULL,
    visit_count             INT          NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_challenge_history_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT uq_challenge_history_challenge_id UNIQUE (challenge_id)
);

CREATE TABLE feed
(
    feed_id                 BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    challenge_member_id     BIGINT       NOT NULL,
    challenge_id            BIGINT       NOT NULL,
    image_url               VARCHAR(500) NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_feed_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_feed_challenge_id FOREIGN KEY (challenge_id) REFERENCES challenge (challenge_id),
    CONSTRAINT fk_feed_challenge_member_id FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);
CREATE INDEX idx_feed_challenge_id_created_date_time ON feed (challenge_id, created_date_time);

CREATE TABLE feed_comment
(
    feed_comment_id         BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    challenge_member_id     BIGINT       NOT NULL,
    feed_id                 BIGINT       NOT NULL,
    comment                 VARCHAR(300) NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_feed_comment_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_feed_comment_feed_id FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_comment_challenge_member_id FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id)
);
CREATE INDEX idx_feed_comment_feed_id ON feed_comment (feed_id);

CREATE TABLE feed_like
(
    feed_like_id            BIGSERIAL PRIMARY KEY,
    challenge_member_id     BIGINT       NOT NULL,
    feed_id                 BIGINT       NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_feed_like_feed_id FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_like_challenge_member_id FOREIGN KEY (challenge_member_id) REFERENCES challenge_member (challenge_member_id),
    CONSTRAINT uq_feed_like_challenge_member_id_feed_id UNIQUE (challenge_member_id, feed_id)
);

CREATE TABLE feed_history
(
    feed_history_id         BIGSERIAL PRIMARY KEY,
    feed_id                 BIGINT       NOT NULL,
    like_count              INT          NOT NULL,
    comment_count           INT          NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL,
    CONSTRAINT fk_feed_history_feed_id FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT uq_feed_history_feed_id UNIQUE (feed_id)
);

CREATE TABLE app_version
(
    app_version_id          BIGSERIAL PRIMARY KEY,
    os                      VARCHAR(15)  NOT NULL,
    min_version             VARCHAR(10)  NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
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

-- user 테이블
INSERT INTO users (email, authentication_code, is_authenticated, username, password, image_url,
                   role, deleted_date, created_date_time,
                   last_modified_date_time)
VALUES ('user1@example.com', 1234, true, 'user1',
        '$2a$10$KcNHY41JcvJd2OGnWmC0bek8qT6XiEE11LeHsOfElgj6bFLYOgGay',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        'USER', null, NOW(), NOW()),
       ('user2@example.com', 1234, true, 'user2',
        '$2a$10$rnp2AMAt4gsvcdxGlGH4UeXLLE2chDTX4aSptgILLUBNaC1ZISGZK',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        'USER', null, NOW(), NOW()),
       ('user3@example.com', 1234, true, 'user3',
        '$2a$10$VOCyMnl8u5sXLJDfgDGlvOoxcUesiULBDOkzebTRavhGXlY63B3qi',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        'USER', null, NOW(), NOW()),
       ('user4@example.com', 1234, true, 'user4',
        '$2a$10$ttevy3H13u6UEbYCDjWOjOImZJKA6SDzNb1rcWcdc.CmQlY4qy8R.',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        'USER', null, NOW(), NOW()),
       ('user5@example.com', 1234, true, 'user5',
        '$2a$10$TMgFmiij5j0NfpOcUyUjtOrbQmBrdKwI/dfzXWI2haHnVcvMD8Vfq',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        'USER', null, NOW(), NOW());
INSERT INTO users (email, authentication_code, is_authenticated, username, password, image_url,
                   role, deleted_date, created_date_time,
                   last_modified_date_time)
SELECT 'user' || gs.i || '@example.com',
       1234,
       true,
       'user' || gs.i,
       '$2a$10$' || substr(md5(random()::TEXT), 1, 53),
       image_urls[CEIL(RANDOM() * ARRAY_LENGTH(image_urls, 1))::INT],
       'USER',
       null,
       NOW(),
       NOW()
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

INSERT INTO users (email, authentication_code, is_authenticated, username, password, image_url,
                   role, deleted_date, created_date_time,
                   last_modified_date_time)
VALUES ('photi.aos@gmail.com', 1234, true, 'photi_aos',
        '$2a$10$IyhRXXoibA7zeoq5IMYWMea1kRnt7BX2qzRmQ8Sn0iQosmSyBTjWa',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        'ADMIN', null, NOW(), NOW()),
       ('photi.ios@gmail.com', 1234, true, 'photi_ios',
        '$2a$10$QyNEaU1.Dkj.dZ34rUzZIuGbOMpi5IV3pddCBraW.3ERu0eOPYHS6',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        'ADMIN', null, NOW(), NOW());

-- user_challenge_history 테이블
INSERT INTO user_challenge_history (user_id,
                                    challenge_count,
                                    ended_challenge_count,
                                    feed_count,
                                    created_date_time,
                                    last_modified_date_time)
SELECT u.user_id,
       FLOOR(RANDOM() * 10 + 1)::INT AS challenge_count,
       FLOOR(RANDOM() * 3)::INT      AS ended_challenge_count,
       FLOOR(RANDOM() * 30)::INT     AS feed_count,
       NOW(),
       NOW()
FROM users u;

-- challenge 테이블
INSERT INTO challenge (name, is_public, goal, prove_time, end_date, image_url, invitation_code,
                       start_date, status, created_date_time, last_modified_date_time, created_by,
                       last_modified_by)
SELECT '챌린지 이름 ' || gs.i,
       is_public_statuses[ceil(random() * array_length(is_public_statuses, 1))::INT],
       '챌린지 목표 ' || gs.i,
       MAKE_TIME(FLOOR(RANDOM() * 24)::INTEGER, 0, 0),
       start_date + (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL,
       image_urls[ceil(random() * array_length(image_urls, 1))::INT],
       'ABC12',
       start_date,
       statuses[ceil(random() * array_length(statuses, 1))::INT],
       '2025-01-01 00:00:00'::TIMESTAMP + (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL,
       '2025-01-01 00:00:00'::TIMESTAMP
           + (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL
           + (FLOOR(RANDOM() * 7)::INTEGER || ' hours')::INTERVAL,
       null,
       null
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
                ARRAY ['ACTIVE', 'END']                               AS statuses,
                ARRAY [TRUE, FALSE]                                   AS is_public_statuses,
                '2025-01-01'::DATE +
                (FLOOR(RANDOM() * 365)::INTEGER || ' days')::INTERVAL AS start_date
         ) AS arrays;

-- challenge_rule 테이블
INSERT INTO challenge_rule (challenge_id, rule, created_date_time, last_modified_date_time,
                            created_by, last_modified_by)
SELECT challenge_id,
       '규칙 ' || gs.i,
       NOW(),
       NOW(),
       null,
       null
FROM challenge,
     LATERAL generate_series(1, 5) AS gs(i);

-- challenge_hashtag 테이블
INSERT INTO challenge_hashtag (challenge_id, hashtag, created_date_time, last_modified_date_time,
                               created_by, last_modified_by)
SELECT challenge_id,
       unnest(array(
               SELECT ARRAY [
                          hashtags[ceil(random() * array_length(hashtags, 1))::INT],
                          hashtags[ceil(random() * array_length(hashtags, 1))::INT],
                          hashtags[ceil(random() * array_length(hashtags, 1))::INT]
                          ]
              )),
       NOW(),
       NOW(),
       null,
       null
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
                            NOW()                                                 AS created_date_time,
                            NOW()                                                 AS last_modified_date_time,
                            'ACTIVE'                                              AS challenge_status,
                            '매일 ' || cd.challenge_id || 'km 달리기'                  AS goal
                     FROM challenge_data cd
                              CROSS JOIN user_data ud
                     WHERE RANDOM() < 0.4)

INSERT
INTO challenge_member (user_id, challenge_id, goal, is_creator, status,
                       created_date_time, last_modified_date_time, created_by, last_modified_by)
SELECT user_id,
       challenge_id,
       goal,
       CASE WHEN rn = 1 THEN TRUE ELSE FALSE END AS is_creator,
       status,
       created_date_time,
       last_modified_date_time,
       null,
       null
FROM ranked_data
WHERE rn <= 20;

UPDATE challenge_member
SET user_id = 1
WHERE challenge_member_id BETWEEN 1 AND 20;

UPDATE challenge_member
SET challenge_id = challenge_member_id
WHERE challenge_member_id BETWEEN 1 AND 20;

-- challenge_example_image 테이블
INSERT INTO challenge_example_image
VALUES (1,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg'),
       (2,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg'),
       (3,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg'),
       (4,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg');

-- challenge_history 테이블
INSERT INTO challenge_history (challenge_id, challenge_member_count, visit_count,
                               created_date_time, last_modified_date_time, created_by,
                               last_modified_by)
SELECT c.challenge_id,
       FLOOR(RANDOM() * 100)::INT + 1,
       FLOOR(RANDOM() * 1000)::INT + 1,
       NOW() - (INTERVAL '1 day' * FLOOR(RANDOM() * 365)),
       NOW(),
       NULL,
       NULL
FROM challenge c;

-- feed 테이블
INSERT INTO feed (user_id, challenge_member_id, challenge_id, image_url, created_date_time,
                  last_modified_date_time)
SELECT 1 AS user_id,
       1 AS challenge_member_id,
       1 AS challenge_id,
       img.image_url,
       NOW() - (INTERVAL '1 day' * FLOOR(RANDOM() * 30)),
       NOW()
FROM generate_series(1, 20) AS gs(i)
         CROSS JOIN LATERAL (
    SELECT image_urls[CEIL(RANDOM() * ARRAY_LENGTH(image_urls, 1))::INT] AS image_url
    FROM (SELECT ARRAY [
                     'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
                     'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
                     'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
                     'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg'
                     ] AS image_urls) a
    ) img;

-- feed_history 테이블
INSERT INTO feed_history (feed_id, like_count, comment_count, created_date_time,
                          last_modified_date_time)
SELECT f.feed_id,
       FLOOR(RANDOM() * 100)::INT AS like_count,
       FLOOR(RANDOM() * 30)::INT  AS comment_count,
       f.created_date_time,
       f.last_modified_date_time
FROM feed f;

-- feed_comment 테이블
WITH comment_texts AS (SELECT ARRAY [
                                  '와우', '멋져요', '굿굿', '짱짱', '최고!',
                                  '대단해요', '화이팅!', '좋아요', '굿굿굿', '대박!'
                                  ] AS comments),
     feed_with_member AS (SELECT f.feed_id, f.challenge_member_id, cm.user_id
                          FROM feed f
                                   JOIN challenge_member cm
                                        ON f.challenge_member_id = cm.challenge_member_id
                          WHERE f.challenge_id IN (1, 2))
INSERT
INTO feed_comment (user_id, challenge_member_id, feed_id, comment, created_date_time,
                   last_modified_date_time)
SELECT fm.user_id,
       fm.challenge_member_id,
       fm.feed_id,
       (SELECT comments[CEIL(RANDOM() * ARRAY_LENGTH(comments, 1))::INT] FROM comment_texts),
       NOW() - (INTERVAL '1 minute' * gs.i),
       NOW()
FROM feed_with_member fm,
     generate_series(1, 20) AS gs(i);
