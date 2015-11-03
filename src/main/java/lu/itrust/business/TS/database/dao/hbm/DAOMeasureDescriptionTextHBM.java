package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * DAOMeasureDescriptionText.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOMeasureDescriptionTextHBM extends DAOHibernate implements lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText {

	/**
	 * Constructor: <br>
	 */
	public DAOMeasureDescriptionTextHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOMeasureDescriptionTextHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(Integer id) throws Exception {
		return (MeasureDescriptionText) getSession().get(MeasureDescription.class, id);
	}

	/**
	 * getMeasureDescriptionTextByIdAndLanguageId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#getMeasureDescriptionTextByIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception {
		String query = "from MeasureDescriptionText where measureDescription.id = :idMeasureDescription and language.id = :idLanguage";
		return (MeasureDescriptionText) getSession().createQuery(query).setParameter("idMeasureDescription", idMeasureDescription).setParameter("idLanguage", idLanguage).uniqueResult();
	}

	/**
	 * existsForLanguageByMeasureDescriptionIdAndLanguageId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#existsForLanguageByMeasureDescriptionIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception {
		String query = "Select count(*) from MeasureDescriptionText where measureDescription.id = :idMeasureDescription and language.id = :idLanguage";
		return (Long) getSession().createQuery(query).setParameter("idMeasureDescription", idMeasureDescription).setParameter("idLanguage", idLanguage).uniqueResult() >= 1;
	}

	/**
	 * getAllMeasureDescriptionTextsByMeasureDescriptionId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#getAllMeasureDescriptionTextsByMeasureDescriptionId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID) throws Exception {
		String query = "from MeasureDescriptionText where measureDescription.id = :measureDescriptionid";
		return (List<MeasureDescriptionText>) getSession().createQuery(query).setParameter("measureDescriptionid", measureDescriptionID).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#save(lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Override
	public void save(MeasureDescriptionText measureDescriptiontext) throws Exception {
		getSession().save(measureDescriptiontext);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#saveOrUpdate(lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Override
	public void saveOrUpdate(MeasureDescriptionText measureDescriptiontext) throws Exception {
		getSession().saveOrUpdate(measureDescriptiontext);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText#delete(lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Override
	public void delete(MeasureDescriptionText measureDescriptiontext) throws Exception {
		getSession().delete(measureDescriptiontext);
	}
}