/**
 * 
 */
package lu.itrust.business.view.controller;

import lu.itrust.business.TS.tsconstant.Constant;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author oensuifudine
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_ADMIN)
@RequestMapping("/role")
@Controller
public class ControllerRole {
	
}
