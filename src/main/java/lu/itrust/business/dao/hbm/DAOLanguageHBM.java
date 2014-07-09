package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.dao.DAOLanguage;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	 * @see lu.itrust.business.dao.DAOLanguage#get(int)
	 */
	@Override
	public Language get(Integer id) throws Exception {
		return (Language) getSession().get(Language.class, id);
	}

	/**
	 * languageExistsByAlpha3: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#languageExistsByAlpha3(java.lang.String)
	 */
	@Override
	public boolean existsByAlpha3(String alpha3) throws Exception {
		return ((Long) getSession().createQuery("select count(*) From Language where alpha3 = :alpha3").setString("alpha3", alpha3.toUpperCase()).uniqueResult()).intValue() > 0;
	}

	/**
	 * languageExistsByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#languageExistsByName(java.lang.String)
	 */
	@Override
	public boolean existsByName(String name) throws Exception {
		return ((Long) getSession().createQuery("select count(*) From Language where name = :name").setString("name", name).uniqueResult()).intValue() > 0;
	}

	/**
	 * languageExistsByAltName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#languageExistsByAltName(java.lang.String)
	 */
	@Override
	public boolean existsByAltName(String altName) throws Exception {
		return ((Long) getSession().createQuery("select count(*) From Language where altName = :altName").setString("altName", altName).uniqueResult()).intValue() > 0;
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#getLanguageOfAnalysis(java.lang.Integer)
	 */
	@Override
	public Language getFromAnalysis(Integer idAnalysis) throws Exception {
		return (Language) getSession().createQuery("Select analysis.language from Analysis analysis where analysis.id = :idAnalysis").setParameter("idAnalysis", idAnalysis).uniqueResult();
	}

	/**
	 * getLanguageByAlpha3: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#getLanguageByAlpha3(java.lang.String)
	 */
	@Override
	public Language getByAlpha3(String alpha3) throws Exception {
		return (Language) getSession().createQuery("From Language where alpha3 = :alpha3").setParameter("alpha3", alpha3.toUpperCase()).uniqueResult();
	}

	/**
	 * getLanguageByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#getLanguageByName(java.lang.String)
	 */
	@Override
	public Language getByName(String name) throws Exception {
		return (Language) getSession().createQuery("From Language where name = :name").setParameter("name", name).uniqueResult();
	}

	/**
	 * getLanguageByAltName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#getLanguageByAltName(java.lang.String)
	 */
	@Override
	public Language getByAltName(String alternativeName) throws Exception {
		return (Language) getSession().createQuery("From Language where altName = :altName").setParameter("altName", alternativeName).uniqueResult();
	}

	/**
	 * getAllLanguages: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#getAllLanguages()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Language> getAll() throws Exception {
		return (List<Language>) getSession().createQuery("From Language").list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#save(lu.itrust.business.TS.Language)
	 */
	@Override
	public void save(Language language) throws Exception {
		language.setAlpha3(language.getAlpha3().toUpperCase());
		getSession().save(language);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#saveOrUpdate(lu.itrust.business.TS.Language)
	 */
	@Override
	public void saveOrUpdate(Language language) throws Exception {
		language.setAlpha3(language.getAlpha3().toUpperCase());
		getSession().saveOrUpdate(language);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer languageId) throws Exception {
		getSession().createQuery("delete from Language where id = :languageId").setParameter("languageId", languageId).executeUpdate();
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#delete(lu.itrust.business.TS.Language)
	 */
	@Override
	public void delete(Language language) throws Exception {
		getSession().delete(language);
	}
}