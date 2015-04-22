var taskManager = undefined;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	$("#section_analysis table").stickyTableHeaders({
		cssTopOffset : ".navbar-fixed-top"
	});
});

function manageAnalysisAccess(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	if (canManageAccess()) {
		$.ajax({
			url : context + "/Analysis/ManageAccess/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				var $newSection = $(doc).find("#manageAnalysisAccessModel");
				$("#manageAnalysisAccessModel").replaceWith($newSection);
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
		url : context + "/Analysis/ManageAccess/Update/" + analysisId,
		type : "post",
		data : serializeForm(userrightsform),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			var $newSection = $(doc).find(".modal-content");
			if ($newSection.length) {
				$("#manageAnalysisAccessModel .modal-content").replaceWith($newSection);
				$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
				$("#userselect").one('focus', function() {
					previous = this.value;
				}).change(function() {
					$("#user_" + previous).attr("hidden", true);
					$("#user_" + this.value).removeAttr("hidden");
					previous = this.value;
				});
				reloadSection("section_analysis");
			} else
				unknowError();
		},
		error : unknowError
	});
}

function findTrickisProfile(element) {
	if (element != undefined && element != null && element.length > 0 && element.length < 2)
		if ($(element).attr("data-trick-is-profile") != undefined)
			return $(element).attr("data-trick-is-profile");
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
	$("#editAnalysisModel .progress").show();
	$("#editAnalysisModel #editAnalysisButton").prop("disabled", true);
	$.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#editAnalysisModel .progress").hide();
			$("#editAnalysisModel #editAnalysisButton").prop("disabled", false);
			var alert = $("#editAnalysisModel .label-danger");
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
					$(errorElement).appendTo($("#editAnalysisModel .modal-body"));
					break;
				}
			}
			if (!$("#editAnalysisModel .label-danger").length) {
				$("#editAnalysisModel").modal("hide");
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

	if (userCan(analysisId, ANALYSIS_RIGHT.MODIFY)) {
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
				success : function(response, textStatus, jqXHR) {
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
			success : function(response, textStatus, jqXHR) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				if ((analysisProfile = doc.getElementById("analysisProfileModal")) == null)
					return false;
				$(analysisProfile).appendTo("#wrap");
				$(analysisProfile).on('hidden.bs.modal', function() {
					$(analysisProfile).remove();
				});

				var allVal = new Array();

				$('#analysisProfileform .list-group-item.active').each(function() {
					allVal.push($(this).attr("data-trick-opt"));
				});

				$('#standards').val(allVal);

				$('#analysisProfileform .list-group-item').on('click', function() {
					$(this).toggleClass('active');
					if ($(this).hasClass("active"))
						$(this).css("border", "1px solid #dddddd");
					else
						$(this).css("border", "");
					allVal = new Array();
					$('#analysisProfileform .list-group-item.active').each(function() {
						allVal.push($(this).attr("data-trick-opt"));
					});
					$('#standards').val(allVal);
				});

				$(analysisProfile).modal("toggle");

			},
			error : unknowError
		});
	}
	return false;
}

