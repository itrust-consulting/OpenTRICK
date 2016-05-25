/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import java.util.List;

/**
 * @author eomar
 *
 */
public interface ProxyAssetScenario {

	int getId();

	String getName();

	/**
	 * List< Object[3] >
	 * <ul>
	 * <li>[0]: field name</li>
	 * <li>[1]: field value</li>
	 * <li>[2]: fieldName code</li>
	 * </ul>
	 * 
	 * @return
	 */
	List<Object[]> getAllFields();

	Object [] getField(String fieldName);
	
	Object get();
}
