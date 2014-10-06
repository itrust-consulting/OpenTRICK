package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceStandard;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ControllerKnowledgeBase: <br>
 * This Controller contains all actions of the knowledgebase page. <br>
 * This includes the management of: <br>
 * - Languages - Customers - Measures
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 10, 2013
 */

@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@RequestMapping("/KnowledgeBase")
@Controller
public class ControllerKnowledgeBase {

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUser serviceUser;

	@RequestMapping
	public String displayKnowledgeBase(Map<String, Object> model, Principal principal) throws Exception {
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		model.put("languages", serviceLanguage.getAll());
		model.put("standards", serviceStandard.getAll());
		model.put("analyses", serviceAnalysis.getAllProfiles());
		return "knowledgebase/knowledgebase";
	}

	@RequestMapping("/Analysis/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		model.addAttribute("analyses", serviceAnalysis.getAllProfiles());
		model.addAttribute("login", principal.getName());
		model.addAttribute("KowledgeBaseView", true);
		return "analysis/analyses";
	}

	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}

	public void setServiceStandard(ServiceStandard serviceStandard) {
		this.serviceStandard = serviceStandard;
	}
}
