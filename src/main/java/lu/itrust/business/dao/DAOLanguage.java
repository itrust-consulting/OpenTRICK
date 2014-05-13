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
	public Language get(Integer id) throws Exception;

	public boolean languageExistsByAlpha3(String alpha3) throws Exception;

	public boolean languageExistsByName(String name) throws Exception;

	public boolean languageExistsByAltName(String altName) throws Exception;

	public Language getLanguageOfAnalysis(Integer idAnalysis) throws Exception;

	public Language getLanguageByAlpha3(String alpha3) throws Exception;

	public Language getLanguageByName(String name) throws Exception;

	public Language getLanguageByAltName(String alternativeName) throws Exception;

	public List<Language> getAll() throws Exception;

	public void save(Language language) throws Exception;

	public void saveOrUpdate(Language language) throws Exception;

	public void delete(Integer languageID) throws Exception;

	public void delete(Language language) throws Exception;
}