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

function deleteNorm(normId, name) {
	if (normId == null || normId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_norm"));
		if (selectedScenario.length != 1)
			return false;
		normId = selectedScenario[0];
		name = $("#section_norm tbody tr[trick-id='" + normId + "']>td:nth-child(2)").text();
	}
	$("#deleteNormBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the norm") + "&nbsp;<strong>" + name + "</strong>?");
	$("#deletenormbuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Norm/Delete/" + normId,
			type : "POST",
			contentType : "application/json",
			success : function(response) {
				reloadSection("section_norm");
				return false;
			}
		});
		$("#deleteNormModel").modal('toggle');
		return false;
	});
	$("#deleteNormModel").modal('toggle');
	return false;
}

function newNorm() {
	$("#norm_id").prop("value", "-1");
	$("#norm_label").prop("value", "");
	$("#norm_version").prop("value", "");
	$("#norm_description").prop("value", "");
	$("#norm_computable").prop("checked", false);
	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.Norm.Add", "Add a new Norm"));
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
	var rows = $("#section_norm").find("tr[trick-id='" + normId + "'] td:not(:first-child)");
	$("#norm_id").prop("value", normId);
	$("#norm_label").prop("value", $(rows[0]).text());
	$("#norm_version").prop("value", $(rows[1]).text());
	$("#norm_description").prop("value", $(rows[2]).text());
	
	$("#norm_computable").prop("checked", $(rows[3]).attr("computable")=='Yes'?"checked":"");
	
	
	$("#addNormModel-title").text(MessageResolver("title.knowledgebase.Norm.Update", "Update a Norm"));
	$("#addnormbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#norm_form").prop("action", "/Save");
	$("#addNormModel").modal('toggle');
	return false;
}
