/**
 * Main.js
 */
var application = new Application();

function Application() {
	this.modal = {};
	this.data = {};
	this.rights = {}
	this.localesMessages = {};
	this.fixedOffset = 0
	this.shownScrollTop = true;
	this.analysisType = '';
	this.errorTemplate = '<div class="popover popover-danger" role="tooltip"><div class="arrow"></div><div class="popover-content"></div></div>';
	this.timeoutSetting = {
		idle : 720000,
		sessionTimeout : 900000,
		refreshTime : 300000,
		idleRefreshTime : 180000,
	}
	this.notification = {
		z_index : 1068,
		offset : {
			x : 0,
			y : 35
		},
		placement : {
			from : "bottom",
			align : "right"
		},
		delay : 5000
	}
	this.currencyFormat = new Intl.NumberFormat("fr-FR", {style: "currency", currency: "EUR", maximumFractionDigits:0, minimumFractionDigits:0});
	this.numberFormat = new Intl.NumberFormat("fr-FR");
	this.numberFormatNoDecimal = new Intl.NumberFormat("fr-FR",{maximumFractionDigits:0, minimumFractionDigits:0});
	this.percentageFormat = new Intl.NumberFormat("fr-FR", {style: "percent", maximumFractionDigits:0, minimumFractionDigits:0});
}

/**
 * Analysis rights / user permissions
 */

var ANALYSIS_RIGHT = {
	ALL : {
		value : 0,
		name : "ALL"
	},
	EXPORT : {
		value : 1,
		name : "EXPORT"
	},
	MODIFY : {
		value : 2,
		name : "MODIFY"
	},
	READ : {
		value : 3,
		name : "READ"
	}
};

/**
 * Open mode
 */
var OPEN_MODE = {
	READ : {
		value : "read-only",
		name : "READ"
	},
	READ_ESTIMATION : {
		value : "read-only-estimation",
		name : "READ_ESTIMATION"
	},
	EDIT : {
		value : "edit",
		name : "EDIT"
	},
	EDIT_ESTIMATION : {
		value : "edit-estimation",
		name : "EDIT_ESTIMATION"
	},
	EDIT_MEASURE : {
		value : "edit-measure",
		name : "EDIT_MEASURE"
	},
	isReadOnly : function() {
		return application.openMode && application.openMode.value.startsWith("read-only");
	},
	valueOf : function(value) {
		for ( var key in OPEN_MODE) {
			if (key == "valueOf")
				continue;
			else if (OPEN_MODE[key] == value || OPEN_MODE[key].value == value || OPEN_MODE[key].name == value)
				return OPEN_MODE[key];
		}
		return undefined;
	}
}

var NOTIFICATION_TYPE = {
	ERROR : {
		type : "danger",
		icon : "glyphicon glyphicon-warning-sign",
		names : [ "error", "#alert-dialog", "#danger-dialog", "alert-dialog", "danger-dialog", "danger", "alert" ]
	},
	WARNING : {
		type : "warning",
		icon : "glyphicon glyphicon-exclamation-sign",
		names : [ "#warning-dialog", "warning-dialog", "warning" ]
	},
	INFO : {
		type : "info",
		icon : "glyphicon glyphicon-info-sign",
		names : [ "#info-dialog", "info-dialog", "info" ]
	},
	SUCCESS : {
		type : "success",
		icon : "glyphicon glyphicon-ok-sign",
		names : [ "#success-dialog", "success-dialog", "success" ]
	},
	valueOf : function(value) {
		for ( var key in NOTIFICATION_TYPE) {
			if (key == "valueOf")
				continue;
			else if (NOTIFICATION_TYPE[key] == value || NOTIFICATION_TYPE[key].type == value || NOTIFICATION_TYPE[key].names.indexOf(value) != -1)
				return NOTIFICATION_TYPE[key];
		}
		return undefined;
	}
}

if (!String.prototype.capitalize) {
	String.prototype.capitalize = function() {
		return this.charAt(0).toUpperCase() + this.slice(1);
	}
}

if (!String.prototype.startsWith) {
	String.prototype.startsWith = function(searchString, position) {
		position = position || 0;
		return this.substr(position, searchString.length) === searchString;
	};
}

function isFunction(value) {
	 var getType = {};
	 return value && getType.toString.call(value) === '[object Function]';
}

