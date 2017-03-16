$(document).ready(function () {
	application["settings-fixed-header"] = {
		fixedOffset: $(".nav-tab"),
		marginTop: application.fixedOffset
	};

	fixTableHeader("#tab-container table");

	$("input[type='checkbox']").removeAttr("checked");
	$("#section_kb_measure #languageselect").change(function () {
		showMeasures($("#section_kb_measure").attr("data-standard-id"), $(this).val());
	});

});

function editSingleAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Edit/" + analysisId,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $view = $("#editAnalysisModel",new DOMParser().parseFromString(response, "text/html"));
			if (!$view.length) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
			} else {
				$view.appendTo("#widget").modal('show').on("hidden.bs.modal", () => $view.remove());
				$("button[name='save']", $view).on("click", e => {
					$progress.show();
					$(".label-danger", $view).remove();
					$.ajax({
						url: context + "/Analysis/Save",
						type: "post",
						data: serializeForm($("form", $view)),
						contentType: "application/json;charset=UTF-8",
						success: function (response, textStatus, jqXHR) {
							var hasError = false;
							for (var error in response) {
								var $errorElement = $("<label class='label label-danger' />").text(response[error]);
								switch (error) {
									case "analysislanguage":
										$errorElement.appendTo($("#analysislanguagecontainer", $view));
										break;
									case "comment":
										$errorElement.appendTo($("input[name='comment']", $view).parent());
										break;
									default:
										showDialog("#alert-dialog", response[error]);
										break;
								}
								hasError = true;
							}
							if (!hasError) {
								$view.modal("hide");
								reloadSection("section_profile_analysis");
							}
						},
						error: unknowError
					}).complete(function () {
						$progress.hide();
					});
					
				});
			}
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Export/" + analysisId,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				application["taskManager"].Start();
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function setAsDefaultProfile(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/AnalysisProfile/SetDefaultProfile/" + analysisId,
		type: "POST",
		data: "\"" + $("tr[data-trick-id='" + analysisId + "']", "#section_profile_analysis").attr('data-trick-type') + "\"",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			reloadSection("section_profile_analysis");
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;

}

function selectAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	$("#loading-indicator").show();
	setTimeout(() =>window.location.replace(context + "/Analysis/" + analysisId + "/Select?open=" + OPEN_MODE.EDIT.value), 0);
	return false;
}

function deleteAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	$("#deleteAnalysisBody").html(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));
	$("#deleteanalysisbuttonYes").unbind().one("click", function () {
		$("#deleteAnalysisModel").modal('hide');
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Delete/" + analysisId,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.success != undefined)
					reloadSection("section_profile_analysis");
				else if (response.error != undefined)
					showDialog("#alert-dialog", response.error);
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
		return false;
	});
	$("#deleteAnalysisModel").modal('show');
	return false;
}