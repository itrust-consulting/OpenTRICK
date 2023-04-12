package lu.itrust.business.ts.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAssetType;
import lu.itrust.business.ts.model.asset.AssetType;

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
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#get(int)
	 */
	@Override
	public AssetType get(Integer id)  {
		return (AssetType) getSession().get(AssetType.class, id);
	}

	/**
	 * getByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#getByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AssetType getByName(String name)  {
		return (AssetType) getSession().createQuery("From AssetType where name= :name").setParameter("name", name).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#getAll()
	 */
	@Override
	public List<AssetType> getAll()  {
		return getSession().createQuery("From AssetType order by name",AssetType.class).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssetType> getAllFromAnalysis(Integer idAnalysis)  {
		String query = "Select distinct(asset.assetType) From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis order by asset.assetType.name asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#save(lu.itrust.business.ts.model.asset.AssetType)
	 */
	@Override
	public void save(AssetType assetType)  {
		getSession().save(assetType);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#saveOrUpdate(lu.itrust.business.ts.model.asset.AssetType)
	 */
	@Override
	public void saveOrUpdate(AssetType assetType)  {
		getSession().saveOrUpdate(assetType);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetType#delete(lu.itrust.business.ts.model.asset.AssetType)
	 */
	@Override
	public void delete(AssetType assetType)  {
		getSession().delete(assetType);
	}
}