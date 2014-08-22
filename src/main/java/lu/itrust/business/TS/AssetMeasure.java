package lu.itrust.business.TS;

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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.exception.TrickException;

/**
 * MaturityMeasure: <br>
 * This class represents the MaturityMeasure and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
@Entity 
@PrimaryKeyJoinColumn(name="idAssetMeasure")
public class AssetMeasure extends Measure implements Cloneable {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	@Transient
	private static final long serialVersionUID = 1L;

	/** The List of AssetTypeValues */
	private List<MeasureAssetValue> measureAssetValues = new ArrayList<MeasureAssetValue>();
	
	/**
	 * getAssetTypeValue: <br>
	 * Returns the Asset Type value at position "index" of the Asset Type Value
	 * List ("assetTypeValues" field)
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
	 * Returns the List of Asset Type Values for this Measure ("assetTypeValue"
	 * field)
	 * 
	 * @return The List of all Asset Type Values
	 */
	@ManyToMany
	@JoinTable(name = "MeasureAssetValue", 
			   joinColumns = { @JoinColumn(name = "fiAssetMeasure", nullable = false) }, 
			   inverseJoinColumns = { @JoinColumn(name = "idMeasureAssetValue", nullable = false) },
			   uniqueConstraints = @UniqueConstraint(columnNames = {"fiAssetMeasure", "idMeasureAssetValue"})
	)
	@Cascade(CascadeType.ALL)
	public List<MeasureAssetValue> getAssetValues() {
		return measureAssetValues;
	}

	/**
	 * setAssetTypeValues: <br>
	 * Sets the Field "assetTypeValues" with a value.
	 * 
	 * @param assetTypeValues
	 *            The Value to set the assetTypeValues field
	 */
	public void setAssetValues(List<MeasureAssetValue> assetValues) {
		this.measureAssetValues = assetValues;
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
	public void addAnAssetValue(MeasureAssetValue assetvalue) throws TrickException {
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
	 * @see lu.itrust.business.TS.Measure#getImplementationRate()
	 */
	@Override
	@Column(name="dtImplmentationRate", nullable=false)
	@Access(AccessType.FIELD)
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
	public AssetMeasure clone() throws CloneNotSupportedException {
		return (AssetMeasure) super.clone();
	}

	@Override
	public AssetMeasure duplicate() throws CloneNotSupportedException {
		return (AssetMeasure) super.duplicate();
	}

}