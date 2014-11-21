package lu.itrust.business.TS.database.service.impl;

import lu.itrust.business.TS.data.standard.measure.MeasureAssetValue;
import lu.itrust.business.TS.database.dao.DAOMeasureAssetValue;
import lu.itrust.business.TS.database.service.ServiceMeasureAssetValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceAssetTypeValueImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceMeasureAssetValueImpl implements ServiceMeasureAssetValue {

	@Autowired
	private DAOMeasureAssetValue daoMeasureAssetValue;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#get(int)
	 */
	@Override
	public MeasureAssetValue get(Integer id) throws Exception {
		return daoMeasureAssetValue.get(id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#save(lu.itrust.business.TS.data.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void save(MeasureAssetValue measureAssetValue) throws Exception {
		daoMeasureAssetValue.save(measureAssetValue);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#saveOrUpdate(lu.itrust.business.TS.data.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureAssetValue measureAssetValue) throws Exception {
		daoMeasureAssetValue.saveOrUpdate(measureAssetValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#delete(lu.itrust.business.TS.data.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void delete(MeasureAssetValue measureAssetValue) throws Exception {
		daoMeasureAssetValue.delete(measureAssetValue);
	}

}