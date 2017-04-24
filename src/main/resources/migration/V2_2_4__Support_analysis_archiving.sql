ALTER TABLE `Analysis` ADD `dtArchived` BIT(1) NOT NULL DEFAULT b'0' AFTER `idAnalysis`;
ALTER TABLE `Analysis` CHANGE `dtArchived` `dtArchived` BIT(1) NOT NULL;
