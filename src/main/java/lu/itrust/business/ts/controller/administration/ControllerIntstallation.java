package lu.itrust.business.ts.controller.administration;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletContext;
import java.net.URL;
import java.nio.file.*;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.ts.asynchronousWorkers.Worker;
import lu.itrust.business.ts.asynchronousWorkers.WorkerTSInstallation;
import lu.itrust.business.ts.component.DefaultTemplateLoader;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceCustomer;
import lu.itrust.business.ts.database.service.ServiceStorage;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.database.service.WorkersPoolManager;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.general.Customer;

@Controller
@PreAuthorize(Constant.ROLE_MIN_ADMIN)
public class ControllerIntstallation {

    @Autowired private ServiceCustomer serviceCustomer;
    @Autowired private SessionFactory sessionFactory;
    @Autowired private MessageSource messageSource;
    @Autowired private ServiceTaskFeedback serviceTaskFeedback;
    @Autowired private WorkersPoolManager workersPoolManager;
    @Autowired private TaskExecutor executor;
    @Autowired private ServiceStorage serviceStorage;
    @Autowired private DefaultTemplateLoader defaultReportTemplateLoader;

    @Autowired
    private ServletContext servletContext;

    @Value("${app.settings.version}")
    private String version;

    @Value("${app.settings.default.profile.mixed.en.sqlite.path}")
    private String defaultProfileMixedEnSqlitePath;

    @Value("${app.settings.default.profile.mixed.fr.sqlite.path}")
    private String defaultProfileMixedFrSqlitePath;

    @RequestMapping(value = "/Install", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> installTS(Model model, Principal principal, Locale locale) throws Exception {
        diagDataFolder(); 

        final Map<String, Object> result = new LinkedHashMap<>();

        try {
            Path root = serviceStorage.getRoot();

            Path en = root.resolve(defaultProfileMixedEnSqlitePath);
            Path fr = root.resolve(defaultProfileMixedFrSqlitePath);

            result.put("storageRoot", root.toString());
            result.put("expectEN", en.toString());
            result.put("expectFR", fr.toString());

            boolean hasEN = Files.exists(en);
            boolean hasFR = Files.exists(fr);
            result.put("hasEN", hasEN);
            result.put("hasFR", hasFR);

            if (!hasEN || !hasFR) {
                serviceStorage.init();
                hasEN = Files.exists(en);
                hasFR = Files.exists(fr);
                result.put("hasEN_afterInit", hasEN);
                result.put("hasFR_afterInit", hasFR);

                if (!hasEN || !hasFR) {
                    result.put("error", "Resource cannot be found in storage. Looked at: " + en + " and " + fr);
                    return result;
                }
            }
        } catch (Exception preflightEx) {
            result.put("error", "Storage preflight failed: " + preflightEx.getMessage());
            return result;
        }
       

        installProfileCustomer(result, locale);
        if (result.containsKey("error")) {
            return result;
        }

        final List<String> fileNames = new LinkedList<>();
        fileNames.add(defaultProfileMixedEnSqlitePath);
        fileNames.add(defaultProfileMixedFrSqlitePath);

        if (installDefaultProfile(fileNames, principal, result, locale)) {
            result.putIfAbsent("success", true);
        }
        return result;
    }

    private Customer installProfileCustomer(Map<String, Object> out, Locale locale) {
        try {
            return defaultReportTemplateLoader.getDefaultCustomer();
        } catch (TrickException e) {
            out.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
            return null;
        } catch (Exception e) {
            TrickLogManager.persist(e);
            out.put("error", e.getMessage());
            return null;
        }
    }

    private boolean installDefaultProfile(List<String> fileNames, Principal principal, Map<String, Object> out, Locale locale) {
        try {
            Customer customer = serviceCustomer.getProfile();
            if (customer == null) {
                customer = installProfileCustomer(out, locale);
                if (customer == null) {
                    out.put("error", messageSource.getMessage("error.customer_profile.no_found", null, "Could not find profile customer!", locale));
                    return false;
                }
            }

            
            try {
                defaultReportTemplateLoader.loadLanguages();
                defaultReportTemplateLoader.load();
            } catch (Exception ex) {
                TrickLogManager.persist(ex);
                out.put("error", "TEMPLATES: " + ex.getMessage());
                return false;
            }

            if (principal == null) {
                out.put("error", messageSource.getMessage("error.analysis.owner.no_found", null, "Could not determine owner!", locale));
                return false;
            }

            final Worker worker = new WorkerTSInstallation(
                    version, workersPoolManager, sessionFactory, serviceTaskFeedback,
                    serviceStorage, fileNames, customer.getId(), principal.getName());

            if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
                out.put("error", messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
                return false;
            }

            executor.execute(worker);
            out.put("idTask", String.valueOf(worker.getId()));
            return true;

        } catch (TrickException e) {
            out.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
            return false;
        } catch (Exception e) {
            TrickLogManager.persist(e);
            out.put("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
            return false;
        }
    }

  
    private void diagDataFolder() {
        try {
            String base = servletContext.getRealPath("/WEB-INF/data");
            System.out.println("TS-DIAG data.realPath=" + base);
            if (base != null) {
                Path root = Paths.get(base);
                if (Files.isDirectory(root)) {
                    try (Stream<Path> s = Files.list(root)) {
                        s.forEach(p -> System.out.println("TS-DIAG data.item=" + p.getFileName()));
                    }
                }
                Path sqliteDir = root.resolve("sqlite");
                if (Files.isDirectory(sqliteDir)) {
                    try (Stream<Path> s = Files.list(sqliteDir)) {
                        s.forEach(p -> System.out.println("TS-DIAG sqlite.item=" + p.getFileName()));
                    }
                }
                Path docxDir = root.resolve("docx");
                if (Files.isDirectory(docxDir)) {
                    try (Stream<Path> s = Files.list(docxDir)) {
                        s.limit(3).forEach(p -> System.out.println("TS-DIAG docx.sample=" + p.getFileName()));
                    }
                }
            }
            URL en = servletContext.getResource("/WEB-INF/data/sqlite/161T_TSE_Profile-OpenTRICK-default-mixed-EN-DB_v1.0.sqlite");
            URL fr = servletContext.getResource("/WEB-INF/data/sqlite/161T_TSE_Profile-OpenTRICK-default-mixed-FR-DB_v1.0.sqlite");
            System.out.println("TS-DIAG sqlite.en.url=" + en);
            System.out.println("TS-DIAG sqlite.fr.url=" + fr);
        } catch (Exception e) {
            System.out.println("TS-DIAG error=" + e.getMessage());
            e.printStackTrace();
        }
    }
}
