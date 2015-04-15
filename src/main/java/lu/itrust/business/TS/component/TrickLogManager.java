/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.database.dao.hbm.DAOTrickLogHBM;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
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
	
	/**
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(String code, String message, List<String> parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(code, message, parameters));
		}
	}
	
	/**
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(String code, String message, String... parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(code, message, parameters));
		}
	}
	
	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, String code, String message, String... parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(level, code, message, parameters));
		}
	}
	
	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, String code, String message, List<String> parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(level, code, message, parameters));
		}
	}
	
	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogType type, String code, String message, List<String> parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(type, code, message, parameters));
		}
	}
	
	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogType type, String code, String message, String... parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(type, code, message, parameters));
		}
	}
	
	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, LogType type, String code, String message, List<String> parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(level, type, code, message, parameters));
		}
	}
	
	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, LogType type, String code, String message, String... parameters) {
		synchronized (getInstance().trickLogs) {
			return getInstance().trickLogs.offer(new TrickLog(level, type, code, message, parameters));
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
