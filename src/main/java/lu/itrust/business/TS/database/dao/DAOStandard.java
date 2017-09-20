package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;

/**
 * DAOStandard.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOStandard {
	public Standard get(Integer id);

	public List<Standard> getStandardByName(String standard);

	public Standard getStandardNotCustomByName(String standard);

	public Standard getStandardByNameAndVersion(String label, Integer version);

	public boolean existsByNameAndVersion(String label, Integer version);

	public boolean existsByNameVersionType(String label, Integer version, StandardType type);
	
	public List<Standard> getAll();

	public List<Standard> getAllNotBoundToAnalysis();
	
	public List<Standard> getAllFromAnalysis(Integer analysisId);
	
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId);

	public List<Standard> getAllFromAnalysis(Analysis analysis);

	public List<Standard> getAllNotInAnalysis(Integer idAnalysis);

	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID);
	
	public Integer getNextVersionByNameAndType(String label, StandardType standardType);
	
	public void save(Standard standard);

	public void saveOrUpdate(Standard standard);

	public void delete(Standard standard);

	public boolean belongToAnalysis(Integer idStandard, int idAnalysis);

	public int getNextVersion(String label);

	public boolean isUsed(Standard standard);

	public List<Standard> getAllNotInAnalysisAndNotMaturity(Integer idAnalysis);
}