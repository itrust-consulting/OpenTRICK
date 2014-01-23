function saveAnalysis(form) {
	result = "";
	return $.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			var alert = $("#addAnalysisModel .alert");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				errorMessage = parseJson(response[error]);
				if (errorMessage.error != undefined)
					data += errorMessage.error + "\n";
			}
			result = data == "" ? true : showError(document
					.getElementById(form), data);
			if (result) {
				$("#addAnalysisModel").modal("hide");
				reloadSection("section_analysis");
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
			reloadSection("section_analysis");
			return false;
		}
	});
	return false;
}

function deleteAnalysis(analysisId) {
	$("#deleteAnalysisBody").html(
			MessageResolver("label.analysis.question.delete",
					"Are you sure that you want to delete the analysis")
					+ "?");
	$("#deleteanalysisbuttonYes").attr("onclick",
			"deleteAnAnalysis(" + analysisId + ")");
	$("#deleteAnalysisModel").modal('toggle');
	return false;
}

function newAnalysis() {

	$.ajax({
		url : context + "/Analysis/New",
		type : "get",
		contentType : "application/json",
		success : function(response) {
			$("#analysis_form").html(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});

	$("#addAnalysisModel-title").text(
			MessageResolver("title.Administration.Analysis.Add",
					"Create a new Analysis"));
	$("#addAnalysisButton").text(
			MessageResolver("label.action.create", "Create"));
	$("#analysis_form").prop("action", "/Save");
	$("#addAnalysisModel").modal('toggle');
	return false;
}

function editSingleAnalysis(analysisId) {
	$.ajax({
		url : context + "/Analysis/Edit/" + analysisId,
		type : "get",
		contentType : "application/json",
		success : function(response) {
			$("#analysis_form").html(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});

	$("#addAnalysisModel-title").text(
			MessageResolver("title.analysis.Update", "Update an Analysis"));
	$("#addAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
	$("#analysis_form").prop("action", "/Save");
	$("#addAnalysisModel").modal('toggle');
	return false;
}

function selectAnalysis(analysisId) {
	window.location.replace(context + "/Analysis/" + analysisId + "/Select");
}

function calculateActionPlan(analysisId) {
	$.ajax({
		url : context + "/ActionPlan/Compute",
		type : "get",
		async:true,
		contentType : "application/json",
		success : function(response) {
			var message = parseJson(response);
			if (message["success"] != undefined) {
				if (taskManager == undefined)
					taskManager = new TaskManager();
				taskManager.Start();
			} else if (message["error"]) {
				$("#alert-dialog .modal-body").html(message["error"]);
				$("#alert-dialog").modal("toggle");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	return false;
}

function calculateRiskRegister(analysisId) {
	href = "${pageContext.request.contextPath}/analysis/${analysis.id}/compute/riskRegister";
	// TODO
}

function exportAnalysis(analysisId) {
	href="${pageContext.request.contextPath}/Analysis/${analysis.id}/Export";
	// TODO	
}
