
/**
 * Represents the application object.
 * @type {Application}
 */
var application = new Application();

function Application() {
	this.modal = {};
	this.data = {};
	this.rights = {};
	this.language = "en";
	this.localesMessages = {};
	this.fixedOffset = 0
	this.shownScrollTop = true;
	this.editingModeFroceAbort = false;
	this.analysisType = {};
	this.errorTemplate = '<div class="popover popover-danger" role="tooltip"><div class="arrow"></div><div class="popover-content"></div></div>';
	this.timeoutSetting = {
		idle: 1680000,
		sessionTimeout: 1800000,
		refreshTime: 300000,
		idleRefreshTime: 180000,
	}
	this.notification = {
		z_index: 1068,
		offset: {
			x: 0,
			y: 35
		},
		placement: {
			from: "bottom",
			align: "right"
		},
		delay: 5000,
		errorDelay: 10000
	}
	this.currencyFormat = new Intl.NumberFormat("fr-FR", { style: "currency", currency: "EUR", maximumFractionDigits: 0, minimumFractionDigits: 0 });
	this.numberFormat = new Intl.NumberFormat("fr-FR");
	this.numberFormatNoDecimal = new Intl.NumberFormat("fr-FR", { maximumFractionDigits: 0, minimumFractionDigits: 0 });
	this.percentageFormat = new Intl.NumberFormat("fr-FR", { style: "percent", maximumFractionDigits: 0, minimumFractionDigits: 0 });
	this.currentNotifications = {};
}


/**
 * Represents the analysis rights.
 * @typedef {Object} ANALYSIS_RIGHT
 * @property {Object} ALL - The "ALL" analysis right.
 * @property {number} ALL.value - The value of the "ALL" analysis right.
 * @property {string} ALL.name - The name of the "ALL" analysis right.
 * @property {Object} EXPORT - The "EXPORT" analysis right.
 * @property {number} EXPORT.value - The value of the "EXPORT" analysis right.
 * @property {string} EXPORT.name - The name of the "EXPORT" analysis right.
 * @property {Object} MODIFY - The "MODIFY" analysis right.
 * @property {number} MODIFY.value - The value of the "MODIFY" analysis right.
 * @property {string} MODIFY.name - The name of the "MODIFY" analysis right.
 * @property {Object} READ - The "READ" analysis right.
 * @property {number} READ.value - The value of the "READ" analysis right.
 * @property {string} READ.name - The name of the "READ" analysis right.
 */
var ANALYSIS_RIGHT = {
	ALL: {
		value: 0,
		name: "ALL"
	},
	EXPORT: {
		value: 1,
		name: "EXPORT"
	},
	MODIFY: {
		value: 2,
		name: "MODIFY"
	},
	READ: {
		value: 3,
		name: "READ"
	}
};


/**
 * Represents the open mode options.
 * @typedef {Object} OPEN_MODE
 * @property {Object} READ - The read-only mode.
 * @property {string} READ.value - The value of the read-only mode.
 * @property {string} READ.name - The name of the read-only mode.
 * @property {Object} READ_ESTIMATION - The read-only estimation mode.
 * @property {string} READ_ESTIMATION.value - The value of the read-only estimation mode.
 * @property {string} READ_ESTIMATION.name - The name of the read-only estimation mode.
 * @property {Object} EDIT - The edit mode.
 * @property {string} EDIT.value - The value of the edit mode.
 * @property {string} EDIT.name - The name of the edit mode.
 * @property {Object} EDIT_ESTIMATION - The edit estimation mode.
 * @property {string} EDIT_ESTIMATION.value - The value of the edit estimation mode.
 * @property {string} EDIT_ESTIMATION.name - The name of the edit estimation mode.
 * @property {Object} EDIT_MEASURE - The edit measure mode.
 * @property {string} EDIT_MEASURE.value - The value of the edit measure mode.
 * @property {string} EDIT_MEASURE.name - The name of the edit measure mode.
 * @property {function} isReadOnly - Checks if the given mode is read-only.
 * @property {function} valueOf - Returns the open mode object based on the given value.
 */
var OPEN_MODE = {
	READ: {
		value: "read-only",
		name: "READ"
	},
	READ_ESTIMATION: {
		value: "read-only-estimation",
		name: "READ_ESTIMATION"
	},
	EDIT: {
		value: "edit",
		name: "EDIT"
	},
	EDIT_ESTIMATION: {
		value: "edit-estimation",
		name: "EDIT_ESTIMATION"
	},
	EDIT_MEASURE: {
		value: "edit-measure",
		name: "EDIT_MEASURE"
	},
	isReadOnly: function (mode) {
		if (mode === undefined)
			return application.openMode && application.openMode.value.startsWith("read-only");
		var openMode = this.valueOf(mode);
		return openMode && openMode.value.startsWith("read-only");
	},
	valueOf: function (value) {
		for (var key in OPEN_MODE) {
			if (key == "valueOf" || key == "isReadOnly")
				continue;
			else if (OPEN_MODE[key] == value || OPEN_MODE[key].value == value || OPEN_MODE[key].name == value)
				return OPEN_MODE[key];
		}
		return undefined;
	}
}

/**
 * Represents the collection of notification types.
 * Represents the notification types with their corresponding properties.
 * 
 * @typedef {Object} NotificationType
 * @property {string} type - The type of the notification.
 * @property {string} icon - The icon associated with the notification.
 * @property {string[]} names - The names or identifiers associated with the notification.
 * @type {Object.<string, NotificationType>}
 */
var NOTIFICATION_TYPE = {
	ERROR: {
		type: "danger",
		icon: "glyphicon glyphicon-warning-sign",
		names: ["error", "#alert-dialog", "#danger-dialog", "alert-dialog", "danger-dialog", "danger", "alert"]
	},
	WARNING: {
		type: "warning",
		icon: "glyphicon glyphicon-exclamation-sign",
		names: ["#warning-dialog", "warning-dialog", "warning"]
	},
	INFO: {
		type: "info",
		icon: "glyphicon glyphicon-info-sign",
		names: ["#info-dialog", "info-dialog", "info", "message"]
	},
	SUCCESS: {
		type: "success",
		icon: "glyphicon glyphicon-ok-sign",
		names: ["#success-dialog", "success-dialog", "success"]
	},
	DOWNLOAD: {
		type: "success",
		icon: "glyphicon glyphicon-download",
		names: ["#download-dialog", "download-dialog", "download"]
	},
	valueOf: function (value) {
		for (var key in NOTIFICATION_TYPE) {
			if (key == "valueOf")
				continue;
			else if (NOTIFICATION_TYPE[key] == value || NOTIFICATION_TYPE[key].type == value || NOTIFICATION_TYPE[key].names.indexOf(value) != -1)
				return NOTIFICATION_TYPE[key];
		}
		return undefined;
	}
}

