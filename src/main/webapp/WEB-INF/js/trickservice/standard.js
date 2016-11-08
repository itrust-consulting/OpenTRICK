var progressBar = undefined;

function saveStandard(form) {
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	$.ajax({
		url : context + "/KnowledgeBase/Standard/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#addStandardModel #addstandardbutton").prop("disabled", false);
			$("#addStandardModel .label-danger").remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "label":
					$(errorElement).appendTo($("#standard_form #standard_label").parent());
					break;
				case "version":
					$(errorElement).appendTo($("#standard_form #standard_version").parent());
					break;

				case "description":
					$(errorElement).appendTo($("#standard_form #standard_description").parent());
					break;

				case "standard":
					$(errorElement).appendTo($("#standard_form .modal-body"));
					break;
				}
			}
			if (!$("#addStandardModel .label-danger").length) {
				$("#addStandardModel").modal("hide");
				reloadSection("section_kb_standard");
			}
			return false;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("#addStandardModel .label-danger").remove();
			$("#addStandardModel #addstandardbutton").prop("disabled", false);
			var errorElement = document.createElement("label");
			errorElement.setAttribute("class", "label label-danger");
			$(errorElement).text(MessageResolver("error.unknown.save.norm", "An unknown error occurred during saving standard"));
			$(errorElement).appendTo($("#addStandardModel .modal-body"));
		}
	});
	return false;
}

function deleteStandard(idStandard, name) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_kb_standard"));
		if (selectedScenario.length != 1)
			return false;
		name = $("#section_kb_standard tbody tr[data-trick-id='" + (idStandard = selectedScenario[0]) + "']>td:nth-child(2)").text();
	}
	$("#deleteStandardBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the standard <strong>" + name + "</strong>?", name));
	$("#deletestandardbuttonYes").one("click", function() {
		$("#deleteStandardModel").modal('hide');
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/KnowledgeBase/Standard/Delete/" + idStandard,
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				}
				reloadSection("section_kb_standard");
				return false;
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		})
		return false;
	});
	$("#deleteStandardModel").modal('show');
	return false;
}

function uploadImportStandardFile() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/KnowledgeBase/Standard/Upload",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $view = $("#uploadStandardModal", new DOMParser().parseFromString(response, "text/html"));
			if (!$view.length)
				unknowError();
			else {
				var $old = $("#uploadStandardModal");
				if ($old.length)
					$old.replaceWith($view);
				else
					$view.appendTo($("#widget"));
				$view.modal("show");
			}
			return false;
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	})
	return false;
}

function importNewStandard() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	if (progressBar != undefined)
		progressBar.Destroy();
	$("#updateStandardNotification").empty();
	var $uploadFile = $("#upload-file-info");
	if (!$uploadFile.length)
		return false;
	else if ($uploadFile.val() == "") {
		$("#updateStandardNotification").text(MessageResolver("error.import.standard.no_select.file", "Please select file to import"));
		return false;
	}
	$("#uploadStandardModal .modal-footer .btn").prop("disabled", true);
	$("#uploadStandardModal .modal-header .close").prop("disabled", true);
	$.ajax({
		url : context + "/KnowledgeBase/Standard/Import",
		type : 'POST',
		data : new FormData($('#uploadStandard_form')[0]),
		cache : false,
		contentType : false,
		processData : false,
		success : function(response, textStatus, jqXHR) {
			if (response.flag != undefined) {
				progressBar = new ProgressBar();
				progressBar.Initialise();
				$(progressBar.progress).appendTo($("#uploadStandard_form").parent());
				callback = {
					failed : function() {
						setTimeout(function() {
							progressBar.Destroy();
						}, 2000);
					},
					success : function() {

						reloadSection('section_kb_standard');
						setTimeout(function() {
							progressBar.Destroy();
							$("#uploadStandardModal").modal("hide");
						}, 2000);
					}
				};
				progressBar.OnComplete(callback.success);
				updateStatus(progressBar, response.taskID, callback, response);
			} else {
				var $view = $("#uploadStandardModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$("#uploadStandardModal").replaceWith($view);
					$("#uploadStandardModal .modal-footer .btn").prop("disabled", false);
					$("#uploadStandardModal .modal-header .close").prop("disabled", false);
				} else {
					$("#updateStandardNotification").text(MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
				}
			}
		},
		error : unknowError

	});
	return false;
}

function newStandard() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	$("#addStandardModel .label-danger").remove();
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	$("#standard_id").prop("value", "-1");
	$("#standard_label").prop("value", "");
	$("#standard_version").prop("value", "");
	$("#standard_description").prop("value", "");
	$("#addStandardModel input[name='type'][value='NORMAL']").prop("checked", true);
	$("#standard_computable").prop("checked", false);
	$("#addStandardModel-title").text(MessageResolver("title.knowledgebase.norm.add", "Add a new Standard"));
	$("#addstandardbutton").text(MessageResolver("label.action.save", "Save"));
	$("#standard_form").prop("action", "/Save");
	$("#addStandardModel").modal('toggle');
	return false;
}

function editSingleStandard(idStandard) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_kb_standard");
		if (selectedScenario.length != 1)
			return false;
		idStandard = selectedScenario[0];
	}
	$("#addStandardModel .label-danger").remove();
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	var rows = $("#section_kb_standard").find("tr[data-trick-id='" + idStandard + "'] td:not(:first-child)");
	$("#standard_id").prop("value", idStandard);
	$("#standard_label").prop("value", $(rows[0]).text());
	$("#standard_version").prop("value", $(rows[1]).text());
	$("#standard_description").prop("value", $(rows[2]).text());
	var standardtype = $($(rows[3])[0]).attr("data-trick-type");
	$("#addStandardModel input[name='type'][value='" + standardtype + "']").prop("checked", "checked");
	$("#standard_computable").prop("checked", $(rows[4]).attr("data-trick-computable") == 'Yes' ? "checked" : "");
	$("#addStandardModel-title").text(MessageResolver("title.knowledgebase.norm.update", "Update a Standard"));
	$("#addstandardbutton").text(MessageResolver("label.action.save", "Save"));
	$("#standard_form").prop("action", "/Save");
	$("#addStandardModel").modal('toggle');
	return false;
}

function getImportStandardTemplate() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	window.location = context + '/KnowledgeBase/Standard/Template';
	return false;
}

function exportSingleStandard(idStandard) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_kb_standard");
		if (selectedScenario.length != 1)
			return false;
		idStandard = selectedScenario[0];
	}
	window.location = context + '/KnowledgeBase/Standard/Export/' + idStandard
	return false;

}
