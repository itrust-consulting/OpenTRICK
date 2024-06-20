package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * ServiceMeasureDescriptionText.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.a.rl.
 * @version 
 * @since Jan 16, 2013
 */
public interface ServiceMeasureDescriptionText {
	public MeasureDescriptionText get(Integer id);

	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage);

	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage);

	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID);

	public void save(MeasureDescriptionText measureDescription);

	public void saveOrUpdate(MeasureDescriptionText measureDescription);

	public void delete(MeasureDescriptionText measureDescription);
}