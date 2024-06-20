/**
 * 
 */
package lu.itrust.business.ts.component;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOUserSqLite;
import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.helper.CleanerDate;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.document.impl.UserSQLite;
import lu.itrust.business.ts.model.general.document.impl.WordReport;

/**
 * @author eomar
 *
 */
@Transactional
public class DataCleaner {

	private CleanerDate sqliteDate;

	private CleanerDate reportDate;

	@Autowired
	private DAOUserSqLite daoUserSqLite;

	@Autowired
	private DAOWordReport daoWordReport;

	@Value("${app.settings.cleaner.data.max.size}")
	private int pageMaxSize;

	public CleanerDate getSqliteDate() {
		return sqliteDate;
	}

	protected void setSqliteDate(CleanerDate sqliteDate) {
		this.sqliteDate = sqliteDate;
	}

	public CleanerDate getReportDate() {
		return reportDate;
	}

	protected void setReportDate(CleanerDate reportDate) {
		this.reportDate = reportDate;
	}

	@Value("${app.settings.cleaner.data.report.max.old}")
	public void setReportDate(String date) {
		setReportDate(CleanerDate.parse(date));
	}

	@Value("${app.settings.cleaner.data.sqlite.max.old}")
	public void setSqliteDate(String date) {
		setSqliteDate(CleanerDate.parse(date));
	}

	
	@Scheduled(cron = "${app.settings.cleaner.cron.setup}")
	public synchronized void cleanUp() {
		final int maxSize = (int) ((double) pageMaxSize / 2.0), pageSize = 30;
		cleanUpSQLITE(pageSize, maxSize);
		cleanUpReport(pageSize, maxSize);
	}

	private void cleanUpReport(int pageSize, int maxSize) {
		if (reportDate == null || reportDate.isEmpty())
			return;
		final Date deleteDate = reportDate.getDate();
		final int pageCount = Math.max(
				(int) Math.ceil(
						(double) Math.min(daoWordReport.countByCreatedBefore(deleteDate), maxSize) / (double) pageSize),
				1);
		for (int i = 1; i <= pageCount; i++) {
			List<WordReport> reports = daoWordReport.findByCreatedBefore(deleteDate, i, pageSize);
			for (WordReport report : reports) {
				daoWordReport.delete(report);
				TrickLogManager.Persist(LogLevel.INFO, LogType.SYSTEM, "log.system.data.cleaner.report",
						String.format("Type: report, analysis: %s,name: %s, version: %s, exported: %s, owner: %s",
								report.getIdentifier(), report.getLabel(), report.getVersion(),
								report.getCreated(), report.getUser().getLogin()),
						"Data cleaner", LogAction.DELETE, report.getIdentifier(), report.getLabel(),
						report.getVersion(), String.valueOf(report.getCreated()),
						report.getUser().getLogin());
			}
		}

	}

	private void cleanUpSQLITE(int pageSize, int maxSize) {
		if (sqliteDate == null || sqliteDate.isEmpty())
			return;
		final Date deleteDate = sqliteDate.getDate();
		final int pageCount = Math.max(
				(int) Math.ceil(
						(double) Math.min(daoUserSqLite.countByCreatedBefore(deleteDate), maxSize) / (double) pageSize),
				1);
		for (int i = 1; i <= pageCount; i++) {
			List<UserSQLite> sqLites = daoUserSqLite.findByCreatedBefore(deleteDate, i, pageSize);
			for (UserSQLite sqlite : sqLites) {
				daoUserSqLite.delete(sqlite);
				TrickLogManager.Persist(LogLevel.INFO, LogType.SYSTEM, "log.system.data.cleaner.sqlite",
						String.format("Type: database, analysis: %s,name: %s, version: %s, exported: %s, owner: %s",
								sqlite.getIdentifier(), sqlite.getLabel(), sqlite.getVersion(),
								sqlite.getCreated(), sqlite.getUser().getLogin()),
						"Data cleaner", LogAction.DELETE, sqlite.getIdentifier(), sqlite.getLabel(),
						sqlite.getVersion(), String.valueOf(sqlite.getCreated()),
						sqlite.getUser().getLogin());
			}
		}
	}

}
