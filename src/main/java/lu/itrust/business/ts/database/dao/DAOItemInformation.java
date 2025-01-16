package lu.itrust.business.ts.database.dao;

import java.util.Collection;
import java.util.List;

import lu.itrust.business.ts.model.iteminformation.ItemInformation;

/**
 * DAOItemInformation.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOItemInformation {
	ItemInformation get(Integer id);

	ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idIteminformation);

	ItemInformation getFromAnalysisByDescription(Integer analysisId, String description);

	boolean belongsToAnalysis(Integer analysisId, Integer historyId);

	List<ItemInformation> getAll();

	List<ItemInformation> getAllFromAnalysis(Integer analysisId);

	void save(ItemInformation itemInformation);

	void saveOrUpdate(ItemInformation itemInformation);

	void delete(ItemInformation itemInformation);

	void delete(Collection<ItemInformation> itemInformation);

}