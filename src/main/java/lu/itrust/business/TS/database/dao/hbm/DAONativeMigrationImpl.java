/**
 * 
 */
package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAONativeMigration;
import lu.itrust.business.TS.model.migration.AssessmentMigration;

/**
 * @author eomar
 *
 */
@Repository
public class DAONativeMigrationImpl extends DAOHibernate implements DAONativeMigration {

	/**
	 * 
	 */
	public DAONativeMigrationImpl() {
	}

	/**
	 * @param session
	 */
	public DAONativeMigrationImpl(Session session) {
		super(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAONativeMigration#hasImpact()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasImpact() {
		Map<String, Object> results = (Map<String, Object>) getSession().createSQLQuery("Select * From Assessment limit 1")
				.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).uniqueResult();
		return results == null ? false : results.keySet().stream().anyMatch(key -> key.startsWith("dtImpact"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAONativeMigration#
	 * findAllAssessmentByAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AssessmentMigration> findAllAssessmentByAnalysisId(int idAnalysis) {
		return (List<AssessmentMigration>) getSession().createSQLQuery("Select * From Assessment where Assessment.fiAnalysis = :idAnalysis").setInteger("idAnalysis", idAnalysis)
				.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list().parallelStream().map(data -> new AssessmentMigration((Map<String, Object>) data))
				.collect(Collectors.toList());
	}

}
