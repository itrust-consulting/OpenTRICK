function initUserCustomerList() {
	$('#usercustomer').hide().after("<ul class='list-group' style='max-height: 300px; padding: 5px;margin:0; overflow: auto;'></ul>");
	$('#usercustomer option').each(function() {
		var selected = "";

		var attr = $(this).attr('selected');

		if (typeof attr !== 'undefined' && attr !== false) {
			selected = " active";
		}
		$('#customerusersform .list-group').append("<li class='list-group-item" + selected + "' data-trick-opt='" + $(this).attr('value') + "'>" + $(this).html() + "</li>");
	});
	$('#customerusersform .list-group li').on('click', function() {
		$(this).toggleClass('active');
		var allVal = new Array();
		$('#customerusersform .list-group li.active').each(function() {
			allVal.push($(this).attr("data-trick-opt"));
		});
		$('#usercustomer').val(allVal);
	});
}

function saveCustomer(form) {
	$("#addCustomerModel #addcustomerbutton").prop("disabled", true);
	$.ajax({
		url : context + "/KnowledgeBase/Customer/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
			var alert = $("#addCustomerModel .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
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
				case "customer":
					$(errorElement).appendTo($("#addCustomerModel .modal-body"));
					break;
				}
			}
			if (!$("#addCustomerModel .label-danger").length) {
				$("#addCustomerModel").modal("toggle");
				reloadSection("section_customer");
			}
			return false;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			var alert = $("#addCustomerModel .label-danger");
			if (alert.length)
				alert.remove();
			$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
			var errorElement = document.createElement("label");
			errorElement.setAttribute("class", "label label-danger");
			$(errorElement).text(MessageResolver("error.unknown.add.customer", "An unknown error occurred during adding customer"));
			$(errorElement).appendTo($("#addCustomerModel .modal-body"));
			return false;
		},
	});
	return false;
}

function deleteCustomer(customerId, organisation) {
	if (customerId == null || customerId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_customer"));
		if (selectedScenario.length != 1)
			return false;
		customerId = selectedScenario[0];
		organisation = $("#section_customer tbody tr[data-trick-id='" + customerId + "']>td:nth-child(2)").text();
	}
	$("#deleteCustomerBody").html(
			MessageResolver("label.customer.question.delete", "Are you sure that you want to delete the customer <strong>" + organisation + "</strong>?", organisation));
	$("#deletecustomerbuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Customer/Delete/" + customerId,
			type : "POST",
			async : false,
			contentType : "application/json",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] == undefined) {
					if (response["error"] == undefined)
						unknowError();
					else {
						$("#alert-dialog .modal-body").html(response["error"]);
						$("#alert-dialog").modal("show");
					}
				} else
					reloadSection("section_customer");
				return false;
			},
			error : unknowError
		}).complete(function(){
			$("#deleteCustomerModel").modal('hide');
			$("#deletecustomerbuttonYes").unbind();
		});
		return false;
	});
	$("#deleteCustomerModel").modal('show');
	return false;
}

function newCustomer() {
	if (findSelectItemIdBySection("section_customer").length)
		return false;
	var alert = $("#addCustomerModel .label-danger");
	if (alert.length)
		alert.remove();
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
	$("#addcustomerbutton").text(MessageResolver("label.action.add", "Add"));
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
	var alert = $("#addCustomerModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
	var rows = $("#section_customer").find("tr[data-trick-id='" + customerId + "'] td[data-trick-name]").each(function() {
		if ($(this).attr("data-real-value") == undefined)
			$("#customer_" + $(this).attr("data-trick-name")).prop("value", $(this).text());
		else
			$("#customer_" + $(this).attr("data-trick-name")).prop("checked", $(this).attr("data-real-value") == 'false');
	});
	$("#customer_id").prop("value", customerId);
	$("#addCustomerModel-title").text(MessageResolver("title.knowledgebase.Customer.Update", "Update a Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#customer_form").prop("action", "Customer/Edit/" + customerId);
	$("#addCustomerModel").modal('show');
	return false;
}

function manageUsers(customerID) {
	if (!isNotCustomerProfile())
		return false;
	if (customerID == null || customerID == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerID = selectedScenario[0];
	}

	$.ajax({
		url : context + "/KnowledgeBase/Customer/" + customerID + "/Users",
		type : "get",
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#customerusersform");
			if ($content.length) {
				var $customer = $("#customerusersbody").html(response);
				initUserCustomerList();
				$("#customerusersform").prop("action", "Customer/" + customerID + "/Users/Update");
				$("#customerusersbutton").attr("onclick", "updateManageUsers(" + customerID + ",'#customerusersform')");
				$("#manageCustomerUserModel").modal('toggle');
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});

	return false;
}

function isNotCustomerProfile() {
	var $selectedCustomer = $("#section_customer tbody>tr>td>input:checked");
	return $selectedCustomer.parent().parent().attr("data-trick-is-profile") === "false";
}

function updateManageUsers(customerID, form) {

	var data = {};

	$(form).find("select[name='usercustomer'] option").each(function() {

		var name = $(this).attr("value");

		var value = $(this).is(":checked");

		data[name] = value;

	});

	var jsonarray = JSON.stringify(data);

	$.ajax({
		url : context + "/KnowledgeBase/Customer/" + customerID + "/Users/Update",
		type : "post",
		data : jsonarray,
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#customerusers");
			if ($content.length) {
				$("#customerusersbody").html(response);
				initUserCustomerList();
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});
	return false;
}