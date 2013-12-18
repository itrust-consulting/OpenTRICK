function saveNorm(form) {
	result = "";
	return $.ajax({
		url : context + "/KnowledgeBase/Norm/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addNormModel").modal("hide");
				reloadSection("section_norm");
			}
			return result;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function deleteANorm(normId) {
	$.ajax({
		url : context + "/KnowledgeBase/Norm/Delete/" + normId,
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			reloadSection("section_norm");
			return false;
		}
	});
	return false;
}

function deleteNorm(normId, name) {
	$("#deleteNormBody").html(MessageResolver("label.norm.question.delete", "Are you sure that you want to delete the norm") + "&nbsp;<strong>" + name + "</strong>?");
	$("#deletenormbuttonYes").attr("onclick", "deleteANorm(" + normId + ")");
	$("#deleteNormModel").modal('toggle');
	return false;
}

function newUser() {
	$("#user_id").prop("value", "-1");
	$("#user_login").prop("value", "");
	$("#user_password").prop("value", "");
	$("#user_firstName").prop("value", "");
	$("#user_lastName").prop("value", "");
	$("#user_email").prop("value", "");
	$("#addUserModel-title").text(MessageResolver("title.Administration.User.Add", "Add a new User"));
	$("#adduserbutton").text(MessageResolver("label.action.add", "Add"));
	$("#user_form").prop("action", "/Save");
	$("#addUserModel").modal('toggle');
	return false;
}

function editSingleUser(userId) {
	var rows = $("#section_user").find("tr[trick-id='" + userId + "'] td");
	$("#user_id").prop("value", userId);
	$("#user_login").prop("value", $(rows[0]).text());
	$("#user_password").prop("value", "");
	$("#user_firstName").prop("value", $(rows[0]).text());
	$("#user_lastName").prop("value", $(rows[0]).text());
	$("#user_email").prop("value", $(rows[0]).text());
	$("#addUserModel-title").text(MessageResolver("title.knowledgebase.Norm.Update", "Update a Norm"));
	$("#adduserbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#user_form").prop("action", "/Save");
	$("#addUserModel").modal('toggle');
	return false;
}
