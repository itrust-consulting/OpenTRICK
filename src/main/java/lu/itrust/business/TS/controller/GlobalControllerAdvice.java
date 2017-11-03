/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.database.service.ServiceTSSetting;
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

	@ModelAttribute
	public void globalAttributes(Model model, Principal principal) {
		if (principal != null) {
			TSSetting url = serviceTSSetting.get(TSSettingName.USER_GUIDE_URL);
			if (url != null) {
				model.addAttribute("userGuideURLInternal", serviceTSSetting.isAllowed(TSSettingName.USER_GUIDE_URL_TYPE));
				model.addAttribute("userGuideURL", url.getString());
			}
		}
	}

	@ExceptionHandler(value = Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) throws Exception {
		if (e instanceof AccessDeniedException || e instanceof AuthenticationException || AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
			throw e;
		TrickLogManager.Persist(e);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("exception", e);
		modelAndView.addObject("url", request.getRequestURL());
		modelAndView.setViewName("error/404");
		return modelAndView;
	}
}
