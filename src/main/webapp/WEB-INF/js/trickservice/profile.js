$(function () {
	application["settings-fixed-header"] = {
		fixedOffset: $(".nav-tab"),
		marginTop: application.fixedOffset
	};
	$(window).scroll(function (e) {
		if (($(window).scrollTop() + $(window).height()) === $(document).height()) {
			var $selectedTab = $(".tab-pane.active"), attr = $selectedTab.attr("data-scroll-trigger");
			if ($selectedTab.attr("data-update-required") === "false" && typeof attr !== typeof undefined && attr !== false)
				window[$selectedTab.attr("data-scroll-trigger")].apply();
		}
	});
});

function deleteSqlite(id) {
	var currentSize = $("#section_sqlite>table>tbody>tr").length, size = parseInt($("#sqlitePageSize").val()), $confirm = showDialog("#confirm-dialog", MessageResolver("confirm.delete.sqlite","Are you sure you want to delete this database?"));
	$(".btn-danger", $confirm).on("click",function(){
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Profile/Sqlite/" + id + "/Delete",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					$("#section_sqlite>table>tbody>tr[data-trick-id='" + id + "']").remove();
					if (currentSize == size)
						loadUserSqlite(true);
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

function deleteReport(id) {
	var $progress = $("#loading-indicator").show(), currentSize = $("#section_report>table>tbody>tr").length, size = parseInt($("#reportPageSize").val()), $confirm = showDialog("#confirm-dialog", MessageResolver("confirm.delete.report","Are you sure you want to delete this report?"));
	$(".btn-danger", $confirm).on("click",function(){
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Profile/Report/" + id + "/Delete",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					$("#section_report>table>tbody>tr[data-trick-id='" + id + "']").remove();
					if (currentSize == size)
						loadUserReport(true);
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
	var currentSize = $("#section_sqlite table>tbody>tr").length, size = parseInt($("#sqlitePageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		var $progress = $("#progress-sqlite").show();
		$.ajax({
			url: context + "/Profile/Section/Sqlite",
			async: false,
			type: "get",
			data: {
				"page": (currentSize / size) + 1
			},
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite>table>tbody>tr").each(function () {
					var $current = $("#section_sqlite>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_sqlite>table>tbody"));
				});
				return false;
			},
			error: unknowError
		}).complete( () => $progress.hide());
	}
	return true;
}

function userReportScrolling() {
	var currentSize = $("#section_report>table>tbody>tr").length, size = parseInt($("#reportPageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		var $progress = $("#progress-report").show();
		$.ajax({
			url: context + "/Profile/Section/Report",
			async: false,
			type: "get",
			data: {
				"page": (currentSize / size) + 1
			},
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_report>table>tbody>tr").each(function () {
					var $current = $("#section_report>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_report>table>tbody"));
				});
				return false;
			},
			error: unknowError
		}).complete( () => $progress.hide());
	}
	return true;
}

function updateReportControl(element) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	var $progress = $("#progress-report").show();
	$.ajax({
		url: context + "/Profile/Control/Report/Update",
		type: "post",
		data: serializeForm("#formReportControl"),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				return loadUserReport();
			else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		},
		error: unknowError
	}).complete( () => $progress.hide());
	return false;
}

function updateSqliteControl(element) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	var $progress = $("#progress-sqlite").show();
	$.ajax({
		url: context + "/Profile/Control/Sqlite/Update",
		type: "post",
		data: serializeForm("#formSqliteControl"),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				return loadUserSqlite();
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
	var $progress = $("#progress-sqlite").show();
	$.ajax({
		url: context + "/Profile/Section/Sqlite",
		type: "get",
		async: true,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (update) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite>table>tbody>tr").each(function () {
					var $current = $("#section_sqlite>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_sqlite>table>tbody"));
				});
			} else {
				$("#section_sqlite").replaceWith($("#section_sqlite", new DOMParser().parseFromString(response, "text/html")));
				fixTableHeader("#section_sqlite table");
			}
			return false;
		},
		error: unknowError
	}).complete(()=> $progress.hide());
	return true;
}

function loadUserReport(update) {
	var $progress = $("#progress-report").show();
	$.ajax({
		url: context + "/Profile/Section/Report",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (update) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite>table>tbody>tr").each(function () {
					var $current = $("#section_sqlite>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_sqlite>table>tbody"));
				});
			} else {
				$("#section_report").replaceWith($("#section_report", new DOMParser().parseFromString(response, "text/html")));
				fixTableHeader("#section_report table");
			}
			return false;
		},
		error: unknowError
	}).complete(()=> $progress.hide());
	return true;
}

function updateProfile(form) {
	$(".label-danger").remove();
	var $progress = $("#loading-indicator").show();
	$.ajax({
			url: context + "/Profile/Update",
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
						setTimeout(() => switchLangueTo(context+"/Profile"+"?lang=" + newlang), 2000);

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
			url: context + "/Profile/OTP/Update",
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