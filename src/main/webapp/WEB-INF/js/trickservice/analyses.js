var taskManager = undefined;

$(document).ready(function() {
	analysisTableSortable();
	$("input[type='checkbox']").removeAttr("checked");
});

function manageAnalysisAccess(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.ALL)) {
		$.ajax({
			url : context + "/Analysis/" + analysisId + "/ManageAccess",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				newSection = $(doc).find("* div#manageAnalysisAccessModel");
				$("div#manageAnalysisAccessModel").replaceWith(newSection);
				$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
				$("#manageAnalysisAccessModel").modal('toggle');
				$("#userselect").one('focus', function() {
					previous = this.value;
				}).change(function() {

					$("#user_" + previous).attr("hidden", true);

					$("#user_" + this.value).removeAttr("hidden");

					previous = this.value;
				});
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;

}

function updatemanageAnalysisAccess(analysisId, userrightsform) {
	$.ajax({
		url : context + "/Analysis/" + analysisId + "/ManageAccess/Update",
		type : "post",
		data : serializeForm(userrightsform),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			newSection = $(doc).find("* div.modal-content");
			$("div#manageAnalysisAccessModel div.modal-content").html(newSection);
			$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
			$("#userselect").one('focus', function() {
				previous = this.value;
			}).change(function() {

				$("#user_" + previous).attr("hidden", true);

				$("#user_" + this.value).removeAttr("hidden");

				previous = this.value;
			});
		},
		error : unknowError
	});
}

function findTrickisProfile(element) {
	if (element != undefined && element != null && element.length > 0 && element.length < 2)
		if ($(element).attr("trick-isProfile") != undefined)
			return $(element).attr("trick-isProfile");
		else if ($(element).parent().prop("tagName") != "BODY")
			return findTrickisProfile($(element).parent());
		else
			return null;
	else
		return null;
}

function disableifprofile(section, menu) {
	var element = $(menu + " li[class='profilemenu']");
	element.addClass("disabled");
	var isProfile = findTrickisProfile($(section + " tbody :checked"));
	if (isProfile != undefined && isProfile != null) {
		if (isProfile == "true")
			element.addClass("disabled");
		else
			element.removeClass("disabled");
	}
}

function saveAnalysis(form, reloadaction) {
	$("#addAnalysisModel .progress").show();
	$("#addAnalysisModel #addAnalysisButton").prop("disabled", true);
	$.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
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
					$(errorElement).appendTo($("#analysis_form select[name='profile']").parent());
					break;

				case "author":
					$(errorElement).appendTo($("#analysis_form input[name='author']").parent());
					break;

				case "version":
					$(errorElement).appendTo($("#analysis_version").parent());
					break;

				case "analysis":
					$(errorElement).appendTo($("#addAnalysisModel .modal-body"));
					break;
				}
			}
			if (!$("#addAnalysisModel .label-danger").length) {
				$("#addAnalysisModel").modal("hide");
				reloadSection("section_analysis");
			}
			return false;
		},
		error : unknowError
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
		$("#deleteAnalysisBody").html(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));
		$("#deleteAnalysisModel .btn").unbind();
		$("#deleteanalysisbuttonNo").click(function() {
			$("#deleteAnalysisModel .btn").unbind();
			$("#deleteAnalysisModel").modal("hide");
		});
		$("#deleteanalysisbuttonYes").click(function() {
			$("#deleteprogressbar").show();
			$("#deleteAnalysisModel .btn").unbind();
			$("#deleteAnalysisModel .btn").prop("disabled", true);
			$.ajax({
				url : context + "/Analysis/Delete/" + analysisId,
				type : "GET",
				contentType : "application/json;charset=UTF-8",
				success : function(response) {
					$("#deleteprogressbar").hide();
					$("#deleteanalysisbuttonYes").prop("disabled", false);
					$("#deleteAnalysisModel").modal('hide');
					if (response.success != undefined) {
						reloadSection("section_analysis");
					} else if (response.error != undefined) {
						$("#alert-dialog .modal-body").html(response.error);
						$("#alert-dialog").modal("toggle");
					}
					return false;
				},
				error : unknowError
			});

			return false;
		});
		$("#deleteAnalysisModel .btn").prop("disabled", false);
		$("#deleteAnalysisModel").modal('show');
	} else
		permissionError();
	return false;
}

