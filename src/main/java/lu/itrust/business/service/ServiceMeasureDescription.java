package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;

/**
 * ServiceMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 07, 2013
 */
public interface ServiceMeasureDescription {
	public MeasureDescription get(Integer id) throws Exception;

	public MeasureDescription getByReferenceAndNorm(String reference, Norm norm) throws Exception;

	public boolean existsForMeasureByReferenceAndNorm(String reference, Integer idNorm) throws Exception;

	public boolean existsForMeasureByReferenceAndNorm(String reference, Norm norm) throws Exception;

	public List<MeasureDescription> getAll() throws Exception;

	public List<MeasureDescription> getAllByNorm(Integer normid) throws Exception;

	public List<MeasureDescription> getAllByNorm(String label) throws Exception;

	public List<MeasureDescription> getAllByNorm(Norm norm) throws Exception;

	public void save(MeasureDescription measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception;

	public void delete(MeasureDescription measureDescription) throws Exception;
}