/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;

import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.AppSettings;
import lu.itrust.business.service.ServiceAppSettings;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eomar
 *
 */

@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Settings")
public class ControllerSettings {

	@Autowired
	private ServiceAppSettings serviceAppSettings;

	@Autowired
	private ServiceUser serviceUser;

	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody boolean update(@RequestParam String group, @RequestParam String name, @RequestParam String key, @RequestParam String value, Principal principal)
			throws Exception {
		AppSettings appSettings = serviceAppSettings.getFromUsername(principal.getName());
		if (appSettings == null)
			appSettings = new AppSettings(serviceUser.get(principal.getName()));
		appSettings.update(group, name, key, value);
		System.out.println(String.format("Group: %s, name: %s, key:%s, value: %s ", group, name, key, value));
		serviceAppSettings.saveOrUpdate(appSettings);

		return appSettings.findByGroupAndNameAndKey(group, name, key) == value;
	}
}
