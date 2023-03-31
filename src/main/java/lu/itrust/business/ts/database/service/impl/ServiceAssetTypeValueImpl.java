package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOAssetTypeValue;
import lu.itrust.business.ts.database.service.ServiceAssetTypeValue;
import lu.itrust.business.ts.model.general.AssetTypeValue;

/**
 * ServiceAssetTypeValueImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#get(int)
	 */
	@Override
	public AssetTypeValue get(Integer id)  {
		return daoAssetTypeValue.get(id);
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
	public AssetTypeValue save(AssetTypeValue assetTypeValue)  {
		return daoAssetTypeValue.save(assetTypeValue);
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
	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue)  {
		return daoAssetTypeValue.saveOrUpdate(assetTypeValue);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param assetTypeValue
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#merge(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue merge(AssetTypeValue assetTypeValue)  {
		return daoAssetTypeValue.merge(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id)  {
		daoAssetTypeValue.delete(id);
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
	public void delete(AssetTypeValue assetTypeValue)  {
		daoAssetTypeValue.delete(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetTypeValues
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAssetTypeValue#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(List<AssetTypeValue> assetTypeValues)  {
		daoAssetTypeValue.delete(assetTypeValues);
	}
}