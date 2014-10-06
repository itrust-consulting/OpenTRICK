package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#get(int)
	 */
	@Override
	public MeasureDescription get(Integer id) throws Exception {
		return daoMeasureDescription.get(id);
	}

	/**
	 * getByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getByReferenceAndStandard(java.lang.String, lu.itrust.business.TS.Standard)
	 */
	@Override
	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard) throws Exception {
		return daoMeasureDescription.getByReferenceAndStandard(reference, standard);
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String, java.lang.Integer)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard) throws Exception {
		return daoMeasureDescription.existsForMeasureByReferenceAndStandard(reference, idStandard);
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String, lu.itrust.business.TS.Standard)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard) throws Exception {
		return daoMeasureDescription.existsForMeasureByReferenceAndStandard(reference, standard);
	}

	/**
	 * getAllMeasureDescriptions: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllMeasureDescriptions()
	 */
	@Override
	public List<MeasureDescription> getAll() throws Exception {
		return this.daoMeasureDescription.getAll();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByStandard(java.lang.Integer)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(Integer idStandard) throws Exception {
		return this.daoMeasureDescription.getAllByStandard(idStandard);
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByStandard(java.lang.String)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(String label) throws Exception {
		return this.daoMeasureDescription.getAllByStandard(label);
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllByStandard(lu.itrust.business.TS.Standard)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(Standard standard) throws Exception {
		return this.daoMeasureDescription.getAllByStandard(standard);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#save(lu.itrust.business.TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void save(MeasureDescription measureDescription) throws Exception {
		daoMeasureDescription.save(measureDescription);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#saveOrUpdate(lu.itrust.business.TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception {
		daoMeasureDescription.saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#delete(lu.itrust.business.TS.MeasureDescription)
	 */
	@Transactional
	@Override
	public void delete(MeasureDescription measureDescription) throws Exception {
		daoMeasureDescription.delete(measureDescription);
	}

	@Transactional
	@Override
	public void delete(int id) throws Exception {
		daoMeasureDescription.delete(id);
	}
}