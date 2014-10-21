var progressBar = undefined;

function saveStandard(form) {
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	$.ajax({
		url : context + "/KnowledgeBase/Standard/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#addStandardModel #addstandardbutton").prop("disabled", false);
			var alert = $("#addStandardModel .label-danger");
			if (alert.length)
				alert.remove();
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
			var alert = $("#addStandardModel .label-danger");
			if (alert.length)
				alert.remove();
			$("#addStandardModel #addstandardbutton").prop("disabled", false);
			var errorElement = document.createElement("label");
			errorElement.setAttribute("class", "label label-danger");
			$(errorElement).text(MessageResolver("error.unknown.save.norm", "An unknown error occurred during saving standard"));
			$(errorElement).appendTo($("#addStandardModel .modal-body"));
		},
	});
	return false;
}

function deleteStandard(idStandard, name) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_kb_standard"));
		if (selectedScenario.length != 1)
			return false;
		idStandard = selectedScenario[0];
		name = $("#section_kb_standard tbody tr[trick-id='" + idStandard + "']>td:nth-child(2)").text();
	}
	$("#deleteStandardBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the standard <strong>" + name + "</strong>?", name));
	$("#deletestandardbuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Standard/Delete/" + idStandard,
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				}
				reloadSection("section_kb_standard");
				return false;
			},
			error : unknowError
		});
		$("#deletestandardbuttonYes").unbind("click");
		$("#deleteStandardModel").modal('toggle');
		return false;
	});
	$("#deleteStandardModel").modal('toggle');
	return false;
}

function uploadImportStandardFile() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	$.ajax({
		url : context + "/KnowledgeBase/Standard/Upload",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((uploadStandardModal = doc.getElementById("uploadStandardModal")) == null)
				return false;
			if ($("#uploadStandardModal").length)
				$("#uploadStandardModal").html($(uploadStandardModal).html());
			else
				$(uploadStandardModal).appendTo($("#widget"));
			$('#uploadStandardModal').on('hidden.bs.modal', function() {
				$('#uploadStandardModal').remove();
			});
			$("#uploadStandardModal").modal("toggle");
			return false;
		},
		error : unknowError
	});
	return false;
}

function onSelectFile(file) {
	$("#upload-file-info").prop("value", $(file).prop("value"));
	return false;
}

function importNewStandard() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	$("#uploadStandardModal .modal-footer .btn").prop("disabled", true);
	$("#uploadStandardModal .modal-header .close").prop("disabled", true);

	if (progressBar != undefined)
		progressBar.Destroy();

	var formData = new FormData($('#uploadStandard_form')[0]);
	$.ajax({
		url : context + "/KnowledgeBase/Standard/Import",
		type : 'POST',
		xhr : function() { // Custom XMLHttpRequest
			var myXhr = $.ajaxSettings.xhr();
			/*
			 * if (myXhr.upload) { // Check if upload property exists
			 * myXhr.upload.addEventListener('progress',
			 * progressHandlingFunction, false); // For handling the // progress
			 * of the // upload }
			 */
			return myXhr;
		},
		// Ajax events
		// beforeSend : beforeSendHandler,
		success : function(response) {
			if (response.flag != undefined) {
				progressBar = new ProgressBar();
				progressBar.Initialise();
				$(progressBar.progress).appendTo($("#uploadStandard_form").parent());
				callback = {
					failed : function() {
						progressBar.Destroy();
						$("#uploadStandardModal").modal("toggle");
						$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.task.execution", "An unknown error occurred during the execution of the task"));
					},
					success : function() {
						progressBar.Destroy();
						reloadSection('section_kb_standard');
						$("#uploadStandardModal").modal("toggle");
					}
				};
				progressBar.OnComplete(callback.success);
				updateStatus(progressBar, response.taskID, callback, response);
			} else {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ((modalForm = doc.getElementById("uploadStandardModal")) == null) {
					$("#uploadStandardModal").modal("toggle");
					$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
				} else {
					$("#uploadStandardModal").replaceWith($(modalForm).text());
					$("#uploadStandardModal .modal-footer .btn").prop("disabled", false);
					$("#uploadStandardModal .modal-header .close").prop("disabled", false);
				}
			}

		},
		error : unknowError,
		// error : errorHandler,
		// Form data
		data : formData,
		// Options to tell jQuery not to process data or worry about
		// content-type.
		cache : false,
		contentType : false,
		processData : false

	});
}

function newStandard() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	var alert = $("#addStandardModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	$("#standard_id").prop("value", "-1");
	$("#standard_label").prop("value", "");
	$("#standard_version").prop("value", "");
	$("#standard_description").prop("value", "");
	// $("#addStandardModel input[name='type']").removeAttr("checked");
	$("#addStandardModel input[name='type'][value='NORMAL']").prop("checked", true);
	$("#standard_computable").prop("checked", false);
	$("#addStandardModel-title").text(MessageResolver("title.knowledgebase.norm.add", "Add a new Standard"));
	$("#addstandardbutton").text(MessageResolver("label.action.add", "Add"));
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
	var alert = $("#addStandardModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	var rows = $("#section_kb_standard").find("tr[trick-id='" + idStandard + "'] td:not(:first-child)");
	$("#standard_id").prop("value", idStandard);
	$("#standard_label").prop("value", $(rows[0]).text());
	$("#standard_version").prop("value", $(rows[1]).text());
	$("#standard_description").prop("value", $(rows[2]).text());
	var standardtype = $($(rows[3])[0]).attr("trick-type");
	$("#addStandardModel input[name='type'][value='" + standardtype + "']").prop("checked", "checked");
	$("#standard_computable").prop("checked", $(rows[4]).attr("computable") == 'Yes' ? "checked" : "");
	$("#addStandardModel-title").text(MessageResolver("title.knowledgebase.norm.update", "Update a Standard"));
	$("#addstandardbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#standard_form").prop("action", "/Save");
	$("#addStandardModel").modal('toggle');
	return false;
}

function getImportStandardTemplate() {
	if (findSelectItemIdBySection("section_kb_standard").length)
		return false;
	$.fileDownload(context + '/data/TL_TRICKService_NormImport_V1.1.xlsx').done(function() {
		alert('File download a success!');
	}).fail(function() {
		unknowError();
	});
	return false;
}

function exportSingleStandard(idStandard) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_kb_standard");
		if (selectedScenario.length != 1)
			return false;
		idStandard = selectedScenario[0];
	}

	$.fileDownload(context + '/KnowledgeBase/Standard/Export/' + idStandard).fail(function() {
		unknowError();
	});
	return false;

}