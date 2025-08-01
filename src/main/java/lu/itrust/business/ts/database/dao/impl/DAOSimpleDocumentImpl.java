package lu.itrust.business.ts.database.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOSimpleDocument;
import lu.itrust.business.ts.model.general.document.impl.SimpleDocument;

@Repository
public class DAOSimpleDocumentImpl extends DAOHibernate implements DAOSimpleDocument {

    @Override
    public List<SimpleDocument> findByAnalysisId(Integer idAnalysis) {
        return createQueryWithCache(
                "Select document From Analysis analysis inner join documents as document where analysis.id = :idAnalysis",
                SimpleDocument.class).setParameter("idAnalysis", idAnalysis).list();
    }

    @Override
    public long count() {
        return createQueryWithCache("Select count(*) From SimpleDocument", Long.class).uniqueResult();
    }

    @Override
    public void delete(Collection<? extends SimpleDocument> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void delete(Long id) {
        createQuery("Delete From SimpleDocument WHERE id = :id").setParameter("id", id).executeUpdate();
    }

    @Override
    public void delete(SimpleDocument entity) {
        getSession().delete(entity);
    }

    @Override
    public void deleteAll() {
        createQuery("Delete From SimpleDocument").executeUpdate();
    }

    @Override
    public boolean exists(Long id) {
        return createQueryWithCache("Select count(*) > 0 From SimpleDocument where id = :id", Boolean.class)
                .setParameter("id", id).uniqueResult();
    }

    @Override
    public List<SimpleDocument> findAll() {
        return createQueryWithCache("From SimpleDocument", SimpleDocument.class).list();
    }

    @Override
    public List<SimpleDocument> findAll(List<Long> ids) {
        return ids == null || ids.isEmpty() ? Collections.emptyList()
                : createQueryWithCache("From SimpleDocument where id in :ids", SimpleDocument.class)
                        .setParameterList("ids", ids).list();
    }

    @Override
    public SimpleDocument findOne(Long id) {
        return getSession().get(SimpleDocument.class, id);
    }

    @Override
    public SimpleDocument merge(SimpleDocument entity) {
        return (SimpleDocument) getSession().merge(entity);
    }

    @Override
    public List<Long> save(List<SimpleDocument> entities) {
        return entities.stream().map(this::save).collect(Collectors.toList());
    }

    @Override
    public Long save(SimpleDocument entity) {
        return (Long) getSession().save(entity);
    }

    @Override
    public void saveOrUpdate(List<SimpleDocument> entities) {
        entities.forEach(this::saveOrUpdate);

    }

    @Override
    public void saveOrUpdate(SimpleDocument entity) {
        getSession().saveOrUpdate(entity);
    }

}
