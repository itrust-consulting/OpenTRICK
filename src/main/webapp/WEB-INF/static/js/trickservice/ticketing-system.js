

/**
 * Creates tickets based on the selected section and ticketing type.
 * @param {HTMLElement} section - The section element containing the checkboxes.
 * @param {string} ticketingType - The type of ticketing system.
 * @returns {boolean} - Returns false if the application is not linked to a project, otherwise returns true.
 */
function createTickets(section, ticketingType) {
	if (!application.isLinkedToProject)
		return false;
	let measures = {
		updates: [],
		news: []
	};
	$("tbody>tr input:checked", section).closest("tr[data-is-linked='false']").each(function () { measures.news.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id")); });
	updateOrGenereteTickets(measures);
	return false;
}

/**
 * Links the analysis to a project.
 * @returns {boolean} - Returns false if the analysis cannot be linked to a project, otherwise returns true.
 */
function linkToProject() {
	let idAnalysis = findSelectItemIdBySection("section_analysis")
	if (idAnalysis.length != 1)
		return false;
	if (!isArchived(idAnalysis) && userCan(idAnalysis[0], ANALYSIS_RIGHT.ALL)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Ticketing/" + idAnalysis[0] + "/Load",
			type: "GET",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $modal = $("#modal-ticketing-project-linker", new DOMParser().parseFromString(response, "text/html")), updateRequired = false;
				if (!$modal.length)
					unknowError();
				else {
					$modal.appendTo("#widget").modal("show").on("hidden.bs.modal", function () {
						if (updateRequired)
							reloadSection("section_analysis");
						$modal.remove();
					});

					let $selector = $modal.find("select[name='project']");
					$modal.find("button[name='save']").on("click", function () {
						let project = $selector.val();
						if (project != undefined && project.length) {
							$.ajax({
								url: context + "/Analysis/Ticketing/" + idAnalysis[0] + "/Link",
								type: "POST",
								async: false,
								data: $selector.val(),
								contentType: "application/json;charset=UTF-8",
								success: function (response, textStatus, jqXHR) {
									if (response.error)
										showDialog("#alert-dialog", response.error);
									else if (response.success) {
										reloadSection("section_analysis");
										$modal.modal("hide");
									} else
										unknowError();
								},
								error: unknowError
							});
						}
						return false;
					})
				}
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

/**
 * Unlinks the analysis from a project.
 * @returns {boolean} - Returns false if the analysis cannot be unlinked from a project, otherwise returns true.
 */
function unLinkToProject() {
	let analyses = [];
	$("tbody>tr input:checked", "#section_analysis").closest("tr").each(function () {
		let idAnalysis = this.getAttribute("data-trick-id");
		if (this.getAttribute("data-is-linked") === "true" && this.getAttribute("data-trick-archived") === "false" && userCan(idAnalysis, ANALYSIS_RIGHT.ALL))
			analyses.push(idAnalysis);
	});
	if (analyses.length) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Ticketing/UnLink",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(analyses),
			success: function (response, textStatus, jqXHR) {
				if (response.error)
					showDialog("#alert-dialog", response.error);
				else if (response.success) {
					reloadSection("section_analysis");
					showDialog("#info-dialog", response.success);
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

/**
 * Opens a ticket based on the selected section.
 * @param {HTMLElement} section - The section element containing the checkboxes.
 * @returns {boolean} - Returns false if the application is not linked to a project, otherwise returns true.
 */
function openTicket(section) {
	if (!application.isLinkedToProject)
		return false;
	let measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Ticketing/Measure/Open",
			type: "POST",
			async: false,
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(measures),
			success: function (response, textStatus, jqXHR) {
				let $modal = $("#modal-ticketing-view", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length) {
					$("#modal-ticketing-view").remove();
					$modal.appendTo($("#widgets")).modal("show");
					let $previous = $modal.find(".previous"), $next = $modal.find(".next"), $title = $modal.find(".modal-title");
					$next.find("a").on("click", function () {
						if (!$next.hasClass("disabled")) {
							let $current = $modal.find("fieldset:visible"), $nextElement = $current.next();
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

					$previous.find("a").on("click", function () {
						if (!$previous.hasClass("disabled")) {
							let $current = $modal.find("fieldset:visible"), $prev = $current.prev();
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
				} else if (response["error"])
					showDialog("#alert-dialog", response['error']);
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.open.no_action.required", "None of the selected measures is related to a task"));
	return false;
}

/**
 * Links the selected measures to the ticketing system.
 * @param {HTMLElement} section - The section element containing the checkboxes.
 * @returns {boolean} - Returns false if the application is not linked to a project, otherwise returns true.
 */
function linkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	let measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "false")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Ticketing/Measure/Link-form",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(measures),
			success: function (response, textStatus, jqXHR) {
				if (response['error'])
					showDialog("#alert-dialog", response['error']);
				else {
					let $modal = $("#modal-ticketing-linker", new DOMParser().parseFromString(response, "text/html")), updateRequired = false;
					if (!$modal.length)
						unknowError();
					else {
						$("#modal-ticketing-linker").remove();
						$modal.appendTo($("#widgets")).modal("show");
						let isFinished = false, $linker = $modal.find("#measure-task-linker"), $measureViewer = $modal.find("#measure-viewer"), $taskViewer = $("#task-viewer"), $taskContainer = $modal
							.find("#task-container"), $tasks = $taskContainer.find("fieldset"), size = parseInt($taskContainer.attr("data-offset"));
						taskController = function () {
							$view = $(this.getAttribute("href"));
							if (!$view.is(":visible")) {
								$taskViewer.find("fieldset:visible").hide();
								$view.show();
							}
							return false;
						}

						$tasks.appendTo($taskViewer);

						$taskContainer.find("a.list-group-item").on("click", taskController);

						$modal.on("hidden.bs.modal", function () {
							if (updateRequired)
								reloadSection("section_actionplans");
							$modal.remove();
						}).on("show.bs.modal", function () {
							$taskContainer.scrollTop();
						}).on("shown.bs.modal", function () {
							$taskContainer.on("scroll", function () {
								if (!isFinished && (($taskContainer.scrollTop() + $taskContainer.innerHeight()) >= $taskContainer[0].scrollHeight)) {
									isFinished = true;
									$.ajax({
										url: context + "/Analysis/Ticketing/Measure/Load?startIndex=" + size,
										type: "POST",
										contentType: "application/json;charset=UTF-8",
										data: JSON.stringify(measures),
										success: function (response, textStatus, jqXHR) {
											$subTaskContainer = $("#task-container", new DOMParser().parseFromString(response, "text/html"));
											let $subTasks = $subTaskContainer.find("fieldset");
											if (!(isFinished = $subTasks.length == 0)) {
												$subTasks.appendTo($taskViewer);
												$subTaskContainer.find("a.list-group-item").appendTo($taskContainer).on("click", taskController);
											}
											size = parseInt($subTaskContainer.attr("data-offset"))
										}
									});
								}
							});

						});

						$modal.find("#measure-container>fieldset").appendTo($measureViewer);
						$modal.find("#measure-container>a.list-group-item").on("click", function () {
							$view = $(this.getAttribute("href"));
							if (!$view.is(":visible")) {
								$measureViewer.find("fieldset:visible").hide();
								$view.show();
							}
							return false;
						});

						$linker.on('click', function () {
							let $measure = $measureViewer.find("fieldset:visible"), $ticket = $taskViewer.find("fieldset:visible")
							if ($measure.length && $ticket.length) {
								$progress.show();
								$linker.prop("disabled", true)
								$.ajax({
									url: context + "/Analysis/Ticketing/Measure/Link",
									type: "POST",
									contentType: "application/json;charset=UTF-8",
									data: JSON.stringify({
												"idMeasure": $measure.attr("data-trick-id"),
												"idTicket": $ticket.attr("data-trick-id")
											}),
											success: function (response, textStatus, jqXHR) {
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
											error: unknowError
										}).complete(function () {
											$linker.prop("disabled", false);
											$progress.hide();
										});
									}
									return false;
								});

							}
						}
					},
					error: unknowError
				}).complete(function () {
					$progress.hide();
				});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.link.no_action.required", "All selected measures are already related to tasks"));
	return false;
}

/**
 * Unlinks measures and tasks from the ticketing system.
 * 
 * @param {string} section - The section where the unlinking is performed.
 * @returns {boolean} - Returns false if the application is not linked to a project.
 */
function unLinkToTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	let measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});
	if (measures.length) {
		let $confirm = $("#confirm-dialog"), $question = measures.length == 1 ? MessageResolver("confirm.unlink.measure", "Are you sure, you want to unlink this measure and task")
			: MessageResolver("confirm.unlink.measures", "Are you sure, you want to unlink measures and tasks");
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function () {
			let $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/Analysis/Ticketing/Measure/UnLink",
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify(measures),
				success: function (response, textStatus, jqXHR) {
					if (response.error)
						showDialog("#alert-dialog", response.error);
					else if (response.success) {
						if (section == "#section_actionplans")
							location.reload();
						else {
							showDialog("#info-dialog", response.success);
							if (measures.length > 30)
								reloadSection([section.replace("#", ''), "section_actionplans"]);
							else {
								reloadSection("section_actionplans");
								setTimeout(function () {
									let idStandard = $(section).attr("data-trick-id");
									for (let i = 0; i < measures.length; i++)
										reloadMeasureRow(measures[i], idStandard);
								}, measures.length * 20);
							}
						}
					} else
						unknowError();
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		});
		$confirm.modal("show");
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.unlink.no_action.required", "None of the selected measures is related to a task"));
	return false;

}

/**
 * Updates or generates tickets based on the provided data.
 * @param {Object} data - The data used to update or generate tickets.
 * @returns {boolean} - Returns false.
 */
function updateOrGenereteTickets(data) {
	if (data != undefined && (data.updates.length || data.news.length)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Ticketing/Measure/Generate",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			data: JSON.stringify(data),
			success: function (response, textStatus, jqXHR) {
				if (response.error)
					showDialog("#alert-dialog", response.error);
				else if (response.success)
					application["taskManager"].Start();
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.generate.no_action.required", "All selected measures are already related to tasks"));
	return false;
}

/**
 * Generates tickets based on the selected checkboxes in the given section.
 * 
 * @param {HTMLElement} section - The section containing the checkboxes.
 * @returns {boolean} Returns false if the application is not linked to a project, otherwise returns true.
 */
function generateTickets(section) {
	if (!application.isLinkedToProject)
		return false;
	let measures = {
		updates: [],
		news: []
	};
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "false")
			measures.news.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
		else
			measures.updates.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.updates.length) {
		let $confirm = $("#confirm-dialog"), $question = measures.updates.length == 1 ? MessageResolver("confirm.update.ticket", "Are you sure, you want to update measure task")
			: MessageResolver("confirm.update.tickets", "Are you sure, you want to update " + measures.updates.length + " measures tasks", [measures.updates.length]);
		$confirm.find(".modal-body").text($question);
		$(".btn-danger", $confirm).click(function () {
			$confirm.modal("hide");
			updateOrGenereteTickets(measures);
		});
		$confirm.modal("show");
	} else
		updateOrGenereteTickets(measures);
	return false;
}

/**
 * Synchronizes the selected measures with the ticketing system.
 * 
 * @param {HTMLElement} section - The section containing the measures.
 * @returns {boolean} Returns false if the application is not linked to a project, otherwise returns true.
 */
function synchroniseWithTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return false;
	let measures = [];
	$("tbody>tr input:checked", section).closest("tr").each(function () {
		if (this.getAttribute("data-is-linked") === "true")
			measures.push(this.hasAttribute("data-measure-id") ? this.getAttribute("data-measure-id") : this.getAttribute("data-trick-id"));
	});

	if (measures.length) {
		let $progress = $("#loading-indicator").show();
		$.ajax(
			{
				url: context + "/Analysis/Ticketing/Measure/Synchronise",
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify(measures),
				success: function (response, textStatus, jqXHR) {
					let $modal = $("#modal-ticketing-synchronise", new DOMParser().parseFromString(response, "text/html"));
					if ($modal.length) {
						$("#modal-ticketing-synchronise").remove();
						$modal.appendTo($("#widgets")).modal("show");
						let $previous = $modal.find(".previous"), $next = $modal.find(".next");
						$previous.find("a").on("click", function () {
							if (!$previous.hasClass("disabled")) {
								let $current = $modal.find("fieldset:visible"), $prev = $current.prev();
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

						$next.find("a").on("click", function () {
							if (!$next.hasClass("disabled")) {
								let $current = $modal.find("fieldset:visible"), $nextElement = $current.next();
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
							function () {
								let $this = $(this), $parent = $this.closest("fieldset"), idMeasure = $parent.attr("data-trick-id"), className = $this
									.attr("data-trick-class"), type = className == "MaturityMeasure" ? "int" : "double";
								$this.parent().removeClass("has-error has-success");
								$.ajax({
									url: context + "/Analysis/EditField/" + className + "/" + idMeasure,
									type: "post",
									data: '{"id":' + idMeasure + ', "fieldName":"implementationRate", "value":"' + defaultValueByType($this.val(), type, true)
										+ '", "type": "' + type + '"}',
									contentType: "application/json;charset=UTF-8",
									success: function (response, textStatus, jqXHR) {
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
									error: function (jqXHR, textStatus, errorThrown) {
										showDialog("#alert-dialog", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
										$this.parent().addClass("has-error");
									}
								});
							});

					} else if (response["error"])
						showDialog("#alert-dialog", response['error']);
					else
						unknowError();
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
	} else
		showDialog("#info-dialog", MessageResolver("info.ticketing.synchronise.no_action.required", "None of the selected measures is related to a task"));
	return false;
}

/**
 * Checks if the ticketing system is linked.
 *
 * @param {HTMLElement} section - The section element containing the ticketing system.
 * @returns {boolean} - Returns true if the ticketing system is linked, false otherwise.
 */
function isLinkedTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return true;
	return $("tbody>tr input:checked", section).closest("tr").attr("data-is-linked") === "true";
}

/**
 * Checks if the ticketing system is unlinked.
 *
 * @param {HTMLElement} section - The section element containing the ticketing system.
 * @returns {boolean} - Returns true if the ticketing system is unlinked, false otherwise.
 */
function isUnLinkedTicketingSystem(section) {
	if (!application.isLinkedToProject)
		return true;
	let $element = $("tbody>tr input:checked", section).closest("tr");
	if ($element.length != 1 || !$element.has("data-is-linked"))
		return true;
	return $element.attr("data-is-linked") === "false";
}