if (!String.prototype.endsWith) {
	String.prototype.endsWith = function(searchString, position) {
		var subjectString = this.toString();
		if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
			position = subjectString.length;
		}
		position -= searchString.length;
		var lastIndex = subjectString.indexOf(searchString, position);
		return lastIndex !== -1 && lastIndex === position;
	};
}

function checkExtention(value, extention, button) {
	var extentions = extention.split(","), match = false;
	for (var i = 0; i < extentions.length; i++)
		match |= value.endsWith(extentions[i]);
	$(button).prop("disabled", !match);
	return match;
}

function switchLangueTo(url){
	if (window.location.hash == undefined)
		return true;
	window.location.replace(url + window.location.hash);
	return false;
}

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

function showDialog(dialog, message, title, url) {
	var notificationType = NOTIFICATION_TYPE.valueOf(dialog);
	if (notificationType == undefined) {
		var $dialog = $(dialog), $modalBody = $dialog.find(".modal-body").text(message);
		return $dialog.modal("show");
	} else {
		return showNotifcation(notificationType.type, message, notificationType.icon, title, url);
	}
}

function showStaticDialog(dialog, message, title, url) {
	var notificationType = NOTIFICATION_TYPE.valueOf(dialog);
	if (notificationType == undefined) {
		var $dialog = $(dialog), $modalBody = $dialog.find(".modal-body").text(message);
		return $dialog.modal("show");
	} else {
		return showStaticNotifcation(notificationType.type, message, notificationType.icon, title, url);
	}
}

function showNotifcation(type, message, icon, url, title) {
	return $.notify({
		title : title,
		icon : icon,
		message : message,
		url : url
	}, {
		type : type,
		z_index : application.notification.z_index,
		offset : application.notification.offset,
		placement : application.notification.placement,
		delay : application.notification.delay
	});
}

function showStaticNotifcation(type, message, icon, url, title) {
	return $.notify({
		title : title,
		icon : icon,
		message : message,
		url : url
	}, {
		type : type,
		z_index : application.notification.z_index,
		offset : application.notification.offset,
		placement : application.notification.placement,
		delay : -1
	});
}

function unknowError(jqXHR, textStatus, errorThrown) {
	if (typeof textStatus != 'undefined' && textStatus === 'abort' || application["isReloading"])
		return false;
	if (jqXHR != undefined) {
		if (textStatus == "timeout" || textStatus == "error" && jqXHR.readyState == 0)
			showDialog("#alert-dialog", MessageResolver("error.timeout", "The server may be down, overloaded, or there may be too much net traffic."));
		else if (jqXHR.readyState == 4 && textStatus == "error") {
			switch (errorThrown) {
			case "Forbidden":
				showDialog("#alert-dialog", MessageResolver("error.forbidden", "Action is not allowed, no analysis selected or you are no longer logged in"));
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

function downloadWordReport(id) {
	window.location = context + '/Profile/Report/' + id + "/Download";
	return false;
}

function downloadExportedSqLite(id) {
	window.location = context + '/Profile/Sqlite/' + id + "/Download";
	return false;
}

function switchTab(tabName) {
	var $tab = $(tabName ? "a[href='#" + tabName + "']" : "a[data-toggle='tab']:first", ".nav-tab,.nav-analysis");
	if ($tab.parent().css("display") != "none")
		$tab.tab("show");
	return false;
}

function hasScrollBar(element) {
	return element.get(0).scrollHeight > element.get(0).clientHeight;
}

$.fn.hasAttr = function(name) {
	return this[0].hasAttribute(name);
};

$.fn.hasScrollBar = function() {
	return this.get(0).scrollHeight > this.get(0).clientHeight;
}

$.fn.removeAttributes = function(only, except) {
	if (only) {
		only = $.map(only, function(item) {
			return item.toString().toLowerCase();
		});
	}
	if (except) {
		except = $.map(except, function(item) {
			return item.toString().toLowerCase();
		});
		if (only) {
			only = $.grep(only, function(item, index) {
				return $.inArray(item, except) == -1;
			});
		}
	}
	return this.each(function() {
		var attributes;
		if (!only) {
			attributes = $.map(this.attributes, function(item) {
				return item.name.toString().toLowerCase();
			});
			if (except) {
				attributes = $.grep(attributes, function(item, index) {
					return $.inArray(item, except) == -1;
				});
			}
		} else {
			attributes = only;
		}
		var handle = $(this);
		$.each(attributes, function(index, item) {
			handle.removeAttr(item);
		});
	});
};

/**
 * serializeJSON serialize an object to json string
 * 
 * @param $
 */

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
		} else if("select-multiple" === this.type) {
			json[this.name] = Array.prototype.filter.call(this.options, (option)=> {
		        return option.selected;
		    }).map((option)=> {
		        return option.value;
		    });
		}else
			json[this.name] = this.value;
	});
	return json;
};

