package lu.itrust.business.TS.database.dao.hbm;

import lu.itrust.business.TS.database.dao.DAOMeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.MeasureAssetValue;

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
public class DAOMeasureAssetValueHBM extends DAOHibernate implements DAOMeasureAssetValue {

	@Override
	public MeasureAssetValue get(Integer id) throws Exception {
		return (MeasureAssetValue) getSession().get(MeasureAssetValue.class, id);
	}

	@Override
	public void save(MeasureAssetValue measureAssetValue) throws Exception {
		getSession().save(measureAssetValue);
	}

	@Override
	public void saveOrUpdate(MeasureAssetValue measureAssetValue) throws Exception {
		getSession().saveOrUpdate(measureAssetValue);
	}

	@Override
	public void delete(MeasureAssetValue measureAssetValue) throws Exception {
		getSession().delete(measureAssetValue);
	}

	@Override
	public MeasureAssetValue getByAssetId(int idAsset) {
		return (MeasureAssetValue) getSession().createQuery("From MeasureAssetValue where asset.id = :idAsset").setParameter("idAsset", idAsset).uniqueResult();
	}

	
}