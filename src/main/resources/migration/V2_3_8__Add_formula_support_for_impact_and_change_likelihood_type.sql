
START TRANSACTION;
-- --------------------------------------------------------

--
-- Table structure for table `FormulaValue`
--

CREATE TABLE `FormulaValue` (
  `idFormulaValue` int(11) NOT NULL,
  `dtLevel` int(11) NOT NULL,
  `dtValue` double NOT NULL,
  `dtFormula` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `AnalysisExcludeAcronyms` (
  `fiAnalysis` int(11) NOT NULL,
  `dtAcronym` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `FormulaValue`
--
ALTER TABLE `FormulaValue`
  ADD PRIMARY KEY (`idFormulaValue`);
  
--
-- Indexes for table `AnalysisExcludeAcronyms`
--
ALTER TABLE `AnalysisExcludeAcronyms`
  ADD UNIQUE KEY `UKmc3w2jwgar70m31w47t8eunar` (`dtAcronym`,`fiAnalysis`),
  ADD KEY `FK988xp6v8i2vbfxtexarrqkgix` (`fiAnalysis`);


--
-- AUTO_INCREMENT for table `FormulaValue`
--
ALTER TABLE `FormulaValue`
  MODIFY `idFormulaValue` int(11) NOT NULL AUTO_INCREMENT;

--
-- Indexes for table `Assessment`
--
ALTER TABLE `Assessment` 
  ADD `dtLikelihoodType` VARCHAR(255) NULL AFTER `dtImpactReal`,
  ADD `fiLikelihood` INT(11) NULL AFTER `fiAsset`,
  ADD UNIQUE KEY `UKrd3d457xxc6s0mc99ionvtfo4` (`dtLikelihoodType`,`fiLikelihood`);
  
--
-- Constraints for table `AnalysisExcludeAcronyms`
--
ALTER TABLE `AnalysisExcludeAcronyms`
  ADD CONSTRAINT `FK988xp6v8i2vbfxtexarrqkgix` FOREIGN KEY (`fiAnalysis`) REFERENCES `Analysis` (`idAnalysis`);

COMMIT;
