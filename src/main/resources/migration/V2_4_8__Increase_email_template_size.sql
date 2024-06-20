START TRANSACTION;

ALTER TABLE `EmailTemplate`
  CHANGE `dtTemplate`  `dtTemplate` varchar(10000) NOT NULL;

COMMIT;