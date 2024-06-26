
/**
 * This file contains functions related to the management of the RRF (Risk Reduction Factor).
 * The RRF Manager handles error handling, updating RRF values, fixing thresholds, loading RRF data, and initializing sliders for measures and scenarios.
 */

/**
 * Displays an error message in the RRF editor.
 *
 * @param {string} message - The error message to display. If not provided, a default message will be used.
 * @returns {boolean} - Returns false.
 */
function rrfError(message) {
	if (message == undefined)
		message = MessageResolver("error.unknown.occurred", "An unknown error occurred");
	var $alert = $(
		"<label class='label label-danger'>" + message + " <a href='#' style='margin-left:10px;color:#fff;'   onclick='return $(this).parent().remove();'>&times;</a></label>")
		.appendTo($("#rrf-error", "#rrfEditor").empty());
	setTimeout(function () {
		$alert.remove();
	}, 5000);
	return false;
}

/**
 * Updates the maximum RRF (Relative Response Factor).
 * 
 * @returns {boolean} Returns false.
 */
function updateMaxRRF() {
	application["maxRRF"] = parseInt($("td[data-name='max_rrf']", "#section_parameter").text().replace(/\s/g, '').replace(",", "."));
	return false;
}

/**
 * Fixes the maximum RRF threshold from RRF.
 * If the application's "maxRRF" property is undefined, it updates it by calling the "updateMaxRRF" function.
 * Sets the "data-trick-max-value" attribute of the "ilr_rrf_threshold" table cell to the value of the application's "maxRRF" property.
 * @returns {boolean} Returns false.
 */
function fixMaxRRFThresholdFromRRF() {
	if (application["maxRRF"] === undefined)
		updateMaxRRF();
	$("td[data-name='ilr_rrf_threshold']", "#section_parameter").attr("data-trick-max-value", application["maxRRF"]);
	return false;
}

/**
 * Fixes the minimum RRF value based on the RRF threshold.
 * @returns {boolean} Returns false.
 */
function fixMinRRFFromRRFThreshold() {
	let minRRF = parseInt($("td[data-name='ilr_rrf_threshold']", "#section_parameter").text().replace(/\s/g, '').replace(",", ".")) || 0;
	$("td[data-name='max_rrf']", "#section_parameter").attr("data-trick-min-value", minRRF);
	return false;
}

/**
 * Loads the RRF (Risk Response Framework) data and initializes the UI.
 * 
 * @returns {boolean} Returns false to prevent the default form submission behavior.
 */
