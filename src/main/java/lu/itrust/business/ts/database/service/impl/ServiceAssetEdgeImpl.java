package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOAssetEdge;
import lu.itrust.business.ts.database.service.ServiceAssetEdge;
import lu.itrust.business.ts.model.ilr.AssetEdge;

@Service
@Transactional(readOnly = true)
public class ServiceAssetEdgeImpl implements ServiceAssetEdge {

    @Autowired
    private DAOAssetEdge daoAssetEdge;

    @Override
    public long count() {
        return daoAssetEdge.count();
    }

    @Transactional
    @Override
    public void delete(Collection<? extends AssetEdge> entities) {
        daoAssetEdge.delete(entities);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        daoAssetEdge.delete(id);
    }

    @Transactional
    @Override
    public void delete(AssetEdge entity) {
        daoAssetEdge.delete(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        daoAssetEdge.deleteAll();
    }

    @Override
    public boolean exists(Long id) {
        return daoAssetEdge.exists(id);
    }

    @Override
    public List<AssetEdge> findAll() {
        return daoAssetEdge.findAll();
    }

    @Override
    public List<AssetEdge> findAll(List<Long> ids) {
        return daoAssetEdge.findAll(ids);
    }

    @Override
    public AssetEdge findOne(Long id) {
        return daoAssetEdge.findOne(id);
    }

    @Transactional
    @Override
    public AssetEdge merge(AssetEdge entity) {
        return daoAssetEdge.merge(entity);
    }

    @Transactional
    @Override
    public List<Long> save(List<AssetEdge> entities) {
        return daoAssetEdge.save(entities);
    }

    @Transactional
    @Override
    public Long save(AssetEdge entity) {
        return daoAssetEdge.save(entity);
    }

    @Transactional
    @Override
    public void saveOrUpdate(List<AssetEdge> entities) {
        daoAssetEdge.saveOrUpdate(entities);
    }

    @Transactional
    @Override
    public void saveOrUpdate(AssetEdge entity) {
        daoAssetEdge.saveOrUpdate(entity);
    }
    
}
