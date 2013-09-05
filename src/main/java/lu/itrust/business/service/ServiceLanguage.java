/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.dao.DAOLanguage;

/**
 * @author oensuifudine
 *
 */
public interface ServiceLanguage {
	
public Language get(int id) throws Exception;
	
	public Language loadFromAlpha3(String alpha3) throws Exception;
	
	public Language loadFromName(String name) throws Exception;
	
	public Language loadFromAlternativeName(String alternativeName) throws Exception;
	
	public List<Language> loadAll() throws Exception;
	
	public void save(Language language) throws Exception;
	
	public void saveOrUpdate(Language language) throws Exception;
	
	public void remove(Language language)throws Exception;
	
	public DAOLanguage getDaoLanguage();

}
