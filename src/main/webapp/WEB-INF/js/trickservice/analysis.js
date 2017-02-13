var el = null, table = null, taskController = function() {
};

var language = { // translated asynchronously below
	"label.dynamicparameter.evolution" : "from {0} to {1}"
};

$(document).ready(function() {
	for ( var key in language)
		language[key] = MessageResolver(key, language[key]);

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************

	$("input[type='checkbox']").removeAttr("checked");

	var $tabOption = $("#tabOption");

	application["settings-fixed-header"] = {
		fixedOffset : $(".nav-analysis"),
		marginTop : application.fixedOffset,
		scrollStartFixMulti : 0.99998
	};

	fixTableHeader("table.table-fixed-header-analysis");

	$('ul.nav-analysis a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
		disableEditMode();
		$tabOption.hide();
	});

	Chart.defaults.global.defaultFontColor = "#333";
	Chart.defaults.global.defaultFontFamily = 'Corbel', 'Lucida Grande', 'Lucida Sans Unicode', 'Verdana', 'Arial', 'Helvetica', 'sans-serif';
	Chart.defaults.global.defaultFontSize = 13;

	Highcharts.setOptions({
		lang : {
			decimalPoint : ',',
			thousandsSep : ' '
		},
		chart : {
			style : {
				fontFamily : 'Corbel,"Lucida Grande", "Lucida Sans Unicode", Verdana, Arial, Helvetica, sans-serif'
			}
		}
	});

	// Periodically reload assessment values
	window.setInterval(function() {
		reloadAssetScenario();
		loadChartDynamicParameterEvolution();
		loadChartDynamicAleEvolutionByAssetType();
		loadChartDynamicAleEvolutionByScenario();
	}, 300000); // every 30s
});

