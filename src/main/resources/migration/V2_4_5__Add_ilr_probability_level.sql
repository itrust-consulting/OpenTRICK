
START TRANSACTION;

ALTER TABLE `LikelihoodParameter` ADD `dtILRLevel` INT(11) NOT NULL DEFAULT '-1' AFTER `dtTo`; 

COMMIT;









