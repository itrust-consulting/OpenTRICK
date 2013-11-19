function saveCustomer(form) {
	result="";
	return $.ajax({
		url : context + "/KnowledgeBase/Customer/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document
					.getElementById(form), data);
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