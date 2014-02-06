/**
 * 
 */
package lu.itrust.business.view.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceAssetType;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
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
 * @author eom
 * 
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Asset")
public class ControllerAsset {

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private AssessmentManager assessmentManager;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private ChartGenerator chartGenerator;

	/**
	 * select: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Select/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String select(@PathVariable int id, Principal principal, Locale locale, HttpSession session) {
		try {

			// retrieve asset
			Asset asset = serviceAsset.get(id);
			if (asset == null)
				return JsonMessage.Error(messageSource.getMessage("error.asset.not_found", null, "Asset cannot be found", locale));

			// set asset selected or unselected (toggle)
			if (asset.isSelected())
				assessmentManager.unSelectAsset(asset);
			else
				assessmentManager.selectAsset(asset);

			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.asset.update.successfully", null, "Asset was updated successfully", locale));
		} catch (InvalidAttributesException e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));

		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
		}
	}

	/**
	 * selectMultiple: <br>
	 * Description
	 * 
	 * @param ids
	 * @param principal
	 * @param locale
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale, HttpSession session) {

		// init list of errors
		List<String> errors = new LinkedList<String>();

		// parse each sent id's
		for (Integer id : ids) {
			try {

				select(id, principal, locale, session);
				/*
				 * // retrieve asset Asset asset = serviceAsset.get(id); if (asset == null)
				 * errors.add(JsonMessage.Error(messageSource.getMessage("error.asset.not_found",
				 * null, "Asset cannot be found", locale)));
				 * 
				 * // check if asset if (asset.isSelected()) assessmentManager.unSelectAsset(asset);
				 * else assessmentManager.selectAsset(asset);
				 */
			} catch (Exception e) {
				e.printStackTrace();
				errors.add(JsonMessage.Error(messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale)));
			}
		}
		return errors;
	}

	@RequestMapping("/Add")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String edit(Model model, HttpSession session, Principal principal) throws Exception {
		model.addAttribute("assettypes", serviceAssetType.loadAll());
		return "analysis/components/widgets/assetForm";
	}
	
	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Delete/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String[] delete(@PathVariable int id, Principal principal, Locale locale, HttpSession session) {
		try {

			// delete asset ( delete asset from from assessments) then from assets
			customDelete.deleteAsset(serviceAsset.get(id));

			// retrun success message
			return new String[] { "success", messageSource.getMessage("success.asset.delete.successfully", null, "Asset was deleted successfully", locale) };
		} catch (Exception e) {

			// return error message
			e.printStackTrace();
			return new String[] { "error", messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale) };
		}
	}

	/**
	 * section: <br>
	 * Description
	 * 
	 * @param model
	 * @param session
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) {

		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;

		// load all assets of analysis to model
		model.addAttribute("assets", serviceAsset.findByAnalysis(integer));

		return "analysis/components/asset";
	}

	/**
	 * edit: <br>
	 * Description
	 * 
	 * @param id
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Edit/{id}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer id, Model model, Principal principal, HttpSession session) throws Exception {

		// add all assettypes to model
		model.addAttribute("assettypes", serviceAssetType.loadAll());

		// add asset object to model
		model.addAttribute("asset", serviceAsset.get(id));

		return "analysis/components/widgets/assetForm";
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param value
	 * @param session
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	List<String[]> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {

		// create error list
		List<String[]> errors = new LinkedList<>();
		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale) });
				return errors;
			}

			// retrieve analysis object
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				errors.add(new String[] { "analysis", messageSource.getMessage("error.analysis.not_found", null, "Selected analysis cannot be found", locale) });
				return errors;
			}

			// create new asset object
			Asset asset = new Asset();

			// build asset
			if (!buildAsset(errors, asset, value, locale))

				// return error on failure
				return errors;

			// check if asset is to be created (new)
			if (asset.getId() < 1) {

				// create assessments for the new asset
				assessmentManager.build(asset, idAnalysis);
			} else {

				// update existing asset object
				serviceAsset.merge(asset);

				// update selected status
				if (asset.isSelected())
					assessmentManager.selectAsset(asset);
				else
					assessmentManager.unSelectAsset(asset);
			}
		} catch (ConstraintViolationException e) {

			// add error on assettype
			errors.add(new String[] { "assetType", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
		} catch (IllegalArgumentException e) {

			// add error on asset
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}

		catch (Exception e) {

			// add general error
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}

		// return errors
		return errors;
	}

	/**
	 * aleByAsset: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/Chart/Ale")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody
	String aleByAsset(HttpSession session, Model model, Locale locale, Principal principal) throws Exception {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// generate chart of assets for this analysis
		return chartGenerator.aleByAsset(idAnalysis, locale);
	}

	/**
	 * assetByALE: <br>
	 * Description
	 * 
	 * @param session
	 * @param model
	 * @param locale
	 * @return
	 */
	@RequestMapping("/Chart/Type/Ale")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody
	String assetByALE(HttpSession session, Model model, Locale locale, Principal principal) {

		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
		if (idAnalysis == null)
			return null;

		// generate chart of assets for this analysis
		return chartGenerator.aleByAssetType(idAnalysis, locale);
	}

	/**
	 * buildAsset: <br>
	 * Description
	 * 
	 * @param errors
	 * @param asset
	 * @param source
	 * @param locale
	 * @return
	 */
	private boolean buildAsset(List<String[]> errors, Asset asset, String source, Locale locale) {
		try {

			// create json parser for the source
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// read asset id node
			int id = jsonNode.get("id").asInt();

			// check if asset is to be updated or created
			if (id > 0)
				asset.setId(jsonNode.get("id").asInt());

			// add data to object
			asset.setName(jsonNode.get("name").asText());
			asset.setValue(jsonNode.get("value").asDouble());
			asset.setSelected(jsonNode.get("selected").asBoolean());
			asset.setComment(jsonNode.get("comment").asText());
			asset.setHiddenComment(jsonNode.get("hiddenComment").asText());

			// get assettype
			JsonNode node = jsonNode.get("assetType");
			AssetType assetType = serviceAssetType.get(node.get("id").asInt());
			if (assetType == null) {
				errors.add(new String[] { "assetType", messageSource.getMessage("error.assettype.not_found", null, "Selected asset type cannot be found", locale) });
				return false;
			}

			// set asset type
			asset.setAssetType(assetType);

			// return success message
			return true;

		} catch (JsonProcessingException e) {

			// return error message
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IOException e) {

			// return error message
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (InvalidAttributesException e) {

			// return error message
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			// return error message
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		} catch (Exception e) {

			// return error message
			errors.add(new String[] { "asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale) });
			e.printStackTrace();
		}

		// return error message
		return false;
	}
}