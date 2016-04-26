var el = null, table = null, taskController = function() {
};

$(document).ready(function() {

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************

	$("input[type='checkbox']").removeAttr("checked");

	var $tabOption = $("#tabOption");

	application["settings-fixed-header"] = {
		fixedOffset : $(".nav-analysis"),
		marginTop : application.fixedOffset,
		scrollStartFixMulti : 0.99998
	};

	fixTableHeader("table.table-fixed-header-analysis");

	$('ul.nav-analysis a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
		disableEditMode();
		$tabOption.hide();
	});

	Highcharts.setOptions({
		lang : {
			decimalPoint : ',',
			thousandsSep : ' '
		}
	});
});

function findAnalysisId() {
	var id = application['selected-analysis-id'];
	if (id === undefined) {
		var el = document.querySelector("#nav-container");
		if (el == null)
			return -1;
		id = application['selected-analysis-id'] = el.getAttribute("data-trick-id");
	}
	return id;
}

function findAnalysisLocale() {
	var locale = application['selected-analysis-locale'];
	if (locale === undefined) {
		var el = document.querySelector("#nav-container");
		if (el == null)
			return 'en';
		locale = application['selected-analysis-locale'] = el.getAttribute("data-trick-language");
	}
	return locale;
}

function isEditable() {
	return userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY);
}

function updateSettings(element, entryKey) {
	$.ajax({
		url : context + "/Settings/Update",
		type : 'post',
		data : {
			'key' : entryKey,
			'value' : !$(element).hasClass('glyphicon-ok')
		},
		async : false,
		success : function(response, textStatus, jqXHR) {
			if (response == undefined || response !== true)
				unknowError();
			else {
				if ($(element).hasClass('glyphicon-ok'))
					$(element).removeClass('glyphicon-ok');
				else
					$(element).addClass('glyphicon-ok');
				var sections = $(element).attr("data-trick-section-dependency");
				if (sections != undefined)
					return reloadSection(sections.split(','));
				var callBack = $(element).attr("data-trick-callback");
				if (callBack != undefined)
					return eval(callBack);
				var reload = $(element).attr("data-trick-reload");
				if (reload == undefined || reload == 'true')
					location.reload();
			}
			return true;
		},
		error : unknowError
	});
	return false;
}

// reload measures
function reloadMeasureRow(idMeasure, standard) {
	var $currentRow = $("#section_standard_" + standard + " tr[data-trick-id='" + idMeasure + "']")
	if (!$currentRow.find("input[type!='checkbox'],select,textarea").length) {
		$.ajax({
			url : context + "/Analysis/Standard/" + standard + "/SingleMeasure/" + idMeasure,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $newRow = $("tr", $("<div/>").html(response));
				if (!$newRow.length)
					$currentRow.addClass("warning").attr("title", MessageResolver("error.ui.no.synchronise", "User interface does not update"));
				else {
					var $checked = $currentRow.find("input:checked");
					$("[data-toggle='popover']", $currentRow).popover('destroy');
					$("[data-toggle='tooltip']", $currentRow).tooltip('destroy');
					$currentRow.replaceWith($newRow);
					$("[data-toggle='popover']", $newRow).popover().on('show.bs.popover', togglePopever);
					$("[data-toggle='tooltip']", $newRow).tooltip();
					if ($checked.length)
						$newRow.find("input[type='checkbox']").prop("checked", true).change();
				}
			},
			error : unknowError
		});
	} else
		$currentRow.attr("data-force-callback", true).addClass("warning").attr("title",
				MessageResolver("error.ui.update.wait.editing", "Data was saved but user interface was not updated, it will be updated after edition"));
	return false;
}

function reloadMeasureAndCompliance(standard, idMeasure) {
	reloadMeasureRow(idMeasure, standard);
	compliance(standard);
	return false;
}

// charts

function compliances() {
	$.ajax({
		url : context + "/Analysis/Standard/Compliances",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response.standards == undefined || response.standards == null)
				return;
			var $complianceBody = $("#chart_compliance_body").empty();
			$.each(response.standards, function(key, data) {
				if ($complianceBody.children().length)
					$complianceBody.append("<hr class='col-xs-12' style='margin: 30px 0;'> <div id='chart_compliance_" + key + "'></div>");
				else
					$complianceBody.append("<div id='chart_compliance_" + key + "'></div>");
				$('div[id="chart_compliance_' + key + '"]').highcharts(data[0]);
			});
		},
		error : unknowError
	});
	return false;
}

