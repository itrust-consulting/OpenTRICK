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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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

	private DAORiskRegister daoRiskRegister;

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
			daoAnalysis = new DAOAnalysisHBM(session);
			daoWordReport = new DAOWordReportHBM(session);
			daoUser = new DAOUserHBM(session);
			daoRiskRegister = new DAORiskRegisterHBM(session);
			session.beginTransaction();
			long reportId = processing();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.export.risk_register", "Risk register has been successfully exported", 100);
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

	private long processing() throws Exception {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		int progress = 2, max = 90, size, index = 0;
		RiskSheetComputation computation = new RiskSheetComputation(analysis);
		InputStream inputStream = null;
		XWPFDocument document = null;
		OutputStream outputStream = null;
		OPCPackage opcPackage = null;
		File workFile = null;
		try {
			Locale locale = new Locale(analysis.getLanguage().getAlpha2());
			serviceTaskFeedback.send(getId(), new MessageHandler("info.risk_register.backup", "Backup of user changes", progress));
			Map<String, RiskRegisterItem> oldRiskRegister = analysis.getRiskRegisters().stream().collect(Collectors.toMap(RiskRegisterItem::getKey, Function.identity()));
			serviceTaskFeedback.send(getId(), new MessageHandler("info.risk_register.compute", "Computing risk register", progress += 5));
			MessageHandler messageHandler = computation.computeRiskRegister();
			if (messageHandler != null)
				throw messageHandler.getException();
			ParameterConvertor convertor = computation.getConvertor();
			Map<String, RiskProfile> riskProfilesMap = analysis.getRiskProfiles().stream().filter(RiskProfile::isSelected)
					.collect(Collectors.toMap(RiskProfile::getKey, Function.identity()));
			if (!oldRiskRegister.isEmpty()) {
				List<RiskRegisterItem> registerItems = analysis.getRiskRegisters();
				for (int i = 0; i < registerItems.size(); i++) {
					RiskRegisterItem current = registerItems.get(i);
					RiskRegisterItem registerItem = oldRiskRegister.remove(current.getKey());
					if (registerItem == null)
						continue;
					registerItems.set(i, registerItem.merge(current));
				}
				if (!oldRiskRegister.isEmpty()) {
					oldRiskRegister.values().forEach(riskRegister -> daoRiskRegister.delete(riskRegister));
					oldRiskRegister.clear();
				}
			}

			serviceTaskFeedback.send(getId(), new MessageHandler("info.loading.risk_register.template", "Loading risk register template", progress += 5));
			workFile = new File(
					String.format("%s/tmp/RISK_REGISTER_%d_%s_V%s.docm", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/%s.dotx", rootPath, locale.getLanguage().equalsIgnoreCase("fr") ? FR_TEMPLATE : ENG_TEMPLATE));
			opcPackage = OPCPackage.open(doctemplate.getAbsoluteFile());
			opcPackage.replaceContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml");
			opcPackage.save(workFile);
			document = new XWPFDocument(inputStream = new FileInputStream(workFile));
			serviceTaskFeedback.send(getId(), new MessageHandler("info.preparing.risk_register.data", "Preparing risk register template", progress += 8));
			Map<String, Assessment> assessments = analysis.getAssessments().stream().filter(assessment -> assessment.isSelected())
					.collect(Collectors.toMap(Assessment::getKey, Function.identity()));
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.generating.risk_register", "Generating risk register", progress += 8));
			size = analysis.getRiskRegisters().size();
			XWPFTable table = getTable(document, 0);// lib contains a bug
			if (table == null)
				throw new IllegalArgumentException(String.format("Please check risk register template: %s", doctemplate.getPath()));
			for (RiskRegisterItem registerItem : analysis.getRiskRegisters()) {
				Assessment assessment = assessments.get(Assessment.key(registerItem.getAsset(), registerItem.getScenario()));
				RiskProfile riskProfile = riskProfilesMap.get(RiskProfile.key(registerItem.getAsset(), registerItem.getScenario()));
				XWPFTableRow row = index == 0 ? table.getRow(table.getRows().size() - 1) : table.createRow();
				String scenarioType = riskProfile.getScenario().getType().getName();
				addInt(index + 1, row, 0);
				addString(riskProfile.getIdentifier(), row, 1);
				addString(getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType, locale), row, 2);
				addString(riskProfile.getScenario().getName(), row, 3);
				addString(riskProfile.getAsset().getName(), row, 4);
				RiskProbaImpact netImpact = new RiskProbaImpact(), raw = riskProfile.getExpProbaImpact(), expected = riskProfile.getExpProbaImpact();
				netImpact.setImpactFin(convertor.getImpact(assessment.getImpactFin()));
				netImpact.setImpactLeg(convertor.getImpact(assessment.getImpactLeg()));
				netImpact.setImpactOp(convertor.getImpact(assessment.getImpactOp()));
				netImpact.setImpactRep(convertor.getImpact(assessment.getImpactRep()));
				netImpact.setProbability(convertor.getProbability(assessment.getLikelihood()));
				if (raw == null) {
					raw = new RiskProbaImpact();
					raw.setImpactFin(convertor.getImpact(registerItem.getRawEvaluation().getImpact()));
					raw.setProbability(convertor.getProbability(registerItem.getRawEvaluation().getProbability()));
				}

				if (expected == null) {
					expected = new RiskProbaImpact();
					expected.setImpactFin(convertor.getImpact(registerItem.getExpectedEvaluation().getImpact()));
					expected.setProbability(convertor.getProbability(registerItem.getExpectedEvaluation().getProbability()));
				}

				addField(raw, row, 5);
				addField(netImpact, row, 8);
				addField(expected, row, 11);
				RiskStrategy strategy = riskProfile.getRiskStrategy();
				if (strategy == null)
					strategy = RiskStrategy.ACCEPT;
				String response = strategy.getNameToLower();
				addString(getMessage("label.risk_register.strategy." + response, response, locale), row, 14);
				addString(assessment.getOwner(), row, 15);
				messageHandler.setProgress((int) (progress + (++index / (double) size) * (max-progress)));
			}
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.saving.risk_register", "Saving risk register", max));
			document.write(outputStream = new FileOutputStream(workFile));
			outputStream.flush();
			WordReport report = WordReport.BuildRiskRegister(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, workFile.getName(), workFile.length(),
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
			
			if(opcPackage!=null)
				opcPackage.close();
		}
	}

	/**
	 * fix bug for index: 0
	 * 
	 * @param document
	 * @param index
	 * @return
	 */
	private XWPFTable getTable(XWPFDocument document, int index) {
		if (index != 0)
			return document.getTableArray(index);
		List<XWPFTable> tables = document.getTables();
		if (tables.size() > 0)
			return tables.get(0);
		return null;
	}

	private void addInt(int value, XWPFTableRow row, int index) {
		XWPFTableCell cell = getCell(row, index);
		cell.setText(value + "");
		cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
	}

	private void addString(String content, XWPFTableRow row, int index) {
		getCell(row, index).setText(content == null ? "" : content);
	}

	private void addField(RiskProbaImpact expProbaImpact, XWPFTableRow row, int index) {
		int impact = 0, proba = 0;
		if (expProbaImpact != null) {
			impact = expProbaImpact.getImpactLevel();
			proba = expProbaImpact.getProbabilityLevel();
		}
		addInt(impact, row, index);
		addInt(proba, row, ++index);
		addInt(proba * impact, row, ++index);
	}

	private XWPFTableCell getCell(XWPFTableRow row, int index) {
		XWPFTableCell cell = row.getCell(index);
		if (cell == null)
			cell = row.addNewTableCell();
		return cell;
	}

	private String getMessage(String code, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, null, defaultMessage, locale);
	}

}
