/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.dao.DAOAsset;

/**
 * @author eom
 * 
 */
@Repository
public class DAOAssetHBM extends DAOHibernate implements DAOAsset {

	/**
	 * 
	 */
	public DAOAssetHBM() {
	}

	/**
	 * @param session
	 */
	public DAOAssetHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#get(int)
	 */
	@Override
	public Asset get(int id) {
		return (Asset) getSession().get(Asset.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#find()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> find() {
		return getSession().createQuery("From Asset").list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#find(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> find(int pageIndex, int pageSize) {
		return getSession().createQuery("From Asset").setMaxResults(pageSize)
				.setFirstResult(pageSize * (pageIndex - 1)).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#findByAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> findByAnalysis(int analysisId) {
		return getSession()
				.createQuery(
						"Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisId")
				.setInteger("analysisId", analysisId).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#findByAnalysis(int, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Asset> findByAnalysis(int pageIndex, int pageSize,
			int analysisId) {
		return getSession()
				.createQuery(
						"Select asset From Analysis as analysis inner join analysis.assets as asset where analysis.id = :analysisId")
				.setInteger("analysisId", analysisId).setMaxResults(pageSize)
				.setFirstResult(pageSize * (pageIndex - 1)).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#save(lu.itrust.business.TS.Asset)
	 */
	@Override
	public Asset save(Asset asset) {
		return (Asset) getSession().save(asset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.dao.DAOAsset#saveOrUpdate(lu.itrust.business.TS.Asset)
	 */
	@Override
	public void saveOrUpdate(Asset asset) {
		getSession().saveOrUpdate(asset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#merge(lu.itrust.business.TS.Asset)
	 */
	@Override
	public Asset merge(Asset asset) {
		return (Asset) getSession().merge(asset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#delete(lu.itrust.business.TS.Asset)
	 */
	@Override
	public void delete(Asset asset) {
		getSession().delete(asset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.dao.DAOAsset#delete(int)
	 */
	@Override
	public void delete(int id) {
		getSession().delete(get(id));
	}

}
