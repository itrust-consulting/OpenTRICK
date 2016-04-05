/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import lu.itrust.business.TS.database.dao.DAORiskRegister;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskRegisterHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.ParameterConvertor;
import lu.itrust.business.TS.model.cssf.helper.RiskSheetComputation;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerExportRiskSheet extends WorkerImpl implements Worker {

	private String username;

	private String rootPath;

	private int idAnalysis;

	private ServiceTaskFeedback serviceTaskFeedback;

	private DAOAnalysis daoAnalysis;

	private DAORiskRegister daoRiskRegister;

	private DAOWordReport daoWordReport;

	private DAOUser daoUser;

	private MessageSource messageSource;

	private Locale locale;

	public static String FR_TEMPLATE;

	public static String ENG_TEMPLATE;

	public WorkerExportRiskSheet(WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, String rootPath, Integer analysisId,
			String username, MessageSource messageSource) {
		super(poolManager, sessionFactory);
		setUsername(username);
		setIdAnalysis(analysisId);
		setRootPath(rootPath);
		setServiceTaskFeedback(serviceTaskFeedback);
		setMessageSource(messageSource);
	}

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
			daoAnalysis = new DAOAnalysisHBM(session);
			daoWordReport = new DAOWordReportHBM(session);
			daoUser = new DAOUserHBM(session);
			daoRiskRegister = new DAORiskRegisterHBM(session);
			session.beginTransaction();
			long reportId = processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.save.risk_sheet", "Risk sheet has been successfully saved", 100);
			messageHandler.setAsyncCallback(new AsyncCallback("downloadWordReport('" + reportId + "');reloadSection('section_riskregister');"));
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

	@Override
	public synchronized void start() {
		run();
	}

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

	private long processing() throws Exception {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		int progress = 2, max = 60, size, index = 0;
		RiskSheetComputation computation = new RiskSheetComputation(analysis);
		setLocale(new Locale(analysis.getLanguage().getAlpha2()));
		InputStream inputStream = null;
		XWPFDocument document = null;
		OutputStream outputStream = null;
		File workFile = null;
		try {
			serviceTaskFeedback.send(getId(), new MessageHandler("info.risk_register.compute", "Computing risk register", progress));
			Map<String, RiskRegisterItem> oldRiskRegister = analysis.getRiskRegisters().stream().collect(Collectors.toMap(RiskRegisterItem::getKey, Function.identity()));
			MessageHandler messageHandler = computation.computeRiskRegister();
			if (messageHandler != null)
				throw messageHandler.getException();
			ParameterConvertor convertor = computation.getConvertor();
			Map<String, RiskProfile> riskProfilesMap = analysis.getRiskProfiles().stream().filter(RiskProfile::isSelected)
					.collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));
			List<RiskProfile> riskProfiles = new LinkedList<>();
			if (!oldRiskRegister.isEmpty()) {
				List<RiskRegisterItem> registerItems = analysis.getRiskRegisters();
				for (int i = 0; i < registerItems.size(); i++) {
					RiskRegisterItem current = registerItems.get(i);
					riskProfiles.add(riskProfilesMap.get(RiskProfile.key(current.getAsset(), current.getScenario())));
					RiskRegisterItem registerItem = oldRiskRegister.remove(current.getKey());
					if (registerItem == null)
						continue;
					registerItems.set(i, registerItem.merge(current));
				}
				if (!oldRiskRegister.isEmpty()) {
					oldRiskRegister.values().forEach(riskRegister -> daoRiskRegister.delete(riskRegister));
					oldRiskRegister.clear();
				}
			} else
				analysis.getRiskRegisters().forEach(current -> riskProfiles.add(riskProfilesMap.get(RiskProfile.key(current.getAsset(), current.getScenario()))));

			serviceTaskFeedback.send(getId(), new MessageHandler("info.loading.risk_sheet.template", "Loading risk sheet template", progress += 5));
			workFile = new File(
					String.format("%s/tmp/RISK_SHEET_%d_%s_V%s.docm", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/%s.dotm", rootPath, analysis.getLanguage().getAlpha2().equalsIgnoreCase("fr") ? FR_TEMPLATE : ENG_TEMPLATE));
			OPCPackage opcPackage = OPCPackage.open(doctemplate.getAbsoluteFile());
			opcPackage.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
			opcPackage.save(workFile);
			document = new XWPFDocument(inputStream = new FileInputStream(workFile));
			serviceTaskFeedback.send(getId(), new MessageHandler("info.preparing.data", "Preparing risk sheet template", progress += 8));
			Map<String, Assessment> assessments = analysis.getAssessments().stream().filter(assessment -> assessment.isSelected())
					.collect(Collectors.toMap(Assessment::getKey, Function.identity()));
			List<ExtendedParameter> probabilities = convertor.getProbabilityParameters(), impacts = convertor.getImpactsParameters();
			boolean isFirst = true;
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", progress += 8));
			size = riskProfiles.size();
			for (RiskProfile riskProfile : riskProfiles) {
				isFirst = addRiskSheetHeader(document, riskProfile, isFirst);
				Assessment assessment = assessments.get(Assessment.key(riskProfile.getAsset(), riskProfile.getScenario()));
				addField(document, getMessage("report.risk_sheet.risk_owner", "Risk owner"), assessment.getOwner());
				addField(document, getMessage("report.risk_sheet.risk_description", "Risk description"), riskProfile.getScenario().getDescription());
				RiskProbaImpact netImpact = new RiskProbaImpact();
				netImpact.setImpactFin(convertor.getImpact(assessment.getImpactFin()));
				netImpact.setImpactLeg(convertor.getImpact(assessment.getImpactLeg()));
				netImpact.setImpactOp(convertor.getImpact(assessment.getImpactOp()));
				netImpact.setImpactRep(convertor.getImpact(assessment.getImpactRep()));
				netImpact.setProbability(convertor.getProbability(assessment.getLikelihood()));
				addTable(document, getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"), riskProfile.getRawProbaImpact(), impacts.get(0), probabilities.get(0));
				addField(document, getMessage("report.risk_sheet.argumentation", "Argumentation"), assessment.getComment());
				addField(document, getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"), riskProfile.getAsset().getName());
				addField(document, getMessage("report.risk_sheet.risk_treatment", "Risk treatment"), riskProfile.getRiskTreatment());
				addTable(document, getMessage("report.risk_sheet.net_evaluation", "Net evaluation"), netImpact, impacts.get(0), probabilities.get(0));
				RiskStrategy strategy = riskProfile.getRiskStrategy();
				if(strategy == null)
					strategy = RiskStrategy.AVOID;
				String response = strategy.getNameToLower();
				addField(document, getMessage("report.risk_sheet.response", "Response strategy"), getMessage("label.risk_register.strategy." + response, response));
				addField(document, getMessage("report.risk_sheet.action_plan", "Action plan"), riskProfile.getActionPlan());
				addTable(document, getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"), riskProfile.getExpProbaImpact(), impacts.get(0), probabilities.get(0));
				messageHandler.setProgress(progress + ((++index / size) * 100 / max));
			}
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", max));
			document.write(outputStream = new FileOutputStream(workFile));
			outputStream.flush();
			WordReport report = WordReport.BuildRiskSheet(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, workFile.getName(), workFile.length(),
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

	private void addTable(XWPFDocument document, String title, RiskProbaImpact probaImpact, ExtendedParameter impact, ExtendedParameter probability) {
		addTitle(document, title);
		XWPFTable table = document.createTable(3, 6);
		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();
		table.setStyleID("TSTABLEEVALUATION");
		XWPFTableRow row = table.getRow(0);
		getCell(row, 0).setText(getMessage("report.risk_sheet.probability", "Probability (P)"));
		getCell(row, 1).setText(getMessage("report.risk_sheet.impact", "Impact (i)"));
		getCell(row, 5).setText(getMessage("report.risk_sheet.importance", "Importance"));
		row = table.getRow(1);
		getCell(row, 1).setText(getMessage("label.impact_rep", "Reputation"));
		getCell(row, 2).setText(getMessage("label.impact_op", "Operation"));
		getCell(row, 3).setText(getMessage("label.impact_leg", "Legal"));
		getCell(row, 4).setText(getMessage("label.impact_fin", "Financial"));
		row = table.getRow(2);
		getCell(row, 0).setText(probaImpact.getProbability(probability).getLevel() + "");
		getCell(row, 1).setText(probaImpact.getImpactRep(impact).getLevel() + "");
		getCell(row, 2).setText(probaImpact.getImpactOp(impact).getLevel() + "");
		getCell(row, 3).setText(probaImpact.getImpactLeg(impact).getLevel() + "");
		getCell(row, 4).setText(probaImpact.getImpactFin(impact).getLevel() + "");
		getCell(row, 5).setText(probaImpact.getImportance() + "");
	}

	private void addTitle(XWPFDocument document, String title) {
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setStyle("TSTitle");
		paragraph.createRun().setText(title);
	}

	private void addField(XWPFDocument document, String title, String content) {
		addTitle(document, title);
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setStyle("CorpsTxt");
		paragraph.createRun().setText(content);
	}

	private boolean addRiskSheetHeader(XWPFDocument document, RiskProfile riskProfile, boolean isFirst) {
		XWPFTable table = null;
		if (isFirst)
			table = document.insertNewTbl(document.getParagraphs().get(0).getCTP().newCursor());
		else {
			document.createParagraph().setPageBreak(true);
			table = document.createTable(1, 6);
		}

		table.setStyleID("TSTABLERISK");
		XWPFTableRow row = table.getRow(0);
		getCell(row, 0).setText(getMessage("report.risk_sheet.risk_id", "Risk ID"));
		getCell(row, 1).setText((riskProfile.getIdentifier() == null ? "" : riskProfile.getIdentifier()));
		getCell(row, 2).setText(getMessage("report.risk_sheet.risk_category", "Category"));
		String scenarioType = riskProfile.getScenario().getType().getName();
		getCell(row, 3).setText(getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType));
		getCell(row, 4).setText(getMessage("report.risk_sheet.title", "Title"));
		getCell(row, 5).setText(riskProfile.getScenario().getName());
		return false;
	}

	private XWPFTableCell getCell(XWPFTableRow row, int i) {
		XWPFTableCell cell;
		cell = row.getCell(i);
		if (cell == null)
			cell = row.addNewTableCell();
		return cell;
	}

	private String getMessage(String code, String defaultMeassge) {
		return messageSource.getMessage(code, null, defaultMeassge, locale);
	}

	/**
	 * @return the username
	 */
	protected String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	protected void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the rootPath
	 */
	protected String getRootPath() {
		return rootPath;
	}

	/**
	 * @param rootPath
	 *            the rootPath to set
	 */
	protected void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * @return the idAnalysis
	 */
	protected int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * @param idAnalysis
	 *            the idAnalysis to set
	 */
	protected void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @return the messageSource
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * @param messageSource
	 *            the messageSource to set
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @return the serviceTaskFeedback
	 */
	protected ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
	}

	/**
	 * @param serviceTaskFeedback
	 *            the serviceTaskFeedback to set
	 */
	protected void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
