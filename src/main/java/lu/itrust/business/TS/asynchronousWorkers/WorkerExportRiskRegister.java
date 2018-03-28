/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

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
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.FileCopyUtils;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
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
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.general.document.impl.WordReport;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerExportRiskRegister extends WorkerImpl {

	public static String FR_TEMPLATE;

	public static String ENG_TEMPLATE;

	private String username;

	private String rootPath;

	private Integer idAnalysis;

	private DAOUser daoUser;

	private DAOAnalysis daoAnalysis;

	private DAOWordReport daoWordReport;

	private MessageSource messageSource;

	private ServiceTaskFeedback serviceTaskFeedback;

	/**
	 * @param idAnalysis
	 * @param username
	 * @param rootPath
	 * @param sessionFactory
	 * @param poolManager
	 * @param serviceTaskFeedback
	 * @param messageSource
	 */
	public WorkerExportRiskRegister(Integer idAnalysis, String username, String rootPath, SessionFactory sessionFactory, WorkersPoolManager poolManager,
			ServiceTaskFeedback serviceTaskFeedback, MessageSource messageSource) {
		super(poolManager, sessionFactory);
		this.idAnalysis = idAnalysis;
		this.username = username;
		this.rootPath = rootPath;
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
						if(getCurrent() == null)
							Thread.currentThread().interrupt();
						else getCurrent().interrupt();
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
			MessageHandler messageHandler = new MessageHandler("success.export.risk_register", "Risk register has been successfully exported", 100);
			messageHandler.setAsyncCallbacks(new AsyncCallback("download","Report", reportId), new AsyncCallback("reloadSection", "section_riskregister"));
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
				messageHandler = new MessageHandler("error.500.message", "Internal error", e);
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
		int progress = 2, max = 90, size, index = 0;
		MessageHandler messageHandler = null;
		File workFile = null;
		try {

			boolean showRawColumn = analysis.getSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			Locale locale = new Locale(analysis.getLanguage().getAlpha2());
			serviceTaskFeedback.send(getId(), new MessageHandler("info.risk_register.backup", "Backup of user changes", progress));
			serviceTaskFeedback.send(getId(), new MessageHandler("info.risk_register.compute", "Computing risk register", progress += 5));
			List<Estimation> estimations = Estimation.GenerateEstimation(analysis, new ValueFactory(analysis.getParameters()), Estimation.IdComparator());
			serviceTaskFeedback.send(getId(), new MessageHandler("info.loading.risk_register.template", "Loading risk register template", progress += 5));
			workFile = new File(
					String.format("%s/tmp/RISK_REGISTER_%d_%s_v%s.docx", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/docx/%s.docx", rootPath, locale.getLanguage().equalsIgnoreCase("fr") ? FR_TEMPLATE : ENG_TEMPLATE));
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(doctemplate);
			wordMLPackage.save(workFile);
			wordMLPackage = WordprocessingMLPackage.load(workFile);
			Document document = wordMLPackage.getMainDocumentPart().getContents();
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.generating.risk_register", "Generating risk register", progress += 8));

			Tbl table = (Tbl) document.getContent().parallelStream().map(tb -> XmlUtils.unwrap(tb)).filter(tb -> tb instanceof Tbl).findFirst().orElse(null);
			if (table == null)
				throw new IllegalArgumentException(String.format("Please check risk register template: %s", doctemplate.getPath()));
			if (!showRawColumn) {
				table.getContent().parallelStream().map(p -> XmlUtils.unwrap(p)).filter(p -> p instanceof Tr).map(tr -> (Tr) tr).forEach(tr -> {
					tr.getContent().remove(5);
					if (tr.getContent().size() > 14) {
						for (int i = 0; i < 2; i++)
							tr.getContent().remove(5);
					}

				});
			}

			List<Tr> trs = new ArrayList<>(size = estimations.size());

			trs.add(table.getContent().stream().map(tr -> XmlUtils.unwrap(tr)).filter(tr -> tr instanceof Tr).map(tr -> (Tr) tr).reduce((f, l) -> l).get());// it will be removed
																																							// laster
			for (int i = 1; i < size; i++)
				trs.add(XmlUtils.deepCopy(trs.get(0)));
			int rawIndex = 5, nextIndex = showRawColumn ? rawIndex + 3 : rawIndex, expIndex = nextIndex + 3;
			for (Estimation estimation : estimations) {
				Tr row = trs.get(index);
				String scenarioType = estimation.getScenario().getType().getName();
				addInt(index + 1, row, 0);
				addString(estimation.getIdentifier(), row, 1);
				addString(getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType, locale), row, 2);
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

			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.saving.risk_register", "Saving risk register", max));
			wordMLPackage.save(workFile);
			WordReport report = WordReport.BuildRiskRegister(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, workFile.getName(), workFile.length(),
					FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			daoAnalysis.saveOrUpdate(analysis);
			return report.getId();
		} finally {
			if (workFile != null && workFile.exists() && !workFile.delete())
				workFile.deleteOnExit();
		}
	}

	private P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(new PPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(new PStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	private void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	private void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().map(p -> XmlUtils.unwrap(p)).filter(p -> p instanceof P).map(p -> (P) p).forEach(p -> setStyle(p, "TabText2"));
		setText(paragraph, text, alignment);
	}

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

	private void addInt(int value, Tr row, int index) {
		Tc cell = (Tc) XmlUtils.unwrap(row.getContent().get(index));
		setCellText(cell, value + "", createAlignment("right"));
	}

	private TextAlignment createAlignment(String value) {
		TextAlignment alignment = new TextAlignment();
		alignment.setVal(value);
		return alignment;
	}

	private void addString(String content, Tr row, int index) {
		setCellText((Tc) XmlUtils.unwrap(row.getContent().get(index)), content);
	}

	private void addField(RiskProbaImpact expProbaImpact, Tr row, int index) {
		int impact = 0, proba = 0;
		if (expProbaImpact != null) {
			impact = expProbaImpact.getImpactLevel();
			proba = expProbaImpact.getProbabilityLevel();
		}
		addInt(impact, row, index);
		addInt(proba, row, ++index);
		addInt(proba * impact, row, ++index);
	}

	private String getMessage(String code, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, null, defaultMessage, locale);
	}

}
