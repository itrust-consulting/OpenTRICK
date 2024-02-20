/**
 * 
 */
package lu.itrust.business.ts.component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOCustomer;
import lu.itrust.business.ts.database.dao.DAOLanguage;
import lu.itrust.business.ts.database.dao.DAOTrickTemplate;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.helper.NaturalOrderComparator;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.Language;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;

/**
 * @author eomar
 *
 */
@Component
@Transactional
public class DefaultTemplateLoader {

	@Value("#{${app.settings.report.hybrid.template.paths}}")
	private Map<String, String> mixedReportPaths;

	@Value("#{${app.settings.report.quantitative.template.paths}}")
	private Map<String, String> qualitativeReportPaths;

	@Value("#{${app.settings.report.quantitative.template.paths}}")
	private Map<String, String> quantitativeReportPaths;

	@Value("#{${app.settings.risk_regsiter.template.paths}}")
	private Map<String, String> riskRegisterTemplatePaths;

	@Value("#{${app.settings.risk_sheet.template.paths}}")
	private Map<String, String> riskSheetTemplatePaths;

	@Value("#{${app.settings.soa.template.paths}}")
	private Map<String, String> soaTemplatePaths;

	@Value("#{${app.settings.risk.information.template.paths}}")
	private Map<String, String> riskInformationTemplatePaths;

	@Value("#{${app.settings.excel.default.template.paths}}")
	private Map<String, String> excelTemplatePaths;

	@Value("#{'${app.settings.default.template.hybrid.names}'.split(',')}")
	private String[] defaultMixedTemplateNames;

	@Value("#{'${app.settings.default.template.quantitative.names}'.split(',')}")
	private String[] defaultQuantitativeTemplateNames;

	@Value("#{'${app.settings.default.template.qualitative.names}'.split(',')}")
	private String[] defaultQualitativeTemplateNames;

	@Value("#{'${app.settings.default.template.risk_regsiter.names}'.split(',')}")
	private String[] defaultRiskRegisterTemplateNames;

	@Value("#{'${app.settings.default.template.risk_sheet.names}'.split(',')}")
	private String[] defaultRiskSheetTemplateNames;

	@Value("#{'${app.settings.default.template.soa.names}'.split(',')}")
	private String[] defaultSOATemplateNames;

	@Value("#{'${app.settings.default.template.default.excel.names}'.split(',')}")
	private String[] defaultExcelTemplateNames;

	@Value("#{'${app.settings.default.template.risk_information.names}'.split(',')}")
	private String[] defaultRiskInformationTemplateNames;

	@Value("#{'${app.settings.default.languages}'.split(';')}")
	private List<String> defaultLanguages;

	@Autowired
	private DAOCustomer daoCustomer;

	@Autowired
	private DAOTrickTemplate daoTrickTemplate;

	@Autowired
	private DAOLanguage daoLanguage;

	private final AtomicBoolean upToDate = new AtomicBoolean(false);

	public List<TrickTemplate> findAll() {
		return load() ? daoTrickTemplate.findDefault() : Collections.emptyList();
	}

	public List<TrickTemplate> findByType(TrickTemplateType type) {
		return findAll().stream().filter(p -> p.getType() == type)
				.collect(Collectors.toList());
	}

	public List<TrickTemplate> findByLanguage(String langue) {
		return findAll().stream()
				.filter(p -> isExpectedLanguage(langue, p))
				.collect(Collectors.toList());
	}

	public TrickTemplate findTemplate(int customerId, TrickTemplateType type, int languageId) {
		return findTemplate(daoCustomer.get(customerId), type, daoLanguage.get(languageId));
	}

	public TrickTemplate findTemplate(Customer customer, TrickTemplateType type, int languageId) {
		return findTemplate(customer, type, daoLanguage.get(languageId));
	}

	public TrickTemplate findTemplate(int customerId, TrickTemplateType type, Language language) {
		return findTemplate(daoCustomer.get(customerId), type, language);
	}

