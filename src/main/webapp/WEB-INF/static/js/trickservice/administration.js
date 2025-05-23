/**
 * This file contains the JavaScript code for the administration functionality of the TrickService application.
 * It includes functions for installing TrickService, switching customers and owners, managing analysis access, and managing IDS access.
 * The code also includes event handlers for various buttons and checkboxes.
 */
$(document).ready(function () {
	$("input[type='checkbox']").removeAttr("checked");
	application["settings-fixed-header"] = {
		fixedOffset: $(".nav-tab"),
		marginTop: application.fixedOffset
	};
	setTimeout(() => fixTableHeader("#tab-container table"), 300);

	$(window).scroll(function (e) {
		if (($(window).scrollTop() + $(window).height()) >= $(document).height() * .98) {
			let $selectedTab = $(".tab-pane.active"), attr = $selectedTab.attr("data-scroll-trigger");
			if ($selectedTab.attr("data-update-required") === "false" && typeof attr !== typeof undefined && attr !== false)
				window[$selectedTab.attr("data-scroll-trigger")].apply();
		}
	});

	$("#btn-add-notification").on("click", notificationForm);
	$("#btn-clear-notification").on("click", clearNotification);
});

/**
 * Installs the OpenTRICK.
 * 
 * @returns {boolean} Returns false.
 */
function installTrickService() {
	$.ajax({
		url: context + "/Install",
		type: "POST",
		async: true,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else if (response["idTask"] != undefined)
				application['taskManager'].Start();
		},
		error: unknowError
	});
	return false;
}

/**
 * Switches the customer for a given section.
 * 
 * @param {string} section - The section identifier.
 * @returns {boolean} Returns false if the profile is not found or if there are multiple selected analyses, otherwise returns true.
 */
