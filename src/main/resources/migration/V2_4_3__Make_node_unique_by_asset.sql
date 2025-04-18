
START TRANSACTION;

ALTER TABLE `AssetNode` DROP FOREIGN KEY `FK1bxme4ptrpss02q80y4wmi6m2`;

ALTER TABLE `AssetNode` DROP INDEX `FK1bxme4ptrpss02q80y4wmi6m2`;

ALTER TABLE `AssetImpact` DROP FOREIGN KEY `FK2kqnhhbtit1bq4ysp2k2sdoq3`;

ALTER TABLE `AssetImpact` DROP INDEX `FK2kqnhhbtit1bq4ysp2k2sdoq3`;

ALTER TABLE `AssetImpact` 
   ADD UNIQUE KEY `UK_f2gsfkn17abolb3ewbj26l5cl` (`fiAsset`),
   ADD CONSTRAINT `FK2kqnhhbtit1bq4ysp2k2sdoq3` FOREIGN KEY (`fiAsset`) REFERENCES `Asset` (`idAsset`);

ALTER TABLE `AssetNode`
  ADD UNIQUE KEY `UK_b1rjirwwvm0c1w0gipoka16iq` (`fiImpact`),
  ADD CONSTRAINT `FK1bxme4ptrpss02q80y4wmi6m2` FOREIGN KEY (`fiImpact`) REFERENCES `AssetImpact` (`idAssetImpact`);

COMMIT;


