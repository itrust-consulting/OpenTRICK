SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `Parameter` DROP FOREIGN KEY `FK_gmt6cbbow3t8j001q8pnu1nw8`;

ALTER TABLE `MaturityMeasure` DROP FOREIGN KEY `FK58aym60jyndinl3ltxuivajah`;

ALTER TABLE `Parameter` DROP FOREIGN KEY `FKh95ugwcyoo8t4m9t5bcalcvas`;

ALTER TABLE `MaturityParameter` DROP FOREIGN KEY `FK7dah74f0jxvoxooeb1rl26j45`;

ALTER TABLE `MaturityParameter` DROP FOREIGN KEY `FK_pmio6p62piu7xoagu4mppguya`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FK5d6s84rfob5jh8hn2dqspsm97`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FK5v3py49odwssfrs3idhbo2usp`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FK7erdxf04tijy7y4y3fyhsulks`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKatkjpeyqkxw37c7jcgi1yd6mb`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKdli964rro7w03npxvw9u18itj`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKfs1phfhan86olxicbf3nxc9pg`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKg3l0o2729wiou1t726idsdkx2`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKgg3cgrnw6uoggwkaw15v8xekj`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKjy0385haqhrgr1r799focvifa`;

ALTER TABLE `RiskProfile` DROP FOREIGN KEY `FKps92hja4napfk2fchw3v902mg`;

SET FOREIGN_KEY_CHECKS=1;

ALTER TABLE `ActionPlan` ADD `dtRiskCount` INT(11) NOT NULL;
ALTER TABLE `Analysis` ADD `dtType` VARCHAR(255) NOT NULL;
ALTER TABLE `Analysis` CHANGE `dtData` `dtData` bit(1) NOT NULL;
ALTER TABLE `Analysis` CHANGE `dtDefaultProfile` `dtDefaultProfile` bit(1) NOT NULL;
ALTER TABLE `Analysis` CHANGE `dtUncertainty` `dtUncertainty` bit(1) NOT NULL;
ALTER TABLE `Assessment` CHANGE `dtSelected` `dtSelected` bit(1) NOT NULL;
ALTER TABLE `Asset` CHANGE `dtSelected` `dtSelected` bit(1) NOT NULL;
ALTER TABLE `AssetMeasure` CHANGE `dtImplmentationRate` `dtImplmentationRate` varchar(255) NOT NULL;
ALTER TABLE `Customer` CHANGE `dtCanBeUsed` `dtCanBeUsed` bit(1) NOT NULL;
ALTER TABLE `History` CHANGE `dtComment` `dtComment` longtext NOT NULL;
ALTER TABLE `MaturityParameter` CHANGE `idMaturityParameter` `idMaturityParameter` int(11) NOT NULL AUTO_INCREMENT FIRST;
ALTER TABLE `MaturityParameter` ADD `dtDescription` varchar(255) NOT NULL;
ALTER TABLE `MaturityParameter` ADD `dtValue` double NOT NULL;
ALTER TABLE `MaturityParameter` ADD `fiAnalysis` int(11) DEFAULT NULL;
ALTER TABLE `MeasureDescriptionText` CHANGE `dtDomain` `dtDomain` longtext NOT NULL;
ALTER TABLE `NormalMeasure` CHANGE `dtImplementationRate` `dtImplementationRate` varchar(255) NOT NULL;
ALTER TABLE `ParameterType` CHANGE `idParameterType` `idParameterType` int(11) NOT NULL AUTO_INCREMENT FIRST;
ALTER TABLE `ParameterType` CHANGE `dtLabel` `dtName` varchar(255) DEFAULT NULL;
ALTER TABLE `Scenario` CHANGE `dtSelected` `dtSelected` bit(1) NOT NULL;
ALTER TABLE `Scenario` ADD `dtAssetLinked` bit(1) DEFAULT NULL;
ALTER TABLE `Standard` CHANGE `dtAnalysisOnly` `dtAnalysisOnly` bit(1) NOT NULL;
ALTER TABLE `Standard` CHANGE `dtComputable` `dtComputable` bit(1) NOT NULL;
ALTER TABLE `TrickService` CHANGE `dtInstalled` `dtInstalled` bit(1) NOT NULL;
ALTER TABLE `User` CHANGE `dtEnabled` `dtEnabled` bit(1) NOT NULL;
ALTER TABLE `UserSQLite` CHANGE `dtSQLite` `dtSQLite` longblob NOT NULL;
ALTER TABLE `WordReport` CHANGE `dtFile` `dtFile` longblob NOT NULL;


CREATE TABLE IF NOT EXISTS `AssessmentImpacts` (
  `fiAssessment` int(11) NOT NULL,
  `dtValueType` varchar(255) DEFAULT NULL,
  `fiValue` int(11) NOT NULL,
  UNIQUE KEY `UK3euk8i7j5ki85knyxaq5r55bq` (`dtValueType`,`fiValue`),
  KEY `FKsabbq3lfu0cf5blg5g4hx9p3` (`fiAssessment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `DynamicParameter` (
  `idDynamicParameter` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` varchar(255) NOT NULL,
  `dtValue` double NOT NULL,
  `dtAcronym` varchar(255) NOT NULL,
  `fiAnalysis` int(11) DEFAULT NULL,
  PRIMARY KEY (`idDynamicParameter`),
  KEY `FKnrmyxyt52xd33q8wnrejjmaf5` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `ExternalNotification` (
  `idExternalNotification` int(11) NOT NULL AUTO_INCREMENT,
  `dtCategory` varchar(255) NOT NULL,
  `dtHalfLife` bigint(20) NOT NULL,
  `dtNumber` int(11) NOT NULL,
  `dtSeverity` double NOT NULL,
  `dtSourceUserName` varchar(255) NOT NULL,
  `dtTimestamp` bigint(20) NOT NULL,
  `dtType` int(11) NOT NULL,
  PRIMARY KEY (`idExternalNotification`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `IDS` (
  `idIDS` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` varchar(255) DEFAULT NULL,
  `dtEnabled` bit(1) DEFAULT NULL,
  `dtLastAlert` datetime DEFAULT NULL,
  `dtLastUpdate` datetime DEFAULT NULL,
  `dtPrefix` varchar(32) NOT NULL,
  `dtToken` longtext NOT NULL,
  PRIMARY KEY (`idIDS`),
  UNIQUE KEY `UK_tqpce7xd4uhxwutnj4mrxqewd` (`dtPrefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `IDSSubscribers` (
  `fiIDS` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  UNIQUE KEY `UKpvtbaf99knaom66puqm7p9nd0` (`fiIDS`,`fiAnalysis`),
  KEY `FKgtwlmy9ul6nix2fccn0pb4go2` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `ImpactParameter` (
  `idImpactParameter` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` varchar(255) NOT NULL,
  `dtValue` double NOT NULL,
  `dtAcronym` varchar(255) NOT NULL,
  `dtFrom` double NOT NULL,
  `dtTo` double NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtLevel` int(11) NOT NULL,
  `fiParameterType` int(11) NOT NULL,
  `fiAnalysis` int(11) DEFAULT NULL,
  PRIMARY KEY (`idImpactParameter`),
  KEY `FKn1cxejwbwgdyceuv9w6wyu6xd` (`fiParameterType`),
  KEY `FKepl4wucbucs45dqnmltau037h` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `LevelValue` (
  `idLevelValue` int(11) NOT NULL AUTO_INCREMENT,
  `dtParameterType` varchar(255) DEFAULT NULL,
  `fiParameter` int(11) DEFAULT NULL,
  `dtLevel` int(11) DEFAULT NULL,
  PRIMARY KEY (`idLevelValue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `LikelihoodParameter` (
  `idLikelihoodParameter` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` varchar(255) NOT NULL,
  `dtValue` double NOT NULL,
  `dtAcronym` varchar(255) NOT NULL,
  `dtFrom` double NOT NULL,
  `dtTo` double NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtLevel` int(11) NOT NULL,
  `fiAnalysis` int(11) DEFAULT NULL,
  PRIMARY KEY (`idLikelihoodParameter`),
  KEY `FK6rq3rtob9a0ig6o1uf5c4ycw4` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `RealValue` (
  `idRealValue` int(11) NOT NULL AUTO_INCREMENT,
  `dtParameterType` varchar(255) DEFAULT NULL,
  `fiParameter` int(11) DEFAULT NULL,
  `dtValue` double DEFAULT NULL,
  PRIMARY KEY (`idRealValue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `RiskAcceptanceParameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` longtext NOT NULL,
  `dtValue` double NOT NULL,
  `dtColor` varchar(255) NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `fiAnalysis` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8ws3qewwg34ceo7spf617genu` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `RiskProfileExpImpacts` (
  `fiRiskProfile` int(11) NOT NULL,
  `fiExpImpact` int(11) NOT NULL,
  UNIQUE KEY `UKb0e9ssqnv4efubeh73kn0kku5` (`fiExpImpact`,`fiRiskProfile`),
  KEY `FK76ah301uvl2dellyqy3svtjcg` (`fiRiskProfile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `RiskProfileMeasures` (
  `fiRiskProfile` int(11) NOT NULL,
  `fiMeasure` int(11) NOT NULL,
  UNIQUE KEY `UK58d7lfuur75ys0fyg1s1bvjhn` (`fiRiskProfile`,`fiMeasure`),
  KEY `FK3sqqm2mrtgf0sq370mnguwmkv` (`fiMeasure`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `RiskProfileRawImpacts` (
  `fiRiskProfile` int(11) NOT NULL,
  `fiRawImpact` int(11) NOT NULL,
  UNIQUE KEY `UKpds08dwu6944tqnouks79hduu` (`fiRawImpact`,`fiRiskProfile`),
  KEY `FKsvgs9teitufgb36erm1terg61` (`fiRiskProfile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `ScaleType` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dtName` varchar(255) DEFAULT NULL,
  `dtAcronym` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2j2wnpkt3xrab4b2cc9ic9ti3` (`dtName`),
  UNIQUE KEY `UK_nbaicmx4hoqyxt2tvtwyiu2io` (`dtAcronym`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `ScaleTypeTranslations` (
  `fiScaleType` int(11) NOT NULL,
  `dtTranslate` varchar(255) NOT NULL,
  `dtShortName` varchar(255) NOT NULL,
  `dtLocale` varchar(255) NOT NULL,
  PRIMARY KEY (`fiScaleType`,`dtLocale`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `ScenarioLinkedAsset` (
  `fiScenario` int(11) NOT NULL,
  `fiAsset` int(11) NOT NULL,
  UNIQUE KEY `UKjc78juqrgxugfljqfmyptswh` (`fiScenario`,`fiAsset`),
  KEY `FKf46a37g72aorrv6atnx10oxc6` (`fiAsset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `SimpleParameter` (
  `idSimpleParameter` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` varchar(255) NOT NULL,
  `dtValue` double NOT NULL,
  `fiParameterType` int(11) NOT NULL,
  `fiAnalysis` int(11) DEFAULT NULL,
  PRIMARY KEY (`idSimpleParameter`),
  KEY `FKaj6832gix9hcbyfxu0m69yv6c` (`fiParameterType`),
  KEY `FK5d7a1yyntmdfm4eni0ww15q8x` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `Value` (
  `idValue` int(11) NOT NULL AUTO_INCREMENT,
  `dtParameterType` varchar(255) DEFAULT NULL,
  `fiParameter` int(11) DEFAULT NULL,
  PRIMARY KEY (`idValue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

ALTER TABLE `AssessmentImpacts`
  ADD CONSTRAINT `FKsabbq3lfu0cf5blg5g4hx9p3` FOREIGN KEY (`fiAssessment`) REFERENCES `Assessment` (`idAssessment`);

ALTER TABLE `DynamicParameter`
  ADD CONSTRAINT `FKnrmyxyt52xd33q8wnrejjmaf5` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `IDSSubscribers`
  ADD CONSTRAINT `FK4io0dwef74svghmj9xlsxlq45` FOREIGN KEY (`fiIDS`) REFERENCES `IDS` (`idIDS`),
  ADD CONSTRAINT `FKgtwlmy9ul6nix2fccn0pb4go2` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `ImpactParameter`
  ADD CONSTRAINT `FKepl4wucbucs45dqnmltau037h` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKn1cxejwbwgdyceuv9w6wyu6xd` FOREIGN KEY (`fiParameterType`) REFERENCES `ScaleType` (`id`);

ALTER TABLE `LikelihoodParameter`
  ADD CONSTRAINT `FK6rq3rtob9a0ig6o1uf5c4ycw4` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

SET FOREIGN_KEY_CHECKS=0;

ALTER TABLE `MaturityMeasure`
  ADD CONSTRAINT `FKl8syxaywv15qxl3hqulsmxgkm` FOREIGN KEY (`fiImplementationRateParameter`) REFERENCES `SimpleParameter` (`idSimpleParameter`);

ALTER TABLE `MaturityParameter`
  ADD CONSTRAINT `FK9po6uuh5762pgkc8de7hgd8ml` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `RiskAcceptanceParameter`
  ADD CONSTRAINT `FK8ws3qewwg34ceo7spf617genu` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `RiskProfile`
  ADD CONSTRAINT `FK7jxaq1niuwu3trrnmly6al9a2` FOREIGN KEY (`fiExpProbability`) REFERENCES `LikelihoodParameter` (`idLikelihoodParameter`),
  ADD CONSTRAINT `FKj44obly2itlu7ri94pj7vur9r` FOREIGN KEY (`fiRawProbability`) REFERENCES `LikelihoodParameter` (`idLikelihoodParameter`);

ALTER TABLE `RiskProfileExpImpacts`
  ADD CONSTRAINT `FK76ah301uvl2dellyqy3svtjcg` FOREIGN KEY (`fiRiskProfile`) REFERENCES `RiskProfile` (`idRiskProfile`),
  ADD CONSTRAINT `FKid19tomt9it720sdh5yojosu1` FOREIGN KEY (`fiExpImpact`) REFERENCES `ImpactParameter` (`idImpactParameter`);

ALTER TABLE `RiskProfileMeasures`
  ADD CONSTRAINT `FK3sqqm2mrtgf0sq370mnguwmkv` FOREIGN KEY (`fiMeasure`) REFERENCES `Measure` (`idMeasure`),
  ADD CONSTRAINT `FKstmid80ifsrem7d76k7syug9n` FOREIGN KEY (`fiRiskProfile`) REFERENCES `RiskProfile` (`idRiskProfile`);

ALTER TABLE `RiskProfileRawImpacts`
  ADD CONSTRAINT `FK6v43hux4u0st9j2qdplk2e0al` FOREIGN KEY (`fiRawImpact`) REFERENCES `ImpactParameter` (`idImpactParameter`),
  ADD CONSTRAINT `FKsvgs9teitufgb36erm1terg61` FOREIGN KEY (`fiRiskProfile`) REFERENCES `RiskProfile` (`idRiskProfile`);

ALTER TABLE `ScaleTypeTranslations`
  ADD CONSTRAINT `FK91ki836cn5aa0x9lls5qq16f4` FOREIGN KEY (`fiScaleType`) REFERENCES `ScaleType` (`id`);

ALTER TABLE `ScenarioLinkedAsset`
  ADD CONSTRAINT `FKf46a37g72aorrv6atnx10oxc6` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`),
  ADD CONSTRAINT `FKhp8w0abs6gx1467b9qjknet0t` FOREIGN KEY (`fiScenario`) REFERENCES `Scenario` (`idScenario`);

ALTER TABLE `SimpleParameter`
  ADD CONSTRAINT `FK5d7a1yyntmdfm4eni0ww15q8x` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKaj6832gix9hcbyfxu0m69yv6c` FOREIGN KEY (`fiParameterType`) REFERENCES `ParameterType` (`idParameterType`);

SET FOREIGN_KEY_CHECKS=1;

UPDATE `Analysis` SET `dtType` = 'QUALITATIVE' WHERE `dtCssf` = TRUE;

UPDATE `Analysis` SET `dtType` = 'QUANTITATIVE' WHERE `dtCssf` = FALSE;

UPDATE `Scenario` SET `dtAssetLinked`= FALSE WHERE `dtAssetLinked` IS NULL;

INSERT INTO `ScaleType` (`dtName`, `dtAcronym`) VALUES
('IMPACT', 'i'), ('FINANCIAL', 'if'), ('MORAL', 'im'),
('PHYSICAL', 'iph'),('PRIVACY', 'ip'),('LEGAL', 'il'),
('OPERATIONAL', 'io'),('REPUTATIONAL', 'ir');

UPDATE `MaturityParameter` `maturityParameter` JOIN `Parameter` `parameter` ON `maturityParameter`.`idMaturityParameter` = `parameter`.`idParameter` 
	SET `maturityParameter`.`dtDescription`=`parameter`.`dtLabel`,`maturityParameter`.`dtValue`=`parameter`.`dtValue`,`maturityParameter`.`fiAnalysis`=`parameter`.`fiAnalysis`;

INSERT INTO `SimpleParameter`(`dtDescription`, `dtValue`, `fiParameterType`, `fiAnalysis`) 
	SELECT `Parameter`.`dtLabel`, `Parameter`.`dtValue`, `Parameter`.`fiParameterType`, `Parameter`.`fiAnalysis` 
		FROM `Parameter` WHERE `Parameter`.`fiParameterType` in (4,5,6,7);
		
UPDATE `MaturityMeasure` `maturityMeasure` 
	JOIN `Parameter` `parameter` 
		on `maturityMeasure`.`fiImplementationRateParameter` = `parameter`.`idParameter` 
	JOIN `SimpleParameter` `simpleParameter` 
		on (`simpleParameter`.`fiAnalysis` = `parameter`.`fiAnalysis` and `simpleParameter`.`fiParameterType` = `parameter`.`fiParameterType` and `simpleParameter`.`dtValue` = `parameter`.`dtValue`) 
	SET `fiImplementationRateParameter`=`simpleParameter`.`idSimpleParameter`;
	
ALTER TABLE `Analysis` DROP `dtCssf`;
