/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.dao.DAOAssetTypeValue;
import lu.itrust.business.service.ServiceAssetTypeValue;

/**
 * @author eomar
 * 
 */
@Service
public class ServiceAssetTypeValueImpl implements ServiceAssetTypeValue {

	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssetTypeValue#findOne(int)
	 */
	@Override
	public AssetTypeValue findOne(int id) {
		return daoAssetTypeValue.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByIdAndAnalysis(int,
	 * int)
	 */
	@Override
	public AssetTypeValue findByIdAndAnalysis(int id, int analysis) {
		return daoAssetTypeValue.findByIdAndAnalysis(id, analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByIdAndScenario(int,
	 * int)
	 */
	@Override
	public AssetTypeValue findByIdAndScenario(int id, int scenario) {
		return daoAssetTypeValue.findByIdAndScenario(id, scenario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByIdAndMeasure(int,
	 * int)
	 */
	@Override
	public AssetTypeValue findByIdAndMeasure(int id, int measure) {
		return daoAssetTypeValue.findByIdAndMeasure(id, measure);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssetTypeValue#findByMeasure(int)
	 */
	@Override
	public List<AssetTypeValue> findByMeasure(int measure) {
		return daoAssetTypeValue.findByMeasure(measure);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssetTypeValue#findByScenario(int)
	 */
	@Override
	public List<AssetTypeValue> findByScenario(int scenario) {
		return daoAssetTypeValue.findByScenario(scenario);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByAndAnalysis(int)
	 */
	@Override
	public List<AssetTypeValue> findByAndAnalysis(int analysis) {
		return daoAssetTypeValue.findByAndAnalysis(analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssetTypeValue#findAll()
	 */
	@Override
	public List<AssetTypeValue> findAll() {
		return daoAssetTypeValue.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByAssetType(lu.itrust
	 * .business.TS.AssetType)
	 */
	@Override
	public List<AssetTypeValue> findByAssetType(AssetType assetType) {
		return daoAssetTypeValue.findByAssetType(assetType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByAssetTypeAndAnalysis
	 * (lu.itrust.business.TS.AssetType, int)
	 */
	@Override
	public List<AssetTypeValue> findByAssetTypeAndAnalysis(AssetType assetType, int analysis) {
		return daoAssetTypeValue.findByAssetTypeAndAnalysis(assetType, analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#findByAssetTypeAndAnalysis
	 * (java.lang.String, int)
	 */
	@Override
	public List<AssetTypeValue> findByAssetTypeAndAnalysis(String assetType, int analysis) {
		return daoAssetTypeValue.findByAssetTypeAndAnalysis(assetType, analysis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#save(lu.itrust.business
	 * .TS.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue save(AssetTypeValue assetTypeValue) {
		return daoAssetTypeValue.save(assetTypeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#saveOrUpdate(lu.itrust
	 * .business.TS.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue) {
		return daoAssetTypeValue.saveOrUpdate(assetTypeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#merge(lu.itrust.business
	 * .TS.AssetTypeValue)
	 */
	@Transactional
	@Override
	public AssetTypeValue merge(AssetTypeValue assetTypeValue) {
		return daoAssetTypeValue.merge(assetTypeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#delete(lu.itrust.business
	 * .TS.AssetTypeValue)
	 */
	@Transactional
	@Override
	public void delete(AssetTypeValue assetTypeValue) {
		daoAssetTypeValue.delete(assetTypeValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.service.ServiceAssetTypeValue#delete(int)
	 */
	@Transactional
	@Override
	public void delete(int id) {
		daoAssetTypeValue.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.service.ServiceAssetTypeValue#delete(java.util.List)
	 */
	@Transactional
	@Override
	public void delete(List<AssetTypeValue> assetTypeValues) {
		daoAssetTypeValue.delete(assetTypeValues);
	}

}