/**
 * Represents the analysis types for data.
 * @typedef {Object} ANALYSIS_TYPE
 * @property {Object} QUANTITATIVE - Represents the quantitative analysis type.
 * @property {string} QUANTITATIVE.type - The type of analysis (QUANTITATIVE).
 * @property {Function} QUANTITATIVE.isQualitative - Checks if the analysis is qualitative (always returns false).
 * @property {Function} QUANTITATIVE.isQuantitative - Checks if the analysis is quantitative (always returns true).
 * @property {Function} QUANTITATIVE.isSupported - Checks if the analysis type is supported.
 * @property {Object} HYBRID - Represents the hybrid analysis type.
 * @property {string} HYBRID.type - The type of analysis (HYBRID).
 * @property {Function} HYBRID.isQualitative - Checks if the analysis is qualitative (always returns true).
 * @property {Function} HYBRID.isQuantitative - Checks if the analysis is quantitative (always returns true).
 * @property {Function} HYBRID.isSupported - Checks if the analysis type is supported.
 * @property {Object} QUALITATIVE - Represents the qualitative analysis type.
 * @property {string} QUALITATIVE.type - The type of analysis (QUALITATIVE).
 * @property {Function} QUALITATIVE.isQualitative - Checks if the analysis is qualitative (always returns true).
 * @property {Function} QUALITATIVE.isQuantitative - Checks if the analysis is quantitative (always returns false).
 * @property {Function} QUALITATIVE.isSupported - Checks if the analysis type is supported.
 * @property {Function} valueOf - Returns the analysis type object based on the given value.
 * @property {Function} isSupported - Checks if the analysis type is supported.
 * @property {Function} isHybrid - Checks if the analysis type is hybrid.
 * @property {Function} isQuantitative - Checks if the analysis type is quantitative.
 * @property {Function} isQualitative - Checks if the analysis type is qualitative.
 */
var ANALYSIS_TYPE = {
	QUANTITATIVE: {
		type: "QUANTITATIVE",
		isQualitative: () => false,
		isQuantitative: () => true,
		isSupported: type => type == "QUANTITATIVE" || type == "HYBRID"
	},
	HYBRID: {
		type: "HYBRID",
		isQualitative: () => true,
		isQuantitative: () => true,
		isSupported: type => true
	},
	QUALITATIVE: {
		type: "QUALITATIVE",
		isQualitative: () => true,
		isQuantitative: () => false,
		isSupported: type => type == "QUALITATIVE" || type == "HYBRID"
	},
	valueOf: value => {
		for (var key in ANALYSIS_TYPE) {
			if (typeof key === "function")
				continue;
			if (ANALYSIS_TYPE[key] === value || ANALYSIS_TYPE[key].type === value)
				return ANALYSIS_TYPE[key];
		}
		return undefined;
	},
	isSupported: (typeName, value) => {
		var type = ANALYSIS_TYPE.valueOf(typeName);
		return type && type.isSupported(value);
	},
	isHybrid: value => value === 'HYBRID',
	isQuantitative: value => value === 'QUANTITATIVE',
	isQualitative: value => value === 'QUALITATIVE'
}


if (!String.prototype.capitalize) {
	String.prototype.capitalize = function () {
		return this.charAt(0).toUpperCase() + this.slice(1);
	}
}

if (!String.prototype.startsWith) {
	String.prototype.startsWith = function (searchString, position) {
		position = position || 0;
		return this.substr(position, searchString.length) === searchString;
	};
}

if (!Array.prototype.removeIf) {
	Array.prototype.removeIf = function (callback) {
		var i = this.length;
		while (i--) {
			if (callback(this[i], i)) {
				this.splice(i, 1);
			}
		}
	}
}

/**
 * Checks if a value is a function.
 *
 * @param {*} value - The value to check.
 * @returns {boolean} - Returns `true` if the value is a function, else `false`.
 */
function isFunction(value) {
	var getType = {};
	return value && getType.toString.call(value) === '[object Function]';
}

if (!String.prototype.endsWith) {
	String.prototype.endsWith = function (searchString, position) {
		var subjectString = this.toString();
		if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
			position = subjectString.length;
		}
		position -= searchString.length;
		var lastIndex = subjectString.indexOf(searchString, position);
		return lastIndex !== -1 && lastIndex === position;
	};
}

/**
 * Converts a hexadecimal color code to an RGB object.
 *
 * @param {string} hex - The hexadecimal color code to convert.
 * @returns {Object|null} - The RGB object with properties r, g, and b representing the red, green, and blue values respectively. Returns null if the input is not a valid hexadecimal color code.
 */
function hexToRgb(hex) {
	var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
	hex = hex.replace(shorthandRegex, function (m, r, g, b) {
		return r + r + g + g + b + b;
	});
	var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
	return result ? {
		r: parseInt(result[1], 16),
		g: parseInt(result[2], 16),
		b: parseInt(result[3], 16)
	} : null;
}

/**
 * Validates an email address.
 *
 * @param {string} email - The email address to validate.
 * @returns {boolean} - Returns true if the email is valid, false otherwise.
 */
function validateEmail(email) {
	var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return re.test(email);
}


/**
 * Checks if a value has a valid extension.
 *
 * @param {string} value - The value to check.
 * @param {string} extention - The valid extension(s) to match against. Multiple extensions can be separated by commas.
 * @param {string} button - The button element to enable/disable based on the match result.
 * @param {boolean} optionnal - Optional parameter to indicate if the match is optional.
 * @returns {boolean} - Returns true if the value has a valid extension, false otherwise.
 */
function checkExtention(value, extention, button, optionnal) {
	var extentions = extention.split(","), match = false;
	for (var i = 0; i < extentions.length; i++)
		match |= value.endsWith(extentions[i]);
	if (button && !optionnal)
		$(button).prop("disabled", !match);
	return match;
}

/**
 * Switches the language of the webpage to the specified URL.
 * @param {string} url - The URL to switch the language to.
 * @returns {boolean} - Returns true if the window location hash is undefined, otherwise returns false.
 */
function switchLangueTo(url) {
	if (window.location.hash == undefined)
		return true;
	window.location.replace(url + window.location.hash);
	return false;
}

/**
 * Triggers a function based on the provided target element.
 *
 * @param {jQuery} $target - The target element to trigger the function on.
 */
function triggerCaller($target) {
	try {
		if ($target.attr("data-update-required") == "true") {
			var trigger = $target.attr("data-trigger"), parameters = $target.attr("data-parameters");
			if (parameters == undefined)
				window[trigger].apply();
			else
				window[trigger].apply(null, parameters.split(","));
			$target.attr("data-update-required", "false");
		}
	} catch (e) {
		console.log(e);
	}
}

/**
 * Calls the specified callback function with optional data.
 * @param {jQuery} $target - The target element.
 */
function callBackCaller($target) {
	try {
		var callback = $target.attr("data-callback");
		if (window[callback] != undefined) {
			var data = $target.attr("data-callback-data");
			if (data == undefined)
				window[callback].apply();
			else
				window[callback].apply(null, data.split(","));
		}
	} catch (e) {
		console.log(e);
	}
}

/**
 * Displays a dialog or notification based on the provided parameters.
 *
 * @param {string} dialog - The dialog element or selector.
 * @param {string} message - The message to display in the dialog or notification.
 * @param {string} title - The title of the dialog or notification.
 * @param {string} url - The URL to navigate to when the dialog or notification is closed.
 * @param {function} onClose - The callback function to execute when the dialog or notification is closed.
 * @param {string} placement - The placement of the dialog or notification.
 * @returns {object} - The dialog or notification object.
 */
