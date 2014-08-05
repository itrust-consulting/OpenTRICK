var progressBar = undefined;

function saveNorm(form) {
	$("#addNormModel #addnormbutton").prop("disabled", false);
	$.ajax({
		url : context + "/KnowledgeBase/Norm/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#addNormModel #addnormbutton").prop("disabled", false);
			var alert = $("#addNormModel .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);
				switch (error) {
				case "label":
					$(errorElement).appendTo($("#norm_form #norm_label").parent());
					break;
				case "version":
					$(errorElement).appendTo($("#norm_form #norm_version").parent());
					break;

				case "description":
					$(errorElement).appendTo($("#norm_form #norm_description").parent());
					break;

				case "norm":
					$(errorElement).appendTo($("#norm_form .modal-body"));
					break;
				}
			}
			if (!$("#addNormModel .label-danger").length) {
				$("#addNormModel").modal("hide");
				reloadSection("section_norm");
			}
			return false;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			var alert = $("#addNormModel .label-danger");
			if (alert.length)
				alert.remove();
			$("#addNormModel #addnormbutton").prop("disabled", false);
			var errorElement = document.createElement("label");
			errorElement.setAttribute("class", "label label-danger");
			$(errorElement).text(MessageResolver("error.unknown.save.norm", "An unknown error occurred during saving standard"));
			$(errorElement).appendTo($("#addNormModel .modal-body"));
		},
	});
	return false;
}

function deleteNorm(normId, name) {
	if (normId == null || normId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_norm"));
		if (selectedScenario.length != 1)
			return false;
		normId = selectedScenario[0];
		name = $("#section_norm tbody tr[trick-id='" + normId + "']>td:nth-child(2)").text();
	}
	$("#deleteNormBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the norm <strong>" + name + "</strong>?",name));
	$("#deletenormbuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Norm/Delete/" + normId,
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				}	
				reloadSection("section_norm");
				return false;
			},error : unknowError
		});
		$("#deletenormbuttonYes").unbind("click");
		$("#deleteNormModel").modal('toggle');
		return false;
	});
	$("#deleteNormModel").modal('toggle');
	return false;
}

function uploadImportNormFile() {
	$.ajax({
		url : context + "/KnowledgeBase/Norm/Upload",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((uploadNormModal = doc.getElementById("uploadNormModal")) == null)
				return false;
			if ($("#uploadNormModal").length)
				$("#uploadNormModal").html($(uploadNormModal).html());
			else
				$(uploadNormModal).appendTo($("#widget"));
			$('#uploadNormModal').on('hidden.bs.modal', function() {
				$('#uploadNormModal').remove();
			});
			$("#uploadNormModal").modal("toggle");
			return false;
		},error : unknowError
	});
	return false;
}

function onSelectFile(file) {
	$("#upload-file-info").prop("value", $(file).prop("value"));
	return false;
}

function importNewNorm() {
	$("#uploadNormModal .modal-footer .btn").prop("disabled", true);
	$("#uploadNormModal .modal-header .close").prop("disabled", true);
	
	if(progressBar != undefined)
		progressBar.Destroy();
	
	var formData = new FormData($('#uploadNorm_form')[0]);
	$.ajax({
		url : context + "/KnowledgeBase/Norm/Import",
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
				$(progressBar.progress).appendTo($("#uploadNorm_form").parent());
				callback = {
					failed : function() {
						progressBar.Destroy();
						$("#uploadNormModal").modal("toggle");
						$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.task.execution", "An unknown error occurred during the execution of the task"));
					},
					success : function() {
						progressBar.Destroy();
						reloadSection('section_norm');
						$("#uploadNormModal").modal("toggle");
					}
				};
				progressBar.OnComplete(callback.success);
				updateStatus(progressBar, response.taskID, callback, response);
			} else {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ((modalForm = doc.getElementById("uploadNormModal")) == null) {
					$("#uploadNormModal").modal("toggle");
					$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
				} else {
					$("#uploadNormModal").replaceWith($(modalForm).text());
					$("#uploadNormModal .modal-footer .btn").prop("disabled", false);
					$("#uploadNormModal .modal-header .close").prop("disabled", false);
				}
			}

		},
		error : unknowError
		,
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

function newNorm() {
	var alert = $("#addNormModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#addNormModel #addnormbutton").prop("disabled", false);
	$("#norm_id").prop("value", "-1");
	$("#norm_label").prop("value", "");
	$("#norm_version").prop("value", "");
	$("#norm_description").prop("value", "");
	$("#norm_computable").prop("checked", false);
	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.norm.add", "Add a new Norm"));
	$("#addnormbutton").text(MessageResolver("label.action.add", "Add"));
	$("#norm_form").prop("action", "/Save");
	$("#addNormModel").modal('toggle');
	return false;
}

function editSingleNorm(normId) {
	if (normId == null || normId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_norm"));
		if (selectedScenario.length != 1)
			return false;
		normId = selectedScenario[0];
	}
	var alert = $("#addNormModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#addNormModel #addnormbutton").prop("disabled", false);
	var rows = $("#section_norm").find("tr[trick-id='" + normId + "'] td:not(:first-child)");
	$("#norm_id").prop("value", normId);
	$("#norm_label").prop("value", $(rows[0]).text());
	$("#norm_version").prop("value", $(rows[1]).text());
	$("#norm_description").prop("value", $(rows[2]).text());

	$("#norm_computable").prop("checked", $(rows[3]).attr("computable") == 'Yes' ? "checked" : "");

	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.norm.update", "Update a Norm"));
	$("#addnormbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#norm_form").prop("action", "/Save");
	$("#addNormModel").modal('toggle');
	return false;
}

function getImportNormTemplate() {
	$.fileDownload(context + '/data/TL_TRICKService_NormImport_V1.1.xlsx').done(function() {
		alert('File download a success!');
	}).fail(function() {
		unknowError();
	});
	return false;
}

function exportSingleNorm(normId) {
	if (normId == null || normId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_norm"));
		if (selectedScenario.length != 1)
			return false;
		normId = selectedScenario[0];
	}
	
	$.fileDownload(context + '/KnowledgeBase/Norm/Export/'+normId).fail(function() {
		unknowError();
	});
	return false;
	
}