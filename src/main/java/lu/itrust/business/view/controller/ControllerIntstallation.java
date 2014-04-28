package lu.itrust.business.view.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.TrickService;
import lu.itrust.business.TS.dbhandler.DatabaseHandler;
import lu.itrust.business.TS.importation.ImportAnalysis;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceActionPlanType;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceCustomer;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;
import lu.itrust.business.service.ServiceMeasureDescriptionText;
import lu.itrust.business.service.ServiceNorm;
import lu.itrust.business.service.ServiceParameterType;
import lu.itrust.business.service.ServicePhase;
import lu.itrust.business.service.ServiceScenarioType;
import lu.itrust.business.service.ServiceTrickService;
import lu.itrust.business.service.ServiceUser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerIntstallation.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Apr 23, 2014
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_ADMIN)
public class ControllerIntstallation {

	@Autowired
	private ServiceTrickService serviceTrickService;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceParameterType serviceParameterType;

	@Autowired
	private ServiceLanguage serviceLanguage;

	@Autowired
	private ServiceScenarioType serviceScenarioType;

	@Autowired
	private ServiceActionPlanType serviceActionPlanType;

	@Autowired
	private ServiceNorm serviceNorm;

	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;

	@Autowired
	private ServiceMeasureDescriptionText serviceMeasureDescriptionText;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private ServicePhase servicePhase;
	
	
	@RequestMapping("/Status")
	public String status(Model model, Principal principal, HttpServletRequest request) throws Exception {

		TrickService status = serviceTrickService.getStatus();

		String version = "0.0.1";

		boolean installed = false;

		XSSFSheet sheet = null;

		XSSFWorkbook workbook = null;

		InputStream defaultFile = null;

		if (status != null) {
			model.addAttribute("status", status);
			return "admin/status";
		}

		defaultFile = new FileInputStream(request.getServletContext().getRealPath("/WEB-INF/data") + "/TS_DEFAULT_VALUES_V0.2.xlsx");
		workbook = new XSSFWorkbook(defaultFile);
		defaultFile.close();

		sheet = workbook.getSheet("TS");

		XSSFCell cell = null;

		// version
		cell = sheet.getRow(1).getCell(0);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		version = cell.getStringCellValue();

		// installed
		cell = sheet.getRow(1).getCell(1);
		cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
		installed = cell.getBooleanCellValue();

		status = new TrickService(version, installed);

		serviceTrickService.save(status);

		Analysis defaultProfile = serviceAnalysis.getDefaultProfile();

		if (defaultProfile != null) {
			status.setInstalled(true);
			serviceTrickService.saveOrUpdate(status);
		}

		model.addAttribute("status", status);

		return "admin/status";

	}

	@RequestMapping("/Install")
	public @ResponseBody
	Map<String, String> install(Model model, Principal principal, HttpServletRequest request) throws Exception {

		Map<String, String> errors = new LinkedHashMap<String, String>();
		
		TrickService status = serviceTrickService.getStatus();

		if (status == null) {
			errors.put("status", "Call analysis status before installing TRICK Service!");
			return errors;
		}

		String fileName = request.getServletContext().getRealPath("/WEB-INF/data") + "/TS_DEFAULT_VALUES_V0.2.xlsx";
	
		installProfileCustomer(errors);

		installDefaultProfile(fileName, principal, errors);

		return errors;
	}

	private Customer installProfileCustomer(Map<String, String> errors) {
		try {

			Customer customer = serviceCustomer.loadProfileCustomer();

			if (customer == null) {
				customer = new Customer();
				customer.setOrganisation("Profile");
				customer.setContactPerson("Profile");
				customer.setEmail("profile@trickservice.lu");
				customer.setTelephoneNumber("0123456");
				customer.setAddress("Profile");
				customer.setCity("Profile");
				customer.setZIPCode("Profile");
				customer.setCountry("Profile");
				customer.setCanBeUsed(false);
				serviceCustomer.save(customer);
			}

			return customer;
		} catch (Exception e) {
			e.printStackTrace();
			
			
			
			errors.put("profileCustomer", e.getMessage());
			return null;
		}
	}

	
	
	private boolean installDefaultProfile(String fileName, Principal principal, Map<String, String> errors) {

		
		Customer customer;
		
		User owner;
		
		Analysis analysis = null;
		
		DatabaseHandler sqlitehandler = null;
		
		try {
		
		
		// analysis profile

		analysis = serviceAnalysis.getDefaultProfile();

		if (analysis != null)
			return true;

		// customer
		customer = serviceCustomer.loadProfileCustomer();

		if (customer == null) {
			customer = installProfileCustomer(errors);
			if (customer==null) {
				System.out.println("Customer could not be installed!");
				return false;
			}
		}
		
		// owner
		owner = serviceUser.get(principal.getName());

		if (owner == null) {
			System.out.println("Could not determine owner! Canceling default Profile creation...");
			errors.put("Owner", "Could not determine owner");
			return false;
		}

		// create analysis 
		analysis = new Analysis();
		analysis.setCustomer(customer);
		analysis.setOwner(owner);
		analysis.setProfile(true);
		analysis.setDefaultProfile(true);
		
		sqlitehandler = new DatabaseHandler(fileName);
		
		// import default values
		ImportAnalysis importAnalysis = new ImportAnalysis(analysis, sqlitehandler);
		return importAnalysis.ImportAnAnalysis();
				
		} catch(Exception e) {
			e.printStackTrace();
			errors.put("createProfile", e.getMessage());
			return false;
		}
	}
}