/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOAnalysisNorm;
import lu.itrust.business.service.ServiceAnalysisNorm;

/**
 * @author oensuifudine
 *
 */
@Service
public class ServiceAnalysisNormImpl implements ServiceAnalysisNorm {

	@Autowired
	private DAOAnalysisNorm daoAnalysisNorm;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#get(int)
	 */
	@Override
	public AnalysisNorm get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoAnalysisNorm.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#loadAll()
	 */
	@Override
	public List<AnalysisNorm> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoAnalysisNorm.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<AnalysisNorm> loadAllFromAnalysis(Analysis analysis)
			throws Exception {
		// TODO Auto-generated method stub
		return daoAnalysisNorm.loadAllFromAnalysis(analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#loadAllFromNorm(lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<AnalysisNorm> loadAllFromNorm(Norm norm) throws Exception {
		// TODO Auto-generated method stub
		return daoAnalysisNorm.loadAllFromNorm(norm);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#save(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Transactional
	@Override
	public void save(AnalysisNorm analysisNorm) throws Exception {
		daoAnalysisNorm.save(analysisNorm);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#saveOrUpdate(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception {
		daoAnalysisNorm.saveOrUpdate(analysisNorm);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAnalysisNorm#remove(lu.itrust.business.TS.AnalysisNorm)
	 */
	@Transactional
	@Override
	public void remove(AnalysisNorm analysisNorm) throws Exception {
		daoAnalysisNorm.remove(analysisNorm);

	}
	
	/**
	 * @return the daoAnalysisNorm
	 */
	public DAOAnalysisNorm getDaoAnalysisNorm() {
		return daoAnalysisNorm;
	}

	/**
	 * @param daoAnalysisNorm the daoAnalysisNorm to set
	 */
	public void setDaoAnalysisNorm(DAOAnalysisNorm daoAnalysisNorm) {
		this.daoAnalysisNorm = daoAnalysisNorm;
	}
	
	

}
