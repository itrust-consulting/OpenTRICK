/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.AssetMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.MeasureProperties;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerSOAExport extends WorkerImpl {

	public static String FR_TEMPLATE;

	public static String ENG_TEMPLATE;

	private String username;

	private String rootPath;

	private Integer idAnalysis;

	private DAOUser daoUser;

	private Locale locale;
	
	private DateFormat format = null;

	private DAOAnalysis daoAnalysis;

	private DAOWordReport daoWordReport;

	private MessageSource messageSource;

	private ServiceTaskFeedback serviceTaskFeedback;

	public WorkerSOAExport(String username, String rootPath, Integer idAnalysis, MessageSource messageSource, ServiceTaskFeedback serviceTaskFeedback,
			WorkersPoolManager poolManager, SessionFactory sessionFactory) {
		super(poolManager, sessionFactory);
		this.username = username;
		this.rootPath = rootPath;
		this.idAnalysis = idAnalysis;
		this.messageSource = messageSource;
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	private void initialiseDAO(Session session) {
		daoUser = new DAOUserHBM(session);
		daoAnalysis = new DAOAnalysisHBM(session);
		daoWordReport = new DAOWordReportHBM(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						Thread.currentThread().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
			TrickLogManager.Persist(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getPoolManager() != null && !getPoolManager().exist(getId()))
					if (!getPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
			}
			session = getSessionFactory().openSession();
			initialiseDAO(session);
			session.beginTransaction();
			long reportId = processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.export.soa", "SOA has been successfully exported", 100);
			messageHandler.setAsyncCallback(new AsyncCallback("downloadWordReport('" + reportId + "');"));
			serviceTaskFeedback.send(getId(), messageHandler);
		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.beginTransaction().getStatus().canRollback())
						session.beginTransaction().rollback();
				} catch (Exception e1) {
				}
			}
			MessageHandler messageHandler = null;
			if (e instanceof TrickException)
				messageHandler = new MessageHandler(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), e);
			else
				messageHandler = new MessageHandler("error.internal", "Internal error", e);
			serviceTaskFeedback.send(getId(), messageHandler);
			TrickLogManager.Persist(e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getPoolManager().remove(this);
		}

	}

	private long processing() throws InvalidFormatException, IOException {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		File workFile = null;
		XWPFDocument document = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			// progress, max, size, index
			int[] progressing = { 2, 95, 0, 0 };
			locale = new Locale(analysis.getLanguage().getAlpha2().toLowerCase());
			format = locale.getLanguage().equals("fr") ? new SimpleDateFormat("dd-MM-yyyy") : new SimpleDateFormat("MM-dd-yyyy");
			serviceTaskFeedback.send(getId(), new MessageHandler("info.loading.soa.template", "Loading soa sheet template", progressing[0] += 3));
			workFile = new File(String.format("%s/tmp/SOA_%d_%s_V%s.docm", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/%s.dotm", rootPath, locale.getLanguage().equals("fr") ? FR_TEMPLATE : ENG_TEMPLATE));
			OPCPackage opcPackage = OPCPackage.open(doctemplate.getAbsoluteFile());
			opcPackage.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
			opcPackage.save(workFile);
			document = new XWPFDocument(inputStream = new FileInputStream(workFile));
			serviceTaskFeedback.send(getId(), new MessageHandler("info.preparing.soa.data", "Preparing soa sheet template", progressing[0] += 5));
			List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards().stream().filter(AnalysisStandard::isSoaEnabled).collect(Collectors.toList());
			MessageHandler handler = new MessageHandler("info.printing.soa.data", "Printing soa data", progressing[0] += 1);
			serviceTaskFeedback.send(getId(), handler);
			progressing[2] = analysisStandards.stream().mapToInt(analysisStandard -> analysisStandard.getMeasures().size()).sum();
			for (AnalysisStandard analysisStandard : analysisStandards) {
				XWPFParagraph paragraph = progressing[3] == 0 ? document.getParagraphArray(0) : document.createParagraph();
				paragraph.createRun().setText(analysisStandard.getStandard().getLabel());
				paragraph.setStyle("Heading1");
				generateTable(analysisStandard.getMeasures(), document, handler, progressing);
			}
			serviceTaskFeedback.send(getId(), new MessageHandler("info.saving.soa", "Saving soa", 95));
			document.write(outputStream = new FileOutputStream(workFile));
			outputStream.flush();
			WordReport report = WordReport.BuildSOA(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, workFile.getName(), workFile.length(),
					FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			daoAnalysis.saveOrUpdate(analysis);
			return report.getId();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (Exception e) {
				}
			}

			if (outputStream != null)
				outputStream.close();

			if (workFile != null && workFile.exists() && !workFile.delete())
				workFile.deleteOnExit();
		}

	}

	/**
	 * 
	 * @param measures
	 * @param document
	 * @param handler
	 * @param progressing
	 *            [progress, max, size, index]
	 */
	private void generateTable(List<Measure> measures, XWPFDocument document, MessageHandler handler, int[] progressing) {
		int rowIndex = 0;
		XWPFTable table = document.createTable(measures.size(), 5);
		table.setStyleID("TSSOA");
		XWPFTableRow row = getRow(table, rowIndex++);
		getCell(row, 0).setText(messageSource.getMessage("report.measure.reference", null, "Ref.", locale));
		getCell(row, 1).setText(messageSource.getMessage("report.measure.domain", null, "Domain", locale));
		getCell(row, 2).setText(messageSource.getMessage("report.measure.due.date", null, "Due date", locale));
		getCell(row, 3).setText(messageSource.getMessage("report.soa.justification", null, "Justification", locale));
		getCell(row, 4).setText(messageSource.getMessage("report.soa.reference", null, "Reference", locale));
		for (Measure measure : measures) {
			row = getRow(table, rowIndex++);
			getCell(row, 0).setText(measure.getMeasureDescription().getReference());
			getCell(row, 1).setText(measure.getMeasureDescription().getMeasureDescriptionTextByAlpha2(locale.getLanguage()).getDomain());
			if (measure.getMeasureDescription().isComputable()) {
				getCell(row, 2).setText(format.format(measure.getPhase().getEndDate()));
				MeasureProperties properties = measure instanceof NormalMeasure ? ((NormalMeasure) measure).getMeasurePropertyList()
						: measure instanceof AssetMeasure ? ((AssetMeasure) measure).getMeasurePropertyList() : null;
				if (properties != null) {
					addCellContent(getCell(row, 3), properties.getSoaComment());
					addCellContent(getCell(row, 4), properties.getSoaReference());
				}
			}
			handler.setProgress((int) (progressing[0] + (++progressing[3] / (double) progressing[2]) * (progressing[1] - progressing[0])));
		}

	}

	private XWPFTableRow getRow(XWPFTable table, int rowIndex) {
		XWPFTableRow row = table.getRow(rowIndex);
		if (row == null)
			row = table.createRow();
		return row;
	}

	private XWPFTableCell getCell(XWPFTableRow row, int index) {
		XWPFTableCell cell = row.getCell(index);
		return cell == null ? row.createCell() : cell;
	}

	private void addCellContent(XWPFTableCell cell, String content) {
		if (content == null || content.isEmpty())
			return;
		String[] texts = content.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			XWPFParagraph paragraph = cell.getParagraphs().size() > i ? cell.getParagraphs().get(i) : cell.addParagraph();
			paragraph.setStyle("BodyOfText");
			paragraph.createRun().setText(texts[i]);
		}
	}

}
