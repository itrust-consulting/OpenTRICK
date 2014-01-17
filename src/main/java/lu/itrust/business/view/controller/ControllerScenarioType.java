/**
 * 
 */
package lu.itrust.business.view.controller;

import java.util.List;

import lu.itrust.business.TS.ScenarioType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceScenarioType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eom
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/ScenarioType")
public class ControllerScenarioType {
	
	@Autowired
	private ServiceScenarioType serviceScenarioType;
	
	@RequestMapping(value = "/All", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody List<ScenarioType> all() throws Exception {
		return serviceScenarioType.loadAll();
	}

}
