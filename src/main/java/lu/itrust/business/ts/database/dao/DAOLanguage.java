package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.general.Language;

/**
 * DAOLanguage.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public interface DAOLanguage {
	public Language get(Integer id);

	public boolean existsByAlpha3(String alpha3);

	public boolean existsByName(String name);

	public boolean existsByAltName(String altName);

	public Language getFromAnalysis(Integer idAnalysis);

	public Language getByAlpha3(String alpha3);

	public Language getByName(String name);

	public Language getByAltName(String alternativeName);

	public List<Language> getAll();

	public void save(Language language);

	public void saveOrUpdate(Language language);

	public void delete(Integer languageID);

	public void delete(Language language);

	public boolean isInUse(Language language);

	public boolean existsByIdAndAlpha3(int id, String alpha3);

	public boolean existsByIdAndName(int id, String name);

	public boolean existsByIdAndAltName(int id, String altName);

	public List<Language> getByAlpha3(String... alpha3s);

	public boolean existsByAlpha3(String... alpha3s);
}