function loadRRF() {
	let $progress = $("#loading-indicator").show();
	$
		.ajax(
			{
				url: context + "/Analysis/RRF",
				type: "get",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					let $rrfUI = $("#rrfEditor", new DOMParser().parseFromString(response, "text/html"));

					if (!$rrfUI.length) {
						unknowError();
						return false;
					} else {
						if (application["maxRRF"] === undefined)
							updateMaxRRF();
						$("#rrfEditor").replaceWith($rrfUI);

						initialiseMeasureSliders();

						initialiseMeasuresClick();

						initialiseScenariosClick();

						initialiseStandardFilter();

						if (application.openMode !== OPEN_MODE.READ) {

							let $controlApplySubChapter = $("#measure-control-apply-sub-chapter", $rrfUI), $selectetiveControlApplySubChapter = $(
								"#measure-control-apply-selective-sub-chapter", $rrfUI), applyMeasureCharacteristics = function (data, idMeasure) {
									if (data.length) {
										$.ajax({
											url: context + "/Analysis/RRF/Measure/" + idMeasure + "/Update-child",
											type: "post",
											data: JSON.stringify(data),
											contentType: "application/json;charset=UTF-8",
											success: function (response, textStatus, jqXHR) {
												if (response.success == undefined)
													rrfError(response.error == undefined ? undefined : response.error);
											},
											error: function () {
												rrfError();
											}

										});
									}
								};
							if ($controlApplySubChapter.length) {
								$controlApplySubChapter.on("click", function () {
									let $selectedMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']"), $parent = $selectedMeasure
										.parent(), level = $selectedMeasure.attr("data-trick-value"), data = [];
									$("[data-trick-value^='" + level + ".']", $parent).each(function () {
										data.push($(this).attr("data-trick-id"));
									});
									applyMeasureCharacteristics(data, $selectedMeasure.attr("data-trick-id"));
								});
							}

							if ($selectetiveControlApplySubChapter.length) {
								$selectetiveControlApplySubChapter
									.on(
										"click",
										function () {
											let $selectedMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']"), $parent = $selectedMeasure
												.parent(), level = $selectedMeasure.attr("data-trick-value"), $form = $("<form class='form-horizontal'></form>");
											$("[data-trick-value^='" + level + ".']", $parent)
												.each(
													function () {
														let $this = $(this), $formGroup = $("<div class='form-group'><div class='col-md-1'><input style='margin-top:-5px' type='checkbox' class='form-control'></div><label class='col-md-11'></label></div>");
														$("label", $formGroup).text($this.text())
														$("input", $formGroup).attr("name", $this.attr("data-trick-id"));
														$formGroup.appendTo($form);
													});
											let $input = $("input", $form);
											if ($input.length) {
												var parentText = $selectedMeasure.text().replace("\t", "").replace("\n", " ").trim(), modal = new Modal(undefined,
													$form).setTitle(MessageResolver("label.title.rrf.apply.measure.characteristics",
														"RRF: Apply measure ({0})  characteristics".replace("{0}", parentText), parentText));

												$("button[data-control-type='ok']", modal.modal_footer).on("click", function () {
													let data = []
													$("input:checked", $form).each(function () {
														data.push($(this).attr("name"));
													});
													applyMeasureCharacteristics(data, $selectedMeasure.attr("data-trick-id"));
												}).text(MessageResolver("label.action.apply", "Apply"));
												modal.Show();
											}
										});
							}
						}
						$rrfUI.modal("show");
						setTimeout(function () {
							loadMeasureChart();
							$('#chart-container-pending', $rrfUI).remove();
						}, 500);

						$rrfUI.on("hidden.bs.modal", (e) => {
							if (window["rrf-chart"]) {
								window["rrf-chart"].destroy();

								delete window["rrf-chart"];
							}
						})
					}
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});

	return false;
}

/**
 * Initializes the measure sliders in the RRF Manager.
 */
function initialiseMeasureSliders() {

	var $modal = $("#rrfEditor"), $selectedMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal), level = $selectedMeasure
		.attr("data-trick-value"), $sliders = $("#control_rrf_measure input[type='range']", $modal);
	if ($("[data-trick-value^='" + level + ".']", $selectedMeasure.parent()).length)
		$("[id^='measure-control-apply']", $modal).show();
	else
		$("[id^='measure-control-apply']", $modal).hide();
	$sliders.each(function () {
		if (application.openMode === OPEN_MODE.READ) {
			$(this).prop("disabled", true);
			$(this).addClass("disabled");
		} else {
			$(this).on("change", function (event) {
				var field = event.target.name, fieldValue = parseFloat(event.target.value), previousValue = $("#control_rrf_measure #measure_" + field.replace("\.", "_") + "_value", $modal).attr("value");
				$("#control_rrf_measure input[id='measure_" + field.replace("\.", "_") + "_value']", $modal).attr("value", fieldValue);
				return updateMeasureProperty(field, fieldValue, parseFloat(previousValue), $(this));
			});
		}
	});
}

/**
 * Updates the measure property with the given value.
 * @param {string} property - The name of the property to update.
 * @param {any} value - The new value for the property.
 * @param {any} previousValue - The previous value of the property.
 * @param {HTMLElement} slider - The slider element associated with the property.
 * @returns {boolean} - Returns false if the value is the same as the previous value, or if idMeasure is null or undefined.
 */