function showDialog(dialog, message, title, url, onClose, placement) {
	var notificationType = NOTIFICATION_TYPE.valueOf(dialog);
	if (notificationType == undefined) {
		var $dialog = $(dialog), $modalBody = $dialog.find(".modal-body").text(message);
		return $dialog.modal("show");
	} else {
		return showNotifcation(notificationType.type, message, notificationType.icon, url, title, onClose, placement);
	}
}

/**
 * Displays a static dialog or notification based on the provided parameters.
 *
 * @param {string} dialog - The dialog element or selector.
 * @param {string} message - The message to be displayed in the dialog or notification.
 * @param {string} title - The title of the dialog or notification.
 * @param {string} url - The URL to navigate to when the dialog or notification is closed.
 * @param {function} onClose - The callback function to be executed when the dialog or notification is closed.
 * @returns {jQuery} - The jQuery object representing the displayed dialog or notification.
 */
function showStaticDialog(dialog, message, title, url, onClose) {
	var notificationType = NOTIFICATION_TYPE.valueOf(dialog);
	if (notificationType == undefined) {
		var $dialog = $(dialog), $modalBody = $dialog.find(".modal-body").text(message);
		return $dialog.modal("show");
	} else {
		return showStaticNotifcation(notificationType.type, message, notificationType.icon, title, url, onClose);
	}
}

/**
 * Navigates to the specified page.
 * @param {string} page - The page to navigate to.
 */
function gotToPage(page) {
	window.location.assign(context + page);
}

/**
 * Displays a notification using the specified parameters.
 *
 * @param {string} type - The type of the notification.
 * @param {string} message - The message to be displayed in the notification.
 * @param {string} icon - The icon to be displayed in the notification.
 * @param {string} url - The URL to be redirected to when the notification is clicked.
 * @param {string} title - The title of the notification.
 * @param {Function} onClose - The function to be called when the notification is closed.
 * @param {string} placement - The placement of the notification (e.g., "top", "bottom").
 * @returns {object} - The notification object.
 */
function showNotifcation(type, message, icon, url, title, onClose, placement) {
	return $.notify({
		title: title ? title : undefined,
		icon: icon,
		message: message,
		url: url
	}, {
		type: type,
		z_index: application.notification.z_index,
		offset: application.notification.offset,
		placement: placement === undefined ? application.notification.placement : placement,
		delay: type === "danger" || type === "warning" ? application.notification.errorDelay : application.notification.delay,
		onClose: onClose
	});
}

/**
 * Displays a static notification.
 *
 * @param {string} type - The type of the notification.
 * @param {string} message - The message to be displayed in the notification.
 * @param {string} icon - The icon to be displayed in the notification.
 * @param {string} title - The title of the notification.
 * @param {string} url - The URL to be redirected to when the notification is clicked.
 * @param {function} onClose - The callback function to be executed when the notification is closed.
 * @returns {object} - The notification object.
 */
function showStaticNotifcation(type, message, icon, title, url, onClose) {
	var $notification = $.notify({
		title: title ? title : undefined,
		icon: icon,
		message: message,
		url: url
	}, {
		type: type,
		z_index: application.notification.z_index,
		offset: application.notification.offset,
		placement: application.notification.placement,
		onClose: onClose,
		delay: -1
	});

	if (url)
		$notification.$ele.on('click', 'a[data-notify="url"]', (e) => {
			setTimeout(() => {
				$notification.close();
			}, 300);
		});
	return $notification;
}

/**
 * Observes mutations in the DOM and triggers a callback function when an element with the specified class is inserted.
 *
 * @param {string} elementClass - The class name of the element to observe.
 * @param {Function} callback - The callback function to be executed when an element with the specified class is inserted.
 */
function onElementInserted(elementClass, callback) {
	var onMutationsObserved = function (mutations) {
		mutations.forEach(function (mutation) {
			for (var i = 0; i < mutation.addedNodes.length; i++) {
				var element = mutation.addedNodes[i];
				if (element.classList && element.classList.contains(elementClass))
					callback(element);
			}
		});
	};
	var MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
	new MutationObserver(onMutationsObserved).observe(document, { childList: true, subtree: true });
}

/**
 * Handles unknown errors that occur during AJAX requests.
 * @param {jqXHR} jqXHR - The jQuery XMLHttpRequest object.
 * @param {string} textStatus - The status of the request.
 * @param {string} errorThrown - The error message.
 * @returns {boolean} - Returns true if the error was handled, false otherwise.
 */
function unknowError(jqXHR, textStatus, errorThrown) {
	if (typeof textStatus != 'undefined' && textStatus === 'abort' || application["isReloading"])
		return false;
	if (jqXHR !== undefined) {
		if (jqXHR.status !== undefined) {
			switch (jqXHR.status) {
				case 400:
					showDialog("#alert-dialog", MessageResolver("error.400.message"));
					break;
				case 401:
					showDialog("#alert-dialog", MessageResolver("error.401.message"));
					break;
				case 403:
					showDialog("#alert-dialog", MessageResolver("error.403.message"));
					break;
				case 404:
					showDialog("#alert-dialog", MessageResolver("error.404.message"));
					break;
				case 500:
					showDialog("#alert-dialog", MessageResolver("error.500.message"));
					break;
				case 503:
					showDialog("#alert-dialog", MessageResolver("error.503.message"));
					break;
				case 504:
					showDialog("#alert-dialog", MessageResolver("error.504.message"));
					break;
				default:
					showDialog("#alert-dialog", MessageResolver("error.unknown.occurred", "An unknown error occurred"));
			}
		} else
			showDialog("#alert-dialog", MessageResolver("error.unknown.occurred", "An unknown error occurred"));
	} else
		showDialog("#alert-dialog", MessageResolver("error.unknown.occurred", "An unknown error occurred"));
	return true;
}

/**
 * Calculates the width of the scrollbar in the current browser.
 * @returns {number} The width of the scrollbar in pixels.
 */
function getScrollbarWidth() {
	var outer = document.createElement("div");
	outer.style.visibility = "hidden";
	outer.style.width = "100px";
	outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

	document.body.appendChild(outer);

	var widthNoScroll = outer.offsetWidth;
	// force scrollbars
	outer.style.overflow = "scroll";

	// add innerdiv
	var inner = document.createElement("div");
	inner.style.width = "100%";
	outer.appendChild(inner);

	var widthWithScroll = inner.offsetWidth;

	// remove divs
	outer.parentNode.removeChild(outer);

	return widthNoScroll - widthWithScroll;
}

/**
 * Checks if the given analysis type is supported for the specified section.
 * @param {string} type - The analysis type to check.
 * @param {string} [section] - The section to search for the analysis type. If not provided, the default section "#section_analysis" will be used.
 * @returns {boolean} - Returns true if the analysis type is supported for the section, otherwise returns false.
 */
function isAnalysisType(type, section) {
	var analysisType = ANALYSIS_TYPE.valueOf(type), trType = $("table>tbody>tr[data-trick-type]>td>input:checked", section ? section : "#section_analysis").closest("tr").attr("data-trick-type");
	return analysisType && analysisType.isSupported(trType);
}

/**
 * Finds the analysis type for a given section and analysis ID.
 *
 * @param {HTMLElement} section - The section element to search within.
 * @param {string} idAnalysis - The ID of the analysis to find the type for.
 * @returns {string} The analysis type.
 */
