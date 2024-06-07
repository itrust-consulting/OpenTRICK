/**
 * 
 */

package lu.itrust.business.ts.controller.analysis;

import static lu.itrust.business.ts.constants.Constant.ACCEPT_APPLICATION_JSON_CHARSET_UTF_8;

import java.security.Principal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lu.itrust.business.ts.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.ts.component.ChartGenerator;
import lu.itrust.business.ts.component.CustomDelete;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.service.ServiceAnalysis;
import lu.itrust.business.ts.database.service.ServiceAsset;
import lu.itrust.business.ts.database.service.ServiceAssetType;
import lu.itrust.business.ts.database.service.ServiceDataValidation;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.Comparators;
import lu.itrust.business.ts.helper.DependencyGraphManager;
import lu.itrust.business.ts.helper.JsonMessage;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.asset.AssetType;
import lu.itrust.business.ts.model.general.OpenMode;
import lu.itrust.business.ts.validator.AssetValidator;
import lu.itrust.business.ts.validator.field.ValidatorField;

/**
 * ControllerAsset is a controller class that handles requests related to asset
 * analysis.
 * It provides methods for generating charts, deleting assets, editing assets,
 * and saving assets.
 * This class is responsible for managing assets and their associated
 * assessments in the analysis.
 */
@PreAuthorize(Constant.ROLE_MIN_USER)
@Controller
@RequestMapping("/Analysis/Asset")
public class ControllerAsset {

	@Autowired
	private AssessmentAndRiskProfileManager assessmentAndRiskProfileManager;

	@Autowired
	private ChartGenerator chartGenerator;

	@Autowired
	private CustomDelete customDelete;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ServiceAnalysis serviceAnalysis;

	@Autowired
	private ServiceAsset serviceAsset;

	@Autowired
	private ServiceAssetType serviceAssetType;

	@Autowired
	private ServiceDataValidation serviceDataValidation;

	/**
	 * Retrieves the ALE (Annual Loss Expectancy) by asset.
	 * 
	 * @param session   the HttpSession object
	 * @param model     the Model object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return an Object representing the chart of assets for the analysis
	 * @throws Exception if an error occurs during the chart generation
	 */
	@RequestMapping(value = "/Chart/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object aleByAsset(HttpSession session, Model model, Principal principal, Locale locale)
			throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// generate chart of assets for this analysis
		return chartGenerator.aleByAsset(idAnalysis, locale);
	}

