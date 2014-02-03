/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.service.ServiceNorm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author oensuifudine
 * 
 */
@Service
@Transactional
public class ServiceNormImpl implements ServiceNorm {

	@Autowired
	private DAONorm daoNorm;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#getNormByID(int)
	 */
	@Override
	public Norm getNormByID(int idNorm) throws Exception {
		return daoNorm.getNormByID(idNorm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#loadNotCustomNormByName(java.lang .String)
	 */
	@Override
	public Norm loadNotCustomNormByName(String norm) throws Exception {
		return daoNorm.loadNotCustomNormByName(norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#loadSingleNormByName(java.lang .String)
	 */
	@Override
	public Norm loadSingleNormByName(String norm) throws Exception {
		return daoNorm.loadSingleNormByName(norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#loadAllFromAnalysis(lu.itrust.
	 * business.TS.Analysis)
	 */
	@Override
	public List<Norm> loadAllFromAnalysis(Analysis analysis) throws Exception {
		return daoNorm.loadAllFromAnalysis(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#
	 * loadAllFromAnalysisIdentifierVersionCreationDate(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Norm> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception {
		return daoNorm.loadAllFromAnalysisIdentifierVersionCreationDate(identifier, version, creationDate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#loadAll()
	 */
	@Override
	public List<Norm> loadAll() throws Exception {
		return daoNorm.loadAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#save(lu.itrust.business.TS.Norm)
	 */
	@Transactional
	@Override
	public void save(Norm Norm) throws Exception {
		daoNorm.save(Norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#saveOrUpdate(lu.itrust.business .TS.Norm)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Norm Norm) throws Exception {
		daoNorm.saveOrUpdate(Norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceNorm#remove(lu.itrust.business.TS.Norm)
	 */
	@Transactional
	@Override
	public void remove(Norm Norm) throws Exception {
		daoNorm.remove(Norm);
	}

	/**
	 * @return the daoNorm
	 */
	public DAONorm getDaoNorm() {
		return daoNorm;
	}

	/**
	 * @param daoNorm
	 *            the daoNorm to set
	 */
	public void setDaoNorm(DAONorm daoNorm) {
		this.daoNorm = daoNorm;
	}

	@Override
	public Norm loadSingleNormByNameAndVersion(String label, int version) throws Exception {
		return daoNorm.loadSingleNormByNameAndVersion(label, version);
	}

	@Override
	public boolean exists(String label, int version) {
		// TODO Auto-generated method stub
		return daoNorm.exists(label, version);
	}
}