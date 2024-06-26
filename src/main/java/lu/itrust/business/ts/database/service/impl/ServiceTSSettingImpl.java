/**
 * 
 */
package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOTSSetting;
import lu.itrust.business.ts.database.service.ServiceTSSetting;
import lu.itrust.business.ts.model.general.TSSetting;
import lu.itrust.business.ts.model.general.TSSettingName;

/**
 * @author eomar
 *
 */
@Service
@Transactional(readOnly = true)
public class ServiceTSSettingImpl implements ServiceTSSetting {

	@Autowired
	private DAOTSSetting daotsSetting;

	@Override
	public TSSetting get(TSSettingName name) {
		return daotsSetting.get(name);
	}

	@Override
	public List<TSSetting> getAll() {
		return daotsSetting.getAll();
	}

	@Transactional
	@Override
	public void saveOrUpdate(TSSetting tsSetting) {
		daotsSetting.saveOrUpdate(tsSetting);
	}

	@Transactional
	@Override
	public TSSetting merge(TSSetting tsSetting) {
		return daotsSetting.merge(tsSetting);
	}

	@Transactional
	@Override
	public void save(TSSetting tsSetting) {
		 daotsSetting.save(tsSetting);
	}

	@Transactional
	@Override
	public void delete(TSSetting tsSetting) {
		daotsSetting.delete(tsSetting);
	}

	@Transactional
	@Override
	public void delete(String name) {
		daotsSetting.delete(name);
	}

	@Override
	public boolean isAllowed(TSSettingName name) {
		return  daotsSetting.isAllowed(name);
	}

	@Override
	public boolean isAllowed(TSSettingName name, boolean defaultValue) {
		return daotsSetting.isAllowed(name,defaultValue);
	}

}
