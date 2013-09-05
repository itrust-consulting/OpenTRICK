/**
 * 
 */
package lu.itrust.business.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.KnowLedgeBase;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAnalysisNorm;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceNorm;
import lu.itrust.business.service.ServiceParameterType;
import lu.itrust.business.service.ServiceScenarioType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author oensuifudine
 */
@Secured("ROLE_CONSULTANT")
@RequestMapping("/import")
@Controller
public class ControllerImportAnalysis {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAnalysisNorm serviceAnalysisNorm;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;

	@Autowired
	private ServiceMeasureDescriptionText serviceMeasureDescriptionText;

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private ServiceParameterType serviceParameterType;

	@Autowired
	private ServiceScenarioType serviceScenarioType;

	public void setServiceCustomer(ServiceCustomer serviceCustomer) {
		this.serviceCustomer = serviceCustomer;
	}

	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	@RequestMapping("/analysis")
	public String importAnalysis(Map<String, Object> model) throws Exception {
		model.put("customerId", -1);
		model.put("customers", serviceCustomer.loadAll());
		return "importAnalysisForm";
	}

	@RequestMapping("/analysis/save")
	public ModelAndView importAnalysisSave(HttpSession session,
			@RequestParam(value = "customerId") Integer customerId,
			@RequestParam(value = "file") MultipartFile file, RedirectAttributes redirectAttributes) throws Exception {

		Customer customer = serviceCustomer.get(customerId);

		if (customer == null || file.isEmpty())
			
			return new ModelAndView("importAnalysisForm");

		ImportAnalysis importAnalysis = new ImportAnalysis();

		importAnalysis.setServiceAnalysis(serviceAnalysis);

		importAnalysis.setServiceAssetType(serviceAssetType);

		importAnalysis.setServiceLanguage(serviceLanguage);

		importAnalysis.setServiceMeasureDescription(serviceMeasureDescription);

		importAnalysis
				.setServiceMeasureDescriptionText(serviceMeasureDescriptionText);

		importAnalysis.setServiceNorm(serviceNorm);

		importAnalysis.setServiceParameterType(serviceParameterType);

		importAnalysis.setServiceScenarioType(serviceScenarioType);

		KnowLedgeBase knowLedgeBase = new KnowLedgeBase(importAnalysis);

		MessageHandler handler = knowLedgeBase.importSQLite(
				file.getOriginalFilename(), file.getInputStream(), customer);

		if (handler == null) {
			
		
			redirectAttributes.addFlashAttribute("analysis", importAnalysis.getAnalysis());
			
			return new ModelAndView(
					"redirect:/analysis/import/compute/actionPlan");
		}
		
		redirectAttributes.addFlashAttribute("errors", handler.getException().getMessage());

		handler.getException().printStackTrace();

		return new ModelAndView("redirect:/analysis/customer/" + customerId);
	}

	public void setServiceAnalysisNorm(ServiceAnalysisNorm serviceAnalysisNorm) {
		this.serviceAnalysisNorm = serviceAnalysisNorm;
	}

	public void setServiceAssetType(ServiceAssetType serviceAssetType) {
		this.serviceAssetType = serviceAssetType;
	}

	public void setServiceLanguage(ServiceLanguage serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	public void setServiceMeasureDescription(
			ServiceMeasureDescription serviceMeasureDescription) {
		this.serviceMeasureDescription = serviceMeasureDescription;
	}

	public void setServiceMeasureDescriptionText(
			ServiceMeasureDescriptionText serviceMeasureDescriptionText) {
		this.serviceMeasureDescriptionText = serviceMeasureDescriptionText;
	}

	public void setServiceNorm(ServiceNorm serviceNorm) {
		this.serviceNorm = serviceNorm;
	}

	public void setServiceParameterType(
			ServiceParameterType serviceParameterType) {
		this.serviceParameterType = serviceParameterType;
	}

	public void setServiceScenarioType(ServiceScenarioType serviceScenarioType) {
		this.serviceScenarioType = serviceScenarioType;
	}

}
