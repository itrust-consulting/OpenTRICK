/**
 * Main.js
 */

String.prototype.endsWith = function(suffix) {
	return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

var application = new Application();

function Application() {
	this.modal = {};
	this.data = {};
	this.rights = {}
	this.localesMessages = {};
	this.fixedOffset = 0
}

function checkExtention(value, extention, button) {
	var extentions = extention.split(","), match = false;
	for (var i = 0; i < extentions.length; i++)
		match |= value.endsWith(extentions[i]);
	$(button).prop("disabled", !match);
	return match;
}

function showDialog(dialog, message) {
	$(dialog).find(".modal-body").text(message);
	return $(dialog).modal("show");
}

function unknowError(jqXHR, textStatus, errorThrown) {
	if (typeof exception != 'undefined' && exception === 'abort' || application["isReloading"])
		return false;
	showDialog("#alert-dialog", MessageResolver("error.unknown.occurred", "An unknown error occurred"));
	return true;
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
	$("a[href='#" + tabName + "']", ".nav-tab,.nav-analysis").tab("show");
	return false;
}

function togglePopever(e) {
	var target = e.target, current = application["settings-open-popover"];
	if (target === current)
		return e;
	application["settings-open-popover"] = target;
	if (current != undefined)
		$(current).popover("hide");
	return e;
}

$(function() {

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
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
	$(window).bind("beforeunload", function() {
		application["isReloading"] = true;
	});

	$(".dropdown-submenu").on("hide.bs.dropdown", function(e) {
		var $target = $(e.currentTarget);
		if ($target.find("li.active").length && !$target.hasClass("active"))
			$target.addClass("active");
	});

	$('.dropdown-submenu a[data-toggle="tab"]', "ul.nav-tab,ul.nav-analysis").on('shown.bs.tab', function(e) {
		var $parent = $(e.target).closest("li.dropdown-submenu");
		if (!$parent.hasClass("active"))
			$parent.addClass("active");
	});

	$("a[data-toggle='taskmanager']").on("click", function(e) { // task manager
		var taksmanager = application['taskManager'];
		if (taksmanager.isEmpty())
			return false;
		var $target = $(e.currentTarget), $parent = $target.parent();
		if ($parent.hasClass("open"))
			taksmanager.Hide();
		else
			taksmanager.Show();
	});

	if ($("#tab-container").length || $("#nav-container").length) {
		var tabMenu = $(".nav-tab").length ? $(".nav-tab") : $(".nav-analysis");
		var tabContainer = $("#tab-container").length ? $("#tab-container") : $("#nav-container");
		var $option = tabMenu.find("#tabOption")
		$(window).on("resize.window", function() {
			tabContainer.css({
				"margin-top" : tabMenu.height() + 12
			// default margin-top is 50px and default tabMenu size is 38px
			});
		});

		if ($option.length) {
			var updateOption = function() {
				var optionMenu = tabContainer.find(".tab-pane.active ul.nav.nav-pills");
				var tableFloatingHeader = tabContainer.find(".tab-pane.active table .tableFloatingHeader");
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
			tabMenu.find('a[data-toggle="tab"]').on('shown.bs.tab', function() {
				$(window).scroll();
			});
			$(window).on("scroll.window", function() {
				setTimeout(updateOption, 100);
			});
		}
	}

	if ($('#confirm-dialog').length) {
		$('#confirm-dialog').on('hidden.bs.modal', function() {
			$("#confirm-dialog .btn-danger").unbind("click");
		});
	}

	if ($('#alert-dialog').length) {
		$('#alert-dialog').on('hidden.bs.modal', function() {
			$("#alert-dialog .btn-danger").unbind("click");
		});
	}

	if ($("ul.nav-analysis,ul.nav-tab").length) {
		$('a[data-toggle="tab"]', "ul.nav-analysis,ul.nav-tab").on('shown.bs.tab', function(e) {
			var $target = $($(e.target).attr("href")), callback = $target.attr("data-callback");
			if (window[callback] != undefined) {
				var data = $target.attr("data-callback-data");
				if (data == undefined)
					window[callback].apply();
				else
					window[callback].apply(null, data.split(","));
			}
			if ($target.attr("data-update-required") == "true") {
				var trigger = $target.attr("data-trigger"), parameters = $target.attr("data-parameters");
				if (parameters == undefined)
					window[trigger].apply();
				else
					window[trigger].apply(null, parameters.split(","));
				$target.attr("data-update-required", "false");
			}
		});
	}

	$('[data-toggle="tooltip"]').tooltip();

	$popevers = $("[data-toggle=popover]").popover().on('show.bs.popover', togglePopever);
});

$.fn.hasAttr = function(name) {
	var attr = this.attr(name);
	return typeof attr !== typeof undefined && attr !== false;
};

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
	EDIT : {
		value : "edit",
		name : "EDIT"
	},
	EDIT_MEASURE : {
		value : "edit-measure",
		name : "EDIT_MEASURE"
	},
	valueOf : function(value) {
		for ( var key in OPEN_MODE)
			if (OPEN_MODE[key] == value || OPEN_MODE[key].value == value || OPEN_MODE[key].name == value)
				return OPEN_MODE[key];
		return undefined;
	}
}

function permissionError() {
	showDialog("#alert-dialog", MessageResolver("error.not_authorized", "Insufficient permissions!", null, findAnalysisLocale()));
	return false;
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

function hasRoleToCreateVersion() {
	return canCreateNewVersion($("#section_analysis tbody>tr>td>input:checked").parent().parent().attr("data-trick-id"));
}

function canCreateNewVersion(idAnalysis) {
	return hasRight("ALL") || $("div[data-trick-id='" + idAnalysis + "'][data-analysis-owner='true']").length;
}

function canManageAccess() {
	return $("#section_analysis tbody>tr>td>input:checked").parent().parent().attr("data-analysis-owner") == "true" || hasRight("ALL");
}

/**
 * MessageResolver
 * 
 * @param code
 * @param defaulttext
 * @param params
 * @returns
 */
function MessageResolver(code, defaulttext, params, language) {

	if (language == undefined || language == null) {
		language = $("[data-trick-language]").attr("data-trick-language");
		if (language == undefined)
			language = $("html").attr("lang");
	}

	var uniqueCode = "|^|" + code + "__uPu_*-" + language + "-*_*+*_+*+_PuP__" + params + "|$|";// mdr
	if (application.localesMessages[uniqueCode] != undefined)
		return application.localesMessages[uniqueCode];
	else
		application.localesMessages[uniqueCode] = defaulttext;

	var data = {
		code : code,
		message : defaulttext,
		language : language,
		parameters : []
	}
	if ($.isArray(params))
		data.parameters = params;
	else if (params && params.length)
		data.parameters[0] = params;
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

function checkControlChange(checkbox, sectionName, appModalVar) {
	var items = (appModalVar == undefined || appModalVar == null) ? $("#section_" + sectionName + " tbody tr td:first-child input") : $(application[appModalVar].modal).find(
			"tbody tr td:first-child input");
	var multiSelectAllowed = ((appModalVar == undefined || appModalVar == null) ? $("#menu_" + sectionName + " li[data-trick-selectable='multi']") : $(
			application[appModalVar].modal).find("#menu_" + sectionName + " li[data-trick-selectable='multi']")).length > 0;
	if (!multiSelectAllowed) {
		$(checkbox).prop("disabled", true);
		$(checkbox).prop("checked", false);
		return false;
	}
	for (var i = 0; i < items.length; i++) {
		$(items[i]).prop("checked", $(checkbox).is(":checked"));
		if ($(checkbox).is(":checked"))
			$(items[i]).parent().parent().addClass("info")
		else
			$(items[i]).parent().parent().removeClass("info")
	}
	updateMenu(undefined, "#section_" + sectionName, "#menu_" + sectionName, appModalVar);
	return false;
}

function updateMenu(sender, idsection, idMenu, appModalVar, callback) {
	if (sender) {
		if ($(sender).is(":checked")) {
			$(sender).parent().parent().addClass("info")
			var multiSelectNotAllowed = ((appModalVar == undefined || appModalVar == null) ? $("li[data-trick-selectable='multi']", idMenu) : $(idMenu
					+ " li[data-trick-selectable='multi']", application[appModalVar].modal)).length == 0;
			if (multiSelectNotAllowed) {
				var items = $("tbody :checked", ((appModalVar == undefined || appModalVar == null) ? idsection : application[appModalVar].modal));
				for (var i = 0; i < items.length; i++) {
					if (sender == $(items[i])[0])
						continue;
					$(items[i]).prop("checked", false);
					$(items[i]).parent().parent().removeClass("info")
				}
			}
		} else
			$(sender).parent().parent().removeClass("info")
	}

	var checkedCount = ((appModalVar == undefined || appModalVar == null) ? $(idsection + " tbody :checked") : $(application[appModalVar].modal).find("tbody :checked")).length;
	if (checkedCount > 1) {
		var $lis = (appModalVar == undefined || appModalVar == null) ? $(idMenu + " li") : $(application[appModalVar].modal).find(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			if ($($lis[i]).attr("data-trick-selectable") === "multi")
				$($lis[i]).removeClass("disabled");
			else
				$($lis[i]).addClass("disabled");

			var checker = $($lis[i]).attr("data-trick-check");

			if (!$($lis[i]).hasClass("disabled") && !(checker == undefined || eval(checker)))
				$($lis[i]).addClass("disabled");
		}
	} else if (checkedCount == 1) {
		var $lis = (appModalVar == undefined || appModalVar == null) ? $(idMenu + " li") : $(application[appModalVar].modal).find(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			var checker = $($lis[i]).attr("data-trick-check");
			if ($($lis[i]).attr("data-trick-selectable") != undefined)
				$($lis[i]).removeClass("disabled");
			else
				$($lis[i]).addClass("disabled");

			var checker = $($lis[i]).attr("data-trick-check");

			if (!$($lis[i]).hasClass("disabled") && !(checker == undefined || eval(checker)))
				$($lis[i]).addClass("disabled");
		}
	} else {
		var $lis = (appModalVar == undefined || appModalVar == null) ? $(idMenu + " li") : $(application[appModalVar].modal).find(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			if ($($lis[i]).attr("data-trick-selectable") != undefined)
				$($lis[i]).addClass("disabled");
			else
				$($lis[i]).removeClass("disabled");

			var checker = $($lis[i]).attr("data-trick-check");

			if (!$($lis[i]).hasClass("disabled") && !(checker == undefined || eval(checker)))
				$($lis[i]).addClass("disabled");
		}
	}

	if (callback != undefined) {
		try {
			if ($.isFunction(callback))
				callback();
			else
				eval(callback);
		} catch (e) {
			console.log(e);
		}
	}
	return false;
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
	var data = $form.serializeJSON();
	return JSON.stringify(data);
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

function post(url, data, refraich) {
	$.ajax({
		url : url,
		type : "post",
		data : $("#" + data).serialize(),
		success : extract
	}).done(function() {
		if (refraich)
			refraichContentSideLeft();
	});
	return false;
}

function findSelectItemIdBySection(section, modal) {
	var selectedItem = [];
	var $item = (modal == null || modal == undefined) ? $("#" + section + " tbody :checked") : $(modal).find("tbody :checked");
	for (var i = 0; i < $item.length; i++) {
		trickId = findTrickID($($item[i])[0]);
		if (trickId == null || trickId == undefined)
			return false;
		selectedItem.push(trickId);
	}
	return selectedItem;
}

function findTrickID(element) {
	if ($(element).attr("data-trick-id") != undefined)
		return $(element).attr("data-trick-id");
	else if ($(element).parent().prop("tagName") != "BODY")
		return findTrickID($(element).parent());
	else
		return null;
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