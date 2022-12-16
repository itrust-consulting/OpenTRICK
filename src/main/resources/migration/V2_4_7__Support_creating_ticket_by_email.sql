START TRANSACTION;

CREATE TABLE `EmailTemplate` (
  `idEmailTemplate` bigint NOT NULL,
  `dtEmail` varchar(255) NOT NULL,
  `dtHtml` bit(1) NOT NULL,
  `dtInternalTime` bigint NOT NULL,
  `dtTemplate` varchar(255) NOT NULL,
  `dtTitle` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


ALTER TABLE `EmailTemplate`
  ADD PRIMARY KEY (`idEmailTemplate`);

ALTER TABLE `EmailTemplate`
  MODIFY `idEmailTemplate` bigint NOT NULL AUTO_INCREMENT;

ALTER TABLE `TicketingSystem` 
  ADD `fiEmailTemplate` bigint DEFAULT NULL AFTER `fiCustomer`,
  ADD KEY `FKjf6vk0fg9gyjrohynkioueayg` (`fiEmailTemplate`),
  ADD CONSTRAINT `FKjf6vk0fg9gyjrohynkioueayg` FOREIGN KEY (`fiEmailTemplate`) REFERENCES `EmailTemplate` (`idEmailTemplate`);

COMMIT;