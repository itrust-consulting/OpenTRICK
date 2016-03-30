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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.helper.ParameterConvertor;
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

	private DAOWordReport daoWordReport;

	private DAOUser daoUser;

	public WorkerExportRiskSheet(WorkersPoolManager poolManager, SessionFactory sessionFactory, ServiceTaskFeedback serviceTaskFeedback, String rootPath, Integer analysisId,
			String username) {
		super(poolManager, sessionFactory);
		setUsername(username);
		setIdAnalysis(analysisId);
		setRootPath(rootPath);
		setServiceTaskFeedback(serviceTaskFeedback);
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
			processing();
			session.getTransaction().commit();
		} /*
			 * catch (InterruptedException e) { try { setCanceled(true); if
			 * (session != null &&
			 * session.getTransaction().getStatus().canRollback())
			 * session.getTransaction().rollback(); } catch (HibernateException
			 * e1) { TrickLogManager.Persist(e1); } }
			 */catch (Exception e) {
			if (session != null) {
				try {
					if (session.beginTransaction().getStatus().canRollback())
						session.beginTransaction().rollback();
				} catch (Exception e1) {
				}
			}
			if (e instanceof TrickException) {
			} else {
			}
			TrickLogManager.Persist(e);
		} finally {
			if (session != null)
				session.close();
		}

	}

	@Override
	public void start() {
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

	private void processing() throws InvalidFormatException, IOException {
		User user = daoUser.get(username);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		if (analysis.getRiskRegisters().isEmpty())
			throw new TrickException("error.analysis.risk_profile.empty", "Please compute risk register and try again");
		if (analysis.getRiskProfiles().isEmpty())
			throw new TrickException("error.risk_profile.empty", "No risk sheet");
		InputStream inputStream = null;
		XWPFDocument document = null;
		OutputStream outputStream = null;
		try {
			File workFile = new File(
					String.format("%s/tmp/RISK_SHEET_%d_%s_V%s.docm", rootPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
			File doctemplate = new File(String.format("%s/data/%s.dotm", rootPath, "Risk_sheet_fr"));
			System.out.println(doctemplate.getAbsoluteFile());
			OPCPackage opcPackage = OPCPackage.open(doctemplate.getAbsoluteFile());
			opcPackage.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
			opcPackage.save(workFile);
			document = new XWPFDocument(inputStream = new FileInputStream(workFile));
			Map<String, Assessment> assessments = analysis.getAssessments().stream().filter(assessment -> assessment.isSelected())
					.collect(Collectors.toMap(Assessment::getKey, Function.identity()));
			List<ExtendedParameter> probabilities = new ArrayList<>(11), impacts = new ArrayList<>(11);
			analysis.groupExtended(probabilities, impacts);
			ParameterConvertor convertor = new ParameterConvertor(impacts, probabilities);
			List<RiskProfile> riskProfiles = analysis.getRiskProfiles().stream().filter(riskProfile -> riskProfile.isSelected()).collect(Collectors.toList());
			boolean isFirst = true;
			for (RiskProfile riskProfile : riskProfiles) {

				isFirst = addRiskSheetHeader(document, riskProfile, isFirst);

				Assessment assessment = assessments.get(Assessment.key(riskProfile.getAsset(), riskProfile.getScenario()));
				addField(document, getMessage("report.risk_sheet.risk_description", "Risk description"), riskProfile.getScenario().getDescription());
				addTitle(document, getMessage("report.risk_sheet.risk_description", "Risk description"));
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
				String response = riskProfile.getRiskStrategy().getNameToLower();
				addField(document, getMessage("report.risk_sheet.response", "Response strategy"), getMessage("label.risk_register.strategy." + response, response));
				addField(document, getMessage("report.risk_sheet.action_plan", "Plan d'action"), riskProfile.getActionPlan());
				addTable(document, getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"), riskProfile.getExpProbaImpact(), impacts.get(0), probabilities.get(0));
			}

			document.write(outputStream = new FileOutputStream(workFile));
			outputStream.flush();

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
		}
	}

	private void addTable(XWPFDocument document, String title, RiskProbaImpact probaImpact, ExtendedParameter impact, ExtendedParameter probability) {
		addTitle(document, title);
		XWPFTable table = document.createTable(3, 6);
		table.setStyleID("Tableitrust1");
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
		paragraph.setStyle("Titre2ssNo");
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
			table = document.insertNewTbl(document.createParagraph().getCTP().newCursor());
		}
		table.setStyleID("TSRISKTABLE");
		XWPFTableRow row = table.getRow(0);
		getCell(row, 0).setText(getMessage("report.risk_sheet.risk_id", "Risk ID"));
		getCell(row, 1).setText(riskProfile.getIdentifier() + "");
		getCell(row, 2).setText(getMessage("report.risk_sheet.risk_type", "Type"));
		String scenarioType = riskProfile.getScenario().getType().getName();
		getCell(row, 3).setText(getMessage("label.scenario.type." + scenarioType.replace("-", "_"), scenarioType));
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

	private String getMessage(String string, String string2) {
		return string2;
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

}
