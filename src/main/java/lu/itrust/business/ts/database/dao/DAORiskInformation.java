package lu.itrust.business.ts.database.dao;

import java.util.Collection;
import java.util.List;

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.riskinformation.RiskInformation;

/**
 * DAORiskInformation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAORiskInformation {
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

	public List<RiskInformation> getAllByIdAnalysisAndCategories(Integer idAnalysis, String... types);

	public List<RiskInformation> findByIdAnalysisAndCategory(Integer idAnalysis, String type);
	
}