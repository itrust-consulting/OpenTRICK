package lu.itrust.business.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.actionplan.ActionPlanComputation;
import lu.itrust.business.TS.cssf.RiskRegisterComputation;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceRiskRegister;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerAnalysis.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since Oct 22, 2013
 */
@Secured("ROLE_USER")
@Controller
public class ControllerAnalysis {

	@Autowired
	private ServiceActionPlanType serviceActionPlanType;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceActionPlan serviceActionPlan;

	@Autowired
	private ServiceActionPlanSummary serviceActionPlanSummary;

	@Autowired
	private ServiceRiskRegister serviceRiskRegister;

	// ******************************************************************************************************************
	// * Request mappers
	// ******************************************************************************************************************

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Display")
	public String displayAll(Map<String, Object> model, HttpSession session) throws Exception {

		model.put("analyses", serviceAnalysis.loadAll());

		return "analysis/analysis";
	}

	/**
	 * loadAll: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/{analysisId}/Select")
	public String selectAnalysis(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model, HttpSession session, RedirectAttributes attributes) throws Exception {
	
		
		if ((session.getAttribute("selectedAnalysis") != null) && (session.getAttribute("selectedAnalysis") == analysisId)) {
				session.setAttribute("selectedAnalysis",null);
		} else {
		
			Analysis analysis = serviceAnalysis.get(analysisId);
			
			if (analysis != null) {
				session.setAttribute("selectedAnalysis", analysisId);
				
				attributes.addFlashAttribute("success", "Analysis selected for editing!");
				
			} else {
				session.setAttribute("selectedAnalysis", null);
				attributes.addFlashAttribute("error", "Analysis not recognized!");
			}
		
		}	
		
		model.put("analyses", serviceAnalysis.loadAll());
		
		return "analysis/analysis";
	}
	
	/**
	 * editAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Edit/{analysisId}")
	public String requestEditAnalysis(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model, HttpSession session) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		model.put("languages", serviceLanguage.loadAll());

		model.put("customers", serviceCustomer.loadAll());

		if (analysis == null)
			model.put("Error", "label.error.analysis.notExist");
		else
			model.put("analysis", analysis);

		return "analysis/editAnalysis";
	}

	/**
	 * saveAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param analysis
	 * @param result
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Edit/{analysisId}/Save")
	public String performEditAnalysis(@PathVariable("analysisId") Integer analysisId, @ModelAttribute("analysis") @Valid Analysis analysis, RedirectAttributes attributes, 
			BindingResult result) throws Exception {

		analysis.setId(Integer.valueOf(result.getFieldValue("id").toString()));
		
		analysis.setIdentifier(result.getFieldValue("identifier").toString());
		
		analysis.setVersion(result.getFieldValue("version").toString());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

		Timestamp timestamp = new Timestamp(format.parse(result.getFieldValue("creationDate").toString()).getTime());
		
		analysis.setCreationDate(timestamp);
		
		serviceAnalysis.saveOrUpdate(analysis);

		attributes.addFlashAttribute("success", "Analysis updated");

		return "redirect:/Analysis/Display";
	}

	/**
	 * deleteAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Delete/{analysisId}")
	public String deleteAnalysis(@PathVariable("analysisId") Integer analysisId, HttpSession session) throws Exception {
		serviceAnalysis.remove(analysisId);
		return "redirect:/Analysis/Display";
	}

	/**
	 * computeRiskRegister: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/{analysisId}/compute/riskRegister")
	public String computeRiskRegister(@PathVariable("analysisId") Integer analysisId, RedirectAttributes attributes) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		if (analysis == null) {
			return "redirect:/index";
		}

		MessageHandler handler = computeRiskRegisters(analysis);

		if (handler != null) {
			attributes.addFlashAttribute("error", handler.getException().getMessage());
		}
		return "redirect:/analysis/customer/" + analysis.getCustomer().getId();
	}

	/**
	 * computeActionPlan: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param attributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/{analysisId}/compute/actionPlan")
	public String computeActionPlan(@PathVariable("analysisId") Integer analysisId, RedirectAttributes attributes) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		if (analysis == null) {
			return "redirect:/index";
		}

		initAnalysis(analysis);

		MessageHandler handler = computeActionPlan(analysis);

		if (handler != null) {
			attributes.addFlashAttribute("error", handler.getException().getMessage());
		}

		return "redirect:/analysis/customer/" + analysis.getCustomer().getId();
	}

	// ******************************************************************************************************************
	// * Actions
	// ******************************************************************************************************************

	/**
	 * initAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 */
	private void initAnalysis(Analysis analysis) {
		Hibernate.initialize(analysis);
		Hibernate.initialize(analysis.getAssets());
		Hibernate.initialize(analysis.getActionPlans());
		Hibernate.initialize(analysis.getAnalysisNorms());
		Hibernate.initialize(analysis.getAssessments());
		Hibernate.initialize(analysis.getScenarios());
		Hibernate.initialize(analysis.getHistories());
		Hibernate.initialize(analysis.getItemInformations());
		Hibernate.initialize(analysis.getLanguage());
		Hibernate.initialize(analysis.getParameters());
		Hibernate.initialize(analysis.getRiskInformations());
		Hibernate.initialize(analysis.getSummaries());
		Hibernate.initialize(analysis.getUsedPhases());
		Hibernate.initialize(analysis.getRiskRegisters());
	}

