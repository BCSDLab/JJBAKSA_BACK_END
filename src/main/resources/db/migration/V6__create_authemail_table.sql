ALTER TABLE user ADD auth_email TINYINT(1) NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS auth_email
(
    id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted       TINYINT      NOT NULL DEFAULT 0,
    user_id          BIGINT,
    secret           VARCHAR(255) NOT NULL,
    expired_at       TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL
) default character set utf8mb4
  collate utf8mb4_bin;