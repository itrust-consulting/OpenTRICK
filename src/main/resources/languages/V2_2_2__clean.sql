SET FOREIGN_KEY_CHECKS = 0;

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

ALTER TABLE `RiskProfile` 
	DROP COLUMN `fiExpImpactFin`, 
	DROP COLUMN `fiExpImpactLeg`, 
	DROP COLUMN `fiExpImpactOp`, 
	DROP COLUMN `fiExpImpactRep`, 
	DROP COLUMN `fiRawImpactFin`, 
	DROP COLUMN `fiRawImpactLeg`, 
	DROP COLUMN `fiRawImpactOp`, 
	DROP COLUMN `fiRawImpactRep`;

DROP TABLE `ExtendedParameter`;

DROP TABLE `Parameter`;

SET FOREIGN_KEY_CHECKS = 1;