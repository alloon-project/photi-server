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
    user_id             BIGINT,
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

INSERT INTO contact(email, verification_code, verify_yn, is_deleted, create_date_time,
                    update_date_time, deleted_date)
VALUES ('user1@example.com', '123456', TRUE, FALSE, NOW(), NOW(), NULL),
       ('user2@example.com', '654321', TRUE, FALSE, NOW(), NOW(), NULL),
       ('user3@example.com', '111111', TRUE, FALSE, NOW(), NOW(), NULL),
       ('user4@example.com', '222222', TRUE, FALSE, NOW(), NOW(), NULL),
       ('user5@example.com', '333333', TRUE, FALSE, NOW(), NOW(), NULL);

INSERT INTO users (username, password, image_url, temporary_password_yn, create_date_time,
                   update_date_time, contact_id, feed_cnt)
VALUES ('user1', '$2a$10$KcNHY41JcvJd2OGnWmC0bek8qT6XiEE11LeHsOfElgj6bFLYOgGay',
        'https://e7.pngegg.com/pngimages/81/570/png-clipart-profile-logo-computer-icons-user-user-blue-heroes.png',
        FALSE, NOW(), NOW(), 1, 5),
       ('user2', '$2a$10$rnp2AMAt4gsvcdxGlGH4UeXLLE2chDTX4aSptgILLUBNaC1ZISGZK',
        'https://e7.pngegg.com/pngimages/81/570/png-clipart-profile-logo-computer-icons-user-user-blue-heroes.png',
        FALSE, NOW(), NOW(), 2, 2),
       ('user3', '$2a$10$VOCyMnl8u5sXLJDfgDGlvOoxcUesiULBDOkzebTRavhGXlY63B3qi',
        'https://e7.pngegg.com/pngimages/81/570/png-clipart-profile-logo-computer-icons-user-user-blue-heroes.png',
        FALSE, NOW(), NOW(), 3, 10),
       ('user4', '$2a$10$ttevy3H13u6UEbYCDjWOjOImZJKA6SDzNb1rcWcdc.CmQlY4qy8R.',
        'https://e7.pngegg.com/pngimages/81/570/png-clipart-profile-logo-computer-icons-user-user-blue-heroes.png',
        FALSE, NOW(), NOW(), 4, 0),
       ('user5', '$2a$10$TMgFmiij5j0NfpOcUyUjtOrbQmBrdKwI/dfzXWI2haHnVcvMD8Vfq',
        'https://e7.pngegg.com/pngimages/81/570/png-clipart-profile-logo-computer-icons-user-user-blue-heroes.png',
        FALSE, NOW(), NOW(), 5, 8);

INSERT INTO user_role (create_date_time, update_date_time, role, user_id)
VALUES (NOW(), NOW(), 'USER', 1),
       (NOW(), NOW(), 'USER', 2),
       (NOW(), NOW(), 'USER', 3),
       (NOW(), NOW(), 'USER', 4),
       (NOW(), NOW(), 'USER', 5);

INSERT INTO challenge (name, goal, prove_time, end_date, image_url, is_public, start_date,
                       current_member_cnt, visit_cnt, invitation_code, create_date_time,
                       update_date_time, service_status)
