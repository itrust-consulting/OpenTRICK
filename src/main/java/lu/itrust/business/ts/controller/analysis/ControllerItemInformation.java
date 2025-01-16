package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.util.Collections;
import java.util.Locale;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.model.iteminformation.helper.ComparatorItemInformation;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Item-information")
public class ControllerItemInformation {

    @Autowired
    private ServiceAnalysis serviceAnalysis;

    @RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String section( Model model, HttpSession session, Principal principal, Locale locale) {
		var analysis  = serviceAnalysis.get( (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
        Collections.sort(analysis.getItemInformations(), new ComparatorItemInformation());
		model.addAttribute("itemInformations", analysis.getItemInformations());
		return "jsp/analyses/single/components/itemInformation";
	}

}
