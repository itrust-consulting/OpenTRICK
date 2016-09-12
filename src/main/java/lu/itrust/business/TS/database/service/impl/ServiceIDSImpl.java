/**
 * 
 */
package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOIDS;
import lu.itrust.business.TS.database.service.ServiceIDS;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.usermanagement.IDS;

/**
 * @author eomar
 *
 */
@Service
public class ServiceIDSImpl implements ServiceIDS {

	@Autowired
	private DAOIDS daoIDS;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#get(int)
	 */
	@Override
	public IDS get(int id) {
		return daoIDS.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#get(java.lang.String)
	 */
	@Override
	public IDS get(String prefix) {
		return daoIDS.get(prefix);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#getByAnalysisId(int)
	 */
	@Override
	public List<IDS> getByAnalysisId(int idAnalysis) {
		return daoIDS.getByAnalysisId(idAnalysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#getByAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<IDS> getByAnalysis(Analysis analysis) {
		return daoIDS.getByAnalysis(analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#save(lu.itrust.business.TS.usermanagement.IDS)
	 */
	@Transactional
	@Override
	public Integer save(IDS ids) {
		return daoIDS.save(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#saveOrUpdate(lu.itrust.business.TS.usermanagement.IDS)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(IDS ids) {
		daoIDS.saveOrUpdate(ids);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.database.service.ServiceIDS#delete(lu.itrust.business.TS.usermanagement.IDS)
	 */
	@Transactional
	@Override
	public void delete(IDS ids) {
		daoIDS.delete(ids);
	}
	
	
	@Override
	public void delete(Integer id) {
		daoIDS.delete(id);
	}

	@Override
	public List<IDS> getAll() {
		return daoIDS.getAll();
	}

	@Override
	public List<IDS> getAllNoSubscribers() {
		return daoIDS.getAllNoSubscribers();
	}

	@Override
	public List<IDS> getAllByState(boolean enabled) {
		return daoIDS.getAllByState(enabled);
	}

	@Override
	public List<String> getPrefixesByAnalysisId(int idAnalysis) {
		return daoIDS.getPrefixesByAnalysisId(idAnalysis);
	}

	@Override
	public List<String> getPrefixesByAnalysis(Analysis analysis) {
		return daoIDS.getPrefixesByAnalysis(analysis);
	}

	@Override
	public boolean existByPrefix(String prefix) {
		return daoIDS.existByPrefix(prefix);
	}

	@Override
	public boolean exists(String token) {
		return daoIDS.exists(token);
	}

	@Override
	public List<IDS> getAllAnalysisNoSubscribe(Integer idAnalysis) {
		return daoIDS.getAllAnalysisNoSubscribe(idAnalysis);
	}

	

}
