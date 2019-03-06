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

function deleteSqlite(id) {
	return deleteUserData(id,"/Account/Sqlite/" + id + "/Delete","sqlite", loadUserSqlite, MessageResolver("confirm.delete.sqlite","Are you sure you want to delete this database?"));
}

function deleteReport(id) {
	return deleteUserData(id,"/Account/Report/" + id + "/Delete","report", loadUserReport, MessageResolver("confirm.delete.report","Are you sure you want to delete this report?"));
}

function inivationManager(id, action, message){
	var sucessAction = (success) => {  
		showDialog("success",success);
		updateUserInvitation();
	};
	return deleteUserData(id,"/Account/Invitation/" + id + "/"+action,"invitation", loadUserInvitation , message, sucessAction);
}

function acceptInvitation(id){
	return inivationManager(id, "Accept", MessageResolver("confirm.accept.invitation","Are you sure you want to accept this request?"));
}

function rejectInvitation(id){
	return inivationManager(id, "Reject", MessageResolver("confirm.reject.invitation","Are you sure you want to reject this request?"));
}

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

function userSqliteScrolling() {
	return userSectionScrolling("/Account/Section/Sqlite", "sqlite");;
}

function userReportScrolling() {
	return userSectionScrolling("/Account/Section/Report", "report");
}

function userInvitationScrolling() {
	return userSectionScrolling("/Account/Section/Invitation", "invitation");
}

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


function updateReportControl(element) {
  return updateUserControl(element,"/Account/Control/Report/Update", "report","#formReportControl",loadUserReport);;
}

function updateSqliteControl(element) {
	return updateUserControl(element,"/Account/Control/Sqlite/Update", "sqlite","#formSqliteControl",loadUserSqlite);
}

function updateInvitationControl(element) {
	return updateUserControl(element,"/Account/Control/Invitation/Update", "invitation","#formInvitationControl",loadUserInvitation);
}

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

function loadUserSqlite(update) {
	return loadUserSection(update,"/Account/Section/Sqlite", "sqlite");
}

function loadUserReport(update) {
	return loadUserSection(update,"/Account/Section/Report", "report");
}

function loadUserInvitation(update) {
	return loadUserSection(update,"/Account/Section/Invitation", "invitation");
}


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

function deleteCredential(){
	var items = findSelectItemIdBySection("section_credential");
	if(!items.length)
		return false;
	else {
		var $confirmModal = showDialog("#confirm-dialog", items.length > 1 ? MessageResolver("confirm.delete.multi.credential", "Are you sure you want to delete all selected credentials") : MessageResolver(
					"confirm.delete.single.credential", "Are you sure, you want to delete selected credentials"));
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
					},	error: unknowError
				}).complete(()=> $progress.hide());
		});
	}
	return false;
}

function saveCredential(e, $view){
	var $form = $view.find("form"), data = serializeFormToJson($form),  $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Account/Credential/Save",
		type: "POST",
		contentType:"application/json;charset=UTF-8",
		data : data,
		success: (response, textStatus, jqXHR) => {
			if(response.success){
				$view.modal("hide");
				reloadSection("section_credential");
			}
			else {
				for (var error in response) {
					var $errorElement = $("<label class='label label-danger'/>").text(response[error]);
					switch (error) {
						case "name":
							$errorElement.appendTo($("input[name='name']",$form).parent());
							break;
						case "value":
							$errorElement.appendTo($("input[name='value']",$form).parent());
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