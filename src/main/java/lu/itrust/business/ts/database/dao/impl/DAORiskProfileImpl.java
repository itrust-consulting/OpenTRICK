/**
 * 
 */
package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAORiskProfile;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * @author eomar
 *
 */
@Repository
public class DAORiskProfileImpl extends DAOHibernate implements DAORiskProfile {

	/**
	 * 
	 */
	public DAORiskProfileImpl() {
	}

	/**
	 * @param session
	 */
	public DAORiskProfileImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskProfile#get(java.lang.Integer)
	 */
	@Override
	public RiskProfile get(Integer id) {
		return (RiskProfile) getSession().get(RiskProfile.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAORiskProfile#belongsToAnalysis(java.
	 * lang.Integer, java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer idRiskProfile) {
		return (boolean) createQueryWithCache(
				"Select count(riskProfile)>0 From Analysis analysis inner join analysis.riskProfiles riskProfile where analysis.id = :idAnalysis and riskProfile.id = :idRiskProfile")
				.setParameter("idAnalysis", analysisId).setParameter("idRiskProfile", idRiskProfile).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAORiskProfile#getAllFromAnalysis(java
	 * .lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskProfile> getAllFromAnalysis(Integer analysisId) {
		return createQueryWithCache("Select riskProfile From Analysis analysis inner join analysis.riskProfiles riskProfile where analysis.id = :idAnalysis")
				.setParameter("idAnalysis", analysisId).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAORiskProfile#save(lu.itrust.business
	 * .ts.model.cssf.RiskProfile)
	 */
	@Override
	public void save(RiskProfile riskProfile) {
		getSession().save(riskProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAORiskProfile#saveOrUpdate(lu.itrust.
	 * business.ts.model.cssf.RiskProfile)
	 */
	@Override
	public void saveOrUpdate(RiskProfile riskProfile) {
		getSession().saveOrUpdate(riskProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskProfile#delete(lu.itrust.
	 * business.ts.model.cssf.RiskProfile)
	 */
	@Override
	public void delete(RiskProfile riskProfile) {
		getSession().delete(riskProfile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskProfile#deleteAllFromAnalysis(
	 * java.lang.Integer)
	 */
	@Override
	public void deleteAllFromAnalysis(Integer analysisID) {
		getAllFromAnalysis(analysisID).forEach(riskProfile -> delete(riskProfile));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskProfile#delete(java.lang.
	 * Integer)
	 */
	@Override
	public void delete(Integer idRiskProfile) {
		createQueryWithCache("Delete From RiskProfile where id = :id").setParameter("id", idRiskProfile).executeUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskProfile#merge(lu.itrust.
	 * business.ts.model.cssf.RiskProfile)
	 */
	@Override
	public RiskProfile merge(RiskProfile riskProfile) {
		return (RiskProfile) getSession().merge(riskProfile);
	}

	@Override
	public RiskProfile getByAssetAndScanrio(int idAsset, int idScenario) {
		return createQueryWithCache("From RiskProfile where asset.id = :idAsset and scenario.id = :idScenario", RiskProfile.class).setParameter("idAsset", idAsset)
				.setParameter("idScenario", idScenario).getSingleResult();
	}

	@Override
	public RiskProfile getFromAnalysisById(int idAnalysis, int idRiskProfile) {
		return createQueryWithCache(
				"Select riskProfile From Analysis as analysis inner join analysis.riskProfiles as riskProfile where analysis.id = :idAnalysis and riskProfile.id = :idRiskProfile",
				RiskProfile.class).setParameter("idRiskProfile", idRiskProfile).setParameter("idAnalysis", idAnalysis).getSingleResult();
	}

	@Override
	public boolean isUsed(String identifier, Integer idAnalysis) {
		return (boolean) createQueryWithCache(
				"Select count(*)>0 From Analysis as analysis inner join analysis.riskProfiles as riskProfile where analysis.id = :idAnalysis and riskProfile.identifier = :identifier")
				.setParameter("identifier", identifier).setParameter("idAnalysis", idAnalysis).getSingleResult();
	}

	@Override
	public List<RiskProfile> findByIdAnalysisAndContainsMeasure(Integer idAnalysis, Measure measure) {
		return createQueryWithCache(
				"Select riskProfile From Analysis analysis inner join analysis.riskProfiles riskProfile inner join riskProfile.measures as measure where analysis.id = :idAnalysis and measure = :measure",
				RiskProfile.class).setParameter("idAnalysis", idAnalysis).setParameter("measure", measure).getResultList();
	}

	@Override
	public void resetRiskIdByIds(List<Integer> ids) {
		if (!ids.isEmpty())
			createQueryWithCache("Update RiskProfile r set r.identifier = null where r.id in (:ids)").setParameterList("ids", ids).executeUpdate();
	}

}
