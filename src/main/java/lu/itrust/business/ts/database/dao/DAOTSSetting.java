/**
 * 
 */
package lu.itrust.business.ts.database.dao;

import java.util.List;

import lu.itrust.business.ts.model.general.TSSetting;
import lu.itrust.business.ts.model.general.TSSettingName;

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
