ALTER TABLE `SecurityCriteria` ADD `dtIlrCat` INT(11) NOT NULL DEFAULT '0' AFTER `dtReliabilityCat`;
ALTER TABLE `SecurityCriteria` CHANGE `dtIlrCat` `dtIlrCat` INT(11) NOT NULL;