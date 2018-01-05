package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;

/**
 * ServiceAssetTypeValue.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceMeasureAssetValue {
	public MeasureAssetValue get(Integer id);

	public void save(MeasureAssetValue measureAssetValue);

	public void saveOrUpdate(MeasureAssetValue measureAssetValue);

	public void delete(MeasureAssetValue measureAssetValue);

	public List<MeasureAssetValue> getByAssetId(int idAsset);

	public MeasureAssetValue getByMeasureIdAndAssetId(int measureId, int assetId);
}