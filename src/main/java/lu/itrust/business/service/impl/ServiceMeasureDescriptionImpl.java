/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return daoMeasureDescription.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getByReferenceNorm
	 * (java.lang.String, lu.itrust.business.TS.Norm)
	 */
	@Override
	public MeasureDescription getByReferenceNorm(String reference, Norm norm) throws Exception {
		return daoMeasureDescription.getByReferenceNorm(reference, norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#exists(java.lang .String,
	 * lu.itrust.business.TS.Norm)
	 */
	@Override
	public boolean exists(String reference, Norm norm) throws Exception {
		return daoMeasureDescription.exists(reference, norm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#existsWithLanguage
	 * (java.lang.String, lu.itrust.business.TS.Norm, lu.itrust.business.TS.Language)
	 */
	@Override
	public boolean existsWithLanguage(String reference, Norm norm, Language language) throws Exception {
		return daoMeasureDescription.existsWithLanguage(reference, norm, language);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#save(lu.itrust.business
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
	 * @see lu.itrust.business.service.ServiceMeasureDescription#saveOrUpdate(lu.
	 * itrust.business.TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception {
		daoMeasureDescription.saveOrUpdate(measureDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#remove(lu.itrust
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
	 * @param daoMeasureDescription
	 *            the daoMeasureDescription to set
	 */
	public void setDaoMeasureDescription(DAOMeasureDescription daoMeasureDescription) {
		this.daoMeasureDescription = daoMeasureDescription;
	}

	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByNorm(lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<MeasureDescription> getAll() throws Exception {
		return this.daoMeasureDescription.getAll();
	}
	
	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByNorm(lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<MeasureDescription> getAllByNorm(Norm norm) throws Exception {
		return this.daoMeasureDescription.getAllByNorm(norm);
	}

	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByNorm(java.lang.Integer)
	 */
	@Override
	public List<MeasureDescription> getAllByNorm(Integer normid) throws Exception {
		return this.daoMeasureDescription.getAllByNorm(normid);
	}
	
	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByNorm(java.lang.Integer)
	 */
	@Override
	public List<MeasureDescription> getAllByNorm(String label) throws Exception {
		return this.daoMeasureDescription.getAllByNorm(label);
	}

	@Override
	public boolean refrenceExists(String reference, int idNorm) {
		
		return daoMeasureDescription.refrenceExists(reference, idNorm);
	}
}
