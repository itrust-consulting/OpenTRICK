/**
 * 
 */
package lu.itrust.business.TS.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

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
	public ModelAndView handleError(HttpServletRequest request) {
		final ModelAndView modelAndView = new ModelAndView("errors/404");
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
				modelAndView.setViewName("errors/" + statusCode);
				break;
			}
		}
		return modelAndView;
	}

	@Override
	public String getErrorPath() {
		return "/Error";
	}

}
