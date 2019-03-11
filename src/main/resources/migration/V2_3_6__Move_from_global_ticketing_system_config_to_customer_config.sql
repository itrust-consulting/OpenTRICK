START TRANSACTION;

CREATE TABLE `TicketingSystem` (
  `idTicketingSystem` bigint(20) NOT NULL,
  `dtEnabled` bit(1) DEFAULT NULL,
  `dtName` varchar(255) DEFAULT NULL,
  `dtType` varchar(255) DEFAULT NULL,
  `dtURL` varchar(255) DEFAULT NULL,
  `fiCustomer` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `UserCredential` (
  `idUserCredential` bigint(20) NOT NULL,
  `dtName` varchar(255) DEFAULT NULL,
  `dtType` varchar(255) DEFAULT NULL,
  `dtValue` varchar(2047) DEFAULT NULL,
  `fiTicketingSystem` bigint(20) DEFAULT NULL,
  `fiUser` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `ScenarioAssetTypeValue` 
  DROP FOREIGN KEY `FKemq58j3ot9446sixrpexek8t`;

ALTER TABLE `ScenarioAssetTypeValue` 
  DROP INDEX `UKekkvp845qonpluat9b9k1bhre`;
  
ALTER TABLE `ScenarioAssetTypeValue`
  ADD CONSTRAINT `FKemq58j3ot9446sixrpexek8t` FOREIGN KEY (`fiAssetTypeValue`) REFERENCES `AssetTypeValue` (`idAssetTypeValue`);

ALTER TABLE `TicketingSystem`
  ADD PRIMARY KEY (`idTicketingSystem`),
  ADD KEY `FK4syipgccnp7n0y31fc7iyg6qk` (`fiCustomer`);

ALTER TABLE `UserCredential`
  ADD PRIMARY KEY (`idUserCredential`),
  ADD UNIQUE KEY `UKralcouj745ijboehtran6tdrj` (`fiUser`,`fiTicketingSystem`),
  ADD KEY `FK2wl9empf5yfayo7knjgcgvt1w` (`fiTicketingSystem`);

ALTER TABLE `TicketingSystem`
  MODIFY `idTicketingSystem` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `UserCredential`
  MODIFY `idUserCredential` bigint(20) NOT NULL AUTO_INCREMENT;

ALTER TABLE `TicketingSystem`
  ADD CONSTRAINT `FK4syipgccnp7n0y31fc7iyg6qk` FOREIGN KEY (`fiCustomer`) REFERENCES `Customer` (`idCustomer`);

ALTER TABLE `UserCredential`
  ADD CONSTRAINT `FK2wl9empf5yfayo7knjgcgvt1w` FOREIGN KEY (`fiTicketingSystem`) REFERENCES `TicketingSystem` (`idTicketingSystem`),
  ADD CONSTRAINT `FKdo2ixc8vmh3y172kdqd8och44` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);

UPDATE
    UserSetting user1
JOIN UserSetting user2 ON
    user1.fiUser = user2.fiUser
SET
    user1.dtValue = CONCAT( '{ENCRYPT:', CONCAT( LENGTH( user2.dtValue ),'}' ),CONCAT( user2.dtValue, user1.dtValue ) )
WHERE
    user1.dtName = 'user-2-factor-secret' AND user2.dtName = 'user-iv-2-factor-secret';

DELETE FROM `UserSetting` WHERE `UserSetting`.`dtName` = 'user-iv-2-factor-secret';

DELETE FROM `UserSetting` WHERE `UserSetting`.`dtName` = 'user-titcketing-credential-username';

DELETE FROM `UserSetting` WHERE `UserSetting`.`dtName` = 'user-titcketing-credential-password';

DELETE FROM `UserSetting` WHERE `UserSetting`.`dtName` = 'user-titcketing-credential-iv';

DELETE FROM `TSSetting` WHERE `TSSetting`.`idTSSetting` = 'TICKETING_SYSTEM_NAME';

DELETE FROM `TSSetting` WHERE `TSSetting`.`idTSSetting` = 'TICKETING_SYSTEM_URL';

COMMIT;