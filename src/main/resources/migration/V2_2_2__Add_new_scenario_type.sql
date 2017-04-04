ALTER TABLE `SecurityCriteria` ADD `dtExploitabilityCat` INT(11) NOT NULL DEFAULT '0' AFTER `dtEnvironmentalSource`;
ALTER TABLE `SecurityCriteria` ADD `dtReliabilityCat` INT(11) NOT NULL DEFAULT '0';
ALTER TABLE `SecurityCriteria` CHANGE `dtExploitabilityCat` `dtExploitabilityCat` INT(11) NOT NULL;
ALTER TABLE `SecurityCriteria` CHANGE `dtReliabilityCat` `dtReliabilityCat` INT(11) NOT NULL;