START TRANSACTION;
ALTER TABLE `EmailTemplate` ADD `dtFormat` ENUM('TEXT', 'HTML', 'JSON') NOT NULL DEFAULT 'TEXT' COMMENT 'Format of the email template: TEXT, HTML or JSON' AFTER `dtHtml`;
UPDATE `EmailTemplate` SET `dtFormat` = 'TEXT' WHERE `dtHtml` IS NULL or `dtHtml` = 0;
UPDATE `EmailTemplate` SET `dtFormat` = 'HTML' WHERE `dtHtml` ;
ALTER TABLE `EmailTemplate` DROP COLUMN `dtHtml`;
COMMIT;