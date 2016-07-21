/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lu.itrust.business.TS.constants.Constant;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Risk-evolution")
public class ControllerRiskEvolution {
	
	@RequestMapping
	public String home(Principal principal){
		return "analyses/risk-evolution/home";
	}

}
