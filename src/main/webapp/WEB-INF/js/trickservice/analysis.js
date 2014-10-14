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
		url : context + "/Measure/SingleMeasure/" + idMeasure,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var element = document.createElement("div");
			$(element).html(response);
			var tag = $(element).find("tr[trick-id='" + idMeasure + "']");
			if (tag.length) {
				$("#section_measure_" + standard + " tr[trick-id='" + idMeasure + "']").replaceWith(tag);
				$("#section_measure_" + standard + " tr[trick-id='" + idMeasure + "']>td.popover-element").popover('hide');
			}
		},
		error : unknowError
	});
	return false;
}

function reloadMeausreAndCompliance(standard, idMeasure) {
	reloadMeasureRow(idMeasure, standard);
	compliance(standard);
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

			if (response.standards == undefined || response.standards == null)
				return;

			var panelbody = $("#chart_compliance .panel-body");

			$(panelbody).html("");

			$.each(response.standards, function(key, data) {
				// console.log(key);

				$(panelbody).append("<div id='chart_compliance_" + key + "'></div>");

				$('#chart_compliance_' + key).highcharts(data[0]);

			});

		},
		error : unknowError
	});
	return false;
}

function compliance(standard) {
	if (!$('#chart_compliance_' + standard).length)
		return false;
	$.ajax({
		url : context + "/Measure/Compliance/" + standard,
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
			url : context + "/Analysis/Standard/Manage",
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
							var selectedStandard = this;
							$(modal.modal_footer).find("button[name='yes']").click(function() {
								if ($(selectedStandard).is(":disabled"))
									return false;
								enableButtonSaveStandardState(false);
								$.ajax({
									url : context + "/Analysis/Standard/Delete/" + $(selectedStandard).attr("trick-id"),
									type : "get",
									async : false,
									contentType : "application/json;charset=UTF-8",
									success : function(response) {
										if (response["error"] != undefined) {
											enableButtonSaveStandardState(true);
											showError($("#addStandardModal .modal-footer")[0], response["error"]);
										} else if (response["success"] != undefined) {
											showSuccess($("#addStandardModal .modal-footer")[0], response["success"]);
											$("#addStandardModal .modal-footer div[class='alert alert-success']").css("margin-bottom", "0");
											setTimeout(function() {
												$("#addStandardModal").modal("hide");
												$("div[class='modal-backdrop fade in']").remove();
												manageStandard();
												reloadSection("section_measure");
											}, 3000);
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
		$("#add_standard_progressbar").css("display", "inline-block");
	else
		$("#add_standard_progressbar").css("display", "none");

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

function saveAnalysisStandard(form) {
	$("#addStandardModel #group_2 #addstandardbutton").prop("disabled", false);
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		$.ajax({
			url : context + "/Analysis/Standard/Create",
			type : "post",
			data : serializeForm(form),
			contentType : "application/json;charset=UTF-8",
			success : function(response) {

				$("#addStandardModel #group_2 #addstandardbutton").prop("disabled", false);
				var alert = $("#addStandardModel #group_2 .label-danger");
				if (alert.length)
					alert.remove();
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");

					$(errorElement).text(response[error]);
					switch (error) {
					case "label":
						$(errorElement).appendTo($("#group_2 #standard_form #standard_label").parent());
						break;
					case "version":
						$(errorElement).appendTo($("#group_2 #standard_form #standard_version").parent());
						break;

					case "description":
						$(errorElement).appendTo($("#group_2 #standard_form #standard_description").parent());
						break;

					case "standard":
						$(errorElement).appendTo($("#group_2 #standard_form .modal-body"));
						showError($("#addStandardModal .modal-footer")[0], response["error"]);
						break;
					}
				}
				if (!$("#addStandardModel #group_2 .label-danger").length) {
					showSuccess($("#addStandardModal .modal-footer")[0], response["success"]);
					$("#addStandardModal .modal-footer div[class='alert alert-success']").css("margin-bottom", "0");
					setTimeout(function() {
						$("#addStandardModal").modal("hide");
						$("div[class='modal-backdrop fade in']").remove();
						manageStandard();
						$("#managestandardtabs a#group_2").tab("show");
						reloadSection("section_measure");
					}, 3000);
				}
				return false;

			},
			error : function(jqXHR, textStatus, errorThrown) {
				var alert = $("#addStandardModel .label-danger");
				if (alert.length)
					alert.remove();
				$("#addStandardModel #group_2 #addstandardbutton").prop("disabled", false);
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(MessageResolver("error.unknown.save.norm", "An unknown error occurred during saving standard"));
				$(errorElement).appendTo($("#addStandardModel .modal-body"));
			},
		});
	} else
		permissionError();
	return false;
}

function saveStandard(form) {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		if ($("#btn_save_standard").is(":disabled"))
			return false;
		enableButtonSaveStandardState(false);
		var idStandard = $("#" + form + " select").val();
		$.ajax({
			url : context + "/Analysis/Standard/Save/" + idStandard,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					enableButtonSaveStandardState(true);
					showError($("#addStandardModal .modal-footer")[0], response["error"]);
				} else if (response["success"] != undefined) {
					showSuccess($("#addStandardModal .modal-footer")[0], response["success"]);
					$("#addStandardModal .modal-footer div[class='alert alert-success']").css("margin-bottom", "0");
					setTimeout(function() {
						$("#addStandardModal").modal("hide");
						$("div[class='modal-backdrop fade in']").remove();
						manageStandard();
						reloadSection("section_measure");
					}, 3000);
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
