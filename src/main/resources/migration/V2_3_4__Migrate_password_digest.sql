START TRANSACTION;

UPDATE `User` SET `dtPassword`=concat("{SHA-256}",dtPassword);

COMMIT;
