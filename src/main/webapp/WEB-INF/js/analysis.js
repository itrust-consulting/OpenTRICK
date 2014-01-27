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
			result = data == "" ? true : showError(document.getElementById(form), data);
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

function deleteAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.DELETE)) {
		$("#deleteAnalysisBody").html(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis") + "?");
		$("#deleteanalysisbuttonYes").click(function() {
			$.ajax({
				url : context + "/Analysis/Delete/" + analysisId,
				type : "POST",
				contentType : "application/json",
				success : function(response) {
					reloadSection("section_analysis");
					return false;
				}
			});
			$("#deleteAnalysisModel").modal('hide');
			return false;
		});
		$("#deleteAnalysisModel").modal('toggle');
	} else
		permissionError();
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

	$("#addAnalysisModel-title").text(MessageResolver("title.Administration.Analysis.Add", "Create a new Analysis"));
	$("#addAnalysisButton").text(MessageResolver("label.action.create", "Create"));
	$("#analysis_form").prop("action", "/Save");
	$("#addAnalysisModel").modal('toggle');
	return false;
}

function editSingleAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.MODIFY)) {
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

		$("#addAnalysisModel-title").text(MessageResolver("title.analysis.Update", "Update an Analysis"));
		$("#addAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
		$("#analysis_form").prop("action", "/Save");
		$("#addAnalysisModel").modal('toggle');
	} else
		permissionError();
	return false;
}

function selectAnalysis(analysisId) {

	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.READ))
		window.location.replace(context + "/Analysis/" + analysisId + "/Select");
	else
		permissionError();
}

function calculateActionPlan(analysisId) {
	
	var analysisID = -1;
	
	if (analysisId == null || analysisId == undefined) {
		
		
	
		var selectedAnalysis = findSelectItemIdBySection(("section_analysis"));
		if (!selectedAnalysis.length)
			return false;
		while (selectedAnalysis.length) {
			rowTrickId = selectedAnalysis.pop();
			if (userCan(rowTrickId, ANALYSIS_RIGHT.CALCULATE_ACTIONPLAN)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}
	
	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.CALCULATE_ACTIONPLAN)) {
		
		var data = {};
		
		data["id"] = analysisID;
		
		$.ajax({
			url : context + "/ActionPlan/Compute",
			type : "post",
			data : JSON.stringify(data),
			async : true,
			contentType : "application/json",
			success : function(response) {
				if (response["success"] != undefined) {
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
	} else
		permissionError();
	return false;
}

function calculateRiskRegister(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.CALCULATE_RISK_REGISTER)) {
		href = "${pageContext.request.contextPath}/analysis/" + analysisId + "/compute/riskRegister";
	} else
		permissionError();
}

function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		href = "${pageContext.request.contextPath}/Analysis/${analysis.id}/Export";
	} else
		permissionError();
}