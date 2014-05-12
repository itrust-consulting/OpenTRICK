package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;

/**
 * DAORiskInformation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAORiskInformation {

	public RiskInformation get(int id) throws Exception;

	public RiskInformation getFromAnalysisById(int id, int idAnalysis) throws Exception;

	public boolean belongsToAnalysis(Integer riskinformationId, Integer analysisId) throws Exception;

	public List<RiskInformation> getAllRiskInformation() throws Exception;

	public List<RiskInformation> getAllByChapter(String chapter) throws Exception;

	public List<RiskInformation> getAllByCategory(String category) throws Exception;

	public List<RiskInformation> getAllFromAnalysisId(int analysisId) throws Exception;

	public List<RiskInformation> getAllFromAnalysis(Analysis analysis) throws Exception;

	public void save(RiskInformation riskInformation) throws Exception;

	public void saveOrUpdate(RiskInformation riskInformation) throws Exception;

	public void delete(RiskInformation riskInformation) throws Exception;
}