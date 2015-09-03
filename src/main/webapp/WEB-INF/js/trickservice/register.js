function register(form) {
	$.ajax({
		url : context + "/DoRegister",
		type : "post",
		contentType : "application/json",
		data : serializeForm(form),
		success : function(response,textStatus,jqXHR) {

			$("#success").attr("hidden", "hidden");
			$("#success div").remove();

			var alert = $("#" + form + " .label-danger");
			if (alert.length)
				alert.remove();

			$("#success").attr("hidden", "hidden");
			$("#success div").remove();
			
			for ( var error in response) {

				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");

				$(errorElement).text(response[error]);
				switch (error) {
				case "login":
					$(errorElement).appendTo($("#" + form + " #login").parent());
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
					errElement.setAttribute("class", "alert alert-danger");
					$(errElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + $(errorElement).text());
					$(errElement).appendTo($("#success"));
					$("#success").removeAttr("hidden");
				}
				case "constraint": {
					var errElement = document.createElement("div");
					errElement.setAttribute("class", "alert alert-danger");
					$(errElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + $(errorElement).text());
					$(errElement).appendTo($("#success"));
					$("#success").removeAttr("hidden");
				}
				
				}
			}

			if (!$("#" + form + " .label-danger").length) {

				var login = $("#" + form + " #login").val();
				
				$('body').load(context + "/Login", {
					"registerSuccess" : true,
					"login" : login
				});

			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			var alert = $("#" + form + " .label-danger");
			if (alert.length)
				alert.remove();
			var errElement = document.createElement("div");
			errElement.setAttribute("class", "alert alert-danger");
			$(errElement).html("<button type='button' class='close' data-dismiss='alert'>&times;</button>" + errorThrown);
			$(errElement).appendTo($("#success"));
			$("#success").removeAttr("hidden");
		},
	});

	return false;
}

$(function() {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});
});

/**
 * serializeJSON serialize an object to json string
 * 
 * @param $
 */
(function($) {

	$.fn.serializeJSON = function() {
		var json = {};
		var form = $(this);
		form.find('input, select, textarea').each(function() {
			var val;
			if (!this.name)
				return;

			if ('radio' === this.type) {
				if (json[this.name]) {
					return;
				}

				json[this.name] = this.checked ? this.value : '';
			} else if ('checkbox' === this.type) {
				val = json[this.name];

				if (!this.checked) {
					if (!val) {
						json[this.name] = '';
					}
				} else {
					json[this.name] = typeof val === 'string' ? [ val, this.value ] : $.isArray(val) ? $.merge(val, [ this.value ]) : this.value;
				}
			} else {
				json[this.name] = this.value;
			}
		});
		return json;
	};

})(jQuery);

function serializeForm(form) {
	var $form = $(form);
	if (!$form.length)
		$form = $("#" + form);
	var data = $form.serializeJSON();
	return JSON.stringify(data);
}
