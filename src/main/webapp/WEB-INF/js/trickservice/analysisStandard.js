// add new standard to analysis

$(document).ready(function() {
	$('#standardModal').on('hidden.bs.modal', function() {
		reloadSection("section_standard");
	})
});

function isAnalysisOnlyStandard(section) {
	var selectedStandard = $(section + " tbody :checked").parent().parent().attr("trick-analysisOnly");
	if (selectedStandard === "true")
		return true;
	else
		return false;
}

function manageStandard() {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	var locale = $("#nav-container").attr("trick-language");
	var alert = $("#standardModal .alert");
	if (alert.length)
		alert.remove();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Manage",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(response) {
				if (response["error"] != undefined)
					showError($("#standardModal .modal-body")[0], response["error"]);
				else {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					var forms = $(doc).find("#section_manage_standards");
					if (!forms.length) {
						showError($("#standardModal .modal-footer")[0], MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data", null, locale));
					} else {
						$("#section_manage_standards").replaceWith(forms);

					}
				}
				$("#standardModal").modal("show");
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

// manage analysis standards

function createStandard() {

	var selectedItem = $("#section_manage_standards tbody :checked").parent().parent();
	if (selectedItem.length != 0)
		return false;

	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {

		var alert = $("#createStandardModal .label-danger");
		if (alert.length)
			alert.remove();

		var locale = $("#nav-container").attr("trick-language");

		$("#createStandardModal #createstandardbutton").prop("disabled", false);
		$("#createStandardModal #standard_label").prop("value", "");
		$("#createStandardModal #standard_version").prop("value", "");
		$("#createStandardModal #standard_description").prop("value", "");
		$("#createStandardModal #standard_form input[name='type'][value='NORMAL']").prop("checked", "checked");
		$("#createStandardModal #standard_computable").removeProp("checked");
		$("#createStandardModal #createstandardtitle").text(MessageResolver("label.title.analysis.manage_standard.create", "Create new standard", null, locale));
		$("#createstandardbutton").text(MessageResolver("label.action.create", "Create", null, locale));
		$("#createstandardbutton").attr("onclick", "doCreateStandard('standard_form')");
		$("#createStandardModal").modal('show');

	} else
		permissionError();
	return false;
}

function doCreateStandard(form) {
	$("#createStandardModal #createstandardbutton").prop("disabled", true);
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Create",
			type : "post",
			data : serializeForm(form),
			contentType : "application/json;charset=UTF-8",
			success : function(response) {

				$("#createStandardModal #createstandardbutton").prop("disabled", false);
				var alert = $("#createStandardModal .label-danger");
				if (alert.length)
					alert.remove();
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");

					$(errorElement).text(response[error]);
					switch (error) {
					case "label":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_label").parent());
						break;
					case "description":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_description").parent());
						break;
					case "standard":
						showError($("#createStandardModal .modal-footer")[0], response["error"]);
						$("#createStandardModal .modal-footer div[class='alert alert-danger']").css("margin-bottom", "0");
						break;
					}
				}
				if (!$("#createStandardModal .label-danger").length) {
					// showSuccess($("#createStandardModal .modal-footer")[0],
					// response["success"]);
					// $("#createStandardModal .modal-footer div[class='alert
					// alert-success']").css("margin-bottom", "0");
					// reloadSection("section_standard");
					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response) {
							var parser = new DOMParser();
							var doc = parser.parseFromString(response, "text/html");
							$("#section_manage_standards table.table").replaceWith($(doc).find("#section_manage_standards table.table"));
							updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
						},
						error : unknowError
					});
					$("#createStandardModal").modal("hide");
				}
				return false;

			},
			error : function() {
				unknowError();
				$("#createStandardModal #createstandardbutton").prop("disabled", false);
			}
		});
	} else
		permissionError();
	return false;
}

// manage analysis standards

