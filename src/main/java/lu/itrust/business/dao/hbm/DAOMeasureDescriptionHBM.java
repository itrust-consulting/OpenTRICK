package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.r.l. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOMeasureDescriptionHBM extends DAOHibernate implements lu.itrust.business.dao.DAOMeasureDescription {
	
	/**
	 * 
	 */
	public DAOMeasureDescriptionHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAOMeasureDescriptionHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#get(int)
	 */
	@Override
	public MeasureDescription get(int id) throws Exception {
		return (MeasureDescription) getSession().get(MeasureDescription.class, id);
	}

	/**
	 * getByReferenceNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getByReferenceNorm(java.lang.String,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public MeasureDescription getByReferenceNorm(String reference, Norm norm) throws Exception {

		Query query = getSession().createQuery("From MeasureDescription where norm = :norm and reference = :reference");
		query.setParameter("norm", norm);
		query.setString("reference", reference);
		return (MeasureDescription) query.uniqueResult();

	}

	/**
	 * exists: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#exists(java.lang.String,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public boolean exists(String reference, Norm norm) throws Exception {

		Query query = getSession().createQuery("Select count(*) From MeasureDescription where norm = :norm and reference = :reference");
		query.setParameter("norm", norm);
		query.setString("reference", reference);
		return (Long) query.uniqueResult() >= 1;

	}

	/**
	 * existsWithLanguage: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#existsWithLanguage(java.lang.String,
	 *      lu.itrust.business.TS.Norm, lu.itrust.business.TS.Language)
	 */
	@Override
	public boolean existsWithLanguage(String reference, Norm norm, Language language) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#save(lu.itrust.business.TS.MeasureDescription)
	 */
	@Override
	public void save(MeasureDescription measureDescription) throws Exception {

		getSession().save(measureDescription);
	}

	/**
	 * saveAndUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#saveOrUpdate(lu.itrust.business.TS.MeasureDescription)
	 */
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception {

		getSession().saveOrUpdate(measureDescription);

	}

	/**
	 * remove: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#remove(lu.itrust.business.TS.MeasureDescription)
	 */
	@Override
	public void remove(MeasureDescription measureDescription) throws Exception {
		getSession().delete(measureDescription);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAll(lu.itrust.business.TS.Norm)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAll() throws Exception {
		Query query = getSession().createQuery("From MeasureDescription");
		return (List<MeasureDescription>) query.list();
	}

	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllByNorm(lu.itrust.business.TS.Norm)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByNorm(Norm norm) throws Exception {
		Query query = getSession().createQuery("From MeasureDescription where norm = :norm");
		query.setParameter("norm", norm);
		return (List<MeasureDescription>) query.list();
	}

	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllByNorm(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByNorm(Integer normid) throws Exception {
		Query query = getSession().createQuery("From MeasureDescription where norm.id = :normid");
		query.setParameter("normid", normid);
		return (List<MeasureDescription>) query.list();
	}
	
	/**
	 * getAllByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllByNorm(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByNorm(String label) throws Exception {
		Query query = getSession().createQuery("From MeasureDescription where norm.label = :normLabel");
		query.setParameter("normLabel", label);
		return (List<MeasureDescription>) query.list();
	}
}