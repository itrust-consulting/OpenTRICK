package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;

/**
 * DAONorm.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAONorm {

	public Norm get(int id) throws Exception;

	public Norm getNormByName(String norm) throws Exception;

	public Norm getNormNotCustomByName(String norm) throws Exception;

	public Norm getNormByNameAndVersion(String label, int version) throws Exception;

	public boolean existsByNameAndVersion(String label, int version) throws Exception;

	public List<Norm> getAllNorms() throws Exception;

	public List<Norm> getAllFromAnalysisId(int analysisId) throws Exception;

	public List<Norm> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<Norm> getAllNormsNotInAnalysis(int idAnalysis) throws Exception;

	public void save(Norm Norm) throws Exception;

	public void saveOrUpdate(Norm Norm) throws Exception;

	public void delete(Norm Norm) throws Exception;
}