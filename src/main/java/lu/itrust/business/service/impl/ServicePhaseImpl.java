/**
 * 
 */
package lu.itrust.business.service.impl;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Phase;
import lu.itrust.business.dao.DAOPhase;
import lu.itrust.business.service.ServicePhase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eomar
 *
 */
@Service
public class ServicePhaseImpl implements ServicePhase {

	@Autowired
	private DAOPhase daoPhase;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#get(int)
	 */
	@Override
	public Phase get(int id) throws Exception {
		return daoPhase.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#loadFromPhaseNumberAnalysis(int, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public Phase loadFromPhaseNumberAnalysis(int number, int analysis)
			throws Exception {
		return daoPhase.loadFromPhaseNumberAnalysis(number, analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#loadByBeginDate(java.sql.Date, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadByBeginDate(Date beginDate, int analysis)
			throws Exception {
		return daoPhase.loadByEndDate(beginDate, analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#loadByEndDate(java.sql.Date, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadByEndDate(Date endDate, int analysis)
			throws Exception {
		return daoPhase.loadByBeginDate(endDate, analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Phase> loadAllFromAnalysis(int analysis) throws Exception {
		return daoPhase.loadAllFromAnalysis(analysis);
	}



	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#loadAll()
	 */
	@Override
	public List<Phase> loadAll() throws Exception {
		return daoPhase.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#save(lu.itrust.business.TS.Phase)
	 */
	@Transactional
	@Override
	public void save(Phase phase) throws Exception {
		daoPhase.save(phase);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#saveOrUpdate(lu.itrust.business.TS.Phase)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Phase phase) throws Exception {
		daoPhase.saveOrUpdate(phase);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServicePhase#remove(lu.itrust.business.TS.Phase)
	 */
	@Transactional
	@Override
	public void remove(Phase phase) throws Exception {
		daoPhase.remove(phase);
	}

}