function findAnalysisType(section, idAnalysis) {
	if (Object.keys(application.analysisType).length)
		return application.analysisType.type;
	return $("table>tbody>tr[data-trick-id='" + idAnalysis + "'][data-trick-type]", section).attr("data-trick-type");
}

/**
 * Checks if a trick with the given analysisId is archived.
 * If analysisId is undefined, it checks if any trick is archived.
 *
 * @param {string} analysisId - The ID of the trick to check for archiving.
 * @returns {boolean} - True if the trick is archived, false otherwise.
 */
function isArchived(analysisId) {
	return analysisId == undefined ?
		$("table>tbody>tr[data-trick-archived='true']>td>input:checked", "#section_analysis").length > 0 :
		$("table>tbody>tr[data-trick-id='" + analysisId + "'][data-trick-archived='true']", "#section_analysis").length > 0;
}

/**
 * Checks if the selected input is linked.
 * 
 * @returns {boolean} True if the selected input is linked, false otherwise.
 */
function isLinked() {
	return $("tbody>tr input:checked", "#section_analysis").closest("tr").attr("data-is-linked") === "true";
}

/**
 * Downloads a Word report for the given ID.
 * @param {string} id - The ID of the report to download.
 * @returns {boolean} - Returns false to prevent the default link behavior.
 */
function downloadWordReport(id) {
	download(context + '/Account/Report/' + id + "/Download");
	return false;
}

/**
 * Downloads the exported SQLite file for the specified ID.
 * 
 * @param {string} id - The ID of the SQLite file to download.
 * @returns {boolean} - Returns false to prevent the default link behavior.
 */
function downloadExportedSqLite(id) {
	download(context + '/Account/Sqlite/' + id + "/Download");
	return false;
}

/**
 * Downloads a file from the given URL.
 *
 * @param {string} url - The URL of the file to download.
 */
function download(url) {
	if ($.fileDownload) {
		$.fileDownload(url).done((e) => { }).fail((e) => { window.open(url, "_blank_" + Math.random()) });
	} else {
		window.open(url, "_blank_" + Math.random());
	}

}

/**
 * Switches to the specified tab.
 * 
 * @param {string} tabName - The name of the tab to switch to.
 * @returns {boolean} - Returns false to prevent default behavior.
 */
function switchTab(tabName) {
	var $tab = $(tabName ? "a[href='#" + tabName + "']" : "a[data-toggle='tab']:first", ".nav-tab,.nav-analysis");
	if ($tab.parent().css("display") != "none")
		$tab.tab("show");
	return false;
}

/**
 * Checks if an element has a vertical scrollbar.
 *
 * @param {jQuery} element - The jQuery element to check.
 * @returns {boolean} - True if the element has a vertical scrollbar, false otherwise.
 */
function hasScrollBar(element) {
	return element.get(0).scrollHeight > element.get(0).clientHeight;
}

/**
 * Sorts a table based on the specified type and element.
 * 
 * @param {string} type - The type of data to sort by.
 * @param {HTMLElement} element - The element that triggered the sorting.
 * @param {number} number - Indicates whether the sorting is based on numbers.
 * @returns {boolean} - Returns false.
 */
function sortTable(type, element, number) {
	const sorter = natsort({ insensitive: true });
	var previousIndexes = [], $table = $(element).closest("table"), $tbody = $("tbody", $table), $trs = $("tr", $tbody), order = element == undefined ? undefined : element.getAttribute("data-order") == "0" ? -1 : 1, selector = "td[data-trick-field='" + type + "']";
	if ($trs.length && order !== undefined) {
		$trs.each((i, e) => previousIndexes[e] = i);
		$trs.sort((a, b) => {
			let oldA = previousIndexes[a], oldB = previousIndexes[b];
			let value1 = $(selector, a).text(), value2 = $(selector, b).text();
			let result = (number ? sorter(value1.replace(/\s+/g, ""), value2.replace(/\s+/g, "")) : sorter(value1, value2)) * order;
			return !result ? oldA > oldB ? 1 : -1 : result;
		}).detach().appendTo($tbody);
		var $tr = $(element).closest("tr"), $thead = $tr.closest("thead");
		if ($thead.length)
			$thead.find("a[data-order]>.fa").remove();
		else $tr.find("a[data-order]>.fa").remove();
		$tr.find("a[data-order]").attr("data-order", 1);
		element.setAttribute("data-order", order > 0 ? 0 : 1);
		$(order > 0 ? "<i class='fa fa-caret-up' aria-hidden='true' style='margin-left:3px;'/>" : "<i class='fa fa-caret-down' style='margin-left:3px;' aria-hidden='true' />").appendTo(element);
	}
	return false;
}

$.fn.hasAttr = function (name) {
	return this[0].hasAttribute(name);
};

$.fn.hasScrollBar = function () {
	return this.get(0).scrollHeight > this.get(0).clientHeight;
}

$.fn.removeAttributes = function (only, except) {
	if (only) {
		only = $.map(only, function (item) {
			return item.toString().toLowerCase();
		});
	}
	if (except) {
		except = $.map(except, function (item) {
			return item.toString().toLowerCase();
		});
		if (only) {
			only = $.grep(only, function (item, index) {
				return $.inArray(item, except) == -1;
			});
		}
	}
	return this.each(function () {
		var attributes;
		if (!only) {
			attributes = $.map(this.attributes, function (item) {
				return item.name.toString().toLowerCase();
			});
			if (except) {
				attributes = $.grep(attributes, function (item, index) {
					return $.inArray(item, except) == -1;
				});
			}
		} else {
			attributes = only;
		}
		var handle = $(this);
		$.each(attributes, function (index, item) {
			handle.removeAttr(item);
		});
	});
};

/**
 * serializeJSON serialize an object to json string
 * 
 * @param $
 */

$.fn.serializeJSON = function () {
	var json = {};
	var form = $(this);
	form.find('input, select, textarea').each(function () {
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
				json[this.name] = typeof val === 'string' ? [val, this.value] : $.isArray(val) ? $.merge(val, [this.value]) : this.value;
			}
		} else if ("select-multiple" === this.type) {
			json[this.name] = Array.prototype.filter.call(this.options, (option) => {
				return option.selected;
			}).map((option) => {
				return option.value;
			});
		} else
			json[this.name] = this.value;
	});
	return json;
};

/**
 * Converts a form to JSON object.
 *
 * @param {HTMLFormElement} form - The form element to convert.
 * @returns {Object} - The JSON object representing the form data.
 */
function convertFormToJSON(form) {
	return $(form)
		.serializeArray()
		.reduce(function (json, { name, value }) {
			json[name] = value;
			return json;
		}, {});
}


/**
 * Displays a permission error dialog.
 * @returns {void}
 */
function permissionError() {
	return showDialog("#alert-dialog", MessageResolver("error.not_authorized", "Insufficient permissions!"));
}

/**
 * Finds the right analysis based on the provided id.
 *
 * @param {string} idAnalysis - The id of the analysis.
 * @returns {Object|undefined} - The analysis right object if found, otherwise undefined.
 */
