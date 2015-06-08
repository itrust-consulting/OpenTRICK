package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Represents a default implementation of the ServiceExternalNotification interface,
 * just passing method calls down to a DAOExternalNotification instance.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
@Service
public class ServiceExternalNotificationImpl implements ServiceExternalNotification {

	@Autowired
	private DAOExternalNotification daoExternalNotification;

	@Override
	public ExternalNotification get(Integer id) throws Exception {
		return daoExternalNotification.get(id); 
	}

	@Override
	public List<ExternalNotification> getAll() throws Exception {
		return daoExternalNotification.getAll();
	}

	@Override
	public void save(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.save(externalNotification);
	}

	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.save(externalNotification);
	}

	@Override
	public void delete(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.delete(externalNotification);
	}

}
