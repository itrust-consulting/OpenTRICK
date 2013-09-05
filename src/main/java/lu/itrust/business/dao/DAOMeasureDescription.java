package lu.itrust.business.dao;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;

/** 
 * DAOMeasureDescription.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Feb 1, 2013
 */
public interface DAOMeasureDescription {
	public MeasureDescription get(int id) throws Exception;
	public MeasureDescription getByReferenceNorm(String Reference, Norm norm) throws Exception;
	public boolean exists(String Reference, Norm norm) throws Exception;
	public boolean existsWithLanguage(String Reference, Norm norm, Language language) throws Exception;
	public void save(MeasureDescription measureDescription) throws Exception;
	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception;
	public void remove(MeasureDescription measureDescription) throws Exception;
}
