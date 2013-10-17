package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOMeasureDescription;

public interface ServiceMeasureDescription {

	public MeasureDescription get(int id) throws Exception;

	public MeasureDescription getByReferenceNorm(String reference, Norm norm) throws Exception;

	public boolean exists(String reference, Norm norm) throws Exception;

	public boolean existsWithLanguage(String reference, Norm norm, Language language) throws Exception;

	public List<MeasureDescription> getAll() throws Exception;
	
	public List<MeasureDescription> getAllByNorm(Norm norm) throws Exception;
	
	public List<MeasureDescription> getAllByNorm(Integer normid) throws Exception;
	
	public List<MeasureDescription> getAllByNorm(String label) throws Exception;

	public void save(MeasureDescription measureDescription) throws Exception;

	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception;

	public void remove(MeasureDescription measureDescription) throws Exception;

	public DAOMeasureDescription getDaoMeasureDescription();

}
