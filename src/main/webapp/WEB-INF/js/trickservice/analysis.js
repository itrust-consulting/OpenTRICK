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

	Highcharts.setOptions({
		lang : {
			decimalPoint : ',',
			thousandsSep : ' '
		}
	});
});

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
	var $currentRow = $("#section_standard_" + standard + " tr[data-trick-id='" + idMeasure + "']")
	if (!$currentRow.find("input[type!='checkbox'],select,textarea").length) {
		$.ajax({
			url : context + "/Analysis/Standard/" + standard + "/SingleMeasure/" + idMeasure,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $newRow = $("tr", $("<div/>").html(response));
				if (!$newRow.length)
					$currentRow.addClass("warning").attr("title", MessageResolver("error.ui.no.synchronise", "User interface does not update"));
				else {
					$("[data-toggle='popover']", $currentRow).popover('destroy');
					$("[data-toggle='tooltip']", $currentRow).tooltip('destroy');
					$currentRow.replaceWith($newRow);
					$("[data-toggle='popover']", $newRow).popover().on('show.bs.popover', togglePopever);
					$("[data-toggle='tooltip']", $newRow).tooltip();
				}
			},
			error : unknowError
		});
	} else
		$currentRow.attr("data-force-callback", true).addClass("warning").attr("title",
				MessageResolver("error.ui.update.wait.editing", "Data was saved but user interface was not updated, it will be updated after edition"));
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
				if ($complianceBody.children().length)
					$complianceBody.append("<hr class='col-xs-12' style='margin: 30px 0;'> <div id='chart_compliance_" + key + "'></div>");
				else
					$complianceBody.append("<div id='chart_compliance_" + key + "'></div>");
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
			success : function(response, textStatus, jqXHR) {
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
			success : function(response, textStatus, jqXHR) {
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
			success : function(response, textStatus, jqXHR) {
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
	return false;
};

function displayChart(id, response) {
	var $element = $(id);
	if ($.isArray(response)) {
		$element.empty();
		for (var i = 0; i < response.length; i++) {
			if (i == 0)
				$("<div/>").appendTo($element).highcharts(response[i]);
			else {
				$("<hr class='col-xs-12' style='margin: 30px 0;'>").appendTo($element);
				$("<div></div>").appendTo($element).highcharts(response[i]);
			}
		}
	} else
		$element.highcharts(response);
}

function loadChartAsset() {

	if ($('#chart_ale_asset').length) {
		if ($('#chart_ale_asset').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset', response);
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
					displayChart('#chart_ale_asset_type', response);
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
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario_type', response);
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
					displayChart('#chart_ale_scenario', response);
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
		$.ajax({
			url : context + "/Analysis/Standard/Ticketing/Open",
			type : "POST",
			async : false,
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(measures),
			success : function(response, textStatus, jqXHR) {
				var $modal = $("#modal-ticketing-view", new DOMParser().parseFromString(response, "text/html"));
				if (!$modal.length)
					unknowError();
				else
					$modal.appendTo($("#widgets")).modal("show");
			},
			error : unknowError
		});
	}
	return false;
	return false;
}

function linkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	return false;
}

function generateTickets(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "false")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		$.ajax({
			url : context + "/Analysis/Standard/Ticketing/Generate",
			type : "POST",
			async : false,
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(measures),
			success : function(response, textStatus, jqXHR) {
				if (response.error)
					unknowError(response.error);
				else if (response.success) {
					if (section == "#section_actionplans" || measures.length > 10)
						location.reload();
					else {
						var $infoDialog = $("#info-dialog");
						$(".modal-body", $infoDialog).html(response.success);
						$infoDialog.modal("show");
						reloadSection("section_actionplans");
						setTimeout(function() {
							var idStandard = $(section).attr("data-trick-id");
							for (var i = 0; i < measures.length; i++)
								reloadMeasureRow(measures[i], idStandard);
						}, measures.length * 20);
					}
				} else
					unknowError();
			},
			error : unknowError
		});
	}
	return false;
}

function synchroniseWithTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
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
