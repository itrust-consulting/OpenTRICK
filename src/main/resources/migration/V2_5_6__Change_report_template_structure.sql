START TRANSACTION;

CREATE TABLE `TrickTemplate` (
  `idTemplate` bigint(20) NOT NULL,
  `dtCreated` datetime DEFAULT NULL,
  `dtFile` longblob DEFAULT NULL,
  `dtSize` bigint(20) DEFAULT NULL,
  `dtFilename` varchar(255) DEFAULT NULL,
  `dtLabel` varchar(255) DEFAULT NULL,
  `dtVersion` varchar(255) DEFAULT NULL,
  `dtAnalysisType` varchar(255) NOT NULL,
  `dtEditable` bit(1) NOT NULL,
  `dtType` varchar(255) NOT NULL,
  `fiLanguage` int(11) DEFAULT NULL,
  `fiCustomer` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `TrickTemplate`
  ADD PRIMARY KEY (`idTemplate`),
  ADD KEY `FKd8kcpscf316iqoajq5htd3eqt` (`fiLanguage`),
  ADD KEY `FKpqxibhie97neuqcetxm2994tx` (`fiCustomer`);

ALTER TABLE `TrickTemplate`
  MODIFY `idTemplate` bigint(20) NOT NULL AUTO_INCREMENT;


ALTER TABLE `TrickTemplate`
  ADD CONSTRAINT `FKd8kcpscf316iqoajq5htd3eqt` FOREIGN KEY (`fiLanguage`) REFERENCES `Language` (`idLanguage`),
  ADD CONSTRAINT `FKpqxibhie97neuqcetxm2994tx` FOREIGN KEY (`fiCustomer`) REFERENCES `Customer` (`idCustomer`);

INSERT INTO `TrickTemplate` (`idTemplate`, `dtCreated`, `dtFile`, `dtFilename`, `dtLabel`, `dtSize`, `dtVersion`, `dtEditable`, `dtAnalysisType`, `fiLanguage`, `fiCustomer`, `dtType`) 
SELECT `idReportTemplate`, `dtCreated`, `dtFile`, `dtFilename`, `dtLabel`, `dtSize`, `dtVersion`, `dtEditable`,`dtType` as `dtAnalysisType`, `fiLanguage`, `fiCustomer`, "REPORT" as `dtType` 
FROM `ReportTemplate`; 

DROP TABLE `ReportTemplate`;

COMMIT;