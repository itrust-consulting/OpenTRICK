
/**
 * It contains functions related to the user profile in the TrickService application.
 */
$(function () {
	application["settings-fixed-header"] = {
		fixedOffset: $(".nav-tab"),
		marginTop: application.fixedOffset
	};
	$(window).scroll(function (e) {
		if (($(window).scrollTop() + $(window).height()) >= ($(document).height()*.98)) {
			var $selectedTab = $(".tab-pane.active"), attr = $selectedTab.attr("data-scroll-trigger");
			if ($selectedTab.attr("data-update-required") === "false" && typeof attr !== typeof undefined && attr !== false)
				window[$selectedTab.attr("data-scroll-trigger")].apply();
		}
	});
});

/**
 * Updates the user invitation count.
 * @returns {boolean} Returns false.
 */
function updateUserInvitation(){
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Account/Invitation/Count",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["count"] !== undefined)
				$("#invitation-count").text(response["count"]);
		}
	}).complete( () => $progress.hide());
	return false;
}

/**
 * Deletes a SQLite database.
 *
 * @param {number} id - The ID of the database to delete.
 * @returns {Promise} - A promise that resolves when the database is deleted.
 */
function deleteSqlite(id) {
	return deleteUserData(id,"/Account/Sqlite/" + id + "/Delete","sqlite", loadUserSqlite, MessageResolver("confirm.delete.sqlite","Are you sure you want to delete this database?"));
}

/**
 * Deletes a report with the specified ID.
 *
 * @param {number} id - The ID of the report to delete.
 * @returns {Promise} - A promise that resolves when the report is successfully deleted.
 */
function deleteReport(id) {
	return deleteUserData(id,"/Account/Report/" + id + "/Delete","report", loadUserReport, MessageResolver("confirm.delete.report","Are you sure you want to delete this report?"));
}

/**
 * Manages the invitation for a user.
 *
 * @param {string} id - The ID of the invitation.
 * @param {string} action - The action to perform on the invitation.
 * @param {string} message - The message to display.
 * @returns {Promise} - A promise that resolves when the invitation is successfully managed.
 */
function inivationManager(id, action, message){
	var sucessAction = (success) => {  
		showDialog("success",success);
		updateUserInvitation();
	};
	return deleteUserData(id,"/Account/Invitation/" + id + "/"+action,"invitation", loadUserInvitation , message, sucessAction);
}

/**
 * Accepts an invitation with the specified ID.
 *
 * @param {number} id - The ID of the invitation to accept.
 * @returns {void}
 */
function acceptInvitation(id){
	return inivationManager(id, "Accept", MessageResolver("confirm.accept.invitation","Are you sure you want to accept this request?"));
}

/**
 * Rejects an invitation with the specified ID.
 *
 * @param {number} id - The ID of the invitation to reject.
 * @returns {boolean} - True if the rejection was successful, false otherwise.
 */
function rejectInvitation(id){
	return inivationManager(id, "Reject", MessageResolver("confirm.reject.invitation","Are you sure you want to reject this request?"));
}

/**
 * Validates the user's email address.
 * 
 * @returns {boolean} Returns false to prevent the default form submission.
 */
function validateUserEmail(){
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Account/Validate/Email",
		type: "POST",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				showDialog("success", response["success"]);
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		}
	}).complete( () => $progress.hide());
	return false;
}

/**
 * Deletes user data.
 *
 * @param {string} id - The ID of the user data to be deleted.
 * @param {string} url - The URL to send the delete request to.
 * @param {string} name - The name of the user data section.
 * @param {function} callBack - The callback function to be called after successful deletion.
 * @param {string} message - The message to be displayed in the confirmation dialog.
 * @param {function} successAction - The action to be performed after successful deletion.
 * @returns {boolean} - Returns false.
 */
function deleteUserData(id,url, name, callBack, message, successAction) {
	var section = "#section_"+name, $body = $(section+">table>tbody"), currentSize = $("tr",$body).length, size = parseInt($("#"+name+"PageSize").val()), $confirm = showDialog("#confirm-dialog",message);
	$(".btn-danger", $confirm).on("click",function(){
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + url,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					$("tr[data-trick-id='" + id + "']", $body).remove();
					if (currentSize == size)
						callBack(true);
					successAction(response["success"]);
				} else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete( () => $progress.hide());
		$confirm.modal("hide");
	});
	return false;
}

