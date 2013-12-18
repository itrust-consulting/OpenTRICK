/**
 * 
 */
package lu.itrust.business.view.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.service.ServiceRole;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.view.model.Role;
import lu.itrust.business.view.model.RoleType;
import lu.itrust.business.view.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
