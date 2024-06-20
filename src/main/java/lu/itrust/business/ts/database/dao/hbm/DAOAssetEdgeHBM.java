package lu.itrust.business.ts.database.dao.hbm;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAssetEdge;
import lu.itrust.business.ts.model.ilr.AssetEdge;

@Repository
public class DAOAssetEdgeHBM extends DAOHibernate implements DAOAssetEdge {

    public DAOAssetEdgeHBM(Session session) {
        super(session);
    }

    public DAOAssetEdgeHBM() {
    }

    @Override
    public long count() {
        return getSession().createQuery("Select count(*) From AssetEdge", Long.class).uniqueResult();
    }

    @Override
    public void delete(Collection<? extends AssetEdge> entities) {
        if (!(entities == null || entities.isEmpty())) {
            entities.forEach(this::delete);
        }

    }

    @Override
    public void delete(Long id) {
        final AssetEdge entity = findOne(id);
        if (entity != null)
            delete(entity);
    }

    @Override
    public void delete(AssetEdge entity) {
        getSession().delete(entity);
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("Delete from AssetEdge").executeUpdate();
    }

    @Override
    public boolean exists(Long id) {
        return getSession().createQuery("Select count(*)>0 From AssetEdge where id = :id", Boolean.class)
                .setParameter("id", id).uniqueResult();
    }

    @Override
    public List<AssetEdge> findAll() {
        return getSession().createQuery("From AssetEdge", AssetEdge.class).list();
    }

    @Override
    public List<AssetEdge> findAll(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return Collections.emptyList();
        return getSession().createQuery("From AssetEdge where id in :ids", AssetEdge.class).setParameterList("ids", ids)
                .list();
    }

    @Override
    public AssetEdge findOne(Long id) {
        return getSession().get(AssetEdge.class,id);
    }

    @Override
    public AssetEdge merge(AssetEdge entity) {
        return (AssetEdge) getSession().merge(entity);
    }

    @Override
    public List<Long> save(List<AssetEdge> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Long save(AssetEdge entity) {
        return (Long) getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(List<AssetEdge> entities) {
        entities.forEach(this::saveOrUpdate);

    }

    @Override
    public void saveOrUpdate(AssetEdge entity) {
        getSession().saveOrUpdate(entity);
    }

}
