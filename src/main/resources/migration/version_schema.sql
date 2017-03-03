

-- --------------------------------------------------------

--
-- Structure de la table `schemaversion`
--

CREATE TABLE IF NOT EXISTS `SchemaVersion` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `SchemaVersion_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `SchemaVersion` (`installed_rank`,`version`, `description`, `type`, `script`, `checksum`, `installed_by`,`execution_time`, `success`) 
    VALUES ('1','1.8', 'Version 1.8', 'BASELINE', '<< Flyway Baseline >>', NULL, 'root', 0, TRUE);
