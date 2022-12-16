CREATE TABLE IF NOT EXISTS search
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    content       VARCHAR(255) NOT NULL,
    score         BIGINT       NOT NULL DEFAULT 1
) default character set utf8mb4
  collate utf8mb4_bin;