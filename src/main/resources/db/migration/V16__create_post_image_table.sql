CREATE TABLE IF NOT EXISTS post_image
(
    id         BIGINT    NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted TINYINT   NOT NULL DEFAULT 0,
    post_id  BIGINT,
    image_id   BIGINT,
    FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE SET NULL,
    FOREIGN KEY (image_id) REFERENCES image (id) ON DELETE SET NULL
    ) default character set utf8mb4
    collate utf8mb4_bin;