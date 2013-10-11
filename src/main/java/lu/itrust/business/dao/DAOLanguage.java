package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Language;

/** 
 * DAOLanguage.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.�.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOLanguage {
	
	public Language get(int id) throws Exception;
	
	public Language loadFromAlpha3(String alpha3) throws Exception;
	
	public Language loadFromName(String name) throws Exception;
	
	public Language loadFromAlternativeName(String alternativeName) throws Exception;
	
	public List<Language> loadAll() throws Exception;
	
	public void save(Language language) throws Exception;
	
	public void saveOrUpdate(Language language) throws Exception;
	
	public void remove(Language language)throws Exception;

	public void remove(Integer languageID)throws Exception;
}
