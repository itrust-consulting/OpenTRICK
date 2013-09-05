/**
 * 
 */
package lu.itrust.business.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eom
 *
 */
@Controller
@RequestMapping("/api")
public class ControllerAPI {
	
	public String index(){
		return "indexAPI";
	}
	
	@RequestMapping("/trespass/connect")
	public String tresPassConnect(){
		return "redirect:/analysis/all";
	}

}
