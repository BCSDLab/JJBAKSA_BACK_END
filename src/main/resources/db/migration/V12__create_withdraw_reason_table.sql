CREATE TABLE IF NOT EXISTS withdraw_reason
(
    user_id          BIGINT       PRIMARY KEY,
    reason           VARCHAR(30)  NOT NULL,
    discomfort       VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) default character set utf8mb4
collate utf8mb4_bin;