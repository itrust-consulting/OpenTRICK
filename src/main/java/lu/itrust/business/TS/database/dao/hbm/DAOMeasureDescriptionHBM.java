package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription;

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
public class DAOMeasureDescriptionHBM extends DAOHibernate implements lu.itrust.business.TS.database.dao.DAOMeasureDescription {

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
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#get(int)
	 */
	@Override
	public MeasureDescription get(Integer id) throws Exception {
		return (MeasureDescription) getSession().get(MeasureDescription.class, id);
	}

	/**
	 * getByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#getByReferenceAndStandard(java.lang.String,
	 *      lu.itrust.business.TS.data.standard.Standard)
	 */
	@Override
	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard) throws Exception {
		String query = "From MeasureDescription where standard = :standard and reference = :reference";
		return (MeasureDescription) getSession().createQuery(query).setParameter("standard", standard).setParameter("reference", reference).uniqueResult();
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard) throws Exception {
		String query = "Select count(*) From MeasureDescription where standard.id = :idStandard and reference = :reference";
		return (Long) getSession().createQuery(query).setParameter("idStandard", idStandard).setParameter("reference", reference).uniqueResult() >= 1;
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String,
	 *      lu.itrust.business.TS.data.standard.Standard)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard) throws Exception {
		return existsForMeasureByReferenceAndStandard(reference, standard.getId());
	}

	/**
	 * getAllMeasureDescriptions: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#getAllMeasureDescriptions()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAll() throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription").list();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#getAllByStandard(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByStandard(Integer idStandard) throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription where standard.id = :idStandard").setParameter("idStandard", idStandard).list();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#getAllByStandard(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByStandard(String label) throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("From MeasureDescription where standard.label = :label").setParameter("label", label).list();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#getAllByStandard(lu.itrust.business.TS.data.standard.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByStandard(Standard standard) throws Exception {
		return (List<MeasureDescription>) getSession().createQuery("FROM MeasureDescription mesDesc where standard.label = :label and standard.version = :version and standard.type = :type")
				.setParameter("label", standard.getLabel()).setParameter("version", standard.getVersion()).setParameter("type", standard.getType()).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#save(lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription)
	 */
	@Override
	public void save(MeasureDescription measureDescription) throws Exception {
		getSession().save(measureDescription);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#saveOrUpdate(lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription)
	 */
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription) throws Exception {
		getSession().saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasureDescription#delete(lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription)
	 */
	@Override
	public void delete(MeasureDescription measureDescription) throws Exception {
		getSession().delete(measureDescription);
	}

	@Override
	public void delete(int id) throws Exception {
		delete(get(id));

	}

	@Override
	public boolean existsForMeasureByReferenceAndAnalysisStandardId(String reference, int idAnalysisStandard) {
		return (boolean) getSession()
				.createQuery(
						"Select count(measure)>0 From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.id = :idAnalysisStandard and measure.measureDescription.reference = :reference ")
				.setInteger("idAnalysisStandard", idAnalysisStandard).setString("reference", reference).uniqueResult();
	}
}