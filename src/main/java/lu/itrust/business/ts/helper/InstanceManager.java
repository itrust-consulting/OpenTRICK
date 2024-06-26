/**
 * 
 */
package lu.itrust.business.ts.helper;

import java.io.File;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import lu.itrust.business.ts.component.DefaultTemplateLoader;
import lu.itrust.business.ts.database.service.ServiceEmailSender;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;

/**
 * @author eomar
 *
 */
public class InstanceManager {

	private static InstanceManager instance = null;

	@Autowired
	private ServiceStorage serviceStorage;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceEmailSender serviceEmailSender;

	@Autowired
	private DefaultTemplateLoader defaultTemplateLoader;

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

	public static ServiceEmailSender getServiceEmailSender() {
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

	public static File loadTemplate(Customer customer, TrickTemplateType type, Language language) {
		return getInstance().defaultTemplateLoader.loadFile(customer, type, language);
	}

	public static File loadTemplate(int customerId, TrickTemplateType type, Language language) {
		return getInstance().defaultTemplateLoader.loadFile(customerId, type, language);
	}

}
