
CREATE TABLE IF NOT EXISTS image
(
    id         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    path       TEXT       NOT NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS user
(
    id               BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted       TINYINT(1)   NOT NULL DEFAULT 0,
    account          VARCHAR(255) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL,
    nickname         VARCHAR(255) NOT NULL,
    profile_image_id BIGINT,
    oauth_type       VARCHAR(255) NOT NULL,
    user_type        VARCHAR(255) NOT NULL,
    FOREIGN KEY (profile_image_id) REFERENCES image (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS user_count
(
    user_id      BIGINT NOT NULL PRIMARY KEY,
    review_count INT    NOT NULL DEFAULT 0,
    friend_count INT    NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS shop
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted    TINYINT(1)   NOT NULL DEFAULT 0,
    place_id      VARCHAR(255) NOT NULL,
    place_name    VARCHAR(255) NOT NULL,
    x             VARCHAR(255) NOT NULL,
    y             VARCHAR(255) NOT NULL,
    category_name VARCHAR(255) NOT NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS shop_count
(
    shop_id      BIGINT NOT NULL PRIMARY KEY,
    total_rating INT    NOT NULL DEFAULT 0,
    rating_count INT    NOT NULL DEFAULT 0,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON DELETE CASCADE
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS rating
(
    id           BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted   TINYINT(1) NOT NULL DEFAULT 0,
    user_id      BIGINT,
    shop_id      BIGINT,
    rating_score INT        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS review
(
    id         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    writer_id  BIGINT,
    shop_id    BIGINT,
    content    TEXT,
    is_temp    TINYINT(1) NOT NULL DEFAULT 0,
    FOREIGN KEY (writer_id) REFERENCES user (id) ON DELETE SET NULL,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS review_image
(
    id         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    review_id  BIGINT,
    image_id   BIGINT,
    FOREIGN KEY (review_id) REFERENCES review (id) ON DELETE SET NULL,
    FOREIGN KEY (image_id) REFERENCES image (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS follow
(
    id          BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    user_id     BIGINT,
    follower_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL,
    FOREIGN KEY (follower_id) REFERENCES user (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS follow_request
(
    id          BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted  TINYINT(1) NOT NULL DEFAULT 0,
    user_id     BIGINT,
    follower_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL,
    FOREIGN KEY (follower_id) REFERENCES user (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS scrap_directory
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted TINYINT(1)   NOT NULL DEFAULT 0,
    name       VARCHAR(255) NOT NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS scrap
(
    id           BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted   TINYINT(1) NOT NULL DEFAULT 0,
    user_id      BIGINT,
    shop_id      BIGINT,
    directory_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL,
    FOREIGN KEY (shop_id) REFERENCES shop (id) ON DELETE SET NULL,
    FOREIGN KEY (directory_id) REFERENCES scrap_directory (id) ON DELETE SET NULL
) default character set utf8
  collate utf8_general_ci;

CREATE TABLE IF NOT EXISTS post
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
    is_deleted TINYINT(1)   NOT NULL DEFAULT 0,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    board_type VARCHAR(255) NOT NULL
) default character set utf8
  collate utf8_general_ci;
