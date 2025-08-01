package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOIlrSoaScaleParameter;
import lu.itrust.business.ts.model.parameter.impl.IlrSoaScaleParameter;

@Repository
public class DAOIlrSoaScaleParameterImpl extends DAOHibernate implements DAOIlrSoaScaleParameter {

    @Override
    public boolean belongsToAnalysis(Integer analysisId, Integer id) {
        return createQueryWithCache(
                        "Select count(parameter) > 0 From Analysis analysis inner join analysis.ilrSoaScaleParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id",
                        Boolean.class)
                .setParameter("id", id).setParameter("idAnalysis", analysisId).getSingleResult();
    }

    @Override
    public List<IlrSoaScaleParameter> findByAnalysisId(Integer idAnalysis) {
        return createQueryWithCache(
                        "Select parameter From Analysis analysis inner join analysis.ilrSoaScaleParameters as parameter where analysis.id = :idAnalysis order by parameter.value",
                        IlrSoaScaleParameter.class)
                .setParameter("idAnalysis", idAnalysis).list();
    }

    @Override
    public IlrSoaScaleParameter findOne(Integer id, Integer idAnalysis) {
        return createQueryWithCache(
                        "Select parameter From Analysis analysis inner join analysis.ilrSoaScaleParameters as parameter where analysis.id = :idAnalysis and parameter.id = :id",
                        IlrSoaScaleParameter.class)
                .setParameter("id", id).setParameter("idAnalysis", idAnalysis).getSingleResult();
    }

    @Override
    public long count() {
        return createQueryWithCache(
                        "Select count(*) From IlrSoaScaleParameter",
                        Long.class)
                .uniqueResultOptional().orElse(0L);
    }

    @Override
    public void delete(Collection<? extends IlrSoaScaleParameter> entities) {
        if (entities == null)
            return;
        entities.forEach(this::delete);
    }

    @Override
    public void delete(Integer id) {
        createQueryWithCache("Delete From IlrSoaScaleParameter where id = :id").setParameter("id", id)
                .executeUpdate();
    }

    @Override
    public void delete(IlrSoaScaleParameter entity) {
        getSession().delete(entity);
    }

    @Override
    public void deleteAll() {
        createQueryWithCache("Delete From IlrSoaScaleParameter").executeUpdate();
    }

    @Override
    public boolean exists(Integer id) {
        return createQueryWithCache("Select count(*)>0 From IlrSoaScaleParameter where id = :id", Boolean.class)
                .setParameter("id", id).getSingleResult();
    }

    @Override
    public List<IlrSoaScaleParameter> findAll() {
        return createQueryWithCache("From IlrSoaScaleParameter", IlrSoaScaleParameter.class).getResultList();
    }

    @Override
    public List<IlrSoaScaleParameter> findAll(List<Integer> ids) {
        return createQueryWithCache("From IlrSoaScaleParameter where id in (:ids) order by type, level",
                IlrSoaScaleParameter.class).setParameterList("ids", ids).getResultList();
    }

    @Override
    public IlrSoaScaleParameter findOne(Integer id) {
        return getSession().get(IlrSoaScaleParameter.class, id);
    }

    @Override
    public IlrSoaScaleParameter merge(IlrSoaScaleParameter entity) {
        return (IlrSoaScaleParameter) getSession().merge(entity);
    }

    @Override
    public List<Integer> save(List<IlrSoaScaleParameter> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Integer save(IlrSoaScaleParameter entity) {
        return (Integer) getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(List<IlrSoaScaleParameter> entities) {
        entities.forEach(this::saveOrUpdate);
    }

    @Override
    public void saveOrUpdate(IlrSoaScaleParameter entity) {
        getSession().saveOrUpdate(entity);
    }

}