function editStandard(standardrowobject) {

	var selectedItem = undefined;

	if (standardrowobject == undefined || standardrowobject == null) {

		selectedItem = $("#section_manage_standards tbody :checked").parent().parent();
		if (selectedItem.length != 1)
			return false;
	} else {
		selectedItem = standardrowobject;
	}
	var canbeedited = $(selectedItem).attr("trick-analysisOnly");

	if (canbeedited === "true") {

		var idItem = $(selectedItem).attr("trick-id");

		var idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
		if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {

			var alert = $("#createStandardModal .label-danger");
			if (alert.length)
				alert.remove();

			var locale = $("#nav-container").attr("trick-language");

			var label = $(selectedItem).find("td:nth-child(2)").text();
			var description = $(selectedItem).find("td:nth-child(4)").text();
			var type = $(selectedItem).attr("trick-type");
			var computable = $(selectedItem).attr("trick-computable");

			$("#createStandardModal #createstandardbutton").prop("disabled", false);
			$("#createStandardModal #standard_label").prop("value", label);
			$("#createStandardModal #standard_description").prop("value", description);
			$("#createStandardModal #standard_form input[name='type']").removeProp("checked");
			$("#createStandardModal #standard_form input[name='type'][value='" + type + "']").prop("checked", true);
			if (computable === "true")
				$("#createStandardModal #standard_computable").prop("checked", true);
			else
				$("#createStandardModal #standard_computable").removeProp("checked");

			$("#createStandardModal #createstandardtitle").text(MessageResolver("label.title.analysis.manage_standard.edit", "Edit standard", null, locale));
			$("#createstandardbutton").text(MessageResolver("label.action.edit", "Edit", null, locale));
			$("#createstandardbutton").attr("onclick", "doEditStandard('standard_form')");
			$("#createStandardModal").modal('show');

		} else
			permissionError();
	}

	return false;
}

function doEditStandard(form) {
	$("#createStandardModal #createstandardbutton").prop("disabled", true);
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Save",
			type : "post",
			data : serializeForm(form),
			contentType : "application/json;charset=UTF-8",
			success : function(response) {

				$("#createStandardModal #createstandardbutton").prop("disabled", false);
				var alert = $("#createStandardModal .label-danger");
				if (alert.length)
					alert.remove();
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");

					$(errorElement).text(response[error]);
					switch (error) {
					case "label":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_label").parent());
						break;
					case "description":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_description").parent());
						break;
					case "type":
						$(errorElement).css({
							"display" : "inline-block",
							"margin-top" : "5px"
						});
						$(errorElement).appendTo($("#createStandardModal #standard_form .panel-body"));
						break;
					case "standard":
						showError($("#createStandardModal .modal-footer")[0], response["error"]);
						$("#createStandardModal .modal-footer div[class='alert alert-danger']").css("margin-bottom", "0");
						break;
					}
				}
				if (!$("#createStandardModal .label-danger").length) {
					// showSuccess($("#createStandardModal .modal-footer")[0],
					// response["success"]);
					// $("#createStandardModal .modal-footer div[class='alert
					// alert-success']").css("margin-bottom", "0");
					// reloadSection("section_standard");
					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response) {
							var parser = new DOMParser();
							var doc = parser.parseFromString(response, "text/html");
							$("#section_manage_standards table.table").replaceWith($(doc).find("#section_manage_standards table.table"));
							updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
						},
						error : unknowError
					});
					$("#createStandardModal").modal("hide");
				}
				return false;

			},
			error : function() {
				unknowError();
				$("#createStandardModal #createstandardbutton").prop("disabled", false);
			}
		});
	} else
		permissionError();
	return false;
}

