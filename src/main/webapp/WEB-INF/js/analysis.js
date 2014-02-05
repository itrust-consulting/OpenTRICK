function saveAnalysis(form) {
	$("#addAnalysisModel .progress").show();
	$("#addAnalysisModel #addAnalysisButton").prop("disabled", true);
	$.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			$("#addAnalysisModel .progress").hide();
			$("#addAnalysisModel #addAnalysisButton").prop("disabled", false);
			var alert = $("#addAnalysisModel .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "analysiscustomer":
					$(errorElement).appendTo($("#analysiscustomercontainer"));
					break;

				case "analysislanguage":
					$(errorElement).appendTo($("#analysislanguagecontainer"));
					break;

				case "comment":
					$(errorElement).appendTo($("#analysis_label").parent());

					break;

				case "profile":
					$(errorElement)
							.appendTo(
									$("#analysis_form select[name='profile']")
											.parent());
					break;

				case "author":
					$(errorElement).appendTo(
							$("#analysis_form input[name='author']").parent());
					break;

				case "version":
					$(errorElement).appendTo($("#analysis_version").parent());
					break;

				case "analysis":
					$(errorElement)
							.appendTo($("#addAnalysisModel .modal-body"));
					break;
				}
			}
			if (!$("#addAnalysisModel .label-danger").length) {
				$("#addAnalysisModel").modal("hide");
				reloadSection("section_analysis");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
	});
	return false;
}

function deleteAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.DELETE)) {
		$("#deleteAnalysisBody").html(
				MessageResolver("label.analysis.question.delete",
						"Are you sure that you want to delete the analysis")
						+ "?");
		$("#deleteanalysisbuttonYes").click(function() {
			$.ajax({
				url : context + "/Analysis/Delete/" + analysisId,
				type : "GET",
				contentType : "application/json",
				success : function(response) {
					$("#deleteAnalysisModel").modal('hide');
					if (response.success != undefined) {
						reloadSection("section_analysis");
					} else if (response.error != undefined) {
						$("#alert-dialog .modal-body").html(response.error);
						$("#alert-dialog").modal("toggle");
					}
					return false;
				}
			});
			return false;
		});
	} else
		permissionError();
	return false;
}

function createAnalysisProfile(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.READ)) {
		$.ajax({
			url : context + "/AnalysisProfile/Add/" + analysisId,
			type : "get",
			contentType : "application/json",
			success : function(response) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ((analysisProfile = doc
						.getElementById("analysisProfileModal")) == null)
					return false;
				$(analysisProfile).appendTo("#wrap");
				$(analysisProfile).on('hidden.bs.modal', function() {
					$(analysisProfile).remove();
				});
				$(analysisProfile).modal("toggle");

			},
			error : function(jqXHR, textStatus, errorThrown) {
				return false;
			},
		});
	}
	return false;
}

function saveAnalysisProfile(form) {
	$
			.ajax({
				url : context + "/AnalysisProfile/Save",
				type : "post",
				aync : true,
				data : $("#" + form).serialize(),
				success : function(response) {
					if (response.flag != undefined) {
						var progressBar = new ProgressBar();
						progressBar.Initialise();
						$(progressBar.progress)
								.appendTo($("#" + form).parent());
						callback = {
							failed : function() {
								progressBar.Distroy();
								$("#analysisProfileModal").modal("toggle");
								$("#alert-dialog .modal-body")
										.html(
												MessageResolver(
														"error.unknown.task.execution",
														"An unknown error occurred during the execution of the task"));
							},
							success : function() {
								progressBar.Distroy();
								$("#analysisProfileModal").modal("toggle");
							}
						};
						progressBar.OnComplete(callback.success);
						updateStatus(progressBar, response.taskID, callback,
								response);
					} else {
						var parser = new DOMParser();
						var doc = parser.parseFromString(response, "text/html");
						if ((error = $(doc).find("#analysisProfileModal")).length) {
							$("#analysisProfileModal .modal-body").html(
									$(error).find(".modal-body"));
							return false;
						}
					}
				}
			});
	return false;
}

function newAnalysis() {
	$
			.ajax({
				url : context + "/Analysis/New",
				type : "get",
				contentType : "application/json",
				success : function(response) {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					if ((form = doc.getElementById("form_add_analysis")) == null) {
						$("#alert-dialog .modal-body")
								.html(
										MessageResolver(
												"error.unknown.data.loading",
												"An unknown error occurred during data loading"));
						$("#alert-dialog").modal("toggle");
					} else {
						$("#analysis_form").html($(form).html());
						$("#addAnalysisModel-title").text(
								MessageResolver(
										"title.Administration.Analysis.Add",
										"Create a new Analysis"));
						$("#addAnalysisButton")
								.text(
										MessageResolver("label.action.create",
												"Create"));
						$("#analysis_form").prop("action", "/Save");
						$("#addAnalysisModel").modal('toggle');
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					return false;
				},
			});

	return false;
}

function editSingleAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.MODIFY)) {
		$
				.ajax({
					url : context + "/Analysis/Edit/" + analysisId,
					type : "get",
					contentType : "application/json",
					success : function(response) {
						var parser = new DOMParser();
						var doc = parser.parseFromString(response, "text/html");
						if ((form = doc.getElementById("form_edit_analysis")) == null) {
							$("#alert-dialog .modal-body")
									.html(
											MessageResolver(
													"error.unknown.data.loading",
													"An unknown error occurred during data loading"));
							$("#alert-dialog").modal("toggle");
						} else {
							$("#analysis_form").html($(form).html());
							$("#addAnalysisModel-title").text(
									MessageResolver("title.analysis.Update",
											"Update an Analysis"));
							$("#addAnalysisButton")
									.text(
											MessageResolver(
													"label.action.edit", "Edit"));
							$("#analysis_form").prop("action", "/Save");
							$("#addAnalysisModel").modal('toggle');
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						return fase;
					},
				});
	} else
		permissionError();
	return false;
}

function selectAnalysis(analysisId) {

	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.READ))
		window.location
				.replace(context + "/Analysis/" + analysisId + "/Select");
	else
		permissionError();
}

function calculateActionPlan(analysisId) {

	var analysisID = -1;

	if (analysisId == null || analysisId == undefined) {

		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
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
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.CALCULATE_RISK_REGISTER)) {
		href = "${pageContext.request.contextPath}/analysis/" + analysisId
				+ "/compute/riskRegister";
	} else
		permissionError();
}

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