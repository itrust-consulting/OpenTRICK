/**
 * 
 */
package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Parameter;

/**
 * @author eom
 * 
 */
public interface ServiceParameter {

	Parameter get(int id);

	List<Parameter> findAll();

	List<Parameter> findAll(int pageIndex, int pageSize);

	List<Parameter> findByAnalysis(int idAnalysis, int pageIndex, int pageSize);

	Parameter save(Parameter parameter);

	void saveOrUpdate(Parameter parameter);

	Parameter merge(Parameter parameter);

	void delete(Parameter parameter);

	void delete(int id);

}
