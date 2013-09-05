package lu.itrust.business.service;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOMeasureDescription;

public interface ServiceMeasureDescription {

	public MeasureDescription get(int id) throws Exception;

	public MeasureDescription getByReferenceNorm(String Reference, Norm norm)
			throws Exception;

	public boolean exists(String Reference, Norm norm) throws Exception;

	public boolean existsWithLanguage(String Reference, Norm norm,
			Language language) throws Exception;

	public void save(MeasureDescription measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescription measureDescription)
			throws Exception;

	public void remove(MeasureDescription measureDescription) throws Exception;
	
	public DAOMeasureDescription getDaoMeasureDescription();

}
