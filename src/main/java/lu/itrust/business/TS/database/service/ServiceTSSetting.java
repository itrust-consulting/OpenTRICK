/**
 * 
 */
package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;

/**
 * @author eomar
 *
 */
public interface ServiceTSSetting {
	
	TSSetting get(TSSettingName name);

	List<TSSetting> getAll();

	boolean isAllowed(TSSettingName name);
	
	void saveOrUpdate(TSSetting tsSetting);

	TSSetting merge(TSSetting tsSetting);

	void save(TSSetting tsSetting);

	void delete(TSSetting tsSetting);

	void delete(String name);

	
}
