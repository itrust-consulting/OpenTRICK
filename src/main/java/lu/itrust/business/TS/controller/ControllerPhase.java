/**
 * 
 */
package lu.itrust.business.TS.controller;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lu.itrust.business.TS.component.JsonMessage;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceAnalysis;
import lu.itrust.business.TS.database.service.ServicePhase;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.OpenMode;
import lu.itrust.business.TS.model.general.Phase;

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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {
		// retrieve analysis id
		OpenMode open = (OpenMode) session.getAttribute(Constant.OPEN_MODE);
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		if (integer == null)
			return null;
		model.addAttribute("isEditable", !OpenMode.isReadOnly(open) && serviceUserAnalysisRight.isUserAuthorized(integer, principal.getName(), AnalysisRight.MODIFY));
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
	@RequestMapping(value = "/Delete/{elementID}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Phase', #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable Integer elementID, HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

			// check if phase can be deleted
			if (!servicePhase.canBeDeleted(elementID))
				return JsonMessage.Error(messageSource.getMessage("error.phase.in_used", null, "Phase is in used", locale));

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			// retrieve phases of analysis
			List<Phase> phases = analysis.getPhases();

			// first phases cannot be deleted (0 and 1)
			if (phases.size() < 2)
				return JsonMessage.Error(messageSource.getMessage("error.phase.on_required", null, "This phase cannot be deleted", locale));

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
			return phase == null ? JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale))
					: JsonMessage.Success(messageSource.getMessage("success.delete.phase", null, "Phase was successfully deleted", locale));
		} catch (TrickException e) {
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage("error.phase.unknown", null, "An unknown error occurred while phase deleting", locale));
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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Map<String, String> save(@RequestBody String source, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create result array
		Map<String, String> errors = new LinkedHashMap<String, String>();

		// check if analysis exists
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);

		// create empty phase object
		Phase phase = new Phase();

		try {

			// try to build phase with sent data
			if (buildPhase(errors, phase, source, locale)) {

				// load analysis
				Analysis analysis = serviceAnalysis.get(idAnalysis);

				Phase previousphase = null;

				Phase nextphase = null;

				if (phase.getId() == -1) {

					phase.setAnalysis(analysis);

					phase.setNumber(analysis.getPhases().size() + 1);

					previousphase = analysis.findPhaseByNumber(phase.getNumber() - 1);

					// check if correct begin and end date and retrun errors
					if (previousphase != null && phase.getBeginDate().before(previousphase.getEndDate())) {
						errors.put("beginDate",
								messageSource.getMessage("error.phase.beginDate.less_previous", null, "Phase begin time has to be greater than previous phase end time", locale));
					} else if (phase.getEndDate().before(phase.getBeginDate())) {
						errors.put("endDate", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time", locale));
					}
					// add phase to analysis
					analysis.add(phase);
				} else {

					if (!servicePhase.belongsToAnalysis(idAnalysis, phase.getId())) {
						errors.put("phase", messageSource.getMessage("error.phase.not_belongs_to_analysis", null, "Phase does not belong to selected analysis", locale));
						return errors;
					}
					for (Phase tphase : analysis.getPhases()) {
						if (tphase.getId() == phase.getId()) {
							tphase.setDates(phase.getBeginDate(), phase.getEndDate());
							phase = tphase;
							break;
						}
					}

					previousphase = analysis.findPhaseByNumber(phase.getNumber() - 1);
					nextphase = analysis.findPhaseByNumber(phase.getNumber() + 1);
					// check if correct begin and end date and retrun errors
					if (previousphase != null && phase.getBeginDate().before(previousphase.getEndDate())) {
						errors.put("beginDate",
								messageSource.getMessage("error.phase.beginDate.less_previous", null, "Phase begin time has to be greater than previous phase end time", locale));
					} else if (phase.getEndDate().before(phase.getBeginDate())) {
						errors.put("endDate", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time", locale));
					} else if (nextphase != null && phase.getEndDate().after(nextphase.getBeginDate())) {
						errors.put("date", messageSource.getMessage("error.phase.endDate.more_next", null, "Phase end time has to be less than next phase begin time", locale));
						return errors;
					}
				}

				// update analysis with phases
				serviceAnalysis.saveOrUpdate(analysis);
			}

		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			errors.put("phase", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			// return errors
			TrickLogManager.Persist(e);
			errors.put("phase",  messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
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
		} catch (TrickException e) {
			errors.put("date", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return false;
		} catch (Exception e) {
			// set error
			errors.put("phase",  messageSource.getMessage("error.internal", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return false;
		}
	}
}