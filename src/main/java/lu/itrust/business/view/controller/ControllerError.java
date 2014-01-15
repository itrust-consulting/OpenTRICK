/**
 * 
 */
package lu.itrust.business.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eomar
 *
 */
@Controller
@RequestMapping("/Error")
public class ControllerError {
	
	@RequestMapping("/403")
	public String error403(){
		return "/errors/403";
	}
	
	@RequestMapping("/404")
	public String error404(){
		return "/errors/404";
	}
}
