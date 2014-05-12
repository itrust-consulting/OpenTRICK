package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.dao.DAOAssetTypeValue;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOAssetTypeValueHBM.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
@Repository
public class DAOAssetTypeValueHBM extends DAOHibernate implements DAOAssetTypeValue {

	/**
	 * Constructor: <br>
	 */
	public DAOAssetTypeValueHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAssetTypeValueHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#get(int)
	 */
	@Override
	public AssetTypeValue get(int id) throws Exception {
		return (AssetTypeValue) getSession().get(AssetTypeValue.class, id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#save(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue save(AssetTypeValue assetTypeValue) throws Exception {
		return (AssetTypeValue) getSession().save(assetTypeValue);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#saveOrUpdate(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue) throws Exception {
		getSession().saveOrUpdate(assetTypeValue);
		return assetTypeValue;
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#merge(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue merge(AssetTypeValue assetTypeValue) throws Exception {
		return (AssetTypeValue) getSession().merge(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#delete(lu.itrust.business.TS.AssetTypeValue)
	 */
	@Override
	public void delete(AssetTypeValue assetTypeValue) throws Exception {
		getSession().delete(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#delete(int)
	 */
	@Override
	public void delete(int id) throws Exception {
		delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAssetTypeValue#delete(java.util.List)
	 */
	@Override
	public void delete(List<AssetTypeValue> assetTypeValues) throws Exception {
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			delete(assetTypeValue);
	}
}