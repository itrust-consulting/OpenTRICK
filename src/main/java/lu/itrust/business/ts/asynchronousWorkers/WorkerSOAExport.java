/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl.mergeCell;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.formatting.Docx4jFormatter.updateRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter.sum;
import static lu.itrust.business.ts.helper.InstanceManager.loadTemplate;

import java.io.File;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Document;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.hibernate.Session;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.ExportFileName;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.usermanagement.User;

/**
 * This class represents a worker for exporting SOA (Statement of Analysis) data.
 * It extends the `WorkerImpl` class and implements the `Worker` interface.
 * The `WorkerSOAExport` class is responsible for exporting SOA data to a Word document.
 * It performs the export operation asynchronously.
 * 
 * The class contains methods for starting, canceling, and running the export process.
 * It also includes helper methods for initializing DAOs, processing data, and generating tables.
 * 
 * The exported SOA data includes measures, their references, domains, statuses, due dates, justifications, and references.
 * The data is retrieved from the database and populated into a Word document template.
 * The template is then saved as a Word document and stored in the database.
 * 
 * The `WorkerSOAExport` class is designed to be used in a multi-threaded environment, where multiple export operations can be performed concurrently.
 * Each instance of the class represents a single export operation.
 * 
 * Usage:
 * WorkerSOAExport worker = new WorkerSOAExport(username, idAnalysis);
 * worker.start(); // Start the export process
 * worker.cancel(); // Cancel the export process
 * 
 * Note: This class assumes the existence of various DAO classes and utility classes, which are not included in this code snippet.
 */
public class WorkerSOAExport extends WorkerImpl {

	public static final String DEFAULT_PARAGRAHP_STYLE = "TabText1";

	private String username;

	private Integer idAnalysis;

	private DAOUser daoUser;

	private Locale locale;

	private DateFormat format = null;

	private DAOAnalysis daoAnalysis;

	private DAOWordReport daoWordReport;