VALUES ('신나게 하는 러닝 챌린지', '하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.', '08:00:00', '2025-01-31',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        TRUE, '2024-12-31', 10, 100, 'ABC12', NOW(), NOW(), 'ACTIVE'),
       ('신나게 하는 러닝 챌린지입니다', '하루에 한 번씩 꼭 러닝을 하는 것이 우리 챌린지의 목표입니다.', '20:00:00', '2024-12-29',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        TRUE, '2024-02-01', 20, 200, 'ABC34', NOW(), NOW(), 'END'),
       ('알고리즘 챌린지', '알고리즘 문제 100개 풀기', '15:00:00', '2025-12-31',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        FALSE, '2025-03-01', 5, 50, 'ABC56', NOW(), NOW(), 'ACTIVE'),
       ('알고리즘 챌린지입니다!!', '알고리즘 문제 100개 풀기', '06:00:00', '2025-06-02',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        TRUE, '2025-04-01', 15, 150, 'ABC78', NOW(), NOW(), 'ACTIVE'),
       ('챌린지챌린지챌린지', '챌린지목표챌린지목표입니다', '10:00:00', '2024-04-20',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        FALSE, '2024-01-01', 25, 250, 'ABC90', NOW(), NOW(), 'END'),
       ('알고리즘 챌린지입니다!!!', '알고리즘 문제 100개 풀기', '06:00:00', '2025-06-02',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        TRUE, '2025-04-01', 15, 150, 'ABD78', NOW(), NOW(), 'ACTIVE'),
       ('챌린지입니다!!~', '알고리즘 문제 100개 풀기', '06:00:00', '2025-06-02',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        TRUE, '2025-03-01', 15, 150, 'ABE78', NOW(), NOW(), 'ACTIVE'),
       ('챌린지입니다!!^^', '알고리즘 문제 100개 풀기', '06:00:00', '2025-06-02',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg',
        TRUE, '2025-02-01', 15, 150, 'ABF78', NOW(), NOW(), 'ACTIVE'),
       ('챌린지입니다요', '알고리즘 문제 100개 풀기', '06:00:00', '2025-06-02',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        TRUE, '2025-01-01', 15, 150, 'ABG78', NOW(), NOW(), 'ACTIVE'),
       ('챌린지입니다**', '알고리즘 문제 100개 풀기', '06:00:00', '2025-06-02',
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg',
        TRUE, '2025-05-01', 15, 150, 'ABH78', NOW(), NOW(), 'ACTIVE');

INSERT INTO challenge_rule(rule, create_date_time, update_date_time, service_status, challenge_id)
VALUES ('하루에 최소 30분 이상 러닝하기', NOW(), NOW(), 'ACTIVE', 1),
       ('얼굴 안 나오게 찍기', NOW(), NOW(), 'ACTIVE', 1),
       ('하루에 한번 인증하기', NOW(), NOW(), 'ACTIVE', 1),
       ('장소 나오게 찍기', NOW(), NOW(), 'ACTIVE', 1),
       ('일주일에 3회 이상 인증하기', NOW(), NOW(), 'ACTIVE', 1),
       ('얼굴 안 나오게 찍기', NOW(), NOW(), 'ACTIVE', 2),
       ('하루에 최소 30분 이상 러닝하기', NOW(), NOW(), 'ACTIVE', 3),
       ('하루에 5번 이상 야채를 섭취하기', NOW(), NOW(), 'ACTIVE', 4),
       ('하루에 최소 30분 이상 러닝하기', NOW(), NOW(), 'ACTIVE', 5),
       ('하루에 5번 이상 야채를 섭취하기', NOW(), NOW(), 'ACTIVE', 6),
       ('하루에 최소 30분 이상 러닝하기', NOW(), NOW(), 'ACTIVE', 7),
       ('하루에 5번 이상 야채를 섭취하기', NOW(), NOW(), 'ACTIVE', 8),
       ('하루에 최소 30분 이상 러닝하기', NOW(), NOW(), 'ACTIVE', 9),
       ('하루에 5번 이상 야채를 섭취하기', NOW(), NOW(), 'ACTIVE', 10);

INSERT INTO challenge_hashtag(hashtag, create_date_time, update_date_time, service_status,
                              challenge_id)
