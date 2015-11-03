package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.service.ServiceAsset;
import lu.itrust.business.TS.model.asset.Asset;

/**
 * ServiceAssetImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceAssetImpl implements ServiceAsset {

	@Autowired
	private DAOAsset daoAsset;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#get(int)
	 */
	@Override
	public Asset get(Integer id) throws Exception {
		return daoAsset.get(id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param assetId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assetId) throws Exception {
		return daoAsset.belongsToAnalysis(analysisId, assetId);
	}

	/**
	 * getFromAnalysisByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#getFromAnalysisByName(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public Asset getFromAnalysisByName(Integer analysisId, String name) throws Exception {
		return daoAsset.getFromAnalysisByName(analysisId, name);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#getAll()
	 */
	@Override
	public List<Asset> getAll() throws Exception {
		return daoAsset.getAll();
	}

	/**
	 * getByPageAndSize: <br>
	 * Description
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#getByPageAndSize(int, int)
	 */
	@Override
	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize) throws Exception {
		return daoAsset.getByPageAndSize(pageIndex, pageSize);
	}

	/**
	 * getFromAnalysisByPageAndSize: <br>
	 * Description
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#getFromAnalysisByPageAndSize(int,
	 *      int, int)
	 */
	@Override
	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize) throws Exception {
		return daoAsset.getFromAnalysisByPageAndSize(analysisId, pageIndex, pageSize);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#getAllFromAnalysis(int)
	 */
	@Override
	public List<Asset> getAllFromAnalysis(Integer analysisId) throws Exception {
		return daoAsset.getAllFromAnalysis(analysisId);
	}

	/**
	 * getSelectedFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis) throws Exception {
		return daoAsset.getAllFromAnalysisIdAndSelected(idAnalysis);
	}

	/**
	 * getSelectedFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#getSelectedFromAnalysisAndOrderByALE(int)
	 */
	@Override
	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis) throws Exception {
		return daoAsset.getSelectedFromAnalysisAndOrderByALE(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#save(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	@Transactional
	public Asset save(Asset asset) throws Exception {
		return daoAsset.save(asset);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param asset
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#saveOrUpdate(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Asset asset) throws Exception {
		daoAsset.saveOrUpdate(asset);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#merge(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	@Transactional
	public Asset merge(Asset asset) throws Exception {
		return daoAsset.merge(asset);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#delete(int)
	 */
	@Override
	@Transactional
	public void delete(Integer id) throws Exception {
		daoAsset.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param asset
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceAsset#delete(lu.itrust.business.TS.model.asset.Asset)
	 */
	@Override
	@Transactional
	public void delete(Asset asset) throws Exception {
		daoAsset.delete(asset);
	}

	@Override
	public boolean exist(Integer idAnalysis, String name) {
		return daoAsset.exist(idAnalysis, name);
	}

	@Override
	public Asset getFromAnalysisById(Integer idAnalysis, int idAsset) {
		return daoAsset.getFromAnalysisById(idAnalysis,idAsset);
	}

	@Override
	public Asset getByNameAndAnlysisId(String name, int idAnalysis) {
		return daoAsset.getByNameAndAnlysisId(name, idAnalysis);
	}
}