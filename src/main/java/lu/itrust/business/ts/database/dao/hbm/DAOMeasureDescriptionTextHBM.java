package lu.itrust.business.ts.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * DAOMeasureDescriptionText.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOMeasureDescriptionTextHBM extends DAOHibernate implements lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText {

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
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(Integer id)  {
		return getSession().get(MeasureDescriptionText.class, id);
	}

	/**
	 * getMeasureDescriptionTextByIdAndLanguageId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#getMeasureDescriptionTextByIdAndLanguageId(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage)  {
		String query = "from MeasureDescriptionText where measureDescription.id = :idMeasureDescription and language.id = :idLanguage";
		return (MeasureDescriptionText) getSession().createQuery(query).setParameter("idMeasureDescription", idMeasureDescription).setParameter("idLanguage", idLanguage).uniqueResultOptional().orElse(null);
	}

	/**
	 * existsForLanguageByMeasureDescriptionIdAndLanguageId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#existsForLanguageByMeasureDescriptionIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage)  {
		String query = "Select count(*)>0 from MeasureDescriptionText where measureDescription.id = :idMeasureDescription and language.id = :idLanguage";
		return (boolean) getSession().createQuery(query).setParameter("idMeasureDescription", idMeasureDescription).setParameter("idLanguage", idLanguage).getSingleResult();
	}

	/**
	 * getAllMeasureDescriptionTextsByMeasureDescriptionId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#getAllMeasureDescriptionTextsByMeasureDescriptionId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID)  {
		String query = "from MeasureDescriptionText where measureDescription.id = :measureDescriptionid";
		return (List<MeasureDescriptionText>) getSession().createQuery(query).setParameter("measureDescriptionid", measureDescriptionID).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#save(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Override
	public void save(MeasureDescriptionText measureDescriptiontext)  {
		getSession().save(measureDescriptiontext);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#saveOrUpdate(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Override
	public void saveOrUpdate(MeasureDescriptionText measureDescriptiontext)  {
		getSession().saveOrUpdate(measureDescriptiontext);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText#delete(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Override
	public void delete(MeasureDescriptionText measureDescriptiontext)  {
		getSession().delete(measureDescriptiontext);
	}
}