function createAnalysisProfile(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.READ)) {
		$.ajax({
			url : context + "/AnalysisProfile/Add/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				if ((analysisProfile = doc.getElementById("analysisProfileModal")) == null)
					return false;
				$(analysisProfile).appendTo("#wrap");
				$(analysisProfile).on('hidden.bs.modal', function() {
					$(analysisProfile).remove();
				});
				$(analysisProfile).modal("toggle");

			},
			error : unknowError
		});
	}
	return false;
}

function saveAnalysisProfile(form) {
	$.ajax({
		url : context + "/AnalysisProfile/Save",
		type : "post",
		aync : true,
		data : $("#" + form).serialize(),
		success : function(response) {
			if (response.flag != undefined) {
				var progressBar = new ProgressBar();
				progressBar.Initialise();
				$(progressBar.progress).appendTo($("#" + form).parent());
				callback = {
					failed : function() {
						progressBar.Destroy();
						$("#analysisProfileModal").modal("toggle");
						$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.task.execution", "An unknown error occurred during the execution of the task"));
						$("#alert-dialog").modal("toggle");
					},
					success : function() {
						progressBar.Destroy();
						$("#analysisProfileModal").modal("toggle");
					}
				};
				progressBar.OnComplete(callback.success);
				updateStatus(progressBar, response.taskID, callback, response);
			} else {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ((error = $(doc).find("#analysisProfileModal")).length) {
					$("#analysisProfileModal .modal-body").html($(error).find(".modal-body"));
					return false;
				}
			}
		},
		error : unknowError
	});
	return false;
}

