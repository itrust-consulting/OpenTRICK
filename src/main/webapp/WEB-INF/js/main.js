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

function FieldEditor(element, validator) {
	this.element = element;
	this.validator = validator;
	this.controllor = null;
	this.defaultValue = $(element).text();
	this.inputField = null;
	this.realValue = null;
	this.fieldName = null;
	this.classId = null;
	this.fieldType = null;

	FieldEditor.prototype.GenerateInputField = function() {
		if ($(this.element).find("input").length)
			return true;
		this.inputField = document.createElement("input");
		this.inputField.setAttribute("class", "form-control");
		this.inputField.setAttribute("placeholder", this.realValue != null
				&& this.realValue != undefined ? this.realValue
				: this.defaultValue);
		var that = this;
		$(this.inputField).blur(function() {
			return that.Save(that);
		});
		return false;
	};

	FieldEditor.prototype.Initialise = function() {
		if (!this.GenerateInputField()) {
			this.controllor = this.__findControllor(this.element);
			this.classId = this.__findClassId(this.element);
			this.fieldName = $(this.element).attr("trick-field");
			this.fieldType = $(this.element).attr("trick-field-type");
			return false;
		}
		return true;
	};

	FieldEditor.prototype.__findControllor = function(element) {
		if ($(element).attr("trick-class") != undefined)
			return $(element).attr("trick-class");
		else if ($(element).parent().prop("tagName") != "body")
			return this.__findControllor($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findClassId = function(element) {
		if ($(element).attr("trick-id") != undefined)
			return $(element).attr("trick-id");
		else if ($(element).parent().prop("tagName") != "body")
			return this.__findClassId($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.Show = function() {
		if (this.inputField == null || this.inputField == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		$(this.element).html(this.inputField);
		$(this.inputField).focus();
		return false;
	};

	FieldEditor.prototype.Validate = function() {
		return true;
	};

	FieldEditor.prototype.HasChanged = function() {
		if (this.realValue != null && this.realValue != undefined)
			return $(this.inputField).prop("value") != this.realValue;
		return $(this.inputField).prop("value") != this.defaultValue;
	};

	FieldEditor.prototype.UpdateUI = function() {
		$(this.element).text($(this.inputField).prop("value"));
		return false;
	};

	FieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$
						.ajax({
							url : context + "/EditField/" + that.controllor,
							type : "post",
							async : true,
							data : '{"id":'
									+ that.classId
									+ ', "fieldName":"'
									+ that.fieldName
									+ '", "value":"'
									+ defaultValueByType($(that.inputField)
											.prop("value"), that.fieldType,
											true) + '", "type": "'
									+ that.fieldType + '"}',
							contentType : "application/json",
							success : function(response) {
								if (response["success"] != undefined) {
									that.UpdateUI();
									$("#info-dialog .modal-body").html(
											response["success"]);
									$("#info-dialog").modal("toggle");
								} else {
									$("#alert-dialog .modal-body").html(
											response["error"]);
									$("#alert-dialog").modal("toggle");
								}
								return true;
							},
							error : function(jqXHR, textStatus, errorThrown) {
								$("#alert-dialog .modal-body").text(
										jqXHR.responseText);
								$("#alert-dialog").modal("toggle");
							},
						});
			} else {
				that.Rollback();
				return false;
			}
		}
		return false;
	};

	FieldEditor.prototype.Rollback = function() {
		$(this.element).html(this.defaultValue);
		return false;
	};
}

AssessmentFieldEditor.prototype = new FieldEditor();

function AssessmentFieldEditor(element) {

	AssessmentFieldEditor.prototype.constructor = AssessmentFieldEditor;

	AssessmentFieldEditor.prototype.Validate = function() {
	};
}

function Modal() {
	this.modal = null;
	this.modal_header = null;
	this.modal_body = null;
	this.modal_title = null;
	this.modal_footer = null;
	this.modal_head_buttons = {};
	this.modal_footer_buttons = {};

	Modal.prototype.__Create = function() {
		// declare modal
		this.modal = document.createElement("div");
		this.modal_header = document.createElement("div");
		this.modal_body = document.createElement("div");
		this.modal_title = document.createElement("h4");
		this.modal_footer = document.createElement("div");

		var modal_diaog = document.createElement("div");
		var modal_content = document.createElement("div");

		// design modal
		this.modal.setAttribute("id", "progressBarTest");
		this.modal.setAttribute("class", "modal fade in");
		this.modal.setAttribute("role", "dialog");
		this.modal.setAttribute("tabindex", "-1");
		this.modal.setAttribute("aria-hidden", "true");
		modal_diaog.setAttribute("class", "modal-dialog");
		modal_content.setAttribute("class", "modal-content");
		this.modal_header.setAttribute("class", "modal-header");
		this.modal_title.setAttribute("class", "modal-title");
		this.modal_body.setAttribute("class", "modal-body");
		this.modal_footer.setAttribute("class", "modal-footer");

		// build modal
		this.modal.appendChild(modal_diaog);
		modal_diaog.appendChild(modal_content);
		modal_content.appendChild(this.modal_header);
		this.modal_header.appendChild(this.modal_title);
		modal_content.appendChild(this.modal_body);
		modal_content.appendChild(this.modal_footer);

		// initialise text
		$(this.modal_title).text("Modal");

		if (!this.modal_head_buttons.length) {
			var button_head_close = document.createElement("button");
			button_head_close.setAttribute("class", "close");
			button_head_close.setAttribute("data-dismiss", "modal");
			$(button_head_close).html("&times;");
			this.modal_header.insertBefore(button_head_close,
					this.modal_header.firstChild);
		} else
			this.__addHeadButton();

		if (!this.modal_footer_buttons.length) {
			var button_footer_OK = document.createElement("button");
			var button_footer_cancel = document.createElement("button");
			button_footer_OK.setAttribute("class", "btn btn-default");
			button_footer_OK.setAttribute("data-dismiss", "modal");
			button_footer_cancel.setAttribute("class", "btn btn-default");
			button_footer_cancel.setAttribute("data-dismiss", "modal");
			$(button_footer_OK).html("OK");
			$(button_footer_cancel).html("Cancel");
			this.modal_footer.appendChild(button_footer_OK);
			this.modal_footer.appendChild(button_footer_cancel);
		} else
			this.__addFooterButton();

		return false;

	};

	Modal.prototype.setTitle = function(title) {
		if (this.modal_title != null)
			$(this.modal_title).text(title);
		return false;
	};

	Modal.prototype.__addHeadButton = function() {
		if (this.modal_header == null)
			return false;

		$(this.modal_header).find("button").each(function(i) {
			$(this).remove();
		});

		for (var i = 0; i < this.modal_head_buttons.length; i++)
			this.modal_header.insertBefore(this.modal_head_buttons[i],
					this.modal_header.firstChild);
		return false;
	};

	Modal.prototype.__addFooterButton = function() {
		if (this.modal_footer == null)
			return false;

		$(this.modal_footer).find("button").each(function(i) {
			$(this).remove();
		});

		for (var i = 0; i < this.modal_footer_buttons.length; i++)
			this.modal_footer.appendChild(this.modal_footer_buttons[i]);
		return false;
	};

	Modal.prototype.Intialise = function() {
		this.__Create();
	};

	Modal.prototype.Hide = function() {
		try {
			if (this.modal != null && this.modal != undefined)
				$(this.modal).modal("hide");
		} catch (e) {
			console.log(e);
		}
	};

	Modal.prototype.Distroy = function() {
		var instance = this;
		instance.Hide();
		setTimeout(function() {
			delete instance;
		}, 10);
		return false;
	};

	Modal.prototype.Show = function() {
		try {
			if (this.modal != null && this.modal != undefined)
				$(this.modal).modal("toggle");
			else {
				this.__Create();
				$(this.modal).modal("toggle");
			}
		} catch (e) {
			console.log(e);
		}
	};
}

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
		this.progressbar
				.setAttribute("class", "progress-bar progress-bar-info");
		this.progressbar.setAttribute("role", "progressbar");
		this.progressbar.setAttribute("aria-valuenow", "100");
		this.progressbar.setAttribute("aria-valuemin", "0");
		this.progressbar.setAttribute("valuemax", "100");
		this.progressbar.setAttribute("style", "width: 100%");
		this.sr_only.setAttribute("class", "sr-only");
		this.infoText.setAttribute("class", ".progress-info");

		// build progress
		this.progress.appendChild(this.progressbar);
		this.progressbar.appendChild(this.infoText);
		this.progressbar.appendChild(this.sr_only);
		return false;
	};

	ProgressBar.prototype.Update = function(value, info) {
		this.setInfo(info);
		this.setValue(value);
	};

	ProgressBar.prototype.Increment = function(value) {
		if (value == null)
			value = parseInt($(this.progressbar).attr("aria-valuenow"))
					+ this.incrementStep;
		else
			value += $(this.progressbar).attr("aria-valuenow");

		return this.setValue(value);
	};

	ProgressBar.prototype.setValue = function(value) {
		if (!$.isNumeric(value))
			return false;
		if (this.progressbar != null) {
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

	ProgressBar.prototype.Distroy = function() {
		this.Remove();
		var instance = this;
		setTimeout(function() {
			delete instance;
		}, 10);
		return false;
	};

}

function TaskManager() {
	this.tasks = [];
	this.progressBars = [];
	this.view = null;

	TaskManager.prototype.Start = function() {
		var instance = this;
		setTimeout(function() {
			instance.UpdateTaskCount();
		}, 1000);
	};

	TaskManager.prototype.__CreateView = function() {
		this.view = new Modal();
		this.view.Intialise();
		this.view.setTitle("Tasks");
	};

	TaskManager.prototype.Show = function() {
		if (this.view == null || this.view == undefined)
			this.__CreateView();
		this.view.Show();
	};

	TaskManager.prototype.isEmpty = function() {
		return this.tasks.length == 0;
	};

	TaskManager.prototype.Distroy = function() {
		if (this.view != null)
			this.view.Distroy();
		return true;
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
				else if (reponse.length) {
					for (var int = 0; int < reponse.length; int++) {
						if ($.isNumeric(reponse[int])
								&& !(reponse[int] in instance.tasks)) {
							instance.tasks.push(reponse[int]);
							instance.UpdateStatus(reponse[int]);
						}
					}
					if (!instance.isEmpty())
						instance.Show();
				}
			}
		});
		/*
		 * setTimeout(function() { instance.UpdateTaskCount(); }, 10000);
		 */
		return false;
	};

	TaskManager.prototype.createProgressBar = function(taskId) {
		if (this.view == null || this.view == undefined)
			this.__CreateView();
		var progressBar = new ProgressBar();
		var instance = this;
		progressBar.Initialise();
		progressBar.progress.setAttribute("id", "task_" + taskId);
		progressBar.Anchor(this.view.modal_body);
		progressBar.OnComplete(function(sender) {
			progressBar.setInfo("Complete");
			setTimeout(function() {
				progressBar.Distroy();
				instance.Remove(taskId);
			}, 10000);
		});
		return progressBar;
	};

	TaskManager.prototype.Remove = function(taskId) {
		var index = this.tasks.indexOf(taskId);
		if (index >-1)
			this.tasks.splice(index, 1);
		index = this.progressBars.indexOf(this.progressBars[taskId]);
		if (index != -1)
			this.progressBars.splice(index, 1);
		if (this.isEmpty())
			this.Distroy();
		return false;
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
				if (reponse == null || reponse.flag == undefined)
					return false;
				if (instance.progressBars[taskId] == null
						|| instance.progressBars[taskId] == undefined) {
					instance.progressBars[taskId] = instance
							.createProgressBar(taskId);
				}
				if (reponse.message != null) {
					instance.progressBars[taskId].Update(reponse.progress,
							reponse.message);
				}
				if (reponse.flag < 5) {
					setTimeout(function() {
						instance.UpdateStatus(taskId);
					}, reponse.flag == 4 ? 1500 : 3000);
				} else
					reloadSection("section_analysis");
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

/* History */
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
			async : true,
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

function editField(element, controller, id, field, type) {
	var fieldEditor = new FieldEditor(element, null);
	if (!fieldEditor.Initialise())
		fieldEditor.Show();
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

function controllerBySection(section) {
	var controllers = {
		"section_asset" : "/Asset/Section",
		"section_parameter" : "/Parameter/Section",
		"section_scenario" : "/Scenario/Section",
		"section_assessment" : "/Assessment/Section",
		"section_analysis" : "/Analysis"
	};
	return controllers[section];
}

function callbackBySection(section) {
	var callbacks = {
		"section_asset" : function() {
			setTimeout(function() {
				reloadSection("section_assessment");
			}, 1);
		},
		"section_scenario" : function() {
			setTimeout(function() {
				reloadSection("section_assessment");
			}, 1);
		}
	};
	return callbacks[section];
}

function sectionPretreatment(section) {
	var pretreatment = {
		"section_assessment" : function(data) {
			var trickCarousel = new TrickCarousel($(data).find(
					"table[trick-table]"));
			trickCarousel.initialise();
		}
	};
	return pretreatment[section];
}

function reloadSection(section) {
	if (Array.isArray(section)) {
		for (var int = 0; int < section.length; int++)
			reloadSection(section[int]);
	} else {
		var controller = controllerBySection(section);
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
						pretreatment = sectionPretreatment(section);
						if ($.isFunction(pretreatment))
							pretreatment(newSection);
						oldSection = $(document.body).find(
								"*[id = '" + section + "']");
						$(oldSection).html($(newSection).html());
						var callback = callbackBySection(section);
						if ($.isFunction(callback))
							callback();
						return false;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						return result;
					},
				});
	}
}
/* Asset */
function serializeAssetForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	data["assetType"] = {
		"id" : parseInt(data["assetType"]),
		"type" : $("#asset_assettype_id option:selected").text()
	};
	data["value"] = parseFloat(data["value"]);
	data["selected"] = data["selected"] == "on";
	return JSON.stringify(data);
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

