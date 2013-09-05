/**
 * 
 */
package lu.itrust.business.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
 * @author oensuifudine
 * 
 */
@Secured("ROLE_USER")
@RequestMapping("/analysis")
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

	/**
	 * @return the serviceAnalysis
	 */
	public ServiceAnalysis getServiceAnalysis() {
		return serviceAnalysis;
	}

	/**
	 * @param serviceAnalysis
	 *            the serviceAnalysis to set
	 */
	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	/**
	 * @return the serviceCustomer
	 */
	public ServiceCustomer getServiceCustomer() {
		return serviceCustomer;
	}

	/**
	 * @param serviceCustomer
	 *            the serviceCustomer to set
	 */
	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}

	/**
	 * @return the serviceLanguage
	 */
	public ServiceLanguage getServiceLanguage() {
		return serviceLanguage;
	}

	/**
	 * @param serviceLanguage
	 *            the serviceLanguage to set
	 */
	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	@RequestMapping("/all")
	public String loadAll(Map<String, Object> model, HttpSession session)
			throws Exception {

		model.put("analyzes", serviceAnalysis.loadAll());

		return "analysis";
	}
	
	@RequestMapping("/customers")
	public String customersAnalysis(Map<String, Object> model, HttpSession session) throws Exception {
		model.put("customers", serviceCustomer.loadAll());
		return "analysisByCustomer";
	}

	@RequestMapping("/customer/{customerId}")
	public String customerAnalysis(
			@PathVariable("customerId") Integer customerId,
			Map<String, Object> model, HttpSession session) throws Exception {

		Customer customer = (Customer) session.getAttribute("customer");

		if (customer == null || customer.getId() != customerId)
			customer = serviceCustomer.get(customerId);

		model.put("analyzes", serviceAnalysis.loadAllFromCustomer(customer));

		return "analysis";
	}

	/**
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteActionPlan(Analysis analysis) throws Exception {

		while (!analysis.getSummaries().isEmpty())
			serviceActionPlanSummary.remove(analysis.getSummaries().remove(
					analysis.getSummaries().size() - 1));

		while (!analysis.getActionPlans().isEmpty())
			serviceActionPlan.delete(analysis.getActionPlans().remove(
					analysis.getActionPlans().size() - 1));
	}

	/**
	 * @param analysis
	 * @throws Exception
	 */
	private void deleteRiskRegister(Analysis analysis) throws Exception {

		while (!analysis.getRiskRegisters().isEmpty())
			serviceRiskRegister.remove(analysis.getRiskRegisters().remove(
					analysis.getRiskRegisters().size() - 1));
	}

	private MessageHandler computeRiskRegisters(Analysis analysis)
			throws Exception {

		deleteRiskRegister(analysis);

		RiskRegisterComputation registerComputation = new RiskRegisterComputation(
				analysis);

		MessageHandler handler = registerComputation.computeRiskRegister();

		if (handler == null) {
			System.out.println("Saving Risk Register...");
			serviceAnalysis.saveOrUpdate(registerComputation.getAnalysis());
			System.out.println("Saving Risk Register done");
		}
		return handler;
	}

	private MessageHandler computeActionPlan(Analysis analysis)
			throws Exception {

		deleteActionPlan(analysis);

		// ****************************************************************
		// Calculate Action Plan - BEGIN
		// ****************************************************************
		ActionPlanComputation actionPlanComputation = new ActionPlanComputation(
				serviceActionPlanType, analysis);

		MessageHandler handler = actionPlanComputation.calculateActionPlans();

		if (handler == null) {

			//System.out.println("Saving Action Plans...");

			serviceAnalysis.saveOrUpdate(actionPlanComputation.getAnalysis());

			//System.out.println("Computing Action Plans Done!");

			// ****************************************************************
			// * Calculate Action Plan - END
			// ****************************************************************

			// ****************************************************************
			// Calculate RiskRegister - BEGIN
			// ****************************************************************
		}

		return handler;

	}

	@RequestMapping("/{analysisId}/compute/riskRegister")
	public String computeRiskRegister(
			@PathVariable("analysisId") Integer analysisId, RedirectAttributes attributes) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		if (analysis == null)
			return "redirect:/index";

		MessageHandler handler =  computeRiskRegisters(analysis);
		
		if(handler !=null )
			attributes.addFlashAttribute("error", handler.getException().getMessage());

		return "redirect:/analysis/customer/" + analysis.getCustomer().getId();

	}

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
	
	
	@RequestMapping("/{analysisId}/compute/actionPlan")
	public String computeActionPlan(
			@PathVariable("analysisId") Integer analysisId, RedirectAttributes attributes) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		if (analysis == null)
			return "redirect:/index";
		initAnalysis(analysis);
		MessageHandler handler = computeActionPlan(analysis);
		
		if(handler != null)
			attributes.addFlashAttribute("error", handler.getException().getMessage());

		return "redirect:/analysis/customer/" + analysis.getCustomer().getId();
	}

	@RequestMapping("/import/compute/riskRegister")
	public ModelAndView computeRiskRegister(@ModelAttribute Analysis analysis, RedirectAttributes attributes)
			throws Exception {
		
		if (analysis == null || analysis.isEmpty())
			return new ModelAndView("redirect:/index");

		MessageHandler handler = computeRiskRegisters(analysis);

		if (handler == null)
			serviceAnalysis.saveOrUpdate(analysis);
		else
		{
			handler.getException().printStackTrace();
			attributes.addFlashAttribute("error", handler.getException().getMessage());
		}
		
		return new ModelAndView("redirect:/analysis/customer/"+analysis.getCustomer().getId());
	}

	@RequestMapping("/import/compute/actionPlan")
	public ModelAndView computeActionPlans(@ModelAttribute Analysis analysis, RedirectAttributes redirectAttributes)
			throws Exception {

		if (analysis == null || analysis.isEmpty())
			return new ModelAndView("redirect:/index");

		MessageHandler handler = computeActionPlan(analysis);

		if (handler == null){
			redirectAttributes.addFlashAttribute("analysis", analysis);
			serviceAnalysis.saveOrUpdate(analysis);
			return new ModelAndView("redirect:/analysis/import/compute/riskRegister");
		}
		else {
			handler.getException().printStackTrace();
			redirectAttributes.addFlashAttribute("error", handler.getException().getMessage());
		}

		return new ModelAndView("redirect:/analysis/customer/" + analysis.getCustomer().getId());
	}

	private void updateData(Analysis analysis, Analysis data) {

		analysis.setLabel(data.getLabel());

		analysis.setLanguage(data.getLanguage());

		analysis.setIdentifier(data.getIdentifier());

		analysis.setCreationDate(data.getCreationDate());

		analysis.setVersion(data.getVersion());

	}

	@RequestMapping("/edit/{analysisId}/save")
	public String saveAnalysis(@PathVariable("analysisId") Integer analysisId,
			@ModelAttribute("analysis") Analysis analysis,
			BindingResult result, HttpSession session) throws Exception {

		Language language = serviceLanguage.get(analysis.getLanguage().getId());

		analysis.setLanguage(language);

		Analysis analysis2 = serviceAnalysis.get(analysisId);

		SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

		Timestamp timestamp = new Timestamp(format.parse(
				result.getFieldValue("creationDate").toString()).getTime());

		analysis.setCreationDate(timestamp);

		updateData(analysis2, analysis);

		serviceAnalysis.saveOrUpdate(analysis2);

		Customer customer = (Customer) session.getAttribute("customer");
		if (customer != null)
			return "redirect:/analysis/customer/" + customer.getId();

		return "index";
	}

	@RequestMapping("/edit/{analysisId}")
	public String editAnalysis(@PathVariable("analysisId") Integer analysisId,
			Map<String, Object> model, HttpSession session) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		model.put("languages", serviceLanguage.loadAll());

		if (analysis == null)
			model.put("Error", "label.error.analysis.notExist");
		else
			model.put("analysis", analysis);

		return "editAnalysis";
	}

	@RequestMapping("/delete/{analysisId}")
	public String deleteAnalysis(
			@PathVariable("analysisId") Integer analysisId, HttpSession session)
			throws Exception {
		serviceAnalysis.remove(analysisId);
		Customer customer = (Customer) session.getAttribute("customer");
		if (customer != null)
			return "redirect:/analysis/customer/" + customer.getId();
		return "redirect:/analysis/all";
	}

	/**
	 * @param serviceActionPlanType
	 *            the serviceActionPlanType to set
	 */
	public void setServiceActionPlanType(
			ServiceActionPlanType serviceActionPlanType) {
		this.serviceActionPlanType = serviceActionPlanType;
	}

	public ServiceActionPlan getServiceActionPlan() {
		return serviceActionPlan;
	}

	public void setServiceActionPlan(ServiceActionPlan serviceActionPlan) {
		this.serviceActionPlan = serviceActionPlan;
	}

	public ServiceActionPlanSummary getServiceActionPlanSummary() {
		return serviceActionPlanSummary;
	}

	public void setServiceActionPlanSummary(
			ServiceActionPlanSummary serviceActionPlanSummary) {
		this.serviceActionPlanSummary = serviceActionPlanSummary;
	}

	public ServiceRiskRegister getServiceRiskRegister() {
		return serviceRiskRegister;
	}

	public void setServiceRiskRegister(ServiceRiskRegister serviceRiskRegister) {
		this.serviceRiskRegister = serviceRiskRegister;
	}

}
