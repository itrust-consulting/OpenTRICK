package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOMeasureDescriptionText.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOMeasureDescriptionTextHBM extends DAOHibernate implements lu.itrust.business.dao.DAOMeasureDescriptionText {
	
	/**
	 * 
	 */
	public DAOMeasureDescriptionTextHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAOMeasureDescriptionTextHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(int id) throws Exception {
		return (MeasureDescriptionText) getSession().get(MeasureDescription.class, id);
	}

	/**
	 * getByMeasureDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#getByMeasureDescription(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescriptionText> getByMeasureDescription(int measureDescriptionID) throws Exception {
		Query query = getSession().createQuery("from MeasureDescriptionText where measureDescription.id = :measureDescriptionid");
		query.setParameter("measureDescriptionid", measureDescriptionID);
		return (List<MeasureDescriptionText>) query.list();
	}

	/**
	 * getByLanguage: <br>
	 * Description
	 *
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#getByLanguage(lu.itrust.business.TS.MeasureDescription, lu.itrust.business.TS.Language)
	 */
	@Override
	public MeasureDescriptionText getByLanguage(MeasureDescription mesDesc, Language language) throws Exception {
		Query query = getSession().createQuery("from MeasureDescriptionText where measureDescription = :measureDescription and language= :language");
		query.setParameter("measureDescription", mesDesc);
		return (MeasureDescriptionText) query.uniqueResult();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#save(lu.itrust.business.dao.DAOMeasureDescriptionText)
	 */
	@Override
	public void save(MeasureDescriptionText measureDescriptiontext) throws Exception {
		getSession().save(measureDescriptiontext);
	}

	/**
	 * saveAndUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#saveAndUpdate(lu.itrust.business.dao.DAOMeasureDescriptionText)
	 */
	@Override
	public void saveAndUpdate(MeasureDescriptionText measureDescriptiontext) throws Exception {
		getSession().saveOrUpdate(measureDescriptiontext);
	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#remove(lu.itrust.business.dao.DAOMeasureDescriptionText)
	 */
	@Override
	public void remove(MeasureDescriptionText measureDescriptiontext) throws Exception {
		getSession().delete(measureDescriptiontext);
	}

	@Override
	public boolean existsForLanguage(MeasureDescription mesDesc, Language language) throws Exception {
		Query query = getSession().createQuery("Select count(*) from MeasureDescriptionText where measureDescription = :measureDescription and language = :language");
		query.setParameter("measureDescription", mesDesc);
		query.setParameter("language", language);
		return (Long) query.uniqueResult() >= 1;

	}

}
