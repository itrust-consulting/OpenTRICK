package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.History;
import lu.itrust.business.TS.Analysis;
import lu.itrust.business.dao.DAOHistory;

/** 
 * DAOHistoryHBM.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Feb 1, 2013
 */
public class DAOHistoryHBM  extends DAOHibernate implements DAOHistory {

	/**
	 * get: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#get(int)
	 */
	@Override
	public History get(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllFromAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<History> loadAllFromAnalysis(Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllByAuthorAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#loadAllByAuthorAnalysis(lu.itrust.business.TS.Analysis, java.lang.String)
	 */
	@Override
	public List<History> loadAllByAuthorAnalysis(Analysis analysis, String author) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAllByVersionAnalysis: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#loadAllByVersionAnalysis(lu.itrust.business.TS.Analysis, java.lang.String)
	 */
	@Override
	public List<History> loadAllByVersionAnalysis(Analysis analysis, String version)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * loadAll: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#loadAll()
	 */
	@Override
	public List<History> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#save(de.hunsicker.jalopy.storage.History)
	 */
	@Override
	public void save(History history) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#saveOrUpdate(de.hunsicker.jalopy.storage.History)
	 */
	@Override
	public void saveOrUpdate(History history) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * remove: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOHistory#delete(de.hunsicker.jalopy.storage.History)
	 */
	@Override
	public void remove(History history) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
