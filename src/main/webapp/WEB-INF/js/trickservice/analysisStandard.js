// add new standard to analysis

$(document).ready(function() {
	$('#standardModal').on('hidden.bs.modal', function() {
		reloadSection("section_standard");
	})
});

function manageStandard() {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
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
						showError($("#standardModal .modal-footer")[0], MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data"));
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

	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {

		var alert = $("#createStandardModal .label-danger");
		if (alert.length)
			alert.remove();

		$("#createStandardModal #createstandardbutton").prop("disabled", false);
		$("#createStandardModal #standard_label").prop("value", "");
		$("#createStandardModal #standard_version").prop("value", "");
		$("#createStandardModal #standard_description").prop("value", "");
		$("#createStandardModal #standard_form input[name='type'][value='NORMAL']").prop("checked", "checked");
		$("#createStandardModal #standard_description").removeProp("checked");
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

function addStandard() {
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

}

// management of measures of analysis only standards

function manageMeasures() {

	var selectedStandard = $("#section_manage_standards :checked");
	if (selectedStandard.length != 1)
		return false;
	selectedStandard = findTrickID(selectedStandard[0]);

	$.ajax({
		url : context + "/Analysis/Standard/Show/" + selectedStandard,
		type : "get",
		async : false,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");

			$("#section_measure_description #measures_header").html($(doc).find("#measures_header").html());

			$("#section_measure_description #measures_body").html($(doc).find("#measures_body").html());

			updateMenu(undefined, "#section_measure_description", "#menu_measure_description", undefined);

			$("#section_measure_description").modal("show");
		},
		error : unknowError
	});

	return false;
	
}

function newMeasure(idStandard) {
	if (findSelectItemIdBySection("section_measure_description").length)
		return false;
	if (idStandard == null || idStandard == undefined)
		idStandard = $("#section_measure_description #measures_header #idStandard").val();
	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	$("#addMeasureModel #addmeasurebutton").prop("disabled", false);
	$("#addMeasureModel #measure_id").prop("value", "-1");
	$("#addMeasureModel #measure_reference").prop("value", "");
	$("#addMeasureModel #measure_level").prop("value", "");
	$("#addMeasureModel input[type='checkbox']").removeAttr("checked");

	$("#addMeasureModel #measure_form").prop("action", context + "/Analysis/Standard/" + idStandard + "/Measure/Save");
	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure"));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.add", "Add"));

	var lang = $("#nav-container").attr("trick-language");

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
		idStandard = $("#section_measure_description #measures_header #idStandard").val();

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_measure_description");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
	}
	var measure = $("#section_measure_description #measures_body tr[trick-id='" + measureId + "'] td:not(:first-child)");

	$("#addMeasureModel #measure_id").prop("value", measureId);
	$("#addMeasureModel #measure_reference").prop("value", $(measure[1]).text());
	$("#addMeasureModel #measure_level").prop("value", $(measure[0]).text());
	$("#addMeasureModel #measure_computable").prop("checked", $(measure[4]).attr("trick-computable") == "true");

	$("#addMeasureModel #measure_form").prop("action", context + "/Analysis/Standard/" + idStandard + "/Measure/Save");
	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.measure.update", "Update Measure"));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.edit", "Update"));

	var lang = $("#nav-container").attr("trick-language");

	var text = '<div style="display: block;"><div class="form-group"><label class="col-sm-2 control-label" for="domain">' + MessageResolver("label.measure.domain", "Domain", null, lang)
			+ '</label><div class="col-sm-10"><input type="text" class="form-control" id="measure_domain" value="' + $(measure[2]).text() + '" name="domain"></div></div>';
	text = text + '<div class="form-group"><label class="col-sm-2 control-label" for="description">' + MessageResolver("label.measure.description", "Description", null, lang)
			+ '</label><div class="col-sm-10"><textarea class="form-control" id="measure_description" name="description">' + $(measure[3]).text() + '</textarea></div></div></div>';

	$("#addMeasureModel #measurelanguages").html(text);

	$("#addMeasureModel").modal("show");

	return false;
}

function saveMeasure() {
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
				var idStandard = $("#section_measure_description #measures_header #idStandard").val();
				var standardLabel = $("#section_measure_description #measures_header #standardLabel").val();
				standardLabel
				$.ajax({
					url : context + "/Analysis/Standard/Show/" + idStandard,
					type : "GET",
					contentType : "application/json",
					success : function(response) {
						var parser = new DOMParser();
						var doc = parser.parseFromString(response, "text/html");

						reloadSection("section_standard_" + standardLabel);

						$("#section_measure_description #measures_header").html($(doc).find("#measures_header").html());

						$("#section_measure_description #measures_body").html($(doc).find("#measures_body").html());

						updateMenu(undefined, "#section_measure_description", "#menu_measure_description", undefined);

						$("#section_measure_description").modal("show");

					},
					error : unknowError
				});
			}
			return false;

		},
		error : unknowError
	});

	return false;
}

function deleteMeasure(measureId, reference, standard) {

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_measure_description");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
	}

	idStandard = $("#section_measure_description #measures_header #idStandard").val();

	if (standard == null || standard == undefined)
		standard = $("#section_measure_description #measures_header #standardLabel").val();

	var measure = $("#section_measure_description #measures_body tr[trick-id='" + measureId + "'] td:not(:first-child)");
	reference = $(measure[1]).text();

	var deleteModal = new Modal();
	deleteModal.FromContent($("#deleteMeasureModel").clone());
	deleteModal.setBody(MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: <strong>" + reference
			+ "</strong> from the standard <strong>" + standard + " </strong>?", [ reference, standard ]));
	$(deleteModal.modal_header).find("button").click(function() {
		delete deleteModal;
	});
	$(deleteModal.modal_footer).find("#deletemeasurebuttonYes").click(function() {
		$.ajax({
			url : context + "/Analysis/Standard/" + idStandard + "/Measure/Delete/" + measureId,
			type : "POST",
			contentType : "application/json",
			async : false,
			success : function(response) {
				if (response["success"] != undefined) {
					var idStandard = $("#section_measure_description #measures_header #idStandard").val();
					var standardLabel = $("#section_measure_description #measures_header #standardLabel").val();
					standardLabel
					$.ajax({
						url : context + "/Analysis/Standard/Show/" + idStandard,
						type : "GET",
						contentType : "application/json",
						success : function(response) {
							var parser = new DOMParser();
							var doc = parser.parseFromString(response, "text/html");

							reloadSection("section_standard_" + standardLabel);

							$("#section_measure_description #measures_header").html($(doc).find("#measures_header").html());

							$("#section_measure_description #measures_body").html($(doc).find("#measures_body").html());

							updateMenu(undefined, "#section_measure_description", "#menu_measure_description", undefined);

							$("#section_measure_description").modal("show");

						},
						error : unknowError
					});
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").css("z-index", "1042");
					$("#alert-dialog").modal("show");
				} else
					unknowError();
				return true;
			},
			error : unknowError
		});
		delete deleteModal;
		return true;
	});
	deleteModal.Show();
	return false;
}
