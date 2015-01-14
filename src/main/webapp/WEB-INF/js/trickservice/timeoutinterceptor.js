function TimeoutInterceptor() {
	this.lastUpdate = null;
	this.LIMIT_SESSION = 15.01 * 60 * 1000;
	this.ALERT_TIME = 1 * 60 * 1000;
	this.timer = {};
	this.intervalTimeout = null;
	this.logoutTimeout = null;
	this.TIME_TO_DISPLAY_ALERT = 14 * 60 * 1000;
	this.alertDialog = null;
	this.messages = {
		Alert : "",
		Logout : "",
		Offine : ""
	};
}

TimeoutInterceptor.prototype = {
	Update : function() {
		if (this.lastUpdate == null || this.CurrentTime() < this.LIMIT_SESSION)
			this.lastUpdate = new Date();
	},
	IsAuthenticate : function() {
		var authentificated = false;
		try {
			$.ajax({
				url : context + "/IsAuthenticate",
				contentType : "application/json;charset=UTF-8",
				async : false,
				success : function(response) {
					authentificated = response === true;
				},
			});
		} catch (e) {
			if(e["name"]==="NS_ERROR_FAILURE"){
				
				this.Stop();
				
				if(this.alertDialog == null)
					this.alertDialog = new Modal($("#alert-dialog").clone());
				
				var alertDialog = this.alertDialog;
				
				alertDialog.setBody(this.messages.Offine);
				
				var $buttonOK = $(alertDialog.modal).find(".btn-danger");
				
				$buttonOK.unbind("click");
				
				$(alertDialog.modal).find(".btn-danger").on("click", function() {
					alertDialog.Destroy();
				});
				
				if (this.alertDialog.isHidden)
					this.alertDialog.Show();
				
				return true;
			}
		}
		return authentificated;
	},
	CurrentTime : function() {
		return (new Date().getTime() - this.lastUpdate.getTime());
	},
	ShowLogin : function() {
		if(this.IsAuthenticate())
			return true;
		var url = undefined;
		if ($("#nav-container").length) {
			var idAnalysis = $("#nav-container").attr("trick-id");
			if (idAnalysis != undefined)
				url = context + "/Analysis/" + idAnalysis + "/Select";
		}

		if (url == undefined)
			url = window.location.href;

		location.href = url;

		return false;
	},
	AlertTimout : function() {
		var that = this;
		if (this.alertDialog == null) {
			this.alertDialog = new Modal($("#alert-dialog").clone());
			
			$(this.alertDialog.modal).find(".btn-danger").on("click", function() {
				if (!that.Reinitialise())
					return that.ShowLogin();
				else {
					that.alertDialog.Hide();
					clearTimeout(that.logoutTimeout);
					that.timer = setTimeout(function() {
						that.Check();
					}, that.TIME_TO_DISPLAY_ALERT);
				}
				return false;
			});

			$(this.alertDialog.modal).on("show.bs.modal", function() {
				var daily = Math.floor((that.LIMIT_SESSION - that.CurrentTime()) * 0.001);
				if (daily > 0) {
					that.alertDialog.setBody(that.messages.Alert.replace("%d", daily));
					that.intervalTimeout = setInterval(function() {
						daily = Math.floor((that.LIMIT_SESSION - that.CurrentTime()) * 0.001);
						if (daily > 0)
							that.alertDialog.setBody(that.messages.Alert.replace("%d", daily));
						else
							that.alertDialog.setBody(that.messages.Logout);
					}, 1000);
				} else
					that.alertDialog.setBody(that.messages.Logout);
			});

			$(this.alertDialog.modal).on("hidden.bs.modal", function() {
				clearInterval(that.intervalTimeout);
			});
		}

		if (this.alertDialog.isHidden)
			this.alertDialog.Show();

		return false;
	},
	Initialise : function() {

		var that = this;

		this.stopState = false;
		this.lastUpdate = null;
		this.loginShow = false;

		if (!this.IsAuthenticate())
			this.ShowLogin();
		else {

			// before jQuery send the request we will push it to our array
			$.ajaxSetup({
				beforeSend : function(jqXHR, options) {
					that.Update();
					//console.log(options.url);
					if (!options.url.match("/IsAuthenticate$")) {
						if (!that.IsAuthenticate())
							that.ShowLogin();
						else {
							clearTimeout(that.timer);
							that.timer = setTimeout(function() {
								that.Check();
							}, that.TIME_TO_DISPLAY_ALERT);
						}
					}
				}
			});

			this.messages.Alert = MessageResolver("info.session.expired", "Your session will be expired in %d secondes");
			this.messages.Logout = MessageResolver("info.session.expired.alert", "Your session has been expired, redirecting to Login ...");
			this.messages.Offine = MessageResolver("error.server.offline", "Server appears to be offline... Try again later!");

			this.timer = setTimeout(function() {
				this.Check();
			}, this.TIME_TO_DISPLAY_ALERT);
		}

	},
	Reinitialise : function() {

		var authentificated = false;

		$.ajax({
			url : context + "/IsAuthenticate",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(response) {
				authentificated = response === true;
			},
			error : unknowError
		});

		return authentificated;

	},
	Check : function() {
		var that = this;
		if (this.CurrentTime() > this.LIMIT_SESSION) {
			this.ShowLogin();
		} else if (this.CurrentTime() > this.TIME_TO_DISPLAY_ALERT) {
			this.AlertTimout();
			var daily = this.LIMIT_SESSION - this.CurrentTime();
			this.logoutTimeout = setTimeout(function() {
				that.ShowLogin();
			}, daily);
		}
	},
	Stop : function(){
		$.ajaxSetup({beforeSend : undefined});
		clearTimeout(this.logoutTimeout);
		clearTimeout(this.timer);
		clearInterval(this.intervalTimeout);
		return true;
	},
	Start : function(login) {
		if (login != null) {
			try {
				if (!this.IsAuthenticate())
					return;
			} catch (e) {
				console.log(e.message);
			} finally {
				delete login;
			}
		} else
			this.Initialise();
		return false;
	}
};