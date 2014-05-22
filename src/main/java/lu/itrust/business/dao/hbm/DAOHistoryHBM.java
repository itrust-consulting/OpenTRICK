package lu.itrust.business.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.History;
import lu.itrust.business.component.GeneralComperator;
import lu.itrust.business.dao.DAOHistory;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOHistoryHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOHistoryHBM extends DAOHibernate implements DAOHistory {

	/**
	 * Constructor: <br>
	 */
	public DAOHistoryHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOHistoryHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#get(int)
	 */
	@Override
	public History get(Integer id) throws Exception {
		return (History) getSession().get(History.class, id);
	}

	public History getFromAnalysisById(Integer idAnalysis, Integer idHistory) throws Exception {
		String query = "Select history From Analysis as analysis inner join analysis.histories as history where analysis.id = :idAnalysis and history.id = :idHistory";
		return (History) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("idHistory", idHistory).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer historyId) throws Exception {
		String query = "Select count(history) From Analysis as analysis inner join analysis.histories as history where analysis.id = :analysisId and history.id = :historyId";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("historyId", historyId).uniqueResult()).intValue() > 0;
	}

	/**
	 * versionExistsByAnalysisIdAndAnalysisVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#versionExistsByAnalysisIdAndAnalysisVersion(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public boolean versionExistsInAnalysis(Integer analysisId, String version) throws Exception {
		boolean res = false;
		Analysis analysis = (Analysis) getSession().get(Analysis.class, analysisId);
		if (analysis != null) {
			Hibernate.initialize(analysis.getHistories());
			res = analysis.versionExists(version);
		}
		return res;
	}

	/**
	 * versionExistsForAnalysisByVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#versionExistsForAnalysisByVersion(lu.itrust.business.TS.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public boolean versionExistsInAnalysis(Analysis analysis, String version) throws Exception {
		return analysis.versionExists(version);
	}

	/**
	 * getVersionsFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#getVersionsFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getVersionsFromAnalysis(Integer analysisId) throws Exception {
		String query = "Select distinct history.version " + "From Analysis as analysis inner join analysis.histories as history where analysis.identifier = ( Select analysis2.identifier";
		query += " From Analysis as analysis2 where analysis2.id = :analysisId )";
		return getSession().createQuery(query).setInteger("analysisId", analysisId).list();
	}

	/**
	 * getAllHistories: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#getAllHistories()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<History> getAll() throws Exception {
		return (List<History>) getSession().createQuery("From History").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#getAllFromAnalysisId(java.lang.Integer)
	 */
	@Override
	public List<History> getAllFromAnalysis(Integer analysisid) throws Exception {
		Analysis analysis = (Analysis) getSession().get(Analysis.class, analysisid);
		List<History> histories = null;
		if (analysis != null) {
			Hibernate.initialize(analysis.getHistories());
			histories = analysis.getHistories();
		}
		return histories;
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<History> getAllFromAnalysis(Analysis analysis) throws Exception {
		return (List<History>) getSession().createQuery("From History where analysis = :analysis").setParameter("analysis", analysis).list();
	}

	/**
	 * getAllHistoriesFromAnalysisByAuthor: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#getAllHistoriesFromAnalysisByAuthor(lu.itrust.business.TS.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public List<History> getAllFromAnalysisByAuthor(Analysis analysis, String author) throws Exception {
		List<History> histories = new ArrayList<History>();
		for (History history : analysis.getHistories())
			if (history.getAuthor().equals(author))
				histories.add(history);
		if (histories.isEmpty())
			return null;
		else
			return histories;
	}

	/**
	 * getAllHistoriesFromAnalysisByVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#getAllHistoriesFromAnalysisByVersion(lu.itrust.business.TS.Analysis,
	 *      java.lang.String)
	 */
	@Override
	public List<History> getAllFromAnalysisByVersion(Analysis analysis, String version) throws Exception {
		List<History> histories = new ArrayList<History>();
		for (History history : analysis.getHistories())
			if (history.getVersion().equals(version))
				histories.add(history);
		if (histories.isEmpty())
			return null;
		else
			return histories;
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#save(java.lang.Integer, lu.itrust.business.TS.History)
	 */
	@Override
	public void save(Integer analysisId, History history) throws Exception {
		Analysis analysis = (Analysis) getSession().get(Analysis.class, analysisId);
		Hibernate.initialize(analysis.getHistories());
		if (GeneralComperator.VersionComparator(history.getVersion(), analysis.getVersion()) == 1) {
			getSession().save(history);
			analysis.addAHistory(history);
			analysis.setVersion(history.getVersion());
			getSession().saveOrUpdate(analysis);
		} else
			throw new IllegalArgumentException("Version of History Entry must be > last Version of Analysis!");
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#save(lu.itrust.business.TS.History)
	 */
	@Override
	public void save(History history) throws Exception {
		getSession().save(history);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#saveOrUpdate(lu.itrust.business.TS.History)
	 */
	@Override
	public void saveOrUpdate(History history) throws Exception {
		getSession().saveOrUpdate(history);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#delete(lu.itrust.business.TS.History)
	 */
	@Override
	public void delete(History history) throws Exception {
		getSession().delete(history);
	}
}