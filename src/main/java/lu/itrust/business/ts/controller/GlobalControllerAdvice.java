/**
 * 
 */
package lu.itrust.business.ts.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.database.service.ServiceAnalysisShareInvitation;
import lu.itrust.business.ts.database.service.ServiceMessageNotifier;
import lu.itrust.business.ts.database.service.ServiceTSSetting;
import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.model.general.TSSetting;
import lu.itrust.business.ts.model.general.TSSettingName;

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

	@ModelAttribute
	public void globalAttributes(HttpServletRequest request, Model model, Principal principal) {
		if (principal != null) {
			TSSetting url = serviceTSSetting.get(TSSettingName.USER_GUIDE_URL);
			if (url != null) {
				model.addAttribute("userGuideURLInternal",
						serviceTSSetting.isAllowed(TSSettingName.USER_GUIDE_URL_TYPE));
				model.addAttribute("userGuideURL", url.getString());
			}
			model.addAttribute("analysisSharedCount",
					serviceAnalysisShareInvitation.countByUsername(principal.getName()));
			if (request.getParameter("lang") != null)
				serviceTaskFeedback.update(principal.getName(), new Locale(request.getParameter("lang")));
			model.addAttribute("userNotifcations", serviceMessageNotifier.findAllByUsername(principal.getName()));

			model.addAttribute("customJSs", new ArrayList<String>());
			model.addAttribute("customCSSs", new ArrayList<String>());
		}
	}

	@ExceptionHandler(value = Exception.class)
	public String defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
		if (!(e instanceof AccessDeniedException || e instanceof AuthenticationException
				|| AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null))
			TrickLogManager.Persist(e);
		throw e;
	}
}
