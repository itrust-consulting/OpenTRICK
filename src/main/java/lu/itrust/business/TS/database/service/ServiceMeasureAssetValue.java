package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.standard.measure.MeasureAssetValue;

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
}