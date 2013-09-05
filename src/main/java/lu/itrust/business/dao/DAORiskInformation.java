package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;

/** 
 * DAORiskInformation.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAORiskInformation {
	public RiskInformation get(int id) throws Exception;
	
	public RiskInformation loadFromDescription(String description) throws Exception;
	
	public RiskInformation loadFromChapter(String chapter) throws Exception;
	
	public List<RiskInformation> loadFromCategory(String category) throws Exception;
	
	public List<RiskInformation> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<RiskInformation> loadAllFromAnalysisID(int analysisID) throws Exception;
	
	public List<RiskInformation> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception;
	
	public List<RiskInformation> loadAll() throws Exception;
	
	public void save(RiskInformation riskInformation) throws Exception;
	
	public void saveOrUpdate(RiskInformation riskInformation) throws Exception;
	
	public void remove(RiskInformation riskInformation)throws Exception;
}