function compliance(standard) {
	if (!$('#chart_compliance_' + standard).length)
		return false;
	if ($('#chart_compliance_' + standard).is(":visible")) {
		$.ajax({
			url : context + "/Analysis/Standard/" + standard + "/Compliance",
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return;
				$('#chart_compliance_' + standard).highcharts(response);
			},
			error : unknowError
		});
	} else
		$("#tabChartCompliance").attr("data-update-required", "true");
	return false;
}

function evolutionProfitabilityComplianceByActionPlanType(actionPlanType) {
	if (!$('#chart_evolution_profitability_compliance_' + actionPlanType).length)
		return false;
	if ($('#chart_evolution_profitability_compliance_' + actionPlanType).is(":visible")) {
		$.ajax({
			url : context + "/Analysis/ActionPlanSummary/Evolution/" + actionPlanType,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_evolution_profitability_compliance_' + actionPlanType).highcharts(response);
			},
			error : unknowError
		});
	} else
		$("#tabChartEvolution").attr("data-update-required", "true");
	return false;
}

function budgetByActionPlanType(actionPlanType) {
	if (!$('#chart_budget_' + actionPlanType).length)
		return false;
	if ($('#chart_budget_' + actionPlanType).is(":visible")) {
		$.ajax({
			url : context + "/Analysis/ActionPlanSummary/Budget/" + actionPlanType,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response.chart == undefined || response.chart == null)
					return true;
				$('#chart_budget_' + actionPlanType).highcharts(response);
			},
			error : unknowError
		});
	} else
		$("#tabChartBudget").attr("data-update-required", "true");
	return false;
}

function summaryCharts() {
	var actionPlanTypes = $("#section_summary *[data-trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			actionPlanType = $(actionPlanTypes[i]).attr("data-trick-nav-control");
			evolutionProfitabilityComplianceByActionPlanType(actionPlanType);
			budgetByActionPlanType(actionPlanType);
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function loadChartEvolution() {
	var actionPlanTypes = $("#section_summary *[data-trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			evolutionProfitabilityComplianceByActionPlanType($(actionPlanTypes[i]).attr("data-trick-nav-control"));
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function loadChartBudget() {
	var actionPlanTypes = $("#section_summary *[data-trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			budgetByActionPlanType($(actionPlanTypes[i]).attr("data-trick-nav-control"));
		} catch (e) {
			console.log(e);
		}
	}
	return false;
}

function reloadCharts() {
	chartALE();
	compliances();
	summaryCharts();
	return false;
};

function displayChart(id, response) {
	var $element = $(id);
	if ($.isArray(response)) {
		$element.empty();
		for (var i = 0; i < response.length; i++) {
			if (i == 0)
				$("<div/>").appendTo($element).highcharts(response[i]);
			else {
				$("<hr class='col-xs-12' style='margin: 30px 0;'>").appendTo($element);
				$("<div></div>").appendTo($element).highcharts(response[i]);
			}
		}
	} else
		$element.highcharts(response);
}

function loadChartAsset() {

	if ($('#chart_ale_asset').length) {
		if ($('#chart_ale_asset').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset', response);
				},
				error : unknowError
			});
		} else
			$("#tabChartAsset").attr("data-update-required", "true");
	}
	if ($('#chart_ale_asset_type').length) {
		if ($('#chart_ale_asset_type').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Asset/Chart/Type/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_asset_type', response);
				},
				error : unknowError
			});
		} else
			$("#tabChartAsset").attr("data-update-required", "true");
	}
}

function loadChartScenario() {
	if ($('#chart_ale_scenario_type').length) {
		if ($('#chart_ale_scenario_type').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Scenario/Chart/Type/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario_type', response);
				},
				error : unknowError
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
	}

	if ($('#chart_ale_scenario').length) {
		if ($('#chart_ale_scenario').is(":visible")) {
			$.ajax({
				url : context + "/Analysis/Scenario/Chart/Ale",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					displayChart('#chart_ale_scenario', response);
				},
				error : unknowError
			});
		} else
			$("#tabChartScenario").attr("data-update-required", "true");
	}
}

