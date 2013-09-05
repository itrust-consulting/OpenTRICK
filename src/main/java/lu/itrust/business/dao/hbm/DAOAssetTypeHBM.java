package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.dao.DAOAssetType;

import org.hibernate.Query;

/**
 * DAOAssetTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since 31 janv. 2013
 */
public class DAOAssetTypeHBM extends DAOHibernate implements DAOAssetType {

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#get(int)
	 */
	@Override
	public AssetType get(int id) throws Exception {
		return (AssetType) getSession().get(AssetType.class, id);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#get(java.lang.String)
	 */
	@Override
	public AssetType get(String assetTypeName) throws Exception {
		Query query = getSession().createQuery(
				"From AssetType where type= :type");
		query.setString("type", assetTypeName);
		return (AssetType) query.uniqueResult();

	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssetType> loadAll() throws Exception {
			Query query = getSession().createQuery("From AssetType");

			return (List<AssetType>) query.list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#save(lu.itrust.business.TS.AssetType)
	 */
	@Override
	public void save(AssetType assetType) throws Exception {
		getSession().save(assetType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#saveOrUpdate(lu.itrust.business.TS.AssetType)
	 */
	@Override
	public void saveOrUpdate(AssetType assetType) throws Exception {

		getSession().saveOrUpdate(assetType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#delete(lu.itrust.business.TS.AssetType)
	 */
	@Override
	public void delete(AssetType assetType) throws Exception {
		getSession().delete(assetType);
	}

}
