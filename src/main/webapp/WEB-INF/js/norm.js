function saveNorm(form) {
	result = "";
	return $.ajax({
		url : context + "/KnowledgeBase/Norm/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addNormModel").modal("hide");
				reloadSection("section_norm");
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function deleteANorm(normId) {
	$.ajax({
		url : context + "/KnowledgeBase/Norm/Delete/" + normId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			reloadSection("section_norm");
			return false;
		}
	});
	return false;
}

function deleteNorm(normId, name) {
	$("#deleteNormBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the norm") + "&nbsp;<strong>" + name + "</strong>?");
	$("#deletenormbuttonYes").attr("onclick", "deleteANorm(" + normId + ")");
	$("#deleteNormModel").modal('toggle');
}

function newNorm() {
	$("#norm_id").prop("value", "-1");
	$("#norm_label").prop("value", "");
	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.Norm.Add", "Add a new Norm"));
	$("#addnormbutton").text(MessageResolver("label.action.add", "Add"));
	$("#norm_form").prop("action", "/Save");
	$("#addNormModel").modal('toggle');
}

function editSingleNorm(normId) {
	var rows = $("#section_norm").find("tr[trick-id='" + normId + "'] td");
	$("#norm_id").prop("value", normId);
	$("#norm_label").prop("value", $(rows[0]).text());
	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.Norm.Update", "Update a Norm"));
	$("#addnormbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#norm_form").prop("action", "/Save");
	$("#addNormModel").modal('toggle');
}