	public WorkerSOAExport(String username, Integer idAnalysis) {
		this.username = username;
		this.idAnalysis = idAnalysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#start()
	 */
	@Override
	public synchronized void start() {
		run();
	}

	/**
	 * Initializes the DAO objects used by the WorkerSOAExport class.
	 * 
	 * @param session the Hibernate session used for database operations
	 */
	private void initialiseDAO(Session session) {
		daoUser = new DAOUserHBM(session);
		daoAnalysis = new DAOAnalysisHBM(session);
		daoWordReport = new DAOWordReportHBM(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.asynchronousWorkers.Worker#cancel()
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
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
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
						return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setName(TaskName.EXPORT_SOA);
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			initialiseDAO(session);
			session.beginTransaction();
			final long reportId = processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.export.soa",
					"SOA has been successfully exported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", reportId));
			getServiceTaskFeedback().send(getId(), messageHandler);
		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.getTransaction().getStatus().canRollback())
						session.getTransaction().rollback();
				} catch (Exception e1) {
				}
			}
			MessageHandler messageHandler = null;
			if (e instanceof TrickException)
				messageHandler = new MessageHandler(((TrickException) e).getCode(),
						((TrickException) e).getParameters(), e.getMessage(), e);
			else
				messageHandler = new MessageHandler("error.500.message", "Internal error", e);
			getServiceTaskFeedback().send(getId(), messageHandler);
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
			getWorkersPoolManager().remove(this);
		}

	}

	/**
	 * Performs the processing of the user and analysis to generate a result.
	 *
	 * @return the result of the processing
	 * @throws Exception if there is an error during the processing
	 */
	private long processing() throws Exception {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		return proccessing(user, analysis);
	}

	/**
	 * Processes the given user and analysis to generate an SOA (Statement of Analysis) report.
	 *
	 * @param user     The user for whom the report is being generated.
	 * @param analysis The analysis for which the report is being generated.
	 * @return The ID of the generated report.
	 * @throws Exception If an error occurs during the processing.
	 */
	protected long proccessing(User user, Analysis analysis) throws Exception {
		final int[] progressing = { 2, 95, 0, 0 };
		final File workFile = loadTemplate(analysis.getCustomer(), TrickTemplateType.SOA,
				analysis.getLanguage());
		try {
			// progress, max, size, index

			locale = new Locale(analysis.getLanguage().getAlpha2().toLowerCase());
			format = new SimpleDateFormat("dd/MM/yyyy");
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.loading.soa.template", "Loading soa sheet template", progressing[0] += 3));

			final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(workFile);
			final Document document = wordMLPackage.getMainDocumentPart().getContents();
			final ValueFactory factory = new ValueFactory(analysis.getParameters());
			final double soaThreshold = analysis.findParameter(Constant.SOA_THRESHOLD, 100d);
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.preparing.soa.data", "Preparing soa sheet template", progressing[0] += 5));
			final List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards().values().stream()
					.filter(AnalysisStandard::isSoaEnabled).collect(Collectors.toList());
			MessageHandler handler = new MessageHandler("info.printing.soa.data", "Printing soa data",
					progressing[0] += 1);
			getServiceTaskFeedback().send(getId(), handler);
			progressing[2] = analysisStandards.stream()
					.mapToInt(analysisStandard -> analysisStandard.getMeasures().size()).sum();

			for (AnalysisStandard analysisStandard : analysisStandards) {
				P p = null;
				if (progressing[3] == 0)
					p = (P) document.getContent().parallelStream().filter(P.class::isInstance).findAny().orElse(null);
				if (p == null)
					document.getContent().add((p = new P()));
				setText(setStyle(p, "Heading1"), analysisStandard.getStandard().getName());
				analysisStandard.getMeasures()
						.sort((e1, e2) -> NaturalOrderComparator.compareTo(e1.getMeasureDescription().getReference(),
								e2.getMeasureDescription().getReference()));

				var measures = analysisStandard.getMeasures().stream()
						.filter(e -> !(e.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
								|| e.getStatus().equals(Constant.MEASURE_STATUS_OPTIONAL)))
						.toList();
				Tbl tbl = generateTable(measures, handler, factory, soaThreshold, progressing);
				document.getContent().add(tbl);
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.saving.soa", "Saving soa", 95));
			wordMLPackage.save(workFile);
			final String filename = String.format(Constant.ITR_FILE_NAMING_WIHT_CTRL,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.SOA)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "SOA", analysis.getVersion(),
					"docx", System.nanoTime());
			final WordReport report = WordReport.BuildSOA(analysis.getIdentifier(), analysis.getLabel(),
					analysis.getVersion(), user, filename, workFile.length(),
					FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			daoAnalysis.saveOrUpdate(analysis);
			return report.getId();
		} finally {
			InstanceManager.getServiceStorage().delete(workFile.getName());
		}
	}

	/**
	 * Represents a table in a document.
	 * 
	 * This class provides methods to create and manipulate tables.
	 */
	private Tbl generateTable(List<Measure> measures, MessageHandler handler, ValueFactory factory, double soaThreshold,
			int[] progressing) {
		int rowIndex = 0;
		Tbl table = createTable("TableBLight", measures.size() + 1, 6);
		Tr row = (Tr) table.getContent().get(rowIndex++);
		setCellText((Tc) row.getContent().get(0),
				getMessageSource().getMessage("report.measure.reference", null, "Ref.", locale));
		setCellText((Tc) row.getContent().get(1),
				getMessageSource().getMessage("report.measure.domain", null, "Domain", locale));
		setCellText((Tc) row.getContent().get(2),
				getMessageSource().getMessage("report.measure.status", null, "Status", locale));
		setCellText((Tc) row.getContent().get(3),
				getMessageSource().getMessage("report.measure.due.date", null, "Due date", locale));
		setCellText((Tc) row.getContent().get(4),
				getMessageSource().getMessage("report.soa.justification", null, "Justification", locale));
		setCellText((Tc) row.getContent().get(5),
				getMessageSource().getMessage("report.soa.reference", null, "Reference", locale));
		for (Measure measure : measures) {
			row = (Tr) table.getContent().get(rowIndex++);
			setCellText((Tc) row.getContent().get(0), measure.getMeasureDescription().getReference());
			setCellText((Tc) row.getContent().get(1), measure.getMeasureDescription()
					.getMeasureDescriptionTextByAlpha2(locale.getLanguage()).getDomain());
			if (measure.getMeasureDescription().isComputable()) {
				setCellText((Tc) row.getContent().get(2),
						getMessageSource().getMessage("label.measure.status." + measure.getStatus().toLowerCase(), null,
								measure.getStatus(), locale));
				if (measure.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE)
						|| measure.getStatus().equals(Constant.MEASURE_STATUS_EXCLUDE))
					setCellText((Tc) row.getContent().get(3),
							getMessageSource().getMessage(
									"label.title.measure.status." + measure.getStatus().toLowerCase(), null,
									measure.getStatus(), locale));
				else if (measure.getImplementationRateValue(factory) >= soaThreshold)
					setCellText((Tc) row.getContent().get(3),
							getMessageSource().getMessage("label.measure.implemented", null, "Implemented", locale));
				else
					setCellText((Tc) row.getContent().get(3), format.format(measure.getPhase().getEndDate()));
				if (measure instanceof AbstractNormalMeasure) {
					setCellText((Tc) row.getContent().get(4), ((AbstractNormalMeasure) measure).getSoaComment());
					setCellText((Tc) row.getContent().get(5), ((AbstractNormalMeasure) measure).getSoaReference());
				}
			} else
				mergeCell(row, 1, 5, null);
			handler.setProgress((int) (progressing[0]
					+ (++progressing[3] / (double) progressing[2]) * (progressing[1] - progressing[0])));
		}
		return format(table);
	}

	/**
	 * Represents a table.
	 */
	private Tbl format(Tbl table) {
		int[] headers = { 878, 3128, 549, 1000, 5784, 2382 }, cols = { 303, 1147, 189, 400, 2086, 875 },
				mergeCols = { 303, sum(1, 5, cols) };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr)
				.forEach(tr -> updateRow(tr, tr.getContent().size() == mergeCols.length ? mergeCols : cols, "pct"));

		return table;
	}

	/**
	 * Represents a table in a document.
	 */
	protected Tbl createTable(String styleId, int rows, int cols) {
		Tbl table = TblFactory.createTable(rows, cols, 1);
		if (styleId != null)
			table.getTblPr().getTblStyle().setVal(styleId);
		if (table.getTblPr().getJc() == null)
			table.getTblPr().setJc(new Jc());
		table.getTblPr().getJc().setVal(JcEnumeration.CENTER);
		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(new TblWidth());
		table.getTblPr().getTblW().setType("auto");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(0));
		return table;
	}

	/**
	 * Represents a paragraph in a document.
	 */
	protected P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(new PPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(new PStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	/**
	 * This class represents a paragraph in a document.
	 */
	protected P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

	/**
	 * Sets the text of a cell in the given Tc object.
	 *
	 * @param tc   the Tc object representing the cell
	 * @param text the text to be set in the cell
	 */
	protected void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	/**
	 * Sets the text and alignment of a cell in a table.
	 *
	 * @param cell      the cell to set the text for
	 * @param text      the text to set in the cell
	 * @param alignment the alignment of the text in the cell
	 */
	protected void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p)
				.forEach(p -> setStyle(p, DEFAULT_PARAGRAHP_STYLE));
		setText(paragraph, text, alignment);
	}

	/**
	 * The `P` class represents a paragraph in a document.
	 * It is used to store and manipulate the content and formatting of a paragraph.
	 */
	protected P setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, DEFAULT_PARAGRAHP_STYLE);
			if (paragraph.getParent() instanceof Tc) {
				if (paragraph.getPPr().getJc() == null)
					paragraph.getPPr().setJc(new Jc());
				paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
			} else
				paragraph.getPPr().setTextAlignment(alignment);
		}
		paragraph.getContent().removeIf(R.class::isInstance);
		R r = new R();
		Text text = new Text();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);
		return paragraph;

	}
}
