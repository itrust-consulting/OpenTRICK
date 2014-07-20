function calculateRiskRegister(analysisId) {

	var analysisID = -1;

	if (analysisId == null || analysisId == undefined) {

		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (!selectedAnalysis.length)
			return false;
		while (selectedAnalysis.length) {
			rowTrickId = selectedAnalysis.pop();
			if (userCan(rowTrickId, ANALYSIS_RIGHT.CALCULATE_RISK_REGISTER)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}

	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.CALCULATE_RISK_REGISTER)) {

		var data = {};

		data["id"] = analysisID;

		$.ajax({
			url : context + "/RiskRegister/Compute",
			type : "post",
			data : JSON.stringify(data),
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["success"] != undefined)
					new TaskManager().Start();
				else if (message["error"]) {
					$("#alert-dialog .modal-body").html(message["error"]);
					$("#alert-dialog").modal("toggle");
				}
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}