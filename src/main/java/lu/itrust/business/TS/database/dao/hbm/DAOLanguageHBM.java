package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOLanguage;
import lu.itrust.business.TS.model.general.Language;

/**
 * DAOLanguageHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
@Repository
public class DAOLanguageHBM extends DAOHibernate implements DAOLanguage {

	/**
	 * Constructor: <br>
	 */
	public DAOLanguageHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOLanguageHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#get(int)
	 */
	@Override
	public Language get(Integer id)  {
		return (Language) getSession().get(Language.class, id);
	}

	/**
	 * languageExistsByAlpha3: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#languageExistsByAlpha3(java.lang.String)
	 */
	@Override
	public boolean existsByAlpha3(String alpha3)  {
		return ((Long) getSession().createQuery("select count(*) From Language where alpha3 = :alpha3").setString("alpha3", alpha3.toUpperCase()).uniqueResult()).intValue() > 0;
	}

	/**
	 * languageExistsByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#languageExistsByName(java.lang.String)
	 */
	@Override
	public boolean existsByName(String name)  {
		return ((Long) getSession().createQuery("select count(*) From Language where name = :name").setString("name", name).uniqueResult()).intValue() > 0;
	}

	/**
	 * languageExistsByAltName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#languageExistsByAltName(java.lang.String)
	 */
	@Override
	public boolean existsByAltName(String altName)  {
		return ((Long) getSession().createQuery("select count(*) From Language where altName = :altName").setString("altName", altName).uniqueResult()).intValue() > 0;
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#getLanguageOfAnalysis(java.lang.Integer)
	 */
	@Override
	public Language getFromAnalysis(Integer idAnalysis)  {
		return (Language) getSession().createQuery("Select analysis.language from Analysis analysis where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis)
				.uniqueResult();
	}

	/**
	 * getLanguageByAlpha3: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#getLanguageByAlpha3(java.lang.String)
	 */
	@Override
	public Language getByAlpha3(String alpha3)  {
		return (Language) getSession().createQuery("From Language where alpha3 = :alpha3").setParameter("alpha3", alpha3.toUpperCase()).uniqueResult();
	}

	/**
	 * getLanguageByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#getLanguageByName(java.lang.String)
	 */
	@Override
	public Language getByName(String name)  {
		return (Language) getSession().createQuery("From Language where name = :name").setParameter("name", name).uniqueResult();
	}

	/**
	 * getLanguageByAltName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#getLanguageByAltName(java.lang.String)
	 */
	@Override
	public Language getByAltName(String alternativeName)  {
		return (Language) getSession().createQuery("From Language where altName = :altName").setParameter("altName", alternativeName).uniqueResult();
	}

	/**
	 * getAllLanguages: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#getAllLanguages()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Language> getAll()  {
		return (List<Language>) getSession().createQuery("From Language").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#save(lu.itrust.business.TS.model.general.Language)
	 */
	@Override
	public void save(Language language)  {
		language.setAlpha3(language.getAlpha3().toUpperCase());
		getSession().save(language);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#saveOrUpdate(lu.itrust.business.TS.model.general.Language)
	 */
	@Override
	public void saveOrUpdate(Language language)  {
		language.setAlpha3(language.getAlpha3().toUpperCase());
		getSession().saveOrUpdate(language);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer languageId)  {
		getSession().createQuery("delete from Language where id = :languageId").setParameter("languageId", languageId).executeUpdate();
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOLanguage#delete(lu.itrust.business.TS.model.general.Language)
	 */
	@Override
	public void delete(Language language)  {
		getSession().delete(language);
	}

	@Override
	public boolean isInUse(Language language) {
		return (boolean) getSession().createQuery("Select count(*) > 0 From Analysis where language = :language").setParameter("language", language).uniqueResult();
	}

	@Override
	public boolean existsByIdAndAlpha3(int id, String alpha3) {
		return (boolean) getSession().createQuery("Select count(*) > 0 From Language where id <> :id and alpha3 = :alpha3").setInteger("id", id)
				.setParameter("alpha3", String.valueOf(alpha3).toUpperCase()).uniqueResult();
	}

	@Override
	public boolean existsByIdAndName(int id, String name) {
		return (boolean) getSession().createQuery("Select count(*) > 0 From Language where id <> :id and name = :name").setInteger("id", id)
				.setParameter("name", String.valueOf(name).toUpperCase()).uniqueResult();
	}

	@Override
	public boolean existsByIdAndAltName(int id, String altName) {
		return (boolean) getSession().createQuery("Select count(*) > 0 From Language where id <> :id and altName = :altName").setInteger("id", id)
				.setParameter("altName", String.valueOf(altName).toUpperCase()).uniqueResult();
	}
}