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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellType;
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
import org.springframework.util.StringUtils;

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
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.CSSFExportForm;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.general.WordReport;
import lu.itrust.business.TS.model.general.helper.ExportType;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
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

	private DAOWordReport daoWordReport;

	private MessageSource messageSource;

	private DAOAnalysis daoAnalysis;

	private DAOUser daoUser;

	private CSSFExportForm cssfExportForm;

	private boolean showRawColumn = true;

	private String alpha2 = "EN";

	private DateFormat dateFormat;

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
			locale = new Locale(analysis.getLanguage().getAlpha2());
			if (locale.getLanguage().equals("fr"))
				dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			else
				dateFormat = new SimpleDateFormat("MM-dd-yyyy");

			showRawColumn = analysis.getSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);

			List<ScaleType> scaleTypes = analysis.getImpacts();
			CSSFFilter cssfFilter = cssfExportForm.getFilter();
			ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
			List<Estimation> directs = new LinkedList<>(), indirects = new LinkedList<>(), cias = new LinkedList<>();
			workFile = new File(
					String.format("%s/tmp/RISK_SHEET_%d_%s_V%s.xlsx", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			Estimation.GenerateEstimation(analysis, cssfFilter, valueFactory, directs, indirects, cias);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 10));
			addHeader(sheet, scaleTypes);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 12));
			addEstimation(sheet, directs, scaleTypes, "Direct", 2);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 50));
			if (!indirects.isEmpty())
				addEstimation(sheet, indirects, scaleTypes, "Indirect", directs.size() + 3);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 80));
			if (!cias.isEmpty())
				addEstimation(sheet, cias, scaleTypes, "CIA", directs.size() + indirects.size() + 3);
			serviceTaskFeedback.send(getId(), new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", 90));
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

	private void addEstimation(XSSFSheet sheet, List<Estimation> estimations, List<ScaleType> types, String title, int startIndex) {
		XSSFRow row = getRow(sheet, startIndex++);
		int size = 16 + types.size() * 3;
		for (int i = 0; i < size; i++) {
			if (row.getCell(i) == null)
				row.createCell(i, CellType.STRING);
		}
		// setCellString(row, 0, title);
		sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, size - 1));
		for (Estimation estimation : estimations) {
			String scenarioType = estimation.getScenario().getType().getName();
			String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType);
			int index = 0;
			row = getRow(sheet, startIndex++);
			setCellString(row, index++, estimation.getIdentifier());
			setCellString(row, index++, category);
			setCellString(row, index++, estimation.getScenario().getName());
			setCellString(row, index++, estimation.getOwner());
			if (showRawColumn) {
				printRiskProba(row, index++, types, estimation.getRawProbaImpact());
				index += types.size() + 1;
			}
			printRiskProba(row, index++, types, estimation.getNetEvaluation());
			index += types.size() + 1;
			printRiskProba(row, index++, types, estimation.getExpProbaImpact());
			index += types.size() + 1;
			setCellString(row, index++, estimation.getScenario().getDescription());
			setCellString(row, index++, estimation.getArgumentation());
			setCellString(row, index++, estimation.getAsset().getName());
			setCellString(row, index++, estimation.getRiskTreatment());
			RiskStrategy strategy = estimation.getRiskStrategy();
			if (strategy == null)
				strategy = RiskStrategy.ACCEPT;
			String response = strategy.getNameToLower();
			setCellString(row, index++, getMessage("label.risk_register.strategy." + response, response));
			List<String> actionPlan = new LinkedList<>();
			for (Measure measure : estimation.getRiskProfile().getMeasures()) {
				MeasureDescription description = measure.getMeasureDescription();
				MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(locale.getLanguage());
				String date = dateFormat.format(measure.getPhase().getEndDate());
				actionPlan.add(getMessage("report.risk_profile.action_plan.measure",
						new Object[] { description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain(), date },
						String.format("%s: %s, %s; %s", description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain(), date)));
			}

			if (actionPlan.isEmpty())
				setCellString(row, index++, estimation.getActionPlan() == null || estimation.getActionPlan().isEmpty() ? "" : estimation.getActionPlan());
			else {
				if (!(estimation.getActionPlan() == null || estimation.getActionPlan().isEmpty()))
					actionPlan.add(estimation.getActionPlan());
				setCellString(row, index++, String.join("\n\r", actionPlan));
			}
		}
	}

	private void addHeader(XSSFSheet sheet, List<ScaleType> types) {
		int rowCount = showRawColumn ? types.size() * 3 + 16 : types.size() * 2 + 14;
		XSSFRow row = sheet.getRow(0), row1 = sheet.getRow(1);
		if (row == null)
			row = sheet.createRow(0);
		if (row1 == null)
			row1 = sheet.createRow(1);
		for (int i = 0; i < rowCount; i++) {
			if (row.getCell(i) == null)
				row.createCell(i, CellType.STRING);
			if (row1.getCell(i) == null)
				row1.createCell(i, CellType.STRING);
		}

		int step = 2, size = types.size() + step, netIndex = (showRawColumn ? 6 + types.size() : 4), expIndex = netIndex + types.size() + step,
				index = expIndex + types.size() + step;
		row.getCell(0).setCellValue(getMessage("report.risk_sheet.risk_id", "Risk ID"));
		row.getCell(1).setCellValue(getMessage("report.risk_sheet.risk_category", "Category"));
		row.getCell(2).setCellValue(getMessage("report.risk_sheet.title", "Title"));
		row.getCell(3).setCellValue(getMessage("report.risk_sheet.risk_owner", "Risk owner"));
		if (showRawColumn)
			row.getCell(4).setCellValue(getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"));
		row.getCell(netIndex).setCellValue(getMessage("report.risk_sheet.net_evaluation", "Net evaluation"));
		row.getCell(expIndex).setCellValue(getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.risk_description", "Risk description"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.argumentation", "Argumentation"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.risk_treatment", "Risk treatment"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.response", "Response strategy"));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.action_plan", "Action plan"));
		if (showRawColumn)
			printEvaluationHeader(row1, types, 4);
		printEvaluationHeader(row1, types, netIndex);
		printEvaluationHeader(row1, types, expIndex);
		for (int i = 0; i < 4; i++)
			sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
		for (int i = 4; i <= expIndex; i += size)
			sheet.addMergedRegion(new CellRangeAddress(0, 0, i, i + size - 1));
		for (int i = expIndex + types.size() + 2; i < index; i++)
			sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
	}

	private void printRiskProba(XSSFRow row, int index, List<ScaleType> scaleTypes, RiskProbaImpact probaImpact) {
		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();
		setCellInt(row, index++, probaImpact.getProbabilityLevel());
		for (ScaleType scaleType : scaleTypes) {
			IImpactParameter parameter = probaImpact.get(scaleType.getName());
			setCellInt(row, index++, parameter == null ? 0 : parameter.getLevel());
		}

		setCellInt(row, index++, probaImpact.getImportance());
	}

	private void printEvaluationHeader(XSSFRow row, List<ScaleType> types, int index) {
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.probability", "Probability (P)"));
		for (ScaleType scaleType : types)
			row.getCell(index++).setCellValue(getMessage("label.impact." + scaleType.getName().toLowerCase(),
					scaleType.getTranslations().containsKey(alpha2) ? scaleType.getTranslations().get(alpha2).getName() : scaleType.getDisplayName()));
		row.getCell(index++).setCellValue(getMessage("report.risk_sheet.importance", "Importance"));
	}

	private long exportReport() throws Exception {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		int progress = 2, max = 60, index = 0;
		setLocale(new Locale(analysis.getLanguage().getAlpha2()));
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		InputStream inputStream = null;
		XWPFDocument document = null;
		OutputStream outputStream = null;
		File workFile = null;
		MessageHandler messageHandler = null;
		boolean isFirst = true;
		try {
			showRawColumn = analysis.getSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.risk_register.compute", "Computing risk register", progress));
			List<Estimation> estimations = Estimation.GenerateEstimation(analysis, new ValueFactory(analysis.getParameters()), cssfExportForm.getFilter(),
					Estimation.IdComparator());
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.loading.risk_sheet.template", "Loading risk sheet template", progress += 5));
			workFile = new File(
					String.format("%s/tmp/RISK_SHEET_%d_%s_V%s.docm", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/%s.dotm", rootPath, analysis.getLanguage().getAlpha2().equalsIgnoreCase("fr") ? FR_TEMPLATE : ENG_TEMPLATE));
			OPCPackage opcPackage = OPCPackage.open(doctemplate.getAbsoluteFile());
			opcPackage.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
			opcPackage.save(workFile);
			document = new XWPFDocument(inputStream = new FileInputStream(workFile));
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.preparing.risk_sheet.data", "Preparing risk sheet template", progress += 8));
			serviceTaskFeedback.send(getId(), messageHandler = new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", progress += 8));
			if (cssfExportForm.hasOwner())
				estimations.removeIf(estimation -> !cssfExportForm.getOwner().equals(estimation.getOwner()));
			List<ScaleType> types = analysis.getImpacts();
			for (Estimation estimation : estimations) {
				RiskProfile riskProfile = estimation.getRiskProfile();
				addRiskSheetHeader(document, estimation.getRiskProfile(), isFirst);
				if (isFirst) {
					addField(document, getMessage("report.risk_sheet.risk_owner", "Risk owner"), estimation.getOwner());
					isFirst = false;
				} else
					addField(document, getMessage("report.risk_sheet.risk_owner", "Risk owner"), estimation.getOwner());
				addField(document, getMessage("report.risk_sheet.risk_description", "Risk description"), riskProfile.getScenario().getDescription());
				if (showRawColumn)
					addTable(document, getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"), estimation.getRawProbaImpact(), types);
				addField(document, getMessage("report.risk_sheet.argumentation", "Argumentation"), estimation.getArgumentation());
				addField(document, getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"), riskProfile.getAsset().getName());
				addField(document, getMessage("report.risk_sheet.risk_treatment", "Risk treatment"), estimation.getRiskTreatment());
				addTable(document, getMessage("report.risk_sheet.net_evaluation", "Net evaluation"), estimation.getNetEvaluation(), types);
				RiskStrategy strategy = riskProfile.getRiskStrategy();
				if (strategy == null)
					strategy = RiskStrategy.ACCEPT;
				String response = strategy.getNameToLower();
				addField(document, getMessage("report.risk_sheet.response", "Response strategy"), getMessage("label.risk_register.strategy." + response, response));
				addTable(document, getMessage("report.risk_sheet.action_plan", "Action plan"), riskProfile);
				addTable(document, getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"), riskProfile.getExpProbaImpact(), types);
				messageHandler.setProgress((int) (progress + (++index / (double) estimations.size()) * (max - progress)));
			}
			serviceTaskFeedback.send(getId(), new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", max));
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

	private void addTable(XWPFDocument document, String title, RiskProfile riskProfile) {
		addTitle(document, title);
		if (!riskProfile.getMeasures().isEmpty()) {
			XWPFTable table = document.createTable(riskProfile.getMeasures().size() + 1, 4);
			table.setStyleID("TSTABLEMEASURE");
			XWPFTableRow row = table.getRow(0);
			getCell(row, 0).setText(getMessage("report.risk_sheet.measure.standard", "Standard"));
			getCell(row, 1).setText(getMessage("report.risk_sheet.measure.reference", "Reference"));
			getCell(row, 2).setText(getMessage("report.risk_sheet.measure.domain", "Domain"));
			getCell(row, 3).setText(getMessage("report.risk_sheet.measure.due_date", "Due date"));
			int index = 1;
			for (Measure measure : riskProfile.getMeasures()) {
				row = table.getRow(index++);
				if (row == null)
					row = table.createRow();
				MeasureDescription description = measure.getMeasureDescription();
				MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(locale.getLanguage());
				addFieldContent(getCell(row, 0), description.getStandard().getLabel());
				addFieldContent(getCell(row, 1), description.getReference());
				addFieldContent(getCell(row, 2), descriptionText.getDomain());
				addFieldContent(getCell(row, 3), dateFormat.format(measure.getPhase().getEndDate()));
			}
		}

		if (!StringUtils.isEmpty(riskProfile.getActionPlan()))
			addFieldContent(document, riskProfile.getActionPlan());

	}

	private void addTable(XWPFDocument document, String title, RiskProbaImpact probaImpact, List<ScaleType> types) {
		addTitle(document, title);
		XWPFTable table = document.createTable(3, 2 + types.size());
		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();
		table.setStyleID("TSTABLEEVALUATION");
		XWPFTableRow row = table.getRow(0);
		getCell(row, 0).setText(getMessage("report.risk_sheet.probability", "Probability (P)"));
		getCell(row, 1).setText(getMessage("report.risk_sheet.impact", "Impact (i)"));
		getCell(row, types.size() + 1).setText(getMessage("report.risk_sheet.importance", "Importance"));
		row = table.getRow(1);
		int index = 1;
		for (ScaleType scaleType : types)
			getCell(row, index++).setText(getMessage("label.impact." + scaleType.getName().toLowerCase(),
					scaleType.getTranslations().containsKey(alpha2) ? scaleType.getTranslations().get(alpha2).getName() : scaleType.getDisplayName()));
		row = table.getRow(2);
		if (probaImpact.getProbability() == null)
			getCell(row, 0).setText("0");
		else
			getCell(row, 0).setText(probaImpact.getProbability().getLevel().toString());

		index = 1;
		for (ScaleType scaleType : types) {
			IImpactParameter impact = probaImpact.get(scaleType.getName());
			getCell(row, index++).setText(impact == null ? "0" : impact.getLevel() + "");
		}
		getCell(row, index).setText(probaImpact.getImportance() + "");
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

	private void addFieldContent(XWPFTableCell cell, String content) {
		if (content == null || content.isEmpty())
			return;
		String[] texts = content.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			XWPFParagraph paragraph = i == 0 ? cell.getParagraphs().get(i) : cell.addParagraph();
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
			cell = row.createCell(index, CellType.NUMERIC);
		cell.setCellValue(value);
	}

	private void setCellString(XSSFRow row, int index, String value) {
		XSSFCell cell = row.getCell(index);
		if (cell == null)
			cell = row.createCell(index, CellType.STRING);
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
