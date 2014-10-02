package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Language;
import lu.itrust.business.TS.settings.AnalysisSetting;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerSettings.java: <br>
 * Detailed description...
 *
 * @author EOMAR itrust consulting s.a.rl.:
 * @version 
 * @since Aug 2014
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Settings")
public class ControllerSettings {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUser serviceUser;
	
	@Autowired
	private ServiceLanguage serviceLanguage;

	@RequestMapping(value = "/Update", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	public @ResponseBody boolean update(@RequestParam String key, @RequestParam String value, Principal principal, HttpSession session) throws Exception {
		
		Integer selectedAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		
		User user = serviceUser.get(principal.getName());
		
		Analysis analysis = serviceAnalysis.get(selectedAnalysis);

		analysis.setAnalysisSettings(serviceAnalysis.getAllAnalysisSettingsFromAnalysis(selectedAnalysis));
		
		Map<String, AnalysisSetting> settings = analysis.getAnalysisSettingsFromUser(user);
		
		switch(key) {
			
			case Constant.SETTING_LANGUAGE:{
				AnalysisSetting setting = settings.get(key);
				
				Language language = serviceLanguage.getByAlpha3(value);
				
				if(language == null)
					return false;
				
				setting.setValue(value);
				
				settings.put(key, setting);
				
				serviceAnalysis.saveOrUpdate(analysis);
				
				return true;
		
			}
			case Constant.SETTING_SHOW_CSSF:{
				
				AnalysisSetting setting = settings.get(key);
				
				if(!value.equals("true") && !value.equals("false"))
					return false;
				
				setting.setValue(value);
				
				settings.put(key, setting);
				
				serviceAnalysis.saveOrUpdate(analysis);
				
				return true;
			}
			case Constant.SETTING_SHOW_UNCERTAINTY:{
				
				AnalysisSetting setting = settings.get(key);
				
				if(!value.equals("true") && !value.equals("false"))
					return false;
				
				setting.setValue(value);
				
				settings.put(key, setting);
				
				serviceAnalysis.saveOrUpdate(analysis);
				
				return true;
			}
			default: return false;
		}
	}
}
