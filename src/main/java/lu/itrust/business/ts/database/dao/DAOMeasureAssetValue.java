package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;

/**
 * DAOMeasure.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
public interface DAOMeasureAssetValue {
	public MeasureAssetValue get(Integer id);

	public void save(MeasureAssetValue measureAssetValue);

	public void saveOrUpdate(MeasureAssetValue measureAssetValue);

	public void delete(MeasureAssetValue measureAssetValue);

	public List<MeasureAssetValue> getByAssetId(int idAsset);

	public MeasureAssetValue getByMeasureIdAndAssetId(int measureId, int assetId);

}