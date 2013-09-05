package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Norm;

/** 
 * DAONorm.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAONorm {
	
	public Norm getNormByID(int idNorm) throws Exception;
		
	public Norm loadNotCustomNormByName(String norm) throws Exception;
	
	public Norm loadSingleNormByName(String norm) throws Exception;
	
	public List<Norm> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<Norm> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, String version, String creationDate) throws Exception;
	
	public List<Norm> loadAll() throws Exception;
	
	public void save(Norm Norm) throws Exception;
	
	public void saveOrUpdate(Norm Norm) throws Exception;
	
	public void remove(Norm Norm)throws Exception;

}
