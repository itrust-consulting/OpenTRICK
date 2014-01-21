package lu.itrust.business.view.controller;

import java.io.File;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.History;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.cssf.RiskRegisterComputation;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.Duplicator;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceRiskRegister;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerAnalysisImport;
import lu.itrust.business.task.WorkerComputeActionPlan;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerAnalysis.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@PreAuthorize(Constant.ROLE_USER_ONLY)
@Controller
@RequestMapping("/Analysis")
public class ControllerAnalysis {

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServiceActionPlanType serviceActionPlanType;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

	@Autowired
	private ServiceAssetType serviceAssetType;

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

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private MessageSource messageSource;

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
	@RequestMapping
	public String displayAll(Principal principal, Map<String, Object> model, HttpSession session, RedirectAttributes attributes, Locale locale) throws Exception {
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");
		if (selected != null) {
			if (serviceUserAnalysisRight.isUserAuthorized(selected, principal.getName(), AnalysisRight.READ)) {
				Analysis analysis = serviceAnalysis.get(selected);
				if (analysis == null) {
					attributes.addFlashAttribute("errors", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
					return "redirect:/Error/404";
				}
				Hibernate.initialize(analysis.getLanguage());
				model.put("assettypes", serviceAssetType.loadAll());
				model.put("language", analysis.getLanguage().getAlpha3());
				model.put("analysis", analysis);
			} else {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.notAuthorized", null, "Insufficient permissions!", locale));
				return "redirect:/Error/403";
			}
		} else {
			model.put("analyses", serviceAnalysis.loadAllFromUser(serviceUser.get(principal.getName())));
			model.put("login", principal.getName());
			model.put("deleteRight", AnalysisRight.DELETE.ordinal());
			model.put("calcRickRegisterRight", AnalysisRight.CALCULATE_RISK_REGISTER.ordinal());
			model.put("calcActionPlanRight", AnalysisRight.CALCULATE_ACTIONPLAN.ordinal());
			model.put("modifyRight", AnalysisRight.MODIFY.ordinal());
			model.put("exportRight", AnalysisRight.EXPORT.ordinal());
			model.put("readRight", AnalysisRight.READ.ordinal());
		}
		return "analysis/analysis";
	}

	@RequestMapping(value = "/Update/ALE", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String update(HttpSession session, Locale locale) {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
		try {
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));
			assessmentManager.UpdateAssetALE(analysis);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.ale.update", null, "ALE was successfully updated", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.analysis.ale.update", null, "ALE cannot be updated", locale));
		}
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping("/{analysisId}/Select")
	public String selectAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, Map<String, Object> model, HttpSession session, RedirectAttributes attributes,
			Locale locale) throws Exception {
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");
		if (selected != null && selected.intValue() == analysisId)
			session.removeAttribute("selectedAnalysis");
		else if (serviceAnalysis.exist(analysisId))
				session.setAttribute("selectedAnalysis", analysisId);
		else {
			session.removeAttribute("selectedAnalysis");
			attributes.addFlashAttribute("error", "Analysis not recognized!");
		}
		return "redirect:/Analysis";

	}

	/**
	 * newAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/New")
	public String requestnewAnalysis(Principal principal, Map<String, Object> model, RedirectAttributes attributes, Locale locale) throws Exception {

		model.put("languages", serviceLanguage.loadAll());

		model.put("customers", serviceCustomer.loadAll());

		model.put("author", principal.getName());

		return "analysis/newAnalysis";

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
	@RequestMapping("/Edit/{analysisId}")
	public String requestEditAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, Map<String, Object> model, RedirectAttributes attributes, Locale locale)
			throws Exception {

		if (!serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), AnalysisRight.MODIFY))
			return "/errors/403";
		Analysis analysis = serviceAnalysis.get(analysisId);
		if (analysis == null)
			return "redirect:/Error/404";
		model.put("languages", serviceLanguage.loadAll());

		model.put("customers", serviceCustomer.loadAll());

		model.put("analysis", analysis);

		return "analysis/editAnalysis";
	}

	/**
	 * buildAnalysis: <br>
	 * Description
	 * 
	 * @param errors
	 * @param owner
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildAnalysis(List<String> errors, User owner, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			Analysis analysis = null;
			int id = jsonNode.get("id").asInt();

			if (jsonNode.get("analysiscustomer").asInt() == -1) {
				throw new IllegalArgumentException("error.customer.null");
			}

			if (jsonNode.get("analysislanguage").asInt() == -1) {
				throw new IllegalArgumentException("error.language.null");
			}

			Customer customer = serviceCustomer.get(jsonNode.get("analysiscustomer").asInt());
			Language language = serviceLanguage.get(jsonNode.get("analysislanguage").asInt());
			String label = jsonNode.get("label").asText();
			String author = "";
			String version = jsonNode.get("version").asText();
			Date date = new Date();
			Timestamp creationDate = new Timestamp(date.getTime());
			String ts = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(creationDate);
			String identifier = language.getAlpha3() + "_" + ts;
			String comment = messageSource.getMessage("label.analysis.newHistoryComment", null, "Analysis creation.", locale);
			if (id > 0) {
				analysis = serviceAnalysis.get(id);
				analysis.setLabel(label);
				analysis.setCustomer(customer);
				analysis.setLanguage(language);
				serviceAnalysis.saveOrUpdate(analysis);
			} else {
				author = jsonNode.get("author").asText();
				analysis = new Analysis();
				analysis.setBasedOnAnalysis(null);
				analysis.setCreationDate(creationDate);
				analysis.setCustomer(customer);
				analysis.setData(false);
				analysis.setIdentifier(identifier);
				analysis.setLabel(label);
				analysis.setLanguage(language);
				analysis.setOwner(owner);
				analysis.setVersion(version);

				History history = new History(version, date, author, comment);

				analysis.addAHistory(history);

				// TODO populate measures, default scenarios and parameters

				UserAnalysisRight uar = new UserAnalysisRight(owner, analysis, AnalysisRight.ALL);

				analysis.addUserRight(uar);

				serviceAnalysis.save(analysis);
			}
			return true;

		} catch (Exception e) {
			errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale)));
			e.printStackTrace();
			return false;
		}
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {
		List<String> errors = new LinkedList<>();
		try {
			buildAnalysis(errors, serviceUser.get(principal.getName()), value, locale);
		} catch (Exception e) {
			errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale)));
			e.printStackTrace();
		}
		return errors;
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
	@RequestMapping("/Delete/{analysisId}")
	public @ResponseBody
	String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal) throws Exception {
		try {
			if (serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), AnalysisRight.DELETE)) {
				serviceAnalysis.remove(analysisId);
				return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
			}
			return JsonMessage.Error(messageSource.getMessage("error.notAutorized", null, "Permission denied!", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("failed.delete.analysis", null, "Analysis cannot be deleted!", locale));
		}
	}

	/**
	 * createNewVersion: <br>
	 * Description
	 * 
	 * @param history
	 * @param analysisId
	 * @param principal
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/{analysisId}/Duplicate", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody String createNewVersion(@ModelAttribute History history, @PathVariable int analysisId, Principal principal, HttpSession session, RedirectAttributes attributes,
			Locale locale) throws Exception {
		if (!serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), AnalysisRight.MODIFY))
			return JsonMessage.Error(messageSource.getMessage("error.notAuthorized", null, "Permission denied!", locale));
		try {
			Analysis analysis = serviceAnalysis.get(analysisId);
			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));
			history.setDate(new Date(System.currentTimeMillis()));
			Duplicator duplicator = new Duplicator();
			Analysis copy = duplicator.duplicate(analysis);
			copy.setBasedOnAnalysis(analysis);
			copy.addAHistory(history);
			copy.setVersion(history.getVersion());
			copy.setLabel(analysis.getLabel());
			copy.setCreationDate(new Timestamp(System.currentTimeMillis()));
			serviceAnalysis.saveOrUpdate(copy);
			return JsonMessage.Success(messageSource.getMessage("success.analysis.duplicate", null, "Analysis was successfully duplicated", locale));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.analysis.duplicate", null, "Analysis cannot be duplicate!", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.analysis.duplicate.unknown", null, "An unknown error occurred during copying", locale));
		}
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
	@RequestMapping("/{analysisId}/compute/riskRegister")
	public String computeRiskRegister(@PathVariable("analysisId") Integer analysisId, RedirectAttributes attributes) throws Exception {

		Analysis analysis = serviceAnalysis.get(analysisId);

		if (analysis == null) {
			return "redirect:/Analysis";
		}

		MessageHandler handler = computeRiskRegisters(analysis);

		if (handler != null) {
			attributes.addFlashAttribute("error", handler.getException().getMessage());
		}
		return "redirect:Analysis";
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
	@RequestMapping("/{analysisId}/Compute/ActionPlan")
	public @ResponseBody
	String computeActionPlan(@PathVariable("analysisId") Integer analysisId, Principal principal, Locale locale) throws Exception {

		if (!serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), AnalysisRight.CALCULATE_ACTIONPLAN))
			return JsonMessage.Error(messageSource.getMessage("errors.403.access.denied", null, "You do not have the nessesary permissions to perform this action!", locale));
		Worker worker = new WorkerComputeActionPlan(sessionFactory, serviceTaskFeedback, analysisId);
		worker.setPoolManager(workersPoolManager);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			return JsonMessage.Error(messageSource.getMessage("failed.start.compute.actionplan", null, "Action plan computation was failed", locale));
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.compute.actionplan", null, "Action plan computation was started successfully", locale));
	}

	// ******************************************************************************************************************
	// * Import Analysis
	// ******************************************************************************************************************

	/**
	 * importAnalysis: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Import")
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
	@RequestMapping("/Import/Execute")
	public Object importAnalysisSave(final Principal principal, final @RequestParam(value = "customerId") Integer customerId, final HttpServletRequest request, final @RequestParam(
			value = "file") MultipartFile file, final RedirectAttributes attributes, Locale locale) throws Exception {

		Customer customer = serviceCustomer.get(customerId);

		if (customer == null || file.isEmpty())
			return "analysis/importAnalysisForm";

		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");

		file.transferTo(importFile);

		Worker worker = new WorkerAnalysisImport(sessionFactory, serviceTaskFeedback, importFile, customer, serviceUser.get(principal.getName()));

		worker.setPoolManager(workersPoolManager);

		String message = null;
		String typeMessage = null;

		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
			executor.execute(worker);
			typeMessage = "success";
			message = messageSource.getMessage("success.start.import.analysis", null, "Analysis importation was started successfully", locale);
		} else {
			typeMessage = "errors";
			message = messageSource.getMessage("failed.start.import.analysis", null, "Analysis importation was failed", locale);
		}

		attributes.addFlashAttribute(typeMessage, message);

		return "redirect:/Analysis";
	}

	// ******************************************************************************************************************
	// * Actions
	// ******************************************************************************************************************

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
}
