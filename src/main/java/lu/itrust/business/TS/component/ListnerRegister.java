/**
 * 
 */
package lu.itrust.business.TS.component;

import javax.annotation.PostConstruct;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.itrust.business.TS.usermanagement.listner.CredentialEncryptListner;
import lu.itrust.business.TS.usermanagement.listner.UserEncryptListner;

/**
 * @author eomar
 *
 */
@Component
public class ListnerRegister {

	@Autowired
	private SessionFactory sessionFactory;
	
	@PostConstruct
	public void registerListner() {
		
		final EventListenerRegistry eventListenerRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
		
		final UserEncryptListner userEncryptListner = new UserEncryptListner();
		final CredentialEncryptListner credentialEncryptListner = new CredentialEncryptListner();
		
		eventListenerRegistry.getEventListenerGroup(EventType.POST_LOAD).appendListener(userEncryptListner);
		eventListenerRegistry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(userEncryptListner);
		eventListenerRegistry.getEventListenerGroup(EventType.PRE_COLLECTION_UPDATE).appendListener(userEncryptListner);
		eventListenerRegistry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(userEncryptListner);
		
		eventListenerRegistry.getEventListenerGroup(EventType.POST_LOAD).appendListener(credentialEncryptListner);
		eventListenerRegistry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(credentialEncryptListner);
		eventListenerRegistry.getEventListenerGroup(EventType.PRE_COLLECTION_UPDATE).appendListener(credentialEncryptListner);
		eventListenerRegistry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(credentialEncryptListner);
		
	}

}
