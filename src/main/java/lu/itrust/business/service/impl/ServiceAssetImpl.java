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

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#get(int)
	 */
	@Override
	public Asset get(int id) {
		return daoAsset.get(id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#getAll()
	 */
	@Override
	public List<Asset> getAll() {
		return daoAsset.getAll();
	}

	/**
	 * getByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#getByPageAndSize(int, int)
	 */
	@Override
	public List<Asset> getByPageAndSize(int pageIndex, int pageSize) {
		return daoAsset.getByPageAndSize(pageIndex, pageSize);
	}

	/**
	 * getFromAnalysisByPageAndSize: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#getFromAnalysisByPageAndSize(int, int, int)
	 */
	@Override
	public List<Asset> getFromAnalysisByPageAndSize(int pageIndex, int pageSize, int analysisId) {
		return daoAsset.getFromAnalysisByPageAndSize(pageIndex, pageSize, analysisId);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(int analysisId, int assetId) {
		return daoAsset.belongsToAnalysis(assetId, analysisId);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#getAllFromAnalysis(int)
	 */
	@Override
	public List<Asset> getAllFromAnalysis(int analysisId) {
		return daoAsset.getAllFromAnalysis(analysisId);
	}

	/**
	 * getSelectedFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#getSelectedFromAnalysis(int)
	 */
	@Override
	public List<Asset> getSelectedFromAnalysis(int idAnalysis) {
		return daoAsset.getSelectedFromAnalysis(idAnalysis);
	}

	/**
	 * getSelectedFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#getSelectedFromAnalysisAndOrderByALE(int)
	 */
	@Override
	public List<Asset> getSelectedFromAnalysisAndOrderByALE(int idAnalysis) {
		return daoAsset.getSelectedFromAnalysisAndOrderByALE(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#save(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public Asset save(Asset asset) {
		return daoAsset.save(asset);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#saveOrUpdate(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Asset asset) {
		daoAsset.saveOrUpdate(asset);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#merge(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public Asset merge(Asset asset) {
		return daoAsset.merge(asset);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#delete(int)
	 */
	@Override
	@Transactional
	public void delete(int id) {
		daoAsset.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceAsset#delete(lu.itrust.business.TS.Asset)
	 */
	@Override
	@Transactional
	public void delete(Asset asset) {
		daoAsset.delete(asset);
	}
}