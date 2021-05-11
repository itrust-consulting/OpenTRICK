
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

--
-- Indexes for dumped tables
--

--
-- Indexes for table `FormulaValue`
--
ALTER TABLE `FormulaValue`
  ADD PRIMARY KEY (`idFormulaValue`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `FormulaValue`
--
ALTER TABLE `FormulaValue`
  MODIFY `idFormulaValue` int(11) NOT NULL AUTO_INCREMENT;
  

ALTER TABLE `Assessment` ADD `dtLikelihoodType` VARCHAR(255) NULL AFTER `dtImpactReal`;
ALTER TABLE `Assessment` ADD `fiLikelihood` INT(11) NULL AFTER `fiAsset`; 

COMMIT;
