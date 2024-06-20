START TRANSACTION;

CREATE TABLE `AnalysisStandardMeasures` (
  `idAnalysisStandard` int(11) NOT NULL,
  `idMeasure` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `AnalysisStandardMeasures`
  ADD UNIQUE KEY `UK_5lmps0tnkfwet2v845kmgmr14` (`idMeasure`),
  ADD KEY `FKcgnoe99mmmx1nfs17u8vv84m7` (`idAnalysisStandard`);

ALTER TABLE `AnalysisStandardMeasures`
  ADD CONSTRAINT `FK4yq6y5gc6ngat9x0326f75d7m` FOREIGN KEY (`idMeasure`) REFERENCES `Measure` (`idMeasure`),
  ADD CONSTRAINT `FKcgnoe99mmmx1nfs17u8vv84m7` FOREIGN KEY (`idAnalysisStandard`) REFERENCES `AnalysisStandard` (`idAnalysisStandard`);

INSERT INTO `AnalysisStandardMeasures` (`idAnalysisStandard`, `idMeasure`)
SELECT `fiAnalysisStandard`,`idMeasure` 
FROM `Measure`;

COMMIT;