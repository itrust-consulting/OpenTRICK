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
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServicePhase;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/Phase")
@Secured("ROLE_USER")
@Controller
public class ControllerPhase {

	@Autowired
	private ServicePhase servicePhase;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	public String section(Model model, HttpSession session, Principal principal)
			throws Exception {
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;
		model.addAttribute("phases", servicePhase.loadAllFromAnalysis(integer));
		return "analysis/components/phase";
	}
	
	@RequestMapping(value = "/Delete/{idPhase}", method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody String delete(@PathVariable int idPhase, HttpSession session, Principal principal, Locale locale) throws Exception{
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return JsonMessage.Error(messageSource.getMessage(
							"error.analysis.no_selected", null,
							"No selected analysis", locale));
		if(!servicePhase.canBeDeleted(idPhase))
			return JsonMessage.Error(messageSource.getMessage(
					"error.phase.cannot_delete", null,
					"Phase cannot be deleted", locale));
		
		List<Phase> phases = servicePhase.loadAllFromAnalysis(idAnalysis);
		if(phases.size()<2)
			return JsonMessage.Error(messageSource.getMessage(
					"error.phase.on_required", null,
					"This phase cannot be deleted", locale));
		
		Phase phase = null;
		Iterator<Phase> iterator = phases.iterator();
		while(iterator.hasNext()){
			if(phase == null){
				phase = iterator.next();
				if(phase.getId() == idPhase){
					servicePhase.remove(phase);
					iterator.remove();
				}
				else phase = null;
			} else {
				Phase phase2 = iterator.next();
				phase2.setNumber(phase2.getNumber()-1);
				servicePhase.saveOrUpdate(phase2);
			}
		}
		
		return phase == null ? JsonMessage.Error(messageSource.getMessage(
					"error.phase.not_found", null,
					"Phase cannot be found", locale)) : JsonMessage.Success(messageSource.getMessage(
							"success.delete.phase", null,
							"Phase was successfully deleted", locale));
	}

	private boolean buildPhase(List<String[]> errors, Phase phase,
			String source, Locale locale) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			phase.setBeginDate(new Date(format.parse(
					jsonNode.get("beginDate").asText()).getTime()));
			phase.setEndDate(new Date(format.parse(
					jsonNode.get("endDate").asText()).getTime()));
			return true;
		} catch (JsonProcessingException e) {
			errors.add(new String[] {
					"phase",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IOException e) {
			errors.add(new String[] {
					"phase",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			errors.add(new String[] {
					"phase",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		} catch (Exception e) {
			errors.add(new String[] {
					"phase",
					messageSource.getMessage(e.getMessage(), null,
							e.getMessage(), locale) });
			e.printStackTrace();
		}
		return false;
	}

	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<String[]> save(@RequestBody String source, HttpSession session,
			Principal principal, Locale locale) {
		Phase phase = new Phase();
		List<String[]> errors = new LinkedList<>();
		try {
			if (buildPhase(errors, phase, source, locale)) {
				Integer idAnalysis = (Integer) session
						.getAttribute("selectedAnalysis");

				if (idAnalysis == null) {
					errors.add(new String[] {
							"analysis",
							messageSource.getMessage(
									"error.analysis.no_selected", null,
									"There is no selected analysis", locale) });
					return errors;
				}

				Analysis analysis = serviceAnalysis.get(idAnalysis);
				if (analysis == null) {
					errors.add(new String[] {
							"analysis",
							messageSource.getMessage("error.analysis.no_found",
									null, "Selected analysis cannot be found",
									locale) });
					return errors;
				}
				if (analysis.getUsedPhases().isEmpty())
					analysis.getUsedPhases().add(new Phase(0));
				phase.setNumber(analysis.getUsedPhases().size());
				for (Phase phase2 : analysis.getUsedPhases()) {
					if (phase2.getNumber() == 0)
						continue;
					if (phase.getBeginDate().before(phase2.getBeginDate())) {
						errors.add(new String[] {
								"beginDate",
								messageSource
										.getMessage(
												"error.phase.beginDate.less_previous",
												null,
												"Begin-date has to be greater than previous phase begin-date",
												locale) });
						return errors;
					} else if (phase.getEndDate().before(phase2.getEndDate())) {
						errors.add(new String[] {
								"endDate",
								messageSource
										.getMessage(
												"error.phase.endDate.less_previous",
												null,
												"Begin-end has to be greater than previous phase begin-end",
												locale) });
						return errors;
					}
				}
				analysis.addUsedPhase(phase);
				serviceAnalysis.saveOrUpdate(analysis);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return errors;
	}
}
