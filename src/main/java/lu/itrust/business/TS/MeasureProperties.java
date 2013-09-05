package lu.itrust.business.TS;

/**
 * MeasureProperties: <br>
 * This class represents a Measure Properties and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class MeasureProperties extends SecurityCriteria {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Strength Measure */
	private int fmeasure = 0;

	/** Strength Sectoral */
	private int fSectoral = 0;

	/** SOA Reference */
	private String soaReference = "";

	/** SOA Comment */
	private String soaComment = "";

	/** SOA Risk */
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
	 */
	public void setFMeasure(int measure) {
		if ((measure < 0) || (measure > 10)) {
			throw new IllegalArgumentException(
					"Force Measure needs to be between 0 and 10 included!");
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
		return fSectoral;
	}

	/**
	 * setFSectoral: <br>
	 * Sets the "fSectoral" field with a value
	 * 
	 * @param fSectoral
	 *            The value to set the Sectoral Strength
	 */
	public void setFSectoral(int fSectoral) {
		if ((fSectoral < 0) || (fSectoral > 4)) {
			throw new IllegalArgumentException(
					"Force Sectoral needs to be between 0 and 4 included!");
		}
		this.fSectoral = fSectoral;
	}

	/**
	 * setPreventive: <br>
	 * Sets the "preventive" field with a value
	 * 
	 * @param preventive
	 *            The value to set the Preventive
	 */
	@Override
	public void setPreventive(double preventive) {
		if ((preventive < 0) || (preventive > 4)) {
			throw new IllegalArgumentException("Preventive needs to be between 0 and 4 included!");
		}
		super.setPreventive(preventive);
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *            The value to set the Detective
	 */
	@Override
	public void setDetective(double detective) {
		if ((detective < 0) || (detective > 4)) {
			throw new IllegalArgumentException("Detective needs to be between 0 and 4 included!");
		}
		super.setDetective(detective);
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *            The value to set the Limitative
	 */
	@Override
	public void setLimitative(double limitative) {
		if ((limitative < 0) || (limitative > 4)) {
			throw new IllegalArgumentException("Limitative needs to be between 0 and 4 included!");
		}
		super.setLimitative(limitative);
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *            The value to set the Corrective
	 */
	@Override
	public void setCorrective(double corrective) {
		if ((corrective < 0) || (corrective > 4)) {
			throw new IllegalArgumentException("Corrective needs to be between 0 and 4 included!");
		}
		super.setCorrective(corrective);
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *            The value to set the Intentional
	 */
	@Override
	public void setIntentional(int intentional) {
		if (!isValidValue(intentional)) {
			throw new IllegalArgumentException("Intentional needs to be between 0 and 4 included!");
		}
		super.setIntentional(intentional);
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *            The value to set the Accidental
	 */
	@Override
	public void setAccidental(int accidental) {
		if (!isValidValue(accidental)) {
			throw new IllegalArgumentException("Accidental needs to be between 0 and 4 included!");
		}
		super.setAccidental(accidental);
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *            The value to set the Environmental
	 */
	@Override
	public void setEnvironmental(int environmental) {
		if (!isValidValue(environmental)) {
			throw new IllegalArgumentException(
					"Environmental needs to be between 0 and 4 included!");
		}
		super.setEnvironmental(environmental);
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *            The value to set the Internal Threat
	 */
	@Override
	public void setInternalThreat(int internalthreat) {
		if (!isValidValue(internalthreat)) {
			throw new IllegalArgumentException(
					"Internal Threat needs to be between 0 and 4 included!");
		}
		super.setInternalThreat(internalthreat);
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *            The value to set the External Threat
	 */
	@Override
	public void setExternalThreat(int externalthreat) {
		if (!isValidValue(externalthreat)) {
			throw new IllegalArgumentException(
					"External Threat needs to be between 0 and 4 included!");
		}
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
}