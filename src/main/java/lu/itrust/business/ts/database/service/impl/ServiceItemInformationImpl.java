package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOItemInformation;
import lu.itrust.business.ts.database.service.ServiceItemInformation;
import lu.itrust.business.ts.model.iteminformation.ItemInformation;

/**
 * ServiceItemInformationImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
@Service
public class ServiceItemInformationImpl implements ServiceItemInformation {

	@Autowired
	private DAOItemInformation daoItemInformation;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#get(int)
	 */
	@Override
	public ItemInformation get(Integer id)  {
		return daoItemInformation.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idItemInformation
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idItemInformation)  {
		return daoItemInformation.getFromAnalysisById(idAnalysis, idItemInformation);
	}

	/**
	 * getFromAnalysisIdByDescription: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param description
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#getFromAnalysisIdByDescription(int,
	 *      java.lang.String)
	 */
	@Override
	public ItemInformation getFromAnalysisByDescription(Integer idAnalysis, String description)  {
		return daoItemInformation.getFromAnalysisByDescription(idAnalysis, description);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param historyId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer historyId, Integer analysisId)  {
		return daoItemInformation.belongsToAnalysis(historyId, analysisId);
	}

	/**
	 * getAllItemInformation: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#getAllItemInformation()
	 */
	@Override
	public List<ItemInformation> getAll()  {
		return daoItemInformation.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#getAllFromAnalysisId(int)
	 */
	@Override
	public List<ItemInformation> getAllFromAnalysis(Integer analysisID)  {
		return daoItemInformation.getAllFromAnalysis(analysisID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param itemInformation
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#save(lu.itrust.business.ts.model.iteminformation.ItemInformation)
	 */
	@Transactional
	@Override
	public void save(ItemInformation itemInformation)  {
		daoItemInformation.save(itemInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param itemInformation
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#saveOrUpdate(lu.itrust.business.ts.model.iteminformation.ItemInformation)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ItemInformation itemInformation)  {
		daoItemInformation.saveOrUpdate(itemInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param itemInformation
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceItemInformation#delete(lu.itrust.business.ts.model.iteminformation.ItemInformation)
	 */
	@Transactional
	@Override
	public void delete(ItemInformation itemInformation)  {
		daoItemInformation.delete(itemInformation);
	}
}