function customAnalysis(element) {
	if ($(element).parent().hasClass("disabled"))
		return false;
	$.ajax({
		url : context + "/Analysis/Build",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			if ($(doc).find("#buildAnalysisModal").length) {
				var modal = new Modal($(doc).find("#buildAnalysisModal").clone());

				// Event user select a customer
				$(modal.modal_body).find("#selector-customer").on("change", function(e) {
					$(modal.modal_body).find("#selector-analysis").find("option[value!=-1]").remove();
					$(modal.modal_body).find("#analysis-versions li[class!='disabled']").remove();
					if ($(e.target).val() < 1)
						$(modal.modal_body).find("#selector-analysis").find("option[value=-1]").prop("selected", true);
					else {
						$.ajax({
							url : context + "/Analysis/Build/Customer/" + $(e.target).val(),
							type : "get",
							contentType : "application/json;charset=UTF-8",
							success : function(response) {
								if (typeof response == 'object') {
									var $analysisSelector = $(modal.modal_body).find("#selector-analysis");
									for (var i = 0; i < response.length; i++)
										$("<option value='" + response[i].identifier + "'>" + response[i].label + "</option>").appendTo($analysisSelector);
								} else
									unknowError();
							},
							error : unknowError
						});
					}
				});

				var $emptyText = $(modal.modal_body).find("*[dropzone='true']>div:first").text();

				$(modal.modal_body).find("#selector-analysis").on(
						"change",
						function(e) {
							$(modal.modal_body).find("#analysis-versions li[class!='disabled']").remove();
							if ($(e.target).val() != -1) {
								$.ajax({
									url : context + "/Analysis/Build/Customer/" + $(modal.modal_body).find("#selector-customer").val() + "/Identifier/" + $(e.target).val(),
									type : "get",
									contentType : "application/json;charset=UTF-8",
									success : function(response) {
										if (typeof response == 'object') {
											var $analysisVersions = $(modal.modal_body).find("#analysis-versions");
											for (var i = 0; i < response.length; i++) {
												var $li = $("<li class='list-group-item' trick-id='" + response[i].id + "' title='" + response[i].label + " v."
														+ response[i].version + "'>" + response[i].version + "</li>");
												$li.appendTo($analysisVersions);
											}
											$(modal.modal_body).find("#analysis-versions li").hover(function() {
												$(this).css('cursor', 'move');
											})
											$(modal.modal_body).find("#analysis-versions li").draggable({
												helper : "clone",
												cancel : "span.glyphicon-remove-sign",
												revert : "invalid",
												containment : "#buildAnalysisModal #group_2",
												accept : "*[dropzone='true']",
												cursor : "move",
												start : function(e, ui) {
													$(ui.helper).css({
														'z-index' : '1085',
														'min-width' : '232px'
													});
												}
											});
										} else
											unknowError();
									},
									error : unknowError
								});
							}
						});

				var checkPhase = function() {
					$(modal.modal_body).find("input[name='phase']").prop("disabled", $(modal.modal_body).find("#analysis-build-standards .well").attr("trick-id") == undefined);
					$(modal.modal_body).find("input[name='phase']").prop("checked", false);
				}

				var checkEstimation = function() {
					var trick_id_asset = $(modal.modal_body).find("#analysis-build-assets .well").attr("trick-id");
					var trick_id_scenario = $(modal.modal_body).find("#analysis-build-scenarios .well").attr("trick-id");
					$(modal.modal_body).find("input[name='assessment']").prop("disabled", trick_id_asset != trick_id_scenario || trick_id_asset == undefined);
					$(modal.modal_body).find("input[name='assessment']").prop("checked", false);
				}

				$(modal.modal_body).find("#analysis-build-scenarios").attr("trick-callback", "checkEstimation()");
				$(modal.modal_body).find("#analysis-build-assets").attr("trick-callback", "checkEstimation()");
				$(modal.modal_body).find("#analysis-build-standards").attr("trick-callback", "checkPhase()");

				$(modal.modal_body).find("*[dropzone='true']>div").droppable({
					accept : "li.list-group-item",
					activeClass : "warning",
					drop : function(event, ui) {
						$(this).attr("trick-id", ui.draggable.attr("trick-id"));
						$(this).attr("title", ui.draggable.attr("title"));
						$(this).text(ui.draggable.attr("title"));
						$(this).addClass("success");
						$(this).parent().find('input').attr("value", ui.draggable.attr("trick-id"));
						var callback = $(this).parent().attr("trick-callback");
						$("<a href='#' class='pull-right text-danger'><span class='glyphicon glyphicon-remove-sign'></span></a>").appendTo($(this)).click(function() {
							var $parent = $(this).parent();
							$(this).remove();
							$parent.removeAttr("trick-id");
							$parent.removeAttr("title");
							$parent.removeClass("success");
							$parent.text($emptyText);
							$parent.parent().find('input').attr("value", '-1');
							if (callback != undefined)
								eval(callback);
							return false;
						});

						if (callback != undefined)
							eval(callback);
					}
				});
				var $saveButton = $(modal.modal_footer).find("button[name='save']");
				var $cancelButton = $(modal.modal_footer).find("button[name='cancel']");
				var $progress_bar = $(modal.modal_body).find(".progress");
				$cancelButton.click(function() {
					if (!$cancelButton.is(":disabled"))
						modal.Destroy();
					return false;
				});
				
				$saveButton.click(function() {
					$(modal.modal).find(".label-danger, .alert").remove();
					$(modal.modal_dialog).find("button").prop("disabled", true);
					$progress_bar.show();
					$.ajax({
						url : context + "/Analysis/Build/Save",
						type : "post",
						data : $(modal.modal_body).find("form").serialize(),
						async : false,
						success : function(response) {
							if (typeof response == 'object') {

								if (response.error != undefined)
									$(showError($(modal.modal_footer).find("#build-analysis-modal-error")[0], response.error)).css({
										'margin-bottom' : '0',
										'padding' : '6px 10px'
									});
								else if (response.success != undefined) {
									$(showSuccess($(modal.modal_footer).find("#build-analysis-modal-error")[0], response.success)).css({
										'margin-bottom' : '0',
										'padding' : '6px 10px'
									});
									setTimeout(function() {
										modal.Destroy();
										reloadSection("section_analysis");
									}, 3000);
								} else {
									for ( var error in response) {
										var errorElement = document.createElement("label");
										errorElement.setAttribute("class", "label label-danger");
										$(errorElement).text(response[error]);
										switch (error) {
										case "customer":
										case "language":
										case "comment":
										case "author":
										case "version":
										case "assessment":
											$(errorElement).appendTo($(modal.modal_body).find("form *[name='" + error + "']").parent());
											break;
										case "riskInformation":
										case "scope":
											$(errorElement).appendTo($(modal.modal_body).find("#analysis-build-" + error));
											break;
										case "asset":
										case "scenario":
										case "standard":
										case "parameter":
											$(errorElement).appendTo($(modal.modal_body).find("#analysis-build-" + error + "s"));
											break;
										}
									}
								}
							} else
								unknowError();
						},
						error : unknowError
					});
					$(modal.modal_dialog).find("button").prop("disabled", false);
					$progress_bar.hide();
				});

				modal.Show();
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});
	return false;
}

function newAnalysis(element) {
	if ($(element).parent().hasClass("disabled"))
		return false;
	$("#addAnalysisModel .progress").hide();
	$("#addAnalysisModel #addAnalysisButton").prop("disabled", false);
	$.ajax({
		url : context + "/Analysis/New",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			if ((form = doc.getElementById("form_add_analysis")) == null) {
				$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
				$("#alert-dialog").modal("toggle");
			} else {
				$("#analysis_form").html($(form).html());
				$("#addAnalysisModel-title").text(MessageResolver("title.Administration.Analysis.Add", "Create a new Analysis"));
				$("#addAnalysisButton").text(MessageResolver("label.action.create", "Create"));
				$("#analysis_form").prop("action", "/Save");
				$("#addAnalysisModel").modal('toggle');
			}
		},
		error : unknowError
	});
	return false;
}