$.fn.loadOrUpdateChart = function(parameters) {
	if (!parameters.tooltip) {
		$.extend(true, parameters, {
			tooltip : {
				formatter : function() {
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
	$.each(chart.series, function(i, series) {
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
	if ($("#section_asset:visible"))
		reloadSection("section_asset")
	else if ($("#section_scenario"))
		reloadSection("section_scenario");
	else
		reloadSection([ "section_asset", "section_scenario", undefined, true ]);
}

function isEditable() {
	return userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY);
}

function updateSettings(element, entryKey) {
	$.ajax({
		url : context + "/Settings/Update",
		type : 'post',
		data : {
			'key' : entryKey,
			'value' : !$(element).hasClass('glyphicon-ok')
		},
		async : false,
		success : function(response, textStatus, jqXHR) {
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
		error : unknowError
	});
	return false;
}

function updateMeasuresCost() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Update/Cost",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response.success == undefined)
					unknowError()
				else {
					$("div[id^='section_standard_']").each(function() {
						reloadSection(this.id);
					});
				}
			},
			error : unknowError
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
			url : context + "/Analysis/Standard/" + standard + "/SingleMeasure/" + idMeasure,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
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
			error : unknowError
		}).complete(function() {
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

function reloadRiskHeatMapSection(tableChange) {
	var $tabSection = $("#tab-chart-heat-map");
	if ($tabSection.is(":visible")) {
		loadRiskHeatMap();
		if (tableChange) {
			var $tbody = $("#tab-chart-heat-map table>tbody"), $trs = $("table#table_parameter_risk_acceptance tbody>tr[data-trick-id]").clone();
			if ($trs.length) {
				$tbody.empty();
				$trs.each(function() {
					var $this = $(this).removeAttributes();
					$("td[data-trick-field!='color']", $this).removeAttributes();
					$("td[data-trick-field]", $this).removeAttr("data-trick-field");
					$this.attr("style", "text-align:center");
					$this.appendTo($tbody);
				});
			}
			$tabSection.attr("data-parameters", false);
		}
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function reloadRiskAssetSection(tableChange){
	var $tabSection = $("#tab-chart-risk-asset");
	if ($tabSection.is(":visible")) {
		loadRiskAssetChart();
		if (tableChange) {
			var $tbody = $("table>tbody", $tabSection), $trs = $("table#table_parameter_risk_acceptance tbody>tr[data-trick-id]").clone();
			if ($trs.length) {
				$tbody.empty();
				$trs.each(function() {
					var $this = $(this).removeAttributes();
					$("td[data-trick-field!='color']", $this).removeAttributes();
					$("td[data-trick-field]", $this).removeAttr("data-trick-field");
					$this.attr("style", "text-align:center");
					$this.appendTo($tbody);
				});
			}
			$tabSection.attr("data-parameters", false);
		}
	} else if (tableChange)
		$tabSection.attr("data-update-required", true).attr("data-parameters", true);
	else
		$tabSection.attr("data-update-required", true);
	return false;
}

function loadRiskHeatMap() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Assessment/Chart/Risk-heat-map",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (window.riskHeatMap != undefined)
				window.riskHeatMap.destroy();
			window.riskHeatMap = new Chart(document.getElementById("risk_acceptance_heat_map_canvas").getContext("2d"), {
				type : 'heatmap',
				data : response,
				options : {
					scales : {
						yAxes : [ {
							scaleLabel : {
								display : true,
								labelString : MessageResolver("label.title.impact", "Impact"),
								fontFamily : "'Corbel','Lucida Grande', 'Lucida Sans Unicode', 'Verdana', 'Arial', 'Helvetica', 'sans-serif'",
								fontSize : 22,
							}
						} ],
						xAxes : [ {
							scaleLabel : {
								display : true,
								labelString : MessageResolver("label.title.likelihood", "Probability"),
								fontFamily : "'Corbel','Lucida Grande', 'Lucida Sans Unicode', 'Verdana', 'Arial', 'Helvetica', 'sans-serif'",
								fontSize : 22,
							}
						} ]
					},
					tooltips : {
						enabled : false
					},
					legend : {
						display : true,
						position : 'top',
						onClick : function() {
							return false;
						},
						labels : {
							generateLabels : function(chart) {
								var data = chart.data;

								return helpers.isArray(data.legends) ? data.legends.map(function(legend) {
									return {
										text : legend.label,
										fillStyle : legend.color
									};
								}, this) : [];
							}
						}
					}
				}
			});
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;

}

function loadRiskAssetChart() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Asset/Chart/Risk",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (window.riskAsset != undefined)
				window.riskAsset.destroy();
			window.riskAsset = new Chart(document.getElementById("risk_acceptance_asset_canvas").getContext("2d"), {
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
		},
		error : unknowError
	}).complete(function() {
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
			$standard27002.find("tr[data-trick-computable='false'][data-trick-level='1']").each(function() {
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
					application["parameter-27002-efficience"] = [ chapter ];
			} else
				$tabPane.attr("data-update-required", updateRequired).attr("data-trigger", triggerName);
		}
	}

	if (!data.length)
		return;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Standard/Compute-efficience",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (typeof response === 'object') {
				for ( var id in response)
					$("tr[data-trick-id='" + id + "'][data-trick-computable='true'] td[data-trick-field='mer']", $standard27002).text(parseInt(response[id], 10));
			} else
				showDialog("#alert-dialog", MessageResolver("error.measure.mer.update", "Maturity-based effectiveness rate cannot be updated"));
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

// charts

function compliances() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Standard/Compliances",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response.standards == undefined || response.standards == null)
				return;
			var $complianceBody = $("#chart_compliance_body").empty();
			$.each(response.standards, function(key, data) {
				if ($complianceBody.children().length)
					$complianceBody.append("<div class='col-xs-4' id='chart_compliance_" + key + "'></div>");
				else
					$complianceBody.append("<div class='col-xs-4' id='chart_compliance_" + key + "'></div>");
				$('div[id="chart_compliance_' + key + '"]').loadOrUpdateChart(data[0]);
			});
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function compliance(standard) {
	if (!$('#chart_compliance_' + standard).length)
		return false;
	if ($('#chart_compliance_' + standard).is(":visible")) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Standard/" + standard + "/Compliance",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return;
				$('#chart_compliance_' + standard).loadOrUpdateChart(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		$("#tabChartCompliance").attr("data-update-required", "true");
	return false;
}

function evolutionProfitabilityComplianceByActionPlanType(actionPlanType) {
	if (!$('#chart_evolution_profitability_compliance_' + actionPlanType).length)
		return false;
	if ($('#chart_evolution_profitability_compliance_' + actionPlanType).is(":visible")) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/ActionPlanSummary/Evolution/" + actionPlanType,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_evolution_profitability_compliance_' + actionPlanType).loadOrUpdateChart(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		$("#tabChartEvolution").attr("data-update-required", "true");
	return false;
}

function budgetByActionPlanType(actionPlanType) {
	if (!$('#chart_budget_' + actionPlanType).length)
		return false;
	if ($('#chart_budget_' + actionPlanType).is(":visible")) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/ActionPlanSummary/Budget/" + actionPlanType,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_budget_' + actionPlanType).loadOrUpdateChart(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		$("#tabChartBudget").attr("data-update-required", "true");
	return false;
}

function summaryCharts() {
	var actionPlanTypes = $("#section_summary *[data-trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			actionPlanType = $(actionPlanTypes[i]).attr("data-trick-nav-control");
			evolutionProfitabilityComplianceByActionPlanType(actionPlanType);
			budgetByActionPlanType(actionPlanType);
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function loadChartEvolution() {
	var actionPlanTypes = $("#section_summary *[data-trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			evolutionProfitabilityComplianceByActionPlanType($(actionPlanTypes[i]).attr("data-trick-nav-control"));
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function loadChartBudget() {
	var actionPlanTypes = $("#section_summary *[data-trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			budgetByActionPlanType($(actionPlanTypes[i]).attr("data-trick-nav-control"));
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function reloadCharts() {
	chartALE();
	compliances();
	summaryCharts();
	loadChartDynamicParameterEvolution();
	loadChartDynamicAleEvolutionByAssetType();
	loadChartDynamicAleEvolutionByScenario();
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
						url : context + "/Analysis/Parameter/Risk-acceptance/form",
						type : "get",
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var $content = $("#modalRiskAcceptanceForm", new DOMParser().parseFromString(response, "text/html"));
							if (!$content.length)
								showError("Error data cannot be loaded");
							else {
								var actionDelete = function() {
									$(this).closest("tr").remove();
								};
								$("button[name='delete']", $content).on("click", actionDelete);
								$("button[name='add']", $content)
										.on(
												"click",
												function() {
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
													$range.on("input change", function() {
														$rangeInfo.text(this.value);
														this.setAttribute("title", this.value);
													});
												});

								$("input[type='range']", $content).on("input change", function() {
									$(".range-text", this.parentElement).text(this.value);
									this.setAttribute("title", this.value);
								});

								$("button[name='save']", $content).on("click", function() {
									$progress.show();
									var $table = $("table[data-trick-size]", $content), size = $table.attr("data-trick-size"), data = [];
									$("tr[data-trick-id]", $table).each(function() {
										var $this = $(this);
										data.push({
											id : $this.attr("data-trick-id"),
											value : $("input[name='value']", $this).val(),
											label : $("input[name='label']", $this).val(),
											description : $("textarea[name='description']", $this).val(),
											color : $("input[name='color']", $this).val()
										});
									});

									$.ajax({
										url : context + "/Analysis/Parameter/Risk-acceptance/Save",
										type : "post",
										data : JSON.stringify(data),
										contentType : "application/json;charset=UTF-8",
										success : function(response, textStatus, jqXHR) {
											if (response.error)
												showNotifcation('danger', response.error);
											else if (response.success) {
												$content.modal("hide");
												showNotifcation('success', response.success);
												reloadSection("section_qualitative_parameter");
											} else
												unknowError();
										},
										error : unknowError
									}).complete(function() {
										$progress.hide();
									});
								});

								$content.appendTo("#widgets").modal("show").on("hidden.bs.modal", function() {
									$content.remove();
								});
							}
						},
						error : unknowError
					}).complete(function() {
				$progress.hide();
			});
	return false;
}

function loadChartAsset() {

	if ($('#chart_ale_asset').length) {
		if ($('#chart_ale_asset').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset', response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartAsset").attr("data-update-required", "true");
	}
	if ($('#chart_ale_asset_type').length) {
		if ($('#chart_ale_asset_type').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Type/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset_type', response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartAsset").attr("data-update-required", "true");
	}
}

function loadChartScenario() {
	if ($('#chart_ale_scenario_type').length) {
		if ($('#chart_ale_scenario_type').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Scenario/Chart/Type/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario_type', response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
	}

	if ($('#chart_ale_scenario').length) {
		if ($('#chart_ale_scenario').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Scenario/Chart/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario', response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
	}
}

function loadChartDynamicParameterEvolution() {
	if ($('#chart_parameterevolution').length) {
		if ($('#chart_parameterevolution').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Dynamic/Chart/ParameterEvolution",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					$('#chart_parameterevolution').loadOrUpdateChart(response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartParameterEvolution").attr("data-update-required", "true");
	}
}

function loadChartDynamicAleEvolutionByAssetType() {
	if ($('#chart_aleevolutionbyassettype').length) {
		if ($('#chart_aleevolutionbyassettype').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Dynamic/Chart/AleEvolutionByAssetType",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_aleevolutionbyassettype', response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartAleEvolutionByAssetType").attr("data-update-required", "true");
	}
}

function loadChartDynamicAleEvolutionByScenario() {
	if ($('#chart_aleevolutionbyscenario').length) {
		if ($('#chart_aleevolutionbyscenario').is(":visible")) {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Dynamic/Chart/AleEvolutionByScenario",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_aleevolutionbyscenario', response);
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		} else
			$("#tabChartAleEvolutionByScenario").attr("data-update-required", "true");
	}
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
	$("li[data-trick-nav-control]", parentMenu).each(function() {
		if (this.getAttribute("data-trick-nav-control") == navSelected)
			this.classList.add("disabled");
		else if (this.classList.contains('disabled'))
			this.classList.remove("disabled");
	});

	$("[data-trick-nav-content]", section).each(function() {
		var $this = $(this);
		if (this.getAttribute("data-trick-nav-content") == navSelected)
			$this.show();
		else if ($this.is(":visible"))
			$this.hide();
	});
	$(window).scroll();
	return false;
}

function openTicket(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Standard/Ticketing/Open",
			type : "POST",
			async : false,
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(measures),
			success : function(response, textStatus, jqXHR) {
				var $modal = $("#modal-ticketing-view", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length) {
					$("#modal-ticketing-view").remove();
					$modal.appendTo($("#widgets")).modal("show");
					var $previous = $modal.find(".previous"), $next = $modal.find(".next"), $title = $modal.find(".modal-title");
					$next.find("a").on("click", function() {
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

					$previous.find("a").on("click", function() {
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
			error : unknowError
		}).complete(function() {
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
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "false")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#loading-indicator").show();
		$
				.ajax(
						{
							url : context + "/Analysis/Standard/Ticketing/Link",
							type : "POST",
							contentType : "application/json;charset=UTF-8",
							data : JSON.stringify(measures),
							success : function(response, textStatus, jqXHR) {
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

										taskController = function() {
											$view = $(this.getAttribute("href"));
											if (!$view.is(":visible")) {
												$taskViewer.find("fieldset:visible").hide();
												$view.show();
											}
											return false;
										}

										$tasks.appendTo($taskViewer);

										$taskContainer.find("a.list-group-item").on("click", taskController);

										$modal.on("hidden.bs.modal", function() {
											if (updateRequired)
												reloadSection("section_actionplans");
											$modal.remove();
										}).on("show.bs.modal", function() {
											$taskContainer.scrollTop();
										}).on("shown.bs.modal", function() {
											$taskContainer.on("scroll", function() {
												if (!isFinished && ($taskContainer.scrollTop() + $taskContainer.innerHeight() >= $taskContainer[0].scrollHeight)) {
													isFinished = true;
													$.ajax({
														url : context + "/Analysis/Standard/Ticketing/Load?startIndex=" + (size + 1),
														type : "POST",
														contentType : "application/json;charset=UTF-8",
														data : JSON.stringify(measures),
														success : function(response, textStatus, jqXHR) {
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
										$modal.find("#measure-container>a.list-group-item").on("click", function() {
											$view = $(this.getAttribute("href"));
											if (!$view.is(":visible")) {
												$measureViewer.find("fieldset:visible").hide();
												$view.show();
											}
											return false;
										});

										$linker.on('click', function() {
											var $measure = $measureViewer.find("fieldset:visible"), $ticket = $taskViewer.find("fieldset:visible")
											if ($measure.length && $ticket.length) {
												$progress.show();
												$linker.prop("disabled", true)
												$.ajax({
													url : context + "/Analysis/Standard/Ticketing/Link/Measure",
													type : "POST",
													contentType : "application/json;charset=UTF-8",
													data : JSON.stringify({
														"idMeasure" : $measure.attr("data-trick-id"),
														"idTicket" : $ticket.attr("data-trick-id")
													}),
													success : function(response, textStatus, jqXHR) {
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
													error : unknowError
												}).complete(function() {
													$linker.prop("disabled", false);
													$progress.hide();
												});
											}
											return false;
										});

									}
								}
							},
							error : unknowError
						}).complete(function() {
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
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});
	if (measures.length) {
		var $confirm = $("#confirm-dialog"), $question = measures.length == 1 ? MessageResolver("confirm.unlink.measure", "Are you sure, you want to unlink this measure and task")
				: MessageResolver("confirm.unlink.measures", "Are you sure, you want to unlink measures and tasks");
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function() {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Standard/Ticketing/UnLink",
				type : "POST",
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(measures),
				success : function(response, textStatus, jqXHR) {
					if (response.error)
						showDialog("#alert-dialog", response.error);
					else if (response.success) {
						if (section == "#section_actionplans")
							location.reload();
						else {
							showDialog("#info-dialog", response.success);
							if (measures.length > 30)
								reloadSection([ section.replace("#", ''), "section_actionplans" ]);
							else {
								reloadSection("section_actionplans");
								setTimeout(function() {
									var idStandard = $(section).attr("data-trick-id");
									for (var i = 0; i < measures.length; i++)
										reloadMeasureRow(measures[i], idStandard);
								}, measures.length * 20);
							}
						}
					} else
						unknowError();
				},
				error : unknowError
			}).complete(function() {
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
			url : context + "/Analysis/Standard/Ticketing/Generate",
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(data),
			success : function(response, textStatus, jqXHR) {
				if (response.error)
					showDialog("#alert-dialog", response.error);
				else if (response.success)
					application["taskManager"].Start();
				else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
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
		updates : [],
		news : []
	};
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "false")
			measures.news.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
		else
			measures.updates.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.updates.length) {
		var $confirm = $("#confirm-dialog"), $question = measures.updates.length == 1 ? MessageResolver("confirm.update.ticket", "Are you sure, you want to update measure task")
				: MessageResolver("confirm.update.tickets", "Are you sure, you want to update " + measures.updates.length + " measures tasks", [ measures.updates.length ]);
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function() {
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
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax(
				{
					url : context + "/Analysis/Standard/Ticketing/Synchronise",
					type : "POST",
					contentType : "application/json;charset=UTF-8",
					data : JSON.stringify(measures),
					success : function(response, textStatus, jqXHR) {
						var $modal = $("#modal-ticketing-synchronise", new DOMParser().parseFromString(response, "text/html"));
						if ($modal.length) {
							$("#modal-ticketing-synchronise").remove();
							$modal.appendTo($("#widgets")).modal("show");
							var $previous = $modal.find(".previous"), $next = $modal.find(".next");
							$previous.find("a").on("click", function() {
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

							$next.find("a").on("click", function() {
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
									function() {
										var $this = $(this), $parent = $this.closest("fieldset"), idMeasure = $parent.attr("data-trick-id"), className = $this
												.attr("data-trick-class"), type = className == "MaturityMeasure" ? "int" : "double";
										$this.parent().removeClass("has-error has-success");
										$.ajax({
											url : context + "/Analysis/EditField/" + className + "/" + idMeasure,
											type : "post",
											data : '{"id":' + idMeasure + ', "fieldName":"implementationRate", "value":"' + defaultValueByType($this.val(), type, true)
													+ '", "type": "' + type + '"}',
											contentType : "application/json;charset=UTF-8",
											success : function(response, textStatus, jqXHR) {
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
											error : function(jqXHR, textStatus, errorThrown) {
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
					error : unknowError
				}).complete(function() {
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
