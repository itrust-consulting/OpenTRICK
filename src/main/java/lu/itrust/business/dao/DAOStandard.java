package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.StandardType;

/**
 * DAOStandard.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOStandard {
	public Standard get(Integer id) throws Exception;

	public Standard getStandardByName(String standard) throws Exception;

	public Standard getStandardNotCustomByName(String standard) throws Exception;

	public Standard getStandardByNameAndVersion(String label, Integer version) throws Exception;

	public boolean existsByNameAndVersion(String label, Integer version) throws Exception;

	public List<Standard> getAll() throws Exception;

	public List<Standard> getAllNotBoundToAnalysis() throws Exception;
	
	public List<Standard> getAllFromAnalysis(Integer analysisId) throws Exception;
	
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId) throws Exception;

	public List<Standard> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) throws Exception;

	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) throws Exception;
	
	public Integer getBiggestVersionFromStandardByNameAndType(String label, StandardType standardType) throws Exception;
	
	public void save(Standard standard) throws Exception;

	public void saveOrUpdate(Standard standard) throws Exception;

	public void delete(Standard standard) throws Exception;
}