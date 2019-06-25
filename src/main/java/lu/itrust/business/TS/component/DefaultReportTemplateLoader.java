/**
 * 
 */
package lu.itrust.business.TS.component;

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

import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.dao.DAOLanguage;
import lu.itrust.business.TS.database.dao.DAOReportTemplate;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.Language;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;

/**
 * @author eomar
 *
 */
@Component
@Transactional
public class DefaultReportTemplateLoader {

	@Value("${app.settings.report.hybrid.french.template.name}")
	private String frenchMixedReportName;

	@Value("${app.settings.report.hybrid.english.template.name}")
	private String englishMixedReportName;

	@Value("${app.settings.report.qualitative.french.template.name}")
	private String frenchQualitativeReportName;

	@Value("${app.settings.report.quantitative.french.template.name}")
	private String frenchQuantitativeReportName;

	@Value("${app.settings.report.qualitative.english.template.name}")
	private String englishQualitativeReportName;

	@Value("${app.settings.report.quantitative.english.template.name}")
	private String englishQuantitativeReportName;

	@Value("#{'${app.settings.default.template.quantitative.names}'.split(',')}")
	private String[] quantitativeTemplateNames;

	@Value("#{'${app.settings.default.template.qualitative.names}'.split(',')}")
	private String[] qualitativeTemplateNames;

	@Value("#{'${app.settings.default.template.hybrid.names}'.split(',')}")
	private String[] mixedTemplateNames;

	@Value("#{'${app.settings.default.languages}'.split(';')}")
	private List<String> defaultLanguages;

	@Autowired
	private DAOCustomer daoCustomer;

	@Autowired
	private DAOReportTemplate daoReportTemplate;

	@Autowired
	private DAOLanguage daoLanguage;

	@Autowired
	private PathManager pathManager;

	private final AtomicBoolean upToDate = new AtomicBoolean(false);

	public List<ReportTemplate> findAll() {
		return load() ? daoReportTemplate.findDefault() : Collections.emptyList();
	}

	public List<ReportTemplate> findByType(AnalysisType type) {
		return findAll().stream().filter(p -> p.getType() == type).collect(Collectors.toList());
	}

	public List<ReportTemplate> findByLanguage(String langue) {
		return findAll().stream().filter(p -> p.getLanguage().getAlpha3().equalsIgnoreCase(langue)).collect(Collectors.toList());
	}

	public List<ReportTemplate> findByTypeAndLanguage(AnalysisType type, String langue) {
		return type == AnalysisType.HYBRID ? findByLanguage(langue)
				: findAll().stream().filter(p -> p.getType() == type && p.getLanguage().getAlpha3().equalsIgnoreCase(langue)).collect(Collectors.toList());
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
		final List<ReportTemplate> templates = new ArrayList<>(4);
		for (String template : mixedTemplateNames)
			loadTemplates(templates, template, AnalysisType.HYBRID, frenchMixedReportName, englishMixedReportName);
		for (String template : qualitativeTemplateNames)
			loadTemplates(templates, template, AnalysisType.QUALITATIVE, frenchQualitativeReportName, englishQualitativeReportName);
		for (String template : quantitativeTemplateNames)
			loadTemplates(templates, template, AnalysisType.QUANTITATIVE, frenchQuantitativeReportName, englishQuantitativeReportName);

		final Customer customer = getDefaultCustomer();
		if (customer == null)
			throw new TrickException("error.default.customer.cannot.be.created", "Profile customer cannot be created");
		if (customer.getTemplates() == null || customer.getTemplates().isEmpty())
			customer.setTemplates(templates);
		else {
			Map<String, ReportTemplate> mappers = customer.getTemplates().stream().collect(Collectors.toMap(ReportTemplate::getKey, Function.identity()));
			templates.forEach(p -> {
				ReportTemplate template = mappers.get(p.getKey());
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

	private void loadTemplates(List<ReportTemplate> templates, String template, AnalysisType type, String frenchReportName, String englishReportName) {
		final String[] fields = template.split(";");
		if (fields.length != 3)
			throw new TrickException("error.default.template.invalid.parameter", "Default template cannot be load, please contact your administrator or your support!");
		final String filename = "FRA".equalsIgnoreCase(fields[1]) ? frenchReportName : englishReportName;
		try {
			final Resource file = loadResource(filename);
			final InputStream stream = file.getInputStream();
			final ReportTemplate reportTemplate = new ReportTemplate();
			reportTemplate.setLabel(fields[0]);
			reportTemplate.setLanguage(daoLanguage.getByAlpha3(fields[1]));
			reportTemplate.setVersion(fields[2]);
			reportTemplate.setFilename(file.getFilename());
			reportTemplate.setFile(IOUtils.toByteArray(stream));
			reportTemplate.setSize(reportTemplate.getFile().length);
			reportTemplate.setCreated(new Timestamp(file.lastModified()));
			reportTemplate.setEditable(false);
			reportTemplate.setType(type);
			templates.add(reportTemplate);
		} catch (IOException e) {
			throw new TrickException("error.default.template.load.failed", String.format("Default template cannot loaded, Filename: %s", filename), e, filename);
		}
	}

	private Resource loadResource(String name) throws IOException {
		final Resource resource = pathManager.getResource(String.format("/WEB-INF/data/docx/%s.docx", name));
		if (!resource.exists())
			throw new TrickException("error.default.report.template.not.exist", String.format("Default report template cannot be loaded, Filename: %s.docx", name), name);
		return resource;
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
				if (customer == null) {
					customer = new Customer();
					customer.setOrganisation("Profile");
					customer.setContactPerson("Profile");
					customer.setEmail("profile@trickservice.lu");
					customer.setPhoneNumber("00000000");
					customer.setAddress("Profile");
					customer.setCity("Profile");
					customer.setZIPCode("Profile");
					customer.setCountry("Profile");
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

}
