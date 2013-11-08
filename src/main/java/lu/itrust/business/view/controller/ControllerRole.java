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
	
	@Autowired
	private ServiceRole serviceRole;
	
	@Autowired
	private ServiceUser serviceUser;
	
	
	@Secured("ROLE_ADMIN")
	@RequestMapping("/manage/user/{userId}")
	public String manageUserRole(@PathVariable("userId") Long userId,
			Map<String, Object> model, HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		if (user == null || user.getId() != userId) {
			model.put("userManageRole", serviceUser.get(userId));
			model.put("roles", RoleType.values());
			model.put("userRole", new Role());
			return "roleManagerForm";
		}
		return "redirect:/index";
	}


	@Secured("ROLE_ADMIN")
	@RequestMapping("/add/user/{userId}")
	public String add(@PathVariable("userId") Long userId,
			@ModelAttribute("userRole") Role userRole, BindingResult result)
			throws Exception {
		User user = serviceUser.get(userId);

		user.add(userRole);

		serviceUser.saveOrUpdate(user);

		return "redirect:/role/manage/user/{userId}";
	}
	
	@Secured("ROLE_ADMIN")
	@RequestMapping("/delete/{roleId}/user/{userId}")
	public String delete(@PathVariable("userId") Long userId,
			@PathVariable("roleId") Long roleId) throws Exception {
		User user = serviceUser.get(userId);

		Role role = user.remove(roleId);

		if (role != null)
			serviceRole.delete(role);

		serviceUser.saveOrUpdate(user);

		return "redirect:/role/manage/user/{userId}";

	}

	public void setServiceRole(ServiceRole serviceRole) {
		this.serviceRole = serviceRole;
	}


	public void setServiceUser(ServiceUser serviceUser) {
		this.serviceUser = serviceUser;
	}

}