function switchCustomer(section) {
	let selectedAnalysis = findSelectItemIdBySection(section);
	if (isProfile("#" + section) || selectedAnalysis.length != 1)
		return false;
	let $progress = $("#loading-indicator").show(), idAnalysis = selectedAnalysis[0];
	$.ajax({
		url: context + "/Admin/Analysis/" + idAnalysis + "/Switch/Customer",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $content = $("#switchCustomerModal", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				if ($("#switchCustomerModal").length)
					$("#switchCustomerModal").replaceWith($content);
				else
					$content.appendTo("#widget");
				$content.find(".modal-footer>button[name='save']").on("click", function () {
					$content.find(".label").remove();
					$progress.show();
					$.ajax({
						url: context + "/Admin/Analysis/" + idAnalysis + "/Switch/Customer/" + $content.find("select").val(),
						type: "post",
						contentType: "application/json;charset=UTF-8",
						success: function (response, textStatus, jqXHR) {
							if (response["success"] != undefined) {
								adminCustomerChange($("#tab-analyses").find("select"));
								$content.modal("hide");
							} else if (response["error"] != undefined)
								$("<label class='label label-danger' />").text(response["error"]).appendTo($content.find("select").parent());
							else
								unknowError();
						},
						error: unknowError
					}).complete(function () {
						$progress.hide();
					});
				});
				$content.modal("show");
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Switches the owner of a section.
 * 
 * @param {string} section - The section to switch the owner for.
 * @returns {boolean} - Returns false if the profile is selected or if there is more than one selected analysis, otherwise returns true.
 */
function switchOwner(section) {
	let selectedAnalysis = findSelectItemIdBySection(section);
	if (isProfile("#" + section) || selectedAnalysis.length != 1)
		return false;
	let $progress = $("#loading-indicator").show(), idAnalysis = selectedAnalysis[0];
	$.ajax({
		url: context + "/Admin/Analysis/" + idAnalysis + "/Switch/Owner",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else {
				let $content = $(new DOMParser().parseFromString(response, "text/html")).find("#switchOwnerModal");
				if ($content.length) {
					if ($("#switchOwnerModal").length)
						$("#switchOwnerModal").replaceWith($content);
					else
						$content.appendTo($("#widget"));
					$content.find(".modal-footer>button[name='save']").on("click", function () {
						$content.find(".label").remove();
						$progress.show();
						$.ajax({
							url: context + "/Admin/Analysis/" + idAnalysis + "/Switch/Owner/" + $content.find("select").val(),
							type: "post",
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								if (response["success"] != undefined) {
									$("#tab-analyses").find("select").trigger("change");
									$content.modal("hide");
								} else if (response["error"] != undefined)
									$("<label class='label label-danger'>" + response["error"] + "</label>").appendTo($content.find("select").parent());
								else
									unknowError();
							},
							error: unknowError
						}).complete(function () {
							$progress.hide();
						});
					});
					$content.modal("show");
				} else
					unknowError();
			}
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Manages the access for a specific analysis.
 * 
 * @param {number} analysisId - The ID of the analysis.
 * @param {string} section_analysis - The section of the analysis.
 * @returns {boolean} - Returns false if the profile is invalid or if the analysisId is null or undefined, otherwise returns true.
 */
function manageAnalysisAccess(analysisId, section_analysis) {
	if (isProfile("#" + section_analysis))
		return false;
	if (analysisId == null || analysisId == undefined) {
		let selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Analysis/" + analysisId + "/ManageAccess",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $content = $("#manageAnalysisAccessModel", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				$("#manageAnalysisAccessModel").replaceWith($content);
				$content.modal("show").find(".modal-footer button[name='save']").one("click", updateAnalysisAccess);
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;

}

/**
 * Updates the analysis access for a user.
 * 
 * @param {Event} e - The event object.
 */
function updateAnalysisAccess(e) {

	let $progress = $("#loading-indicator").show(), $modal = $("#manageAnalysisAccessModel"), me = $modal.attr("data-trick-user-id"), data = {
		analysisId: $modal.attr("data-trick-id"),
		userRights: {}
	};

	$modal.find(".form-group[data-trick-id][data-default-value]").each(function () {
		let $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
		if (newRight != oldRight) {
			data.userRights[$this.attr("data-trick-id")] = {
				oldRight: oldRight == "" ? undefined : oldRight,
				newRight: newRight == "" ? undefined : newRight
			};
		}
	});

	if (Object.keys(data.userRights).length) {
		$.ajax({
			url: context + "/Admin/Analysis/ManageAccess/Update",
			type: "post",
			data: JSON.stringify(data),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.error != undefined)
					showDialog("#alert-dialog", response.error);
				else if (response.success != undefined) {
					if (data.userRights[me] != undefined && data.userRights[me].oldRight != data.userRights[me].newRight)
						reloadSection("section_analysis");
					else
						showDialog("success", response.success);
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$progress.hide();
}

/**
 * Manages the IDS access for a specific section.
 * 
 * @param {string} section - The section identifier.
 * @returns {boolean} - Returns false if the analysis type is not "QUANTITATIVE" or if it is a profile, otherwise returns true.
 */
function manageAnalysisIDSAccess(section) {
	if (!isAnalysisType("QUANTITATIVE", "#" + section) || isProfile("#" + section))
		return false;
	let selectedAnalysis = findSelectItemIdBySection(section);
	if (selectedAnalysis.length != 1)
		return false;
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Manage/IDS/" + selectedAnalysis[0],
		type: "GET",
		success: function (response, textStatus, jqXHR) {
			let $content = $("#manageAnalysisIDSAccessModel", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				$content.appendTo("#widget").on("hidden.bs.modal", function () {
					$content.remove();
				}).modal("show").find(".modal-footer button[name='save']").one("click", function () {
					let data = {};
					$content.find(".form-group[data-trick-id][data-default-value]").each(function () {
						let $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
						if (newRight != oldRight)
							data[this.getAttribute("data-trick-id")] = newRight;
					});
					if (Object.keys(data).length) {
						$.ajax({
							url: context + "/Admin/Manage/IDS/" + selectedAnalysis[0] + "/Update",
							type: "post",
							data: JSON.stringify(data),
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								if (response.error != undefined)
									showDialog("#alert-dialog", response.error);
								else if (response.success != undefined)
									showDialog("success", response.success);
								else
									unknowError();
							},
							error: unknowError
						}).complete(function () {
							$progress.hide();
						});
					} else
						$progress.hide();
				});
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Finds the value of the "data-trick-is-profile" attribute for the given element or its closest ancestor.
 * @param {HTMLElement} element - The element to search for the "data-trick-is-profile" attribute.
 * @returns {string|null} - The value of the "data-trick-is-profile" attribute, or null if not found.
 */
function findTrickisProfile(element) {
	if (element && element.length == 1)
		if ($(element).attr("data-trick-is-profile") != undefined)
			return $(element).attr("data-trick-is-profile");
		else
			return findTrickisProfile($(element).closest("[data-trick-is-profile]"));
	else
		return null;
}

/**
 * Checks if a profile is the default profile.
 *
 * @param {string} section - The section where the profile is located.
 * @param {string} id - The ID of the profile to check. If undefined or null, it checks the selected profile.
 * @returns {boolean} - True if the profile is the default profile, false otherwise.
 */
function isDefaultProfile(section, id) {
	if (id === undefined || id === null) {
		let $items = $(section + ">table>tbody :checked");
		if ($items.length !== 1)
			return false;
		return $items.closest("[data-trick-is-profile='true'][data-trick-is-default='true']").length > 0;
	} else return $(section + ">table>tbody>tr[data-trick-id='" + id + "'][data-trick-is-profile='true'][data-trick-is-default='true']").length > 0;
}

/**
 * Checks if the given section has a default profile.
 *
 * @param {string} section - The section to check.
 * @returns {boolean} - True if the section has a default profile, false otherwise.
 */
function hasDefaultProfile(section) {
	return $(section + ">table>tbody :checked").closest("[data-trick-is-profile='true'][data-trick-is-default='true']").length > 0
}

/**
 * Checks if the given section is a profile.
 *
 * @param {string} section - The section to check.
 * @returns {boolean} - Returns true if the section is a profile, false otherwise.
 */
function isProfile(section) {
	return findTrickisProfile($(section + " tbody :checked")) === "true";
}

/**
 * Handles the change event of the customer selector.
 * Makes an AJAX request to retrieve analysis data for the selected customer and updates the UI accordingly.
 *
 * @param {string} selector - The selector for the customer dropdown element.
 * @returns {boolean} - Returns false to prevent the default form submission behavior.
 */
function adminCustomerChange(selector) {
	let customer = $(selector).find("option:selected").val();
	$.ajax({
		url: context + "/Admin/Analysis/DisplayByCustomer/" + customer,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $newSection = $("#section_admin_analysis", new DOMParser().parseFromString(response, "text/html"));
			if ($newSection.length) {
				$("#section_admin_analysis").replaceWith($newSection);
				fixTableHeader($("table", $newSection));
			} else
				unknowError();
		},
		error: unknowError
	});
	return false;
}

/**
 * Deletes the admin analysis with the specified analysisId or section_analysis.
 * If analysisId is null or undefined, it finds the selected analysis items by section_analysis.
 * If analysisId is an array, it uses the provided analysisId.
 * If analysisId is not an array, it adds analysisId to the selectedAnalysis array.
 * Removes any items from selectedAnalysis that have a default profile.
 * Sends an AJAX request to delete the selected analysis items.
 * Updates the UI after successful deletion.
 * Shows an error message if deletion fails.
 * 
 * @param {string|number|null|undefined|Array} analysisId - The ID(s) of the analysis item(s) to delete.
 * @param {string} section_analysis - The section of the analysis item(s).
 * @returns {boolean} - Returns false if no analysis items are selected or if the deletion fails.
 */
function deleteAdminAnalysis(analysisId, section_analysis) {
	let selectedAnalysis = [];
	if (analysisId == null || analysisId == undefined) {
		selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (!selectedAnalysis.length)
			return false;
	} else if (!Array.isArray(analysisId))
		selectedAnalysis[selectedAnalysis.length] = analysisId;
	else
		selectedAnalysis = analysisId;

	selectedAnalysis.removeIf(i => isDefaultProfile("#" + section_analysis, i));

	if (!selectedAnalysis.length)
		return false;

	let $modal = showDialog("#deleteAnalysisModel", MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?", selectedAnalysis.length));
	$("button[name='delete']", $modal).unbind().one("click", function () {
		let $progress = $("#loading-indicator").show();
		$.ajax(
			{
				url: context + "/Admin/Analysis/Delete",
				type: "post",
				contentType: "application/json;charset=UTF-8",
				data: JSON.stringify(selectedAnalysis),
				success: function (response, textStatus, jqXHR) {
					if (response === true)
						$("#section_admin_analysis select").change();
					else if (response === false)
						showDialog("#alert-dialog", selectedAnalysis.length == 1 ? MessageResolver("failed.delete.analysis", "Analysis cannot be deleted!")
							: MessageResolver("failed.delete.analyses", "Analyses cannot be deleted!"));
					else
						unknowError();
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		$modal.modal("hide");
		return false;
	});

	return false;
}

/**
 * Updates the log filter based on the selected options.
 * @param {HTMLElement} element - The element that triggered the update. (optional)
 * @returns {boolean} Returns false if the element is unchecked, otherwise returns true.
 */
function updateLogFilter(element) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	let data = $("#logFilterForm").serializeJSON();
	if (data["level"] === "ALL")
		delete data["level"];
	if (data["type"] === "ALL")
		delete data["type"];
	if (data["author"] === "ALL")
		delete data["author"];
	if (data["action"] === "ALL")
		delete data["action"];
	$.ajax({
		url: context + "/Admin/Log/Filter/Update",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				return loadSystemLog();
			else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		},
		error: unknowError
	});
	return false;
}

/**
 * Loads the system log by making an AJAX request to the server.
 * 
 * @returns {boolean} Returns true if the system log is successfully loaded, otherwise false.
 */
function loadSystemLog() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Log/Section",
		async: false,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $section = $("#section_log", new DOMParser().parseFromString(response, "text/html"));
			if ($section.length) {
				$("#section_log").replaceWith($section);
				fixTableHeader($("table.table-fixed-header-analysis", $section));
			} else
				unknowError();
		},
		error: unknowError

	}).complete(() => {
		$progress.hide();
	});
	return true;
}

/**
 * Loads the system log with scrolling functionality.
 * 
 * @returns {boolean} Returns true if the system log is loaded successfully, otherwise false.
 */
function loadSystemLogScrolling() {
	let currentSize = $("#section_log table>tbody>tr").length, size = parseInt($("#logFilterPageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Admin/Log/Section",
			async: false,
			data: {
				"page": (currentSize / size) + 1
			},
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_log>table>tbody>tr").each(function () {
					let $current = $("#section_log>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_log>table>tbody"));
				});
				return false;
			},
			error: unknowError
		}).complete(() => {
			$progress.hide();
		});
	}
	return true;
}

/**
 * Updates the setting based on the provided form and sender.
 *
 * @param {string} idForm - The ID of the form to be updated.
 * @param {string} sender - The sender element that triggered the update.
 * @returns {boolean} Returns false.
 */
function updateSetting(idForm, sender) {
	let $form = $(idForm), $sender = $(sender), olvalue = $sender.attr("placeholder"), value = $sender.val();
	if ($sender.attr("type") != "radio" && value !== olvalue || $sender.is(":checked")) {
		$.ajax({
			url: context + "/Admin/TSSetting/Update",
			async: false,
			type: "post",
			data: JSON.stringify($form.serializeJSON()),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response !== true)
					unknowError();
				else {
					if ($sender.attr("type") != "radio") {
						$sender.parent().addClass("has-success");
						if ($sender.hasAttr("placeholder"))
							$sender.attr("placeholder", value);
						setTimeout(() => {
							$sender.parent().removeClass("has-success")
						}, 5000);
					}
					staticReload($form[0].id);
				}
				return false;
			},
			error: unknowError
		});
	}
	return false;
}

/**
 * Loads notifications from the server and inserts or updates them in the notification container.
 * @returns {boolean} Returns false to prevent the default form submission behavior.
 */
function loadNotification() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Notification/ALL",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (Array.isArray(response) && response.length) {
				let $container = $("#notification-content");
				for (let notification of response)
					insertOrUpdateNotification(notification, $container);
			}
			return false;
		},
		error: unknowError
	}).complete(() => {
		$progress.hide();
	});

	return false;
}