function findRight(idAnalysis) {
	var right = $("*[data-trick-id='" + idAnalysis + "'][data-trick-rights-id]");
	if (!right.length)
		return undefined;
	var idRight = $(right).attr('data-trick-rights-id');
	if (!$.trim(idRight).length)
		return undefined;
	for (var key in ANALYSIS_RIGHT)
		if (ANALYSIS_RIGHT[key].value == idRight)
			return ANALYSIS_RIGHT[key];
	return undefined;
}

/**
 * Checks if the user has the specified action right for the given analysis.
 *
 * @param {string} idAnalysis - The ID of the analysis.
 * @param {object} action - The action right to check.
 * @returns {boolean} - Returns true if the user has the specified action right, otherwise false.
 */
function userCan(idAnalysis, action) {
	var right = findRight(idAnalysis);
	if (right != undefined && action.value != undefined) {
		if (application.openMode === OPEN_MODE.READ)
			return action == ANALYSIS_RIGHT.READ
		else
			return right.value <= action.value;
	}
	return false;
}

/**
 * Checks if the user has the right to perform a specific action.
 *
 * @param {string} action - The action to check for.
 * @returns {boolean} - Returns true if the user has the right, false otherwise.
 */
function hasRight(action) {
	if (!(action instanceof jQuery))
		action = ANALYSIS_RIGHT[action];
	return userCan($("#section_analysis tbody>tr>td>input:checked").parent().parent().attr("data-trick-id"), action);
}

/**
 * Checks if the specified analysis ID belongs to the owner.
 * If no ID is provided, it checks if any of the selected analysis IDs belong to the owner.
 *
 * @param {string} [idAnalysis] - The ID of the analysis to check ownership for.
 * @returns {boolean} - Returns true if the analysis belongs to the owner, false otherwise.
 */
function isOwner(idAnalysis) {
	return idAnalysis === undefined ? $("#section_analysis tbody>tr>td>input:checked").parent().parent().attr("data-analysis-owner") === "true" : $("#section_analysis tbody>tr[data-trick-id='" + idAnalysis + "']").attr("data-analysis-owner") === "true"
}

/**
 * Checks if the user can manage access.
 * @returns {boolean} True if the user can manage access, false otherwise.
 */
function canManageAccess() {
	return isOwner() || hasRight("ALL");
}

/**
 * Selects an element and performs an action based on its type.
 *
 * @param {HTMLElement} elm - The element to select.
 * @returns {boolean} - Returns false.
 */
function selectElement(elm) {
	const $input = $(elm).find("input,textarea,select");
	if ($input.length == 1) {
		const $checkbox = $input.filter("input[type='checkbox']:not(:hover):not(:focus)");
		if ($checkbox.length)
			$checkbox[0].click();
	}
	return false;
}

/**
 * Generates a unique message code with the given code and parameters.
 *
 * @param {string} code - The message code.
 * @param {string} params - The parameters for the message.
 * @returns {string} The generated unique message code.
 */
function generateMessageUniqueCode(code, params) {
	return "|^|" + code + "__uPu_*-^|^-*_*+*_+*+_PuP__" + params + "|$|";// mdr
}

/**
 * Resolves a message by generating a unique code and storing it in the application's localesMessages object.
 *
 * @param {string} code - The code associated with the message.
 * @param {string} text - The text of the message.
 * @param {Array} params - An array of parameters to be used in the message.
 */
function resolveMessage(code, text, params) {
	application.localesMessages[generateMessageUniqueCode(code, params)] = he.decode(text);
}


/**
 * Resolves a message based on the provided code, default text, and parameters.
 *
 * @param {string} code - The code of the message.
 * @param {string} defaulttext - The default text of the message.
 * @param {Array|number|string} params - The parameters for the message. Can be an array, a number, or a string.
 * @returns {string} - The resolved message.
 */
function MessageResolver(code, defaulttext, params) {

	var uniqueCode = generateMessageUniqueCode(code, params);

	if (application.localesMessages[uniqueCode] != undefined)
		return application.localesMessages[uniqueCode];
	else
		application.localesMessages[uniqueCode] = defaulttext;

	var data = {
		code: code,
		message: defaulttext,
		parameters: []
	}

	if ($.isArray(params))
		data.parameters = params;
	else if ($.isNumeric(params) || params && params.length)
		data.parameters[0] = params;
	else delete data.parameters;

	$.ajax({
		url: context + "/MessageResolver",
		type: 'post',
		data: JSON.stringify(data),
		async: false,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (!(response.message == undefined || response.message == null || !response.message.length))
				data.message = application.localesMessages[uniqueCode] = response.message;
		}
	});
	return data.message;
}

/**
 * Fixes the table header by making it sticky.
 *
 * @param {HTMLElement[]|jQuery} items - The table(s) to fix the header for.
 * @returns {jQuery} - The jQuery object representing the fixed table header(s).
 */
function fixTableHeader(items) {
	return $(items).stickyTableHeaders(application["settings-fixed-header"]);
}


/**
 * Displays an error message in the specified parent element.
 *
 * @param {HTMLElement} parent - The parent element where the error message will be displayed.
 * @param {string} text - The error message text.
 * @returns {HTMLElement} - The created error element.
 */
function showError(parent, text) {
	var error = document.createElement("div");
	var close = document.createElement("a");
	var content = document.createElement("p");
	close.setAttribute("class", "close");
	close.setAttribute("href", "#");
	close.setAttribute("data-dismiss", "alert");
	error.setAttribute("class", "alert alert-danger");
	error.setAttribute("aria-hidden", "true");
	close.appendChild(document.createTextNode("x"));
	error.appendChild(close);
	content.setAttribute("style", "text-align: left");
	content.appendChild(document.createTextNode(text));
	error.appendChild(content);
	parent.insertBefore(error, parent.firstChild);
	return error;
}

/**
 * Displays a success message in the specified parent element.
 *
 * @param {HTMLElement} parent - The parent element where the success message will be displayed.
 * @param {string} text - The text content of the success message.
 * @returns {HTMLElement} - The created success message element.
 */
function showSuccess(parent, text) {
	var success = document.createElement("div");
	var close = document.createElement("a");
	var content = document.createElement("p");
	close.setAttribute("class", "close");
	close.setAttribute("href", "#");
	close.setAttribute("data-dismiss", "alert");
	success.setAttribute("class", "alert alert-success");
	success.setAttribute("aria-hidden", "true");
	close.appendChild(document.createTextNode("x"));
	success.appendChild(close);
	content.setAttribute("style", "text-align: left");
	content.appendChild(document.createTextNode(text));
	success.appendChild(content);
	parent.insertBefore(success, parent.firstChild);
	return success;
}


/**
 * Checks if a section is selected.
 * 
 * @param {string} sectionName - The name of the section.
 * @returns {boolean} - True if the section is selected, false otherwise.
 */
function isSelected(sectionName) {
	return $("#section_" + sectionName + " tbody tr[data-trick-selected='true'] td:first-child input:checked").length > 0;
}

/**
 * Checks if any row in a specific section has a selected state.
 *
 * @param {string} sectionName - The name of the section.
 * @param {boolean} state - The state to check for (true for selected, false for not selected).
 * @returns {boolean} - True if at least one row in the section has the specified state, false otherwise.
 */
