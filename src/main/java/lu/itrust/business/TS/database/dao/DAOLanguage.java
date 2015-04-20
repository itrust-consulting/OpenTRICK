package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.general.Language;

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

	public boolean existsByAlpha3(String alpha3) throws Exception;

	public boolean existsByName(String name) throws Exception;

	public boolean existsByAltName(String altName) throws Exception;

	public Language getFromAnalysis(Integer idAnalysis) throws Exception;

	public Language getByAlpha3(String alpha3) throws Exception;

	public Language getByName(String name) throws Exception;

	public Language getByAltName(String alternativeName) throws Exception;

	public List<Language> getAll() throws Exception;

	public void save(Language language) throws Exception;

	public void saveOrUpdate(Language language) throws Exception;

	public void delete(Integer languageID) throws Exception;

	public void delete(Language language) throws Exception;

	public boolean isInUse(Language language);
}