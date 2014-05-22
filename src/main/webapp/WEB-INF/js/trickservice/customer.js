function initUserCustomerList() {
	$('#usercustomer').hide().after("<ul class='list-group'></ul>");
	$('#usercustomer option').each(function() {
		var selected = "";

		var attr = $(this).attr('selected');

		if (typeof attr !== 'undefined' && attr !== false) {
			selected = " active";
		}
		$('.list-group').append("<li class='list-group-item" + selected + "' opt='" + $(this).attr('value') + "'>" + $(this).html() + "</li>");
	});
	$('.list-group li').on('click', function() {
		$(this).toggleClass('active');
		var allVal = new Array();
		$('.list-group li.active').each(function() {
			allVal.push($(this).attr('opt'));
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
		success : function(response) {
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
		organisation = $("#section_customer tbody tr[trick-id='" + customerId + "']>td:nth-child(2)").text();
	}
	$("#deleteCustomerBody").html(MessageResolver("label.customer.question.delete", "Are you sure that you want to delete the customer") + "&nbsp;<strong>" + organisation + "</strong>?");
	$("#deletecustomerbuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Customer/Delete/" + customerId,
			type : "POST",
			contentType : "application/json",
			success : function(response) {
				if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				}				
				reloadSection("section_customer");
				return false;
			}
		});
		$("#deleteCustomerModel").modal('toggle');
		$("#deletecustomerbuttonYes").unbind();
		return false;
	});
	$("#deleteCustomerModel").modal('toggle');
	return false;
}

function newCustomer() {
	var alert = $("#addCustomerModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#addCustomerModel #addcustomerbutton").prop("disabled", false);
	$("#customer_id").prop("value", "-1");
	$("#customer_organisation").prop("value", "");
	$("#customer_contactPerson").prop("value", "");
	$("#customer_telephoneNumber").prop("value", "");
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
	$("#addCustomerModel").modal('toggle');
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
	var rows = $("#section_customer").find("tr[trick-id='" + customerId + "'] td:not(:first-child)");
	$("#customer_id").prop("value", customerId);
	$("#customer_organisation").prop("value", $(rows[0]).text());
	$("#customer_contactPerson").prop("value", $(rows[1]).text());
	$("#customer_phoneNumber").prop("value", $(rows[2]).text());
	$("#customer_email").prop("value", $(rows[3]).text());
	$("#customer_address").prop("value", $(rows[4]).text());
	$("#customer_city").prop("value", $(rows[5]).text());
	$("#customer_ZIPCode").prop("value", $(rows[6]).text());
	$("#customer_country").prop("value", $(rows[7]).text());
	if ($("#customer_canBeUsed").length)
		$("#customer_canBeUsed").prop("checked", $(rows[8]).attr("trick-real-value") == "false");
	$("#addCustomerModel-title").text(MessageResolver("title.knowledgebase.Customer.Update", "Update a Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#customer_form").prop("action", "Customer/Edit/" + customerId);
	$("#addCustomerModel").modal('toggle');
	return false;
}

function manageUsers(customerID) {

	if (customerID == null || customerID == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_customer"));
		if (selectedScenario.length != 1)
			return false;
		customerID = selectedScenario[0];
	}

	$.ajax({
		url : context + "/KnowledgeBase/Customer/" + customerID + "/Users",
		type : "get",
		contentType : "application/json",
		success : function(response) {
			$("#customerusersbody").html(response);
			initUserCustomerList();
			$("#customerusersform").prop("action", "Customer/" + customerID + "/Users/Update");
			$("#customerusersbutton").attr("onclick", "updateManageUsers(" + customerID + ",'#customerusersform')");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	$("#manageCustomerUserModel").modal('toggle');
	return false;
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
		success : function(response) {
			$("#customerusersbody").html(response);
			initUserCustomerList();
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
	return false;
}