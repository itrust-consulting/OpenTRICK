function changePassword() {
	$.ajax({
		url : context + "/changePassword",
		contentType : "text/html",
		success : extract
	});
	return false;
}

function MessageResolver(code) {
	$.ajax({
		url : context + "/MessageResolver",
		data : {
			source : code
		},
		contentType : "application/json",
		success : function(response) {
			if (response == null || response == "")
				return code;
			return response;
		}
	});
	return code;
}

function extract(data) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(data, "text/html");
	$("#content")
			.html(
					doc.getElementById("login") == null ? doc
							.getElementById("content").innerHTML : doc
							.getElementById("login").outerHTML);
	return false;
}

function changeDisplay(source) {
	$.ajax({
		url : context + "/changeDisplay",
		data : {
			source : source
		},
		contentType : "text/html",
		success : extract
	});
	return false;
}

function createLeftContent() {
	if (document.getElementById("content_side_left") == null) {
		var leftContent = document.createElement("div");
		leftContent.setAttribute("id", "content_side_left");
		leftContent.setAttribute("class", "content content_side_left");
		var content = document.getElementById("content");
		var parent = content.parentNode;
		parent.insertBefore(leftContent, content);
	}
	return true;
}

function cancelTask(taskId) {
	$.ajax({
		url : context + "/Task/Stop/" + taskId,
		async : true,
		contentType : "application/json",
		success : function(reponse) {
			Alert(reponse);
			$("#task_" + taskId).remove();
		}
	});
}

function refraichContentSideLeft() {
	$.ajax({
		url : context + "/admin",
		contentType : "text/html",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			$("#content_side_left").html(
					doc.getElementById("content_side_left").innerHTML);
		}
	});
	return false;
}

function leftSideOpenLink(url, extractor) {
	if (extractor == null)
		extractor = extractLeftSide;
	$.ajax({
		url : url,
		contentType : "text/html",
		success : extractor
	});
	return false;
}

function extractLeftSide(data) {
	var parser = new DOMParser();
	var doc = parser.parseFromString(data, "text/html");
	if (doc.getElementById("login") != null)
		$("#content").html(doc.getElementById("login").outerHTML);
	else
		$("#content_side_left").html(
				doc.getElementById("content_side_left").innerHTML);
	return false;
}

function openLink(url, confirmAction, message) {
	if (!confirmAction || confirm(message)) {
		$.ajax({
			url : url,
			contentType : "text/html",
			success : extract
		});
	}
	return false;
}

function openLinkWithReferer(url, referer, refraich) {
	$.ajax({
		url : url,
		data : {
			redirection : referer
		},
		contentType : "text/html",
		success : extract
	}).done(function() {
		if (refraich)
			refraichContentSideLeft();
	});
	return false;
}

function TaskManager() {
	this.tasks = [];
	this.view = null;
	this.progressBars = [];

	TaskManager.prototype.Start = function() {
		var instance = this;
		setTimeout(function() {
			instance.UpdateTaskCount();
		}, 1000);
	};

	TaskManager.prototype.createView = function() {
		var div = document.getElementById("tasks");
		if (div == null) {
			div = document.createElement("div");
			div.setAttribute("id", "tasks");
			div.setAttribute("title", "Tasks");
			var content = document.getElementById("content");
			content.insertBefore(div, content.firstChild);
		}
		;
		this.view = $("#tasks").dialog();
	};

	TaskManager.prototype.Show = function() {
		if (this.view == null)
			this.createView();
		this.view.show();
	};
	TaskManager.prototype.UpdateTaskCount = function() {
		var instance = this;
		$.ajax({
			url : context + "/Task/InProcessing",
			async : true,
			contentType : "application/json",
			success : function(reponse) {
				if (reponse == null || reponse == "")
					return false;
				else if (reponse.length)
					instance.Show();
				for (var int = 0; int < reponse.length; int++) {
					if (!(reponse[int] in instance.tasks)) {
						instance.tasks.push(reponse[int]);
						instance.UpdateStatus(reponse[int]);
					}
				}
			}
		});
		/*
		 * setTimeout(function() { instance.UpdateTaskCount(); }, 10000);
		 */
		return false;
	};

	TaskManager.prototype.createProgressBar = function(taskId) {
		if (!$("#" + taskId + "-progress-bar").length) {
			div = document.createElement("div");
			div.setAttribute("id", taskId + "-progress-bar");
			div.setAttribute("class", "ui-progressbar");
			label = document.createElement("div");
			label.setAttribute("id", taskId + "-progress-label");
			label.setAttribute("class", "progress-label");
			div.appendChild(label);
			task = document.getElementById("task_" + taskId);
			task.insertBefore(div, task.firstChild);
		}
		var progressbar = $("#" + taskId + "-progress-bar"), progressLabel = $("#"
				+ taskId + "-progress-label");
		progressbar.progressbar({
			value : false,
			change : function() {
				progressLabel.text(progressbar.progressbar("value") + "%");
			},
			complete : function() {
				progressLabel.text("Complete!");
			}
		});
		return progressbar;
	};

	TaskManager.prototype.UpdateStatus = function(taskId) {
		if (!$.isNumeric(taskId))
			return;
		var instance = this;
		$.ajax({
			url : context + "/Task/Status/" + taskId,
			async : true,
			contentType : "application/json",
			success : function(reponse) {
				if (reponse == null)
					return false;
				if (!$("#task_" + taskId).length) {
					var div = document.createElement("div");
					div.setAttribute("id", "task_" + taskId);
					var linkRemove = document.createElement("a");
					linkRemove.setAttribute("class", "ui-button");
					linkRemove.setAttribute("onclick", "return cancelTask('"
							+ taskId + "');");
					linkRemove.setAttribute("href", "#");
					linkRemove.appendChild(document.createTextNode("X"));
					var text = document.createElement("label");
					text.setAttribute("class", "ui-widget");
					div.appendChild(text);
					div.appendChild(linkRemove);
					var tasks = document.getElementById("tasks");
					if (tasks == null)
						return;
					tasks.insertBefore(div, tasks.firstChild);
					instance.progressBars[taskId] = instance
							.createProgressBar(taskId);
				}
				if (reponse.message != null) {
					$("#task_" + taskId + ">label").text(reponse.message);
					instance.progressBars[taskId].progressbar("value",
							reponse.progress);
				}
				if (reponse.flag < 5) {
					setTimeout(function() {
						instance.UpdateStatus(taskId);
					}, reponse.flag == 4 ? 250 : 500);
				} else
					openLink(context + "/Analysis/Display");
				return true;
			}
		});
	};
};

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

