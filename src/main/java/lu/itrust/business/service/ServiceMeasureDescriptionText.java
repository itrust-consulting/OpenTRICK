package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.dao.DAOMeasureDescriptionText;

/**
 * ServiceMeasureDescriptionText.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Oct 15, 2013
 */
public interface ServiceMeasureDescriptionText {

	public MeasureDescriptionText get(int id) throws Exception;

	public List<MeasureDescriptionText> getByMeasureDescription(int measureDescriptionID) throws Exception;

	public boolean exists(MeasureDescription mesDesc, Language language) throws Exception;

	public MeasureDescriptionText getByLanguage(MeasureDescription mesDesc, Language language) throws Exception;

	public void save(MeasureDescriptionText measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescriptionText measureDescription) throws Exception;

	public void remove(MeasureDescriptionText measureDescription) throws Exception;

	public DAOMeasureDescriptionText getDaoMeasureDescriptionText();

}
