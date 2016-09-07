package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.iteminformation.ItemInformation;

/**
 * DAOItemInformation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOItemInformation {
	public ItemInformation get(Integer id);

	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idIteminformation);
	
	public ItemInformation getFromAnalysisByDescription(Integer analysisId, String description);

	public boolean belongsToAnalysis(Integer analysisId, Integer historyId);

	public List<ItemInformation> getAll();

	public List<ItemInformation> getAllFromAnalysis(Integer analysisId);

	public void save(ItemInformation itemInformation);

	public void saveOrUpdate(ItemInformation itemInformation);

	public void delete(ItemInformation itemInformation);
	
}