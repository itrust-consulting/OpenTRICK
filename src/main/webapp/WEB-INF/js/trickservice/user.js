function saveUser(form) {
	var $form = $("#" + form),  idUser = $form.find("#user_id");
	if (!idUser.length)
		idUser = -1;
	else
		idUser = parseInt(idUser.val());
	$(".label-danger", $form).remove();
	var $progress = $("#loading-indicator").show();
	$.ajax(
		{
			url: context + "/Admin/User/Save",
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
						case "login":
							$(errorElement).appendTo($("#user_login",$form).parent());
							break;
						case "password":
							$(errorElement).appendTo($("#user_password",$form).parent());
							break;
						case "firstName":
							$(errorElement).appendTo($("#user_firstName",$form).parent());
							break;
						case "lastName":
							$(errorElement).appendTo($("#user_lastName",$form).parent());
							break;
						case "email":
							$(errorElement).appendTo($("#user_email",$form).parent());
							break;
						default: 
							showDialog("#alert-dialog", response[error]);
					}
					hasError = true;
				}

				if (!hasError) {
					showDialog("success",idUser === -1 ? MessageResolver("success.user.created", "User was successfully created") : MessageResolver("success.user.update",
					"User was successfully updated"));
					reloadSection("section_user");
					$form.closest(".modal").modal("hide");
				}
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.add.user", "An unknown error occurred during adding/updating users"));
				$("#user_password",$form).prop("value", "");
			}
		}).complete(function () {
			$progress.hide();
		});
	return false;
}

function deleteUser(userId, name) {
	if (userId == null || userId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_user");
		if (selectedScenario.length != 1)
			return false;
		userId = selectedScenario[0];
		name = $("#section_user tbody tr[data-trick-id='" + userId + "'] td:nth-child(2)").text();
	}
	var $loadProgress = $("#loading-indicator").show();
	$.ajax(
		{
			url: context + "/Admin/User/" + userId + "/Prepare-to-delete",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else {
					var $deleteUserDialog = $("#deleteUserModal", new DOMParser().parseFromString(response, "text/html"));
					if ($deleteUserDialog.length) {
						if ($("#deleteUserModal").length)
							$("#deleteUserModal").replaceWith($deleteUserDialog);
						else
							$deleteUserDialog.appendTo("#dialog-body");
						var $form = $deleteUserDialog.find("form"), $progress = $deleteUserDialog.find(".progress"), $submitInput = $deleteUserDialog
							.find("input[type='submit']"), $deleteButton;

						$progress.hide();

						function fadeOutComplete() {
							return $(this).remove();
						}

						$deleteButton = $("button[name='delete']", $deleteUserDialog).click(function () {
							$submitInput.click();
						});

						$form.on("submit", function () {
							$deleteUserDialog.find(".label").remove();
							$deleteButton.prop('disabled', true);
							var data = {
								idUser: $form.find("input[name='idUser']").val(),
								switchOwners: {},
								deleteAnalysis: []
							}
							$form.find("select").each(function () {
								var $this = $(this), value = $this.val(), name = $this.attr("name");
								if (value < 1)
									data.deleteAnalysis.push(name);
								else
									data.switchOwners[name] = value;
							});

							$progress.show();

							$.ajax({
								url: context + "/Admin/User/Delete",
								type: "post",
								contentType: "application/json;charset=UTF-8",
								data: JSON.stringify(data),
								success: function (response, textStatus, jqXHR) {
									var result = parseJson(response);
									if (result == undefined)
										$("<label class='label label-danger'/>").appendTo("#deleteUserErrors").text(
											MessageResolver("error.unknown.occurred", "An unknown error occurred")).fadeOut(15000, fadeOutComplete);
									if (result.success != undefined) {
										$deleteUserDialog.modal("hide");
										reloadSection(['section_user', 'section_admin_analysis']);
									} else if (result.error != undefined)
										$("<label class='label label-danger' />").appendTo("#deleteUserErrors").text(result.error).fadeOut(15000, fadeOutComplete);
									else {
										for (var key in result) {
											var $select = $deleteUserDialog.find("select[name=" + key + "]");
											if ($select.length)
												$("<label class='label label-danger' />").appendTo($select.parent()).text(result[key]);
											else
												$("<label class='label label-danger' />").appendTo("#deleteUserErrors").text(result.error);
										}
									}
								},
								error: function (jqXHR, textStatus, errorThrown) {
									$("<label class='label label-danger' />").appendTo("#deleteUserErrors").text(
										MessageResolver("error.unknown.occurred", "An unknown error occurred")).fadeOut(15000, fadeOutComplete);
								},
								complete: function () {
									$deleteButton.prop('disabled', false);
									$progress.hide();
								}
							});
							return false;
						});

						$deleteUserDialog.modal("show");
					} else
						unknowError();
				}
				return false;
			},
			error: unknowError
		}).complete(function () {
			$loadProgress.hide();
		});
	return false;
}

function newUser(isIDSUser) {
	if (findSelectItemIdBySection("section_user").length > 0)
		return false;
	var $progress = $("#loading-indicator").show();
	$("#addUserModel .alert,.label-danger").remove()
	$("#user_id").prop("value", "-1");
	$("#user_login").prop("value", "");
	$("#user_login").removeAttr("disabled");
	$("#user_password").prop("value", "");
	$("#user_firstName").prop("value", "");
	$("#user_lastName").prop("value", "");
	$("#user_email").prop("value", "");
	$("#radioConnexionType input[value='0']").prop("checked", "ckecked");
	$("#radioConnexionType").button("reset");
	$.ajax({
		url: context + "/Admin/Roles",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			$("#rolescontainer").html(response);
			$("#addUserModel-title").text(MessageResolver("title.administration.user.add", "Add a new User"));
			$("#addUserbutton").text(MessageResolver("label.action.save", "save"));
			$("#user_form").prop("action", "/Save");
			$("#addUserModel").modal('show');
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	$("#addUserModel-title").text(MessageResolver("title.administration.user.add", "Add a new User"));
	$("#addUserbutton").text(MessageResolver("label.action.add", "Add"));
	$("#user_form").prop("action", "/Save");
	return false;
}

function editSingleUser(userId) {

	if (userId == null || userId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_user");
		if (selectedScenario.length != 1)
			return false;
		userId = selectedScenario[0];
	}

	var rows = $("#section_user").find("tr[data-trick-id='" + userId + "'] td:not(:first-child)"), $progress = $("#loading-indicator").show();
	;
	$("#user_id").prop("value", userId);
	$("#user_login").prop("value", $(rows[0]).text());
	$("#user_login").prop("disabled", "disabled");
	$("#user_password").prop("value", "");
	$("#user_firstName").prop("value", $(rows[1]).text());
	$("#user_lastName").prop("value", $(rows[2]).text());
	$("#user_email").prop("value", $(rows[3]).text());
	$("#radioConnexionType input[value='" + $(rows[5]).attr("data-trick-real-value") + "']").parent().button("toggle");
	$("#addUserModel .alert,.label-danger").remove()
	$.ajax({
		url: context + "/Admin/User/Roles/" + userId,
		type: "get",
		async: false,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			$("#rolescontainer").html(response);
			$("#addUserModel-title").text(MessageResolver("title.user.update", "Update a User"));
			$("#addUserbutton").text(MessageResolver("label.action.save", "Save"));
			$("#user_form").prop("action", "/Save");
			$("#addUserModel").modal('show');
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}
