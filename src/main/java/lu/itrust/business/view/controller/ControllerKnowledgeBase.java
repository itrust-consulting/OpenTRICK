package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Map;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceNorm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
	private ServiceNorm serviceNorm;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@RequestMapping
	public String displayKowledgeBase(Map<String, Object> model, Principal principal) throws Exception {
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));
		model.put("languages", serviceLanguage.loadAll());
		model.put("norms", serviceNorm.loadAll());
		model.put("norms", serviceNorm.loadAll());
		model.put("KowledgeBaseView", true);
		model.put("analyses", serviceAnalysis.loadProfiles());
		return "knowledgebase/knowledgebase";
	}

	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}

	public void setServiceNorm(ServiceNorm serviceNorm) {
		this.serviceNorm = serviceNorm;
	}
}
