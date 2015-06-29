/**
 * 
 */
package lu.itrust.business.TS.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.Phase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author eomar
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Phase")
@Controller
public class ControllerPhase {

	private static final String SELECTED_ANALYSIS_READ_ONLY = "selected-analysis-read-only";

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceUserAnalysisRight serviceUserAnalysisRight;

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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Boolean isReadOnly = (Boolean) session.getAttribute(SELECTED_ANALYSIS_READ_ONLY);
		if (isReadOnly == null)
			isReadOnly = false;
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (integer == null)
			return null;
		model.addAttribute("isEditable", !isReadOnly && serviceUserAnalysisRight.isUserAuthorized(integer, principal.getName(), AnalysisRight.MODIFY));
		// add phases of this analysis
		model.addAttribute("phases", servicePhase.getAllFromAnalysis(integer));

		return "analyses/single/components/phase/phase";
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
	@RequestMapping(value = "/Delete/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable Integer elementID, HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());

			// check if phase can be deleted
			if (!servicePhase.canBeDeleted(elementID))
				return JsonMessage.Error(messageSource.getMessage("error.phase.in_used", null, "Phase is in used", customLocale != null ? customLocale : locale));

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			// retrieve phases of analysis
			List<Phase> phases = analysis.getPhases();

			// first phases cannot be deleted (0 and 1)
			if (phases.size() < 2)
				return JsonMessage.Error(messageSource.getMessage("error.phase.on_required", null, "This phase cannot be deleted", customLocale != null ? customLocale : locale));

			// iterate through phases
			Phase phase = null;
			Iterator<Phase> iterator = phases.iterator();
			while (iterator.hasNext()) {

				// set next phase
				if (phase == null) {
					phase = iterator.next();
					// delete phase
					if (phase.getId() == elementID) {
						iterator.remove();
						servicePhase.delete(phase);
					} else
						phase = null;
				} else {
					// update phase number of other phases
					Phase phase2 = iterator.next();
					phase2.setNumber(phase2.getNumber() - 1);
					servicePhase.saveOrUpdate(phase2);
				}
			}

			// return result
			return phase == null ? JsonMessage
					.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", customLocale != null ? customLocale : locale)) : JsonMessage
					.Success(messageSource.getMessage("success.delete.phase", null, "Phase was successfully deleted", customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.phase.unknown", null, "An unknown error occurred while phase deleting", customLocale != null ? customLocale
					: locale));
		}
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> save(@RequestBody String source, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create result array
		Map<String, String> errors = new LinkedHashMap<String, String>();

		// check if analysis exists
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (idAnalysis == null) {
			errors.put("analysis", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
			return errors;
		}

		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha2());
		// create empty phase object
		Phase phase = new Phase();

		try {

			// try to build phase with sent data
			if (buildPhase(errors, phase, source, customLocale != null ? customLocale : locale)) {

				// load analysis
				Analysis analysis = serviceAnalysis.get(idAnalysis);
				if (analysis == null) {
					errors.put("analysis", messageSource.getMessage("error.analysis.no_found", null, "Analysis cannot be found", customLocale != null ? customLocale : locale));
					return errors;
				}

				Phase previousphase = null;

				Phase nextphase = null;

				if (phase.getId() == -1) {

					phase.setAnalysis(analysis);

					phase.setNumber(analysis.getPhases().size() + 1);

					previousphase = analysis.getPhaseByNumber(phase.getNumber() - 1);

					// check if correct begin and end date and retrun errors
					if (previousphase != null && phase.getBeginDate().before(previousphase.getEndDate())) {
						errors.put("beginDate", messageSource.getMessage("error.phase.beginDate.less_previous", null,
								"Phase begin time has to be greater than previous phase end time", customLocale != null ? customLocale : locale));
					} else if (phase.getEndDate().before(phase.getBeginDate())) {
						errors.put("endDate", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time",
								customLocale != null ? customLocale : locale));
					}
					// add phase to analysis
					analysis.addPhase(phase);
				} else {

					if (!servicePhase.belongsToAnalysis(idAnalysis, phase.getId())) {
						errors.put("phase", messageSource.getMessage("error.phase.not_belongs_to_analysis", null, "Phase does not belong to selected analysis",
								customLocale != null ? customLocale : locale));
						return errors;
					}
					for (Phase tphase : analysis.getPhases()) {
						if (tphase.getId() == phase.getId()) {
							tphase.setDates(phase.getBeginDate(), phase.getEndDate());
							phase = tphase;
							break;
						}
					}

					previousphase = analysis.getPhaseByNumber(phase.getNumber() - 1);

					nextphase = analysis.getPhaseByNumber(phase.getNumber() + 1);
					// check if correct begin and end date and retrun errors
					if (previousphase != null && phase.getBeginDate().before(previousphase.getEndDate())) {
						errors.put("beginDate", messageSource.getMessage("error.phase.beginDate.less_previous", null,
								"Phase begin time has to be greater than previous phase end time", customLocale != null ? customLocale : locale));
					} else if (phase.getEndDate().before(phase.getBeginDate())) {
						errors.put("endDate", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time",
								customLocale != null ? customLocale : locale));
					} else if (nextphase != null && phase.getEndDate().after(nextphase.getBeginDate())) {
						errors.put("date", messageSource.getMessage("error.phase.endDate.more_next", null, "Phase end time has to be less than next phase begin time",
								customLocale != null ? customLocale : locale));
						return errors;
					}
				}

				// update analysis with phases
				serviceAnalysis.saveOrUpdate(analysis);
			}

		} catch (TrickException e) {
			e.printStackTrace();
			errors.put("phase", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale != null ? customLocale : locale));
		} catch (Exception e) {
			// return errors
			e.printStackTrace();
			errors.put("phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale != null ? customLocale : locale));
		}
		// return empty errors (no errors -> success)
		return errors;

	}

	/**
	 * buildPhase: <br>
	 * Description
	 * 
	 * @param errors
	 * @param phase
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildPhase(Map<String, String> errors, Phase phase, String source, Locale locale) {
		try {
			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			// set date format
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			phase.setId(jsonNode.get("id").asInt());
			phase.setDates(new Date(format.parse(jsonNode.get("beginDate").asText()).getTime()), new Date(format.parse(jsonNode.get("endDate").asText()).getTime()));
			// return success
			return true;
		} catch (JsonProcessingException e) {
			// set error
			errors.put("phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// set error
			errors.put("phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			// set error
			errors.put("phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		} catch (TrickException e) {
			errors.put("date", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			// set error
			errors.put("phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			return false;
		}
	}
}