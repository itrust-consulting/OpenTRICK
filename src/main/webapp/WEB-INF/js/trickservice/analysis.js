var el = null, table = null, taskController = function () {
};

var language = { // translated asynchronously below
	"label.dynamicparameter.evolution": "from {0} to {1}"
};

$(document).ready(function () {
	for (var key in language)
		language[key] = MessageResolver(key, language[key]);

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************
	application["settings-fixed-header"] = {
		fixedOffset: $(".nav-analysis"),
		marginTop: application.fixedOffset,
		scrollStartFixMulti: 0.99998
	};

	setTimeout(() => {
		$('ul.nav-analysis a[data-toggle="tab"]').on('shown.bs.tab', (e) => {
			disableEditMode();
		});

		$(window).bind("beforeunload", (e) => {
			if (!application.editingModeFroceAbort && application.editMode) {
				showDialog("info", MessageResolver("info.leave.page.in_mode_editing"));
				application.editingModeFroceAbort = true;
				return false;
			}
		});

		fixTableHeader("table.table-fixed-header-analysis");

	}, 100);

	// Periodically reload assessment values
	/*
	 * window.setInterval(function () { reloadAssetScenario();
	 * loadChartDynamicParameterEvolution();
	 * loadChartDynamicAleEvolutionByAssetType();
	 * loadChartDynamicAleEvolution(); }, 300000);
	 */ // every 30s
});

$.fn.loadOrUpdateChart = function (parameters) {
	if (!parameters.tooltip) {
		$.extend(true, parameters, {
			tooltip: {
				formatter: function () {
					var str_value = this.series.yAxis.userOptions.labels.format.replace("{value}", this.point.y);
					var str = "<span style=\"font-size:80%;\">" + this.x + "</span><br/><span style=\"color:" + this.point.series.color + "\">" + this.point.series.name
						+ ":</span>   <b>" + str_value + "</b>";

					if (this.series.options.metadata) {
						var dataIndex = this.series.xAxis.categories.indexOf(this.x);
						var metadata = this.series.options.metadata[dataIndex];
						if (metadata.length > 0)
							str += "<br/>\u00A0"; // non-breaking space;
						// prevents empty line from
						// being ignored
						for (var i = 0; i < metadata.length; i++)
							str += "<br/><b>" + metadata[i].dynamicParameter + "</b>: "
								+ language["label.dynamicparameter.evolution"].replace("{0}", metadata[i].valueOld).replace("{1}", metadata[i].valueNew);
					}
					return str;
				}
			}
		});
	}

	var chart = this.highcharts();
	if (chart === undefined || parameters.series.length != chart.series.length)
		return this.highcharts(parameters);

	// Invalidate whole graph if the collection of series changed
	for (var i = 0; i < chart.series.length; i++) {
		if (chart.series[i].name != parameters.series[i].name)
			return this.highcharts(parameters);
	}

	// Otherwise update only data
	$.each(chart.series, function (i, series) {
		series.options.metadata = parameters.series[i].metadata;
		series.setData(parameters.series[i].data);
	});

	return this;
};

function findAnalysisId() {
	var id = application['selected-analysis-id'];
	if (id === undefined) {
		var el = document.querySelector("#nav-container");
		if (el == null)
			return -1;
		id = application['selected-analysis-id'] = el.getAttribute("data-trick-id");
	}
	return id;
}

function updateScroll(element) {
	var currentActive = document.activeElement;
	if (element != currentActive) {
		element.focus();// update scroll
		currentActive.focus();
	}
	return false;
}

function findAnalysisLocale() {
	var locale = application['selected-analysis-locale'];
	if (locale === undefined) {
		var el = document.querySelector("#nav-container");
		if (el == null)
			return 'en';
		locale = application['selected-analysis-locale'] = el.getAttribute("data-trick-language");
	}
	return locale;
}

function reloadAssetScenario() {
	if ($("#section_asset:visible").length)
		reloadSection("section_asset")
	else if ($("#section_scenario:visible").length)
		reloadSection("section_scenario");
	else
		reloadSection(["section_asset", "section_scenario"], undefined, true);
}

function reloadAssetScenarioChart() {
	return application.analysisType == "QUALITATIVE" ? reloadRiskChart() : chartALE();
}

function isEditable() {
	return userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY);
}

function updateSettings(element, entryKey) {
	$.ajax({
		url: context + "/Settings/Update",
		type: 'post',
		data: {
			'key': entryKey,
			'value': !$(element).hasClass('glyphicon-ok')
		},
		async: false,
		success: function (response, textStatus, jqXHR) {
			if (response == undefined || response !== true)
				unknowError();
			else {
				if ($(element).hasClass('glyphicon-ok'))
					$(element).removeClass('glyphicon-ok');
				else
					$(element).addClass('glyphicon-ok');
				var sections = $(element).attr("data-trick-section-dependency");
				if (sections != undefined)
					return reloadSection(sections.split(','));
				var callBack = $(element).attr("data-trick-callback");
				if (callBack != undefined)
					return eval(callBack);
				var reload = $(element).attr("data-trick-reload");
				if (reload == undefined || reload == 'true')
					location.reload();
			}
			return true;
		},
		error: unknowError
	});
	return false;
}

function updateMeasuresCost() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url: context + "/Analysis/Standard/Update/Cost",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.success == undefined)
					unknowError()
				else {
					$("div[id^='section_standard_']").each(function () {
						reloadSection(this.id);
					});
				}
			},
			error: unknowError
		});
	} else
		permissionError();
	return false;
}

