package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;

/**
 * ServiceNorm.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceNorm {
	public Norm get(Integer id) throws Exception;

	public Norm getNormByName(String norm) throws Exception;

	public Norm getNormNotCustomByName(String norm) throws Exception;

	public Norm getNormByNameAndVersion(String label, int version) throws Exception;

	public boolean existsByNameAndVersion(String label, int version) throws Exception;

	public List<Norm> getAll() throws Exception;

	public List<Norm> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<Norm> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<Norm> getAllNotInAnalysis(Integer idAnalysis) throws Exception;

	public void save(Norm Norm) throws Exception;

	public void saveOrUpdate(Norm Norm) throws Exception;

	public void delete(Norm Norm) throws Exception;
}