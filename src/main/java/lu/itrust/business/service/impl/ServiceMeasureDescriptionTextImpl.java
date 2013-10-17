package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class ServiceMeasureDescriptionTextImpl implements ServiceMeasureDescriptionText {

	@Autowired
	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(int id) throws Exception {
		return daoMeasureDescriptionText.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText# getByMeasureDescription(int)
	 */
	@Override
	public List<MeasureDescriptionText> getByMeasureDescription(int measureDescriptionID) throws Exception {
		return daoMeasureDescriptionText.getByMeasureDescription(measureDescriptionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#exists(lu.itrust
	 * .business.TS.MeasureDescription, lu.itrust.business.TS.Language)
	 */
	@Override
	public boolean exists(MeasureDescription mesDesc, Language language) throws Exception {
		return daoMeasureDescriptionText.existsForLanguage(mesDesc, language);
	}

	/**
	 * 
	 * getByLanguage: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#getByLanguage(lu.itrust.business.TS.MeasureDescription, lu.itrust.business.TS.Language)
	 */
	@Override
	public MeasureDescriptionText getByLanguage(MeasureDescription mesDesc, Language language) throws Exception {
		return daoMeasureDescriptionText.getByLanguage(mesDesc, language);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#save(lu.itrust
	 * .business.TS.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void save(MeasureDescriptionText measureDescription) throws Exception {
		daoMeasureDescriptionText.save(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#saveAndUpdate
	 * (lu.itrust.business.TS.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescriptionText measureDescription) throws Exception {
		daoMeasureDescriptionText.saveAndUpdate(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescriptionText#remove(lu.itrust
	 * .business.TS.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void remove(MeasureDescriptionText measureDescription) throws Exception {
		daoMeasureDescriptionText.remove(measureDescription);

	}

	public DAOMeasureDescriptionText getDaoMeasureDescriptionText() {
		return daoMeasureDescriptionText;
	}

	public void setDaoMeasureDescriptionText(DAOMeasureDescriptionText daoMeasureDescriptionText) {
		this.daoMeasureDescriptionText = daoMeasureDescriptionText;
	}

}
