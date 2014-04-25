package lu.itrust.business.view.controller;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.Bounds;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.MaturityParameter;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.ParameterType;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.TrickService;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.ActionPlanType;
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
import lu.itrust.business.service.ServiceScenarioType;
import lu.itrust.business.service.ServiceTrickService;
import lu.itrust.business.service.ServiceUser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
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
	Boolean install(Model model, Principal principal, HttpServletRequest request) throws Exception {

		boolean installstatus = false;

		XSSFWorkbook workbook = null;

		InputStream defaultFile = null;

		List<Norm> norms = new ArrayList<Norm>();

		List<Language> languages = new ArrayList<Language>();

		TrickService status = serviceTrickService.getStatus();

		if (status == null)
			return false;

		defaultFile = new FileInputStream(request.getServletContext().getRealPath("/WEB-INF/data") + "/TS_DEFAULT_VALUES_V0.2.xlsx");
		workbook = new XSSFWorkbook(defaultFile);
		defaultFile.close();

		installstatus = installAssetTypes(workbook);

		installstatus = installParameterTypes(workbook);

		installstatus = installScenarioTypes(workbook);

		installstatus = installActionPlanTypes(workbook);

		installstatus = installLanguages(workbook, languages);

		installstatus = installNorms(workbook, norms);

		installstatus = installProfileCustomer();

		installstatus = installDefaultProfile(workbook, languages, norms, principal);

		return installstatus;
	}

	private boolean installAssetTypes(XSSFWorkbook workbook) {
		XSSFSheet sheet = null;
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			sheet = workbook.getSheet("Asset Types");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("ASSETTYPES")) {
					break;
				}
			}

			int startrow, endrow, idcol, labelcol;

			idcol = table.getStartCellReference().getCol();
			labelcol = idcol + 1;

			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {
				// id
				cell = sheet.getRow(i).getCell(idcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int id = (int) cell.getNumericCellValue();

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue();

				AssetType obj = serviceAssetType.get(label);

				if (obj == null) {
					obj = new AssetType(label);
					obj.setId(id);
					serviceAssetType.save(obj);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean installParameterTypes(XSSFWorkbook workbook) {
		XSSFSheet sheet = null;
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			sheet = workbook.getSheet("Parameter Types");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("PARAMETERTYPES")) {
					break;
				}
			}

			int startrow, endrow, idcol, labelcol;

			idcol = table.getStartCellReference().getCol();
			labelcol = idcol + 1;

			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {
				// id
				cell = sheet.getRow(i).getCell(idcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int id = (int) cell.getNumericCellValue();

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue();

				ParameterType obj = serviceParameterType.get(label);

				if (obj == null) {
					obj = new ParameterType(label);
					obj.setId(id);
					serviceParameterType.save(obj);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installScenarioTypes(XSSFWorkbook workbook) {
		XSSFSheet sheet = null;
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			sheet = workbook.getSheet("Scenario Types");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("SCENARIOTYPES")) {
					break;
				}
			}

			int startrow, endrow, idcol, labelcol;

			idcol = table.getStartCellReference().getCol();
			labelcol = idcol + 1;

			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {
				// id
				cell = sheet.getRow(i).getCell(idcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int id = (int) cell.getNumericCellValue();

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue();

				ScenarioType obj = serviceScenarioType.get(label);

				if (obj == null) {
					obj = new ScenarioType(label);
					obj.setId(id);
					serviceScenarioType.save(obj);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installActionPlanTypes(XSSFWorkbook workbook) {
		XSSFSheet sheet = null;
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			sheet = workbook.getSheet("Action Plan Types");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("ACTIONPLANTYPES")) {
					break;
				}
			}

			int startrow, endrow, idcol, labelcol;

			idcol = table.getStartCellReference().getCol();
			labelcol = idcol + 1;

			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {
				// id
				cell = sheet.getRow(i).getCell(idcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int id = (int) cell.getNumericCellValue();

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue();

				ActionPlanType obj = serviceActionPlanType.get(label);

				if (obj == null) {
					obj = new ActionPlanType(ActionPlanMode.getByName(label));
					obj.setId(id);
					serviceActionPlanType.save(obj);
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installLanguages(XSSFWorkbook workbook, List<Language> languages) {
		XSSFSheet sheet = null;
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			sheet = workbook.getSheet("Languages");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("LANGUAGES")) {
					break;
				}
			}

			int startrow, endrow, idcol, alpha3col, namecol, alternativenamecol;

			idcol = table.getStartCellReference().getCol();
			alpha3col = idcol + 1;
			namecol = alpha3col + 1;
			alternativenamecol = namecol + 1;

			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {
				// id
				cell = sheet.getRow(i).getCell(idcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int id = (int) cell.getNumericCellValue();

				// label
				cell = sheet.getRow(i).getCell(alpha3col);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String alpha3 = cell.getStringCellValue();

				// name
				cell = sheet.getRow(i).getCell(namecol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String name = cell.getStringCellValue();

				// altname
				cell = sheet.getRow(i).getCell(alternativenamecol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String altname = cell.getStringCellValue();

				Language obj = serviceLanguage.loadFromAlpha3(alpha3);

				if (obj == null) {
					obj = new Language();
					obj.setId(id);
					obj.setAlpha3(alpha3);
					obj.setName(name);
					obj.setAltName(altname);
					serviceLanguage.save(obj);
				}

				languages.add(obj);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installNorms(XSSFWorkbook workbook, List<Norm> norms) {
		XSSFSheet sheet = null;
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			sheet = workbook.getSheet("Norms");

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("NORMS")) {
					break;
				}
			}

			int startrow, endrow, labelcol, versioncol, descriptioncol, computablecol;

			labelcol = table.getStartCellReference().getCol();
			versioncol = labelcol + 1;
			descriptioncol = versioncol + 1;
			computablecol = descriptioncol + 1;

			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {
				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue();

				// version
				cell = sheet.getRow(i).getCell(versioncol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int version = (int) cell.getNumericCellValue();

				// description
				cell = sheet.getRow(i).getCell(descriptioncol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String desc = cell.getStringCellValue();

				// computable
				cell = sheet.getRow(i).getCell(computablecol);
				cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
				boolean computable = cell.getBooleanCellValue();

				Norm obj = serviceNorm.loadSingleNormByNameAndVersion(label, version);

				if (obj == null) {
					obj = new Norm(label, version, desc, computable);
					serviceNorm.save(obj);
				} else {
					obj.setDescription(desc);
					obj.setComputable(computable);
					serviceNorm.saveOrUpdate(obj);
				}
				installMeasures(obj, workbook.getSheet(obj.getLabel()));
				serviceNorm.saveOrUpdate(obj);
				norms.add(obj);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installMeasures(Norm norm, XSSFSheet sheet) {
		XSSFTable table = null;
		XSSFCell cell = null;

		try {

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("NORM_" + norm.getLabel())) {
					break;
				}
			}

			int startrow, endrow, idcol, levelcol, referencecol, computablecol, phasecol;

			idcol = table.getStartCellReference().getCol();
			levelcol = idcol + 1;
			referencecol = levelcol + 1;
			computablecol = referencecol + 1;
			phasecol = computablecol + 1;
			endrow = table.getEndCellReference().getRow() + 1;

			startrow = table.getStartCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {

				// level
				cell = sheet.getRow(i).getCell(levelcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				int level = (int) cell.getNumericCellValue();

				// reference
				cell = sheet.getRow(i).getCell(referencecol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String reference = cell.getStringCellValue();

				// computable
				cell = sheet.getRow(i).getCell(computablecol);
				cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
				boolean computable = cell.getBooleanCellValue();

				MeasureDescription obj = serviceMeasureDescription.getByReferenceNorm(reference, norm);

				if (obj == null) {
					obj = new MeasureDescription(reference, norm, level, computable);
					serviceMeasureDescription.save(obj);
				} else {
					obj.setLevel(level);
					obj.setComputable(computable);
					serviceMeasureDescription.saveOrUpdate(obj);
				}

				Pattern pattern;
				Matcher matcher;
				Language lang = null;

				for (int indexCol = (phasecol + 1); indexCol <= table.getEndCellReference().getCol(); indexCol++) {
					pattern = Pattern.compile("(domain|question)_(\\w{3})");

					if (sheet.getRow(startrow).getCell(indexCol) == null)
						break;

					String cellcontent = sheet.getRow(startrow - 1).getCell(indexCol).getStringCellValue();

					matcher = pattern.matcher(cellcontent);
					if (matcher.matches()) {

						String type = matcher.group(1).trim().toLowerCase();

						if (type.equals("question"))
							continue;

						String alpha3 = matcher.group(2).trim().toLowerCase();

						lang = serviceLanguage.loadFromAlpha3(alpha3);

						if (lang == null) {
							lang = new Language();
							lang.setAlpha3(alpha3);
							lang.setName(lang.getAlpha3());
							serviceLanguage.save(lang);

						}

						String domain = sheet.getRow(i).getCell(indexCol) != null ? sheet.getRow(i).getCell(indexCol).getStringCellValue() : "";
						String description = sheet.getRow(i).getCell(indexCol + 1) != null ? sheet.getRow(i).getCell(indexCol + 1).getStringCellValue() : "";

						MeasureDescriptionText mt = serviceMeasureDescriptionText.getByLanguage(obj.getId(), lang.getId());

						if (mt == null) {

							mt = new MeasureDescriptionText();
							mt.setMeasureDescription(obj);
							mt.setLanguage(lang);
							mt.setDomain(domain);
							mt.setDescription(description);
							obj.addMeasureDescriptionText(mt);
						} else {
							mt.setDomain(domain);
							mt.setDescription(description);
						}

						serviceMeasureDescription.saveOrUpdate(obj);

					} else {
						break;
					}
				}

			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installProfileCustomer() {
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

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean installDefaultProfile(XSSFWorkbook workbook, List<Language> languages, List<Norm> norms, Principal principal) {

		Customer customer = null;
		User owner = null;
		XSSFSheet sheet = null;
		try {

			// analysis profile

			Analysis analysis = serviceAnalysis.getDefaultProfile();

			if (analysis == null)
				return true;

			// customer
			customer = serviceCustomer.loadProfileCustomer();

			if (customer == null)
				if (!installProfileCustomer()) {
					System.out.println("Could not install customer! Canceling default Profile creation...");
					return false;
				}

			customer = serviceCustomer.loadProfileCustomer();

			// owner

			owner = serviceUser.get(principal.getName());

			if (owner == null) {
				System.out.println("Could not determine owner! Canceling default Profile creation...");
				return false;
			}

			// language

			if (languages.isEmpty()) {
				System.out.println("Could not determine language! Canceling default Profile creation...");
				return false;
			}

			// analysis creation

			analysis = new Analysis();
			analysis.setBasedOnAnalysis(null);
			analysis.setData(true);
			analysis.setProfile(true);
			analysis.setDefaultProfile(true);
			analysis.setIdentifier("Default Profile");
			analysis.setLabel("Default Profile");
			analysis.setVersion("1.0.0");
			Date date = new Date();
			Timestamp creationDate = new Timestamp(date.getTime());
			analysis.setCreationDate(creationDate);
			analysis.setOwner(owner);
			analysis.setLanguage(languages.get(0));
			analysis.setCustomer(customer);

			// scope

			sheet = workbook.getSheet("SCOPE");

			analysis.setItemInformations(getScope(sheet));

			// parameters
			
			List<Parameter> parameters = new ArrayList<Parameter>();
			
			// single
			
			sheet = workbook.getSheet("SINGLE");
			parameters.addAll(getSimpleParameters(sheet, "SINGLE"));
			
			// implementation scale
			
			sheet = workbook.getSheet("IMPSCALE");
			parameters.addAll(getSimpleParameters(sheet, "IMPSCALE"));
			
			// maxeffency
			
			sheet = workbook.getSheet("MAXEFF");
			parameters.addAll(getSimpleParameters(sheet, "MAXEFF"));
			
			// proba
			
			sheet = workbook.getSheet("PROBA");
			parameters.addAll(getExtendedParameters(sheet, "PROBA"));
			
			// impact
			
			sheet = workbook.getSheet("IMPACT");
			parameters.addAll(getExtendedParameters(sheet, "IMPACT"));

			// ilps

			sheet = workbook.getSheet("ILPS");
			parameters.addAll(getMaturityParameters(sheet, "ILPS"));
			
			// add to analysis
			analysis.setParameters(parameters);
			
			// risk information
			
			List<RiskInformation> riskinformations = new ArrayList<RiskInformation>();
			
			// threat

			sheet = workbook.getSheet("THREAT");
			riskinformations.addAll(getRiskInformations(sheet,"THREAT"));
			
			// vul

			sheet = workbook.getSheet("VUL");
			riskinformations.addAll(getRiskInformations(sheet,"VUL"));
			
			// risk
			
			sheet = workbook.getSheet("RISK");
			riskinformations.addAll(getRiskInformations(sheet,"RISK"));

			// add risk information to analysis
			analysis.setRiskInformations(riskinformations);
			
			// scenario

			
			
			// norms

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private List<ItemInformation> getScope(XSSFSheet sheet) {

		List<ItemInformation> iteminformations = new ArrayList<ItemInformation>();

		XSSFTable table = null;

		XSSFCell cell = null;

		int startrow, endrow, labelcol, typecol;

		try {

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals("SCOPE")) {
					break;
				}
			}

			labelcol = table.getStartCellReference().getCol();
			typecol = labelcol + 1;

			startrow = table.getStartCellReference().getRow() + 1;
			endrow = table.getEndCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue();

				// type
				cell = sheet.getRow(i).getCell(typecol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String type = cell.getStringCellValue();

				Pattern pattern;
				Matcher matcher;

				pattern = Pattern.compile("(Scope|Organisation)");

				matcher = pattern.matcher(type.trim());
				if (matcher.matches()) {

					ItemInformation iteminfo = new ItemInformation(label.trim(), type.trim(), "");

					iteminformations.add(iteminfo);

				}

			}

			return iteminformations;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private List<Parameter> getSimpleParameters(XSSFSheet sheet, String tableName) {

		List<Parameter> parameters = new ArrayList<Parameter>();

		XSSFTable table = null;

		XSSFCell cell = null;

		int startrow, endrow, labelcol, valuecol, typecol;

		try {

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals(tableName)) {
					break;
				}
			}

			labelcol = table.getStartCellReference().getCol();
			valuecol = labelcol + 1;
			typecol = valuecol + 1;

			startrow = table.getStartCellReference().getRow() + 1;
			endrow = table.getEndCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue().trim();

				// value
				cell = sheet.getRow(i).getCell(valuecol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Double value = cell.getNumericCellValue();

				// type
				cell = sheet.getRow(i).getCell(typecol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Integer type = (int) cell.getNumericCellValue();

				ParameterType parametertype = serviceParameterType.get(type);

				if (parametertype == null) {
					System.out.println("Parametertype not recognized! Skipping parameter...");
					continue;
				}

				Parameter parameter = new Parameter(parametertype, label, value);

				parameters.add(parameter);

			}

			return parameters;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private List<Parameter> getExtendedParameters(XSSFSheet sheet, String tableName) {

		List<Parameter> parameters = new ArrayList<Parameter>();

		XSSFTable table = null;

		XSSFCell cell = null;

		int startrow, endrow, labelcol, valuecol, typecol, fromcol, tocol, levelcol, acronymcol;

		try {

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals(tableName)) {
					break;
				}
			}

			labelcol = table.getStartCellReference().getCol();
			valuecol = labelcol + 1;
			typecol = valuecol + 1;
			fromcol = typecol + 1;
			tocol = fromcol + 1;
			levelcol = tocol + 1;
			acronymcol = levelcol + 1;

			startrow = table.getStartCellReference().getRow() + 1;
			endrow = table.getEndCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue().trim();

				// value
				cell = sheet.getRow(i).getCell(valuecol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Double value = cell.getNumericCellValue();

				// type
				cell = sheet.getRow(i).getCell(typecol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Integer type = (int) cell.getNumericCellValue();

				// from
				cell = sheet.getRow(i).getCell(fromcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Double from = cell.getNumericCellValue();

				// to
				cell = sheet.getRow(i).getCell(tocol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Double to = cell.getNumericCellValue();

				// level
				cell = sheet.getRow(i).getCell(levelcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Integer level = (int) cell.getNumericCellValue();

				// acronym
				cell = sheet.getRow(i).getCell(acronymcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String acronym = cell.getStringCellValue();

				ParameterType parametertype = serviceParameterType.get(type);

				if (parametertype == null) {
					System.out.println("Parametertype not recognized! Skipping parameter...");
					continue;
				}

				ExtendedParameter parameter = new ExtendedParameter();
				parameter.setDescription(label);
				parameter.setType(parametertype);
				parameter.setValue(value);
				Bounds bounds = new Bounds(from, to);
				parameter.setBounds(bounds);
				parameter.setLevel(level);
				parameter.setAcronym(acronym);
				parameters.add(parameter);

			}

			return parameters;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private List<Parameter> getMaturityParameters(XSSFSheet sheet, String tableName) {

		List<Parameter> parameters = new ArrayList<Parameter>();

		XSSFTable table = null;

		XSSFCell cell = null;

		int startrow, endrow, labelcol, valuecol, typecol, categorycol, smlcol;

		try {

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals(tableName)) {
					break;
				}
			}

			labelcol = table.getStartCellReference().getCol();
			valuecol = labelcol + 1;
			typecol = valuecol + 1;
			categorycol = typecol + 1;
			smlcol = categorycol + 1;

			startrow = table.getStartCellReference().getRow() + 1;
			endrow = table.getEndCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue().trim();

				// value
				cell = sheet.getRow(i).getCell(valuecol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Double value = cell.getNumericCellValue();

				// type
				cell = sheet.getRow(i).getCell(typecol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Integer type = (int) cell.getNumericCellValue();

				// category
				cell = sheet.getRow(i).getCell(categorycol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String category = cell.getStringCellValue();

				// sml
				cell = sheet.getRow(i).getCell(smlcol);
				cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				Integer sml = (int) cell.getNumericCellValue();

				ParameterType parametertype = serviceParameterType.get(type);

				if (parametertype == null) {
					System.out.println("Parametertype not recognized! Skipping parameter...");
					continue;
				}

				Pattern pattern;
				Matcher matcher;

				pattern = Pattern.compile("(Implementation|Integration|Policies|Procedure|Test)");

				matcher = pattern.matcher(category.trim());
				if (matcher.matches()) {
				
					pattern = Pattern.compile("^(Imp|Int|Pol|Pro|Tes)\\s{1}(\\d{1})$");
					matcher = pattern.matcher(label.trim());
					
					if (matcher.matches()) {
					
						MaturityParameter parameter = new MaturityParameter();
						parameter.setDescription(label);
						parameter.setType(parametertype);
						parameter.setValue(value);
						parameter.setCategory(category.trim());
						parameter.setSMLLevel(sml);
						parameters.add(parameter);
					} else {
						System.out.println("Task/Label not recognized! Skipping...");
						continue;
					}
				} else {
					System.out.println("Category not recognized! Skipping...");
					continue;
				}

			}

			return parameters;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public List<RiskInformation> getRiskInformations(XSSFSheet sheet, String tableName) {
		List<RiskInformation> riskinformations = new ArrayList<RiskInformation>();
		
		XSSFTable table = null;

		XSSFCell cell = null;

		int startrow, endrow, chaptercol, editablecol, labelcol, acronymcol, categorycol;

		try {

			for (int indexTable = 0; indexTable < sheet.getTables().size(); indexTable++) {

				table = sheet.getTables().get(indexTable);

				if (table.getName().equals(tableName)) {
					break;
				}
			}

			chaptercol = table.getStartCellReference().getCol();
			editablecol = chaptercol + 1;
			labelcol = editablecol + 1;
			
			
			if (tableName.equals("THREAT")) {
				acronymcol = labelcol + 1;
				categorycol = acronymcol + 1;
			} else {
				acronymcol = 0;
				categorycol = labelcol + 1;
			}
			
			startrow = table.getStartCellReference().getRow() + 1;
			endrow = table.getEndCellReference().getRow() + 1;

			for (int i = startrow; i < endrow; i++) {

				// chapter
				cell = sheet.getRow(i).getCell(chaptercol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String chapter = cell.getStringCellValue().trim();

				// editable
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
				boolean editable = cell.getBooleanCellValue();

				// label
				cell = sheet.getRow(i).getCell(labelcol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String label = cell.getStringCellValue().trim();

				String acronym = "";
				
				if (tableName.equals("THREAT")) {
				
					// acronym
					cell = sheet.getRow(i).getCell(acronymcol);
					cell.setCellType(Cell.CELL_TYPE_STRING);
					acronym = cell.getStringCellValue().trim();

				}
								
				// category 
				cell = sheet.getRow(i).getCell(categorycol);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				String category = cell.getStringCellValue().trim();
				
				if (!category.matches(Constant.REGEXP_VALID_RISKINFORMATION_TYPE)) {
					System.out.println("Cateogry not recognized! Skipping risk information...");
					continue;
				}
				
				if (!chapter.matches(Constant.REGEXP_VALID_ANALYSIS_VERSION)) {
					System.out.println("Chapter not recognized! Skipping risk information...");
					continue;
				}
				
				RiskInformation riskinfo = new RiskInformation();
				riskinfo.setAcronym(acronym);
				riskinfo.setCategory(category);
				riskinfo.setChapter(chapter);
				riskinfo.setComment("");
				riskinfo.setEditable(editable);
				riskinfo.setExposed("");
				riskinfo.setHiddenComment("");
				riskinfo.setLabel(label.trim());
				
				riskinformations.add(riskinfo);
				
			}
			
			return riskinformations;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}