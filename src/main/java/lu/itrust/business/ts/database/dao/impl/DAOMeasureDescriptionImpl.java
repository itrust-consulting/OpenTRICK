package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;

/**
 * DAOMeasureDescription.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.r.l. :
 * @version
 * @since Feb 1, 2013
 */
@Repository
public class DAOMeasureDescriptionImpl extends DAOHibernate implements lu.itrust.business.ts.database.dao.DAOMeasureDescription {

	/**
	 * Constructor: <br>
	 */
	public DAOMeasureDescriptionImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOMeasureDescriptionImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#get(int)
	 */
	@Override
	public MeasureDescription get(Integer id) {
		return (MeasureDescription) getSession().get(MeasureDescription.class, id);
	}

	/**
	 * getByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#getByReferenceAndStandard(java.lang.String,
	 *      lu.itrust.business.ts.model.standard.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public MeasureDescription getByReferenceAndStandard(String reference, Standard standard) {
		String query = "From MeasureDescription where standard = :standard and reference = :reference";
		return (MeasureDescription) createQueryWithCache(query).setParameter("standard", standard).setParameter("reference", reference).uniqueResultOptional().orElse(null);
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String,
	 *      java.lang.Integer)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Integer idStandard) {
		String query = "Select count(*)>0 From MeasureDescription where standard.id = :idStandard and reference = :reference";
		return (boolean) createQueryWithCache(query).setParameter("idStandard", idStandard).setParameter("reference", reference).getSingleResult();
	}

	/**
	 * existsForMeasureByReferenceAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#existsForMeasureByReferenceAndStandard(java.lang.String,
	 *      lu.itrust.business.ts.model.standard.Standard)
	 */
	@Override
	public boolean existsForMeasureByReferenceAndStandard(String reference, Standard standard) {
		return existsForMeasureByReferenceAndStandard(reference, standard.getId());
	}

	/**
	 * getAllMeasureDescriptions: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#getAllMeasureDescriptions()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAll() {
		return (List<MeasureDescription>) createQueryWithCache("From MeasureDescription").getResultList();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#getAllByStandard(java.lang.Integer)
	 */
	@Override
	public List<MeasureDescription> getAllByStandard(Integer idStandard) {
		return  createQueryWithCache("From MeasureDescription where standard.id = :idStandard", MeasureDescription.class).setParameter("idStandard", idStandard)
				.getResultList();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#getAllByStandard(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByStandard(String label) {
		return (List<MeasureDescription>) createQueryWithCache("From MeasureDescription where standard.label = :label").setParameter("label", label).getResultList();
	}

	/**
	 * getAllByStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#getAllByStandard(lu.itrust.business.ts.model.standard.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MeasureDescription> getAllByStandard(Standard standard) {
		return (List<MeasureDescription>) getSession()
				.createQuery("FROM MeasureDescription mesDesc where standard.label = :label and standard.version = :version and standard.type = :type")
				.setParameter("label", standard.getLabel()).setParameter("version", standard.getVersion()).setParameter("type", standard.getType()).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#save(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription)
	 */
	@Override
	public void save(MeasureDescription measureDescription) {
		getSession().save(measureDescription);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#saveOrUpdate(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription)
	 */
	@Override
	public void saveOrUpdate(MeasureDescription measureDescription) {
		getSession().saveOrUpdate(measureDescription);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOMeasureDescription#delete(lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription)
	 */
	@Override
	public void delete(MeasureDescription measureDescription) {
		getSession().delete(measureDescription);
	}

	@Override
	public void delete(int id) {
		delete(get(id));

	}

	@Override
	public boolean existsForMeasureByReferenceAndAnalysisStandardId(String reference, int idAnalysisStandard) {
		return (boolean) createQueryWithCache(
				"Select count(measure)>0 From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.id = :idAnalysisStandard and measure.measureDescription.reference = :reference ")
				.setParameter("idAnalysisStandard", idAnalysisStandard).setParameter("reference", reference).getSingleResult();
	}

	@Override
	public boolean exists(int idMeasureDescription, int idStandard) {
		return (boolean) createQueryWithCache("Select count(*) > 0 From MeasureDescription where id = :idMeasureDescription and standard.id = :idStandard")
				.setParameter("idMeasureDescription", idMeasureDescription).setParameter("idStandard", idStandard).getSingleResult();
	}

	@Override
	public boolean isUsed(MeasureDescription measureDescription) {
		return createQueryWithCache("Select count(*) > 0 From Measure where measureDescription  = :measureDescription", Boolean.class)
				.setParameter("measureDescription", measureDescription).uniqueResult();
	}
}