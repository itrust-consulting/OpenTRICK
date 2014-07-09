function TimeoutInterceptor() {
	this.lastUpdate = null;
	this.LIMIT_SESSION = 15 * 60 * 1000;
	this.ALERT_TIME = 3 * 60 * 1000;
	this.stopState = true;
	this.timer = {};
	this.loginShow = false;
	this.TIME_TO_DISPLAY_ALERT = 0;
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
	CurrentTime : function() {
		return (new Date().getTime() - this.lastUpdate.getTime());
	},
	ShowLogin : function() {
		this.Stop();
		$("#alert-dialog").modal("hide");
		var url = undefined;
		if ($("#nav-container").length) {
			var idAnalysis = $("*[trick-class='Analysis']").attr("trick-id");
			if (idAnalysis != undefined)
				url = context + "/Analysis/" + idAnalysis + "/Select";
		}
		if (url == undefined)
			url = document.URL;
		this.loginShow = true;
		new Login(url, this).Display();
		return false;
	},
	AlertTimout : function() {
		var that = this;
		$("#alert-dialog .modal-body").html(this.messages.Alert.replace("%d", Math.floor((this.LIMIT_SESSION - this.CurrentTime()) * 0.001)));
		$("#alert-dialog").modal("show");
		$("#alert-dialog .btn-danger").on("click", function() {
			if(!that.Reinitialise())
				return that.ShowLogin();
			return false;
		});
		setTimeout(function() {
			$("#alert-dialog").modal("hide");
		}, 5000);
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
			success : function(response) {
				authentificated = response === true;
			},error : unknowError
		});
		this.loginShow = temp;
		return authentificated;
	}
	
	,
	Check : function() {
		if (this.CurrentTime() > this.LIMIT_SESSION) {
			this.ShowLogin();
		} else if (this.CurrentTime() > this.TIME_TO_DISPLAY_ALERT)
			this.AlertTimout();
	},
	Stop : function() {
		this.stopState = true;
		clearInterval(this.timer);
	},
	Start : function(login) {
		if (login != null) {
			try {
				if (!login.IsAuthenticate())
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
		that.timer = setInterval(function() {
			that.Check();
		}, 60000);
		// before jQuery send the request we will push it to our array
		$.ajaxSetup({
			beforeSend : function() {
				that.Update();
			}
		});
		return false;
	}
};