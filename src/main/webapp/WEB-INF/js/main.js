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
	this.localesMessages = {};
}

function unknowError(jqXHR, textStatus, errorThrown) {
	$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.occurred", "An unknown error occurred"));
	$("#alert-dialog").modal("toggle");
	return true;
}

$(function() {

	$.extend($.tablesorter.themes.bootstrap, {
		// these classes are added to the table. To see other table classes
		// available,
		// look here: http://twitter.github.com/bootstrap/base-css.html#tables
		table : 'table',
		caption : 'caption',
		header : 'bootstrap-header', // give the header a gradient background
		footerRow : '',
		footerCells : '',
		icons : '', // add "icon-white" to make them white; this icon class is
		// added to the <i> in the header
		sortNone : 'bootstrap-icon-unsorted',
		sortAsc : 'icon-chevron-up glyphicon glyphicon-chevron-up', // includes
		// classes
		// for
		// Bootstrap
		// v2 & v3
		sortDesc : 'icon-chevron-down glyphicon glyphicon-chevron-down', // includes
		// classes
		// for
		// Bootstrap
		// v2 &
		// v3
		active : '', // applied when column is sorted
		hover : '', // use custom css here - bootstrap class may not override it
		filterRow : '', // filter row class
		even : '', // odd row zebra striping
		odd : '' // even row zebra striping
	});

	var trick_table = $(this).find("table[trick-table]");
	if (trick_table.length) {
		for (var i = 0; i < trick_table.length; i++) {
			var table = new TrickCarousel(trick_table[i]);
			table.initialise();
		}
	}

	$('.modal').on('shown', function() {
		$('body').css({
			overflow : 'hidden'
		});
	}).on('hidden', function() {
		$('body').css({
			overflow : ''
		});
	});

	if ($('#confirm-dialog').length)
		$('#confirm-dialog').on('hidden.bs.modal', function() {
			$("#confirm-dialog .btn-danger").unbind("click");
		});

	if ($('#alert-dialog').length)
		$('#alert-dialog').on('hidden.bs.modal', function() {
			$("#alert-dialog .btn-danger").unbind("click");
		});

	if ($('.table-fixed-header').length) {
		$('table.table-fixed-header').floatThead({
			scrollContainer : function($table) {
				return $table.closest('.panel-body');
			}
		});
	}

	var $window = $(window);
	var previewScrollTop = $window.scrollTop();
	if (!$(".navbar-custom").length)
		return false;
	var startPosition = $(".navbar-custom").position().top;
	var previewPosition = $(".navbar-custom").offset().top - $(".navbar-fixed-top").offset().top;
	if (previewScrollTop != 0)
		$(".navbar-custom").addClass("affix");
	$window.scroll(function() {
		var currentPosition = $(".navbar-custom").offset().top - $(".navbar-fixed-top").offset().top;
		var scrollTop = $window.scrollTop();
		if (previewPosition > 0 && currentPosition <= 50 && scrollTop > previewScrollTop) {
			$(".navbar-custom").addClass("affix");
		} else if (scrollTop < startPosition && scrollTop < previewScrollTop && scrollTop < 50)
			$(".navbar-custom").removeClass("affix");
		previewPosition = currentPosition;
		previewScrollTop = scrollTop;
	});

});

