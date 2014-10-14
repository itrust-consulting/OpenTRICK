package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Standard;

/**
 * ServiceStandard.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceStandard {
	public Standard get(Integer id) throws Exception;

	public Standard getStandardByName(String standard) throws Exception;

	public Standard getStandardNotCustomByName(String standard) throws Exception;

	public Standard getStandardByNameAndVersion(String label, int version) throws Exception;

	public boolean existsByNameAndVersion(String label, int version) throws Exception;

	public List<Standard> getAll() throws Exception;

	public List<Standard> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<Standard> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) throws Exception;

	public List<Standard> getAllNotBoundToAnalysis() throws Exception;
	
	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) throws Exception;
	
	public void save(Standard standard) throws Exception;

	public void saveOrUpdate(Standard standard) throws Exception;

	public void delete(Standard standard) throws Exception;
}