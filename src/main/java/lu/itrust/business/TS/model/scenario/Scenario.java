package lu.itrust.business.TS.model.scenario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.SecurityCriteria;

/**
 * Scenario: <br>
 * This class represents a Scenario and its data.
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@PrimaryKeyJoinColumn(name = "idScenario")
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "fiAnalysis", "dtLabel" }),
		@UniqueConstraint(columnNames = { "fiAnalysis", "dtThreat", "dtVulnerability" }) })
public class Scenario extends SecurityCriteria {

	/**
	 *
	 */
	private static final String ILR_KEY_FORMATING = "-!-!-%s:-:-:%s-!-!-";

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	private boolean assetLinked;

	/** List of Asset Type Values */
	private List<AssetTypeValue> assetTypeValues = new ArrayList<>();

	/** The Scenario Description */
	private String description = "";

	private List<Asset> linkedAssets = new ArrayList<>();

	/** The Scenario Name */
	private String name = "";

	private String threat;

	private String vulnerability;

	/** The Selected Flag (Selected for calculation) */
	private boolean selected = false;

	private ScenarioType type = null;

	/**
	 * Constructor: <br>
	 */
	public Scenario() {
	}

	/***********************************************************************************************
	 * Setters and Getters
	 **********************************************************************************************/

	public Scenario(String name) {
		setName(name);
	}

	/**
	 * addAssetTypeValue<br />
	 * Appends the specified element to the end of this list.
	 * 
	 * @param assetTypeValue
	 *                       The Object of AssetTypeValue to add to the list
	 */
	public void add(AssetTypeValue assetTypeValue) {
		if (!isAssetLinked()) {
			AssetTypeValue typeValue = findByAssetType(assetTypeValue.getAssetType());
			if (typeValue == null)
				getAssetTypeValues().add(assetTypeValue);
		}
	}

	public String assetTypeIds() {
		if (isAssetLinked()) {
			return getLinkedAssets().stream().map(a -> a.getId() + "").distinct().collect(Collectors.joining("|"));
		} else {
			return getAssetTypeValues().stream().filter(a -> a.getValue() > 0).map(a -> a.getAssetType().getId() + "")
					.distinct().collect(Collectors.joining("|"));
		}
	}

	/**
	 * clone: <br>
	 * Description
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Scenario clone() {
		try {
			Scenario scenario = (Scenario) super.clone();
			if (isAssetLinked()) {
				scenario.setAssetTypeValues(new LinkedList<>());
				scenario.setLinkedAssets(
						scenario.getLinkedAssets().stream().map(Asset::clone).collect(Collectors.toList()));
			} else {
				scenario.setLinkedAssets(new LinkedList<>());
				scenario.setAssetTypeValues(
						getAssetTypeValues().stream().map(AssetTypeValue::clone).collect(Collectors.toList()));
			}
			return scenario;
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.scenario", "Scenario cannot be copied");
		}
	}

	/**
	 * deleteAssetTypeDuplication: <br>
	 * Description
	 * 
	 * @return
	 */
	@Transient
	public List<AssetTypeValue> deleteDuplicatedAndUnsed() {
		List<AssetTypeValue> deletedAssetTypeValues = new LinkedList<>();
		if (isAssetLinked()) {
			deletedAssetTypeValues.addAll(getAssetTypeValues());
			getAssetTypeValues().clear();
		} else {
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
			getLinkedAssets().clear();
		}
		return deletedAssetTypeValues;
	}

	/**
	 * duplicate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#duplicate()
	 */
	public Scenario duplicate(Map<Integer, Asset> assets) {
		try {
			Scenario scenario = (Scenario) super.duplicate();
			if (isAssetLinked()) {
				if (assets == null || assets.isEmpty())
					scenario.setLinkedAssets(new ArrayList<>());
				else
					scenario.setLinkedAssets(scenario.getLinkedAssets().stream()
							.filter(asset -> assets.containsKey(asset.getId())).map(asset -> assets.get(asset.getId()))
							.collect(Collectors.toList()));
				scenario.setAssetTypeValues(new LinkedList<>());
			} else {
				scenario.setLinkedAssets(new LinkedList<>());
				scenario.setAssetTypeValues(
						getAssetTypeValues().stream().map(AssetTypeValue::duplicate).collect(Collectors.toList()));
			}
			return scenario;
		} catch (CloneNotSupportedException e) {
			throw new TrickException("error.clone.scenario", "Scenario cannot be copied");
		}
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

		if (getId() > 0 && other.getId() > 0)
			return getId() == other.getId();

		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * addAssetTypeValue<br />
	 * Appends the specified element to the end of this list.
	 * 
	 * @param assetTypeValue
	 *                       The Object of AssetTypeValue to add to the list
	 */
	public AssetTypeValue findByAssetType(AssetType assetType) {
		return getAssetTypeValues().stream().filter(typeValue -> typeValue.getAssetType().equals(assetType)).findAny()
				.orElse(null);
	}

	/**
	 * getAssetTypeValueList<br />
	 * Returns the List of AssetTypeValues.
	 * 
	 * @return The List of AssetTypeValues
	 */
	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "ScenarioAssetTypeValue", joinColumns = {
			@JoinColumn(name = "fiScenario", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "fiAssetTypeValue", nullable = false) }, uniqueConstraints = @UniqueConstraint(columnNames = {
							"fiAssetTypeValue", "fiScenario" }))
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	public List<AssetTypeValue> getAssetTypeValues() {
		return assetTypeValues;
	}

	/**
	 * getDescription: <br>
	 * Returns the "description" field value
	 * 
	 * @return The Scenario Description
	 */
	@Column(name = "dtDescription", nullable = false, length = 16777216)
	public String getDescription() {
		return description;
	}

	/**
	 * @return the linkedAssets
	 */
	@ManyToMany
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "ScenarioLinkedAsset", joinColumns = {
			@JoinColumn(name = "fiScenario", nullable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "fiAsset", nullable = false) }, uniqueConstraints = @UniqueConstraint(columnNames = {
							"fiScenario", "fiAsset" }))
	@Cascade(CascadeType.SAVE_UPDATE)
	public List<Asset> getLinkedAssets() {
		return linkedAssets;
	}

	/**
	 * getName: <br>
	 * Returns the "name" field value
	 * 
	 * @return The Scenario Name
	 */
	@Column(name = "dtLabel", nullable = false)
	public String getName() {
		return name;
	}

	/***
	 * getThreat
	 * 
	 * @return The scenario threat
	 */
	@Column(name = "dtThreat")
	public String getThreat() {
		return threat;
	}

	/***
	 * getVulnerability
	 * 
	 * @return the scenario vulnerability
	 */
	@Column(name = "dtVulnerability")
	public String getVulnerability() {
		return vulnerability;
	}

	/**
	 * getType: <br>
	 * Returns the "type" field value
	 * 
	 * @return The Scenario Type
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "dtType", nullable = false)
	@Access(AccessType.PROPERTY)
	public ScenarioType getType() {
		return type;
	}

	public boolean hasControlCharacteristics() {
		return Math.abs(1 - (getCorrective() + getLimitative() + getPreventive() + getDetective())) < 1e-3;
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
	 * hasInfluenceOnAsset: <br>
	 * Description
	 * 
	 * @param assettype
	 * @return
	 */
	public boolean hasInfluenceOnAsset(Asset asset) {
		return isAssetLinked() ? getLinkedAssets().contains(asset) : hasInfluenceOnAsset(asset.getAssetType());
	}

	/**
	 * hasInfluenceOnAsset: <br>
	 * Description
	 * 
	 * @param assettype
	 * @return
	 */
	public boolean hasInfluenceOnAsset(AssetType assetType) {
		return !isAssetLinked()
				&& getAssetTypeValues().stream()
						.anyMatch(typeValue -> typeValue.getValue() > 0 && assetType.equals(typeValue.getAssetType()));
	}

	public boolean hasThreatSource() {
		return (getAccidental() + getIntentional() + getEnvironmental() + getExternalThreat()
				+ getInternalThreat()) > 0;
	}

	/**
	 * @return the assetLinked
	 */
	@Column(name = "dtAssetLinked")
	public boolean isAssetLinked() {
		return assetLinked;
	}

	/**
	 * isSelected: <br>
	 * Returns the "selected" field value
	 * 
	 * @return The Selected Flag
	 */
	@Column(name = "dtSelected", nullable = false)
	public boolean isSelected() {
		return selected;
	}

	/**
	 * setAccidental: <br>
	 * Sets the "accidental" field with a value
	 * 
	 * @param accidental
	 *                   The value to set the Accidental
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setAccidental(int)
	 */
	@Override
	public void setAccidental(int accidental) throws TrickException {
		if (!isValidValue(accidental))
			throw new TrickException("error.scenario.accidental.invalid", "Accidental needs to be between 0 and 4");
		super.setAccidental(accidental);
	}

	/**
	 * @param assetLinked
	 *                    the assetLinked to set
	 */
	public void setAssetLinked(boolean assetLinked) {
		this.assetLinked = assetLinked;
	}

	/**
	 * setAssetTypeValues: <br>
	 * Sets the Field "assetTypeValues" with a value.
	 * 
	 * @param assetTypeValues
	 *                        The Value to set the assetTypeValues field
	 */
	public void setAssetTypeValues(List<AssetTypeValue> assetTypeValues) {
		this.assetTypeValues = assetTypeValues;
	}

	/**
	 * setCorrective: <br>
	 * Sets the "corrective" field with a value
	 * 
	 * @param corrective
	 *                   The value to set the Corrective
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setCorrective(double)
	 */
	@Override
	public void setCorrective(double corrective) throws TrickException {
		if (corrective < 0 || corrective > 1)
			throw new TrickException("error.scenario.corrective.invalid", "Corrective needs to be 0 or 1!");
		super.setCorrective(corrective);
	}

	/**
	 * setDescription: <br>
	 * Sets the "description" field with a value
	 * 
	 * @param description
	 *                    The value to set the Scenario Description
	 */
	public void setDescription(String description) {
		if (description == null)
			this.description = "";
		else
			this.description = description;
	}

	/**
	 * setDetective: <br>
	 * Sets the "detective" field with a value
	 * 
	 * @param detective
	 *                  The value to set the Detective
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setDetective(double)
	 */
	@Override
	public void setDetective(double detective) throws TrickException {
		if (detective < 0 || detective > 1)
			throw new TrickException("error.scenario.detective.invalid", "Detective needs to be 0 or 1!");
		super.setDetective(detective);
	}

	/**
	 * setEnvironmental: <br>
	 * Sets the "environmental" field with a value
	 * 
	 * @param environmental
	 *                      The value to set the Environmental
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setEnvironmental(int)
	 */
	@Override
	public void setEnvironmental(int environmental) throws TrickException {
		if (!isValidValue(environmental))
			throw new TrickException("error.scenario.environmental.invalid",
					"Environmental needs to be between 0 and 4");
		super.setEnvironmental(environmental);
	}

	/**
	 * setExternalthreat: <br>
	 * Sets the "externalthreat" field with a value
	 * 
	 * @param externalthreat
	 *                       The value to set the External Threat
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setExternalThreat(int)
	 */
	@Override
	public void setExternalThreat(int externalthreat) throws TrickException {
		if (!isValidValue(externalthreat))
			throw new TrickException("error.scenario.external_threat.invalid",
					"External Threat needs to be between 0 and 4");
		super.setExternalThreat(externalthreat);
	}

	/**
	 * setIntentional: <br>
	 * Sets the "intentional" field with a value
	 * 
	 * @param intentional
	 *                    The value to set the Intentional
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setIntentional(int)
	 */
	@Override
	public void setIntentional(int intentional) throws TrickException {
		if (!isValidValue(intentional))
			throw new TrickException("error.scenario.intentional.invalid", "Intentional needs to be between 0 and 4");
		super.setIntentional(intentional);
	}

	/**
	 * setInternalthreat: <br>
	 * Sets the "internalthreat" field with a value
	 * 
	 * @param internalthreat
	 *                       The value to set the Internal Threat
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setInternalThreat(int)
	 */
	@Override
	public void setInternalThreat(int internalthreat) throws TrickException {
		if (!isValidValue(internalthreat))
			throw new TrickException("error.scenario.internal_threat.invalid",
					"Internal Threat needs to be between 0 and 4");
		super.setInternalThreat(internalthreat);
	}

	/**
	 * setLimitative: <br>
	 * Sets the "limitative" field with a value
	 * 
	 * @param limitative
	 *                   The value to set the Limitative
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setLimitative(double)
	 */
	@Override
	public void setLimitative(double limitative) throws TrickException {
		if (limitative < 0 || limitative > 1)
			throw new TrickException("error.scenario.limitative.invalid", "Limitative needs to be 0 or 1!");
		super.setLimitative(limitative);
	}

	/**
	 * @param linkedAssets
	 *                     the linkedAssets to set
	 */

	public void setLinkedAssets(List<Asset> linkedAssets) {
		this.linkedAssets = linkedAssets;
	}

	/**
	 * setName: <br>
	 * Sets the "name" field with a value
	 * 
	 * @param name
	 *             The value to set the Scenario Name
	 * @throws TrickException
	 */
	public void setName(String name) throws TrickException {
		if ((name == null) || (name.trim().isEmpty()))
			throw new TrickException("error.scenario.name.empty", "Name cannot be empty!");
		this.name = name;
	}

	/**
	 * setThreat : <br>
	 * Sets the "threat" field with the given value
	 * 
	 * @param threat
	 */
	public void setThreat(String threat) {
		this.threat = threat == null || threat.trim().isEmpty() ? null : threat.trim();
	}

	/**
	 * setVulnerability : <br>
	 * Sets the "vulnerability" field with the given value
	 * 
	 * @param threat
	 */
	public void setVulnerability(String vulnerability) {
		this.vulnerability = vulnerability == null || vulnerability.trim().isEmpty() ? null : vulnerability;
	}

	/**
	 * setPreventive: <br>
	 * Sets the "preventive" field with a value
	 * 
	 * @param preventive
	 *                   The value to set the Preventive
	 * @throws TrickException
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#setPreventive(double)
	 */
	@Override
	public void setPreventive(double preventive) throws TrickException {
		if (preventive < 0 || preventive > 1)
			throw new TrickException("error.scenario.preventive.invalid", "Preventive needs to be 0 or 1!");
		super.setPreventive(preventive);
	}

	/**
	 * setSelected: <br>
	 * Sets the "selected" field with a value
	 * 
	 * @param selected
	 *                 The value to set the Selected Flag
	 * @throws TrickException
	 */
	public void setSelected(boolean selected) throws TrickException {
		if (((this.getCorrective() + this.getLimitative() + this.getDetective() + this.getPreventive()) != 1)
				&& (this.getName().isEmpty()) && (selected))
			throw new TrickException("error.scenario.initialisation.early",
					"Scenario Fields have not been correctly initialised in order to be selected!");
		this.selected = selected;
	}

	/**
	 * setType: <br>
	 * Sets the "type" field with a value
	 * 
	 * @param type
	 *             The value to set the Scenario Type
	 * @throws TrickException
	 */
	public void setType(ScenarioType type) throws TrickException {
		if ((type == null) || (type.getName() == null) || (type.getName().trim().isEmpty()))
			throw new TrickException("error.scenario.type.empty", "Type cannot be empty!");
		this.type = type;
		setCategoryValue(getType().getCategory(), 1);
	}

	@Transient
	public String getILRKey() {
		return getILRKey(threat, vulnerability);
	}

	public static String getILRKey(String threat, String vulnerability) {
		if (threat == null) {
			if (vulnerability == null)
				return String.format(ILR_KEY_FORMATING, "-", "-");
			return String.format(ILR_KEY_FORMATING, "-", vulnerability.trim().toLowerCase());
		} else if (vulnerability == null)
			return String.format(ILR_KEY_FORMATING, threat.trim().toLowerCase(), "-");
		return String.format(ILR_KEY_FORMATING, threat.trim().toLowerCase(), vulnerability.trim().toLowerCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Scenario [name=" + name + ", type=" + type + ", selected=" + selected + ", description=" + description
				+ ", assetTypeValues=" + assetTypeValues + "]";
	}

	public void addApplicable(AssetType assetType) {
		if (isAssetLinked() || assetType == null)
			return;
		AssetTypeValue assetTypeValue = findByAssetType(assetType);
		if (assetTypeValue == null)
			getAssetTypeValues().add(new AssetTypeValue(assetType, 1));
		else
			assetTypeValue.setValue(1);
	}

	public void addApplicable(Asset asset) {
		if (asset == null)
			return;
		if (!isAssetLinked())
			addApplicable(asset.getAssetType());
		else if (!getLinkedAssets().contains(asset))
			getLinkedAssets().add(asset);

	}

	@Transient
	public List<AssetType> getAssetTypes() {
		return isAssetLinked() ? getLinkedAssets().stream().map(Asset::getAssetType).collect(Collectors.toList())
				: getAssetTypeValues().stream().filter(assetValue -> assetValue.getValue() > 0)
						.map(AssetTypeValue::getAssetType).collect(Collectors.toList());
	}

	@Transient
	public List<AssetType> getDistinctAssetTypes() {
		return isAssetLinked()
				? getLinkedAssets().stream().map(Asset::getAssetType).distinct().collect(Collectors.toList())
				: getAssetTypeValues().stream().filter(assetValue -> assetValue.getValue() > 0)
						.map(AssetTypeValue::getAssetType).distinct().collect(Collectors.toList());
	}

	/**
	 * isValidValue: <br>
	 * Check if Category value is valid or not. A valid value in scenario is 0
	 * or 1 or 4.
	 * 
	 * @param value
	 *              The value to check if valid
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#isValidValue(int)
	 */
	@Override
	protected boolean isValidValue(int value) {
		return value >= 0 && value <= 4;
	}

	/**
	 * valueFixer: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.model.general.SecurityCriteria#valueFixer(java.lang.String,
	 *      int)
	 */
	@Override
	protected int valueFixer(String category, int value) throws TrickException {
		if (value < 0 || value > 4)
			throw new TrickException("error.security_criteria.category.invalid",
					String.format("'%s' is not valid!", category), category);
		return value == 0 ? 0 : 4;
	}

}