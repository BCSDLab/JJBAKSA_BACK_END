CREATE TABLE IF NOT EXISTS inquiry
(
    id          BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted  TINYINT         NOT NULL DEFAULT 0,
    is_secreted TINYINT         NOT NULL DEFAULT 0,
    title       VARCHAR(255)    NOT NULL,
    content     TEXT            NOT NULL,
    secret      VARCHAR(255),
    answer      TEXT,
    writer_id   BIGINT,
    FOREIGN KEY (writer_id) REFERENCES user (id) ON DELETE CASCADE
    ) default character set utf8mb4
    collate utf8mb4_bin;