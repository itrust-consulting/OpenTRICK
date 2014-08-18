package lu.itrust.business.TS;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

import lu.itrust.business.exception.TrickException;

/**
 * MeasureProperties: <br>
 * This class represents a Measure Properties and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity 
public class MeasureProperties extends SecurityCriteria {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** Strength Measure */
	@Column(name="dtStrengthMeasure")
	private int fmeasure = 0;

	/** Strength Sectoral */
	@Column(name="dtStrengthSectoral")
	private int fsectoral = 0;

	/** SOA Reference */
	@Column(name="dtSOAReference")
	private String soaReference = "";

	/** SOA Comment */
	@Column(name="dtSOAComment")
	private String soaComment = "";

	/** SOA Risk */
	@Column(name="dtSOARisk")
	private String soaRisk = "";

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getSoaReference: <br>
	 * Returns the "soaReference" field value
	 * 
	 * @return The SOA Reference
	 */
	public String getSoaReference() {
		return soaReference;
	}

	/**
	 * setSoaReference: <br>
	 * Sets the "soaReference" field with a value
	 * 
	 * @param soaReference
	 *            The value to set the SOA Reference
	 */
	public void setSoaReference(String soaReference) {
		this.soaReference = soaReference;
	}

	/**
	 * getSoaComment: <br>
	 * Returns the "soaComment" field value
	 * 
	 * @return The SOA Comment
	 */
	public String getSoaComment() {
		return soaComment;
	}

	/**
	 * setSoaComment: <br>
	 * Sets the "soaComment" field with a value
	 * 
	 * @param soaComment
	 *            The value to set the SOA Comment
	 */
	public void setSoaComment(String soaComment) {
		this.soaComment = soaComment;
	}

	/**
	 * getSoaRisk: <br>
	 * Returns the "soaRisk" field value
	 * 
	 * @return The SOA Risk
	 */
	public String getSoaRisk() {
		return soaRisk;
	}

	/**
	 * setSoaRisk: <br>
	 * Sets the "soaRisk" field with a value
	 * 
	 * @param soaRisk
	 *            The value to set the SOA Risk
	 */
	public void setSoaRisk(String soaRisk) {
		this.soaRisk = soaRisk;
	}

	/**
	 * getFMeasure: <br>
	 * Returns the "fmeasure" field value
	 * 
	 * @return The Measure Strength value
	 */
	public int getFMeasure() {
		return fmeasure;
	}

	/**
	 * setFMeasure: <br>
	 * Sets the "fmeasure" field with a value
	 * 
	 * @param measure
	 *            The value to set the Measure Strength
	 * @throws TrickException
	 */
	public void setFMeasure(int measure) throws TrickException {
		if (measure < 0 || measure > 10) {
			throw new TrickException("error.measure_property.measure.strenght", "Measure strenght needs to be between 0 and 10!");
		}
		this.fmeasure = measure;
	}

	/**
	 * getFSectoral: <br>
	 * Returns the "fSectoral" field value
	 * 
	 * @return The Sectoral Strength value
	 */
	public int getFSectoral() {
		return fsectoral;
	}

	/**
	 * setFSectoral: <br>
	 * Sets the "fSectoral" field with a value
	 * 
	 * @param fSectoral
	 *            The value to set the Sectoral Strength
	 * @throws TrickException
	 */
	public void setFSectoral(int fSectoral) throws TrickException {
		if ((fSectoral < 0) || (fSectoral > 4))
			throw new TrickException("error.measure_property.sectoral.strenght", "Sectoral strenght needs to be between 0 and 4!");
		this.fsectoral = fSectoral;
	}

	/**
	 * setPreventive: <br>
	 * Sets the "preventive" field with a value
	 * 
	 * @param preventive
	 *            The value to set the Preventive
	 * @throws TrickException
	 */
	@Override
	public void setPreventive(double preventive) throws TrickException {
		if ((preventive < 0) || (preventive > 4))
			throw new TrickException("error.measure_property.preventive", "Preventive needs to be between 0 and 4!");
		super.setPreventive(preventive);
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *            The value to set the Detective
	 * @throws TrickException
	 */
	@Override
	public void setDetective(double detective) throws TrickException {
		if ((detective < 0) || (detective > 4))
			throw new TrickException("error.measure_property.detective", "Detective needs to be between 0 and 4!");
		super.setDetective(detective);
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *            The value to set the Limitative
	 * @throws TrickException
	 */
	@Override
	public void setLimitative(double limitative) throws TrickException {
		if ((limitative < 0) || (limitative > 4))
			throw new TrickException("error.measure_property.limitative", "Limitative needs to be between 0 and 4!");
		super.setLimitative(limitative);
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *            The value to set the Corrective
	 * @throws TrickException
	 */
	@Override
	public void setCorrective(double corrective) throws TrickException {
		if ((corrective < 0) || (corrective > 4))
			throw new TrickException("error.measure_property.corrective", "Corrective needs to be between 0 and 4!");
		super.setCorrective(corrective);
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *            The value to set the Intentional
	 * @throws TrickException
	 */
	@Override
	public void setIntentional(int intentional) throws TrickException {
		if (!isValidValue(intentional))
			throw new TrickException("error.measure_property.intentional", "Intentional needs to be between 0 and 4!");

		super.setIntentional(intentional);
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *            The value to set the Accidental
	 * @throws TrickException
	 */
	@Override
	public void setAccidental(int accidental) throws TrickException {
		if (!isValidValue(accidental))
			throw new TrickException("error.measure_property.accidental", "Accidental needs to be between 0 and 4!");
		super.setAccidental(accidental);
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *            The value to set the Environmental
	 * @throws TrickException
	 */
	@Override
	public void setEnvironmental(int environmental) throws TrickException {
		if (!isValidValue(environmental))
			throw new TrickException("error.measure_property.environmental", "Environmental needs to be between 0 and 4!");
		super.setEnvironmental(environmental);
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *            The value to set the Internal Threat
	 * @throws TrickException
	 */
	@Override
	public void setInternalThreat(int internalthreat) throws TrickException {
		if (!isValidValue(internalthreat))
			throw new TrickException("error.measure_property.internal_threat", "Internal threat needs to be between 0 and 4!");
		super.setInternalThreat(internalthreat);
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *            The value to set the External Threat
	 * @throws TrickException
	 */
	@Override
	public void setExternalThreat(int externalthreat) throws TrickException {
		if (!isValidValue(externalthreat))
			throw new TrickException("error.measure_property.extternal_threat", "External threat needs to be between 0 and 4!");
		super.setExternalThreat(externalthreat);
	}

	/**
	 * isValidCategoryValue: <br>
	 * Check if value for Category is valid. (Valid values are: 0 <= value >= 4)
	 * 
	 */
	@Override
	protected boolean isValidValue(int value) {
		return !(value < 0 || value > 4);
	}

	public void copyTo(MeasureProperties measurePropertyList) throws TrickException {
		if (measurePropertyList == null)
			return;
		super.copyTo(measurePropertyList);
		measurePropertyList.fmeasure = fmeasure;
		measurePropertyList.fsectoral = fsectoral;

	}

	@Override
	protected int valueFixer(String category, int value) throws TrickException {
		if (value < 0 || value > 4)
			throw new TrickException("error.security_criteria.category.invalid", String.format("'%s' is not valid!", category), category);
		return value == 0 ? 0 : 4;
	}
}