function manageAnalysisSettings() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Manage-settings",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $view = $("#analysisSettingModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$view.appendTo("#widgets").modal("show").on('hidden.bs.modal', () => $view.remove());
					$("button[name='save']").on("click", e => {
						var data = {};

						$(".form-group[data-trick-name]", $view).each(function () {
							var $this = $(this), newValue = $("input[type='radio']:checked,input[type!='radio']:visible", this).val(), oldValue = $("input[type!='radio']:hidden", this).val();
							if (newValue != oldValue)
								data[$this.attr("data-trick-name")] = newValue;
						});

						if (Object.keys(data).length) {
							$.ajax({
								url: context + "/Analysis/Manage-settings/Save",
								type: "post",
								data: JSON.stringify(data),
								contentType: "application/json;charset=UTF-8",
								success: function (response, textStatus, jqXHR) {
									if (response.error != undefined)
										showDialog("#alert-dialog", response.error);
									else if (response.success != undefined) {
										showDialog("success", response.success);
										setTimeout(() => window.location.reload(), 1000);
									} else
										unknowError();
								},
								error: unknowError
							}).complete(function () {
								$progress.hide();
							});
						} else
							$progress.hide();
					});
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

// reload measures
function reloadMeasureRow(idMeasure, standard) {
	var $currentRow = $("#section_standard_" + standard + " tr[data-trick-id='" + idMeasure + "']");
	if (!$currentRow.find("input[type!='checkbox'],select,textarea").length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/" + standard + "/SingleMeasure/" + idMeasure,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $newRow = $("tr", $("<div/>").html(response));
				if (!$newRow.length)
					$currentRow.addClass("warning").attr("title", MessageResolver("error.ui.no.synchronise", "User interface does not update"));
				else {
					var $checked = $currentRow.find("input:checked");
					$("[data-toggle='tooltip']", $currentRow).tooltip('destroy');
					$currentRow.replaceWith($newRow);
					$("[data-toggle='tooltip']", $newRow).tooltip().on('show.bs.tooltip', toggleToolTip);
					if ($checked.length)
						$newRow.find("input[type='checkbox']").prop("checked", true).change();

					if (application["measure-view-init"]) {// See
						// analysis-measure
						if ($("#measure-ui[data-trick-id='" + idMeasure + "']:hidden").length)
							$("#tabMeasureEdition").attr("data-update-required", application["measure-view-invalidate"] = true);
					}

					if (application["estimation-helper"]) {// See
						// risk-estimation
						application["estimation-helper"].$tabSection.attr("data-update-required", application["estimation-helper"].invalidate = true);
						if (application["standard-caching"])// See
							// risk-estimation
							// -> manage measure
							application["standard-caching"].clear(standard);
					}
				}
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$currentRow.attr("data-force-callback", true).addClass("warning").attr("title",
			MessageResolver("error.ui.update.wait.editing", "Data was saved but user interface was not updated, it will be updated after edition"));
	return false;
}

function reloadMeasureAndCompliance(standard, idMeasure) {
	reloadMeasureRow(idMeasure, standard);
	compliance(standard);
	if (document.getElementById("table_SOA_" + standard))
		reloadSection("section_soa");
	return false;
}

function reloadRiskAcceptanceTable($tabSection) {
	var $tbody = $("table>tbody", $tabSection), $trs = $("table#table_parameter_risk_acceptance tbody>tr[data-trick-id]").clone();
	if ($trs.length) {
		$tbody.empty();
		$trs.each(function () {
			var $this = $(this).removeAttributes();
			$("td[data-trick-field!='color']", $this).removeAttributes();
			$("td[data-trick-field]", $this).removeAttr("data-trick-field");
			$this.attr("style", "text-align:center");
			$this.appendTo($tbody);
		});
	}
	$tabSection.attr("data-parameters", false);
}

function reloadRiskHeatMapSection(tableChange) {
	var $tabSection = $("#tab-chart-heat-map");
	if ($tabSection.is(":visible")) {
		loadRiskHeatMap();
		if (tableChange)
			reloadRiskAcceptanceTable($tabSection);
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function reloadRiskAssetSection(tableChange) {
	var $tabSection = $("#tab-chart-risk-asset");
	if ($tabSection.is(":visible")) {
		loadRiskChart(context + "/Analysis/Asset/Chart/Risk", "riskAsset", "#risk_acceptance_assets", "risk_acceptance_asset_canvas");
		if (tableChange)
			reloadRiskAcceptanceTable($tabSection);
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function reloadRiskAssetTypeSection(tableChange) {
	var $tabSection = $("#tab-chart-risk-asset-type");
	if ($tabSection.is(":visible")) {
		loadRiskChart(context + "/Analysis/Asset/Chart/Type/Risk", "riskAssetType", "#risk_acceptance_asset_types", "risk_acceptance_asset_types_canvas");
		if (tableChange)
			reloadRiskAcceptanceTable($tabSection);
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function reloadRiskScenarioSection(tableChange) {
	var $tabSection = $("#tab-chart-risk-scenario");
	if ($tabSection.is(":visible")) {
		loadRiskChart(context + "/Analysis/Scenario/Chart/Risk", "riskScenario", "#risk_acceptance_scenarios", "risk_acceptance_scenarios_canvas");
		if (tableChange)
			reloadRiskAcceptanceTable($tabSection);
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function reloadRiskScenarioTypeSection(tableChange) {
	var $tabSection = $("#tab-chart-risk-scenario-type");
	if ($tabSection.is(":visible")) {
		loadRiskChart(context + "/Analysis/Scenario/Chart/Type/Risk", "riskScenarioType", "#risk_acceptance_scenario_types", "risk_acceptance_scenario_types_canvas");
		if (tableChange)
			reloadRiskAcceptanceTable($tabSection);
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}


function reloadRiskChart(tableChange) {
	reloadRiskHeatMapSection(tableChange);
	reloadRiskAssetSection(tableChange);
	reloadRiskAssetTypeSection(tableChange);
	reloadRiskScenarioSection(tableChange);
	reloadRiskScenarioTypeSection(tableChange);
	return false;
}

function loadRiskHeatMap() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Assessment/Chart/Risk-heat-map",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (window.riskHeatMap != undefined)
				window.riskHeatMap.destroy();
			window.riskHeatMap = new Chart(document.getElementById("risk_acceptance_heat_map_canvas").getContext("2d"), {
				type: 'heatmap',
				data: response,
				options: heatMapOption()
			});
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;

}

function loadRiskChart(url, name, container, canvas) {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: url,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (window[name] != undefined)
				helpers.isArray(window[name]) ? window[name].map(chart => chart.destroy()) : window[name].destroy();
			if (helpers.isArray(response)) {
				window[name] = [];
				var $container = $(container).empty();
				response.map(chart => {
					var $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($container);
					window[name].push(new Chart($canvas[0].getContext("2d"), {
						type: "bar",
						data: chart,
						options: riskOptions()
					}));

				});
			}
			else {
				$(container).html("<canvas id='" + canvas + "' style='max-width: 1000px; margin-left: auto; margin-right: auto;' />")
				window[name] = new Chart(document.getElementById(canvas).getContext("2d"), {
					type: "bar",
					data: response,
					options: {
						scales: {
							xAxes: [{
								stacked: true
							}],
							yAxes: [{
								stacked: true
							}]
						}
					}
				});
			}
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;

}


function updateMeasureEffience(reference) {
	if (!application.hasMaturity)
		return;
	var $standard27002 = $("div[id^='section_standard_'][data-trick-label='27002']");
	if (!$standard27002.length)
		return;
	var $tabPane = $standard27002.closest(".tab-pane"), updateRequired = $tabPane.attr("data-update-required"), triggerName = $tabPane.attr('data-trigger');
	if (updateRequired && triggerName == "reloadSection")
		return;
	var data = [], chapters = application["parameter-27002-efficience"];
	if ($standard27002.is(":visible")) {
		if (Array.isArray(chapters)) {
			data = chapters;
			delete application["parameter-27002-efficience"];
		} else
			$standard27002.find("tr[data-trick-computable='false'][data-trick-level='1']").each(function () {
				data.push(this.getAttribute('data-trick-reference'))
			});
	} else {
		$tabPane.attr("data-update-required", true).attr("data-trigger", 'updateMeasureEffience');
		if (reference == undefined)
			delete application["parameter-27002-efficience"];
		else {
			var chapter = reference.split(".", 3)[1], parameters = application["parameter-27002-efficience"], $selector = $standard27002
				.find("tr[data-trick-computable='false'][data-trick-level='1'][data-trick-reference='" + chapter + "']");
			if ($selector.length) {
				if (updateRequired && triggerName == 'updateMeasureEffience') {
					if (parameters && parameters.indexOf(chapter) == -1)
						parameters.push(chapter);
				} else
					application["parameter-27002-efficience"] = [chapter];
			} else
				$tabPane.attr("data-update-required", updateRequired).attr("data-trigger", triggerName);
		}
	}

	if (!data.length)
		return;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Standard/Compute-efficience",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (typeof response === 'object') {
				for (var id in response)
					$("tr[data-trick-id='" + id + "'][data-trick-computable='true'] td[data-trick-field='mer']", $standard27002).text(parseInt(response[id], 10));
			} else
				showDialog("#alert-dialog", MessageResolver("error.measure.mer.update", "Maturity-based effectiveness rate cannot be updated"));
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function compliances() {
	var $section = $("#tab-chart-compliance");
	if ($section.is(":visible"))
		loadComplianceChart(context + "/Analysis/Standard/Compliances");
	else $section.attr("data-update-required", "true");
	return false;
}

function compliance(standard) {
	var $section = $("#tab-chart-compliance");
	if ($section.is(":visible"))
		loadComplianceChart(context + "/Analysis/Standard/" + standard + "/Compliance");
	else
		$section.attr("data-update-required", "true");
	return false;
}

function loadComplianceChart(url) {
	var $progress = $("#loading-indicator").show(), name = "compliancesChart", $container = $("#chart_compliance_body"), canvas = "chart_canvas_compliance_";
	try {
		$.ajax({
			url: url,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						if (window[name].has(chart.trickId)) {
							window[name].get(chart.trickId).config.data = chart;
							window[name].get(chart.trickId).update();
						} else {
							var $parent = $("<div class='col-sm-6 col-md-4 col-lg-3' id= 'chart_compliance_" + chart.trickId + "' />").appendTo($container), $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($parent);
							window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
								type: "radar",
								data: chart,
								options: complianceOptions(chart.title)
							}));
						}
					} else if (window[name].has(chart.trickId)) {
						window[name].get(chart.trickId).destroy();
						window[name].delete(chart.trickId);
						$("#chart_compliance_" + chart.trickId).remove();
					}
				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} catch (e) {
		$progress.hide();
	}
	return false;
}


function evolutionProfitabilityComplianceByActionPlanType(actionPlanType) {
	var $section = $("#tab-chart-evolution");
	if ($section.is(":visible")) {
		var $progress = $("#loading-indicator").show(), name = "evolutionProfitabilityComplianceChart";
		$.ajax({
			url: context + "/Analysis/ActionPlanSummary/Evolution/" + actionPlanType,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				charts.map(chart => {

					if (chart.datasets && chart.datasets.length) {
						chart.datasets.filter(dataset => dataset.type == "line").map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						if (window[name].has(chart.trickId)) {
							window[name].get(chart.trickId).config.data = chart;
							window[name].get(chart.trickId).update();
						} else {
							var $parent = $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo("#" + chart.trickId);
							window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
								type: "bar",
								data: chart,
								options: evolutionProfitabilityComplianceOption(chart.trickId, chart.title)
							}));
						}
					} else if (window[name].has(chart.trickId)) {
						window[name].get(chart.trickId).destroy();
						window[name].delete(chart.trickId);
						$("#" + chart.trickId).empty();
					}
				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$section.attr("data-update-required", "true");
	return false;
}

function summaryCharts() {
	loadChartBudget();
	loadChartEvolution();
	return false;
}

function loadChartEvolution() {
	application['actionPlanType'].map(actionType => evolutionProfitabilityComplianceByActionPlanType(actionType));
	return false;
}

function loadChartBudget() {
	var $section = $("#tab-chart-budget");
	if ($section.is(":visible")) {
		var $progress = $("#loading-indicator").show(), name = "budgetCharts";
		$.ajax({
			url: context + "/Analysis/ActionPlanSummary/Budget",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						if (window[name].has(chart.trickId)) {
							window[name].get(chart.trickId).config.data = chart;
							window[name].get(chart.trickId).update();
						} else {
							var $parent = $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo("#" + chart.trickId);
							window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
								type: "bar",
								data: chart,
								options: budgetChartOption(chart.trickId, chart.title)
							}));
						}
					} else if (window[name].has(chart.trickId)) {
						window[name].get(chart.trickId).destroy();
						window[name].delete(chart.trickId);
						$("#" + chart.trickId).empty();
					}
				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$section.attr("data-update-required", "true");
	return false;
}

function reloadCharts() {
	compliances();
	summaryCharts();
	loadChartDynamicParameterEvolution();
	loadChartDynamicAleEvolutionByAssetType();
	loadChartDynamicAleEvolution();
	reloadAssetScenarioChart();
	return false;
};

function displayChart(id, response) {
	var $element = $(id);
	if ($.isArray(response)) {
		// First prepare the document structure so that there is exactly one
		// <div> available for each chart
		if ($element.find(">div").length != response.length) {
			$element.empty();
			for (var i = 0; i < response.length; i++) {
				if (i > 0)
					$("<hr  style='margin: 30px 0;'>").appendTo($element);
				$("<div/>").appendTo($element)
			}
		}
		// Now load the charts themselves
		var divSelector = $element.find(">div");
		for (var i = 0; i < response.length; i++)
			$(divSelector.get(i)).loadOrUpdateChart(response[i]);
	} else
		$element.loadOrUpdateChart(response);
}

function manageRiskAcceptance() {
	var $progress = $("#loading-indicator").show();
	$
		.ajax(
		{
			url: context + "/Analysis/Parameter/Risk-acceptance/form",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $content = $("#modalRiskAcceptanceForm", new DOMParser().parseFromString(response, "text/html"));
				if (!$content.length)
					showError("Error data cannot be loaded");
				else {
					var actionDelete = function () {
						$(this).closest("tr").remove();
					};
					$("button[name='delete']", $content).on("click", actionDelete);
					$("button[name='add']", $content)
						.on(
						"click",
						function () {
							var $this = $(this), $trParent = $this.closest("tr"), maxValue = $trParent.attr("data-trick-max-value"), $tr = $("<tr data-trick-id='-1' />"), $div = $("<div class='range-group' />"), $rangeInfo = $(
								"<span class='range-text'>0</span>").appendTo($div), $range = $(
									"<input type='range' min='1' max='" + maxValue + "'  name='value' value='0' class='range-input'>").appendTo($div), $removeBtn = $("<button class='btn btn-danger outline' type='button' name='delete'><i class='fa fa-remove'></i></button>"), $inputColor = $("<input name='color' type='color' value='#fada91' class='form-control'>");
							$removeBtn.appendTo($("<td/>").appendTo($tr));
							$("<td><input name='label' class='form-control'></td>").appendTo($tr);
							$div.appendTo($("<td />").appendTo($tr));
							$("<td><textarea name='description' class='form-control' rows='1' /></td>").appendTo($tr);
							$inputColor.appendTo($("<td />").appendTo($tr));
							$trParent.before($tr);
							$removeBtn.on("click", actionDelete);
							$range.on("input change", function () {
								$rangeInfo.text(this.value);
								this.setAttribute("title", this.value);
							});
						});

					$("input[type='range']", $content).on("input change", function () {
						$(".range-text", this.parentElement).text(this.value);
						this.setAttribute("title", this.value);
					});

					$("button[name='save']", $content).on("click", function () {
						$progress.show();
						var $table = $("table[data-trick-size]", $content), size = $table.attr("data-trick-size"), data = [];
						$("tr[data-trick-id]", $table).each(function () {
							var $this = $(this);
							data.push({
								id: $this.attr("data-trick-id"),
								value: $("input[name='value']", $this).val(),
								label: $("input[name='label']", $this).val(),
								description: $("textarea[name='description']", $this).val(),
								color: $("input[name='color']", $this).val()
							});
						});

						$.ajax({
							url: context + "/Analysis/Parameter/Risk-acceptance/Save",
							type: "post",
							data: JSON.stringify(data),
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								if (response.error)
									showNotifcation('danger', response.error);
								else if (response.success) {
									$content.modal("hide");
									showNotifcation('success', response.success);
									reloadSection("section_qualitative_parameter");
								} else
									unknowError();
							},
							error: unknowError
						}).complete(function () {
							$progress.hide();
						});
					});

					$content.appendTo("#widgets").modal("show").on("hidden.bs.modal", function () {
						$content.remove();
					});
				}
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	return false;
}

function loadChartAsset() {
	var $section = $("#tab-chart-asset");
	if ($section.is(":visible")) {
		loadALEChart(context + "/Analysis/Asset/Chart/Ale", "aleAsset", "#chart_ale_asset", "risk_ale_asset_canvas");
		loadALEChart(context + "/Analysis/Asset/Chart/Type/Ale", "aleAssetType", "#chart_ale_asset_type", "risk_ale_asset_type_canvas");
	}
	else
		$section.attr("data-update-required", "true");
	return false;
}

function loadChartScenario() {
	var $section = $("#tab-chart-scenario");
	if ($section.is(":visible")) {
		loadALEChart(context + "/Analysis/Scenario/Chart/Type/Ale", "aleScenarioType", "#chart_ale_scenario_type", "risk_ale_scenario_type_canvas");
		loadALEChart(context + "/Analysis/Scenario/Chart/Ale", "aleScenario", "#chart_ale_scenario", "risk_ale_scenario_canvas");
	}
	else
		$section.attr("data-update-required", "true");
	return false;
}

function loadChartDynamicParameterEvolution() {
	var $section = $("#tab-chart-parameter-evolution"), name = 'chart-parameter-evolution-map';
	if ($section.is(":visible")) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Dynamic/Chart/ParameterEvolution",
			type: "get",
			async: true,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				else {
					window[name].forEach((chart, key) => {
						chart.destroy();
						$("[id='chart-parameter-evolution-" + key + "']", $section).remove();
					});
				}

				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						var $parent = $("<div class='col-lg-6' id='chart-parameter-evolution-" + chart.trickId + "' />").appendTo($section), $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($parent);
						window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
							type: "line",
							data: chart,
							options: aleEvolutionOptions(chart.title)
						}));

					}
				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$section.attr("data-update-required", "true");
}

function loadChartDynamicAleEvolutionByAssetType() {

	var $section = $("#tab-chart-ale-evolution-by-asset-type"), name = 'chart-ale-evolution-by-asset-type-map';

	if ($section.is(":visible")) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Dynamic/Chart/AleEvolutionByAssetType",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			async: true,
			success: function (response, textStatus, jqXHR) {
				var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				else {
					window[name].forEach((chart, key) => {
						chart.destroy();
						$("[id='chart-ale-evolution-by-asset-type-" + key + "']", $section).remove();
					});
				}

				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						var $parent = $("<div class='col-lg-6' id='chart-ale-evolution-by-asset-type-" + chart.trickId + "' />").appendTo($section), $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($parent);
						window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
							type: "line",
							data: chart,
							options: aleEvolutionOptions(chart.title)
						}));

					}
				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$section.attr("data-update-required", "true");

}

function loadChartDynamicAleEvolution() {
	var $section = $("#tab-chart-ale-evolution"), name = 'chart-ale-evolution';
	if ($section.is(":visible")) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Dynamic/Chart/AleEvolution",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			async: true,
			success: function (response, textStatus, jqXHR) {
				var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				else {
					window[name].forEach((chart, key) => {
						chart.destroy();
						$("[id='chart-ale-evolution-" + key + "']", $section).remove();
					});
				}

				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						var $parent = $("<div id='chart-ale-evolution-" + chart.trickId + "' />").appendTo($section), $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($parent);
						window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
							type: "line",
							data: chart,
							options: aleEvolutionOptions(chart.title)
						}));

					}
				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else $section.attr("data-update-required", "true");
}

