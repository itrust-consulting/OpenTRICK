package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerImportStandard;
import lu.itrust.business.TS.component.CustomDelete;
import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceDataValidation;
import lu.itrust.business.TS.database.service.ServiceLanguage;
import lu.itrust.business.TS.database.service.ServiceMeasureDescription;
import lu.itrust.business.TS.database.service.ServiceMeasureDescriptionText;
import lu.itrust.business.TS.database.service.ServiceStandard;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureManager;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.model.standard.measuredescription.helper.ComparatorMeasureDescription;
import lu.itrust.business.TS.validator.MeasureDescriptionTextValidator;
import lu.itrust.business.TS.validator.MeasureDescriptionValidator;
import lu.itrust.business.TS.validator.StandardValidator;
import lu.itrust.business.TS.validator.field.ValidatorField;

/**
 * ControllerStandard.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 14, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Standard")
public class ControllerKnowledgeBaseStandard {

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
	private SessionFactory sessionFactory;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	@Autowired
	private WorkersPoolManager workersPoolManager;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MeasureManager measureManager;

	@Value("${app.settings.standard.template.path}")
	private String template;

	/**
	 * displayAll: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping
	public String displayAll(Model model) throws Exception {

		// load all standards to model

		model.addAttribute("standards", serviceStandard.getAllNotBoundToAnalysis());
		return "knowledgebase/standards/standard/standards";
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
	public String section(Model model) throws Exception {

		// call default
		return displayAll(model);
	}

	/**
	 * loadSingleStandard: <br>
	 * Description
	 * 
	 * @param idStandard
	 * @param model
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{idStandard}")
	public String loadSingleStandard(@PathVariable("idStandard") String idStandard, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {

		// load standard object
		Standard standard = serviceStandard.getStandardByName(idStandard);
		if (standard == null) {

			// retrun error if standard does not exist
			String msg = messageSource.getMessage("error.norm.not_exist", null, "Norm does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Standard";
		}

		// load standard to model
		model.put("standard", standard);

		return "knowledgebase/standards/standard/showStandard";
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
		Map<String, String> errors = new LinkedHashMap<String, String>();

		try {
			// create new empty object
			Standard standard = new Standard();

			// build standard object
			if (!buildStandard(errors, standard, value, locale))
				return errors;

			// check if standard has to be create (new) or updated
			if (standard.getId() < 1) {

				try {
					serviceStandard.save(standard);
					/**
					 * Log
					 */
					TrickLogManager.Persist(LogType.ANALYSIS, "log.standard.add", String.format("Standard: %s, version: %d", standard.getLabel(), standard.getVersion()),
							principal.getName(), LogAction.CREATE, standard.getLabel(), String.valueOf(standard.getVersion()));
				} catch (Exception e) {
					TrickLogManager.Persist(e);
					errors.put("version", messageSource.getMessage("error.norm.version.duplicate", null, "Version already exists", locale));
				}

			} else {

				Standard tmpStandard = serviceStandard.get(standard.getId());
				if (tmpStandard == null)
					errors.put("standard", messageSource.getMessage("error.norm.not_exist", null, "Norm does not exist", locale));
				else if (!tmpStandard.isAnalysisOnly()) {

					if (!tmpStandard.getType().equals(standard.getType()) && serviceStandard.isUsed(tmpStandard))
						errors.put("type", messageSource.getMessage("error.norm.type.update", null, "Standard is in use, type cannot be updated!", locale));
					else {
						serviceStandard.saveOrUpdate(tmpStandard.update(standard));
						/**
						 * Log
						 */
						TrickLogManager.Persist(LogType.ANALYSIS, "log.standard.update", String.format("Standard: %s, version: %d", standard.getLabel(), standard.getVersion()),
								principal.getName(), LogAction.UPDATE, standard.getLabel(), String.valueOf(standard.getVersion()));
					}
				} else
					errors.put("standard", messageSource.getMessage("error.norm.manage_analysis_standard", null,
							"This standard can only be managed within the selected analysis where this standard belongs!", locale));
			}

			// errors
		} catch (Exception e) {
			// return errors
			errors.put("standard", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
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
	public @ResponseBody String deleteStandard(@PathVariable("idStandard") Integer idStandard, Principal principal, Locale locale) throws Exception {

		try {
			Standard standard = serviceStandard.get(idStandard);
			if (standard.isAnalysisOnly())
				return JsonMessage.Error(messageSource.getMessage("error.norm.manage_analysis_standard", null,
						"This standard can only be managed within the selected analysis where this standard belongs!", locale));
			// try to delete the standard
			customDelete.deleteStandard(standard);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.standard.delete",
					String.format("Standard: %s, version: %d", standard.getLabel(), standard.getVersion()), principal.getName(), LogAction.DELETE, standard.getLabel(),
					String.valueOf(standard.getVersion()), principal.getName());
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.norm.delete.successfully", null, "Standard was deleted successfully", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			String[] parts = e.getMessage().split(":");
			String code = parts[0];
			String defaultmessage = parts[1];
			return JsonMessage.Error(messageSource.getMessage(code, null, defaultmessage, locale));
		}
	}

	@RequestMapping(value = "/Template", method = RequestMethod.GET)
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		File templateFile = new File(request.getServletContext().getRealPath(template));
		if (!(templateFile.exists() && templateFile.isFile()))
			throw new ResourceNotFoundException();
		response.setContentLength((int) templateFile.length());
		response.setContentType(FilenameUtils.getExtension(template));
		response.setHeader("Content-Disposition", "attachment; filename=\"Template.xlsx\"");
		Files.copy(templateFile.toPath(), response.getOutputStream());
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
		return "knowledgebase/standards/standard/uploadForm";
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
	@RequestMapping(value = "/Import", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public @ResponseBody String importNewStandard(@RequestParam(value = "file") MultipartFile file, Principal principal, HttpServletRequest request, RedirectAttributes attributes,
			Locale locale) throws Exception {
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");
		file.transferTo(importFile);
		Worker worker = new WorkerImportStandard(serviceTaskFeedback, sessionFactory, workersPoolManager, importFile);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId()))
			return JsonMessage.Error(messageSource.getMessage("failed.start.export.analysis", null, "Analysis export was failed", locale));
		executor.execute(worker);
		return JsonMessage.Success(messageSource.getMessage("success.start.import.standard", null, "Importing of measure collection", locale));

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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Export/{idStandard}", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String exportStandard(@PathVariable("idStandard") Integer idStandard, Principal principal, HttpServletRequest request, Locale locale, HttpServletResponse response)
			throws Exception {

		Standard standard = serviceStandard.get(idStandard);

		if (standard == null)
			return "404";

		XSSFWorkbook workbook = null;

		try {

			workbook = new XSSFWorkbook(request.getServletContext().getRealPath(template));

			XSSFSheet sheet = null;
			XSSFTable table = null;

			/**
			 * Standard
			 */

			sheet = workbook.getSheet("NormInfo");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("TableNormInfo")) {
					break;
				}
			}

			int row, namecol, versioncol, desccol, computablecol;

			namecol = table.getStartCellReference().getCol();
			versioncol = namecol + 1;
			desccol = versioncol + 1;
			computablecol = table.getEndCellReference().getCol();
			row = table.getStartCellReference().getRow() + 1;

			XSSFCell cell = null;

			// standard name
			cell = sheet.getRow(row).getCell(namecol);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(standard.getLabel());

			// standard version
			cell = sheet.getRow(row).getCell(versioncol);
			cell.setCellType(CellType.NUMERIC);
			cell.setCellValue(standard.getVersion());

			// standard description
			cell = sheet.getRow(row).getCell(desccol);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(standard.getDescription());

			// standard computable
			cell = sheet.getRow(row).getCell(computablecol);
			cell.setCellType(CellType.BOOLEAN);
			cell.setCellValue(standard.isComputable());

			/**
			 * Measures
			 */

			sheet = workbook.getSheet("NormData");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("TableNormData")) {
					break;
				}
			}

			List<MeasureDescription> measuredescriptions = serviceMeasureDescription.getAllByStandard(standard.getId());

			int levelcol, referencecol;
			levelcol = 0;
			referencecol = 1;
			computablecol = 2;

			List<Language> languages = serviceLanguage.getAll();

			int headerRow = 0;

			XSSFRow sheetrow = sheet.getRow(headerRow);
			XSSFCellStyle headerStyle = sheetrow.getCell(0).getCellStyle();

			int colnumber = 0;

			cell = sheetrow.getCell(colnumber);
			if (cell == null) {
				cell = sheetrow.createCell(colnumber);
			}
			cell.setCellValue("Level");
			cell.setCellStyle(headerStyle);

			colnumber++;

			cell = sheetrow.getCell(colnumber);
			if (cell == null) {
				cell = sheetrow.createCell(colnumber);
			}
			cell.setCellValue("Reference");
			cell.setCellStyle(headerStyle);
			colnumber++;

			cell = sheetrow.getCell(colnumber);
			if (cell == null) {
				cell = sheetrow.createCell(colnumber);
			}
			cell.setCellValue("Computable");
			cell.setCellStyle(headerStyle);
			colnumber++;

			for (Language language : languages) {

				XSSFCell domaincell = sheetrow.getCell(colnumber);
				XSSFCell desccell = sheetrow.getCell(colnumber + 1);

				if (domaincell == null) {
					domaincell = sheetrow.createCell(colnumber);
				}
				domaincell.setCellValue("Domain_" + language.getAlpha3());
				domaincell.setCellStyle(headerStyle);

				if (desccell == null) {
					desccell = sheetrow.createCell(colnumber + 1);
				}
				desccell.setCellValue("Description_" + language.getAlpha3());
				desccell.setCellStyle(headerStyle);
				colnumber = colnumber + 2;
			}

			CellReference ref1 = table.getStartCellReference();

			// update the table. coumn headers must match the corresponding
			// cells in
			// the sheet
			CTTableColumns cols = table.getCTTable().getTableColumns();
			cols.setTableColumnArray(null);
			cols.setCount(colnumber);
			CTTableColumn col = cols.addNewTableColumn();
			col.setName("domain");
			col.setId(4);
			for (int i = 4; i < colnumber; i++) {
				col = cols.addNewTableColumn();
				col.setName(sheetrow.getCell(i).getRawValue());
				col.setId(i + 1);
			}

			// update the "ref" attribute
			table.getCTTable().setRef(new CellRangeAddress((ref1.getRow()), measuredescriptions.size(), (ref1.getCol()), colnumber).formatAsString());

			// System.out.println("Rows: ("+(ref1.getRow()+1)
			// +":"+measuredescriptions.size()+"):::Cols: ("+ (ref1.getCol()+1)
			// +
			// ":"+ colnumber +")");

			row = 1;

			for (MeasureDescription measuredescription : measuredescriptions) {

				sheetrow = sheet.getRow(row);
				cell = sheetrow.getCell(levelcol);
				if (cell == null)
					cell = sheetrow.createCell(levelcol);
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(measuredescription.getLevel());

				cell = sheet.getRow(row).getCell(referencecol);
				if (cell == null)
					cell = sheetrow.createCell(referencecol);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(measuredescription.getReference());

				cell = sheet.getRow(row).getCell(computablecol);
				if (cell == null)
					cell = sheetrow.createCell(computablecol);
				cell.setCellType(CellType.BOOLEAN);
				cell.setCellValue(measuredescription.isComputable());

				int domaincol = computablecol + 1;

				int descriptioncol = domaincol + 1;

				for (Language language : languages) {

					MeasureDescriptionText measureDescriptionText = serviceMeasureDescriptionText.getForMeasureDescriptionAndLanguage(measuredescription.getId(), language.getId());

					String domain = "";

					String description = "";

					if (measureDescriptionText != null) {
						domain = measureDescriptionText.getDomain();
						description = measureDescriptionText.getDescription();
					}

					cell = sheet.getRow(row).getCell(domaincol);
					if (cell == null)
						cell = sheetrow.createCell(domaincol);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(domain);
					// System.out.println("Domaincol: "+domaincol);
					domaincol++;
					domaincol++;

					cell = sheet.getRow(row).getCell(descriptioncol);
					if (cell == null)
						cell = sheetrow.createCell(descriptioncol);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(description);
					// System.out.println("Desccol: "+descriptioncol);
					descriptioncol++;
					descriptioncol++;

				}

				row = row + 1;

				sheet.createRow(row);

			}

			/**
			 * Output
			 */

			ByteArrayOutputStream standardFile = new ByteArrayOutputStream();

			workbook.write(standardFile);

			String identifierName = "TL_TRICKService_Norm_" + standard.getLabel() + "_Version_" + standard.getVersion();

			// return standardFile to user

			// set response contenttype to sqlite
			response.setContentType("xlsx");

			// set sqlite file size as response size
			response.setContentLength(standardFile.size());

			// set response header with location of the filename
			response.setHeader("Content-Disposition", "attachment; filename=\"" + (identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".xlsx\"");

			// return the sqlite file (as copy) to the response outputstream (
			// whihc
			// creates on the
			// client side the sqlite file)

			response.getOutputStream().write(standardFile.toByteArray());

			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.export.standard",
					String.format("Standard: %s, version: %d", standard.getLabel(), standard.getVersion()), principal.getName(), LogAction.EXPORT, standard.getLabel(),
					String.valueOf(standard.getVersion()));
			// return
			return null;
		} finally {
			if (workbook != null)
				workbook.close();
		}

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
	public String displayAll(@PathVariable int idStandard, @PathVariable int idLanguage, HttpServletRequest request, Model model) throws Exception {

		// load all measuredescriptions of a standard
		List<MeasureDescription> mesDescs = serviceMeasureDescription.getAllByStandard(idStandard);

		// chekc if measuredescriptions are not null
		if (mesDescs != null) {

			// set language
			Language lang = null;
			if (idLanguage != 0) {
				lang = serviceLanguage.get(idLanguage);
			} else {
				lang = serviceLanguage.getByAlpha3("ENG");
			}

			// parse measuredescriptions and remove texts to add only selected
			// language text
			for (MeasureDescription mesDesc : mesDescs) {
				mesDesc.setMeasureDescriptionTexts(new ArrayList<MeasureDescriptionText>());
				// load only from language
				MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getForMeasureDescriptionAndLanguage(mesDesc.getId(), lang.getId());
				// check if not null
				if (mesDescText == null) {
					// create new empty descriptiontext with language
					mesDescText = new MeasureDescriptionText();
					mesDescText.setLanguage(lang);
				}
				mesDesc.addMeasureDescriptionText(mesDescText);
				// System.out.println(mesDescText.getDomain() + "::" +
				// mesDescText.getDescription());

			}
			Collections.sort(mesDescs, new ComparatorMeasureDescription());
			// put data to model
			model.addAttribute("selectedLanguage", lang);
			model.addAttribute("languages", serviceLanguage.getAll());
			model.addAttribute("standard", serviceStandard.get(idStandard));
			model.addAttribute("measureDescriptions", mesDescs);
		}
		return "knowledgebase/standards/measure/section";
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
	public String displaySingle(@PathVariable int idStandard, @PathVariable int idLanguage, @PathVariable int idMeasure, HttpServletRequest request, HttpServletResponse response,
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
			MeasureDescriptionText mesDescText = serviceMeasureDescriptionText.getForMeasureDescriptionAndLanguage(mesDesc.getId(), lang.getId());

			// put data to model
			model.addAttribute("standard", serviceStandard.get(idStandard));
			model.addAttribute("measureDescription", mesDesc);
			model.addAttribute("measureDescriptionText", mesDescText);
		}

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		return "knowledgebase/standards/measure/measure";
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
	public String displayAddForm(@PathVariable("idStandard") Integer idStandard, HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.getAll();

		// add languages to model
		model.addAttribute("languages", languages);

		// select first language
		if (languages != null) {
			model.addAttribute("selectedLanguage", languages.get(0));
		}

		return "knowledgebase/standards/measure/measuredescriptionform";
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
	public String displayEditForm(@PathVariable("idStandard") Integer idStandard, @PathVariable int idMeasure, HttpServletRequest request, Model model) throws Exception {

		// load all languages
		List<Language> languages = serviceLanguage.getAll();

		// load measuredescriptiontexts of measuredescription
		List<MeasureDescriptionText> mesDesc = serviceMeasureDescription.get(idMeasure).getMeasureDescriptionTexts();

		MeasureDescription md = null;

		if (mesDesc.isEmpty())
			return "knowledgebase/standards/measure/measuredescriptioneditform";

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

		return "knowledgebase/standards/measure/measuredescriptioneditform";
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
	public @ResponseBody Map<String, String> save(@PathVariable("idStandard") int idStandard, @RequestBody String value, Locale locale) {
		// create error list
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(value);

			// retrieve measure id
			int id = jsonNode.get("id").asInt();
			boolean isNew = false;
			// create new empty measuredescription object
			MeasureDescription measureDescription = serviceMeasureDescription.get(id);

			if ((isNew = measureDescription == null)) {
				// retrieve standard
				measureDescription = new MeasureDescription();
				Standard standard = serviceStandard.get(idStandard);
				if (standard == null)
					errors.put("measureDescription.norm", messageSource.getMessage("error.norm.not_found", null, "Standard is not exist", locale));
				measureDescription.setStandard(standard);
			} else if (measureDescription.getStandard().getId() != idStandard)
				errors.put("measureDescription.norm",
						messageSource.getMessage("error.measure_description.norm.not_matching", null, "Measure description or standard is not exist", locale));

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
			errors.put("measuredescription", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
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
	public @ResponseBody String deleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasure") int idMeasure, Locale locale) {
		try {
			// try to delete measure
			MeasureDescription measureDescription = serviceMeasureDescription.get(idMeasure);
			if (measureDescription == null || measureDescription.getStandard().getId() != idStandard)
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
			customDelete.delete(measureDescription);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in used", locale));
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
	public @ResponseBody String forceDeleteMeasureDescription(@PathVariable("idStandard") int idStandard, @PathVariable("idMeasureDescription") int idMeasureDescription,
			Principal principal, Locale locale) {
		try {
			if (!serviceMeasureDescription.exists(idMeasureDescription, idStandard))
				return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
			customDelete.forceDeleteMeasureDescription(idMeasureDescription, principal);
			return JsonMessage.Success(messageSource.getMessage("success.measure.delete.successfully", null, "Measure was deleted successfully", locale));
		} catch (Exception e) {
			// return error
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.measure.delete.failed", null, "Measure deleting was failed: Standard might be in used", locale));
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
	private boolean buildMeasureDescription(Map<String, String> errors, MeasureDescription measuredescription, JsonNode jsonNode, Locale locale) {
		try {

			String reference = jsonNode.get("reference").asText();

			Integer level = null;
			Boolean computable = jsonNode.get("computable").asText().equals("on") ? true : false;
			try {
				level = jsonNode.get("level").asInt();
			} catch (Exception e) {
			}
			if (!serviceDataValidation.isRegistred(MeasureDescription.class))
				serviceDataValidation.register(new MeasureDescriptionValidator());

			if (!serviceDataValidation.isRegistred(MeasureDescriptionText.class))
				serviceDataValidation.register(new MeasureDescriptionTextValidator());

			String error = serviceDataValidation.validate(measuredescription, "reference", reference);

			if (error != null)
				errors.put("measuredescription.reference", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				reference = reference.trim();
				if (measuredescription.getId() < 1 && serviceMeasureDescription.existsForMeasureByReferenceAndStandard(reference, measuredescription.getStandard()))
					errors.put("measuredescription.reference",
							messageSource.getMessage("error.measuredescription.reference.duplicate", null, "Reference already exists in this standard", locale));
				else
					measuredescription.setReference(reference);
			}

			error = serviceDataValidation.validate(measuredescription, "level", level);

			if (error != null)
				errors.put("measuredescription.level", serviceDataValidation.ParseError(error, messageSource, locale));
			else if (!errors.containsKey("measuredescription.reference")) {
				if (reference.split(Constant.REGEX_SPLIT_REFERENCE).length != level)
					errors.put("measuredescription.level",
							messageSource.getMessage("error.measure_description.level.not.match.reference", null, "The level and the reference do not match.", locale));
				else
					measuredescription.setLevel(level);
			}

			error = serviceDataValidation.validate(measuredescription, "computable", computable);

			if (error != null)
				errors.put("measuredescription.computable", serviceDataValidation.ParseError(error, messageSource, locale));
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
					errors.put("measureDescriptionText.domain_" + language.getId(), serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDomain(domain);

				if (level == 3 && !(measuredescription.getStandard().getLabel().equals("27001") && measuredescription.getStandard().getVersion() == 2013))
					error = validator.validate(mesDescText, "description", description);
				else
					error = null;
				if (error != null)
					errors.put("measureDescriptionText.description_" + language.getId(), serviceDataValidation.ParseError(error, messageSource, locale));
				else
					mesDescText.setDescription(description);
			}
			// return success message
			return errors.isEmpty();

		} catch (Exception e) {

			// return error message
			errors.put("measureDescription", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
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
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			ValidatorField validator = serviceDataValidation.findByClass(Standard.class);

			if (validator == null)
				serviceDataValidation.register(validator = new StandardValidator());

			// load standard id
			int id = jsonNode.get("id").asInt();

			String label = jsonNode.get("label").asText();

			String description = jsonNode.get("description").asText();

			StandardType type = StandardType.getByName(jsonNode.get("type").asText());

			Integer version = null;

			try {
				version = jsonNode.get("version").asInt();
			} catch (NumberFormatException e) {
				TrickLogManager.Persist(e);
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
			standard.setComputable(jsonNode.get("computable").asText().equals("on"));

			standard.setAnalysisOnly(false);

			// return success
			return errors.isEmpty();

		} catch (Exception e) {
			// return error
			errors.put("standard", messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return false;
		}
	}

}