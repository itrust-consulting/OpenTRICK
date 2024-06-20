package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * ServiceMeasureDescriptionTextImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
@Transactional(readOnly = true)
public class ServiceMeasureDescriptionTextImpl implements ServiceMeasureDescriptionText {

	@Autowired
	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#get(int)
	 */
	@Override
	public MeasureDescriptionText get(Integer id)  {
		return daoMeasureDescriptionText.get(id);
	}

	/**
	 * getMeasureDescriptionTextByIdAndLanguageId: <br>
	 * Description
	 * 
	 * @param idMeasureDescription
	 * @param idLanguage
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#getMeasureDescriptionTextByIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public MeasureDescriptionText getForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage)  {
		return daoMeasureDescriptionText.getForMeasureDescriptionAndLanguage(idMeasureDescription, idLanguage);
	}

	/**
	 * existsForLanguageByMeasureDescriptionIdAndLanguageId: <br>
	 * Description
	 * 
	 * @param idMeasureDescription
	 * @param idLanguage
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#existsForLanguageByMeasureDescriptionIdAndLanguageId(int,
	 *      int)
	 */
	@Override
	public boolean existsForMeasureDescriptionAndLanguage(Integer idMeasureDescription, Integer idLanguage)  {
		return daoMeasureDescriptionText.existsForMeasureDescriptionAndLanguage(idMeasureDescription, idLanguage);
	}

	/**
	 * getAllMeasureDescriptionTextsByMeasureDescriptionId: <br>
	 * Description
	 * 
	 * @param measureDescriptionID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#getAllMeasureDescriptionTextsByMeasureDescriptionId(int)
	 */
	@Override
	public List<MeasureDescriptionText> getAllFromMeasureDescription(Integer measureDescriptionID)  {
		return daoMeasureDescriptionText.getAllFromMeasureDescription(measureDescriptionID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#save(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void save(MeasureDescriptionText measureDescription)  {
		daoMeasureDescriptionText.save(measureDescription);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#saveOrUpdate(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(MeasureDescriptionText measureDescription)  {
		daoMeasureDescriptionText.saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param measureDescription
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText#delete(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText)
	 */
	@Transactional
	@Override
	public void delete(MeasureDescriptionText measureDescription)  {
		daoMeasureDescriptionText.delete(measureDescription);
	}
}