/* History */
function addHistory(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection(("section_analysis"));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
		oldVersion = $("#section_analysis tr[trick-id='" + analysisId + "']>td:nth-child(6)").text();
	}
	$.ajax({
		url : context + "/Analysis/" + analysisId + "/NewVersion",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#addHistoryModal").replaceWith(response);
			$('#addHistoryModal').modal("toggle");
		},
		error : unknowError
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
		$("#addAnalysisModel .progress").hide();
		$("#addAnalysisModel #addAnalysisButton").prop("disabled", false);
		$.ajax({
			url : context + "/Analysis/Edit/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				if ((form = doc.getElementById("form_edit_analysis")) == null) {
					$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
					$("#alert-dialog").modal("toggle");
				} else {
					$("#analysis_form").html($(form).html());
					$("#addAnalysisModel-title").text(MessageResolver("title.analysis.Update", "Update an Analysis"));
					$("#addAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
					$("#analysis_form").prop("action", "/Save");
					$("#addAnalysisModel").modal('toggle');
				}
			},
			error : unknowError
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
		window.location.replace(context + "/Analysis/" + analysisId + "/Select");

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
			contentType : "application/json;charset=UTF-8",
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
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

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
				if (response["success"] != undefined) {
					if (taskManager == undefined)
						taskManager = new TaskManager();
					taskManager.Start();
				} else if (message["error"]) {
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
		$.fileDownload(context + '/Analysis/Export/Report/' + analysisId).fail(unknowError);
		return false;
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

function duplicateAnalysis(form, analyisId) {
	var oldVersion = $("#history_oldVersion").prop("value");
	$(".progress-striped").show();
	$(".progress-striped").addClass("active");
	var labels = $("#addHistoryModal .label-danger");
	if (labels.length)
		labels.remove();

	var alert = $("#addHistoryModal [class='alert alert-warning']");
	if (alert.length)
		alert.remove();

	$.ajax({
		url : context + "/Analysis/Duplicate/" + analyisId,
		type : "post",
		aync : true,
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {

			$("#history_oldVersion").attr("value", oldVersion);

			for ( var error in response) {
				$(".progress-striped").removeClass("active");
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);

				switch (error) {
				case "author":
					$(errorElement).appendTo($("#addHistoryModal #history_author").parent());
					break;
				case "version":
					$(errorElement).appendTo($("#addHistoryModal #history_version").parent());
					break;
				case "comment":
					$(errorElement).appendTo($("#addHistoryModal #history_comment").parent());
					break;
				case "analysis": {
					var alertElement = document.createElement("div");
					alertElement.setAttribute("class", "alert alert-warning");

					$(alertElement).text($(errorElement).text());

					$("#addHistoryModal .modal-body").prepend($(alertElement));
					break;
				}
				}

			}
			if (!$("#addHistoryModal .label-danger").length && !$("#addHistoryModal [class='alert alert-warning alert-dismissable']").length) {

				var alertElement = document.createElement("div");
				alertElement.setAttribute("class", "alert alert-success");

				$(alertElement).text(MessageResolver("success.newversion.created", "New version created sucessfully!"));

				$("#addHistoryModal div.modal-body").prepend($(alertElement));

				$(".progress-striped").removeClass("active");

				setTimeout("location.reload()", 2000);
			}

		},
		error : unknowError
	});
	return false;
}

function analysisTableSortable() {

	// check if datatable has to be initialised
	var tables = $("#section_analysis table");
	if (!tables.length) {
		tables = $("#section_admin_analysis table");
		if (!tables.length) {
			tables = $("#section_profile_analysis table");
			if (!tables.length)
				return false;
		}
	}

	// define sort order of text
	Array.AlphanumericSortOrder = 'AaÁáBbCcDdÐðEeÉéĘęFfGgHhIiÍíJjKkLlMmNnOoÓóPpQqRrSsTtUuÚúVvWwXxYyÝýZzÞþÆæÖö';

	// flag to check for case sensitive comparation
	Array.AlphanumericSortIgnoreCase = true;

	// call the tablesorter plugin and apply the uitheme widget
	$(tables).tablesorter({
		headers : {
			0 : {
				sorter : false,
				filter : false,
				width : "0.5%"
			},
			1 : {
				sorter : "text",
				filter : false,
				width : "10%",
			},
			2 : {
				sorter : "text",
				filter : false,
			},
			3 : {
				sorter : "text",
				filter : false,
			},
			4 : {
				sorter : "text",
				filter : false,
			},
			5 : {
				sorter : "text",
				filter : false,
			},
			6 : {
				sorter : "text",
				filter : false,
			},
			7 : {
				sorter : "text",
				filter : false,
			},
		},
		textSorter : {
			1 : Array.AlphanumericSort,
			2 : function(a, b, direction, column, table) {
				if (table.config.sortLocaleCompare)
					return a.localeCompare(b);
				return versionComparator(a, b, direction);
			},
			3 : $.tablesorter.sortNatural,
		},
		theme : "bootstrap",
		dateFormat : "yyyymmdd",
		widthFixed : false,
		headerTemplate : '{content} {icon}',
		widgets : [ "uitheme", "filter", "zebra" ],
		widgetOptions : {
			zebra : [ "even", "odd" ],
			filter_reset : ".reset"
		}
	});
	$("th[class~='tablesorter-header'][data-column='0']").css({
		'width' : '2px'
	});
	$("th[class~='tablesorter-header'][data-column='1']").css({
		'width' : '150px'
	});
	// $("th[class~='tablesorter-header'][data-column='2']").css({'width':'2px'});
	$("th[class~='tablesorter-header'][data-column='3']").css({
		'width' : '250px'
	});
	$("th[class~='tablesorter-header'][data-column='4']").css({
		'width' : '250px'
	});
	$("th[class~='tablesorter-header'][data-column='5']").css({
		'width' : '150px'
	});
	$("th[class~='tablesorter-header'][data-column='6']").css({
		'width' : '5px'
	});
	$("th[class~='tablesorter-header'][data-column='7']").css({
		'width' : '5px'
	});
	return false;
}

function downloadExportedSqLite(idFile) {
	$.fileDownload(context + '/Analysis/Download/' + idFile).fail(unknowError);
	return false;
}

function customerChange(selector) {
	var customer = $(selector).find("option:selected").val();
	$.ajax({
		url : context + "/Analysis/DisplayByCustomer/" + customer,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("*[id ='section_analysis']");
			$("#section_analysis").replaceWith(newSection);
			analysisTableSortable();
		},
		error : unknowError
	});
	return false;
}