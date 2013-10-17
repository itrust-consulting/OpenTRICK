package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
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
	public MeasureDescriptionText get(int id) throws Exception;

	public List<MeasureDescriptionText> getByMeasureDescription(int measureDescriptionID) throws Exception;

	public boolean existsForLanguage(MeasureDescription mesDesc, Language language) throws Exception;

	public MeasureDescriptionText getByLanguage(MeasureDescription mesDesc, Language language) throws Exception;

	public void save(MeasureDescriptionText measureDescription) throws Exception;

	public void saveAndUpdate(MeasureDescriptionText measureDescription) throws Exception;

	public void remove(MeasureDescriptionText measureDescription) throws Exception;

}
