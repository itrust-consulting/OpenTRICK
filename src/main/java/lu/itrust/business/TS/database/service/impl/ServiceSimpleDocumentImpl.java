package lu.itrust.business.TS.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOSimpleDocument;
import lu.itrust.business.TS.database.service.ServiceSimpleDocument;
import lu.itrust.business.TS.model.general.document.impl.SimpleDocument;

@Service
@Transactional(readOnly = true)
public class ServiceSimpleDocumentImpl implements ServiceSimpleDocument {

    @Autowired
    private DAOSimpleDocument daoSimpleDocument;

    @Override
    public List<SimpleDocument> findByAnalysisId(Integer idAnalysis) {
        return daoSimpleDocument.findByAnalysisId(idAnalysis);
    }

    @Override
    public long count() {
        return daoSimpleDocument.count();
    }

    @Transactional
    @Override
    public void delete(Collection<? extends SimpleDocument> entities) {
        daoSimpleDocument.delete(entities);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        daoSimpleDocument.delete(id);
    }

    @Transactional
    @Override
    public void delete(SimpleDocument entity) {
        daoSimpleDocument.delete(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        daoSimpleDocument.deleteAll();
    }

    @Override
    public boolean exists(Long id) {
        return daoSimpleDocument.exists(id);
    }

    @Override
    public List<SimpleDocument> findAll() {
        return daoSimpleDocument.findAll();
    }

    @Override
    public List<SimpleDocument> findAll(List<Long> ids) {
        return daoSimpleDocument.findAll(ids);
    }

    @Override
    public SimpleDocument findOne(Long id) {
        return daoSimpleDocument.findOne(id);
    }

    @Transactional
    @Override
    public SimpleDocument merge(SimpleDocument entity) {
        return daoSimpleDocument.merge(entity);
    }

    @Transactional
    @Override
    public List<Long> save(List<SimpleDocument> entities) {
        return daoSimpleDocument.save(entities);
    }

    @Transactional
    @Override
    public Long save(SimpleDocument entity) {
        return daoSimpleDocument.save(entity);
    }

    @Transactional
    @Override
    public void saveOrUpdate(List<SimpleDocument> entities) {
        daoSimpleDocument.saveOrUpdate(entities);
    }

    @Transactional
    @Override
    public void saveOrUpdate(SimpleDocument entity) {
        daoSimpleDocument.saveOrUpdate(entity);
    }
    
}
