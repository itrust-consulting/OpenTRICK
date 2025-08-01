
/**
 * Edits a scenario based on the provided parameters.
 * 
 * @param {number} rowTrickId - The ID of the row trick.
 * @param {boolean} isAdding - Indicates whether the scenario is being added or edited.
 * @returns {boolean} - Returns false.
 */
function editScenario(rowTrickId, isAdding) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (!isAdding && (rowTrickId == null || rowTrickId == undefined)) {
			let selectedScenario = $("#section_scenario :checked");
			if (selectedScenario.length != 1)
				return false;
			rowTrickId = findTrickID(selectedScenario[0]);
		}
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + (rowTrickId == null || rowTrickId == undefined || rowTrickId < 1 ? "/Analysis/Scenario/Add" : "/Analysis/Scenario/Edit/" + rowTrickId),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $modal = $("#addScenarioModal", new DOMParser().parseFromString(response, "text/html"));

				if (!$modal.length)
					unknowError();
				else {
					;
					$("input[name='assetLinked'][type='radio']", $modal).on("change", function () {
						if (this.checked) {
							let $assetVlues = $("#scenario-asset-values", $modal), $assetTypeValues = $("#scenario-asset-type-values", $modal)
							if (this.value == "true") {
								$assetVlues.show();
								$assetTypeValues.hide();
							} else {
								$assetVlues.hide();
								$assetTypeValues.show();
							}
						}
					}).trigger("change");

					if (application.analysisType.isQuantitative()) {
						let scrollY = window.scrollY;
						let scrollX = window.scrollX;
						$modal.appendTo("#widgets").modal("show").on("shown.bs.modal", (e) => {
							$modal.find("input[type='range'][orient='vertical']").each(
								function () {
									$(this).on(
										"change",
										function (event) {
											$modal.find("#scenario_" + event.target.name + "_value").val(event.target.value);
											event.target.title = event.target.value;
											switch (event.target.name) {
												case "preventive":
												case "detective":
												case "limitative":
												case "corrective":
													let total = parseFloat($("#scenario_preventive_value", $modal).val())
														+ parseFloat($("#scenario_detective_value", $modal).val()) + parseFloat($("#scenario_limitative_value", $modal).val())
														+ parseFloat($("#scenario_corrective_value", $modal).val());
													if (Math.abs(1 - total) > 0.01)
														$(".pdlc", $modal).removeClass("success").addClass("danger");
													else
														$(".pdlc", $modal).removeClass("danger").addClass("success");
													break;
												default:
													break;
											}
										});
								});
						}).on("hidden.bs.modal", (e) => {
							$modal.remove();
						});
						$(document.body).addClass("modal-open");
					} else $modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", (e) => $modal.remove());
				}
				return false;
			},
			error: unknowError
		}).complete(() => $progress.hide());
	}
	return false;
}

/**
 * Saves a scenario based on the provided form data.
 * 
 * @param {string} form - The ID of the form containing the scenario data.
 * @returns {boolean} - Returns false.
 */
