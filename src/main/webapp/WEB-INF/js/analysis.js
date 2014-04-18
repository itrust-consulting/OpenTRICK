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

	$("div[class='panel-body panelbodydefinition']").click(function() {

		
		if (($(this).find("div.fht-table-wrapper")).length <= 0) {
			initialisefixheadertables($(this).find("table:visible"));
		}	
		
		

	});

	$("div[class='panel-body panelbodydefinition']").scroll(function() {

		if (($(this).find("div.fht-table-wrapper")).length <= 0) {
			initialisefixheadertables($(this).find("table:visible"));
		}
	});

	// ******************************************************************************************************************
	// * measure description in popover
	// ******************************************************************************************************************

	initmeasuredescriptionpopover();

});
function loadDefaultParameter(idAnalysis) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Parameter/Load/Default",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["success"] != undefined) {
					$("#alert-dialog .modal-body").html(response["success"]);
					setTimeout(function() {
						location.reload();
					}, 3000);
				} else if (response["error"] != undefined)
					$("#alert-dialog .modal-body").html(response["error"]);
				else
					$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");

				return true;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
			}
		});
	} else
		permissionError();
	return false;
}
function loadDefaultRiskInformation(idAnalysis) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/RiskInformation/Load/Default",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["success"] != undefined) {
					$("#alert-dialog .modal-body").html(response["success"]);
					setTimeout(function() {
						location.reload();
					}, 3000);
				} else if (response["error"] != undefined)
					$("#alert-dialog .modal-body").html(response["error"]);
				else
					$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
				return true;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
			}
		});
	} else
		permissionError();
	return false;
}

function initialisefixheadertables(parent) {

	// console.log(parent);
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

		$("div[class='panel-body panelbodydefinition'] div[class='fht-tbody']").scroll(function() {
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
	$(document).on("click",".descriptiontooltip",function() {
		
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
			container : ".panelbodydefinition"
		}).popover('toggle');

		// avoid scroll top
		return false;
	});

	// when table is scrolled, hide popover
	$("div[class='panel-body panelbodydefinition']").scroll(function() {
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
		async : true,
		success : function(response) {
			
			var tag = response.substring(response.indexOf('<'));
			
			if ($(tag).attr("trick-id")==idMeasure) {
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

// phase

function extractPhase(that) {
	var phases = $("#section_phase *[trick-class='Phase']>*:nth-child(2)");
	if (!$(phases).length)
		return true;
	that.choose.push("NA");
	for (var i = 0; i < phases.length; i++)
		that.choose.push($(phases[i]).text());
	return false;
}

// assassments

function computeAssessment() {
	$.ajax({
		url : context + "/Assessment/Update",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response['error'] != undefined) {
				$("#info-dialog .modal-body").text(response['error']);
				$("#info-dialog").modal("toggle");
			} else if (response['success'] != undefined) {
				$("#info-dialog .modal-body").text(response['success']);
				$("#info-dialog").modal("toggle");
				chartALE();
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			return false;
		},
	});
	return false;
}

function wipeAssessment() {
	$.ajax({
		url : context + "/Assessment/Wipe",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response['error'] != undefined) {
				$("#info-dialog .modal-body").text(response['error']);
				$("#info-dialog").modal("toggle");
			} else if (response['success'] != undefined) {
				$("#info-dialog .modal-body").text(response['success']);
				$("#info-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			return false;
		},
	});
	return false;
}

function updateAssessmentAcronym(idParameter, acronym) {
	$.ajax({
		url : context + "/Assessment/Update/Acronym/" + idParameter + "/" + acronym,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog").modal("toggle");
				setTimeout("updateALE()", 2000);
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return true;
		}
	});
	return false;
}

// field edit

function saveField(element, controller, id, field, type) {
	if ($(element).prop("value") != $(element).prop("placeholder")) {
		$.ajax({
			url : context + "/editField/" + controller,
			type : "post",
			async : true,
			data : '{"id":' + id + ', "fieldName":"' + field + '", "value":"' + defaultValueByType($(element).prop("value"), type, true) + '", "type": "' + type + '"}',
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response == "" || response == null) {
					updateFieldValue(element, $(element).prop("value"));
					return false;
				}
				bootbox.alert(jqXHR.responseText);
				updateFieldValue(element, $(element).prop("placeholder"));
				return true;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				bootbox.alert(jqXHR.responseText);
				updateFieldValue(element, $(element).prop("placeholder"));
			},
		});
	} else {
		updateFieldValue(element, $(element).prop("placeholder"));
		return false;
	}
}

function editField(element, controller, id, field, type) {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		var fieldEditor = null;
		if (controller == null || controller == undefined)
			controller = FieldEditor.prototype.__findControllor(element);
		if (controller == "ExtendedParameter")
			fieldEditor = new ExtendedFieldEditor(element);
		else if (controller == "Assessment") {
			field = $(element).attr("trick-field");
			var fieldImpact = [ "impactRep", "impactLeg", "impactOp", "impactFin" ];
			var fieldProba = "likelihood";
			if (fieldImpact.indexOf(field) != -1)
				fieldEditor = new AssessmentImpactFieldEditor(element);
			else if (field == fieldProba)
				fieldEditor = new AssessmentProbaFieldEditor(element);
			else
				fieldEditor = new AssessmentFieldEditor(element);
		} else if (controller == "MaturityMeasure")
			fieldEditor = new MaturityMeasureFieldEditor(element);
		else
			fieldEditor = new FieldEditor(element);

		if (!fieldEditor.Initialise())
			fieldEditor.Show();
	} else
		permissionError();
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
						if ($("#addStandardModal").length)
							$("#addStandardModal").remove();
						$(forms).appendTo($("#widget"));
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

$(function(){
	Highcharts.setOptions({
		lang : {
			decimalPoint : ',',
			thousandsSep : ' '
		}
	});
	
	if ($("#addPhaseModel").length) {
		
		var l_lang;
		if (navigator.userLanguage) // Explorer
			l_lang = navigator.userLanguage;
		else if (navigator.language) // FF
			l_lang = navigator.language;
		else
			l_lang = "en";

		if (l_lang == "en-US") {
			l_lang = "en";
		}
		
		if (l_lang != "en")
			$.getScript(context + "/js/locales/bootstrap-datepicker." + l_lang + ".js");
		$('#addPhaseModel').on('show.bs.modal', function() {
			var lastDate = $("#section_phase td").last();
			if (lastDate.length) {
				var beginDate = lastDate.text();
				if (beginDate.match("\\d{4}-\\d{2}-\\d{2}")) {
					var endDate = beginDate.split("-");
					endDate[0]++;
					$("#addPhaseModel #phase_begin_date").prop("value", beginDate);
					$("#addPhaseModel #phase_endDate").prop("value", endDate[0] + "-" + endDate[1] + "-" + endDate[2]);
				}
			}
			$("#addPhaseModel input").datepicker({
				format : "yyyy-mm-dd",
				language : l_lang
			});
		});
	}
});