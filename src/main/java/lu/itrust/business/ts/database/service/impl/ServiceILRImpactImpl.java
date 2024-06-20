package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOILRImpact;
import lu.itrust.business.ts.database.service.ServiceILRImpact;
import lu.itrust.business.ts.model.ilr.ILRImpact;

@Service
@Transactional(readOnly = true)
public class ServiceILRImpactImpl implements ServiceILRImpact {

    @Autowired
    private DAOILRImpact daoILRImpact;

    @Override
    public long count() {
        return daoILRImpact.count();
    }

    @Transactional
    @Override
    public void delete(Collection<? extends ILRImpact> entities) {
        daoILRImpact.delete(entities);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        daoILRImpact.delete(id);
    }

    @Transactional
    @Override
    public void delete(ILRImpact entity) {
        daoILRImpact.delete(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        daoILRImpact.deleteAll();
    }

    @Override
    public boolean exists(Long id) {
        return daoILRImpact.exists(id);
    }

    @Override
    public List<ILRImpact> findAll() {
        return daoILRImpact.findAll();
    }

    @Override
    public List<ILRImpact> findAll(List<Long> ids) {
        return daoILRImpact.findAll(ids);
    }

    @Override
    public ILRImpact findOne(Long id) {
        return daoILRImpact.findOne(id);
    }

    @Transactional
    @Override
    public ILRImpact merge(ILRImpact entity) {
        return daoILRImpact.merge(entity);
    }

    @Transactional
    @Override
    public List<Long> save(List<ILRImpact> entities) {
        return daoILRImpact.save(entities);
    }

    @Transactional
    @Override
    public Long save(ILRImpact entity) {
        return daoILRImpact.save(entity);
    }

    @Transactional
    @Override
    public void saveOrUpdate(List<ILRImpact> entities) {
        daoILRImpact.saveOrUpdate(entities);
    }

    @Transactional
    @Override
    public void saveOrUpdate(ILRImpact entity) {
        daoILRImpact.saveOrUpdate(entity);
    }
    
}