/**
 * Scrolls to the user section for SQLite.
 * @returns {void}
 */
function userSqliteScrolling() {
	return userSectionScrolling("/Account/Section/Sqlite", "sqlite");;
}

/**
 * Scrolls to the user report section.
 * @returns {void}
 */
function userReportScrolling() {
	return userSectionScrolling("/Account/Section/Report", "report");
}

/**
 * Scrolls to the user invitation section.
 * @returns {void}
 */
function userInvitationScrolling() {
	return userSectionScrolling("/Account/Section/Invitation", "invitation");
}

/**
 * Scrolls the user section and loads more data using AJAX.
 * 
 * @param {string} url - The URL to send the AJAX request to.
 * @param {string} name - The name of the section.
 * @returns {boolean} Returns false if the scrolling is already in progress, otherwise returns true.
 */
function userSectionScrolling(url, name) {
	var key = "section-"+name+"scrolling",  section = "#section_"+name, $section = $(section),  currentSize = $("table>tbody>tr", $section).length, size = parseInt($("#"+name+"PageSize").val());
	if(application[key])
		return false;
	else application[key] = true;
	try{
		if (currentSize >= size && currentSize % size === 0) {
			var $progress = $("#progress-"+name).show();
			$.ajax({
				url: context + url,
				type: "get",
				data: {
					"page": (currentSize / size) + 1
				},
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					var $body = $("table>tbody", $section);
					$(new DOMParser().parseFromString(response, "text/html"), section).find("table>tbody>tr").each(function () {
						var $this = $(this), $current = $("tr[data-trick-id='" + $this.attr("data-trick-id") + "']", $body);
						if (!$current.length)
							$this.appendTo($body);
					});
					return false;
				},
				error: unknowError
			}).complete( () => { 
				$progress.hide();
				application[key] = false;
			});
		}else application[key] = false;
	}catch (e) {
		application[key] = false;
	}
	return false;
}


/**
 * Updates the report control element.
 *
 * @param {HTMLElement} element - The element to update.
 * @returns {Promise} - A promise that resolves when the update is complete.
 */
function updateReportControl(element) {
  return updateUserControl(element,"/Account/Control/Report/Update", "report","#formReportControl",loadUserReport);;
}

/**
 * Updates the SQLite control for a user.
 *
 * @param {HTMLElement} element - The HTML element triggering the update.
 * @returns {Promise} - A promise that resolves when the update is complete.
 */
function updateSqliteControl(element) {
	return updateUserControl(element,"/Account/Control/Sqlite/Update", "sqlite","#formSqliteControl",loadUserSqlite);
}

/**
 * Updates the invitation control for a user.
 *
 * @param {HTMLElement} element - The HTML element triggering the update.
 * @returns {Promise} - A promise that resolves when the update is complete.
 */
function updateInvitationControl(element) {
	return updateUserControl(element,"/Account/Control/Invitation/Update", "invitation","#formInvitationControl",loadUserInvitation);
}

/**
 * Updates the user control based on the provided parameters.
 *
 * @param {Element} element - The element to check if it is checked.
 * @param {string} url - The URL to send the AJAX request to.
 * @param {string} name - The name of the progress element to show.
 * @param {HTMLFormElement} form - The form to serialize and send as data.
 * @param {Function} callBack - The callback function to execute on success.
 * @returns {boolean} Returns false if the element is not checked, otherwise returns undefined.
 */
function updateUserControl(element,url, name, form, callBack) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	var $progress = $("#progress-"+name).show();
	$.ajax({
		url: context + url,
		type: "post",
		data: serializeForm(form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				callBack();
			else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		},
		error: unknowError
	}).complete(()=> $progress.hide());
	return false;
}

/**
 * Loads the user's SQLite section.
 *
 * @param {boolean} update - Indicates whether to update the section.
 * @returns {Promise} - A promise that resolves when the section is loaded.
 */
function loadUserSqlite(update) {
	return loadUserSection(update,"/Account/Section/Sqlite", "sqlite");
}

/**
 * Loads the user report section.
 *
 * @param {boolean} update - Indicates whether to update the section.
 * @returns {Promise} - A promise that resolves when the section is loaded.
 */
