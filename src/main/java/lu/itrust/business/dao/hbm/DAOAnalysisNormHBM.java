package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOAnalysisNorm;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOAnalysisNormHBM.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version 
 * @since Feb 12, 2013
 */
@Repository
public class DAOAnalysisNormHBM extends DAOHibernate implements DAOAnalysisNorm {

	/**
	 * Constructor: <br>
	 */
	public DAOAnalysisNormHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAnalysisNormHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#get(int)
	 */
	@Override
	public AnalysisNorm get(int id) throws Exception {
		return (AnalysisNorm) getSession().get(AnalysisNorm.class, id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> getAll() throws Exception {
		return (List<AnalysisNorm>) getSession().createQuery("From AnalysisNorm").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> getAllFromAnalysis(Integer analysisID) throws Exception {
		return (List<AnalysisNorm>) getSession().createQuery("From AnalysisNorm where analysis.id = :analysis ORDER BY norm.label ASC").setParameter("analysis", analysisID).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> getAllFromAnalysis(Analysis analysis) throws Exception {
		return (List<AnalysisNorm>) getSession().createQuery("From AnalysisNorm where analysis = :analysis").setParameter("analysis", analysis).list();
	}

	/**
	 * getAllAnalysisNormFromNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#getAllAnalysisNormFromNorm(lu.itrust.business.TS.Norm)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> getAllAnalysisNormFromNorm(Norm norm) throws Exception {
		return (List<AnalysisNorm>) getSession().createQuery("From AnalysisNorm where norm = :norm").setParameter("norm", norm).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#save(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Override
	public void save(AnalysisNorm analysisNorm) throws Exception {
		getSession().save(analysisNorm);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#saveOrUpdate(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Override
	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception {
		getSession().saveOrUpdate(analysisNorm);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisNorm#delete(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Override
	public void delete(AnalysisNorm analysisNorm) throws Exception {
		getSession().delete(analysisNorm);
	}
}