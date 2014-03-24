/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.dao.DAOMeasure;
import lu.itrust.business.service.ServiceMeasure;

/**
 * @author eomar
 * 
 */
@Service
public class ServiceMeasureImpl implements ServiceMeasure {

	@Autowired
	private DAOMeasure daoMeasure;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#findOne(int)
	 */
	@Override
	public Measure findOne(int id) {
		return daoMeasure.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#findByAnalysis(int)
	 */
	@Override
	public List<Measure> findByAnalysis(int idAnalysis) {
		return daoMeasure.findByAnalysis(idAnalysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#findByAnalysisAndNorm(int,
	 * int)
	 */
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, int idNorm) {
		return daoMeasure.findByAnalysisAndNorm(idAnalysis, idNorm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#findByAnalysisAndNorm(int,
	 * java.lang.String)
	 */
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, String norm) {
		return daoMeasure.findByAnalysisAndNorm(idAnalysis, norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#findByAnalysisAndNorm(int,
	 * lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, Norm norm) {
		return daoMeasure.findByAnalysisAndNorm(idAnalysis, norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasure#save(lu.itrust.business.TS.
	 * Measure)
	 */
	@Transactional
	@Override
	public Measure save(Measure measure) {
		return daoMeasure.save(measure);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasure#saveOrUpdate(lu.itrust.business
	 * .TS.Measure)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Measure measure) {
		daoMeasure.saveOrUpdate(measure);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasure#merge(lu.itrust.business.TS
	 * .Measure)
	 */
	@Transactional
	@Override
	public Measure merge(Measure measure) {
		return daoMeasure.merge(measure);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasure#delete(lu.itrust.business.TS
	 * .Measure)
	 */
	@Transactional
	@Override
	public void delete(Measure measure) {
		daoMeasure.delete(measure);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#delete(int)
	 */
	@Transactional
	@Override
	public void delete(int id) {
		daoMeasure.delete(id);

	}

	@Override
	public Measure findByIdAndAnalysis(Integer id, Integer idAnalysis) {
		return daoMeasure.findByIdAndAnalysis(id,idAnalysis);
	}

	@Override
	public List<NormMeasure> findNormMeasureByAnalysis(int idAnalysis) {
		return daoMeasure.findNormMeasureByAnalysis(idAnalysis);
	}

	@Override
	public List<NormMeasure> findNormMeasureByAnalysisAndComputable(int idAnalysis) {
		return daoMeasure.findNormMeasureByAnalysisAndComputable(idAnalysis);
	}

}
