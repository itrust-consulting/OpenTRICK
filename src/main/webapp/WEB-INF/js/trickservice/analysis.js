var el = null;

var table = null;

$(document).ready(function() {

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************
	reloadCharts();

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************

	$("input[type='checkbox']").removeAttr("checked");

	// ******************************************************************************************************************
	// * fixed header tables
	// ******************************************************************************************************************

	/*
	 * $("div.autofitpanelbodydefinition").click(function() {
	 * 
	 * if ($(this).outerHeight() >= 600) {
	 * 
	 * if (!$(this).hasClass("panelbodydefinition"))
	 * $(this).addClass("panelbodydefinition");
	 * 
	 * if (($(this).find("div.fht-table-wrapper")).length <= 0) {
	 * initialisefixheadertables($(this).find("table:visible")); } } });
	 * 
	 * $("div.autofitpanelbodydefinition").scroll(function() {
	 * 
	 * if ($(this).outerHeight() >= 600) {
	 * 
	 * if (!$(this).hasClass("panelbodydefinition"))
	 * $(this).addClass("panelbodydefinition");
	 * 
	 * if (($(this).find("div.fht-table-wrapper")).length <= 0) {
	 * initialisefixheadertables($(this).find("table:visible")); } } });
	 */

	// ******************************************************************************************************************
	// * measure description in popover
	// ******************************************************************************************************************
	initmeasuredescriptionpopover();

});

function initialisefixheadertables(parent) {

	if (parent.length !== 0) {
		// initialise fixedheader table with parameters
		$(parent).fixedHeaderTable("destroy");
		var parentt = $(parent).parent();
		$(parentt).find("table:visible").not("[id]").remove();
		$(parent).fixedHeaderTable({
			footer : false,
			cloneHeadToFoot : false,
			fixedColumn : false,
			width : "100%",
			themeClass : 'table table-hover'
		});

		$(parentt).find("div[class='fht-tbody']").scroll(function() {
			// console.log("ohe");

			// check if a popover is active
			if (el != null) {

				// hide popover
				el.popover('hide');

				// remove popover from dom (to avoid unclickable references)
				$('.popover').remove();

				// set watcher to null (no popover is active)
				el = null;
			}
		});

		// first data row has wrong margin top
		// $('.headertofixtable').css("margin-top", "-49px");

		// remove small scrolling which causes scrolling inside panel
		$('div [class="fht-table-wrapper table table-hover"]').css("margin", "0");
		$('div [class="fht-table-wrapper table table-hover"]').css("padding", "0");
	}
}

function initmeasuredescriptionpopover() {
	// tooltip / popover click on reference
	$(document).on("click", ".descriptiontooltip", function() {

		// check if the same reference had been clicked to hide
		if (el != null && el.attr("data-original-title") != $(this).attr("data-original-title")) {

			// hide opened popover
			el.popover("hide");

			// set watcher to null (no popover active)
			el = null;
		}

		// set current popover as active
		el = $(this);

		// initialise popover and toggle visablility
		$(this).popover({
			trigger : 'manual',
			placement : 'bottom',
			html : true,
			container : ".autofitpanelbodydefinition"
		}).popover('toggle');

		// avoid scroll top
		return false;
	});

	// when table is scrolled, hide popover
	$("div[class='autofitpanelbodydefinition']").scroll(function() {
		// console.log("ohe");
		// check if a popover is active
		if (el != null) {

			// hide popover
			el.popover('hide');

			// remove popover from dom (to avoid unclickable references)
			$('.popover').remove();

			// set watcher to null (no popover is active)
			el = null;
		}
	});
}

// reload measures

function reloadMeasureRow(idMeasure, norm) {
	$.ajax({
		url : context + "/Measure/SingleMeasure/" + idMeasure,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var element = document.createElement("div");
			$(element).html(response);
			var tag = $(element).find("tr[trick-id='" + idMeasure + "']");
			if (tag.length) {
				$("#section_measure_" + norm + " tr[trick-id='" + idMeasure + "']").replaceWith(tag);
				var popover = $(tag).find("a.descriptiontooltip");
				$(popover).popover({
					trigger : 'manual',
					placement : 'bottom',
					html : true,
					container : ".panelbodydefinition"
				});
				popover.popover("hide");
				$('.popover').remove();
			}
		}
	});
	return false;
}

