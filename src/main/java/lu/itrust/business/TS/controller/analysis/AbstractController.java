/**
 * 
 */
package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ADMIN_ALLOWED_TICKETING;
import static lu.itrust.business.TS.constants.Constant.ALLOWED_TICKETING;
import static lu.itrust.business.TS.constants.Constant.ROLE_MIN_USER;
import static lu.itrust.business.TS.constants.Constant.TICKETING_NAME;
import static lu.itrust.business.TS.constants.Constant.TICKETING_URL;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.database.service.ServiceUser;
import lu.itrust.business.TS.database.service.WorkersPoolManager;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.ReportSetting;
import lu.itrust.business.TS.model.cssf.helper.ColorManager;
import lu.itrust.business.TS.model.general.TSSettingName;
import lu.itrust.business.TS.model.general.TicketingSystem;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@PreAuthorize(ROLE_MIN_USER)
public abstract class AbstractController {
	
	@Autowired
	protected TaskExecutor executor;

	@Autowired
	protected ServiceUser serviceUser;

	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	protected MessageSource messageSource;

	@Autowired
	protected ServiceAnalysis serviceAnalysis;

	@Autowired
	protected ServiceTSSetting serviceTSSetting;

	@Autowired
	protected WorkersPoolManager workersPoolManager;

	@Autowired
	protected ServiceTaskFeedback serviceTaskFeedback;

	protected boolean loadUserSettings(Principal principal, @Nullable TicketingSystem ticketingSystem, @Nullable Model model, @Nullable User user) {
		boolean allowedTicketing = false, adminAllowedTicketing = false;
		try {
			adminAllowedTicketing = serviceTSSetting.isAllowed(TSSettingName.SETTING_ALLOWED_TICKETING_SYSTEM_LINK);
			if (!(ticketingSystem == null || ticketingSystem.getType() == null) && StringUtils.hasText(ticketingSystem.getUrl()) && ticketingSystem.isEnabled()
					&& adminAllowedTicketing) {
				if (user == null)
					user = serviceUser.get(principal.getName());
				allowedTicketing = user.getCredentials().containsKey(ticketingSystem);
				if (model != null && allowedTicketing) {
					model.addAttribute(TICKETING_NAME, StringUtils.capitalize(ticketingSystem.getType().name().toLowerCase()));
					model.addAttribute(TICKETING_URL, ticketingSystem.getUrl());
				}
			}
		} catch (Exception e) {
			TrickLogManager.Persist(e);
		} finally {
			if (model != null) {
				model.addAttribute(ALLOWED_TICKETING, allowedTicketing);
				model.addAttribute(ADMIN_ALLOWED_TICKETING, adminAllowedTicketing);
			}
		}
		return allowedTicketing;
	}
	
	protected Map<ReportSetting, String> loadReportSettings(Analysis analysis) {
		final Map<ReportSetting, String> settings = new LinkedHashMap<>();
		for (ReportSetting setting : ReportSetting.values()) {
			if (setting == ReportSetting.CEEL_COLOR)
				continue;
			settings.put(setting, analysis.findSetting(setting));
		}
		return settings;
	}
	
	protected void setupQualitativeParameterUI(Model model, Analysis analysis) {
		model.addAttribute("impactLabel", analysis.findImpacts().stream().filter(scaleType -> !scaleType.getName().equals(Constant.DEFAULT_IMPACT_NAME)).findAny()
				.map(ScaleType::getName).orElse(null));
		int level = analysis.getLikelihoodParameters().size() - 1;
		model.addAttribute("maxImportance", level * level);
		model.addAttribute("colorManager", new ColorManager(analysis.getRiskAcceptanceParameters()));
	}
}