function reloadActionPlansAndCharts() {
	reloadSection('section_actionplans');
}

function chartALE() {
	loadChartAsset();
	loadChartScenario();
	return false;
}

// common
function navToogled(section, parentMenu, navSelected) {
	var currentMenu = $("li[data-trick-nav-control='" + navSelected + "']", parentMenu);
	if (!currentMenu.length || $(currentMenu).hasClass("disabled"))
		return false;
	$("li[data-trick-nav-control]", parentMenu).each(function() {
		if (this.getAttribute("data-trick-nav-control") == navSelected)
			this.classList.add("disabled");
		else if (this.classList.contains('disabled'))
			this.classList.remove("disabled");
	});

	$("[data-trick-nav-content]", section).each(function() {
		var $this = $(this);
		if (this.getAttribute("data-trick-nav-content") == navSelected)
			$this.show();
		else if ($this.is(":visible"))
			$this.hide();
	});
	$(window).scroll();
	return false;
}

function openTicket(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#progress-dialog").modal("show");
		$.ajax({
			url : context + "/Analysis/Standard/Ticketing/Open",
			type : "POST",
			async : false,
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(measures),
			success : function(response, textStatus, jqXHR) {
				$progress.one("hide.bs.modal", function() {
					var $modal = $("#modal-ticketing-view", new DOMParser().parseFromString(response, "text/html"));
					if (!$modal.length)
						unknowError();
					else {
						$("#modal-ticketing-view").remove();
						$modal.appendTo($("#widgets")).modal("show");
						var $previous = $modal.find(".previous"), $next = $modal.find(".next"), $title = $modal.find(".modal-title");
						$next.find("a").on("click", function() {
							if (!$next.hasClass("disabled")) {
								var $current = $modal.find("fieldset:visible"), $nextElement = $current.next();
								if ($nextElement.length) {
									$current.hide();
									$title.text($nextElement.show().attr("data-title"));
									if (!$nextElement.next().length)
										$next.addClass("disabled")
									if ($previous.hasClass("disabled"))
										$previous.removeClass("disabled");
								}
							}
							return false;
						});

						$previous.find("a").on("click", function() {
							if (!$previous.hasClass("disabled")) {
								var $current = $modal.find("fieldset:visible"), $prev = $current.prev();
								if ($prev.length) {
									$current.hide();
									$title.text($prev.show().attr("data-title"));
									if (!$prev.prev().length)
										$previous.addClass("disabled")
									if ($next.hasClass("disabled"))
										$next.removeClass("disabled");
								}
							}
							return false;
						});
					}
				});
			},
			error : unknowError
		}).complete(function() {
			$progress.modal("hide");
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.open.no_action.required", "None of the selected measures is related to a task"));
	return false;
}

function linkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "false")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#progress-dialog").modal("show");
		$
				.ajax(
						{
							url : context + "/Analysis/Standard/Ticketing/Link",
							type : "POST",
							async : false,
							contentType : "application/json;charset=UTF-8",
							data : JSON.stringify(measures),
							success : function(response, textStatus, jqXHR) {
								$progress
										.one(
												"hide.bs.modal",
												function() {
													if (response['error'])
														showDialog("#alert-dialog", response['error']);
													else {
														var $modal = $("#modal-ticketing-linker", new DOMParser().parseFromString(response, "text/html")), updateRequired = false;
														if (!$modal.length)
															unknowError();
														else {
															$("#modal-ticketing-linker").remove();
															$modal.appendTo($("#widgets")).modal("show");
															var isFinished = false, $linker = $modal.find("#measure-task-linker"), $measureViewer = $modal.find("#measure-viewer"), $taskViewer = $("#task-viewer"), $taskContainer = $modal
																	.find("#task-container"), $tasks = $taskContainer.find("fieldset"), size = $tasks.length;

															taskController = function() {
																$view = $(this.getAttribute("href"));
																if (!$view.is(":visible")) {
																	$taskViewer.find("fieldset:visible").hide();
																	$view.show();
																}
																return false;
															}

															$tasks.appendTo($taskViewer);

															$taskContainer.find("a.list-group-item").on("click", taskController);

															$modal
																	.on("hidden.bs.modal", function() {
																		if (updateRequired)
																			reloadSection("section_actionplans");
																		$modal.remove();
																	}).on("show.bs.modal",function(){
																		$taskContainer.scrollTop();
																	})
																	.on(
																			"shown.bs.modal",
																			function() {
																				$taskContainer
																						.on(
																								"scroll",
																								function() {

																									if (!isFinished
																											&& ($taskContainer.scrollTop() + $taskContainer.innerHeight() >= $taskContainer[0].scrollHeight)) {
																										$.ajax({
																											url : context + "/Analysis/Standard/Ticketing/Load?startIndex=" + (size + 1),
																											type : "POST",
																											async : false,
																											contentType : "application/json;charset=UTF-8",
																											data : JSON.stringify(measures),
																											success : function(response, textStatus, jqXHR) {
																												$subTaskContainer = $("#task-container", new DOMParser()
																														.parseFromString(response, "text/html"));
																												var $subTasks = $subTaskContainer.find("fieldset");
																												if (!(isFinished = $subTasks.length == 0)) {
																													size += $subTasks.length;
																													$subTasks.appendTo($taskViewer);
																													$subTaskContainer.find("a.list-group-item").appendTo($taskContainer)
																															.on("click", taskController);
																												}
																											},
																											error : function() {
																												isFinished = true;
																											}
																										});
																									}
																								});

																			});

															$modal.find("#measure-container>fieldset").appendTo($measureViewer);
															$modal.find("#measure-container>a.list-group-item").on("click", function() {
																$view = $(this.getAttribute("href"));
																if (!$view.is(":visible")) {
																	$measureViewer.find("fieldset:visible").hide();
																	$view.show();
																}
																return false;
															});

															$linker.on('click', function() {
																var $measure = $measureViewer.find("fieldset:visible"), $ticket = $taskViewer.find("fieldset:visible")
																if ($measure.length && $ticket.length) {
																	$.ajax({
																		url : context + "/Analysis/Standard/Ticketing/Link/Measure",
																		type : "POST",
																		async : false,
																		contentType : "application/json;charset=UTF-8",
																		data : JSON.stringify({
																			"idMeasure" : $measure.attr("data-trick-id"),
																			"idTicket" : $ticket.attr("data-trick-id")
																		}),
																		success : function(response, textStatus, jqXHR) {
																			if (response.success) {
																				reloadMeasureRow($measure.attr("data-trick-id"), $measure.attr("data-trick-parent-id"));
																				$("#" + $measure.remove().attr("aria-controls")).remove();
																				$("#" + $ticket.remove().attr("aria-controls")).remove();
																				updateRequired = true;
																				if (!$measureViewer.find("fieldset").length || !$taskViewer.find("fieldset").length)
																					$modal.modal("hide");
																			} else if (response.error)
																				showDialog("#alert-dialog", response.error);
																			else
																				unknowError();
																		},
																		error : unknowError
																	});
																}
																return false;
															});

														}
													}
												});
							},
							error : unknowError
						}).complete(function() {
					$progress.modal("hide");
				});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.link.no_action.required", "All selected measures are already related to tasks"));
	return false;
}

function unLinkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});
	if (measures.length) {

		var $confirm = $("#confirm-dialog"), $question = measures.length == 1 ? MessageResolver("confirm.unlink.measure", "Are you sure, you want to unlink this measure and task") : MessageResolver(
				"confirm.unlink.measures", "Are you sure, you want to unlink measures and tasks");
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function() {
			var $progress = $("#progress-dialog").modal("show");
			$.ajax({
				url : context + "/Analysis/Standard/Ticketing/UnLink",
				type : "POST",
				async : false,
				contentType : "application/json;charset=UTF-8",
				data : JSON.stringify(measures),
				success : function(response, textStatus, jqXHR) {
					$progress.one("hide.bs.modal", function() {
						if (response.error)
							showDialog("#alert-dialog", response.error);
						else if (response.success) {
							if (section == "#section_actionplans")
								location.reload();
							else {
								showDialog("#info-dialog", response.success);
								if (measures.length > 30)
									reloadSection([ section.replace("#", ''), "section_actionplans" ]);
								else {
									reloadSection("section_actionplans");
									setTimeout(function() {
										var idStandard = $(section).attr("data-trick-id");
										for (var i = 0; i < measures.length; i++)
											reloadMeasureRow(measures[i], idStandard);
									}, measures.length * 20);
								}
							}
						} else
							unknowError();
					});

				},
				error : unknowError
			}).complete(function() {
				$progress.modal("hide");
			});
		});
		$confirm.modal("show");
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.unlink.no_action.required", "None of the selected measures is related to a task"));
	return false;

}

