package lu.itrust.business.ts.controller.knowledgebase;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.CustomerManager;
import lu.itrust.business.ts.component.DefaultTemplateLoader;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceTrickTemplate;
import lu.itrust.business.ts.database.service.ServiceUser;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.form.CustomerForm;
import lu.itrust.business.ts.form.TemplateForm;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;
import lu.itrust.business.ts.usermanagement.User;

/**
 * ControllerCustomer.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 11, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Customer")
public class ControllerCustomer {

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private CustomerManager customerManager;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceTrickTemplate serviceTrickTemplate;

	@Autowired
	private DefaultTemplateLoader defaultReportTemplateLoader;

	private Long maxTemplateSize;

	private Long maxUploadFileSize;

	/**
	 * 
	 * Display all customers
	 * 
	 */
	@RequestMapping
	public String loadAllCustomers(Principal principal, Map<String, Object> model) throws Exception {
		model.put("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "jsp/knowledgebase/customer/customers";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(Model model, HttpSession session, Principal principal, HttpServletRequest request)
			throws Exception {
		model.addAttribute("customers", serviceCustomer.getAllNotProfileOfUser(principal.getName()));
		return "jsp/knowledgebase/customer/customers";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@RequestBody CustomerForm value, Principal principal, Locale locale) {
		final Map<String, String> errors = new LinkedHashMap<>();
		try {
			Customer customer = customerManager.buildCustomer(errors, value, locale, false);
			if (!errors.isEmpty())
				return errors;
			User user = serviceUser.get(principal.getName());
			if (customer.getId() < 1) {
				if (customer.isCanBeUsed()) {
					user.addCustomer(customer);
					serviceUser.saveOrUpdate(user);
				} else if (!serviceCustomer.profileExists())
					serviceCustomer.save(customer);
				else
					errors.put("canBeUsed", messageSource.getMessage("error.customer.profile.duplicate", null,
							"A customer profile already exists", locale));
			} else if (serviceCustomer.hasUsers(customer.getId()) && customer.isCanBeUsed()
					|| !(serviceCustomer.hasUsers(customer.getId()) || customer.isCanBeUsed())
							&& (!serviceCustomer.profileExists() || serviceCustomer.isProfile(customer.getId())))
				serviceCustomer.saveOrUpdate(customer);
			else
				errors.put("canBeUsed",
						messageSource.getMessage("error.customer.profile.attach.user", null,
								"Only a customer who is not attached to a user can be used as profile", locale));
			/**
			 * Log
			 */
			if (errors.isEmpty())
				TrickLogManager.Persist(LogType.ANALYSIS, "log.add_or_update.customer",
						String.format("Customer: %s", customer.getOrganisation()), principal.getName(),
						LogAction.CREATE_OR_UPDATE, customer.getOrganisation());
		} catch (Exception e) {
			errors.put("customer",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}
		return errors;
	}

	/**
	 * 
	 * Delete single customer
	 * 
	 */
	@RequestMapping(value = "/{customerId}/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteCustomer(@PathVariable("customerId") int customerId, Principal principal,
			Locale locale) throws Exception {
		try {
			if (!serviceCustomer.hasAccess(principal.getName(), customerId))
				throw new AccessDeniedException(
						messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
			customDelete.deleteCustomer(customerId, principal.getName());
			return JsonMessage.Success(messageSource.getMessage("success.customer.delete.successfully", null,
					"Customer was deleted successfully", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		}
	}

	@GetMapping(value = "/{customerId}/Template/Manage", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String templateForm(@PathVariable("customerId") int customerId, Model model, Principal principal,
			Locale locale) {
		final Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		if (customer == null || !customer.isCanBeUsed())
			throw new AccessDeniedException(
					messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));
		final List<TrickTemplate> templates = new LinkedList<>(defaultReportTemplateLoader.findAll());
		final Map<String, String> versions = templates.stream()
				.collect(Collectors.toMap(TrickTemplate::getKey, TrickTemplate::getVersion));
		customer.getTemplates().stream()
				.sorted((p1, p2) -> NaturalOrderComparator.compareTo(p1.getVersion(), p2.getVersion())).forEach(p -> {
					templates.add(p);
					p.setOutToDate(!p.getVersion().equalsIgnoreCase(versions.get(p.getKey())));
				});
		model.addAttribute("customer", customer);
		model.addAttribute("versions", versions.values().stream()
				.sorted(NaturalOrderComparator::compareTo).collect(Collectors.toList()));
		model.addAttribute("templates", templates);
		model.addAttribute("analysisTypes", AnalysisType.values());
		model.addAttribute("types", TrickTemplateType.values());
		model.addAttribute("languages", serviceLanguage.getByAlpha3("ENG", "FRA"));
		model.addAttribute("maxFileSize", Math.min(maxUploadFileSize, maxTemplateSize));
		return "jsp/knowledgebase/customer/template/home";
	}

	@PostMapping(value = "/{customerId}/Template/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object templateSave(@PathVariable("customerId") int customerId,
			@ModelAttribute TemplateForm templateForm, Model model, Principal principal,
			Locale locale) {
		final Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		if (customer == null || !customer.isCanBeUsed())
			throw new AccessDeniedException(
					messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));

		final Map<String, Object> result = new LinkedHashMap<>();

		if (templateForm.getType() == null && templateForm.getId() < 1) {
			result.put("type",
					messageSource.getMessage("error.report.template.type", null, "Type cannot be empty", locale));
		} else {
			final TrickTemplate template;

			if (templateForm.getId() > 0)
				template = serviceTrickTemplate.findByIdAndCustomer(templateForm.getId(), customerId);
			else {
				template = new TrickTemplate(templateForm.getType());
				if (templateForm.getType() == TrickTemplateType.REPORT) {
					if (templateForm.getAnalysisType() != null)
						template.setAnalysisType(templateForm.getAnalysisType());
					else
						result.put("analysisType",
								messageSource.getMessage("error.template.analysis.type", null,
										"Analysis type cannot be empty",
										locale));
				}
			}

			if (template == null)
				return JsonMessage.Error(
						messageSource.getMessage("error.template.not_exist", null, "Template does not exist", locale));

			template.setVersion(templateForm.getVersion());

			if (!templateForm.getFile().isEmpty()) {

				try {
					long maxSize = Math.min(maxUploadFileSize, maxTemplateSize);
					if (templateForm.getFile().getSize() > maxSize)
						result.put("file", messageSource.getMessage("error.file.too.large", new Object[] { maxSize },
								"File is to large", locale));
					else {
						template.setName(templateForm.getFile().getOriginalFilename());
						template.setLength(templateForm.getFile().getSize());
						if (DefaultTemplateLoader.checkTemplate(templateForm.getFile().getInputStream(),
								templateForm.getType()))
							template.setData(templateForm.getFile().getBytes());
						else
							result.put("file",
									messageSource.getMessage("error.file.no.docx", null, "Docx file is excepted",
											locale));
					}
				} catch (IOException e) {
					result.put("file",
							messageSource.getMessage("error.file.not.updated", null, "File cannot be loaded", locale));
				}
			}

			if (template.getData() == null || template.getData().length == 0)
				result.put("file",
						messageSource.getMessage("error.report.template.file.empty", null, "File cannot be empty",
								locale));

			if (!StringUtils.hasText(templateForm.getLabel()))
				result.put("label", messageSource.getMessage("error.report.template.label.empty", null,
						"Title cannot be empty", locale));
			else {
				templateForm.setLabel(templateForm.getLabel().trim());
				if (templateForm.getLabel().length() == 0)
					result.put("label", messageSource.getMessage("error.report.template.label.empty", null,
							"Title cannot be empty", locale));
				else if (templateForm.getLabel().getBytes(StandardCharsets.UTF_8).length > 255)
					result.put("label", messageSource.getMessage("error.report.template.label.too.long", null,
							"Title is to long.", locale));
				else
					template.setLabel(templateForm.getLabel());
			}

			if (!StringUtils.hasText(templateForm.getVersion()))
				result.put("version", messageSource.getMessage("error.report.template.version.empty", null,
						"Version cannot be empty", locale));
			else {
				templateForm.setVersion(templateForm.getVersion().trim());
				if (templateForm.getVersion().length() == 0)
					result.put("version", messageSource.getMessage("error.report.template.version.empty", null,
							"Version cannot be empty", locale));
				else if (templateForm.getVersion().getBytes(StandardCharsets.UTF_8).length > 255)
					result.put("version", messageSource.getMessage("error.report.template.version.too.long", null,
							"Version is to long", locale));
				else
					template.setVersion(templateForm.getVersion());
			}

			if (templateForm.getLanguage() == -2)
				template.setLanguage(null);
			else {
				template.setLanguage(serviceLanguage.get(templateForm.getLanguage()));
				if (template.getLanguage() == null)
					result.put("language",
							messageSource.getMessage("error.language.not.found", null, "Lnaguage cannot be found",
									locale));
			}

			if (result.isEmpty()) {
				if (template.getId() < 1)
					customer.getTemplates().add(template);
				template.setEditable(true);
				template.setCreated(new Timestamp(System.currentTimeMillis()));
				serviceCustomer.saveOrUpdate(customer);
				return templateForm.getId() > 0
						? JsonMessage.Success(messageSource.getMessage("success.report.template.update", null, locale))
						: JsonMessage.Success(messageSource.getMessage("success.report.template.save", null, locale));
			}
		}

		return result;
	}

	@DeleteMapping(value = "/{customerId}/Template", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Object deleteTemplate(@PathVariable("customerId") int customerId, Principal principal,
			@RequestBody List<Long> ids, Locale locale) {
		Customer customer = serviceCustomer.getFromUsernameAndId(principal.getName(), customerId);
		if (customer == null)
			throw new AccessDeniedException(
					messageSource.getMessage("error.customer.not_exist", null, "Customer does not exist", locale));

		final TrickTemplate emptyTempalte = new TrickTemplate();
		final Map<Long, TrickTemplate> templates = ids.parallelStream()
				.collect(Collectors.toMap(Function.identity(), i -> emptyTempalte));

		customer.getTemplates()
				.removeIf(p -> templates.containsKey(p.getId()) && emptyTempalte.equals(templates.put(p.getId(), p)));
		templates.entrySet().removeIf(e -> e.getValue().equals(emptyTempalte));
		if (!templates.isEmpty()) {
			serviceCustomer.saveOrUpdate(customer);
			serviceTrickTemplate.delete(templates.values());
		}
		return JsonMessage.Success(
				messageSource.getMessage("success.report.template.delete", new Object[] { ids.size() }, locale));
	}

	/**
	 * download: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Template/{id}/Download")
	public String downloadReport(@PathVariable Long id, Principal principal, HttpServletResponse response,
			Locale locale) throws Exception {

		TrickTemplate template = serviceTrickTemplate.findOne(id);

		// if file could not be found retrun 404 error
		if (template == null)
			return "jsp/errors/404";

		Customer customer = serviceCustomer.findByReportTemplateId(id);

		if (customer == null)
			return "jsp/errors/404";

		if (customer.isCanBeUsed() && !serviceCustomer.hasAccess(principal.getName(), customer.getId()))
			throw new AccessDeniedException(
					messageSource.getMessage("error.permission_denied", null, "Permission denied!", locale));

		final String extension = FileNameUtils.getExtension(template.getName());

		// set response contenttype to extension
		response.setContentType(extension);

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ String.format("%s_v%s.%s", template.getLabel(), template.getVersion(), extension) + "\"");

		// set sqlite file size as response size
		response.setContentLength((int) template.getLength());

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		FileCopyUtils.copy(template.getData(), response.getOutputStream());
		/**
		 * Log
		 */

		if (template.getType() == TrickTemplateType.REPORT) {
			TrickLogManager.Persist(LogType.ANALYSIS, "log.customer.report.template.download",
					String.format("Customer: %s, Template: %s, version: %s, created at: %s, type: %s, Language: %s",
							customer.getContactPerson(), template.getLabel(),
							template.getVersion(), template.getCreated(), template.getAnalysisType(),
							template.getLanguage().getAlpha3()),
					principal.getName(), LogAction.DOWNLOAD, customer.getContactPerson(), template.getLabel(),
					template.getVersion(),
					String.valueOf(template.getCreated()), String.valueOf(template.getAnalysisType()),
					template.getLanguage().getAlpha3());
		} else {
			TrickLogManager.Persist(LogType.ANALYSIS, "log.customer.template.download",
					String.format("Customer: %s, Template: %s, version: %s, created at: %s, type: %s, Language: %s",
							customer.getContactPerson(), template.getLabel(),
							template.getVersion(), template.getCreated(), template.getType(),
							template.getLanguage().getAlpha3()),
					principal.getName(), LogAction.DOWNLOAD, customer.getContactPerson(), template.getLabel(),
					template.getVersion(),
					String.valueOf(template.getCreated()), String.valueOf(template.getType()),
					template.getLanguage().getAlpha3());
		}

		// return
		return null;
	}

	@Value("${app.settings.report.template.max.size}")
	public void setMaxTemplateSize(String value) {
		this.maxTemplateSize = DataSize.parse(value).toBytes();
	}

	@Value("${spring.servlet.multipart.max-file-size}")
	public void setMaxUploadFileSize(String value) {
		this.maxUploadFileSize = DataSize.parse(value).toBytes();
	}

}