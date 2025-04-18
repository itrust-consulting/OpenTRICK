ALTER TABLE `LikelihoodParameter` CHANGE `dtDescription` `dtDescription` VARCHAR( 1024 );
ALTER TABLE `ImpactParameter` CHANGE `dtDescription` `dtDescription` VARCHAR( 1024 );
ALTER TABLE `ScaleType` CHANGE `id` `idScaleType` INT(11) NOT NULL AUTO_INCREMENT;