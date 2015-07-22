function saveUser(form) {
	result = "";
	var idUser = $("#" + form).find("#user_id");
	if (!idUser.length)
		idUser = -1;
	else
		idUser = parseInt(idUser.val());

	$.ajax({
		url : context + "/Admin/User/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
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
				$(successElement).html(
						"<button type='button' class='close' data-dismiss='alert'>&times;</button>"
								+ (idUser === -1 ? MessageResolver("success.user.created", "User was successfully created") : MessageResolver("success.user.update",
										"User was successfully updated")));
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
					"<button type='button' class='close' data-dismiss='alert'>&times;</button>"
							+ MessageResolver("error.unknown.add.user", "An unknown error occurred during adding/updating users"));
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
	$.ajax({
		url : context + "/Admin/User/" + userId + "/Prepare-to-delete",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else {
				var $deleteUserDialog = $(new DOMParser().parseFromString(response, "text/html")).find("#deleteUserModal");
				if ($deleteUserDialog.length) {
					if ($("#deleteUserModal").length)
						$("#deleteUserModal").replaceWith($deleteUserDialog);
					else
						$deleteUserDialog.appendTo("#dialog-body");
					var $form = $deleteUserDialog.find("form");
					var $progress = $deleteUserDialog.find(".progress");
					var $submitInput = $deleteUserDialog.find("input[type='submit']");
					var $deleteButton = $deleteUserDialog.find("button[name='delete']").click(function() {
						$submitInput.click();
					});
					
					$progress.hide();

					function fadeOutComplete() {
						return $(this).remove();
					}

					$form.on("submit", function() {

						$deleteUserDialog.find(".label").remove();

						$deleteButton.prop('disabled', true);
						var data = {
							idUser : $form.find("input[name='idUser']").val(),
							switchOwners : {},
							deleteAnalysis : []
						}
						$form.find("select").each(function() {
							var $this = $(this), value = $this.val(), name = $this.attr("name");
							if (value < 1)
								data.deleteAnalysis.push(name);
							else
								data.switchOwners[name] = value;
						});
						
						$progress.show();

						$.ajax({
							url : context + "/Admin/User/Delete",
							type : "post",
							contentType : "application/json;charset=UTF-8",
							data : JSON.stringify(data),
							success : function(response, textStatus, jqXHR) {
								var result = parseJson(response);
								if (result == undefined)
									$("<label class='label label-danger'></label>").appendTo($("#deleteUserErrors")).text(
											MessageResolver("error.unknown.occurred", "An unknown error occurred")).fadeOut(15000, fadeOutComplete);
								if (result.success != undefined) {
									$deleteUserDialog.modal("hide");
									reloadSection([ 'section_user', 'section_admin_analysis' ]);
								} else if (result.error != undefined)
									$("<label class='label label-danger'></label>").appendTo($("#deleteUserErrors")).text(result.error).fadeOut(15000, fadeOutComplete);
								else {
									for ( var key in result) {
										var $select = $deleteUserDialog.find("select[name=" + key + "]");
										if ($select.length)
											$("<label class='label label-danger'></label>").appendTo($select.parent()).text(result[key]);
										else
											$("<label class='label label-danger'></label>").appendTo($("#deleteUserErrors")).text(result.error);
									}
								}
							},
							error : function(jqXHR, textStatus, errorThrown) {
								$("<label class='label label-danger'></label>").appendTo($("#deleteUserErrors")).text(
										MessageResolver("error.unknown.occurred", "An unknown error occurred")).fadeOut(15000, fadeOutComplete);
							},
							complete : function() {
								$deleteButton.prop('disabled', false);
								$progress.hide();
							}
						});
						return false;
					});

					new Modal($deleteUserDialog).Show();
				} else
					unknowError();
			}
			return false;
		},
		error : unknowError
	});
	return false;
}

function newUser() {
	if (findSelectItemIdBySection(("section_user")).length > 0)
		return false;
	$("#addUserModel .alert,.label-danger").remove()
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
		success : function(response, textStatus, jqXHR) {
			$("#rolescontainer").html(response);
			$("#addUserModel-title").text(MessageResolver("title.administration.user.add", "Add a new User"));
			$("#addUserbutton").text(MessageResolver("label.action.add", "Add"));
			$("#user_form").prop("action", "/Save");
			$("#addUserModel").modal('toggle');
		},
		error : unknowError
	});
	return false;
}

function editSingleUser(userId) {
	if (userId == null || userId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_user"));
		if (selectedScenario.length != 1)
			return false;
		userId = selectedScenario[0];
	}
	$("#addUserModel .alert,.label-danger").remove()
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
		success : function(response, textStatus, jqXHR) {
			$("#rolescontainer").html(response);
			$("#addUserModel-title").text(MessageResolver("title.user.update", "Update a User"));
			$("#addUserbutton").text(MessageResolver("label.action.edit", "Edit"));
			$("#user_form").prop("action", "/Save");
			$("#addUserModel").modal('toggle');
		},
		error : unknowError
	});
	return false;
}
