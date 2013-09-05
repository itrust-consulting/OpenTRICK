package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;

import org.hibernate.Query;

/**
 * DAOMeasureDescriptionText.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Feb 1, 2013
 */
public class DAOMeasureDescriptionTextHBM extends DAOHibernate implements
		lu.itrust.business.dao.DAOMeasureDescriptionText {

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * getByMeasureDescription: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#getByMeasureDescription(int)
	 */
	@Override
	public List<MeasureDescriptionText> getByMeasureDescription(int measureDescriptionID)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * getByMeasureDescriptionReferenceNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#getByMeasureDescriptionReferenceNorm(java.lang.String,
	 *      java.lang.String, lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<MeasureDescriptionText> getByMeasureDescriptionReferenceNorm(String Reference,
			String norm, Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * getByMeasureDescriptionReferenceNormLanguage: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#getByMeasureDescriptionReferenceNormLanguage(java.lang.String,
	 *      java.lang.String, lu.itrust.business.TS.Analysis, lu.itrust.business.TS.Language)
	 */
	@Override
	public MeasureDescriptionText getByMeasureDescriptionReferenceNormLanguage(String Reference,
			String norm, Analysis analysis, Language language) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#save(lu.itrust.business.dao.DAOMeasureDescriptionText)
	 */
	@Override
	public void save(MeasureDescriptionText measureDescription) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * saveAndUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#saveAndUpdate(lu.itrust.business.dao.DAOMeasureDescriptionText)
	 */
	@Override
	public void saveAndUpdate(MeasureDescriptionText measureDescription) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescriptionText#remove(lu.itrust.business.dao.DAOMeasureDescriptionText)
	 */
	@Override
	public void remove(MeasureDescriptionText measureDescription) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean exists(MeasureDescription measureDescription, Language language)
			throws Exception {
			Query query =
				getSession().createQuery("Select count(*) from MeasureDescriptionText where measureDescription = :measureDescription and language = :language");
			query.setParameter("measureDescription", measureDescription);
			query.setParameter("language", language);
			return (Long) query.uniqueResult() >= 1;
		
	}

}
