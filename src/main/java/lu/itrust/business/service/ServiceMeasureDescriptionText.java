package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.dao.DAOMeasureDescriptionText;

public interface ServiceMeasureDescriptionText {

	public MeasureDescriptionText get(int id) throws Exception;

	public List<MeasureDescriptionText> getByMeasureDescription(
			int measureDescriptionID) throws Exception;

	public boolean exists(MeasureDescription mesDesc, Language language)
			throws Exception;

	public List<MeasureDescriptionText> getByMeasureDescriptionReferenceNorm(
			String Reference, String norm, Analysis analysis) throws Exception;

	public MeasureDescriptionText getByMeasureDescriptionReferenceNormLanguage(
			String Reference, String norm, Analysis analysis, Language language)
			throws Exception;

	public void save(MeasureDescriptionText measureDescription)
			throws Exception;

	public void saveAndUpdate(MeasureDescriptionText measureDescription)
			throws Exception;

	public void remove(MeasureDescriptionText measureDescription)
			throws Exception;
	
	public DAOMeasureDescriptionText getDaoMeasureDescriptionText();

}
