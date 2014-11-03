package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.data.basic.ItemInformation;
import lu.itrust.business.TS.database.dao.DAOItemInformation;
import lu.itrust.business.TS.database.service.ServiceItemInformation;

/**
 * ServiceItemInformationImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#get(int)
	 */
	@Override
	public ItemInformation get(Integer id) throws Exception {
		return daoItemInformation.get(id);
	}

	/**
	 * getFromAnalysisById: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idItemInformation
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#getFromAnalysisById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public ItemInformation getFromAnalysisById(Integer idAnalysis, Integer idItemInformation) throws Exception {
		return daoItemInformation.getFromAnalysisById(idAnalysis, idItemInformation);
	}

	/**
	 * getFromAnalysisIdByDescription: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param description
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#getFromAnalysisIdByDescription(int,
	 *      java.lang.String)
	 */
	@Override
	public ItemInformation getFromAnalysisByDescription(Integer idAnalysis, String description) throws Exception {
		return daoItemInformation.getFromAnalysisByDescription(idAnalysis, description);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param historyId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#belongsToAnalysis(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean belongsToAnalysis(Integer historyId, Integer analysisId) throws Exception {
		return daoItemInformation.belongsToAnalysis(historyId, analysisId);
	}

	/**
	 * getAllItemInformation: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#getAllItemInformation()
	 */
	@Override
	public List<ItemInformation> getAll() throws Exception {
		return daoItemInformation.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#getAllFromAnalysisId(int)
	 */
	@Override
	public List<ItemInformation> getAllFromAnalysis(Integer analysisID) throws Exception {
		return daoItemInformation.getAllFromAnalysis(analysisID);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param itemInformation
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#save(lu.itrust.business.TS.data.basic.ItemInformation)
	 */
	@Transactional
	@Override
	public void save(ItemInformation itemInformation) throws Exception {
		daoItemInformation.save(itemInformation);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param itemInformation
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#saveOrUpdate(lu.itrust.business.TS.data.basic.ItemInformation)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ItemInformation itemInformation) throws Exception {
		daoItemInformation.saveOrUpdate(itemInformation);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param itemInformation
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceItemInformation#delete(lu.itrust.business.TS.data.basic.ItemInformation)
	 */
	@Transactional
	@Override
	public void delete(ItemInformation itemInformation) throws Exception {
		daoItemInformation.delete(itemInformation);
	}
}