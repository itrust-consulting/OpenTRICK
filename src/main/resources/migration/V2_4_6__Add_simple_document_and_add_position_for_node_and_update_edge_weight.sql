
START TRANSACTION;

ALTER TABLE `AssetEdge` CHANGE `dtWeight` `dtWeight` DOUBLE NOT NULL;
ALTER TABLE `AssetNode` ADD `dtPositionX` double DEFAULT NULL AFTER `dtInheritedIntegrity`;
ALTER TABLE `AssetNode` ADD `dtPositionY` double DEFAULT NULL AFTER `dtPositionX`; 

CREATE TABLE `SimpleDocument` (
  `idSimpleDocument` bigint(20) NOT NULL,
  `dtCreated` datetime DEFAULT NULL,
  `dtData` longblob,
  `dtLength` bigint(20) DEFAULT NULL,
  `dtName` varchar(255) DEFAULT NULL,
  `dtType` varchar(255) NOT NULL,
  `fiAnalysis` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `SimpleDocument`
  ADD PRIMARY KEY (`idSimpleDocument`),
  ADD KEY `FK3ivt6ehveh0sc2hbq5wk1ypat` (`fiAnalysis`);

ALTER TABLE `SimpleDocument`
  MODIFY `idSimpleDocument` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `SimpleDocument`
  ADD CONSTRAINT `FK3ivt6ehveh0sc2hbq5wk1ypat` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);


COMMIT;

