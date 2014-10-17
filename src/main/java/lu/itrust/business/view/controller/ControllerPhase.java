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
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.exception.TrickException;
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
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;

		// add phases of this analysis
		model.addAttribute("phases", servicePhase.getAllFromAnalysis(integer));

		return "analysis/components/phase/phase";
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Phase', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable Integer elementID, HttpSession session, Principal principal, Locale locale) throws Exception {
		try {
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			
			// check if phase can be deleted
			if (!servicePhase.canBeDeleted(elementID))
				return JsonMessage.Error(messageSource.getMessage("error.phase.in_used", null, "Phase is in used", customLocale!=null?customLocale:locale));

			Analysis analysis = serviceAnalysis.get(idAnalysis);

			// retrieve phases of analysis
			List<Phase> phases = analysis.getUsedPhases();

			// first phases cannot be deleted (0 and 1)
			if (phases.size() < 2)
				return JsonMessage.Error(messageSource.getMessage("error.phase.on_required", null, "This phase cannot be deleted", customLocale!=null?customLocale:locale));

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
			return phase == null ? JsonMessage.Error(messageSource.getMessage("error.phase.not_found", null, "Phase cannot be found", customLocale!=null?customLocale:locale)) : JsonMessage.Success(messageSource
					.getMessage("success.delete.phase", null, "Phase was successfully deleted", customLocale!=null?customLocale:locale));
		} catch (Exception e) {
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null)
				return JsonMessage.Error(messageSource.getMessage("error.analysis.no_selected", null, "No selected analysis", locale));

			Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage("error.phase.unknown", null, "An unknown error occurred while phase deleting", customLocale!=null?customLocale:locale));
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
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody List<String[]> save(@RequestBody String source, HttpSession session, Principal principal, Locale locale) throws Exception {

		// create result array
		List<String[]> errors = new LinkedList<>();

		// check if analysis exists
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null) {
			errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale) });
			return errors;
		}
		
		Locale customLocale = new Locale(serviceAnalysis.getLanguageOfAnalysis(idAnalysis).getAlpha3().substring(0, 2));
		// create empty phase object
		Phase phase = new Phase();

		try {

			// try to build phase with sent data
			if (buildPhase(errors, phase, source, customLocale!=null?customLocale:locale)) {

				// load analysis
				Analysis analysis = serviceAnalysis.get(idAnalysis);
				if (analysis == null) {
					errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_found", null, "Analysis cannot be found", customLocale!=null?customLocale:locale) });
					return errors;
				}

				Phase previousphase = null;
				
				Phase nextphase = null;
				
				if(phase.getId() == -1) {
					phase.setNumber(analysis.getUsedPhases().size() + 1);

					previousphase = analysis.getPhaseByNumber(phase.getNumber()-1);
					
					// check if correct begin and end date and retrun errors
					if (previousphase != null && phase.getBeginDate().before(previousphase.getEndDate())) {
						errors.add(new String[] { "beginDate", messageSource.getMessage("error.phase.beginDate.less_previous", null, "Phase begin time has to be greater than previous phase end time", customLocale!=null?customLocale:locale) });
						return errors;
					} else if (phase.getEndDate().before(phase.getBeginDate())) {
						errors.add(new String[] { "endDate", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time", customLocale!=null?customLocale:locale) });
						return errors;
					} 
				

				// add phase to analysis
				analysis.addUsedPhase(phase);
				} else {
					
					if (!servicePhase.belongsToAnalysis(idAnalysis, phase.getId())) {
						errors.add(new String[] { "phase", messageSource.getMessage("error.phase.not_belongs_to_analysis", null, "Phase does not belong to selected analysis", customLocale!=null?customLocale:locale) });
						return errors;
					}
					
					for(Phase tphase : analysis.getUsedPhases()){
						if(tphase.getId()==phase.getId()){
							tphase.setDates(phase.getBeginDate(), phase.getEndDate());
							phase = tphase;
							break;
						}
					}
					
					previousphase = analysis.getPhaseByNumber(phase.getNumber()-1);
					
					nextphase = analysis.getPhaseByNumber(phase.getNumber()+1);
					
					// check if correct begin and end date and retrun errors
					if (previousphase != null && phase.getBeginDate().before(previousphase.getEndDate())) {
						errors.add(new String[] { "beginDate", messageSource.getMessage("error.phase.beginDate.less_previous", null, "Phase begin time has to be greater than previous phase end time", customLocale!=null?customLocale:locale) });
						return errors;
					} else if (phase.getEndDate().before(phase.getBeginDate())) {
						errors.add(new String[] { "endDate", messageSource.getMessage("error.phase.endDate.less", null, "Phase end time has to be greater than phase begin time", customLocale!=null?customLocale:locale) });
						return errors;
					}  else if(nextphase != null && phase.getEndDate().after(nextphase.getBeginDate())) {
						errors.add(new String[] { "Date", messageSource.getMessage("error.phase.endDate.more_next", null, "Phase end time has to be less than next phase begin time", customLocale!=null?customLocale:locale) });
						return errors;
					}
				}
				
				
				// update analysis with phases
				serviceAnalysis.saveOrUpdate(analysis);
			}

		} catch (TrickException e) {
			
			e.printStackTrace();
			errors.add(new String[] { "endDate", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), customLocale!=null?customLocale:locale) });
		} catch (Exception e) {
			
			// return errors
			e.printStackTrace();
			errors.add(new String[] { "endDate", messageSource.getMessage(e.getMessage(), null, e.getMessage(), customLocale!=null?customLocale:locale) });
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
	private boolean buildPhase(List<String[]> errors, Phase phase, String source, Locale locale) {

		try {

			// create json parser
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// set date format
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			phase.setId(jsonNode.get("id").asInt());
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