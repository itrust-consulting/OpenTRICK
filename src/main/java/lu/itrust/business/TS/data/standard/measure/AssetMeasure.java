package lu.itrust.business.TS.data.standard.measure;

import java.util.ArrayList;
import java.util.List;

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

import lu.itrust.business.TS.data.general.Phase;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.exception.TrickException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 * MaturityMeasure: <br>
 * This class represents the MaturityMeasure and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity
@PrimaryKeyJoinColumn(name = "idAssetMeasure")
public class AssetMeasure extends Measure implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** The List of AssetTypeValues */
	private List<MeasureAssetValue> measureAssetValues = new ArrayList<MeasureAssetValue>();

	/** The List of Measure Properties */
	private MeasureProperties measurePropertyList = null;

	/** The "To Check" comment */
	private String toCheck = "";

	/**
	 * getMeasurePropertyList: <br>
	 * Returns the MeasureProperties object which has all property values
	 * 
	 * @return The Measure Properties List object
	 */
	@ManyToOne
	@JoinColumn(name = "fiMeasureProperties", nullable = false)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
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
			throw new TrickException("error.asset_measure.measure_property.empty", "Measure properties cannot be empty");
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
	public MeasureAssetValue getAssetValue(int index) {
		if (index < 0 || index >= measureAssetValues.size())
			throw new IndexOutOfBoundsException("Index (" + index + ") should be between 0 and " + (measureAssetValues.size() - 1));
		return measureAssetValues.get(index);
	}

	/**
	 * getAssetTypeValueList: <br>
	 * Returns the List of Asset Type Values for this Measure ("assetTypeValue" field)
	 * 
	 * @return The List of all Asset Type Values
	 */
	@ManyToMany
	@JoinTable(name = "MeasureAssetValue",
			joinColumns = { @JoinColumn(name = "fiAssetMeasure", nullable = false) },
			inverseJoinColumns = { @JoinColumn(name = "idMeasureAssetValue", nullable = false) },
			uniqueConstraints = @UniqueConstraint(columnNames = { "fiAssetMeasure", "idMeasureAssetValue" }))
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@Access(AccessType.FIELD)
	public List<MeasureAssetValue> getMeasureAssetValues() {
		return measureAssetValues;
	}

	/**
	 * setAssetTypeValues: <br>
	 * Sets the Field "assetTypeValues" with a value.
	 * 
	 * @param assetTypeValues
	 *            The Value to set the assetTypeValues field
	 */
	public void setMeasureAssetValues(List<MeasureAssetValue> assetValues) {
		this.measureAssetValues = assetValues;
	}

	/**
	 * addAnAssetTypeValue: <br>
	 * Adds a new Asset Type Value object to the list of Asset Type Values ("assetTypeValue" field)
	 * 
	 * @param assettypevalue
	 *            The Asset Type Value object to add to list
	 * @throws TrickException
	 */
	public void addAnMeasureAssetValue(MeasureAssetValue assetvalue) {
		if (measureAssetValues.contains(assetvalue)) {
			System.err.println("Asset value cannot be duplicated");
			return;
		}
		this.measureAssetValues.add(assetvalue);
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the Implementation Rate value
	 * 
	 * @return Implementation Rate value
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#getImplementationRate()
	 */
	@Override
	@Column(name = "dtImplmentationRate", nullable = false)
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
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#clone()
	 */
	@Override
	public AssetMeasure clone() throws CloneNotSupportedException {
		AssetMeasure assetMeasure = (AssetMeasure) super.clone();
		assetMeasure.measureAssetValues = new ArrayList<>();
		for (MeasureAssetValue assetValue : measureAssetValues)
			assetMeasure.addAnMeasureAssetValue(assetValue.clone());
		assetMeasure.measurePropertyList = (MeasureProperties) measurePropertyList.duplicate();
		return assetMeasure;
	}

	/**
	 * duplicate: <br>
	 * Description
	 * 
	 * @throws TrickException
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.data.standard.measure.Measure#duplicate()
	 */
	@Override
	public AssetMeasure duplicate(AnalysisStandard analysisStandard, Phase phase) throws CloneNotSupportedException {
		AssetMeasure assetMeasure = (AssetMeasure) super.duplicate(analysisStandard, phase);
		assetMeasure.measureAssetValues = new ArrayList<>();
		for (MeasureAssetValue assetValue : measureAssetValues)
			assetMeasure.addAnMeasureAssetValue(assetValue.duplicate());
		assetMeasure.measurePropertyList = (MeasureProperties) measurePropertyList.duplicate();
		return assetMeasure;
	}

}