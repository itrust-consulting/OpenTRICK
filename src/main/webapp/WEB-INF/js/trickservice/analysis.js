var el = null;

var table = null;

var language = { // translated asynchronously below
		"label.dynamicparameter.evolution": "from {0} to {1}"
};

$(document).ready(function() {
	for (var key in language)
		language[key] = MessageResolver(key, language[key]);

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************

	$("input[type='checkbox']").removeAttr("checked");
	
	$("table.table-fixed-header-analysis").stickyTableHeaders({
		cssTopOffset : ".nav-analysis",
		fixedOffset : application.fixedOffset
	});
	
	$(".dropdown-submenu").on("hide.bs.dropdown", function(e) {
		var $target = $(e.currentTarget);
		if ($target.find("li.active").length && !$target.hasClass("active"))
			$target.addClass("active");
	});

	$('ul.nav-analysis a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
		disableEditMode();
		var target = $(e.target).attr("href");
		if ($(target).attr("data-update-required") == "true") {
			window[$(target).attr("data-trigger")].apply();
			$(target).attr("data-update-required", "false");
		}
		$("#tabOption").hide();
	});
	Highcharts.setOptions({
		lang : {
			decimalPoint : ',',
			thousandsSep : ' '
		}
	});

	// Periodically reload assessment values
	window.setInterval(function() {
		reloadSection("section_asset", undefined, true /* prevent propagation */);
		reloadSection("section_scenario", undefined, true /* prevent propagation */);
		loadChartDynamicParameterEvolution();
		loadChartDynamicAleEvolutionByAssetType();
		loadChartDynamicAleEvolutionByScenario();
	}, 30000); // every 30s
});

$.fn.loadOrUpdateChart = function(parameters) {
	$.extend(true, parameters, {
		tooltip: {
			formatter: function() {
				var str_value = this.series.yAxis.userOptions.labels.format.replace("{value}", this.point.y);
				var str = this.x + "<br/><span style=\"color:" + this.point.series.color + "\">" + this.point.series.name + ":</span>   <b>" + str_value + "</b>";

				if (this.series.options.metadata) {
					var dataIndex = this.series.xAxis.categories.indexOf(this.x);
					var metadata = this.series.options.metadata[dataIndex];
					if (metadata.length > 0)
						str += "<br/>\u00A0"; // non-breaking space; prevents empty line from being ignored
					for (var i = 0; i < metadata.length; i++)
						str += "<br/><b>" + metadata[i].dynamicParameter + "</b>: " + language["label.dynamicparameter.evolution"].replace("{0}", metadata[i].valueOld).replace("{1}", metadata[i].valueNew);
				}
				return str;
			}
		}
	});

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
		series.setData(parameters.series[i].data);
	});

	return this;
};

function findAnalysisId() {
	return $("#nav-container").attr("data-trick-id");
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

// reload measures
function reloadMeasureRow(idMeasure, standard) {
	$.ajax({
		url : context + "/Analysis/Standard/" + standard + "/SingleMeasure/" + idMeasure,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $newData = $("tr" ,$("<div/>").html(response));
			if (!$newData.length)
				$("#section_standard_" + standard + " tr[data-trick-id='" + idMeasure + "']").addClass("danger").attr("title",
						MessageResolver("error.ui.no.synchronise", "User interface does not update"));
			else {
				$(".popover").remove();
				$("#section_standard_" + standard + " tr[data-trick-id='" + idMeasure + "']").replaceWith($newData);
				$("td[data-toggle='popover']",$newData).popover("hide");
			}
		},
		error : unknowError
	});
	return false;
}

function reloadMeasureAndCompliance(standard, idMeasure) {
	reloadMeasureRow(idMeasure, standard);
	compliance(standard);
	return false;
}

// charts

function compliances() {
	$.ajax({
		url : context + "/Analysis/Standard/Compliances",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response.standards == undefined || response.standards == null)
				return;
			var $complianceBody = $("#chart_compliance_body").empty();
			$.each(response.standards, function(key, data) {
				if($complianceBody.children().length)
					$complianceBody.append("<hr class='col-xs-12' style='margin: 30px 0;'> <div id='chart_compliance_" + key + "'></div>");
				else $complianceBody.append("<div id='chart_compliance_" + key + "'></div>");
				$('div[id="chart_compliance_' + key + '"]').loadOrUpdateChart(data[0]);
			});
		},
		error : unknowError
	});
	return false;
}

function compliance(standard) {
	if (!$('#chart_compliance_' + standard).length)
		return false;
	if ($('#chart_compliance_' + standard).is(":visible")) {
		$.ajax({
			url : context + "/Analysis/Standard/" + standard + "/Compliance",
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return;
				$('#chart_compliance_' + standard).loadOrUpdateChart(response);
			},
			error : unknowError
		});
	} else
		$("#tabChartCompliance").attr("data-update-required", "true");
	return false;
}

