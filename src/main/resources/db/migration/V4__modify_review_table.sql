ALTER TABLE `review` DROP `is_temp`;
ALTER TABLE `review` ADD `rate` TINYINT DEFAULT 0;