
/**
 * This file contains the JavaScript code for the knowledgebase functionality in the TrickService application.
 * It includes functions for editing, exporting, setting as default, selecting, and deleting analysis profiles.
 * The code also includes event handlers for various actions and AJAX requests for data manipulation.
 * 
 * @version 1.0.0
 */
$(document).ready(function () {
	application["settings-fixed-header"] = {
		fixedOffset: $(".nav-tab"),
		marginTop: application.fixedOffset
	};
	
	$("#section_kb_measure #languageselect").change(function () {
		showMeasures($("#section_kb_measure").attr("data-standard-id"), $(this).val());
	});
	
	setTimeout(() => fixTableHeader("#tab-container table"), 300);
	
	application["reportTemplateDownloadItemLimit"] = 10;
});

/**
 * Edits a single analysis.
 * 
 * @param {number} analysisId - The ID of the analysis to edit.
 * @returns {boolean} Returns false if the analysis ID is null or undefined, or if there are multiple selected scenarios. Otherwise, returns true.
 */
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

/**
 * Exports the analysis with the given ID.
 * If the analysis ID is not provided, it will use the selected scenario ID from the "section_profile_analysis" section.
 * 
 * @param {number} analysisId - The ID of the analysis to export.
 * @returns {boolean} - Returns false if the analysis ID is not found or if there was an error during the export process.
 */
function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Data-manager/Sqlite/Export-process?idAnalysis=" + analysisId,
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

/**
 * Sets the specified analysis profile as the default profile.
 * If no analysisId is provided, it will use the selected scenario from the "section_profile_analysis" section.
 * 
 * @param {number} analysisId - The ID of the analysis profile to set as default.
 * @returns {boolean} - Returns false if the analysisId is not valid or if the profile is already the default, otherwise returns true.
 */
function setAsDefaultProfile(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if(!isDefaultProfile(analysisId)){
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
	}
	return false;

}

/**
 * Selects an analysis based on the provided analysisId.
 * If analysisId is null or undefined, it selects the analysis based on the selectedScenario.
 * It then shows the loading indicator and redirects to the analysis page for editing.
 *
 * @param {string} analysisId - The ID of the analysis to be selected.
 * @returns {boolean} - Returns false.
 */
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

/**
 * Deletes an analysis profile.
 * If the analysisId is not provided, it will attempt to find the selected scenario and use its ID.
 * If the analysis profile is not a default profile, it will prompt the user for confirmation before deleting.
 * 
 * @param {number} analysisId - The ID of the analysis profile to delete.
 * @returns {boolean} - Returns false.
 */
function deleteAnalysisProfile(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	
	if(!isDefaultProfile(analysisId)){
		$("#deleteAnalysisBody").html(MessageResolver("label.analysis.profile.question.delete", "Are you sure that you want to delete this analysis profile?"));
		$("#deleteanalysisbuttonYes").unbind().one("click", function () {
			$("#deleteAnalysisModel").modal('hide');
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/AnalysisProfile/Delete/" + analysisId,
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
	}
	return false;
}

/**
 * Checks if a given analysis ID has a default profile.
 * If the `idAnalysis` parameter is not provided, it checks if the currently selected profile has a default value.
 *
 * @param {string} [idAnalysis] - The ID of the analysis to check for a default profile.
 * @returns {boolean} - Returns `true` if the analysis has a default profile, otherwise `false`.
 */
function isDefaultProfile(idAnalysis){
	return idAnalysis === undefined?  $("#section_profile_analysis tbody>tr>td>input:checked").parent().parent().attr("data-trick-profile-default") === "true" : $("#section_profile_analysis tbody>tr[data-trick-id='"+idAnalysis+"']").attr("data-trick-profile-default") === "true";
}