START TRANSACTION;

ALTER TABLE `Assessment` ADD `dtCockpit` VARCHAR(1024) NOT NULL DEFAULT '' AFTER `dtHiddenComment`;
ALTER TABLE `Asset` ADD `dtRelatedName` VARCHAR(255) NOT NULL DEFAULT '' AFTER `dtHiddenComment`;

COMMIT;