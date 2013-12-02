/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.service.ServiceAsset;

/**
 * @author eom
 *
 */
@Service
public class ServiceAssetImpl implements ServiceAsset {

	@Autowired
	private DAOAsset daoAsset;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#get(int)
	 */
	@Override
	public Asset get(int id) {
		return daoAsset.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#find()
	 */
	@Override
	public List<Asset> find() {
		return daoAsset.find();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#find(int, int)
	 */
	@Override
	public List<Asset> find(int pageIndex, int pageSize) {
		return daoAsset.find(pageIndex, pageSize);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#findByAnalysis(int)
	 */
	@Override
	public List<Asset> findByAnalysis(int analysisId) {
		return daoAsset.findByAnalysis(analysisId);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#findByAnalysis(int, int, int)
	 */
	@Override
	public List<Asset> findByAnalysis(int pageIndex, int pageSize,
			int analysisId) {
		return daoAsset.findByAnalysis(pageIndex, pageSize, analysisId);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#save(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public Asset save(Asset asset) {
		return daoAsset.save(asset);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#saveOrUpdate(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Asset asset) {
		daoAsset.saveOrUpdate(asset);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#merge(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public Asset merge(Asset asset) {
		return daoAsset.merge(asset);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#delete(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public void delete(Asset asset) {
		daoAsset.delete(asset);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceAsset#delete(int)
	 */
	@Override
	@Transactional
	public void delete(int id) {
		daoAsset.delete(id);
	}

	@Override
	public List<Asset> findByAnalysisAndSelected(int idAnalysis) {
		return daoAsset.findByAnalysisAndSelected(idAnalysis);
	}

}