function hasSelectedState(sectionName, state) {
	return $("#section_" + sectionName + " tbody tr[data-trick-selected='" + state + "'] td:first-child input:checked").length > 0;
}

/**
 * Checks or unchecks a checkbox and updates the menu based on the checkbox state.
 * @param {HTMLElement} checkbox - The checkbox element.
 * @param {string} sectionName - The name of the section.
 * @param {string} appModalVar - The variable name of the application modal.
 * @returns {boolean} Returns false.
 */
function checkControlChange(checkbox, sectionName, appModalVar) {
	var items = (appModalVar == undefined || appModalVar == null) ? $("#section_" + sectionName + " tbody tr td:first-child input:not(:disabled)") : $(
		application[appModalVar].modal).find("tbody tr td:first-child input");
	var multiSelectAllowed = ((appModalVar == undefined || appModalVar == null) ? $("#menu_" + sectionName + " li[data-trick-selectable='multi']") : $(
		application[appModalVar].modal).find("#menu_" + sectionName + " li[data-trick-selectable='multi']")).length > 0, $checkbox = $(checkbox);
	if (!multiSelectAllowed) {
		$(checkbox).prop("disabled", true).prop("checked", false);
		return false;
	}
	var isChecked = $checkbox.is(":checked");
	for (var i = 0; i < items.length; i++) {
		var $item = $(items[i]);
		$item.prop("checked", isChecked);
		if (isChecked)
			$item.parent().parent().addClass("selected");
		else
			$item.parent().parent().removeClass("selected");
	}
	updateMenu(undefined, "#section_" + sectionName, "#menu_" + sectionName, appModalVar);
	return false;
}

/**
 * Updates the menu based on the provided parameters.
 *
 * @param {string} sender - The sender of the update.
 * @param {string} idsection - The ID of the section.
 * @param {string} idMenu - The ID of the menu.
 * @param {string} appModalVar - The variable for the application modal.
 * @param {Function} callback - The callback function to be invoked.
 * @returns {boolean} - Returns false.
 */
function updateMenu(sender, idsection, idMenu, appModalVar, callback) {

	var $section, $menu;

	if (!(appModalVar == undefined || appModalVar == null)) {
		$section = $(application[appModalVar].modal);
		$menu = $(idMenu, $section);
	} else {
		$section = $(idsection);
		$menu = $(idsection + " " + idMenu + ", #menu-option " + idMenu);
	}

	if (sender) {
		var $sender = $(sender), items = $("tbody :checked", $section);
		if ($sender.is(":checked")) {
			$sender.closest("tr").addClass("selected")
			var multiSelectNotAllowed = $("li[data-trick-selectable='multi']", $menu).length == 0;
			if (multiSelectNotAllowed) {
				for (var i = 0; i < items.length; i++) {
					var $item = $(items[i]);
					if (sender == $item[0])
						continue;
					$item.prop("checked", false);
					$item.closest("tr").removeClass("selected");
				}
			}
		} else
			$sender.closest("tr").removeClass("selected")
	}

	var cachingChecker = {}, $lis = $("li", $menu), $selectedItems = $("tbody :checked", $section);
	if ($selectedItems.length > 1) {
		for (var i = 0; i < $lis.length; i++) {
			var $liSelected = $($lis[i]), checker = $liSelected.attr("data-trick-check");
			if ($liSelected.attr("data-trick-selectable") === "multi" || $liSelected.attr("data-trick-ignored"))
				$liSelected.removeClass("disabled");
			else
				$liSelected.addClass("disabled");
			updateMenuItemState(cachingChecker, $liSelected, checker);
		}
	} else if ($selectedItems.length == 1) {
		for (var i = 0; i < $lis.length; i++) {
			var $liSelected = $($lis[i]), singleChecker = $liSelected.attr("data-trick-single-check");
			if ($liSelected.attr("data-trick-selectable") != undefined || $liSelected.attr("data-trick-ignored"))
				$liSelected.removeClass("disabled");
			else
				$liSelected.addClass("disabled");

			if (singleChecker !== undefined)
				updateMenuItemState(cachingChecker, $liSelected, singleChecker);
			else
				updateMenuItemState(cachingChecker, $liSelected, $liSelected.attr("data-trick-check"));
		}
	} else {
		for (var i = 0; i < $lis.length; i++) {
			var $liSelected = $($lis[i]);
			if ($liSelected.attr("data-trick-selectable") == undefined)
				$liSelected.removeClass("disabled");
			else
				$liSelected.addClass("disabled");
		}
	}

	if (!(callback === undefined || callback === null))
		invokeCallback(callback);

	var menuCallBack = $menu.attr("data-trick-callback");

	if (!(menuCallBack === undefined || menuCallBack === null))
		invokeCallback(menuCallBack);

	return false;
}

/**
 * Invokes the provided callback function or evaluates the callback string.
 * 
 * @param {Function|string} callback - The callback function or string to be invoked or evaluated.
 */
function invokeCallback(callback) {
	try {
		if ($.isFunction(callback))
			callback();
		else if (window[callback])
			window[callback].apply();
		else
			eval(callback);
	} catch (e) {
		console.log(e);
	}
}

/**
 * Updates the dropdown menu based on the number of available options.
 * If there are options available, the dropdown toggle is enabled.
 * If there are no options available, the dropdown toggle is disabled.
 * 
 * @returns {boolean} Returns false to prevent default behavior.
 */
function updateDropdown() {
	var $menu = $(".nav-dropdown-menu"), $controller = $("a.dropdown-toggle", $menu), $lis = $('ul.dropdown-menu>li:not(.disabled)', $menu);
	if ($lis.length)
		$controller.removeClass("disabled");
	else $controller.addClass("disabled");
	return false;
}

/**
 * Updates the state of a menu item based on a checker function.
 *
 * @param {Object} cachingChecker - An object used to cache the results of checker functions.
 * @param {jQuery} $liSelected - The jQuery object representing the selected menu item.
 * @param {string} checker - The name of the checker function to evaluate.
 */
function updateMenuItemState(cachingChecker, $liSelected, checker) {
	if (checker === undefined)
		return;
	if (!$liSelected.hasClass("disabled")) {
		if (cachingChecker[checker] == undefined)
			cachingChecker[checker] = eval(checker);
		if (!cachingChecker[checker])
			$liSelected.addClass("disabled");
	}
}

/**
 * Serializes a form into a JSON string.
 *
 * @param {string|HTMLElement|jQuery} form - The form element or its ID or jQuery object.
 * @returns {string} - The serialized form data as a JSON string.
 */
function serializeForm(form) {
	var $form = $(form);
	if (!$form.length)
		$form = $("#" + form);
	return JSON.stringify($form.serializeJSON());
}

/**
 * Serializes a form to JSON.
 *
 * @param {string|HTMLElement|jQuery} form - The form element or its ID or a jQuery object representing the form.
 * @returns {string} - The serialized form data in JSON format.
 */
function serializeFormToJson(form) {
	var $form = $(form);
	if (!$form.length)
		$form = $("#" + form);
	return JSON.stringify($form.serializeToJSON());
}

/**
 * Parses the given data as JSON.
 * 
 * @param {any} data - The data to be parsed.
 * @returns {object|boolean|undefined} - The parsed JSON object, false if data is false, or undefined if parsing fails.
 */
