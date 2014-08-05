package lu.itrust.business.TS;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.exception.TrickException;

/**
 * NormMeasure: <br>
 * This class represents a AnalysisNorm Measure and its data. This class extends
 * Measure, it is used to represent measures that are NOT Maturity Measures. <br>
 * <br>
 * - Asset Type Values <br>
 * - Data for measures of AnalysisNorm 27001, 27002 and custom norms
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class NormMeasure extends Measure {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The "To Check" comment */
	private String toCheck;

	/** The List of AssetTypeValues */
	private List<AssetTypeValue> assetTypeValues = new ArrayList<AssetTypeValue>();

	/** The List of Measure Properties */
	private MeasureProperties measurePropertyList;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getMeasurePropertyList: <br>
	 * Returns the MeasureProperties object which has all property values
	 * 
	 * @return The Measure Properties List object
	 */
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
	 * getAssetTypeValueList: <br>
	 * Returns the List of Asset Type Values for this Measure ("assetTypeValue"
	 * field)
	 * 
	 * @return The List of all Asset Type Values
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
	 * addAnAssetTypeValue: <br>
	 * Adds a new Asset Type Value object to the list of Asset Type Values
	 * ("assetTypeValue" field)
	 * 
	 * @param assettypevalue
	 *            The Asset Type Value object to add to list
	 * @throws TrickException
	 */
	public void addAnAssetTypeValue(AssetTypeValue assettypevalue) throws TrickException {
		if (assetTypeValues.contains(assettypevalue))
			throw new TrickException("error.norm_measure.asset_type_value", "Assettype value cannot be duplicated");
		this.assetTypeValues.add(assettypevalue);
	}

	/**
	 * getToCheck: <br>
	 * Returns the "toCheck" field value
	 * 
	 * @return The To Check Value
	 */
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
	 * @see lu.itrust.business.TS.Measure#getImplementationRate()
	 */
	@Override
	public Double getImplementationRate() {
		return (Double) super.getImplementationRate();
	}

	/**
	 * getImplementationRateValue: <br>
	 * Returns the Implementation Rate value using the getImplementationRate
	 * method.
	 * 
	 * @return Implementation Rate Value
	 * @see lu.itrust.business.TS.Measure#getImplementationRateValue()
	 * @see lu.itrust.business.TS.NormMeasure#getImplementationRate()
	 */
	@Override
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
	 * @see lu.itrust.business.TS.Measure#setImplementationRate(java.lang.Object)
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
	 * @see lu.itrust.business.TS.Measure#setImplementationRate(java.lang.Object)
	 */
	public void setImplementationRate(double implementationRate) throws TrickException {
		super.setImplementationRate(implementationRate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.Measure#clone()
	 */
	@Override
	public NormMeasure clone() throws CloneNotSupportedException {
		NormMeasure normMeasure = (NormMeasure) super.clone();
		normMeasure.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			normMeasure.assetTypeValues.add(assetTypeValue.clone());
		normMeasure.measurePropertyList = (MeasureProperties) measurePropertyList.clone();
		return normMeasure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.Measure#duplicate()
	 */
	@Override
	public NormMeasure duplicate() throws CloneNotSupportedException {
		NormMeasure normMeasure = (NormMeasure) super.duplicate();
		normMeasure.assetTypeValues = new ArrayList<>();
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			normMeasure.assetTypeValues.add(assetTypeValue.duplicate());
		normMeasure.measurePropertyList = (MeasureProperties) measurePropertyList.duplicate();
		return normMeasure;
	}

	public void copyMeasureCharacteristicsTo(NormMeasure measure) throws TrickException, CloneNotSupportedException {
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