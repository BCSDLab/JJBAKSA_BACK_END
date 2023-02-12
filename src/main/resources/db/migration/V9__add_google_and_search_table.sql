CREATE TABLE IF NOT EXISTS google_shop
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted    TINYINT      NOT NULL DEFAULT 0,
    place_id      VARCHAR(255) NOT NULL
) default character set utf8mb4
    collate utf8mb4_bin;

CREATE TABLE IF NOT EXISTS google_shop_count
(
    shop_id      BIGINT NOT NULL PRIMARY KEY,
    total_rating INT    NOT NULL DEFAULT 0,
    rating_count INT    NOT NULL DEFAULT 0,
    FOREIGN KEY (shop_id) REFERENCES google_shop (id) ON DELETE CASCADE
) default character set utf8mb4
    collate utf8mb4_bin;

CREATE TABLE IF NOT EXISTS search
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    content       VARCHAR(255) NOT NULL,
    score         BIGINT       NOT NULL DEFAULT 1
) default character set utf8mb4
    collate utf8mb4_bin;
