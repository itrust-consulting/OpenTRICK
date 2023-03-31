/**
 * 
 */
package lu.itrust.business.ts.database.dao;

import java.util.Date;
import java.util.List;

import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.FilterControl;
import lu.itrust.business.ts.usermanagement.User;

/**
 * @author eomar
 *
 */
public interface DAOWordReport {

	WordReport get(Long id);

	WordReport getByName(String fileName);

	WordReport getByIdAndUser(Long id, String username);

	List<WordReport> getAllFromUser(String username);

	List<WordReport> getAllFromUser(String username, Integer pageIndex, Integer pageSize);

	List<WordReport> getAllFromUserByFilterControl(String username, Integer page, FilterControl filter);

	List<WordReport> getAllFromUserAndIdentifier(String username, String identifier, Integer pageIndex, Integer pageSize);

	WordReport save(WordReport wordReport);

	void saveOrUpdate(WordReport wordReport);

	WordReport merge(WordReport wordReport);

	void delete(Long id);

	void delete(String filename);

	void delete(WordReport wordReport);

	List<String> getDistinctIdentifierByUser(User user);

	void deleteByUser(User user);

	List<WordReport> findByCreatedBefore(Date date, int page, int size);

	long countByCreatedBefore(Date date);

}
