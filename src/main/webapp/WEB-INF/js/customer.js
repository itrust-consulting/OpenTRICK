function saveCustomer(form) {
	result = "";
	return $.ajax({
					url : context + "/KnowledgeBase/Customer/Save",
					type : "post",
					data : serializeForm(form),
					contentType : "application/json",
					success : function(response) {
									var data = "";
									for ( var error in response)
										data += response[error][1] + "\n";
									result = data == "" ? true : showError(document.getElementById(form), data);
									if (result) {
										$("#addCustomerModel").modal("hide");
										reloadSection("section_customer");
									}
									return result;

					},
				    error : function(jqXHR, textStatus, errorThrown) {
				    	return result;
				    },
	});
}

function deleteACustomer(customerId) {
	$.ajax({
		url : context + "/KnowledgeBase/Customer/Delete/" + customerId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			reloadSection("section_customer");
			return false;
		}
	});
	return false;
}

function deleteCustomer(customerId, organisation) {
	$("#deleteCustomerBody").html(
			MessageResolver("label.customer.question.delete",
					"Are you sure that you want to delete the customer")
					+ "&nbsp;<strong>" + organisation + "</strong>?");
	$("#deletecustomerbuttonYes").attr("onclick",
			"deleteACustomer(" + customerId + ")");
	$("#deleteCustomerModel").modal('toggle');
}

function newCustomer() {
	$("#customer_id").prop("value", "-1");
	$("#customer_organisation").prop("value", "");
	$("#customer_contactPerson").prop("value", "");
	$("#customer_telephoneNumber").prop("value", "");
	$("#customer_email").prop("value", "");
	$("#customer_address").prop("value", "");
	$("#customer_city").prop("value", "");
	$("#customer_ZIPCode").prop("value", "");
	$("#customer_country").prop("value", "");
	$("#addCustomerModel-title").text(
			MessageResolver("title.knowledgebase.Customer.Add",
					"Add a new Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.add", "Add"));
	$("#customer_form").prop("action", "Customer/Create");
	$("#addCustomerModel").modal('toggle');
}

function editSingleCustomer(customerId) {
	var rows = $("#section_customer").find(
			"tr[trick-id='" + customerId + "'] td");
	$("#customer_id").prop("value", customerId);
	$("#customer_organisation").prop("value", $(rows[0]).text());
	$("#customer_contactPerson").prop("value", $(rows[1]).text());
	$("#customer_telephoneNumber").prop("value", $(rows[2]).text());
	$("#customer_email").prop("value", $(rows[3]).text());
	$("#customer_address").prop("value", $(rows[4]).text());
	$("#customer_city").prop("value", $(rows[5]).text());
	$("#customer_ZIPCode").prop("value", $(rows[6]).text());
	$("#customer_country").prop("value", $(rows[7]).text());
	$("#addCustomerModel-title").text(
			MessageResolver("title.knowledgebase.Customer.Update",
					"Update a Customer"));
	$("#addcustomerbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#customer_form").prop("action", "Customer/Edit/" + customerId);
	$("#addCustomerModel").modal('toggle');
}