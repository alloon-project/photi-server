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
DROP TABLE IF EXISTS mission_hashtag;
DROP TABLE IF EXISTS hashtag;
DROP TABLE IF EXISTS mission_member;
DROP TABLE IF EXISTS mission_image_template;
DROP TABLE IF EXISTS mission_rule;
DROP TABLE IF EXISTS mission;
DROP TABLE IF EXISTS user_image_template;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS contact;


CREATE TABLE contact
(
    contact_id	                BIGINT AUTO_INCREMENT PRIMARY KEY,
    email	                    VARCHAR(100)	          NOT NULL,
    verification_code	        VARCHAR(6)	              NOT NULL,
    verify_yn	                BIT	                      NOT NULL,
    create_date_time	        DATETIME(6)	              NOT NULL,
    update_date_time	        DATETIME(6)	              NOT NULL,
    CONSTRAINT uq_contact UNIQUE (email)
);

CREATE TABLE users
(
    user_id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    username                    VARCHAR(20)               NOT NULL,
    password                    VARCHAR(255)              NOT NULL,
    image_url                   VARCHAR(500)              NOT NULL,
    temporary_password_yn       BIT                       NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    contact_id                  BIGINT                    NOT NULL,
    CONSTRAINT fk_user_contact FOREIGN KEY (contact_id) REFERENCES contact (contact_id),
    CONSTRAINT uq_users UNIQUE (username)
);

CREATE TABLE user_role
(
    user_role_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    role                        VARCHAR(6)                NOT NULL,
    user_id                     BIGINT                    NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_template_image
(
    user_template_image_id      INT AUTO_INCREMENT PRIMARY KEY,
    image_url                   VARCHAR(500)              NOT NULL,
    start_date_time             DATETIME(6)               NOT NULL,
    end_date_time               DATETIME(6)               NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_user_template_image_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE mission
(
    mission_id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    mission_name                VARCHAR(16)               NOT NULL,
    description                 VARCHAR(120)              NOT NULL,
    goal                        VARCHAR(30),
    image_url                   VARCHAR(500)              NOT NULL,
    current_member_cnt          INT                       NOT NULL,
    visit_cnt                   INT                       NOT NULL,
    recruit_yn                  BIT                       NOT NULL,
    start_date                  DATE                      NOT NULL,
    end_date                    DATE                      NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL
);

CREATE TABLE mission_rule
(
    mission_rule_id             INT AUTO_INCREMENT PRIMARY KEY,
    rule                        VARCHAR(30)               NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    mission_id                  BIGINT                    NOT NULL,
    CONSTRAINT fk_mission_rule_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
);

CREATE TABLE mission_template_image
(
    mission_template_image_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url                   VARCHAR(500)              NOT NULL,
    start_date_time             DATETIME(6)               NOT NULL,
    end_date_time               DATETIME(6)               NOT NULL,
    sort                        INT                       NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_mission_image_template_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE mission_member
(
    mission_member_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    creator_yn                  BIT                       NOT NULL,
    mission_member_status       VARCHAR(15)               NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    mission_id                  BIGINT                    NOT NULL,
    user_id                     BIGINT,
    CONSTRAINT fk_mission_member_user_id FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_mission_member_mission_id FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
);


CREATE TABLE hashtag
(
    hashtag_id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    hashtag                     VARCHAR(30)               NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL
);

CREATE TABLE mission_hashtag
(
    mission_hashtag_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    hashtag_id                  BIGINT                    NOT NULL,
    mission_id                  BIGINT                    NOT NULL,
    CONSTRAINT fk_mission_hashtag_hashtag FOREIGN KEY (hashtag_id) REFERENCES hashtag (hashtag_id),
    CONSTRAINT fk_mission_hashtag_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id)
);

CREATE TABLE feed
(
    feed_id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    like_cnt                    INT                       NOT NULL,
    image_url                   VARCHAR(500)              NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    mission_id                  BIGINT                    NOT NULL,
    mission_member_id           BIGINT                    NOT NULL,
    CONSTRAINT fk_feed_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id),
    CONSTRAINT fk_feed_mission_member FOREIGN KEY (mission_member_id) REFERENCES mission_member (mission_member_id)
);

CREATE TABLE feed_comment
(
    feed_comment_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment                     VARCHAR(300),
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    feed_id                     BIGINT                    NOT NULL,
    mission_member_id           BIGINT                    NOT NULL,
    CONSTRAINT fk_feed_comment_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_comment_mission_member FOREIGN KEY (mission_member_id) REFERENCES mission_member (mission_member_id)
);

create table feed_like
(
    feed_like_id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    feed_id                     BIGINT                    NOT NULL,
    mission_member_id           BIGINT                    NOT NULL,
    CONSTRAINT fk_feed_like_feed FOREIGN KEY (feed_id) REFERENCES feed (feed_id),
    CONSTRAINT fk_feed_like_mission_member FOREIGN KEY (mission_member_id) REFERENCES mission_member (mission_member_id)
);

CREATE TABLE inquiry_category
(
    inquiry_category_id         INT AUTO_INCREMENT PRIMARY KEY,
    description                 VARCHAR(30)               NOT NULL,
    sort                        INT                       NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_inquiry_category_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE inquiry
(
    inquiry_id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    content                     VARCHAR(100)              NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    inquiry_category_id         INT                       NOT NULL,
    user_id                     BIGINT,
    CONSTRAINT fk_inquiry_inquiry_category FOREIGN KEY (inquiry_category_id) REFERENCES inquiry_category (inquiry_category_id),
    CONSTRAINT fk_inquiry_category_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE report_category
(
    report_category_id          INT AUTO_INCREMENT PRIMARY KEY,
    type                        VARCHAR(15)               NOT NULL,
    description                 VARCHAR(30)               NOT NULL,
    sort                        INT                       NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_report_category_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);

CREATE TABLE report
(
    report_id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    reason                      VARCHAR(300),
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    user_id                     BIGINT,
    reporter_id                 BIGINT,
    mission_id                  BIGINT,
    report_category_id          INT                       NOT NULL,
    CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (user_id),
    CONSTRAINT fk_report_mission FOREIGN KEY (mission_id) REFERENCES mission (mission_id),
    CONSTRAINT fk_report_report_category FOREIGN KEY (report_category_id) REFERENCES report_category (report_category_id)
);

CREATE TABLE suspension
(
    suspend_id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date                  DATE                      NOT NULL,
    end_date                    DATE                      NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    user_id                     BIGINT,
    admin_id                    BIGINT,
    CONSTRAINT fk_suspension_admin FOREIGN KEY (admin_id) REFERENCES users (user_id),
    CONSTRAINT fk_suspension_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE block
(
    block_id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    service_status              VARCHAR(10)               NOT NULL,
    user_id                     BIGINT,
    blocker_id                  BIGINT,
    CONSTRAINT fk_bock_blocker FOREIGN KEY (blocker_id) REFERENCES users (user_id),
    CONSTRAINT fk_bock_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE ver
(
    ver_id                      INT AUTO_INCREMENT PRIMARY KEY,
    version                     VARCHAR(10)               NOT NULL,
    create_date_time            DATETIME(6)               NOT NULL,
    update_date_time            DATETIME(6)               NOT NULL,
    admin_id                    BIGINT,
    CONSTRAINT fk_app_version_admin FOREIGN KEY (admin_id) REFERENCES users (user_id)
);
