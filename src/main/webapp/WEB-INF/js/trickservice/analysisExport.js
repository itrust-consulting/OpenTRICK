function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		$.ajax({
			url : context + "/Analysis/Export/" + analysisId,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					application["taskManager"].Start();
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function exportAnalysisReport(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		$.ajax({
			url : context + "/Analysis/Export/Report/" + analysisId,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function exportAnalysisReportData(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		$.fileDownload(context + '/Analysis/Export/ReportData/' + analysisId).fail(unknowError);
		return false;
	} else
		permissionError();
	return false;
}

function exportRiskSheet(idAnalysis) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.EXPORT)) {
		$.ajax({
			url : context + "/Analysis/RiskRegister/Export",
			type : "post",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					new TaskManager().Start();
				else if (message["error"]) {
					$("#alert-dialog .modal-body").html(message["error"]);
					$("#alert-dialog").modal("toggle");
				} else
					unknowError()
			},
			error : unknowError
		});
	}else permissionError();
	return false;
}
