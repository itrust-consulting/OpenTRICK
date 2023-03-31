/**
 * 
 */
package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.general.helper.Notification;

/**
 * @author eomar
 *
 */
public interface ServiceMessageNotifier {
	
	void clear();
	
	void remove(String id);
	
	void remove(String id, String username);
	
	void notifyAll(Notification notification);
	
	void notifyUser(String username, Notification notification);
	
	List<Notification> findAllByUsername(String username);
	
	List<Notification> findAll();

	Notification findById(String id);

	void clear(String username);
}
