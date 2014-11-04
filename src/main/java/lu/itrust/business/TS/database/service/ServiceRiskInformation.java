package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.riskinformation.RiskInformation;

/**
 * ServiceRiskInformation.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceRiskInformation {
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