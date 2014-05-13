package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Language;

/**
 * ServiceLanguage.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
public interface ServiceLanguage {
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