function parseJson(data) {
	try {
		if (typeof data == 'object')
			return data;
		else if (data === false)
			return false
		else
			return JSON.parse(data);
	} catch (e) {
		return undefined;
	}
}

/**
 * Logs a message and throws an error asynchronously.
 *
 * @param {string} msg - The message to log.
 * @throws {Error} - The error with the specified message.
 */
function log(msg) {
	setTimeout(function () {
		throw new Error(msg);
	}, 0);
}

/**
 * Finds the selected item IDs by section.
 *
 * @param {string} section - The section to search in.
 * @param {string} [modal] - The modal to search in (optional).
 * @returns {Array<number>|undefined} - An array of selected item IDs, or undefined if trickId is null or undefined.
 */
function findSelectItemIdBySection(section, modal) {
	var selectedItem = [], $items = (modal == null || modal == undefined) ? $("tbody>tr>td>:checked", "#" + section) : $("tbody>tr>td>:checked", modal);
	for (var i = 0; i < $items.length; i++) {
		trickId = findTrickID($items[i]);
		if (trickId == null || trickId == undefined)
			return undefined;
		selectedItem.push(trickId);
	}
	return selectedItem;
}

/**
 * Finds the trick ID associated with the given element.
 * @param {HTMLElement} element - The element to find the trick ID for.
 * @returns {string} The trick ID of the element.
 */
function findTrickID(element) {
	if ($(element).attr("data-trick-id") != undefined)
		return $(element).attr("data-trick-id");
	return $(element).closest("[data-trick-id]").attr("data-trick-id");
}

/**
 * Toggles the tooltip based on the event target.
 *
 * @param {Event} e - The event object.
 * @returns {Event} - The event object.
 */
function toggleToolTip(e) {
	var target = e.target, current = application["settings-open-tooltip"];
	if (!(current == undefined || current.$element == null)) {
		if (target === current.$element[0])
			return e;
		else if ($(current.$tip).is(":visible")) {
			current.hide();
			current.inState.click = false;
		}
	} else
		$(".tooltip.fade.in:visible").remove();
	application["settings-open-tooltip"] = $(target).data("bs.tooltip");
	return e;
}

/**
 * Forces an update of the menu by triggering the change event on the first checkbox in the specified section.
 * If no checkboxes are found, it triggers the change event on the first checkbox in the entire section.
 *
 * @param {jQuery} $section - The jQuery object representing the section containing the checkboxes.
 */
function forceUpdateMenu($section) {
	if (!$("tbody>tr:first input[type='checkbox']", $section).trigger("change").length)
		$("input[type='checkbox']:first", $section).trigger("change");
}

/**
 * Closes the tooltips if the "settings-open-tooltip" is present in the application object.
 */
function closeToolTips() {
	if (application["settings-open-tooltip"]) {
		application["settings-open-tooltip"].hide();
		delete application["settings-open-tooltip"];
	}
}

/**
 * Displays a timeout notification.
 *
 * @param {Object} notification - The notification object.
 * @param {string} message - The notification message.
 * @param {string} title - The notification title.
 */
function displayTimeoutNotification(notification, message, title) {
	if (application['sessionNotification']) {
		application['sessionNotification'].update({
			type: notification.type,
			icon: notification.icon,
			message: message
		});
	} else {
		application['sessionNotification'] = $.notify({
			title: title,
			icon: notification.icon,
			message: message,
		}, {
			type: notification.type,
			z_index: application.notification.z_index,
			offset: application.notification.offset,
			placement: application.notification.placement,
			delay: -1,
			onClose: function () {
				clearTimeout(application['sessionTimerId']);
				delete application['sessionNotification'];
			}
		});
	}
}

/**
 * Displays a timeout warning message with a countdown.
 *
 * @param {number} counter - The initial counter value for the countdown.
 */
function displayTimeoutWarning(counter) {
	var message = MessageResolver("info.session.expire.in.x.seconds", "Your session will be expired in {0} seconds");
	displayTimeoutNotification(NOTIFICATION_TYPE.WARNING, message.replace("{0}", counter))
	application['sessionTimerId'] = setInterval(function () {
		displayTimeoutNotification(NOTIFICATION_TYPE.WARNING, message.replace("{0}", (--counter)))
		if (counter < 2)
			clearTimeout(application['sessionTimerId']);
	}, 1000);
}

/**
 * Forces the closure of tooltips.
 * This function closes any open tooltips and removes any tooltips that are still visible after a short delay.
 */
function forceCloseToolTips() {
	closeToolTips();
	setTimeout(function () {
		$(".tooltip.fade.in:visible").remove();
	}, 100);
}

/**
 * Generates helper elements for the given selection.
 * @param {jQuery} $selection - The jQuery selection of elements to generate helpers for.
 * @param {string} [container='body'] - The container element where the helpers will be appended.
 * @returns {boolean} - Returns false.
 */
function generateHelper($selection, container) {
	if (container == undefined || container == null)
		container = "body";
	if ($selection == undefined || $selection == null)
		$selection = $("[data-helper-content]", container);
	$selection
		.each(function () {
			var $this = $(this), placement = $this.attr("data-helper-placement"), title = $this.attr("data-helper-content"), $helper = $("<span data-trigger='hover focus' class='helper'><i class='fa fa-info'/></span>");
			$this.removeAttr("data-helper-content");
			if (title == "" || title == undefined) {
				title = $this.attr("title");
				if (title == "" || title == undefined)
					return false;
			}
			if (placement == undefined || placement == "")
				placement = "auto right";

			$helper.attr("data-content", title).attr("data-placement", placement).appendTo($this).popover({
				'container': container
			});
		});
	return false;
}

/**
 * Updates the URL of the user guide link in the footer.
 * @returns {boolean} Returns false.
 */
function updateUserGuideURL() {
	try {
		var $this = $("#footer a[data-base-url]"), baseURL = $this.attr("data-base-url");
		var $container = $("[data-ug-root]"), $tabActive = $(".tab-pane.active", $container);
		if (!$container.length || !$this.length)
			return false;
		baseURL += ("#" + $container.attr("data-ug-root"));
		if ($tabActive.length)
			baseURL += $tabActive.attr("id").substr(3);
		$this.attr("href", baseURL);
	} catch (error) {
		console.log(error);
	}
	return false;
}