function selectAsset(assetId, selectVaue) {
	$.ajax({
		url : context + "/Asset/Select/" + assetId,
		async : true,
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
			async : true,
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
		async : true,
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

function saveAsset(form) {
	return $.ajax({
		url : context + "/Asset/Save",
		type : "post",
		async : true,
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

/* Scenario */

/**
 * Scenario section
 * 
 * @param formId
 * @returns
 */

function serializeScenarioForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	data["assetType"] = {
		"id" : parseInt(data["scenarioType"], 0),
		"type" : $("#scenario_scenariotype_id option:selected").text()
	};
	data["value"] = parseFloat(data["value"]);
	data["selected"] = data["selected"] == "on";
	return JSON.stringify(data);
}

function findAllScenarioType(selector) {
	var element = document.getElementById(selector);
	$.ajax({
		url : context + "/ScenarioType/All",
		async : true,
		contentType : "application/json",
		success : function(reponse) {
			var option = document.createElement("option");
			option.setAttribute("value", -1);
			element.innerHTML = "";
			option.appendChild(document.createTextNode(MessageResolver(
					"label.scenario.type.default", "Select scenario type")));
			element.appendChild(option);
			for (var int = 0; int < reponse.length; int++) {
				var scenariotype = reponse[int];
				if (scenariotype.typeName == undefined)
					continue;
				var option = document.createElement("option");
				option.setAttribute("value", scenariotype.id);
				option.appendChild(document
						.createTextNode(scenariotype.typeName));
				element.appendChild(option);
			}
		}
	});
}

function selectScenario(assetId, selectVaue) {
	$.ajax({
		url : context + "/Scenario/Select/" + assetId,
		async : true,
		contentType : "application/json",
		success : function(reponse) {
			reloadSection("section_scenario");
			return false;
		}
	});
	return false;
}

function deleteScenario(assetId) {
	$("#confirm-dialog .modal-body").text(
			MessageResolver("confirm.delete.scenario",
					"Are you sur, you want to delete this scenario"));
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Scenario/Delete/" + assetId,
			contentType : "application/json",
			async : true,
			success : function(reponse) {
				reloadSection("section_scenario");
				return false;
			}
		});
	});
	$("#confirm-dialog").modal("toggle");
	return false;

}

function editScenarioRow(rowTrickId) {
	$
			.ajax({
				url : context + "/Scenario/Edit/" + rowTrickId,
				contentType : "application/json",
				async : true,
				success : function(response) {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					if ((addAssetModel = doc.getElementById("addScenarioModal")) == null)
						return false;
					var parent = $("#addScenarioModal").parent();
					$("#addScenarioModel").remove();
					parent[0].insertBefore(addAssetModel, parent[0].firstChild);
					$('#addScenarioModal').modal("toggle");
					return false;
				}
			});
	return false;
}

function saveScenario(form) {
	return $.ajax({
		url : context + "/Scenario/Save",
		type : "post",
		data : serializeScenarioForm(form),
		contentType : "application/json",
		async : true,
		success : function(response) {
			var previewError = $("#addScenarioModal .alert");
			if (previewError.length)
				previewError.remove();
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document
					.getElementById(form), data);
			if (result) {
				$("#addScenarioModal").modal("hide");
				reloadSection("section_scenario");
			}
			return result;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}

function TrickCarousel(table) {
	this.table = table;
	this.count = 0;
	this.theader = null;
	this.tdata = null;
	this.navLeft = null;
	this.navRight = null;
	this.selected = 1;

	TrickCarousel.prototype.initialise = function() {
		this.theader = $(this.table).find("th");
		this.tdata = $(this.table).find("td");
		this.count = $(this.table).find("th[trick-table-part]").length - 1;
		this.navLeft = $($(this.table).parent()).find(
				"a[control-trick-table='left']");
		this.navRight = $($(this.table).parent()).find(
				"a[control-trick-table='right']");
		this.showGroup(1);
		var that = this;

		$(this.navLeft).click(function() {
			if (that.selected < 2)
				that.showGroup(that.count);
			else
				that.showGroup(that.selected - 1);
			return false;
		});
		$(this.navRight).click(function() {
			if (that.selected >= that.count)
				that.showGroup(1);
			else
				that.showGroup(that.selected + 1);
			return false;
		});
		return false;

	};

	TrickCarousel.prototype.showGroup = function(groupId) {
		this.showPart(this.theader, groupId);
		this.showPart(this.tdata, groupId);
		return false;
	};

	TrickCarousel.prototype.showPart = function(items, groupId) {
		var canHide = false;
		this.selected = groupId;
		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			part = $(item).attr('trick-table-part');
			if (part !== undefined) {
				if (part == 0 || part == groupId)
					canHide = false;
				else
					canHide = true;
			}
			if (canHide)
				$(item).hide();
			else
				$(item).show();
		}
		return false;
	};
}

function generateMissingAssessment() {

	return $.ajax({
		url : context + "/Assessment/Generate/Missing",
		type : "get",
		contentType : "application/json",
		async : true,
		success : function(response) {
			if (response['error'] != undefined)
				alert(response['error']);
			else if (response['success'] != undefined) {
				alert(response['success']);
				reloadSection("section_assessment");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			alert(jqXHR.responseText);
			return false;
		},
	});
}

$(function() {
	var trick_table = $(this).find("table[trick-table]");
	if (!trick_table.length)
		return false;
	for (var i = 0; i < trick_table.length; i++) {
		var table = new TrickCarousel(trick_table[i]);
		table.initialise();
	}
});

/* context menu */
$(function() {
	var $contextMenu = $("#contextMenu");

	if ($contextMenu == null || $contextMenu == undefined)
		return false;
	var select = $contextMenu.find("li[name='select'] a");
	var unSelect = $contextMenu.find("li[name='unselect'] a");
	var editRow = $contextMenu.find("li[name='edit_row'] a");
	var deleteElement = $contextMenu.find("li[name='delete'] a");

	$("#section_asset").on(
			"contextmenu",
			"table tbody tr",
			function(e) {
				var rowTrickId = $(e.currentTarget).attr('trick-id');
				$contextMenu.attr("trick-selected-id", rowTrickId);
				select.attr("onclick", "return selectAsset('" + rowTrickId
						+ "','true');");
				unSelect.attr("onclick", "return selectAsset('" + rowTrickId
						+ "','false');");
				editRow.attr("onclick", "return editAssetRow('" + rowTrickId
						+ "');");
				deleteElement.attr("onclick", "return deleteAsset('"
						+ rowTrickId + "');");
				editRow.attr("href", "#addAssetModel");

				if ($(e.currentTarget).attr('trick-selected') == "true") {
					select.parent().attr("hidden", true);
					unSelect.parent().attr("hidden", false);
				} else {
					select.parent().attr("hidden", false);
					unSelect.parent().attr("hidden", true);
				}
				$contextMenu.css({
					display : "block",
					left : e.screenX,
					top : $(e.target).position().top + 20
				});
				return false;
			});

	$("#section_scenario").on(
			"contextmenu",
			"table tbody tr",
			function(e) {
				var rowTrickId = $(e.currentTarget).attr('trick-id');
				select.attr("onclick", "return selectScenario('" + rowTrickId
						+ "',true);");
				unSelect.attr("onclick", "return selectScenario('" + rowTrickId
						+ "',false);");
				editRow.attr("onclick", "return editScenarioRow('" + rowTrickId
						+ "');");
				deleteElement.attr("onclick", "return deleteScenario('"
						+ rowTrickId + "');");
				editRow.attr("href", "#addScenarioModel");
				if ($(e.currentTarget).attr('trick-selected') == "true") {
					select.parent().attr("hidden", true);
					unSelect.parent().attr("hidden", false);
				} else {
					select.parent().attr("hidden", false);
					unSelect.parent().attr("hidden", true);
				}
				$contextMenu.attr("trick-selected-id", rowTrickId);
				$contextMenu.css({
					display : "block",
					left : e.screenX,
					top : $(e.target).position().top + 20
				});
				return false;
			});

	$contextMenu.on("click", "a", function() {
		$contextMenu.hide();
	});

	$('html').click(function() {
		$contextMenu.hide();
	});

	$('#contextMenu').click(function(event) {
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

/**
 * Content Navigation
 */
$(function() {
	var $window = $(window);
	var previewScrollTop = $window.scrollTop();
	if (!$(".navbar-custom").length)
		return false;
	var startPosition = $(".navbar-custom").position().top;
	var previewPosition = $(".navbar-custom").offset().top
			- $(".navbar-fixed-top").offset().top;
	if (previewScrollTop != 0)
		$(".navbar-custom").addClass("affix");

	$window.scroll(function() {
		var currentPosition = $(".navbar-custom").offset().top
				- $(".navbar-fixed-top").offset().top;
		var scrollTop = $window.scrollTop();
		if (previewPosition > 0 && currentPosition <= 50
				&& scrollTop > previewScrollTop) {
			$(".navbar-custom").addClass("affix");
		} else if (scrollTop < startPosition && scrollTop < previewScrollTop)
			$(".navbar-custom").removeClass("affix");
		previewPosition = currentPosition;
		previewScrollTop = scrollTop;

	});

});
