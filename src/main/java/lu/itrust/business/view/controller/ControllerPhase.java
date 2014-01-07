/**
 * 
 */
package lu.itrust.business.view.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eomar
 *
 */
@RequestMapping("/Phase")
@Secured("ROLE_USER")
@Controller
public class ControllerPhase {
	
	public String section(){
		return null;
	}

}
