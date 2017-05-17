package lu.itrust.business.TS.model.actionplan.helper;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;

/**
 * TMA: <br>
 * Stands for Threat, Measure, Asset. It contains data to calculate for each
 * triple of T,M,A the delta ALE. Using each T,M,A inside a list it is possible
 * to calculate and create the action plans.
 * 
 * @author itrust consulting s.� r.l. - BJA,SME
 * @version 0.1
 * @since 2012-09-13
 */
public class TMA {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** Assessment object */
	private Assessment assessment;

	/** AnalysisStandard of Measure */
	private Standard standard;

	/** Measure object */
	private Measure measure;

	/** RRF: Risk Reduction Factor */
	private double RRF;

	/** This ALE (for this Asset in this Scenario) - Annual Loss Expectancy */
	private double ALE;

	/** This delta ALE calculated for the T,M,A (Threat, Measure, Assessment) */
	private double deltaALE;

	/** Current SML Maximum Efficiency value */
	private double cMaxEff;

	/** Next SML Maximum Efficiency value */
	private double nMaxEff;

	/** Delta ALE calculated for the Maturity Chapter */
	private double deltaALEMat;

	/***********************************************************************************************
	 * Constructors
	 **********************************************************************************************/

	/**
	 * Constructor: <br>
	 * Creates a new instance of TMA and selects the ALE using the mode and sets
	 * the Assessment, AnalysisStandard, Measure objects and the calculated RRF
	 * from the parameters.
	 * 
	 * @param mode
	 * @param asessment
	 * @param measure
	 * @param RRF
	 * @throws TrickException
	 */
	public TMA(ActionPlanMode mode, Assessment asessment, Measure measure, double RRF) throws TrickException {

		// the assessment (-> Asset + Scenario)
		this.assessment = asessment;

		// get ALE corresponding to type of computation
		switch (mode) {
		case APN:
		case APPN:
		case APQ:
			this.ALE = this.assessment.getALE();
			break;
		case APO:
		case APPO:
			this.ALE = this.assessment.getALEO();
			break;
		case APP:
		case APPP:
			this.ALE = this.assessment.getALEP();
			break;
		}

		// the analysisStandard
		this.standard = measure.getAnalysisStandard().getStandard();

		// the measure
		this.measure = measure;

		// the calculated RRF
		this.RRF = RRF;

		if (Double.isNaN(RRF))
			throw new TrickException("error.tma.rrf.nan", "Please check your data: RRF is not a number");
	}

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * calculateDeltaALE: <br>
	 * Calculates the delta ALE using the formula <br>
	 * ALE * RRF * (1 - ImpRate / 1 - RRF * ImpRate)
	 * 
	 * @throws TrickException
	 */
	public void calculateDeltaALE(ValueFactory factory) throws TrickException {
		if (Double.isNaN(RRF))
			throw new TrickException("error.tma.rrf.nan", "Please check your data: RRF is not a number");
		if (Double.isNaN(ALE))
			throw new TrickException("error.tma.ale.nan", "Please check your data: ALE is not a number");
		double implementationRate = this.measure.getImplementationRateValue(factory) / 100.;
		this.deltaALE = this.ALE * RRF * (1. - implementationRate) / (1. - RRF * implementationRate);

	}

	/**
	 * calculateDeltaALE: <br>
	 * Calculates the delta ALE of a given ALE using the formula <br>
	 * ALE * RRF * (1 - ImpRate / 1 - RRF * ImpRate)
	 * 
	 * @param ALE
	 *            The ALE before
	 * @param RRF
	 *            The calculated RRF
	 * @param measure
	 *            The Measure to calculate the deltaALE
	 * 
	 * @return the computed deltaALE for this measure using a given ALE
	 * @throws TrickException
	 */
	public static double calculateDeltaALE(double ALE, double RRF, Measure measure, ValueFactory valueFactory) throws TrickException {
		if (Double.isNaN(RRF))
			throw new TrickException("error.tma.rrf.nan", "Please check your data: RRF is not a number");
		if (Double.isNaN(ALE))
			throw new TrickException("error.tma.ale.nan", "Please check your data: ALE is not a number");
		double implementationRate = measure.getImplementationRateValue(valueFactory) / 100.0;
		return ALE * RRF * (1.0 - implementationRate) / (1.0 - RRF * implementationRate);
	}

	/**
	 * calculateDeltaALEMaturity: <br>
	 * Calculates the delta ALE for 27002 measures using the formula <br>
	 * ALE * RRF * ImpRate * ((maxEffnextSML - maxEffcurrentSML) / 1 - RRF *
	 * ImpRate)
	 */
	public void calculateDeltaALEMaturity(ValueFactory factory) {
		double implementationRate = measure.getImplementationRateValue(factory) / 100.0;
		this.deltaALEMat = this.ALE * RRF * implementationRate * ((nMaxEff / 100. - cMaxEff / 100.) / (1. - RRF * cMaxEff / 100. * implementationRate));
	}

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getAssessment: <br>
	 * Returns the "assessment" field value
	 * 
	 * @return The Assessment
	 */
	public Assessment getAssessment() {
		return assessment;
	}

	/**
	 * setAssessment: <br>
	 * Sets the "assessment" field with a value
	 * 
	 * @param assessment
	 *            The value to set the Assessment
	 */
	public void setAssessment(Assessment assessment) {
		this.assessment = assessment;
	}

