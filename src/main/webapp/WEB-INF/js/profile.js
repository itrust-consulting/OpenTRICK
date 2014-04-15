function updateProfile(form) {
	result = "";
	return $.ajax({
		url : context + "/Profile/Update",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				// reload section
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}