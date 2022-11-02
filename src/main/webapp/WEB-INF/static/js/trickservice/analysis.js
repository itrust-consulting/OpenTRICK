let el = null, table = null, taskController = function () {
};

let language = { // translated asynchronously below
	"label.dynamicparameter.evolution": "from {0} to {1}"
};

$(document).ready(function () {
	for (let key in language)
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

	// Periodically dynamic charts
	if (application.isDynamic) {
		window.setInterval(function () {
			loadChartDynamicParameterEvolution();
			loadChartDynamicAleEvolutionByAssetType();
			loadChartDynamicAleEvolution();
		}, 30000); // every 30s
	}
});

$.fn.loadOrUpdateChart = function (parameters) {
	if (!parameters.tooltip) {
		$.extend(true, parameters, {
			tooltip: {
				formatter: function () {
					let str_value = this.series.yAxis.userOptions.labels.format.replace("{value}", this.point.y);
					let str = "<span style=\"font-size:80%;\">" + this.x + "</span><br/><span style=\"color:" + this.point.series.color + "\">" + this.point.series.name
						+ ":</span>   <b>" + str_value + "</b>";

					if (this.series.options.metadata) {
						let dataIndex = this.series.xAxis.categories.indexOf(this.x);
						let metadata = this.series.options.metadata[dataIndex];
						if (metadata.length > 0)
							str += "<br/>\u00A0"; // non-breaking space;
						// prevents empty line from
						// being ignored
						for (let i = 0; i < metadata.length; i++)
							str += "<br/><b>" + metadata[i].dynamicParameter + "</b>: "
								+ language["label.dynamicparameter.evolution"].replace("{0}", metadata[i].valueOld).replace("{1}", metadata[i].valueNew);
					}
					return str;
				}
			}
		});
	}

	let chart = this.highcharts();
	if (chart === undefined || parameters.series.length != chart.series.length)
		return this.highcharts(parameters);

	// Invalidate whole graph if the collection of series changed
	for (let i = 0; i < chart.series.length; i++) {
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
	let id = application['selected-analysis-id'];
	if (id === undefined) {
		let el = document.querySelector("#nav-container");
		if (el == null)
			return -1;
		id = application['selected-analysis-id'] = el.getAttribute("data-trick-id");
	}
	return id;
}

function manageImpactScale() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Parameter/Impact-scale/Manage",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("#manageImpactModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {

					$view.appendTo("#widgets").modal("show").on('hidden.bs.modal', () => $view.remove());
					// load view static error message
					$("[data-lang-code]", $view).each(function () {
						resolveMessage(this.getAttribute("data-lang-code"), this.textContent);
					});

					$("button[name='save']", $view).on("click", e => {
						let data = {}, notEmpty = false;
						$(".form-group[data-trick-id]", $view).each(function () {
							let $this = $(this), newValue = $("input[type='radio']:checked,input[type!='radio']:visible", this).val(), oldValue = $("input[type!='radio']:hidden", this).val();
							if (newValue != oldValue)
								data[$this.attr("data-trick-id")] = newValue;
							notEmpty |= newValue === 'true';
						});

						if (!notEmpty) {
							showDialog("#alert-dialog", MessageResolver("error.manage.impact.empty"));
							return false;
						}

						if (Object.keys(data).length) {

							$progress.show();
							$.ajax({
								url: context + "/Analysis/Parameter/Impact-scale/Manage/Save",
								type: "post",
								data: JSON.stringify(data),
								contentType: "application/json;charset=UTF-8",
								success: function (response, textStatus, jqXHR) {
									if (response.error != undefined)
										showDialog("#alert-dialog", response.error);
									else if (response.success != undefined) {
										showDialog("success", response.success);
										setTimeout(() => window.location.reload(), 1000);
									} else if (response.warning != undefined)
										showDialog("warning", response.warning);
									else
										unknowError();
								},
								error: unknowError
							}).complete(function () {
								$progress.hide();
							});
						}

						$view.modal("hide");
					});
				}
			},
			error: unknowError
		}).complete(() => $progress.hide());
	}
	return false;
}

