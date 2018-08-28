package lu.itrust.business.TS.model.rrf;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.SecurityCriteria;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.impl.NormalMeasure;

/**
 * RRF.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Nov 5, 2014
 */
public class RRF {
	/***********************************************************************************************
	 * RRF - BEGIN
	 **********************************************************************************************/

	/**
	 * calculateRRF: <br>
	 * Calculates the RRF (Risk Reduction Factor) using the Formulas from a
	 * given measure, given Scenario and given Asset (asset and scenario
	 * together: assessment) values.
	 * 
	 * @param tmpAssessment
	 *            The Assessment to take Values to calculate
	 * @param parameters
	 *            The Parameters List
	 * @param measure
	 *            The Measure to take Values to calculate
	 * 
	 * @return The Calculated RRF
	 * @throws TrickException
	 */
	public static double calculateRRF(Assessment tmpAssessment, IParameter tuningParameter, Measure measure) throws TrickException {
		if (tuningParameter == null)
			return 0;
		if (measure instanceof NormalMeasure)
			return calculateNormalMeasureRRF(tmpAssessment.getScenario(), tmpAssessment.getAsset().getAssetType(), tuningParameter, (NormalMeasure) measure);
		else if (measure instanceof AssetMeasure)
			return calculateAssetMeasureRRF(tmpAssessment.getScenario(), tmpAssessment.getAsset(), tuningParameter, (AssetMeasure) measure);
		else
			return 0;
	}

