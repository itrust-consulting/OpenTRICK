package lu.itrust.business.ts.model.standard.measure.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.general.AssetTypeValue;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * NormalMeasure: <br>
 * This class represents a AnalysisStandard Measure and its data. This class
 * extends Measure, it is used to represent measures that are NOT Maturity
 * Measures. <br>
 * <br>
 * - Asset Type Values <br>
 * - Data for measures of Analysisstandard 27001, 27002 and custom standards
 */
@Entity
@PrimaryKeyJoinColumn(name = "idNormalMeasure")
public class NormalMeasure extends AbstractNormalMeasure {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The List of AssetTypeValues */
	private List<AssetTypeValue> assetTypeValues = new ArrayList<>();

	
	public NormalMeasure() {
	}

	public NormalMeasure(MeasureDescription measureDescription) {
		super(measureDescription);
	}

	

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	
	/**
	 * getAssetTypeValue: <br>
	 * Returns the Asset Type value at position "index" of the Asset Type Value
	 * List ("assetTypeValues" field)
	 * 
	 * @param index
	 *            The index of the element position to retrieve from the list
	 * @return AssetTypeValue The Asset Type Value object at position "index"
	 */
	public AssetTypeValue getAssetTypeValue(int index) {
		if (index < 0 || index >= assetTypeValues.size())
			throw new IndexOutOfBoundsException("Index (" + index + ") should be between 0 and " + (assetTypeValues.size() - 1));
		return assetTypeValues.get(index);
	}

	/**
	 * getAssetTypeValue: <br>
	 * Returns the Asset Type value at position "index" of the Asset Type Value
	 * List ("assetTypeValues" field)
	 * 
	 * @param index
	 *            The index of the element position to retrieve from the list
	 * @return AssetTypeValue The Asset Type Value object at position "index"
	 */
	public AssetTypeValue getAssetTypeValueByAssetType(AssetType assetType) {
		for (AssetTypeValue atv : assetTypeValues)
			if (atv.getAssetType().equals(assetType))
				return atv;
		return null;
	}

	/**
	 * getAssetTypeValueList: <br>
	 * Returns the List of Asset Type Values for this Measure ("assetTypeValue"
	 * field)
	 * 
	 * @return The List of all Asset Type Values
	 */
	@ManyToMany
	@Access(AccessType.FIELD)
	@Cascade(CascadeType.ALL)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinTable(name = "MeasureAssetTypeValue", joinColumns = { @JoinColumn(name = "fiNormalMeasure", nullable = false) }, inverseJoinColumns = {
			@JoinColumn(name = "fiAssetTypeValue", nullable = false) }, uniqueConstraints = @UniqueConstraint(columnNames = { "fiAssetTypeValue" }) )
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
	 * addAnAssetTypeValue: <br>
	 * Adds a new Asset Type Value object to the list of Asset Type Values
	 * ("assetTypeValue" field)
	 * 
	 * @param assettypevalue
	 *            The Asset Type Value object to add to list
	 * @throws TrickException
	 */
	public void addAnAssetTypeValue(AssetTypeValue assettypevalue) throws TrickException {
		if (assetTypeValues.contains(assettypevalue)) {
			System.err.println("Assettype value cannot be duplicated");
			return;
		}
		this.assetTypeValues.add(assettypevalue);
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#clone()
	 */
	@Override
	public NormalMeasure clone() throws CloneNotSupportedException {
		NormalMeasure normalMeasure = (NormalMeasure) super.clone();
		normalMeasure.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			normalMeasure.assetTypeValues.add(assetTypeValue.clone());
		return normalMeasure;
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.model.standard.measure.Measure#duplicate()
	 */
	@Override
	public NormalMeasure duplicate(AnalysisStandard analysisStandard, Phase phase) throws CloneNotSupportedException {
		NormalMeasure normalMeasure = (NormalMeasure) super.duplicate(analysisStandard, phase);
		normalMeasure.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			normalMeasure.assetTypeValues.add(assetTypeValue.duplicate());
		return normalMeasure;
	}

	/**
	 * copyMeasureCharacteristicsTo: <br>
	 * Description
	 * 
	 * @param measure
	 * @throws TrickException
	 * @throws CloneNotSupportedException
	 */
	public void copyMeasureCharacteristicsTo(NormalMeasure measure) throws TrickException, CloneNotSupportedException {
		if (this.getMeasurePropertyList() == null || measure == null || measure.getMeasurePropertyList() == null)
			return;
		measurePropertyList.copyTo(measure.measurePropertyList);
		if (assetTypeValues == null || measure.getAssetTypeValues() == null)
			return;
		Map<Integer, AssetTypeValue> mappedAssetTypeValues = new LinkedHashMap<Integer, AssetTypeValue>(getAssetTypeValues().size());
		for (AssetTypeValue assetTypeValue : measure.getAssetTypeValues())
			mappedAssetTypeValues.put(assetTypeValue.getAssetType().getId(), assetTypeValue);
		measure.assetTypeValues.clear();

		for (AssetTypeValue assetTypeValue : getAssetTypeValues()) {
			AssetTypeValue typeValue = mappedAssetTypeValues.get(assetTypeValue.getAssetType().getId());
			if (typeValue == null)
				typeValue = assetTypeValue.duplicate();
			else
				typeValue.setValue(assetTypeValue.getValue());
			measure.assetTypeValues.add(typeValue);
			mappedAssetTypeValues.remove(assetTypeValue.getAssetType().getId());
		}

		for (AssetTypeValue assetTypeValue : mappedAssetTypeValues.values()) {
			assetTypeValue.setValue(0);
			measure.assetTypeValues.add(assetTypeValue);
		}
	}
}