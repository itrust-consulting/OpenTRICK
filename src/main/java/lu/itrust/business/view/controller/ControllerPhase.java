/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Phase;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServicePhase;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
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

/**
 * @author eomar
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Phase")
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;

		// add phases of this analysis
		model.addAttribute("phases", servicePhase.loadAllFromAnalysis(integer));

		return "analysis/components/phase";
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
	@RequestMapping(value = "/Delete/{idPhase}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String delete(@PathVariable int idPhase, HttpSession session, Principal principal, Locale locale) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

		// check if phase can be deleted
		if (!servicePhase.canBeDeleted(idPhase))
			return JsonMessage.Error(messageSource.getMessage("error.phase.cannot_delete", null, "Phase cannot be deleted", locale));

		// retrieve phases of analysis
		List<Phase> phases = servicePhase.loadAllFromAnalysis(idAnalysis);

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
				if (phase.getId() == idPhase) {
					servicePhase.remove(phase);
					iterator.remove();
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
		return phase == null ? JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", locale)) : JsonMessage.Success(messageSource.getMessage(
				"success.delete.phase", null, "Phase was successfully deleted", locale));
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
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	List<String[]> save(@RequestBody String source, HttpSession session, Principal principal, Locale locale) {

		// create result array
		List<String[]> errors = new LinkedList<>();

		// check if analysis exists
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null) {
			errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale) });
			return errors;
		}

		// create empty phase object
		Phase phase = new Phase();

		try {

			// try to build phase with sent data
			if (buildPhase(errors, phase, source, locale)) {

				// load analysis
				Analysis analysis = serviceAnalysis.get(idAnalysis);
				if (analysis == null) {
					errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_found", null, "Selected analysis cannot be found", locale) });
					return errors;
				}

				// check if phases are empty to add phase 0
				if (analysis.getUsedPhases().isEmpty())
					analysis.getUsedPhases().add(new Phase(0));

				// set phase number
				phase.setNumber(analysis.getUsedPhases().size());

				// parse phase og analysis
				for (Phase phase2 : analysis.getUsedPhases()) {

					// skip phase 0
					if (phase2.getNumber() == 0)
						continue;

					// check if correct begin and end date and retrun errors
					if (phase.getBeginDate().before(phase2.getBeginDate())) {
						errors.add(new String[] { "beginDate",
							messageSource.getMessage("error.phase.beginDate.less_previous", null, "Begin-date has to be greater than previous phase begin-date", locale) });
						return errors;
					} else if (phase.getEndDate().before(phase2.getEndDate())) {
						errors.add(new String[] { "endDate",
							messageSource.getMessage("error.phase.endDate.less_previous", null, "Begin-end has to be greater than previous phase begin-end", locale) });
						return errors;
					}
				}

				// add phase to analysis
				analysis.addUsedPhase(phase);

				// update analysis with phases
				serviceAnalysis.saveOrUpdate(analysis);
			}

			// return empty errors (no errors -> success)
			return errors;

		} catch (Exception e) {

			// return errors
			e.printStackTrace();
			errors.add(new String[] { "endDate", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			return errors;
		}
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
	private boolean buildPhase(List<String[]> errors, Phase phase, String source, Locale locale) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// set date format
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			// retrieve begin and end date
			phase.setBeginDate(new Date(format.parse(jsonNode.get("beginDate").asText()).getTime()));
			phase.setEndDate(new Date(format.parse(jsonNode.get("endDate").asText()).getTime()));

			// return success
			return true;
		} catch (JsonProcessingException e) {

			// set error
			errors.add(new String[] { "phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;

		} catch (IOException e) {

			// set error
			errors.add(new String[] { "phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;

		} catch (IllegalArgumentException e) {

			// set error
			errors.add(new String[] { "phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;

		} catch (Exception e) {

			// set error
			errors.add(new String[] { "phase", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
			return false;
		}
	}
}