START TRANSACTION;

ALTER TABLE `Assessment` ADD `dtVulnerability` INT(11) NOT NULL DEFAULT '1' AFTER `dtUncertainty`;
ALTER TABLE `RiskProfile` ADD `dtRawVulnerability` INT(11) NULL DEFAULT '1' AFTER `dtTreatment`; 
ALTER TABLE `RiskProfile` ADD `dtExpVulnerability` INT(11) NULL DEFAULT '1' AFTER `dtRawVulnerability`;

ALTER TABLE `Assessment` CHANGE `dtVulnerability` `dtVulnerability` INT(11) NOT NULL;
--ALTER TABLE `RiskProfile` CHANGE `dtRawVulnerability` `dtRawVulnerability` INT(11) NOT NULL;
--ALTER TABLE `RiskProfile` CHANGE `dtExpVulnerability` `dtExpVulnerability` INT(11) NOT NULL; 

COMMIT;