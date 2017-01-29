/**
 * 
 */
package lu.itrust.business.TS.database;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author eomar
 *
 */
public interface TemplateDAOService<T, ID extends Serializable> {
	boolean belongsToAnalysis(Integer analysisId, ID id);
	
	/**
	 * Returns the number of entities available.
	 * 
	 * @return the number of entities
	 */
	long count();
	
	/**
	 * Deletes the given entities.
	 * 
	 * @param entities
	 * @throws IllegalArgumentException
	 *             in case the given {@link Iterable} is (@literal null}.
	 */
	void delete(Collection<? extends T> entities);
	
	/**
	 * Deletes the entity with the given id.
	 * 
	 * @param id
	 *            must not be {@literal null}.
	 * @throws IllegalArgumentException
	 *             in case the given {@code id} is {@literal null}
	 */
	void delete(ID id);

	/**
	 * Deletes a given entity.
	 * 
	 * @param entity
	 * @throws IllegalArgumentException
	 *             in case the given entity is (@literal null}.
	 */
	void delete(T entity);
	 
	 

	/**
	 * Deletes all entities managed by the repository.
	 */
	void deleteAll();
	
	/**
	 * Returns whether an entity with the given id exists.
	 * 
	 * @param id
	 *            must not be {@literal null}.
	 * @return true if an entity with the given id exists, alse otherwise
	 * @throws IllegalArgumentException
	 *             if {@code id} is {@literal null}
	 */
	boolean exists(ID id);
	
	/**
	 * Returns all instances of the type.
	 * 
	 * @return all entities
	 */
	List<T> findAll();

	/**
	 * Returns all instances of the type with the given IDs.
	 * 
	 * @param ids
	 * @return
	 */
	List<T> findAll(List<ID> ids);

	List<T> findByAnalysisId(Integer idAnalysis);
	
	/**
	 * Retrives an entity by its id.
	 * 
	 * @param id
	 *            must not be {@literal null}.
	 * @return the entity with the given id or {@literal null} if none found
	 * @throws IllegalArgumentException
	 *             if {@code id} is {@literal null}
	 */
	T findOne(ID id);

	T findOne(ID id,Integer idAnalysis);

	T merge(T entity);

	/**
	 * Saves all given entities.
	 * 
	 * @param entities
	 * @return the saved entities
	 * @throws IllegalArgumentException
	 *             in case the given entity is (@literal null}.
	 */
	 List<ID> save(List<T> entities);

	/**
	 * Saves a given entity. Use the returned instance for further operations as
	 * the save operation might have changed the entity instance completely.
	 * 
	 * @param entity
	 * @return the saved entity
	 */
	ID save(T entity);

	void saveOrUpdate(List<T> entities);

	void saveOrUpdate(T entity);

}
