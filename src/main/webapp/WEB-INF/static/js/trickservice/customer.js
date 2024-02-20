function initUserCustomerList() {
	$('#usercustomer').hide().after("<ul class='list-group' style='max-height: 300px; padding: 5px;margin:0; overflow: auto;'></ul>");
	$('#usercustomer option').each(function () {
		let selected = "";
		let attr = $(this).attr('selected');

		if (typeof attr !== 'undefined' && attr !== false) {
			selected = " active";
		}
		$('#customerusersform .list-group').append("<li class='list-group-item" + selected + "' data-trick-opt='" + $(this).attr('value') + "'>" + $(this).html() + "</li>");
	});
	$('#customerusersform .list-group li').on('click', function () {
		$(this).toggleClass('active');
		let allVal = new Array();
		$('#customerusersform .list-group li.active').each(function () {
			allVal.push($(this).attr("data-trick-opt"));
		});
		$('#usercustomer').val(allVal);
	});
}

function loadTargetContext() {
	return context + (application['isAdministration'] ? "/Admin" : "/KnowledgeBase");
}

function canManageCustomerTemplate() {
	return application['isAdministration'] ? isCustomerProfile() : true;
}

function getCustomerSection() {
	return application['isAdministration'] ? "section_admin_customer" : "section_customer";
}

function saveCustomer(form) {
	let $progress = $("#loading-indicator").show();
	$("#addCustomerModel .label-danger").remove();
	$.ajax({
		url: loadTargetContext() + "/Customer/Save",
		type: "post",
		data: serializeFormToJson(form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let hasError = false;
			for (let error in response) {
				let errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
					case "organisation":
						$(errorElement).appendTo($("#customer_form #customer_organisation").parent());
						break;
					case "address":
						$(errorElement).appendTo($("#customer_form #customer_address").parent());
						break;
					case "email":
						$(errorElement).appendTo($("#customer_form #customer_email").parent());
						break;
					case "city":
						$(errorElement).appendTo($("#customer_form #customer_city").parent());
						break;
					case "ZIPCode":
						$(errorElement).appendTo($("#customer_form #customer_ZIPCode").parent());
						break;
					case "country":
						$(errorElement).appendTo($("#customer_form #customer_country").parent());
						break;
					case "contactPerson":
						$(errorElement).appendTo($("#customer_form #customer_contactPerson").parent());
						break;
					case "phoneNumber":
						$(errorElement).appendTo($("#customer_form #customer_phoneNumber").parent());
						break;
					case "ticketingSystem.url":
						$(errorElement).appendTo($("#customer_form #customer_tickecting_system_url").parent());
						break;
					case "ticketingSystem.name":
						$(errorElement).appendTo($("#customer_form #customer_tickecting_system_name").parent());
						break;
					case "ticketingSystem.tracker":
						$(errorElement).appendTo($("#customer_form #customer_tickecting_system_tracker").parent());
						break;
					case "ticketingSystem.type":
						$(errorElement).appendTo($("#customer_form #customer_tickecting_system_type").parent());
						break;
					case "ticketingSystem.enabled":
						$(errorElement).appendTo($("#customer_form #customer_tickecting_system_enabled").parent());
						break;
					default:
						showDialog("#alert-dialog", response[error]);
						break;
				}
				hasError = true;
			}
			if (!hasError) {
				$("#addCustomerModel").modal("hide");
				reloadSection(getCustomerSection());
			}
			return false;

		},
		error: function (jqXHR, textStatus, errorThrown) {
			showDialog("#alert-dialog", MessageResolver("error.unknown.add.customer", "An unknown error occurred during adding customer"));
			return false;
		}
	}).complete(() => $progress.hide());
	return false;
}

