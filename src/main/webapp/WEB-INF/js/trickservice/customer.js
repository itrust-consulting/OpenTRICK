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

function saveCustomer(form) {
	var $progress = $("#loading-indicator").show();
	$("#addCustomerModel .label-danger").remove();
	$.ajax({
		url: context + "/KnowledgeBase/Customer/Save",
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
				reloadSection("section_customer");
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
			url: context + "/KnowledgeBase/Customer/Delete/" + customerId,
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
					reloadSection("section_customer");
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
	if (findSelectItemIdBySection("section_customer").length)
		return false;
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
	$("#customer_form").prop("action", "Customer/Create");
	$("#addCustomerModel").modal('show');
	return false;
}

function editSingleCustomer(customerId) {
	if (customerId == null || customerId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_customer"));
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
	$("#customer_form").prop("action", "Customer/Edit/" + customerId);
	$("#addCustomerModel").modal('show');
	return false;
}

function manageCustomerAccess(customerID) {
	if (!isNotCustomerProfile())
		return false;
	if (customerID == null || customerID == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerID = selectedScenario[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/KnowledgeBase/Customer/" + customerID + "/Manage-access",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $view = $(new DOMParser().parseFromString(response, "text/html")).find("#manageCustomerUserModel");
			if ($view.length) {
				$view.appendTo("#widget").modal("show").on("hidden.bs.modal", () => $view.remove());
				$("button[name='save']", $view).on("click" , e => updateCustomerAccess(e,$view,$progress,customerID));
			} else
				unknowError();
			return false;
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

function isNotCustomerProfile() {
	return $("#section_customer tbody>tr>td>input:checked").parent().parent().attr("data-trick-is-profile") === "false";
}

function updateCustomerAccess(e,$view,$progress,customerID) {
	var data = {};
	$view.find(".form-group[data-trick-id][data-default-value]").each(function () {
		var $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
		if (newRight != oldRight)
			data[$this.attr("data-trick-id")] = newRight;
	});
	if (Object.keys(data).length) {
		$progress.show();
		$.ajax({
			url: context + "/KnowledgeBase/Customer/" + customerID + "/Manage-access/Update",
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