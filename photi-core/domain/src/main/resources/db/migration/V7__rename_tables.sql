BEGIN;

ALTER TABLE app_version
    RENAME TO app_version_old;
ALTER TABLE app_version_new
    RENAME TO app_version;
ALTER TABLE users
    RENAME TO users_old;
ALTER TABLE users_new
    RENAME TO users;
ALTER TABLE challenge
    RENAME TO challenge_old;
ALTER TABLE challenge_new
    RENAME TO challenge;
ALTER TABLE challenge_rule
    RENAME TO challenge_rule_old;
ALTER TABLE challenge_rule_new
    RENAME TO challenge_rule;
ALTER TABLE challenge_hashtag
    RENAME TO challenge_hashtag_old;
ALTER TABLE challenge_hashtag_new
    RENAME TO challenge_hashtag;
ALTER TABLE challenge_member
    RENAME TO challenge_member_old;
ALTER TABLE challenge_member_new
    RENAME TO challenge_member;
ALTER TABLE feed
    RENAME TO feed_old;
ALTER TABLE feed_new
    RENAME TO feed;
ALTER TABLE feed_comment
    RENAME TO feed_comment_old;
ALTER TABLE feed_comment_new
    RENAME TO feed_comment;
ALTER TABLE feed_like
    RENAME TO feed_like_old;
ALTER TABLE feed_like_new
    RENAME TO feed_like;
ALTER TABLE inquiry
    RENAME TO inquiry_old;
ALTER TABLE inquiry_new
    RENAME TO inquiry;
ALTER TABLE report
    RENAME TO report_old;
ALTER TABLE report_new
    RENAME TO report;
ALTER TABLE challenge_history_new
    RENAME TO challenge_history;
ALTER TABLE feed_history_new
    RENAME TO feed_history;
ALTER TABLE user_challenge_history_new
    RENAME TO user_challenge_history;

DROP TABLE block;
DROP TABLE suspension;
DROP TABLE challenge_template_image;
DROP TABLE inquiry_category;
DROP TABLE report_category;
DROP TABLE user_template_image;
DROP TABLE user_role;
DROP TABLE contact;
DROP TABLE app_version_old;
DROP TABLE users_old;
DROP TABLE challenge_old;
DROP TABLE challenge_rule_old;
DROP TABLE challenge_hashtag_old;
DROP TABLE challenge_member_old;
DROP TABLE feed_old;
DROP TABLE feed_comment_old;
DROP TABLE feed_like_old;
DROP TABLE inquiry_old;
DROP TABLE report_old;

ANALYZE;

COMMIT;
