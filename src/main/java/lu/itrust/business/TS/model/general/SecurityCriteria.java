package lu.itrust.business.TS.model.general;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.cssf.tools.CategoryConverter;

/**
 * SecurityCriteria: <br>
 * This class represents SecurityCriteria which are properties of either a
 * Measure or a Scenario.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-11-26
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SecurityCriteria implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	protected static Pattern CATEGOTY_PATTERN = Pattern.compile(Constant.REGEXP_VALID_SCENARIO_CATEGORY);

	/** The Scenario Identifier */
	private int id = -1;

	/** The Preventive value */
	private double preventive = 0;

	/** The Detective value */
	private double detective = 0;

	/** The Limitative value */
	private double limitative = 0;

	/** The Corrective value */
	private double corrective = 0;

	/** The Intentional value */
	private int intentional = 0;

	/** The Accidental value */
	private int accidental = 0;

	/** The Environmental value */
	private int environmental = 0;

	/** The Internal Threat value */
	private int internalThreat = 0;

	/** The External Threat value */
	private int externalThreat = 0;

	/** The Map of Scenario Categories */
	private Map<String, Integer> categories = new LinkedHashMap<String, Integer>(25);

	/***********************************************************************************************
	 * Constructor
	 **********************************************************************************************/

	/**
	 * Constructor:<br>
	 */
	public SecurityCriteria() {
	}

	/***********************************************************************************************
	 * Setters and Getters
	 **********************************************************************************************/

	/**
	 * getId: <br>
	 * Returns the "id" field value
	 * 
	 * @return The Scenario ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idSecurityCriteria")
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The value to set the
	 * @throws TrickException
	 */
	public void setId(int id) throws TrickException {
		if (id < 1)
			throw new TrickException("error.security_criteria.id.invalid", "Id should be greater than 0");
		this.id = id;
	}

	/**
	 * getDirect1: <br>
	 * Returns the "Direct1" value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The "Direct1" value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect1Cat", nullable = false)
	public int getDirect1() throws TrickException {
		return getCategoryValue("Direct1");
	}

	/**
	 * setDirect1: <br>
	 * Sets the "Direct1" with a value. Uses
	 * {@link #setCategoryValue(String, int)}.
	 * 
	 * @param value
	 *            the value to set
	 * @throws TrickException
	 */

	public void setDirect1(int value) throws TrickException {
		setCategoryValue("Direct1", value);
	}

	/**
	 * getDirect2: <br>
	 * Returns the "Direct2" value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The "Direct2" value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect2Cat", nullable = false)
	public int getDirect2() throws TrickException {
		return getCategoryValue("Direct2");
	}

	/**
	 * setDirect2: <br>
	 * Sets the "Direct2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect2(int value) throws TrickException {
		setCategoryValue("Direct2", value);
	}

	/**
	 * getDirect3: <br>
	 * Return the Direct3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct3 value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect3Cat", nullable = false)
	public int getDirect3() throws TrickException {
		return getCategoryValue("Direct3");
	}

	/**
	 * setDirect3: <br>
	 * Sets the "Direct3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect3(int value) throws TrickException {
		setCategoryValue("Direct3", value);
	}

	/**
	 * getDirect4: <br>
	 * Return the Direct4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct4 value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect4Cat", nullable = false)
	public int getDirect4() throws TrickException {
		return getCategoryValue("Direct4");
	}

	/**
	 * setDirect4: <br>
	 * Sets the "Direct4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect4(int value) throws TrickException {
		setCategoryValue("Direct4", value);
	}

	/**
	 * getDirect5: <br>
	 * Return the Direct5 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct5 value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect5Cat", nullable = false)
	public int getDirect5() throws TrickException {
		return getCategoryValue("Direct5");
	}

	/**
	 * setDirect5: <br>
	 * Sets the "Direct5" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect5(int value) throws TrickException {
		setCategoryValue("Direct5", value);
	}

	/**
	 * getDirect6: <br>
	 * Return the Direct6 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6 value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect6Cat", nullable = false)
	public int getDirect6() throws TrickException {
		return getCategoryValue("Direct6");
	}

	/**
	 * setDirect6: <br>
	 * Sets the "Direct6" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect6(int value) throws TrickException {
		setCategoryValue("Direct6", value);
	}

	/**
	 * getDirect61: <br>
	 * Return the Direct6.1 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.1 value
	 * @throws TrickException
	 */
	@Column(name = "`dtDirect6.1Cat`", nullable = false)
	public int getDirect61() throws TrickException {
		return getCategoryValue("Direct6.1");
	}

	/**
	 * setDirect61: <br>
	 * Sets the "Direct6.1" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect61(int value) throws TrickException {
		setCategoryValue("Direct6.1", value);
	}

	/**
	 * getDirect62: <br>
	 * Return the Direct6.2 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.2 value
	 * @throws TrickException
	 */
	@Column(name = "`dtDirect6.2Cat`", nullable = false)
	public int getDirect62() throws TrickException {
		return getCategoryValue("Direct6.2");
	}

	/**
	 * setDirect62: <br>
	 * Sets the "Direct6.2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect62(int value) throws TrickException {
		setCategoryValue("Direct6.2", value);
	}

	/**
	 * getDirect63: <br>
	 * Return the Direct6.3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.3 value
	 * @throws TrickException
	 */
	@Column(name = "`dtDirect6.3Cat`", nullable = false)
	public int getDirect63() throws TrickException {
		return getCategoryValue("Direct6.3");
	}

	/**
	 * setDirect63: <br>
	 * Sets the "Direct6.3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect63(int value) throws TrickException {
		setCategoryValue("Direct6.3", value);
	}

	/**
	 * getDirect64: <br>
	 * Return the Direct6.4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.4 value
	 * @throws TrickException
	 */
	@Column(name = "`dtDirect6.4Cat`", nullable = false)
	public int getDirect64() throws TrickException {
		return getCategoryValue("Direct6.4");
	}

	/**
	 * setDirect64: <br>
	 * Sets the "Direct6.4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect64(int value) throws TrickException {
		setCategoryValue("Direct6.4", value);
	}

	/**
	 * getDirect7: <br>
	 * Return the Direct7 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct7 value
	 * @throws TrickException
	 */
	@Column(name = "dtDirect7Cat", nullable = false)
	public int getDirect7() throws TrickException {
		return getCategoryValue("Direct7");
	}

	/**
	 * setDirect7: <br>
	 * Sets the "Direct7" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setDirect7(int value) throws TrickException {
		setCategoryValue("Direct7", value);
	}

	/**
	 * getIndirect1: <br>
	 * Return the Indirect1 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect1 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect1Cat", nullable = false)
	public int getIndirect1() throws TrickException {
		return getCategoryValue("Indirect1");
	}

	/**
	 * setIndirect1: <br>
	 * Sets the "Indirect1" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect1(int value) throws TrickException {
		setCategoryValue("Indirect1", value);
	}

	/**
	 * getIndirect2: <br>
	 * Return the Indirect2 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect2 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect2Cat", nullable = false)
	public int getIndirect2() throws TrickException {
		return getCategoryValue("Indirect2");
	}

	/**
	 * setIndirect2: <br>
	 * Sets the "Indirect2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect2(int value) throws TrickException {
		setCategoryValue("Indirect2", value);
	}

	/**
	 * getIndirect3: <br>
	 * Return the Indirect3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect3 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect3Cat", nullable = false)
	public int getIndirect3() throws TrickException {
		return getCategoryValue("Indirect3");
	}

	/**
	 * setIndirect3: <br>
	 * Sets the "Indirect3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect3(int value) throws TrickException {
		setCategoryValue("Indirect3", value);
	}

	/**
	 * getIndirect4: <br>
	 * Return the Indirect4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect4 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect4Cat", nullable = false)
	public int getIndirect4() throws TrickException {
		return getCategoryValue("Indirect4");
	}

	/**
	 * setIndirect4: <br>
	 * Sets the "Indirect4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect4(int value) throws TrickException {
		setCategoryValue("Indirect4", value);
	}

	/**
	 * getIndirect5: <br>
	 * Return the Indirect5 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect5 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect5Cat", nullable = false)
	public int getIndirect5() throws TrickException {
		return getCategoryValue("Indirect5");
	}

	/**
	 * setIndirect5: <br>
	 * Sets the "Indirect5" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect5(int value) throws TrickException {
		setCategoryValue("Indirect5", value);
	}

	/**
	 * getIndirect6: <br>
	 * Return the Indirect6 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect6 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect6Cat", nullable = false)
	public int getIndirect6() throws TrickException {
		return getCategoryValue("Indirect6");
	}

	/**
	 * setIndirect6: <br>
	 * Sets the "Indirect6" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect6(int value) throws TrickException {
		setCategoryValue("Indirect6", value);
	}

	/**
	 * getIndirect7: <br>
	 * Return the Indirect7 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect7 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect7Cat", nullable = false)
	public int getIndirect7() throws TrickException {
		return getCategoryValue("Indirect7");
	}

	/**
	 * setIndirect7: <br>
	 * Sets the "Indirect7" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect7(int value) throws TrickException {
		setCategoryValue("Indirect7", value);
	}

	/**
	 * getIndirect8: <br>
	 * Return the Indirect8 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect8Cat", nullable = false)
	public int getIndirect8() throws TrickException {
		return getCategoryValue("Indirect8");
	}

	/**
	 * setIndirect8: <br>
	 * Sets the "Indirect8" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect8(int value) throws TrickException {
		setCategoryValue("Indirect8", value);
	}

	/**
	 * getIndirect81: <br>
	 * Return the Indirect8.1 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.1 value
	 * @throws TrickException
	 */
	@Column(name = "`dtIndirect8.1Cat`", nullable = false)
	public int getIndirect81() throws TrickException {
		return getCategoryValue("Indirect8.1");
	}

	/**
	 * setIndirect81: <br>
	 * Sets the "Indirect8.1" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect81(int value) throws TrickException {
		setCategoryValue("Indirect8.1", value);
	}

	/**
	 * getIndirect82: <br>
	 * Return the Indirect8.2 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.2 value
	 * @throws TrickException
	 */
	@Column(name = "`dtIndirect8.2Cat`", nullable = false)
	public int getIndirect82() throws TrickException {
		return getCategoryValue("Indirect8.2");
	}

	/**
	 * setIndirect82: <br>
	 * Sets the "Indirect8.2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect82(int value) throws TrickException {
		setCategoryValue("Indirect8.2", value);
	}

	/**
	 * getIndirect83: <br>
	 * Return the Indirect8.3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.3 value
	 * @throws TrickException
	 */
	@Column(name = "`dtIndirect8.3Cat`", nullable = false)
	public int getIndirect83() throws TrickException {
		return getCategoryValue("Indirect8.3");
	}

	/**
	 * setIndirect83: <br>
	 * Sets the "Indirect8.3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect83(int value) throws TrickException {
		setCategoryValue("Indirect8.3", value);
	}

	/**
	 * getIndirect84: <br>
	 * Return the Indirect8.4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.4 value
	 * @throws TrickException
	 */
	@Column(name = "`dtIndirect8.4Cat`", nullable = false)
	public int getIndirect84() throws TrickException {
		return getCategoryValue("Indirect8.4");
	}

	/**
	 * setIndirect84: <br>
	 * Sets the "Indirect8.4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect84(int value) throws TrickException {
		setCategoryValue("Indirect8.4", value);
	}

	/**
	 * getIndirect9: <br>
	 * Return the Indirect9 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect9 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect9Cat", nullable = false)
	public int getIndirect9() throws TrickException {
		return getCategoryValue("Indirect9");
	}

	/**
	 * setIndirect9: <br>
	 * Sets the "Indirect9" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect9(int value) throws TrickException {
		setCategoryValue("Indirect9", value);
	}

	/**
	 * getIndirect10: <br>
	 * Return the Indirect10 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect10 value
	 * @throws TrickException
	 */
	@Column(name = "dtIndirect10Cat", nullable = false)
	public int getIndirect10() throws TrickException {
		return getCategoryValue("Indirect10");
	}

	/**
	 * setIndirect10: <br>
	 * Sets the "Indirect10" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIndirect10(int value) throws TrickException {
		setCategoryValue("Indirect10", value);
	}

	/**
	 * getConfidentiality: <br>
	 * Return the Confidentiality value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Confidentifality Value
	 * @throws TrickException
	 */
	@Column(name = "dtConfidentialityCat", nullable = false)
	public int getConfidentiality() throws TrickException {
		return getCategoryValue("Confidentiality");
	}

	/**
	 * setConfidentiality: <br>
	 * Sets the "Confidentiality" value. Uses
	 * {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setConfidentiality(int value) throws TrickException {
		setCategoryValue("Confidentiality", value);
	}

	/**
	 * getIntegrity: <br>
	 * Return the Integrity value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Integrity Value
	 * @throws TrickException
	 */
	@Column(name = "dtIntegrityCat", nullable = false)
	public int getIntegrity() throws TrickException {
		return getCategoryValue("Integrity");
	}

	/**
	 * setIntegrity: <br>
	 * Sets the "Integrity" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 * @throws TrickException
	 */
	public void setIntegrity(int value) throws TrickException {
		setCategoryValue("Integrity", value);
	}

	/**
	 * getAvailability: <br>
	 * Return the Availability value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Availability value
	 * @throws TrickException
	 */
	@Column(name = "dtAvailabilityCat", nullable = false)
	public int getAvailability() throws TrickException {
		return getCategoryValue("Availability");
	}

	/**
	 * setAvailability: <br>
	 * Sets the "Availability" value. Uses
	 * {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set the Availability
	 * @throws TrickException
	 */
	public void setAvailability(int value) throws TrickException {
		setCategoryValue("Availability", value);
	}
	
	/**
	 * getExploitability: <br>
	 * Return the Availability value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Exploitability value
	 */
	@Column(name = "dtExploitabilityCat", nullable = false)
	public int getExploitability(){
		return getCategoryValue("Exploitability");
	}
	
	/**
	 * setExploitability: <br>
	 * Sets the "Exploitability" value. Uses
	 * {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set the Exploitability
	 * @throws TrickException
	 */
	public void setExploitability(int value) {
		setCategoryValue("Exploitability", value);
	}
	
	/**
	 * getReliability: <br>
	 * Return the Availability value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Reliability value
	 */
	@Column(name = "dtReliabilityCat", nullable = false)
	public int getReliability(){
		return getCategoryValue("Reliability");
	}
	
	/**
	 * setExploitability: <br>
	 * Sets the "Reliability" value. Uses
	 * {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set the Reliability
	 * @throws TrickException
	 */
	public void setReliability(int value) {
		setCategoryValue("Reliability", value);
	}

	/**
	 * getPreventive: <br>
	 * Returns the "preventive" field value
	 * 
	 * @return The Preventive Value
	 */
	@Column(name = "dtPreventiveType", nullable = false)
	public double getPreventive() {
		return preventive;
	}

	/**
	 * setPreventive: <br>
	 * Sets the "preventive" field with a value
	 * 
	 * @param preventive
	 *            The value to set the Preventive
	 * @throws TrickException
	 */
	public void setPreventive(double preventive) throws TrickException {
		this.preventive = preventive;
	}

	/**
	 * getDetective: <br>
	 * Returns the "detective" field value
	 * 
	 * @return The Detective Value
	 */
	@Column(name = "dtDetectiveType", nullable = false)
	public double getDetective() {
		return detective;
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *            The value to set the Detective
	 * @throws TrickException
	 */
	public void setDetective(double detective) throws TrickException {
		this.detective = detective;
	}

	/**
	 * getLimitative: <br>
	 * Returns the "limitative" field value
	 * 
	 * @return The Limitative Value
	 */
	@Column(name = "dtLimitativeType", nullable = false)
	public double getLimitative() {
		return limitative;
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *            The value to set the Limitative
	 * @throws TrickException
	 */
	public void setLimitative(double limitative) throws TrickException {
		this.limitative = limitative;
	}

	/**
	 * getCorrective: <br>
	 * Returns the "corrective" field value
	 * 
	 * @return The Corrective Value
	 */
	@Column(name = "dtCorrectiveType", nullable = false)
	public double getCorrective() {
		return corrective;
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *            The value to set the Corrective
	 * @throws TrickException
	 */
	public void setCorrective(double corrective) throws TrickException {
		this.corrective = corrective;
	}

	/**
	 * getIntentional: <br>
	 * Returns the "intentional" field value
	 * 
	 * @return The Intentional Value
	 */
	@Column(name = "dtIntentionalSource", nullable = false)
	public int getIntentional() {
		return intentional;
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *            The value to set the Intentional
	 * @throws TrickException
	 */
	public void setIntentional(int intentional) throws TrickException {
		this.intentional = intentional;
	}

	/**
	 * getAccidental: <br>
	 * Returns the "accidental" field value
	 * 
	 * @return The Accidental Value
	 */
	@Column(name = "dtAccidentalSource", nullable = false)
	public int getAccidental() {
		return accidental;
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *            The value to set the Accidental
	 * @throws TrickException
	 */
	public void setAccidental(int accidental) throws TrickException {
		this.accidental = accidental;
	}

	/**
	 * getEnvironmental: <br>
	 * Returns the "environmental" field value
	 * 
	 * @return The Environmental Value
	 */
	@Column(name = "dtEnvironmentalSource", nullable = false)
	public int getEnvironmental() {
		return environmental;
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *            The value to set the Environmental
	 * @throws TrickException
	 */
	public void setEnvironmental(int environmental) throws TrickException {
		this.environmental = environmental;
	}

	/**
	 * getInternalthreat: <br>
	 * Returns the "internalthreat" field value
	 * 
	 * @return The Internal Threat
	 */
	@Column(name = "dtInternalThreatSource", nullable = false)
	public int getInternalThreat() {
		return internalThreat;
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *            The value to set the Internal Threat
	 * @throws TrickException
	 */
	public void setInternalThreat(int internalthreat) throws TrickException {
		this.internalThreat = internalthreat;
	}

	/**
	 * getExternalthreat: <br>
	 * Returns the "externalthreat" field value
	 * 
	 * @return The External Threat
	 */
	@Column(name = "dtExternalThreatSource", nullable = false)
	public int getExternalThreat() {
		return externalThreat;
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *            The value to set the External Threat
	 * @throws TrickException
	 */
	public void setExternalThreat(int externalthreat) throws TrickException {
		this.externalThreat = externalthreat;
	}

	/**
	 * isCategoryKey: <br>
	 * Checks if a given key is a valid Risk Category.
	 * 
	 * @param category
	 *            The Category to check
	 * 
	 * @return True if Key is valid; False if Key is not valid
	 */
	public static boolean isCategoryKey(String category) {
		return category != null && CATEGOTY_PATTERN.matcher(category).find();
	}

	/**
	 * isValidValue: <br>
	 * Checks if a given Value is valid. Will be overriden inside inherited
	 * classes (Scenario and MeasureProperties).
	 * 
	 * @param value
	 *            The Value to check
	 * @return True if Value is valid; False if Value is not valid
	 */
	protected abstract boolean isValidValue(int value);

	/**
	 * valueFixer: <br>
	 * Description
	 * 
	 * @param category
	 * @param value
	 * @return
	 * @throws TrickException
	 */
	protected abstract int valueFixer(String category, int value) throws TrickException;

	/**
	 * hasCSSFInfluence: <br>
	 * Check if this object has CSSF Categories that are influenced. (CSSF
	 * Category Value is > 0).
	 * 
	 * @return True if there is at least 1 CSSF category influenced; False if
	 *         none are influenced
	 * @throws TrickException
	 */
	@Transient
	public boolean hasCSSFInfluence() throws TrickException {

		// ***********************************************************************
		// * Retrieve valid CSSF Category Keys
		// ***********************************************************************
		String[] keys = getCSSFCategoryKeys();

		// ***********************************************************************
		// * Check each Category if it influences and return the value
		// ***********************************************************************

		// parse all keys
		for (String category : keys) {

			// retrieve value for this key or add new key if it does not exist,
			// and check if
			// value > 0
			if (getCategoryValue(category) != 0) {

				// return true -> CSSF Category is influenced
				return true;
			}
		}

		// return false -> No CSSF Category is influenced
		return false;
	}

	/**
	 * hasCIAInfluence: <br>
	 * Check if Categories Confidentiality, Integrity or Availability are
	 * influenced (Category Value > 0).
	 * 
	 * @return True if at least 1 Category is influenced; False if none is
	 *         influenced
	 * @throws TrickException
	 */
	@Transient
	public boolean hasCIAInfluence() throws TrickException {

		// ***********************************************************************
		// * Retrieve valid CIA Category Keys
		// ***********************************************************************
		String[] keys = getCIACategoryKeys();

		// ***********************************************************************
		// * Check each Category if it influences and return the value
		// ***********************************************************************

		// parse all keys
		for (String category : keys) {

			// retrieve value for this key or add new key if it does not exist,
			// and check if
			// value > 0
			if (getCategoryValue(category) != 0) {

				// return true -> CIA category is influenced
				return true;
			}
		}

		// return false -> No CIA Category is influenced
		return false;
	}

	/**
	 * hasInfluenceOnAllCategories: <br>
	 * Checks if Categories of CIA or CSSF are influenced.
	 * 
	 * @return True if CIA or CSSF Categories are influenced. Returns False if
	 *         none of both Categories are influenced.
	 * @throws TrickException
	 */
	@Transient
	public boolean hasInfluenceOnAllCategories() throws TrickException {

		// ***********************************************************************
		// * Check if this object has influence on CIA or CSSF Categories
		// ***********************************************************************
		return hasCIAInfluence() || hasCSSFInfluence();
	}

	/**
	 * hasInfluenceOnCategory: <br>
	 * Checks if given Category exists and is valid, then checks if it is
	 * influenced. (Category Value > 0)
	 * 
	 * @return True if the given category is influenced. Returns False if it is
	 *         not influenced.
	 * @throws TrickException
	 */
	@Transient
	public boolean hasInfluenceOnCategory(String key) throws TrickException {
		return (categories.containsKey(key) || isCategoryKey(key)) && getCategoryValue(key) != 0;
	}

	/**
	 * getCategoryValue: <br>
	 * Checks if the given Category Key is valid, then checks if the Key exists
	 * inside the Map. If the Key exists: the value is returned, if the Key does
	 * not exist, it will be added with the default value 0, which will be
	 * returned.
	 * 
	 * @param category
	 *            The Key that represents the category
	 * @return The value for the key
	 * @throws TrickException
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Transient
	public int getCategoryValue(String category) throws TrickException {
		// check if the category key is valid -> NO
		if (!(categories.containsKey(category) || isCategoryKey(category)))
			throw new TrickException("error.security_criteria.category.invalid", String.format("'%s' is not valid!"), category);

		Integer value = categories.get(category);

		// check if category key exists in MAP -> NO
		if (value == null) {
			// check if the category key is valid -> NO
			if (!isCategoryKey(category))
				throw new TrickException("error.security_criteria.category.invalid", String.format("'%s' is not valid!"), category);

			// add category with default value 0 to MAP
			categories.put(category, value = 0);
		}

		// return value of Category (At this moment, the Key is valid and
		// already exists in MAP)
		return value;
	}

	/**
	 * setCategoryValue: <br>
	 * Set the value of a key given by parameters. A check will be performed to
	 * have only valid keys and valid values.
	 * 
	 * @param category
	 *            The Key that represents the Category
	 * @param value
	 *            The value to set the Category
	 * @throws TrickException
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public void setCategoryValue(String category, int value) throws TrickException {

		// ***********************************************************************
		// * Check if Category is valid
		// ***********************************************************************
		if (!(categories.containsKey(category) || isCategoryKey(category)))
			throw new TrickException("error.security_criteria.category.invalid", String.format("'%s' is not valid!", category), category);

		// ***********************************************************************
		// * Check if Value is valid
		// ***********************************************************************
		// else if (!isValidValue(value))
		// throw new TrickException("error.security_criteria.category.invalid",
		// String.format("'%s' is not valid!"), category);

		// ***********************************************************************
		// * Add valid value to the valid Category
		// ***********************************************************************
		categories.put(category, valueFixer(category, value));

	}

	/**
	 * getAllCategories: <br>
	 * Description
	 * 
	 * @return
	 */
	@Transient
	public Map<String, Integer> getCategories() {
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (String category : categories.keySet())
			result.put(category, categories.get(category));
		return result;
	}

	/**
	 * getAllCategories: <br>
	 * Description
	 * 
	 * @return
	 */
	@Transient
	public Map<String, Integer> getAllCategories() {
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (String category : CategoryConverter.JAVAKEYS)
			result.put(category, categories.get(category));
		return result;
	}

	/**
	 * getCIACategories: <br>
	 * Description
	 * 
	 * @return
	 */
	@Transient
	public Map<String, Integer> getCIACategories() {
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (String category : CategoryConverter.TYPE_CIA_KEYS)
			result.put(category, categories.get(category));
		return result;
	}

	/**
	 * getCSSFCategories: <br>
	 * Description
	 * 
	 * @return
	 */
	@Transient
	public Map<String, Integer> getCSSFCategories() {
		LinkedHashMap<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (String category : CategoryConverter.TYPE_CSSF_KEYS)
			result.put(category, categories.get(category));
		return result;
	}

	/**
	 * getGenericCategoryKeys:<br>
	 * Returns a String[] (array) with confidentiality, integrity and
	 * availability categories.
	 * 
	 * @return Array of Scenario Confidentiality, Integrity and Availability
	 *         Categories
	 */
	public static final String[] getCIACategoryKeys() {
		return CategoryConverter.TYPE_CIA_KEYS;
	}

	/**
	 * getCSSFCategoryKeys:<br>
	 * Returns a String[] (array) witha all CSSF valid categories.
	 * 
	 * @return Array of all Scenario CSSF Categories
	 */
	public static final String[] getCSSFCategoryKeys() {
		return CategoryConverter.TYPE_CSSF_KEYS;
	}

	/**
	 * getCategoryKeys:<br>
	 * Returns a String[] (array) witha all categories.
	 * 
	 * @return Array of all Scenario Categories
	 */
	public static final String[] getCategoryKeys() {
		return CategoryConverter.JAVAKEYS;
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SecurityCriteria clone() throws CloneNotSupportedException {
		SecurityCriteria securityCriteria = (SecurityCriteria) super.clone();
		securityCriteria.categories = new LinkedHashMap<String, Integer>();
		for (String key : categories.keySet())
			securityCriteria.categories.put(key, categories.get(key));
		return securityCriteria;
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public SecurityCriteria duplicate() throws CloneNotSupportedException {
		SecurityCriteria securityCriteria = (SecurityCriteria) super.clone();
		securityCriteria.categories = new LinkedHashMap<String, Integer>();
		for (String key : categories.keySet())
			securityCriteria.categories.put(key, categories.get(key));
		securityCriteria.id = -1;
		return securityCriteria;
	}

	/**
	 * copyTo: <br>
	 * Description
	 * 
	 * @param securityCriteria
	 */
	public void copyTo(SecurityCriteria securityCriteria) {
		if (securityCriteria == null)
			return;
		/** The Preventive value */
		securityCriteria.preventive = preventive;
		/** The Detective value */
		securityCriteria.detective = detective;
		/** The Limitative value */
		securityCriteria.limitative = limitative;
		/** The Corrective value */
		securityCriteria.corrective = corrective;
		/** The Intentional value */
		securityCriteria.intentional = intentional;
		/** The Accidental value */
		securityCriteria.accidental = accidental;
		/** The Environmental value */
		securityCriteria.environmental = environmental;
		/** The Internal Threat value */
		securityCriteria.internalThreat = internalThreat;
		/** The External Threat value */
		securityCriteria.externalThreat = externalThreat;
		for (String category : getCategoryKeys())
			securityCriteria.categories.put(category, categories.get(category));

	}

}