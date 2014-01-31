package lu.itrust.business.view.controller;

import java.io.File;
import java.io.FileInputStream;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.service.ServiceNorm;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

/**
 * ControllerNorm.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 14, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_CONSULTANT)
@Controller
@RequestMapping("/KnowledgeBase/Norm")
public class ControllerNorm {

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CustomDelete customDelete;

	/**
	 * 
	 * Display all Norms
	 * 
	 * */
	@RequestMapping
	public String displayAll(Map<String, Object> model) throws Exception {
		model.put("norms", serviceNorm.loadAll());
		return "knowledgebase/standard/norm/norms";
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model) throws Exception {
		model.addAttribute("norms", serviceNorm.loadAll());
		return "knowledgebase/standard/norm/norms";
	}

	/**
	 * 
	 * Display single Norm
	 * 
	 * */
	@RequestMapping("/{normId}")
	public String loadSingleNorm(@PathVariable("normId") String normId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		Norm norm = (Norm) session.getAttribute("norm");
		if (norm == null || norm.getLabel() != normId)
			norm = serviceNorm.loadSingleNormByName(normId);
		if (norm == null) {
			String msg = messageSource.getMessage("errors.norm.notexist", null, "Norm does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/KnowLedgeBase/Norm";
		}
		model.put("norm", norm);
		return "knowledgebase/standard/norm/showNorm";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, Locale locale) {
		List<String[]> errors = new LinkedList<>();
		try {

			Norm norm = new Norm();
			if (!buildNorm(errors, norm, value, locale))
				return errors;
			if (norm.getId() < 1) {
				serviceNorm.save(norm);
			} else {
				serviceNorm.saveOrUpdate(norm);
			}
		} catch (Exception e) {
			errors.add(new String[] { "norm", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}
		return errors;
	}

	/**
	 * 
	 * Delete single norm
	 * 
	 * */
	@RequestMapping(value = "/Delete/{normId}", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	String[] deleteNorm(@PathVariable("normId") Integer normId, Locale locale) throws Exception {
		try {
			customDelete.deleteNorm(serviceNorm.getNormByID(normId));
			return new String[] { "success", messageSource.getMessage("success.norm.delete.successfully", null, "Norm was deleted successfully", locale) };
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage("error.norm.delete.successfully", null, "Norm was not deleted. Make sure it is not used in an analysis", locale) };
		}
	}

	/**
	 * 
	 * Upload new norm file
	 * 
	 * */
	@RequestMapping(value = "/Upload", method = RequestMethod.GET, headers = "Accept=application/json")
	public String UploadNorm() throws Exception {
		return "knowledgebase/standard/norm/uploadForm";
	}

	/**
	 * importNewNorm: <br>
	 * Description
	 */
	@RequestMapping(value = "/Import")
	public Object importNewNorm(@RequestParam(value = "file") MultipartFile file, Principal principal, HttpServletRequest request, RedirectAttributes attributes, Locale locale) throws Exception {

		File importFile = new File(request.getServletContext().getRealPath("/WEB-INF/tmp") + "/" + principal.getName() + "_" + System.nanoTime() + "");
		file.transferTo(importFile);

		FileInputStream fileToOpen = new FileInputStream(importFile);

		// Get the workbook instance for XLS file
		XSSFWorkbook workbook = new XSSFWorkbook(fileToOpen);
		XSSFSheet sheet = null;
		XSSFTable table = null;

		Norm newNorm = new Norm();

		short startColSheet, endColSheet;
		int startRowSheet, endRowSheet;

		int sheetNumber = workbook.getNumberOfSheets();

		for (int indexSheet = 0; indexSheet < sheetNumber; indexSheet++) {

			sheet = workbook.getSheetAt(indexSheet);

			if (sheet.getSheetName().equals("NormInfo")) {

				for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

					table = sheet.getTables().get(indexTable);

					if (table.getName().equals("TableNormInfo")) {
						startColSheet = table.getStartCellReference().getCol();
						endColSheet = table.getEndCellReference().getCol();
						startRowSheet = table.getStartCellReference().getRow();
						endRowSheet = table.getEndCellReference().getRow();

						if (startColSheet <= endColSheet && startRowSheet <= endRowSheet)
							for (int indexRow = startRowSheet + 1; indexRow <= endRowSheet; indexRow++) {
								newNorm.setLabel(sheet.getRow(indexRow).getCell(0).getStringCellValue());
								newNorm.setVersion((int) sheet.getRow(indexRow).getCell(1).getNumericCellValue());
								newNorm.setDescription(sheet.getRow(indexRow).getCell(2).getStringCellValue());
								newNorm.setComputable(sheet.getRow(indexRow).getCell(3).getBooleanCellValue());
								
							}
					}

				}

				System.out.println(newNorm.getLabel() + " " + newNorm.getVersion() + " " + newNorm.getDescription() + " " + newNorm.isComputable());

				System.out.println(sheet.getTables().get(0).getStartCellReference() + " " + sheet.getTables().get(0).getEndCellReference());

			}

		}

		return "redirect:/Analysis";
	}

	/**
	 * buildLanguage: <br>
	 * Description
	 * 
	 * @param errors
	 * @param language
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildNorm(List<String[]> errors, Norm norm, String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			int id = jsonNode.get("id").asInt();
			if (id > 0)
				norm.setId(jsonNode.get("id").asInt());

			norm.setLabel(jsonNode.get("label").asText());

			norm.setDescription(jsonNode.get("description").asText());

			norm.setVersion(jsonNode.get("version").asInt());

			if (jsonNode.get("computable") != null && (jsonNode.get("computable").asText() == "on")) {
				norm.setComputable(true);
			} else {
				norm.setComputable(false);
			}
			return true;

		} catch (Exception e) {

			errors.add(new String[] { "norm", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;
		}

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

}