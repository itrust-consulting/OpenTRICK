function initUserCustomerList() {
	$('#usercustomer').hide().after("<ul class='list-group' style='max-height: 300px; padding: 5px;margin:0; overflow: auto;'></ul>");
	$('#usercustomer option').each(function () {
		var selected = "";

		var attr = $(this).attr('selected');

		if (typeof attr !== 'undefined' && attr !== false) {
			selected = " active";
		}
		$('#customerusersform .list-group').append("<li class='list-group-item" + selected + "' data-trick-opt='" + $(this).attr('value') + "'>" + $(this).html() + "</li>");
	});
	$('#customerusersform .list-group li').on('click', function () {
		$(this).toggleClass('active');
		var allVal = new Array();
		$('#customerusersform .list-group li.active').each(function () {
			allVal.push($(this).attr("data-trick-opt"));
		});
		$('#usercustomer').val(allVal);
	});
}

function loadTargetContext() {
	return context + (application['isAdministration'] ? "/Admin" : "/KnowledgeBase");
}

function getCustomerSection() {
	return application['isAdministration'] ? "section_admin_customer" : "section_customer";
}

function saveCustomer(form) {
	var $progress = $("#loading-indicator").show();
	$("#addCustomerModel .label-danger").remove();
	$.ajax({
		url: loadTargetContext() + "/Customer/Save",
		type: "post",
		data: serializeForm(form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var hasError = false;
			for (var error in response) {
				var errorElement = document.createElement("label");
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
					case "canBeUsed":
						$(errorElement).appendTo($("#customer_form #customer_canBeUsed").parent());
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
		var selectedScenario = findSelectItemIdBySection(("section_customer"));
		if (selectedScenario.length != 1)
			return false;
		organisation = $("#section_customer tbody tr[data-trick-id='" + (customerId = selectedScenario[0]) + "']>td:nth-child(2)").text();
	}
	$("#deleteCustomerBody").html(
		MessageResolver("label.customer.question.delete", "Are you sure that you want to delete the customer <strong>" + organisation + "</strong>?", organisation));
	$("#deletecustomerbuttonYes").unbind().click(function () {
		$("#deleteCustomerModel").modal('hide');
		var $progress = $("#loading-indicator").show();
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
	$("#customer_id").prop("value", "-1");
	$("#customer_organisation").prop("value", "");
	$("#customer_contactPerson").prop("value", "");
	$("#customer_phoneNumber").prop("value", "");
	$("#customer_email").prop("value", "");
	$("#customer_address").prop("value", "");
	$("#customer_city").prop("value", "");
	$("#customer_ZIPCode").prop("value", "");
	$("#customer_country").prop("value", "");
	if ($("#customer_canBeUsed").length)
		$("#customer_canBeUsed").prop("checked", false);
	$("#addCustomerModel-title").text(MessageResolver("title.knowledgebase.Customer.Add", "Add a new Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.save", "Save"));
	$("#customer_form").prop("action", loadTargetContext() + "/Customer/Save");
	$("#addCustomerModel").modal('show');
	return false;
}

function editSingleCustomer(customerId) {
	if (customerId == null || customerId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerId = selectedScenario[0];
	}
	$("#addCustomerModel .label-danger").remove();
	$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
	var rows = $("#section_customer").find("tr[data-trick-id='" + customerId + "'] td[data-trick-name]").each(function () {
		if ($(this).attr("data-real-value") == undefined)
			$("#customer_" + $(this).attr("data-trick-name")).prop("value", $(this).text());
		else
			$("#customer_" + $(this).attr("data-trick-name")).prop("checked", $(this).attr("data-real-value") == 'false');
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
		var selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerId = selectedScenario[0];
	}

	if (customerId == null || customerId == undefined)
		return false;

	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: loadTargetContext() + "/Customer/" + customerId + "/Report-template/Manage",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $modal = $("#reportTemplateModal", new DOMParser().parseFromString(response, "text/html"));
			if ($modal.length) {

				var $tabs = $modal.find("#menu_manage_customer_template a[data-toggle='tab']"), $cancelBtn = $modal.find(".modal-footer button[name='cancel']"), $backBtn = $modal
					.find(".modal-footer a[role='back'].btn"), $saveBtn = $modal.find(".modal-footer button[name='save']"), $btnSubmit = $("button[name='submit']", $modal);

				var $file = $("input[type='file']", $modal), $fileInfo = $("input[name='filename']", $modal), $browse = $("button[name='browse']", $modal);

				$browse.on("click", (e) => $file.trigger("click"));

				$file.on("change", (e) => {
					var value = $file.val();
					if (value.trim() === '')
						$saveBtn.prop("disabled", $file.is(":required"));
					else {
						var size = parseInt($file.attr("maxlength"))
						if ($file[0].files[0].size > size) {
							showDialog("error", MessageResolver("error.file.too.large", undefined, size));
							return false;
						} else if (!checkExtention(value, ".docx", $saveBtn))
							return false;
					}

					$fileInfo.val(value);
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
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: loadTargetContext() + "/Customer/" + customerId + "/Report-template/Manage",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $table = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_customer_template table.table");
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
	var $modal = $(e.currentTarget).closest(".modal"), $form = $("#reportTemplate-form", $modal), $progress = $("#loading-indicator").show(), customerId = $("input[name='customer']", $form).val();
	$("label.label-danger",$modal).remove();
	$.ajax({
		url: loadTargetContext() + "/Customer/" + customerId + "/Report-template/Save",
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

				for (var field in response) {
					var message = response[field];

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
	var $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	var $modal = $current.closest(".modal"), $form = $("#reportTemplate-form",$modal);
	var $tr = $("#section_manage_customer_template tbody>tr[data-trick-editable='true'] input:checked").closest("tr");
	if (!$tr.length)
		return false;
	var type = $("td[data-trick-field='type']", $tr).attr("data-trick-real-value"),
		idLanguage = $("td[data-trick-field='language']", $tr).attr("data-trick-real-value"), isProfile = $modal.attr("data-trick-is-profile")==="true";
	$("select[name='language']", $form).val(idLanguage);
	$("input[name='id']", $form).val($tr.attr("data-trick-id"));
	
	if(isProfile){
		$("input[type='file']", $form).attr("required",true).trigger("reset").trigger("change");
		$("label[data-trick-real-value].btn", $form).removeClass("active").filter("[data-trick-real-value='"+type+"']").addClass("active");
	}
	else {
		$("input[type='file']", $form).removeAttr("required").trigger("reset").trigger("change");
		$("input[name='type'][value='" + type + "']", $form).closest(".btn").trigger("click");
	}
	
	$("input[name='version']", $form).val($("td[data-trick-field='version']", $tr).text());
	$("textarea[name='label']", $form).val($("td[data-trick-field='label']", $tr).text());

	return true;
}

function addReportTemplate(e) {
	var $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	var $form = $("#reportTemplate-form", $current.closest(".modal")).trigger("reset");
	$("input[name='type'][value='QUANTITATIVE']", $form).closest(".btn").trigger("click");
	$("input[type='file']", $form).attr("required", "required").trigger("change");
	$("input[name='id']", $form).val("-1");
}
function deleteReportTemplate(e) {
	var $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	var $modal = $current.closest(".modal"), selections = findSelectItemIdBySection("section_manage_customer_template", $modal).filter((i) => parseInt(i) > 0);
	if (!selections.length)
		return false;
	var customerId = $("input[name='customer']", $modal).val(), $confirmModal = showDialog("#confirm-dialog", MessageResolver("confirm.delete.report.template", "Are you sure, you want to delete selected template?", selections.length));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function (e) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: loadTargetContext() + "/Customer/" + customerId + "/Report-template",
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
	var $current = $(e.currentTarget);
	if ($current.parent().hasClass("disabled"))
		return false;
	var $modal = $current.closest(".modal"), selections = findSelectItemIdBySection("section_manage_customer_template", $modal).filter((i) => parseInt(i) > 0);
	if (!selections.length)
		return false;
	if (selections.length > application["reportTemplateDownloadItemLimit"])
		showDialog("error", MessageResolver("error.too.many.item.selected", undefined, application["reportTemplateDownloadItemLimit"]))
	for (let id of selections)
		showStaticDialog("download", MessageResolver("info.download.exported.file"), MessageResolver("label.title.export.report.template"), loadTargetContext() + "/Customer/Report-template/" + id + "/Download");
	return false;
}