START TRANSACTION;

ALTER TABLE `AssetMeasure`
  ADD UNIQUE KEY `UK_5gaab2m8he3ywg5q1p9fia2oi` (`fiMeasureProperties`);
  
 ALTER TABLE `AssetMeasure`
  CHANGE `dtImplmentationRate` `dtImplementationRate` VARCHAR(255) NOT NULL;
  
COMMIT;