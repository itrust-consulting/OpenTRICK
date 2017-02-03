function editScenario(rowTrickId, isAdd) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (isAdd) {
			var selectedScenario = $("#section_scenario:visible :checked");
			if (selectedScenario.length != 0)
				return false;
			rowTrickId = undefined;
		} else if (rowTrickId == null || rowTrickId == undefined) {
			var selectedScenario = $("#section_scenario :checked");
			if (selectedScenario.length != 1)
				return false;
			rowTrickId = findTrickID(selectedScenario[0]);
		}

		$.ajax({
			url : context + (rowTrickId == null || rowTrickId == undefined || rowTrickId < 1 ? "/Analysis/Scenario/Add" : "/Analysis/Scenario/Edit/" + rowTrickId),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $modal = $("#addScenarioModal", new DOMParser().parseFromString(response, "text/html"));
				if (!$modal.length)
					unknowError();
				else {
					$("#addScenarioModal").replaceWith($modal);

					$("#scenario_asset_linked", $modal).on("change", function() {
						var $assetVlues = $("#scenario-asset-values", $modal), $assetTypeValues = $("#scenario-asset-type-values", $modal)
						if (this.checked) {
							$assetVlues.show();
							$assetTypeValues.hide();
						} else {
							$assetVlues.hide();
							$assetTypeValues.show();
						}
					}).trigger("change");

					if (application.analysisType == "QUANTITATIVE") {
						$modal.find(".slider").slider({
							reversed : true
						}).each(
								function() {
									$(this).on(
											"change",
											function(event) {
												$modal.find("#scenario_" + event.target.name + "_value").val(event.value.newValue);
												switch (event.target.name) {
												case "preventive":
												case "detective":
												case "limitative":
												case "corrective":
													var total = parseFloat($("#scenario_preventive_value", $modal).val())
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
					}
					$modal.modal("show");
				}
				return false;
			},
			error : unknowError
		});
	}
	return false;
}

function saveScenario(form) {
	try {
		var $progress = $("#loading-indicator").show(), $scenarioModal = $("#addScenarioModal"), $form = $("#" + form), scenario = serializeScenario($form);
		$(".label-danger", $scenarioModal).remove();
		$.ajax({
			url : context + "/Analysis/Scenario/Save",
			type : "post",
			data : JSON.stringify(scenario),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				for ( var error in response.errors) {
					var $errorElement = $("<label class='label label-danger' />").text(response.errors[error]);
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
					var type = "";
					for ( var assetType in scenario.assetTypes) {
						if (scenario.assetTypes[assetType] == '1')
							type += (type.length == 0 ? '' : ';') + assetType;
					}
					scenario.type = type;
					scenario.id = response.id;
					updateEstimationIteam("scenario", scenario);
				} else
					$("li:not(.active) a[href='#tab_scenario_general']", $scenarioModal).tab("show");
				return false;
			},
			error : unknowError
		}).complete(function() {
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

function deleteScenario(scenarioId) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (scenarioId == null || scenarioId == undefined) {
			var selectedScenario = findSelectItemIdBySection(("section_scenario"));
			if (!selectedScenario.length)
				return false;
			var text = selectedScenario.length == 1 ? MessageResolver("confirm.delete.scenario", "Are you sure, you want to delete this scenario") : MessageResolver(
					"confirm.delete.selected.scenario", "Are you sure, you want to delete selected scenarios");
			$("#confirm-dialog .modal-body").text(text);
			$("#confirm-dialog .btn-danger").click(function() {
				var $progress = $("#loading-indicator").show(), hasChange = false;
				while (selectedScenario.length) {
					rowTrickId = selectedScenario.pop();
					$.ajax({
						url : context + "/Analysis/Scenario/Delete/" + rowTrickId,
						type : 'POST',
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							if (response["success"] != undefined) {
								hasChange |= $("tr[data-trick-id='" + rowTrickId + "']", "#section_scenario").remove().length > 0;
								removeEstimation("scenario", [ rowTrickId ]);
							} else if (response["error"] != undefined) {
								$("#alert-dialog .modal-body").html(response["error"]);
								$("#alert-dialog").modal("toggle");
							} else {
								$("#alert-dialog .modal-body").html(MessageResolver("error.delete.scenario.unkown", "Unknown error occoured while deleting scenario"));
								$("#alert-dialog").modal("toggle");
							}
							return false;
						},
						error : unknowError
					}).complete(function() {
						if (!selectedScenario.length) {
							if (hasChange)
								reloadSection("section_scenario");
							else
								$progress.hide();
						}
					});
				}

			});
		} else {
			$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.scenario", "Are you sure, you want to delete this scenario"));
			$("#confirm-dialog .btn-danger").click(function() {
				$.ajax({
					url : context + "/Analysis/Scenario/Delete/" + scenarioId,
					contentType : "application/json;charset=UTF-8",
					async : true,
					success : function(reponse) {
						reloadSection("section_scenario");
						return false;
					},
					error : unknowError
				});
			});
		}
		$("#confirm-dialog").modal("toggle");
	}
	return false;

}

function serializeScenario($form) {
	var data = $form.serializeJSON(), assetTypes = {};

	for ( var field in data) {
		if (field.startsWith("assetTypes[")) {
			assetTypes[field.replace("assetTypes['", "").replace("']", "")] = parseInt(data[field] | "0");
			delete data[field];
		}
	}

	data["assetTypes"] = assetTypes;

	data["scenarioType"] = {
		"id" : parseInt(data["scenarioType"], 0),
		"type" : $("#scenario_scenariotype_id option:selected").text()
	};
	if (application.analysisType == "QUANTITATIVE") {
		var total = parseFloat(data['preventive']) + parseFloat(data['detective']) + parseFloat(data['limitative']) + parseFloat(data['corrective']), source = parseFloat(data['intentional'])
				+ parseFloat(data['accidental']) + parseFloat(data['environmental']) + parseFloat(data['internalThreat']) + parseFloat(data['externalThreat']);
		if (Math.abs(1 - total) > 0.001)
			throw "error.scenario.control.characteristic";
		if (source == 0)
			throw "error.scenario.threat.source";
	}
	return data;
}

function clearScenarioFormData() {
	var lang = findAnalysisLocale();
	$("#addScenarioModal #addScenarioModel-title").html(MessageResolver("label.scenario.add", "Add new scenario"));
	$("#addScenarioModal #scenario_id").attr("value", -1);
}

function selectScenario(scenarioId, value) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (scenarioId == undefined) {
			var selectedItem = findSelectItemIdBySection("section_scenario");
			if (!selectedItem.length)
				return false;
			var requiredUpdates = [];
			for (var i = 0; i < selectedItem.length; i++) {
				var selected = $("#section_scenario tbody tr[data-trick-id='" + selectedItem[i] + "']").attr("data-trick-selected");
				if (value != selected)
					requiredUpdates.push(selectedItem[i]);
			}
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Scenario/Select",
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(requiredUpdates, null, 2),
				type : 'post',
				success : function(reponse) {
					reloadSection('section_scenario');
					updateEstimationSelect("scenario", requiredUpdates, value);
					return false;
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			})
		} else {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Scenario/Select/" + scenarioId,
				contentType : "application/json;charset=UTF-8",
				success : function(reponse) {
					reloadSection("section_scenario");
					updateEstimationSelect("scenario", [ scenarioId ], value);
					return false;
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			})
		}
	}
	return false;
}