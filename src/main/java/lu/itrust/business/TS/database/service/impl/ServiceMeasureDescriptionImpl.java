package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.service.ServiceMeasureDescription;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;

/**
 * ServiceMeasureDescriptionImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Feb 07, 2013
 */
@Service
public class ServiceMeasureDescriptionImpl implements ServiceMeasureDescription {

	@Autowired
	private DAOMeasureDescription daoMeasureDescription;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#get(int)
	 */
	@Override
	public MeasureDescription get(Integer id)  {
		return daoMeasureDescription.get(id);
	}

	/**
	 * getByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#getByReferenceAndStandard(java.lang.String, lu.itrust.business.TS.model.standard.Standard)
	 */
	@Override
	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard)  {
		return daoMeasureDescription.getByReferenceAndStandard(reference, standard);
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard)  {
		return daoMeasureDescription.existsForMeasureByReferenceAndStandard(reference, idStandard);
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String, lu.itrust.business.TS.model.standard.Standard)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard)  {
		return daoMeasureDescription.existsForMeasureByReferenceAndStandard(reference, standard);
	}

	/**
	 * getAllMeasureDescriptions: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#getAllMeasureDescriptions()
	 */
	@Override
	public List<MeasureDescription> getAll()  {
		return this.daoMeasureDescription.getAll();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#getAllByStandard(java.lang.Integer)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(Integer idStandard)  {
		return this.daoMeasureDescription.getAllByStandard(idStandard);
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#getAllByStandard(java.lang.String)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(String label)  {
		return this.daoMeasureDescription.getAllByStandard(label);
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#getAllByStandard(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(Standard standard)  {
		return this.daoMeasureDescription.getAllByStandard(standard);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#save(lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription)
	 */
	@Transactional
	@Override
	public void save(MeasureDescription measureDescription)  {
		daoMeasureDescription.save(measureDescription);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#saveOrUpdate(lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription)  {
		daoMeasureDescription.saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescription#delete(lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription)
	 */
	@Transactional
	@Override
	public void delete(MeasureDescription measureDescription)  {
		daoMeasureDescription.delete(measureDescription);
	}

	@Transactional
	@Override
	public void delete(int id)  {
		daoMeasureDescription.delete(id);
	}

	@Override
	public boolean existsForMeasureByReferenceAndAnalysisStandardId(String reference, int idAnalysisStandard) {
		return daoMeasureDescription.existsForMeasureByReferenceAndAnalysisStandardId(reference,idAnalysisStandard);
	}

	@Override
	public boolean exists(int idMeasureDescription, int idStandard) {
		return daoMeasureDescription.exists(idMeasureDescription, idStandard);
	}

	@Override
	public boolean isUsed(MeasureDescription measureDescription) {
		return daoMeasureDescription.isUsed(measureDescription);
	}
}