function loadALEChart(url, name, container, canvas) {
	var $progress = $("#loading-indicator").show();
	try {
		$.ajax({
			url: url,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (window[name] != undefined)
					helpers.isArray(window[name].destroy) ? window[name].map(chart => chart.destroy()) : window[name].destroy();
				if (helpers.isArray(response)) {
					window[name] = [];
					var $container = $(container).empty();
					response.map(chart => {
						var $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($container);
						window[name].push(new Chart($canvas[0].getContext("2d"), {
							type: "bar",
							data: chart,
							options: aleChartOption(chart.title)
						}));
					});
				}
				else {
					$(container).html("<canvas id='" + canvas + "' style='max-width: 1000px; margin-left: auto; margin-right: auto;' />")
					window[name] = new Chart(document.getElementById(canvas).getContext("2d"), {
						type: "bar",
						data: response,
						options: aleChartOption(response.title)
					});
				}
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} catch (e) {
		$progress.hide();
	}
	return false;
}

function reloadActionPlansAndCharts() {
	reloadSection('section_actionplans');
}

function chartALE() {
	loadChartAsset();
	loadChartScenario();
	return false;
}

// common
function navToogled(section, parentMenu, navSelected) {
	var currentMenu = $("li[data-trick-nav-control='" + navSelected + "']", parentMenu);
	if (!currentMenu.length || $(currentMenu).hasClass("disabled"))
		return false;
	$("li[data-trick-nav-control]", parentMenu).each(function () {
		if (this.getAttribute("data-trick-nav-control") == navSelected)
			this.classList.add("disabled");
		else if (this.classList.contains('disabled'))
			this.classList.remove("disabled");
	});

	$("[data-trick-nav-content]", section).each(function () {
		var $this = $(this);
		if (this.getAttribute("data-trick-nav-content") == navSelected)
			$this.show();
		else if ($this.is(":visible"))
			$this.hide();
	});
	$(window).scroll();
	return false;
}

function manageBrainstorming() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Risk-information/Manage",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $modal = $("#modal-manage-brainstorming", new DOMParser().parseFromString(response, "text/html"));
			if ($modal.length) {
				$modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", e => $modal.remove());
				updateRiskInformationAddButton($("#tab-manage-risk-information-risk button[name='add']", $modal)).on("click", addNewRiskInformtion);
				updateRiskInformationAddButton($("#tab-manage-risk-information-vul button[name='add']", $modal)).on("click", addNewRiskInformtion);
				updateRiskInformationAddButton($("#tab-manage-risk-information-threat button[name='add']", $modal)).on("click", addNewRiskInformtionChapter);
				$("button[name='add-chapter']", $modal).on("click",addNewRiskInformtionChapter);
				$("a[data-action='delete-chapter']", $modal).on("click",removeRiskInformtionChapter);
				$("a[data-action='delete-all']", $modal).on("click",removeRiskInformtionChapter);
				$("button[name='delete']", $modal).on("click", removeRiskInformtion);
			} else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

function removeRiskInformtionChapter(e){
	var $this = $(this), action = $this.attr("data-action"), $tr = $this.closest("tr"), $table = $this.closest('tbody'), chapter = $tr.attr("data-chapter");
	switch (action) {
	case "delete-chapter":
		var deleteHeader = $("tr[data-chapter].lead", $table).filter(function(){return this.getAttribute("data-chapter") > chapter}).length == 0;
		if(deleteHeader)
			$("tr[data-chapter='"+chapter+"']", $table).remove();
		else $("tr[data-chapter='"+chapter+"']:not(.lead)", $table).remove();
		break;
	case "delete-all":
		$("tr[data-chapter]", $table).filter(function() {return this.getAttribute("data-chapter") >= chapter}).remove();
		break;
	}
}

function nextRiskInformation(chapter) {
	if (parseInt(chapter[2]) < 9)
		chapter[2]++;
	else if (parseInt(chapter[1]) < 9) {
		chapter[2] = '0';
		chapter[1]++;
	} else return undefined
	return chapter.join(".");
}

function addNewRiskInformtion(e) {
	var $this = $(this), $currentTr = $this.closest("tr"),  $tr = $("<tr data-trick-id='-1' />") , chapter = $currentTr.find("td:first-child").text().split("."), value = nextRiskInformation(chapter);
	addNewRiskInformation($currentTr,$tr, $("#risk-information-btn",$this.closest(".modal")),chapter,value,true);
	$this.attr("disabled", true);
}

function addNewRiskInformtionChapter(e){
	var $this = $(this), $currentTr = $this.closest("tr"),  $tr = $("<tr data-trick-id='-1' class='lead'/>"), $prevTr = $currentTr.prev();
	var chapter = $prevTr.length? $prevTr.find("td:first-child").text().split(".") : chapter = ['0','0','0'];
	if(chapter[0]<9){
		chapter[0]++;
		chapter[1] = chapter[2] = '0';
		addNewRiskInformation($currentTr,$tr, $("#risk-information-btn-chapter",$this.closest(".modal")),chapter,chapter.join("."),false);
		$("a[data-action='delete-chapter']", $tr).on("click",removeRiskInformtionChapter);
		$("a[data-action='delete-all']", $tr).on("click",removeRiskInformtionChapter);
	}else showDialog("alert-dialog",$this.attr("data-error-full-message"));
}

function addNewRiskInformation($currentTr,$tr,$buttons,chapter, value, after){
	$("<td>" + value + "<input type='hidden' name='id' value='-1' /><input type='hidden' name='chapter' value='" + value + "'><input type='hidden' name='custom' value='true' /></td>").appendTo($tr);
	$("<td><input class='form-control' type='text' name='label'></td>").appendTo($tr);
	$("<td />").html($buttons.html()).appendTo($tr);
	$("button[name='delete']", $tr).on("click", removeRiskInformtion);
	if(after)
		$tr.insertAfter($currentTr);
	else $tr.insertBefore($currentTr);
	var nextValue = nextRiskInformation(chapter), $addBtn = $("button[name='add']", $tr).on("click", addNewRiskInformtion);
	if (nextValue != undefined) {
		var $nextTr = $tr.next();
		if ($nextTr.find("td:first-child").text() == nextValue)
			$addBtn.attr("disabled", true);
	}
	$tr.attr("data-chapter", chapter[0]);
}

function removeRiskInformtion(e) {
	var $currentTr = $(this).closest("tr"),$prevTr = $currentTr.prev();
	if($prevTr.length){
		var  currentGroup = $("td:first-child",$currentTr).text().split(".",2)[0], prevGroup = $("td", $prevTr).text().split(".",2)[0];
		if(currentGroup == prevGroup)
			$("button[name='add']",$prevTr).removeAttr("disabled");
	}
	$currentTr.remove();
	return false;
}

function updateRiskInformationAddButton($btns) {
	var chatpers = {};
	$btns.each(function (i) {
		var $this = $(this),$tr = $this.closest("tr"), chapter = $tr.find("td:first-child").text(), group = chapter.split(".", 2)[0], value = parseInt(chapter.replace(/\./g, ''));
		if (!chatpers[group])
			chatpers[group] = { min: value, max: parseInt(group + "99"), chapter: group }
		else {
			if (chatpers[group].min + 1 == value)
				$($btns[i - 1]).attr("disabled", true);
			else $($btns[i - 1]).removeAttr("disabled");
			if (chatpers[group].max == value)
				$this.attr("disabled", true);
			chatpers[group].min = value;
		}
		$tr.attr("data-chapter", group);
	});
	return $btns;
}

function openTicket(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/Ticketing/Open",
			type: "POST",
			async: false,
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(measures),
			success: function (response, textStatus, jqXHR) {
				var $modal = $("#modal-ticketing-view", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length) {
					$("#modal-ticketing-view").remove();
					$modal.appendTo($("#widgets")).modal("show");
					var $previous = $modal.find(".previous"), $next = $modal.find(".next"), $title = $modal.find(".modal-title");
					$next.find("a").on("click", function () {
						if (!$next.hasClass("disabled")) {
							var $current = $modal.find("fieldset:visible"), $nextElement = $current.next();
							if ($nextElement.length) {
								$current.hide();
								$title.text($nextElement.show().attr("data-title"));
								if (!$nextElement.next().length)
									$next.addClass("disabled")
								if ($previous.hasClass("disabled"))
									$previous.removeClass("disabled");
							}
						}
						return false;
					});

					$previous.find("a").on("click", function () {
						if (!$previous.hasClass("disabled")) {
							var $current = $modal.find("fieldset:visible"), $prev = $current.prev();
							if ($prev.length) {
								$current.hide();
								$title.text($prev.show().attr("data-title"));
								if (!$prev.prev().length)
									$previous.addClass("disabled")
								if ($next.hasClass("disabled"))
									$next.removeClass("disabled");
							}
						}
						return false;
					});
				} else if (response["error"])
					showDialog("#alert-dialog", response['error']);
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.open.no_action.required", "None of the selected measures is related to a task"));
	return false;
}

function linkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "false")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#loading-indicator").show();
		$
			.ajax(
			{
				url: context + "/Analysis/Standard/Ticketing/Link",
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify(measures),
				success: function (response, textStatus, jqXHR) {
					if (response['error'])
						showDialog("#alert-dialog", response['error']);
					else {
						var $modal = $("#modal-ticketing-linker", new DOMParser().parseFromString(response, "text/html")), updateRequired = false;
						if (!$modal.length)
							unknowError();
						else {
							$("#modal-ticketing-linker").remove();
							$modal.appendTo($("#widgets")).modal("show");
							var isFinished = false, $linker = $modal.find("#measure-task-linker"), $measureViewer = $modal.find("#measure-viewer"), $taskViewer = $("#task-viewer"), $taskContainer = $modal
								.find("#task-container"), $tasks = $taskContainer.find("fieldset"), size = $tasks.length;

							taskController = function () {
								$view = $(this.getAttribute("href"));
								if (!$view.is(":visible")) {
									$taskViewer.find("fieldset:visible").hide();
									$view.show();
								}
								return false;
							}

							$tasks.appendTo($taskViewer);

							$taskContainer.find("a.list-group-item").on("click", taskController);

							$modal.on("hidden.bs.modal", function () {
								if (updateRequired)
									reloadSection("section_actionplans");
								$modal.remove();
							}).on("show.bs.modal", function () {
								$taskContainer.scrollTop();
							}).on("shown.bs.modal", function () {
								$taskContainer.on("scroll", function () {
									if (!isFinished && ($taskContainer.scrollTop() + $taskContainer.innerHeight() >= $taskContainer[0].scrollHeight)) {
										isFinished = true;
										$.ajax({
											url: context + "/Analysis/Standard/Ticketing/Load?startIndex=" + (size + 1),
											type: "POST",
											contentType: "application/json;charset=UTF-8",
											data: JSON.stringify(measures),
											success: function (response, textStatus, jqXHR) {
												$subTaskContainer = $("#task-container", new DOMParser().parseFromString(response, "text/html"));
												var $subTasks = $subTaskContainer.find("fieldset");
												if (!(isFinished = $subTasks.length == 0)) {
													size += $subTasks.length;
													$subTasks.appendTo($taskViewer);
													$subTaskContainer.find("a.list-group-item").appendTo($taskContainer).on("click", taskController);
												}
											}
										});
									}
								});

							});

							$modal.find("#measure-container>fieldset").appendTo($measureViewer);
							$modal.find("#measure-container>a.list-group-item").on("click", function () {
								$view = $(this.getAttribute("href"));
								if (!$view.is(":visible")) {
									$measureViewer.find("fieldset:visible").hide();
									$view.show();
								}
								return false;
							});

							$linker.on('click', function () {
								var $measure = $measureViewer.find("fieldset:visible"), $ticket = $taskViewer.find("fieldset:visible")
								if ($measure.length && $ticket.length) {
									$progress.show();
									$linker.prop("disabled", true)
									$.ajax({
										url: context + "/Analysis/Standard/Ticketing/Link/Measure",
										type: "POST",
										contentType: "application/json;charset=UTF-8",
										data: JSON.stringify({
											"idMeasure": $measure.attr("data-trick-id"),
											"idTicket": $ticket.attr("data-trick-id")
										}),
										success: function (response, textStatus, jqXHR) {
											if (response.success) {
												reloadMeasureRow($measure.attr("data-trick-id"), $measure.attr("data-trick-parent-id"));
												$("#" + $measure.remove().attr("aria-controls")).remove();
												$("#" + $ticket.remove().attr("aria-controls")).remove();
												updateRequired = true;
												if (!$measureViewer.find("fieldset").length || !$taskViewer.find("fieldset").length)
													$modal.modal("hide");
											} else if (response.error)
												showDialog("#alert-dialog", response.error);
											else
												unknowError();
										},
										error: unknowError
									}).complete(function () {
										$linker.prop("disabled", false);
										$progress.hide();
									});
								}
								return false;
							});

						}
					}
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.link.no_action.required", "All selected measures are already related to tasks"));
	return false;
}

function unLinkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});
	if (measures.length) {
		var $confirm = $("#confirm-dialog"), $question = measures.length == 1 ? MessageResolver("confirm.unlink.measure", "Are you sure, you want to unlink this measure and task")
			: MessageResolver("confirm.unlink.measures", "Are you sure, you want to unlink measures and tasks");
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function () {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/Analysis/Standard/Ticketing/UnLink",
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify(measures),
				success: function (response, textStatus, jqXHR) {
					if (response.error)
						showDialog("#alert-dialog", response.error);
					else if (response.success) {
						if (section == "#section_actionplans")
							location.reload();
						else {
							showDialog("#info-dialog", response.success);
							if (measures.length > 30)
								reloadSection([section.replace("#", ''), "section_actionplans"]);
							else {
								reloadSection("section_actionplans");
								setTimeout(function () {
									var idStandard = $(section).attr("data-trick-id");
									for (var i = 0; i < measures.length; i++)
										reloadMeasureRow(measures[i], idStandard);
								}, measures.length * 20);
							}
						}
					} else
						unknowError();
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		});
		$confirm.modal("show");
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.unlink.no_action.required", "None of the selected measures is related to a task"));
	return false;

}

function updateOrGenereteTickets(data) {
	if (data != undefined && (data.updates.length || data.news.length)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/Ticketing/Generate",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(data),
			success: function (response, textStatus, jqXHR) {
				if (response.error)
					showDialog("#alert-dialog", response.error);
				else if (response.success)
					application["taskManager"].Start();
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.generate.no_action.required", "All selected measures are already related to tasks"));
	return false;
}

function generateTickets(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = {
		updates: [],
		news: []
	};
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "false")
			measures.news.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
		else
			measures.updates.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.updates.length) {
		var $confirm = $("#confirm-dialog"), $question = measures.updates.length == 1 ? MessageResolver("confirm.update.ticket", "Are you sure, you want to update measure task")
			: MessageResolver("confirm.update.tickets", "Are you sure, you want to update " + measures.updates.length + " measures tasks", [measures.updates.length]);
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function () {
			$confirm.modal("hide");
			updateOrGenereteTickets(measures);
		});
		$confirm.modal("show");
	} else
		updateOrGenereteTickets(measures);
	return false;
}

function synchroniseWithTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax(
			{
				url: context + "/Analysis/Standard/Ticketing/Synchronise",
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify(measures),
				success: function (response, textStatus, jqXHR) {
					var $modal = $("#modal-ticketing-synchronise", new DOMParser().parseFromString(response, "text/html"));
					if ($modal.length) {
						$("#modal-ticketing-synchronise").remove();
						$modal.appendTo($("#widgets")).modal("show");
						var $previous = $modal.find(".previous"), $next = $modal.find(".next");
						$previous.find("a").on("click", function () {
							if (!$previous.hasClass("disabled")) {
								var $current = $modal.find("fieldset:visible"), $prev = $current.prev();
								if ($prev.length) {
									$current.hide();
									if (!$prev.show().prev().length)
										$previous.addClass("disabled")
									if ($next.hasClass("disabled"))
										$next.removeClass("disabled");
								}
							}
							return false;
						});

						$next.find("a").on("click", function () {
							if (!$next.hasClass("disabled")) {
								var $current = $modal.find("fieldset:visible"), $nextElement = $current.next();
								if ($nextElement.length) {
									$current.hide();
									if (!$nextElement.show().next().length)
										$next.addClass("disabled")
									if ($previous.hasClass("disabled"))
										$previous.removeClass("disabled");
								}
							}
							return false;
						});

						$modal.find("select[name='implementationRate']").on(
							"change",
							function () {
								var $this = $(this), $parent = $this.closest("fieldset"), idMeasure = $parent.attr("data-trick-id"), className = $this
									.attr("data-trick-class"), type = className == "MaturityMeasure" ? "int" : "double";
								$this.parent().removeClass("has-error has-success");
								$.ajax({
									url: context + "/Analysis/EditField/" + className + "/" + idMeasure,
									type: "post",
									data: '{"id":' + idMeasure + ', "fieldName":"implementationRate", "value":"' + defaultValueByType($this.val(), type, true)
									+ '", "type": "' + type + '"}',
									contentType: "application/json;charset=UTF-8",
									success: function (response, textStatus, jqXHR) {
										if (response["success"] != undefined) {
											$this.parent().addClass("has-success");
											reloadMeasureRow(idMeasure, $parent.attr("data-trick-parent-id"));
										} else {
											if (response["error"] != undefined)
												showDialog("#alert-dialog", response["error"]);
											else
												showDialog("#alert-dialog", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
											$this.parent().addClass("has-error");
										}
										return true;
									},
									error: function (jqXHR, textStatus, errorThrown) {
										showDialog("#alert-dialog", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
										$this.parent().addClass("has-error");
									}
								});
							});

					} else if (response["error"])
						showDialog("#alert-dialog", response['error']);
					else
						unknowError();
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.synchronise.no_action.required", "None of the selected measures is related to a task"));
	return false;
}

function isLinkedTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return true;
	return $("tbody>tr input:checked", section).closest("tr").attr("data-is-linked") === "true";
}

function isUnLinkedTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return true;
	var $element = $("tbody>tr input:checked", section).closest("tr");
	if ($element.length != 1 || !$element.has("data-is-linked"))
		return true;
	return $element.attr("data-is-linked") === "false";
}