	/**
	 * getStandard: <br>
	 * Description
	 * 
	 * @return
	 */
	public Standard getStandard() {
		return standard;
	}

	/**
	 * setStandard: <br>
	 * Description
	 * 
	 * @param standard
	 */
	public void setStandard(Standard standard) {
		this.standard = standard;
	}

	/**
	 * getMeasure: <br>
	 * Returns the "measure" field value
	 * 
	 * @return The Measure
	 */
	public Measure getMeasure() {
		return measure;
	}

	/**
	 * setMeasure: <br>
	 * Sets the "measure" field with a value
	 * 
	 * @param measure
	 *            The value to set the Measure
	 */
	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	/**
	 * getRRF: <br>
	 * Returns the "RRF" field value
	 * 
	 * @return The RRF
	 */
	public double getRRF() {
		return RRF;
	}

	/**
	 * setRRF: <br>
	 * Sets the "RRF" field with a value
	 * 
	 * @param rrf
	 *            The value to set the RRF
	 */
	public void setRRF(double rrf) {
		RRF = rrf;
	}

	/**
	 * getALE: <br>
	 * Returns the "ALE" field value
	 * 
	 * @return The ALE
	 */
	public double getALE() {
		return ALE;
	}

	/**
	 * setALE: <br>
	 * Sets the "ALE" field with a value
	 * 
	 * @param ale
	 *            The value to set the ALE
	 */
	public void setALE(double ale) {
		ALE = ale;
	}

	/**
	 * getDeltaALE: <br>
	 * Returns the "deltaALE" field value
	 * 
	 * @return The delta ALE
	 */
	public double getDeltaALE() {
		return deltaALE;
	}

	/**
	 * setDeltaALE: <br>
	 * Sets the "deltaALE" field with a value
	 * 
	 * @param deltaALE
	 *            The value to set the delta ALE
	 */
	public void setDeltaALE(double deltaALE) {
		this.deltaALE = deltaALE;
	}

	/**
	 * getcMaxEff: <br>
	 * Returns the "cMaxEff" field value
	 * 
	 * @return The Current Max Effency value
	 */
	public double getcMaxEff() {
		return cMaxEff;
	}

	/**
	 * setcMaxEff: <br>
	 * Sets the "cMaxEff" field with a value
	 * 
	 * @param cMaxEff
	 *            The value to set the Current Max Effency value
	 */
	public void setcMaxEff(double cMaxEff) {
		this.cMaxEff = cMaxEff;
	}

	/**
	 * getnMaxEff: <br>
	 * Returns the "nMaxEff" field value
	 * 
	 * @return The Next Max Effency value
	 */
	public double getnMaxEff() {
		return nMaxEff;
	}

	/**
	 * setnMaxEff: <br>
	 * Sets the "nMaxEff" field with a value
	 * 
	 * @param nMaxEff
	 *            The value to set the Next Max Effency value
	 */
	public void setnMaxEff(double nMaxEff) {
		this.nMaxEff = nMaxEff;
	}

	/**
	 * getDeltaALEMat: <br>
	 * Returns the "deltaALEMat" field value
	 * 
	 * @return The delta ALE for Maturity
	 */
	public double getDeltaALEMat() {
		return deltaALEMat;
	}

	/**
	 * setDeltaALEMat: <br>
	 * Sets the "deltaALEMat" field with a value
	 * 
	 * @param deltaALEMat
	 *            The value to set the delta ALE for Maturity
	 */
	public void setDeltaALEMat(double deltaALEMat) {
		this.deltaALEMat = deltaALEMat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assessment == null) ? 0 : assessment.hashCode());
		result = prime * result + ((measure == null) ? 0 : measure.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TMA))
			return false;
		TMA other = (TMA) obj;
		if (assessment == null) {
			if (other.assessment != null)
				return false;
		} else if (!assessment.equals(other.assessment))
			return false;
		if (measure == null) {
			if (other.measure != null)
				return false;
		} else if (!measure.equals(other.measure))
			return false;
		return true;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return "TMA [" + "Assessment [id=" + assessment.getId() + ", ALE=" + assessment.getALE() + ", ALEO=" + assessment.getALEO() + ", ALEP=" + assessment.getALEP() + ", Impact="
				+ assessment.getImpactReal() + ", Likelihood=" + assessment.getLikelihoodReal() + ", Uncertainty=" + assessment.getUncertainty() + ", " + "Asset [id="
				+ assessment.getAsset().getId() + ", name=" + assessment.getAsset().getName() + "], " + "Scenario [id=" + assessment.getScenario().getName() + ", name="
				+ assessment.getScenario().getName() + "]], " + "Measure[id=" + measure.getId() + ", Standard [ id=" + standard.getId() + ", name=" + standard.getLabel()
				+ ", version=" + standard.getVersion() + "], reference=" + measure.getMeasureDescription().getReference() + "], " + "RRF=" + RRF + ", ALE=" + ALE + ", deltaALE="
				+ deltaALE + ", current SML Max Eff= " + cMaxEff + ", next SML Max Eff=" + nMaxEff + ", deltaALE Maturtity=" + deltaALEMat + "]";
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public TMA clone() {
		try {
			TMA tma = (TMA) super.clone();
			tma.assessment = (Assessment) assessment.clone();
			return (TMA) super.clone();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}