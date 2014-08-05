function TimeoutInterceptor() {
	this.lastUpdate = null;
	this.LIMIT_SESSION = 15.01 * 60 * 1000;
	this.ALERT_TIME = 3 * 60 * 1000;
	this.stopState = true;
	this.timer = {};
	this.loginShow = false;
	this.intervalTimeout = null;
	this.TIME_TO_DISPLAY_ALERT = 0;
	this.alertDialog = null;
	this.messages = {
		Alert : "",
		Logout : ""
	};
}

TimeoutInterceptor.prototype = {
	Update : function() {
		if (this.stopState) {
			if (!this.loginShow)
				return this.ShowLogin();
			return false;
		}
		if (this.lastUpdate == null || this.CurrentTime() < this.LIMIT_SESSION)
			this.lastUpdate = new Date();
	},
	IsAuthenticate : function() {
		var authentificated = false;
		$.ajax({
			url : context + "/IsAuthenticate",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(response) {
				authentificated = response === true;
			}
		});
		return authentificated;
	},
	CurrentTime : function() {
		return (new Date().getTime() - this.lastUpdate.getTime());
	},
	ShowLogin : function() {
		this.Stop();
		var url = undefined;
		if ($("#nav-container").length) {
			var idAnalysis = $("#nav-container").attr("trick-id");
			if (idAnalysis != undefined)
				url = context + "/Analysis/" + idAnalysis + "/SelectOnly";
		}
		if (url == undefined)
			url = document.URL;
		this.loginShow = true;
		new Login(url, this).Display();
		return false;
	},
	AlertTimout : function() {
		var that = this;
		if (this.alertDialog == null) {
			this.alertDialog = new Modal();
			this.alertDialog.FromContent($("#alert-dialog").clone());
			$(this.alertDialog.modal).find(".btn-danger").on("click", function() {
				setTimeout(function() {
					if (!that.Reinitialise())
						return that.ShowLogin();
				}, 2000);
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
		if (this.loginShow)
			return false;
		if (this.alertDialog.isHidden) {
			this.alertDialog.Show();
			if ((this.LIMIT_SESSION - this.CurrentTime()) > 10000) {
				setTimeout(function() {
					that.alertDialog.Hide();
				}, 5000);
			}
		}
		return false;
	},
	Initialise : function() {
		this.messages.Alert = MessageResolver("info.session.expired", "Your session will be expired in %d secondes");
		this.messages.Logout = MessageResolver("info.session.expired.alert", "Your session has been expired");
		this.TIME_TO_DISPLAY_ALERT = this.LIMIT_SESSION - this.ALERT_TIME;

	},
	Reinitialise : function() {
		var temp = this.loginShow;
		this.loginShow = true;
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
		this.loginShow = temp;
		return authentificated;
	},
	UpdateInterval : function() {
		if (this.stopState || this.loginShow)
			return;
		var that = this;
		var daily = this.LIMIT_SESSION - this.CurrentTime();
		var interval = 50000;
		if (daily < 10000)
			interval = 5000;
		else if (daily < 30000)
			interval = 10000;
		else if (daily < 60000)
			interval = 30000;
		this.timer = setTimeout(function() {
			that.Check();
		}, interval);
	},
	Check : function() {
		if (this.CurrentTime() > this.LIMIT_SESSION) {
			this.ShowLogin();
		} else if (this.CurrentTime() > this.TIME_TO_DISPLAY_ALERT)
			this.AlertTimout();
		this.UpdateInterval();
	},
	Stop : function() {
		this.stopState = true;
		if (this.alertDialog != null)
			this.alertDialog.Hide();
		clearTimeout(this.timer);
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
		var that = this;
		this.stopState = false;
		this.lastUpdate = new Date();
		this.loginShow = false;
		if (!this.IsAuthenticate())
			this.ShowLogin();
		else
			this.UpdateInterval();
		// before jQuery send the request we will push it to our array
		$.ajaxSetup({
			beforeSend : function() {
				that.Update();
			}
		});
		return false;
	}
};