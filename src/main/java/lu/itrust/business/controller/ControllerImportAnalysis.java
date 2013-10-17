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

	// ******************************************************************************************************************
	// * Request Mappings
	// ******************************************************************************************************************

	/**
	 * importAnalysis: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Import/Display")
	public String importAnalysis(Map<String, Object> model) throws Exception {
		model.put("customerId", -1);
		model.put("customers", serviceCustomer.loadAll());
		return "analysis/importAnalysisForm";
	}

	/**
	 * importAnalysisSave: <br>
	 * Description
	 * 
	 * @param session
	 * @param customerId
	 * @param file
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("Analysis/Import/Execute")
	public ModelAndView importAnalysisSave(HttpSession session, @RequestParam(value = "customerId") Integer customerId, @RequestParam(value = "file") MultipartFile file,
			RedirectAttributes redirectAttributes) throws Exception {

		Customer customer = serviceCustomer.get(customerId);

		if (customer == null || file.isEmpty()) {
			return new ModelAndView("analysis/importAnalysisForm");
		}

		ImportAnalysis importAnalysis = new ImportAnalysis();

		importAnalysis.setServiceAnalysis(serviceAnalysis);

		importAnalysis.setServiceAssetType(serviceAssetType);

		importAnalysis.setServiceLanguage(serviceLanguage);

		importAnalysis.setServiceMeasureDescription(serviceMeasureDescription);

		importAnalysis.setServiceMeasureDescriptionText(serviceMeasureDescriptionText);

		importAnalysis.setServiceNorm(serviceNorm);

		importAnalysis.setServiceParameterType(serviceParameterType);

		importAnalysis.setServiceScenarioType(serviceScenarioType);

		KnowLedgeBase knowLedgeBase = new KnowLedgeBase(importAnalysis);

		MessageHandler handler = knowLedgeBase.importSQLite(file.getOriginalFilename(), file.getInputStream(), customer);

		if (handler == null) {

			redirectAttributes.addFlashAttribute("success", "Import Success");

			return new ModelAndView("redirect:/Analyses/Display");
		} else {

			redirectAttributes.addFlashAttribute("errors", handler.getException().getMessage());

			handler.getException().printStackTrace();

			return new ModelAndView("redirect:/Analysis/Import/Display");
		}
	}

	// ******************************************************************************************************************
	// * Setters
	// ******************************************************************************************************************

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
	 * setServiceAnalysis: <br>
	 * Description
	 * 
	 * @param serviceAnalysis
	 */
	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
	}

	/**
	 * setServiceAnalysisNorm: <br>
	 * Description
	 * 
	 * @param serviceAnalysisNorm
	 */
	public void setServiceAnalysisNorm(ServiceAnalysisNorm serviceAnalysisNorm) {
		this.serviceAnalysisNorm = serviceAnalysisNorm;
	}

	/**
	 * setServiceAssetType: <br>
	 * Description
	 * 
	 * @param serviceAssetType
	 */
	public void setServiceAssetType(ServiceAssetType serviceAssetType) {
		this.serviceAssetType = serviceAssetType;
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
	 * setServiceMeasureDescription: <br>
	 * Description
	 * 
	 * @param serviceMeasureDescription
	 */
	public void setServiceMeasureDescription(ServiceMeasureDescription serviceMeasureDescription) {
		this.serviceMeasureDescription = serviceMeasureDescription;
	}

	/**
	 * setServiceMeasureDescriptionText: <br>
	 * Description
	 * 
	 * @param serviceMeasureDescriptionText
	 */
	public void setServiceMeasureDescriptionText(ServiceMeasureDescriptionText serviceMeasureDescriptionText) {
		this.serviceMeasureDescriptionText = serviceMeasureDescriptionText;
	}

	/**
	 * setServiceNorm: <br>
	 * Description
	 * 
	 * @param serviceNorm
	 */
	public void setServiceNorm(ServiceNorm serviceNorm) {
		this.serviceNorm = serviceNorm;
	}

	/**
	 * setServiceParameterType: <br>
	 * Description
	 * 
	 * @param serviceParameterType
	 */
	public void setServiceParameterType(ServiceParameterType serviceParameterType) {
		this.serviceParameterType = serviceParameterType;
	}

	/**
	 * setServiceScenarioType: <br>
	 * Description
	 * 
	 * @param serviceScenarioType
	 */
	public void setServiceScenarioType(ServiceScenarioType serviceScenarioType) {
		this.serviceScenarioType = serviceScenarioType;
	}
}