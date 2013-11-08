/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.ItemInformation;

/**
 * @author eom
 * 
 */
public interface ServiceItemInformation {
	
	public ItemInformation get(int id) throws Exception;

	public ItemInformation loadFromDescription(String description)
			throws Exception;

	public List<ItemInformation> loadAllFromAnalysisID(int analysisID)
			throws Exception;

	public List<ItemInformation> loadAllFromAnalysisIdentifierVersionCreationDate(
			int identifier, String version, String creationDate)
			throws Exception;

	public List<ItemInformation> loadAll() throws Exception;

	public void save(ItemInformation itemInformation) throws Exception;

	public void saveOrUpdate(ItemInformation itemInformation) throws Exception;

	public void remove(ItemInformation itemInformation) throws Exception;
}