function saveAnalysisProfile(form) {

	var data = {};

	var id = $("#" + form).find("#id").val();

	var description = $("#" + form).find("#name").val();

	data["id"] = id;

	data["description"] = description;

	$("#" + form).find("select[name='standards'] option").each(function() {

		var name = $(this).attr("value");

		var value = $(this).is(":checked");

		data[name] = value;

	});

	var jsonarray = JSON.stringify(data);

	$.ajax({
		url : context + "/AnalysisProfile/Save",
		type : "post",
		data : jsonarray,
		contentType : "application/json;charset=UTF-8",
		aync : true,
		success : function(response, textStatus, jqXHR) {

			var alert = $("#analysisProfileform .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				if (error === "taskid")
					continue;
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);
				switch (error) {
				case "description":
					$(errorElement).appendTo($("#analysisProfileform #name").parent());
					break;
				case "analysisprofile":
				default:
					$(errorElement).appendTo($("#analysisProfileform").parent());
					break;
				}
			}

			if (!$("#analysisProfileform .label-danger").length) {
				var progressBar = new ProgressBar();
				progressBar.Initialise();
				$(progressBar.progress).appendTo($("#" + form).parent());
				$(progressBar.progress).css("width", "100%");
				$(progressBar.progress).css("display", "inline-block");
				callback = {
					failed : function() {
						progressBar.Destroy();
						$("#analysisProfileModal").modal("toggle");
						$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.task.execution", "An unknown error occurred during the execution of the task"));
						$("#alert-dialog").modal("toggle");
					},
					success : function() {
						setTimeout(function() {
							progressBar.Destroy();
							$("#analysisProfileModal").modal("toggle");
						}, 2000);

					}
				};
				progressBar.OnComplete(callback.success);
				updateStatus(progressBar, response["taskid"], callback, undefined);
			}
			return false;

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
		success : function(response, textStatus, jqXHR) {
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
							success : function(response, textStatus, jqXHR) {
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

				var $removeText = MessageResolver("label.action.delete", "Delete");

				$(modal.modal_body).find("#selector-analysis").on(
						"change",
						function(e) {
							$(modal.modal_body).find("#analysis-versions li[class!='disabled']").remove();
							if ($(e.target).val() != -1) {
								$.ajax({
									url : context + "/Analysis/Build/Customer/" + $(modal.modal_body).find("#selector-customer").val() + "/Identifier/" + $(e.target).val(),
									type : "get",
									contentType : "application/json;charset=UTF-8",
									success : function(response, textStatus, jqXHR) {
										if (typeof response == 'object') {
											var $analysisVersions = $(modal.modal_body).find("#analysis-versions");
											for (var i = 0; i < response.length; i++) {
												var $li = $("<li class='list-group-item' data-trick-id='" + response[i].id + "' title='" + response[i].label + " v."
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
					$(modal.modal_body).find("input[name='phase']")
							.prop("disabled", $(modal.modal_body).find("#analysis-build-standards .well").attr("data-trick-id") == undefined);
					$(modal.modal_body).find("input[name='phase']").prop("checked", false);
				}

				var checkEstimation = function() {
					var trick_id_asset = $(modal.modal_body).find("#analysis-build-assets .well").attr("data-trick-id");
					var trick_id_scenario = $(modal.modal_body).find("#analysis-build-scenarios .well").attr("data-trick-id");
					$(modal.modal_body).find("input[name='assessment']").prop("disabled", trick_id_asset != trick_id_scenario || trick_id_asset == undefined);
					$(modal.modal_body).find("input[name='assessment']").prop("checked", false);
				}

				$(modal.modal_body).find("#analysis-build-scenarios").attr("data-trick-callback", "checkEstimation()");
				$(modal.modal_body).find("#analysis-build-assets").attr("data-trick-callback", "checkEstimation()");
				$(modal.modal_body).find("#analysis-build-standards").attr("data-trick-callback", "checkPhase()");

				$(modal.modal_body).find("*[dropzone='true']>div").droppable(
						{
							accept : "li.list-group-item",
							activeClass : "warning",
							drop : function(event, ui) {
								$(this).attr("data-trick-id", ui.draggable.attr("data-trick-id"));
								$(this).attr("title", ui.draggable.attr("title"));
								$(this).text(ui.draggable.attr("title"));
								$(this).addClass("success");
								$(this).parent().find('input').attr("value", ui.draggable.attr("data-trick-id"));
								var callback = $(this).parent().attr("data-trick-callback");
								$(
										"<a href='#' class='pull-right text-danger' title='" + $removeText
												+ "' style='font-size:18px'><span class='glyphicon glyphicon-remove-circle'></span></a>").appendTo($(this)).click(function() {
									var $parent = $(this).parent();
									$(this).remove();
									$parent.removeAttr("data-trick-id");
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
						success : function(response, textStatus, jqXHR) {
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
									$saveButton.unbind();
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
										case "profile":
										case "name":
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

/* History */
function addHistory(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection(("section_analysis"));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
		oldVersion = $("#section_analysis tr[data-trick-id='" + analysisId + "']>td:nth-child(6)").text();
	}

	if (canCreateNewVersion(analysisId)) {
		$.ajax({
			url : context + "/Analysis/" + analysisId + "/NewVersion",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#addHistoryModal");
				if ($content.length) {
					$("#addHistoryModal").replaceWith(response);
					$('#addHistoryModal').modal("toggle");
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
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
		$("#editAnalysisModel .progress").hide();
		$("#editAnalysisModel #editAnalysisButton").prop("disabled", false);
		$.ajax({
			url : context + "/Analysis/Edit/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				if ((form = doc.getElementById("form_edit_analysis")) == null) {
					$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
					$("#alert-dialog").modal("toggle");
				} else {
					$("#analysis_form").html($(form).html());
					$("#editAnalysisModel-title").text(MessageResolver("title.analysis.Update", "Update an Analysis"));
					$("#editAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
					$("#analysis_form").prop("action", "/Save");
					$("#editAnalysisModel").modal('toggle');
				}
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function selectAnalysis(analysisId, selectionOnly, isReadOnly) {

	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.READ))
		window.location.replace(context + "/Analysis/" + analysisId + "/Select?readOnly=" + (isReadOnly === true));
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
			if (userCan(rowTrickId, ANALYSIS_RIGHT.READ)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}

	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.READ)) {

		var data = {};

		data["id"] = analysisID;

		$.ajax({
			url : context + "/Analyis/ActionPlan/Compute",
			type : "post",
			data : JSON.stringify(data),
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

function calculateRiskRegister(analysisId) {

	var analysisID = -1;

	if (analysisId == null || analysisId == undefined) {

		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (!selectedAnalysis.length)
			return false;
		while (selectedAnalysis.length) {
			rowTrickId = selectedAnalysis.pop();
			if (userCan(rowTrickId, ANALYSIS_RIGHT.READ)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}

	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.READ)) {

		var data = {};

		data["id"] = analysisID;

		$.ajax({
			url : context + "/Analyis/RiskRegister/Compute",
			type : "post",
			data : JSON.stringify(data),
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

function duplicateAnalysis(form, analyisId) {
	var oldVersion = $("#history_oldVersion").prop("value");
	/*
	 * $(".progress-striped").show(); $(".progress-striped").addClass("active");
	 */
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
		success : function(response, textStatus, jqXHR) {

			$("#history_oldVersion").attr("value", oldVersion);

			var errorcounter = 0;

			for ( var error in response) {
				$(".progress-striped").removeClass("active");
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);

				switch (error) {
				case "author":
					errorcounter++;
					$(errorElement).appendTo($("#addHistoryModal #history_author").parent());
					break;
				case "version":
					$(errorElement).appendTo($("#addHistoryModal #history_version").parent());
					errorcounter++;
					break;
				case "comment":
					errorcounter++;
					$(errorElement).appendTo($("#addHistoryModal #history_comment").parent());
					break;
				case "analysis":
					errorcounter++;
					var alertElement = document.createElement("div");
					alertElement.setAttribute("class", "alert alert-warning");
					$(alertElement).text($(errorElement).text());
					$("#addHistoryModal .modal-body").prepend($(alertElement));
					break;
				}

			}
			if (errorcounter == 0) {
				progressBar = new ProgressBar();
				progressBar.Initialise();
				$(progressBar.progress).appendTo($("#history_form").parent());
				callback = {
					failed : function() {
						progressBar.Destroy();
						$("#addHistoryModal").modal("hide");
						$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.task.execution", "An unknown error occurred during the execution of the task"));
						$("#alert-dialog").modal("show");
					},
					success : function() {
						progressBar.Destroy();
						setTimeout("location.reload()", 1000);
						$("#addHistoryModal").modal("hide");
					}
				};
				progressBar.OnComplete(callback.success);
				updateStatus(progressBar, response["analysis_task_id"], callback, undefined);
			}
		},
		error : unknowError
	});
	return false;
}

function customerChange(customer, nameFilter) {
	$.ajax({
		url : context + "/Analysis/DisplayByCustomer/" + $(customer).val(),
		type : "post",
		async : true,
		contentType : "application/json;charset=UTF-8",
		data : $(nameFilter).val(),
		success : function(response, textStatus, jqXHR) {
			var $newSection = $(new DOMParser().parseFromString(response, "text/html")).find("#section_analysis");
			if ($newSection.length) {
				$("#section_analysis").replaceWith($newSection);
				$(document).ready(function() {
					$("input[type='checkbox']").removeAttr("checked");
					$("#section_analysis table").stickyTableHeaders({
						cssTopOffset : "#container",
						fixedOffset : 6
					});
				});
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}