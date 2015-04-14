package lu.itrust.business.TS.database.service;

import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;

/**
 * ServiceAssetTypeValue.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceMeasureAssetValue {
	public MeasureAssetValue get(Integer id) throws Exception;

	public void save(MeasureAssetValue measureAssetValue) throws Exception;

	public void saveOrUpdate(MeasureAssetValue measureAssetValue) throws Exception;

	public void delete(MeasureAssetValue measureAssetValue) throws Exception;

	public MeasureAssetValue getByAssetId(int idAsset);
}