/**
 * Inserts or updates a notification in the specified container.
 * If the container is not provided, it defaults to "#notification-content".
 *
 * @param {Object} notification - The notification object to insert or update.
 * @param {jQuery} [$container] - The container element where the notification should be inserted or updated.
 * @returns {boolean} - Returns false.
 */
function insertOrUpdateNotification(notification, $container) {
	if (!$container)
		$container = $("#notification-content");
	let $current = $("[data-trick-id='" + notification.id + "'][data-role='notification']", $container);
	if (!$current.length) {
		$current = $("<div class='col-lg-4 col-md-6' data-role='notification' ><div class='panel'><div class='panel-heading'><h3 class='panel-title'><span style='display:block; margin-bottom:8px;'><span data-role='type' class='col-xs-6' style='padding-left:0'/><span data-role='control' class='col-xs-6 text-right' style='padding-right: 0;'><span class='btn-group'><button class='btn btn-xs btn-warning' name='edit'><i class='fa fa-edit'></i></button><button class='btn btn-xs btn-danger' name='delete'><i class='fa fa-remove'></i></button></span></span><span class='clearfix'/></span><span style='display:block;'><span class='col-xs-6' data-role='startDate' style='padding-left:0'/><span class='col-xs-6 text-right' data-role='endDate'/> <span class='clearfix'/></span></h3></div><div class='panel-body'><fieldset><legend>Français</legend><div lang='fr' data-trick-content='text'></div></fieldset><fieldset><legend>English</legend><div lang='en' data-trick-content='text'></div></fieldset></div><div class='panel-footer'><span data-role='created' /></div></div></div>");
		$current.attr("data-trick-id", notification.id);
		$("button[name='delete']", $current).on("click", (e) => { deleteNotification(notification.id); });
		$("button[name='edit']", $current).on("click", (e) => { notificationForm(e, notification.id); });
	}

	let locale = application.language == 'en' ? 'en-GB' : 'fr-FR', options = { weekday: "long", year: "numeric", month: "long", day: "numeric", hour: "numeric", minute: "numeric" };
	let type = notification.type.toLowerCase(), $panel = $("div.panel", $current), $body = $("div.panel-body", $panel);

	$panel.attr("class", "panel panel-" + (type === 'error' ? 'danger' : type));

	$("[data-role='type']", $panel).text(MessageResolver("label.log.level." + type, type.capitalize()));

	$("[data-role='created']", $panel).text(MessageResolver("label.created.date", "Created at: ") + new Date(notification.created).toLocaleString(locale, options));

	if (notification.startDate)
		$("[data-role='startDate']", $panel).text(MessageResolver("label.notification.date.from", "From at: ") + new Date(notification.startDate).toLocaleString(locale, options));
	else $("[data-role='startDate']", $panel).text(MessageResolver("label.notification.date.from", "From at: ") + "-");

	if (notification.endDate)
		$("[data-role='endDate']", $panel).text(MessageResolver("label.notification.date.until", "Until: ") + new Date(notification.endDate).toLocaleString(locale, options));
	else $("[data-role='endDate']", $panel).text(MessageResolver("label.notification.date.until", "Until: ") + "-");

	for (let lang in notification.messages) {
		let $language = $("[lang='" + lang + "']", $body);
		if ($language.length)
			$language.text(notification.messages[lang]);
	}

	$current.appendTo($container);

	return false;
}

