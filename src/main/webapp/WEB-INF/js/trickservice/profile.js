function updateProfile(form) {

	$.ajax({
		url : context + "/Profile/Update",
		type : "post",
		contentType : "application/json",
		data : serializeForm(form),
		success : function(response) {

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
					$(errElement).appendTo($("#success"));
					$("#success").removeAttr("hidden");
				}

				}
			}

			if (!$("#" + form + " .label-danger").length) {
				var successElement = document.createElement("div");
				successElement.setAttribute("class", "alert alert-success");
				$(successElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + MessageResolver("label.user.update.success", "Profile successfully updated"));
				$(successElement).appendTo($("#success"));
				$("#success").removeAttr("hidden");
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
			$(errElement).appendTo($("#success"));
			$("#success").removeAttr("hidden");
		},
	});

	return false;
}