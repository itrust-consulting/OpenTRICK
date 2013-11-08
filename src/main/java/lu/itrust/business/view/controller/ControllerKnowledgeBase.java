package lu.itrust.business.view.controller;

import lu.itrust.business.service.ServiceLanguage;
import lu.itrust.business.service.ServiceMeasureDescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** 
 * ControllerKnowledgeBase: <br>
 * This Controller contains all actions of the knowledgebase page. <br>
 * This includes the management of: <br>
 * - Languages
 * - Customers
 * - Measures
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 0.1
 * @since Oct 10, 2013
 */

@Secured("ROLE_ADMIN")
@RequestMapping("/KnowLedgeBase")
@Controller
public class ControllerKnowledgeBase {
	
	@Autowired
	private ServiceLanguage serviceLanguage;
		
	@Autowired
	private ServiceMeasureDescription serviceMeasureDescription;
	
	@RequestMapping("/Display")
	public String displayKowledgeBase()  {
		return "knowledgebase";
	}
	
	
		
	public void setServiceLanguage(ServiceLanguage serviceLanguage){
		this.serviceLanguage = serviceLanguage;
	}
		
	public void setServiceMeasureDescription(ServiceMeasureDescription serviceMeasureDescription){
		this.serviceMeasureDescription = serviceMeasureDescription;
	}	
}