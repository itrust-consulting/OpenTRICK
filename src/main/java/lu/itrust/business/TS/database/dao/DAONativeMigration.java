/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.migration.AssessmentMigration;

/**
 * @author eomar
 *
 */
public interface DAONativeMigration {
	
	boolean hasImpact();
	
	List<AssessmentMigration> findAllAssessmentByAnalysisId(int idAnalysis);
}
