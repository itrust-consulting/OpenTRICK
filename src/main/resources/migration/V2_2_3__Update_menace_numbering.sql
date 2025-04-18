UPDATE `RiskInformation` SET `dtChapter`= CONCAT('7',SUBSTRING(`dtChapter`,2)) WHERE dtCategory = 'Risk_TBA';
ALTER TABLE `RiskInformation` ADD `dtCustom` BIT(1) NOT NULL DEFAULT b'0' AFTER `dtComment`;
ALTER TABLE `RiskInformation` CHANGE `dtCustom` `dtCustom` BIT(1) NOT NULL;
