package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAssetNode;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.ilr.AssetNode;

@Repository
public class DAOAssetNodeImpl extends DAOHibernate implements DAOAssetNode {

    public DAOAssetNodeImpl(Session session) {
        super(session);
    }

    public DAOAssetNodeImpl() {
    }

    @Override
    public long count() {
        return createQueryWithCache("Select count(*) From AssetNode", Long.class).uniqueResult();
    }

    @Override
    public void delete(Collection<? extends AssetNode> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void delete(Long id) {
        final AssetNode entity = findOne(id);
        if (entity != null)
            delete(entity);
    }

    @Override
    public void delete(AssetNode entity) {
        getSession().delete(entity);
    }

    @Override
    public void deleteAll() {
        createQueryWithCache("Delete from AssetNode").executeUpdate();
    }

    @Override
    public boolean exists(Long id) {
        return createQueryWithCache("Select count(*)>0 From AssetNode where id = :id", Boolean.class)
                .setParameter("id", id).uniqueResult();
    }

    @Override
    public List<AssetNode> findAll() {
        return createQueryWithCache("From AssetNode", AssetNode.class).list();
    }

    @Override
    public List<AssetNode> findAll(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Collections.emptyList();
        return createQueryWithCache("From AssetNode where id in :ids", AssetNode.class)
                .setParameterList("ids", ids)
                .list();
    }

    @Override
    public AssetNode findOne(Long id) {
        return getSession().get(AssetNode.class, id);
    }

    @Override
    public AssetNode merge(AssetNode entity) {
        return (AssetNode) getSession().merge(entity);
    }

    @Override
    public List<Long> save(List<AssetNode> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Long save(AssetNode entity) {
        return (Long) getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(List<AssetNode> entities) {
        entities.forEach(this::saveOrUpdate);
    }

    @Override
    public void saveOrUpdate(AssetNode entity) {
        getSession().saveOrUpdate(entity);
    }

    @Override
    public List<AssetNode> findByAsset(Asset asset) {
        return createQueryWithCache("From AssetNode where impact.asset = :asset", AssetNode.class)
                .setParameter("asset", asset).list();
    }

    @Override
    public List<AssetNode> findByAssetId(int assetId) {
        return createQueryWithCache("From AssetNode where impact.asset.id = :assetId", AssetNode.class)
                .setParameter("assetId", assetId).list();
    }

    @Override
    public List<AssetNode> findByAnalysisIdAndAssetName(int analysisId, String assetName) {
        return createQueryWithCache(
                "Select node From Analysis as analysis inner join analysis.assetNodes as node where analysis.id = :analysisId and node.assetImpact.asset.name = :assetName",
                AssetNode.class)
                .setParameter("analysisId", analysisId).setParameter("assetName", assetName).list();
    }

    @Override
    public boolean belongsToAnalysis(Integer analysisId, Long id) {
        return createQueryWithCache(
                "Select count(node) > 0 From Analysis as analysis inner join analysis.assetNodes as node where analysis.id = :analysisId and node.id = :id",
                Boolean.class)
                .setParameter("analysisId", analysisId).setParameter("id", id).uniqueResult();
    }

    @Override
    public List<AssetNode> findByAnalysisId(Integer analysisId) {
        return createQueryWithCache(
            "Select node From Analysis as analysis inner join analysis.assetNodes as node where analysis.id = :analysisId",
            AssetNode.class)
            .setParameter("analysisId", analysisId).list();
    }

    @Override
    public AssetNode findOne(Long id, Integer analysisId) {
        return createQueryWithCache(
            "Select node From Analysis as analysis inner join analysis.assetNodes as node where analysis.id = :analysisId and node.id = :id",
            AssetNode.class)
            .setParameter("analysisId", analysisId).setParameter("id", id).uniqueResult();
    }

}
