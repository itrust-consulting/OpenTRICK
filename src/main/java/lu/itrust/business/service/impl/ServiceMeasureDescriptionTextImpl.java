/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.dao.DAOMeasureDescriptionText;
import lu.itrust.business.service.ServiceMeasureDescriptionText;

/**
 * @author oensuifudine
 * 
 */
@Service
@Transactional
public class ServiceMeasureDescriptionTextImpl implements
		ServiceMeasureDescriptionText {

	@Autowired
	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescriptionText.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#
	 * getByMeasureDescription(int)
	 */
	@Override
	public List<MeasureDescriptionText> getByMeasureDescription(
			int measureDescriptionID) throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescriptionText
				.getByMeasureDescription(measureDescriptionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescriptionText#exists(lu.itrust
	 * .business.TS.MeasureDescription, lu.itrust.business.TS.Language)
	 */
	@Override
	public boolean exists(MeasureDescription mesDesc, Language language)
			throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescriptionText.exists(mesDesc, language);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#
	 * getByMeasureDescriptionReferenceNorm(java.lang.String, java.lang.String,
	 * lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<MeasureDescriptionText> getByMeasureDescriptionReferenceNorm(
			String Reference, String norm, Analysis analysis) throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescriptionText.getByMeasureDescriptionReferenceNorm(
				Reference, norm, analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#
	 * getByMeasureDescriptionReferenceNormLanguage(java.lang.String,
	 * java.lang.String, lu.itrust.business.TS.Analysis,
	 * lu.itrust.business.TS.Language)
	 */
	@Override
	public MeasureDescriptionText getByMeasureDescriptionReferenceNormLanguage(
			String Reference, String norm, Analysis analysis, Language language)
			throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescriptionText
				.getByMeasureDescriptionReferenceNormLanguage(Reference, norm,
						analysis, language);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescriptionText#save(lu.itrust
	 * .business.TS.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void save(MeasureDescriptionText measureDescription)
			throws Exception {
		daoMeasureDescriptionText.save(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescriptionText#saveAndUpdate
	 * (lu.itrust.business.TS.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void saveAndUpdate(MeasureDescriptionText measureDescription)
			throws Exception {
		daoMeasureDescriptionText.saveAndUpdate(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescriptionText#remove(lu.itrust
	 * .business.TS.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void remove(MeasureDescriptionText measureDescription)
			throws Exception {
		daoMeasureDescriptionText.remove(measureDescription);

	}

	public DAOMeasureDescriptionText getDaoMeasureDescriptionText() {
		return daoMeasureDescriptionText;
	}

	public void setDaoMeasureDescriptionText(
			DAOMeasureDescriptionText daoMeasureDescriptionText) {
		this.daoMeasureDescriptionText = daoMeasureDescriptionText;
	}

}
