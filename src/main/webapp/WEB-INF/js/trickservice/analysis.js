var el = null;

var table = null;

$(document).ready(function() {

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************

	$("input[type='checkbox']").removeAttr("checked");
});

function loadAnalysisSections() {

}

function findAnalysisId() {
	return $("#nav-container").attr("trick-id");
}

function isEditable(){
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
		success : function(response) {
			if (response == undefined || response !== true)
				unknowError();
			else {
				if ($(element).hasClass('glyphicon-ok'))
					$(element).removeClass('glyphicon-ok');
				else
					$(element).addClass('glyphicon-ok');
				var sections = $(element).attr("trick-section-dependency");
				if (sections != undefined)
					return reloadSection(sections.split(','));
				var callBack = $(element).attr("trick-callback");
				if (callBack != undefined)
					return eval(callBack);
				var reload = $(element).attr("trick-reload");
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
		success : function(response) {
			var element = document.createElement("div");
			$(element).html(response);
			var tag = $(element).find("tr[trick-id='" + idMeasure + "']");
			if (tag.length) {
				$("#section_standard_" + standard + " tr[trick-id='" + idMeasure + "']").replaceWith(tag);
				$("#section_standard_" + standard + " tr[trick-id='" + idMeasure + "']>td.popover-element").popover('hide');
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
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {

			if (response.standards == undefined || response.standards == null)
				return;

			var panelbody = $("#chart_compliance_body");

			$(panelbody).html("");

			$.each(response.standards, function(key, data) {

				// console.log(key);

				$(panelbody).append("<div id='chart_compliance_" + key + "'></div>");

				$('div[id="chart_compliance_' + key + '"]').highcharts(data[0]);

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
			success : function(response) {
				if (response.chart == undefined || response.chart == null)
					return;
				$('#chart_compliance_' + standard).highcharts(response);
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
			success : function(response) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_evolution_profitability_compliance_' + actionPlanType).highcharts(response);
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
			success : function(response) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_budget_' + actionPlanType).highcharts(response);
			},
			error : unknowError
		});
	} else
		$("#tabChartBudget").attr("data-update-required", "true");
	return false;
}

function summaryCharts() {
	var actionPlanTypes = $("#section_summary *[trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			actionPlanType = $(actionPlanTypes[i]).attr("trick-nav-control");
			evolutionProfitabilityComplianceByActionPlanType(actionPlanType);
			budgetByActionPlanType(actionPlanType);
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function loadChartEvolution(){
	var actionPlanTypes = $("#section_summary *[trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			evolutionProfitabilityComplianceByActionPlanType($(actionPlanTypes[i]).attr("trick-nav-control"));
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function loadChartBudget(){
	var actionPlanTypes = $("#section_summary *[trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			budgetByActionPlanType($(actionPlanTypes[i]).attr("trick-nav-control"));
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
	return false;
};

function loadChartAsset() {

	if ($('#chart_ale_asset').length) {
		if ($('#chart_ale_asset').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Ale",
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response) {
					$('#chart_ale_asset').highcharts(response);
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
				success : function(response) {
					$('#chart_ale_asset_type').highcharts(response);
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
				success : function(response) {
					$('#chart_ale_scenario_type').highcharts(response);
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
				async : true,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response) {
					$('#chart_ale_scenario').highcharts(response);
				},
				error : unknowError
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
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

function measureSortTable(element) {
	// check if datatable has to be initialised
	var tables = $(element).find("table");
	if (!tables.length)
		return false;
	// define sort order of text
	Array.AlphanumericSortOrder = 'AaÃ�Ã¡BbCcDdÃ�Ã°EeÃ‰Ã©Ä˜Ä™FfGgHhIiÃ�Ã­JjKkLlMmNnOoÃ“Ã³PpQqRrSsTtUuÃšÃºVvWwXxYyÃ�Ã½ZzÃžÃ¾Ã†Ã¦Ã–Ã¶';

	// flag to check for case sensitive comparation
	Array.AlphanumericSortIgnoreCase = true;

	// call the tablesorter plugin and apply the uitheme widget
	$(tables).tablesorter({
		headers : {
			0 : {
				sorter : false,
			}
		},
		textSorter : {
			1 : Array.AlphanumericSort,
			2 : function(a, b, direction, column, table) {
				if (table.config.sortLocaleCompare)
					return a.localeCompare(b);
				return versionComparator(a, b);
			},
			3 : $.tablesorter.sortNatural
		},
		theme : "bootstrap",
		headerTemplate : '{icon} {content}',
		widthFixed : true,
		widgets : [ "uitheme" ]
	});
}

// common

function navToogled(section, navSelected, fixedHeader) {
	var currentMenu = $("#" + section + " *[trick-nav-control='" + navSelected + "']");
	if (!currentMenu.length || $(currentMenu).hasClass("disabled"))
		return false;
	var controls = $("#" + section + " *[trick-nav-control]");
	var data = $("#" + section + " *[trick-nav-data]");

	for (var i = 0; i < controls.length; i++) {
		if ($(controls[i]).attr("trick-nav-control") == navSelected)
			$(controls[i]).addClass("disabled");
		else
			$(controls[i]).removeClass("disabled");
		if ($(data[i]).attr("trick-nav-data") != navSelected) {
			/*if (fixedHeader) {
				var table = $(data[i]).find("table");
				if (table.length && $(table).destroy != undefined)
					$(table).destroy();
			}*/
			$(data[i]).hide();
		} else {
			$(data[i]).show();
			/*if (fixedHeader) {
				var table = $(data[i]).find("table");
				if (table.length){
					$(table).stickyTableHeaders({
						cssTopOffset : ".nav-analysis",
						fixedOffset : 6
					});
				}
			}*/
		}
	}
	return false;

}

$(function() {
	Highcharts.setOptions({
		lang : {
			decimalPoint : ',',
			thousandsSep : ' '
		}
	});

});
