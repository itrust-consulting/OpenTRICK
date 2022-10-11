package lu.itrust.business.TS.database.dao.hbm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOAssetImpact;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.ilr.AssetImpact;

@Repository
public class DAOAssetImpactHBM extends DAOHibernate implements DAOAssetImpact {

    public DAOAssetImpactHBM(Session session) {
        super(session);
    }

    public DAOAssetImpactHBM() {
    }

    @Override
    public long count() {
        return getSession().createQuery("Select count(*) From AssetImpact", Long.class).uniqueResult();
    }

    @Override
    public void delete(Collection<? extends AssetImpact> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void delete(Long id) {
        final AssetImpact entity = findOne(id);
        if (entity != null)
            delete(entity);
    }

    @Override
    public void delete(AssetImpact entity) {
        getSession().delete(entity);
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("Delete from AssetImpact").executeUpdate();
    }

    @Override
    public boolean exists(Long id) {
        return getSession().createQuery("Select count(*)>0 From AssetImpact where id = :id", Boolean.class)
                .setParameter("id", id).uniqueResult();
    }

    @Override
    public List<AssetImpact> findAll() {
        return getSession().createQuery("From AssetImpact", AssetImpact.class).list();
    }

    @Override
    public List<AssetImpact> findAll(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Collections.emptyList();
        return getSession().createQuery("From AssetImpact where id in :ids", AssetImpact.class)
                .setParameterList("ids", ids)
                .list();
    }

    @Override
    public AssetImpact findOne(Long id) {
        return getSession().get(AssetImpact.class, id);
    }

    @Override
    public AssetImpact merge(AssetImpact entity) {
        return (AssetImpact) getSession().merge(entity);
    }

    @Override
    public List<Long> save(List<AssetImpact> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Long save(AssetImpact entity) {
        return (Long) getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(List<AssetImpact> entities) {
        entities.forEach(this::saveOrUpdate);

    }

    @Override
    public void saveOrUpdate(AssetImpact entity) {
        getSession().saveOrUpdate(entity);

    }

    @Override
    public List<AssetImpact> findByAsset(Asset asset) {
        return getSession().createQuery("From AssetImpact where asset = :asset", AssetImpact.class)
                .setParameter("asset", asset).list();
    }

    @Override
    public List<AssetImpact> findByAssetId(int assetId) {
        return getSession().createQuery("From AssetImpact where asset.id = :assetId", AssetImpact.class)
                .setParameter("assetId", assetId).list();
    }

    @Override
    public List<AssetImpact> findByAnalysisIdAndAssetName(int analysisId, String assetName) {
        return getSession().createQuery(
                "Select assetImpact From Analysis as analysis, AssetImpact as assetImpact where analysis.id = :analysisId and assetImpact.asset in analysis.assets and assetImpact.asset.name = :assetName",
                AssetImpact.class)
                .setParameter("analysisId", analysisId).setParameter("assetName", assetName).list();
    }

    @Override
    public boolean belongsToAnalysis(Integer analysisId, Long id) {
        return getSession().createQuery(
                "Select count(assetImpact) > 0 From Analysis as analysis, AssetImpact as assetImpact where assetImpact.id = :id and analysis.id = :analysisId and assetImpact.asset in analysis.assets",
                Boolean.class)
                .setParameter("analysisId", analysisId).setParameter("id", id).uniqueResult();
    }

    @Override
    public List<AssetImpact> findByAnalysisId(Integer analysisId) {
        return getSession().createQuery(
                "Select assetImpact From Analysis as analysis, AssetImpact as assetImpact where analysis.id = :analysisId and assetImpact.asset in analysis.assets",
                AssetImpact.class)
                .setParameter("analysisId", analysisId).list();
    }

    @Override
    public AssetImpact findOne(Long id, Integer analysisId) {
        return getSession().createQuery(
                "Select assetImpact From Analysis as analysis, AssetImpact as assetImpact where assetImpact.id = :id and analysis.id = :analysisId and assetImpact.asset in analysis.assets",
                AssetImpact.class)
                .setParameter("analysisId", analysisId).setParameter("id", id).uniqueResult();
    }

}