function deleteCustomer(customerId, organisation) {
	if (customerId == null || customerId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		organisation = $("#section_customer tbody tr[data-trick-id='" + (customerId = selectedScenario[0]) + "']>td:nth-child(2)").text();
	}
	$("#deleteCustomerBody").html(
		MessageResolver("label.customer.question.delete", "Are you sure that you want to delete the customer <strong>" + organisation + "</strong>?", organisation));
	$("#deletecustomerbuttonYes").unbind().click(function () {
		$("#deleteCustomerModel").modal('hide');
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: loadTargetContext() + "/Customer/" + customerId + "/Delete",
			type: "POST",
			async: false,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] == undefined) {
					if (response["error"] == undefined)
						unknowError();
					else
						showDialog("#alert-dialog", response["error"]);
				} else
					reloadSection(getCustomerSection());
				return false;
			},
			error: unknowError
		}).complete(() => $progress.hide());
		return false;
	});
	$("#deleteCustomerModel").modal('show');
	return false;
}

function newCustomer() {
	$("#addCustomerModel .label-danger").remove();
	$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
	$("#customer_id").prop("value", "0");
	$("#customer_organisation").prop("value", "");
	$("#customer_contactPerson").prop("value", "");
	$("#customer_phoneNumber").prop("value", "");
	$("#customer_email").prop("value", "");
	$("#customer_address").prop("value", "");
	$("#customer_city").prop("value", "");
	$("#customer_ZIPCode").prop("value", "");
	$("#customer_country").prop("value", "");
	$("#customer_canBeUsed").prop("value", true);
	$("#addCustomerModel-title").text(MessageResolver("title.knowledgebase.Customer.Add", "Add a new Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.save", "Save"));
	$("#customer_form").prop("action", loadTargetContext() + "/Customer/Save");
	$("#addCustomerModel").modal('show');
	return false;
}

function editSingleCustomer(customerId) {
	if (customerId == null || customerId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerId = selectedScenario[0];
	}
	$("#addCustomerModel .label-danger").remove();
	$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
	$("#section_customer").find("tr[data-trick-id='" + customerId + "'] *[data-trick-name]").each(function () {
		let $this = $(this), $field = $("#customer_" + $(this).attr("data-trick-name")), value = $this.attr("data-real-value");
		if (value == undefined)
			$field.prop("value", $this.text().trim());
		else if ($field.hasClass("btn-group"))
			$field.find("*input[value='" + value + "']").click();
		else $field.prop("value", value);
	});
	$("#customer_id").prop("value", customerId);
	$("#addCustomerModel-title").text(MessageResolver("title.knowledgebase.Customer.Update", "Update a Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.save", "Save"));
	$("#customer_form").prop("action", loadTargetContext() + "/Customer/" + customerId + "/Edit");
	$("#addCustomerModel").modal('show');
	return false;
}


