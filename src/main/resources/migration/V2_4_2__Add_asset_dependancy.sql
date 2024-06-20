
START TRANSACTION;

CREATE TABLE `AnalysisILRImpactTypes` (
  `fiAnalysis` int(11) NOT NULL,
  `fiScaleType` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AssetEdge` (
  `idAssetEdge` bigint(20) NOT NULL,
  `dtWeight` int(11) DEFAULT NULL,
  `fiChild` bigint(20) DEFAULT NULL,
  `fiParent` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AssetILRImpactAvailabilities` (
  `fiAssetImpact` bigint(20) NOT NULL,
  `fiILRImpact` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AssetILRImpactConfidentialities` (
  `fiAssetImpact` bigint(20) NOT NULL,
  `fiILRImpact` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AssetILRImpactIntegrities` (
  `fiAssetImpact` bigint(20) NOT NULL,
  `fiILRImpact` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `AssetImpact` (
  `idAssetImpact` bigint(20) NOT NULL,
  `fiAsset` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AssetNode` (
  `idAssetNode` bigint(20) NOT NULL,
  `dtInheritedAvailability` int(11) NOT NULL,
  `dtInheritedConfidentiality` int(11) NOT NULL,
  `dtInheritedIntegrity` int(11) NOT NULL,
  `fiImpact` bigint(20) NOT NULL,
  `fiAnalysis` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ILRImpact` (
  `idILRImpact` bigint(20) NOT NULL,
  `dtValue` int(11) DEFAULT NULL,
  `fiType` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `AnalysisILRImpactTypes`
  ADD PRIMARY KEY (`fiAnalysis`,`fiScaleType`);

ALTER TABLE `AssetEdge`
  ADD PRIMARY KEY (`idAssetEdge`),
  ADD KEY `FKsig4wfgvdongfgtpt3yrks8jl` (`fiChild`),
  ADD KEY `FKa0b3mo3ps7k7luneftqt390me` (`fiParent`);

ALTER TABLE `AssetILRImpactAvailabilities`
  ADD PRIMARY KEY (`fiAssetImpact`,`fiILRImpact`);

ALTER TABLE `AssetILRImpactConfidentialities`
  ADD PRIMARY KEY (`fiAssetImpact`,`fiILRImpact`);

ALTER TABLE `AssetILRImpactIntegrities`
  ADD PRIMARY KEY (`fiAssetImpact`,`fiILRImpact`);

ALTER TABLE `AssetImpact`
  ADD PRIMARY KEY (`idAssetImpact`),
  ADD KEY `FK2kqnhhbtit1bq4ysp2k2sdoq3` (`fiAsset`);

ALTER TABLE `AssetNode`
  ADD PRIMARY KEY (`idAssetNode`),
  ADD KEY `FK1bxme4ptrpss02q80y4wmi6m2` (`fiImpact`),
  ADD KEY `FKl5ax3k5g6bo1wriiwpenvm86d` (`fiAnalysis`);

ALTER TABLE `ILRImpact`
  ADD PRIMARY KEY (`idILRImpact`),
  ADD KEY `FKnqe19lngcad30p4p1h6xdbelt` (`fiType`);

ALTER TABLE `AssetEdge`
  MODIFY `idAssetEdge` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `AssetImpact`
  MODIFY `idAssetImpact` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `AssetNode`
  MODIFY `idAssetNode` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `ILRImpact`
  MODIFY `idILRImpact` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `AnalysisILRImpactTypes`
  ADD CONSTRAINT `FK3v6ixesebl3b7xjwaic1c863b` FOREIGN KEY (`fiScaleType`) REFERENCES `ScaleType` (`idScaleType`),
  ADD CONSTRAINT `FKofid82kxfsr5drxcl88cd9nrg` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `AssetEdge`
  ADD CONSTRAINT `FKa0b3mo3ps7k7luneftqt390me` FOREIGN KEY (`fiParent`) REFERENCES `AssetNode` (`idAssetNode`),
  ADD CONSTRAINT `FKsig4wfgvdongfgtpt3yrks8jl` FOREIGN KEY (`fiChild`) REFERENCES `AssetNode` (`idAssetNode`);

ALTER TABLE `AssetILRImpactAvailabilities`
  ADD CONSTRAINT `FK36tt7tej32eig9jdvxll25v50` FOREIGN KEY (`fiAssetImpact`) REFERENCES `AssetImpact` (`idAssetImpact`),
  ADD CONSTRAINT `FK8io25tnj9i7y6ohvk9w22rvjj` FOREIGN KEY (`fiILRImpact`) REFERENCES `ILRImpact` (`idILRImpact`);

ALTER TABLE `AssetILRImpactConfidentialities`
  ADD CONSTRAINT `FK37qajduiu8rf55gbn3m6ein6d` FOREIGN KEY (`fiAssetImpact`) REFERENCES `AssetImpact` (`idAssetImpact`),
  ADD CONSTRAINT `FKhrxyd82sldar90ycuo8qm8hoj` FOREIGN KEY (`fiILRImpact`) REFERENCES `ILRImpact` (`idILRImpact`);

ALTER TABLE `AssetILRImpactIntegrities`
  ADD CONSTRAINT `FK9n82vhn0bcur3wk1jhhm4og56` FOREIGN KEY (`fiAssetImpact`) REFERENCES `AssetImpact` (`idAssetImpact`),
  ADD CONSTRAINT `FKrewjw2bjtt4pn6sd6hwa1h0uo` FOREIGN KEY (`fiILRImpact`) REFERENCES `ILRImpact` (`idILRImpact`);

ALTER TABLE `AssetImpact`
  ADD CONSTRAINT `FK2kqnhhbtit1bq4ysp2k2sdoq3` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`);

ALTER TABLE `AssetNode`
  ADD CONSTRAINT `FK1bxme4ptrpss02q80y4wmi6m2` FOREIGN KEY (`fiImpact`) REFERENCES `AssetImpact` (`idAssetImpact`),
  ADD CONSTRAINT `FKl5ax3k5g6bo1wriiwpenvm86d` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `ILRImpact`
  ADD CONSTRAINT `FKnqe19lngcad30p4p1h6xdbelt` FOREIGN KEY (`fiType`) REFERENCES `ScaleType` (`idScaleType`);

COMMIT;