	/**
	 * computeActionPlan: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private MessageHandler computeActionPlan(Analysis analysis) throws Exception {

		deleteActionPlan(analysis);

		// ****************************************************************
		// Calculate Action Plan - BEGIN
		// ****************************************************************
		ActionPlanComputation actionPlanComputation = new ActionPlanComputation(serviceActionPlanType, analysis);

		MessageHandler handler = actionPlanComputation.calculateActionPlans();

		if (handler == null) {

			// System.out.println("Saving Action Plans...");

			serviceAnalysis.saveOrUpdate(actionPlanComputation.getAnalysis());

			// System.out.println("Computing Action Plans Done!");

			// ****************************************************************
			// * Calculate Action Plan - END
			// ****************************************************************

			// ****************************************************************
			// Calculate RiskRegister - BEGIN
			// ****************************************************************
		}

		return handler;

	}

	/**
	 * deleteActionPlan: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteActionPlan(Analysis analysis) throws Exception {

		while (!analysis.getSummaries().isEmpty())
			serviceActionPlanSummary.remove(analysis.getSummaries().remove(analysis.getSummaries().size() - 1));

		while (!analysis.getActionPlans().isEmpty())
			serviceActionPlan.delete(analysis.getActionPlans().remove(analysis.getActionPlans().size() - 1));
	}

	/**
	 * deleteRiskRegister: <br>
	 * Description
	 * 
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteRiskRegister(Analysis analysis) throws Exception {

		while (!analysis.getRiskRegisters().isEmpty())
			serviceRiskRegister.remove(analysis.getRiskRegisters().remove(analysis.getRiskRegisters().size() - 1));
	}

	/**
	 * computeRiskRegisters: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 */
	private MessageHandler computeRiskRegisters(Analysis analysis) throws Exception {

		deleteRiskRegister(analysis);

		RiskRegisterComputation registerComputation = new RiskRegisterComputation(analysis);

		MessageHandler handler = registerComputation.computeRiskRegister();

		if (handler == null) {
			System.out.println("Saving Risk Register...");
			serviceAnalysis.saveOrUpdate(registerComputation.getAnalysis());
			System.out.println("Saving Risk Register done");
		}
		return handler;
	}

	// ******************************************************************************************************************
	// * Setters
	// ******************************************************************************************************************

	/**
	 * getServiceAnalysis: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceAnalysis getServiceAnalysis() {
		return serviceAnalysis;
	}

	/**
	 * setServiceAnalysis: <br>
	 * Description
	 * 
	 * @param serviceAnalysis
	 */
	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	/**
	 * getServiceCustomer: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceCustomer getServiceCustomer() {
		return serviceCustomer;
	}

	/**
	 * setServiceCustomer: <br>
	 * Description
	 * 
	 * @param serviceCustomer
	 */
	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}

	/**
	 * getServiceLanguage: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceLanguage getServiceLanguage() {
		return serviceLanguage;
	}

	/**
	 * setServiceLanguage: <br>
	 * Description
	 * 
	 * @param serviceLanguage
	 */
	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	/**
	 * setServiceActionPlanType: <br>
	 * Description
	 * 
	 * @param serviceActionPlanType
	 */
	public void setServiceActionPlanType(ServiceActionPlanType serviceActionPlanType) {
		this.serviceActionPlanType = serviceActionPlanType;
	}

	/**
	 * getServiceActionPlan: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceActionPlan getServiceActionPlan() {
		return serviceActionPlan;
	}

	/**
	 * setServiceActionPlan: <br>
	 * Description
	 * 
	 * @param serviceActionPlan
	 */
	public void setServiceActionPlan(ServiceActionPlan serviceActionPlan) {
		this.serviceActionPlan = serviceActionPlan;
	}

	/**
	 * getServiceActionPlanSummary: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceActionPlanSummary getServiceActionPlanSummary() {
		return serviceActionPlanSummary;
	}

	/**
	 * setServiceActionPlanSummary: <br>
	 * Description
	 * 
	 * @param serviceActionPlanSummary
	 */
	public void setServiceActionPlanSummary(ServiceActionPlanSummary serviceActionPlanSummary) {
		this.serviceActionPlanSummary = serviceActionPlanSummary;
	}

	/**
	 * getServiceRiskRegister: <br>
	 * Description
	 * 
	 * @return
	 */
	public ServiceRiskRegister getServiceRiskRegister() {
		return serviceRiskRegister;
	}

	/**
	 * setServiceRiskRegister: <br>
	 * Description
	 * 
	 * @param serviceRiskRegister
	 */
	public void setServiceRiskRegister(ServiceRiskRegister serviceRiskRegister) {
		this.serviceRiskRegister = serviceRiskRegister;
	}
}