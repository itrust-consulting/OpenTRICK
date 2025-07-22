/**
 * 
 */
package lu.itrust.business.ts.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import lu.itrust.business.ts.database.dao.DAOTrickLog;
import lu.itrust.business.ts.database.dao.impl.DAOTrickLogImpl;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.CleanerDate;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.TrickLog;

/**
 * @author eomar
 *
 */
public final class TrickLogManager {

	private static volatile TrickLogManager instance;

	private Queue<TrickLog> trickLogs = new LinkedList<>();

	private static final Object LOCK = new Object();

	private Logger logger = LogManager.getLogger(TrickLogManager.class.getSimpleName());

	/**
	 * Retention time for logs, in days.
	 * This is used to determine how long logs should be kept before being deleted.
	 */
	@Value("${app.log.retention.time:2y}")
	private String retentionTime;

	private TrickLogManager() {
	}

	public static String getLoggerName() {
		try {
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostName();
		} catch (UnknownHostException e) {
			return TrickLogManager.class.getName();
		}
	}

	public static TrickLogManager getInstance() {
		if (TrickLogManager.instance == null) {
			synchronized (LOCK) {
				if (TrickLogManager.instance == null)
					TrickLogManager.instance = new TrickLogManager();
			}
		}
		return TrickLogManager.instance;
	}

	public static boolean persist(TrickLog trickLog) {
		return logMe(trickLog);
	}

	protected static boolean logMe(TrickLog trickLog) {
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
				case INFO, SUCCESS:
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
	public static boolean persist(String code, String message, String author, LogAction action,
			List<String> parameters) {
		return logMe(new TrickLog(code, message, author, action, parameters));
	}

	/**
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(String code, String message, String author, LogAction action, String... parameters) {
		return logMe(new TrickLog(code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(LogLevel level, String code, String message, String author, LogAction action,
			String... parameters) {
		return logMe(new TrickLog(level, code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(LogLevel level, String code, String message, String author, LogAction action,
			List<String> parameters) {
		return logMe(new TrickLog(level, code, message, author, action, parameters));
	}

	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(LogType type, String code, String message, String author, LogAction action,
			List<String> parameters) {
		return logMe(new TrickLog(type, code, message, author, action, parameters));
	}

	/**
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(LogType type, String code, String message, String author, LogAction action,
			String... parameters) {
		return logMe(new TrickLog(type, code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(LogLevel level, LogType type, String code, String message, String author,
			LogAction action, List<String> parameters) {
		return logMe(new TrickLog(level, type, code, message, author, action, parameters));
	}

	/**
	 * @param level
	 * @param type
	 * @param code
	 * @param message
	 * @param parameters
	 */
	public static boolean persist(LogLevel level, LogType type, String code, String message, String author,
			LogAction action, String... parameters) {
		return logMe(new TrickLog(level, type, code, message, author, action, parameters));
	}

	@Scheduled(initialDelay = 5000, fixedDelay = 5000)
	public void persist() {
		if (trickLogs.isEmpty())
			return;
		synchronized (trickLogs) {
			Session session = null;
			try {
				if (trickLogs.isEmpty())
					return;
				session = InstanceManager.getSessionFactory().openSession();
				DAOTrickLog daoTrickLog = new DAOTrickLogImpl(session);
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

	@Scheduled(cron = "${app.log.retention.cron:0 0 0 * * ?}")
	public void applyLogRetention() {
		Session session = null;
		try {
			var logRetention = CleanerDate.parse(retentionTime);
			if (logRetention.isEmpty()) {
				logger.warn("Log retention time is empty, skipping log retention.");
				return;
			}
			var maxLogDate = logRetention.getDate();
			logger.info(String.format("Applying log retention, removing logs older than %s", maxLogDate));
			session = InstanceManager.getSessionFactory().openSession();
			DAOTrickLog daoTrickLog = new DAOTrickLogImpl(session);
			session.beginTransaction();
			var count = daoTrickLog.countByDateBefore(maxLogDate);
			if (count == 0) {
				logger.info("No logs to delete, log retention is up to date.");

			} else if (count > 10000) {
				logger.warn(String.format(
						"Log retention will delete %d logs, this is a large number of logs to delete at once.", count));
				daoTrickLog.deleteByDateBefore(maxLogDate, 1, 10000);
			} else {
				logger.info(String.format("Log retention will delete %d logs.", count));
				daoTrickLog.deleteByDateBefore(maxLogDate);

			}
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

	public static boolean persist(Exception e) {
		ByteArrayOutputStream outStream = null;
		PrintStream printStream = null;
		try {
			if (e == null)
				return false;
			if (e instanceof TrickException e1)
				return persist(e1);
			printStream = new PrintStream(outStream = new ByteArrayOutputStream());
			if (e.getCause() != null)
				e.getCause().printStackTrace(printStream);
			e.printStackTrace(printStream);
			String stackTrace = outStream.toString();
			return persist(LogLevel.ERROR, LogType.SYSTEM, "error.system.exception",
					String.format("Stack trace: %s", stackTrace), "TS logger", LogAction.RISE_EXCEPTION,
					stackTrace);
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

			if (e != null && getInstance().logger.isDebugEnabled())
				e.printStackTrace();
		}
	}

	protected static boolean persist(TrickException e) {
		return persist(LogLevel.ERROR, LogType.SYSTEM, e.getCode(), e.getMessage(), "TS logger",
				LogAction.RISE_EXCEPTION, e.getStringParameters());
	}
}
