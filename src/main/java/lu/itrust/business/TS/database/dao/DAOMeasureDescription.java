package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

/**
 * DAOMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.�.rl. :
 * @version
 * @since Feb 1, 2013
 */
public interface DAOMeasureDescription {
	public MeasureDescription get(Integer id) ;

	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard) ;

	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard) ;

	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard) ;

	public List<MeasureDescription> getAll() ;

	public List<MeasureDescription> getAllByStandard(Integer idStandard) ;

	public List<MeasureDescription> getAllByStandard(String label) ;

	public List<MeasureDescription> getAllByStandard(Standard standard) ;

	public void save(MeasureDescription measureDescription) ;

	public void saveOrUpdate(MeasureDescription measureDescription) ;

	public void delete(MeasureDescription measureDescription) ;

	public void delete(int id) ;

	public boolean existsForMeasureByReferenceAndAnalysisStandardId(String reference, int idAnalysisStandard);

	public boolean exists(int idMeasureDescription, int idStandard);
}