/**
 * Clears the notification.
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns false.
 */
function clearNotification(e) {
	let $confirmModal = showDialog("#confirm-dialog", MessageResolver("confirm.clear.notification", "Are you sure, you want to clear notification?"));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function (e) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Admin/Notification/Clear",
			type: "delete",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.success) {
					$("#notification-content").empty();
					showDialog("success", response.success);
				} else if (response.error)
					showDialog("error", response.error);
				else unknowError();
			},
			error: unknowError
		}).complete(() => {
			$progress.hide();
		});
	});
	return false;
}

/**
 * Deletes a notification with the specified ID.
 * 
 * @param {number} id - The ID of the notification to delete.
 * @returns {boolean} - Returns false.
 */
function deleteNotification(id) {
	let $confirmModal = showDialog("#confirm-dialog", MessageResolver("confirm.delete.notification", "Are you sure, you want to delete this notification?"));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function (e) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Admin/Notification/" + id + "/Delete",
			type: "delete",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.success) {
					$('#notification-content div[data-role="notification"][data-trick-id="' + id + '"]').remove();
					showDialog("success", response.success);
				} else if (response.error)
					showDialog("error", response.error);
				else unknowError();
			},
			error: unknowError
		}).complete(() => {
			$progress.hide();
		});
	});
	return false;
}