function evolutionProfitabilityComplianceByActionPlanType(actionPlanType) {
	if (!$('#chart_evolution_profitability_compliance_' + actionPlanType).length)
		return false;
	if ($('#chart_evolution_profitability_compliance_' + actionPlanType).is(":visible")) {
		$.ajax({
			url : context + "/Analysis/ActionPlanSummary/Evolution/" + actionPlanType,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_evolution_profitability_compliance_' + actionPlanType).loadOrUpdateChart(response);
			},
			error : unknowError
		});
	} else
		$("#tabChartEvolution").attr("data-update-required", "true");
	return false;
}

function budgetByActionPlanType(actionPlanType) {
	if (!$('#chart_budget_' + actionPlanType).length)
		return false;
	if ($('#chart_budget_' + actionPlanType).is(":visible")) {
		$.ajax({
			url : context + "/Analysis/ActionPlanSummary/Budget/" + actionPlanType,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_budget_' + actionPlanType).loadOrUpdateChart(response);
			},
			error : unknowError
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

function loadChartDynamicParameterEvolution() {
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

function displayChart(id,response) {
	var $element = $(id);
	if ($.isArray(response)) {
		// First prepare the document structure so that there is exactly one <div> available for each chart
		if ($element.find(">div").length != response.length) {
			$element.empty();
			for (var i = 0; i < response.length; i++) {
				if (i > 0)
					$("<hr class='col-xs-12' style='margin: 30px 0;'>").appendTo($element);
				$("<div/>").appendTo($element)
			}
		}
		// Now load the charts themselves
		var divSelector = $element.find(">div");
		for (var i = 0; i < response.length; i++)
			$(divSelector.get(i)).loadOrUpdateChart(response[i]);
	}
	else
		$element.loadOrUpdateChart(response);
}

function loadChartAsset() {

	if ($('#chart_ale_asset').length) {
		if ($('#chart_ale_asset').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Ale",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset',response);
				},
				error : unknowError
			});
		} else
			$("#tabChartAsset").attr("data-update-required", "true");
	}
	if ($('#chart_ale_asset_type').length) {
		if ($('#chart_ale_asset_type').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Type/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset_type',response);
				},
				error : unknowError
			});
		} else
			$("#tabChartAsset").attr("data-update-required", "true");
	}
}

function loadChartScenario() {
	if ($('#chart_ale_scenario_type').length) {
		if ($('#chart_ale_scenario_type').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Scenario/Chart/Type/Ale",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario_type',response);
				},
				error : unknowError
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
	}

	if ($('#chart_ale_scenario').length) {
		if ($('#chart_ale_scenario').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Scenario/Chart/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario',response);
				},
				error : unknowError
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
	}
}

function loadChartDynamicParameterEvolution() {
	if ($('#chart_parameterevolution').length) {
		if ($('#chart_parameterevolution').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Dynamic/Chart/ParameterEvolution",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					$('#chart_parameterevolution').loadOrUpdateChart(response);
				},
				error : unknowError
			});
		} else
			$("#tabChartParameterEvolution").attr("data-update-required", "true");
	}
}

function loadChartDynamicAleEvolutionByAssetType() {
	if ($('#chart_aleevolutionbyassettype').length) {
		if ($('#chart_aleevolutionbyassettype').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Dynamic/Chart/AleEvolutionByAssetType",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_aleevolutionbyassettype',response);
				},
				error : unknowError
			});
		} else
			$("#tabChartAleEvolutionByAssetType").attr("data-update-required", "true");
	}
}

function loadChartDynamicAleEvolutionByScenario() {
	if ($('#chart_aleevolutionbyscenario').length) {
		if ($('#chart_aleevolutionbyscenario').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Dynamic/Chart/AleEvolutionByScenario",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_aleevolutionbyscenario',response);
				},
				error : unknowError
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
	var currentMenu = $("li[data-trick-nav-control='" + navSelected + "']",parentMenu);
	if (!currentMenu.length || $(currentMenu).hasClass("disabled"))
		return false;
	$("li[data-trick-nav-control]",parentMenu).each(function() {
		var $this = $(this);
		if ($this.attr("data-trick-nav-control") == navSelected)
			$this.addClass("disabled");
		else if($this.hasClass('disabled'))
			$this.removeClass("disabled");
	});

	$("[data-trick-nav-content]",section).each(function() {
		var $this = $(this);
		if ($this.attr("data-trick-nav-content") == navSelected)
			$this.show();
		else if($this.is(":visible"))
			$this.hide();
	});
	$(window).scroll();
	return false;
}

