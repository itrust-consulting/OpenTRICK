/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;

/**
 * @author oensuifudine
 * 
 */
public interface ServiceNorm {
	public Norm getNormByID(int idNorm) throws Exception;

	public Norm loadNotCustomNormByName(String norm) throws Exception;
	
	public Norm loadSingleNormByName(String norm) throws Exception;
	
	public Norm loadSingleNormByNameAndVersion(String label, int version) throws Exception; 
	
	public List<Norm> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<Norm> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception;
	
	public List<Norm> loadAll() throws Exception;
	
	public void save(Norm Norm) throws Exception;
	
	public void saveOrUpdate(Norm Norm) throws Exception;
	
	public void remove(Norm Norm)throws Exception;

	public boolean exists(String label, int version);


}
