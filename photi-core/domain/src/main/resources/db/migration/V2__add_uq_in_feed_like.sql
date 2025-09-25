ALTER TABLE feed_like
    DROP CONSTRAINT IF EXISTS uq_feed_like_challenge_member_feed;

ALTER TABLE feed_like
    ADD CONSTRAINT uq_feed_like_challenge_member_feed
        UNIQUE (challenge_member_id, feed_id);