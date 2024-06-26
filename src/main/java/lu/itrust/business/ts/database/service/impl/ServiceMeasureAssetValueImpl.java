package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOMeasureAssetValue;
import lu.itrust.business.ts.database.service.ServiceMeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;

/**
 * ServiceAssetTypeValueImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#get(int)
	 */
	@Override
	public MeasureAssetValue get(Integer id)  {
		return daoMeasureAssetValue.get(id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#save(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void save(MeasureAssetValue measureAssetValue)  {
		daoMeasureAssetValue.save(measureAssetValue);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#saveOrUpdate(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureAssetValue measureAssetValue)  {
		daoMeasureAssetValue.saveOrUpdate(measureAssetValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#delete(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void delete(MeasureAssetValue measureAssetValue)  {
		daoMeasureAssetValue.delete(measureAssetValue);
	}

	@Override
	public List<MeasureAssetValue> getByAssetId(int idAsset) {
		
		return daoMeasureAssetValue.getByAssetId(idAsset);
	}


	@Override
	public MeasureAssetValue getByMeasureIdAndAssetId(int measureId, int assetId) {
		return daoMeasureAssetValue.getByMeasureIdAndAssetId(measureId, assetId);
	}

}