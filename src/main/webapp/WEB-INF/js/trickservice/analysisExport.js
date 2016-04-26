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
			url : context + "/Analysis/RiskRegister/Form/Export",
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $modal = $("div#exportRiskSheetForm", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length) {
					$("button[name='export']", $modal).on("click", function() {
						$.ajax({
							url : context + "/Analysis/RiskRegister/Export",
							type : "post",
							data : serializeForm($("form", $modal)),
							contentType : "application/json;charset=UTF-8",
							success : function(response, textStatus, jqXHR) {
								$(".label-danger",$modal).remove();
								if (response["success"] != undefined) {
									$modal.modal("hide");
									new TaskManager().Start();
								} else if (response["error"]) {
									$("#alert-dialog .modal-body").html(message["error"]);
									$("#alert-dialog").modal("toggle");
								} else {
									for ( var error in response) {
										var errorElement = document.createElement("label");
										errorElement.setAttribute("class", "label label-danger");
										$(errorElement).text(response[error]);
										switch (error) {
										case "impact":
										case "probability":
										case "direct":
										case "indirect":
										case "cia":
										case "owner":
											$(errorElement).appendTo($("select[name='" + error + "']", $modal));
											console.log(response[error]);
											break;
										}
									}

								}
							},
							error : unknowError
						});
					});

					$modal.appendTo("#widgets");

					$modal.on("hidden.bs.modal", function() {
						$modal.remove();
					});

					$modal.modal("show");
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}
