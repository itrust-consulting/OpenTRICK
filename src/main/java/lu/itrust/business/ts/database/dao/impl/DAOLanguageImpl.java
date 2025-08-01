package lu.itrust.business.ts.database.dao.impl;

import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOLanguage;
import lu.itrust.business.ts.model.general.Language;

/**
 * DAOLanguageImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
@Repository
public class DAOLanguageImpl extends DAOHibernate implements DAOLanguage {

	/**
	 * Constructor: <br>
	 */
	public DAOLanguageImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOLanguageImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#get(int)
	 */
	@Override
	public Language get(Integer id) {
		return (Language) getSession().get(Language.class, id);
	}

	/**
	 * languageExistsByAlpha3: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#languageExistsByAlpha3(java.lang.String)
	 */
	@Override
	public boolean existsByAlpha3(String alpha3) {
		return (boolean) createQueryWithCache("select count(*) > 0 From Language where alpha3 = :alpha3").setParameter("alpha3", alpha3.toUpperCase()).getSingleResult();
	}

	/**
	 * languageExistsByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#languageExistsByName(java.lang.String)
	 */
	@Override
	public boolean existsByName(String name) {
		return (boolean) createQueryWithCache("select count(*) > 0 From Language where name = :name").setParameter("name", name).getSingleResult();
	}

	/**
	 * languageExistsByAltName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#languageExistsByAltName(java.lang.String)
	 */
	@Override
	public boolean existsByAltName(String altName) {
		return (boolean) createQueryWithCache("select count(*)>0 From Language where altName = :altName").setParameter("altName", altName).getSingleResult();
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#getLanguageOfAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Language getFromAnalysis(Integer idAnalysis) {
		return (Language) createQueryWithCache("Select analysis.language from Analysis analysis where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * getLanguageByAlpha3: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#getLanguageByAlpha3(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Language getByAlpha3(String alpha3) {
		return (Language) createQueryWithCache("From Language where alpha3 = :alpha3").setParameter("alpha3", alpha3.toUpperCase()).uniqueResultOptional().orElse(null);
	}

	/**
	 * getLanguageByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#getLanguageByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Language getByName(String name) {
		return (Language) createQueryWithCache("From Language where name = :name").setParameter("name", name).uniqueResultOptional().orElse(null);
	}

	/**
	 * getLanguageByAltName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#getLanguageByAltName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Language getByAltName(String alternativeName) {
		return (Language) createQueryWithCache("From Language where altName = :altName").setParameter("altName", alternativeName).uniqueResultOptional().orElse(null);
	}

	/**
	 * getAllLanguages: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#getAllLanguages()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Language> getAll() {
		return (List<Language>) createQueryWithCache("From Language").getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#save(lu.itrust.business.ts.model.general.Language)
	 */
	@Override
	public void save(Language language) {
		language.setAlpha3(language.getAlpha3().toUpperCase());
		getSession().save(language);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#saveOrUpdate(lu.itrust.business.ts.model.general.Language)
	 */
	@Override
	public void saveOrUpdate(Language language) {
		language.setAlpha3(language.getAlpha3().toUpperCase());
		getSession().saveOrUpdate(language);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer languageId) {
		createQueryWithCache("delete from Language where id = :languageId").setParameter("languageId", languageId).executeUpdate();
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOLanguage#delete(lu.itrust.business.ts.model.general.Language)
	 */
	@Override
	public void delete(Language language) {
		getSession().delete(language);
	}

	@Override
	public boolean isInUse(Language language) {
		return (boolean) createQueryWithCache("Select count(*) > 0 From Analysis where language = :language").setParameter("language", language).getSingleResult();
	}

	@Override
	public boolean existsByIdAndAlpha3(int id, String alpha3) {
		return (boolean) createQueryWithCache("Select count(*) > 0 From Language where id <> :id and alpha3 = :alpha3").setParameter("id", id)
				.setParameter("alpha3", String.valueOf(alpha3).toUpperCase()).getSingleResult();
	}

	@Override
	public boolean existsByIdAndName(int id, String name) {
		return (boolean) createQueryWithCache("Select count(*) > 0 From Language where id <> :id and name = :name").setParameter("id", id)
				.setParameter("name", String.valueOf(name).toUpperCase()).getSingleResult();
	}

	@Override
	public boolean existsByIdAndAltName(int id, String altName) {
		return (boolean) createQueryWithCache("Select count(*) > 0 From Language where id <> :id and altName = :altName").setParameter("id", id)
				.setParameter("altName", String.valueOf(altName).toUpperCase()).getSingleResult();
	}

	@Override
	public List<Language> getByAlpha3(String... alpha3s) {
		for (int i = 0; i < alpha3s.length; i++)
			alpha3s[i] = alpha3s[i].toUpperCase();
		return alpha3s.length == 0 ? Collections.emptyList()
				: createQueryWithCache("From Language where alpha3 in :alpha3s", Language.class).setParameterList("alpha3s", alpha3s).list();
	}

	@Override
	public boolean existsByAlpha3(String... alpha3s) {
		for (int i = 0; i < alpha3s.length; i++)
			alpha3s[i] = alpha3s[i].toUpperCase();
		return alpha3s.length == 0 ? true
				: createQueryWithCache("Select count(*) From Language where alpha3 in :alpha3s", Long.class).setParameterList("alpha3s", alpha3s)
						.uniqueResult() == alpha3s.length;
	}
}