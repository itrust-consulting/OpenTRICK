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
	public MeasureDescriptionText get(int id) throws Exception;

	public MeasureDescriptionText getMeasureDescriptionTextByIdAndLanguageId(int idMeasureDescription, int idLanguage) throws Exception;

	public boolean existsForLanguageByMeasureDescriptionIdAndLanguageId(int idMeasureDescription, int idLanguage) throws Exception;

	public List<MeasureDescriptionText> getAllMeasureDescriptionTextsByMeasureDescriptionId(int measureDescriptionID) throws Exception;

	public void save(MeasureDescriptionText measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescriptionText measureDescription) throws Exception;

	public void delete(MeasureDescriptionText measureDescription) throws Exception;
}