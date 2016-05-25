package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;

/**
 * DAOMeasure.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since May 12, 2014
 */
public interface DAOMeasureAssetValue {
	public MeasureAssetValue get(Integer id) ;

	public void save(MeasureAssetValue measureAssetValue) ;

	public void saveOrUpdate(MeasureAssetValue measureAssetValue) ;

	public void delete(MeasureAssetValue measureAssetValue) ;

	public List<MeasureAssetValue> getByAssetId(int idAsset);

	public MeasureAssetValue getByMeasureIdAndAssetId(int measureId, int assetId);

}