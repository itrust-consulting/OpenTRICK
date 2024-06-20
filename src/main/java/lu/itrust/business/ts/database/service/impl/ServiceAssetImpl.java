package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOAsset;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.model.asset.Asset;

/**
 * ServiceAssetImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#get(int)
	 */
	@Override
	public Asset get(Integer id)  {
		return daoAsset.get(id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param assetId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assetId)  {
		return daoAsset.belongsToAnalysis(analysisId, assetId);
	}

	/**
	 * getFromAnalysisByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#getFromAnalysisByName(java.lang.Integer,
	 *      java.lang.String)
	 */
	@Override
	public Asset getFromAnalysisByName(Integer analysisId, String name)  {
		return daoAsset.getFromAnalysisByName(analysisId, name);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#getAll()
	 */
	@Override
	public List<Asset> getAll()  {
		return daoAsset.getAll();
	}

	/**
	 * getByPageAndSize: <br>
	 * Description
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#getByPageAndSize(int, int)
	 */
	@Override
	public List<Asset> getByPageAndSize(Integer pageIndex, Integer pageSize)  {
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#getFromAnalysisByPageAndSize(int,
	 *      int, int)
	 */
	@Override
	public List<Asset> getFromAnalysisByPageAndSize(Integer analysisId, Integer pageIndex, Integer pageSize)  {
		return daoAsset.getFromAnalysisByPageAndSize(analysisId, pageIndex, pageSize);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#getAllFromAnalysis(int)
	 */
	@Override
	public List<Asset> getAllFromAnalysis(Integer analysisId)  {
		return daoAsset.getAllFromAnalysis(analysisId);
	}

	/**
	 * getSelectedFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 */
	@Override
	public List<Asset> getAllFromAnalysisIdAndSelected(Integer idAnalysis)  {
		return daoAsset.getAllFromAnalysisIdAndSelected(idAnalysis);
	}

	/**
	 * getSelectedFromAnalysisAndOrderByALE: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#getSelectedFromAnalysisAndOrderByALE(int)
	 */
	@Override
	public List<Asset> getSelectedFromAnalysisAndOrderByALE(Integer idAnalysis)  {
		return daoAsset.getSelectedFromAnalysisAndOrderByALE(idAnalysis);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#save(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	@Transactional
	public Asset save(Asset asset)  {
		return daoAsset.save(asset);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param asset
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#saveOrUpdate(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	@Transactional
	public void saveOrUpdate(Asset asset)  {
		daoAsset.saveOrUpdate(asset);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param asset
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#merge(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	@Transactional
	public Asset merge(Asset asset)  {
		return daoAsset.merge(asset);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#delete(int)
	 */
	@Override
	@Transactional
	public void delete(Integer id)  {
		daoAsset.delete(id);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param asset
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAsset#delete(lu.itrust.business.ts.model.asset.Asset)
	 */
	@Override
	@Transactional
	public void delete(Asset asset)  {
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

	@Override
	public boolean belongsToAnalysis(Integer idAnalysis, List<Integer> assetIds) {
		return daoAsset.belongsToAnalysis(idAnalysis, assetIds);
	}
}