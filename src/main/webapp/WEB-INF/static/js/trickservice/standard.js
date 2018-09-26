function saveStandard(form) {
	var $progress = $("#loading-indicator").show();
	$("#addStandardModel .label-danger").remove();
	$.ajax({
		url: context + "/KnowledgeBase/Standard/Save",
		type: "post",
		data: serializeForm(form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var hasError = false;
			for (var error in response) {
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
					default:
						showDialog("#alert-dialog",response[error]);
				}
				hasError = true;
			}
			if (!hasError) {
				$("#addStandardModel").modal("hide");
				reloadSection("section_kb_standard");
			}
			return false;

		},
		error: unknowError
	}).complete(()=> $progress.hide());
	return false;
}

function deleteStandard(idStandard, name) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_kb_standard"));
		if (selectedScenario.length != 1)
			return false;
		name = $("#section_kb_standard tbody tr[data-trick-id='" + (idStandard = selectedScenario[0]) + "']>td:nth-child(2)").text();
	}
	var $progress = $("#loading-indicator").show();
	$("#deleteStandardBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the standard <strong>" + name + "</strong>?", name));
	$("#deletestandardbuttonYes").off("click.delete").one("click.delete", function () {
		$("#deleteStandardModel").modal('hide');
		$progress.show();
		$.ajax({
			url: context + "/KnowledgeBase/Standard/Delete/" + idStandard,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				reloadSection("section_kb_standard");
				return false;
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		})
		return false;
	});
	$progress.hide();
	$("#deleteStandardModel").modal('show');
	return false;
}

function uploadImportStandardFile() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/KnowledgeBase/Standard/Upload",
		async: true,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
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
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function importNewStandard() {
	$("#updateStandardNotification").empty();
	var $uploadFile = $("#upload-file-info"), $progress = $("#loading-indicator");
	if (!$uploadFile.length)
		return false;
	else if ($uploadFile.val() == "") {
		$("#updateStandardNotification").text(MessageResolver("error.import.standard.no_select.file", "Please select file to import"));
		return false;
	} else
		$("#uploadStandardModal").modal("hide");
	$progress.show();
	$.ajax({
		url: context + "/KnowledgeBase/Standard/Import",
		type: 'POST',
		data: new FormData($('#uploadStandard_form')[0]),
		cache: false,
		contentType: false,
		processData: false,
		success: function (response, textStatus, jqXHR) {
			if (response.success)
				application["taskManager"].SetTitle(MessageResolver("label.title.import.norm", "Import a new standard")).Start();
			else if (response.error)
				showDialog("#alert-dialog", response.error);
			else
				showDialog("#alert-dialog", MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
		},
		error: unknowError

	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function newStandard() {
	$("#addStandardModel .label-danger").remove();
	$("#addStandardModel #addstandardbutton").prop("disabled", false);
	$("#standard_id").prop("value", "-1");
	$("#standard_label").prop("value", "");
	$("#standard_version").prop("value", "");
	$("#standard_description").prop("value", "");
	$("#addStandardModel input[name='type'][value='NORMAL']").parent().button("toggle");
	$("#standard_computable input[value='true']").parent().button("toggle");
	$("#addStandardModel-title").text(MessageResolver("title.knowledgebase.norm.add", "Add a new Standard"));
	$("#addstandardbutton").text(MessageResolver("label.action.save", "Save"));
	$("#standard_form").prop("action", "/Save");
	$("#addStandardModel").modal('show');
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
	var standardtype = $($(rows[3])[0]).attr("data-trick-type"), computable = $(rows[4]).attr("data-trick-computable") == 'Yes';
	$("#addStandardModel input[name='type'][value='" + standardtype + "']").parent().button("toggle");
	$("#standard_computable input[value='"+computable+"']").parent().button("toggle");
	$("#addStandardModel-title").text(MessageResolver("title.knowledgebase.norm.update", "Update a Standard"));
	$("#addstandardbutton").text(MessageResolver("label.action.save", "Save"));
	$("#standard_form").prop("action", "/Save");
	$("#addStandardModel").modal('show');
	return false;
}

function getImportStandardTemplate() {
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
