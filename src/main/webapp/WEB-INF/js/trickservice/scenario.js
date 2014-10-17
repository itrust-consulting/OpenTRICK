function editScenario(rowTrickId, isAdd) {
	if (isAdd)
		rowTrickId = undefined;
	else if (rowTrickId == null || rowTrickId == undefined) {
		var selectedScenario = $("#section_scenario :checked");
		if (selectedScenario.length != 1)
			return false;
		rowTrickId = findTrickID(selectedScenario[0]);
	}

	$.ajax({
		url : context + (rowTrickId == null || rowTrickId == undefined || rowTrickId < 1 ? "/Analysis/Scenario/Add" : "/Analysis/Scenario/Edit/" + rowTrickId),
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((addScenarioModal = doc.getElementById("addScenarioModal")) == null)
				return false;
			if ($("#addScenarioModal").length)
				$("#addScenarioModal").html($(addScenarioModal).html());
			$("#addScenarioModal").modal("toggle");
			return false;
		},
		error : unknowError
	});
	return false;
}

function saveScenario(form) {
	return $.ajax({
		url : context + "/Analysis/Scenario/Save",
		type : "post",
		data : serializeScenarioForm(form),
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			var label = $("#addScenarioModal .label-danger");
			if (label.length)
				label.remove();
			var alert = $("#addScenarioModal [class='alert alert-danger alert-dismissable']");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);
				switch (error) {
				case "name":
					$(errorElement).appendTo($("#scenario_form #scenario_name").parent());
					break;
				case "scenarioType":
					$(errorElement).appendTo($("#scenario_form #scenario_scenariotype_id").parent());
					break;
				case "description":
					$(errorElement).appendTo($("#scenario_form #scenario_description").parent());
					break;
				case "selected":
					$(errorElement).appendTo($("#scenario_form #asset_selected").parent());
					break;
				case "scenario":
					var errorElement = document.createElement("div");
					errorElement.setAttribute("class", "alert alert-danger alert-dismissable");

					var tmpelement = document.createElement("button");
					tmpelement.setAttribute("class", "close");
					tmpelement.setAttribute("data-dismiss", "alert");
					tmpelement.setAttribute("aria-hidden", "true");
					$(tmpelement).html("&times;");

					$(errorElement).text(response[error]);

					$(tmpelement).appendTo($(errorElement));

					$(errorElement).insertBefore($("#scenario_form"));
					break;
				}
			}
			if (!$("#addScenarioModal .label-danger").length && !$("#addScenarioModal .alert-danger").length) {
				$("#addScenarioModal").modal("toggle");
				reloadSection("section_scenario");
			}
			return false;
		},
		error : unknowError
	});
}

function deleteScenario(scenarioId) {
	if (scenarioId == null || scenarioId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_scenario"));
		if (!selectedScenario.length)
			return false;
		var lang = $("#nav-container").attr("trick-language");
		var text = selectedScenario.length == 1 ? MessageResolver("confirm.delete.scenario", "Are you sure, you want to delete this scenario", null, lang) : MessageResolver("confirm.delete.selected.scenario",
				"Are you sure, you want to delete selected scenarios", null, lang);
		$("#confirm-dialog .modal-body").text(text);
		$("#confirm-dialog .btn-danger").click(function() {
			while (selectedScenario.length) {
				rowTrickId = selectedScenario.pop();
				$.ajax({
					url : context + "/Analysis/Scenario/Delete/" + rowTrickId,
					contentType : "application/json;charset=UTF-8",
					async : false,
					success : function(response) {
						var trickSelect = parseJson(response);
						if (trickSelect != undefined && trickSelect["success"] != undefined) {
							var row = $("#section_scenario tr[trick-id='" + rowTrickId + "']");
							var checked = $("#section_scenario tr[trick-id='" + rowTrickId + "'] :checked");
							if (checked.length)
								$(checked).removeAttr("checked");
							if (row.length)
								$(row).remove();
						}
						return false;
					},
					error : unknowError
				});
			}
			reloadSection('section_scenario');
		});
	} else {
		$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.scenario", "Are you sure, you want to delete this scenario", null, lang));
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
	return false;

}

function serializeScenarioForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	data["scenarioType"] = {
		"id" : parseInt(data["scenarioType"], 0),
		"type" : $("#scenario_scenariotype_id option:selected").text()
	};
	return JSON.stringify(data);
}

function clearScenarioFormData() {
	var lang = $("#nav-container").attr("trick-language");
	$("#addScenarioModal #addScenarioModel-title").html(MessageResolver("label.scenario.add", "Add new scenario", null, lang));
	$("#addScenarioModal #scenario_id").attr("value", -1);
}

function selectScenario(scenarioId, value) {
	if (scenarioId == undefined) {
		var selectedItem = findSelectItemIdBySection("section_scenario");
		if (!selectedItem.length)
			return false;
		var requiredUpdate = [];
		for (var i = 0; i < selectedItem.length; i++) {
			var selected = $("#section_scenario tbody tr[trick-id='" + selectedItem[i] + "']").attr("trick-selected");
			if (value != selected)
				requiredUpdate.push(selectedItem[i]);
		}
		$.ajax({
			url : context + "/Analysis/Scenario/Select",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(requiredUpdate, null, 2),
			type : 'post',
			success : function(reponse) {
				reloadSection('section_scenario');
				return false;
			},
			error : unknowError
		});
	} else {
		$.ajax({
			url : context + "/Analysis/Scenario/Select/" + scenarioId,
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				reloadSection("section_scenario");
				return false;
			},
			error : unknowError
		});
	}
	return false;
}