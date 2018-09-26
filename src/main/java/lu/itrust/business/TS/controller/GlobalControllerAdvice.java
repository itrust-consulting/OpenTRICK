/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceAnalysisShareInvitation;
import lu.itrust.business.TS.database.service.ServiceMessageNotifier;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.model.general.TSSetting;
import lu.itrust.business.TS.model.general.TSSettingName;

/**
 * @author eomar
 *
 */
@ControllerAdvice
public class GlobalControllerAdvice {

	@Autowired
	private ServiceTSSetting serviceTSSetting;

	@Autowired
	private ServiceTaskFeedback serviceTaskFeedback;

	@Autowired
	private ServiceAnalysisShareInvitation serviceAnalysisShareInvitation;

	@Autowired
	private ServiceMessageNotifier serviceMessageNotifier;

	@Value("${app.settings.static.image.version}")
	private String imageVersion;

	@Value("${app.settings.static.js.version}")
	private String jsVersion;

	@Value("${app.settings.static.css.version}")
	private String cssVersion;

	@Value("${app.settings.static.font.version}")
	private String fontVersion;

	@Value("${app.settings.static.version}")
	private String staticVersion;

	@Value("${app.settings.static.user.guide.version}")
	private String userGuideVersion;

	@ModelAttribute
	public void globalAttributes(HttpServletRequest request, Model model, Principal principal) {
		if (principal != null) {
			TSSetting url = serviceTSSetting.get(TSSettingName.USER_GUIDE_URL);
			if (url != null) {
				model.addAttribute("userGuideURLInternal", serviceTSSetting.isAllowed(TSSettingName.USER_GUIDE_URL_TYPE));
				model.addAttribute("userGuideURL", url.getString());
			}
			model.addAttribute("analysisSharedCount", serviceAnalysisShareInvitation.countByUsername(principal.getName()));
			if (request.getParameter("lang") != null)
				serviceTaskFeedback.update(principal.getName(), new Locale(request.getParameter("lang")));
			model.addAttribute("userNotifcations", serviceMessageNotifier.findAllByUsername(principal.getName()));
			model.addAttribute("userGuideVersion", userGuideVersion);
		}
		model.addAttribute("jsVersion", jsVersion);
		model.addAttribute("cssVersion", cssVersion);
		model.addAttribute("fontVersion", fontVersion);
		model.addAttribute("imageVersion", imageVersion);
		model.addAttribute("staticVersion", staticVersion);

	}

	@ExceptionHandler(value = Exception.class)
	public void defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
		if (!(e instanceof AccessDeniedException || e instanceof AuthenticationException || AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null))
			TrickLogManager.Persist(e);
		throw e;
	}
}
