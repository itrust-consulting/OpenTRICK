ALTER TABLE `Analysis` CHANGE `dtProfile` `dtProfile` bit(1) NOT NULL;
ALTER TABLE `Standard` CHANGE `dtDescription` `dtDescription` VARCHAR(2048) NOT NULL;

CREATE TABLE IF NOT EXISTS `AnalysisSetting` (
  `fiAnalysis` int(11) NOT NULL,
  `dtValue` varchar(255) DEFAULT NULL,
  `dtName` varchar(255) NOT NULL,
  PRIMARY KEY (`fiAnalysis`,`dtName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `AnalysisSetting`
  ADD CONSTRAINT `FK72buktkxml77ml75wv5i2491t` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);