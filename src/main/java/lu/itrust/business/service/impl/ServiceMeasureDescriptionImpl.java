package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;
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
	 * getByReferenceAndNorm: <br>
	 * Description
	 * 
	 * @param reference
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getByReferenceAndNorm(java.lang.String,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public MeasureDescription getByReferenceAndNorm(String reference, Norm norm) throws Exception {
		return daoMeasureDescription.getByReferenceAndNorm(reference, norm);
	}

	/**
	 * existsForMeasureByReferenceAndNorm: <br>
	 * Description
	 * 
	 * @param reference
	 * @param idnorm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#existsForMeasureByReferenceAndNorm(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndNorm(String reference, Integer idnorm) throws Exception {
		return daoMeasureDescription.existsForMeasureByReferenceAndNorm(reference, idnorm);
	}

	/**
	 * existsForMeasureByReferenceAndNorm: <br>
	 * Description
	 * 
	 * @param reference
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#existsForMeasureByReferenceAndNorm(java.lang.String,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndNorm(String reference, Norm norm) throws Exception {
		return daoMeasureDescription.existsForMeasureByReferenceAndNorm(reference, norm);
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
	 * getAllMeasureDescriptionsByNorm: <br>
	 * Description
	 * 
	 * @param normid
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllMeasureDescriptionsByNorm(java.lang.Integer)
	 */
	@Override
	public List<MeasureDescription> getAllByNorm(Integer normid) throws Exception {
		return this.daoMeasureDescription.getAllByNorm(normid);
	}

	/**
	 * getAllMeasureDescriptionsByNorm: <br>
	 * Description
	 * 
	 * @param label
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllMeasureDescriptionsByNorm(java.lang.String)
	 */
	@Override
	public List<MeasureDescription> getAllByNorm(String label) throws Exception {
		return this.daoMeasureDescription.getAllByNorm(label);
	}

	/**
	 * getAllMeasureDescriptionsByNorm: <br>
	 * Description
	 * 
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasureDescription#getAllMeasureDescriptionsByNorm(lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<MeasureDescription> getAllByNorm(Norm norm) throws Exception {
		return this.daoMeasureDescription.getAllByNorm(norm);
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
}