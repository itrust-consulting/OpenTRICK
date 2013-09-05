package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.dao.DAOAssetType;
import lu.itrust.business.service.ServiceAssetType;
@Service
public class ServiceAssetTypeImpl implements ServiceAssetType {

	@Autowired
	private DAOAssetType daoAssetType;
	
	@Override
	public AssetType get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoAssetType.get(id);
	}

	@Override
	public AssetType get(String assetTypeName) throws Exception {
		// TODO Auto-generated method stub
		return daoAssetType.get(assetTypeName);
	}

	@Override
	public List<AssetType> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoAssetType.loadAll();
	}

	@Transactional
	@Override
	public void save(AssetType assetType) throws Exception {
		daoAssetType.save(assetType);

	}

	@Transactional
	@Override
	public void saveOrUpdate(AssetType assetType) throws Exception {
		daoAssetType.saveOrUpdate(assetType);

	}

	@Transactional
	@Override
	public void delete(AssetType assetType) throws Exception {
		daoAssetType.delete(assetType);
	}

	/**
	 * @return the daoAssetType
	 */
	public DAOAssetType getDaoAssetType() {
		return daoAssetType;
	}

	/**
	 * @param daoAssetType the daoAssetType to set
	 */
	public void setDaoAssetType(DAOAssetType daoAssetType) {
		this.daoAssetType = daoAssetType;
	}
	
	

}