function addStandard() {

	var selectedItem = $("#section_manage_standards tbody :checked").parent().parent();
	if (selectedItem.length != 0)
		return false;

	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	var alert = $("#addStandardModal .alert");
	if (alert.length)
		alert.remove();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#add_standard_progressbar").css("display", "none");
		$.ajax({
			url : context + "/Analysis/Standard/Available",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["0"] != undefined) {
					$("#add_standard_progressbar").css("display", "none");
					showError($("#addStandardModal .modal-footer")[0], response["0"]);
				} else {

					if (!$.isEmptyObject(response)) {

						var text = '<div class="col-sm-6"><select name="idNorm" class="form-control">';

						for ( var standard in response) {

							// alert(standard + ' ' + response[standard]);
							text += '<option value="' + standard + '">' + response[standard] + '</option>';

						}

						var lang = $("#nav-container").attr("trick-language");

						text += '</select></div><div class="col-sm-2">';
						text += '<button type="button" class="btn btn-primary" onclick="return doAddStandard(\'addStandardModal\');">' + MessageResolver("label.action.add", "add", null, lang)
								+ '</button></div>';

						$("#addStandardModal .modal-body").html(text);

					} else {

						var lang = $("#nav-container").attr("trick-language");
						var text = '<div class="col-sm-12"><b>' + MessageResolver("label.no_standards_available", "No standards available", null, lang) + '</b></div>';
						$("#addStandardModal .modal-body").html(text);
					}

					$("#addStandardModal").modal("show");
				}
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function doAddStandard(form) {
	var alert = $("#standardModal .alert");
	if (alert.length)
		alert.remove();
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#add_standard_progressbar").css("display", "inline-block");
		var idStandard = $("#" + form + " select").val();
		$.ajax({
			url : context + "/Analysis/Standard/Add/" + idStandard,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					$("#add_standard_progressbar").css("display", "none");
					showError($("#addStandardModal .modal-footer")[0], response["error"]);
					$("#addStandardModal .modal-footer div[class='alert alert-danger']").css("margin-bottom", "0");
				} else if (response["success"] != undefined) {
					// reloadSection("section_standard");
					$("#add_standard_progressbar").css("display", "none");
					// showSuccess($("#addStandardModal .modal-footer")[0],
					// response["success"]);
					// $("#addStandardModal .modal-footer div[class='alert
					// alert-success']").css("margin-bottom", "0");

					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response) {
							var parser = new DOMParser();
							var doc = parser.parseFromString(response, "text/html");
							$("#section_manage_standards table.table").replaceWith($(doc).find("#section_manage_standards table.table"));
							updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
						},
						error : unknowError
					});
					$("#addStandardModal").modal("hide");

				}
			},
			error : function() {
				unknowError();
				$("#add_standard_progressbar").css("display", "none");
			}
		});
	} else
		permissionError();
	return false;
}

function removeStandard() {

	var lang = $("#nav-container").attr("trick-language");
	var selectedStandard = $("#section_manage_standards :checked");
	if (selectedStandard.length != 1)
		return false;
	selectedStandard = findTrickID(selectedStandard[0]);

	var deleteModal = new Modal();
	deleteModal.FromContent($("#deleteStandardModal").clone());

	deleteModal.setBody(MessageResolver("confirm.delete.analysis.norm", "Are you sure, you want to remove this standard from this analysis?", null, lang));

	$(deleteModal.modal_footer).find("#deletestandardbuttonYes").click(function() {
		var alert = $("#standardModal .alert");
		if (alert.length)
			alert.remove();
		$(deleteModal.modal_footer).find("#delete_standard_progressbar").css("display", "inline-block");
		$.ajax({
			url : context + "/Analysis/Standard/Delete/" + selectedStandard,
			type : "get",
			async : false,
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					$(deleteModal.modal_footer).find("#delete_standard_progressbar").css("display", "none");
					showError($("#standardModal .modal-footer")[0], response["error"]);
					$("#standardModal .modal-footer").find("div[class='alert alert-danger']").css("margin-bottom", "0");
				} else if (response["success"] != undefined) {
					$(deleteModal.modal_footer).find("#delete_standard_progressbar").css("display", "none");
					// showSuccess($("#standardModal .modal-footer")[0],
					// response["success"]);
					// $("#standardModal .modal-footer").find("div[class='alert
					// alert-success']").css("margin-bottom", "0");
					// reloadSection("section_standard");

					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response) {
							var parser = new DOMParser();
							var doc = parser.parseFromString(response, "text/html");
							$("#section_manage_standards table.table").replaceWith($(doc).find("#section_manage_standards table.table"));
							updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
						},
						error : unknowError
					});

				} else
					unknowError();
			},
			error : unknowError
		});
	});
	deleteModal.Show();
	return false;
}

// management of measures of analysis only standards

function newMeasure(idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	var selectedItem = $("#section_standard_" + idStandard + " tbody :checked");
	if (selectedItem.length != 0)
		return false;

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	$("#addMeasureModel #addmeasurebutton").prop("disabled", false);
	$("#addMeasureModel #measure_id").prop("value", "-1");
	$("#addMeasureModel #measure_reference").prop("value", "");
	$("#addMeasureModel #measure_level").prop("value", "");
	$("#addMeasureModel input[type='checkbox']").removeAttr("checked");

	$("#addMeasureModel #measure_form").prop("action", context + "/Analysis/Standard/" + idStandard + "/Measure/Save");

	var lang = $("#nav-container").attr("trick-language");

	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure", null, lang));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.add", "Add", null, lang));

	$("#addMeasureModel #addmeasurebutton").attr("onclick", "saveMeasure('" + idStandard + "')");

	var text = '<div style="display: block;"><div class="form-group"><label class="col-sm-2 control-label" for="domain">' + MessageResolver("label.measure.domain", "Domain", null, lang)
			+ '</label><div class="col-sm-10"><input type="text" class="form-control" id="measure_domain" name="domain"></div></div>';
	text = text + '<div class="form-group"><label class="col-sm-2 control-label" for="description">' + MessageResolver("label.measure.description", "Description", null, lang)
			+ '</label><div class="col-sm-10"><textarea class="form-control" id="measure_description" name="description"></textarea></div></div></div>';

	$("#addMeasureModel #measurelanguages").html(text);

	$("#addMeasureModel").modal("show");

	return false;
}