function updateMeasureProperty(property, value, previousValue, slider) {
	if (value === previousValue)
		return false;
	var $modal = $("#rrfEditor"), idMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal).attr("data-trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	$.ajax({
		url: context + "/Analysis/EditField/Measure/" + idMeasure,
		type: "post",
		data: JSON.stringify({
			"id": idMeasure,
			"fieldName": property,
			"value": value,
			"type": "numeric"
		}),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response.success == undefined) {
				$("#control_rrf_measure #measure_" + property.replace("\.", "_") + "_value", $modal).attr("value", previousValue);
				$(slider).attr("value", previousValue);
				rrfError(response.error == undefined ? undefined : response.error);
			} else
				loadMeasureChart();
		},
		error: function () {
			rrfError();
		}
	});
}

/**
 * Initializes the scenario sliders for the RRF Manager.
 * @function initialiseScenarioSliders
 * @returns {void}
 */
function initialiseScenarioSliders() {
	var $modal = $("#rrfEditor"), $sliders = $("#control_rrf_scenario input[type='range']", $modal);
	$sliders.each(
		function () {
			if (application.openMode === OPEN_MODE.READ) {
				$(this).prop("disabled", true);
				$(this).addClass("disabled");
			} else {
				$(this).on(
					"change",
					function (event) {
						var field = event.target.name, fieldValue = parseFloat(event.target.value), previousValue = $("#control_rrf_scenario #scenario_" + field + "_value", $modal).attr(
							"value"), displayvalue = fieldValue;
						if (field == "preventive" || field == "detective" || field == "limitative" || field == "corrective")
							displayvalue = fieldValue.toFixed(2);
						$("#control_rrf_scenario #scenario_" + field + "_value", $modal).attr("value", displayvalue);
						return updateScenarioProperty(field, fieldValue, parseFloat(previousValue), $(this));
					});
			}
		});
}

/**
 * Updates the scenario property with the given value.
 * @param {string} property - The name of the property to update.
 * @param {number} value - The new value for the property.
 * @param {number} previousValue - The previous value of the property.
 * @param {HTMLElement} slider - The slider element associated with the property.
 * @returns {boolean} - Returns false if the value is the same as the previous value, otherwise returns true.
 */
function updateScenarioProperty(property, value, previousValue, slider) {
	if (value === previousValue)
		return false;
	var $modal = $("#rrfEditor"), idScenario = $("#selectable_rrf_scenario_controls .active[data-trick-class='Scenario']", $modal).attr("data-trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	if (property == "preventive" || property == "detective" || property == "limitative" || property == "corrective")
		value = value.toFixed(2);
	$.ajax({
		url: context + "/Analysis/EditField/Scenario/" + idScenario,
		type: "post",
		data: JSON.stringify({
			"id": idScenario,
			"fieldName": property,
			"value": value,
			"type": "numeric"
		}),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response.success == undefined) {
				$("#control_rrf_scenario #scenario_" + property + "_value", $modal).attr("value", previousValue);
				rrfError(response.error == undefined ? undefined : response.error);
				$(slider).attr("value", previousValue);
			} else {

				if (property == "preventive" || property == "detective" || property == "limitative" || property == "corrective") {
					var result = (+$("#control_rrf_scenario #scenario_preventive_value", $modal).val() + +$("#control_rrf_scenario #scenario_detective_value", $modal).val()
						+ +$("#control_rrf_scenario #scenario_limitative_value", $modal).val() + +$("#control_rrf_scenario #scenario_corrective_value", $modal).val())
						.toFixed(2);
					$("#control_rrf_scenario .pdlc", $modal).removeClass("success danger");
					if (result == 1)
						$("#control_rrf_scenario .pdlc", $modal).addClass("success");
					else
						$("#control_rrf_scenario .pdlc", $modal).addClass("danger");
				}

				loadScenarioChart();
			}

		},
		error: function () {
			rrfError();
		}
	});
}

/**
 * Initializes the click event for the scenarios in the RRF Manager.
 * 
 * @function initialiseScenariosClick
 * @memberof rrfManager
 * @returns {void}
 */
