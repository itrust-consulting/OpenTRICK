package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.dao.DAOAssetType;
import lu.itrust.business.service.ServiceAssetType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#get(int)
	 */
	@Override
	public AssetType get(Integer id) throws Exception {
		return daoAssetType.get(id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @param assetTypeName
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#getByName(java.lang.String)
	 */
	@Override
	public AssetType getByName(String assetTypeName) throws Exception {
		return daoAssetType.getByName(assetTypeName);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#getAll()
	 */
	@Override
	public List<AssetType> getAll() throws Exception {
		return daoAssetType.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#getAllFromAnalysis(int)
	 */
	@Override
	public List<AssetType> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		return this.daoAssetType.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param assetType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#save(lu.itrust.business.TS.AssetType)
	 */
	@Transactional
	@Override
	public void save(AssetType assetType) throws Exception {
		daoAssetType.save(assetType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param assetType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#saveOrUpdate(lu.itrust.business.TS.AssetType)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AssetType assetType) throws Exception {
		daoAssetType.saveOrUpdate(assetType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param assetType
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceAssetType#delete(lu.itrust.business.TS.AssetType)
	 */
	@Transactional
	@Override
	public void delete(AssetType assetType) throws Exception {
		daoAssetType.delete(assetType);
	}
}