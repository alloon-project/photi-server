CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE users_new
(
    user_id                 BIGSERIAL PRIMARY KEY,
    email                   VARCHAR(100) NOT NULL,
    authentication_code     VARCHAR(6)   NOT NULL,
    is_authenticated        BOOLEAN      NOT NULL,
    username                VARCHAR(20)  NULL,
    password                VARCHAR(255) NULL,
    image_url               VARCHAR(500) NULL,
    role                    VARCHAR(25)  NOT NULL,
    deleted_date            TIMESTAMP(6) NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL
);

CREATE TABLE user_challenge_history_new
(
    user_challenge_history_id BIGSERIAL PRIMARY KEY,
    user_id                   BIGINT       NOT NULL,
    challenge_count           INT          NOT NULL,
    ended_challenge_count     INT          NOT NULL,
    feed_count                INT          NOT NULL,
    created_date_time         TIMESTAMP(6) NULL,
    last_modified_date_time   TIMESTAMP(6) NULL,
    created_by                VARCHAR(255) NULL,
    last_modified_by          VARCHAR(255) NULL
);

CREATE TABLE report_new
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

CREATE TABLE inquiry_new
(
    inquiry_id              BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    category                VARCHAR(15)  NOT NULL,
    content                 VARCHAR(120) NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE challenge_new
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

CREATE TABLE challenge_rule_new
(
    challenge_rule_id       BIGSERIAL PRIMARY KEY,
    challenge_id            BIGINT       NOT NULL,
    rule                    VARCHAR(30)  NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE challenge_hashtag_new
(
    challenge_hashtag_id    BIGSERIAL PRIMARY KEY,
    challenge_id            BIGINT       NOT NULL,
    hashtag                 VARCHAR(6)   NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE challenge_member_new
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
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE challenge_example_image_new
(
    challenge_example_image_id BIGSERIAL PRIMARY KEY,
    image_url                  VARCHAR(500) NOT NULL
);

CREATE TABLE challenge_history_new
(
    challenge_history_id    BIGSERIAL PRIMARY KEY,
    challenge_id            BIGINT       NOT NULL,
    challenge_member_count  INT          NOT NULL,
    visit_count             INT          NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE feed_new
(
    feed_id                 BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    challenge_member_id     BIGINT       NOT NULL,
    challenge_id            BIGINT       NOT NULL,
    image_url               VARCHAR(500) NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE feed_comment_new
(
    feed_comment_id         BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT       NOT NULL,
    challenge_member_id     BIGINT       NOT NULL,
    feed_id                 BIGINT       NOT NULL,
    comment                 VARCHAR(300) NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE feed_like_new
(
    feed_like_id            BIGSERIAL PRIMARY KEY,
    challenge_member_id     BIGINT       NOT NULL,
    feed_id                 BIGINT       NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE feed_history_new
(
    feed_history_id         BIGSERIAL PRIMARY KEY,
    feed_id                 BIGINT       NOT NULL,
    like_count              INT          NOT NULL,
    comment_count           INT          NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

CREATE TABLE app_version_new
(
    app_version_id          BIGSERIAL PRIMARY KEY,
    os                      VARCHAR(15)  NOT NULL,
    min_version             VARCHAR(10)  NOT NULL,
    created_date_time       TIMESTAMP(6) NULL,
    last_modified_date_time TIMESTAMP(6) NULL,
    created_by              VARCHAR(255) NULL,
    last_modified_by        VARCHAR(255) NULL
);

SET session_replication_role = 'replica';

INSERT INTO users_new (user_id,
                       email,
                       authentication_code,
                       is_authenticated,
                       username,
                       password,
                       image_url,
                       role,
                       deleted_date,
                       created_date_time,
                       last_modified_date_time)
SELECT u.user_id,
       c.email,
       c.verification_code,
       c.verify_yn,
       u.username,
       u.password,
       u.image_url,
       ur.role,
       c.deleted_date,
       u.create_date_time,
       u.update_date_time
FROM users u
         JOIN contact c
              ON u.contact_id = c.contact_id
         JOIN user_role ur
              ON u.user_id = ur.user_id
ORDER BY u.user_id;

INSERT INTO challenge_new (challenge_id, name, is_public, goal, prove_time, end_date, image_url,
                           invitation_code, start_date, status, created_date_time,
                           last_modified_date_time)
SELECT challenge_id,
       name,
       is_public,
       goal,
       prove_time,
       end_date,
       image_url,
       invitation_code,
       start_date,
       service_status,
       create_date_time,
       update_date_time
FROM challenge
ORDER BY challenge_id;

INSERT INTO challenge_rule_new (challenge_rule_id, challenge_id, rule, created_date_time,
                                last_modified_date_time)
SELECT challenge_rule_id, challenge_id, rule, create_date_time, update_date_time
FROM challenge_rule
ORDER BY challenge_rule_id;

INSERT INTO challenge_hashtag_new (challenge_hashtag_id, challenge_id, hashtag, created_date_time,
                                   last_modified_date_time)
SELECT challenge_hashtag_id, challenge_id, hashtag, create_date_time, update_date_time
FROM challenge_hashtag
ORDER BY challenge_hashtag_id;

INSERT INTO challenge_member_new (challenge_member_id, user_id, challenge_id, goal, is_creator,
                                  status, created_date_time, last_modified_date_time)
SELECT challenge_member_id,
       user_id,
       challenge_id,
       goal,
       is_creator,
       status,
       create_date_time,
       update_date_time
FROM challenge_member
ORDER BY challenge_member_id;

INSERT INTO challenge_example_image
VALUES (1,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_lucky.jpg'),
       (2,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_photo.jpg'),
       (3,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_health.jpg'),
       (4,
        'https://photi-bucket-1.s3.ap-northeast-2.amazonaws.com/challenges/examples/img_cover_study.jpg');

INSERT INTO challenge_history_new (challenge_id, challenge_member_count, visit_count,
                                   created_date_time, last_modified_date_time)
SELECT c.challenge_id,
       c.current_member_cnt,
       c.visit_cnt,
       c.create_date_time,
       c.update_date_time
FROM challenge c
ORDER BY c.challenge_id;

INSERT INTO feed_new (feed_id, user_id, challenge_member_id, challenge_id, image_url,
                      created_date_time, last_modified_date_time)
SELECT f.feed_id,
       cm.user_id,
       f.challenge_member_id,
       f.challenge_id,
       f.image_url,
       f.create_date_time,
       f.update_date_time
FROM feed f
         LEFT JOIN challenge_member cm ON cm.challenge_member_id = f.challenge_member_id
ORDER BY f.feed_id;

INSERT INTO feed_comment_new (feed_comment_id, user_id, challenge_member_id, feed_id, comment,
                              created_date_time, last_modified_date_time)
SELECT fc.feed_comment_id,
       cm.user_id,
       fc.challenge_member_id,
       fc.feed_id,
       COALESCE(fc.comment, '')::varchar(300),
       fc.create_date_time,
       fc.update_date_time
FROM feed_comment fc
         LEFT JOIN challenge_member cm ON cm.challenge_member_id = fc.challenge_member_id
ORDER BY fc.feed_comment_id;

INSERT INTO feed_like_new (feed_like_id, challenge_member_id, feed_id, created_date_time,
                           last_modified_date_time)
SELECT fl.feed_like_id,
       fl.challenge_member_id,
       fl.feed_id,
       fl.create_date_time,
       fl.update_date_time
FROM feed_like fl
ORDER BY fl.feed_like_id;

INSERT INTO feed_history_new (feed_id, like_count, comment_count, created_date_time,
                              last_modified_date_time)
SELECT f.feed_id,
       f.like_cnt,
       f.comment_cnt,
       f.create_date_time,
       f.update_date_time
FROM feed f
ORDER BY f.feed_id;

INSERT INTO inquiry_new (inquiry_id, user_id, category, content, created_date_time,
                         last_modified_date_time)
SELECT i.inquiry_id,
       i.user_id,
       i.type,
       i.content,
       i.create_date_time,
       i.update_date_time
FROM inquiry i
ORDER BY i.inquiry_id;

INSERT INTO report_new (report_id, reporter_id, target_id, category, reason, content,
                        created_date_time, last_modified_date_time)
SELECT r.report_id,
       r.reporter_id,
       r.target_id,
       r.category,
       r.reason,
       r.content,
       r.create_date_time,
       r.update_date_time
FROM report r
ORDER BY r.report_id;

INSERT INTO app_version_new (app_version_id, os, min_version, created_date_time,
                             last_modified_date_time)
SELECT v.app_version_id,
       v.os,
       v.min_version,
       v.create_date_time,
       v.update_date_time
FROM app_version v
ORDER BY v.app_version_id;

INSERT INTO user_challenge_history_new (user_id, challenge_count, ended_challenge_count, feed_count,
                                        created_date_time, last_modified_date_time)
SELECT u.user_id,
       COALESCE(u.challenge_cnt, 0),
       0,
       COALESCE(u.feed_cnt, 0),
       u.create_date_time,
       u.update_date_time
FROM users u
ORDER BY u.user_id;

SET session_replication_role = 'origin';

ALTER TABLE users_new
    ADD CONSTRAINT uq_email_new UNIQUE (email);

ALTER TABLE users_new
    ADD CONSTRAINT uq_username_new UNIQUE (username);

ALTER TABLE user_challenge_history_new
    ADD CONSTRAINT fk_user_challenge_history_user_id_new FOREIGN KEY (user_id) REFERENCES users_new (user_id);

ALTER TABLE inquiry_new
    ADD CONSTRAINT fk_inquiry_user_id_new FOREIGN KEY (user_id) REFERENCES users_new (user_id);

ALTER TABLE challenge_rule_new
    ADD CONSTRAINT fk_challenge_rule_challenge_id_new FOREIGN KEY (challenge_id) REFERENCES challenge_new (challenge_id) ON DELETE CASCADE;

ALTER TABLE challenge_hashtag_new
    ADD CONSTRAINT fk_challenge_hashtag_challenge_id_new FOREIGN KEY (challenge_id) REFERENCES challenge_new (challenge_id) ON DELETE CASCADE;

ALTER TABLE challenge_member_new
    ADD CONSTRAINT fk_challenge_member_user_id_new FOREIGN KEY (user_id) REFERENCES users_new (user_id);

ALTER TABLE challenge_member_new
    ADD CONSTRAINT fk_challenge_member_challenge_id_new FOREIGN KEY (challenge_id) REFERENCES challenge_new (challenge_id);

ALTER TABLE challenge_history_new
    ADD CONSTRAINT fk_challenge_history_challenge_id_new FOREIGN KEY (challenge_id) REFERENCES challenge_new (challenge_id);

ALTER TABLE feed_new
    ADD CONSTRAINT fk_feed_user_id_new FOREIGN KEY (user_id) REFERENCES users_new (user_id);

ALTER TABLE feed_new
    ADD CONSTRAINT fk_feed_challenge_id_new FOREIGN KEY (challenge_id) REFERENCES challenge_new (challenge_id);

ALTER TABLE feed_new
    ADD CONSTRAINT fk_feed_challenge_member_id_new FOREIGN KEY (challenge_member_id) REFERENCES challenge_member_new (challenge_member_id);

ALTER TABLE feed_comment_new
    ADD CONSTRAINT fk_feed_comment_user_id_new FOREIGN KEY (user_id) REFERENCES users_new (user_id);

ALTER TABLE feed_comment_new
    ADD CONSTRAINT fk_feed_comment_feed_id_new FOREIGN KEY (feed_id) REFERENCES feed_new (feed_id);

ALTER TABLE feed_comment_new
    ADD CONSTRAINT fk_feed_comment_challenge_member_id_new FOREIGN KEY (challenge_member_id) REFERENCES challenge_member_new (challenge_member_id);

ALTER TABLE feed_like_new
    ADD CONSTRAINT fk_feed_like_feed_id_new FOREIGN KEY (feed_id) REFERENCES feed_new (feed_id);

ALTER TABLE feed_like_new
    ADD CONSTRAINT fk_feed_like_challenge_member_id_new FOREIGN KEY (challenge_member_id) REFERENCES challenge_member_new (challenge_member_id);

ALTER TABLE feed_like_new
    ADD CONSTRAINT uq_feed_like_challenge_member_id_feed_id_new UNIQUE (challenge_member_id, feed_id);

ALTER TABLE feed_history_new
    ADD CONSTRAINT fk_feed_history_feed_id_new FOREIGN KEY (feed_id) REFERENCES feed_new (feed_id);

ALTER TABLE feed_history_new
    ADD CONSTRAINT uq_feed_history_feed_id_new UNIQUE (feed_id);

CREATE INDEX idx_challenge_status_new ON challenge_new (status);
CREATE INDEX idx_challenge_name_trgm_new ON challenge_new USING GIN (name gin_trgm_ops);
CREATE INDEX idx_challenge_hashtag_trgm_new ON challenge_hashtag_new USING GIN (hashtag gin_trgm_ops);
CREATE INDEX idx_feed_challenge_id_created_date_time_new ON feed_new (challenge_id, created_date_time);
CREATE INDEX idx_feed_comment_feed_id_new ON feed_comment_new (feed_id);

SELECT setval(pg_get_serial_sequence('users_new', 'user_id'),
              COALESCE((SELECT MAX(user_id) FROM users_new), 1), true);
SELECT setval(pg_get_serial_sequence('report_new', 'report_id'),
              COALESCE((SELECT MAX(report_id) FROM report_new), 1), true);
SELECT setval(pg_get_serial_sequence('inquiry_new', 'inquiry_id'),
              COALESCE((SELECT MAX(inquiry_id) FROM inquiry_new), 1), true);
SELECT setval(pg_get_serial_sequence('challenge_new', 'challenge_id'),
              COALESCE((SELECT MAX(challenge_id) FROM challenge_new), 1), true);
SELECT setval(pg_get_serial_sequence('challenge_rule_new', 'challenge_rule_id'),
              COALESCE((SELECT MAX(challenge_rule_id) FROM challenge_rule_new), 1), true);
SELECT setval(pg_get_serial_sequence('challenge_hashtag_new', 'challenge_hashtag_id'),
              COALESCE((SELECT MAX(challenge_hashtag_id) FROM challenge_hashtag_new), 1), true);
SELECT setval(pg_get_serial_sequence('challenge_member_new', 'challenge_member_id'),
              COALESCE((SELECT MAX(challenge_member_id) FROM challenge_member_new), 1), true);
SELECT setval(pg_get_serial_sequence('feed_new', 'feed_id'),
              COALESCE((SELECT MAX(feed_id) FROM feed_new), 1), true);
SELECT setval(pg_get_serial_sequence('feed_comment_new', 'feed_comment_id'),
              COALESCE((SELECT MAX(feed_comment_id) FROM feed_comment_new), 1), true);
SELECT setval(pg_get_serial_sequence('feed_like_new', 'feed_like_id'),
              COALESCE((SELECT MAX(feed_like_id) FROM feed_like_new), 1), true);
SELECT setval(pg_get_serial_sequence('app_version_new', 'app_version_id'),
              COALESCE((SELECT MAX(app_version_id) FROM app_version_new), 1), true);
SELECT setval(pg_get_serial_sequence('user_challenge_history_new', 'user_challenge_history_id'),
              COALESCE((SELECT MAX(user_challenge_history_id) FROM user_challenge_history_new), 1),
              true);
SELECT setval(pg_get_serial_sequence('challenge_history_new', 'challenge_history_id'),
              COALESCE((SELECT MAX(challenge_history_id) FROM challenge_history_new), 1), true);
SELECT setval(pg_get_serial_sequence('feed_history_new', 'feed_history_id'),
              COALESCE((SELECT MAX(feed_history_id) FROM feed_history_new), 1), true);
