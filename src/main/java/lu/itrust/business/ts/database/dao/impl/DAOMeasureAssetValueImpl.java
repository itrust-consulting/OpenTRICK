package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOMeasureAssetValue;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;

/**
 * DAOAssetTypeValueImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Jan 16, 2013
 */
@Repository
public class DAOMeasureAssetValueImpl extends DAOHibernate implements DAOMeasureAssetValue {

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
		return createQueryWithCache("From MeasureAssetValue where asset.id = :idAsset").setParameter("idAsset", idAsset).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public MeasureAssetValue getByMeasureIdAndAssetId(int measureId, int assetId) {
		return (MeasureAssetValue) createQueryWithCache(
						"Select measureAssetValue From AssetMeasure assetMeasure inner join assetMeasure.measureAssetValues as measureAssetValue where assetMeasure.id = :idMeasure and measureAssetValue.asset.id = :idAsset")
				.setParameter("idMeasure", measureId).setParameter("idAsset", assetId).uniqueResultOptional().orElse(null);
	}

}