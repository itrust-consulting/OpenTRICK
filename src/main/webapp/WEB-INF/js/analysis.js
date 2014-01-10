function saveAnalysis(form) {
	result = "";
	return $.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addAnalysisModel").modal("hide");
				// TODO reload analysis
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function deleteAnAnalysis(analysisId) {
	$.ajax({
		url : context + "/Analysis/Delete/" + analysisId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			if (response == false) {
				// TODO 
			} else {
				// TODO
			}
			return false;
		}
	});
	return false;
}

function deleteAnalysis(analysisId) {
	$("#deleteAnalysisBody").html(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis")+"?");
	$("#deleteanalysisbuttonYes").attr("onclick", "deleteAAnalysis(" + userId + ")");
	$("#deleteAnalysisModel").modal('toggle');
	return false;
}

function newAnalysis() {
	$("#analysis_id").prop("value", "");
	$("#analysis_identifier").prop("value", "");
	$("#analysis_version").prop("disabled", "disabled");
	$("#analysis_creationDate").prop("value", "");
	$("#analysis_basedOnAnalysis").prop("value", $(rows[1]).text());
	$("#analysis_owner").prop("value", $(rows[2]).text());
	$("#analysis_empty").prop("value", $(rows[3]).text());
	$("#analysis_customer").prop("value", $(rows[3]).text());
	$("#analysis_language").prop("value", $(rows[3]).text());
	$("#analysis_label").prop("value", $(rows[3]).text());
	
	$.ajax({
		url : context + "/Admin/Roles",
		type : "get",
		contentType : "application/json",
		success : function(response) {
				$("#rolescontainer").html(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	
	$("#addUserModel-title").text(MessageResolver("title.Administration.User.Add", "Add a new User"));
	$("#addUserbutton").text(MessageResolver("label.action.add", "Add"));
	$("#user_form").prop("action", "/Save");
	$("#addUserModel").modal('toggle');
	return false;
}

function editSingleAnalysis(analysisId) {
	$.ajax({
		url : context + "/Analysis/Edit"+analysisId,
		type : "get",
		contentType : "application/json",
		success : function(response) {
				$("#analysis_form").html(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	
	$("#addAnalysisModel-title").text(MessageResolver("title.analysis.Update", "Update an Analysis"));
	$("#addAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
	$("#addAnalysisModel").modal('toggle');
	return false;
}
