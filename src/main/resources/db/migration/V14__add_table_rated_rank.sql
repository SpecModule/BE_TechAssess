CREATE TABLE rated_rank
(
    id                 BIGINT AUTO_INCREMENT  NOT NULL,
    user_id            BIGINT                 NOT NULL,
    created_at         datetime DEFAULT NOW() NULL,
    total_point        DECIMAL(15, 2)         NULL,
    `rank`             VARCHAR(45)            NULL,
    level_up_recommend INT                    NULL,
    CONSTRAINT pk_rated_rank PRIMARY KEY (id)
);

ALTER TABLE rated_rank
    ADD CONSTRAINT FK_RATED_RANK_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);