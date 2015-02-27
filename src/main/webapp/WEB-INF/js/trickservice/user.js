function saveUser(form) {
	result = "";
	$.ajax({
		url : context + "/Admin/User/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response,textStatus,jqXHR) {
			$("#success").attr("hidden", "hidden");
			$("#success div").remove();

			var alert = $("#" + form + " .label-danger");
			if (alert.length)
				alert.remove();

			for ( var error in response) {

				$("#success").attr("hidden", "hidden");
				$("#success div").remove();

				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);
				switch (error) {
				case "login":
					$(errorElement).appendTo($("#" + form + " #user_login").parent());
					break;
				case "password":
					$(errorElement).appendTo($("#" + form + " #user_password").parent());
					break;
				case "firstName":
					$(errorElement).appendTo($("#" + form + " #user_firstName").parent());
					break;
				case "lastName":
					$(errorElement).appendTo($("#" + form + " #user_lastName").parent());
					break;
				case "email":
					$(errorElement).appendTo($("#" + form + " #user_email").parent());
					break;
				case "user": {
					var errElement = document.createElement("div");
					errElement.setAttribute("class", "alert alert-danger");
					$(errElement).text($(errorElement).text());
					$(errElement).appendTo($("#success"));
					$("#success").removeAttr("hidden");
					$("#user_password").prop("value", "");
				}
				}
			}

			if (!$("#" + form + " .label-danger").length) {
				var successElement = document.createElement("div");
				successElement.setAttribute("class", "alert alert-success");
				$(successElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + MessageResolver("label.user.update.success", "Profile successfully updated"));
				$(successElement).appendTo($("#addUserModel .modal-body #success"));
				$("#success").removeAttr("hidden");
				$("#user_password").prop("value", "");
				setTimeout(reloadSection("section_user"), 2000);
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			var alert = $("#addUserModel .label-danger");
			if (alert.length)
				alert.remove();
			var errorElement = document.createElement("div");
			errorElement.setAttribute("class", "alert alert-danger");
			$(errorElement).text(
					"<button type='button' class='close' data-dismiss='alert'>&times;</button>" + MessageResolver("error.unknown.add.user", "An unknown error occurred during adding/updating users"));
			$(errorElement).appendTo($("#addUserModel .modal-body #success"));
			$("#user_password").prop("value", "");
		},
	});

	return false;
}

function deleteUser(userId, name) {
	if (userId == null || userId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_user"));
		if (selectedScenario.length != 1)
			return false;
		userId = selectedScenario[0];
		name = $("#section_user tbody tr[data-trick-id='" + userId + "'] td:nth-child(2)").text();
	}
	$("#deleteUserBody").html(MessageResolver("label.user.question.delete", "Are you sure that you want to delete the user") + "&nbsp;<strong>" + name + "</strong>?");
	$("#deleteuserbuttonYes").click(function() {
		$.ajax({
			url : context + "/Admin/User/Delete/" + userId,
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			success : function(response,textStatus,jqXHR) {

				if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else {
					reloadSection("section_user");
				}
				return false;
			},
			error : unknowError
		});
		$("#deleteUserModel").modal('hide');
		return false;
	});
	$("#deleteUserModel").modal('show');
	return false;
}

function newUser() {
	if (findSelectItemIdBySection(("section_user")).length > 0)
		return false;
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
		success : function(response,textStatus,jqXHR) {
			$("#rolescontainer").html(response);
		},
		error : unknowError
	});

	$("#addUserModel-title").text(MessageResolver("title.administration.user.add", "Add a new User"));
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
	var rows = $("#section_user").find("tr[data-trick-id='" + userId + "'] td:not(:first-child)");
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
		success : function(response,textStatus,jqXHR) {
			$("#rolescontainer").html(response);
		},
		error : unknowError
	});

	$("#addUserModel-title").text(MessageResolver("title.user.update", "Update a User"));
	$("#addUserbutton").text(MessageResolver("label.action.edit", "Edit"));
	$("#user_form").prop("action", "/Save");
	$("#addUserModel").modal('toggle');
	return false;
}
