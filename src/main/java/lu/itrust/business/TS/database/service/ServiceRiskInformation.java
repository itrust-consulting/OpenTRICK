package lu.itrust.business.TS.database.service;

import java.util.Collection;
import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;

/**
 * ServiceRiskInformation.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceRiskInformation {
	public RiskInformation get(Integer id);

	public RiskInformation getFromAnalysisById(Integer idAnalysis, Integer id);

	public boolean belongsToAnalysis(Integer analysisId, Integer riskinformationId);

	public List<RiskInformation> getAll();

	public List<RiskInformation> getAllByChapter(String chapter);

	public List<RiskInformation> getAllByCategory(String category);

	public List<RiskInformation> getAllFromAnalysis(Integer analysisId);

	public List<RiskInformation> getAllFromAnalysis(Analysis analysis);

	public void save(RiskInformation riskInformation);

	public void saveOrUpdate(RiskInformation riskInformation);

	public void delete(RiskInformation riskInformation);

	public void delete(Collection<RiskInformation> riskInformations);

	public List<RiskInformation> getAllByIdAnalysisAndCategory(Integer idAnalysis, String...types);

	public List<RiskInformation> findByIdAnalysisAndCategory(Integer idAnalysis, String type);
}