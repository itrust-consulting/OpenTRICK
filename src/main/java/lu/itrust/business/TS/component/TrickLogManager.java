/**
 * 
 */
package lu.itrust.business.TS.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.scheduling.annotation.Scheduled;

import lu.itrust.business.TS.database.dao.DAOTrickLog;
import lu.itrust.business.TS.database.dao.hbm.DAOTrickLogHBM;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.TrickLog;

/**
 * @author eomar
 *
 */
public class TrickLogManager {

	private SessionFactory sessionFactory;

	private static volatile TrickLogManager instance;

	private Queue<TrickLog> trickLogs = new LinkedList<TrickLog>();

	private Logger logger = Logger.getLogger("TRICKLogManager");

	private TrickLogManager() {
	}

	public static String GetLoggerName() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostName();
		} catch (UnknownHostException e) {
			return TrickLogManager.class.getName();
		}
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public static TrickLogManager getInstance() {
		if (instance == null) {
			synchronized (TrickLogManager.class) {
				if (instance == null)
					instance = new TrickLogManager();
			}
		}
		return instance;
	}

	public static boolean Persist(TrickLog trickLog) {
		return LogMe(trickLog);
	}

	protected static boolean LogMe(TrickLog trickLog) {
		try {
			synchronized (getInstance().trickLogs) {
				return getInstance().trickLogs.offer(trickLog);
			}
		} finally {
			switch (trickLog.getLevel()) {
			case ERROR:
				getInstance().logger.error(trickLog.toLog4J());
				break;
			case WARNING:
				getInstance().logger.warn(trickLog.toLog4J());
				break;
			case INFO:
			case SUCCESS:
				getInstance().logger.info(trickLog.toLog4J());
				break;
			}
		}
	}

	/**
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(String code, String message, String author, LogAction action, List<String> parameters) {
		return LogMe(new TrickLog(code, message, author, action, parameters));
	}

	/**
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(String code, String message, String author, LogAction action, String... parameters) {
		return LogMe(new TrickLog(code, message, author, action, parameters));

	}

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, String code, String message, String author, LogAction action, String... parameters) {
		return LogMe(new TrickLog(level, code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, String code, String message, String author, LogAction action, List<String> parameters) {
		return LogMe(new TrickLog(level, code, message, author, action, parameters));
	}

	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogType type, String code, String message, String author, LogAction action, List<String> parameters) {
		return LogMe(new TrickLog(type, code, message, author, action, parameters));

	}

	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogType type, String code, String message, String author, LogAction action, String... parameters) {
		return LogMe(new TrickLog(type, code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, LogType type, String code, String message, String author, LogAction action, List<String> parameters) {
		return LogMe(new TrickLog(level, type, code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean Persist(LogLevel level, LogType type, String code, String message, String author, LogAction action, String... parameters) {
		return LogMe(new TrickLog(level, type, code, message, author, action, parameters));
	}

	@Scheduled(initialDelay = 5000, fixedDelay = 5000)
	public void Persist() {
		if (trickLogs.isEmpty())
			return;
		synchronized (trickLogs) {
			Session session = null;
			try {
				if (trickLogs.isEmpty())
					return;
				session = sessionFactory.openSession();
				DAOTrickLog daoTrickLog = new DAOTrickLogHBM(session);
				session.beginTransaction();
				while (!trickLogs.isEmpty())
					daoTrickLog.saveOrUpdate(trickLogs.remove());
				session.getTransaction().commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (session != null && session.isOpen())
						session.close();
				} catch (HibernateException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static boolean Persist(Exception e) {
		ByteArrayOutputStream outStream = null;
		PrintStream printStream = null;
		try {
			if (e == null)
				return false;
			if (e instanceof TrickException)
				return Persist((TrickException) e);
			printStream = new PrintStream(outStream = new ByteArrayOutputStream());
			if(e.getCause()!=null)
				e.getCause().printStackTrace(printStream);
			e.printStackTrace(printStream);
			String stackTrace = outStream.toString();
			return Persist(LogLevel.ERROR, LogType.SYSTEM, "error.system.exception", String.format("Stack trace: %s", stackTrace), "TS logger", LogAction.RISE_EXCEPTION, stackTrace);
		} finally {
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e1) {
			}
			try {
				if (printStream != null)
					printStream.close();
			} catch (Exception e1) {
			}
			
			if(e!=null && instance.logger.isDebugEnabled())
				e.printStackTrace();
		}
	}

	public static boolean Persist(TrickException e) {
		if (e == null)
			return false;
		return Persist(LogLevel.ERROR, LogType.SYSTEM, e.getCode(), e.getMessage(), "TS logger", LogAction.RISE_EXCEPTION, e.getStringParameters());
	}
}