function editSingleMeasure(measureId, idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	if (measureId == null || measureId == undefined)
		measureId = $("#section_standard_" + idStandard + " tbody :checked").parent().parent();
	else {
		measureId = $(measureId);
	}
	if (measureId.length != 1)
		return false;

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	var measure = $(measureId).find("td:not(:first-child)");

	$("#addMeasureModel #measure_id").prop("value", $(measureId).attr("trick-id"));
	$("#addMeasureModel #measure_reference").prop("value", $(measure[0]).text());
	$("#addMeasureModel #measure_level").prop("value", $(measureId).attr("trick-level"));
	$("#addMeasureModel #measure_computable").prop("checked", $(measureId).attr("trick-computable") === "true");

	$("#addMeasureModel #measure_form").prop("action", context + "/Analysis/Standard/" + idStandard + "/Measure/Save");

	var lang = $("#nav-container").attr("trick-language");

	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.measure.update", "Update Measure", null, lang));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.edit", "Update", null, lang));

	$("#addMeasureModel #addmeasurebutton").attr("onclick", "saveMeasure('" + idStandard + "')");

	var domain = $(measure[1]).text();

	var description = $(measureId).attr("trick-description") == undefined ? '' : $(measureId).attr("trick-description");

	var text = '<div style="display: block;"><div class="form-group"><label class="col-sm-2 control-label" for="domain">' + MessageResolver("label.measure.domain", "Domain", null, lang)
			+ '</label><div class="col-sm-10"><input type="text" class="form-control" id="measure_domain" value="' + domain + '" name="domain"></div></div>';
	text = text + '<div class="form-group"><label class="col-sm-2 control-label" for="description">' + MessageResolver("label.measure.description", "Description", null, lang)
			+ '</label><div class="col-sm-10"><textarea class="form-control" id="measure_description" name="description">' + description + '</textarea></div></div></div>';

	$("#addMeasureModel #measurelanguages").html(text);

	$("#addMeasureModel").modal("show");

	return false;
}

function saveMeasure(idStandard) {
	var form = $("#addMeasureModel #measure_form");
	$.ajax({
		url : form.prop("action"),
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var alert = $("#addMeasureModel").find(".label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "reference":
					$(errorElement).appendTo(form.find("#measure_reference").parent());
					break;
				case "level":
					$(errorElement).appendTo(form.find("#measure_level").parent());
					break;
				case "computable":
					$(errorElement).appendTo(form.find("#measure_computable").parent());
					break;
				case "domain":
					$(errorElement).appendTo(form.find("#measure_domain").parent());
					break;
				case "description":
					$(errorElement).appendTo(form.find("#measure_description").parent());
					break;
				case "norm":
				case "measureDescription":
					$(errorElement).appendTo(form.parent());
					break;
				}
			}
			if (!$("#addMeasureModel").find(".label-danger").length) {
				$("#addMeasureModel").modal("hide");
				reloadSection("section_standard_" + idStandard);
			}
			return false;

		},
		error : unknowError
	});

	return false;
}

