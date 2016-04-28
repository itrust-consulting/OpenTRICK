/**
 * 
 */
package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;

/**
 * @author eomar
 *
 */
public interface DAOTSSetting {

	TSSetting get(TSSettingName name);
	
	List<TSSetting> getAll();
	
	void saveOrUpdate(TSSetting tsSetting);
	
	TSSetting merge(TSSetting tsSetting);
	
	void save(TSSetting tsSetting);
	
	void delete(TSSetting tsSetting);
	
	void delete(String name);

	boolean isAllowed(TSSettingName name);

	boolean isAllowed(TSSettingName name, boolean defaultValue);
}
