/**
 * Converts a form to JSON object.
 *
 * @param {HTMLFormElement} form - The form element to convert.
 * @returns {Object} - The JSON object representing the form data.
 */
function formToJson(form) {
	return $(form)
		.serializeArray()
		.reduce(function (json, { name, value }) {
			json[name] = value;
			return json;
		}, {});
}

/**
 * Handles the registration form submission.
 * 
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns false to prevent the default form submission behavior.
 */
function register(e) {
	e.preventDefault();
	let $form = $(e.target);
	$.ajax({
		type: "post",
		url: context + "/DoRegister",
		contentType: "application/json;charset=UTF-8",
		data: JSON.stringify(formToJson($form)),
		success: function (response) {

			$("#success").attr("hidden", "hidden");
			$("#success div").remove();
			$(".label-danger", $form).remove();
			$("#success").attr("hidden", "hidden");
			$("#success div").remove();

			for (let error in response) {

				let errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);
				switch (error) {
					case "login":
						$(errorElement).appendTo($("#login", $form).parent());
						break;
					case "password":
						$(errorElement).appendTo($("#password", $form).parent());
						break;
					case "repeatPassword":
						$(errorElement).appendTo($("#repeatPassword", $form).parent());
						break;
					case "firstName":
						$(errorElement).appendTo($("#firstName", $form).parent());
						break;
					case "lastName":
						$(errorElement).appendTo($("#lastName", $form).parent());
						break;
					case "email":
						$(errorElement).appendTo($("#email", $form).parent());
						break;
					case "locale":
						$(errorElement).appendTo($("#locale", $form).parent());
						break;
					case "user": {
						let errElement = document.createElement("div");
						errElement.setAttribute("class", "alert alert-danger");
						$(errElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + $(errorElement).text());
						$(errElement).appendTo($("#success"));
						$("#success").removeAttr("hidden");
						break;
					}
					case "constraint": {
						let errEle = document.createElement("div");
						errEle.setAttribute("class", "alert alert-danger");
						$(errEle).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + $(errEle).text());
						$(errEle).appendTo($("#success"));
						$("#success").removeAttr("hidden");
						break;
					}

				}
			}

			if (!$(".label-danger", $form).length) {
				let token = $("meta[name='_csrf']").attr("content");
				let header = $("meta[name='_csrf_header']").attr("content");
				$('body').load(context + "/Signin", {
					"registerSuccess": true,
					"username": $("#login", $form).val(),
					"password": $("#password", $form).val(),
					header: token
				});
			}
			return false;
		},
		error: function (_jqXHR, _textStatus, errorThrown) {
			let errElement = document.createElement("div");
			errElement.setAttribute("class", "alert alert-danger");
			$(errElement).html("<button type='button' class='close' onclick='$(this).parent().remove()'>&times;</button>" + errorThrown);
			$(errElement).appendTo($("#success"));
			$(".label-danger", $form).remove();
			$("#success").removeAttr("hidden");
		}
	});

	return false;
}


(function ($) {
	let token = $("meta[name='_csrf']").attr("content");
	let header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (_e, xhr) { xhr.setRequestHeader(header, token); });
	$("#registerform").on("submit", register);
})(jQuery);
