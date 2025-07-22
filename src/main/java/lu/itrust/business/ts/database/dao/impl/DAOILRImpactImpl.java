package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOILRImpact;
import lu.itrust.business.ts.model.ilr.ILRImpact;

@Repository
public class DAOILRImpactImpl extends DAOHibernate implements DAOILRImpact {

    public DAOILRImpactImpl() {
    }

    public DAOILRImpactImpl(Session session) {
        super(session);
    }

    @Override
    public long count() {
        return createQueryWithCache("Select count(*) From ILRImpact", Long.class).uniqueResult();
    }

    @Override
    public void delete(Collection<? extends ILRImpact> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void delete(Long id) {
        final ILRImpact entity = findOne(id);
        if (entity != null)
            delete(entity);
    }

    @Override
    public void delete(ILRImpact entity) {
        getSession().delete(entity);

    }

    @Override
    public void deleteAll() {
        createQueryWithCache("Delete from ILRImpact").executeUpdate();

    }

    @Override
    public boolean exists(Long id) {
        return createQueryWithCache("Select count(*)>0 From ILRImpact where id = :id", Boolean.class)
                .setParameter("id", id).uniqueResult();
    }

    @Override
    public List<ILRImpact> findAll() {
        return createQueryWithCache("From ILRImpact", ILRImpact.class).list();
    }

    @Override
    public List<ILRImpact> findAll(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Collections.emptyList();
        return createQueryWithCache("From ILRImpact where id in :ids", ILRImpact.class)
                .setParameterList("ids", ids)
                .list();
    }

    @Override
    public ILRImpact findOne(Long id) {
        return getSession().get(ILRImpact.class, id);
    }

    @Override
    public ILRImpact merge(ILRImpact entity) {
        return (ILRImpact) getSession().merge(entity);
    }

    @Override
    public List<Long> save(List<ILRImpact> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Long save(ILRImpact entity) {
        return (Long) getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(List<ILRImpact> entities) {
        entities.forEach(this::saveOrUpdate);
    }

    @Override
    public void saveOrUpdate(ILRImpact entity) {
        getSession().saveOrUpdate(entity);
    }

}
