package lu.itrust.business.dao.hbm;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.dao.DAOPhase;

/** 
 * DAOPhaseHBM.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Feb 1, 2013
 */
public class DAOPhaseHBM extends DAOHibernate implements DAOPhase {

	/**
	 * get: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#get(int)
	 */
	@Override
	public Phase get(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadFromPhaseNumberAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#loadFromPhaseNumberAnalysis(int, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadFromPhaseNumberAnalysis(int number, Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadByBeginDate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#loadByBeginDate(java.sql.Date, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadByBeginDate(Date beginDate, Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadByEndDate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#loadByEndDate(java.sql.Date, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadByEndDate(Date beginDate, Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadAllFromAnalysis(Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysisIdentifierVersionCreationDate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#loadAllFromAnalysisIdentifierVersionCreationDate(int, int, java.lang.String)
	 */
	@Override
	public List<Phase> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier,
			int version, String creationDate) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAll: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#loadAll()
	 */
	@Override
	public List<Phase> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#save(lu.itrust.business.TS.Phase)
	 */
	@Override
	public void save(Phase phase) throws Exception {
		getSession().save(phase);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#saveOrUpdate(lu.itrust.business.TS.Phase)
	 */
	@Override
	public void saveOrUpdate(Phase phase) throws Exception {
		getSession().saveOrUpdate(phase);

	}

	/**
	 * remove: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOPhase#remove(lu.itrust.business.TS.Phase)
	 */
	@Override
	public void remove(Phase phase) throws Exception {
		getSession().delete(phase);

	}

}