function createDialogInfo(title, content) {
	var info = document.getElementById("info");
	if (info == null) {
		info = document.createElement("div");
		info.setAttribute("id", "info");
		info.setAttribute("title", title);
		document.getElementById("content").appendChild(info);
	} else
		info.setAttribute("title", title);
	$("#info").html(content);
	return false;
}

function addNewRole(id) {
	var role = $("#role_" + id).val();
	$.ajax({
		url : context + "/role/add/user/" + id,
		contentType : "text/html",
		data : {
			role : role
		},
		success : extract
	});
	return false;
}

function defaultValueByType(value, type, protect) {
	if (value.length == 0) {
		if (type == "int" || type == "integer")
			value = 0;
		else if (type == "float")
			value = 0.0;
		else if (type == "double")
			value = 0.0;
		else if (type == "bool" || type == "boolean")
			value = false;
		else if (type == "date")
			return new Date().toDateString();
		else
			value = "";
	}
	return value;
}

function updateFieldValue(element, value, type) {
	$(element).parent().text(defaultValueByType(value, type));
}

function saveField(element, controller, id, field, type) {
	if ($(element).prop("value") != $(element).prop("placeholder")) {
		$.ajax({
			url : context + "/editField/" + controller,
			type : "post",
			data : '{"id":' + id + ', "fieldName":"' + field + '", "value":"'
					+ defaultValueByType($(element).prop("value"), type, true)
					+ '", "type": "' + type + '"}',
			contentType : "application/json",
			success : function(response) {
				if (response == "" || response == null) {
					updateFieldValue(element, $(element).prop("value"));
					return false;
				}
				bootbox.alert(jqXHR.responseText);
				updateFieldValue(element, $(element).prop("placeholder"));
				return true;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				bootbox.alert(jqXHR.responseText);
				updateFieldValue(element, $(element).prop("placeholder"));
			},
		});
	} else {
		updateFieldValue(element, $(element).prop("placeholder"));
		return false;
	}
}

function editField(element, controller, id, field, type) {
	if ($(element).find("input").length)
		return;
	if (type == null)
		type = "string";
	var content = element.innerHTML;
	var input = document.createElement("input");
	input.setAttribute("value", content);
	input.setAttribute("class", "form-control");
	input.setAttribute("placeholder", content);
	input.setAttribute("onblur", "return saveField(this,'" + controller + "','"
			+ id + "','" + field + "','" + type + "')");
	if (element.firstChild != null)
		element.replaceChild(input, element.firstChild);
	else
		element.appendChild(input);
	$(input).focus();
}

!function($) {
	$(function() {
		var $window = $(window)
		var $body = $(document.body)
		var $sideBar = $('.bs-sidebar')
		var navHeight = $('.nav-tabs').outerHeight(true) + 10

		$body.scrollspy({
			target : '.bs-sidebar',
			offset : navHeight
		})

		$('.bs-container [href=#]').click(function(e) {
			e.preventDefault()
		})

		$window.on('resize', function() {
			$body.scrollspy('refresh')
			// We were resized. Check the position of the nav box
			$sideBar.affix('checkPosition')
		})

		$window.on('load', function() {
			$body.scrollspy('refresh')
			$('.bs-top').affix();
			$sideBar.affix({
				offset : {
					top : function() {
						var offsetTop = $sideBar.offset().top
						var sideBarMargin = parseInt($sideBar.children(0).css(
								'margin-top'), 10)
						var navOuterHeight = $('.bs-docs-nav').height()

						// We can cache the height of the header (hence the
						// this.top=)
						// This function will never be called again.
						return (this.top = offsetTop - navOuterHeight
								- sideBarMargin);
					},
					bottom : function() {
						// We can't cache the height of the footer, since it
						// could change
						// when the window is resized. This function will be
						// called every
						// time the window is scrolled or resized
						return $('.bs-footer').outerHeight(true)
					}
				}
			})
			setTimeout(function() {
				// Check the position of the nav box ASAP
				$sideBar.affix('checkPosition')
			}, 10);
			setTimeout(function() {
				// Check it again after a while (required for IE)
				$sideBar.affix('checkPosition')
			}, 100);
		});

		// tooltip demo
		$('.tooltip-demo').tooltip({
			selector : "[data-toggle=tooltip]",
			container : "body"
		})

		$('.tooltip-test').tooltip()
		$('.popover-test').popover()

		$('.bs-docs-navbar').tooltip({
			selector : "a[data-toggle=tooltip]",
			container : ".bs-docs-navbar .nav"
		})
	})
}(window.jQuery)