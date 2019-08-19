/**
 *
 */
package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.model.analysis.helper.AnalysisDataManagement.findLastPhase;
import static lu.itrust.business.TS.model.analysis.helper.AnalysisDataManagement.findPhaseById;
import static lu.itrust.business.TS.model.analysis.helper.AnalysisDataManagement.findPhaseByNumber;
import static lu.itrust.business.TS.model.analysis.helper.AnalysisDataManagement.findPreviousPhase;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.general.helper.PhaseForm;
import lu.itrust.business.TS.model.general.helper.PhaseManager;

/**
 * @author eomar
 *
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Phase")
@Controller
public class ControllerPhase {

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

	/**
	 * section: <br>
	 * Description
	 *
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis id
		final OpenMode open = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null)
			return null;
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		PhaseManager.updateStatistics(analysis);
		model.addAttribute("phases", analysis.getPhases());
		model.addAttribute("totalPhase", PhaseManager.computeTotal(analysis.getPhases()));
		model.addAttribute("isEditable", !OpenMode.isReadOnly(open) && analysis.isUserAuthorized(principal.getName(), AnalysisRight.MODIFY));
		return "analyses/single/components/phase/home";
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @param idPhase
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}/Delete", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #id, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable Integer id, HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			// retrieve analysis id
			final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			// check if phase can be deleted
			if (!servicePhase.canBeDeleted(id))
				return JsonMessage.Error(messageSource.getMessage("error.phase.in_used", null, "Phase is in used", locale));

			final Analysis analysis = serviceAnalysis.get(idAnalysis);

			// retrieve phases of analysis
			final List<Phase> phases = analysis.getPhases();

			phases.sort((p1, p2) -> Integer.compare(p1.getNumber(), p2.getNumber()));

			// first phases cannot be deleted (0 and 1)
			if (phases.size() < 2)
				return JsonMessage.Error(messageSource.getMessage("error.phase.on_required", null, "This phase cannot be deleted", locale));

			// iterate through phases
			final Iterator<Phase> iterator = phases.iterator();
			while (iterator.hasNext()) {
				final Phase current = iterator.next();
				if (current.getId() != id)
					continue;
				iterator.remove();
				servicePhase.delete(current);
				break;
			}

			Phase current = null, previous = null;
			for (int i = 0, numering = 1; i < phases.size(); i++) {
				current = phases.get(i);
				if (current.getNumber() == 0)
					continue;
				current.setNumber(numering++);
				if (previous != null)
					current.setBeginDate(previous.getEndDate());
				previous = current;
			}

			serviceAnalysis.saveOrUpdate(analysis);
			// return result
			return JsonMessage.Success(messageSource.getMessage("success.delete.phase", null, "Phase was successfully deleted", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.phase.unknown", null, "An unknown error occurred while phase deleting", locale));
		}
	}

	@GetMapping(value = "/Add", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String add(Model model, HttpSession session, Principal principal, Locale locale) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Phase last = servicePhase.findAllByIdAnalysis(idAnalysis);
		final PhaseForm form = new PhaseForm();
		if (last != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(last.getEndDate());
			form.setBegin(last.getEndDate());
			calendar.add(Calendar.YEAR, 1);
			form.setEnd(calendar.getTime());
			form.setBeginEnabled(false);
			form.setEndEnabled(true);
			form.setNumber(last.getNumber() + 1);
		} else
			form.setNumber(1);
		model.addAttribute("form", form);
		return "analyses/single/components/phase/form";
	}

	@GetMapping(value = "/{id}/Edit", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #id, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer id, Model model, HttpSession session, Principal principal, Locale locale) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Phase phase = servicePhase.getFromAnalysisById(idAnalysis, id);
		final Phase previous = phase.getNumber() <= 1 ? null : servicePhase.getFromAnalysisByPhaseNumber(idAnalysis, phase.getNumber() - 1);
		final PhaseForm form = new PhaseForm();
		form.setEnd(phase.getEndDate());
		if (previous == null) {
			form.setBegin(phase.getBeginDate());
			form.setBeginEnabled(true);
		} else {
			form.setBegin(previous.getEndDate());
			form.setBeginEnabled(false);
		}
		form.setEndEnabled(true);
		form.setId(phase.getId());
		form.setNumber(phase.getNumber());
		model.addAttribute("form", form);
		return "analyses/single/components/phase/form";
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @param source
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/Save", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object save(@ModelAttribute PhaseForm form, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create result array
		final Map<String, String> errors = new LinkedHashMap<String, String>();
		// check if analysis exists
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		try {
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			final Phase phase = form.getId() > 0 ? findPhaseById(form.getId(), analysis) : new Phase();
			if (phase == null)
				throw new AccessDeniedException(messageSource.getMessage("error.phase.not_belongs_to_analysis", null, "Phase does not belong to selected analysis", locale));
			final Phase previous = phase.getId() > 1 ? findPreviousPhase(phase, analysis) : findLastPhase(analysis);

			if (!isValid(form, phase, previous, errors, locale))
				return errors;
			Phase next = null;
			if (phase.getId() < 1) {
				phase.setAnalysis(analysis);
				phase.setNumber((previous == null ? analysis.getPhases().size() : previous.getNumber()) + 1);
				analysis.add(phase);
			} else {
				next = findPhaseByNumber(phase.getNumber() + 1, analysis);
				if (next != null && phase.getEndDate().after(next.getBeginDate()))
					errors.put("warning", messageSource.getMessage("error.phase.endDate.more_next", null, "Phase end time has to be less than next phase begin time", locale));
			}
			// update analysis with phases
			serviceAnalysis.saveOrUpdate(analysis);
			return errors.isEmpty() ? JsonMessage.Success(messageSource.getMessage("success.phase.save.update", new Object[] { form.getId() }, null, locale)) : errors;

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			if (e instanceof AccessDeniedException)
				throw e;
			TrickLogManager.Persist(e);
			errors.put("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
		return errors;

	}

	private boolean isValid(PhaseForm form, Phase phase, Phase previous, Map<String, String> errors, Locale locale) {
		if (form.getBegin() == null && previous == null)
			errors.put("begin", messageSource.getMessage("error.phase.beginDate.empty", null, "Phase begin date cannot be empty", locale));
		if (form.getEnd() == null)
			errors.put("end", messageSource.getMessage("error.phase.endDate.empty", null, "Phase end date cannot be empty", locale));
		else if (form.getBegin() != null && form.getBegin().after(form.getEnd()) || previous != null && previous.getEndDate().after(form.getEnd()))
			errors.put("end", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time", locale));
		if (errors.isEmpty()) {
			if (previous == null)
				phase.setBeginDate(new Date(form.getBegin().getTime()));
			else
				phase.setBeginDate(new Date(previous.getEndDate().getTime()));
			phase.setEndDate(new Date(form.getEnd().getTime()));
		}

		return errors.isEmpty();
	}

}