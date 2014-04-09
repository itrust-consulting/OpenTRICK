/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.dao.DAOAssetTypeValue;

/**
 * @author eomar
 *
 */
@Repository
public class DAOAssetTypeValueHBM extends DAOHibernate implements DAOAssetTypeValue {
	
	/**
	 * 
	 */
	public DAOAssetTypeValueHBM() {
	}

	/**
	 * @param session
	 */
	public DAOAssetTypeValueHBM(Session session) {
		super(session);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findOne(int)
	 */
	@Override
	public AssetTypeValue findOne(int id) {
		return (AssetTypeValue) getSession().get(AssetTypeValue.class, id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByIdAndAnalysis(int, int)
	 */
	@Override
	public AssetTypeValue findByIdAndAnalysis(int id, int analysis) {
		return null; //getSession().createQuery("Select assetTypeValue From AssetTypeValue assetTypeValue,  AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure, Scenario as scenario where scenario.analysis.id = :Analysis and assetTypeValue in scenario.assetTypeValues or analysisNorm.analysis.id = :idAnalysis and ");
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByIdAndScenario(int, int)
	 */
	@Override
	public AssetTypeValue findByIdAndScenario(int id, int scenario) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByIdAndMeasure(int, int)
	 */
	@Override
	public AssetTypeValue findByIdAndMeasure(int id, int measure) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByMeasure(int)
	 */
	@Override
	public List<AssetTypeValue> findByMeasure(int measure) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByScenario(int)
	 */
	@Override
	public List<AssetTypeValue> findByScenario(int scenario) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByAndAnalysis(int)
	 */
	@Override
	public List<AssetTypeValue> findByAndAnalysis(int analysis) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findAll()
	 */
	@Override
	public List<AssetTypeValue> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByAssetType(lu.itrust.business.TS.AssetType)
	 */
	@Override
	public List<AssetTypeValue> findByAssetType(AssetType assetType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByAssetTypeAndAnalysis(lu.itrust.business.TS.AssetType, int)
	 */
	@Override
	public List<AssetTypeValue> findByAssetTypeAndAnalysis(AssetType assetType, int analysis) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#findByAssetTypeAndAnalysis(java.lang.String, int)
	 */
	@Override
	public List<AssetTypeValue> findByAssetTypeAndAnalysis(String assetType, int analysis) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#save(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue save(AssetTypeValue assetTypeValue) {
		return (AssetTypeValue) getSession().save(assetTypeValue);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#saveOrUpdate(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue) {
		getSession().saveOrUpdate(assetTypeValue);
		return assetTypeValue;
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#merge(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue merge(AssetTypeValue assetTypeValue) {
		return (AssetTypeValue) getSession().merge(assetTypeValue);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#delete(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public void delete(AssetTypeValue assetTypeValue) {
		getSession().delete(assetTypeValue);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#delete(int)
	 */
	@Override
	public void delete(int id) {
		delete(findOne(id));
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#delete(java.util.List)
	 */
	@Override
	public void delete(List<AssetTypeValue> assetTypeValues) {
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			delete(assetTypeValue);
	}

}
