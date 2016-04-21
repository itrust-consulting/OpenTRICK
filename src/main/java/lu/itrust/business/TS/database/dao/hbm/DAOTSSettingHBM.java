package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOTSSetting;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;

@Repository
public class DAOTSSettingHBM extends DAOHibernate implements DAOTSSetting {

	@Override
	public TSSetting get(TSSettingName name) {
		return (TSSetting) getSession().get(TSSetting.class, name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TSSetting> getAll() {
		return getSession().createQuery("From TSSetting").list();
	}

	@Override
	public void saveOrUpdate(TSSetting tsSetting) {
		getSession().saveOrUpdate(tsSetting);
	}

	@Override
	public TSSetting merge(TSSetting tsSetting) {
		return (TSSetting) getSession().merge(tsSetting);
	}

	@Override
	public void save(TSSetting tsSetting) {
		getSession().save(tsSetting);

	}

	@Override
	public void delete(TSSetting tsSetting) {
		getSession().delete(tsSetting);

	}

	@Override
	public void delete(String name) {
		getSession().createQuery("Delete TSSetting where name = :name").setString("name", name).executeUpdate();
	}

	@Override
	public boolean isAllowed(TSSettingName name) {
		try {
			if (!name.name().startsWith("SETTING_ALLOWED"))
				return false;
			Boolean allowed = (Boolean) getSession().createQuery("Select value = 'true' From TSSetting where name = :name").setParameter("name", name).uniqueResult();
			return allowed == null ? false : allowed;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return false;
		}
	}

}
