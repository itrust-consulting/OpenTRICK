$(function() {
	$('ul.nav-tab a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
		var target = $(e.target).attr("href");
		if ($(target).attr("data-update-required") === "true") {
			window[$(target).attr("data-trigger")].apply();
			$(target).attr("data-update-required", "false");
		}
	});

	$(window).scroll(function(e) {
		if (($(window).scrollTop() + $(window).height()) === $(document).height()) {
			var $selectedTab = $(".tab-pane.active"), attr = $selectedTab.attr("data-scroll-trigger");
			if ($selectedTab.attr("data-update-required") === "false" && typeof attr !== typeof undefined && attr !== false)
				window[$selectedTab.attr("data-scroll-trigger")].apply();
		}
	});
});

function deleteSqlite(id) {
	var currentSize = $("#section_sqlite>table>tbody>tr").length, size = parseInt($("#sqlitePageSize").val());
	$.ajax({
		url : context + "/Profile/Sqlite/" + id + "/Delete",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response,textStatus,jqXHR) {
			if (response["success"] != undefined) {
				$("#section_sqlite>table>tbody>tr[data-trick-id='" + id + "']").remove();
				if (currentSize == size)
					loadUserSqlite(true);
			} else if (response["error"] != undefined)
				new Modal($("#alert-dialog").clone(), response["error"]).Show();
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function deleteReport(id) {
	var currentSize = $("#section_report>table>tbody>tr").length, size = parseInt($("#reportPageSize").val());
	$.ajax({
		url : context + "/Profile/Report/" + id + "/Delete",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response,textStatus,jqXHR) {
			if (response["success"] != undefined) {
				$("#section_report>table>tbody>tr[data-trick-id='" + id + "']").remove();
				if (currentSize == size)
					loadUserReport(true);
			} else if (response["error"] != undefined)
				new Modal($("#alert-dialog").clone(), response["error"]).Show();
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function userSqliteScrolling() {
	var currentSize = $("#section_sqlite table>tbody>tr").length, size = parseInt($("#sqlitePageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		$.ajax({
			url : context + "/Profile/Section/Sqlite",
			async : false,
			type : "get",
			data : {
				"page" : (currentSize / size) + 1
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response,textStatus,jqXHR) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite>table>tbody>tr").each(function() {
					var $current = $("#section_sqlite>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_sqlite>table>tbody"));
				});
				return false;
			},
			error : unknowError
		});
	}
	return true;
}

function userReportScrolling() {
	var currentSize = $("#section_report>table>tbody>tr").length, size = parseInt($("#reportPageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		$.ajax({
			url : context + "/Profile/Section/Report",
			async : false,
			type : "get",
			data : {
				"page" : (currentSize / size) + 1
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response,textStatus,jqXHR) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_report>table>tbody>tr").each(function() {
					var $current = $("#section_report>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_report>table>tbody"));
				});
				return false;
			},
			error : unknowError
		});
	}
	return true;
}

