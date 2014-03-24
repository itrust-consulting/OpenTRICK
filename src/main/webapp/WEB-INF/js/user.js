function saveUser(form) {
	result = "";
	return $
			.ajax({
				url : context + "/Admin/User/Save",
				type : "post",
				data : serializeForm(form),
				contentType : "application/json;charset=UTF-8",
				success : function(response) {
					var data = "";
					for ( var error in response)
						data += response[error][1] + "\n";
					result = data == "" ? true : showError(document
							.getElementById(form), data);
					if (result) {
						$("#addUserModel").modal("hide");
						reloadSection("section_user");
						str = '<div class="alert alert-success" aria-hidden="true">';
						str = str
								+ '<a class="close" href="#" data-dismiss="alert">x</a>';
						str = str
								+ MessageResolver("success.user.add",
										"User Added/Saved: ")
								+ "&nbsp;<strong>"
								+ $("#user_login").prop("value")
								+ "</strong>!</div>";
						$("#messages").html(str);
					}
					return result;

				},
				error : function(jqXHR, textStatus, errorThrown) {
					return result;
				},
			});
}

function deleteUser(userId, name) {
	if (userId == null || userId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_user"));
		if (selectedScenario.length != 1)
			return false;
		userId = selectedScenario[0];
		name = $("#section_user tbody tr[trick-id='"+userId+"'] td:nth-child(3)").text();
	}
	$("#deleteUserBody").html(
			MessageResolver("label.user.question.delete",
					"Are you sure that you want to delete the user")
					+ "&nbsp;<strong>" + name + "</strong>?");
	$("#deleteuserbuttonYes")
			.click(
					function() {
						$
								.ajax({
									url : context + "/Admin/User/Delete/"
											+ userId,
									type : "POST",
									contentType : "application/json;charset=UTF-8",
									success : function(response) {
										if (response == false) {
											str = '<div class="alert alert-error" aria-hidden="true">';
											str = str
													+ '<a class="close" href="#" data-dismiss="alert">x</a>';
											str = str
													+ MessageResolver(
															"error.user.delete.failed",
															"Could not delete the user")
													+ "&nbsp;<strong>" + name
													+ "</strong>!</div>";
											$("#user_messages").html(str);
										} else {
											reloadSection("section_user");
											str = '<div class="alert alert-success" aria-hidden="true">';
											str = str
													+ '<a class="close" href="#" data-dismiss="alert">x</a>';
											str = str
													+ MessageResolver(
															"success.user.delete.done",
															"Deleted the user")
													+ "&nbsp;<strong>" + name
													+ "</strong>!</div>";
											$("#messages").html(str);
										}
										return false;
									}
								});
						$("#deleteUserModel").modal('toggle');
						return false;
					});
	$("#deleteUserModel").modal('toggle');
	return false;
}

function newUser() {
	$("#user_id").prop("value", "-1");
	$("#user_login").prop("value", "");
	$("#user_login").removeAttr("disabled");
	$("#user_password").prop("value", "");
	$("#user_firstName").prop("value", "");
	$("#user_lastName").prop("value", "");
	$("#user_email").prop("value", "");

	$.ajax({
		url : context + "/Admin/Roles",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#rolescontainer").html(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});

	$("#addUserModel-title").text(
			MessageResolver("title.Administration.User.Add", "Add a new User"));
	$("#addUserbutton").text(MessageResolver("label.action.add", "Add"));
	$("#user_form").prop("action", "/Save");
	$("#addUserModel").modal('toggle');
	return false;
}

function editSingleUser(userId) {
	if (userId == null || userId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_user"));
		if (selectedScenario.length != 1)
			return false;
		userId = selectedScenario[0];
	}
	var rows = $("#section_user").find("tr[trick-id='" + userId + "'] td:not(:first-child)");
	$("#user_id").prop("value", userId);
	$("#user_login").prop("value", $(rows[0]).text());
	$("#user_login").prop("disabled", "disabled");
	$("#user_password").prop("value", "");
	$("#user_firstName").prop("value", $(rows[1]).text());
	$("#user_lastName").prop("value", $(rows[2]).text());
	$("#user_email").prop("value", $(rows[3]).text());

	$.ajax({
		url : context + "/Admin/User/Roles/" + userId,
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#rolescontainer").html(response);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});

	$("#addUserModel-title").text(
			MessageResolver("title.user.Update", "Update a User"));
	$("#addUserbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#user_form").prop("action", "/Save");
	$("#addUserModel").modal('toggle');
	return false;
}
