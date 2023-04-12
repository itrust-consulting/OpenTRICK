package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOAssetImpact;
import lu.itrust.business.ts.database.service.ServiceAssetImpact;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.ilr.AssetImpact;


@Service
@Transactional(readOnly = true)
public class ServiceAssetImpactImpl implements ServiceAssetImpact{

    @Autowired
    private DAOAssetImpact daoAssetImpact;

    @Override
    public long count() {
        return daoAssetImpact.count();
    }

    @Transactional
    @Override
    public void delete(Collection<? extends AssetImpact> entities) {
        daoAssetImpact.delete(entities);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        daoAssetImpact.delete(id);
    }

    @Transactional
    @Override
    public void delete(AssetImpact entity) {
        daoAssetImpact.delete(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        daoAssetImpact.deleteAll();
    }

    @Override
    public boolean exists(Long id) {
        return daoAssetImpact.exists(id);
    }

    @Override
    public List<AssetImpact> findAll() {
        return daoAssetImpact.findAll();
    }

    @Override
    public List<AssetImpact> findAll(List<Long> ids) {
        return daoAssetImpact.findAll(ids);
    }

    @Override
    public AssetImpact findOne(Long id) {
        return daoAssetImpact.findOne(id);
    }

    @Transactional
    @Override
    public AssetImpact merge(AssetImpact entity) {
        return daoAssetImpact.merge(entity);
    }

    @Transactional
    @Override
    public List<Long> save(List<AssetImpact> entities) {
        return daoAssetImpact.save(entities);
    }

    @Transactional
    @Override
    public Long save(AssetImpact entity) {
        return daoAssetImpact.save(entity);
    }

    @Transactional
    @Override
    public void saveOrUpdate(List<AssetImpact> entities) {
        daoAssetImpact.saveOrUpdate(entities);
    }

    @Transactional
    @Override
    public void saveOrUpdate(AssetImpact entity) {
        daoAssetImpact.saveOrUpdate(entity);
    }

    @Override
    public List<AssetImpact> findByAsset(Asset asset) {
        return daoAssetImpact.findByAsset(asset);
    }

    @Override
    public List<AssetImpact> findByAssetId(int assetId) {
        return daoAssetImpact.findByAssetId(assetId);
    }

    @Override
    public List<AssetImpact> findByAnalysisIdAndAssetName(int analysisId, String assetName) {
        return daoAssetImpact.findByAnalysisIdAndAssetName(analysisId, assetName);
    }

    @Override
    public boolean belongsToAnalysis(Integer analysisId, Long id) {
        return daoAssetImpact.belongsToAnalysis(analysisId, id);
    }

    @Override
    public List<AssetImpact> findByAnalysisId(Integer idAnalysis) {
        return daoAssetImpact.findByAnalysisId(idAnalysis);
    }

    @Override
    public AssetImpact findOne(Long id, Integer idAnalysis) {
        return daoAssetImpact.findOne(id, idAnalysis);
    }
    
}
