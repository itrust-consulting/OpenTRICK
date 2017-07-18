/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jWordExporter.MergeCell;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter.sum;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter.updateRow;

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
import lu.itrust.business.TS.messagehandler.TaskName;
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

	public static final String DEFAULT_PARAGRAHP_STYLE = "TabText1";

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
				setName(TaskName.EXPORT_SOA);
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

	private long processing() throws Exception {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		return proccessing(user, analysis);
	}

	protected long proccessing(User user, Analysis analysis) throws Exception {
		File workFile = null;
		WordprocessingMLPackage wordMLPackage = null;
		Document document = null;
		try {
			// progress, max, size, index
			int[] progressing = { 2, 95, 0, 0 };
			locale = new Locale(analysis.getLanguage().getAlpha2().toLowerCase());
			format = new SimpleDateFormat("dd/MM/yyyy");
			serviceTaskFeedback.send(getId(), new MessageHandler("info.loading.soa.template", "Loading soa sheet template", progressing[0] += 3));
			workFile = new File(String.format("%s/tmp/SOA_%d_%s_V%s.docx", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/docx/%s.docx", rootPath, locale.getLanguage().equals("fr") ? FR_TEMPLATE : ENG_TEMPLATE));
			wordMLPackage = createDocument(doctemplate, workFile);
			document = wordMLPackage.getMainDocumentPart().getContents();
			serviceTaskFeedback.send(getId(), new MessageHandler("info.preparing.soa.data", "Preparing soa sheet template", progressing[0] += 5));
			List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards().stream().filter(AnalysisStandard::isSoaEnabled).collect(Collectors.toList());
			MessageHandler handler = new MessageHandler("info.printing.soa.data", "Printing soa data", progressing[0] += 1);
			serviceTaskFeedback.send(getId(), handler);
			progressing[2] = analysisStandards.stream().mapToInt(analysisStandard -> analysisStandard.getMeasures().size()).sum();
			for (AnalysisStandard analysisStandard : analysisStandards) {
				P p = null;
				if (progressing[3] == 0)
					p = (P) document.getContent().parallelStream().filter(p1 -> p1 instanceof P).findAny().orElse(null);
				if (p == null)
					document.getContent().add(p = new P());
				setText(setStyle(p, "Heading1"), analysisStandard.getStandard().getLabel());
				Tbl tbl = generateTable(analysisStandard.getMeasures(), handler, progressing);
				document.getContent().add(tbl);
			}
			serviceTaskFeedback.send(getId(), new MessageHandler("info.saving.soa", "Saving soa", 95));
			wordMLPackage.save(workFile);
			WordReport report = WordReport.BuildSOA(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, workFile.getName(), workFile.length(),
					FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			daoAnalysis.saveOrUpdate(analysis);
			return report.getId();
		} finally {
			if (workFile != null && workFile.exists() && !workFile.delete())
				workFile.deleteOnExit();
		}
	}

	private Tbl generateTable(List<Measure> measures, MessageHandler handler, int[] progressing) {
		int rowIndex = 0;
		Tbl table = createTable("TSSOA", measures.size()+1, 6);
		Tr row = (Tr) table.getContent().get(rowIndex++);
		setCellText((Tc) row.getContent().get(0), messageSource.getMessage("report.measure.reference", null, "Ref.", locale));
		setCellText((Tc) row.getContent().get(1), messageSource.getMessage("report.measure.domain", null, "Domain", locale));
		setCellText((Tc) row.getContent().get(2), messageSource.getMessage("report.measure.status", null, "Status", locale));
		setCellText((Tc) row.getContent().get(3), messageSource.getMessage("report.measure.due.date", null, "Due date", locale));
		setCellText((Tc) row.getContent().get(4), messageSource.getMessage("report.soa.justification", null, "Justification", locale));
		setCellText((Tc) row.getContent().get(5), messageSource.getMessage("report.soa.reference", null, "Reference", locale));
		for (Measure measure : measures) {
			row = (Tr) table.getContent().get(rowIndex++);
			setCellText((Tc) row.getContent().get(0), measure.getMeasureDescription().getReference());
			setCellText((Tc) row.getContent().get(1), measure.getMeasureDescription().getMeasureDescriptionTextByAlpha2(locale.getLanguage()).getDomain());
			if (measure.getMeasureDescription().isComputable()) {
				setCellText((Tc) row.getContent().get(2), messageSource.getMessage("label.measure.status." + measure.getStatus().toLowerCase(), null, measure.getStatus(), locale));
				setCellText((Tc) row.getContent().get(3), format.format(measure.getPhase().getEndDate()));
				MeasureProperties properties = measure instanceof NormalMeasure ? ((NormalMeasure) measure).getMeasurePropertyList()
						: measure instanceof AssetMeasure ? ((AssetMeasure) measure).getMeasurePropertyList() : null;
				if (properties != null) {
					setCellText((Tc) row.getContent().get(4), properties.getSoaComment());
					setCellText((Tc) row.getContent().get(5), properties.getSoaReference());
				}
			} else
				MergeCell(row, 1, 5, null);
			handler.setProgress((int) (progressing[0] + (++progressing[3] / (double) progressing[2]) * (progressing[1] - progressing[0])));
		}
		return format(table);
	}

	private Tbl format(Tbl table) {
		int[] headers = { 878, 3128, 549, 1000, 5784, 2382 }, cols = { 303, 1147, 189, 400, 2086, 875 }, mergeCols = { 303, sum(1, 5, cols) };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).forEach(tr -> updateRow(tr, tr.getContent().size() == mergeCols.length ? mergeCols : cols, "pct"));

		return table;
	}

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

	protected P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(new PPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(new PStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	protected P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

	
	protected void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	protected void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p).forEach(p -> setStyle(p, DEFAULT_PARAGRAHP_STYLE));
		setText(paragraph, text, alignment);
	}

	

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
		paragraph.getContent().removeIf(r -> r instanceof R);
		R r = new R();
		Text text = new Text();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);
		return paragraph;

	}

	private WordprocessingMLPackage createDocument(File doctemplate, File workFile) throws Exception {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(doctemplate);
		wordMLPackage.save(workFile);
		return WordprocessingMLPackage.load(workFile);
	}

}
