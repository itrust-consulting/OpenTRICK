package lu.itrust.business.ts.database.service.impl;

import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.database.dao.DAOExternalNotification;
import lu.itrust.business.ts.database.dao.impl.DAOExternalNotificationImpl;
import lu.itrust.business.ts.database.service.ServiceExternalNotification;
import lu.itrust.business.ts.model.externalnotification.ExternalNotification;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Represents a default implementation of the ServiceExternalNotification
 * interface,
 * just passing method calls down to a DAOExternalNotification instance.
 * 
 * @author Steve Muller itrust consulting s.à r.l.
 * @since Jun 8, 2015
 */
@Service
public class ServiceExternalNotificationImpl implements ServiceExternalNotification {

	@Autowired
	private DAOExternalNotification daoExternalNotification;

	public ServiceExternalNotificationImpl() {
	}

	public ServiceExternalNotificationImpl(Session session) {
		this.daoExternalNotification = new DAOExternalNotificationImpl(session);
	}

	/** {@inheritDoc} */
	@Override
	public ExternalNotification get(Integer id) {
		return daoExternalNotification.get(id);
	}

	/** {@inheritDoc} */
	@Override
	public List<ExternalNotification> getAll() {
		return daoExternalNotification.getAll();
	}

	/** {@inheritDoc} */
	@Override
	public void save(ExternalNotification externalNotification) {
		daoExternalNotification.save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void saveOrUpdate(ExternalNotification externalNotification) {
		daoExternalNotification.save(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public void delete(ExternalNotification externalNotification) {
		daoExternalNotification.delete(externalNotification);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestampNow, String sourceUserName) {
		return daoExternalNotification.computeProbabilitiesAtTime(timestampNow, sourceUserName);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesInInterval(long timestampBegin, long timestampEnd,
			String sourceUserName, double minimumProbability) {
		return daoExternalNotification.computeProbabilitiesInInterval(timestampBegin, timestampEnd, sourceUserName,
				minimumProbability);
	}

	/** {@inheritDoc} */
	@Override
	public Double computeProbabilityAtTime(long timestamp, String prefix, String category) {
		return daoExternalNotification.computeProbabilityAtTime(timestamp, prefix, category);
	}

	/** {@inheritDoc} */
	@Override
	public Double findLastSeverity(String prefix, String category) {
		return daoExternalNotification.findLastSeverity(prefix, category);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> computeProbabilitiesAtTime(long timestamp, String sourceUserName,
			List<String> categories) {
		return daoExternalNotification.computeProbabilitiesAtTime(timestamp, sourceUserName, categories);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Double> findLastSeverities(String sourceUserName,
			List<String> categories) {
		return daoExternalNotification.findLastSeverities(sourceUserName, categories);
	}

	/** {@inheritDoc} */
	@Override
	public String[] extractPrefixAndCategory(String parameter, List<String> idsNames) {
		return daoExternalNotification.extractPrefixAndCategory(parameter, idsNames);
	}
}
