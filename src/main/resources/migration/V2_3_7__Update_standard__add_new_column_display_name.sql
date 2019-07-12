START TRANSACTION;

ALTER TABLE `Standard` ADD `dtName` VARCHAR(255) NOT NULL DEFAULT '' AFTER `dtLabel`;

UPDATE `Standard` SET `dtName`= `dtLabel`;

ALTER TABLE `Standard` CHANGE `dtName` `dtName` VARCHAR(255) NOT NULL;

COMMIT;