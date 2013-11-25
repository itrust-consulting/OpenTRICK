function changePassword() {
	$.ajax({
		url : context + "/changePassword",
		contentType : "text/html",
		success : extract
	});
	return false;
}

function MessageResolver(code, defaulttext) {
	$.ajax({
		url : context + "/MessageResolver",
		data : code,
		contentType : "application/json",
		success : function(response) {
			if (response == null || response == "")
				return defaulttext;
			defaulttext = response;
		}
	});
	return defaulttext;
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
					openLink(context + "/Analysis");
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

function duplicateAnalysis(form, analyisId) {
	$(".progress-striped").show();
	return $.ajax({
		url : context + "/History/Save/Analysis/" + analyisId,
		type : "post",
		aync : true,
		data : $("#" + form).serialize(),
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((error = $(doc).find("#addHistoryModal")).length) {
				$("#addHistoryModal .modal-body").html(
						$(error).find(".modal-body"));
				return false;
				;
			} else {
				$("#addHistoryModal").modal("hide");
				$("body").html($(doc).find("body").html());
				return false;
			}
		}
	});
}

function selectAsset(assetId, selectVaue) {
	$.ajax({
		url : context + "/Asset/Select/" + assetId,
		contentType : "application/json",
		success : function(reponse) {
			reloadSection("section_asset");
			return false;
		}
	});
	return false;
}

function deleteAsset(assetId) {
	$("#confirm-dialog .modal-body").text(
			MessageResolver("confirm.delete.asset",
					"Are you sur, you want to delete this asset"));
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Asset/Delete/" + assetId,
			contentType : "application/json",
			success : function(reponse) {
				reloadSection("section_asset");
				return false;
			}
		});
	});
	$("#confirm-dialog").modal("toggle");
	return false;

}

function editAssetRow(rowTrickId) {
	$.ajax({
		url : context + "/Asset/Edit/" + rowTrickId,
		contentType : "application/json",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((addAssetModel = doc.getElementById("addAssetModel")) == null)
				return false;
			var parent = $("#addAssetModel").parent();
			$("#addAssetModel").remove();
			parent[0].insertBefore(addAssetModel, parent[0].firstChild);
			$('#addAssetModel').modal("toggle");
			return false;
		}
	});
	return false;
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
	return false;
}

function findAllAssetType(selector) {
	var element = document.getElementById(selector);
	$.ajax({
		url : context + "/AssetType/All",
		async : true,
		contentType : "application/json",
		success : function(reponse) {
			var option = document.createElement("option");
			option.setAttribute("value", -1);
			element.innerHTML = "";
			option.appendChild(document.createTextNode(MessageResolver(
					"label.asset.type.default", "Select assettype")));
			element.appendChild(option);
			for (var int = 0; int < reponse.length; int++) {
				var assettype = reponse[int];
				var option = document.createElement("option");
				option.setAttribute("value", assettype.id);
				option.appendChild(document.createTextNode(assettype.type));
				element.appendChild(option);
			}
		}
	});
}

function showError(parent, text) {
	var error = document.createElement("div");
	var close = document.createElement("a");
	close.setAttribute("class", "close");
	close.setAttribute("href", "#");
	close.setAttribute("data-dismiss", "alert");
	error.setAttribute("class", "alert alert-error");
	error.setAttribute("aria-hidden", "true");
	error
			.setAttribute("style",
					"background-color: #F2DEDE; border-color: #EBCCD1; color: #B94A48;");
	close.appendChild(document.createTextNode("x"));
	error.appendChild(close);
	error.appendChild(document.createTextNode(text));
	parent.insertBefore(error, parent.firstChild);
	return false;
}

function getControllerBySection(section) {
	var controllers = {
		"section_asset" : "/Asset/Section",
		"section_parameter" : "/Parameter/Section",
		"section_scenario" : "/Scenario/Section"
	};
	return controllers[section];
}

function serializeAssetForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	data["assetType"] = {
		"id" : parseInt(data["assetType"], 0),
		"type" : $("#asset_assettype_id option:selected").text()
	};
	data["value"] = parseFloat(data["value"]);
	data["selected"] = data["selected"] == "on";
	return JSON.stringify(data);
}

function reloadSection(section) {
	if (Array.isArray(section)) {
		for (var int = 0; int < section.length; int++)
			reloadSection(section[int]);
	} else {
		var controller = getControllerBySection(section);
		if (controller == null || controller == undefined)
			return false;
		$
				.ajax({
					url : context + controller,
					type : "get",
					async : true,
					contentType : "application/json",
					success : function(response) {
						var parser = new DOMParser();
						var doc = parser.parseFromString(response, "text/html");
						newSection = $(doc).find("*[id = '" + section + "']");
						oldSection = $(document.body).find(
								"*[id = '" + section + "']");
						$(oldSection).html($(newSection).html());
						return result;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						return result;
					},
				});
	}
}

function saveAsset(form) {
	return $.ajax({
		url : context + "/Asset/Save",
		type : "post",
		data : serializeAssetForm(form),
		contentType : "application/json",
		success : function(response) {
			var previewError = $("#addAssetModel .alert");
			if (previewError.length)
				previewError.remove();
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document
					.getElementById(form), data);
			if (result) {
				$("#addAssetModel").modal("hide");
				reloadSection("section_asset");
			}
			return result;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function addHistory(analysisId) {
	return $
			.ajax({
				url : context + "/History/Add/Analysis/" + analysisId,
				contentType : "application/json",
				success : function(response) {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					if ((addAssetModel = doc.getElementById("addHistoryModal")) == null)
						return false;
					var parent = $("#addHistoryModal").parent();
					$("#addHistoryModal").remove();
					parent[0].insertBefore(addAssetModel, parent[0].firstChild);
					$('#addHistoryModal').modal("toggle");
					return false;
				}
			});
}

$(function() {
	var $contextMenu = $("#asset_contextMenu");

	if ($contextMenu == null || $contextMenu == undefined)
		return false;

	$("#section_asset").on("contextmenu", "table tbody tr", function(e) {
		$contextMenu.css({
			display : "block",
			left : $(e.target).position().left,
			top : $(e.target).position().top + 20
		});
		if ($(e.currentTarget).attr('trick-selected') == "true") {
			$contextMenu.find("li[name='select']").attr("hidden", true);
			$contextMenu.find("li[name='unselect']").attr("hidden", false);
		} else {
			$contextMenu.find("li[name='select']").attr("hidden", false);
			$contextMenu.find("li[name='unselect']").attr("hidden", true);
		}
		var rowTrickId = $(e.currentTarget).attr('trick-id');

		// $contextMenu.find("li[name='edit_row'] >
		// a").attr("href",context+"/Asset/Edit/"+rowTrickId);

		$contextMenu.attr("trick-selected-id", rowTrickId);
		return false;
	});
	$contextMenu.on("click", "a", function() {
		$contextMenu.hide();
	});

	$('html').click(function() {
		$contextMenu.hide();
	});

	$('#asset_contextMenu').click(function(event) {
		event.stopPropagation();
	});
});

/**
 * Serialize form fields into JSON
 */

(function($) {

	$.fn.serializeJSON = function() {
		var json = {};
		var form = $(this);
		form.find('input, select, textarea').each(
				function() {
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
							json[this.name] = typeof val === 'string' ? [ val,
									this.value ] : $.isArray(val) ? $.merge(
									val, [ this.value ]) : this.value;
						}
					} else {
						json[this.name] = this.value;
					}
				});
		return json;
	};

})(jQuery);