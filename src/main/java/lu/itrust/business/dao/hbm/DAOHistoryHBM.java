package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.History;
import lu.itrust.business.dao.DAOHistory;

import org.hibernate.Hibernate;
import org.hibernate.Query;
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
	 * 
	 */
	public DAOHistoryHBM() {
		// TODO Auto-generated constructor stub
	}



	/**
	 * @param sessionFactory
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
	public History get(int id) throws Exception {
		return (History) getSession().get(History.class, id);
	}

	
	
	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#loadAllFromAnalysis(java.lang.Integer)
	 */
	@Override
	public List<History> loadAllFromAnalysis(Integer analysisid) throws Exception {
		Analysis analysis = (Analysis) getSession().get(Analysis.class, analysisid);
		List<History> histories = null;
		if (analysis != null) {
			Hibernate.initialize(analysis.getHistories());
			histories = analysis.getHistories();
		}
		return histories;
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<History> loadAllFromAnalysis(Analysis analysis) throws Exception {
		Query query = getSession().createQuery("From History where analysis = :analysis");
		query.setParameter("analysis", analysis);
		return (List<History>) query.list();
	}

	/**
	 * loadAllByAuthorAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#loadAllByAuthorAnalysis(lu.itrust.business.TS.Analysis,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<History> loadAllByAuthorAnalysis(Analysis analysis, String author) throws Exception {
		Query query = getSession().createQuery("From History where analysis = :analysis and history.author = :author");
		query.setString("author", author);
		query.setParameter("analysis", analysis);
		return (List<History>) query.list();
	}

	/**
	 * loadAllByVersionAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#loadAllByVersionAnalysis(lu.itrust.business.TS.Analysis,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<History> loadAllByVersionAnalysis(Analysis analysis, String version) throws Exception {
		Query query = getSession().createQuery("From History where History.version = :version and analysis = :analysis");
		query.setString("version", version);
		query.setParameter("analysis", analysis);
		return (List<History>) query.list();
	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#loadAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<History> loadAll() throws Exception {
		Query query = getSession().createQuery("From History");
		return (List<History>) query.list();

	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#save(de.hunsicker.jalopy.storage.History)
	 */
	@Override
	public void save(History history) throws Exception {
		getSession().save(history);		
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#saveOrUpdate(de.hunsicker.jalopy.storage.History)
	 */
	@Override
	public void saveOrUpdate(History history) throws Exception {
		getSession().saveOrUpdate(history);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOHistory#delete(de.hunsicker.jalopy.storage.History)
	 */
	@Override
	public void remove(History history) throws Exception {
		getSession().delete(history);
	}



	/**
	 * versionExists: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#versionExists(lu.itrust.business.TS.Analysis, java.lang.String)
	 */
	@Override
	public boolean versionExists(Analysis analysis, String version) throws Exception {
		return analysis.versionExists(version);
	}



	/**
	 * versionExists: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#versionExists(java.lang.Integer, java.lang.String)
	 */
	@Override
	public boolean versionExists(Integer analysisId, String version) throws Exception {
		boolean res = false;
		Analysis analysis = (Analysis) getSession().get(Analysis.class, analysisId);
		if (analysis != null) {
			Hibernate.initialize(analysis.getHistories());
			res = analysis.versionExists(version);
		}
		return res;
	}



	/**
	 * save: <br>
	 * this will add the history entry to the analysis as well
	 *
	 * @see lu.itrust.business.dao.DAOHistory#save(java.lang.Integer, lu.itrust.business.TS.History)
	 */
	@Override
	public void save(Integer analysisId, History history) throws Exception {
		
		Analysis analysis = (Analysis) getSession().get(Analysis.class, analysisId);
		
		if (analysis != null) {
			
			Hibernate.initialize(analysis);
			Hibernate.initialize(analysis.getAssets());
			Hibernate.initialize(analysis.getActionPlans());
			Hibernate.initialize(analysis.getAnalysisNorms());
			Hibernate.initialize(analysis.getAssessments());
			Hibernate.initialize(analysis.getScenarios());
			Hibernate.initialize(analysis.getHistories());
			Hibernate.initialize(analysis.getItemInformations());
			Hibernate.initialize(analysis.getLanguage());
			Hibernate.initialize(analysis.getParameters());
			Hibernate.initialize(analysis.getRiskInformations());
			Hibernate.initialize(analysis.getSummaries());
			Hibernate.initialize(analysis.getUsedPhases());
			Hibernate.initialize(analysis.getRiskRegisters());
			
			analysis.setId(-1);
			
			if (History.versionAGreaterThanB(history.getVersion(), analysis.getVersion())) {
				getSession().save(history);

				analysis.addAHistory(history);
				
				analysis.setVersion(history.getVersion());
				
				getSession().saveOrUpdate(analysis);				
					
			} else {
				throw new IllegalArgumentException("Version of History Entry must be > last Version of Analysis!");
			}

		}		
	}
}