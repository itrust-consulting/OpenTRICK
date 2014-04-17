/**
 * 
 */
package lu.itrust.business.view.controller;

import lu.itrust.business.component.helper.JsonMessage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eomar
 * 
 */
@Controller
@RequestMapping("/Error")
public class ControllerError {

	
	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody String error(@ModelAttribute("error") String error){
		return JsonMessage.Error(error);
	}
	
	/**
	 * error403: <br>
	 * Description
	 * 
	 * @return
	 */
	@RequestMapping("/403")
	public String error403() {
		return "/errors/403";
	}

	/**
	 * error404: <br>
	 * Description
	 * 
	 * @return
	 */
	@RequestMapping("/404")
	public String error404() {
		return "/errors/404";
	}
}
