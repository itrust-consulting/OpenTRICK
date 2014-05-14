package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.History;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.HistoryValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerHistory.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/History")
public class ControllerHistory {

	@Autowired
	private ServiceHistory serviceHistory;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	/**
	 * initBinder: <br>
	 * Description
	 * 
	 * @param binder
	 * @throws Exception
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) throws Exception {
		HistoryValidator historyValidator = new HistoryValidator();
		serviceDataValidation.register(historyValidator);
		binder.replaceValidators(historyValidator);
	}

	/**
	 * display: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String display(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model, HttpSession session, Principal principal) throws Exception {

		// load all of analysis
		model.put("histories", serviceHistory.getAllFromAnalysis(analysisId));

		return "analysis/history/histories";
	}

	/**
	 * displayHistory: <br>
	 * Description
	 * 
	 * @param historyId
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/{elementID}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'History', #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String displayHistory(@PathVariable Integer elementID, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes,
			Locale locale, Principal principal) throws Exception {

		History history = serviceHistory.get(elementID);
		if (history == null) {

			// return error message and redirect to analysis page
			String msg = messageSource.getMessage("errors.history.notexist", null, "History does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/Analysis";
		} else {

			// add history object to model
			model.put("history", history);

			return "analysis/history/showHistory";
		}
	}

	/**
	 * editHistory: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param historyId
	 * @param session
	 * @param model
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	// @RequestMapping("Analysis/{analysisId}/History/Edit/{historyId}")
	public String editHistory(@PathVariable("analysisId") Integer analysisId, @PathVariable("historyId") Integer historyId, HttpSession session, Map<String, Object> model,
			RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		History history = (History) session.getAttribute("history");
		if (history == null || history.getId() != historyId)
			history = serviceHistory.get(historyId);
		if (history == null) {
			String msg = messageSource.getMessage("errors.history.notexist", null, "History does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/Analysis/" + analysisId + "/History/Display";
		}
		model.put("history", history);
		return "analysis/history/editHistory";
	}

	/**
	 * updateHistory: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param historyId
	 * @param history
	 * @param result
	 * @param redirectAttributes
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	// @RequestMapping("Analysis/{analysisId}/History/Update/{historyId}")
	public String updateHistory(@PathVariable("analysisId") Integer analysisId, @PathVariable("historyId") Integer historyId, @ModelAttribute("history") @Valid History history,
			BindingResult result, RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		if (history == null || history.getId() != historyId) {
			String msg = messageSource.getMessage("errors.history.update.notrecognized", null, "History not recognized", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
		} else {
			try {
				serviceHistory.saveOrUpdate(history);
				String msg = messageSource.getMessage("success.history.update.success", null, "History had been updated!", locale);
				redirectAttributes.addFlashAttribute("success", msg);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.history.update.fail", null, "History update failed!", locale);
				redirectAttributes.addFlashAttribute("errors", msg);
			}
		}
		return "redirect:/Analysis/" + analysisId + "/History/Display";
	}
}