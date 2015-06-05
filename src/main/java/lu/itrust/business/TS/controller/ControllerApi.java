package lu.itrust.business.TS.controller;

import java.util.List;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.api.ApiResult;
import lu.itrust.business.TS.model.api.ExternalNotification;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ControllerApi.java: <br>
 *     This controller is responsible for accepting external notifications
 *     which serve as risk indicator (such as IDS alerts). From these the
 *     probabilities that certain events happen, are deduced and stored in
 *     variables ready to be used within the TRICK service user interface
 *     (asset/scenario estimation).
 * @author SMU, itrust consulting s.à r.l.
 * @since Jun 4, 2015
 */
@Controller
@RequestMapping("/Api")
@ResponseBody
public class ControllerApi {

	@RequestMapping
	public String home() {
		return "It works.";		
	}
	
	/**
	 * This method is responsible for accepting external notifications
	 * which serve as risk indicator (such as IDS alerts). From these, the
	 * probabilities that certain events happen, are deduced and stored in
	 * variables ready to be used within the TRICK service user interface
	 * (asset/scenario estimation).
	 * @param data One of multiple notification sent to TRICK Service.
	 * @return Returns an error code (0 = success).
	 */
	@RequestMapping(value = "/notify", headers = Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8, method = RequestMethod.POST)
	public Object notify(@RequestBody List<ExternalNotification> data) {
		return new ApiResult(0);
	}
}
