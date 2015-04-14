/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.general.helper.FilterControl;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface DAOWordReport {

	WordReport get(Integer id);

	WordReport getByFilename(String fileName);

	WordReport getByIdAndUser(Integer id, String username);

	List<WordReport> getAllFromUser(String username);

	List<WordReport> getAllFromUser(String username, Integer pageIndex, Integer pageSize);
	
	List<WordReport> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter);
	
	List<WordReport> getAllFromUserAndIdentifier(String username,String identifier, Integer pageIndex, Integer pageSize);

	WordReport save(WordReport wordReport);

	void saveOrUpdate(WordReport wordReport);

	WordReport merge(WordReport wordReport);

	void delete(Integer id);
	
	void delete(String filename);

	void delete(WordReport wordReport);

	List<String> getDistinctIdentifierByUser(User user);

	

}
