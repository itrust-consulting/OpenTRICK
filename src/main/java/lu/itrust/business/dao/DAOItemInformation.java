package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.ItemInformation;

/**
 * DAOItemInformation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOItemInformation {
	public ItemInformation get(Integer id) throws Exception;

	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idIteminformation) throws Exception;
	
	public ItemInformation getFromAnalysisByDescription(Integer analysisId, String description) throws Exception;

	public boolean belongsToAnalysis(Integer analysisId, Integer historyId) throws Exception;

	public List<ItemInformation> getAll() throws Exception;

	public List<ItemInformation> getAllFromAnalysis(Integer analysisId) throws Exception;

	public void save(ItemInformation itemInformation) throws Exception;

	public void saveOrUpdate(ItemInformation itemInformation) throws Exception;

	public void delete(ItemInformation itemInformation) throws Exception;
}