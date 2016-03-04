package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAssetType;
import lu.itrust.business.TS.database.service.ServiceAssetType;
import lu.itrust.business.TS.model.asset.AssetType;

/**
 * ServiceAssetTypeImpl.java: <br>
 * Detailed description...
 *
 * @author eomar, itrust consulting s.a.rl.
 * @version 
 * @since Jan 16, 2013
 */
@Service
public class ServiceAssetTypeImpl implements ServiceAssetType {

	@Autowired
	private DAOAssetType daoAssetType;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#get(int)
	 */
	@Override
	public AssetType get(Integer id)  {
		return daoAssetType.get(id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @param assetTypeName
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#getByName(java.lang.String)
	 */
	@Override
	public AssetType getByName(String assetTypeName)  {
		return daoAssetType.getByName(assetTypeName);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#getAll()
	 */
	@Override
	public List<AssetType> getAll()  {
		return daoAssetType.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#getAllFromAnalysis(int)
	 */
	@Override
	public List<AssetType> getAllFromAnalysis(Integer idAnalysis)  {
		return this.daoAssetType.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assetType
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#save(lu.itrust.business.TS.model.asset.AssetType)
	 */
	@Transactional
	@Override
	public void save(AssetType assetType)  {
		daoAssetType.save(assetType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assetType
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#saveOrUpdate(lu.itrust.business.TS.model.asset.AssetType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AssetType assetType)  {
		daoAssetType.saveOrUpdate(assetType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetType
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAssetType#delete(lu.itrust.business.TS.model.asset.AssetType)
	 */
	@Transactional
	@Override
	public void delete(AssetType assetType)  {
		daoAssetType.delete(assetType);
	}
}