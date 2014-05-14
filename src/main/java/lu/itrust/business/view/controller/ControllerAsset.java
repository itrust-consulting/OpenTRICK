/**
 * 
 */
package lu.itrust.business.view.controller;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.directory.InvalidAttributesException;
import javax.servlet.http.HttpSession;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetType;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.ChartGenerator;
import lu.itrust.business.component.CustomDelete;
import lu.itrust.business.component.helper.JsonMessage;
import lu.itrust.business.service.ServiceAnalysis;
import lu.itrust.business.service.ServiceAsset;
import lu.itrust.business.service.ServiceAssetType;
import lu.itrust.business.service.ServiceDataValidation;
import lu.itrust.business.validator.AssetValidator;
import lu.itrust.business.validator.field.ValidatorField;

import org.codehaus.jackson.JsonNode;
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

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	/**
	 * select: <br>
	 * Description
	 * 
	 * @param id
	 * @param principal
	 * @param locale
	 * @return
	 */
	@RequestMapping(value = "/Select/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Asset', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	String select(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) {
		try {

			// retrieve asset
			Asset asset = serviceAsset.get(elementID);
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
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
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
		model.addAttribute("assettypes", serviceAssetType.getAll());
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
	@RequestMapping(value = "/Delete/{elementID}", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Asset', #principal, T(lu.itrust.business.TS.AnalysisRight).DELETE)")
	public @ResponseBody
	String[] delete(@PathVariable int elementID, Principal principal, Locale locale, HttpSession session) {
		try {

			// delete asset ( delete asset from from assessments) then from assets
			customDelete.deleteAsset(serviceAsset.get(elementID));

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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Section", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal) throws Exception {

		// retrieve analysis id
		Integer integer = (Integer) session.getAttribute("selectedAnalysis");
		if (integer == null)
			return null;

		// load all assets of analysis to model
		model.addAttribute("assets", serviceAsset.getAllFromAnalysis(integer));

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
	@RequestMapping("/Edit/{elementID}")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #elementID, 'Asset', #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer elementID, Model model, Principal principal, HttpSession session) throws Exception {

		// add all assettypes to model
		model.addAttribute("assettypes", serviceAssetType.getAll());

		// add asset object to model
		model.addAttribute("asset", serviceAsset.get(elementID));

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
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).MODIFY)")
	public @ResponseBody
	Map<String, String> save(@RequestBody String value, HttpSession session, Principal principal, Locale locale) {

		// create error list
		Map<String, String> errors = new LinkedHashMap<String, String>();
		try {

			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute("selectedAnalysis");
			if (idAnalysis == null) {
				errors.put("asset", messageSource.getMessage("error.analysis.no_selected", null, "There is no selected analysis", locale));
				return errors;
			}

			// retrieve analysis object
			Analysis analysis = serviceAnalysis.get(idAnalysis);
			if (analysis == null) {
				errors.put("asset", messageSource.getMessage("error.analysis.not_found", null, "Selected analysis cannot be found", locale));
				return errors;
			}

			// create new asset object
			Asset asset = new Asset();

			// build asset
			buildAsset(errors, asset, value, locale);

			if (!errors.isEmpty())
				// return error on failure
				return errors;

			asset.setValue(asset.getValue() * 1000);

			// check if asset is to be created (new)
			if (asset.getId() < 1) {
				// create assessments for the new asset and save asset and asessments into analysis
				assessmentManager.build(asset, idAnalysis);
			} else {

				if(!serviceAsset.belongsToAnalysis(idAnalysis, asset.getId())) {
					errors.put("asset", serviceDataValidation.ParseError("asset.not_belongs_to_analysis", messageSource, locale));
					return errors;
				}
				// update existing asset object
				serviceAsset.merge(asset);

				// update selected status
				if (asset.isSelected())
					assessmentManager.selectAsset(asset);
				else
					assessmentManager.unSelectAsset(asset);
			}

			// return errors
			return errors;
		} catch (Exception e) {

			// add general error
			errors.put("asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
			// return errors
			return errors;
		}
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
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
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
	 * @throws Exception
	 */
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = "Accept=application/json;charset=UTF-8")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session.getAttribute('selectedAnalysis'), #principal, T(lu.itrust.business.TS.AnalysisRight).READ)")
	public @ResponseBody
	String assetByALE(HttpSession session, Model model, Locale locale, Principal principal) throws Exception {

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
	private boolean buildAsset(Map<String, String> errors, Asset asset, String source, Locale locale) {
		try {

			// create json parser for the source
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(source);

			// read asset id node
			int id = jsonNode.get("id").asInt();

			// check if asset is to be updated or created
			if (id > 0)
				asset.setId(id);

			ValidatorField validator = serviceDataValidation.findByClass(Asset.class);
			if (validator == null)
				serviceDataValidation.register(validator = new AssetValidator());

			String name = jsonNode.get("name").asText();

			JsonNode node = jsonNode.get("assetType");
			AssetType assetType = serviceAssetType.get(node.get("id").asInt());

			double value = jsonNode.get("value").asDouble();

			String error = null;

			asset.setComment(jsonNode.get("comment").asText());
			asset.setHiddenComment(jsonNode.get("hiddenComment").asText());

			error = validator.validate(asset, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else {
				asset.setName(name);
				asset.setSelected(jsonNode.get("selected").asBoolean());
			}

			error = validator.validate(asset, "assetType", assetType);
			if (error != null)
				errors.put("assetType", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				asset.setAssetType(assetType);

			error = validator.validate(asset, "value", value);
			if (error != null)
				errors.put("value", serviceDataValidation.ParseError(error, messageSource, locale));
			else
				asset.setValue(value);

			// return success message
			return true;

		} catch (Exception e) {

			// return error message
			errors.put("asset", messageSource.getMessage(e.getMessage(), null, e.getMessage(), locale));
			e.printStackTrace();
		}

		// return error message
		return false;
	}
}