	/**
	 * Retrieves the assets by ALE (Annual Loss Expectancy) for a specific analysis.
	 * 
	 * @param session   the HttpSession object
	 * @param model     the Model object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return an Object representing the chart of assets for the analysis
	 * @throws Exception if an error occurs during the chart generation
	 */
	@RequestMapping(value = "/Chart/Type/Ale", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object assetByALE(HttpSession session, Model model, Principal principal, Locale locale)
			throws Exception {
		// retrieve analysis id
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// generate chart of assets for this analysis
		return chartGenerator.aleByAssetType(idAnalysis, locale);
	}

	/**
	 * Deletes an asset.
	 *
	 * @param idAsset   The ID of the asset to delete.
	 * @param principal The principal object representing the currently
	 *                  authenticated user.
	 * @param locale    The locale for message localization.
	 * @param session   The HttpSession object.
	 * @return A JSON response indicating the success or failure of the delete
	 *         operation.
	 * @throws Exception If an error occurs during the delete operation.
	 */
	@RequestMapping(value = "/Delete/{idAsset}", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #idAsset, 'Asset', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String delete(@PathVariable int idAsset, Principal principal, Locale locale,
			HttpSession session) throws Exception {
		try {
			// delete asset ( delete asset from from assessments) then from
			// assets
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			customDelete.deleteAsset(idAsset, idAnalysis);
			return JsonMessage.Success(messageSource.getMessage("success.asset.delete.successfully", null,
					"Asset was deleted successfully", locale));
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return JsonMessage.Error(
					messageSource.getMessage("error.asset.delete.failed", null, "Asset cannot be deleted", locale));
		}
	}

	/**
	 * Retrieves the asset with the specified element ID and prepares the model for
	 * editing.
	 *
	 * @param elementID the ID of the asset to be edited
	 * @param model     the model object to be populated with data
	 * @param principal the principal object representing the currently
	 *                  authenticated user
	 * @param session   the HTTP session object
	 * @param locale    the locale object representing the user's preferred language
	 * @return the name of the view to be rendered for editing the asset
	 * @throws Exception if an error occurs during the retrieval or preparation of
	 *                   the asset
	 */
	@RequestMapping(value = "/Edit/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(@PathVariable Integer elementID, Model model, Principal principal, HttpSession session,
			Locale locale) throws Exception {
		// add all assettypes to model
		model.addAttribute("assettypes", serviceAssetType.getAll());
		// add asset object to model
		model.addAttribute("asset", serviceAsset.get(elementID));
		loadAnalysisSettings(model, (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		return "jsp/analyses/single/components/asset/form";
	}

	/**
	 * Edits the asset.
	 * 
	 * @param model     the model object
	 * @param session   the HttpSession object
	 * @param principal the Principal object
	 * @return the view name for the asset form
	 * @throws Exception if an error occurs during the editing process
	 */
	@RequestMapping("/Add")
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public String edit(Model model, HttpSession session, Principal principal) throws Exception {
		model.addAttribute("assettypes", serviceAssetType.getAll());
		loadAnalysisSettings(model, (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS));
		return "jsp/analyses/single/components/asset/form";
	}

	/**
	 * Retrieves the risk by asset as a chart.
	 *
	 * @param session   the HttpSession object
	 * @param model     the Model object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return an Object representing the risk by asset chart
	 * @throws Exception if an error occurs during the retrieval process
	 */
	@RequestMapping(value = "/Chart/Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object riskByAsset(HttpSession session, Model model, Principal principal, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.riskByAsset(idAnalysis, locale);
	}

	/**
	 * Retrieves the risk by asset type as a chart.
	 *
	 * @param session   the HttpSession object
	 * @param model     the Model object
	 * @param principal the Principal object
	 * @param locale    the Locale object
	 * @return an Object representing the risk by asset type chart
	 * @throws Exception if an error occurs during the retrieval process
	 */
	@RequestMapping(value = "/Chart/Type/Risk", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public @ResponseBody Object riskByAssetType(HttpSession session, Model model, Principal principal, Locale locale)
			throws Exception {
		Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		return chartGenerator.riskByAssetType(idAnalysis, locale);
	}

	/**
	 * Saves the value of the asset and returns the results.
	 *
	 * @param value     the value of the asset as a JSON string
	 * @param session   the HttpSession object
	 * @param principal the Principal object representing the authenticated user
	 * @param locale    the Locale object representing the user's locale
	 * @return an Object containing the results of the save operation
	 * @throws Exception if an error occurs during the save operation
	 */
	@RequestMapping(value = "/Save", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody Object save(@RequestBody String value, HttpSession session, Principal principal, Locale locale)
			throws Exception {
		Map<String, Object> results = new LinkedHashMap<>(), errors = new HashMap<>();
		try {
			results.put("errors", errors);
			// retrieve analysis id
			Integer idAnalysis = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
			if (serviceAnalysis.isProfile(idAnalysis))
				throw new TrickException("error.action.not_authorise", "Action does not authorised");
			JsonNode assetNode = new ObjectMapper().readTree(value);
			// read asset id node
			int idAsset = getInt("id", assetNode);

			Asset asset = idAsset > 1 ? serviceAsset.getFromAnalysisById(idAnalysis, idAsset) : new Asset();
			if (asset == null) {
				errors.put("asset", messageSource.getMessage("error.asset.not_belongs_to_analysis", null,
						"Asset does not belong to selected analysis", locale));
				return results;
			}
			// build asset
			buildAsset(assetNode, idAnalysis, errors, asset, locale);
			if (!errors.isEmpty())
				return results;
			asset.setValue(asset.getValue() * 1000);
			if (asset.getId() > 0)
				serviceAsset.saveOrUpdate(asset);
			else {
				Analysis analysis = serviceAnalysis.get(idAnalysis);
				analysis.add(asset);
				serviceAnalysis.saveOrUpdate(analysis);
			}

			// update selected status
			if (asset.isSelected())
				assessmentAndRiskProfileManager.selectAsset(asset);
			else
				assessmentAndRiskProfileManager.unSelectAsset(asset);
			// create assessments for the new asset and save asset and
			// Assessments into analysis
			assessmentAndRiskProfileManager.build(asset, idAnalysis);
			results.put("id", asset.getId());
		} catch (TrickException e) {
			errors.put("asset", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			errors.put("asset", messageSource.getMessage(e.getMessage(), null, locale));
		}
		return results;
	}

	/**
	 * Retrieves the section of the analysis for the given model, session,
	 * principal, and locale.
	 * 
	 * @param model     the model object to populate with data
	 * @param session   the HTTP session object
	 * @param principal the principal object representing the authenticated user
	 * @param locale    the locale object representing the user's preferred language
	 * @return the name of the view to render
	 * @throws Exception if an error occurs during the retrieval of the analysis
	 *                   section
	 */
	@GetMapping(value = "/Section", headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).READ)")
	public String section(Model model, HttpSession session, Principal principal, Locale locale) throws Exception {
		// retrieve analysis id
		final Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		final Analysis analysis = serviceAnalysis.get(integer);
		final AnalysisType type = analysis.getType();
		final List<Asset> assets = analysis.getAssets();
		assets.sort(Comparators.ASSET());

		loadAnalysisSettings(model, integer);

		if (Analysis.isILR(analysis)) {
			DependencyGraphManager.computeImpact(analysis.getAssetNodes());
			model.addAttribute("assetNodes", analysis.getAssetNodes().stream()
					.collect(Collectors.toMap(e -> e.getAsset().getId(), Function.identity())));
			model.addAttribute("isILR", true);
		}

		// load all assets of analysis to model
		if (AnalysisType.isQuantitative(type))
			model.addAttribute("assetALE",
					AssessmentAndRiskProfileManager.ComputeAssetALE(assets, analysis.findSelectedAssessments()));
		model.addAttribute("assets", assets);
		model.addAttribute("type", type);
		model.addAttribute("isEditable", !OpenMode.isReadOnly((OpenMode) session.getAttribute(Constant.OPEN_MODE)));

		model.addAttribute("show_uncertainty", analysis.isUncertainty());
		return "jsp/analyses/single/components/asset/asset";
	}

	/**
	 * Selects an asset with the given element ID.
	 *
	 * @param elementID the ID of the asset to be selected
	 * @param principal the principal object representing the authenticated user
	 * @param locale    the locale for message localization
	 * @param session   the HTTP session object
	 * @return a JSON string representing the success or error message
	 * @throws Exception if an error occurs during the selection process
	 */
	@RequestMapping(value = "/Select/{elementID}", method = RequestMethod.GET, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #elementID, 'Asset', #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody String select(@PathVariable int elementID, Principal principal, Locale locale,
			HttpSession session) throws Exception {
		try {
			// retrieve asset
			assessmentAndRiskProfileManager.toggledAsset(elementID);
			// return success message
			return JsonMessage.Success(messageSource.getMessage("success.asset.update.successfully", null,
					"Asset was updated successfully", locale));
		} catch (Exception e) {
			// return error message
			TrickLogManager.Persist(e);
			return JsonMessage
					.Error(messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
		}
	}

	/**
	 * Selects multiple assets based on the provided IDs.
	 * 
	 * @param ids       The list of asset IDs to select.
	 * @param principal The principal object representing the authenticated user.
	 * @param locale    The locale for message localization.
	 * @param session   The HttpSession object.
	 * @return A list of error messages, if any.
	 * @throws Exception If an error occurs during the selection process.
	 */
	@RequestMapping(value = "/Select", method = RequestMethod.POST, headers = ACCEPT_APPLICATION_JSON_CHARSET_UTF_8)
	@PreAuthorize("@permissionEvaluator.userIsAuthorized(#session, #principal, T(lu.itrust.business.ts.model.analysis.rights.AnalysisRight).MODIFY)")
	public @ResponseBody List<String> selectMultiple(@RequestBody List<Integer> ids, Principal principal, Locale locale,
			HttpSession session) throws Exception {
		Integer integer = (Integer) session.getAttribute(Constant.SELECTED_ANALYSIS);
		// init list of errors
		final List<String> errors = new LinkedList<>();
		if (!serviceAsset.belongsToAnalysis(integer, ids)) {
			errors.add(JsonMessage.Error(messageSource.getMessage("label.unauthorized_asset", null,
					"One of the assets does not belong to this analysis!", locale)));
			return errors;
		}
		assessmentAndRiskProfileManager.toggledAssets(ids);
		return errors;
	}

	/**
	 * Builds an Asset object based on the provided JSON data and performs
	 * validation.
	 * 
	 * @param jsonNode   The JSON data containing the asset information.
	 * @param idAnalysis The ID of the analysis.
	 * @param errors     A map to store any validation errors encountered during the
	 *                   build process.
	 * @param asset      The Asset object to be populated with the data from the
	 *                   JSON.
	 * @param locale     The locale used for error message localization.
	 * @return True if the asset was successfully built and validated, false
	 *         otherwise.
	 */
	private boolean buildAsset(JsonNode jsonNode, Integer idAnalysis, Map<String, Object> errors, Asset asset,
			Locale locale) {
		try {
			// check if asset is to be updated or created
			ValidatorField validator = serviceDataValidation.findByClass(Asset.class);
			if (validator == null)
				serviceDataValidation.register(validator = new AssetValidator());

			String name = jsonNode.get("name").asText("").trim();

			JsonNode node = jsonNode.get("assetType");

			AssetType assetType = serviceAssetType.get(node.get("id").asInt(0));

			Double value = getDouble(jsonNode);

			String error = null;

			asset.setComment(jsonNode.get("comment").asText("").trim());

			asset.setHiddenComment(jsonNode.get("hiddenComment").asText("").trim());

			asset.setRelatedName(jsonNode.get("relatedName").asText("").trim());

			error = validator.validate(asset, "name", name);
			if (error != null)
				errors.put("name", serviceDataValidation.ParseError(error, messageSource, locale));
			else if ((asset.getId() > 0 && !asset.getName().equalsIgnoreCase(name) || asset.getId() < 0)
					&& serviceAsset.exist(idAnalysis, name))
				errors.put("name", messageSource.getMessage("error.asset.duplicate", null,
						String.format("Asset name is already in use", name), locale));
			else
				asset.setName(name);

			asset.setSelected(jsonNode.get("selected").asBoolean());

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

		} catch (TrickException e) {
			// return error message
			errors.put("asset", messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
			TrickLogManager.Persist(e);
		} catch (Exception e) {
			// return error message
			errors.put("asset", messageSource.getMessage("error.500.message", null, "Internal error occurred", locale));
			TrickLogManager.Persist(e);
		}

		// return error message
		return false;
	}

	/**
	 * Converts a JSON node to a double value.
	 *
	 * @param jsonNode the JSON node containing the value to be converted
	 * @return the double value of the JSON node, or null if the conversion fails
	 * @throws ParseException if the value cannot be parsed as a double
	 */
	private Double getDouble(JsonNode jsonNode) throws ParseException {
		try {
			return NumberFormat.getInstance(Locale.FRANCE).parse(jsonNode.get("value").asText()).doubleValue();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Retrieves an integer value from the specified field in the given JSON node.
	 * If the field does not exist or cannot be parsed as an integer, it returns 0.
	 *
	 * @param fieldName the name of the field to retrieve the integer value from
	 * @param jsonNode  the JSON node containing the field
	 * @return the integer value of the field, or 0 if the field does not exist or
	 *         cannot be parsed as an integer
	 */
	private int getInt(String fieldName, JsonNode jsonNode) {
		try {
			return jsonNode.get(fieldName).asInt(0);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Loads the analysis settings for a given integer value and adds them to the
	 * model.
	 *
	 * @param model   the model to which the analysis settings will be added
	 * @param integer the integer value used to retrieve the analysis settings
	 */
	private void loadAnalysisSettings(Model model, Integer integer) {
		final Map<String, String> settings = serviceAnalysis.getSettingsByIdAnalysis(integer);
		final AnalysisSetting rawSetting = AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN;
		final AnalysisSetting hiddenCommentSetting = AnalysisSetting.ALLOW_RISK_HIDDEN_COMMENT;
		model.addAttribute("showHiddenComment",
				Analysis.findSetting(hiddenCommentSetting, settings.get(hiddenCommentSetting.name())));
		model.addAttribute("showRawColumn", Analysis.findSetting(rawSetting, settings.get(rawSetting.name())));

	}
}
