package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

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

	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard) throws Exception;

	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard) throws Exception;

	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard) throws Exception;

	public List<MeasureDescription> getAll() throws Exception;

	public List<MeasureDescription> getAllByStandard(Integer idStandard) throws Exception;

	public List<MeasureDescription> getAllByStandard(String standard) throws Exception;

	public List<MeasureDescription> getAllByStandard(Standard standard) throws Exception;

	public void save(MeasureDescription measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception;

	public void delete(MeasureDescription measureDescription) throws Exception;

	public void delete(int id) throws Exception;

	public boolean existsForMeasureByReferenceAndAnalysisStandardId(String reference, int idAnalysisStandard);

	public boolean exists(int idMeasure, int idStandard);
}