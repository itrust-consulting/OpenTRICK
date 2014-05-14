package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.dao.DAOItemInformation;
import lu.itrust.business.service.ServiceItemInformation;

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
	 * @see lu.itrust.business.service.ServiceItemInformation#get(int)
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
	 * @see lu.itrust.business.service.ServiceItemInformation#getFromAnalysisById(java.lang.Integer,
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
	 * @see lu.itrust.business.service.ServiceItemInformation#getFromAnalysisIdByDescription(int,
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
	 * @see lu.itrust.business.service.ServiceItemInformation#belongsToAnalysis(java.lang.Integer,
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
	 * @see lu.itrust.business.service.ServiceItemInformation#getAllItemInformation()
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
	 * @see lu.itrust.business.service.ServiceItemInformation#getAllFromAnalysisId(int)
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
	 * @see lu.itrust.business.service.ServiceItemInformation#save(lu.itrust.business.TS.ItemInformation)
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
	 * @see lu.itrust.business.service.ServiceItemInformation#saveOrUpdate(lu.itrust.business.TS.ItemInformation)
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
	 * @see lu.itrust.business.service.ServiceItemInformation#delete(lu.itrust.business.TS.ItemInformation)
	 */
	@Transactional
	@Override
	public void delete(ItemInformation itemInformation) throws Exception {
		daoItemInformation.delete(itemInformation);
	}
}