function loadUserReport(update) {
	return loadUserSection(update,"/Account/Section/Report", "report");
}

/**
 * Loads the user invitation section.
 *
 * @param {boolean} update - Indicates whether to update the section.
 * @returns {Promise} - A promise that resolves when the section is loaded.
 */
function loadUserInvitation(update) {
	return loadUserSection(update,"/Account/Section/Invitation", "invitation");
}


/**
 * Loads the user section using AJAX.
 *
 * @param {boolean} update - Indicates whether to update the section or replace it entirely.
 * @param {string} url - The URL to fetch the user section from.
 * @param {string} name - The name of the section.
 * @returns {boolean} - Returns false.
 */
function loadUserSection(update,url,name) {
	var section = "#section_"+name, $progress = $("#progress-"+name).show();
	$.ajax({
		url: context + url,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $section = $(section), $body = $("table>tbody", $section);
			if (update) {
				$(new DOMParser().parseFromString(response, "text/html")).find(section+">table>tbody>tr").each(function () {
					var $current = $("tr[data-trick-id='" + $(this).attr("data-trick-id") + "']", $body);
					if (!$current.length)
						$(this).appendTo($body);
				});
			} else {
				$section.replaceWith($(section, new DOMParser().parseFromString(response, "text/html")));
				fixTableHeader(section+" table");
			}
			return false;
		},
		error: unknowError
	}).complete(()=> $progress.hide());
	return false;
}

/**
 * Updates the user profile.
 * 
 * @param {string} form - The ID of the form to be submitted.
 * @returns {boolean} - Returns false to prevent the default form submission.
 */
function updateProfile(form) {
	$(".label-danger").remove();
	var $progress = $("#loading-indicator").show();
	$.ajax({
			url: context + "/Account/Update",
			type: "post",
			contentType: "application/json;charset=UTF-8",
			data: serializeForm(form),
			success: function (response, textStatus, jqXHR) {
				for (var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");
					$(errorElement).text(response[error]);
					switch (error) {
						case "currentPassword":
						case "password":
						case "repeatPassword":
						case "firstName":
						case "lastName":
						case "email":
						case "locale":
						case "ticketingUsername":
						case "ticketingPassword":
							$(errorElement).appendTo($("#" + form + " #" + error).parent());
							break;
						default :
							showDialog("#alert-dialog", response[error]);
					}
				}

				if (!$(".label-danger,.alert-danger").length) {
					showDialog("success", MessageResolver("label.user.update.success", "Profile successfully updated"));
					var prevlang = $("#perviouslanguage").val(), newlang = $("#" + form + " #locale").val();
					if (prevlang !== newlang)
						setTimeout(() => switchLangueTo(context+"/Account"+"?lang=" + newlang), 2000);

				}

			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			}
		}).complete(()=> $progress.hide());

	return false;
}

/**
 * Updates the user's OTP (One-Time Password).
 * 
 * @returns {boolean} Returns false to prevent the default form submission.
 */
function updateUserOtp(){
	var $progress = $("#loading-indicator").show(), $form =  $("#section_user_otp form#user-otp-form"), data = serializeForm($form);
	$.ajax({
			url: context + "/Account/OTP/Update",
			type: "post",
			contentType: "application/json;charset=UTF-8",
			data: serializeForm($form),
			success: function (response, textStatus, jqXHR) {
				$view = $("form#user-otp-form",new DOMParser().parseFromString(response, "text/html"));
				if($view.length)
					$form.replaceWith($view);
				else if(response['error'])
					showDialog("#alert-dialog",response['error'] );
				else unknowError();
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			}
		}).complete(()=> $progress.hide());
	return false;
}

/**
 * Adds a credential.
 * @returns {boolean} Returns false.
 */
function addCredential(){
	var $progress = $("#loading-indicator").show();
	$.ajax({
			url: context + "/Account/Credential/Form",
			type: "Get",
			contentType: "text/plain;charset=UTF-8",
			success: credentialFormProcessing,
			error: unknowError
		}).complete(()=> $progress.hide());
	return false;
}

/**
 * Process the credential form response.
 *
 * @param {any} response - The response from the server.
 * @param {string} textStatus - The status of the request.
 * @param {XMLHttpRequest} jqXHR - The XMLHttpRequest object.
 */
