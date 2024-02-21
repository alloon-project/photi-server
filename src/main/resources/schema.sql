DROP TABLE IF EXISTS block;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS suspension;
DROP TABLE IF EXISTS mission_hashtag;
DROP TABLE IF EXISTS hashtag;
DROP TABLE IF EXISTS feed_like;
DROP TABLE IF EXISTS feed_comment;
DROP TABLE IF EXISTS feed;
DROP TABLE IF EXISTS mission_member;
DROP TABLE IF EXISTS mission;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS contact;


CREATE TABLE contact
(
    contact_id	        BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email	            VARCHAR(127)	            NOT NULL,
    verification_code	VARCHAR(6)	                NOT NULL,
    is_verified	        BIT	                        NOT NULL,
    created_date_time	DATETIME(6)	                NOT NULL,
    updated_date_time	DATETIME(6)	                NOT NULL,
    CONSTRAINT uq_contact UNIQUE (email)
);

CREATE TABLE users
(
    user_id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username              VARCHAR(15)               NOT NULL,
    password              VARCHAR(255)              NOT NULL,
    image_url             VARCHAR(255),
    is_temporary_password BIT                       NOT NULL,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    contact_id            BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_user_contact FOREIGN KEY (contact_id) REFERENCES contact (contact_id)
);

CREATE TABLE user_role
(
    user_role_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    role                  VARCHAR(6)                NOT NULL,
    user_id               BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE mission
(
    mission_id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    mission_name          VARCHAR(31)               NOT NULL,
    description           VARCHAR(511)              NOT NULL,
    goal                  VARCHAR(255),
    rule                  VARCHAR(255),
    image_url             VARCHAR(255),
    current_member_cnt    INT UNSIGNED              NOT NULL,
    visited_cnt           INT UNSIGNED              NOT NULL,
    is_recruiting         BIT                       NOT NULL,
    started_date          DATE                      NOT NULL,
    ended_date            DATE                      NOT NULL,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL
);

CREATE TABLE report
(
    report_id             BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    user_id               BIGINT UNSIGNED,
    reporter_id           BIGINT UNSIGNED,
    mission_id            BIGINT UNSIGNED,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
);

CREATE TABLE suspension
(
    suspend_id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    started_datetime      DATE                      NOT NULL,
    ended_datetime        DATE                      NOT NULL,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    user_id               BIGINT UNSIGNED           NOT NULL,
    admin_id              BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_suspension_admin FOREIGN KEY (admin_id) REFERENCES users (user_id),
    CONSTRAINT fk_suspension_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE block
(
    block_id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL,
    user_id               BIGINT UNSIGNED           NOT NULL,
    blocker_id            BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_bock_blocker FOREIGN KEY (blocker_id) REFERENCES users (user_id),
    CONSTRAINT fk_bock_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE mission_member
(
    mission_member_id     BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    is_creator            BIT                       NOT NULL,
    status                VARCHAR(15)               NOT NULL,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL,
    mission_id            BIGINT UNSIGNED           NOT NULL,
    user_id               BIGINT UNSIGNED,
    CONSTRAINT fk_mission_member_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_mission_member_mission_id FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
);


CREATE TABLE hashtag
(
    hashtag_id            BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    hashtag               VARCHAR(31)               NOT NULL,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL
);

CREATE TABLE mission_hashtag
(
    mission_hashtag_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL,
    hashtag_id            BIGINT UNSIGNED           NOT NULL,
    mission_id            BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_mission_hashtag_hashtag FOREIGN KEY (hashtag_id) REFERENCES hashtag (hashtag_id),
    CONSTRAINT fk_mission_hashtag_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
);

CREATE TABLE feed
(
    feed_id               BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    liked_cnt             INT UNSIGNED              NOT NULL,
    image_url             VARCHAR(255),
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL,
    mission_id            BIGINT UNSIGNED           NOT NULL,
    mission_member_id     BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_feed_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id),
    CONSTRAINT fk_feed_mission_member FOREIGN KEY (mission_member_id) REFERENCES mission_member (mission_member_id)
);

CREATE TABLE feed_comment
(
    feed_comment_id       BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    comment               VARCHAR(255),
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL,
    feed_id               BIGINT UNSIGNED           NOT NULL,
    mission_member_id     BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_feed_comment_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_comment_mission_member FOREIGN KEY (mission_member_id) REFERENCES mission_member (mission_member_id)
);

create table feed_like
(
    feed_like_id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    created_date_time     DATETIME(6)               NOT NULL,
    updated_date_time     DATETIME(6)               NOT NULL,
    is_deleted            BIT                       NOT NULL,
    feed_id               BIGINT UNSIGNED           NOT NULL,
    mission_member_id     BIGINT UNSIGNED           NOT NULL,
    CONSTRAINT fk_feed_like_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_like_mission_member FOREIGN KEY (mission_member_id) REFERENCES mission_member (mission_member_id)
);