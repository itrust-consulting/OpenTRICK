package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.MeasureDescriptionText;

/**
 * MeasureDescriptionText.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
public interface DAOMeasureDescriptionText {
	public MeasureDescriptionText get(Integer id) throws Exception;

	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception;

	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception;

	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID) throws Exception;

	public void save(MeasureDescriptionText measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescriptionText measureDescription) throws Exception;

	public void delete(MeasureDescriptionText measureDescription) throws Exception;
}