function reloadMeausreAndCompliance(norm, idMeasure) {
	reloadMeasureRow(idMeasure, norm);
	compliance(norm);
	return false;
}

// charts

function compliance(norm) {
	if (!$('#chart_compliance_' + norm).length)
		return false;
	$.ajax({
		url : context + "/Measure/Compliance/" + norm,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response.chart == undefined || response.chart == null)
				return;
			$('#chart_compliance_' + norm).highcharts(response);
		}
	});
	return false;
}

function evolutionProfitabilityComplianceByActionPlanType(actionPlanType) {
	if (!$('#chart_evolution_profitability_compliance_' + actionPlanType).length)
		return false;
	return $.ajax({
		url : context + "/ActionPlanSummary/Evolution/" + actionPlanType,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response.chart == undefined || response.chart == null)
				return true;
			$('#chart_evolution_profitability_compliance_' + actionPlanType).highcharts(response);
		}
	});
}

function budgetByActionPlanType(actionPlanType) {
	if (!$('#chart_budget_' + actionPlanType).length)
		return false;
	return $.ajax({
		url : context + "/ActionPlanSummary/Budget/" + actionPlanType,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response.chart == undefined || response.chart == null)
				return true;
			$('#chart_budget_' + actionPlanType).highcharts(response);
		}
	});
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

function reloadCharts() {
	chartALE();
	compliance('27001');
	compliance('27002');
	summaryCharts();
	return false;
};

function reloadActionPlansAndCharts() {
	reloadSection('section_actionplans');
}

function chartALE() {
	if ($('#chart_ale_scenario_type').length) {
		$.ajax({
			url : context + "/Scenario/Chart/Type/Ale",
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response) {
				$('#chart_ale_scenario_type').highcharts(response);
			}
		});
	}
	if ($('#chart_ale_scenario').length) {
		$.ajax({
			url : context + "/Scenario/Chart/Ale",
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response) {
				$('#chart_ale_scenario').highcharts(response);
			}
		});
	}

	if ($('#chart_ale_asset').length) {
		$.ajax({
			url : context + "/Asset/Chart/Ale",
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response) {
				$('#chart_ale_asset').highcharts(response);
			}
		});
	}
	if ($('#chart_ale_asset_type').length) {
		$.ajax({
			url : context + "/Asset/Chart/Type/Ale",
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response) {
				$('#chart_ale_asset_type').highcharts(response);
			}
		});
	}
	return false;
}

// add new standard to analysis

function addStandard() {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		enableButtonSaveStandardState(true);
		$.ajax({
			url : context + "/Analysis/Add/Standard",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined)
					showError($("#addStandardModal .modal-body")[0], response["error"]);
				else {
					var forms = $.parseHTML(response);
					if (!$(forms).find("#addStandardForm").length) {
						showError($("#addStandardModal .modal-body")[0], MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data"));
					} else {
						$("#addStandardModal").replaceWith($(forms));
						enableButtonSaveStandardState(true);
						$("#addStandardModal").modal("toggle");
					}
				}
			}
		});
	} else
		permissionError();
	return false;
}

function enableButtonSaveStandardState(state) {
	if (!$("#btn_save_standard").length)
		return false;
	if ($("#addStandardModal .alert").length)
		$("#addStandardModal .alert").remove();
	if (!state)
		$("#add_standard_progressbar").show();
	else
		$("#add_standard_progressbar").hide();
	$("#btn_save_standard").prop("disabled", !state);
	return false;
}

function saveStandard(form) {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		enableButtonSaveStandardState(false);
		var normId = $("#" + form + " select").val();
		$.ajax({
			url : context + "/Analysis/Save/Standard/" + normId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					enableButtonSaveStandardState(true);
					showError($("#addStandardModal .modal-body")[0], response["error"]);
				} else if (response["success"] != undefined) {
					showSuccess($("#addStandardModal .modal-body")[0], response["success"]);
					location.reload();
					setTimeout(function() {
						$("#addStandardModal").modal("toggle");
					}, 10000);
				}
			}
		});
	} else
		permissionError();
	return false;
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
			if (fixedHeader) {
				var table = $(data[i]).find("table");
				if (table.length)
					$(table).floatThead("destroy");
			}
			$(data[i]).hide();
		} else {
			$(data[i]).show();
			if (fixedHeader) {
				var table = $(data[i]).find("table");
				if (table.length)
					fixedTableHeader(table);
			}
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
