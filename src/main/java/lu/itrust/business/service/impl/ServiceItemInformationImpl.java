/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.dao.DAOItemInformation;
import lu.itrust.business.service.ServiceItemInformation;

/**
 * @author eom
 *
 */
@Service
public class ServiceItemInformationImpl implements ServiceItemInformation {

	@Autowired
	private DAOItemInformation daoItemInformation;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#get(int)
	 */
	@Override
	public ItemInformation get(int id) throws Exception {
		return daoItemInformation.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#loadFromDescription(java.lang.String)
	 */
	@Override
	public ItemInformation loadFromDescription(String description)
			throws Exception {
		return daoItemInformation.loadFromDescription(description);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#loadAllFromAnalysisID(int)
	 */
	@Override
	public List<ItemInformation> loadAllFromAnalysisID(int analysisID)
			throws Exception {
		return daoItemInformation.loadAllFromAnalysisID(analysisID);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#loadAllFromAnalysisIdentifierVersionCreationDate(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ItemInformation> loadAllFromAnalysisIdentifierVersionCreationDate(
			int identifier, String version, String creationDate)
			throws Exception {
		return daoItemInformation.loadAllFromAnalysisIdentifierVersionCreationDate(identifier, version, creationDate);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#loadAll()
	 */
	@Override
	public List<ItemInformation> loadAll() throws Exception {
		return daoItemInformation.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#save(lu.itrust.business.TS.ItemInformation)
	 */
	@Transactional
	@Override
	public void save(ItemInformation itemInformation) throws Exception {
		daoItemInformation.save(itemInformation);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#saveOrUpdate(lu.itrust.business.TS.ItemInformation)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(ItemInformation itemInformation) throws Exception {
		daoItemInformation.saveOrUpdate(itemInformation);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceItemInformation#remove(lu.itrust.business.TS.ItemInformation)
	 */
	@Transactional
	@Override
	public void remove(ItemInformation itemInformation) throws Exception {
		daoItemInformation.remove(itemInformation);
	}

}
