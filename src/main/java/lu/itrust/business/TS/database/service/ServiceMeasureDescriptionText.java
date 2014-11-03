package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.data.basic.MeasureDescriptionText;

/**
 * ServiceMeasureDescriptionText.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.a.rl.
 * @version 
 * @since Jan 16, 2013
 */
public interface ServiceMeasureDescriptionText {
	public MeasureDescriptionText get(Integer id) throws Exception;

	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception;

	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception;

	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID) throws Exception;

	public void save(MeasureDescriptionText measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescriptionText measureDescription) throws Exception;

	public void delete(MeasureDescriptionText measureDescription) throws Exception;
}