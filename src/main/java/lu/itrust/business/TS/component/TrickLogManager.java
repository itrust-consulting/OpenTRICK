/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.LinkedList;
import java.util.Queue;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.database.dao.hbm.DAOTrickLogHBM;
import lu.itrust.business.TS.model.general.TrickLog;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author eomar
 *
 */
public class TrickLogManager {
	
	private SessionFactory sessionFactory;

	private static volatile TrickLogManager instance;
	
	private Queue<TrickLog> trickLogs = new LinkedList<TrickLog>();
	
	private TrickLogManager() {
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory = sessionFactory;
	}
	
	public static TrickLogManager getInstance() {
		if(instance == null){
			synchronized (TrickLogManager.class) {
				if(instance == null)
					instance = new TrickLogManager();
			}
		}
		return instance;
	}
	
	public static boolean Persist(TrickLog trickLog){
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(trickLog);
		}
	}
	
	@Scheduled(initialDelay = 5000, fixedDelay = 5000)
	public void Persist(){
		synchronized (trickLogs) {
			Session session = null;
			try {
				session = sessionFactory.openSession();
				DAOTrickLog daoTrickLog = new DAOTrickLogHBM(session);
				session.beginTransaction();
				while(!trickLogs.isEmpty())
					daoTrickLog.saveOrUpdate(trickLogs.remove());
				session.getTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try {
					if(session!=null && session.isOpen())
						session.close();
				} catch (HibernateException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
