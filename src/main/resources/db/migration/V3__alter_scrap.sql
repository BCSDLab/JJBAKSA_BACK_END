ALTER TABLE user_count
    ADD COLUMN scrap_count INT NOT NULL DEFAULT 0;

ALTER TABLE scrap_directory
    ADD COLUMN user_id BIGINT,
    ADD FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE SET NULL;

CREATE TABLE IF NOT EXISTS scrap_directory_count
(
    directory_id BIGINT NOT NULL PRIMARY KEY,
    scrap_count  INT    NOT NULL DEFAULT 0,
    FOREIGN KEY (directory_id) REFERENCES scrap_directory (id) ON DELETE CASCADE
) default character set utf8mb4
  collate utf8mb4_bin;