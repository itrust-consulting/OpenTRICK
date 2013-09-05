/**
 * 
 */
package lu.itrust.business.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescription;

/**
 * @author oensuifudine
 * 
 */
@Service
public class ServiceMeasureDescriptionImpl implements ServiceMeasureDescription {

	@Autowired
	private DAOMeasureDescription daoMeasureDescription;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#get(int)
	 */
	@Override
	public MeasureDescription get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescription.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescription#getByReferenceNorm
	 * (java.lang.String, lu.itrust.business.TS.Norm)
	 */
	@Override
	public MeasureDescription getByReferenceNorm(String Reference, Norm norm)
			throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescription.getByReferenceNorm(Reference, norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescription#exists(java.lang
	 * .String, lu.itrust.business.TS.Norm)
	 */
	@Override
	public boolean exists(String Reference, Norm norm) throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescription.exists(Reference, norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescription#existsWithLanguage
	 * (java.lang.String, lu.itrust.business.TS.Norm,
	 * lu.itrust.business.TS.Language)
	 */
	@Override
	public boolean existsWithLanguage(String Reference, Norm norm,
			Language language) throws Exception {
		// TODO Auto-generated method stub
		return daoMeasureDescription.existsWithLanguage(Reference, norm,
				language);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescription#save(lu.itrust.business
	 * .TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void save(MeasureDescription measureDescription) throws Exception {
		daoMeasureDescription.save(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescription#saveOrUpdate(lu.
	 * itrust.business.TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription)
			throws Exception {
		daoMeasureDescription.saveOrUpdate(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceMeasureDescription#remove(lu.itrust
	 * .business.TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void remove(MeasureDescription measureDescription) throws Exception {
		daoMeasureDescription.remove(measureDescription);

	}

	/**
	 * @return the daoMeasureDescription
	 */
	public DAOMeasureDescription getDaoMeasureDescription() {
		return daoMeasureDescription;
	}

	/**
	 * @param daoMeasureDescription the daoMeasureDescription to set
	 */
	public void setDaoMeasureDescription(DAOMeasureDescription daoMeasureDescription) {
		this.daoMeasureDescription = daoMeasureDescription;
	}

}
