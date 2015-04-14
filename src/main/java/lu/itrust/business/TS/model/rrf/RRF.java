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
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.SecurityCriteria;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

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
	public static double calculateRRF(Assessment tmpAssessment, List<Parameter> parameters, Measure measure) throws TrickException {

		// ****************************************************************
		// * retrieve tuning value
		// ****************************************************************
		Parameter parameter = null;
		// parse parameters
		for (int i = 0; i < parameters.size(); i++) {

			// check if parameter is tuning -> YES
			if ((parameters.get(i).getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME)) && (parameters.get(i).getDescription().equals(Constant.PARAMETER_MAX_RRF))) {
				// ****************************************************************
				// * store tuning value
				// ****************************************************************
				parameter = parameters.get(i);
				// leave loop when found
				break;
			}
		}

		if (parameter == null)
			return 0;

		if (measure instanceof NormalMeasure)
			return calculateNormalMeasureRRF(tmpAssessment.getScenario(), tmpAssessment.getAsset().getAssetType(), parameter, (NormalMeasure) measure);
		else if (measure instanceof AssetMeasure)
			return calculateAssetMeasureRRF(tmpAssessment.getScenario(), tmpAssessment.getAsset(), parameter, (AssetMeasure) measure);
		else
			return 0;
	}

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
	 * @param parameter
	 *            The tuning parameter
	 * @param measure
	 *            The Measure to take Values to calculate
	 * @return The Calculated RRF
	 * @throws TrickException
	 */
	public static double calculateNormalMeasureRRF(Scenario scenario, AssetType assetType, Parameter parameter, NormalMeasure measure) throws TrickException {

		// ****************************************************************
		// * initialise variables
		// ****************************************************************
		int assetValue = 0;
		double tuning = parameter.getValue();
		double strength = 0;
		double category = 0;
		double type = 0;
		double source = 0;
		double RRF = 0;

		// ****************************************************************
		// * retrieve tuning value
		// ****************************************************************

		// ****************************************************************
		// * retrieve asset type value for this asset type
		// * (inside assessment)
		// ****************************************************************

		// parse assettype value list from given measure
	
		for(AssetTypeValue atv : measure.getAssetTypeValues()) {
			if(atv.getAssetType().equals(assetType)) {
				assetValue = atv.getValue();
				break;
			}
		}
	
		// ****************************************************************
		// * Strength calculation
		// ****************************************************************
		strength = measure.getMeasurePropertyList().getFMeasure();
		strength = strength * measure.getMeasurePropertyList().getFSectoral();
		strength = strength / 40.;

		if (Double.isNaN(strength))
			throw new TrickException("error.analysis.rrf.measure.strength.nan", String.format("RRF computation: please check strength of measure (%s)", measure
					.getMeasureDescription().getReference()));

		// ****************************************************************
		// * Category calculation
		// ****************************************************************
		category = calculateRRFCategory(measure.getMeasurePropertyList(), scenario);

		if (Double.isNaN(category))
			throw new TrickException("error.analysis.rrf.scenario.category.nan", String.format("RRF computation: please check categories for scenario (%s)", scenario.getName()),
					scenario.getName());

		// ****************************************************************
		// * Type calculation
		// ****************************************************************

		type = measure.getMeasurePropertyList().getLimitative() * scenario.getLimitative();

		type += measure.getMeasurePropertyList().getPreventive() * scenario.getPreventive();

		type += measure.getMeasurePropertyList().getDetective() * scenario.getDetective();

		type += measure.getMeasurePropertyList().getCorrective() * scenario.getCorrective();

		type /= 4.;

		if (Double.isNaN(type))
			throw new TrickException("error.analysis.rrf.type.nan", String.format("RRF computation: please check scenario(%s) and measure (%s for %s), type is not number",
					scenario.getName(), measure.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel()), scenario.getName(), measure
					.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel());

		NumberFormat nf = new DecimalFormat();

		nf.setMaximumFractionDigits(2);

		try {
			type = nf.parse(nf.format(type)).doubleValue();
		} catch (ParseException e) {
			throw new TrickException("error.number.format", e.getMessage());
		}

		// ****************************************************************
		// * Source calculation
		// ****************************************************************
		source = (measure.getMeasurePropertyList().getIntentional() * scenario.getIntentional()) + (measure.getMeasurePropertyList().getAccidental() * scenario.getAccidental())
				+ (measure.getMeasurePropertyList().getEnvironmental() * scenario.getEnvironmental())
				+ (measure.getMeasurePropertyList().getInternalThreat() * scenario.getInternalThreat())
				+ (measure.getMeasurePropertyList().getExternalThreat() * scenario.getExternalThreat());

		source = source
				/ (4. * (double) (scenario.getIntentional() + scenario.getAccidental() + scenario.getEnvironmental() + scenario.getInternalThreat() + scenario.getExternalThreat()));

		if (Double.isNaN(source))
			throw new TrickException("error.analysis.rrf.scenario.source.nan", String.format("RRF computation: please check menace source for scenario (%s)", scenario.getName()),
					scenario.getName());

		// ****************************************************************
		// * RRF completion :
		// * (((Asset_Measure/100)*Strength*CID*Type*Source) / 500) * tuning
		// ****************************************************************

		RRF = ((assetValue / 100. * strength * category * type * source)) * (tuning / 100.);

		
		  /*System.out.println("Measure: " +
		  measure.getMeasureDescription().getReference() + "Asset: " +
		  assetType.getType() + "Scenario: " + scenario.getName() + " ;RRF=" +
		  RRF + ", atv=" + assetValue + ", strength=" + strength
		  + ", Category=" + category + ", type=" + type + ", source=" + source
		  + ", tuning=" + tuning);*/
		 

		// ****************************************************************
		// * return the value
		// ****************************************************************
		return RRF;
	}

	public static double calculateAssetMeasureRRF(Scenario scenario, Asset asset, Parameter parameter, AssetMeasure measure) throws TrickException {

		int assetValue = 0;
		double tuning = parameter.getValue();
		double strength = 0;
		double category = 0;
		double type = 0;
		double source = 0;
		double RRF = 0;

		// parse assettype value list from given measure
		for (int atvc = 0; atvc < measure.getMeasureAssetValues().size(); atvc++) {

			// check if asset type of measure matches asset type of assessment
			if (measure.getAssetValue(atvc).getAsset().equals(asset)) {

				// ****************************************************************
				// * store assetTypevalue
				// ****************************************************************
				assetValue = measure.getAssetValue(atvc).getValue();
				// System.out.println("Measure: " +
				// measure.getMeasureDescription().getReference() +
				// ":: Asset Type Value:" + assetTypeValue);

				// leave loop
				break;
			}
		}

		// ****************************************************************
		// * Strength calculation
		// ****************************************************************
		strength = measure.getMeasurePropertyList().getFMeasure();
		strength = strength * measure.getMeasurePropertyList().getFSectoral();
		strength = strength / 40.;

		if (Double.isNaN(strength))
			throw new TrickException("error.analysis.rrf.measure.strength.nan", String.format("RRF computation: please check strength of measure (%s)", measure
					.getMeasureDescription().getReference()));

		// ****************************************************************
		// * Category calculation
		// ****************************************************************
		category = calculateRRFCategory(measure.getMeasurePropertyList(), scenario);

		if (Double.isNaN(category))
			throw new TrickException("error.analysis.rrf.scenario.category.nan", String.format("RRF computation: please check categories for scenario (%s)", scenario.getName()),
					scenario.getName());

		// ****************************************************************
		// * Type calculation
		// ****************************************************************

		type = measure.getMeasurePropertyList().getLimitative() * scenario.getLimitative();

		type += measure.getMeasurePropertyList().getPreventive() * scenario.getPreventive();

		type += measure.getMeasurePropertyList().getDetective() * scenario.getDetective();

		type += measure.getMeasurePropertyList().getCorrective() * scenario.getCorrective();

		type /= 4.;

		if (Double.isNaN(type))
			throw new TrickException("error.analysis.rrf.type.nan", String.format("RRF computation: please check scenario(%s) and measure (%s for %s), type is not number",
					scenario.getName(), measure.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel()), scenario.getName(), measure
					.getMeasureDescription().getReference(), measure.getAnalysisStandard().getStandard().getLabel());

		NumberFormat nf = new DecimalFormat();
		nf.setMaximumFractionDigits(2);

		try {
			type = nf.parse(nf.format(type)).doubleValue();
		} catch (ParseException e) {
			throw new TrickException("error.number.format", e.getMessage());
		}

		// ****************************************************************
		// * Source calculation
		// ****************************************************************
		source = (measure.getMeasurePropertyList().getIntentional() * scenario.getIntentional()) + (measure.getMeasurePropertyList().getAccidental() * scenario.getAccidental())
				+ (measure.getMeasurePropertyList().getEnvironmental() * scenario.getEnvironmental())
				+ (measure.getMeasurePropertyList().getInternalThreat() * scenario.getInternalThreat())
				+ (measure.getMeasurePropertyList().getExternalThreat() * scenario.getExternalThreat());

		source = source
				/ (4. * (double) (scenario.getIntentional() + scenario.getAccidental() + scenario.getEnvironmental() + scenario.getInternalThreat() + scenario.getExternalThreat()));

		if (Double.isNaN(source))
			throw new TrickException("error.analysis.rrf.scenario.source.nan", String.format("RRF computation: please check menace source for scenario (%s)", scenario.getName()),
					scenario.getName());

		// ****************************************************************
		// * RRF completion :
		// * (((Asset_Measure/100)*Strength*CID*Type*Source) / 500) * tuning
		// ****************************************************************

		RRF = ((assetValue / 100. * strength * category * type * source)) * (tuning / 100.);

		/*
		 * System.out.println("Measure: " +
		 * measure.getMeasureDescription().getReference() + "Asset: " +
		 * asset.getName() + "Scenario: " + scenario.getName() + " ;RRF=" + RRF
		 * + ", atv=" + assetValue + ", strength=" + strength + ", Category=" +
		 * category + ", type=" + type + ", source=" + source + ", tuning=" +
		 * tuning);
		 */

		// ****************************************************************
		// * return the value
		// ****************************************************************
		return RRF;
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

			// calculate: Category of Measure * Category of Scenario
			categoryNumerator += properties.getCategoryValue(risk) * scenario.getCategoryValue(risk);

			// calculate: sum of Scenario Category
			categoryDenominator += scenario.getCategoryValue(risk);
		}

		// check if not Division by 0
		if (categoryDenominator == 0) {
			throw new TrickException("error.scenario.rrf.compute.arithmetic_denominator_zero", String.format("Please check scenario (%s) data: RRF is not a number",
					scenario.getName()), scenario.getName());
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
