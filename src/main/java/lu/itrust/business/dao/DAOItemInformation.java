package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.ItemInformation;

/**
 * DAOItemInformation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOItemInformation {
	public ItemInformation get(int id) throws Exception;

	public ItemInformation getFromAnalysisIdByDescription(int analysisId, String description) throws Exception;

	boolean belongsToAnalysis(Integer historyId, Integer analysisId) throws Exception;

	public List<ItemInformation> getAllItemInformation() throws Exception;

	public List<ItemInformation> getAllFromAnalysisId(int analysisId) throws Exception;

	public void save(ItemInformation itemInformation) throws Exception;

	public void saveOrUpdate(ItemInformation itemInformation) throws Exception;

	public void delete(ItemInformation itemInformation) throws Exception;
}