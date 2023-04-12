package lu.itrust.business.ts.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAssetTypeValue;
import lu.itrust.business.ts.model.general.AssetTypeValue;

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
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#get(int)
	 */
	@Override
	public AssetTypeValue get(Integer id)  {
		return (AssetTypeValue) getSession().get(AssetTypeValue.class, id);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#save(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue save(AssetTypeValue assetTypeValue)  {
		return (AssetTypeValue) getSession().save(assetTypeValue);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#saveOrUpdate(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue saveOrUpdate(AssetTypeValue assetTypeValue)  {
		getSession().saveOrUpdate(assetTypeValue);
		return assetTypeValue;
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#merge(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Override
	public AssetTypeValue merge(AssetTypeValue assetTypeValue)  {
		return (AssetTypeValue) getSession().merge(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#delete(lu.itrust.business.ts.model.general.AssetTypeValue)
	 */
	@Override
	public void delete(AssetTypeValue assetTypeValue)  {
		getSession().delete(assetTypeValue);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#delete(int)
	 */
	@Override
	public void delete(Integer id)  {
		delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAssetTypeValue#delete(java.util.List)
	 */
	@Override
	public void delete(List<AssetTypeValue> assetTypeValues)  {
		for (AssetTypeValue assetTypeValue : assetTypeValues)
			delete(assetTypeValue);
	}
}