function permissionError() {
	return showDialog("#alert-dialog", MessageResolver("error.not_authorized", "Insufficient permissions!"));
}

function findRight(idAnalysis) {
	var right = $("*[data-trick-id='" + idAnalysis + "'][data-trick-rights-id]");
	if (!right.length)
		return undefined;
	var idRight = $(right).attr('data-trick-rights-id');
	if (!$.trim(idRight).length)
		return undefined;
	for ( var key in ANALYSIS_RIGHT)
		if (ANALYSIS_RIGHT[key].value == idRight)
			return ANALYSIS_RIGHT[key];
	return undefined;
}

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

function hasRight(action) {
	if (!(action instanceof jQuery))
		action = ANALYSIS_RIGHT[action];
	return userCan($("#section_analysis tbody>tr>td>input:checked").parent().parent().attr("data-trick-id"), action);
}

function canManageAccess() {
	return $("#section_analysis tbody>tr>td>input:checked").parent().parent().attr("data-analysis-owner") == "true" || hasRight("ALL");
}

function selectElement(element) {
	var $input = $(element).find("input,textarea,select");
	if ($input.length == 1)
		$input.filter("input[type='checkbox']:not(:hover):not(:focus)").trigger("click");
	return false;
}

function generateMessageUniqueCode(code, params) {
	return "|^|" + code + "__uPu_*-^|^-*_*+*_+*+_PuP__" + params + "|$|";// mdr
}

function resolveMessage(code, text, params) {
	application.localesMessages[generateMessageUniqueCode(code, params)] = text;
}

/**
 * MessageResolver
 * 
 * @param code
 * @param defaulttext
 * @param params
 * @returns
 */
function MessageResolver(code, defaulttext, params) {

	var uniqueCode = generateMessageUniqueCode(code, params);

	if (application.localesMessages[uniqueCode] != undefined)
		return application.localesMessages[uniqueCode];
	else
		application.localesMessages[uniqueCode] = defaulttext;

	var data = {
		code : code,
		message : defaulttext,
		parameters : []
	}
	
	if ($.isArray(params))
		data.parameters = params;
	else if (params && params.length)
		data.parameters[0] = params;
	else delete data.parameters;
	
	$.ajax({
		url : context + "/MessageResolver",
		type : 'post',
		data : JSON.stringify(data),
		async : false,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (!(response.message == undefined || response.message == null || !response.message.length))
				data.message = application.localesMessages[uniqueCode] = response.message;
		}
	});
	return data.message;
}

function fixTableHeader(items) {
	return $(items).stickyTableHeaders(application["settings-fixed-header"]);
}

/**
 * success and error message display
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
 * section menu update
 */

function isSelected(sectionName) {
	return $("#section_" + sectionName + " tbody tr[data-trick-selected='true'] td:first-child input:checked").length > 0;
}

function hasSelectedState(sectionName, state) {
	return $("#section_" + sectionName + " tbody tr[data-trick-selected='" + state + "'] td:first-child input:checked").length > 0;
}

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

