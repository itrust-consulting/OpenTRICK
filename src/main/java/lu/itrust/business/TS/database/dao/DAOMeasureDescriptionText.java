package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * MeasureDescriptionText.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
public interface DAOMeasureDescriptionText {
	public MeasureDescriptionText get(Integer id) ;

	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) ;

	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) ;

	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID) ;

	public void save(MeasureDescriptionText measureDescription) ;

	public void saveOrUpdate(MeasureDescriptionText measureDescription) ;

	public void delete(MeasureDescriptionText measureDescription) ;
}