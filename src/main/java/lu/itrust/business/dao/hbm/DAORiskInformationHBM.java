package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.dao.DAORiskInformation;

/**
 * DAORiskInformationHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
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
	 * @see lu.itrust.business.dao.DAORiskInformation#get(int)
	 */
	@Override
	public RiskInformation get(int id) throws Exception {
		return (RiskInformation) getSession().get(RiskInformation.class, id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#getFromAnalysisById(int, int)
	 */
	@Override
	public RiskInformation getFromAnalysisById(int id, int idAnalysis) throws Exception {
		String query = "Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis and riskInformation.id = :id";
		return (RiskInformation) getSession().createQuery(query).setParameter("id", id).setParameter("idAnalysis", idAnalysis).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public boolean belongsToAnalysis(Integer riskinformationId, Integer analysisId) throws Exception {
		String query = "Select count(riskinformation) From Analysis as analysis inner join analysis.usedPhases as riskinformation where analysis.id = :analysisid and phase.id = ";
		query += ":riskinformationId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("riskinformationId", riskinformationId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllRiskInformation: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#getAllRiskInformation()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllRiskInformation() throws Exception {
		return getSession().createQuery("From RiskInformation").list();
	}

	/**
	 * getAllByChapter: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#getAllByChapter(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllByChapter(String chapter) throws Exception {
		return getSession().createQuery("From RiskInformation where chapter = :chapter").setString("chapter", chapter).list();
	}

	/**
	 * getAllByCategory: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#getAllByCategory(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllByCategory(String category) throws Exception {
		return getSession().createQuery("From RiskInformation where category = :category").setString("category", category).list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> getAllFromAnalysisId(int analysisID) throws Exception {
		String query = "Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis";
		return getSession().createQuery(query).setParameter("idAnalysis", analysisID).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<RiskInformation> getAllFromAnalysis(Analysis analysis) throws Exception {
		return analysis.getRiskInformations();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#save(lu.itrust.business.TS.RiskInformation)
	 */
	@Override
	public void save(RiskInformation riskInformation) throws Exception {
		getSession().save(riskInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#saveOrUpdate(lu.itrust.business.TS.RiskInformation)
	 */
	@Override
	public void saveOrUpdate(RiskInformation riskInformation) throws Exception {
		getSession().saveOrUpdate(riskInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAORiskInformation#delete(lu.itrust.business.TS.RiskInformation)
	 */
	@Override
	public void delete(RiskInformation riskInformation) throws Exception {
		getSession().delete(riskInformation);
	}
}