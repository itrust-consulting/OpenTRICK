function saveLanguage(form) {
	result = "";
	return $.ajax({
		url : context + "/KnowledgeBase/Language/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addLanguageModel").modal("hide");
				reloadSection("section_language");
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function deleteALanguage(languageId) {
	$.ajax({
		url : context + "/KnowledgeBase/Language/Delete/" + languageId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			reloadSection("section_language");
			return false;
		}
	});
	return false;
}

function deleteLanguage(languageId, name) {
	$("#deleteLanguageBody").html(MessageResolver("label.language.question.delete", "Are you sure that you want to delete the language") + "&nbsp;<strong>" + name + "</strong>?");
	$("#deletelanguagebuttonYes").attr("onclick", "deleteALanguage(" + languageId + ")");
	$("#deleteLanguageModel").modal('toggle');
	return false;
}

function newLanguage() {
	$("#language_id").prop("value", "-1");
	$("#language_alpha3").prop("value", "");
	$("#language_name").prop("value", "");
	$("#language_altName").prop("value", "");
	$("#addLanguageModel-title").text(MessageResolver("title.knowledgebase.Language.Add", "Add a new Language"));
	$("#addlanguagebutton").text(MessageResolver("label.action.add", "Add"));
	$("#language_form").prop("action", "Language/Save");
	$("#addLanguageModel").modal('toggle');
	return false;
}

function editSingleLanguage(languageId) {
	var rows = $("#section_language").find("tr[trick-id='" + languageId + "'] td");
	$("#language_id").prop("value", languageId);
	$("#language_alpha3").prop("value", $(rows[0]).text());
	$("#language_name").prop("value", $(rows[1]).text());
	$("#language_altName").prop("value", $(rows[2]).text());
	$("#addLanguageModel-title").text(MessageResolver("title.knowledgebase.Language.Update", "Update a Language"));
	$("#addlanguagebutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#language_form").prop("action", "Language/Edit/" + languageId);
	$("#addLanguageModel").modal('toggle');
	return false;
}