	public TrickTemplate findTemplate(Customer customer, TrickTemplateType type, Language language) {
		List<TrickTemplate> templates = customer.getTemplates().stream()
				.filter(e -> e.getType() == type && (e.getLanguage() == null || e.getLanguage().equals(language)))
				.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e2.getVersion(), e1.getVersion()))
				.toList();
		if (templates.isEmpty()) {
			templates = findByType(type).stream().filter(
					e -> e.getType() == type && (e.getLanguage() == null || e.getLanguage().equals(language)))
					.sorted((e1, e2) -> NaturalOrderComparator.compareTo(e2.getVersion(), e1.getVersion())).toList();
		}

		return templates.stream().filter(e -> e.getLanguage() != null).findFirst()
				.orElse(templates.stream().findFirst().orElse(null));

	}

	public File loadFile(int customerId, TrickTemplateType type, int languageId) {
		return loadFile(daoCustomer.get(customerId), type, daoLanguage.get(languageId));
	}

	public File loadFile(Customer customer, TrickTemplateType type, int languageId) {
		return loadFile(customer, type, daoLanguage.get(languageId));
	}

	public File loadFile(int customerId, TrickTemplateType type, Language language) {
		return loadFile(daoCustomer.get(customerId), type, language);
	}

	public File loadFile(Customer customer, TrickTemplateType type, Language language) {
		final TrickTemplate template = findTemplate(customer, type, language);
		if (template == null)
			throw new TrickException("error.template." + type.name().toLowerCase() + ".not_found",
					"Template '" + type.name().toLowerCase() + "' cannot be found");
		final String filename = ServiceStorage.RandoomFilename(FileNameUtils.getExtension(template.getName()));
		InstanceManager.getServiceStorage().store(template.getData(), filename);
		return InstanceManager.getServiceStorage().loadAsFile(filename);
	}

	private boolean isExpectedLanguage(String langue, TrickTemplate p) {
		if (langue == null)
			return p.getLanguage() == null;
		else if (p.getLanguage() != null)
			return p.getLanguage().getAlpha3().equalsIgnoreCase(langue);
		else
			return false;
	}

	public List<TrickTemplate> findReportByTypeAndLanguage(AnalysisType type, String langue) {
		return (type == AnalysisType.HYBRID
				? findByLanguage(langue).stream().filter(p -> p.getType() == TrickTemplateType.REPORT)
				: findAll().stream()
						.filter(p -> p.getType() == TrickTemplateType.REPORT && p.getAnalysisType() == type
								&& isExpectedLanguage(langue, p)))
				.toList();
	}

	public boolean load() {
		if (upToDate.get())
			return true;
		if (!upToDate.get()) {
			synchronized (upToDate) {
				if (!upToDate.get())
					internalLoad();
			}
		}
		return upToDate.get();
	}

	private void internalLoad() {
		if (daoLanguage.existsByAlpha3("ENG", "FRA"))
			loadLanguages();
		final List<TrickTemplate> templates = initializedTemplates();
		;

		final Customer customer = getDefaultCustomer();
		if (customer == null)
			throw new TrickException("error.default.customer.cannot.be.created", "Profile customer cannot be created");
		if (customer.getTemplates() == null || customer.getTemplates().isEmpty())
			customer.setTemplates(templates);
		else {
			Map<String, TrickTemplate> mappers = customer.getTemplates().stream()
					.collect(Collectors.toMap(TrickTemplate::getKey, Function.identity()));
			templates.forEach(p -> {
				TrickTemplate template = mappers.get(p.getKey());
				if (template == null)
					customer.getTemplates().add(p);
				else {
					if (!template.getVersion().equals(p.getVersion()))
						template.update(p);
					template.setEditable(false);
				}
			});
		}

		daoCustomer.saveOrUpdate(customer);
		upToDate.set(true);
	}

	private List<TrickTemplate> initializedTemplates() {

		final List<TrickTemplate> templates = new ArrayList<>();

		for (String template : defaultMixedTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.REPORT, AnalysisType.HYBRID, mixedReportPaths);
		for (String template : defaultQualitativeTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.REPORT, AnalysisType.QUALITATIVE,
					qualitativeReportPaths);
		for (String template : defaultQuantitativeTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.REPORT, AnalysisType.QUANTITATIVE,
					quantitativeReportPaths);

		for (String template : defaultRiskRegisterTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.RISK_REGISTER, AnalysisType.HYBRID,
					riskRegisterTemplatePaths);

		for (String template : defaultRiskSheetTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.RISK_SHEET, AnalysisType.HYBRID,
					riskSheetTemplatePaths);

		for (String template : defaultRiskInformationTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.RISK_INFORMATION, AnalysisType.HYBRID,
					riskInformationTemplatePaths);

		for (String template : defaultSOATemplateNames)
			loadTemplates(templates, template, TrickTemplateType.SOA, AnalysisType.HYBRID,
					soaTemplatePaths);

		for (String template : defaultExcelTemplateNames)
			loadTemplates(templates, template, TrickTemplateType.DEFAULT_EXCEL, AnalysisType.HYBRID,
					excelTemplatePaths);

		return templates;
	}

	private void loadTemplates(List<TrickTemplate> templates, String template, TrickTemplateType type,
			AnalysisType analysisType, Map<String, String> paths) {
		final String[] fields = template.split(";");
		if (fields.length != 3)
			throw new TrickException("error.default.template.invalid.parameter",
					"Default template cannot be load, please contact your administrator or your support!");
		final String filename = paths.get(fields[1]);
		try {
			final Resource file = loadResource(filename);
			final InputStream stream = file.getInputStream();
			final TrickTemplate reportTemplate = new TrickTemplate(type);
			reportTemplate.setAnalysisType(analysisType);
			reportTemplate.setLabel(fields[0]);
			reportTemplate.setLanguage(daoLanguage.getByAlpha3(fields[1]));
			reportTemplate.setVersion(fields[2]);
			reportTemplate.setName(file.getFilename());
			reportTemplate.setData(IOUtils.toByteArray(stream));
			reportTemplate.setLength(reportTemplate.getData().length);
			reportTemplate.setCreated(new Timestamp(file.lastModified()));
			reportTemplate.setEditable(false);
			templates.add(reportTemplate);
		} catch (IOException e) {
			throw new TrickException("error.default.template.load.failed",
					String.format("Default template cannot loaded, Filename: %s", filename), e, filename);
		}
	}

	private Resource loadResource(String name) throws IOException {
		return InstanceManager.getServiceStorage().loadAsResource(name);
	}

	public void loadLanguages() {
		defaultLanguages.forEach(value -> {
			String[] values = value.split(",");
			if (values.length == 3 && !daoLanguage.existsByAlpha3(values[0])) {
				daoLanguage.saveOrUpdate(new Language(values[0], values[1], values[2]));
			}
		});
	}

	public Customer getDefaultCustomer() {
		Customer customer = daoCustomer.getProfile();
		if (customer == null) {
			synchronized (upToDate) {
				customer = daoCustomer.getProfile();
				if (customer == null) {
					customer = new Customer();
					customer.setOrganisation("itrust consulting s.à r.l");
					customer.setContactPerson("Carlo HARPES");
					customer.setEmail("profile@trickservice.com");
					customer.setPhoneNumber("00000000");
					customer.setAddress("Profile");
					customer.setCity("Luxembourg");
					customer.setZIPCode("Profile");
					customer.setCountry("Luxembourg");
					customer.setCanBeUsed(false);
					daoCustomer.save(customer);
				}
			}
		}
		return customer;
	}

	public static boolean isDocx(InputStream stream) {
		try {
			return WordprocessingMLPackage.load(stream).getMainDocumentPart() != null;
		} catch (Docx4JException e) {
			return false;
		}

	}

	public static boolean checkTemplate(InputStream stream, TrickTemplateType type) {
		switch (type) {
			case DEFAULT_EXCEL:
			case RISK_INFORMATION:
				return isExcel(stream);
			case REPORT:
			case RISK_REGISTER:
			case RISK_SHEET:
			case SOA:
				return isDocx(stream);
			default:
				return false;
		}
	}

	private static boolean isExcel(InputStream stream) {
		try {
			return SpreadsheetMLPackage.load(stream).getWorkbookPart() != null;
		} catch (Docx4JException e) {
			return false;
		}
	}

}
