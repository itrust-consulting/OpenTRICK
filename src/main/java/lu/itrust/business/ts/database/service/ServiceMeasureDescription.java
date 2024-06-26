package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * ServiceMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Feb 07, 2013
 */
public interface ServiceMeasureDescription {
	public MeasureDescription get(Integer id);

	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard);

	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard);

	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard);

	public List<MeasureDescription> getAll();

	public List<MeasureDescription> getAllByStandard(Integer idStandard);

	public List<MeasureDescription> getAllByStandard(String standard);

	public List<MeasureDescription> getAllByStandard(Standard standard);

	public void save(MeasureDescription measureDescription);

	public void saveOrUpdate(MeasureDescription measureDescription);

	public void delete(MeasureDescription measureDescription);

	public void delete(int id);

	public boolean existsForMeasureByReferenceAndAnalysisStandardId(String reference, int idAnalysisStandard);

	public boolean exists(int idMeasure, int idStandard);

	public boolean isUsed(MeasureDescription measureDescription);
}