function updateMenu(sender, idsection, idMenu, appModalVar, callback) {
	if (sender) {
		var $sender = $(sender);
		if ($sender.is(":checked")) {
			$sender.closest("tr").addClass("selected")
			var multiSelectNotAllowed = ((appModalVar == undefined || appModalVar == null) ? $("li[data-trick-selectable='multi']", idMenu) : $(idMenu
					+ " li[data-trick-selectable='multi']", application[appModalVar].modal)).length == 0;
			if (multiSelectNotAllowed) {
				var items = $("tbody :checked", ((appModalVar == undefined || appModalVar == null) ? idsection : application[appModalVar].modal));
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

	var checkedCount = ((appModalVar == undefined || appModalVar == null) ? $(idsection + " tbody :checked") : $(application[appModalVar].modal).find("tbody :checked")).length, cachingChecker = {};
	if (checkedCount > 1) {
		var $lis = (appModalVar == undefined || appModalVar == null) ? $(idMenu + " li") : $(application[appModalVar].modal).find(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			var $liSelected = $($lis[i]), checker = $liSelected.attr("data-trick-check");
			if ($liSelected.attr("data-trick-selectable") === "multi" || $liSelected.attr("data-trick-ignored"))
				$liSelected.removeClass("disabled");
			else
				$liSelected.addClass("disabled");
			updateMenuItemState(cachingChecker, $liSelected, checker);
		}
	} else if (checkedCount == 1) {
		var $lis = (appModalVar == undefined || appModalVar == null) ? $(idMenu + " li") : $(application[appModalVar].modal).find(idMenu + " li");
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
		var $lis = (appModalVar == undefined || appModalVar == null) ? $(idMenu + " li") : $(application[appModalVar].modal).find(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			var $liSelected = $($lis[i]);
			if ($liSelected.attr("data-trick-selectable") == undefined)
				$liSelected.removeClass("disabled");
			else
				$liSelected.addClass("disabled");
		}
	}

	if (callback != undefined) {
		try {
			if ($.isFunction(callback))
				callback();
			else
				eval(callback);
		} catch (e) {
			console.log(idsection);
			console.log(e);
		}
	}
	return false;
}

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
 * asynchronous task feedback
 */

function cancelTask(taskId) {
	$.ajax({
		url : context + "/Task/Stop/" + taskId,
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(reponse) {
			$("#task_" + taskId).remove();
		},
		error : unknowError
	});
}

function updateStatus(progressBar, idTask, callback, status) {
	if (status == null || status == undefined) {
		$.ajax({
			url : context + "/Task/Status/" + idTask,
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				if (reponse.flag == undefined) {
					eval(callback.failed);
					return false;
				}
				return updateStatus(progressBar, idTask, callback, reponse);
			},
			error : unknowError
		});
	} else {
		if (status.message != null)
			progressBar.Update(status.progress, status.message);
		if (status.flag == 3) {
			setTimeout(function() {
				updateStatus(progressBar, idTask, callback);
			}, 1500);
		} else {
			$(progressBar.progress).parent().parent().find("button").each(function() {
				$(this).removeAttr("disabled");
			});
			if (callback.success != undefined)
				eval(callback.success);
			else if (status.asyncCallback != undefined && status.asyncCallback != null)
				eval(status.asyncCallback.action);
			else if (status.taskName != null && status.taskName != undefined)
				eval(status.taskName.action);
		}
	}
	return false;
}

function serializeForm(form) {
	var $form = $(form);
	if (!$form.length)
		$form = $("#" + form);
	return JSON.stringify($form.serializeJSON());
}

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

function log(msg) {
	setTimeout(function() {
		throw new Error(msg);
	}, 0);
}

function findSelectItemIdBySection(section, modal) {
	var selectedItem = [], $items = (modal == null || modal == undefined) ? $("#" + section + " tbody :checked") : $("tbody :checked", modal);
	for (var i = 0; i < $items.length; i++) {
		trickId = findTrickID($items[i]);
		if (trickId == null || trickId == undefined)
			return undefined;
		selectedItem.push(trickId);
	}
	return selectedItem;
}

function findTrickID(element) {
	if ($(element).attr("data-trick-id") != undefined)
		return $(element).attr("data-trick-id");
	return $(element).closest("[data-trick-id]").attr("data-trick-id");
}

function versionComparator(version1, version2) {
	var values1 = version1.split("\\.", 2);
	var values2 = version2.split("\\.", 2);

	var vers1 = "";

	var vers2 = "";

	for (var i = 0; i < values1.length; i++)
		vers1 += values1[i];

	for (var i = 0; i < values2.length; i++)
		vers2 += values2[i];

	console.log(vers1 + "::" + vers2);

	return vers1 > vers2 ? 1 : -1;
}

function oldversionComparator(version1, version2) {
	var values1 = version1.split("\\.", 2);
	var values2 = version2.split("\\.", 2);
	var value1 = parseInt(values1[0]);
	var value2 = parseInt(values2[0]);
	if (value1 == value2) {
		if (values1.length == 1 && values2.length == 1)
			return 0;
		else if (values1.length == 1 && values2.length > 1)
			return -1;
		else if (values1.length > 1 && values2.length == 1)
			return 1;
		else
			return versionComparator(values1[1], values2[1]);
	} else
		return value1 > value2 ? 1 : -1;
}

function toggleToolTip(e) {
	var target = e.target, current = application["settings-open-tooltip"];
	if (current != undefined) {
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

function forceUpdateMenu($section) {
	if (!$("tbody>tr:first input[type='checkbox']", $section).trigger("change").length)
		$("input[type='checkbox']:first", $section).trigger("change");
}

function closeToolTips() {
	if (application["settings-open-tooltip"]) {
		application["settings-open-tooltip"].hide();
		delete application["settings-open-tooltip"];
	}
}

function displayTimeoutNotification(notification, message, title) {
	if (application['sessionNotification']) {
		application['sessionNotification'].update({
			type : notification.type,
			icon : notification.icon,
			message : message
		});
	} else {
		application['sessionNotification'] = $.notify({
			title : title,
			icon : notification.icon,
			message : message,
		}, {
			type : notification.type,
			z_index : application.notification.z_index,
			offset : application.notification.offset,
			placement : application.notification.placement,
			delay : -1,
			onClose : function() {
				clearTimeout(application['sessionTimerId']);
				delete application['sessionNotification'];
			}
		});
	}
}

function displayTimeoutWarning(counter) {
	var message = MessageResolver("info.session.expire.in.x.seconds", "Your session will be expired in {0} seconds");
	displayTimeoutNotification(NOTIFICATION_TYPE.WARNING, message.replace("{0}", counter))
	application['sessionTimerId'] = setInterval(function() {
		displayTimeoutNotification(NOTIFICATION_TYPE.WARNING, message.replace("{0}", (--counter)))
		if (counter < 2)
			clearTimeout(application['sessionTimerId']);
	}, 1000);
}

function forceCloseToolTips() {
	closeToolTips();
	setTimeout(function() {
		$(".tooltip.fade.in:visible").remove();
	}, 100);
}

function generateHelper($selection, container) {
	if ($selection == undefined || $selection == null)
		$selection = $("[data-helper-content]");
	if (container == undefined || container == null)
		container = "body";
	$selection
			.each(function() {
				var $this = $(this), placement = $this.attr("data-helper-placement"), title = $this.attr("data-helper-content"), $helper = $("<span data-trigger='hover focus' class='helper'><i class='fa fa-info'/></span>");
				if (title == "" || title == undefined) {
					title = $this.attr("title");
					if (title == "" || title == undefined)
						return false;
				}

				if (placement == undefined || placement == "")
					placement = "auto right";

				$helper.attr("data-content", title).attr("data-placement", placement).appendTo($this).popover({
					'container' : container
				});
			});
	return false;
}

$(document)
		.ready(
				function() {
					var token = $("meta[name='_csrf']").attr("content"), $bodyHtml = $('body,html'), header = $("meta[name='_csrf_header']").attr("content"), $tabNav = $("ul.nav-tab,ul.nav-analysis"), $window = $(window);

					$(document).ajaxSend(function(e, xhr, options) {
						if (options.url !== (context + '/IsAuthenticate'))
							$(document).trigger("session:resquest:send");
						xhr.setRequestHeader(header, token);
					}).idle(
							{
								idle : application.timeoutSetting.idle,
								sessionTimeout : application.timeoutSetting.sessionTimeout,
								refreshTime : application.timeoutSetting.refreshTime,
								idleRefreshTime : application.timeoutSetting.idleRefreshTime,
								onIdle : function() {
									displayTimeoutWarning((application.timeoutSetting.sessionTimeout - application.timeoutSetting.idle) / 1000);
								},
								onRefreshSession : function() {
									$.get(context + '/IsAuthenticate', function(isAuthenticated) {
										if (isAuthenticated === false)
											$(document).trigger("session.timeout");
									});
								},
								onSessionTimeout : function() {
									displayTimeoutNotification(NOTIFICATION_TYPE.ERROR, MessageResolver("error.session.expired",
											"Your session has been expired, you will be redirected to the login page in few seconds."));
									setTimeout(function() {
										$.get(context + '/IsAuthenticate', function(isAuthenticated) {
											if (isAuthenticated === false)
												location.reload();
											else
												displayTimeoutNotification(NOTIFICATION_TYPE.ERROR, MessageResolver("error.session.expire.monitor",
														"It seems you have many tabs opened on TS, Session timeout monitoring is not supported that, it is now disabled."));
										});
									}, 10000);
								},
								onActive : function() {
									if (application['sessionNotification']) {
										application['sessionNotification'].close();
									}
								},
								onIdleRefreshTime : function() {
									showDialog("info", MessageResolver("info.session.expire.in.x.minutes", "Your session will be expired in {0} minute(s)").replace("{0}",
											application.timeoutSetting.sessionTimeout / 60000))
								}
							});

					// prevent perform click while a menu is disabled
					$("ul.nav li>a").on("click", function(e) {
						if ($(e.currentTarget).parent().hasClass("disabled"))
							e.preventDefault();
					});

					// prevent perform click while a menu is disabled
					$("ul.nav li").on("click", function(e) {
						if ($(e.currentTarget).hasClass("disabled"))
							e.stopPropagation();
					});

					// prevent unknown error modal display
					$window.bind("beforeunload", function() {
						application["isReloading"] = true;
					});

					$(".dropdown-submenu").on("hide.bs.dropdown", function(e) {
						var $target = $(e.currentTarget);
						if ($target.find("li.active").length && !$target.hasClass("active"))
							$target.addClass("active");
					});

					$('.dropdown-submenu a[data-toggle="tab"]', $tabNav).on('shown.bs.tab', function(e) {
						var $parent = $(e.target).closest("li.dropdown-submenu");
						if (!$parent.hasClass("active"))
							$parent.addClass("active");
					});

					$("a[data-toggle='taskmanager']").on("click", function(e) { // task
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

					$('#confirm-dialog').on('hidden.bs.modal', function() {
						$("#confirm-dialog .btn-danger").unbind("click");
					});

					$('#alert-dialog').on('hidden.bs.modal', function() {
						$("#alert-dialog .btn-danger").unbind("click");
					});

					if ($tabNav.length) {

						var $tabContainer = $("#tab-container").length ? $("#tab-container") : $("#nav-container"), $option = $tabNav.find("#tabOption");
						$window.on("resize.window", function() {
							$tabContainer.css({
								"margin-top" : $tabNav.height() + 10
							// default margin-top is 50px and default $tabNav
							// size is 38px
							});
						});

						if ($option.length) {
							var updateOption = function() {
								var optionMenu = $tabContainer.find(".tab-pane.active ul.nav.nav-pills:visible");
								var tableFloatingHeader = $tabContainer.find(".tab-pane.active table .tableFloatingHeader");
								if (!optionMenu.length || !tableFloatingHeader.length || !tableFloatingHeader.is(":visible"))
									$option.fadeOut(function() {
										$option.hide();
									});
								else {
									if (!$option.find("#" + optionMenu.prop("id")).length) {
										$option.find("ul").remove();
										var cloneOption = optionMenu.clone(), $subMenu = $("li.dropdown-submenu", cloneOption);
										$("li[data-role='title']", cloneOption).remove()
										cloneOption.removeAttr("style");
										if ($subMenu.length) {
											$subMenu.each(function() {
												var $this = $(this), text = $("a.dropdown-toggle", $this).text(), $lis = $("ul.dropdown-menu>li", $this);
												$this.removeClass();
												if ($this.closest("li").length)
													$this.before("<li class='divider'></li>");
												$lis.appendTo(cloneOption);
												$this.text(text);
												$this.addClass("dropdown-header");
											});
										} else {
											$("li.dropdown-header", cloneOption).each(function() {
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
										cloneOption.addClass("dropdown-menu")
									}

									if (!$option.is(":visible")) {
										$option.fadeIn(function() {
											$option.show();
										});
									}
								}
							}

							$window.on("scroll.window", function() {
								setTimeout(updateOption, 100);
							});
						}

						$window.on('hashchange', function() {
							var hash = window.location.hash;
							application["no-update-hash"] = true;
							switchTab(hash ? hash.split('#')[1] : hash);
							application["no-update-hash"] = false;
						});

						if (window.location.hash) {
							$window.trigger("hashchange");
							$bodyHtml.animate({
								scrollTop : 0
							}, 20);
						}

						$('a[data-toggle="tab"]', $tabNav).on('shown.bs.tab', function(e) {

							forceCloseToolTips();

							if (application.shownScrollTop) {
								$bodyHtml.animate({
									scrollTop : 0
								}, 20);
							}

							var hash = e.target.getAttribute("href"), $target = $(hash);

							callBackCaller($target);
							triggerCaller($target);

							if (!application["no-update-hash"])
								window.location.hash = $target.attr("id");
						});
					}

					$('[data-toggle="tooltip"]').tooltip().on('show.bs.tooltip', toggleToolTip);

					$window.keydown(function(e) {
						if (e.keyCode == 27)
							forceCloseToolTips();
					});

					if (window.location.hash != undefined)
						$('a[data-toggle="tab"][href="' + window.location.hash + '"]', $tabNav).trigger("shown.bs.tab");
				});