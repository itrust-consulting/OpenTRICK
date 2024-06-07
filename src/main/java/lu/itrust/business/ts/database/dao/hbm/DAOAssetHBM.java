package lu.itrust.business.ts.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAsset;
import lu.itrust.business.ts.model.asset.Asset;

/**
 * DAOAssetHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOAssetHBM extends DAOHibernate implements DAOAsset {

	/**
	 * Constructor: <br>
	 */
	public DAOAssetHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAssetHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#get(int)
	 */
	@Override
	public Asset get(Integer id) {
		return (Asset) getSession().get(Asset.class, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#belongsToAnalysis(int,
	 *      int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assetId) {
		String query = "Select count(asset) > 0 From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisid and asset.id = :assetid";
		return (boolean) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("assetid", assetId).getSingleResult();
	}

	/**
	 * getFromAnalysisByName: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getFromAnalysisByName(java.lang.Integer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Asset getFromAnalysisByName(Integer analysisId, String name) {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisid and asset.name = :name";
		return (Asset) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("name", name).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getAll() {
		return getSession().createQuery("From Asset").getResultList();
	}

	/**
	 * getByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getByPageAndSize(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize) {
		return getSession().createQuery("From Asset").setMaxResults(pageSize).setFirstResult(pageSize * (pageIndex - 1)).getResultList();
	}

	/**
	 * getFromAnalysisByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getFromAnalysisByPageAndSize(int,
	 *      int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize) {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisId";
		return getSession().createQuery(query).setParameter("analysisId", analysisId).setMaxResults(pageSize).setFirstResult(pageSize * (pageIndex - 1)).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getAllFromAnalysis(Integer analysisId) {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisId order by asset.selected desc, asset.value desc, asset.ALE asc, asset.name asc";
		return getSession().createQuery(query).setParameter("analysisId", analysisId).getResultList();
	}

	/**
	 * getSelectedFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getSelectedFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis) {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.selected = true order by asset.value desc, ";
		query += "asset.name asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getSelectedFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#getSelectedFromAnalysisAndOrderByALE(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis) {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.selected = true order by asset.ALE asc, ";
		query += "asset.ALEO asc, asset.ALEP asc , asset.name asc, asset.value asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#save(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	public Asset save(Asset asset) {
		return (Asset) getSession().save(asset);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#saveOrUpdate(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	public void saveOrUpdate(Asset asset) {
		getSession().saveOrUpdate(asset);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#merge(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	public Asset merge(Asset asset) {
		return (Asset) getSession().merge(asset);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#delete(int)
	 */
	@Override
	public void delete(Integer id) {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAsset#delete(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	public void delete(Asset asset) {
		getSession().delete(asset);
	}

	@Override
	public boolean exist(Integer idAnalysis, String name) {
		return 0 < (Long) getSession()
				.createQuery("Select count(asset) From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.name = :name")
				.setParameter("idAnalysis", idAnalysis).setParameter("name", name).getSingleResult();
	}

	@Override
	public Integer getAnalysisIdFromAsset(Integer assetId) {
		return (Integer) getSession().createQuery("SELECT analysis.id From Analysis analysis join analysis.assets asset where asset.id = :assetID").setParameter("assetID", assetId)
				.getSingleResult();
	}

	@Override
	public Asset getFromAnalysisById(Integer idAnalysis, int idAsset) {
		return (Asset) getSession()
				.createQuery("Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.id = :idAsset")
				.setParameter("idAnalysis", idAnalysis).setParameter("idAsset", idAsset).getSingleResult();
	}

	@Override
	public Asset getByNameAndAnlysisId(String name, int idAnalysis) {
		return (Asset) getSession().createQuery("Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.name = :name")
				.setParameter("idAnalysis", idAnalysis).setParameter("name", name).getSingleResult();
	}

	@Override
	public boolean belongsToAnalysis(Integer idAnalysis, List<Integer> assetIds) {
		if(assetIds.isEmpty())
			return true;
		Long count = (Long) getSession()
				.createQuery("Select count(asset) From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisid and asset.id in (:assetIds)")
				.setParameter("analysisid", idAnalysis).setParameterList("assetIds", assetIds).getSingleResult();
		return count == assetIds.size();
	}
}