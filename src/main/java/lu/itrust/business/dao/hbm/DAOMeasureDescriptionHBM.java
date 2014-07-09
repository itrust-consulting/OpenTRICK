package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.Norm;

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
	 * Constructor: <br>
	 */
	public DAOMeasureDescriptionHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
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
	public MeasureDescription get(Integer id) throws Exception {
		return (MeasureDescription) getSession().get(MeasureDescription.class, id);
	}

	/**
	 * getByReferenceAndNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getByReferenceAndNorm(java.lang.String,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public MeasureDescription getByReferenceAndNorm(String reference, Norm norm) throws Exception {
		String query = "From MeasureDescription where norm = :norm and reference = :reference";
		return (MeasureDescription) getSession().createQuery(query).setParameter("norm", norm).setParameter("reference", reference).uniqueResult();
	}

	/**
	 * existsForMeasureByReferenceAndNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#existsForMeasureByReferenceAndNorm(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndNorm(String reference, Integer idNorm) throws Exception {
		String query = "Select count(*) From MeasureDescription where norm.id = :idNorm and reference = :reference";
		return (Long) getSession().createQuery(query).setParameter("idNorm", idNorm).setParameter("reference", reference).uniqueResult() >= 1;
	}

	/**
	 * existsForMeasureByReferenceAndNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#existsForMeasureByReferenceAndNorm(java.lang.String,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndNorm(String reference, Norm norm) throws Exception {
		return existsForMeasureByReferenceAndNorm(reference, norm.getId());
	}

	/**
	 * getAllMeasureDescriptions: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllMeasureDescriptions()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAll() throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription").list();
	}

	/**
	 * getAllMeasureDescriptionsByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllMeasureDescriptionsByNorm(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByNorm(Integer normid) throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription where norm.id = :normid").setParameter("normid", normid).list();
	}

	/**
	 * getAllMeasureDescriptionsByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllMeasureDescriptionsByNorm(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByNorm(String label) throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription where norm.label = :normLabel").setParameter("normLabel", label).list();
	}

	/**
	 * getAllMeasureDescriptionsByNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#getAllMeasureDescriptionsByNorm(lu.itrust.business.TS.Norm)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByNorm(Norm norm) throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription where norm = :norm").setParameter("norm", norm).list();
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
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#saveOrUpdate(lu.itrust.business.TS.MeasureDescription)
	 */
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception {
		getSession().saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasureDescription#delete(lu.itrust.business.TS.MeasureDescription)
	 */
	@Override
	public void delete(MeasureDescription measureDescription) throws Exception {
		getSession().delete(measureDescription);
	}
}