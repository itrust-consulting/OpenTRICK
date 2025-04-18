START TRANSACTION;

ALTER TABLE `User` ADD `dtEmailValidated` BIT(1) NOT NULL DEFAULT b'1' AFTER `dtEmail`;
ALTER TABLE `User` CHANGE `dtEmailValidated` `dtEmailValidated` BIT(1) NOT NULL;

CREATE TABLE `AnalysisShareInvitation` (
  `id` bigint(20) NOT NULL,
  `dtEmail` varchar(255) COLLATE utf8_bin NOT NULL,
  `dtRight` varchar(255) COLLATE utf8_bin NOT NULL,
  `dtToken` varchar(255) COLLATE utf8_bin NOT NULL,
  `fiAnalysis` int(11) NOT NULL,
  `fiHost` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `EmailValidatingRequest` (
  `idEmailValidatingRequest` bigint(20) NOT NULL,
  `dtEmail` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `dtToken` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `fiUser` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `AnalysisShareInvitation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKaf1uh16hmwf21i0bxrctbwpke` (`fiAnalysis`,`dtEmail`),
  ADD UNIQUE KEY `UK_ekm9s7yu3d81xvfhuyi71r8x3` (`dtToken`),
  ADD KEY `FK5e1abq0xckesqn0md1rrujtds` (`fiHost`);
  
 ALTER TABLE `EmailValidatingRequest`
  ADD PRIMARY KEY (`idEmailValidatingRequest`),
  ADD UNIQUE KEY `UK_jinpog88wwk6ueng68q9abqxs` (`dtEmail`),
  ADD UNIQUE KEY `UK_f5o15imh5ea1kgch2sa3tmffb` (`dtToken`),
  ADD UNIQUE KEY `UK_magy9hl1eoownfqurkp401dwt` (`fiUser`);
  
ALTER TABLE `AnalysisShareInvitation`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `EmailValidatingRequest`
  MODIFY `idEmailValidatingRequest` bigint(20) NOT NULL AUTO_INCREMENT;
  
ALTER TABLE `AnalysisShareInvitation`
  ADD CONSTRAINT `FK5e1abq0xckesqn0md1rrujtds` FOREIGN KEY (`fiHost`) REFERENCES `User` (`idUser`),
  ADD CONSTRAINT `FKc1cfb08gi9fvvlfjhvlivpxgj` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);
  
ALTER TABLE `EmailValidatingRequest`
  ADD CONSTRAINT `FK74ptyvvuhfv3jj2318jq3j1nu` FOREIGN KEY (`fiUser`) REFERENCES `User` (`idUser`);
 
COMMIT;
  
 