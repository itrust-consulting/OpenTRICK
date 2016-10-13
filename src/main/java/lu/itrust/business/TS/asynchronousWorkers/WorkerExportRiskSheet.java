/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.CSSFExportForm;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.cssf.helper.RiskSheetComputation;
import lu.itrust.business.TS.model.cssf.helper.RiskSheetExportComparator;
import lu.itrust.business.TS.model.cssf.tools.CSSFSort;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.general.helper.ExportType;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.helper.value.IValue;
import lu.itrust.business.TS.model.parameter.helper.value.ValueFactory;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerExportRiskSheet extends WorkerImpl {

	private String username;

	private String rootPath;

	private int idAnalysis;

	private Locale locale;

	private ServiceTaskFeedback serviceTaskFeedback;

	private DAORiskRegister daoRiskRegister;

	private DAOWordReport daoWordReport;

	private MessageSource messageSource;

	private DAOAnalysis daoAnalysis;

	private DAOUser daoUser;

	private CSSFExportForm cssfExportForm;

	public static String FR_TEMPLATE;

	public static String ENG_TEMPLATE;

	public WorkerExportRiskSheet(CSSFExportForm cssfExportForm, WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback,
			String rootPath, Integer analysisId, String username, MessageSource messageSource) {
		super(poolManager, sessionFactory);
		setCssfExportForm(cssfExportForm);
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
			long reportId = getCssfExportForm().getType() == ExportType.RAW ? exportData() : exportReport();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.export.risk_sheet", "Risk sheet has been successfully exported", 100);
			if (getCssfExportForm().getType() == ExportType.RAW)
				messageHandler.setAsyncCallback(new AsyncCallback("downloadWordReport('" + reportId + "');"));
			else
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

	private long exportData() throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = null;
		OutputStream outputStream = null;
		File workFile = null;
		try {
			serviceTaskFeedback.send(getId(), new MessageHandler("info.preparing.risk_sheet.data", "Preparing risk sheet template", 2));
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet();
			Analysis analysis = daoAnalysis.get(idAnalysis);
			CSSFFilter cssfFilter = cssfExportForm.getFilter();
			ValueFactory factory = new ValueFactory(analysis.getParameters());
			Map<String, Assessment> mappedAssessment = analysis.getAssessments().stream().filter(Assessment::isSelected)
					.collect(Collectors.toMap(Assessment::getKey, Function.identity()));
			List<Estimation> directs = new LinkedList<>(), indirects = new LinkedList<>(), cias = new LinkedList<>();
			int cia = cssfFilter.getCia(), direct = cssfFilter.getDirect(), inderect = cssfFilter.getIndirect();
			workFile = new File(
					String.format("%s/tmp/RISK_SHEET_%d_%s_V%s.xlsx", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			analysis.getRiskProfiles().stream().filter(RiskProfile::isSelected)
					.map(riskProfile -> new Estimation(mappedAssessment.get(Assessment.key(riskProfile.getAsset(), riskProfile.getScenario())), riskProfile, factory))
					.sorted(Estimation.Comparator().reversed()).forEach(estimation -> {
						switch (CSSFSort.findGroup(estimation.getScenario().getType().getName())) {
						case CSSFSort.DIRECT:
							if (direct == -1
									|| direct > -1 && (cssfFilter.getDirect() > 0 || estimation.isCompliant((int) cssfFilter.getImpact(), (int) cssfFilter.getProbability()))) {
								directs.add(estimation);
								if (direct > 0)
									cssfFilter.setDirect(cssfFilter.getDirect() - 1);
							}
							break;
						case CSSFSort.INDIRECT:
							if (inderect == -1
									|| inderect > -1 && (cssfFilter.getIndirect() > 0 || estimation.isCompliant((int) cssfFilter.getImpact(), (int) cssfFilter.getProbability()))) {
								indirects.add(estimation);
								if (inderect > 0)
									cssfFilter.setIndirect(cssfFilter.getIndirect() - 1);
							}
							break;
						default:
							if (cia == -1 || cia > -1 && (cssfFilter.getCia() > 0 || estimation.isCompliant((int) cssfFilter.getImpact(), (int) cssfFilter.getProbability()))) {
								cias.add(estimation);
								if (cia > 0)
									cssfFilter.setCia(cssfFilter.getCia() - 1);
							}
							break;
						}
					});
			mappedAssessment.clear();
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 10));
			addHeader(sheet);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 12));
			addEstimation(sheet, directs, "Direct", 2);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 50));
			if (!indirects.isEmpty())
				addEstimation(sheet, indirects, "Indirect", directs.size() + 3);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 80));
			if (!cias.isEmpty())
				addEstimation(sheet, cias, "CIA", directs.size() + indirects.size() + 3);
			serviceTaskFeedback.send(getId(),new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", 90));
			workbook.write(outputStream = new FileOutputStream(workFile));
			outputStream.flush();
			WordReport report = WordReport.BuildRawRiskSheet(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), daoUser.get(username), workFile.getName(),
					workFile.length(), FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			return report.getId();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (workFile != null && !workFile.delete())
				workFile.deleteOnExit();
		}

	}

	private void addEstimation(XSSFSheet sheet, List<Estimation> estimations, String title, int startIndex) {
		XSSFRow row = getRow(sheet, startIndex++);
		for (int i = 0; i < 28; i++) {
			if (row.getCell(i) == null)
				row.createCell(i, Cell.CELL_TYPE_STRING);
		}
		// setCellString(row, 0, title);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 27));
		for (Estimation estimation : estimations) {
			String scenarioType = estimation.getScenario().getType().getName();
			String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType);
			row = getRow(sheet, startIndex++);
			setCellString(row, 0, estimation.getIdentifier());
			setCellString(row, 1, category);
			setCellString(row, 2, estimation.getScenario().getName());
			setCellString(row, 3, estimation.getOwner());
			printRiskProba(row, 4, estimation.getRawProbaImpact());
			printRiskProba(row, 10, estimation.getNetEvaluation());
			printRiskProba(row, 16, estimation.getExpProbaImpact());
			setCellString(row, 22, estimation.getScenario().getDescription());
			setCellString(row, 23, estimation.getArgumentation());
			setCellString(row, 24, estimation.getAsset().getName());
			setCellString(row, 25, estimation.getRiskTreatment());
			RiskStrategy strategy = estimation.getRiskStrategy();
			if (strategy == null)
				strategy = RiskStrategy.ACCEPT;
			String response = strategy.getNameToLower();
			setCellString(row, 26, getMessage("label.risk_register.strategy." + response, response));
			setCellString(row, 27, estimation.getActionPlan());
		}
	}

	private void addHeader(XSSFSheet sheet) {
		XSSFRow row = sheet.getRow(0), row1 = sheet.getRow(1);
		if (row == null)
			row = sheet.createRow(0);
		if (row1 == null)
			row1 = sheet.createRow(1);
		for (int i = 0; i < 28; i++) {
			if (row.getCell(i) == null)
				row.createCell(i, Cell.CELL_TYPE_STRING);
			if (row1.getCell(i) == null)
				row1.createCell(i, Cell.CELL_TYPE_STRING);
		}

		row.getCell(0).setCellValue(getMessage("report.risk_sheet.risk_id", "Risk ID"));
		row.getCell(1).setCellValue(getMessage("report.risk_sheet.risk_category", "Category"));
		row.getCell(2).setCellValue(getMessage("report.risk_sheet.title", "Title"));
		row.getCell(3).setCellValue(getMessage("report.risk_sheet.risk_owner", "Risk owner"));
		row.getCell(4).setCellValue(getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"));
		row.getCell(10).setCellValue(getMessage("report.risk_sheet.net_evaluation", "Net evaluation"));
		row.getCell(16).setCellValue(getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"));
		row.getCell(22).setCellValue(getMessage("report.risk_sheet.risk_description", "Risk description"));
		row.getCell(23).setCellValue(getMessage("report.risk_sheet.argumentation", "Argumentation"));
		row.getCell(24).setCellValue(getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"));
		row.getCell(25).setCellValue(getMessage("report.risk_sheet.risk_treatment", "Risk treatment"));
		row.getCell(26).setCellValue(getMessage("report.risk_sheet.response", "Response strategy"));
		row.getCell(27).setCellValue(getMessage("report.risk_sheet.action_plan", "Action plan"));
		printEvaluationHeader(row1, 4);
		printEvaluationHeader(row1, 10);
		printEvaluationHeader(row1, 16);
		for (int i = 0; i < 4; i++)
			sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
		for (int i = 4; i < 17; i += 6)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, i, i + 5));
		for (int i = 22; i < 28; i++)
			sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
	}

	private void printRiskProba(XSSFRow row, int index, RiskProbaImpact probaImpact) {
		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();
		setCellInt(row, index++, probaImpact.getProbabilityLevel());
		setCellInt(row, index++, probaImpact.getImpactFin() == null ? 0 : probaImpact.getImpactFin().getLevel());
		setCellInt(row, index++, probaImpact.getImpactLeg() == null ? 0 : probaImpact.getImpactLeg().getLevel());
		setCellInt(row, index++, probaImpact.getImpactOp() == null ? 0 : probaImpact.getImpactOp().getLevel());
		setCellInt(row, index++, probaImpact.getImpactRep() == null ? 0 : probaImpact.getImpactRep().getLevel());
		setCellInt(row, index++, probaImpact.getImportance());
	}

	private void printEvaluationHeader(XSSFRow row, int index) {
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.probability", "Probability (P)"));
		row.getCell(index++).setCellValue(getMessage("label.impact_rep", "Reputation"));
		row.getCell(index++).setCellValue(getMessage("label.impact_op", "Operation"));
		row.getCell(index++).setCellValue(getMessage("label.impact_leg", "Legal"));
		row.getCell(index++).setCellValue(getMessage("label.impact_fin", "Financial"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.importance", "Importance"));
	}

	private long exportReport() throws Exception {
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
			MessageHandler messageHandler = computation.computeRiskRegister(getCssfExportForm().getFilter());
			if (messageHandler != null)
				throw messageHandler.getException();
			ValueFactory factory = computation.getFactory();
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
			serviceTaskFeedback.send(getId(), new MessageHandler("info.preparing.risk_sheet.data", "Preparing risk sheet template", progress += 8));
			Map<String, Assessment> assessments = getCssfExportForm().hasOwner()
					? analysis.getAssessments().stream().filter(assessment -> assessment.isSelected() && getCssfExportForm().getOwner().equals(assessment.getOwner()))
							.collect(Collectors.toMap(Assessment::getKey, Function.identity()))
					: analysis.getAssessments().stream().filter(assessment -> assessment.isSelected()).collect(Collectors.toMap(Assessment::getKey, Function.identity()));
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", progress += 8));
			size = riskProfiles.size();
			boolean isFirst = true;
			riskProfiles.sort(new RiskSheetExportComparator());
			IValue minImpact = factory.findMinImpactByLevel(0), probability = factory.findExp(0);
			for (RiskProfile riskProfile : riskProfiles) {
				Assessment assessment = assessments.get(Assessment.key(riskProfile.getAsset(), riskProfile.getScenario()));
				if (assessment == null)
					continue;
				addRiskSheetHeader(document, riskProfile, isFirst);
				if (isFirst) {
					addField(document, getMessage("report.risk_sheet.risk_owner", "Risk owner"), assessment.getOwner());
					isFirst = false;
				} else
					addField(document, getMessage("report.risk_sheet.risk_owner", "Risk owner"), assessment.getOwner());
				addField(document, getMessage("report.risk_sheet.risk_description", "Risk description"), riskProfile.getScenario().getDescription());
				RiskProbaImpact netImpact = new RiskProbaImpact();
				netImpact.setImpactFin(factory.findImpactFinParameter(assessment.getImpactFin()));
				netImpact.setImpactLeg(factory.findImpactLegParameter(assessment.getImpactLeg()));
				netImpact.setImpactOp(factory.findImpactOpParameter(assessment.getImpactOp()));
				netImpact.setImpactRep(factory.findImpactRepParameter(assessment.getImpactRep()));
				netImpact.setProbability(factory.findProbParameter(assessment.getLikelihood()));
				addTable(document, getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"), riskProfile.getRawProbaImpact(), minImpact.getParameter(), probability.getParameter());
				addField(document, getMessage("report.risk_sheet.argumentation", "Argumentation"), assessment.getComment());
				addField(document, getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"), riskProfile.getAsset().getName());
				addField(document, getMessage("report.risk_sheet.risk_treatment", "Risk treatment"), riskProfile.getRiskTreatment());
				addTable(document, getMessage("report.risk_sheet.net_evaluation", "Net evaluation"), netImpact, minImpact.getParameter(), probability.getParameter());
				RiskStrategy strategy = riskProfile.getRiskStrategy();
				if (strategy == null)
					strategy = RiskStrategy.ACCEPT;
				String response = strategy.getNameToLower();
				addField(document, getMessage("report.risk_sheet.response", "Response strategy"), getMessage("label.risk_register.strategy." + response, response));
				addField(document, getMessage("report.risk_sheet.action_plan", "Action plan"), riskProfile.getActionPlan());
				addTable(document, getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"), riskProfile.getExpProbaImpact(), minImpact.getParameter(), probability.getParameter());
				messageHandler.setProgress((int) (progress + (++index / (double) size) * (max - progress)));
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

	private void addTable(XWPFDocument document, String title, RiskProbaImpact probaImpact, AcronymParameter impact, AcronymParameter probability) {
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
		getCell(row, 0).setText(probaImpact.getProbability((ExtendedParameter) probability).getLevel() + "");
		getCell(row, 1).setText(probaImpact.getImpactRep((ExtendedParameter) impact).getLevel() + "");
		getCell(row, 2).setText(probaImpact.getImpactOp((ExtendedParameter) impact).getLevel() + "");
		getCell(row, 3).setText(probaImpact.getImpactLeg((ExtendedParameter) impact).getLevel() + "");
		getCell(row, 4).setText(probaImpact.getImpactFin((ExtendedParameter) impact).getLevel() + "");
		getCell(row, 5).setText(probaImpact.getImportance() + "");
	}

	private void addTitle(XWPFDocument document, String title) {
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setStyle("TSTitle");
		paragraph.createRun().setText(title);
	}

	private void addField(XWPFDocument document, String title, String content) {
		addTitle(document, title);
		addFieldContent(document, content);
	}

	private void addFieldContent(XWPFDocument document, String content) {
		if (content == null || content.isEmpty())
			return;
		String[] texts = content.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setStyle("BodyOfText");
			paragraph.createRun().setText(texts[i]);
		}
	}

	private void addRiskSheetHeader(XWPFDocument document, RiskProfile riskProfile, boolean isFirst) {
		String scenarioType = riskProfile.getScenario().getType().getName();
		String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType),
				idRisk = riskProfile.getIdentifier() == null ? "" : riskProfile.getIdentifier();
		String text = getMessage("report.risk_sheet.page_title", new Object[] { category, idRisk }, String.format("Category %s - Risk %s", category, idRisk));
		XWPFParagraph paragraph = null;
		if (isFirst)
			paragraph = document.getLastParagraph();
		else
			paragraph = document.createParagraph();

		paragraph.createRun().setText(text);

		paragraph.setStyle("Heading1");

		XWPFTable table = document.createTable(2, 3);
		table.setStyleID("TSTABLERISK");
		XWPFTableRow row = table.getRow(0);
		getCell(row, 0).setText(getMessage("report.risk_sheet.risk_id", "Risk ID"));
		getCell(row, 1).setText(getMessage("report.risk_sheet.risk_category", "Category"));
		getCell(row, 2).setText(getMessage("report.risk_sheet.title", "Title"));
		row = table.getRow(1);
		if (row == null)
			row = table.createRow();
		getCell(row, 0).setText(idRisk);
		getCell(row, 1).setText(category);
		getCell(row, 2).setText(riskProfile.getScenario().getName());
	}

	private XWPFTableCell getCell(XWPFTableRow row, int i) {
		XWPFTableCell cell;
		cell = row.getCell(i);
		if (cell == null)
			cell = row.addNewTableCell();
		return cell;
	}

	private void setCellInt(XSSFRow row, int index, int value) {
		XSSFCell cell = row.getCell(index);
		if (cell == null)
			cell = row.createCell(index, Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(value);
	}

	private void setCellString(XSSFRow row, int index, String value) {
		XSSFCell cell = row.getCell(index);
		if (cell == null)
			cell = row.createCell(index, Cell.CELL_TYPE_STRING);
		cell.setCellValue(value);
	}

	private XSSFRow getRow(XSSFSheet sheet, int index) {
		XSSFRow row = sheet.getRow(index);
		return row == null ? sheet.createRow(index) : row;
	}

	private String getMessage(String code, String defaultMeassge) {
		return messageSource.getMessage(code, null, defaultMeassge, locale);
	}

	private String getMessage(String code, Object[] parameters, String defaultMeassge) {
		return messageSource.getMessage(code, parameters, defaultMeassge, locale);
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

	/**
	 * @return the cssfExportForm
	 */
	public CSSFExportForm getCssfExportForm() {
		return cssfExportForm;
	}

	/**
	 * @param cssfExportForm
	 *            the cssfExportForm to set
	 */
	protected void setCssfExportForm(CSSFExportForm cssfExportForm) {
		this.cssfExportForm = cssfExportForm;
	}

}
