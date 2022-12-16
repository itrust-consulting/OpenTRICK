/**
 * 
 */
package lu.itrust.business.TS.helper;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.database.service.ServiceEmailSender;
import lu.itrust.business.TS.database.service.ServiceStorage;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;

/**
 * @author eomar
 *
 */
public class InstanceManager {

	private volatile static InstanceManager instance = null;

	@Autowired
	private ServiceStorage serviceStorage;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceEmailSender serviceEmailSender;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private MessageSource messageSource;

	

	private InstanceManager() {
		setInstance(this);
	}

	private static final synchronized void setInstance(InstanceManager instance) {
		if (InstanceManager.instance == null)
			InstanceManager.instance = instance;
	}

	public static final InstanceManager getInstance() {
		InstanceManager manager = instance;
		if (manager == null) {
			synchronized (InstanceManager.class) {
				manager = instance;
				if (manager == null) {
					manager = new InstanceManager();
				}
			}
		}
		return manager;
	}

	public static ServiceEmailSender getServiceEmailSender(){
		return getInstance().serviceEmailSender;
	}

	public static ServiceStorage getServiceStorage() {
		return getInstance().serviceStorage;
	}

	public static ServiceTaskFeedback getServiceTaskFeedback() {
		return getInstance().serviceTaskFeedback;
	}

	public static WorkersPoolManager getWorkersPoolManager() {
		return getInstance().workersPoolManager;
	}

	public static SessionFactory getSessionFactory() {
		return getInstance().sessionFactory;
	}

	public static MessageSource getMessageSource() {
		return getInstance().messageSource;
	}

}