function updateReportControl(element) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	$.ajax({
		url : context + "/Profile/Control/Report/Update",
		type : "post",
		data : serializeForm("#formReportControl"),
		contentType : "application/json;charset=UTF-8",
		success : function(response,textStatus,jqXHR) {
			if (response["success"] != undefined)
				return loadUserReport();
			else if (response["error"] != undefined)
				new Modal($("#alert-dialog").clone(), response["error"]).Show();
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function updateSqliteControl(element) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	$.ajax({
		url : context + "/Profile/Control/Sqlite/Update",
		type : "post",
		data : serializeForm("#formSqliteControl"),
		contentType : "application/json;charset=UTF-8",
		success : function(response,textStatus,jqXHR) {
			if (response["success"] != undefined)
				return loadUserSqlite();
			else if (response["error"] != undefined)
				new Modal($("#alert-dialog").clone(), response["error"]).Show();
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function loadUserSqlite(update) {
	$.ajax({
		url : context + "/Profile/Section/Sqlite",
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response,textStatus,jqXHR) {
			if (update) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite>table>tbody>tr").each(function() {
					var $current = $("#section_sqlite>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_sqlite>table>tbody"));
				});
			} else {
				$("#section_sqlite").replaceWith($(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite"));
				setTimeout(function() {
					$("#section_sqlite>table").stickyTableHeaders({
						cssTopOffset : ".nav-tab",
						fixedOffset : 6
					});
				}, 500);
			}
			return false;
		},
		error : unknowError
	});
	return true;
}

function loadUserReport(update) {
	$.ajax({
		url : context + "/Profile/Section/Report",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response,textStatus,jqXHR) {
			if (update) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_sqlite>table>tbody>tr").each(function() {
					var $current = $("#section_sqlite>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_sqlite>table>tbody"));
				});
			} else {
				$("#section_report").replaceWith($(new DOMParser().parseFromString(response, "text/html")).find("#section_report"));
				setTimeout(function() {
					$("#section_report table").stickyTableHeaders({
						cssTopOffset : ".nav-tab",
						fixedOffset : 6
					});
				}, 500);
			}
			return false;
		},
		error : unknowError
	});
	return true;
}

function updateProfile(form) {

	$
			.ajax({
				url : context + "/Profile/Update",
				type : "post",
				contentType : "application/json",
				data : serializeForm(form),
				success : function(response,textStatus,jqXHR) {

					$("#profileInfo").attr("hidden", "hidden");
					$("#profileInfo div").remove();

					var alert = $("#" + form + " .label-danger");
					if (alert.length)
						alert.remove();

					for ( var error in response) {

						$("#profileInfo").attr("hidden", "hidden");
						$("#profileInfo div").remove();
						var errorElement = document.createElement("label");
						errorElement.setAttribute("class", "label label-danger");

						$(errorElement).text(response[error]);
						switch (error) {
						case "currentPassword":
							$(errorElement).appendTo($("#" + form + " #currentPassword").parent());
							break;
						case "password":
							$(errorElement).appendTo($("#" + form + " #password").parent());
							break;
						case "repeatPassword":
							$(errorElement).appendTo($("#" + form + " #repeatPassword").parent());
							break;
						case "firstName":
							$(errorElement).appendTo($("#" + form + " #firstName").parent());
							break;
						case "lastName":
							$(errorElement).appendTo($("#" + form + " #lastName").parent());
							break;
						case "email":
							$(errorElement).appendTo($("#" + form + " #email").parent());
							break;
						case "locale":
							$(errorElement).appendTo($("#" + form + " #locale").parent());
							break;
						case "user": {

							var errElement = document.createElement("div");
							errElement.setAttribute("class", "alert alert-success");
							$(errElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + $(errorElement).text());
							$(errElement).appendTo($("#profileInfo"));
							$("#profileInfo").removeAttr("hidden");
						}

						}
					}

					if (!$("#" + form + " .label-danger").length) {
						var successElement = document.createElement("div");
						successElement.setAttribute("class", "alert alert-success");
						$(successElement).html(
								"<button type='button' class='close' data-dismiss='alert'>&times;</button>"
										+ MessageResolver("label.user.update.success", "Profile successfully updated"));
						$(successElement).appendTo($("#profileInfo"));
						$("#profileInfo").removeAttr("hidden");
						var prevlang = $("#perviouslanguage").val();
						var newlang = $("#" + form + " #locale").val();
						if (prevlang !== newlang)
							setTimeout(function() {
								document.location.href = "?lang=" + newlang;
							}, 2000);

					}
					return false;

				},
				error : function(jqXHR, textStatus, errorThrown) {
					var alert = $("#" + form + " .label-danger");
					if (alert.length)
						alert.remove();
					var errElement = document.createElement("div");
					errElement.setAttribute("class", "alert alert-success");
					$(errElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + $(errorElement).text());
					$(errElement).appendTo($("#profileInfo"));
					$("#profileInfo").removeAttr("hidden");
				},
			});

	return false;
}