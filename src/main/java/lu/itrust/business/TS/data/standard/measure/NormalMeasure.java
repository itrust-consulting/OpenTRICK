package lu.itrust.business.TS.data.standard.measure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lu.itrust.business.TS.data.asset.AssetType;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.exception.TrickException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * NormalMeasure: <br>
 * This class represents a AnalysisStandard Measure and its data. This class extends Measure, it is
 * used to represent measures that are NOT Maturity Measures. <br>
 * <br>
 * - Asset Type Values <br>
 * - Data for measures of Analysisstandard 27001, 27002 and custom standards
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@PrimaryKeyJoinColumn(name = "idNormalMeasure")
public class NormalMeasure extends Measure {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The "To Check" comment */
	private String toCheck = "";

	/** The List of AssetTypeValues */
	private List<AssetTypeValue> assetTypeValues = new ArrayList<AssetTypeValue>();

	/** The List of Measure Properties */
	private MeasureProperties measurePropertyList = null;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getMeasurePropertyList: <br>
	 * Returns the MeasureProperties object which has all property values
	 * 
	 * @return The Measure Properties List object
	 */
	@ManyToOne
	@JoinColumn(name = "fiMeasureProperties", nullable = false, unique = true)
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	public MeasureProperties getMeasurePropertyList() {
		return measurePropertyList;
	}

	/**
	 * setMeasurePropertyList: <br>
	 * Sets the "measurePropertyList" field with a measureProperties object
	 * 
	 * @param measurePropertyList
	 *            The measureProperties Object to set the List of Properties
	 * @throws TrickException
	 */
	public void setMeasurePropertyList(MeasureProperties measurePropertyList) throws TrickException {
		if (measurePropertyList == null)
			throw new TrickException("error.norm_measure.measure_property.empty", "Measure properties cannot be empty");
		this.measurePropertyList = measurePropertyList;
	}

	/**
	 * getAssetTypeValue: <br>
	 * Returns the Asset Type value at position "index" of the Asset Type Value List
	 * ("assetTypeValues" field)
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
	 * Returns the Asset Type value at position "index" of the Asset Type Value List
	 * ("assetTypeValues" field)
	 * 
	 * @param index
	 *            The index of the element position to retrieve from the list
	 * @return AssetTypeValue The Asset Type Value object at position "index"
	 */
	public AssetTypeValue getAssetTypeValueByAssetType(AssetType assetType) {
		for(AssetTypeValue atv : assetTypeValues)
			if(atv.getAssetType().equals(assetType))
				return atv;
		return null;
	}
	
	/**
	 * getAssetTypeValueList: <br>
	 * Returns the List of Asset Type Values for this Measure ("assetTypeValue" field)
	 * 
	 * @return The List of all Asset Type Values
	 */
	@ManyToMany
	@JoinTable(name = "MeasureAssetTypeValue", joinColumns = { @JoinColumn(name = "fiNormalMeasure", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "fiAssetTypeValue",
			nullable = false) }, uniqueConstraints = @UniqueConstraint(columnNames = { "fiAssetTypeValue" }))
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
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
	 * Adds a new Asset Type Value object to the list of Asset Type Values ("assetTypeValue" field)
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
	 * getToCheck: <br>
	 * Returns the "toCheck" field value
	 * 
	 * @return The To Check Value
	 */
	@Column(name = "dtToCheck", nullable = false)
	public String getToCheck() {
		return this.toCheck;
	}

	/**
	 * setToCheck: <br>
	 * Sets the "toCheck" field with a value
	 * 
	 * @param toCheck
	 *            The value to set the "To Check" Comment
	 */
	public void setToCheck(String toCheck) {
		this.toCheck = toCheck;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the Implementation Rate value
	 * 
	 * @return Implementation Rate value
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#getImplementationRate()
	 */
	@Override
	@Column(name = "dtImplementationRate", nullable = false)
	@Access(AccessType.FIELD)
	public Double getImplementationRate() {
		return (Double) super.getImplementationRate();
	}

	/**
	 * getImplementationRateValue: <br>
	 * Returns the Implementation Rate value using the getImplementationRate method.
	 * 
	 * @return Implementation Rate Value
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#getImplementationRateValue()
	 * @see lu.itrust.business.TS.data.standard.measure.NormalMeasure#getImplementationRate()
	 */
	@Override
	@Transient
	public double getImplementationRateValue() {
		return getImplementationRate();
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Implementation Rate with a Value
	 * 
	 * @param implementationRate
	 *            The Implementation Rate Value as object
	 * @throws TrickException
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#setImplementationRate(java.lang.Object)
	 */
	@Override
	public void setImplementationRate(Object implementationRate) throws TrickException {
		if (!(implementationRate instanceof Double))
			throw new TrickException("error.norm_measure.implementation_rate.invalid", "ImplementationRate needs to be of Type Double!");
		super.setImplementationRate((Double) implementationRate);
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Implementation Rate with a Value
	 * 
	 * @param implementationRate
	 *            The Implementation Rate Value as Double
	 * @throws TrickException
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#setImplementationRate(java.lang.Object)
	 */
	public void setImplementationRate(double implementationRate) throws TrickException {
		super.setImplementationRate(implementationRate);
	}

	/**
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#clone()
	 */
	@Override
	public NormalMeasure clone() throws CloneNotSupportedException {
		NormalMeasure normalMeasure = (NormalMeasure) super.clone();
		normalMeasure.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			normalMeasure.assetTypeValues.add(assetTypeValue.clone());
		normalMeasure.measurePropertyList = (MeasureProperties) measurePropertyList.clone();
		return normalMeasure;
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#duplicate()
	 */
	@Override
	public NormalMeasure duplicate(AnalysisStandard analysisStandard, Phase phase) throws CloneNotSupportedException {
		NormalMeasure normalMeasure = (NormalMeasure) super.duplicate(analysisStandard, phase);
		normalMeasure.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			normalMeasure.assetTypeValues.add(assetTypeValue.duplicate());
		normalMeasure.measurePropertyList = (MeasureProperties) measurePropertyList.duplicate();
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