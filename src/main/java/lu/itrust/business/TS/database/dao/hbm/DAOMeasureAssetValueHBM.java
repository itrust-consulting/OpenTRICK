package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOMeasureAssetValue;
import lu.itrust.business.TS.model.standard.measure.impl.MeasureAssetValue;

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
	public MeasureAssetValue get(Integer id)  {
		return (MeasureAssetValue) getSession().get(MeasureAssetValue.class, id);
	}

	@Override
	public void save(MeasureAssetValue measureAssetValue)  {
		getSession().save(measureAssetValue);
	}

	@Override
	public void saveOrUpdate(MeasureAssetValue measureAssetValue)  {
		getSession().saveOrUpdate(measureAssetValue);
	}

	@Override
	public void delete(MeasureAssetValue measureAssetValue)  {
		getSession().delete(measureAssetValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureAssetValue> getByAssetId(int idAsset) {
		return getSession().createQuery("From MeasureAssetValue where asset.id = :idAsset").setParameter("idAsset", idAsset).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MeasureAssetValue getByMeasureIdAndAssetId(int measureId, int assetId) {
		return (MeasureAssetValue) getSession()
				.createQuery(
						"Select measureAssetValue From AssetMeasure assetMeasure inner join assetMeasure.measureAssetValues as measureAssetValue where assetMeasure.id = :idMeasure and measureAssetValue.asset.id = :idAsset")
				.setParameter("idMeasure", measureId).setParameter("idAsset", assetId).uniqueResultOptional().orElse(null);
	}

}