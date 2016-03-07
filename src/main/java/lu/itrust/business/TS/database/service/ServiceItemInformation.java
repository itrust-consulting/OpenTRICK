package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.iteminformation.ItemInformation;

/**
 * ServiceItemInformation.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceItemInformation {
	public ItemInformation get(Integer id) ;

	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idItemInformation) ;
	
	public ItemInformation getFromAnalysisByDescription(Integer analysisId, String description) ;

	public boolean belongsToAnalysis(Integer analysisId, Integer historyId) ;

	public List<ItemInformation> getAll() ;

	public List<ItemInformation> getAllFromAnalysis(Integer analysisId) ;

	public void save(ItemInformation itemInformation) ;

	public void saveOrUpdate(ItemInformation itemInformation) ;

	public void delete(ItemInformation itemInformation) ;
}