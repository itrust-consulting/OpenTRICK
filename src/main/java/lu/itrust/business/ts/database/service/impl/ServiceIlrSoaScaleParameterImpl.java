package lu.itrust.business.ts.database.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOIlrSoaScaleParameter;
import lu.itrust.business.ts.database.service.ServiceIlrSoaScaleParameter;
import lu.itrust.business.ts.model.parameter.impl.IlrSoaScaleParameter;

@Service
@Transactional(readOnly = true)
public class ServiceIlrSoaScaleParameterImpl implements ServiceIlrSoaScaleParameter{

    @Autowired
    private DAOIlrSoaScaleParameter daoIlrSoaScaleParameter;

    @Override
    public boolean belongsToAnalysis(Integer analysisId, Integer id) {
        return daoIlrSoaScaleParameter.belongsToAnalysis(analysisId, id);
    }

    @Override
    public List<IlrSoaScaleParameter> findByAnalysisId(Integer idAnalysis) {
        return daoIlrSoaScaleParameter.findByAnalysisId(idAnalysis);
    }

    @Override
    public IlrSoaScaleParameter findOne(Integer id, Integer idAnalysis) {
        return daoIlrSoaScaleParameter.findOne(id, idAnalysis);
    }

    @Override
    public long count() {
        return daoIlrSoaScaleParameter.count();
    }

    @Transactional
    @Override
    public void delete(Collection<? extends IlrSoaScaleParameter> entities) {
        daoIlrSoaScaleParameter.delete(entities);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        daoIlrSoaScaleParameter.delete(id);
    }

    @Transactional
    @Override
    public void delete(IlrSoaScaleParameter entity) {
        daoIlrSoaScaleParameter.delete(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        daoIlrSoaScaleParameter.deleteAll();
    }

    @Override
    public boolean exists(Integer id) {
        return daoIlrSoaScaleParameter.exists(id);
    }

    @Override
    public List<IlrSoaScaleParameter> findAll() {
        return daoIlrSoaScaleParameter.findAll();
    }

    @Override
    public List<IlrSoaScaleParameter> findAll(List<Integer> ids) {
        return daoIlrSoaScaleParameter.findAll(ids);
    }

    @Override
    public IlrSoaScaleParameter findOne(Integer id) {
        return daoIlrSoaScaleParameter.findOne(id);
    }

    @Transactional
    @Override
    public IlrSoaScaleParameter merge(IlrSoaScaleParameter entity) {
        return daoIlrSoaScaleParameter.merge(entity);
    }

    @Transactional
    @Override
    public List<Integer> save(List<IlrSoaScaleParameter> entities) {
        return daoIlrSoaScaleParameter.save(entities);
    }

    @Transactional
    @Override
    public Integer save(IlrSoaScaleParameter entity) {
        return daoIlrSoaScaleParameter.save(entity);
    }

    @Transactional
    @Override
    public void saveOrUpdate(List<IlrSoaScaleParameter> entities) {
        daoIlrSoaScaleParameter.saveOrUpdate(entities);
    }

    @Transactional
    @Override
    public void saveOrUpdate(IlrSoaScaleParameter entity) {
        daoIlrSoaScaleParameter.saveOrUpdate(entity);
    }
    
}
