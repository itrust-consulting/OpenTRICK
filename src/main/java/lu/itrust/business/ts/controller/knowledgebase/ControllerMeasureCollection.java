package lu.itrust.business.ts.controller.knowledgebase;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findSheet;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.findTable;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getOrCreateRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getWorksheetPart;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.TablePart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.xlsx4j.sml.CTTable;
import org.xlsx4j.sml.CTTableColumn;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerImportStandard;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.MeasureManager;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.database.service.ServiceMeasureDescription;
import lu.itrust.business.ts.database.service.ServiceMeasureDescriptionText;
import lu.itrust.business.ts.database.service.ServiceStandard;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.AddressRef;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.model.standard.measuredescription.helper.ComparatorMeasureDescription;
import lu.itrust.business.ts.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.ts.validator.MeasureDescriptionValidator;
import lu.itrust.business.ts.validator.StandardValidator;
import lu.itrust.business.ts.validator.field.ValidatorField;

/**
 * ControllerStandard.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à r.l
 * @version 0.1
 * @since Oct 14, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Standard")
public class ControllerMeasureCollection {

	@Autowired
	private ServiceStandard serviceStandard;

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;

	@Autowired
	private ServiceMeasureDescriptionText serviceMeasureDescriptionText;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private TaskExecutor executor;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MeasureManager measureManager;

	@Autowired
	private ServiceStorage serviceStorage;

	@Value("${app.settings.standard.template.path}")
	private String template;

	@Value("${app.settings.version}${app.settings.version.revision}")
	private String appVersion;

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String displayAll(Model model) {
		model.addAttribute("standards", serviceStandard.getAllNotBoundToAnalysis());
		return "jsp/knowledgebase/standards/standard/standards";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String section(Model model) {
		// call default
		return displayAll(model);
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
	public @ResponseBody Map<String, String> save(@RequestBody String value, Principal principal, Locale locale) {

		// init errors list
		final Map<String, String> errors = new LinkedHashMap<>();

		// create new empty object
		final Standard standard = new Standard();

		// build standard object
		if (!buildStandard(errors, standard, value, locale))
			return errors;

		// check if standard has to be create (new) or updated
		if (standard.getId() < 1) {
			if (serviceStandard.existsByLabelAndVersion(standard.getLabel(), standard.getVersion()))
				errors.put("version", messageSource.getMessage("error.norm.version.duplicate", null,
						"Version already exists", locale));
			else
				serviceStandard.save(standard);
			/**
			 * Log
			 */
			TrickLogManager.persist(LogType.KNOWLEDGE_BASE, "log.standard.add",
					String.format("Standard: %s, version: %d", standard.getLabel(), standard.getVersion()),
					principal.getName(), LogAction.CREATE, standard.getLabel(), String.valueOf(standard.getVersion()));
		} else {
			Standard persited = serviceStandard.get(standard.getId());
			if (persited == null)
				errors.put("standard",
						messageSource.getMessage("error.norm.not_exist", null, "Norm does not exist", locale));
			else if (persited.isAnalysisOnly())
				errors.put("standard", messageSource.getMessage("error.norm.manage_analysis_standard", null,
						"This standard can only be managed within the selected analysis where this standard belongs!",
						locale));
			else if (!persited.getType().equals(standard.getType()) && serviceStandard.isUsed(persited))
				errors.put("type", messageSource.getMessage("error.norm.type.update", null,
						"Standard is in use, type cannot be updated!", locale));
			else {

				if (!persited.getName().equalsIgnoreCase(standard.getName())
						&& serviceStandard.isNameConflicted(standard.getName(), persited.getName()))
					errors.put("name", messageSource.getMessage("error.norm.rename.name.conflict", null, locale));

				if (!persited.getLabel().equalsIgnoreCase(standard.getLabel())) {
					if (serviceStandard.isLabelConflicted(standard.getLabel(), persited.getLabel()))
						errors.put("label", messageSource.getMessage("error.norm.rename.label.conflict", null, locale));
					else {
						final List<Standard> standards = serviceStandard
								.findByLabelAndAnalysisOnlyFalse(persited.getLabel());
						standards.remove(persited);
						for (Standard std : standards) {
							if (serviceStandard.existsByLabelAndVersion(standard.getLabel(), std.getVersion())) {
								errors.put("standard",
										messageSource.getMessage("error.norm.rename.sub.version", null, locale));
								break;
							}
						}
						final String oldName = persited.getLabel();
						if (errors.isEmpty()) {
							standards.forEach(s -> {
								s.setLabel(standard.getLabel());
								serviceStandard.saveOrUpdate(s);
								TrickLogManager.persist(LogType.KNOWLEDGE_BASE, "log.standard.rename.label",
										String.format("Standard, name: %s, version: %d, old name: %s, old version: %d",
												s.getLabel(), s.getVersion(), oldName, s.getVersion()),
										principal.getName(), LogAction.RENAME, s.getLabel(),
										String.valueOf(s.getVersion()), oldName, String.valueOf(s.getVersion()));
							});
							final int oldVersion = standard.getVersion();
							serviceStandard.saveOrUpdate(persited.update(standard));
							TrickLogManager.persist(LogType.KNOWLEDGE_BASE, "log.standard.rename.label",
									String.format("Standard, name: %s, version: %d, old name: %s, old version: %d",
											persited.getLabel(), persited.getVersion(), oldName,
											oldVersion),
									principal.getName(), LogAction.RENAME, persited.getLabel(),
									String.valueOf(persited.getVersion()), oldName, String.valueOf(oldVersion));
						}
					}
				} else if (errors.isEmpty()) {
					serviceStandard.saveOrUpdate(persited.update(standard));
					/**
					 * Log
					 */
					TrickLogManager.persist(LogType.KNOWLEDGE_BASE, "log.standard.update",
							String.format("Standard: %s, version: %d", persited.getLabel(), persited.getVersion()),
							principal.getName(), LogAction.UPDATE, persited.getLabel(),
							String.valueOf(persited.getVersion()));
				}
			}

		}
		return errors;
	}

	/**
	 * deleteStandard: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Delete/{idStandard}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteStandard(@PathVariable("idStandard") Integer idStandard, Principal principal,
			Locale locale) throws Exception {

		try {
			Standard standard = serviceStandard.get(idStandard);
			if (standard.isAnalysisOnly())
				return JsonMessage.error(messageSource.getMessage("error.norm.manage_analysis_standard", null,
						"This standard can only be managed within the selected analysis where this standard belongs!",
						locale));
			// try to delete the standard
			customDelete.deleteStandard(standard);
			/**
			 * Log
			 */
			TrickLogManager.persist(LogLevel.WARNING, LogType.KNOWLEDGE_BASE, "log.standard.delete",
					String.format("Standard: %s, version: %d", standard.getName(), standard.getVersion()),
					principal.getName(), LogAction.DELETE, standard.getName(),
					String.valueOf(standard.getVersion()), principal.getName());
			// return success message
			return JsonMessage.success(messageSource.getMessage("success.norm.delete.successfully", null,
					"Standard was deleted successfully", locale));
		} catch (TrickException e) {
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.persist(e);
			String[] parts = e.getMessage().split(":");
			String code = parts[0];
			String defaultmessage = parts[1];
			return JsonMessage.error(messageSource.getMessage(code, null, defaultmessage, locale));
		}
	}

	@RequestMapping(value = "/Template", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Resource> downloadTemplate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		final String filename = String.format(Constant.ITR_FILE_NAMING,
				"R5xx_STA_TSE",
				"KB",
				"Template", "MeasureCollection", appVersion,
				"xlsx");
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
				.body(serviceStorage.loadAsResource(template));
	}

	/**
	 * uploadStandard: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Upload", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String uploadStandard() throws Exception {
		return "jsp/knowledgebase/standards/standard/uploadForm";
	}

	/**
	 * importNewStandard: <br>
	 * Description
	 * 
	 * @param file
	 * @param principal
	 * @param request
	 * @param attributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String importNewStandard(@RequestParam(value = "file") MultipartFile file, Principal principal,
			RedirectAttributes attributes, Locale locale)
			throws Exception {
		final String filename = ServiceStorage.RandoomFilename();
		final Worker worker = new WorkerImportStandard(filename);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale))
			return JsonMessage.error(messageSource.getMessage("error.task_manager.too.many", null,
					"Too many tasks running in background", locale));
		serviceStorage.store(file, filename);
		executor.execute(worker);
		return JsonMessage.success(messageSource.getMessage("success.start.import.standard", null,
				"Importing of measure collection", locale));
	}

	/**
	 * exportStandard: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param principal
	 * @param request
	 * @param locale
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws Docx4JException
	 * @throws TrickException
	 * @throws Exception
	 */
	@GetMapping(value = "/Export/{idStandard}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public void exportStandard(@PathVariable("idStandard") Integer idStandard, Principal principal,
			HttpServletRequest request, HttpServletResponse response, Locale locale)
			throws TrickException, Docx4JException, IOException {
		if (measureManager.exportStandard(idStandard, response, principal.getName()))
			throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND,
					messageSource.getMessage("error.standard.not_found", null, "Standard not found", locale));

	}

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param idLanguage
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{idStandard}/Language/{idLanguage}/Measures")
	public String displayAll(@PathVariable int idStandard, @PathVariable int idLanguage, HttpServletRequest request,
			Model model) throws Exception {

		final Standard standard = serviceStandard.get(idStandard);
		if (standard != null) {
			// load all measuredescriptions of a standard
			final List<MeasureDescription> mesDescsDescriptions = serviceMeasureDescription.getAllByStandard(standard);

			// chekc if measuredescriptions are not null

			final List<Language> languages = serviceLanguage.getAll();

			// set language
			Language lang = null;
			if (idLanguage != 0)
				lang = languages.stream().filter(e -> e.getId() == idLanguage).findAny().orElse(null);
			if (lang == null) {
				lang = languages.stream().filter(e -> e.getAlpha2().equalsIgnoreCase("en")).findAny().orElse(null);
				if (lang == null && !languages.isEmpty())
					lang = languages.get(0);
			}

			Collections.sort(mesDescsDescriptions, new ComparatorMeasureDescription());
			// put data to model
			model.addAttribute("selectedLanguage", lang);
			model.addAttribute("languages", languages);
			model.addAttribute("standard", standard);
			model.addAttribute("measureDescriptions", mesDescsDescriptions);
		}
		return "jsp/knowledgebase/standards/measure/section";
	}

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{idStandard}/Language/{idLanguage}/Measures/{idMeasure}")
	public String displaySingle(@PathVariable int idStandard, @PathVariable int idLanguage, @PathVariable int idMeasure,
			HttpServletRequest request, HttpServletResponse response,
			Model model, Locale locale) throws Exception {

		// load all measuredescriptions of a standard
		MeasureDescription mesDesc = serviceMeasureDescription.get(idMeasure);

		// chekc if measuredescriptions are not null
		if (mesDesc != null) {

			// set language
			Language lang = null;
			if (idLanguage != 0) {
				lang = serviceLanguage.get(idLanguage);
			} else {
				lang = serviceLanguage.getByAlpha3("ENG");
			}

			// load only from language
			MeasureDescriptionText mesDescText = serviceMeasureDescriptionText
					.getForMeasureDescriptionAndLanguage(mesDesc.getId(), lang.getId());

			// put data to model
			model.addAttribute("standard", serviceStandard.get(idStandard));
			model.addAttribute("measureDescription", mesDesc);
			model.addAttribute("measureDescriptionText", mesDescText);
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		return "jsp/knowledgebase/standards/measure/measure";
	}

	/**
	 * displayAddForm: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{idStandard}/Measures/Add")
	public String displayAddForm(@PathVariable("idStandard") Integer idStandard, HttpServletRequest request,
			Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.getAll();

		// add languages to model
		model.addAttribute("languages", languages);

		// select first language
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "jsp/knowledgebase/standards/measure/measuredescriptionform";
	}

	/**
	 * displayEditForm: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{idStandard}/Measures/{idMeasure}/Edit")
	public String displayEditForm(@PathVariable("idStandard") Integer idStandard, @PathVariable int idMeasure,
			HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.getAll();

		// load measuredescriptiontexts of measuredescription
		List<MeasureDescriptionText> mesDesc = serviceMeasureDescription.get(idMeasure).getMeasureDescriptionTexts();

		MeasureDescription md = null;

		if (mesDesc.isEmpty())
			return "jsp/knowledgebase/standards/measure/measuredescriptioneditform";

		md = mesDesc.get(0).getMeasureDescription();

		// add languages to model
		model.addAttribute("languages", languages);

		for (Language lang : languages) {
			boolean found = false;
			for (MeasureDescriptionText mdt : mesDesc) {
				if (mdt.getLanguage().getId() == lang.getId()) {
					found = true;
					break;
				}
			}
			if (!found) {
				MeasureDescriptionText mdt = new MeasureDescriptionText();
				mdt.setLanguage(lang);
				mdt.setMeasureDescription(md);
				mesDesc.add(mdt);
			}
		}

		// add texts to model
		model.addAttribute("measuredescriptionTexts", mesDesc);

		// add to model: first language as selected
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "jsp/knowledgebase/standards/measure/measuredescriptioneditform";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/{idStandard}/Measures/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody Map<String, String> save(@PathVariable("idStandard") int idStandard, @RequestBody String value,
			Locale locale) {
		// create error list
		Map<String, String> errors = new LinkedHashMap<>();
		try {
			// create json parser
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode jsonNode = mapper.readTree(value);

			// retrieve measure id
			final int id = jsonNode.get("id").asInt();
			boolean isNew = false;
			// create new empty measuredescription object
			MeasureDescription measureDescription = serviceMeasureDescription.get(id);

			if ((isNew = measureDescription == null)) {
				// retrieve standard
				measureDescription = new MeasureDescription();
				Standard standard = serviceStandard.get(idStandard);
				if (standard == null)
					errors.put("measureDescription.norm",
							messageSource.getMessage("error.norm.not_found", null, "Standard is not exist", locale));
				measureDescription.setStandard(standard);
			} else if (measureDescription.getStandard().getId() != idStandard)
				errors.put("measureDescription.norm",
						messageSource.getMessage("error.measure_description.norm.not_matching", null,
								"Measure description or standard is not exist", locale));

			if (errors.isEmpty() && buildMeasureDescription(errors, measureDescription, jsonNode, locale)) {
				if (isNew)
					measureManager.createNewMeasureForAllAnalyses(measureDescription);
				else
					serviceMeasureDescription.saveOrUpdate(measureDescription);
			}

			// System.out.println(measureDescription.isComputable()==true?"TRUE":"FALSE");

			// return errors
			return errors;
		}

		catch (Exception e) {

			// return errors
			errors.put("measuredescription",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.persist(e);
			return errors;
		}

	}

	/**
	 * deleteMeasureDescription: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param measureid
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idStandard}/Measures/Delete/{idMeasure}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard,
			@PathVariable("idMeasure") int idMeasure, Locale locale) {
		try {
			// try to delete measure
			final MeasureDescription measureDescription = serviceMeasureDescription.get(idMeasure);
			if (measureDescription == null || measureDescription.getStandard().getId() != idStandard)
				return JsonMessage.error(
						messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
			else if (serviceMeasureDescription.isUsed(measureDescription))
				return JsonMessage.error(messageSource.getMessage("error.measure.delete.failed", null,
						"Measure deleting was failed: Standard might be in used", locale));
			else
				serviceMeasureDescription.delete(measureDescription);
			// return success message
			return JsonMessage.success(messageSource.getMessage("success.measure.delete.successfully", null,
					"Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	/**
	 * deleteMeasureDescription: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param measureid
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize(Constant.ROLE_SUPERVISOR_ONLY)
	@RequestMapping(value = "/{idStandard}/Measures/Force/Delete/{idMeasureDescription}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String forceDeleteMeasureDescription(@PathVariable("idStandard") int idStandard,
			@PathVariable("idMeasureDescription") int idMeasureDescription,
			Principal principal, Locale locale) {
		try {
			if (!serviceMeasureDescription.exists(idMeasureDescription, idStandard))
				return JsonMessage.error(
						messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
			customDelete.forceDeleteMeasureDescription(idMeasureDescription, principal);
			return JsonMessage.success(messageSource.getMessage("success.measure.delete.successfully", null,
					"Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.persist(e);
			return JsonMessage.error(messageSource.getMessage("error.measure.delete.failed", null,
					"Measure deleting was failed: Standard might be in used", locale));
		}
	}

	/**
	 * buildMeasureDescription: <br>
	 * Description
	 * 
	 * @param errors
	 * @param measuredescription
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildMeasureDescription(Map<String, String> errors, MeasureDescription measuredescription,
			JsonNode jsonNode, Locale locale) {
		try {

			String reference = jsonNode.get("reference").asText();
			Boolean computable = jsonNode.get("computable").asBoolean(false);
			if (!serviceDataValidation.isRegistred(MeasureDescription.class))
				serviceDataValidation.register(new MeasureDescriptionValidator());

			if (!serviceDataValidation.isRegistred(MeasureDescriptionText.class))
				serviceDataValidation.register(new MeasureDescriptionTextValidator());

			String error = serviceDataValidation.validate(measuredescription, "reference", reference);

			if (error != null)
				errors.put("measuredescription.reference",
						serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				reference = reference.trim();
				if (measuredescription.getId() < 1 && serviceMeasureDescription
						.existsForMeasureByReferenceAndStandard(reference, measuredescription.getStandard()))
					errors.put("measuredescription.reference",
							messageSource.getMessage("error.measuredescription.reference.duplicate", null,
									"Reference already exists in this standard", locale));
				else
					measuredescription.setReference(reference);
			}

			// error = serviceDataValidation.validate(measuredescription, "level", level);

			if (error != null)
				errors.put("measuredescription.level", serviceDataValidation.ParseError(error, messageSource, locale));

			error = serviceDataValidation.validate(measuredescription, "computable", computable);

			if (error != null)
				errors.put("measuredescription.computable",
						serviceDataValidation.ParseError(error, messageSource, locale));
			else
				measuredescription.setComputable(computable);

			// load languages
			List<Language> languages = serviceLanguage.getAll();

			ValidatorField validator = serviceDataValidation.findByClass(MeasureDescriptionText.class);

			// parse all languages
			for (int i = 0; i < languages.size(); i++) {

				// get language
				Language language = languages.get(i);

				// get domain in this language
				String domain = jsonNode.get("domain_" + language.getId()).asText().trim();

				// get description in this language
				String description = jsonNode.get("description_" + language.getId()).asText().trim();

				if (domain.equals(Constant.EMPTY_STRING) && description.equals(Constant.EMPTY_STRING))
					continue;

				// init measdesctext object
				MeasureDescriptionText mesDescText = measuredescription.findByLanguage(language);

				// if new measure or text for this language does not exist:
				// create new text and save
				if (mesDescText == null) {
					// create new and add data
					mesDescText = new MeasureDescriptionText();
					mesDescText.setLanguage(language);
					measuredescription.addMeasureDescriptionText(mesDescText);
				}

				error = validator.validate(mesDescText, "domain", domain);

				if (error != null)
					errors.put("measureDescriptionText.domain_" + language.getId(),
							serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDomain(domain);

				error = validator.validate(mesDescText, "description", description);

				if (error != null)
					errors.put("measureDescriptionText.description_" + language.getId(),
							serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDescription(description);
			}
			// return success message
			return errors.isEmpty();

		} catch (Exception e) {

			// return error message
			errors.put("measureDescription",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.persist(e);
			return false;
		}
	}

	/**
	 * buildStandard: <br>
	 * Description
	 * 
	 * @param errors
	 * @param standard
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildStandard(Map<String, String> errors, Standard standard, String source, Locale locale) {

		try {

			// create json parser
			final JsonNode jsonNode = new ObjectMapper().readTree(source);

			ValidatorField validator = serviceDataValidation.findByClass(Standard.class);

			if (validator == null)
				serviceDataValidation.register(validator = new StandardValidator());

			// load standard id
			final int id = jsonNode.get("id").asInt();

			final String label = jsonNode.get("label").asText();

			final String name = jsonNode.get("name").asText();

			final String description = jsonNode.get("description").asText();

			StandardType type = StandardType.getByName(jsonNode.get("type").asText());

			Integer version = null;

			try {
				version = jsonNode.get("version").asInt();
			} catch (NumberFormatException e) {
				TrickLogManager.persist(e);
			}

			// check if standard has to be updated
			if (id > 0)
				// init id
				standard.setId(id);

			// set data
			String error = validator.validate(standard, "label", label);
			if (error != null)
				errors.put("label", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setLabel(label);

			error = validator.validate(standard, "name", name);

			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setName(name);

			error = validator.validate(standard, "version", version);

			if (error != null)
				errors.put("version", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setVersion(version);

			error = validator.validate(standard, "description", description);

			if (error != null)
				errors.put("description", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setDescription(description);

			error = validator.validate(standard, "type", type);

			if (error != null)
				errors.put("type", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				standard.setType(type);

			// set computable flag
			standard.setComputable(jsonNode.get("computable").asBoolean(false));

			standard.setAnalysisOnly(false);

			// return success
			return errors.isEmpty();

		} catch (Exception e) {
			// return error
			errors.put("standard",
					messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.persist(e);
			return false;
		}
	}
}