function credentialFormProcessing(response, textStatus, jqXHR){
	var $view = $("#credential-modal-form",new DOMParser().parseFromString(response, "text/html"));
	if($view.length){
		$view.appendTo("#dialog-body");
		setTimeout(() => {
			var $btnSubmit=$view.find("input[type='submit']"), $username = $view.find("input[name='name']"), $value = $view.find("input[name='value']") ;
			$view.find("form").on("submit", (e) => saveCredential(e, $view));
			$view.find("button[name='save']").on("click", (e) => $btnSubmit.click());
			$view.find("input[name='type']").on("change", (e) => {
				if(e.currentTarget.checked){
					$username.prop("required", e.currentTarget.value==="PASSWORD").prop("readonly", e.currentTarget.value!=="PASSWORD");
					if(e.currentTarget.value==="PASSWORD")
						$value.prop("type", "password");
					else $value.prop("type", "text");
				}
			}).trigger("change");
		}, 10);
		$view.modal("show").on('hidden.bs.modal', () => $view.remove());
	}
	else unknowError();
}

/**
 * Edits a credential.
 * @param {string} id - The ID of the credential to edit.
 * @returns {boolean} Returns false if the ID is undefined or null, or if there are multiple items found. Otherwise, returns true.
 */
function editCredential(id){
	if(id == undefined || id == null){
		var items = findSelectItemIdBySection("section_credential");
		if(items.length!=1)
			return false;
		id = items[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
			url: context + "/Account/Credential/"+id+"/Edit",
			type: "Get",
			contentType: "text/plain;charset=UTF-8",
			success: credentialFormProcessing,
			error: unknowError
		}).complete(()=> $progress.hide());
	return false;
}

/**
 * Deletes selected credentials.
 * @returns {boolean} Returns false if no items are selected, otherwise returns undefined.
 */
function deleteCredential(){
	var items = findSelectItemIdBySection("section_credential");
	if(!items.length)
		return false;
	else {
		var $confirmModal = showDialog("#confirm-dialog", MessageResolver(
					"confirm.delete.credential", "Are you sure, you want to delete selected credentials"));
				$confirmModal.find(".modal-footer>button[name='yes']").one("click", function (e) {
					$confirmModal.modal("hide");
				var $progress = $("#loading-indicator").show();
				$.ajax({
					url: context + "/Account/Credential/Delete",
					type: "Delete",
					contentType:"application/json;charset=UTF-8",
					data : JSON.stringify(items),
					success: (response, textStatus, jqXHR) => {
						$("#section_credential table>tbody>tr[data-trick-id]").filter((i,e)=>response[parseInt(e.getAttribute("data-trick-id"))]).remove();
						updateMenu(undefined,"#section_credential", "#menu_credential");
					},	error: unknowError
				}).complete(()=> $progress.hide());
		});
	}
	return false;
}

/**
 * Saves the credential data using an AJAX request.
 * 
 * @param {Event} e - The event object.
 * @param {jQuery} $view - The jQuery object representing the view.
 * @returns {boolean} Returns false to prevent the default form submission behavior.
 */
function saveCredential(e, $view){
	var $form = $view.find("form"), data = serializeFormToJson($form),  $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Account/Credential/Save",
		type: "POST",
		contentType:"application/json;charset=UTF-8",
		data : data,
		success: (response, textStatus, jqXHR) => {
			$form.find(".label-danger").remove();
			if(response.success){
				$view.modal("hide");
				reloadSection("section_credential");
			}
			else {
				for (var error in response) {
					var $errorElement = $("<label class='label label-danger'/>").text(response[error]);
					switch (error) {
						case "name":
						case "value":
						case "publicUrl":
							$errorElement.appendTo($("input[name='"+error+"']",$form).parent());
							break;
						case "customer":
							$errorElement.appendTo($("select[name='customer']",$form).parent());
							break;
						case "type":
							$errorElement.appendTo($("#radio-btn-credential-type",$form));
							break;
						default:
							showDialog("#alert-dialog", response[error]);
							break;
					}
				}
			}
			
		},
		error: unknowError
	}).complete(()=> $progress.hide());
	
	return false;
}