VALUES ('러닝', NOW(), NOW(), 'ACTIVE', 1),
       ('건강식', NOW(), NOW(), 'ACTIVE', 1),
       ('게임', NOW(), NOW(), 'ACTIVE', 1),
       ('게임', NOW(), NOW(), 'ACTIVE', 2),
       ('건강식', NOW(), NOW(), 'ACTIVE', 3),
       ('러닝', NOW(), NOW(), 'ACTIVE', 4),
       ('건강식', NOW(), NOW(), 'ACTIVE', 5),
       ('러닝', NOW(), NOW(), 'ACTIVE', 6),
       ('건강식', NOW(), NOW(), 'ACTIVE', 7),
       ('러닝', NOW(), NOW(), 'ACTIVE', 8),
       ('건강식', NOW(), NOW(), 'ACTIVE', 9),
       ('건강식', NOW(), NOW(), 'ACTIVE', 10),
       ('게임', NOW(), NOW(), 'ACTIVE', 10);

INSERT INTO challenge_member(is_creator, status, create_date_time, update_date_time, service_status,
                             challenge_id, user_id, goal)
VALUES (TRUE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 1, 1, '매일 5km 달리기'),
       (FALSE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 1, 2, '매일 3km 달리기'),
       (TRUE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 2, 3, '매일 과일 3종 섭취'),
       (FALSE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 2, 2, '매일 3km 달리기'),
       (FALSE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 2, 4, '매일 야채 5종 섭취'),
       (TRUE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 3, 3, '매일 과일 3종 섭취'),
       (FALSE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 3, 5, '매일 과일 3종 섭취'),
       (FALSE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 3, 4, '매일 야채 5종 섭취'),
       (TRUE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 4, 2, '매일 과일 3종 섭취'),
       (FALSE, 'PROGRESS', NOW(), NOW(), 'ACTIVE', 4, 3, '매일 야채 5종 섭취');

INSERT INTO feed (like_cnt, comment_cnt, image_url, create_date_time, update_date_time,
                  service_status, challenge_id, challenge_member_id)
VALUES (10, 5,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        NOW(), NOW(), 'ACTIVE', 1, 1),
       (15, 3,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        NOW(), NOW(), 'ACTIVE', 2, 2),
       (8, 6,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        NOW(), NOW(), 'ACTIVE', 3, 3),
       (12, 7,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_running.jpg',
        NOW(), NOW(), 'ACTIVE', 4, 4),
       (6, 2,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        NOW(), NOW(), 'ACTIVE', 5, 5),
       (25, 1,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        NOW(), NOW(), 'ACTIVE', 6, 6),
       (30, 8,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        NOW(), NOW(), 'ACTIVE', 7, 7),
       (18, 10,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        NOW(), NOW(), 'ACTIVE', 8, 8),
       (9, 4,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg',
        NOW(), NOW(), 'ACTIVE', 9, 1),
       (13, 9,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg',
        NOW(), NOW(), 'ACTIVE', 10, 2);

INSERT INTO feed_comment (comment, create_date_time, update_date_time, service_status, feed_id,
                          challenge_member_id)
VALUES ('와우', NOW(), NOW(), 'ACTIVE', 1, 2),
       ('와우와', NOW(), NOW(), 'ACTIVE', 1, 2),
       ('멋져요', NOW(), NOW(), 'ACTIVE', 2, 3),
       ('와아아아', NOW(), NOW(), 'ACTIVE', 3, 4),
       ('굳굳', NOW(), NOW(), 'ACTIVE', 4, 5),
       ('화이팅화이팅화이팅화이팅', NOW(), NOW(), 'ACTIVE', 5, 6),
       ('굳굳굳굳굳', NOW(), NOW(), 'ACTIVE', 6, 7),
       ('짱짱짱', NOW(), NOW(), 'ACTIVE', 7, 8),
       ('멋져요멋져요', NOW(), NOW(), 'ACTIVE', 8, 1),
       ('와아아아~~~~', NOW(), NOW(), 'ACTIVE', 9, 1),
       ('와아아아!!~~', NOW(), NOW(), 'ACTIVE', 9, 1),
       ('굳굳굳', NOW(), NOW(), 'ACTIVE', 10, 2);
