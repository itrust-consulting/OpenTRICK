/**
 * 
 */
package lu.itrust.business.view.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author oensuifudine
 * 
 */
@Secured("ROLE_USER")
@RequestMapping("/role")
@Controller
public class ControllerRole {
	
	

}
