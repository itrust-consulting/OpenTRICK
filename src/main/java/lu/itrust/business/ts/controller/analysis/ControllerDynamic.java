package lu.itrust.business.ts.controller.analysis;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.component.ChartGenerator;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.helper.chartJS.model.Chart;

/**
 * Controller for functionality related to dynamic risk analysis.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jul 1, 2015
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Dynamic")
public class ControllerDynamic {

	@Autowired
	private ChartGenerator chartGenerator;


	@RequestMapping(value = "/Chart/ParameterEvolution", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart chartParameterEvolution(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		return chartGenerator.dynamicParameterEvolution((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS),  locale);
	}

	@RequestMapping(value = "/Chart/AleEvolution", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Chart chartAleEvolutionByAssetType(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		return chartGenerator.aleEvolutionOfAllAssetTypes((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS),locale);
	}

	@RequestMapping(value = "/Chart/AleEvolutionByAssetType", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody List<Chart> chartAleEvolutionByScenario(HttpSession session, Model model, Principal principal, Locale locale) throws Exception {
		return chartGenerator.allAleEvolutionsofAllScenarios((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS),locale);
	}
}