$.fn.removeAttributes = function(only, except) {
    if (only) {
        only = $.map(only, function(item) {
            return item.toString().toLowerCase();
        });
    };
    if (except) {
        except = $.map(except, function(item) {
            return item.toString().toLowerCase();
        });
        if (only) {
            only = $.grep(only, function(item, index) {
                return $.inArray(item, except) == -1;
            });
        };
    };
    return this.each(function() {
        var attributes;
        if(!only){
            attributes = $.map(this.attributes, function(item) {
                return item.name.toString().toLowerCase();
            });
             if (except) {
                attributes = $.grep(attributes, function(item, index) {
                    return $.inArray(item, except) == -1;
                });
            };
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
	DELETE : {
		value : 1,
		name : "DELETE"
	},
	CALCULATE_RISK_REGISTER : {
		value : 2,
		name : "CALCULATE_RISK_REGISTER"
	},
	CALCULATE_ACTIONPLAN : {
		value : 3,
		name : "CALCULATE_ACTIONPLAN"
	},
	MODIFY : {
		value : 4,
		name : "MODIFY"
	},
	EXPORT : {
		value : 5,
		name : "EXPORT"
	},
	READ : {
		value : 6,
		name : "READ"
	}
};

function permissionError() {
	$("#alert-dialog .modal-body").html(MessageResolver("error.notAuthorized", "Insufficient permissions!"));
	$("#alert-dialog").modal("toggle");
	return false;
}

function findRight(idAnalysis) {
	var right = $("*[trick-id='" + idAnalysis + "'][trick-rights-id]");
	if (!right.length)
		return undefined;
	var idRight = $(right).attr('trick-rights-id');
	if (!$.trim(idRight).length)
		return undefined;
	for ( var key in ANALYSIS_RIGHT)
		if (ANALYSIS_RIGHT[key].value == idRight)
			return ANALYSIS_RIGHT[key];
	return undefined;
}

function userCan(idAnalysis, action) {
	var right = findRight(idAnalysis);
	if (right != undefined && action.value != undefined)
		return right.value <= action.value;
	return false;
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
	var uniqueCode = "|^|" + code + "__uPu_*+*_*+*_+*+_PuP__" + params + "|$|";// mdr
	if (application.localesMessages[uniqueCode] != undefined)
		return application.localesMessages[uniqueCode];
	else
		application.localesMessages[uniqueCode] = defaulttext;
	$.ajax({
		url : context + "/MessageResolver",
		data : {
			"code" : code,
			"default" : defaulttext,
			"params" : params
		},
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response == null || response == "")
				return defaulttext;
			return application.localesMessages[uniqueCode] = defaulttext = response;
		}/*,error : unknowError*/
	});
	return defaulttext;
}

function fixedTableHeader(table) {
	if (table == undefined || $(table).length == 0)
		return false;
	$(table).floatThead({
		scrollContainer : function($table) {
			return $table.closest('.panel-body');
		}
	});
	return true;
}

/**
 * success and error message display
 */

function showError(parent, text) {
	var error = document.createElement("div");
	var close = document.createElement("a");
	close.setAttribute("class", "close");
	close.setAttribute("href", "#");
	close.setAttribute("data-dismiss", "alert");
	error.setAttribute("class", "alert alert-error");
	error.setAttribute("aria-hidden", "true");
	error.setAttribute("style", "background-color: #F2DEDE; border-color: #EBCCD1; color: #B94A48;");
	close.appendChild(document.createTextNode("x"));
	error.appendChild(close);
	error.appendChild(document.createTextNode(text));
	parent.insertBefore(error, parent.firstChild);
	return false;
}

function showSuccess(parent, text) {
	var success = document.createElement("div");
	var close = document.createElement("a");
	close.setAttribute("class", "close");
	close.setAttribute("href", "#");
	close.setAttribute("data-dismiss", "alert");
	success.setAttribute("class", "alert alert-success");
	success.setAttribute("aria-hidden", "true");
	close.appendChild(document.createTextNode("x"));
	success.appendChild(close);
	success.appendChild(document.createTextNode(text));
	parent.insertBefore(success, parent.firstChild);
	return false;
}


/**
 * section menu update
 */

function checkControlChange(checkbox, sectionName) {
	var items = $("#section_" + sectionName + " tbody tr td:first-child input");
	for (var i = 0; i < items.length; i++)
		$(items[i]).prop("checked", $(checkbox).is(":checked"));
	updateMenu("#section_" + sectionName, "#menu_" + sectionName);
	return false;
}

function updateMenu(idsection, idMenu) {
	var checkedCount = $(idsection + " tbody :checked").length;
	if (checkedCount == 1) {
		var $lis = $(idMenu + " li");
		for (var i = 0; i < $lis.length; i++)
			$($lis[i]).removeClass("disabled");
	} else if (checkedCount > 1) {
		var $lis = $(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			if ($($lis[i]).attr("trick-selectable") == undefined || $($lis[i]).attr("trick-selectable") === "multi")
				$($lis[i]).removeClass("disabled");
			else
				$($lis[i]).addClass("disabled");
		}
	} else {
		var $lis = $(idMenu + " li");
		for (var i = 0; i < $lis.length; i++) {
			if ($($lis[i]).attr("trick-selectable") != undefined)
				$($lis[i]).addClass("disabled");
			else
				$($lis[i]).removeClass("disabled");
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
		},error : unknowError
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
			},error : unknowError
		});
	} else {
		if (status.message != null)
			progressBar.Update(status.progress, status.message);
		if (status.flag == 3) {
			setTimeout(function() {
				updateStatus(progressBar, idTask, callback);
			}, 1500);
		} else {
			/*
			 * setTimeout(function() { progressBar.Distroy(); }, 3000);
			 */
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

function deleteAssetTypeValueDuplication() {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Scenario/Delete/AssetTypeValueDuplication",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else if (response["success"] != undefined) {
					$("#alert-dialog .modal-body").html(response["success"]);
					$("#alert-dialog").modal("toggle");
				}
			},error : unknowError
		});
	} else
		permissionError();
	return false;
}

function serializeForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	return JSON.stringify(data);
}

function parseJson(data) {
	try {
		return JSON.parse(data);
	} catch (e) {
		return undefined;
	}
}

function escape(key, val) {
	if (typeof (val) != "string")
		return val;
	return val.replace(/[\\]/g, '\\\\').replace(/[\/]/g, '\\/').replace(/[\b]/g, '\\b').replace(/[\f]/g, '\\f').replace(/[\n]/g, '\\n').replace(/[\r]/g, '\\r').replace(/[\t]/g,
			'\\t').replace(/[\"]/g, '\\"').replace(/\\'/g, "\\'");
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

function findSelectItemIdBySection(section) {
	var selectedItem = [];
	var $item = $("#" + section + " tbody :checked");
	for (var i = 0; i < $item.length; i++) {
		trickId = findTrickID($($item[i])[0]);
		if (trickId == null || trickId == undefined)
			return false;
		selectedItem.push(trickId);
	}
	return selectedItem;
}

function findTrickID(element) {
	if ($(element).attr("trick-id") != undefined)
		return $(element).attr("trick-id");
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