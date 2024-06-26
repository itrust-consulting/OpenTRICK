/**
 * Represents a progress bar.
 * @constructor
 */
function ProgressBar() {
	this.progress = null;
	this.progressbar = null;
	this.sr_only = null;
	this.infoText = null;
	this.incrementStep = 1;
	this.completeText = "complete";
	this.waitting = true;
	this.listners = {};
}

/**
 * Creates the progress bar elements.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.__Create = function () {
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

/**
 * Updates the progress bar with the given value and info.
 * @param {number} value - The value of the progress bar.
 * @param {string} info - The info text to display.
 */
ProgressBar.prototype.Update = function (value, info) {
	this.setInfo(info);
	this.setValue(value);
};

/**
 * Increments the progress bar by the given value.
 * @param {number} value - The value to increment by.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.Increment = function (value) {
	if (value == null)
		value = parseInt($(this.progressbar).attr("aria-valuenow")) + this.incrementStep;
	else
		value += $(this.progressbar).attr("aria-valuenow");

	return this.setValue(value);
};

/**
 * Sets the value of the progress bar.
 * @param {number} value - The value to set.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.setValue = function (value) {
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

/**
 * Sets the change event listener for the progress bar.
 * @param {function} callack - The callback function to be called on change.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.OnChange = function (callack) {
	if ($.isFunction(callack))
		this.listners["change"] = callack;
	return false;
};

/**
 * Sets the complete event listener for the progress bar.
 * @param {function} callack - The callback function to be called on complete.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.OnComplete = function (callack) {
	if ($.isFunction(callack))
		this.listners["complete"] = callack;
	return false;
};

/**
 * Sets the info text of the progress bar.
 * @param {string} info - The info text to set.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.setInfo = function (info) {
	if (info != null)
		$(this.infoText).text(info);
	return false;
};

/**
 * Anchors the progress bar to the specified element.
 * @param {HTMLElement} anchor - The element to anchor the progress bar to.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.Anchor = function (anchor) {

	if (anchor != null && anchor != undefined)
		anchor.insertBefore(this.progress, anchor.firstChild);
	return false;
};

/**
 * Initializes the progress bar with the specified element.
 * @param {HTMLElement} element - The element to initialize the progress bar with.
 */
ProgressBar.prototype.Initialise = function (element) {
	this.__Create();
	this.Anchor(element);
};

/**
 * Removes the progress bar from the DOM.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.Remove = function () {
	$(this.progress).remove();
	return false;
};

/**
 * Destroys the progress bar and cleans up any resources.
 * @returns {boolean} - Returns false.
 */
ProgressBar.prototype.Destroy = function () {
	this.Remove();
	var instance = this;
	setTimeout(function () {
		delete instance;
	}, 10);
	return false;
};