function initialiseScenariosClick() {
	var $modal = $("#rrfEditor");
	$("#selectable_rrf_scenario_controls a", $modal).click(function () {
		// remove previous selection
		$("#selectable_rrf_scenario_controls a.active", $modal).removeClass("active");
		// select current
		var $this = $(this).addClass("active"), classname = $this.attr("data-trick-class"), trickid = $this.attr("data-trick-id");
		if (classname === "ScenarioType") {
			var idMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal).attr("data-trick-id");
			if (idMeasure == null || idMeasure == undefined) {
				$("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']", $modal).parent().parent().find("div.list-group a:first").addClass("active");
				idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal).attr("data-trick-id");
				if (idMeasure == null || idMeasure == undefined)
					return false;
			}
			loadMeasure();
			loadMeasureChart();
		} else if (classname === "Scenario") {

			// select measure parent
			$(this).parent().parent().find("h4 a").addClass("active");
			loadScenario();
			loadScenarioChart();
		}
	});
}

/**
 * Initializes the click event for the measures in the RRF editor.
 * 
 * @function initialiseMeasuresClick
 * @memberof rrfManager
 * @returns {void}
 */
function initialiseMeasuresClick() {
	var $modal = $("#rrfEditor");
	$("#selectable_rrf_measures_chapter_controls a", $modal).click(function () {
		// remove previous selection
		$("#selectable_rrf_measures_chapter_controls a.active", $modal).removeClass("active");
		// get current element data
		var $this = $(this).addClass("active"), classname = $this.attr("data-trick-class"), trickid = $this.attr("data-trick-id");
		if (classname === "Standard") {
			var idScenario = $("#selectable_rrf_scenario_controls .active[data-trick-class='Scenario']", $modal).attr("data-trick-id");
			if (idScenario == null || idScenario == undefined) {
				$("#selectable_rrf_scenario_controls .active[data-trick-class='ScenarioType']", $modal).parent().parent().find("div.list-group a:first").addClass("active");
				idScenario = $("#selectable_rrf_scenario_controls .active[data-trick-class='Scenario']", $modal).attr("data-trick-id");
				if (idScenario == null || idScenario == undefined)
					return false;
			}
			loadScenario();
			loadScenarioChart();
		} else if (classname === "Measure") {
			// select measure parent
			$this.parent().parent().find("h4 a").addClass("active");
			loadMeasure();
			loadMeasureChart();
		}
	});
}

/**
 * Loads the measure data using AJAX and updates the UI accordingly.
 * @returns {boolean} Returns false if the measure or standard ID is not available, otherwise returns true.
 */
function loadMeasure() {
	var $modal = $("#rrfEditor"), idMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal).attr("data-trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	var idStandard = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']", $modal).attr("data-trick-id");
	if (idStandard == null || idStandard == undefined)
		return false;
	$.ajax({
		url: context + "/Analysis/RRF/Standard/" + idStandard + "/Measure/" + idMeasure,
		type: "GET",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $measurerrf = $("[data-trick-controller-name='measure']", new DOMParser().parseFromString(response, "text/html"));
			if (!$measurerrf.length)
				return true;
			$("[data-trick-controller-name='measure']", $modal).replaceWith($measurerrf);
			initialiseMeasureSliders();
			var $controlMeasure = $("#control_rrf_measure", $modal);
			if (!$controlMeasure.is(":visible")) {
				$("#control_rrf_scenario", $modal).hide();
				$controlMeasure.show();
			}
		},
		error: function () {
			rrfError();
			return false;
		}
	});
	return false;
}

/**
 * Loads the measure chart.
 * 
 * @returns {boolean} Returns false if either idMeasure or idScenarioType is null or undefined, otherwise returns true.
 */
