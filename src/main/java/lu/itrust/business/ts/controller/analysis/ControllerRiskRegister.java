package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.asynchronousWorkers.WorkerExportRiskSheet;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.assessment.helper.Estimation;
import lu.itrust.business.ts.model.cssf.helper.ColorManager;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;

/**
 * ControllerRiskRegister.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 17, 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/RiskRegister")
@Controller
public class ControllerRiskRegister {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	/**
	 * showRiskRegister: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping
	public String showRiskRegister(HttpSession session, Map<String, Object> model, Principal principal) throws Exception {
		// retrieve analysis ID
		Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		// load all actionplans from the selected analysis
		// prepare model
		ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
		model.put("estimations", Estimation.GenerateEstimation(analysis, valueFactory, Estimation.IdComparator()));
		model.put("type", analysis.getType());
		model.put("riskregister", analysis.getRiskRegisters());
		model.put("valueFactory", valueFactory);
		model.put("colorManager", new ColorManager(analysis.getRiskAcceptanceParameters()));
		model.put("language", analysis.getLanguage().getAlpha2());
		loadAnalysisSettings(model, analysis);
		// return view
		return "jsp/analyses/single/components/riskRegister/home";
	}

	/**
	 * section: <br>
	 * reload the section of the risk register
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(Map<String, Object> model, HttpSession session, Principal principal) throws Exception {
		return showRiskRegister(session, model, principal);

	}

	// *****************************************************************
	// * compute risk register
	// *****************************************************************
	private void loadAnalysisSettings(Map<String, Object> model, Analysis analysis) {
		AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN, hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT;
		model.put("showHiddenComment", analysis.findSetting(hiddenCommentSetting));
		model.put("showRawColumn", analysis.findSetting(rawSetting));
		model.put("showDynamicAnalysis", analysis.findSetting(AnalysisSetting.ALLOW_DYNAMIC_ANALYSIS));
	}

	@Value("${app.settings.excel.default.table.style}")
	public void setRiskRegisterExcelTable(String table) {
		WorkerExportRiskSheet.DEFAULT_EXCEL_TABLE = table;
	}

	@Value("${app.settings.excel.header.footer.sheet.name}")
	public void setHeaderFooterSheetName(String name) {
		WorkerExportRiskSheet.EXCEL_HEADER_FOOTER_SHEET_NAME = name;
	}
	

	
}