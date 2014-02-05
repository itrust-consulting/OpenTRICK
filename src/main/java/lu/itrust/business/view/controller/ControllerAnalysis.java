package lu.itrust.business.view.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import lu.itrust.business.TS.usermanagement.UserSqLite;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.Duplicator;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
import lu.itrust.business.service.ServiceActionPlan;
import lu.itrust.business.service.ServiceActionPlanSummary;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceNorm;
import lu.itrust.business.service.ServiceRiskRegister;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.service.ServiceUserAnalysisRight;
import lu.itrust.business.service.ServiceUserSqLite;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerAnalysisImport;
import lu.itrust.business.task.WorkerExportAnalysis;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
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
	private ServiceNorm serviceNorm;

	@Autowired
	private ServiceUserSqLite serviceUserSqLite;

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
	public String displayAll(Principal principal, Model model, HttpSession session, RedirectAttributes attributes, Locale locale) throws Exception {
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");
		if (selected != null) {

			PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceUserAnalysisRight);

			if (permissionEvaluator.userIsAuthorized(selected, principal, AnalysisRight.READ)) {
				Analysis analysis = serviceAnalysis.get(selected);
				if (analysis == null) {
					attributes.addFlashAttribute("errors", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
					return "redirect:/Error/404";
				}
				Hibernate.initialize(analysis.getLanguage());
				model.addAttribute("login", principal.getName());
				model.addAttribute("language", analysis.getLanguage().getAlpha3());
				model.addAttribute("analysis", analysis);
			} else {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.notAuthorized", null, "Insufficient permissions!", locale));
				return "redirect:/Error/403";
			}
		} else {
			List<Customer> customers = serviceCustomer.loadByUser(principal.getName());
			String customer = (String) session.getAttribute("currentCustomer");
			if (customer == null && !customers.isEmpty())
				session.setAttribute("currentCustomer", customer = customers.get(0).getOrganisation());
			if (customer != null)
				section(customer, 0, principal.getName(), model, customers);
		}
		return "analysis/analysis";
	}

	@RequestMapping("/Section")
	public String section(HttpServletRequest request, Principal principal, Model model) throws Exception {
		String referer = request.getHeader("Referer");
		if (referer != null && referer.contains("/trickservice/KnowledgeBase")) {
			model.addAttribute("analyses", serviceAnalysis.loadAllFromCustomer(serviceCustomer.loadProfileCustomer()));
			model.addAttribute("login", principal.getName());
			model.addAttribute("deleteRight", AnalysisRight.DELETE.ordinal());
			model.addAttribute("calcRickRegisterRight", AnalysisRight.CALCULATE_RISK_REGISTER.ordinal());
			model.addAttribute("calcActionPlanRight", AnalysisRight.CALCULATE_ACTIONPLAN.ordinal());
			model.addAttribute("modifyRight", AnalysisRight.MODIFY.ordinal());
			model.addAttribute("exportRight", AnalysisRight.EXPORT.ordinal());
			model.addAttribute("readRight", AnalysisRight.READ.ordinal());
			model.addAttribute("KowledgeBaseView", true);
		} else {
			String customer = (String) request.getSession().getAttribute("currentCustomer");
			if (customer == null) {
				List<Customer> customers = serviceCustomer.loadByUser(principal.getName());
				if (!customers.isEmpty())
					request.getSession().setAttribute("currentCustomer", customer = customers.get(0).getOrganisation());
			}
			section(customer, 0, principal.getName(), model, serviceCustomer.loadByUser(principal.getName()));
		}
		return "analysis/components/analyses";
	}

	@RequestMapping("/Section/{customer}/{pageIndex}")
	public String section(@PathVariable String customer, @PathVariable int pageIndex, HttpServletRequest request, Principal principal, Model model) throws Exception {
		String referer = request.getHeader("Referer");
		if (referer != null && referer.contains("/trickservice/KnowledgeBase")) {
			model.addAttribute("analyses", serviceAnalysis.loadAllFromCustomer(serviceCustomer.loadProfileCustomer()));
			model.addAttribute("login", principal.getName());
			model.addAttribute("deleteRight", AnalysisRight.DELETE.ordinal());
			model.addAttribute("calcRickRegisterRight", AnalysisRight.CALCULATE_RISK_REGISTER.ordinal());
			model.addAttribute("calcActionPlanRight", AnalysisRight.CALCULATE_ACTIONPLAN.ordinal());
			model.addAttribute("modifyRight", AnalysisRight.MODIFY.ordinal());
			model.addAttribute("exportRight", AnalysisRight.EXPORT.ordinal());
			model.addAttribute("readRight", AnalysisRight.READ.ordinal());
			return "knowledgebase/analysis";
		} else {
			request.getSession().setAttribute("currentCustomer", customer);
			section(customer, pageIndex, principal.getName(), model, serviceCustomer.loadByUser(principal.getName()));
			return "analysis/analysis";
		}
	}

	private void section(String customer, int pageIndex, String userName, Model model, List<Customer> customers) {
		model.addAttribute("analyses", serviceAnalysis.loadByUserAndCustomer(userName, customer));
		model.addAttribute("customer", customer);
		model.addAttribute("customers", customers);
		model.addAttribute("login", userName);
		model.addAttribute("deleteRight", AnalysisRight.DELETE.ordinal());
		model.addAttribute("calcRickRegisterRight", AnalysisRight.CALCULATE_RISK_REGISTER.ordinal());
		model.addAttribute("calcActionPlanRight", AnalysisRight.CALCULATE_ACTIONPLAN.ordinal());
		model.addAttribute("modifyRight", AnalysisRight.MODIFY.ordinal());
		model.addAttribute("exportRight", AnalysisRight.EXPORT.ordinal());
		model.addAttribute("readRight", AnalysisRight.READ.ordinal());
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
	public String selectAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, Map<String, Object> model, HttpSession session,
			RedirectAttributes attributes, Locale locale) throws Exception {
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

	@RequestMapping("/Download/{idFile}")
	public String download(@PathVariable long idFile, Principal principal, HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserSqLite userSqLite = serviceUserSqLite.findByIdAndUser(idFile, principal.getName());
		if (userSqLite == null)
			return "errors/404";
		response.setContentType("sqlite");

		String identifierName = userSqLite.getAnalysisIdentifier();

		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ (identifierName == null || identifierName.trim().isEmpty() ? "Analysis" : identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".sqlite\"");

		response.setContentLength((int) userSqLite.getSize());

		FileCopyUtils.copy(userSqLite.getSqLite(), response.getOutputStream());
		return null;
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

		model.put("customers", serviceCustomer.loadByUser(principal.getName()));

		model.put("profiles", serviceAnalysis.loadProfiles());

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

		model.put("customers", serviceCustomer.loadByUser(principal.getName()));

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
	private boolean buildAnalysis(Map<String, String> errors, User owner, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			Analysis analysis = null;
			int id = jsonNode.get("id").asInt();
			int idCustomer = jsonNode.has("analysiscustomer")? jsonNode.get("analysiscustomer").asInt() : -1;
			int idLanguage = jsonNode.has("analysislanguage")? jsonNode.get("analysislanguage").asInt() : -1;
			int idProfile = jsonNode.has("profile") ? jsonNode.get("profile").asInt() : -1;
			String comment = jsonNode.has("label") ? jsonNode.get("label").asText() : "";
			String author = jsonNode.has("author") ? jsonNode.get("author").asText() : "";
			String version = jsonNode.has("version") ? jsonNode.get("version").asText() : "";
			if (idCustomer < 1)
				errors.put("analysiscustomer", messageSource.getMessage("error.customer.null", null, "Customer cannot be empty", locale));
			if (idLanguage < 1)
				errors.put("analysislanguage", messageSource.getMessage("error.language.null", null, "Language cannot be empty", locale));
			if (comment.trim().isEmpty())
				errors.put("comment", messageSource.getMessage("error.comment.null", null, "Comment cannot be empty", locale));
			if (author.trim().isEmpty())
				errors.put("author", messageSource.getMessage("error.author.null", null, "Author cannot be empty", locale));

			if (version.trim().isEmpty())
				errors.put("version", messageSource.getMessage("error.version.null", null, "Version cannot be empty", locale));
			else if (!version.matches(Constant.REGEXP_VALID_ANALYSIS_VERSION))
				errors.put("version", messageSource.getMessage("error.version.invalid", null, "Invalid version format, Please respect this format (0.0.1)", locale));

			if (!errors.isEmpty())
				return false;

			Customer customer = serviceCustomer.get(jsonNode.get("analysiscustomer").asInt());
			Language language = serviceLanguage.get(jsonNode.get("analysislanguage").asInt());
			String label = jsonNode.get("label").asText();
			Date date = new Date();
			Timestamp creationDate = new Timestamp(date.getTime());
			String ts = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(creationDate);
			String identifier = language.getAlpha3() + "_" + ts;
			if (id > 0) {
				analysis = serviceAnalysis.get(id);

				if (analysis == null) {
					errors.put("analysis", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
					return false;
				}

				analysis.setLabel(label);
				analysis.setCustomer(customer);
				analysis.setLanguage(language);
				serviceAnalysis.saveOrUpdate(analysis);
			} else {
				analysis = null;
				if (idProfile > 1) {
					Analysis profile = serviceAnalysis.get(idProfile);
					if (profile == null) {
						errors.put("profile", messageSource.getMessage("error.analysis.profile.not_found", null, "Selected profile cannot be found", locale));
						return false;
					}
					analysis = new Duplicator().duplicate(profile, null);
					analysis.setProfile(false);
				} else{
					analysis = new Analysis();
					analysis.setData(true);
				}
				analysis.setBasedOnAnalysis(null);
				analysis.setCreationDate(creationDate);
				analysis.setCustomer(customer);
				
				analysis.setIdentifier(identifier.toUpperCase());
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
			errors.put("analysis", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			buildAnalysis(errors, serviceUser.get(principal.getName()), value, locale);
		} catch (Exception e) {
			errors.put("ower", messageSource.getMessage("error.user.not_found", null, "User cannot be found", locale));
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
	@RequestMapping(value = "/Delete/{analysisId}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal) throws Exception {
		try {
			serviceAnalysis.remove(analysisId);
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
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
	@RequestMapping(value = "/{analysisId}/Duplicate", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String createNewVersion(@ModelAttribute History history, @PathVariable int analysisId, Principal principal, HttpSession session, RedirectAttributes attributes, Locale locale)
			throws Exception {
		if (!serviceUserAnalysisRight.isUserAuthorized(analysisId, principal.getName(), AnalysisRight.MODIFY))
			return JsonMessage.Error(messageSource.getMessage("error.notAuthorized", null, "Permission denied!", locale));
		try {
			Analysis analysis = serviceAnalysis.get(analysisId);
			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));
			history.setDate(new Date(System.currentTimeMillis()));
			Duplicator duplicator = new Duplicator();
			Analysis copy = duplicator.duplicate(analysis, null);
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
	public String importAnalysis(Principal principal, Map<String, Object> model) throws Exception {
		model.put("customerId", -1);
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));
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
	public Object importAnalysisSave(final Principal principal, final @RequestParam(value = "customerId") Integer customerId, final HttpServletRequest request,
			final @RequestParam(value = "file") MultipartFile file, final RedirectAttributes attributes, Locale locale) throws Exception {

		Customer customer = serviceCustomer.get(customerId);

		if (customer == null || file.isEmpty())
			return "analysis/importAnalysisForm";

		request.getSession().setAttribute("currentCustomer", customer.getOrganisation());

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Export/{analysisId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String exportAnalysis(@PathVariable int analysisId, Principal principal, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		Worker worker = new WorkerExportAnalysis(serviceTaskFeedback, sessionFactory, principal, request.getServletContext(), workersPoolManager, analysisId);
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.start.export.analysis", null, "Analysis export was started successfully", locale));
		} else
			return JsonMessage.Error(messageSource.getMessage("failed.start.export.analysis", null, "Analysis export was failed", locale));
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
