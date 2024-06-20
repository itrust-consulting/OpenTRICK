/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.helper.InstanceManager.loadTemplate;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.docx4j.XmlUtils;
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
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.ExportFileName;
import lu.itrust.business.ts.model.assessment.helper.Estimation;
import lu.itrust.business.ts.model.cssf.RiskProbaImpact;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.usermanagement.User;


/**
 * This class represents a worker responsible for exporting the risk register.
 * It extends the WorkerImpl class and implements the Worker interface.
 * The worker exports the risk register based on the provided analysis ID and username.
 * It performs the export operation asynchronously.
 */
public class WorkerExportRiskRegister extends WorkerImpl {

	private String username;

	private Integer idAnalysis;

	private DAOUser daoUser;

	private DAOAnalysis daoAnalysis;

	private DAOWordReport daoWordReport;

	/**
	 * @param idAnalysis
	 * @param username
	 * @param rootPath
	 * @param sessionFactory
	 * @param poolManager
	 * @param serviceTaskFeedback
	 * @param messageSource
	 */
	public WorkerExportRiskRegister(Integer idAnalysis, String username) {
		this.idAnalysis = idAnalysis;
		this.username = username;
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
				setName(TaskName.EXPORT_RISK_REGISTER);
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			daoAnalysis = new DAOAnalysisHBM(session);
			daoWordReport = new DAOWordReportHBM(session);
			daoUser = new DAOUserHBM(session);
			session.beginTransaction();
			long reportId = processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.export.risk_register",
					"Risk register has been successfully exported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", reportId),
					new AsyncCallback("reloadSection", "section_riskregister"));
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
	 * Performs the processing of generating a risk register document based on the given analysis and user.
	 * 
	 * @return The ID of the generated WordReport.
	 * @throws Exception If there is an error during the processing.
	 */
	private long processing() throws Exception {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		int progress = 2, max = 90, size, index = 0;
		MessageHandler messageHandler = null;
		final File workFile = loadTemplate(analysis.getCustomer(), TrickTemplateType.RISK_REGISTER,
				analysis.getLanguage());
		try {
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.risk_register.compute", "Computing risk register", progress += 5));
			final boolean showRawColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			final Locale locale = new Locale(analysis.getLanguage().getAlpha2());
			final List<Estimation> estimations = Estimation.GenerateEstimation(analysis,
					new ValueFactory(analysis.getParameters()), Estimation.IdComparator());

			getServiceTaskFeedback().send(getId(), new MessageHandler("info.loading.risk_register.template",
					"Loading risk register template", progress += 5));

			final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(workFile);
			final Document document = wordMLPackage.getMainDocumentPart().getContents();
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.generating.risk_register",
					"Generating risk register", progress += 8));

			final Tbl table = (Tbl) document.getContent().parallelStream().map(XmlUtils::unwrap)
					.filter(tb -> tb instanceof Tbl).findFirst().orElse(null);
			if (table == null)
				throw new IllegalArgumentException("Please check risk register template");
			if (!showRawColumn) {
				table.getContent().parallelStream().map(p -> XmlUtils.unwrap(p)).filter(p -> p instanceof Tr)
						.map(tr -> (Tr) tr).forEach(tr -> {
							tr.getContent().remove(5);
							if (tr.getContent().size() > 14) {
								for (int i = 0; i < 2; i++)
									tr.getContent().remove(5);
							}

						});
			}

			final List<Tr> trs = new ArrayList<>(size = estimations.size());

			trs.add(table.getContent().stream().map(tr -> XmlUtils.unwrap(tr)).filter(tr -> tr instanceof Tr)
					.map(tr -> (Tr) tr).reduce((f, l) -> l).get());// it will be removed
																	// laster
			for (int i = 1; i < size; i++)
				trs.add(XmlUtils.deepCopy(trs.get(0)));
			int rawIndex = 5, nextIndex = showRawColumn ? rawIndex + 3 : rawIndex, expIndex = nextIndex + 3;
			for (Estimation estimation : estimations) {
				final Tr row = trs.get(index);
				String scenarioType = estimation.getScenario().getType().getName();
				addInt(index + 1, row, 0);
				addString(estimation.getIdentifier(), row, 1);
				addString(getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(),
						scenarioType, locale), row, 2);
				addString(estimation.getScenario().getName(), row, 3);
				addString(estimation.getAsset().getName(), row, 4);
				if (showRawColumn)
					addField(estimation.getRawProbaImpact(), row, rawIndex);
				addField(estimation.getNetEvaluation(), row, nextIndex);
				addField(estimation.getExpProbaImpact(), row, expIndex);
				RiskStrategy strategy = estimation.getRiskStrategy();
				if (strategy == null)
					strategy = RiskStrategy.ACCEPT;
				String response = strategy.getNameToLower();
				addString(getMessage("label.risk_register.strategy." + response, response, locale), row, expIndex + 3);
				addString(estimation.getOwner(), row, expIndex + 4);
				messageHandler.setProgress((int) (progress + (++index / (double) size) * (max - progress)));
			}

			trs.remove(0);

			table.getContent().addAll(trs);

			getServiceTaskFeedback().send(getId(),
					messageHandler = new MessageHandler("info.saving.risk_register", "Saving risk register", max));
			wordMLPackage.save(workFile);

			final String filename = String.format(Constant.ITR_FILE_NAMING_WIHT_CTRL,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.RISK_REGISTER)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "RiskRegister", analysis.getVersion(),
					"docx", System.nanoTime());

			final WordReport report = WordReport.BuildRiskRegister(analysis.getIdentifier(), analysis.getLabel(),
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
	 * Represents a paragraph in a document.
	 */
	private P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(new PPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(new PStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	/**
	 * Sets the text of a cell in a table cell (Tc) object.
	 *
	 * @param tc   The table cell object to set the text for.
	 * @param text The text to set in the table cell.
	 */
	private void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	/**
	 * Sets the text and alignment of a cell in a table.
	 *
	 * @param cell      the cell to set the text for
	 * @param text      the text to set in the cell
	 * @param alignment the alignment of the text in the cell
	 */
	private void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().map(p -> XmlUtils.unwrap(p)).filter(p -> p instanceof P).map(p -> (P) p)
				.forEach(p -> setStyle(p, "TabText2"));
		setText(paragraph, text, alignment);
	}

	/**
	 * Represents a paragraph in a document.
	 */
	private P setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, "TabText2");
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

	/**
	 * Adds an integer value to the specified row at the given index.
	 *
	 * @param value the integer value to add
	 * @param row the row to add the value to
	 * @param index the index at which to add the value
	 */
	private void addInt(int value, Tr row, int index) {
		Tc cell = (Tc) XmlUtils.unwrap(row.getContent().get(index));
		setCellText(cell, value + "", createAlignment("right"));
	}

	/**
	 * Represents the alignment of text.
	 */
	private TextAlignment createAlignment(String value) {
		TextAlignment alignment = new TextAlignment();
		alignment.setVal(value);
		return alignment;
	}

	/**
	 * Adds a string to the specified cell in the risk register.
	 *
	 * @param content The string to be added to the cell.
	 * @param row The row containing the cell.
	 * @param index The index of the cell in the row.
	 */
	private void addString(String content, Tr row, int index) {
		setCellText((Tc) XmlUtils.unwrap(row.getContent().get(index)), content);
	}

	/**
	 * Adds the fields related to risk probability and impact to the given row at the specified index.
	 *
	 * @param expProbaImpact The risk probability and impact values to be added. Can be null.
	 * @param row The row to which the fields will be added.
	 * @param index The index at which the fields will be added.
	 */
	private void addField(RiskProbaImpact expProbaImpact, Tr row, int index) {
		int impact = 0, proba = 0;
		if (expProbaImpact != null) {
			impact = expProbaImpact.getImpactLevel();
			proba = expProbaImpact.getProbabilityLevel();
		}
		addInt(proba, row, index);
		addInt(impact, row, ++index);
		addInt(proba * impact, row, ++index);
	}

	private String getMessage(String code, String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(code, null, defaultMessage, locale);
	}

}
