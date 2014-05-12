package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.dao.DAOAssetType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOAssetTypeHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 31 janv. 2013
 */
@Repository
public class DAOAssetTypeHBM extends DAOHibernate implements DAOAssetType {

	/**
	 * Constructor: <br>
	 */
	public DAOAssetTypeHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAssetTypeHBM(Session session) {
		super(session);
	}

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
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#getByName(java.lang.String)
	 */
	@Override
	public AssetType getByName(String assetTypeName) throws Exception {
		Query query = getSession().createQuery("From AssetType where type= :type").setParameter("type", assetTypeName);
		return (AssetType) query.uniqueResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssetType> getAll() throws Exception {
		return (List<AssetType>) getSession().createQuery("From AssetType").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetType#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssetType> getAllFromAnalysis(int idAnalysis) throws Exception {
		String query = "Select distinct(asset.assetType) From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis order by asset.assetType.type asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
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