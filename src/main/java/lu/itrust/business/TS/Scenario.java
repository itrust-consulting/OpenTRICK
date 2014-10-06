package lu.itrust.business.TS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.exception.TrickException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * Scenario: <br>
 * This class represents a Scenario and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity 
@PrimaryKeyJoinColumn(name="idScenario")
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"fiAnalysis","dtLabel"}))
public class Scenario extends SecurityCriteria {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Scenario Name */
	private String name = "";

	/** The Scenario Type */
	private ScenarioType scenarioType = new ScenarioType();

	/** The Selected Flag (Selected for calculation) */
	private boolean selected = false;

	/** The Scenario Description */
	private String description = "";

	/** List of Asset Type Values */
	private List<AssetTypeValue> assetTypeValues = new ArrayList<AssetTypeValue>();

	/**
	 * Constructor: <br>
	 */
	public Scenario() {

	}

	/**
	 * Constructor: <br>
	 * 
	 * @param assettypes
	 */
	public Scenario(List<AssetType> assettypes) {
		for (AssetType at : assettypes) {
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
	@Column(name="dtLabel", nullable=false)
	public String getName() {
		return name;
	}

	/**
	 * setName: <br>
	 * Sets the "name" field with a value
	 * 
	 * @param name
	 *            The value to set the Scenario Name
	 * @throws TrickException
	 */
	public void setName(String name) throws TrickException {
		if ((name == null) || (name.trim().isEmpty()))
			throw new TrickException("error.scenario.name.empty", "Name cannot be empty!");
		this.name = name;
	}

	/**
	 * getType: <br>
	 * Returns the "type" field value
	 * 
	 * @return The Scenario Type
	 */
	@ManyToOne 
	@JoinColumn(name="fiScenarioType", nullable=false)
	@Access(AccessType.FIELD)
	public ScenarioType getScenarioType() {
		return scenarioType;
	}

	/**
	 * setType: <br>
	 * Sets the "type" field with a value
	 * 
	 * @param type
	 *            The value to set the Scenario Type
	 * @throws TrickException
	 */
	public void setScenarioType(ScenarioType type) throws TrickException {
		if ((type == null) || (type.getName() == null) || (type.getName().trim().isEmpty()))
			throw new TrickException("error.scenario.type.empty", "Type cannot be empty!");
		this.scenarioType = type;
	}

	/**
	 * isSelected: <br>
	 * Returns the "selected" field value
	 * 
	 * @return The Selected Flag
	 */
	@Column(name="dtSelected", nullable=false, columnDefinition="TINYINT(1)")
	public boolean isSelected() {
		return selected;
	}

	/**
	 * setSelected: <br>
	 * Sets the "selected" field with a value
	 * 
	 * @param selected
	 *            The value to set the Selected Flag
	 * @throws TrickException
	 */
	public void setSelected(boolean selected) throws TrickException {
		if (((this.getCorrective() + this.getLimitative() + this.getDetective() + this.getPreventive()) != 1) && (this.getName().isEmpty()) && (selected))
			throw new TrickException("error.scenario.initialisation.early", "Scenario Fields have not been correctly initialised in order to be selected!");
		this.selected = selected;
	}

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The Scenario Description
	 */
	@Column(name="dtDescription", nullable=false, columnDefinition="LONGTEXT")
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
		if (description == null)
			this.description = "";
		else
			this.description = description;
	}

	public void setAssetTypeValue(AssetType assetType, int value) throws TrickException {
		for (AssetTypeValue typeValue : assetTypeValues) {
			if (typeValue.getAssetType().equals(assetType)) {
				typeValue.setValue(value);
				return;
			}
		}
		assetTypeValues.add(new AssetTypeValue(assetType, value));
	}

	@Transient
	public List<AssetTypeValue> deleteAssetTypeDuplication() {
		List<AssetTypeValue> deletedAssetTypeValues = new LinkedList<>();
		Map<AssetType, Boolean> mapping = new LinkedHashMap<>();
		Iterator<AssetTypeValue> iterator = assetTypeValues.iterator();
		while (iterator.hasNext()) {
			AssetTypeValue assetTypeValue = iterator.next();
			if (!mapping.containsKey(assetTypeValue.getAssetType()))
				mapping.put(assetTypeValue.getAssetType(), true);
			else {
				iterator.remove();
				deletedAssetTypeValues.add(assetTypeValue);
			}
		}
		return deletedAssetTypeValues;
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
			if (assettype.equalsIgnoreCase(assetTypeValue.getAssetType().getType()))
				return assetTypeValue.getValue() > 0;
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
	 * @throws TrickException
	 * @see lu.itrust.business.TS.SecurityCriteria#setPreventive(double)
	 */
	@Override
	public void setPreventive(double preventive) throws TrickException {
		if (preventive<0 || preventive >1)
			throw new TrickException("error.scenario.preventive.invalid","Preventive needs to be 0 or 1!");
		super.setPreventive(preventive);
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *            The value to set the Detective
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setDetective(double)
	 */
	@Override
	public void setDetective(double detective) throws TrickException {
		if (detective<0 || detective>1)
			throw new TrickException("error.scenario.detective.invalid","Detective needs to be 0 or 1!");
		super.setDetective(detective);
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *            The value to set the Limitative
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setLimitative(double)
	 */
	@Override
	public void setLimitative(double limitative) throws TrickException {
		if (limitative<0 || limitative > 1) 
			throw new TrickException("error.scenario.limitative.invalid","Limitative needs to be 0 or 1!");
		super.setLimitative(limitative);
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *            The value to set the Corrective
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setCorrective(double)
	 */
	@Override
	public void setCorrective(double corrective) throws TrickException {
		if (corrective<0 || corrective>1)
			throw new TrickException("error.scenario.corrective.invalid","Corrective needs to be 0 or 1!");
		super.setCorrective(corrective);
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *            The value to set the Intentional
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setIntentional(int)
	 */
	@Override
	public void setIntentional(int intentional) throws TrickException {
		if (!isValidValue(intentional))
			throw new TrickException("error.scenario.intentional.invalid","Intentional needs to be between 0 and 4");
		super.setIntentional(intentional);
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *            The value to set the Accidental
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setAccidental(int)
	 */
	@Override
	public void setAccidental(int accidental) throws TrickException {
		if (!isValidValue(accidental))
			throw new TrickException("error.scenario.accidental.invalid","Accidental needs to be between 0 and 4");
		super.setAccidental(accidental);
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *            The value to set the Environmental
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setEnvironmental(int)
	 */
	@Override
	public void setEnvironmental(int environmental) throws TrickException {
		if (!isValidValue(environmental))
			throw new TrickException("error.scenario.environmental.invalid","Environmental needs to be between 0 and 4");
		super.setEnvironmental(environmental);
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *            The value to set the Internal Threat
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setInternalThreat(int)
	 */
	@Override
	public void setInternalThreat(int internalthreat) throws TrickException {
		if (!isValidValue(internalthreat))
			throw new TrickException("error.scenario.internal_threat.invalid","Internal Threat needs to be between 0 and 4");
		super.setInternalThreat(internalthreat);
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *            The value to set the External Threat
	 * @throws TrickException 
	 * @see lu.itrust.business.TS.SecurityCriteria#setExternalThreat(int)
	 */
	@Override
	public void setExternalThreat(int externalthreat) throws TrickException {
		if (!isValidValue(externalthreat))
			throw new TrickException("error.scenario.external_threat.invalid","External Threat needs to be between 0 and 4");
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
		AssetTypeValue typeValue = retrieveAssetTypeValue(assetTypeValue.getAssetType());
		if (typeValue == null)
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

		for (AssetTypeValue atv : assetTypeValues) {
			if (atv.getAssetType().equals(assetType)) {
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
	@ManyToMany
	@JoinTable(name = "ScenarioAssetTypeValue", 
			   joinColumns = { @JoinColumn(name = "idScenario", nullable = false, updatable = false) }, 
			   inverseJoinColumns = { @JoinColumn(name = "idScenarioAssetTypeValue", nullable = false, updatable = false) },
			   uniqueConstraints = @UniqueConstraint(columnNames = {"idScenario", "idScenarioAssetTypeValue"})
	)
	@Cascade({CascadeType.ALL, CascadeType.DELETE})
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
		result = prime * result + ((scenarioType == null) ? 0 : scenarioType.hashCode());
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
		if (scenarioType == null) {
			if (other.scenarioType != null) {
				return false;
			}
		} else if (!scenarioType.equals(other.scenarioType)) {
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
			scenario.addAssetTypeValue(assetTypeValue.clone());
		return scenario;
	}

	public Scenario duplicate() throws CloneNotSupportedException {
		Scenario scenario = (Scenario) super.duplicate();
		scenario.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			scenario.addAssetTypeValue(assetTypeValue.duplicate());
		return scenario;
	}

	@Override
	protected int valueFixer(String category, int value) throws TrickException {
		if (value < 0 || value > 4)
			throw new TrickException("error.security_criteria.category.invalid", String.format("'%s' is not valid!", category), category);
		return value == 0 ? 0 : 4;
	}
}