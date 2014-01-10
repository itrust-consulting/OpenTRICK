package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lu.itrust.business.TS.History;
import lu.itrust.business.service.ServiceHistory;
import lu.itrust.business.service.ServiceUser;
import lu.itrust.business.validator.HistoryValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ControllerHistory.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl. :
 * @version
 * @since Oct 22, 2013
 */
@Secured("ROLE_USER")
@Controller
@RequestMapping("/History")
public class ControllerHistory {

	@Autowired
	private ServiceHistory serviceHistory;

	@Autowired
	private ServiceUser serviceUser;

	@Autowired
	private MessageSource messageSource;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.replaceValidators(new HistoryValidator());
	}

	/**
	 * setServiceHistory: <br>
	 * Description
	 * 
	 * @param serviceHistory
	 */
	public void setServiceHistory(ServiceHistory serviceHistory) {
		this.serviceHistory = serviceHistory;
	}

	/**
	 * 
	 * Display all history of analysis
	 * 
	 * */
	@RequestMapping("/Analysis/{analysisId}")
	public String display(@PathVariable("analysisId") Integer analysisId, Map<String, Object> model) throws Exception {
		model.put("histories", serviceHistory.getAllFromAnalysis(analysisId));
		return "analysis/history/histories";
	}

	/**
	 * 
	 * Display single history
	 * 
	 * */
	@RequestMapping("/Analysis/{analysisId}/History/{historyid}")
	public String displayHistory(@PathVariable("historyId") Integer historyId, HttpSession session, Map<String, Object> model, RedirectAttributes redirectAttributes, Locale locale)
			throws Exception {
		History history = (History) session.getAttribute("history");
		if (history == null || history.getId() != historyId)
			history = serviceHistory.get(historyId);
		if (history == null) {
			String msg = messageSource.getMessage("errors.history.notexist", null, "History does not exist", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:/History";
		}
		model.put("history", history);
		return "analysis/history/showHistory";
	}

	/**
	 * 
	 * Request add new history
	 * 
	 * */
	@RequestMapping(value = "Analysis/{analysisId}/NewVersion", method = RequestMethod.GET, headers = "Accept=application/json")
	public String addHistory(@PathVariable("analysisId") Integer analysisId, String oldVersion, Map<String, Object> model,  Principal principal) throws Exception {
		History history = new History();
		history.setAuthor(principal.getName()); 
		model.put("history", history);
		model.put("oldVersion", oldVersion);
		model.put("analysisId", analysisId);
		
		return "analysis/components/widgets/historyForm";
	}

	@RequestMapping(value = "Analysis/{analysisId}/NewVersion/Save", method = RequestMethod.POST)
	public String save(@ModelAttribute @Valid History history, BindingResult result, @PathVariable Integer analysisId, HttpSession session, RedirectAttributes attributes) {
		if (result.hasFieldErrors())
			return "analysis/components/widgets/historyForm";
		for (String version : serviceHistory.findVersionByAnalysis(analysisId)) {
			if (History.VersionComparator(history.getVersion(), version) != 1) {
				result.rejectValue("version", "error.history.version.less_current", "Version of History entry must be greater than last Version of Analysis!");
				return "analysis/components/widgets/historyForm";
			}
		}
		attributes.addFlashAttribute("history", history);
		return "redirect:/Analysis/{analysisId}/Duplicate";
	}

	/**
	 * 
	 * Perform add new History
	 * 
	 * */
/*	@RequestMapping("Analysis/{analysisId}/History/Create")
	public String createHistory(@PathVariable("analysisId") Integer analysisId, @ModelAttribute("history") @Valid History history, BindingResult result,
			RedirectAttributes redirectAttributes, Locale locale) throws Exception {
		if (!this.serviceHistory.versionExists(analysisId, history.getVersion())) {
			try {
				this.serviceHistory.save(analysisId, history);
			} catch (Exception e) {
				String msg = messageSource.getMessage("errors.history.wrongversion", null, e.getMessage(), locale);
				redirectAttributes.addFlashAttribute("errors", msg);
				return "redirect:../History/Add";
			}
			String msg = messageSource.getMessage("success.history.added", null, "History sucessfully added!", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:../History/Display";
		} else {
			String msg = messageSource.getMessage("errors.history.versionexists", null, "History with this version already exists!", locale);
			redirectAttributes.addFlashAttribute("errors", msg);
			return "redirect:../History/Add";
		}

	}*/

	/**
	 * 
	 * Request edit single history
	 * 
	 * */
	@RequestMapping("Analysis/{analysisId}/History/Edit/{historyId}")
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
	 * 
	 * Perform edit single customer
	 * 
	 * */
	@RequestMapping("Analysis/{analysisId}/History/Update/{historyId}")
	public String updateCustomer(@PathVariable("analysisId") Integer analysisId, @PathVariable("historyId") Integer historyId, @ModelAttribute("history") @Valid History history,
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

	/**
	 * 
	 * Delete single customer
	 * 
	 * */
	@RequestMapping("Analysis/{analysisId}/History/Delete/{historyId}")
	public String deleteCustomer(@PathVariable("historyId") Integer historyId) throws Exception {
		serviceHistory.delete(serviceHistory.getDAOHistory().get(historyId));
		return "redirect:../Display";
	}

}
