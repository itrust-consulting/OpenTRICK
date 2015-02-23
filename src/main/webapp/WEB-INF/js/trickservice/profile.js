$(function() {
	$('ul.nav-tab a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
		var target = $(e.target).attr("href");
		if ($(target).attr("data-update-required") == "true") {
			window[$(target).attr("data-trigger")].apply();
			$(target).attr("data-update-required", "false");
		}
	});
});

function loadUserSqlite() {
	$.ajax({
		url : context + "/Profile/Section/Sqlite",
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var $section = $(parser.parseFromString(response, "text/html")).find("#section_sqlite");
			$("#section_sqlite").replaceWith($section);
			setTimeout(function() {
				$("#section_sqlite table").stickyTableHeaders({
					cssTopOffset : ".nav-tab",
					fixedOffset : 6
				});
			}, 500);
			return false;
		},
		error : unknowError
	});
	return true;
}

function updateReportControl(element){
	if(element!=undefined && !$(element).is(":checked"))
		return false;
	console.log("here");
}

function updateSqliteControl(element){
	if(element!=undefined && !$(element).is(":checked"))
		return false;
	console.log("here");
}

function loadUserReport() {
	$.ajax({
		url : context + "/Profile/Section/Report",
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var $section = $(parser.parseFromString(response, "text/html")).find("#section_report");
			$("#section_report").replaceWith($section);
			setTimeout(function() {
				$("#section_report table").stickyTableHeaders({
					cssTopOffset : ".nav-tab",
					fixedOffset : 6
				});
			}, 500);
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
				success : function(response) {

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