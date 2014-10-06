package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;

/**
 * DAORiskInformation.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAORiskInformation {
	public RiskInformation get(Integer id) throws Exception;

	public RiskInformation getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer riskinformationId) throws Exception;

	public List<RiskInformation> getAll() throws Exception;

	public List<RiskInformation> getAllByChapter(String chapter) throws Exception;

	public List<RiskInformation> getAllByCategory(String category) throws Exception;

	public List<RiskInformation> getAllFromAnalysis(Integer analysisId) throws Exception;

	public List<RiskInformation> getAllFromAnalysis(Analysis analysis) throws Exception;

	public void save(RiskInformation riskInformation) throws Exception;

	public void saveOrUpdate(RiskInformation riskInformation) throws Exception;

	public void delete(RiskInformation riskInformation) throws Exception;
	
}