function generateTickets(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "false")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		$.ajax({
			url : context + "/Analysis/Standard/Ticketing/Generate",
			type : "POST",
			async : false,
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(measures),
			success : function(response, textStatus, jqXHR) {
				if (response.error)
					showDialog("#alert-dialog", response.error);
				else if (response.success)
					application["taskManager"].Start();
				else
					unknowError();
			},
			error : unknowError
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.generate.no_action.required", "All selected measures are already related to tasks"));
	return false;
}

function synchroniseWithTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	var measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function() {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		var $progress = $("#progress-dialog").modal("show");
		$.ajax(
				{
					url : context + "/Analysis/Standard/Ticketing/Synchronise",
					type : "POST",
					async : false,
					contentType : "application/json;charset=UTF-8",
					data : JSON.stringify(measures),
					success : function(response, textStatus, jqXHR) {
						$progress.one("hide.bs.modal", function() {
							var $modal = $("#modal-ticketing-synchronise", new DOMParser().parseFromString(response, "text/html"));
							if (!$modal.length)
								unknowError();
							else {
								$("#modal-ticketing-synchronise").remove();
								$modal.appendTo($("#widgets")).modal("show");
								var $previous = $modal.find(".previous"), $next = $modal.find(".next");
								$previous.find("a").on("click", function() {
									if (!$previous.hasClass("disabled")) {
										var $current = $modal.find("fieldset:visible"), $prev = $current.prev();
										if ($prev.length) {
											$current.hide();
											if (!$prev.show().prev().length)
												$previous.addClass("disabled")
											if ($next.hasClass("disabled"))
												$next.removeClass("disabled");
										}
									}
									return false;
								});

								$next.find("a").on("click", function() {
									if (!$next.hasClass("disabled")) {
										var $current = $modal.find("fieldset:visible"), $nextElement = $current.next();
										if ($nextElement.length) {
											$current.hide();
											if (!$nextElement.show().next().length)
												$next.addClass("disabled")
											if ($previous.hasClass("disabled"))
												$previous.removeClass("disabled");
										}
									}
									return false;
								});

								$modal.find("select[name='implementationRate']").on(
										"change",
										function() {
											var $this = $(this), $parent = $this.closest("fieldset"), idMeasure = $parent.attr("data-trick-id"), className = $this
													.attr("data-trick-class"), type = className == "MaturityMeasure" ? "int" : "double";
											$this.parent().removeClass("has-error has-success");
											$.ajax({
												url : context + "/Analysis/EditField/" + className + "/" + idMeasure,
												type : "post",
												data : '{"id":' + idMeasure + ', "fieldName":"implementationRate", "value":"' + defaultValueByType($this.val(), type, true)
														+ '", "type": "' + type + '"}',
												contentType : "application/json;charset=UTF-8",
												success : function(response, textStatus, jqXHR) {
													if (response["success"] != undefined) {
														$this.parent().addClass("has-success");
														reloadMeasureRow(idMeasure, $parent.attr("data-trick-parent-id"));
													} else {
														if (response["error"] != undefined)
															showDialog("#alert-dialog", response["error"]);
														else
															showDialog("#alert-dialog", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
														$this.parent().addClass("has-error");
													}
													return true;
												},
												error : function(jqXHR, textStatus, errorThrown) {
													showDialog("#alert-dialog", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
													$this.parent().addClass("has-error");
												}
											});
										});

							}
						})
					},
					error : unknowError
				}).complete(function() {
			$progress.modal("hide");
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.synchronise.no_action.required", "None of the selected measures is related to a task"));
	return false;
}

function isLinkedTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return true;
	return $("tbody>tr input:checked", section).closest("tr").attr("data-is-linked") === "true";
}

function isUnLinkedTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return true;
	var $element = $("tbody>tr input:checked", section).closest("tr");
	if ($element.length != 1 || !$element.has("data-is-linked"))
		return true;
	return $element.attr("data-is-linked") === "false";
}
