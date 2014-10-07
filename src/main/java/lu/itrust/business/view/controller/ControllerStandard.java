package lu.itrust.business.view.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.StandardType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.MeasureManager;
import lu.itrust.business.component.helper.ImportRRFForm;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceStandard;
import lu.itrust.business.service.ServiceTaskFeedback;
import lu.itrust.business.service.WorkersPoolManager;
import lu.itrust.business.task.Worker;
import lu.itrust.business.task.WorkerImportStandard;
import lu.itrust.business.validator.StandardValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SessionFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class ControllerStandard {

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
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MeasureManager measureManager;

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

		model.addAttribute("standards", serviceStandard.getAll());
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	public String loadSingleStandard(@PathVariable("idStandard") String idStandard, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Map<String, String> save(@RequestBody String value, Locale locale) {

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

				if (!serviceStandard.existsByNameAndVersion(standard.getLabel(), standard.getVersion()))
					// save
					serviceStandard.save(standard);
				else
					errors.put("version", messageSource.getMessage("error.norm.version.duplicate", null, "Version already exists", locale));
			} else
				// update
				serviceStandard.saveOrUpdate(standard);
			// errors
		} catch (Exception e) {
			// return errors
			errors.put("standard", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
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
	@RequestMapping(value = "/Delete/{idStandard}", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String deleteStandard(@PathVariable("idStandard") Integer idStandard, Locale locale) throws Exception {

		try {

			// try to delete the standard
			customDelete.deleteStandard(serviceStandard.get(idStandard));

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.norm.delete.successfully", null, "Standard was deleted successfully", locale));
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			String[] parts = e.getMessage().split(":");
			String code = parts[0];
			String defaultmessage = parts[1];

			return JsonMessage.Error(messageSource.getMessage(code, null, defaultmessage, locale));
		}
	}

	/**
	 * uploadStandard: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Upload", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	@RequestMapping(value = "/Import", headers = "Accept=application/json;charset=UTF-8")
	public String importNewStandard(@RequestParam(value = "file") MultipartFile file, Principal principal, HttpServletRequest request, RedirectAttributes attributes, Locale locale)
			throws Exception {
		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");
		file.transferTo(importFile);
		Worker worker = new WorkerImportStandard(serviceTaskFeedback, sessionFactory, workersPoolManager, importFile);
		if (serviceTaskFeedback.registerTask(principal.getName(), worker.getId())) {
			executor.execute(worker);
			return "redirect:/Task/Status/" + worker.getId();
		}
		attributes.addFlashAttribute("errors", messageSource.getMessage("failed.start.export.analysis", null, "Analysis export was failed", locale));
		return "redirect:/KnowledgeBase/Standard/Upload";
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
	@RequestMapping(value = "/Export/{idStandard}", headers = "Accept=application/json;charset=UTF-8")
	public String exportStandard(@PathVariable("idStandard") Integer idStandard, Principal principal, HttpServletRequest request, Locale locale, HttpServletResponse response) throws Exception {

		Standard standard = serviceStandard.get(idStandard);

		if (standard == null)
			return "404";

		InputStream templateFile = new FileInputStream(request.getServletContext().getRealPath("/WEB-INF/data") + "/TL_TRICKService_NormImport_V1.1.xlsx");
		@SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook(templateFile);
		templateFile.close();

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
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(standard.getLabel());

		// standard version
		cell = sheet.getRow(row).getCell(versioncol);
		cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		cell.setCellValue(standard.getVersion());

		// standard description
		cell = sheet.getRow(row).getCell(desccol);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue(standard.getDescription());

		// standard computable
		cell = sheet.getRow(row).getCell(computablecol);
		cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
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

		// update the table. coumn headers must match the corresponding cells in
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
		// +":"+measuredescriptions.size()+"):::Cols: ("+ (ref1.getCol()+1) +
		// ":"+ colnumber +")");

		row = 1;

		for (MeasureDescription measuredescription : measuredescriptions) {

			sheetrow = sheet.getRow(row);
			cell = sheetrow.getCell(levelcol);
			if (cell == null)
				cell = sheetrow.createCell(levelcol);
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(measuredescription.getLevel());

			cell = sheet.getRow(row).getCell(referencecol);
			if (cell == null)
				cell = sheetrow.createCell(referencecol);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(measuredescription.getReference());

			cell = sheet.getRow(row).getCell(computablecol);
			if (cell == null)
				cell = sheetrow.createCell(computablecol);
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
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
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(domain);
				// System.out.println("Domaincol: "+domaincol);
				domaincol++;
				domaincol++;

				cell = sheet.getRow(row).getCell(descriptioncol);
				if (cell == null)
					cell = sheetrow.createCell(descriptioncol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
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

		int length = standardFile.size();

		String identifierName = "";

		identifierName = "TL_TRICKService_Norm_" + standard.getLabel() + "_Version_" + standard.getVersion() + "_V1.1";

		// return standardFile to user

		// set response contenttype to sqlite
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		// set sqlite file size as response size
		response.setContentLength(length);

		// set response header with location of the filename
		response.setHeader("Content-Disposition", "attachment; filename=\"" + (identifierName.trim().replaceAll(":|-|[ ]", "_")) + ".xlsx\"");

		// return the sqlite file (as copy) to the response outputstream ( whihc
		// creates on the
		// client side the sqlite file)
		OutputStream out = response.getOutputStream();

		out.write(standardFile.toByteArray());

		// return
		return null;
	}

	/**
	 * importRRF: <br>
	 * Description
	 * 
	 * @param session
	 * @param principal
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import/RRF", headers = "Accept=application/json;charset=UTF-8")
	public String importRRF(HttpSession session, Principal principal, Model model) throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		List<Standard> standards = serviceStandard.getAllFromAnalysis(idAnalysis);
		List<Integer> idStandards = new ArrayList<Integer>(standards.size());
		for (Standard standard : standards) {
			if (!Constant.STANDARD_MATURITY.equalsIgnoreCase(standard.getLabel()))
				idStandards.add(standard.getId());
		}
		List<Analysis> profiles = serviceAnalysis.getAllProfileContainsStandard(standards);
		model.addAttribute("idStandards", idStandards);
		model.addAttribute("profiles", profiles);
		return "analysis/components/forms/importMeasureCharacteristics";

	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	@RequestMapping(value = "/Import/RRF/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody Object importRRFSave(@ModelAttribute ImportRRFForm rrfForm, HttpSession session, Principal principal, Locale locale) {
		try {
			if (rrfForm.getProfile() < 1)
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.no_profile", null, "No profile", locale));
			else if (rrfForm.getStandards() == null || rrfForm.getStandards().isEmpty())
				return JsonMessage.Error(messageSource.getMessage("error.import_rrf.norm", null, "No standard", locale));
			measureManager.importStandard((Integer) session.getAttribute("selectedAnalysis"), rrfForm);

			return JsonMessage.Success(messageSource.getMessage("success.import_rrf", null, "Measure characteristics has been successfully imported", locale));
		} catch (Exception e) {
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
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
				e.printStackTrace();
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

			// return success
			return errors.isEmpty();

		} catch (Exception e) {
			// return error
			errors.put("standard", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}

}