function manageCustomerTemplate(customerId) {
	if (customerId == null || customerId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerId = selectedScenario[0];
	}

	if (customerId == null || customerId == undefined || !canManageCustomerTemplate())
		return false;

	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: loadTargetContext() + "/Customer/" + customerId + "/Template/Manage",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $modal = $("#reportTemplateModal", new DOMParser().parseFromString(response, "text/html"));
			if ($modal.length) {

				let $tabs = $modal.find("#menu_manage_customer_template a[data-toggle='tab']");
				let $cancelBtn = $modal.find(".modal-footer button[name='cancel']");
				let $backBtn = $modal.find(".modal-footer a[role='back'].btn");
				let $saveBtn = $modal.find(".modal-footer button[name='save']")
				let $btnSubmit = $("button[name='submit']", $modal);
				let $selectType = $("select[name='type']", $modal);
				let $file = $("input[type='file']", $modal);
				let $fileInfo = $("input[name='filename']", $modal);
				let $browse = $("button[name='browse']", $modal);

				$selectType.on("change", (e) => {
					switch (e.currentTarget.value) {
						case "RISK_INFORMATION":
						case "DEFAULT_EXCEL": {
							$file.prop("accept", ".xlsx,.xlsm");
							$browse.prop("disabled", false);
							break;
						}
						case "RISK_REGISTER":
						case "RISK_SHEET":
						case "REPORT":
						case "SOA": {
							$file.prop("accept", ".docx");
							$browse.prop("disabled", false);
							break;
						}
						default: {
							$file.prop("accept", "");
							$browse.prop("disabled", true);
						}
					}
				}).trigger("change");

				$browse.on("click", (e) => $file.trigger("click"));

				$file.on("change", (e) => {
					let value = $file.val();

					if (value.trim() === '')
						$saveBtn.prop("disabled", $file.is(":required"));
					else {
						let size = parseInt($file.attr("maxlength"))
						if ($file[0].files[0].size > size) {
							showDialog("error", MessageResolver("error.file.too.large", undefined, size));
							return false;
						}
					}

					$fileInfo.val(value);
					$saveBtn.prop("disabled", false);
				});

				$saveBtn.on("click", (e) => $btnSubmit.trigger("click"));

				$("#reportTemplate-form", $modal).on("submit", saveReportTemplate);

				$tabs.on('shown.bs.tab', function () {
					$(this).parent().removeClass("active");
					$saveBtn.show();
					$backBtn.show();
					$cancelBtn.hide();
				});

				$("#menu_manage_customer_template a[role]", $modal).each(function () {
					switch (this.getAttribute("role")) {
						case "edit":
							$(this).on('show.bs.tab', editReportTemplate);
							break;
						case "add":
							$(this).on('show.bs.tab', addReportTemplate);
							break;
						case "delete":
							$(this).on('click', deleteReportTemplate);
							break;
						case "download":
							$(this).on('click', downloadReportTemplate);
							break;
					}
				});

				$backBtn.on('click', function () {
					$saveBtn.hide();
					$backBtn.hide();
					$cancelBtn.show();
					$modal.find(".label-danger,.alert-danger").remove();
				});


				$modal.appendTo("#widget").modal("show").on("hidden.bs.modal", e => $modal.remove());

				if (application["reportTemplateDownloadItemLimit"] === undefined)
					application["reportTemplateDownloadItemLimit"] = 10;

			} else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

function reloadReportTemplateTable(customerId, $modal) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: loadTargetContext() + "/Customer/" + customerId + "/Template/Manage",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $table = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_customer_template table.table");
			if ($table.length) {
				$("#section_manage_customer_template table.table", $modal).replaceWith($table);
				updateMenu(undefined, '#section_manage_customer_template', '#menu_manage_customer_template');
				$("a[role='back']", $modal).trigger("click");
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function saveReportTemplate(e) {
	let $modal = $(e.currentTarget).closest(".modal"), $form = $("#reportTemplate-form", $modal), $progress = $("#loading-indicator").show(), customerId = $("input[name='customer']", $form).val();
	$("label.label-danger", $modal).remove();
	$.ajax({
		url: loadTargetContext() + "/Customer/" + customerId + "/Template/Save",
		type: 'POST',
		data: new FormData($form[0]),
		cache: false,
		contentType: false,
		processData: false,
		success: function (response, textStatus, jqXHR) {
			if (response.success) {
				showDialog("success", response.success);
				reloadReportTemplateTable(customerId, $modal);
			}
			else if (response.error)
				showDialog("#alert-dialog", response.error);
			else if (typeof response == 'object') {

				for (let field in response) {
					let message = response[field];

					if (field === "customer")
						showDialog("#alert-dialog", message);
					else
						$("<label class='label label-danger'/>").text(message).appendTo($("div[data-trick-info='" + field + "']", $modal));
				}

			} else
				showDialog("#alert-dialog", MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
		},
		error: unknowError

	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function isDefaultCustomTemplate() {
	return $("#section_manage_customer_template tbody>tr[data-trick-editable='false'] :checked").length
}

function checkItemCount() {
	return $("#section_manage_customer_template tbody>tr :checked").length < application["reportTemplateDownloadItemLimit"];
}

function editReportTemplate(e) {
	let $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	let $modal = $current.closest(".modal"), $form = $("#reportTemplate-form", $modal);
	let $tr = $("#section_manage_customer_template tbody>tr[data-trick-editable='true'] input:checked").closest("tr");
	if (!$tr.length)
		return false;

	let analysisType = $("td[data-trick-field='analysisType']", $tr).attr("data-trick-real-value");
	let templateType = $("td[data-trick-field='type']", $tr).attr("data-trick-real-value");
	let idLanguage = $("td[data-trick-field='language']", $tr).attr("data-trick-real-value");
	let isProfile = $modal.attr("data-trick-is-profile") === "true";

	$("select[name='language']", $form).val(idLanguage);
	$("select[name='type']", $form).val(templateType).removeAttr("required").prop("disabled", true);
	$("input[name='id']", $form).val($tr.attr("data-trick-id"));

	if (isProfile) {
		$("input[type='file']", $form).attr("required", true).trigger("reset").trigger("change");
		$("label[data-trick-real-value].btn", $form).removeClass("active").filter("[data-trick-real-value='" + analysisType + "']").addClass("active");
	}
	else {
		$("input[type='file']", $form).removeAttr("required").trigger("reset").trigger("change");
		$("input[name='analysisType'][value='" + analysisType + "']", $form).closest(".btn").trigger("click");
	}

	$("input[name='version']", $form).val($("td[data-trick-field='version']", $tr).text());
	$("textarea[name='label']", $form).val($("td[data-trick-field='label']", $tr).text());

	return true;
}

function addReportTemplate(e) {
	let $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	let $form = $("#reportTemplate-form", $current.closest(".modal")).trigger("reset");

	$("input[name='type'][value='HYBRID']", $form).attr("required", "required").prop("disabled", false).closest(".btn").trigger("click");
	$("input[type='file']", $form).attr("required", "required").trigger("change");
	$("select>option:disabled", $form).prop("selected", true).parent().prop("disabled", false).attr("required", "required").trigger("change");
	$("input[name='id']", $form).val("0");
}
function deleteReportTemplate(e) {
	let $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	let $modal = $current.closest(".modal"), selections = findSelectItemIdBySection("section_manage_customer_template", $modal).filter((i) => parseInt(i) > 0);
	if (!selections.length)
		return false;
	let customerId = $("input[name='customer']", $modal).val(), $confirmModal = showDialog("#confirm-dialog", MessageResolver("confirm.delete.report.template", "Are you sure, you want to delete selected template?", selections.length));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function (e) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: loadTargetContext() + "/Customer/" + customerId + "/Template",
			data: JSON.stringify(selections),
			type: "delete",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.success) {
					showDialog("success", response.success);
					for (let id of selections)
						$("#section_manage_customer_template tbody>tr[data-trick-id='" + id + "']", $modal).remove();
					updateMenu(undefined, '#section_manage_customer_template', '#menu_manage_customer_template');
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

function downloadReportTemplate(e) {
	let $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	let $modal = $current.closest(".modal"), selections = findSelectItemIdBySection("section_manage_customer_template", $modal).filter((i) => parseInt(i) > 0);
	if (!selections.length)
		return false;
	if (selections.length > application["reportTemplateDownloadItemLimit"])
		showDialog("error", MessageResolver("error.too.many.item.selected", undefined, application["reportTemplateDownloadItemLimit"]))

	for (let i = 0; i < selections.length; i++) {
		let id = selections[i];
		if (!id) {
			download(loadTargetContext() + "/Customer/Template/" + id + "/Download");
		} else {
			setTimeout(() => {
				download(loadTargetContext() + "/Customer/Template/" + id + "/Download");
			}, (i + 1) * 500);
		}
	}
	return false;
}