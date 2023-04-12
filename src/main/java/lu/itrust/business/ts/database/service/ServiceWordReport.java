/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface ServiceWordReport {

	WordReport get(Long id);

	WordReport getByName(String fileName);

	WordReport getByIdAndUser(Long id, String username);

	List<WordReport> getAllFromUser(String username);

	List<WordReport> getAllFromUser(String username, Integer pageIndex, Integer pageSize);
	
	List<WordReport> getAllFromUserByFilterControl(String name, Integer page, FilterControl filter);
	
	List<WordReport> getAllFromUserAndIdentifier(String username,String identifier, Integer pageIndex, Integer pageSize);

	WordReport save(WordReport wordReport);

	void saveOrUpdate(WordReport wordReport);

	WordReport merge(WordReport wordReport);

	void delete(Long id);
	
	void delete(String filename);

	void delete(WordReport wordReport);

	List<String> getDistinctIdentifierByUser(User user);

}
