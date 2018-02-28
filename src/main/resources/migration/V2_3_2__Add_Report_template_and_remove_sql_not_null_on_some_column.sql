START TRANSACTION;

ALTER TABLE `UserSQLite` CHANGE `dtSize` `dtSize` BIGINT(20) NULL;
ALTER TABLE `UserSQLite` CHANGE `dtLabel` `dtLabel` VARCHAR(255)  NULL;
ALTER TABLE `UserSQLite` CHANGE `dtVersion` `dtVersion` VARCHAR(255) NULL;
ALTER TABLE `UserSQLite` CHANGE `idUserSQLite` `idUserSQLite` BIGINT(20) NOT NULL AUTO_INCREMENT;

CREATE TABLE `ReportTemplate` (
  `idReportTemplate` bigint(20) NOT NULL,
  `dtCreated` datetime DEFAULT NULL,
  `dtFile` longblob,
  `dtFilename` varchar(255) DEFAULT NULL,
  `dtLabel` varchar(255) DEFAULT NULL,
  `dtSize` bigint(20) DEFAULT NULL,
  `dtVersion` varchar(255) DEFAULT NULL,
  `dtEditable` bit(1) NOT NULL,
  `dtType` varchar(255) NOT NULL,
  `fiLanguage` int(11) NOT NULL,
  `fiCustomer` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `ReportTemplate`
  ADD PRIMARY KEY (`idReportTemplate`),
  ADD KEY `FK488jbpqbl7v7p28mg9ba97jp3` (`fiLanguage`),
  ADD KEY `FKj2sgax8ddwgfk4k3krcc6tttk` (`fiCustomer`);
  
ALTER TABLE `ReportTemplate`
  MODIFY `idReportTemplate` bigint(20) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `ReportTemplate`
  ADD CONSTRAINT `FK488jbpqbl7v7p28mg9ba97jp3` FOREIGN KEY (`fiLanguage`) REFERENCES `Language` (`idLanguage`),
  ADD CONSTRAINT `FKj2sgax8ddwgfk4k3krcc6tttk` FOREIGN KEY (`fiCustomer`) REFERENCES `Customer` (`idCustomer`);

COMMIT;
