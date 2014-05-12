package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;

/**
 * DAOMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
public interface DAOMeasureDescription {
	public MeasureDescription get(int id) throws Exception;

	public MeasureDescription getByReferenceAndNorm(String reference, Norm norm) throws Exception;

	public boolean existsForMeasureByReferenceAndNorm(String reference, int idNorm) throws Exception;

	public boolean existsForMeasureByReferenceAndNorm(String reference, Norm norm) throws Exception;

	public List<MeasureDescription> getAllMeasureDescriptions() throws Exception;

	public List<MeasureDescription> getAllMeasureDescriptionsByNorm(Integer normid) throws Exception;

	public List<MeasureDescription> getAllMeasureDescriptionsByNorm(String label) throws Exception;

	public List<MeasureDescription> getAllMeasureDescriptionsByNorm(Norm norm) throws Exception;

	public void save(MeasureDescription measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception;

	public void delete(MeasureDescription measureDescription) throws Exception;
}