function deleteMeasure(measureId, standardid) {

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (standardid == null || standardid == undefined)
		return false;

	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_standard_" + standardid);
		if (!selectedScenario.length)
			return false;
	}

	var lang = $("#nav-container").attr("trick-language");

	var standard = $("#section_standard_" + standardid + " #menu_standard_" + standardid + " li:first-child").text();

	if (selectedScenario.length == 1) {
		measureId = selectedScenario[0];
		var measure = $("#section_standard_" + standardid + " tr[trick-id='" + measureId + "'] td:not(:first-child)");
		reference = $(measure[0]).text();

		$("#confirm-dialog .modal-body").html(
				MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: <strong>" + reference + "</strong> from the standard <strong>"
						+ standard + " </strong>?<b>ATTENTION:</b> This will delete complete <b>Action Plans</b> that depend on these measures!", [ reference, standard ], lang));
	} else {
		$("#confirm-dialog .modal-body").html(
				MessageResolver("label.measure.question.selected.delete", "Are you sure, you want to delete the selected measures from the standard <b>" + standard
						+ "</b>?<br/><b>ATTENTION:</b> This will delete complete <b>Action Plans</b> that depend on these measures!", standard, lang));
	}

	$("#confirm-dialog .btn-danger").click(function() {

		var errors = false;

		while (selectedScenario.length) {

			if (errors)
				break;

			rowTrickId = selectedScenario.pop();

			$.ajax({
				url : context + "/Analysis/Standard/" + standardid + "/Measure/Delete/" + rowTrickId,
				async : true,
				contentType : "application/json",
				success : function(response) {
					var trickSelect = parseJson(response);
					if (trickSelect != undefined && trickSelect["success"] == undefined) {
						errors = true;
						if (response["error"] != undefined) {
							$("#alert-dialog .modal-body").html(response["error"]);
						} else {
							$("#alert-dialog .modal-body").html(MessageResolver("error.delete.measure.unkown", "Unknown error occoured while deleting the measure", null, lang));

						}
						$("#alert-dialog").modal("show");
					}

				},
				error : unknowError
			});
		}
		$("#confirm-dialog").modal("hide");
		reloadSection("section_standard_" + standardid);
		return false;
	});
	$("#confirm-dialog").modal("show");
	return false;
}

function manageMeasureAssets(idMeasure, idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	if (idMeasure == null || idMeasure == undefined) {
		var selectedMeasure = findSelectItemIdBySection("section_standard_" + idStandard);
		if (!selectedMeasure.length)
			return false;
		else
			idMeasure = selectedMeasure;
	}

	$.ajax({
		url : context + "/Analysis/Standard/" + idStandard + "/Measure/" + idMeasure + "/ManageAssets",
		async : true,
		contentType : "application/json",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			$("#manageAssetMeasureModel-body").html($(doc).find("#manageAssetMeasure_form"));
			$("#manageAssetMeasureModel").modal("show");
			$("#manageAssetMeasureModel #manageAssetMeasure_form li").click(function(event) {
				manageAssetLiClick($(this));
			});
		},
		error : unknowError
	});
}

function manageAssetLiClick(event) {
	var asset = $(event).attr("opt");
	if ($(event).parent().attr("trick-type") == 'available') {
		$("select#availableAssets option[value='" + asset + "']").clone().appendTo("select#measureAssets");
		$("select#availableAssets option[value='" + asset + "']").remove();
		$(event).clone().appendTo("ul[trick-type='measure']");
		$("li[opt='" + asset + "']").click(function(ev) {
			manageAssetLiClick($(this));
		});
		$(event).remove();

	} else if ($(event).parent().attr("trick-type") == 'measure') {
		$("select#measureAssets option[value='" + asset + "']").clone().appendTo("select#availableAssets");
		$("select#measureAssets option[value='" + asset + "']").remove();
		$(event).clone().appendTo("ul[trick-type='available']");
		$("li[opt='" + asset + "']").click(function(ev) {
			manageAssetLiClick($(this));
		});
		$(event).remove();
	}
}

function saveAssetMeasure(form) {

	var alert = $("#manageAssetMeasureModel").find(".alert");
	if (alert.length)
		alert.remove();

	var idStandard = $(form + " #standard_id").val();

	if (idStandard == null || idStandard == undefined)
		return false;

	var idMeasure = $(form + " #measure_id").val();

	if (idMeasure == null || idMeasure == undefined)
		return false;

	var data = {};

	$(form).find("select[name='measureAssets'] option").each(function() {

		var name = $(this).attr("value");

		var value = $(this).is(":checked");

		data[name] = value;

	});

	var jsonarray = JSON.stringify(data);

	$.ajax({
		url : context + "/Analysis/Standard/" + idStandard + "/Measure/" + idMeasure + "/ManageAssets/Save",
		async : true,
		type : "post",
		data : jsonarray,
		contentType : "application/json",
		success : function(response) {
			if (response["error"] != undefined) {
				$("#manageAssetMeasureModel-body").prepend('<div class="alert alert-danger">' + response["error"] + '</div>');
			} else if (response["success"] != undefined) {
				$("#manageAssetMeasureModel-body").prepend('<div class="alert alert-success">' + response["success"] + '</div>');
			} else
				unknowError();
		},
		error : unknowError
	});
}
