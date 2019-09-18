START TRANSACTION;

ALTER TABLE `Standard` ADD `dtName` VARCHAR(255) NOT NULL DEFAULT '' AFTER `dtLabel`;

UPDATE `Standard` SET `dtName`= `dtLabel`;

ALTER TABLE `Standard` CHANGE `dtName` `dtName` VARCHAR(255) NOT NULL;


ALTER TABLE `ActionPlanSummary`
  DROP `dtNotCompliantMeasure27001Count`,
  DROP `dtNotCompliantMeasure27002Count`;

ALTER TABLE `ActionPlanSummaryStandardConformance` ADD `dtNotCompliantMeasureCount` INT NOT NULL DEFAULT '0' AFTER `dtConformance`;

COMMIT;