	public static double calculateRRF(Assessment tmpAssessment, List<SimpleParameter> simpleParameters, Measure measure) throws TrickException {
		IParameter parameter = simpleParameters.stream().filter(param -> param.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF)).findAny().orElse(null);
		return calculateRRF(tmpAssessment, parameter, measure);
	}
	
	/***********************************************************************************************
	 * RRF - BEGIN
	 **********************************************************************************************/

	/**
	 * calculateRRF: <br>
	 * Calculates the RRF (Risk Reduction Factor) using the Formulas from a
	 * given measure, given Scenario and given Asset (asset and scenario
	 * together: assessment) values.
	 * 
	 * @param scenario
	 *            The scenario to take Values to calculate
	 * @param assetType
	 *            The assetType to take Values to calculate
	 * @param tuningParameter
	 *            The tuning parameter
	 * @param measure
	 *            The Measure to take Values to calculate
	 * @return The Calculated RRF
	 * @throws TrickException
	 */
	public static double calculateNormalMeasureRRF(Scenario scenario, AssetType assetType, IParameter tuningParameter, NormalMeasure measure) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		double strength;
		double category;
		double type;
		double source;
		final MeasureProperties measureProperties = measure.getMeasurePropertyList();

		// ****************************************************************
		// * retrieve tuning value
		// ****************************************************************

		// ****************************************************************
		// * retrieve asset type value for this asset type
		// * (inside assessment)
		// ****************************************************************

		// parse assettype value list from given measure

		int assetValue = measure.getAssetTypeValues().stream().filter(assetTypeValue -> assetTypeValue.getAssetType().equals(assetType))
				.mapToInt(assetTypeValue -> assetTypeValue.getValue()).findAny().orElse(0);

		// ****************************************************************
		// * Strength calculation
		// ****************************************************************
		strength = measure.getMeasurePropertyList().getFMeasure();
		strength = strength * measure.getMeasurePropertyList().getFSectoral();
		strength = strength / 40.;

		// Strength calculation
		strength = measureProperties.getFMeasure() * measureProperties.getFSectoral() / 40.;
		if (Double.isNaN(strength))
			throw new TrickException("error.analysis.rrf.measure.strength.nan",
					String.format("RRF computation: please check strength of measure (%s)", measure.getMeasureDescription().getReference()));

		// ****************************************************************
		// * Category calculation
		// ****************************************************************
		category = calculateRRFCategory(measure.getMeasurePropertyList(), scenario);

		// Category calculation
		category = calculateRRFCategory(measureProperties, scenario);
		if (Double.isNaN(category))
			throw new TrickException("error.analysis.rrf.scenario.category.nan", String.format("RRF computation: please check categories for scenario (%s)", scenario.getName()), scenario.getName());

		// Type calculation
		type =
			measureProperties.getLimitative() * scenario.getLimitative() +
			measureProperties.getPreventive() * scenario.getPreventive() +
			measureProperties.getDetective() * scenario.getDetective() +
			measureProperties.getCorrective() * scenario.getCorrective();
		type /= 4.;
		if (Double.isNaN(type))
			throw new TrickException("error.analysis.rrf.type.nan",
					String.format("RRF computation: please check scenario(%s) and measure (%s for %s), type is not number", scenario.getName(),
							measure.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel()),
					scenario.getName(), measure.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel());

		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(2);
		try {
			type = nf.parse(nf.format(type)).doubleValue();
		} catch (ParseException e) {
			throw new TrickException("error.number.format", e.getMessage());
		}

		// Source calculation
		source =
			measureProperties.getIntentional() * scenario.getIntentional() +
			measureProperties.getAccidental() * scenario.getAccidental() +
			measureProperties.getEnvironmental() * scenario.getEnvironmental() +
			measureProperties.getInternalThreat() * scenario.getInternalThreat() +
			measureProperties.getExternalThreat() * scenario.getExternalThreat();
		source /= 4. * (scenario.getIntentional() + scenario.getAccidental() + scenario.getEnvironmental() + scenario.getInternalThreat() + scenario.getExternalThreat());
		if (Double.isNaN(source))
			throw new TrickException("error.analysis.rrf.scenario.source.nan", String.format("RRF computation: please check menace source for scenario (%s)", scenario.getName()), scenario.getName());

		// RRF completion:
		// (((Asset_Measure/100)*Strength*CID*Type*Source) / 500) * tuning
		return assetValue / 100. * strength * category * type * source * tuningParameter.getValue().doubleValue() / 100.;
	}

	public static double calculateAssetMeasureRRF(Scenario scenario, Asset asset, IParameter tuningParameter, AssetMeasure measure) throws TrickException {
		double strength;
		double category;
		double type;
		double source;
		final MeasureProperties measureProperties = measure.getMeasurePropertyList();

		int assetValue = measure.getMeasureAssetValues().stream().filter(measureAssetValue -> measureAssetValue.getAsset().equals(asset))
				.mapToInt(measureAssetValue -> measureAssetValue.getValue()).findAny().orElse(0);

		// Strength calculation
		strength = measureProperties.getFMeasure() * measureProperties.getFSectoral() / 40.;
		if (Double.isNaN(strength))
			throw new TrickException("error.analysis.rrf.measure.strength.nan",
					String.format("RRF computation: please check strength of measure (%s)", measure.getMeasureDescription().getReference()));

		// ****************************************************************
		// * Category calculation
		// ****************************************************************
		category = calculateRRFCategory(measure.getMeasurePropertyList(), scenario);

		// Category calculation
		category = calculateRRFCategory(measureProperties, scenario);
		if (Double.isNaN(category))
			throw new TrickException("error.analysis.rrf.scenario.category.nan", String.format("RRF computation: please check categories for scenario (%s)", scenario.getName()), scenario.getName());

		// Type calculation
		type =
			measureProperties.getLimitative() * scenario.getLimitative() +
			measureProperties.getPreventive() * scenario.getPreventive() +
			measureProperties.getDetective() * scenario.getDetective() +
			measureProperties.getCorrective() * scenario.getCorrective();
		type /= 4.;
		if (Double.isNaN(type))
			throw new TrickException("error.analysis.rrf.type.nan",
					String.format("RRF computation: please check scenario(%s) and measure (%s for %s), type is not number", scenario.getName(),
							measure.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel()),
					scenario.getName(), measure.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel());

		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(2);
		try {
			type = nf.parse(nf.format(type)).doubleValue();
		} catch (ParseException e) {
			throw new TrickException("error.number.format", e.getMessage());
		}

		// Source calculation
		source =
			measureProperties.getIntentional() * scenario.getIntentional() +
			measureProperties.getAccidental() * scenario.getAccidental() +
			measureProperties.getEnvironmental() * scenario.getEnvironmental() +
			measureProperties.getInternalThreat() * scenario.getInternalThreat() +
			measureProperties.getExternalThreat() * scenario.getExternalThreat();
		source /= 4. * (scenario.getIntentional() + scenario.getAccidental() + scenario.getEnvironmental() + scenario.getInternalThreat() + scenario.getExternalThreat());

		if (Double.isNaN(source))
			throw new TrickException("error.analysis.rrf.scenario.source.nan", String.format("RRF computation: please check menace source for scenario (%s)", scenario.getName()), scenario.getName());

		// RRF completion:
		// (((Asset_Measure/100)*Strength*CID*Type*Source) / 500) * tuning
		return assetValue / 100. * strength * category * type * source * tuningParameter.getValue().doubleValue() / 100.;
	}

	/**
	 * calculateRRFCategory: <br>
	 * RRF Category calculation Returns SUM(Rm*RiS)/4*SUM(Rs): R =
	 * RISK(CONFIDENTIALITY,AVAILABILITY,INTEGRITY,Direct[1-7], Indirect[1-10]),
	 * M=MeasureProperties and S=scenario
	 * 
	 * @param properties
	 *            MeasureProperties
	 * @param scenario
	 *            Scenario
	 * 
	 * @return The Calculated RRF Category value
	 * @throws TrickException
	 */
	public static double calculateRRFCategory(MeasureProperties properties, Scenario scenario) throws TrickException {

		// check if properties and scenario are not null to avoid failures
		if (properties == null)
			throw new TrickException("error.rrf.compute.properties_null", "Measure properties cannot be empty");

		if (scenario == null)
			throw new TrickException("error.rrf.compute.scenario_null", "Scenario cannot be empty");

		// **************************************************************
		// * intialise variables
		// **************************************************************
		double categoryNumerator = 0;
		double categoryDenominator = 0;
		final double MULTIPLICATOR = 4;
		String[] keys = SecurityCriteria.getCategoryKeys();

		// **************************************************************
		// * calculate numerator and denominator of Category Formula
		// **************************************************************

		// parse Category Keys
		for (String risk : keys) {
			double measureValue = properties.getCategoryValue(risk);
			double scenarioValue = scenario.getCategoryValue(risk);
			
			categoryNumerator += measureValue * scenarioValue;
			categoryDenominator += scenarioValue;
		}

		// check if not Division by 0
		if (categoryDenominator == 0) {
			throw new TrickException("error.scenario.rrf.compute.arithmetic_denominator_zero",
					String.format("Please check RRF settings for scenario (%s)", scenario.getName()), scenario.getName());
		}

		// **************************************************************
		// * return numerator / MULTIPLICATOR * denominator
		// **************************************************************
		return categoryNumerator / (MULTIPLICATOR * categoryDenominator);
	}

	/***********************************************************************************************
	 * RRF - END
	 **********************************************************************************************/

}
