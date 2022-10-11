package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAssetNode;
import lu.itrust.business.TS.database.service.ServiceAssetNode;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.ilr.AssetNode;

@Service
@Transactional(readOnly = true)
public class ServiceAssetNodeImpl implements ServiceAssetNode {

    @Autowired
    private DAOAssetNode daoAssetNode;

    @Override
    public long count() {
        return daoAssetNode.count();
    }

    @Transactional
    @Override
    public void delete(Collection<? extends AssetNode> entities) {
        daoAssetNode.delete(entities);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        daoAssetNode.delete(id);
    }

    @Transactional
    @Override
    public void delete(AssetNode entity) {
        daoAssetNode.delete(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        daoAssetNode.deleteAll();
    }

    @Override
    public boolean exists(Long id) {
        return daoAssetNode.exists(id);
    }

    @Override
    public List<AssetNode> findAll() {
        return daoAssetNode.findAll();
    }

    @Override
    public List<AssetNode> findAll(List<Long> ids) {
        return daoAssetNode.findAll(ids);
    }

    @Override
    public AssetNode findOne(Long id) {
        return daoAssetNode.findOne(id);
    }

    @Transactional
    @Override
    public AssetNode merge(AssetNode entity) {
        return daoAssetNode.merge(entity);
    }

    @Transactional
    @Override
    public List<Long> save(List<AssetNode> entities) {
        return daoAssetNode.save(entities);
    }

    @Transactional
    @Override
    public Long save(AssetNode entity) {
        return daoAssetNode.save(entity);
    }

    @Transactional
    @Override
    public void saveOrUpdate(List<AssetNode> entities) {
        daoAssetNode.saveOrUpdate(entities);
    }

    @Transactional
    @Override
    public void saveOrUpdate(AssetNode entity) {
        daoAssetNode.saveOrUpdate(entity);
    }

    @Override
    public List<AssetNode> findByAsset(Asset asset) {
        return daoAssetNode.findByAsset(asset);
    }

    @Override
    public List<AssetNode> findByAssetId(int assetId) {
        return daoAssetNode.findByAssetId(assetId);
    }

    @Override
    public List<AssetNode> findByAnalysisIdAndAssetName(int analysisId, String assetName) {
        return daoAssetNode.findByAnalysisIdAndAssetName(analysisId, assetName);
    }

    @Override
    public boolean belongsToAnalysis(Integer analysisId, Long id) {
        return daoAssetNode.belongsToAnalysis(analysisId, id);
    }

    @Override
    public List<AssetNode> findByAnalysisId(Integer idAnalysis) {
        return daoAssetNode.findByAnalysisId(idAnalysis);
    }

    @Override
    public AssetNode findOne(Long id, Integer idAnalysis) {
        return daoAssetNode.findOne(id, idAnalysis);
    }
    
}
