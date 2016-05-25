package lu.itrust.business.TS.database.service.impl;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.database.dao.hbm.DAOExternalNotificationHBM;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.externalnotification.ExternalNotification;

import org.hibernate.Session;
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

	public ServiceExternalNotificationImpl() {
	}

	public ServiceExternalNotificationImpl(Session session) {
		this.daoExternalNotification = new DAOExternalNotificationHBM(session);
	}

	/** {@inheritDoc} */
	@Override
	public ExternalNotification get(Integer id) throws Exception {
		return daoExternalNotification.get(id); 
	}

	/** {@inheritDoc} */
	@Override
	public List<ExternalNotification> getAll() throws Exception {
		return daoExternalNotification.getAll();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification) throws Exception {
		daoExternalNotification.delete(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestampNow, String sourceUserName, double minimumProbability) throws Exception {
		return daoExternalNotification.computeProbabilitiesAtTime(timestampNow, sourceUserName, minimumProbability);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd, String sourceUserName, double minimumProbability) throws Exception {
		return daoExternalNotification.computeProbabilitiesInInterval(timestampBegin, timestampEnd, sourceUserName, minimumProbability);
	}
}