$(document)
	.ready(
		function () {
			var token = $("meta[name='_csrf']").attr("content"), $bodyHtml = $('body,html'), header = $("meta[name='_csrf_header']").attr("content"), $tabNav = $("ul.nav-tab,ul.nav-analysis"), $window = $(window);

			$(document).ajaxSend(function (e, xhr, options) {
				if (options.url !== (context + '/IsAuthenticate'))
					$(document).trigger("session:resquest:send");
				xhr.setRequestHeader(header, token);
			}).idle(
				{
					idle: application.timeoutSetting.idle,
					sessionTimeout: application.timeoutSetting.sessionTimeout,
					refreshTime: application.timeoutSetting.refreshTime,
					idleRefreshTime: application.timeoutSetting.idleRefreshTime,
					onIdle: function () {
						displayTimeoutWarning((application.timeoutSetting.sessionTimeout - application.timeoutSetting.idle) / 1000);
					},
					onRefreshSession: function () {
						$.get(context + '/IsAuthenticate', function (isAuthenticated) {
							if (isAuthenticated === false)
								$(document).trigger("session.timeout");
						});
					},
					onSessionTimeout: function () {
						displayTimeoutNotification(NOTIFICATION_TYPE.ERROR, MessageResolver("error.session.expired",
							"Your session has been expired, you will be redirected to the login page in few seconds."));
						setTimeout(function () {
							$.get(context + '/IsAuthenticate', function (isAuthenticated) {
								if (isAuthenticated === false)
									location.reload();
								else
									displayTimeoutNotification(NOTIFICATION_TYPE.ERROR, MessageResolver("error.session.expire.monitor",
										"It seems you have many tabs opened on TS, Session timeout monitoring is not supported that, it is now disabled."));
							});
						}, 10000);
					},
					onActive: function () {
						if (application['sessionNotification']) {
							application['sessionNotification'].close();
						}
					},
					onIdleRefreshTime: function () {
						showDialog("info", MessageResolver("info.session.expire.in.x.minutes", "Your session will be expired in {0} minute(s)").replace("{0}",
							application.timeoutSetting.sessionTimeout / 60000))
					}
				});


			if ($tabNav.length) {

				var $tabContainer = $("#tab-container").length ? $("#tab-container") : $("#nav-container"), $option = $tabContainer.find("#menu-option");
				$window.on("resize.window", function () {
					$tabContainer.css({
						"margin-top": $tabNav.height() + 10
						// default margin-top is 50px and default $tabNav
						// size is 38px
					});
				});

				if ($option.length) {
					var updateOption = function () {
						var optionMenu = $tabContainer.find(".tab-pane.active ul.nav.nav-pills:visible");
						var tableFloatingHeader = $tabContainer.find(".tab-pane.active table .tableFloatingHeader");
						if (!optionMenu.length || !tableFloatingHeader.length || !tableFloatingHeader.is(":visible"))
							$option.fadeOut(function () {
								$option.hide();
							});
						else {
							if (!$option.find("#" + optionMenu.prop("id")).length) {
								$option.find("ul").remove();
								var cloneOption = optionMenu.clone(), $subMenu = $("li.dropdown-submenu", cloneOption);
								$("li[data-role='title']", cloneOption).remove()
								cloneOption.removeAttr("style");
								if ($subMenu.length) {
									$subMenu.each(function () {
										var $this = $(this), text = $("a.dropdown-toggle", $this).text(), $lis = $("ul.dropdown-menu>li", $this);
										$this.removeClass();
										if ($this.closest("li").length)
											$this.before("<li class='divider'></li>");
										$lis.appendTo(cloneOption);
										$this.text(text);
										$this.addClass("dropdown-header");
									});
								} else {
									$("li.dropdown-header", cloneOption).each(function () {
										var $this = $(this), $closestli = $this.closest("li");
										if ($closestli.length && !$closestli.hasClass("divider"))
											$this.before("<li class='divider'></li>");
										$this.show();
									});
								}
								$("li.divider", cloneOption).show();
								cloneOption.appendTo($option);
								cloneOption.removeClass();
								cloneOption.find("li").removeClass("pull-right")
								cloneOption.addClass("dropdown-menu dropdown-menu-right")
							}

							if (!$option.is(":visible")) {
								$option.fadeIn(function () {
									$option.show();
								});
							}
						}
					}

					$window.on("scroll.window", function () {
						setTimeout(updateOption, 100);
					});
				}


				// prevent perform click while a menu is disabled
				$("ul.nav li>a").on("click", function (e) {
					if ($(e.currentTarget).parent().hasClass("disabled"))
						e.preventDefault();
				});

				// prevent perform click while a menu is disabled
				$("ul.nav li").on("click", function (e) {
					if ($(e.currentTarget).hasClass("disabled"))
						e.stopPropagation();
				});

				// prevent unknown error modal display
				$window.bind("beforeunload", function () {
					application["isReloading"] = true;
				});

				$(".dropdown-submenu").on("hide.bs.dropdown", function (e) {
					var $target = $(e.currentTarget);
					if ($target.find("li.active").length && !$target.hasClass("active"))
						$target.addClass("active");
				});

				$('.dropdown-submenu a[data-toggle="tab"]', $tabNav).on('shown.bs.tab', function (e) {
					var $parent = $(e.target).closest("li.dropdown-submenu");
					if (!$parent.hasClass("active"))
						$parent.addClass("active");
				});

				$("a[data-toggle='taskmanager']").on("click", function (e) { // task
					// manager
					var taksmanager = application['taskManager'];
					if (taksmanager.isEmpty())
						return false;
					var $target = $(e.currentTarget), $parent = $target.parent();
					if ($parent.hasClass("open"))
						taksmanager.Hide();
					else
						taksmanager.Show();
				});



				$window.on('hashchange', function () {
					var hash = window.location.hash;
					application["no-update-hash"] = true;
					switchTab(hash ? hash.split('#')[1] : hash);
					application["no-update-hash"] = false;
					updateUserGuideURL();
				});

				if (window.location.hash)
					$window.trigger("hashchange");

				$('[data-toggle="tooltip"]').tooltip().on('show.bs.tooltip', toggleToolTip);

				$window.keydown(function (e) {
					if (e.keyCode == 27)
						forceCloseToolTips();
				});

				$('a[data-toggle="tab"]', $tabNav).on('shown.bs.tab', function (e) {
					forceCloseToolTips();
					var hash = e.target.getAttribute("href"), $target = $(hash);
					callBackCaller($target);
					triggerCaller($target);
					if (!application["no-update-hash"])
						window.location.hash = $target.attr("id");
					$bodyHtml.animate({
						scrollTop: 0
					}, 300);
				});
			}

			$('#confirm-dialog').on('hidden.bs.modal', function () {
				$("#confirm-dialog .btn-danger").unbind("click");
			});

			$('#alert-dialog').on('hidden.bs.modal', function () {
				$("#alert-dialog .btn-danger").unbind("click");
			});

			if (window.location.hash != undefined)
				$('a[data-toggle="tab"][href="' + window.location.hash + '"]', $tabNav).trigger("shown.bs.tab");

			setTimeout(() => {
				generateHelper();
				onElementInserted("modal", (element) => generateHelper(undefined, element));
				updateUserGuideURL();
			}, 100);

			// load notification.
			$("#controller-notifications div[data-notification-type]").each(function () {
				var id = this.id, type = this.getAttribute("data-type");
				if (type === null || type === undefined)
					showDialog(this.getAttribute("data-notification-type"), this.innerText);
				else {
					application.currentNotifications[id] = showStaticDialog(this.getAttribute("data-notification-type"), this.innerText, undefined, undefined, (e) => {
						application['taskManager'].Remove(id);
					});
				}
				this.parentNode.removeChild(this);
			});

			$("#logout-form").on("submit", (e) => application["taskManager"].Disconnect());

			// Prevent click on disabled menus
			$("li>a,a").on("click", (e) => {
				var parent = e.currentTarget.closest("li");
				if (e.currentTarget.classList.contains("disabled") || parent && parent.classList.contains("disabled")) {
					e.preventDefault();
					e.stopPropagation();
					return false;
				}
			});

		});