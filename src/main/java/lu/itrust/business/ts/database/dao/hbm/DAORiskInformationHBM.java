package lu.itrust.business.ts.database.dao.hbm;

import static lu.itrust.business.ts.constants.Constant.RI_TYPE_RISK_TBA;
import static lu.itrust.business.ts.constants.Constant.RI_TYPE_RISK_TBS;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAORiskInformation;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;

/**
 * DAORiskInformationHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
@Repository
public class DAORiskInformationHBM extends DAOHibernate implements DAORiskInformation {

	/**
	 * Constructor: <br>
	 */
	public DAORiskInformationHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAORiskInformationHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#get(int)
	 */
	@Override
	public RiskInformation get(Integer id) {
		return (RiskInformation) getSession().get(RiskInformation.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#getFromAnalysisById(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public RiskInformation getFromAnalysisById(Integer idAnalysis, Integer id) {
		String query = "Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis and riskInformation.id = :id";
		return (RiskInformation) getSession().createQuery(query).setParameter("id", id).setParameter("idAnalysis", idAnalysis).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public boolean belongsToAnalysis(Integer analysisId, Integer riskinformationId) {
		String query = "Select count(riskinformation)>0 From Analysis as analysis inner join analysis.riskInformations as riskinformation where analysis.id = :analysisid and riskinformation.id = ";
		query += ":riskinformationId";
		return (boolean) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("riskinformationId", riskinformationId).getSingleResult();
	}

	/**
	 * getAllRiskInformation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#getAllRiskInformation()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAll() {
		return getSession().createQuery("From RiskInformation").getResultList();
	}

	/**
	 * getAllByChapter: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#getAllByChapter(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllByChapter(String chapter) {
		return getSession().createQuery("From RiskInformation where chapter = :chapter").setParameter("chapter", chapter).getResultList();
	}

	/**
	 * getAllByCategory: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#getAllByCategory(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllByCategory(String category) {
		if (category == null)
			return Collections.emptyList();
		else if (category.startsWith("Risk"))
			return getSession().createQuery("From RiskInformation where category in :categories")
					.setParameterList("categories", new String[] { RI_TYPE_RISK_TBA, RI_TYPE_RISK_TBS }).getResultList();
		else
			return getSession().createQuery("From RiskInformation where category = :category").setParameter("category", category).getResultList();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllFromAnalysis(Integer analysisID) {
		String query = "Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", analysisID).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public List<RiskInformation> getAllFromAnalysis(Analysis analysis) {
		return analysis.getRiskInformations();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#save(lu.itrust.business.ts.model.riskinformation.RiskInformation)
	 */
	@Override
	public void save(RiskInformation riskInformation) {
		getSession().save(riskInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#saveOrUpdate(lu.itrust.business.ts.model.riskinformation.RiskInformation)
	 */
	@Override
	public void saveOrUpdate(RiskInformation riskInformation) {
		getSession().saveOrUpdate(riskInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAORiskInformation#delete(lu.itrust.business.ts.model.riskinformation.RiskInformation)
	 */
	@Override
	public void delete(RiskInformation riskInformation) {
		getSession().delete(riskInformation);
	}

	@Override
	public void delete(Collection<RiskInformation> riskInformations) {
		riskInformations.forEach(riskInformation -> getSession().delete(riskInformation));
	}

	@Override
	public List<RiskInformation> getAllByIdAnalysisAndCategories(Integer idAnalysis, String... types) {
		return getSession().createQuery(
				"Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis and riskInformation.category in :categories",
				RiskInformation.class).setParameter("idAnalysis", idAnalysis).setParameterList("categories", types).getResultList();
	}

	@Override
	public List<RiskInformation> findByIdAnalysisAndCategory(Integer idAnalysis, String type) {
		if (type == null)
			return Collections.emptyList();
		else if (type.startsWith("Risk"))
			return getAllByIdAnalysisAndCategories(idAnalysis, RI_TYPE_RISK_TBA, RI_TYPE_RISK_TBS);
		else
			return getSession().createQuery(
					"Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis and riskInformation.category = :category",
					RiskInformation.class).setParameter("idAnalysis", idAnalysis).setParameter("category", type).getResultList();
	}
}