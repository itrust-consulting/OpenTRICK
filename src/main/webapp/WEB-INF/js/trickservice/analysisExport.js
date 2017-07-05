function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Export/" + analysisId,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				 else
					unknowError();
			},
			error: unknowError
		}).complete(()  => $progress.hide());
	} else
		permissionError();
	return false;
}

function exportAnalysisSOA(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/SOA/Export?idAnalysis=" + analysisId,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide());
	} else
		permissionError();
	return false;
}

function exportRawActionPlan(analysisId, type) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if(type == null || type == undefined)
		type = findAnalysisType("#section_analysis", analysisId);
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		if(ANALYSIS_TYPE.isHybrid(type)){
			var $dialogModal = $("#analysis-export-raw-action-plan-dialog");
			$dialogModal.modal("show").find("button[name='export']").unbind("click").one("click", (e) => {
				$dialogModal.modal("hide");
				window.location = context + "/Analysis/Export/Raw-Action-plan/"+analysisId+"/"+e.currentTarget.getAttribute("data-trick-type");
			});
		}else window.location = context + "/Analysis/Export/Raw-Action-plan/"+analysisId+"/"+type;
	} else
		permissionError();
	return false;
}


function exportAnalysisReport(analysisId, type) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if(type == null || type == undefined)
		type = findAnalysisType("#section_analysis", analysisId);
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		if(ANALYSIS_TYPE.isHybrid(type)){
			var $dialogModal = $("#analysis-export-dialog");
			$dialogModal.modal("show").find("button[name='export']").unbind("click").one("click", (e) => {
				$dialogModal.modal("hide");
				exportProcessing(analysisId, e.currentTarget.getAttribute("data-trick-type"));
			});
		}else exportProcessing(analysisId, type);
	} else
		permissionError();
	return false;
}

function exportProcessing(analysisId, type){
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Export/Report/" + analysisId+"/"+ type,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				application["taskManager"].Start();
			else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		},
		error: unknowError
	}).complete(()  =>$progress.hide());
}

function exportRiskSheet(idAnalysis, report) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/RiskRegister/RiskSheet/Form/Export?type=" + report,
			type: "GET",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $modal = $("div#exportRiskSheetForm", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length) {
					$("button[name='export']", $modal).on("click", function () {
						var data = $("form", $modal).serializeJSON();
						data.filter = {};
						for (var field in data) {
							if (field.indexOf("filter.") != -1) {
								data['filter'][field.replace("filter.", "")] = data[field];
								delete data[field];
							}
						}

						$.ajax({
							url: context + "/Analysis/RiskRegister/RiskSheet/Export",
							type: "post",
							data: JSON.stringify(data),
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								$(".label-danger", $modal).remove();
								if (response["success"] != undefined) {
									$modal.modal("hide");
									application["taskManager"].Start();
								} else if (response["error"])
									showDialog("#alert-dialog", response["error"]);
								 else {
									for (var error in response) {
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
												$(errorElement).appendTo($("select[name='" + error + "']", $modal).parent());
												break;
										}
									}

								}
							},
							error: unknowError
						});
					});

					$modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", () => $modal.remove())
				} else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide());
	} else
		permissionError();
	return false;
}

function exportRiskRegister(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/RiskRegister/Export",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined) 
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide());
	} else
		permissionError();
	return false;
}