function manageScaleLevel() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Parameter/Scale-level/Manage",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("#manageScaleLevelModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$view.appendTo("#widgets").modal("show").on('hidden.bs.modal', () => $view.remove());
					// load view static error message
					let $orignalContainer = $("#original-container"), $container = $("#new-level-container", $view), $levelTemplate = $("#level-template-ui", $view);

					let drop = (e) => {
						e.preventDefault();
						let $body = $(".panel-body", e.currentTarget), $item = $(document.getElementById(e.originalEvent.dataTransfer.getData("level"))), $oldParent = $item.parent();
						$item.appendTo($body);
						$("span:visible", $body).hide();
						if (!$("[data-value]", $oldParent).length)
							$("span:hidden", $oldParent).show();
					}, dragover = (e) => {
						e.preventDefault();
					}, removeLevel = (e) => {
						let $panelUI = $(e.currentTarget).closest('.panel');
						$("[data-value]", $panelUI).appendTo($orignalContainer);
						$panelUI.remove();
						$(".panel[data-container-level!='0']", $container).each(function (i) {
							this.setAttribute("data-container-level", (i + 1));
							$(".panel-title", this).text(MessageResolver("label.scale.level.value").replace("{0}", (i + 1)));
						});
						return false;
					};


					$(".list-group-item[data-value]", $orignalContainer).on("dragstart", function (e) {
						e.originalEvent.dataTransfer.setData("level", e.target.id);
						$(".panel-body", $container).addClass("alert-warning");
					});

					$(".list-group-item[data-value]", $orignalContainer).on("dragend", function (e) {
						$(".panel-body.alert-warning", $container).removeClass("alert-warning");
					});

					$(".panel[data-container-level]", $container).on("drop", drop).on("dragover", dragover);

					$("[data-role='remove']", $container).on("click", removeLevel);


					$("[data-lang-code]", $view).each(function () {
						resolveMessage(this.getAttribute("data-lang-code"), this.textContent);
					});

					$("#btn-add-level", $view).on("click", (e) => {
						let index = parseInt($(".panel[data-container-level]:last-child", $container).attr("data-container-level")) + 1;
						let $ui = $levelTemplate.clone().removeAttr('id').attr("data-container-level", index);
						$(".panel-title", $ui).text(MessageResolver("label.scale.level.value").replace("{0}", index));
						$ui.appendTo($container).on("drop", drop).on("dragover", dragover);
						$("[data-role='remove']", $ui).on("click", removeLevel);
					});

					$("[data-value][draggable='true']", $view).on("dragstart", function (e) {
						if (e.target.getAttribute("draggable") === "true")
							e.originalEvent.dataTransfer.setData("level", e.target.id);
						else e.preventDefault();
					});


					$("button[name='save']").on("click", e => {
						if ($("[data-value]", $orignalContainer).length) {
							showDialog("#alert-dialog", MessageResolver("error.scale.level.not.all.selected"));
							return false;
						}

						let data = {};
						$(".panel[data-container-level]", $container).each(function (i) {
							data[i] = [];
							$("[data-value]", this).each(function () {
								data[i].push(parseInt(this.getAttribute("data-value")));
							});
						})

						$progress.show();
						$.ajax({
							url: context + "/Analysis/Parameter/Scale-level/Manage/Save",
							type: "post",
							data: JSON.stringify(data),
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								if (response.error != undefined)
									showDialog("#alert-dialog", response.error);
								else if (response.success != undefined) {
									showDialog("success", response.success);
									application["taskManager"].Start();
								} else if (response.warning != undefined)
									showDialog("warning", response.warning);
								else
									unknowError();
							},
							error: unknowError
						}).complete(function () {
							$progress.hide();
						});

						$view.modal("hide");
					});
				}
			},
			error: unknowError
		}).complete(() => $progress.hide());
	}
	return false;
}

function updateScroll(element) {
	let currentActive = document.activeElement;
	if (element != currentActive) {
		element.focus();// update scroll
		currentActive.focus();
	}
	return false;
}

