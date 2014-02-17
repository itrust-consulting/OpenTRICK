package lu.itrust.business.TS;

import java.util.ArrayList;
import java.util.List;

/**
 * Scenario: <br>
 * This class represents a Scenario and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class Scenario extends SecurityCriteria {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Scenario Name */
	private String name = "";

	/** The Scenario Type */
	private ScenarioType type = new ScenarioType();

	/** The Selected Flag (Selected for calculation) */
	private boolean selected = false;

	/** The Scenario Description */
	private String description = "";

	/** List of Asset Type Values */
	private List<AssetTypeValue> assetTypeValues = new ArrayList<AssetTypeValue>();

	/**
	 * Constructor: <br>
	 */
	public Scenario(){
		
	}
	
	/**
	 * Constructor: <br>
	 * @param assettypes
	 */
	public Scenario(List<AssetType> assettypes){
		for(AssetType at : assettypes){
			AssetTypeValue tmpATV = new AssetTypeValue(at, 0);
			assetTypeValues.add(tmpATV);
		}
	}
	
	/***********************************************************************************************
	 * Setters and Getters
	 **********************************************************************************************/

	/**
	 * getName: <br>
	 * Returns the "name" field value
	 * 
	 * @return The Scenario Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * setName: <br>
	 * Sets the "name" field with a value
	 * 
	 * @param name
	 *            The value to set the Scenario Name
	 */
	public void setName(String name) {
		if ((name == null) || (name.trim().isEmpty())) {
			throw new IllegalArgumentException(
					"Scenario Name cannot be null or empty!");
		}
		this.name = name;
	}

	/**
	 * getType: <br>
	 * Returns the "type" field value
	 * 
	 * @return The Scenario Type
	 */
	public ScenarioType getType() {
		return type;
	}

	/**
	 * setType: <br>
	 * Sets the "type" field with a value
	 * 
	 * @param type
	 *            The value to set the Scenario Type
	 */
	public void setType(ScenarioType type) {
		if ((type == null) || (type.getTypeName() == null)
				|| (type.getTypeName().trim().isEmpty())) {
			throw new IllegalArgumentException(
					"Scenario Type cannot be null or empty!");
		}
		this.type = type;
	}

	/**
	 * isSelected: <br>
	 * Returns the "selected" field value
	 * 
	 * @return The Selected Flag
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * setSelected: <br>
	 * Sets the "selected" field with a value
	 * 
	 * @param selected
	 *            The value to set the Selected Flag
	 */
	public void setSelected(boolean selected) {
		if (((this.getCorrective() + this.getLimitative() + this.getDetective() + this
				.getPreventive()) != 1)
				&& (this.getName().isEmpty())
				&& (selected)) {
			throw new IllegalArgumentException(
					"Scenario Fields have not been correctly initialised in order to be selected!");
		}
		this.selected = selected;
	}

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The Scenario Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * setDescription: <br>
	 * Sets the "description" field with a value
	 * 
	 * @param description
	 *            The value to set the Scenario Description
	 */
	public void setDescription(String description) {
		if (description == null) {
			this.description = "";
		} else {
			this.description = description;
		}
	}

	public void setAssetTypeValue(AssetType assetType, int value) {
		for (AssetTypeValue typeValue : assetTypeValues) {
			if (typeValue.getAssetType()
					.equals(assetType)) {
				typeValue.setValue(value);
				return;
			}
		}
		assetTypeValues.add(new AssetTypeValue(assetType, value));
	}

	public int getAssetTypeValue(AssetType assetType) {
		for (AssetTypeValue typeValue : assetTypeValues) {
			if (typeValue.getAssetType().equals(assetType)) {
				return typeValue.getValue();
			}
		}
		assetTypeValues.add(new AssetTypeValue(assetType, 0));
		return 0;
	}

	public boolean hasInfluenceOnAsset(String assettype) {
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			if (assettype.equalsIgnoreCase(assetTypeValue.getAssetType()
					.getType()))
				return assetTypeValue.getValue()>0;
		return false;
	}

	public boolean hasInfluenceOnAsset(AssetType assettype) {
		return hasInfluenceOnAsset(assettype.getType());
	}

	/**
	 * setPreventive: <br>
	 * Sets the "preventive" field with a value
	 * 
	 * @param preventive
	 *            The value to set the Preventive
	 * @see lu.itrust.business.TS.SecurityCriteria#setPreventive(double)
	 */
	@Override
	public void setPreventive(double preventive) {
		if (!(preventive >= 0) || !(preventive <= 1)) {
			throw new IllegalArgumentException("Preventive needs to be 0 or 1!");
		}
		super.setPreventive(preventive);
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *            The value to set the Detective
	 * @see lu.itrust.business.TS.SecurityCriteria#setDetective(double)
	 */
	@Override
	public void setDetective(double detective) {
		if (!(detective >= 0) || !(detective <= 1)) {
			throw new IllegalArgumentException("Detective needs to be 0 or 1!");
		}
		super.setDetective(detective);
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *            The value to set the Limitative
	 * @see lu.itrust.business.TS.SecurityCriteria#setLimitative(double)
	 */
	@Override
	public void setLimitative(double limitative) {
		if (!(limitative >= 0) || !(limitative <= 1)) {
			throw new IllegalArgumentException("Limitative needs to be 0 or 1!");
		}
		super.setLimitative(limitative);
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *            The value to set the Corrective
	 * @see lu.itrust.business.TS.SecurityCriteria#setCorrective(double)
	 */
	@Override
	public void setCorrective(double corrective) {
		if (!(corrective >= 0) || !(corrective <= 1)) {
			throw new IllegalArgumentException("Corrective needs to be 0 or 1!");
		}
		super.setCorrective(corrective);
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *            The value to set the Intentional
	 * @see lu.itrust.business.TS.SecurityCriteria#setIntentional(int)
	 */
	@Override
	public void setIntentional(int intentional) {
		if (!isValidValue(intentional)) {
			throw new IllegalArgumentException(
					"Intentional needs to be 0 or 1!");
		}
		super.setIntentional(intentional);
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *            The value to set the Accidental
	 * @see lu.itrust.business.TS.SecurityCriteria#setAccidental(int)
	 */
	@Override
	public void setAccidental(int accidental) {
		if (!isValidValue(accidental)) {
			throw new IllegalArgumentException("Accidental needs to be 0 or 1!");
		}
		super.setAccidental(accidental);
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *            The value to set the Environmental
	 * @see lu.itrust.business.TS.SecurityCriteria#setEnvironmental(int)
	 */
	@Override
	public void setEnvironmental(int environmental) {
		if (!isValidValue(environmental)) {
			throw new IllegalArgumentException(
					"Environmental needs to be 0 or 1!");
		}
		super.setEnvironmental(environmental);
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *            The value to set the Internal Threat
	 * @see lu.itrust.business.TS.SecurityCriteria#setInternalThreat(int)
	 */
	@Override
	public void setInternalThreat(int internalthreat) {
		if (!isValidValue(internalthreat)) {
			throw new IllegalArgumentException(
					"Internal Threat needs to be 0 or 1!");
		}
		super.setInternalThreat(internalthreat);
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *            The value to set the External Threat
	 * @see lu.itrust.business.TS.SecurityCriteria#setExternalThreat(int)
	 */
	@Override
	public void setExternalThreat(int externalthreat) {
		if (!isValidValue(externalthreat)) {
			throw new IllegalArgumentException(
					"External Threat needs to be 0 or 1!");
		}
		super.setExternalThreat(externalthreat);
	}

	/**
	 * isValidValue: <br>
	 * Check if Category value is valid or not. A valid value in scenario is 0
	 * or 1 or 4.
	 * 
	 * @param value
	 *            The value to check if valid
	 * @see lu.itrust.business.TS.SecurityCriteria#isValidValue(int)
	 */
	@Override
	protected boolean isValidValue(int value) {
		return value >= 0 && value <= 4;
	}

	/**
	 * addAssetTypeValue<br />
	 * Appends the specified element to the end of this list.
	 * 
	 * @param assetTypeValue
	 *            The Object of AssetTypeValue to add to the list
	 */
	public void addAssetTypeValue(AssetTypeValue assetTypeValue) {
		assetTypeValues.add(assetTypeValue);
	}

	/**
	 * addAssetTypeValue<br />
	 * Appends the specified element to the end of this list.
	 * 
	 * @param assetTypeValue
	 *            The Object of AssetTypeValue to add to the list
	 */
	public AssetTypeValue retrieveAssetTypeValue(AssetType assetType) {
		
		AssetTypeValue atvreturn = null;
		
		for (AssetTypeValue atv : assetTypeValues){
			if (atv.getAssetType().equals(assetType)){
				atvreturn = atv;
				break;
			}
		}
		
		return atvreturn;
	}
	
	/**
	 * getAssetTypeValue<br />
	 * Returns the AssetTypeValue at the given position in this list.
	 * 
	 * @param index
	 *            The Position to retrieve the AssetTypeValue from
	 * @return The AssetTypeValue at the requested position
	 */
	public AssetTypeValue getAssetTypeValue(int index) {
		return assetTypeValues.get(index);
	}

	/**
	 * getAssetTypeValueList<br />
	 * Returns the List of AssetTypeValues.
	 * 
	 * @return The List of AssetTypeValues
	 */
	public List<AssetTypeValue> getAssetTypeValues() {
		return assetTypeValues;
	}

	/**
	 * setAssetTypeValues: <br>
	 * Sets the Field "assetTypeValues" with a value.
	 * 
	 * @param assetTypeValues
	 *            The Value to set the assetTypeValues field
	 */
	public void setAssetTypeValues(List<AssetTypeValue> assetTypeValues) {
		this.assetTypeValues = assetTypeValues;
	}

	/**
	 * hashCode: <br>
	 * Used inside equals method to check if object equals another.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getId();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * equals: <br>
	 * This method is used to determine if the current object equals another
	 * object. Fields that identify a Scenario object are: id, name and type.
	 * 
	 * @param obj
	 *            The object to check
	 * @return True if the object equals the other object; False if the objects
	 *         are not the same
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Scenario other = (Scenario) obj;
		if (getId() != other.getId()) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Scenario clone() throws CloneNotSupportedException {
		Scenario scenario = (Scenario) super.clone();
		scenario.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			scenario.assetTypeValues.add(assetTypeValue.clone());
		return scenario;
	}

	public Scenario duplicate() throws CloneNotSupportedException {
		Scenario scenario = (Scenario) super.duplicate();
		scenario.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			scenario.assetTypeValues.add(assetTypeValue.duplicate());
		return scenario;
	}
}