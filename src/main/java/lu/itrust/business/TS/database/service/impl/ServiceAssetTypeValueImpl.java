package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOAssetTypeValue;
import lu.itrust.business.TS.database.service.ServiceAssetTypeValue;
import lu.itrust.business.TS.model.general.AssetTypeValue;

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
public class ServiceAssetTypeValueImpl implements ServiceAssetTypeValue {

	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

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
	public AssetTypeValue get(Integer id) throws Exception {
		return daoAssetTypeValue.get(id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#save(lu.itrust.business.TS.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue save(AssetTypeValue assetTypeValue) throws Exception {
		return daoAssetTypeValue.save(assetTypeValue);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#saveOrUpdate(lu.itrust.business.TS.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue) throws Exception {
		return daoAssetTypeValue.saveOrUpdate(assetTypeValue);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#merge(lu.itrust.business.TS.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue merge(AssetTypeValue assetTypeValue) throws Exception {
		return daoAssetTypeValue.merge(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		daoAssetTypeValue.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#delete(lu.itrust.business.TS.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void delete(AssetTypeValue assetTypeValue) throws Exception {
		daoAssetTypeValue.delete(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetTypeValues
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetTypeValue#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(List<AssetTypeValue> assetTypeValues) throws Exception {
		daoAssetTypeValue.delete(assetTypeValues);
	}
}