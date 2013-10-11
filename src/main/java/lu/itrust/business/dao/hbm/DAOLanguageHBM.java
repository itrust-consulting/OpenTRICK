package lu.itrust.business.dao.hbm;

import java.util.List;
import lu.itrust.business.TS.Language;
import lu.itrust.business.dao.DAOLanguage;
import org.hibernate.Query;

/**
 * DAOLanguageHBM.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since 16 janv. 2013
 */
public class DAOLanguageHBM extends DAOHibernate implements DAOLanguage {

	@Override
	public Language get(int id) throws Exception {
		return (Language) getSession().get(Language.class, id);

	}

	/**
	 * loadFromAlpha3: <br>
	 * load language from alpha3
	 * 
	 * @see lu.itrust.business.dao.DAOLanguage#loadFromAlpha3(java.lang.String)
	 */
	@Override
	public Language loadFromAlpha3(String alpha3) throws Exception {

	
			// prepare statement
			Query query = getSession().createQuery("From Language where alpha3 = :alpha3");

			// sets data
			query.setParameter("alpha3", alpha3);

			return (Language) query.uniqueResult();
	
	}

	@Override
	public Language loadFromName(String name) throws Exception {

		Query query = getSession().createQuery("From Language where name = :name");

		query.setString("name", name);

		return (Language) query.uniqueResult();
	}

	@Override
	public Language loadFromAlternativeName(String alternativeName) throws Exception {
		
		// prepare statement
		Query query = getSession().createQuery("From Language where altName = :altName");

		// sets data
		query.setString("altName", alternativeName);

		// execute query

		return (Language) query.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Language> loadAll() throws Exception {
	
		// prepare statement
		Query query = getSession().createQuery("From Language");

		// execute query

		return (List<Language>) query.list();
	}

	@Override
	public void save(Language language) throws Exception {
		getSession().save(language);
	}

	@Override
	public void saveOrUpdate(Language language) throws Exception {
		// open session
		getSession().saveOrUpdate(language);
		// close session

	}

	@Override
	public void remove(Language language) throws Exception {
		getSession().delete(language);
	}
	
	@Override
	public void remove(Integer languageId) throws Exception {
		Query query = getSession().createQuery(
				"delete from Language where id = :languageId");
		query.setParameter("languageId", languageId);
		query.executeUpdate();
	}

}
