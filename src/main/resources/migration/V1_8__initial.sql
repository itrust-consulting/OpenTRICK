
SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Structure de la table `ActionPlan`
--

CREATE TABLE IF NOT EXISTS `ActionPlan` (
  `idActionPlanCalculation` int(11) NOT NULL AUTO_INCREMENT,
  `dtROI` double NOT NULL,
  `dtCost` double NOT NULL,
  `dtDeltaALE` double NOT NULL,
  `dtOrder` varchar(5) NOT NULL,
  `dtPosition` int(11) NOT NULL,
  `dtTotalALE` double NOT NULL,
  `fiActionPlanType` int(11) NOT NULL,
  `fiMeasure` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idActionPlanCalculation`),
  KEY `FKhua5fcoi2s6g9l3sgupp0x4e9` (`fiActionPlanType`),
  KEY `FK5pfurt5y6wr50pd5n4ip0t85q` (`fiMeasure`),
  KEY `FKirfl6n4tv90n4k2ldn48qo5ob` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ActionPlanAsset`
--

CREATE TABLE IF NOT EXISTS `ActionPlanAsset` (
  `idActionPlanAssetCalculation` int(11) NOT NULL AUTO_INCREMENT,
  `dtCurrentALE` double NOT NULL,
  `fiActionPlanCalculation` int(11) NOT NULL,
  `fiAsset` int(11) NOT NULL,
  PRIMARY KEY (`idActionPlanAssetCalculation`),
  UNIQUE KEY `UK78r5850n0kitlh89ubyxwvidi` (`fiActionPlanCalculation`,`fiAsset`),
  KEY `FK39opodnlit4x3123uvpht66yp` (`fiAsset`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ActionPlanSummary`
--

CREATE TABLE IF NOT EXISTS `ActionPlanSummary` (
  `idActionPlanSummary` int(11) NOT NULL AUTO_INCREMENT,
  `dtROSI` double NOT NULL,
  `dtCurrentCostMeasures` double NOT NULL,
  `dtCurrentDeltaALE` double NOT NULL,
  `dtTotalExternalMaintenance` double NOT NULL,
  `dtTotalExternalWorkLoad` double NOT NULL,
  `dtImplementCost` double NOT NULL,
  `dtImplementedMeasureCount` int(11) NOT NULL,
  `dtTotalInternalMaintenance` double NOT NULL,
  `dtTotalInternalWorkLoad` double NOT NULL,
  `dtInvestment` double NOT NULL,
  `dtMeasureCount` int(11) NOT NULL,
  `dtNotCompliantMeasure27001Count` int(11) NOT NULL,
  `dtNotCompliantMeasure27002Count` int(11) NOT NULL,
  `dtRecurrentCost` double NOT NULL,
  `dtRecurrentInvestment` double NOT NULL,
  `dtRelativeROSI` double NOT NULL,
  `dtName` varchar(255) NOT NULL,
  `dtCurrentTotalALE` double NOT NULL,
  `dtTotalCost` double NOT NULL,
  `fiActionPlanType` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idActionPlanSummary`),
  KEY `FKcee7c9qkl3k1eukxfpc5lynp4` (`fiActionPlanType`),
  KEY `FK3ks01mxl98jsyhkax1b2w3nku` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ActionPlanSummaryStandardConformance`
--

CREATE TABLE IF NOT EXISTS `ActionPlanSummaryStandardConformance` (
  `idActionPlanSummaryStandardConformance` int(11) NOT NULL AUTO_INCREMENT,
  `dtConformance` double NOT NULL,
  `fiAnalysisStandard` int(11) NOT NULL,
  `fiActionPlanSummary` int(11) NOT NULL,
  PRIMARY KEY (`idActionPlanSummaryStandardConformance`),
  KEY `FKgeq1sneb3tlohjy10wxrc048p` (`fiAnalysisStandard`),
  KEY `FKahxdpq58c3pli50m2mma5ldem` (`fiActionPlanSummary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ActionPlanType`
--

CREATE TABLE IF NOT EXISTS `ActionPlanType` (
  `idActionPlanType` int(11) NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  PRIMARY KEY (`idActionPlanType`),
  UNIQUE KEY `UK_14udtogdb3qtcueffbt2i28eu` (`dtLabel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `Analysis`
--

CREATE TABLE IF NOT EXISTS `Analysis` (
  `idAnalysis` int(11) NOT NULL AUTO_INCREMENT,
  `dtCreationDate` datetime NOT NULL,
  `dtCssf` tinyint(1) NOT NULL,
  `dtData` tinyint(1) NOT NULL,
  `dtDefaultProfile` tinyint(1) NOT NULL,
  `dtIdentifier` varchar(23) NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtProfile` tinyint(1) NOT NULL,
  `dtProject` varchar(255) DEFAULT NULL,
  `dtUncertainty` tinyint(1) NOT NULL,
  `dtVersion` varchar(12) NOT NULL,
  `fiBasedOnAnalysis` int(11) DEFAULT NULL,
  `fiCustomer` int(11) NOT NULL,
  `fiLanguage` int(11) NOT NULL,
  `fiOwner` int(11) NOT NULL,
  PRIMARY KEY (`idAnalysis`),
  UNIQUE KEY `UKoto41qwve9opv67ht8vqvn8lt` (`dtIdentifier`,`dtVersion`),
  KEY `FKoqkukkj68vry6btvq2nshjtg9` (`fiBasedOnAnalysis`),
  KEY `FKgyww0nk44kodsklq8ptkbwhbh` (`fiCustomer`),
  KEY `FKiiefkdu6wty6tg7k1a4q9gl6n` (`fiLanguage`),
  KEY `FKta4kv80k4nclyc5dbp1aeja41` (`fiOwner`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `AnalysisStandard`
--

CREATE TABLE IF NOT EXISTS `AnalysisStandard` (
  `dtDiscriminator` varchar(31) NOT NULL,
  `idAnalysisStandard` int(11) NOT NULL AUTO_INCREMENT,
  `dtSOAEnabled` bit(1) NOT NULL,
  `fiStandard` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idAnalysisStandard`),
  UNIQUE KEY `UK5xygk11efrcj6437hle9jp693` (`fiAnalysis`,`fiStandard`),
  KEY `FKjpp2nju7biojshnwgl8w22e90` (`fiStandard`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Assessment`
--

CREATE TABLE IF NOT EXISTS `Assessment` (
  `idAssessment` int(11) NOT NULL AUTO_INCREMENT,
  `dtALE` double NOT NULL,
  `dtALEO` double NOT NULL,
  `dtALEP` double NOT NULL,
  `dtComment` longtext NOT NULL,
  `dtHiddenComment` longtext NOT NULL,
  `dtImpactFin` varchar(255) NOT NULL,
  `dtImpactLeg` varchar(255) NOT NULL,
  `dtImpactOp` varchar(255) NOT NULL,
  `dtImpactReal` double NOT NULL,
  `dtImpactRep` varchar(255) NOT NULL,
  `dtLikelihood` varchar(255) NOT NULL,
  `dtLikelihoodReal` double NOT NULL,
  `dtOwner` varchar(255) DEFAULT NULL,
  `dtSelected` tinyint(1) NOT NULL,
  `dtUncertainty` double NOT NULL,
  `fiAsset` int(11) NOT NULL,
  `fiScenario` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idAssessment`),
  UNIQUE KEY `UKlghvh412ogcn9fh3yhjo737qw` (`fiAsset`,`fiScenario`),
  KEY `FKlco6v1x3ajkrmh71i0kvkurl9` (`fiScenario`),
  KEY `FKjfm5fdv1d7xfro3je91awhlvu` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Asset`
--

CREATE TABLE IF NOT EXISTS `Asset` (
  `idAsset` int(11) NOT NULL AUTO_INCREMENT,
  `dtALE` double NOT NULL,
  `dtALEO` double NOT NULL,
  `dtALEP` double NOT NULL,
  `dtComment` longtext NOT NULL,
  `dtHiddenComment` longtext NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtSelected` tinyint(1) NOT NULL,
  `dtValue` double NOT NULL,
  `fiAssetType` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idAsset`),
  UNIQUE KEY `UKm67h192rrpmmojllhtpcewm7l` (`fiAnalysis`,`dtLabel`),
  KEY `FKpoi7suot29ym5ubbatqhhjfon` (`fiAssetType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `AssetMeasure`
--

CREATE TABLE IF NOT EXISTS `AssetMeasure` (
  `dtImplmentationRate` double NOT NULL,
  `dtToCheck` longtext NOT NULL,
  `idAssetMeasure` int(11) NOT NULL,
  `fiMeasureProperties` int(11) NOT NULL,
  PRIMARY KEY (`idAssetMeasure`),
  KEY `FKncc1ie3ikv9xl77fcfjsad26k` (`fiMeasureProperties`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `AssetType`
--

CREATE TABLE IF NOT EXISTS `AssetType` (
  `idAssetType` int(11) NOT NULL AUTO_INCREMENT,
  `dtLabel` varchar(255) NOT NULL,
  PRIMARY KEY (`idAssetType`),
  UNIQUE KEY `UK_teipihgxl0avef11pa6dyjxtx` (`dtLabel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `AssetTypeValue`
--

CREATE TABLE IF NOT EXISTS `AssetTypeValue` (
  `idAssetTypeValue` int(11) NOT NULL AUTO_INCREMENT,
  `dtValue` int(11) NOT NULL,
  `fiAssetType` int(11) NOT NULL,
  PRIMARY KEY (`idAssetTypeValue`),
  KEY `FK6n01d9mu4we5800umqkuqg6qw` (`fiAssetType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Customer`
--

CREATE TABLE IF NOT EXISTS `Customer` (
  `idCustomer` int(11) NOT NULL AUTO_INCREMENT,
  `dtZIP` varchar(20) NOT NULL,
  `dtAddress` varchar(255) NOT NULL,
  `dtCanBeUsed` tinyint(1) NOT NULL,
  `dtCity` varchar(255) NOT NULL,
  `dtContactPerson` varchar(255) NOT NULL,
  `dtCountry` varchar(255) NOT NULL,
  `dtEmail` varchar(255) NOT NULL,
  `dtOrganisation` varchar(255) NOT NULL,
  `dtTelephone` varchar(255) NOT NULL,
  PRIMARY KEY (`idCustomer`),
  UNIQUE KEY `UK_c8iuohewwuep8j61wq3xunhlo` (`dtOrganisation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ExtendedParameter`
--

CREATE TABLE IF NOT EXISTS `ExtendedParameter` (
  `dtAcronym` varchar(255) NOT NULL,
  `dtFrom` double NOT NULL,
  `dtTo` double NOT NULL,
  `dtLevel` int(11) NOT NULL,
  `idExtendedParameter` int(11) NOT NULL,
  PRIMARY KEY (`idExtendedParameter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `History`
--

CREATE TABLE IF NOT EXISTS `History` (
  `idHistory` int(11) NOT NULL AUTO_INCREMENT,
  `dtAuthor` varchar(255) NOT NULL,
  `dtComment` text NOT NULL,
  `dtDateComment` datetime NOT NULL,
  `dtVersion` varchar(12) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idHistory`),
  UNIQUE KEY `UKgi4hxyys25slhy3nsrvrdh3vf` (`fiAnalysis`,`dtVersion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ItemInformation`
--

CREATE TABLE IF NOT EXISTS `ItemInformation` (
  `idItemInformation` int(11) NOT NULL AUTO_INCREMENT,
  `dtLabel` varchar(255) NOT NULL,
  `dtType` varchar(255) NOT NULL,
  `dtValue` longtext NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idItemInformation`),
  UNIQUE KEY `UKe8d7auu9engnnqdb99r614jnv` (`fiAnalysis`,`dtLabel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Language`
--

CREATE TABLE IF NOT EXISTS `Language` (
  `idLanguage` int(11) NOT NULL AUTO_INCREMENT,
  `dtAlpha3` varchar(3) NOT NULL,
  `dtAlternativeName` varchar(255) NOT NULL,
  `dtName` varchar(255) NOT NULL,
  PRIMARY KEY (`idLanguage`),
  UNIQUE KEY `UK_im4kejjoo4reqk7iwyer6h9w9` (`dtAlpha3`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `MaturityMeasure`
--

CREATE TABLE IF NOT EXISTS `MaturityMeasure` (
  `dtSML1Cost` double NOT NULL,
  `dtSML2Cost` double NOT NULL,
  `dtSML3Cost` double NOT NULL,
  `dtSML4Cost` double NOT NULL,
  `dtSML5Cost` double NOT NULL,
  `dtReachedLevel` int(11) NOT NULL,
  `idMaturityMeasure` int(11) NOT NULL,
  `fiImplementationRateParameter` int(11) NOT NULL,
  PRIMARY KEY (`idMaturityMeasure`),
  KEY `FK58aym60jyndinl3ltxuivajah` (`fiImplementationRateParameter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `MaturityParameter`
--

CREATE TABLE IF NOT EXISTS `MaturityParameter` (
  `dtSML` int(11) NOT NULL,
  `dtSML0` double NOT NULL,
  `dtSML1` double NOT NULL,
  `dtSML2` double NOT NULL,
  `dtSML3` double NOT NULL,
  `dtSML4` double NOT NULL,
  `dtSML5` double NOT NULL,
  `dtCategory` varchar(255) NOT NULL,
  `idMaturityParameter` int(11) NOT NULL,
  PRIMARY KEY (`idMaturityParameter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `Measure`
--

CREATE TABLE IF NOT EXISTS `Measure` (
  `idMeasure` int(11) NOT NULL AUTO_INCREMENT,
  `dtComment` longtext NOT NULL,
  `dtCost` double NOT NULL,
  `dtExternalMaintenance` double NOT NULL,
  `dtExternalWorkLoad` double NOT NULL,
  `dtInternalMaintenance` double NOT NULL,
  `dtInternalWorkLoad` double NOT NULL,
  `dtInvestment` double NOT NULL,
  `dtLifetime` double NOT NULL,
  `dtRecurrentInvestment` double NOT NULL,
  `dtResponsible` varchar(255) DEFAULT NULL,
  `dtStatus` varchar(255) NOT NULL,
  `dtTicket` varchar(255) DEFAULT NULL,
  `dtToDo` longtext NOT NULL,
  `fiAnalysisStandard` int(11) NOT NULL,
  `fiMeasureDescription` int(11) NOT NULL,
  `fiPhase` int(11) NOT NULL,
  PRIMARY KEY (`idMeasure`),
  KEY `FK9ehy1xxwxnhtoldkasv1e23sg` (`fiAnalysisStandard`),
  KEY `FKkg287wtx4yu4b5q5n5qefca6w` (`fiMeasureDescription`),
  KEY `FKjq2inmekhcfx2hwwljb14acks` (`fiPhase`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `MeasureAssetTypeValue`
--

CREATE TABLE IF NOT EXISTS `MeasureAssetTypeValue` (
  `fiNormalMeasure` int(11) NOT NULL,
  `fiAssetTypeValue` int(11) NOT NULL,
  UNIQUE KEY `UKb1b69wlbbtcrqe0m1ts220tui` (`fiAssetTypeValue`),
  KEY `FK1uj65x0ptyvtyee6kn4e48081` (`fiNormalMeasure`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `MeasureAssetValue`
--

CREATE TABLE IF NOT EXISTS `MeasureAssetValue` (
  `idMeasureAssetValue` int(11) NOT NULL AUTO_INCREMENT,
  `dtValue` int(11) NOT NULL,
  `fiAsset` int(11) NOT NULL,
  `fiAssetMeasure` int(11) NOT NULL,
  PRIMARY KEY (`idMeasureAssetValue`),
  KEY `FKfp5h4ru70s6l725jlqfqh7i2n` (`fiAsset`),
  KEY `FK7t4d2weigpqv383qsuqsg2xc` (`fiAssetMeasure`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `MeasureDescription`
--

CREATE TABLE IF NOT EXISTS `MeasureDescription` (
  `idMeasureDescription` int(11) NOT NULL AUTO_INCREMENT,
  `dtComputable` bit(1) NOT NULL,
  `dtLevel` int(11) NOT NULL,
  `dtReference` varchar(255) NOT NULL,
  `fiStandard` int(11) NOT NULL,
  PRIMARY KEY (`idMeasureDescription`),
  UNIQUE KEY `UKe6mqys214nahlknh7rdg6fjt9` (`fiStandard`,`dtReference`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `MeasureDescriptionText`
--

CREATE TABLE IF NOT EXISTS `MeasureDescriptionText` (
  `idMeasureDescriptionText` int(11) NOT NULL AUTO_INCREMENT,
  `dtDescription` longtext NOT NULL,
  `dtDomain` text NOT NULL,
  `fiLanguage` int(11) NOT NULL,
  `fiMeasureDescription` int(11) NOT NULL,
  PRIMARY KEY (`idMeasureDescriptionText`),
  KEY `FK3jm24635203j33jb1qnfldid8` (`fiLanguage`),
  KEY `FKro3k8kqpao4cyhi0puleaqggw` (`fiMeasureDescription`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `MeasureProperties`
--

CREATE TABLE IF NOT EXISTS `MeasureProperties` (
  `dtStrengthMeasure` int(11) NOT NULL,
  `dtStrengthSectoral` int(11) NOT NULL,
  `dtSOAComment` varchar(255) NOT NULL,
  `dtSOAReference` varchar(255) NOT NULL,
  `dtSOARisk` varchar(255) NOT NULL,
  `idMeasureProperties` int(11) NOT NULL,
  PRIMARY KEY (`idMeasureProperties`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `NormalMeasure`
--

CREATE TABLE IF NOT EXISTS `NormalMeasure` (
  `dtImplementationRate` double NOT NULL,
  `dtToCheck` longtext NOT NULL,
  `idNormalMeasure` int(11) NOT NULL,
  `fiMeasureProperties` int(11) NOT NULL,
  PRIMARY KEY (`idNormalMeasure`),
  UNIQUE KEY `UK_6v3ev1awrld66vj0yo17dnliv` (`fiMeasureProperties`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `Parameter`
--

CREATE TABLE IF NOT EXISTS `Parameter` (
  `idParameter` int(11) NOT NULL AUTO_INCREMENT,
  `dtLabel` varchar(255) NOT NULL,
  `dtValue` double NOT NULL,
  `fiParameterType` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idParameter`),
  KEY `FKh95ugwcyoo8t4m9t5bcalcvas` (`fiParameterType`),
  KEY `FKhs5bfhmo73ds4hnhefo3poiaa` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ParameterType`
--

CREATE TABLE IF NOT EXISTS `ParameterType` (
  `idParameterType` int(11) NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  PRIMARY KEY (`idParameterType`),
  UNIQUE KEY `UK_do3x0jxkang6dvfrrac9ylayn` (`dtLabel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `Phase`
--

CREATE TABLE IF NOT EXISTS `Phase` (
  `idPhase` int(11) NOT NULL AUTO_INCREMENT,
  `dtBeginDate` date DEFAULT NULL,
  `dtEndDate` date DEFAULT NULL,
  `dtNumber` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idPhase`),
  UNIQUE KEY `UKp0mha8lbju5mqryshcs7f9r64` (`fiAnalysis`,`dtNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `ResetPassword`
--

CREATE TABLE IF NOT EXISTS `ResetPassword` (
  `idResetPassword` bigint(20) NOT NULL AUTO_INCREMENT,
  `dtKeyControl` varchar(255) DEFAULT NULL,
  `dtLimitTime` datetime DEFAULT NULL,
  `fiUser` int(11) DEFAULT NULL,
  PRIMARY KEY (`idResetPassword`),
  UNIQUE KEY `UK_e1n4g3qcq0ijklhpck5qed71y` (`dtKeyControl`),
  KEY `FKslpd57d06b2645xwtak0imhv9` (`fiUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `RiskInformation`
--

CREATE TABLE IF NOT EXISTS `RiskInformation` (
  `idRiskInformation` int(11) NOT NULL AUTO_INCREMENT,
  `dtAcronym` varchar(15) NOT NULL,
  `dtCategory` varchar(255) NOT NULL,
  `dtChapter` varchar(255) NOT NULL,
  `dtComment` longtext NOT NULL,
  `dtExposed` varchar(255) NOT NULL,
  `dtHiddenComment` longtext NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtOwner` varchar(255) DEFAULT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idRiskInformation`),
  UNIQUE KEY `UK6b0166e2f2ku3yogqyq2i8qqx` (`fiAnalysis`,`dtLabel`,`dtChapter`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `RiskProfile`
--

CREATE TABLE IF NOT EXISTS `RiskProfile` (
  `idRiskProfile` int(11) NOT NULL AUTO_INCREMENT,
  `dtActionPlan` longtext,
  `dtIdentifier` varchar(255) DEFAULT NULL,
  `dtStrategy` varchar(255) DEFAULT NULL,
  `dtTreatment` longtext,
  `fiAsset` int(11) DEFAULT NULL,
  `fiExpImpactFin` int(11) DEFAULT NULL,
  `fiExpImpactLeg` int(11) DEFAULT NULL,
  `fiExpImpactOp` int(11) DEFAULT NULL,
  `fiExpImpactRep` int(11) DEFAULT NULL,
  `fiExpProbability` int(11) DEFAULT NULL,
  `fiRawImpactFin` int(11) DEFAULT NULL,
  `fiRawImpactLeg` int(11) DEFAULT NULL,
  `fiRawImpactOp` int(11) DEFAULT NULL,
  `fiRawImpactRep` int(11) DEFAULT NULL,
  `fiRawProbability` int(11) DEFAULT NULL,
  `fiScenario` int(11) DEFAULT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idRiskProfile`),
  UNIQUE KEY `UKqi3ghk0vbpsswk8gioh6ojm72` (`fiAsset`,`fiScenario`),
  UNIQUE KEY `UKp7a5lb3ea58yvrkxe1i2onesi` (`dtIdentifier`,`fiAnalysis`),
  KEY `FKjy0385haqhrgr1r799focvifa` (`fiExpImpactFin`),
  KEY `FKatkjpeyqkxw37c7jcgi1yd6mb` (`fiExpImpactLeg`),
  KEY `FKdli964rro7w03npxvw9u18itj` (`fiExpImpactOp`),
  KEY `FKgg3cgrnw6uoggwkaw15v8xekj` (`fiExpImpactRep`),
  KEY `FKps92hja4napfk2fchw3v902mg` (`fiExpProbability`),
  KEY `FK7erdxf04tijy7y4y3fyhsulks` (`fiRawImpactFin`),
  KEY `FK5d6s84rfob5jh8hn2dqspsm97` (`fiRawImpactLeg`),
  KEY `FKfs1phfhan86olxicbf3nxc9pg` (`fiRawImpactOp`),
  KEY `FK5v3py49odwssfrs3idhbo2usp` (`fiRawImpactRep`),
  KEY `FKg3l0o2729wiou1t726idsdkx2` (`fiRawProbability`),
  KEY `FKp3y94mca67hij0e4o7gggtb7x` (`fiScenario`),
  KEY `FKqq7u4wx9ex1xxrh6rpm01jvml` (`fiAnalysis`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `RiskRegister`
--

CREATE TABLE IF NOT EXISTS `RiskRegister` (
  `idRiskRegisterItem` int(11) NOT NULL AUTO_INCREMENT,
  `dtExpEvaluationImpact` double NOT NULL,
  `dtExpEvaluationImportance` double NOT NULL,
  `dtExpEvaluationProbability` double NOT NULL,
  `dtNetEvaluationImpact` double NOT NULL,
  `dtNetEvaluationImportance` double NOT NULL,
  `dtNetEvaluationProbability` double NOT NULL,
  `dtRawEvaluationImpact` double NOT NULL,
  `dtRawEvaluationImportance` double NOT NULL,
  `dtRawEvaluationProbability` double NOT NULL,
  `fiAsset` int(11) NOT NULL,
  `fiScenario` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idRiskRegisterItem`),
  UNIQUE KEY `UKsq32dtswu1thyd1cnay4dun72` (`fiAnalysis`,`fiAsset`,`fiScenario`),
  KEY `FK53s8g4uhamavymtcvkn5i3hw8` (`fiAsset`),
  KEY `FKp3s41s35pa0stqtgbsmfcvrw8` (`fiScenario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Role`
--

CREATE TABLE IF NOT EXISTS `Role` (
  `idRole` int(11) NOT NULL AUTO_INCREMENT,
  `dtType` varchar(255) NOT NULL,
  PRIMARY KEY (`idRole`),
  UNIQUE KEY `UK_ntnhauv5mvygkfcexgnn52dqr` (`dtType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Scenario`
--

CREATE TABLE IF NOT EXISTS `Scenario` (
  `dtDescription` longtext NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtSelected` tinyint(1) NOT NULL,
  `dtType` varchar(255) NOT NULL,
  `idScenario` int(11) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  PRIMARY KEY (`idScenario`),
  UNIQUE KEY `UKtf8ijfaqllujsklhlpv76ivpq` (`fiAnalysis`,`dtLabel`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `ScenarioAssetTypeValue`
--

CREATE TABLE IF NOT EXISTS `ScenarioAssetTypeValue` (
  `fiScenario` int(11) NOT NULL,
  `fiAssetTypeValue` int(11) NOT NULL,
  UNIQUE KEY `UKekkvp845qonpluat9b9k1bhre` (`fiAssetTypeValue`),
  KEY `FKbcxk3bq40sh5bv6qebk2a5a5t` (`fiScenario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `SecurityCriteria`
--

CREATE TABLE IF NOT EXISTS `SecurityCriteria` (
  `idSecurityCriteria` int(11) NOT NULL AUTO_INCREMENT,
  `dtAccidentalSource` int(11) NOT NULL,
  `dtAvailabilityCat` int(11) NOT NULL,
  `dtConfidentialityCat` int(11) NOT NULL,
  `dtCorrectiveType` double NOT NULL,
  `dtDetectiveType` double NOT NULL,
  `dtDirect1Cat` int(11) NOT NULL,
  `dtDirect2Cat` int(11) NOT NULL,
  `dtDirect3Cat` int(11) NOT NULL,
  `dtDirect4Cat` int(11) NOT NULL,
  `dtDirect5Cat` int(11) NOT NULL,
  `dtDirect6Cat` int(11) NOT NULL,
  `dtDirect6.1Cat` int(11) NOT NULL,
  `dtDirect6.2Cat` int(11) NOT NULL,
  `dtDirect6.3Cat` int(11) NOT NULL,
  `dtDirect6.4Cat` int(11) NOT NULL,
  `dtDirect7Cat` int(11) NOT NULL,
  `dtEnvironmentalSource` int(11) NOT NULL,
  `dtExternalThreatSource` int(11) NOT NULL,
  `dtIndirect1Cat` int(11) NOT NULL,
  `dtIndirect10Cat` int(11) NOT NULL,
  `dtIndirect2Cat` int(11) NOT NULL,
  `dtIndirect3Cat` int(11) NOT NULL,
  `dtIndirect4Cat` int(11) NOT NULL,
  `dtIndirect5Cat` int(11) NOT NULL,
  `dtIndirect6Cat` int(11) NOT NULL,
  `dtIndirect7Cat` int(11) NOT NULL,
  `dtIndirect8Cat` int(11) NOT NULL,
  `dtIndirect8.1Cat` int(11) NOT NULL,
  `dtIndirect8.2Cat` int(11) NOT NULL,
  `dtIndirect8.3Cat` int(11) NOT NULL,
  `dtIndirect8.4Cat` int(11) NOT NULL,
  `dtIndirect9Cat` int(11) NOT NULL,
  `dtIntegrityCat` int(11) NOT NULL,
  `dtIntentionalSource` int(11) NOT NULL,
  `dtInternalThreatSource` int(11) NOT NULL,
  `dtLimitativeType` double NOT NULL,
  `dtPreventiveType` double NOT NULL,
  PRIMARY KEY (`idSecurityCriteria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `Standard`
--

CREATE TABLE IF NOT EXISTS `Standard` (
  `idStandard` int(11) NOT NULL AUTO_INCREMENT,
  `dtAnalysisOnly` tinyint(1) NOT NULL,
  `dtComputable` tinyint(1) NOT NULL,
  `dtDescription` varchar(255) NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtType` varchar(255) NOT NULL,
  `dtVersion` int(11) NOT NULL,
  PRIMARY KEY (`idStandard`),
  UNIQUE KEY `UKbeua79uua3ypj776opy5s2cis` (`dtLabel`,`dtVersion`,`dtType`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `TrickLog`
--

CREATE TABLE IF NOT EXISTS `TrickLog` (
  `idTrickLog` bigint(20) NOT NULL AUTO_INCREMENT,
  `dtAction` varchar(255) DEFAULT NULL,
  `dtAuthor` varchar(255) DEFAULT NULL,
  `dtCode` varchar(255) DEFAULT NULL,
  `dtCreated` datetime DEFAULT NULL,
  `dtLevel` varchar(255) DEFAULT NULL,
  `dtMessage` longtext,
  `dtType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idTrickLog`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `TrickLogParameters`
--

CREATE TABLE IF NOT EXISTS `TrickLogParameters` (
  `fiTrickLog` bigint(20) NOT NULL,
  `dtParameter` longtext,
  KEY `FKfmrl3n89tg7lk19huhnrniu7i` (`fiTrickLog`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `TrickService`
--

CREATE TABLE IF NOT EXISTS `TrickService` (
  `idTrickService` int(11) NOT NULL AUTO_INCREMENT,
  `dtInstalled` tinyint(1) NOT NULL,
  `dtVersion` varchar(255) NOT NULL,
  PRIMARY KEY (`idTrickService`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `TSSetting`
--

CREATE TABLE IF NOT EXISTS `TSSetting` (
  `idTSSetting` varchar(255) NOT NULL,
  `dtValue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idTSSetting`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `User`
--

CREATE TABLE IF NOT EXISTS `User` (
  `idUser` int(11) NOT NULL AUTO_INCREMENT,
  `dtConnexionType` int(11) NOT NULL,
  `dtEmail` varchar(255) NOT NULL,
  `dtEnabled` tinyint(1) NOT NULL,
  `dtFirstName` varchar(255) NOT NULL,
  `dtLastName` varchar(255) NOT NULL,
  `dtLocale` varchar(255) NOT NULL,
  `dtLogin` varchar(255) NOT NULL,
  `dtPassword` varchar(255) NOT NULL,
  PRIMARY KEY (`idUser`),
  UNIQUE KEY `UKal72omw4e5h62ra8cltcws38f` (`dtEmail`),
  UNIQUE KEY `UK3gf9kid5jg1h5lk4n0yy0u9g7` (`dtLogin`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `UserAnalysisRight`
--

CREATE TABLE IF NOT EXISTS `UserAnalysisRight` (
  `idUserAnalysisRight` bigint(20) NOT NULL AUTO_INCREMENT,
  `dtRight` varchar(255) NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  `fiUser` int(11) NOT NULL,
  PRIMARY KEY (`idUserAnalysisRight`),
  UNIQUE KEY `UKoj715i6ro26b90g07umwc38y5` (`fiAnalysis`,`fiUser`),
  KEY `FKbfomj9cdm9hcypbqsectv98t2` (`fiUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `UserCustomer`
--

CREATE TABLE IF NOT EXISTS `UserCustomer` (
  `fiUser` int(11) NOT NULL,
  `fiCustomer` int(11) NOT NULL,
  UNIQUE KEY `UKtailmv1xn3efj0n9ejm0njr7v` (`fiUser`,`fiCustomer`),
  KEY `FK2orgv4b6f89u2e99bqj7cfnk1` (`fiCustomer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `UserRole`
--

CREATE TABLE IF NOT EXISTS `UserRole` (
  `fiUser` int(11) NOT NULL,
  `fiRole` int(11) NOT NULL,
  KEY `FKobpkx1eri0yuf8bgpymw7n0k1` (`fiRole`),
  KEY `FK38q44e7o0x8kyko7yymag19ro` (`fiUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `UserSetting`
--

CREATE TABLE IF NOT EXISTS `UserSetting` (
  `fiUser` int(11) NOT NULL,
  `dtValue` varchar(255) DEFAULT NULL,
  `dtName` varchar(255) NOT NULL,
  PRIMARY KEY (`fiUser`,`dtName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Structure de la table `UserSQLite`
--

CREATE TABLE IF NOT EXISTS `UserSQLite` (
  `idUserSQLite` int(11) NOT NULL AUTO_INCREMENT,
  `dtDeleteTime` datetime NOT NULL,
  `dtExportTime` datetime NOT NULL,
  `dtFilename` varchar(255) NOT NULL,
  `dtIdentifier` varchar(255) NOT NULL,
  `dtLabel` varchar(255) NOT NULL,
  `dtSize` bigint(20) NOT NULL,
  `dtSQLite` mediumblob NOT NULL,
  `dtVersion` varchar(255) NOT NULL,
  `fiUser` int(11) NOT NULL,
  PRIMARY KEY (`idUserSQLite`),
  UNIQUE KEY `UK_59k6yftpvsos2r7d8uncsdtfr` (`dtFilename`),
  KEY `FK7dam6qhmrfvcbegkh2yj5vnui` (`fiUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Structure de la table `WordReport`
--

CREATE TABLE IF NOT EXISTS `WordReport` (
  `idWordReport` bigint(20) NOT NULL AUTO_INCREMENT,
  `dtCreated` datetime DEFAULT NULL,
  `dtFile` mediumblob,
  `dtFilename` varchar(255) DEFAULT NULL,
  `dtIdentifier` varchar(255) DEFAULT NULL,
  `dtLabel` varchar(255) DEFAULT NULL,
  `dtSize` bigint(20) DEFAULT NULL,
  `dtType` varchar(255) DEFAULT 'STA',
  `dtVersion` varchar(255) DEFAULT NULL,
  `fiUser` int(11) DEFAULT NULL,
  PRIMARY KEY (`idWordReport`),
  UNIQUE KEY `UK_fw3abfm3vnd3d0k0ebjo7x897` (`dtFilename`),
  KEY `FK4ab0rvaw6yuhxhes8fktx4gvi` (`fiUser`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Contraintes pour les tables exportées
--

--
-- Contraintes pour la table `ActionPlan`
--
ALTER TABLE `ActionPlan`
  ADD CONSTRAINT `FK5pfurt5y6wr50pd5n4ip0t85q` FOREIGN KEY (`fiMeasure`) REFERENCES `Measure` (`idMeasure`),
  ADD CONSTRAINT `FKhua5fcoi2s6g9l3sgupp0x4e9` FOREIGN KEY (`fiActionPlanType`) REFERENCES `ActionPlanType` (`idActionPlanType`),
  ADD CONSTRAINT `FKirfl6n4tv90n4k2ldn48qo5ob` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `ActionPlanAsset`
--
ALTER TABLE `ActionPlanAsset`
  ADD CONSTRAINT `FK39opodnlit4x3123uvpht66yp` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`),
  ADD CONSTRAINT `FKsttkcd94xsf748lt15nbnbtcx` FOREIGN KEY (`fiActionPlanCalculation`) REFERENCES `ActionPlan` (`idActionPlanCalculation`);

--
-- Contraintes pour la table `ActionPlanSummary`
--
ALTER TABLE `ActionPlanSummary`
  ADD CONSTRAINT `FK3ks01mxl98jsyhkax1b2w3nku` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKcee7c9qkl3k1eukxfpc5lynp4` FOREIGN KEY (`fiActionPlanType`) REFERENCES `ActionPlanType` (`idActionPlanType`);

--
-- Contraintes pour la table `ActionPlanSummaryStandardConformance`
--
ALTER TABLE `ActionPlanSummaryStandardConformance`
  ADD CONSTRAINT `FKahxdpq58c3pli50m2mma5ldem` FOREIGN KEY (`fiActionPlanSummary`) REFERENCES `ActionPlanSummary` (`idActionPlanSummary`),
  ADD CONSTRAINT `FKgeq1sneb3tlohjy10wxrc048p` FOREIGN KEY (`fiAnalysisStandard`) REFERENCES `AnalysisStandard` (`idAnalysisStandard`);

--
-- Contraintes pour la table `Analysis`
--
ALTER TABLE `Analysis`
  ADD CONSTRAINT `FKgyww0nk44kodsklq8ptkbwhbh` FOREIGN KEY (`fiCustomer`) REFERENCES `Customer` (`idCustomer`),
  ADD CONSTRAINT `FKiiefkdu6wty6tg7k1a4q9gl6n` FOREIGN KEY (`fiLanguage`) REFERENCES `Language` (`idLanguage`),
  ADD CONSTRAINT `FKoqkukkj68vry6btvq2nshjtg9` FOREIGN KEY (`fiBasedOnAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKta4kv80k4nclyc5dbp1aeja41` FOREIGN KEY (`fiOwner`) REFERENCES `User` (`idUser`);

--
-- Contraintes pour la table `AnalysisStandard`
--
ALTER TABLE `AnalysisStandard`
  ADD CONSTRAINT `FKf61t320o40hnwltuo0qcsgmvk` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKjpp2nju7biojshnwgl8w22e90` FOREIGN KEY (`fiStandard`) REFERENCES `Standard` (`idStandard`);

--
-- Contraintes pour la table `Assessment`
--
ALTER TABLE `Assessment`
  ADD CONSTRAINT `FKjfm5fdv1d7xfro3je91awhlvu` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKlco6v1x3ajkrmh71i0kvkurl9` FOREIGN KEY (`fiScenario`) REFERENCES `Scenario` (`idScenario`),
  ADD CONSTRAINT `FKq4u0ccgyg76wtl2mp3d1kf35d` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`);

--
-- Contraintes pour la table `Asset`
--
ALTER TABLE `Asset`
  ADD CONSTRAINT `FKpoi7suot29ym5ubbatqhhjfon` FOREIGN KEY (`fiAssetType`) REFERENCES `AssetType` (`idAssetType`),
  ADD CONSTRAINT `FKrcswljbs0cb54b7f9x4xn927i` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `AssetMeasure`
--
ALTER TABLE `AssetMeasure`
  ADD CONSTRAINT `FKncc1ie3ikv9xl77fcfjsad26k` FOREIGN KEY (`fiMeasureProperties`) REFERENCES `MeasureProperties` (`idMeasureProperties`),
  ADD CONSTRAINT `FKnnl8gqmuqe8vays8ph9dq0sbb` FOREIGN KEY (`idAssetMeasure`) REFERENCES `Measure` (`idMeasure`);

--
-- Contraintes pour la table `AssetTypeValue`
--
ALTER TABLE `AssetTypeValue`
  ADD CONSTRAINT `FK6n01d9mu4we5800umqkuqg6qw` FOREIGN KEY (`fiAssetType`) REFERENCES `AssetType` (`idAssetType`);

--
-- Contraintes pour la table `ExtendedParameter`
--
ALTER TABLE `ExtendedParameter`
  ADD CONSTRAINT `FKsmrtu6ohd8dmdhn111os6tkxt` FOREIGN KEY (`idExtendedParameter`) REFERENCES `Parameter` (`idParameter`);

--
-- Contraintes pour la table `History`
--
ALTER TABLE `History`
  ADD CONSTRAINT `FKghtdalqjdgc72k26wy1ssiogg` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `ItemInformation`
--
ALTER TABLE `ItemInformation`
  ADD CONSTRAINT `FKbfsa0ykfwi8jeslrs5h7vhx3m` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `MaturityMeasure`
--
ALTER TABLE `MaturityMeasure`
  ADD CONSTRAINT `FK58aym60jyndinl3ltxuivajah` FOREIGN KEY (`fiImplementationRateParameter`) REFERENCES `Parameter` (`idParameter`),
  ADD CONSTRAINT `FKritxqxgvpxsmrdnl6j5423i78` FOREIGN KEY (`idMaturityMeasure`) REFERENCES `Measure` (`idMeasure`);

--
-- Contraintes pour la table `MaturityParameter`
--
ALTER TABLE `MaturityParameter`
  ADD CONSTRAINT `FK7dah74f0jxvoxooeb1rl26j45` FOREIGN KEY (`idMaturityParameter`) REFERENCES `Parameter` (`idParameter`);

--
-- Contraintes pour la table `Measure`
--
ALTER TABLE `Measure`
  ADD CONSTRAINT `FK9ehy1xxwxnhtoldkasv1e23sg` FOREIGN KEY (`fiAnalysisStandard`) REFERENCES `AnalysisStandard` (`idAnalysisStandard`),
  ADD CONSTRAINT `FKjq2inmekhcfx2hwwljb14acks` FOREIGN KEY (`fiPhase`) REFERENCES `Phase` (`idPhase`),
  ADD CONSTRAINT `FKkg287wtx4yu4b5q5n5qefca6w` FOREIGN KEY (`fiMeasureDescription`) REFERENCES `MeasureDescription` (`idMeasureDescription`);

--
-- Contraintes pour la table `MeasureAssetTypeValue`
--
ALTER TABLE `MeasureAssetTypeValue`
  ADD CONSTRAINT `FK1uj65x0ptyvtyee6kn4e48081` FOREIGN KEY (`fiNormalMeasure`) REFERENCES `NormalMeasure` (`idNormalMeasure`),
  ADD CONSTRAINT `FK2rbdkeig466buvrggvcligada` FOREIGN KEY (`fiAssetTypeValue`) REFERENCES `AssetTypeValue` (`idAssetTypeValue`);

--
-- Contraintes pour la table `MeasureAssetValue`
--
ALTER TABLE `MeasureAssetValue`
  ADD CONSTRAINT `FK7t4d2weigpqv383qsuqsg2xc` FOREIGN KEY (`fiAssetMeasure`) REFERENCES `AssetMeasure` (`idAssetMeasure`),
  ADD CONSTRAINT `FKfp5h4ru70s6l725jlqfqh7i2n` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`);

--
-- Contraintes pour la table `MeasureDescription`
--
ALTER TABLE `MeasureDescription`
  ADD CONSTRAINT `FK18gfaoaj91gfcqg6iaitop7ek` FOREIGN KEY (`fiStandard`) REFERENCES `Standard` (`idStandard`);

--
-- Contraintes pour la table `MeasureDescriptionText`
--
ALTER TABLE `MeasureDescriptionText`
  ADD CONSTRAINT `FK3jm24635203j33jb1qnfldid8` FOREIGN KEY (`fiLanguage`) REFERENCES `Language` (`idLanguage`),
  ADD CONSTRAINT `FKro3k8kqpao4cyhi0puleaqggw` FOREIGN KEY (`fiMeasureDescription`) REFERENCES `MeasureDescription` (`idMeasureDescription`);

--
-- Contraintes pour la table `MeasureProperties`
--
ALTER TABLE `MeasureProperties`
  ADD CONSTRAINT `FK5ykwtxhv6488pgof3tsf6btk2` FOREIGN KEY (`idMeasureProperties`) REFERENCES `SecurityCriteria` (`idSecurityCriteria`);

--
-- Contraintes pour la table `NormalMeasure`
--
ALTER TABLE `NormalMeasure`
  ADD CONSTRAINT `FKjtyrctvui6rr4h1p6awcqj9n8` FOREIGN KEY (`fiMeasureProperties`) REFERENCES `MeasureProperties` (`idMeasureProperties`),
  ADD CONSTRAINT `FKltyx9bm46pyr45y9gn99lnwy8` FOREIGN KEY (`idNormalMeasure`) REFERENCES `Measure` (`idMeasure`);

--
-- Contraintes pour la table `Parameter`
--
ALTER TABLE `Parameter`
  ADD CONSTRAINT `FKh95ugwcyoo8t4m9t5bcalcvas` FOREIGN KEY (`fiParameterType`) REFERENCES `ParameterType` (`idParameterType`),
  ADD CONSTRAINT `FKhs5bfhmo73ds4hnhefo3poiaa` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `Phase`
--
ALTER TABLE `Phase`
  ADD CONSTRAINT `FK1y0afrbxucwgjivf96tu4ycrv` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `ResetPassword`
--
ALTER TABLE `ResetPassword`
  ADD CONSTRAINT `FKslpd57d06b2645xwtak0imhv9` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);

--
-- Contraintes pour la table `RiskInformation`
--
ALTER TABLE `RiskInformation`
  ADD CONSTRAINT `FKsluycit6wdlkmh23hpv4k3w3` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `RiskProfile`
--
ALTER TABLE `RiskProfile`
  ADD CONSTRAINT `FK5d6s84rfob5jh8hn2dqspsm97` FOREIGN KEY (`fiRawImpactLeg`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FK5v3py49odwssfrs3idhbo2usp` FOREIGN KEY (`fiRawImpactRep`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FK7erdxf04tijy7y4y3fyhsulks` FOREIGN KEY (`fiRawImpactFin`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKatkjpeyqkxw37c7jcgi1yd6mb` FOREIGN KEY (`fiExpImpactLeg`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKdli964rro7w03npxvw9u18itj` FOREIGN KEY (`fiExpImpactOp`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKfs1phfhan86olxicbf3nxc9pg` FOREIGN KEY (`fiRawImpactOp`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKg3l0o2729wiou1t726idsdkx2` FOREIGN KEY (`fiRawProbability`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKgg3cgrnw6uoggwkaw15v8xekj` FOREIGN KEY (`fiExpImpactRep`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKi25o5f1lat8eqna9tgwe0c1t4` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`),
  ADD CONSTRAINT `FKjy0385haqhrgr1r799focvifa` FOREIGN KEY (`fiExpImpactFin`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKp3y94mca67hij0e4o7gggtb7x` FOREIGN KEY (`fiScenario`) REFERENCES `Scenario` (`idScenario`),
  ADD CONSTRAINT `FKps92hja4napfk2fchw3v902mg` FOREIGN KEY (`fiExpProbability`) REFERENCES `ExtendedParameter` (`idExtendedParameter`),
  ADD CONSTRAINT `FKqq7u4wx9ex1xxrh6rpm01jvml` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `RiskRegister`
--
ALTER TABLE `RiskRegister`
  ADD CONSTRAINT `FK53s8g4uhamavymtcvkn5i3hw8` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`),
  ADD CONSTRAINT `FKm0kt9erjqju7ywfj53gl822se` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`),
  ADD CONSTRAINT `FKp3s41s35pa0stqtgbsmfcvrw8` FOREIGN KEY (`fiScenario`) REFERENCES `Scenario` (`idScenario`);

--
-- Contraintes pour la table `Scenario`
--
ALTER TABLE `Scenario`
  ADD CONSTRAINT `FK8j761hdf6euf620ppvpib0p6n` FOREIGN KEY (`idScenario`) REFERENCES `SecurityCriteria` (`idSecurityCriteria`),
  ADD CONSTRAINT `FKd4jq3ve87l2iay0delxrp5fp6` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `ScenarioAssetTypeValue`
--
ALTER TABLE `ScenarioAssetTypeValue`
  ADD CONSTRAINT `FKbcxk3bq40sh5bv6qebk2a5a5t` FOREIGN KEY (`fiScenario`) REFERENCES `Scenario` (`idScenario`),
  ADD CONSTRAINT `FKemq58j3ot9446sixrpexek8t` FOREIGN KEY (`fiAssetTypeValue`) REFERENCES `AssetTypeValue` (`idAssetTypeValue`);

--
-- Contraintes pour la table `TrickLogParameters`
--
ALTER TABLE `TrickLogParameters`
  ADD CONSTRAINT `FKfmrl3n89tg7lk19huhnrniu7i` FOREIGN KEY (`fiTrickLog`) REFERENCES `TrickLog` (`idTrickLog`);

--
-- Contraintes pour la table `UserAnalysisRight`
--
ALTER TABLE `UserAnalysisRight`
  ADD CONSTRAINT `FKbfomj9cdm9hcypbqsectv98t2` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`),
  ADD CONSTRAINT `FKl5f3spwjc74x5l3bdey0knaud` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

--
-- Contraintes pour la table `UserCustomer`
--
ALTER TABLE `UserCustomer`
  ADD CONSTRAINT `FK2orgv4b6f89u2e99bqj7cfnk1` FOREIGN KEY (`fiCustomer`) REFERENCES `Customer` (`idCustomer`),
  ADD CONSTRAINT `FKhxkwpew03b94w9qrndtka5fh9` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);

--
-- Contraintes pour la table `UserRole`
--
ALTER TABLE `UserRole`
  ADD CONSTRAINT `FK38q44e7o0x8kyko7yymag19ro` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`),
  ADD CONSTRAINT `FKobpkx1eri0yuf8bgpymw7n0k1` FOREIGN KEY (`fiRole`) REFERENCES `Role` (`idRole`);

--
-- Contraintes pour la table `UserSetting`
--
ALTER TABLE `UserSetting`
  ADD CONSTRAINT `FKe9vekivuwimk0s3oi6q3qbow8` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);

--
-- Contraintes pour la table `UserSQLite`
--
ALTER TABLE `UserSQLite`
  ADD CONSTRAINT `FK7dam6qhmrfvcbegkh2yj5vnui` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);

--
-- Contraintes pour la table `WordReport`
--
ALTER TABLE `WordReport`
  ADD CONSTRAINT `FK4ab0rvaw6yuhxhes8fktx4gvi` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);
