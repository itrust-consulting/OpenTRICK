/**
 * 
 */
package lu.itrust.business.ts.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author eomar
 *
 */
@Controller
public class ControllerError implements ErrorController {

	@RequestMapping("/Error")
	public ModelAndView handleError(HttpServletRequest request, HttpServletResponse response) {
		if (response.isCommitted())
			return null;
		final ModelAndView modelAndView = new ModelAndView("jsp/errors/404");
		final Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (status != null) {
			final Integer statusCode = Integer.valueOf(status.toString());
			switch (statusCode) {
				case 200:
					break;
				case 400:
				case 401:
				case 403:
				case 405:
				case 500:
				case 503:
				case 504:
					modelAndView.setViewName("jsp/errors/" + statusCode);
					break;
				default:
					modelAndView.setViewName("jsp/errors/404");
			}
		}
		return modelAndView;
	}
	/*
	 * @Override
	 * public String getErrorPath() {
	 * return "/Error";
	 * }
	 */
}
