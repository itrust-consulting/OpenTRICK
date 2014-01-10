/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.dao.DAOParameter;
import lu.itrust.business.service.ServiceParameter;

/**
 * @author eom
 * 
 */
@Service
public class ServiceParameterImpl implements ServiceParameter {

	@Autowired
	private DAOParameter daoParameter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#get(int)
	 */
	@Override
	public Parameter get(int id) {
		return daoParameter.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#findAll()
	 */
	@Override
	public List<Parameter> findAll() {
		return daoParameter.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#findAll(int, int)
	 */
	@Override
	public List<Parameter> findAll(int pageIndex, int pageSize) {
		return daoParameter.findAll(pageIndex, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#findByAnalysis(int, int,
	 * int)
	 */
	@Override
	public List<Parameter> findByAnalysis(int idAnalysis, int pageIndex,
			int pageSize) {
		return daoParameter.findByAnalysis(idAnalysis, pageIndex, pageSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameter#save(lu.itrust.business.TS
	 * .Parameter)
	 */
	@Transactional
	@Override
	public Parameter save(Parameter parameter) {
		return daoParameter.save(parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameter#saveOrUpdate(lu.itrust.business
	 * .TS.Parameter)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Parameter parameter) {
		daoParameter.saveOrUpdate(parameter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameter#merge(lu.itrust.business.
	 * TS.Parameter)
	 */
	@Transactional
	@Override
	public Parameter merge(Parameter parameter) {
		return daoParameter.merge(parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceParameter#delete(lu.itrust.business
	 * .TS.Parameter)
	 */
	@Transactional
	@Override
	public void delete(Parameter parameter) {
		daoParameter.delete(parameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceParameter#delete(int)
	 */
	@Transactional
	@Override
	public void delete(int id) {
		daoParameter.delete(id);
	}
	
	@Transactional
	@Override
	public void saveOrUpdate(List<? extends Parameter> parameters) {
		daoParameter.saveOrUpdate( parameters);
	}

	@Override
	public List<ExtendedParameter> findImpactByAnalysisAndType(int idAnalysis) {
		return daoParameter.findImpactByAnalysisAndType(idAnalysis);
	}

	@Override
	public List<ExtendedParameter> findProbaByAnalysisAndType(int idAnalysis) {
		return daoParameter.findProbaByAnalysisAndType(idAnalysis);
	}

	@Override
	public List<ExtendedParameter> findExtendedByAnalysisAndType(
			int idAnalysis, ParameterType type) {
		return daoParameter.findExtendedByAnalysisAndType(idAnalysis, type);
	}

	@Override
	public List<Parameter> findByAnalysisAndType(int idAnalysis, String type) {
		return daoParameter.findByAnalysisAndType(idAnalysis, type);
	}

	@Override
	public List<Parameter> findByAnalysisAndType(int idAnalysis, int idType) {
		return daoParameter.findByAnalysisAndType(idAnalysis, idType);
	}

	@Override
	public List<Parameter> findByAnalysisAndType(int idAnalysis,
			ParameterType type) {
		return daoParameter.findByAnalysisAndType(idAnalysis, type);
	}

	@Override
	public List<Parameter> findByAnalysis(int idAnalysis) {
		return daoParameter.findByAnalysis(idAnalysis);
	}

	@Override
	public List<ExtendedParameter> findExtendedByAnalysis(int idAnalysis) {
		return daoParameter.findExtendedByAnalysis(idAnalysis);
	}

	@Override
	public List<Parameter> findByAnalysisAndTypeAndNoLazy(int idAnalysis,
			String type) {
		return daoParameter.findByAnalysisAndTypeAndNoLazy(idAnalysis, type);
	}

	@Override
	public List<String> findAcronymByAnalysis(int idAnalysis) {
		return daoParameter.findAcronymByAnalysis(idAnalysis);
	}

	@Override
	public List<String> findAcronymByAnalysisAndType(int idAnalysis, String type) {
		return daoParameter.findAcronymByAnalysisAndType(idAnalysis, type);
	}

	@Override
	public List<String> findAcronymByAnalysisAndType(int idAnalysis,
			ParameterType type) {
		return daoParameter.findAcronymByAnalysisAndType(idAnalysis, type);
	}

	

}
