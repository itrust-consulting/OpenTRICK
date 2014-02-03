package lu.itrust.business.view.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
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
import lu.itrust.business.permissionevaluator.PermissionEvaluator;
import lu.itrust.business.permissionevaluator.PermissionEvaluatorImpl;
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
import org.springframework.security.access.AccessDeniedException;
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

	// *****************************************************************
	// * default request (/Analysis) display selected or all analyses
	// *****************************************************************

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String displayAll(Principal principal, Model model, HttpSession session, RedirectAttributes attributes, Locale locale) throws Exception {

		// retrieve analysisId if an analysis was already selected
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// check if an analysis is selected
		if (selected != null) {

			// prepare permission evaluator
			PermissionEvaluatorImpl permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceUserAnalysisRight);

			// check if user has permission to read the analysis
			if (permissionEvaluator.userIsAuthorized(selected, principal, AnalysisRight.READ)) {

				// retrieve the analysis object
				Analysis analysis = serviceAnalysis.get(selected);
				if (analysis == null) {
					attributes.addFlashAttribute("errors", messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found", locale));
					return "redirect:/Error/404";
				}

				// initialise and send data to the data model
				Hibernate.initialize(analysis.getLanguage());
				model.addAttribute("login", principal.getName());
				model.addAttribute("assettypes", serviceAssetType.loadAll());
				model.addAttribute("language", analysis.getLanguage().getAlpha3());
				model.addAttribute("analysis", analysis);
			} else {
				attributes.addFlashAttribute("errors", messageSource.getMessage("error.notAuthorized", null, "Insufficient permissions!", locale));
				return "redirect:/Error/403";
			}
		} else {

			// load only customers of this user
			List<Customer> customers = serviceCustomer.loadByUser(principal.getName());

			// retrieve currently selected customer
			String customer = (String) session.getAttribute("currentCustomer");

			// check if the current customer is set -> no
			if (customer == null && !customers.isEmpty())

				// use first customer as selected customer
				session.setAttribute("currentCustomer", customer = customers.get(0).getOrganisation());
			if (customer != null)

				// load model with objects by the selected customer
				section(customer, 0, principal.getName(), model, customers);
		}
		return "analysis/analysis";
	}

	// *****************************************************************
	// * reload customer section by pageindex
	// *****************************************************************

	/**
	 * section: <br>
	 * reload customer section by page index
	 * 
	 * @param customer
	 * @param pageIndex
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 */
	@RequestMapping("/Section/{customerSection}/{pageIndex}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(@PathVariable String customerSection, @PathVariable int pageIndex, HttpSession session, Principal principal, Model model) {

		// set currently selected customer
		session.setAttribute("currentCustomer", customerSection);

		// load model with objects by the selected customer
		section(customerSection, pageIndex, principal.getName(), model, serviceCustomer.loadByUser(principal.getName()));
		return "analysis/analysis";
	}

	// *****************************************************************
	// * select or deselect analysis
	// *****************************************************************

	/**
	 * selectAnalysis: <br>
	 * selects or deselects an analysis
	 * 
	 * @param principal
	 * @param analysisId
	 * @param model
	 * @param session
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	@RequestMapping("/{analysisId}/Select")
	public String selectAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, Map<String, Object> model, HttpSession session, RedirectAttributes attributes,
			Locale locale) throws Exception {

		// retrieve selected analysis
		Integer selected = (Integer) session.getAttribute("selectedAnalysis");

		// check if analysis is selected and if thee selected value is the same as the analysis to
		// select (in order to deselect analysis)
		if (selected != null && selected.intValue() == analysisId)

			// deselect the analysis
			session.removeAttribute("selectedAnalysis");

		// check if analysis exists -> YES
		else if (serviceAnalysis.exist(analysisId))

			// select the analysis
			session.setAttribute("selectedAnalysis", analysisId);
		else {

			// deselect the analysis
			session.removeAttribute("selectedAnalysis");

			// add error attribute
			attributes.addFlashAttribute("error", "Analysis not recognized!");
		}
		return "redirect:/Analysis";
	}

	// *****************************************************************
	// * request create new analysis
	// *****************************************************************

	/**
	 * requestnewAnalysis: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/New")
	public String requestnewAnalysis(Principal principal, Map<String, Object> model) throws Exception {

		// add languages
		model.put("languages", serviceLanguage.loadAll());

		// add only customers of the current user
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));

		// set author as the username
		model.put("author", principal.getName());

		return "analysis/newAnalysis";
	}

	// *****************************************************************
	// * request edit analysis
	// *****************************************************************

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String requestEditAnalysis(Principal principal, @PathVariable("analysisId") Integer analysisId, Map<String, Object> model) throws Exception {

		// retrieve analysis
		Analysis analysis = serviceAnalysis.get(analysisId);
		if (analysis == null)
			return "redirect:/Error/404";

		// add languages
		model.put("languages", serviceLanguage.loadAll());

		// add customers of user
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));

		// add the analysis object
		model.put("analysis", analysis);

		return "analysis/editAnalysis";
	}

	// *****************************************************************
	// * save or update analysis object
	// *****************************************************************

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
	List<String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {

		// initialise list of return messages
		List<String> errors = new LinkedList<>();

		// prepare permission verifier
		PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceUserAnalysisRight);

		try {

			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			// retrieve analysis id to compute
			int analysisId = jsonNode.get("id").asInt();

			// check if it is a new analysis or the user is authorized to modify the analysis
			if (analysisId == -1 || permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.MODIFY)) {

				// create/update analysis object and set access rights
				buildAnalysis(errors, serviceUser.get(principal.getName()), value, locale);
			} else {

				// throw error
				throw new AccessDeniedException("user.not.authorized");
			}

		} catch (Exception e) {
			errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale)));
			e.printStackTrace();
		}
		return errors;
	}

	// *****************************************************************
	// * delete analysis
	// *****************************************************************

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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String deleteAnalysis(@PathVariable("analysisId") int analysisId, RedirectAttributes attributes, Locale locale, Principal principal) throws Exception {
		try {

			// delete the analysis
			serviceAnalysis.remove(analysisId);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null, "Customer was deleted successfully", locale));
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("failed.delete.analysis", null, "Analysis cannot be deleted!", locale));
		}
	}

	// *****************************************************************
	// * create new version of analysis
	// *****************************************************************

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
	String createNewVersion(@ModelAttribute History history, @PathVariable int analysisId, Principal principal, Locale locale) throws Exception {

		// prepare permission verifier
		PermissionEvaluator permissionEvaluator = new PermissionEvaluatorImpl(serviceUser, serviceUserAnalysisRight);

		// check if user is authorized to duplicate the analysis
		if (!permissionEvaluator.userIsAuthorized(analysisId, principal, AnalysisRight.MODIFY))
			return JsonMessage.Error(messageSource.getMessage("error.notAuthorized", null, "Permission denied!", locale));

		try {

			// retrieve analysis object
			Analysis analysis = serviceAnalysis.get(analysisId);

			// check if object is not null
			if (analysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.not_found", null, "Analysis cannot be found!", locale));

			// update date of history object
			history.setDate(new Date(System.currentTimeMillis()));

			// duplicate analysis
			Duplicator duplicator = new Duplicator();
			Analysis copy = duplicator.duplicate(analysis);

			// attribute new values to new analysis
			copy.setBasedOnAnalysis(analysis);
			copy.addAHistory(history);
			copy.setVersion(history.getVersion());
			copy.setLabel(analysis.getLabel());
			copy.setCreationDate(new Timestamp(System.currentTimeMillis()));

			// save the new version
			serviceAnalysis.saveOrUpdate(copy);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.analysis.duplicate", null, "Analysis was successfully duplicated", locale));
		} catch (CloneNotSupportedException e) {

			// return dubplicate error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.analysis.duplicate", null, "Analysis cannot be duplicate!", locale));
		} catch (Exception e) {

			// return general error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.analysis.duplicate.unknown", null, "An unknown error occurred during copying", locale));
		}
	}

	// *****************************************************************
	// * compute risk register TODO place into controllerriskregister
	// *****************************************************************

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

	// *****************************************************************
	// * import form and import action
	// *****************************************************************

	/**
	 * importAnalysis: <br>
	 * Description
	 * 
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Import")
	public String importAnalysis(Principal principal, Map<String, Object> model) throws Exception {

		// add the customers of the user to the data model
		model.put("customers", serviceCustomer.loadByUser(principal.getName()));
		return "analysis/importAnalysisForm";
	}

	/**
	 * importAnalysisSave: <br>
	 * Description
	 * 
	 * @param principal
	 * @param customerId
	 * @param request
	 * @param file
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Import/Execute")
	public Object importAnalysisSave(Principal principal, @RequestParam(value = "customerId") Integer customerId, HttpServletRequest request,
			@RequestParam(value = "file") MultipartFile file, final RedirectAttributes attributes, Locale locale) throws Exception {

		// retrieve the customer
		Customer customer = serviceCustomer.get(customerId);

		String message = null;
		String typeMessage = null;

		// if the customer or the file are not correct
		if (customer == null || file.isEmpty()) {
			typeMessage = "errors";
			message = messageSource.getMessage("error.customerorfile.import.analysis", null, "Customer or file are not set or empty!", locale);
			attributes.addFlashAttribute(typeMessage, message);
			return "analysis/importAnalysisForm";
		}

		// set selected customer, the selected customer of the analysis
		request.getSession().setAttribute("currentCustomer", customer.getOrganisation());

		// the file to import
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");

		// transfer form file to java file
		file.transferTo(importFile);

		// create worker
		Worker worker = new WorkerAnalysisImport(sessionFactory, serviceTaskFeedback, importFile, customer, serviceUser.get(principal.getName()));
		worker.setPoolManager(workersPoolManager);

		// register worker to tasklist
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {

			// execute task
			executor.execute(worker);

			// prepare success return message
			typeMessage = "success";
			message = messageSource.getMessage("success.start.import.analysis", null, "Analysis importation was started successfully", locale);
		} else {

			// prepare error return message
			typeMessage = "errors";
			message = messageSource.getMessage("failed.start.import.analysis", null, "Analysis importation was failed", locale);
		}

		// add return message
		attributes.addFlashAttribute(typeMessage, message);

		return "redirect:/Analysis/importAnalysisForm";
	}

	// *****************************************************************
	// * export and download
	// *****************************************************************

	/**
	 * exportAnalysis: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param principal
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#analysisId, #principal, T(lu.itrust.business.TS.AnalysisRight).EXPORT)")
	@RequestMapping(value = "/Export/{analysisId}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody
	String exportAnalysis(@PathVariable int analysisId, Principal principal, HttpServletRequest request, Locale locale) throws Exception {

		// create worker
		Worker worker = new WorkerExportAnalysis(serviceTaskFeedback, sessionFactory, principal, request.getServletContext(), workersPoolManager, analysisId);

		// register worker
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {

			// execute task
			executor.execute(worker);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.start.export.analysis", null, "Analysis export was started successfully", locale));
		} else

			// return error message
			return JsonMessage.Error(messageSource.getMessage("failed.start.export.analysis", null, "Analysis export was failed", locale));
	}

	/**
	 * download: <br>
	 * Description
	 * 
	 * @param idFile
	 * @param principal
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/Download/{idFile}")
	public String download(@PathVariable long idFile, Principal principal, HttpServletResponse response) throws IOException {

		// get user file by given file id and username
		UserSqLite userSqLite = serviceUserSqLite.findByIdAndUser(idFile, principal.getName());

		// if file could not be found retrun 404 error
		if (userSqLite == null)
			return "errors/404";

		// set response contenttype to sqlite
		response.setContentType("sqlite");

		// retireve sqlite file name to set
		String identifierName = userSqLite.getAnalysisIdentifier();

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\""
			+ (identifierName == null || identifierName.trim().isEmpty() ? "Analysis" : identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".sqlite\"");

		// set sqlite file size as response size
		response.setContentLength((int) userSqLite.getSize());

		// return the sqlite file (as copy) to the response outputstream ( whihc creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(userSqLite.getSqLite(), response.getOutputStream());

		// return
		return null;
	}

	// ******************************************************************************************************************
	// * Actions
	// ******************************************************************************************************************

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

			// create object mapper (json)
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			Analysis analysis = null;

			// get analysis id
			int id = jsonNode.get("id").asInt();

			// chekc if customer is null
			if (jsonNode.get("analysiscustomer").asInt() == -1) {
				throw new IllegalArgumentException("error.customer.null");
			}

			// check if language is null
			if (jsonNode.get("analysislanguage").asInt() == -1) {
				throw new IllegalArgumentException("error.language.null");
			}

			// set language and customer
			Customer customer = serviceCustomer.get(jsonNode.get("analysiscustomer").asInt());
			Language language = serviceLanguage.get(jsonNode.get("analysislanguage").asInt());

			// set other data to analysis object
			String label = jsonNode.get("label").asText();
			String author = "";
			String version = jsonNode.get("version").asText();
			Date date = new Date();
			Timestamp creationDate = new Timestamp(date.getTime());
			String ts = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(creationDate);
			String identifier = language.getAlpha3() + "_" + ts;
			String comment = messageSource.getMessage("label.analysis.newHistoryComment", null, "Analysis creation.", locale);

			// check if analysis needs only update or creation
			if (id > 0) {

				// update analysis with data
				analysis = serviceAnalysis.get(id);
				analysis.setLabel(label);
				analysis.setCustomer(customer);
				analysis.setLanguage(language);
				serviceAnalysis.saveOrUpdate(analysis);
			} else {

				// create new analysis with data
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

				// create first history entry
				History history = new History(version, date, author, comment);
				analysis.addAHistory(history);

				// TODO populate measures, default scenarios and parameters

				// set user analysis right of owner to all
				UserAnalysisRight uar = new UserAnalysisRight(owner, analysis, AnalysisRight.ALL);
				analysis.addUserRight(uar);

				// save analysis
				serviceAnalysis.save(analysis);
			}

			// return success message
			return true;

		} catch (Exception e) {

			// return error message
			errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale)));
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * section: <br>
	 * reload section by selected customer
	 * 
	 * @param customer
	 * @param pageIndex
	 * @param userName
	 * @param model
	 * @param customers
	 */
	private void section(String customer, int pageIndex, String userName, Model model, List<Customer> customers) {
		model.addAttribute("analyses", serviceAnalysis.loadByUserAndCustomer(userName, customer, pageIndex, 20));
		model.addAttribute("customer", customer);
		model.addAttribute("customers", serviceCustomer.loadByUser(userName));
		model.addAttribute("login", userName);
		model.addAttribute("deleteRight", AnalysisRight.DELETE.ordinal());
		model.addAttribute("calcRickRegisterRight", AnalysisRight.CALCULATE_RISK_REGISTER.ordinal());
		model.addAttribute("calcActionPlanRight", AnalysisRight.CALCULATE_ACTIONPLAN.ordinal());
		model.addAttribute("modifyRight", AnalysisRight.MODIFY.ordinal());
		model.addAttribute("exportRight", AnalysisRight.EXPORT.ordinal());
		model.addAttribute("readRight", AnalysisRight.READ.ordinal());
	}
}