function findAnalysisLocale() {
	let locale = application['selected-analysis-locale'];
	if (locale === undefined) {
		let el = document.querySelector("#nav-container");
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
	if (application.analysisType.isQualitative())
		reloadRiskChart();
	if (application.analysisType.isQuantitative())
		chartALE();
	return false;
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
				let sections = $(element).attr("data-trick-section-dependency");
				if (sections != undefined)
					return reloadSection(sections.split(','));
				let callBack = $(element).attr("data-trick-callback");
				if (callBack != undefined)
					return eval(callBack);
				let reload = $(element).attr("data-trick-reload");
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
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Manage-settings",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("#analysisSettingModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$view.appendTo("#widgets").modal("show").on('hidden.bs.modal', () => $view.remove());
					$("button[name='save']").on("click", e => {
						let data = {};

						$(".form-group[data-trick-name]", $view).each(function () {
							let $this = $(this), newValue = $("input[type='radio']:checked,input[type!='radio']:visible", this).val(), oldValue = $("input[type!='radio']:hidden", this).val();
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
	let $currentRow = $("#section_standard_" + standard + " tr[data-trick-id='" + idMeasure + "']");
	if (!$currentRow.find("input[type!='checkbox'],select,textarea").length) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/" + standard + "/SingleMeasure/" + idMeasure,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $newRow = $("tr", $("<div/>").html(response));
				if (!$newRow.length)
					$currentRow.addClass("warning").attr("title", MessageResolver("error.ui.no.synchronise", "User interface does not update"));
				else {
					let $checked = $currentRow.find("input:checked");
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

	reloadSection("section_phase");

	return false;
}

function soaThresholdUpdate() {
	return reloadSection(["section_soa", "section_phase"]);
}


function reloadMeasureAndCompliance(standard, idMeasure) {
	reloadMeasureRow(idMeasure, standard);
	compliance(standard);
	return tryToReloadSOA(standard, idMeasure);
}

function tryToReloadSOA(standard, idMeasure) {
	if (document.getElementById("table_SOA_" + standard))
		reloadSection("section_soa");
	return false;
}

function reloadRiskAcceptanceTable($tabSection) {
	let $tbody = $("table>tbody", $tabSection), $trs = $("table#table_parameter_risk_acceptance tbody>tr[data-trick-id]").clone();
	if ($trs.length) {
		$tbody.empty();
		$trs.each(function () {
			let $this = $(this).removeAttributes();
			$("td[data-trick-field!='color']", $this).removeAttributes();
			$("td[data-trick-field]", $this).removeAttr("data-trick-field");
			$this.attr("style", "text-align:center");
			$this.appendTo($tbody);
		});
	}
	$tabSection.attr("data-parameters", false);
}

function reloadRiskHeatMapSection(tableChange) {
	let $tabSection = $("#tab-chart-heat-map");
	if ($tabSection.is(":visible")) {
		loadRiskHeatMap();
		loadRiskEvolutionHeatMap();
		if (tableChange)
			reloadRiskAcceptanceTable($tabSection);
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function reloadRiskAssetSection(tableChange) {
	let $tabSection = $("#tab-chart-risk-asset");
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
	let $tabSection = $("#tab-chart-risk-asset-type");
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
	let $tabSection = $("#tab-chart-risk-scenario");
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
	let $tabSection = $("#tab-chart-risk-scenario-type");
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
	let $progress = $("#loading-indicator").show();
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


function loadRiskEvolutionHeatMap() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Assessment/Chart/Risk-evolution-heat-map",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (window.riskEvolutionHeatMap != undefined)
				window.riskEvolutionHeatMap.destroy();
			window.riskEvolutionHeatMap = new Chart(document.getElementById("risk_acceptance_evolution_canvas").getContext("2d"), {
				data: response,
				options: evolutionHeatMapOption(response["xLabels"], response["yLabels"])
			});
			let $displayValue = $("#chart-show-x-element").unbind("change"), count = parseInt($displayValue.val());
			let $container = $("#risk_acceptance_evolution_legend"), chart = window.riskEvolutionHeatMap, datasets = chart.data.datasets, $legends = $("<div class='list-group' />");
			if (isNaN(count))
				count = 10;
			let displayed = 0;
			for (let i = 0; i < datasets.length; i++) {
				let dataset = datasets[i];
				if (dataset.type === "heatmap")
					continue;
				let $legend = $("<button type='button' class='list-group-item' data-chart-id='" + i + "'><span  style='background-color: " + dataset.backgroundColor + "; padding: 1px 12px;margin-right: 3px;' /></button>"), $text = $("<span class='text'/>").text(dataset.legendText).appendTo($legend);
				$legend.appendTo($legends).on("click", function (e) {
					let $target = $(e.currentTarget), id = parseInt($target.attr("data-chart-id")), dataset = datasets[id];
					if (dataset.hidden)
						$target.css({ "text-decoration": "none" });
					else
						$target.css({ "text-decoration": "line-through" });
					dataset.hidden = !dataset.hidden;
					chart.update();
				});

				if ((dataset.hidden = (++displayed > count)))
					$legend.css({ "text-decoration": "line-through" });
			}
			$displayValue.attr("max", displayed);
			chart.update();
			$legends.appendTo($container.empty());
			$legends.css({ "height": chart.height });

			$displayValue.on("change", function (e) {
				let value = this.value, count = 0;
				for (let i = 0; i < datasets.length; i++) {
					let dataset = datasets[i];
					if (dataset.type === "heatmap")
						continue;
					let $target = $("[data-chart-id='" + i + "']", $container);
					if ((dataset.hidden = (++count > value)))
						$target.css({ "text-decoration": "line-through" });
					else $target.css({ "text-decoration": "none" });
				}
				chart.update();
				return false;
			});
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;

}


function checkForCollectionUpdate() {
	triggerCaller($("div[id~='tab-standard-']:visible"));
}

function loadRiskChart(url, name, container, canvas) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: url,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (window[name] != undefined)
				helpers.isArray(window[name]) ? window[name].map(chart => chart.destroy()) : window[name].destroy();
			if (helpers.isArray(response)) {
				window[name] = [];
				let $container = $(container).empty();
				response.map(chart => {
					let $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo($container);
					window[name].push(new Chart($canvas[0].getContext("2d"), {
						type: "bar",
						data: chart,
						options: riskOptions()
					}));

				});
			}
			else {
				$(container).html("<canvas id='" + canvas + "' style='margin-left: auto; margin-right: auto;' />")
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

function updateMeasureEffience(reference, force) {
	if (!application.hasMaturity)
		return;
	let $standard27002 = $("div[id^='section_standard_'][data-trick-label='27002']");
	if (!$standard27002.length)
		return;
	let $tabPane = $standard27002.closest("div[data-targetable='true']"), updateRequired = $tabPane.attr("data-update-required"), triggerName = $tabPane.attr('data-trigger');
	if (updateRequired && triggerName == "reloadSection")
		return;
	let data = [], chapters = application["parameter-27002-efficience"];
	if ($standard27002.is(":visible") || force) {
		if (Array.isArray(chapters)) {
			data = chapters;
			delete application["parameter-27002-efficience"];
		} else
			$standard27002.find("tr[data-trick-computable='false']").each(function () {
				data.push(this.getAttribute('data-trick-reference'))
			});
	} else {
		$tabPane.attr("data-update-required", true).attr("data-trigger", 'updateMeasureEffience');
		if (reference == undefined)
			delete application["parameter-27002-efficience"];
		else {
			let chapter = reference.split(".", 3)[1], parameters = application["parameter-27002-efficience"], $selector = $standard27002
				.find("tr[data-trick-computable='false'][data-trick-reference='" + chapter + "']");
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
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Standard/Compute-efficiency",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (typeof response === 'object') {
				for (let id in response)
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
	let $section = $("#tab-chart-compliance");
	if ($section.is(":visible")) {
		for (let type of application['complianceType'])
			loadComplianceChart(context + "/Analysis/Standard/Compliances/" + type);

	}
	else $section.attr("data-update-required", "true");
	return false;
}

function compliance(standard) {
	let $section = $("#tab-chart-compliance");
	if ($section.is(":visible")) {
		for (let type of application['complianceType'])
			loadComplianceChart(context + "/Analysis/Standard/" + standard + "/Compliance/" + type);
	}
	else
		$section.attr("data-update-required", "true");
	return false;
}

function loadComplianceChart(url) {
	let $progress = $("#loading-indicator").show(), name = "compliancesChart", $container = $("#chart_compliance_body"), canvas = "chart_canvas_compliance_";
	try {
		$.ajax({
			url: url,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						if (window[name].has(chart.trickId)) {
							window[name].get(chart.trickId).config.data = chart;
							window[name].get(chart.trickId).update();
						} else {
							let $parent = $("<div class='col-sm-6 col-md-4 col-lg-3' id= 'chart_compliance_" + chart.trickId + "' />").appendTo($container), $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo($parent);
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
	let $section = $("#tab-chart-evolution");
	if ($section.is(":visible")) {
		let $progress = $("#loading-indicator").show(), name = "evolutionProfitabilityComplianceChart";
		$.ajax({
			url: context + "/Analysis/ActionPlanSummary/Evolution/" + actionPlanType,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				charts.map(chart => {

					if (chart.datasets && chart.datasets.length) {
						chart.datasets.filter(dataset => dataset.type == "line").map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						if (window[name].has(chart.trickId)) {
							window[name].get(chart.trickId).config.data = chart;
							window[name].get(chart.trickId).update();
						} else {
							let $parent = $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo("#" + chart.trickId);
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
	let $section = $("#tab-chart-budget");
	if ($section.is(":visible")) {
		let $progress = $("#loading-indicator").show(), name = "budgetCharts";
		$.ajax({
			url: context + "/Analysis/ActionPlanSummary/Budget",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						if (window[name].has(chart.trickId)) {
							window[name].get(chart.trickId).config.data = chart;
							window[name].get(chart.trickId).update();
						} else {
							let $parent = $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo("#" + chart.trickId);
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
	let $element = $(id);
	if ($.isArray(response)) {
		// First prepare the document structure so that there is exactly one
		// <div> available for each chart
		if ($element.find(">div").length != response.length) {
			$element.empty();
			for (let i = 0; i < response.length; i++) {
				if (i > 0)
					$("<hr  style='margin: 30px 0;'>").appendTo($element);
				$("<div/>").appendTo($element)
			}
		}
		// Now load the charts themselves
		let divSelector = $element.find(">div");
		for (let i = 0; i < response.length; i++)
			$(divSelector.get(i)).loadOrUpdateChart(response[i]);
	} else
		$element.loadOrUpdateChart(response);
}


function deleteDynamicParameter(id, acronym) {
	let $modal = showDialog("#confirm-dialog", MessageResolver("label.dynamic_parameter.question.delete", "Are you sure that you want to delete letiable (" + acronym + ")?", acronym));
	$("button[name='yes']", $modal).unbind().one("click", function () {
		let $progress = $("#loading-indicator").show();
		$.ajax(
			{
				url: context + "/Analysis/Parameter/Dynamic/Delete/" + id,
				type: "DELETE",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					if (response.success) {
						$("tr[data-trick-class='DynamicParameter'][data-trick-id=" + id + "]").remove();
						$("datalist[id^='dataList-parameter-']").remove();
						updateAssessmentAle(true);
					}
					else if (response.error)
						showDialog("#alert-dialog", response.error);
					else
						unknowError();
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		$modal.modal("hide");
		return false;
	});

}

function manageRiskAcceptance() {
	let $progress = $("#loading-indicator").show();
	$
		.ajax(
			{
				url: context + "/Analysis/Parameter/Risk-acceptance/form",
				type: "get",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					let $content = $("#modalRiskAcceptanceForm", new DOMParser().parseFromString(response, "text/html"));
					if (!$content.length)
						showError("Error data cannot be loaded");
					else {
						let actionDelete = function () {
							$(this).closest("tr").remove();
						};
						$("button[name='delete']", $content).on("click", actionDelete);
						$("button[name='add']", $content)
							.on(
								"click",
								function () {
									let $this = $(this), $trParent = $this.closest("tr"), maxValue = $trParent.attr("data-trick-max-value"), $tr = $("<tr data-trick-id='-1' />"), $div = $("<div class='range-group' />"), $rangeInfo = $(
										"<span class='range-text'>0</span>").appendTo($div), $range = $(
											"<input type='range' min='1' max='" + maxValue + "'  name='value' value='0' class='range-input'>").appendTo($div), $removeBtn = $("<button class='btn btn-danger outline' type='button' name='delete'><i class='fa fa-remove'></i></button>"), $inputColor = $("<input name='color' type='color' value='#fada91' class='form-control form-control-static'>");
									$removeBtn.appendTo($("<td/>").appendTo($tr));
									$("<input name='label' class='form-control'>").appendTo($("<td />").appendTo($tr));
									$div.appendTo($("<td />").appendTo($tr));
									$("<textarea name='description' class='form-control resize_vectical_only' rows='1' />").appendTo($("<td />").appendTo($tr));
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
							let $table = $("table[data-trick-size]", $content), size = $table.attr("data-trick-size"), data = [];
							$("tr[data-trick-id]", $table).each(function () {
								let $this = $(this);
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
										reloadSection(["section_parameter_impact_probability", "section_parameter", "section_riskregister"]);
										riskEstimationUpdate(true);
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
	let $section = $("#tab-chart-asset");
	if ($section.is(":visible")) {
		loadALEChart(context + "/Analysis/Asset/Chart/Ale", "aleAsset", "#chart_ale_asset", "risk_ale_asset_canvas");
		loadALEChart(context + "/Analysis/Asset/Chart/Type/Ale", "aleAssetType", "#chart_ale_asset_type", "risk_ale_asset_type_canvas");
	}
	else
		$section.attr("data-update-required", "true");
	return false;
}

function loadChartScenario() {
	let $section = $("#tab-chart-scenario");
	if ($section.is(":visible")) {
		loadALEChart(context + "/Analysis/Scenario/Chart/Type/Ale", "aleScenarioType", "#chart_ale_scenario_type", "risk_ale_scenario_type_canvas");
		loadALEChart(context + "/Analysis/Scenario/Chart/Ale", "aleScenario", "#chart_ale_scenario", "risk_ale_scenario_canvas");
	}
	else
		$section.attr("data-update-required", "true");
	return false;
}

function loadChartDynamicParameterEvolution() {
	let $section = $("#tab-chart-parameter-evolution"), name = 'chart-parameter-evolution-map';
	if ($section.is(":visible")) {
		let $progress = $("#loading-indicator").show(), $container = $section.find("#chart_parameterevolution_body");
		$.ajax({
			url: context + "/Analysis/Dynamic/Chart/ParameterEvolution",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				else {
					window[name].forEach((chart, key) => {
						chart.destroy();
						$("[id='chart-parameter-evolution-" + key + "']", $container).remove();
					});
				}

				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						let $parent = $("<div class='col-md-offset-1 col-md-10' id='chart-parameter-evolution-" + chart.trickId + "' />").appendTo($container), $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo($parent);
						window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
							type: "line",
							data: chart,
							options: dynamicParameterEvolutionOptions(chart.title)
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

	let $section = $("#tab-chart-ale-evolution-by-asset-type"), name = 'chart-ale-evolution-by-asset-type-map';

	if ($section.is(":visible")) {
		let $progress = $("#loading-indicator").show(), $container = $section.find("#chart_aleevolutionbyassettype_body");
		$.ajax({
			url: context + "/Analysis/Dynamic/Chart/AleEvolutionByAssetType",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				else {
					window[name].forEach((chart, key) => {
						chart.destroy();
						$("[id='chart-ale-evolution-by-asset-type-" + key + "']", $container).remove();
					});
				}

				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						let $parent = $("<div class='col-lg-6' id='chart-ale-evolution-by-asset-type-" + chart.trickId + "' />").appendTo($container), $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo($parent);
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
	let $section = $("#tab-chart-ale-evolution"), name = 'chart-ale-evolution';
	if ($section.is(":visible")) {
		let $progress = $("#loading-indicator").show(), $container = $section.find("#chart_aleevolution_body");
		$.ajax({
			url: context + "/Analysis/Dynamic/Chart/AleEvolution",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			async: true,
			success: function (response, textStatus, jqXHR) {
				let color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
				if (window[name] == undefined)
					window[name] = new Map();
				else {
					window[name].forEach((chart, key) => {
						chart.destroy();
						$("[id='chart-ale-evolution-" + key + "']", $container).remove();
					});
				}

				charts.map(chart => {
					if (chart.datasets && chart.datasets.length) {
						chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
						let $parent = $("<div class='col-md-offset-1 col-md-10' id='chart-ale-evolution-" + chart.trickId + "' />").appendTo($container), $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo($parent);
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
	let $progress = $("#loading-indicator").show();
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
					let $container = $(container).empty();
					response.map(chart => {
						let $canvas = $("<canvas style='margin-left: auto; margin-right: auto;' />").appendTo($container);
						window[name].push(new Chart($canvas[0].getContext("2d"), {
							type: "bar",
							data: chart,
							options: aleChartOption(chart.title)
						}));
					});
				}
				else {
					$(container).html("<canvas id='" + canvas + "' style='margin-left: auto; margin-right: auto;' />")
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
	let currentMenu = $("li[data-trick-nav-control='" + navSelected + "']", parentMenu);
	if (!currentMenu.length || $(currentMenu).hasClass("disabled"))
		return false;
	$("li[data-trick-nav-control]", parentMenu).each(function () {
		if (this.getAttribute("data-trick-nav-control") == navSelected)
			this.classList.add("disabled");
		else if (this.classList.contains('disabled'))
			this.classList.remove("disabled");
	});

	$("[data-trick-nav-content]", section).each(function () {
		let $this = $(this);
		if (this.getAttribute("data-trick-nav-content") == navSelected)
			$this.show();
		else if ($this.is(":visible"))
			$this.hide();
	});
	$(window).scroll();
	return false;
}

function manageBrainstorming(type) {
	let $progress = $("#loading-indicator").show(), category = type.replace(/\b\w/g, s => s.toUpperCase());
	$.ajax({
		url: context + "/Analysis/Risk-information/Manage/" + category,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $modal = $("#modal-manage-brainstorming", new DOMParser().parseFromString(response, "text/html"));
			if ($modal.length) {
				$modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", e => $modal.remove());

				updateRiskInformationAddBtn($("table", $modal));
				$("form button[name='add']", $modal).on("click", addNewRiskInformtion);;
				$("button[name='add-chapter']", $modal).on("click", addNewRiskInformtionChapter);
				$("button[name='clear']", $modal).on("click", clearRiskInformtion);
				$("button[name='delete']", $modal).on("click", removeRiskInformtion);
				$("button[name='save']", $modal).on("click", e => $("input[type='submit']", $modal).trigger("click"));
				$("input[name='chapter']", $modal).on("blur", (e) => validateRiskInformationChapter($modal));
				$("form", $modal).on("submit", e => {
					$progress.show();
					let data = parseRiskInformationData(category === "Risk" ? "Risk_TBA" : category, $("form tbody>tr[data-trick-id]", $modal));
					$.ajax({
						url: context + "/Analysis/Risk-information/Manage/" + category + "/Save",
						type: "post",
						data: JSON.stringify(data),
						contentType: "application/json;charset=UTF-8",
						success: function (response, textStatus, jqXHR) {
							if (response['success']) {
								reloadSection('section_risk-information_' + type);
								$modal.modal("hide");
							}
							else if (response['error'])
								showDialog("#alert-dialog", response['error']);
							else unknowError();
						}, error: unknowError
					}).complete(() => $progress.hide());
					return false;
				});


			} else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

function parseRiskInformationData(category, $trs) {
	let data = [];
	$trs.each(function (i) {
		let $this = $(this), $label = $("input[name='label']", $this), $chapter = $("input[name='chapter']", $this), id = $this.attr("data-trick-id"), chapter = $chapter.val(), label = $label.val(), custom = $("input[name='cutom']", $this).val();
		data.push({
			id: id,
			category: category,
			chapter: chapter,
			label: label,
			custom: custom === 'true' || label !== $label.attr('placeholder') || chapter !== $chapter.attr('placeholder')
		});
	});
	return data;

}

function addNewRiskInformtion(e) {
	let $this = $(this), $currentTr = $this.closest("tr"), $tr = $("<tr data-trick-id='-1' />");
	addNewRiskInformation($currentTr, $tr, $("#risk-information-btn", $this.closest(".modal")), true);
	return false;
}

function addNewRiskInformtionChapter(e) {
	let $this = $(this), $currentTr = $this.closest("tr"), $tr = $("<tr data-trick-id='-1'/>");
	addNewRiskInformation($currentTr, $tr, $("#risk-information-btn", $this.closest(".modal")), false);
	return false;
}

function addNewRiskInformation($currentTr, $tr, $buttons, after) {
	$("<td><input type='hidden' name='id' value='-1' /><input name='chapter' required class='form-control'><input type='hidden' name='custom' value='true' /></td>").appendTo($tr);
	$("<td><input class='form-control' type='text' name='label' placeholder='' required ></td>").appendTo($tr);
	$("<td />").html($buttons.html()).appendTo($tr);
	$("button[name='delete']", $tr).on("click", removeRiskInformtion);
	$("button[name='clear']", $tr).on("click", clearRiskInformtion);
	if (after)
		$tr.insertAfter($currentTr);
	else $tr.insertBefore($currentTr);
	$("button[name='add']", $tr).on("click", addNewRiskInformtion);
	$("input[name='chapter']", $tr).on("blur", (e) => {
		validateRiskInformationChapter($(e.currentTarget).closest(".modal"));
	})
	updateRiskInformationAddBtn($tr.closest("table"));
	return false;
}

function removeRiskInformtion(e) {
	let $currentTr = $(this).closest("tr"), $table = $currentTr.closest("table");
	$currentTr.remove();
	updateRiskInformationAddBtn($table);
	return false;
}

function updateRiskInformationAddBtn($table) {
	if ($table || $table.length) {
		let $chapter = $("input[name='chapter']", $table);
		if ($chapter.length)
			$("tr[data-role='add-btn']", $table).hide();
		else $("tr[data-role='add-btn']", $table).show();
		validateRiskInformationChapter($table.closest(".modal"));
	}
	return false;
}

function validateRiskInformationChapter($modal) {
	let chpaters = new Map();
	$(".has-error", $modal).removeClass("has-error");
	$("input[name='chapter']", $modal).filter((i, el) => {
		if (chpaters.has(el.value.trim()))
			return true
		chpaters.set(el.value, true);
		return false;
	}).each((i, el) => $(el).closest("td").addClass("has-error"));
	$("button[name='save']", $modal).prop('disabled', $(".has-error", $modal).length);
	return false;
}

function clearRiskInformtion(e) {
	let $target = $(e.currentTarget), $tr = $target.closest("tr"), $table = $target.closest('table'), value = $("input[name='chapter']", $tr).val();
	if (!(value === undefined || value === null)) {
		let filter = () => false;

		value = value.trim();

		if (value === "")
			filter = (i, el) => el.value.trim() === value;
		else if (value.match(/(\d+\.)+(\d+\.*)*$/g)) {
			let chapter = value.replace(/(\.0*)*$/g, '');
			filter = (i, el) => el.value.startsWith(chapter);
		}
		else filter = (i, el) => el.value.startsWith(value) || el.value === value;

		$("input[name='chapter']", $table).filter(filter).closest("tr").remove();
	}
	updateRiskInformationAddBtn($table);
	return false;
}

function importRiskInformationForm() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Risk-information/Import-form",
		success: function (response, textStatus, jqXHR) {
			let $modal = $("#risk-information-modal", new DOMParser().parseFromString(response, "text/html"));
			if ($modal.length) {
				$("button[name='import']", $modal).on("click", importRiskInformation);
				$modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", e => $modal.remove());
			} else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error: unknowError

	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function importRiskInformation() {
	let $modal = $("#risk-information-modal"), $uploadFile = $("#upload-file-info", $modal), $progress = $("#loading-indicator"), $riskNotification = $("#riskInfromationNotification", $modal);
	if (!$uploadFile.length)
		return false;
	else if ($uploadFile.val() == "") {
		$riskNotification.text(MessageResolver("error.import.risk.information.no_select.file", "Please select file to import!"));
		return false;
	}
	try {
		$progress.show();
		$.ajax({
			url: context + "/Analysis/Risk-information/Import",
			type: 'POST',
			data: new FormData($('#importRiskInformationForm', $modal)[0]),
			cache: false,
			contentType: false,
			processData: false,
			success: function (response, textStatus, jqXHR) {
				if (response.success)
					application["taskManager"].SetTitle(MessageResolver("label.title.import.risk.information", "Import brainstorming")).Start();
				else if (response.error)
					showDialog("#alert-dialog", response.error);
				else
					showDialog("#alert-dialog", MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
			},
			error: unknowError

		}).complete(function () {
			$progress.hide();
		});
	} finally {
		$modal.modal("hide");
	}
	return false;
}

function backupFieldHeight(baseName, name, container) {
	if (Array.isArray(name)) {
		for (let field of name)
			backupFieldHeight(baseName, field, container);
	}
	else {
		let backupName = baseName + "-" + name + "-height", data = application[backupName];
		if (data !== undefined) {
			let $textarea = $("#" + name, container);
			if ($textarea.length) {
				let height = $textarea.outerHeight();
				if (Math.abs(height - data.defaultValue) > 8) {
					data.previous = data.value;
					data.value = $textarea.outerHeight();
				} else if (data.value != height && data.previous != height)
					data.previous = data.value = null;
			}
		}
	}
	return false;
}

function restoreFieldHeight(baseName, name, container) {
	if (Array.isArray(name)) {
		for (let field of name)
			restoreFieldHeight(baseName, field, container);
	}
	else {
		let $textarea = $("#" + name, container);
		if ($textarea.length) {
			let backupName = baseName + "-" + name + "-height", height = $textarea.outerHeight(), data = application[backupName];
			if (data === undefined)
				data = application[backupName] = { defaultValue: height, value: null, previous: null };
			else data.defaultValue = height;
			if (data.value !== null) {
				$textarea.css({
					"height": data.value
				});
			}
		}
	}
	return false;
}