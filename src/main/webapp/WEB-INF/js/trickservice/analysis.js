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
});

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
				$("#section_measure_" + norm + " tr[trick-id='" + idMeasure + "']>td.popover-element").popover('hide');
			}
		},
		error : unknowError
	});
	return false;
}

function reloadMeausreAndCompliance(norm, idMeasure) {
	reloadMeasureRow(idMeasure, norm);
	compliance(norm);
	return false;
}

// charts

function compliances() {
	$.ajax({
		url : context + "/Measure/Compliances",
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {

			if (response.norms == undefined || response.norms == null)
				return;
			
			var panelbody = $("#chart_compliance .panel-body");
			
			$(panelbody).html("");
			
			$.each(response.norms, function (key, data) {
			    //console.log(key); 
			    
			    $(panelbody).append("<div id='chart_compliance_"+ key +"'></div>");
			    
				$('#chart_compliance_' + key).highcharts(data[0]);
			    
			});
			
		},
		error : unknowError
	});
	return false;
}

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
		},
		error : unknowError
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
		},
		error : unknowError
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
		},
		error : unknowError
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
	compliances();
	// compliance('27001');
	// compliance('27002');
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
			},
			error : unknowError
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
			},
			error : unknowError
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
			},
			error : unknowError
		});
	}
	if ($('#chart_ale_asset_type').length) {
		$.ajax({
			url : context + "/Asset/Chart/Type/Ale",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				$('#chart_ale_asset_type').highcharts(response);
			},
			error : unknowError
		});
	}
	return false;
}

// add new standard to analysis

function manageStandard() {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		$.ajax({
			url : context + "/Analysis/Add/Standard",
			type : "get",
			async : false,
			success : function(response) {
				if (response["error"] != undefined)
					showError($("#addStandardModal .modal-body")[0], response["error"]);
				else {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					var forms = $(doc).find("#addStandardModal");
					if (!forms.length) {
						showError($("#addStandardModal .modal-body")[0], MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data"));
					} else {
						$("#addStandardModal").replaceWith(forms);
						$("#addStandardModal").modal("toggle");
						$("#addStandardModal").find("a[role='remove-standard']").click(function() {
							if ($(this).is(":disabled"))
								return false;
							var modal = new Modal($("#confirm-dialog").clone(), MessageResolver("confirm.delete.analysis.norm", "Are you sure, you want to remove this standard from this analysis?"));
							var selectedNorm = this;
							$(modal.modal_footer).find("button[name='yes']").click(function() {
								if ($(selectedNorm).is(":disabled"))
									return false;
								enableButtonSaveStandardState(false);
								$.ajax({
									url : context + "/Analysis/Delete/Standard/" + $(selectedNorm).attr("trick-id"),
									type : "get",
									async : false,
									contentType : "application/json;charset=UTF-8",
									success : function(response) {
										if (response["error"] != undefined) {
											enableButtonSaveStandardState(true);
											showError($("#addStandardModal .modal-footer")[0], response["error"]);
										} else if (response["success"] != undefined) {
											showSuccess($("#addStandardModal .modal-footer")[0], response["success"]);
											location.reload();
											setTimeout(function() {
												$("#addStandardModal").modal("hide");
											}, 10000);
										} else
											unknowError();
									},
									error : unknowError
								});
							});
							modal.Show();
						});
					}
				}
			},
			error : unknowError
		});
		enableButtonSaveStandardState(true);
	} else
		permissionError();
	return false;
}

function enableButtonSaveStandardState(state) {
	if (!($("#btn_save_standard").length || $("#addStandardModal").find("a[role='remove-standard']").length))
		return false;
	$("#addStandardModal .alert").remove();
	if (!state)
		$("#add_standard_progressbar").show();
	else
		$("#add_standard_progressbar").hide();

	$("#btn_save_standard").prop("disabled", !state);
	$("#addStandardModal").find("a[role='remove-standard']").prop("disabled", !state);

	if (state) {
		$("#btn_save_standard").removeClass("disabled");
		$("#addStandardModal").find("a[role='remove-standard']").removeClass("disabled");
	} else {
		$("#btn_save_standard").addClass("disabled");
		$("#addStandardModal").find("a[role='remove-standard']").addClass("disabled");
	}
	return false;
}

function saveStandard(form) {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		if ($("#btn_save_standard").is(":disabled"))
			return false;
		enableButtonSaveStandardState(false);
		var normId = $("#" + form + " select").val();
		$.ajax({
			url : context + "/Analysis/Save/Standard/" + normId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					enableButtonSaveStandardState(true);
					showError($("#addStandardModal .modal-footer")[0], response["error"]);
				} else if (response["success"] != undefined) {
					showSuccess($("#addStandardModal .modal-footer")[0], response["success"]);
					location.reload();
					setTimeout(function() {
						$("#addStandardModal").modal("hide");
					}, 10000);
				}
			},
			error : unknowError
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
