package lu.itrust.business.TS;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lu.itrust.business.TS.cssf.tools.CategoryConverter;
import lu.itrust.business.TS.tsconstant.Constant;

/**
 * SecurityCriteria: <br>
 * This class represents SecurityCriteria which are properties of either a Measure or a Scenario.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-11-26
 */
public abstract class SecurityCriteria implements Serializable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

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
	private Map<String, Integer> categories = new HashMap<String, Integer>(25);

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
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the "id" field with a value
	 * 
	 * @param id
	 *            The value to set the
	 */
	public void setId(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("ID needs to be >= 1!");
		}
		this.id = id;
	}

	/**
	 * getDirect1: <br>
	 * Returns the "Direct1" value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The "Direct1" value
	 */
	public int getDirect1() {
		return getCategoryValue("Direct1");
	}

	/**
	 * setDirect1: <br>
	 * Sets the "Direct1" with a value. Uses {@link #setCategoryValue(String, int)}.
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setDirect1(int value) {
		setCategoryValue("Direct1", value);
	}

	/**
	 * getDirect2: <br>
	 * Returns the "Direct2" value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The "Direct2" value
	 */
	public int getDirect2() {
		return getCategoryValue("Direct2");
	}

	/**
	 * setDirect2: <br>
	 * Sets the "Direct2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect2(int value) {
		setCategoryValue("Direct2", value);
	}

	/**
	 * getDirect3: <br>
	 * Return the Direct3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct3 value
	 */
	public int getDirect3() {
		return getCategoryValue("Direct3");
	}

	/**
	 * setDirect3: <br>
	 * Sets the "Direct3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect3(int value) {
		setCategoryValue("Direct3", value);
	}

	/**
	 * getDirect4: <br>
	 * Return the Direct4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct4 value
	 */
	public int getDirect4() {
		return getCategoryValue("Direct4");
	}

	/**
	 * setDirect4: <br>
	 * Sets the "Direct4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect4(int value) {
		setCategoryValue("Direct4", value);
	}

	/**
	 * getDirect5: <br>
	 * Return the Direct5 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct5 value
	 */
	public int getDirect5() {
		return getCategoryValue("Direct5");
	}

	/**
	 * setDirect5: <br>
	 * Sets the "Direct5" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect5(int value) {
		setCategoryValue("Direct5", value);
	}

	/**
	 * getDirect6: <br>
	 * Return the Direct6 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6 value
	 */
	public int getDirect6() {
		return getCategoryValue("Direct6");
	}

	/**
	 * setDirect6: <br>
	 * Sets the "Direct6" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect6(int value) {
		setCategoryValue("Direct6", value);
	}

	/**
	 * getDirect61: <br>
	 * Return the Direct6.1 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.1 value
	 */
	public int getDirect61() {
		return getCategoryValue("Direct6.1");
	}

	/**
	 * setDirect61: <br>
	 * Sets the "Direct6.1" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect61(int value) {
		setCategoryValue("Direct6.1", value);
	}

	/**
	 * getDirect62: <br>
	 * Return the Direct6.2 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.2 value
	 */
	public int getDirect62() {
		return getCategoryValue("Direct6.2");
	}

	/**
	 * setDirect62: <br>
	 * Sets the "Direct6.2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect62(int value) {
		setCategoryValue("Direct6.2", value);
	}

	/**
	 * getDirect63: <br>
	 * Return the Direct6.3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.3 value
	 */
	public int getDirect63() {
		return getCategoryValue("Direct6.3");
	}

	/**
	 * setDirect63: <br>
	 * Sets the "Direct6.3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect63(int value) {
		setCategoryValue("Direct6.3", value);
	}

	/**
	 * getDirect64: <br>
	 * Return the Direct6.4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct6.4 value
	 */
	public int getDirect64() {
		return getCategoryValue("Direct6.4");
	}

	/**
	 * setDirect64: <br>
	 * Sets the "Direct6.4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect64(int value) {
		setCategoryValue("Direct6.4", value);
	}

	/**
	 * getDirect7: <br>
	 * Return the Direct7 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Direct7 value
	 */
	public int getDirect7() {
		return getCategoryValue("Direct7");
	}

	/**
	 * setDirect7: <br>
	 * Sets the "Direct7" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setDirect7(int value) {
		setCategoryValue("Direct7", value);
	}

	/**
	 * getIndirect1: <br>
	 * Return the Indirect1 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect1 value
	 */
	public int getIndirect1() {
		return getCategoryValue("Indirect1");
	}

	/**
	 * setIndirect1: <br>
	 * Sets the "Indirect1" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect1(int value) {
		setCategoryValue("Indirect1", value);
	}

	/**
	 * getIndirect2: <br>
	 * Return the Indirect2 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect2 value
	 */
	public int getIndirect2() {
		return getCategoryValue("Indirect2");
	}

	/**
	 * setIndirect2: <br>
	 * Sets the "Indirect2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect2(int value) {
		setCategoryValue("Indirect2", value);
	}

	/**
	 * getIndirect3: <br>
	 * Return the Indirect3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect3 value
	 */
	public int getIndirect3() {
		return getCategoryValue("Indirect3");
	}

	/**
	 * setIndirect3: <br>
	 * Sets the "Indirect3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect3(int value) {
		setCategoryValue("Indirect3", value);
	}

	/**
	 * getIndirect4: <br>
	 * Return the Indirect4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect4 value
	 */
	public int getIndirect4() {
		return getCategoryValue("Indirect4");
	}

	/**
	 * setIndirect4: <br>
	 * Sets the "Indirect4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect4(int value) {
		setCategoryValue("Indirect4", value);
	}

	/**
	 * getIndirect5: <br>
	 * Return the Indirect5 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect5 value
	 */
	public int getIndirect5() {
		return getCategoryValue("Indirect5");
	}

	/**
	 * setIndirect5: <br>
	 * Sets the "Indirect5" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect5(int value) {
		setCategoryValue("Indirect5", value);
	}

	/**
	 * getIndirect6: <br>
	 * Return the Indirect6 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect6 value
	 */
	public int getIndirect6() {
		return getCategoryValue("Indirect6");
	}

	/**
	 * setIndirect6: <br>
	 * Sets the "Indirect6" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect6(int value) {
		setCategoryValue("Indirect6", value);
	}

	/**
	 * getIndirect7: <br>
	 * Return the Indirect7 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect7 value
	 */
	public int getIndirect7() {
		return getCategoryValue("Indirect7");
	}

	/**
	 * setIndirect7: <br>
	 * Sets the "Indirect7" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect7(int value) {
		setCategoryValue("Indirect7", value);
	}

	/**
	 * getIndirect8: <br>
	 * Return the Indirect8 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8 value
	 */
	public int getIndirect8() {
		return getCategoryValue("Indirect8");
	}

	/**
	 * setIndirect8: <br>
	 * Sets the "Indirect8" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect8(int value) {
		setCategoryValue("Indirect8", value);
	}

	/**
	 * getIndirect81: <br>
	 * Return the Indirect8.1 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.1 value
	 */
	public int getIndirect81() {
		return getCategoryValue("Indirect8.1");
	}

	/**
	 * setIndirect81: <br>
	 * Sets the "Indirect8.1" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect81(int value) {
		setCategoryValue("Indirect8.1", value);
	}

	/**
	 * getIndirect82: <br>
	 * Return the Indirect8.2 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.2 value
	 */
	public int getIndirect82() {
		return getCategoryValue("Indirect8.2");
	}

	/**
	 * setIndirect82: <br>
	 * Sets the "Indirect8.2" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect82(int value) {
		setCategoryValue("Indirect8.2", value);
	}

	/**
	 * getIndirect83: <br>
	 * Return the Indirect8.3 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.3 value
	 */
	public int getIndirect83() {
		return getCategoryValue("Indirect8.3");
	}

	/**
	 * setIndirect83: <br>
	 * Sets the "Indirect8.3" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect83(int value) {
		setCategoryValue("Indirect8.3", value);
	}

	/**
	 * getIndirect84: <br>
	 * Return the Indirect8.4 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect8.4 value
	 */
	public int getIndirect84() {
		return getCategoryValue("Indirect8.4");
	}

	/**
	 * setIndirect84: <br>
	 * Sets the "Indirect8.4" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect84(int value) {
		setCategoryValue("Indirect8.4", value);
	}

	/**
	 * getIndirect9: <br>
	 * Return the Indirect9 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect9 value
	 */
	public int getIndirect9() {
		return getCategoryValue("Indirect9");
	}

	/**
	 * setIndirect9: <br>
	 * Sets the "Indirect9" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect9(int value) {
		setCategoryValue("Indirect9", value);
	}

	/**
	 * getIndirect10: <br>
	 * Return the Indirect10 value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Indirect10 value
	 */
	public int getIndirect10() {
		return getCategoryValue("Indirect10");
	}

	/**
	 * setIndirect10: <br>
	 * Sets the "Indirect10" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIndirect10(int value) {
		setCategoryValue("Indirect10", value);
	}

	/**
	 * getConfidentiality: <br>
	 * Return the Confidentiality value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Confidentifality Value
	 */
	public int getConfidentiality() {
		return getCategoryValue("Confidentiality");
	}

	/**
	 * setConfidentiality: <br>
	 * Sets the "Confidentiality" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setConfidentiality(int value) {
		setCategoryValue("Confidentiality", value);
	}

	/**
	 * getIntegrity: <br>
	 * Return the Integrity value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Integrity Value
	 */
	public int getIntegrity() {
		return getCategoryValue("Integrity");
	}

	/**
	 * setIntegrity: <br>
	 * Sets the "Integrity" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set
	 */
	public void setIntegrity(int value) {
		setCategoryValue("Integrity", value);
	}

	/**
	 * getAvailability: <br>
	 * Return the Availability value. Uses {@link #getCategoryValue(String)}
	 * 
	 * @return The Availability value
	 */
	public int getAvailability() {
		return getCategoryValue("Availability");
	}

	/**
	 * setAvailability: <br>
	 * Sets the "Availability" value. Uses {@link #setCategoryValue(String, int)}
	 * 
	 * @param value
	 *            Value to set the Availability
	 */
	public void setAvailability(int value) {
		setCategoryValue("Availability", value);
	}

	/**
	 * getPreventive: <br>
	 * Returns the "preventive" field value
	 * 
	 * @return The Preventive Value
	 */
	public double getPreventive() {
		return preventive;
	}

	/**
	 * setPreventive: <br>
	 * Sets the "preventive" field with a value
	 * 
	 * @param preventive
	 *            The value to set the Preventive
	 */
	public void setPreventive(double preventive) {
		this.preventive = preventive;
	}

	/**
	 * getDetective: <br>
	 * Returns the "detective" field value
	 * 
	 * @return The Detective Value
	 */
	public double getDetective() {
		return detective;
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *            The value to set the Detective
	 */
	public void setDetective(double detective) {
		this.detective = detective;
	}

	/**
	 * getLimitative: <br>
	 * Returns the "limitative" field value
	 * 
	 * @return The Limitative Value
	 */
	public double getLimitative() {
		return limitative;
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *            The value to set the Limitative
	 */
	public void setLimitative(double limitative) {
		this.limitative = limitative;
	}

	/**
	 * getCorrective: <br>
	 * Returns the "corrective" field value
	 * 
	 * @return The Corrective Value
	 */
	public double getCorrective() {
		return corrective;
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *            The value to set the Corrective
	 */
	public void setCorrective(double corrective) {
		this.corrective = corrective;
	}

	/**
	 * getIntentional: <br>
	 * Returns the "intentional" field value
	 * 
	 * @return The Intentional Value
	 */
	public int getIntentional() {
		return intentional;
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *            The value to set the Intentional
	 */
	public void setIntentional(int intentional) {
		this.intentional = intentional;
	}

	/**
	 * getAccidental: <br>
	 * Returns the "accidental" field value
	 * 
	 * @return The Accidental Value
	 */
	public int getAccidental() {
		return accidental;
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *            The value to set the Accidental
	 */
	public void setAccidental(int accidental) {
		this.accidental = accidental;
	}

	/**
	 * getEnvironmental: <br>
	 * Returns the "environmental" field value
	 * 
	 * @return The Environmental Value
	 */
	public int getEnvironmental() {
		return environmental;
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *            The value to set the Environmental
	 */
	public void setEnvironmental(int environmental) {
		this.environmental = environmental;
	}

	/**
	 * getInternalthreat: <br>
	 * Returns the "internalthreat" field value
	 * 
	 * @return The Internal Threat
	 */
	public int getInternalThreat() {
		return internalThreat;
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *            The value to set the Internal Threat
	 */
	public void setInternalThreat(int internalthreat) {
		this.internalThreat = internalthreat;
	}

	/**
	 * getExternalthreat: <br>
	 * Returns the "externalthreat" field value
	 * 
	 * @return The External Threat
	 */
	public int getExternalThreat() {
		return externalThreat;
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *            The value to set the External Threat
	 */
	public void setExternalThreat(int externalthreat) {
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
	public boolean isCategoryKey(String category) {
		return category != null && category.matches(Constant.REGEXP_VALID_SCENARIO_CATEGORY);
	}

	/**
	 * isValidValue: <br>
	 * Checks if a given Value is valid. Will be overriden inside inherited classes (Scenario and
	 * MeasureProperties).
	 * 
	 * @param value
	 *            The Value to check
	 * @return True if Value is valid; False if Value is not valid
	 */
	protected abstract boolean isValidValue(int value);

	/**
	 * hasCSSFInfluence: <br>
	 * Check if this object has CSSF Categories that are influenced. (CSSF Category Value is > 0).
	 * 
	 * @return True if there is at least 1 CSSF category influenced; False if none are influenced
	 */
	public boolean hasCSSFInfluence() {

		// ***********************************************************************
		// * Retrieve valid CSSF Category Keys
		// ***********************************************************************
		String[] keys = getCSSFCategoryKeys();

		// ***********************************************************************
		// * Check each Category if it influences and return the value
		// ***********************************************************************

		// parse all keys
		for (String category : keys) {

			// retrieve value for this key or add new key if it does not exist, and check if
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
	 * Check if Categories Confidentiality, Integrity or Availability are influenced (Category Value
	 * > 0).
	 * 
	 * @return True if at least 1 Category is influenced; False if none is influenced
	 */
	public boolean hasCIAInfluence() {

		// ***********************************************************************
		// * Retrieve valid CIA Category Keys
		// ***********************************************************************
		String[] keys = getCIACategoryKeys();

		// ***********************************************************************
		// * Check each Category if it influences and return the value
		// ***********************************************************************

		// parse all keys
		for (String category : keys) {

			// retrieve value for this key or add new key if it does not exist, and check if
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
	 * @return True if CIA or CSSF Categories are influenced. Returns False if none of both
	 *         Categories are influenced.
	 */
	public boolean hasInfluenceOnAllCategories() {

		// ***********************************************************************
		// * Check if this object has influence on CIA or CSSF Categories
		// ***********************************************************************
		return hasCIAInfluence() || hasCSSFInfluence();
	}

	/**
	 * hasInfluenceOnCategory: <br>
	 * Checks if given Category exists and is valid, then checks if it is influenced. (Category
	 * Value > 0)
	 * 
	 * @return True if the given category is influenced. Returns False if it is not influenced.
	 */
	public boolean hasInfluenceOnCategory(String key) {
		return isCategoryKey(key) && getCategoryValue(key) != 0;
	}

	/**
	 * getCategoryValue: <br>
	 * Checks if the given Category Key is valid, then checks if the Key exists inside the Map. If
	 * the Key exists: the value is returned, if the Key does not exist, it will be added with the
	 * default value 0, which will be returned.
	 * 
	 * @param category
	 *            The Key that represents the category
	 * @return The value for the key
	 * 
	 * @throws IllegalArgumentException
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public int getCategoryValue(String category) {

		// check if the category key is valid -> NO
		if (!isCategoryKey(category)) {
			throw new IllegalArgumentException("Category '" + category + "' is not Valid!");
		}

		// check if category key exists in MAP -> NO
		if (!categories.containsKey(category)) {

			// add category with default value 0 to MAP
			categories.put(category, 0);
		}

		// return value of Category (At this moment, the Key is valid and already exists in MAP)
		return categories.get(category);
	}

	/**
	 * setCategoryValue: <br>
	 * Set the value of a key given by parameters. A check will be performed to have only valid keys
	 * and valid values.
	 * 
	 * @param category
	 *            The Key that represents the Category
	 * @param value
	 *            The value to set the Category
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public void setCategoryValue(String category, int value) {

		// ***********************************************************************
		// * Check if Category is valid
		// ***********************************************************************
		if (!isCategoryKey(category)) {
			throw new IllegalArgumentException("Category '" + category + "' is not Valid!");

			// ***********************************************************************
			// * Check if Value is valid
			// ***********************************************************************
		} else if (!isValidValue(value)) {
			throw new IllegalArgumentException("Category Value '" + value + "' is not Valid!");
		}

		// ***********************************************************************
		// * Add valid value to the valid Category
		// ***********************************************************************
		categories.put(category, value);
	}

	/**
	 * getGenericCategoryKeys:<br>
	 * Returns a String[] (array) with confidentiality, integrity and availability categories.
	 * 
	 * @return Array of Scenario Confidentiality, Integrity and Availability Categories
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
}