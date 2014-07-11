function ProgressBar() {
	this.progress = null;
	this.progressbar = null;
	this.sr_only = null;
	this.infoText = null;
	this.incrementStep = 1;
	this.completeText = "complete";
	this.waitting = true;
	this.listners = {};

	ProgressBar.prototype.__Create = function() {
		// declare progressbar
		this.progress = document.createElement("div");
		this.progressbar = document.createElement("div");
		this.sr_only = document.createElement("span");
		this.infoText = document.createElement("span");
		// design progressbar
		this.progress.setAttribute("class", "progress progress-striped active");
		this.progressbar.setAttribute("class", "progress-bar progress-bar-info");
		this.progressbar.setAttribute("role", "progressbar");
		this.progressbar.setAttribute("aria-valuenow", "1");
		this.progressbar.setAttribute("aria-valuemin", "0");
		this.progressbar.setAttribute("valuemax", "100");
		this.progressbar.setAttribute("style", "width: 100%");
		this.sr_only.setAttribute("class", "sr-only");
		this.infoText.setAttribute("class", "progress-info");

		// build progress
		this.progress.appendChild(this.progressbar);
		this.progress.appendChild(this.infoText);
		this.progressbar.appendChild(this.sr_only);
		return false;
	};

	ProgressBar.prototype.Update = function(value, info) {
		this.setInfo(info);
		this.setValue(value);
	};

	ProgressBar.prototype.Increment = function(value) {
		if (value == null)
			value = parseInt($(this.progressbar).attr("aria-valuenow")) + this.incrementStep;
		else
			value += $(this.progressbar).attr("aria-valuenow");

		return this.setValue(value);
	};

	ProgressBar.prototype.setValue = function(value) {
		if (!$.isNumeric(value))
			return false;
		if (this.progressbar != null) {
			if (value == 0)
				$(this.infoText).css("margin-top", 0);
			else
				$(this.infoText).css("margin-top", "-17px");
			$(this.progressbar).css("width", value + "%");
			$(this.progressbar).prop("aria-valuenow", value);
			if (this.waitting && $(this.progress).hasClass("active")) {
				this.waitting = false;
				$(this.progressbar).removeClass("active");
			}

			if (this.listners["change"] != undefined) {
				try {
					this.listners["change"](this, value);
				} catch (e) {
					console.log(e);
				}
			}
			if (value == 100 && this.listners["complete"] != undefined) {
				try {
					this.listners["complete"](this);
				} catch (e) {
					console.log(e);
				}
			}
		}

		if (this.sr_only != null)
			$(this.sr_only).text(value + "% " + this.completeText);
		return false;
	};

	ProgressBar.prototype.OnChange = function(callack) {
		if ($.isFunction(callack))
			this.listners["change"] = callack;
		return false;
	};

	ProgressBar.prototype.OnComplete = function(callack) {
		if ($.isFunction(callack))
			this.listners["complete"] = callack;
		return false;
	};

	ProgressBar.prototype.setInfo = function(info) {
		if (info != null)
			$(this.infoText).text(info);
		return false;
	};

	ProgressBar.prototype.Anchor = function(anchor) {

		if (anchor != null && anchor != undefined)
			anchor.insertBefore(this.progress, anchor.firstChild);
		return false;
	};

	ProgressBar.prototype.Initialise = function(element) {
		this.__Create();
		this.Anchor(element);
	};

	ProgressBar.prototype.Remove = function() {
		if (this.progress != null)
			$(this.progress).remove();
		return false;
	};

	ProgressBar.prototype.Destroy = function() {
		this.Remove();
		var instance = this;
		setTimeout(function() {
			delete instance;
		}, 10);
		return false;
	};

}