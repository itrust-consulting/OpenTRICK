package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.ItemInformation;

/** 
 * DAOItemInformation.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOItemInformation {
	public ItemInformation get(int id) throws Exception;
	
	public ItemInformation loadFromDescription(String description) throws Exception;
	
	public List<ItemInformation> loadAllFromAnalysisID(int analysisID) throws Exception;
	
	public List<ItemInformation> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception;
	
	public List<ItemInformation> loadAll() throws Exception;
	
	public void save(ItemInformation itemInformation) throws Exception;
	
	public void saveOrUpdate(ItemInformation itemInformation) throws Exception;
	
	public void remove(ItemInformation itemInformation)throws Exception;
}
