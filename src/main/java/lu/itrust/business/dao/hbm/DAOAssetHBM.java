package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.dao.DAOAsset;

/**
 * DAOAssetHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
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
	 * @see lu.itrust.business.dao.DAOAsset#get(int)
	 */
	@Override
	public Asset get(Integer id) throws Exception {
		return (Asset) getSession().get(Asset.class, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assetId) throws Exception {
		String query = "Select count(asset) From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisid and asset.id = :assetid";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("assetid", assetId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getAll() throws Exception {
		return getSession().createQuery("From Asset").list();
	}

	/**
	 * getByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#getByPageAndSize(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize) throws Exception {
		return getSession().createQuery("From Asset").setMaxResults(pageSize).setFirstResult(pageSize * (pageIndex - 1)).list();
	}

	/**
	 * getFromAnalysisByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#getFromAnalysisByPageAndSize(int, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize) throws Exception {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisId";
		return getSession().createQuery(query).setParameter("analysisId", analysisId).setMaxResults(pageSize).setFirstResult(pageSize * (pageIndex - 1)).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#getAllFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getAllFromAnalysis(Integer analysisId) throws Exception {
		String query =
			"Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisId order by asset.selected desc, asset.value desc, asset.ALE asc, asset.name asc";
		return getSession().createQuery(query).setParameter("analysisId", analysisId).list();
	}

	/**
	 * getSelectedFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#getSelectedFromAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis) throws Exception {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.selected = true order by asset.value desc, ";
		query += "asset.name asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getSelectedFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#getSelectedFromAnalysisAndOrderByALE(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis) throws Exception {
		String query = "Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.selected = true order by asset.ALE asc, ";
		query += "asset.ALEO asc, asset.ALEP asc , asset.name asc, asset.value asc";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#save(lu.itrust.business.TS.Asset)
	 */
	@Override
	public Asset save(Asset asset) throws Exception {
		return (Asset) getSession().save(asset);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#saveOrUpdate(lu.itrust.business.TS.Asset)
	 */
	@Override
	public void saveOrUpdate(Asset asset) throws Exception {
		getSession().saveOrUpdate(asset);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#merge(lu.itrust.business.TS.Asset)
	 */
	@Override
	public Asset merge(Asset asset) throws Exception {
		return (Asset) getSession().merge(asset);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#delete(int)
	 */
	@Override
	public void delete(Integer id) throws Exception {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#delete(lu.itrust.business.TS.Asset)
	 */
	@Override
	public void delete(Asset asset) throws Exception {
		getSession().delete(asset);
	}

	@Override
	public boolean exist(Integer idAnalysis, String name) {
		return 0 < (Long) getSession().createQuery("Select count(asset) From Analysis as analysis inner join analysis.assets as asset where analysis.id = :idAnalysis and asset.name = :name")
				.setInteger("idAnalysis", idAnalysis).setString("name", name).uniqueResult();
	}

	@Override
	public Integer getAnalysisIdFromAsset(Integer assetId) throws Exception {
		return (Integer) getSession().createQuery("SELECT analysis.id From Analysis analysis join analysis.assets asset where asset.id = :assetID").setParameter("assetID", assetId).uniqueResult();
	}
}