function loadMeasureChart() {
	var $modal = $("#rrfEditor"), idMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal).attr("data-trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	var idScenarioType = $("#selectable_rrf_scenario_controls .active[data-trick-class='ScenarioType']", $modal).attr("data-trick-id");
	if (idScenarioType == null || idScenarioType == undefined)
		return false;
	var $progress = $("#loading-indicator").show(), idScenario = $("#selectable_rrf_scenario_controls .active[data-trick-class='Scenario']", $modal).attr("data-trick-id");
	$.ajax({
		url: context + "/Analysis/RRF/Measure/" + idMeasure + "/Chart",
		type: "POST",
		data: JSON.stringify({
			"idScenarioType": idScenarioType,
			"idScenario": idScenario
		}),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			updateRffChart(response, $("#chart_rrf_canvas", $modal));
		},
		error: function () {
			rrfError();
			return false;
		}
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Loads a scenario using an AJAX request and updates the UI accordingly.
 * @returns {boolean} Returns false if the idScenario is null or undefined, otherwise returns true.
 */
function loadScenario() {
	var $modal = $("#rrfEditor"), idScenario = $("#selectable_rrf_scenario_controls .active[data-trick-class='Scenario']", $modal).attr("data-trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	$.ajax({
		url: context + "/Analysis/RRF/Scenario/" + idScenario,
		type: "GET",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $scenariorrf = $("[data-trick-controller-name='scenario']", new DOMParser().parseFromString(response, "text/html"))
			if (!$scenariorrf.length)
				return true;
			$("[data-trick-controller-name='scenario']", $modal).replaceWith($scenariorrf);
			initialiseScenarioSliders();
			var $scenarioControl = $("#control_rrf_scenario", $modal);
			if (!$scenarioControl.is(":visible")) {
				$("#control_rrf_measure", $modal).hide();
				$scenarioControl.show();
			}
		},
		error: function () {
			rrfError();
			return false;
		}
	});
	return false;
}

/**
 * Loads the scenario chart.
 * 
 * @returns {boolean} Returns false if the required data is missing, otherwise returns null.
 */
function loadScenarioChart() {
	var $modal = $("#rrfEditor"), idStandard = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']", $modal).attr("data-trick-id");
	if (idStandard == null || idStandard == undefined)
		return false;
	var chapter = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']", $modal).attr("data-trick-value");
	if (chapter == null || chapter == undefined)
		return false;
	var idMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']", $modal).attr("data-trick-id"), idScenario = $(
		"#selectable_rrf_scenario_controls .active[data-trick-class='Scenario']", $modal).attr("data-trick-id");
	if (idScenario == null || idScenario == undefined)
		return null;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/RRF/Scenario/" + idScenario + "/Chart",
		type: "POST",
		data: JSON.stringify({
			"idStandard": idStandard,
			"chapter": chapter,
			"idMeasure": idMeasure
		}),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			updateRffChart(response, $("#chart_rrf_canvas", $modal));
		},
		error: function () {
			rrfError();
			return false;
		}
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Updates the RFF chart with the given response data.
 * @param {Object} response - The response data containing the datasets and title.
 * @param {jQuery} $canvas - The jQuery object representing the canvas element.
 * @returns {boolean} - Returns false.
 */
function updateRffChart(response, $canvas) {
	if (response.datasets != null && response.datasets != undefined) {
		var color = Chart.helpers.color;
		response.datasets.filter(dataset => dataset.type == 'line').map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
		if (window["rrf-chart"]) {
			window["rrf-chart"].config.data = response;
			window["rrf-chart"].config.options.title.text = response.title;
			window["rrf-chart"].config.options.display = response.title != undefined;
			window["rrf-chart"].update();
		}
		else {
			window["rrf-chart"] = new Chart($canvas[0].getContext("2d"), {
				type: "bar",
				data: response,
				options: rffOptions(response.title)
			});
		}

	} else
		rrfError(response.error == undefined ? undefined : response.error);
	return false;
}

/**
 * Initializes the standard filter for the RRF manager.
 */
function initialiseStandardFilter() {
	var $modal = $("#rrfEditor");
	$("#section_rrf [name='chapterselection']", $modal).change(function () {
		var filter = $("option:selected", this).attr("value");
		$("#selectable_rrf_measures_chapter_controls [class='list-group'][data-trick-filter-value]", $modal).css("display", "none");
		$("#selectable_rrf_measures_chapter_controls [class='list-group'][data-trick-filter-value='" + filter + "']", $modal).css("display", "block");
	});
}
