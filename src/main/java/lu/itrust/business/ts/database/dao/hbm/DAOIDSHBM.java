/**
 * 
 */
package lu.itrust.business.ts.database.dao.hbm;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOIDS;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.usermanagement.IDS;

/**
 * @author eomar
 *
 */
@Repository
public class DAOIDSHBM extends DAOHibernate implements DAOIDS {

	/**
	 * 
	 */
	public DAOIDSHBM() {
	}

	/**
	 * @param session
	 */
	public DAOIDSHBM(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOIDS#get(int)
	 */
	@Override
	public IDS get(int id) {
		return getSession().get(IDS.class, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOIDS#get(java.lang.String)
	 */
	@Override
	public IDS get(String prefix) {
		return (IDS) getSession().createQuery("From IDS where prefix = :prefix").setParameter("prefix", prefix).getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOIDS#getByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<IDS> getByAnalysisId(int idAnalysis) {
		return getSession()
				.createQuery("Select ids From IDS ids inner join ids.subscribers as subscriber  where ids.enable = true and subscriber.id = :idAnalysis order by ids.prefix")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOIDS#getByAnalysis(lu.itrust.
	 * business.ts.model.analysis.Analysis)
	 */
	@Override
	public List<IDS> getByAnalysis(Analysis analysis) {
		if (analysis == null || analysis.getId() < 1)
			return Collections.emptyList();
		return getByAnalysisId(analysis.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOIDS#save(lu.itrust.business.ts.
	 * usermanagement.IDS)
	 */
	@Override
	public Integer save(IDS ids) {
		return (Integer) getSession().save(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.ts.database.dao.DAOIDS#saveOrUpdate(lu.itrust.business
	 * .ts.usermanagement.IDS)
	 */
	@Override
	public void saveOrUpdate(IDS ids) {
		getSession().saveOrUpdate(ids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOIDS#delete(lu.itrust.business.ts.
	 * usermanagement.IDS)
	 */
	@Override
	public void delete(IDS ids) {
		getSession().delete(ids);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IDS> getAllByState(boolean enabled) {
		return getSession().createQuery("From IDS ids where ids.enable = :enabled order by ids.prefix").setParameter("enabled", enabled).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IDS> getAllNoSubscribers() {
		return getSession().createQuery("From IDS ids where ids.subscribers IS EMPTY order by ids.prefix").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IDS> getAll() {
		return getSession().createQuery("From IDS order by prefix").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getPrefixesByAnalysisId(int idAnalysis) {
		return getSession()
				.createQuery("Select ids.prefix From IDS ids inner join ids.subscribers as subscriber  where ids.enable = true and subscriber.id = :idAnalysis order by ids.prefix")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@Override
	public List<String> getPrefixesByAnalysis(Analysis analysis) {
		if (analysis == null || analysis.getId() < 1)
			return Collections.emptyList();
		return getPrefixesByAnalysisId(analysis.getId());
	}

	@Override
	public boolean existByPrefix(String prefix) {
		return (boolean) getSession().createQuery("Select count(*)> 0 From IDS where prefix = :prefix").setParameter("prefix", prefix).getSingleResult();
	}

	@Override
	public boolean exists(String token) {
		return (boolean) getSession().createQuery("Select count(*)> 0 From IDS where token = :token").setParameter("token", token).getSingleResult();
	}

	@Override
	public void delete(Integer id) {
		getSession().createQuery("Delete IDS where id = :id").setParameter("id", id).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IDS> getAllAnalysisNoSubscribe(Integer idAnalysis) {
		return getSession().createQuery(
				"Select ids From IDS ids where ids.enable = true and ids not in ( Select subIds From IDS subIds inner join subIds.subscribers as subscriber where subIds.enable = true and subscriber.id = :idAnalysis) order by ids.prefix")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IDS getByToken(String token) {
		return (IDS) getSession().createQuery("From IDS ids where ids.token = :token ").setParameter("token", token).uniqueResultOptional().orElse(null);
	}

	@Override
	public boolean exists(boolean state) {
		return getSession().createQuery("Select count(*)> 0 From IDS ids where ids.enable = :enabled", Boolean.class).setParameter("enabled", state).uniqueResultOptional()
				.orElse(false);
	}

	@Override
	public Stream<Integer> findSubscriberIdByUsername(String username) {
		return getSession().createQuery("Select distinct (subscriber.id) From IDS ids inner join ids.subscribers as subscriber where ids.prefix = :prefix", Integer.class)
				.setParameter("prefix", username).getResultStream();
	}

}
