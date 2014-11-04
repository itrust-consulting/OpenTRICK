package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceMeasureDescriptionTextImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional
public class ServiceMeasureDescriptionTextImpl implements ServiceMeasureDescriptionText {

	@Autowired
	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(Integer id) throws Exception {
		return daoMeasureDescriptionText.get(id);
	}

	/**
	 * getMeasureDescriptionTextByIdAndLanguageId: <br>
	 * Description
	 * 
	 * @param idMeasureDescription
	 * @param idLanguage
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#getMeasureDescriptionTextByIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception {
		return daoMeasureDescriptionText.getForMeasureDescriptionAndLanguage(idMeasureDescription, idLanguage);
	}

	/**
	 * existsForLanguageByMeasureDescriptionIdAndLanguageId: <br>
	 * Description
	 * 
	 * @param idMeasureDescription
	 * @param idLanguage
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#existsForLanguageByMeasureDescriptionIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage) throws Exception {
		return daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(idMeasureDescription, idLanguage);
	}

	/**
	 * getAllMeasureDescriptionTextsByMeasureDescriptionId: <br>
	 * Description
	 * 
	 * @param measureDescriptionID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#getAllMeasureDescriptionTextsByMeasureDescriptionId(int)
	 */
	@Override
	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID) throws Exception {
		return daoMeasureDescriptionText.getAllFromMeasureDescription(measureDescriptionID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#save(lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void save(MeasureDescriptionText measureDescription) throws Exception {
		daoMeasureDescriptionText.save(measureDescription);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#saveOrUpdate(lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescriptionText measureDescription) throws Exception {
		daoMeasureDescriptionText.saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText#delete(lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void delete(MeasureDescriptionText measureDescription) throws Exception {
		daoMeasureDescriptionText.delete(measureDescription);
	}
}