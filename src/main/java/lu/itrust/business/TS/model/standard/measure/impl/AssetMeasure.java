package lu.itrust.business.TS.model.standard.measure.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.AbstractNormalMeasure;

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
public class AssetMeasure extends AbstractNormalMeasure implements Cloneable {

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
	 * getAssetTypeValue: <br>
	 * Returns the Asset Type value at position "index" of the Asset Type Value
	 * List ("assetTypeValues" field)
	 * 
	 * @param index
	 *            The index of the element position to retrieve from the list
	 * @return AssetTypeValue The Asset Type Value object at position "index"
	 */
	public MeasureAssetValue getMeasureAssetValueByAsset(Asset asset) {
		for (MeasureAssetValue value : measureAssetValues)
			if (value.getAsset().equals(asset))
				return value;
		return null;
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
	public List<MeasureAssetValue> getMeasureAssetValueByAssetType(AssetType assetType) {
		return measureAssetValues.stream().filter(measureAssetValue -> measureAssetValue.getAsset().getAssetType().equals(assetType)).collect(Collectors.toList());
	}

	/**
	 * getAssetTypeValueList: <br>
	 * Returns the List of Asset Type Values for this Measure ("assetTypeValue"
	 * field)
	 * 
	 * @return The List of all Asset Type Values
	 */
	@OneToMany
	@Cascade(CascadeType.ALL)
	@Access(AccessType.FIELD)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JoinColumn(name = "fiAssetMeasure", nullable = false, insertable = true)
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
	 * Adds a new Asset Type Value object to the list of Asset Type Values
	 * ("assetTypeValue" field)
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
	 * clone: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.model.standard.measure.Measure#clone()
	 */
	@Override
	public AssetMeasure clone() throws CloneNotSupportedException {
		AssetMeasure assetMeasure = (AssetMeasure) super.clone();
		assetMeasure.measureAssetValues = new ArrayList<>();
		for (MeasureAssetValue assetValue : measureAssetValues)
			assetMeasure.addAnMeasureAssetValue(assetValue.clone());
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
	 * @see lu.itrust.business.TS.model.standard.measure.Measure#duplicate()
	 */
	@Override
	public AssetMeasure duplicate(AnalysisStandard analysisStandard, Phase phase) throws CloneNotSupportedException {
		AssetMeasure assetMeasure = (AssetMeasure) super.duplicate(analysisStandard, phase);
		assetMeasure.measureAssetValues = new ArrayList<>();
		for (MeasureAssetValue assetValue : measureAssetValues)
			assetMeasure.addAnMeasureAssetValue(assetValue.duplicate(null));
		return assetMeasure;
	}

}