START TRANSACTION;

CREATE TABLE `IlrSoaScaleParameter` (
  `id` int NOT NULL,
  `dtDescription` varchar(255) NOT NULL,
  `dtValue` double NOT NULL,
  `dtColor` varchar(255) NOT NULL,
  `fiAnalysis` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

ALTER TABLE `IlrSoaScaleParameter`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKst49chk5g9xafqriybv6m885p` (`fiAnalysis`);

ALTER TABLE `IlrSoaScaleParameter`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

ALTER TABLE `IlrSoaScaleParameter`
  ADD CONSTRAINT `FKst49chk5g9xafqriybv6m885p` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

ALTER TABLE `Measure` ADD `dtImportance` INT NOT NULL DEFAULT '2' AFTER `dtToDo`; 
  
COMMIT;