function saveScenario(form) {
	try {
		let $progress = $("#loading-indicator").show(), $scenarioModal = $("#addScenarioModal"), $form = $("#" + form), scenario = serializeScenario($form);
		$(".label-danger", $scenarioModal).remove();
		$.ajax({
			url: context + "/Analysis/Scenario/Save",
			type: "post",
			data: JSON.stringify(scenario),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				for (let error in response.errors) {
					let $errorElement = $("<label class='label label-danger' />").text(response.errors[error]);
					switch (error) {
						case "name":
							$errorElement.appendTo($("#scenario_name", $scenarioModal).parent());
							break;
						case "scenarioType":
							$errorElement.appendTo($("#scenario_scenariotype_id", $scenarioModal).parent());
							break;
						case "description":
							$errorElement.appendTo($("#scenario_description", $scenarioModal).parent());
							break;
						case "selected":
							$errorElement.appendTo($("#asset_selected", $scenarioModal).parent());
							break;
						case "scenario":
							$errorElement.appendTo($("#error_scenario_container"));
							break;
					}
				}
				if (!$(".label-danger", $scenarioModal).length) {
					$scenarioModal.modal("hide");
					reloadSection("section_scenario");
					if (!application.isProfile) {
						scenario.id = response.id;
						if (scenario.assetLinked === 'true')
							delete scenario['assetTypeValues'];
						else
							delete scenario['assetValues'];
						updateEstimationIteam("scenario", scenario);

					}
				} else
					$("li:not(.active) a[href='#tab_scenario_general']", $scenarioModal).tab("show");
				return false;
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} catch (e) {
		$progress.hide();
		switch (e) {
			case "error.scenario.control.characteristic":
				$("<label class='label label-danger'></label>").text(MessageResolver(e, "Please check control characteristics, sum must be 1"))
					.appendTo($("#error_scenario_container"));
				break;
			case "error.scenario.threat.source":
				$("<label class='label label-danger'></label>").text(MessageResolver(e, "Please define a threat source")).appendTo($("#error_scenario_container"));
				break;
			default:
				$("<label class='label label-danger'></label>").text(MessageResolver("error.unknown.occurred", "An unknown error occurred")).appendTo($("#error_scenario_container"));
				break;
		}
		if (application.analysisType == "QUANTITATIVE")
			$("#addScenarioModal li:not(.active) a[href='#tab_scenario_properties']").tab("show");
	}
	return false;
}

/**
 * Deletes a scenario with the specified ID.
 * 
 * @param {number} scenarioId - The ID of the scenario to delete.
 * @returns {boolean} - Returns false.
 */
function deleteScenario(scenarioId) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		let selectedScenario = (scenarioId == null || scenarioId == undefined) ? findSelectItemIdBySection("section_scenario") : [scenarioId];
		if (!selectedScenario.length)
			return false;
		let $confirmDialog = showDialog("#confirm-dialog", selectedScenario.length == 1 ? MessageResolver("confirm.delete.scenario", "Are you sure, you want to delete this scenario") : MessageResolver(
			"confirm.delete.selected.scenario", "Are you sure, you want to delete selected scenarios"));
		$(".btn-danger", $confirmDialog).click(function () {
			let $progress = $("#loading-indicator").show();
			let hasChange = false;
			$confirmDialog.modal("hide");
			$.ajax({
				url: context + "/Analysis/Scenario/Delete",
				type: 'DELETE',
				data: JSON.stringify(selectedScenario),
				contentType: "application/json;charset=UTF-8",
				success: function (response) {
					let deletedIds = response["ids"];
					if (Array.isArray(deletedIds)) {
						for (const scenarioId of deletedIds) {
							hasChange |= $("tr[data-trick-id='" + scenarioId + "']", "#section_scenario").remove().length > 0;
							if (!application.isProfile)
								removeEstimation("scenario", [scenarioId]);
						}
					} else if (response["error"] != undefined)
						showDialog("#alert-dialog", response["error"]);
					else
						showDialog("#alert-dialog", MessageResolver("error.delete.scenario.unkown", "Unknown error occoured while deleting scenario"));
					return false;
				},
				error: unknowError
			}).complete(function () {
				if (hasChange)
					reloadSection("section_scenario");
				$progress.hide();
			});
		});
	}
	return false;
}

/**
 * Serializes the scenario data from the provided form.
 * 
 * @param {jQuery} $form - The jQuery object representing the form.
 * @returns {Object} - The serialized scenario data.
 */
function serializeScenario($form) {
	let data = $form.serializeJSON();

	if (data["assetLinked"] === "true")
		data["assetTypeValues"] = [];
	else
		data["assetValues"] = [];

	data["scenarioType"] = {
		"id": parseInt(data["scenarioType"], 0)
	};

	if (application.analysisType == "QUANTITATIVE") {
		let total = parseFloat(data['preventive']) + parseFloat(data['detective']) + parseFloat(data['limitative']) + parseFloat(data['corrective']), source = parseFloat(data['intentional'])
			+ parseFloat(data['accidental']) + parseFloat(data['environmental']) + parseFloat(data['internalThreat']) + parseFloat(data['externalThreat']);
		if (Math.abs(1 - total) > 0.001)
			throw "error.scenario.control.characteristic";
		if (source == 0)
			throw "error.scenario.threat.source";
	}
	return data;
}

/**
 * Clears the form data for the scenario.
 */
function clearScenarioFormData() {
	$("#addScenarioModal #addScenarioModel-title").html(MessageResolver("label.scenario.add", "Add new scenario"));
	$("#addScenarioModal #scenario_id").attr("value", -1);
}

/**
 * Selects a scenario with the specified ID and value.
 * 
 * @param {number} scenarioId - The ID of the scenario to select.
 * @param {string} value - The value to set for the selected scenario.
 * @returns {boolean} - Returns false.
 */
function selectScenario(scenarioId, value) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (scenarioId == undefined) {
			let selectedItem = findSelectItemIdBySection("section_scenario");
			if (!selectedItem.length)
				return false;
			let requiredUpdates = [];
			for (let i = 0; i < selectedItem.length; i++) {
				let selected = $("#section_scenario tbody tr[data-trick-id='" + selectedItem[i] + "']").attr("data-trick-selected");
				if (value != selected)
					requiredUpdates.push(selectedItem[i]);
			}
			if (requiredUpdates.length) {
				let $progress = $("#loading-indicator").show();
				$.ajax({
					url: context + "/Analysis/Scenario/Select",
					contentType: "application/json;charset=UTF-8",
					data: JSON.stringify(requiredUpdates, null, 2),
					type: 'post',
					success: function (reponse) {
						reloadSection('section_scenario');
						if (!application.isProfile)
							updateEstimationSelect("scenario", requiredUpdates, value);
						return false;
					},
					error: unknowError
				}).complete(function () {
					$progress.hide();
				});
			}
		} else {
			let $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/Analysis/Scenario/Select/" + scenarioId,
				contentType: "application/json;charset=UTF-8",
				success: function (reponse) {
					reloadSection("section_scenario");
					if (!application.isProfile)
						updateEstimationSelect("scenario", [scenarioId], value);
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			})
		}
	}
	return false;
}