/**
 * Handles the notification form.
 *
 * @param {Event} e - The event object.
 * @param {string} id - The ID of the notification.
 * @returns {boolean} - Returns false.
 */
function notificationForm(e, id) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + (id ? "/Admin/Notification/" + id + "/Edit" : "/Admin/Notification/Add"),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $view = $("#modal-add-notification", new DOMParser().parseFromString(response, "text/html"));
			if (!$view.length)
				unknowError();
			else {
				$view.appendTo("#widget");
				$view.modal("show").on('hidden.bs.modal', () => $view.remove());
				$("button[name='save']", $view).on("click", saveNotification);
			}
			return false;
		},
		error: unknowError
	}).complete(() => {
		$progress.hide();
	});

	return false;
}


/**
 * Parses a date and time string and returns a Date object.
 * If only a date is provided, the time is set to 00:00.
 * If only a time is provided, the date is set to the current date.
 * If both date and time are empty or invalid, undefined is returned.
 *
 * @param {string} date - The date string in the format "YYYY-MM-DD".
 * @param {string} time - The time string in the format "HH:MM".
 * @returns {Date|undefined} The parsed Date object or undefined if the input is invalid.
 */
function parseDate(date, time) {
	if (date.length && time.length)
		return new Date(date + "T" + time);
	else if (date.length)
		return new Date(date + "T00:00");
	else {
		let current = new Date(), times = time.split(":");
		if (times.length != 2)
			return undefined;
		current.setHours(times[0]);
		current.setMinutes(times[1])
		return current;
	}
}

