/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAORiskProfile;
import lu.itrust.business.TS.model.cssf.RiskProfile;

/**
 * @author eomar
 *
 */
@Repository
public class DAORiskProfileHBM extends DAOHibernate implements DAORiskProfile {

	/**
	 * 
	 */
	public DAORiskProfileHBM() {
	}

	/**
	 * @param session
	 */
	public DAORiskProfileHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAORiskProfile#get(java.lang.Integer)
	 */
	@Override
	public RiskProfile get(Integer id) {
		return (RiskProfile) getSession().get(RiskProfile.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAORiskProfile#belongsToAnalysis(java.
	 * lang.Integer, java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer idRiskProfile) {
		return (boolean) getSession()
				.createQuery(
						"Select count(riskProfile)>0 From Analysis analysis inner join analysis.riskProfiles riskProfile where analysis.id = :idAnalysis and riskProfile.id = :idRiskProfile")
				.setInteger("idAnalysis", analysisId).setInteger("idRiskProfile", idRiskProfile).uniqueResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAORiskProfile#getAllFromAnalysis(java
	 * .lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskProfile> getAllFromAnalysis(Integer analysisId) {
		return getSession().createQuery("Select riskProfile From Analysis analysis inner join analysis.riskProfiles riskProfile where analysis.id = :idAnalysis")
				.setInteger("idAnalysis", analysisId).list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAORiskProfile#save(lu.itrust.business
	 * .TS.model.cssf.RiskProfile)
	 */
	@Override
	public void save(RiskProfile riskProfile) {
		getSession().save(riskProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAORiskProfile#saveOrUpdate(lu.itrust.
	 * business.TS.model.cssf.RiskProfile)
	 */
	@Override
	public void saveOrUpdate(RiskProfile riskProfile) {
		getSession().saveOrUpdate(riskProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskProfile#delete(lu.itrust.
	 * business.TS.model.cssf.RiskProfile)
	 */
	@Override
	public void delete(RiskProfile riskProfile) {
		getSession().delete(riskProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.database.dao.DAORiskProfile#deleteAllFromAnalysis(
	 * java.lang.Integer)
	 */
	@Override
	public void deleteAllFromAnalysis(Integer analysisID) {
		getAllFromAnalysis(analysisID).forEach(riskProfile -> delete(riskProfile));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskProfile#delete(java.lang.
	 * Integer)
	 */
	@Override
	public void delete(Integer idRiskProfile) {
		getSession().createQuery("Delete From RiskProfile where id = :id").setInteger("id", idRiskProfile).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAORiskProfile#merge(lu.itrust.
	 * business.TS.model.cssf.RiskProfile)
	 */
	@Override
	public RiskProfile merge(RiskProfile riskProfile) {
		return (RiskProfile) getSession().merge(riskProfile);
	}

	@Override
	public RiskProfile getByAssetAndScanrio(int idAsset, int idScenario) {
		return (RiskProfile) getSession().createQuery("From RiskProfile where asset.id = :idAsset and scenario.id = :idScenario").setInteger("idAsset", idAsset)
				.setInteger("idScenario", idScenario).uniqueResult();
	}

	@Override
	public RiskProfile getFromAnalysisById(int idAnalysis, int idRiskProfile) {
		return (RiskProfile) getSession()
				.createQuery(
						"Select riskProfile From Analysis as analysis inner join analysis.riskProfiles as riskProfile where analysis.id = :idAnalysis and riskProfile.id = :idRiskProfile")
				.setInteger("idRiskProfile", idRiskProfile).setInteger("idAnalysis", idAnalysis).uniqueResult();
	}

}
