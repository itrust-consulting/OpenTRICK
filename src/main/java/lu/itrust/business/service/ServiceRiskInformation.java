package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;

public interface ServiceRiskInformation {

	public RiskInformation get(int id) throws Exception;
	
	public RiskInformation findByIdAndAnalysis(int id, int idAnalysis) throws Exception;

	public List<RiskInformation> loadFromChapter(String chapter) throws Exception;

	public List<RiskInformation> loadFromCategory(String category) throws Exception;

	public List<RiskInformation> loadAllFromAnalysis(Analysis analysis) throws Exception;

	public List<RiskInformation> loadAllFromAnalysisID(int analysisID) throws Exception;

	public List<RiskInformation> loadAll() throws Exception;

	public void save(RiskInformation riskInformation) throws Exception;

	public void saveOrUpdate(RiskInformation riskInformation) throws Exception;

	public void remove(RiskInformation riskInformation) throws Exception;
}