/**
 * Saves the notification data.
 *
 * @param {Event} e - The event object.
 */
function saveNotification(e) {
	let $progress = $("#loading-indicator").show(), $view = $("#modal-add-notification"), $form = $("form", $view), data = $form.serializeJSON(), keys = Object.keys(data);

	data["messages"] = {};

	for (let key of keys) {
		if (key.startsWith("messages[")) {
			data["messages"][key.replace(/messages\[|\]/g, '')] = data[key];
			delete data[key];
		}
	}

	data.startDate = parseDate(data.startDate, data.startDateTime);
	data.endDate = parseDate(data.endDate, data.endDateTime);

	if (!data.startDate || typeof data.startDate === "invalid date")
		delete data["startDate"];

	if (!data.endDate || typeof data.endDate === "invalid date")
		delete data["endDate"];

	delete data["startDateTime"];

	delete data["endDateTime"];

	$.ajax({
		url: context + "/Admin/Notification/Save",
		type: "post",
		data: JSON.stringify({ data: data }),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response.error)
				showDialog("error", response.error);
			else if (response.length == 2) {
				$view.modal("hide");
				showDialog("success", response[0]);
				insertOrUpdateNotification(response[1]);
			}
			else unknowError();
			return false;
		},
		error: unknowError
	}).complete(() => {
		$progress.hide();
	});
}


/**
 * Manages customer access.
 * 
 * @param {number} customerID - The ID of the customer.
 * @returns {boolean} - Returns false if the customer profile is active or if the customerID is null or undefined. Otherwise, returns true.
 */
