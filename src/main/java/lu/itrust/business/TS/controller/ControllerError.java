/**
 * 
 */
package lu.itrust.business.TS.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eomar
 *
 */
@Controller
public class ControllerError implements ErrorController {
	
	@RequestMapping("/Error")
	public String handleError(HttpServletRequest request) {
		final Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if(status == null)
			return "errors/404";
		else {
			final Integer statusCode = Integer.valueOf(status.toString());
			switch (statusCode) {
			case 400:
			case 401:
			case 403:
			case 404:
			case 405:
			case 500:
			case 503:
			case 504:
				return "errors/"+statusCode;
			default:
				return "errors/404";
			}
		}
	}

	@Override
	public String getErrorPath() {
		return "/Error";
	}

}
