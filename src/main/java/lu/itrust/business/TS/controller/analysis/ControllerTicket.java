/**
 * 
 */
package lu.itrust.business.TS.controller.analysis;

import static lu.itrust.business.TS.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;
import static lu.itrust.business.TS.constants.Constant.TICKETING_NAME;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lu.itrust.business.TS.asynchronousWorkers.Worker;
import lu.itrust.business.TS.asynchronousWorkers.WorkerGenerateTickets;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceCustomer;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.exception.ResourceNotFoundException;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.form.LinkForm;
import lu.itrust.business.TS.form.TicketingForm;
import lu.itrust.business.TS.helper.JsonMessage;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.general.Credential;
import lu.itrust.business.TS.model.general.CredentialType;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.TicketingSystem;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.TS.model.ticketing.TicketingProject;
import lu.itrust.business.TS.model.ticketing.TicketingTask;
import lu.itrust.business.TS.model.ticketing.builder.Client;
import lu.itrust.business.TS.model.ticketing.builder.ClientBuilder;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
@Controller
@PreAuthorize(Constant.ROLE_MIN_USER)
@RequestMapping("/Analysis/Ticketing")
public class ControllerTicket extends AbstractController {

	@Autowired
	private ServiceMeasure serviceMeasure;

	@Autowired
	private ServiceCustomer serviceCustomer;

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	@RequestMapping(value = "/{idAnalysis}/Link", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String linkToProject(@PathVariable Integer idAnalysis, @RequestBody String idProject, Principal principal, Locale locale) {
		final Analysis analysis = serviceAnalysis.get(idAnalysis);
		if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), null, null))
			throw new ResourceNotFoundException();
		if (StringUtils.isEmpty(idProject))
			return JsonMessage.Error(messageSource.getMessage("error.project.not_found", null, "Project cannot be found", locale));
		final String OldProject = serviceAnalysis.getProjectIdByIdentifier(analysis.getIdentifier());
		if (!(OldProject == null || OldProject.equals(idProject)))
			return JsonMessage.Error(
					messageSource.getMessage("error.analysis.linked.to.another.project", null, "Another project is already linked to another version of this analysis", locale));
		analysis.setProject(idProject);
		serviceAnalysis.saveOrUpdate(analysis);
		return JsonMessage.Success(messageSource.getMessage("success.link.analysis.project", null, "Analysis has been successfully linked to project", locale));
	}

	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#idAnalysis, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).ALL)")
	@RequestMapping(value = "/{idAnalysis}/Load", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public String loadProject(@PathVariable Integer idAnalysis, Model model, Principal principal, RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			final Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null))
				throw new ResourceNotFoundException();
			final String idProject = serviceAnalysis.getProjectIdByIdentifier(analysis.getIdentifier());
			client = buildClient(principal.getName(), analysis.getCustomer().getTicketingSystem());
			List<TicketingProject> projects = null;
			if (idProject != null) {
				TicketingProject project = client.findProjectById(idProject);
				if (project != null)
					(projects = new LinkedList<>()).add(project);
			} else 
				projects = client.findProjects();
			model.addAttribute("projects", projects);
			model.addAttribute("analysis", analysis);
			return String.format("analyses/all/forms/ticketing_%s_link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			return "redirect:/Error";
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@RequestMapping(value = "/UnLink", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	public @ResponseBody String unlinkToProject(@RequestBody List<Integer> ids, Model model, Principal principal, Locale locale) {
		final User user = serviceUser.get(principal.getName());
		final Customer customer = serviceCustomer.findByAnalysisId(ids.size() > 1 ? ids.get(0) : -1);
		if (customer == null || !(user.containsCustomer(customer) && loadUserSettings(principal, customer.getTicketingSystem(), model, user)))
			throw new ResourceNotFoundException();
		String name = (String) model.asMap().get(TICKETING_NAME);
		ids.stream().map(id -> serviceAnalysis.findByIdAndCustomer(id, customer)).filter(a -> a != null && a.hasProject() && a.isUserAuthorized(user, AnalysisRight.ALL))
				.forEach(analysis -> {
					analysis.setProject(null);
					serviceAnalysis.saveOrUpdate(analysis);
				});
		return JsonMessage.Success(ids.size() > 1
				? messageSource.getMessage("sucess.analyses.unlink.from.project", new String[] { name }, String.format("Analyses has been successfully unlinked from %s", name),
						locale)
				: messageSource.getMessage("sucess.analysis.unlink.from.project", new String[] { name }, String.format("Analysis has been successfully unlinked from %s", name),
						locale));
	}

	@RequestMapping(value = "/Measure/Generate", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String generateTickets(@RequestBody TicketingForm form, Principal principal, HttpSession session, Locale locale) {
		final Integer analysisId = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Customer customer = serviceCustomer.findByAnalysisId(analysisId);
		if (!loadUserSettings(principal, customer.getTicketingSystem(), null, null))
			throw new ResourceNotFoundException();
		final Worker worker = new WorkerGenerateTickets(analysisId, null, form);
		if (!serviceTaskFeedback.registerTask(principal.getName(), worker.getId(), locale)) {
			worker.cancel();
			return JsonMessage.Error(messageSource.getMessage("error.task_manager.too.many", null, "Too many tasks running in background", locale));
		} else {
			((WorkerGenerateTickets) worker).setClient(buildClient(principal.getName(), customer.getTicketingSystem()));
			executor.execute(worker);
			return JsonMessage.Success(messageSource.getMessage("success.starting.creating.tickets", null, "Please wait while creating tickets", locale));
		}
	}

	@RequestMapping(value = "/Measure/Link-form", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String linkTickets(@RequestBody List<Integer> measureIds, Model model, Principal principal, HttpSession session, RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null))
				throw new ResourceNotFoundException();
			if (analysis.hasProject()) {
				client = buildClient(principal.getName(), analysis.getCustomer().getTicketingSystem());
				List<Measure> measures;
				List<String> excludes = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
						.filter(measure -> !StringUtils.isEmpty(measure.getTicket())).map(Measure::getTicket).collect(Collectors.toList());

				if (measureIds.size() > 5) {
					Map<Integer, Integer> contains = measureIds.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					measures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && StringUtils.isEmpty(measure.getTicket())).collect(Collectors.toList());
				} else {
					measures = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> StringUtils.isEmpty(measure.getTicket()) && measureIds.contains(measure.getId())).collect(Collectors.toList());
				}
				measures.sort(new MeasureComparator());
				model.addAttribute("tasks", client.findOtherTasksByProjectId(analysis.getProject(), excludes, 0, 40));
				model.addAttribute("measures", measures);
			}
			return String.format("analyses/single/components/ticketing/%s/forms/link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@RequestMapping(value = "/Measure/Load", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String loadTickets(@RequestBody List<Integer> measureIds, @RequestParam(name = "startIndex") int startIndex, Model model, Principal principal, HttpSession session,
			RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null))
				throw new ResourceNotFoundException();
			if (analysis.hasProject()) {
				client = buildClient(principal.getName(), analysis.getCustomer().getTicketingSystem());
				List<String> excludes = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
						.filter(measure -> !StringUtils.isEmpty(measure.getTicket())).map(Measure::getTicket).collect(Collectors.toList());
				model.addAttribute("tasks", client.findOtherTasksByProjectId(analysis.getProject(), excludes, startIndex, 40));
			}
			return String.format("analyses/single/components/ticketing/%s/forms/link", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@RequestMapping(value = "/Measure/Open", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).READ)")
	public String openTickets(@RequestBody List<Integer> measures, Model model, Principal principal, HttpSession session, RedirectAttributes attributes, Locale locale) {
		Client client = null;
		try {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null))
				throw new ResourceNotFoundException();
			if (analysis.hasProject()) {
				client = buildClient(principal.getName(), analysis.getCustomer().getTicketingSystem());
				Map<Integer, String> keyIssues;
				if (measures.size() > 5) {
					Map<Integer, Integer> contains = measures.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					keyIssues = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && !StringUtils.isEmpty(measure.getTicket()))
							.collect(Collectors.toMap(Measure::getId, Measure::getTicket));
				} else {
					keyIssues = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> !StringUtils.isEmpty(measure.getTicket()) && measures.contains(measure.getId()))
							.collect(Collectors.toMap(Measure::getId, Measure::getTicket));
				}
				Map<String, TicketingTask> taskMap = client.findByIdsAndProjectId(analysis.getProject(), keyIssues.values()).stream()
						.collect(Collectors.toMap(TicketingTask::getId, Function.identity()));
				if (!taskMap.isEmpty()) {
					final List<TicketingTask> tasks = new LinkedList<>();
					measures.stream().filter(id -> taskMap.containsKey(keyIssues.get(id))).forEach(id -> tasks.add(taskMap.get(keyIssues.get(id))));
					tasks.sort((e1, e2) -> NaturalOrderComparator.compareTo(e1.getName(), e2.getName()));
					model.addAttribute("first", tasks.get(0));
					model.addAttribute("tasks", tasks);
				}
			}
			return String.format("analyses/single/components/ticketing/%s/home", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
	}

	@RequestMapping(value = "/Measure/Synchronise", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public String synchroniseWithTicketingSystem(@RequestBody List<Integer> ids, Model model, Principal principal, HttpSession session, RedirectAttributes attributes,
			Locale locale) {
		Client client = null;
		try {
			final Analysis analysis = serviceAnalysis.get((Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
			if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), model, null))
				throw new ResourceNotFoundException();
			if (analysis.hasProject()) {
				client = buildClient(principal.getName(), analysis.getCustomer().getTicketingSystem());
				Map<Integer, Measure> measuresMap;
				if (ids.size() > 5) {
					Map<Integer, Integer> contains = ids.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
					measuresMap = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> contains.containsKey(measure.getId()) && !StringUtils.isEmpty(measure.getTicket()))
							.collect(Collectors.toMap(Measure::getId, Function.identity()));
				} else {
					measuresMap = analysis.getAnalysisStandards().stream().flatMap(listMeasures -> listMeasures.getMeasures().stream())
							.filter(measure -> !StringUtils.isEmpty(measure.getTicket()) && ids.contains(measure.getId()))
							.collect(Collectors.toMap(Measure::getId, Function.identity()));
				}

				List<Measure> measures = new LinkedList<>();

				List<String> keyIssues = new LinkedList<>();

				ids.stream().filter(id -> measuresMap.containsKey(id)).forEach(id -> {
					Measure measure = measuresMap.get(id);
					measures.add(measure);
					keyIssues.add(measure.getTicket());
				});

				Map<String, TicketingTask> tasks = client.findByIdsAndProjectId(analysis.getProject(), keyIssues).stream()
						.collect(Collectors.toMap(task -> task.getId(), Function.identity()));
				List<? extends IParameter> parameters = analysis.findParametersByType(Constant.PARAMETERTYPE_TYPE_IMPLEMENTATION_RATE_NAME);
				measures.sort(new MeasureComparator());
				model.addAttribute("measures", measures);
				model.addAttribute("parameters", parameters);
				model.addAttribute("tasks", tasks);
			}
			return String.format("analyses/single/components/ticketing/%s/forms/synchronise", model.asMap().get(TICKETING_NAME).toString().toLowerCase());
		} catch (ResourceNotFoundException e) {
			throw e;
		} catch (TrickException e) {
			attributes.addAttribute("error", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} catch (Exception e) {
			attributes.addAttribute("error", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
			return "redirect:/Error";
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}

	}

	@RequestMapping(value = "/Measure/UnLink", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String unlinkTickets(@RequestBody List<Integer> measureIds, Principal principal, HttpSession session, Locale locale) {
		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Customer customer = serviceCustomer.findByAnalysisId(idAnalysis);
		if (!loadUserSettings(principal, customer.getTicketingSystem(), null, null))
			throw new ResourceNotFoundException();
		serviceMeasure.getByIdAnalysisAndIds(idAnalysis, measureIds).forEach(measure -> {
			if (!StringUtils.isEmpty(measure.getTicket())) {
				measure.setTicket(null);
				serviceMeasure.saveOrUpdate(measure);
			}
		});
		if (measureIds.size() > 1)
			return JsonMessage.Success(messageSource.getMessage("success.unlinked.measures.from.tickets", null, "Measures has been successfully unlinked from tickets", locale));
		return JsonMessage.Success(messageSource.getMessage("success.unlinked.measure.from.ticket", null, "Measure has been successfully unlinked from a ticket", locale));

	}

	@RequestMapping(value = "/Measure/Link", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.TS.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String linkTicket(@RequestBody LinkForm form, Principal principal, HttpSession session, Locale locale) {

		final Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(idAnalysis);

		if (!loadUserSettings(principal, analysis.getCustomer().getTicketingSystem(), null, null))
			throw new ResourceNotFoundException();
		if (StringUtils.isEmpty(form.getIdTicket()))
			return JsonMessage.Error(messageSource.getMessage("error.ticket.not_found", null, "Ticket cannot be found", locale));

		if (!analysis.hasProject())
			return JsonMessage.Error(messageSource.getMessage("error.analysis.no_project", null, "Please link your analysis to a project and try again", locale));
		Measure measure = analysis.findMeasureById(form.getIdMeasure());
		if (measure == null)
			return JsonMessage.Error(messageSource.getMessage("error.measure.not_found", null, "Measure cannot be found", locale));
		if (!StringUtils.isEmpty(measure.getTicket())) {
			return measure.getTicket().equals(form.getIdTicket())
					? JsonMessage.Success(messageSource.getMessage("info.measure.already.link", null, "Measure has been already linked to this ticket", locale))
					: JsonMessage.Error(messageSource.getMessage("error.measure.already.link", null, "Measure is already linked to another ticket", locale));
		}
		if (analysis.hasTicket(form.getIdTicket()))
			return JsonMessage.Error(messageSource.getMessage("error.ticket.already.linked", null, "Ticket is already linked to another measure", locale));

		measure.setTicket(form.getIdTicket());
		serviceMeasure.saveOrUpdate(measure);
		return JsonMessage.Success(messageSource.getMessage("success.link.measure.to.ticket", null, "Measure has been successfully linked to a ticket", locale));

	}

	private Client buildClient(String username, TicketingSystem ticketingSystem) {
		final User user = serviceUser.get(username);
		if (ticketingSystem == null || !ticketingSystem.isEnabled())
			throw new ResourceNotFoundException();
		final Credential credential = user.getCredentials().get(ticketingSystem);
		if (credential == null)
			throw new TrickException("error.user.no.ticketing.system.credential", "You do have ticketing system credentials for this customer");
		final Map<String, Object> settings = new HashMap<>(3);
		if (credential.getType() == CredentialType.TOKEN)
			settings.put("token", credential.getValue());
		else {
			settings.put("username", credential.getName());
			settings.put("password", credential.getValue());
		}
		settings.put("url", ticketingSystem.getUrl());
		Client client = null;
		boolean isConnected = false;
		try {
			client = ClientBuilder.Build(ticketingSystem.getType().name().toLowerCase());
			isConnected = client.connect(settings);
		} catch (TrickException e) {
			throw e;
		} catch (Exception e) {
			throw new TrickException("error.ticket_system.connexion.failed", "Unable to connect to your ticketing system", e);
		} finally {
			if (!(client == null || isConnected)) {
				try {
					client.close();
				} catch (IOException e) {
					TrickLogManager.Persist(e);
				}
			}
		}
		return client;
	}

}
