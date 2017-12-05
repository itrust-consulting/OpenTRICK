function saveLanguage(form) {
	$("#addLanguageModel .label-danger").remove();
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/KnowledgeBase/Language/Save",
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
					case "altName":
						$(errorElement).appendTo($("#language_form #language_altName").parent());
						break;
					case "alpha3":
						$(errorElement).appendTo($("#language_form #language_alpha3").parent());
						break;

					case "name":
						$(errorElement).appendTo($("#language_form #language_name").parent());
						break;
					default:
						showDialog("#alert-dialog",response[error]);
				}
				hasError = true;
			}
			if (!hasError) {
				$("#addLanguageModel").modal("hide");
				reloadSection("section_language");
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showDialog("#alert-dialog", MessageResolver("error.unknown.save.language", "An unknown error occurred during saving language"))
		}
	}).complete(()=> $progress.hide());
	return false;
}

function deleteLanguage(languageId, name) {
	if (languageId == null || languageId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_language"));
		if (selectedScenario.length != 1)
			return false;
		languageId = selectedScenario[0];
		name = $("#section_language tbody tr[data-trick-id='" + languageId + "']>td:nth-child(3)").text();
	}
	
	$("#deleteLanguageBody").html(MessageResolver("label.language.question.delete", "Are you sure that you want to delete the language <strong>" + name + "</strong>?", name));
	$("#deletelanguagebuttonYes").unbind().click(function () {
		$("#deleteLanguageModel").modal('hide');
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/KnowledgeBase/Language/Delete/" + languageId,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			async: true,
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					reloadSection("section_language");
				else if (response["error"] != undefined)
					showDialog("#alert-dialog",response["error"]);
				else unknowError();
			},
			error: unknowError
		}).complete( () => $progress.hide());
		return false;
	});
	$("#deleteLanguageModel").modal('show');
	return false;
}

function newLanguage() {
	if (findSelectItemIdBySection("section_language").length)
		return false;
	$("#addLanguageModel .label-danger").remove();
	$("#addLanguageModel #addlanguagebutton").prop("disabled", false);
	$("#language_id").prop("value", "-1");
	$("#language_alpha3").prop("value", "");
	$("#language_name").prop("value", "");
	$("#language_altName").prop("value", "");
	$("#addLanguageModel-title").text(MessageResolver("title.knowledgebase.language.add", "Add a new Language"));
	$("#addlanguagebutton").text(MessageResolver("label.action.save", "Save"));
	$("#language_form").prop("action", "Language/Save");
	$("#addLanguageModel").modal('toggle');
	return false;
}

function editSingleLanguage(languageId) {
	if (languageId == null || languageId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_language"));
		if (selectedScenario.length != 1)
			return false;
		languageId = selectedScenario[0];
	}
	$("#addLanguageModel .label-danger").remove();
	$("#addLanguageModel #addlanguagebutton").prop("disabled", false);
	var rows = $("#section_language").find("tr[data-trick-id='" + languageId + "'] td:not(:first-child)");
	$("#language_id").prop("value", languageId);
	$("#language_alpha3").prop("value", $(rows[0]).text());
	$("#language_name").prop("value", $(rows[1]).text());
	$("#language_altName").prop("value", $(rows[2]).text());
	$("#addLanguageModel-title").text(MessageResolver("title.knowledgebase.language.update", "Update a Language"));
	$("#addlanguagebutton").text(MessageResolver("label.action.save", "Save"));
	$("#language_form").prop("action", "Language/Edit/" + languageId);
	$("#addLanguageModel").modal('toggle');
	return false;
}