function manageCustomerAccess(customerID) {
	if (isCustomerProfile())
		return false;
	if (customerID == null || customerID == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerID = selectedScenario[0];
	}
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Customer/" + customerID + "/Manage-access",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $view = $(new DOMParser().parseFromString(response, "text/html")).find("#manageCustomerUserModel");
			if ($view.length) {
				$view.appendTo("#widget").modal("show").on("hidden.bs.modal", () => $view.remove());
				$("button[name='save']", $view).on("click", e => updateCustomerAccess(e, $view, $progress, customerID));
			} else
				unknowError();
			return false;
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Checks if the current profile is not a customer profile.
 * @returns {boolean} Returns true if the current profile is not a customer profile, false otherwise.
 */
function isNotCustomerProfile() {
	return !isCustomerProfile();
}


/**
 * Checks if the current profile is a customer profile.
 * @returns {boolean} Returns true if the profile is a customer profile, otherwise false.
 */
function isCustomerProfile() {
	return isProfile("#section_customer");
}

/**
 * Checks if the selected ticketing system type matches the given type.
 * 
 * @param {string} type - The ticketing system type to check against.
 * @returns {boolean} - Returns true if the selected ticketing system type matches the given type, false otherwise.
 */
function isTicketingType(type) {
	let $selections = $("#section_customer>table>tbody>tr input:checked");
	if ($selections.length !== 1)
		return false;
	return $selections.closest("tr").find("td[data-trick-name='tickecting_system_type'][data-real-value='" + type + "']").length > 0;
}

/**
 * Updates the customer access based on the selected values in the view.
 * @param {Event} e - The event object.
 * @param {jQuery} $view - The jQuery object representing the view.
 * @param {jQuery} $progress - The jQuery object representing the progress element.
 * @param {string} customerID - The ID of the customer.
 */
function updateCustomerAccess(e, $view, $progress, customerID) {
	let data = {};
	$view.find(".form-group[data-trick-id][data-default-value]").each(function () {
		let $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
		if (newRight != oldRight)
			data[$this.attr("data-trick-id")] = newRight;
	});
	if (Object.keys(data).length) {
		$progress.show();
		$.ajax({
			url: context + "/Admin/Customer/" + customerID + "/Manage-access/Update",
			type: "post",
			data: JSON.stringify(data),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.error != undefined)
					showDialog("#alert-dialog", response.error);
				else if (response.success != undefined)
					showDialog("success", response.success);
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
}

/**
 * Checks if the given HTML string is valid.
 * 
 * @param {string} html - The HTML string to validate.
 * @param {function} [callback] - Optional callback function to handle the result.
 * @return {boolean} - Returns true if the HTML is valid, false otherwise.
 * */
function isValidHTML(html, callback) {
	let result = false;
	try {
		// Use XML parsing to detect well-formedness errors
		const doc = new DOMParser().parseFromString(html, 'application/xhtml+xml');
		result = !doc.querySelector('parsererror');
	} catch (err) {
		console.error("Error parsing HTML:", err);
	}
	if (typeof callback === "function") 
		callback(result);
	return result;

}

/**
 * Checks if the given JSON string is valid.
 * 
 * @param {string} json - The JSON string to validate.
 * @param {function} [callback] - Optional callback function to handle the result.
 * @return {boolean} - Returns true if the JSON is valid, false otherwise.
 * */
function isValidJSON(json, callback) {
	let result = false;
	try {
		result = JSON.parse(json) !== null;
	} catch (err) {
		console.error("Error parsing JSON:", err);
	}
	if (callback != undefined && typeof callback === "function")
		callback(result);
	return result;
}

function hideError(element) {
	$(element).removeClass("has-error").parent().removeClass("has-error").find(".label-danger").remove();
}

/**
 * Edits the ticketing system email template for a given customer.
 * 
 * @param {number} customerID - The ID of the customer.
 * @returns {boolean} Returns false if the ticketing type is not "EMAIL" or if the customer ID is null or undefined.
 */
function editTicketingSystemEmailTemplate(customerID) {
	if (!isTicketingType("EMAIL"))
		return false;
	if (customerID == null || customerID == undefined) {
		let $selections = findSelectItemIdBySection("section_customer");
		if ($selections.length !== 1)
			return false;
		customerID = $selections[0];
	}
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Customer/" + customerID + "/Email-template/Edit",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $view = $(new DOMParser().parseFromString(response, "text/html")).find("#ticketingSystemEmailTempalteModel");
			if ($view.length) {
				let availableParameters = JSON.parse($view.find("#measureAvailableParameters").text());
				let $subjectInput = $view.find("#email_template_title");
				let $subjectInputEntry = $view.find("#email_template_title_entry");
				let $subjectDatalist = $view.find("#dataListEmailSubject");


				let $current = $subjectDatalist.find("option[data-value='" + $subjectInput.val() + "']");

				if ($current.length == 1)
					$subjectInputEntry.val($current.text());
				else $subjectInputEntry.val($subjectInput.val());

				$subjectInputEntry.attr("placeholder", $subjectInputEntry.val());

				$subjectInputEntry.on("change", (e) => {
					$current = $subjectDatalist.find("option:contains('" + $subjectInputEntry.val() + "')");
					if ($current.text() === $subjectInputEntry.val())
						$subjectInput.val($current.attr("data-value"));
					else $subjectInput.val($subjectInputEntry.val());
				});

				$view.find('#email_template_template')

				$view.find('#email_template_template').suggest('$', {
					data: availableParameters,
					filter: {
						casesensitive: false,
						limit: availableParameters.length
					},
					map: function (parameter) {
						return {
							value: "{" + parameter.name + "}",
							text: '<strong>' + parameter.name + '</strong>: <small>' + parameter.title + '</small>'
						}
					}
				}).on("blur", function (e) {
					let format = $view.find("input[name='format']:checked").val();
					let $element = $view.find("#email_template_template");
					if (format === "HTML") {
						isValidHTML($element.val(), function (isValid) {
							if (!isValid) {
								if (!$element.parent().hasClass("has-error")) {
									let $errorElement = $("<label class='label label-danger'/>").text(MessageResolver("label.email.template.html.error", "Invalid HTML format!"));
									$errorElement.appendTo($element.parent());
									$element.parent().addClass("has-error");
								}
							} else
								hideError($element);
						});
					} else if (format === "JSON") {
						isValidJSON($element.val(), function (isValid) {
							if (!isValid) {
								if (!$element.parent().hasClass("has-error")) {
									let $errorElement = $("<label class='label label-danger'/>").text(MessageResolver("label.email.template.json.error", "Invalid JSON format!"));
									$errorElement.appendTo($element.parent());
									$element.parent().addClass("has-error");
								}
							} else
								hideError($element);

						});
					} else if ($element.parent().hasClass("has-error")) {
						hideError($element);
					}

				});

				$view.appendTo("#widget").modal("show").on("hidden.bs.modal", () => $view.remove());

				$("button[name='save']", $view).on("click", e => $("input[name='submit']", $view).click());


				$view.find("input[name='format']").on("change", function (e) {
					$view.find("#email_template_template").trigger("blur");
				}).trigger("change");

				$view.find("form").on("submit", e => {
					e.preventDefault();
					if ($view.find(".has-error").length) {
						showDialog("#alert-dialog", MessageResolver("label.email.template.error", "Please correct the errors before saving!"));
						return;
					}
					let $form = $(e.currentTarget)
					$progress.show();
					$.ajax({
						url: context + "/Admin/Customer/" + customerID + "/Email-template/Save",
						type: "post",
						data: JSON.stringify(convertFormToJSON($form)),
						contentType: "application/json;charset=UTF-8",
						success: function (response, textStatus, jqXHR) {
							if (response.success)
								$view.modal('hide');
							else {
								for (let error in response.errors) {
									switch (error) {
										case "format":
										case "title":
										case "email":
										case "template":
										case "internalTime":
											let $errorElement = $("<label class='label label-danger'/>").text(response.errors[error]);
											$errorElement.appendTo($("*[name='" + error + "']", $form).parent());
											break;
										default:
											showError($form.parent()[0], response.errors[error]);
									}
								}
							}
						},
						error: unknowError
					}).complete(function () {
						$progress.hide();
					});
					return false;
				});
			} else
				unknowError();
			return false;
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}