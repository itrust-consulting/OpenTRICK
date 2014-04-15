/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.dao.DAORiskInformation;

/**
 * @author eomar
 *
 */
@Repository
public class DAORiskInformationHBM extends DAOHibernate implements DAORiskInformation {

	/**
	 * 
	 */
	public DAORiskInformationHBM() {
	}

	/**
	 * @param session
	 */
	public DAORiskInformationHBM(Session session) {
		super(session);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#get(int)
	 */
	@Override
	public RiskInformation get(int id) throws Exception {
		return (RiskInformation) getSession().get(RiskInformation.class, id);
	}


	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#loadFromChapter(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> loadFromChapter(String chapter) throws Exception {
		return getSession().createQuery("From RiskInformation where chapter = :chapter").setString("chapter", chapter).list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#loadFromCategory(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> loadFromCategory(String category) throws Exception {
		return getSession().createQuery("From RiskInformation where category = :category").setString("category", category).list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<RiskInformation> loadAllFromAnalysis(Analysis analysis) throws Exception {
		return analysis.getRiskInformations();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#loadAllFromAnalysisID(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> loadAllFromAnalysisID(int analysisID) throws Exception {
		return getSession().createQuery("Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis").setParameter("idAnalysis", analysisID).list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RiskInformation> loadAll() throws Exception {
		return getSession().createQuery("From RiskInformation").list();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#save(lu.itrust.business.TS.RiskInformation)
	 */
	@Override
	public void save(RiskInformation riskInformation) throws Exception {
		getSession().save(riskInformation);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#saveOrUpdate(lu.itrust.business.TS.RiskInformation)
	 */
	@Override
	public void saveOrUpdate(RiskInformation riskInformation) throws Exception {
		getSession().saveOrUpdate(riskInformation);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#remove(lu.itrust.business.TS.RiskInformation)
	 */
	@Override
	public void remove(RiskInformation riskInformation) throws Exception {
		getSession().delete(riskInformation);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAORiskInformation#findbyIdAndAnalysis(int, int)
	 */
	@Override
	public RiskInformation findbyIdAndAnalysis(int id, int idAnalysis) {
		return (RiskInformation) getSession().createQuery("Select riskInformation From Analysis analysis inner join analysis.riskInformations as riskInformation where analysis.id = :idAnalysis and riskInformation.id = :id").setParameter("id", id).setParameter("idAnalysis", idAnalysis).uniqueResult();
	}

}
