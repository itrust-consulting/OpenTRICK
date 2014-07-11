function Login(url, timeoutInterceptor) {
	this.url = url;
	this.timeoutInterceptor = timeoutInterceptor;
}

Login.prototype = {
	IsAuthenticate : function() {
		var authentificated = false;
		$.ajax({
			url : context + "/IsAuthenticate",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				authentificated = response === true;
			}
		});
		return authentificated;
	},
	Display : function() {
		this.timeoutInterceptor.Stop();
		this.timeoutInterceptor.loginShow = true;
		var authentificate = this.IsAuthenticate();
		if (authentificate)
			return this.timeoutInterceptor.Start(this);
		var view = new Modal();
		var that = this;
		view.DefaultFooterButton = function() {
		};
		view.Intialise();
		$(view.modal_footer).remove();
		$.ajax({
			url : this.url,
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var login = $($.parseHTML(response)).find("#login");
				if (login.length) {
					view.setBody($(login).html());
					$(view.modal).on('hidden.bs.modal', function() {
						that.timeoutInterceptor.loginShow = false;
					});
					$(view.modal_body).find("#login_signin_button").on("click", function() {
						var alerts = $(view.modal_body).find(".alert");
						if (alerts.length)
							alerts.remove();
						$.ajax({
							url : context + "/j_spring_security_check",
							contentType : "application/x-www-form-urlencoded",
							type : "post",
							data : $(view.modal_body).find("#login_form").serialize(),
							success : function(response) {
								var $htmlResult = $.parseHTML(response);
								if ($($htmlResult).find("#login").length)
									$(view.modal_body).prepend($($htmlResult).find(".alert"));
								else {
									view.Destroy();
									that.timeoutInterceptor.Start(that);
								}
							}
						});
						return false;
					});
					view.Show();
				}
				return false;
			},error : unknowError
		});
		return authentificate;
	}
};