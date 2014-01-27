/*******************************************************************************
 * 
 */

var application = new Application();

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
	$("#alert-dialog .modal-body")
			.html(
					MessageResolver("error.notAuthorized",
							"Insufficient permissions!"));
	$("#alert-dialog").modal("toggle");
	return false;
}

function findRight(idAnalysis) {
	var right = $("*[trick-id='" + idAnalysis + "'][trick-rights-id]");
	if (!right.length)
		return undefined;
	var idRight = $(right).attr('trick-rights-id');
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

function Application() {
	this.modal = {};
	this.data = {};
}

function changePassword() {
	$.ajax({
		url : context + "/changePassword",
		contentType : "text/html",
		success : extract
	});
	return false;
}

function serializeForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	return JSON.stringify(data);
}

function MessageResolver(code, defaulttext, params) {
	$.ajax({
		url : context + "/MessageResolver",
		data : {
			"code" : code,
			"default" : defaulttext,
			"params" : params
		},
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
	$("#content").html(doc.getElementById("login") == null ? doc.getElementById("content").innerHTML : doc.getElementById("login").outerHTML);
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

function cancelTask(taskId) {
	$.ajax({
		url : context + "/Task/Stop/" + taskId,
		async : true,
		contentType : "application/json",
		success : function(reponse) {
			$("#task_" + taskId).remove();
		}
	});
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

function computeAssessment() {
	$.ajax({
		url : context + "/Assessment/Update",
		type : "get",
		contentType : "application/json",
		async : true,
		success : function(response) {
			if (response['error'] != undefined) {
				$("#info-dialog .modal-body").text(response['error']);
				$("#info-dialog").modal("toggle");
			} else if (response['success'] != undefined) {
				$("#info-dialog .modal-body").text(response['success']);
				$("#info-dialog").modal("toggle");
				chartALE();
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			return false;
		},
	});
	return false;
}

function wipeAssessment() {
	$.ajax({
		url : context + "/Assessment/Wipe",
		type : "get",
		contentType : "application/json",
		async : true,
		success : function(response) {
			if (response['error'] != undefined) {
				$("#info-dialog .modal-body").text(response['error']);
				$("#info-dialog").modal("toggle");
			} else if (response['success'] != undefined) {
				$("#info-dialog .modal-body").text(response['success']);
				$("#info-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			return false;
		},
	});
	return false;
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
		this.navLeft = $($(this.table).parent()).find("a[control-trick-table='left']");
		this.navRight = $($(this.table).parent()).find("a[control-trick-table='right']");
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

function extractPhase(that) {
	var phases = $("#section_phase *[trick-class='Phase']>*:nth-child(2)");
	if (!$(phases).length)
		return true;
	that.choose.push("NA");
	for (var i = 0; i < phases.length; i++)
		that.choose.push($(phases[i]).text());
	return false;
}

function FieldEditor(element, validator) {
	this.element = element;
	this.validator = validator;
	this.controllor = null;
	this.defaultValue = $(element).text().trim();
	this.choose = [];
	this.inputField = null;
	this.realValue = null;
	this.fieldName = null;
	this.classId = null;
	this.fieldType = null;
	this.callback = null;

	FieldEditor.prototype.GenerateInputField = function() {
		if ($(this.element).find("input").length || $(this.element).find("select").length)
			return true;
		if (!this.LoadData())
			return true;
		if (!this.choose.length) {
			this.inputField = document.createElement("input");
			this.realValue = this.element.hasAttribute("real-value") ? $(this.element).attr("real-value") : null;
		} else {
			this.inputField = document.createElement("select");
			this.inputField.setAttribute("style", "min-width:60px;");
			for (var i = 0; i < this.choose.length; i++) {
				var option = document.createElement("option");
				option.setAttribute("value", this.choose[i]);
				$(option).text(this.choose[i]);
				if (this.choose[i] == this.defaultValue)
					option.setAttribute("selected", true);
				$(option).appendTo($(this.inputField));
			}
		}
		var that = this;
		this.inputField.setAttribute("class", "form-control");
		this.inputField.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);
		$(this.inputField).blur(function() {
			return that.Save(that);
		});
		return false;
	};

	FieldEditor.prototype.HasAttr = function(element, attribute) {
		var attr = $(element).attr(attribute);
		return typeof attr !== 'undefined' && attr !== false;
	};

	FieldEditor.prototype.Initialise = function() {

		if (!this.GenerateInputField()) {
			this.controllor = this.__findControllor(this.element);
			this.classId = this.__findClassId(this.element);
			this.callback = this.__findCallback(this.element);
			this.fieldName = $(this.element).attr("trick-field");
			this.fieldType = $(this.element).attr("trick-field-type");
			return false;
		}
		return true;
	};

	FieldEditor.prototype.__findChoose = function(element) {
		if ($(element).attr("trick-choose") != undefined)
			return $(element).attr("trick-choose").split(",");
		return [];
	};

	FieldEditor.prototype.__findControllor = function(element) {
		if ($(element).attr("trick-class") != undefined)
			return $(element).attr("trick-class");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findControllor($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findClassId = function(element) {
		if ($(element).attr("trick-id") != undefined)
			return $(element).attr("trick-id");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findClassId($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findCallback = function(element) {
		if ($(element).attr("trick-callback") != undefined)
			return $(element).attr("trick-callback");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findCallback($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findCallbackPreExec = function(element) {
		if ($(element).attr("trick-callback-pre") != undefined)
			return $(element).attr("trick-callback-pre");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findCallbackPreExec($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.Show = function() {
		if (this.inputField == null || this.inputField == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		$(this.inputField).prop("value", this.realValue != null ? this.realValue : $(this.element).text().trim());
		$(this.element).html(this.inputField);
		$(this.inputField).focus();
		return false;
	};

	FieldEditor.prototype.Validate = function() {
		return true;
	};

	FieldEditor.prototype.LoadData = function() {
		var callback = this.__findCallbackPreExec(this.element);
		if (callback != null)
			eval(callback);
		if (!this.choose.length)
			this.choose = this.__findChoose(this.element);
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
				$.ajax({
					url : context + "/EditField/" + that.controllor,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"'
							+ defaultValueByType($(that.inputField).prop("value"), that.fieldType, true) + '", "type": "' + that.fieldType + '"}',
					contentType : "application/json",
					success : function(response) {
						if (response["success"] != undefined) {
							that.UpdateUI();
							if (that.callback != null && that.callback != undefined)
								setTimeout(that.callback, 10);
						} else {
							$("#alert-dialog .modal-body").html(response["error"]);
							$("#alert-dialog").modal("toggle");
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						$("#alert-dialog .modal-body").text(jqXHR.responseText);
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

ExtendedFieldEditor.prototype = new FieldEditor();

function ExtendedFieldEditor(element) {
	this.element = element;
	this.controllor = "ExtendedParameter";
	this.defaultValue = $(element).text();

	ExtendedFieldEditor.prototype.constructor = ExtendedFieldEditor;

	ExtendedFieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/EditField/" + that.controllor,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"'
							+ defaultValueByType($(that.inputField).prop("value"), that.fieldType, true) + '", "type": "' + that.fieldType + '"}',
					contentType : "application/json",
					success : function(response) {
						if (response["success"] != undefined) {
							if (that.fieldName == "acronym")
								setTimeout("updateAssessmentAcronym('" + that.classId + "', '" + that.defaultValue + "')", 100);
							return reloadSection("section_parameter");
						} else {
							$("#alert-dialog .modal-body").html(response["error"]);
							$("#alert-dialog").modal("toggle");
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						$("#alert-dialog .modal-body").text(jqXHR.responseText);
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
}

AssessmentFieldEditor.prototype = new FieldEditor();

function AssessmentFieldEditor(element) {

	this.element = element;

	this.defaultValue = $(element).text();

	AssessmentFieldEditor.prototype.constructor = AssessmentFieldEditor;

	AssessmentFieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/EditField/" + that.controllor,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"'
							+ defaultValueByType($(that.inputField).prop("value"), that.fieldType, true) + '", "type": "' + that.fieldType + '"}',
					contentType : "application/json",
					success : function(response) {
						console.log(response);
						if (response["success"] != undefined) {
							if (application.modal["AssessmentViewer"] != undefined)
								application.modal["AssessmentViewer"].Load();
							else {
								$("#info-dialog .modal-body").html(response["success"]);
								$("#info-dialog").prop("style", "z-index:1070");
								$("#info-dialog").modal("toggle");

							}
						} else {
							$("#alert-dialog .modal-body").html(response["error"]);
							$("#alert-dialog").prop("style", "z-index:1070");
							$("#alert-dialog").modal("toggle");

						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						$("#alert-dialog .modal-body").text(jqXHR.responseText);
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
}

MaturityMeasureFieldEditor.prototype = new FieldEditor();

function MaturityMeasureFieldEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text();
	this.implementations = [];

	MaturityMeasureFieldEditor.prototype.constructor = MaturityMeasureFieldEditor;

	MaturityMeasureFieldEditor.prototype.LoadData = function() {
		var $implementationRate = $("#Maturity_implementation_rate td[trick-class='Parameter']");
		if (!$implementationRate.length)
			return true;
		for (var i = 0; i < $implementationRate.length; i++)
			this.implementations[i] = {
				'id' : $($implementationRate[i]).attr('trick-id'),
				'value' : $($implementationRate[i]).text()
			};
		return !this.implementations.length;
	};

	MaturityMeasureFieldEditor.prototype.GenerateInputField = function() {
		if ($(this.element).find("select").length)
			return true;
		if (this.LoadData())
			return true;
		this.inputField = document.createElement("select");
		this.inputField.setAttribute("class", "form-control");
		this.inputField.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);
		for ( var i in this.implementations) {
			var option = document.createElement("option");
			option.setAttribute("value", this.implementations[i].value);
			option.setAttribute("trick-id", this.implementations[i].id);
			$(option).text(this.implementations[i].value);
			$(option).appendTo($(this.inputField));
			if (this.defaultValue == this.implementations[i].value)
				$(option).prop("selected", true);
		}

		var that = this;
		this.realValue = this.element.hasAttribute("real-value") ? $(this.element).attr("real-value") : null;
		$(this.inputField).blur(function() {
			return that.Save(that);
		});
		return false;
	};

}

function Modal() {
	this.modal = null;
	this.modal_dialog = null;
	this.modal_header = null;
	this.modal_body = null;
	this.modal_title = null;
	this.modal_footer = null;
	this.modal_head_buttons = [];
	this.modal_footer_buttons = [];

	Modal.prototype.Size = function(map) {
		var size = 0;
		for ( var value in map)
			size++;
		return size;

	};

	Modal.prototype.__Create = function() {
		// declare modal
		this.modal = document.createElement("div");
		this.modal_header = document.createElement("div");
		this.modal_body = document.createElement("div");
		this.modal_title = document.createElement("h4");
		this.modal_footer = document.createElement("div");
		this.modal_dialog = document.createElement("div");
		var modal_content = document.createElement("div");

		// design modal
		this.modal.setAttribute("id", "modalBox");
		this.modal.setAttribute("class", "modal fade in");
		this.modal.setAttribute("role", "dialog");
		this.modal.setAttribute("tabindex", "-1");
		this.modal.setAttribute("aria-hidden", "true");
		this.modal.setAttribute("aria-hidden", "true");
		this.modal_dialog.setAttribute("class", "modal-dialog");
		modal_content.setAttribute("class", "modal-content");
		this.modal_header.setAttribute("class", "modal-header");
		this.modal_title.setAttribute("class", "modal-title");
		this.modal_body.setAttribute("class", "modal-body");
		this.modal_footer.setAttribute("class", "modal-footer");

		// build modal
		this.modal.appendChild(this.modal_dialog);
		this.modal_dialog.appendChild(modal_content);
		modal_content.appendChild(this.modal_header);
		this.modal_header.appendChild(this.modal_title);
		modal_content.appendChild(this.modal_body);
		modal_content.appendChild(this.modal_footer);

		if (!this.Size(this.modal_head_buttons)) {
			var button_head_close = document.createElement("button");
			button_head_close.setAttribute("class", "close");
			button_head_close.setAttribute("data-dismiss", "modal");
			$(button_head_close).html("&times;");
			this.modal_header.insertBefore(button_head_close, this.modal_header.firstChild);
		} else
			this.__addHeadButton();

		if (!this.Size(this.modal_footer_buttons)) {
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

	Modal.prototype.setBody = function(body) {
		if (this.modal_body != null)
			$(this.modal_body).html(body);
		return false;
	};
	
	Modal.prototype.__addHeadButton = function() {
		if (this.modal_header == null)
			return false;

		$(this.modal_header).find("button").each(function(i) {
			$(this).remove();
		});

		for ( var button in this.modal_head_buttons)
			this.modal_header.insertBefore(this.modal_head_buttons[button], this.modal_header.firstChild);
		return false;
	};

	Modal.prototype.__addFooterButton = function() {
		if (this.modal_footer == null)
			return false;

		$(this.modal_footer).find("button").each(function(i) {
			$(this).remove();
		});

		for ( var button in this.modal_footer_buttons)
			this.modal_footer.appendChild(this.modal_footer_buttons[button]);
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
		instance.modal.remove();
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
				this.Intialise();
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
		this.progressbar.setAttribute("class", "progress-bar progress-bar-info");
		this.progressbar.setAttribute("role", "progressbar");
		this.progressbar.setAttribute("aria-valuenow", "100");
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

	ProgressBar.prototype.Distroy = function() {
		this.Remove();
		var instance = this;
		setTimeout(function() {
			delete instance;
		}, 10);
		return false;
	};

}

function parseJson(data) {
	try {
		return JSON.parse(data);
	} catch (e) {
		return undefined;
	}
}

function TaskManager(title) {
	
	this.tasks = [];
	this.progressBars = [];
	this.title = title;
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
			this.view.setTitle(this.title);			
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
						if ($.isNumeric(reponse[int]) && !(reponse[int] in instance.tasks)) {
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
				instance.Distroy();
			}, 10000);
		});
		return progressBar;
	};

	TaskManager.prototype.Remove = function(taskId) {
		var index = this.tasks.indexOf(taskId);
		if (index > -1)
			this.tasks.splice(index, 1);
		if (this.progressBars[taskId] != undefined && this.progressBars[taskId] != null) {
			this.progressBars[taskId].Remove();
			this.progressBars.splice(taskId, 1);
		}
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
				if (reponse == null || reponse.flag == undefined) {
					if (!instance.progressBars.length)
						instance.Remove(taskId);
					return false;
				}
				if (instance.progressBars[taskId] == null || instance.progressBars[taskId] == undefined) {
					instance.progressBars[taskId] = instance.createProgressBar(taskId);
				}
				if (reponse.message != null) {
					instance.progressBars[taskId].Update(reponse.progress, reponse.message);
				}
				if (reponse.flag == 3) {
					setTimeout(function() {
						instance.UpdateStatus(taskId);
					}, reponse.flag == 4 ? 1500 : 3000);
				} else {
					setTimeout(function() {
						instance.Remove(taskId);
					}, 3000);
					if (reponse.flag == 5)
						reloadSection("section_analysis");
					
				}
				return true;
			}
		});
	};
};

AssessmentViewer.prototype = new Modal();

function AssessmentViewer() {

	AssessmentViewer.prototype.Intialise = function() {
		var instance = this;

		this.modal_footer_buttons["update"] = document.createElement("button");
		this.modal_footer_buttons["update"].setAttribute("class", "btn btn-warning");

		this.modal_footer_buttons["close"] = document.createElement("button");
		this.modal_footer_buttons["close"].setAttribute("class", "btn btn-default");

		$(this.modal_footer_buttons["close"]).click(function() {
			return instance.Distroy();
		});

		$(this.modal_footer_buttons["close"]).html(MessageResolver("label.action.cloase", "Close"));
		$(this.modal_footer_buttons["update"]).html(MessageResolver("label.assessment.recompute", "Update assessments"));

		$(this.modal_footer_buttons["close"]).click(function() {
			return instance.Distroy();
		});

		$(this.modal_footer_buttons["update"]).click(function() {
			return instance.Update();
		});

		Modal.prototype.Intialise.call(this);
		$(this.modal_dialog).prop("style", "width: 95%; min-width:1170px; max-width:1300px;");

		return false;

	};

	AssessmentViewer.prototype.Show = function() {
		var instance = this;
		return this.Load(function() {
			return Modal.prototype.Show.call(instance);
		});
	};

	AssessmentViewer.prototype.Load = function(callback) {
		throw "Not implemented";
	};

	AssessmentViewer.prototype.Update = function() {
		throw "Not implemented";
	};
}

/**
 * Class AssessmentAssetViewer
 */
AssessmentAssetViewer.prototype = new AssessmentViewer();

function AssessmentAssetViewer(assetId) {
	this.assetId = assetId;

	AssessmentAssetViewer.prototype.constructor = AssessmentAssetViewer;

	AssessmentAssetViewer.prototype.Load = function(callback) {
		if (this.modal_body == null)
			this.Intialise();
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Asset/" + instance.assetId,
			contentType : "application/json",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_asset_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				if (callback != null && $.isFunction(callback))
					return callback();
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};

	AssessmentAssetViewer.prototype.Update = function() {
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Asset/" + instance.assetId + "/Update",
			contentType : "application/json",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_asset_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};
}

AssessmentScenarioViewer.prototype = new AssessmentViewer();

function AssessmentScenarioViewer(scenarioId) {
	this.scenarioId = scenarioId;

	AssessmentScenarioViewer.prototype.constructor = AssessmentScenarioViewer;

	AssessmentScenarioViewer.prototype.Load = function(callback) {
		if (this.modal_body == null)
			this.Intialise();
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Scenario/" + instance.scenarioId,
			contentType : "application/json",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_scenario_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				if (callback != null && $.isFunction(callback))
					return callback();
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};

	AssessmentScenarioViewer.prototype.Update = function() {
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Scenario/" + instance.scenarioId + "/Update",
			contentType : "application/json",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_scenario_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};
}

function updateALE() {
	return $.ajax({
		url : context + "/Analysis/Update/ALE",
		contentType : "application/json",
		async : true,
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog").modal("toggle");
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return true;
		}
	});
}

function updateAssessmentAcronym(idParameter, acronym) {

	return $.ajax({
		url : context + "/Assessment/Update/Acronym/" + idParameter + "/" + acronym,
		contentType : "application/json",
		async : true,
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog").modal("toggle");
				setTimeout("updateALE()", 2000);
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return true;
		}
	});
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
function addHistory(analysisId, oldVersion) {

	$.ajax({
		url : context + "/History/Analysis/" + analysisId + "/NewVersion",
		type : "get",
		contentType : "application/json",
		data : {
			"oldVersion" : oldVersion
		},
		success : function(response) {
			$("#addHistoryModal").replaceWith(response);
			$('#addHistoryModal').modal("toggle");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
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
			async : true,
			data : '{"id":' + id + ', "fieldName":"' + field + '", "value":"' + defaultValueByType($(element).prop("value"), type, true) + '", "type": "' + type + '"}',
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
		url : context + "/History/Analysis/" + analyisId + "/NewVersion/Save",
		type : "post",
		aync : true,
		data : $("#" + form).serialize(),
		success : function(response) {
			var alert = $("#addHistoryModal .alert");
			if (alert.length)
				alert.remove();
			if (response["success"] != undefined) {
				$(".progress-striped").hide();
				showSuccess($("#addHistoryModal .modal-body")[0], response["success"]);
				setTimeout("location.reload()", 2000);
			} else if (response["error"]) {
				$(".progress-striped").hide();
				showError($("#addHistoryModal .modal-body")[0], response["error"]);

				return false;
			} else {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ((error = $(doc).find("#addHistoryModal")).length) {
					$("#addHistoryModal .modal-body").html($(error).find(".modal-body"));
					return false;
				}
			}
		}
	});
}

function editField(element, controller, id, field, type) {
	var fieldEditor = null;
	if (controller == null || controller == undefined)
		controller = FieldEditor.prototype.__findControllor(element);
	if (controller == "ExtendedParameter")
		fieldEditor = new ExtendedFieldEditor(element);
	else if (controller == "Assessment")
		fieldEditor = new AssessmentFieldEditor(element);
	else if (controller == "MaturityMeasure")
		fieldEditor = new MaturityMeasureFieldEditor(element);
	else
		fieldEditor = new FieldEditor(element);

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

function controllerBySection(section, subSection) {
	var controllers = {
		"section_asset" : "/Asset/Section",
		"section_parameter" : "/Parameter/Section",
		"section_scenario" : "/Scenario/Section",
		"section_assessment" : "/Assessment/Section",
		"section_phase" : "/Phase/Section",
		"section_analysis" : "/Analysis",
		"section_measure" : "/Measure/Section",
		"section_customer" : "/KnowledgeBase/Customer/Section",
		"section_language" : "/KnowledgeBase/Language/Section",
		"section_norm" : "/KnowledgeBase/Norm/Section",
		"section_user" : "/Admin/User/Section",
		"section_actionplans" : "/ActionPlan/Section",
	};

	if (subSection == null || subSection == undefined)
		return controllers[section];
	else
		return controllers[section] + "/" + subSection;
}

function callbackBySection(section) {
	var callbacks = {
		"section_asset" : function() {
			chartALE();
			return false;
		},
		"section_scenario" : function() {
			chartALE();
			return false;
		}
	};
	return callbacks[section];
}

function sectionPretreatment(section) {
	var pretreatment = {
		"section_assessment" : function(data) {
			var trickCarousel = new TrickCarousel($(data).find("table[trick-table]"));
			trickCarousel.initialise();
		}
	};
	return pretreatment[section];
}

function reloadSection(section, subSection) {
	if (Array.isArray(section)) {
		for (var int = 0; int < section.length; int++) {
			if (Array.isArray(section[int]))
				reloadSection(section[int][0], section[int][1]);
			else
				reloadSection(section[int]);
		}
	} else {
		var controller = controllerBySection(section, subSection);
		if (controller == null || controller == undefined)
			return false;
		$.ajax({
			url : context + controller,
			type : "get",
			async : true,
			contentType : "application/json",
			success : function(response) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if (subSection != null && subSection != undefined)
					section += "_" + subSection;
				newSection = $(doc).find("*[id = '" + section + "']");
				pretreatment = sectionPretreatment(section);
				if ($.isFunction(pretreatment))
					pretreatment(newSection);
				oldSection = $(document.body).find("*[id = '" + section + "']");
				$(oldSection).html($(newSection).html());
				var callback = callbackBySection(section);
				if ($.isFunction(callback))
					callback();
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return false;
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

function selectAsset(assetId, value) {
	if (assetId == undefined) {
		var selectedItem = findSelectItemIdBySection("section_asset");
		if (!selectedItem.length)
			return false;
		var requiredUpdate = [];
		for (var i = 0; i < selectedItem.length; i++) {
			var selected = $(
					"#section_asset tbody tr[trick-id='" + selectedItem[i]
							+ "']").attr("trick-selected");
			if (value != selected)
				requiredUpdate.push(selectedItem[i]);
		}
		$.ajax({
			url : context + "/Asset/Select",
			contentType : "application/json",
			data : JSON.stringify(requiredUpdate, null, 2),
			type : 'post',
			success : function(reponse) {
				reloadSection('section_asset');
				return false;
			}
		});
	} else {
		$.ajax({
			url : context + "/Asset/Select/" + assetId,
			async : true,
			contentType : "application/json",
			success : function(reponse) {
				reloadSection("section_asset");
				return false;
			}
		});
	}
	return false;
}

function deleteAsset(assetId) {
	if (assetId == null || assetId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_asset"));
		if (!selectedScenario.length)
			return false;
		while (selectedScenario.length) {
			rowTrickId = selectedScenario.pop();
			$.ajax({
				url : context + "/Asset/Delete/" + rowTrickId,
				contentType : "application/json",
				async : true,
				success : function(response) {
					var trickSelect = parseJson(response);
					if (trickSelect != undefined
							&& trickSelect["success"] != undefined) {
						var row = $("#section_asset tr[trick-id='" + rowTrickId
								+ "']");
						var checked = $("#section_asset tr[trick-id='"
								+ rowTrickId + "'] :checked");
						if (checked.length)
							$(checked).removeAttr("checked");
						if (row.length)
							$(row).remove();
					}
					return false;
				}
			});
		}
		setTimeout("reloadSection('section_asset')", 100);
		return false;
	}

	$("#confirm-dialog .modal-body").text(
			MessageResolver("confirm.delete.asset",
					"Are you sure, you want to delete this asset"));
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

function editAsset(rowTrickId, isAdd) {
	if (isAdd)
		rowTrickId = undefined;
	else if (rowTrickId == null || rowTrickId == undefined) {
		var selectedScenario = $("#section_asset :checked");
		if (selectedScenario.length != 1)
			return false;
		rowTrickId = findTrickID(selectedScenario[0]);
	}
	$
			.ajax({
				url : context
						+ ((rowTrickId == null || rowTrickId == undefined || rowTrickId < 1) ? "/Asset/Add"
								: "/Asset/Edit/" + rowTrickId),
				async : true,
				contentType : "application/json",
				success : function(response) {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					if ((addAssetModal = doc.getElementById("addAssetModal")) == null)
						return false;
					if ($("#addAssetModal").length)
						$("#addAssetModal").html($(addAssetModal).html());
					else
						$(addAssetModal).appendTo($("#widget"));
					$('#addAssetModal').on('hidden.bs.modal', function() {
						$('#addAssetModal').remove();
					});
					$("#addAssetModal").modal("toggle");
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
			var previewError = $("#addAssetModal .alert");
			if (previewError.length)
				previewError.remove();
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addAssetModal").modal("hide");
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
	data["scenarioType"] = {
		"id" : parseInt(data["scenarioType"], 0),
		"type" : $("#scenario_scenariotype_id option:selected").text()
	};
	return JSON.stringify(data);
}

function clearScenarioFormData() {
	$("#addScenarioModal #addScenarioModel-title").html(MessageResolver("label.scenario.add", "Add new scenario"));
	$("#addScenarioModal #scenario_id").attr("value", -1);
}

function selectScenario(scenarioId, value) {
	if (scenarioId == undefined) {
		var selectedItem = findSelectItemIdBySection("section_scenario");
		if (!selectedItem.length)
			return false;
		var requiredUpdate = [];
		for (var i = 0; i < selectedItem.length; i++) {
			var selected = $(
					"#section_scenario tbody tr[trick-id='" + selectedItem[i]
							+ "']").attr("trick-selected");
			if (value != selected)
				requiredUpdate.push(selectedItem[i]);
		}
		$.ajax({
			url : context + "/Scenario/Select",
			contentType : "application/json",
			data : JSON.stringify(requiredUpdate, null, 2),
			type : 'post',
			success : function(reponse) {
				reloadSection('section_scenario');
				return false;
			}
		});
	} else {
		$.ajax({
			url : context + "/Scenario/Select/" + scenarioId,
			async : true,
			contentType : "application/json",
			success : function(reponse) {
				reloadSection("section_scenario");
				return false;
			}
		});
	}
	return false;
}

function displayAssessmentByScenario() {
	var selectedItem = findSelectItemIdBySection("section_scenario");
	if (selectedItem.length != 1)
		return false;
	application.modal["AssessmentViewer"] = new AssessmentScenarioViewer(
			selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
	return false;
}

function displayAssessmentByAsset(){

	var selectedItem = findSelectItemIdBySection("section_asset");
	if (selectedItem.length != 1)
		return false;
	application.modal["AssessmentViewer"] = new AssessmentAssetViewer(
			selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
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

function deleteScenario(scenarioId) {
	if (scenarioId == null || scenarioId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_scenario"));
		if (!selectedScenario.length)
			return false;
		while (selectedScenario.length) {
			rowTrickId = selectedScenario.pop();
			$.ajax({
				url : context + "/Scenario/Delete/" + rowTrickId,
				contentType : "application/json",
				async : true,
				success : function(response) {
					var trickSelect = parseJson(response);
					if (trickSelect != undefined
							&& trickSelect["success"] != undefined) {
						var row = $("#section_scenario tr[trick-id='"
								+ rowTrickId + "']");
						var checked = $("#section_scenario tr[trick-id='"
								+ rowTrickId + "'] :checked");
						if (checked.length)
							$(checked).removeAttr("checked");
						if (row.length)
							$(row).remove();
					}
					return false;
				}
			});
		}
		setTimeout("reloadSection('section_scenario')", 100);
		return false;
	}

	$("#confirm-dialog .modal-body").text(
			MessageResolver("confirm.delete.scenario",
					"Are you sure, you want to delete this scenario"));
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Scenario/Delete/" + scenarioId,
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

function findTrickID(element) {
	if ($(element).attr("trick-id") != undefined)
		return $(element).attr("trick-id");
	else if ($(element).parent().prop("tagName") != "BODY")
		return findTrickID($(element).parent());
	else
		return null;
}

function editScenario(rowTrickId, isAdd) {
	if (isAdd)
		rowTrickId = undefined;
	else if (rowTrickId == null || rowTrickId == undefined) {
		var selectedScenario = $("#section_scenario :checked");
		if (selectedScenario.length != 1)
			return false;
		rowTrickId = findTrickID(selectedScenario[0]);
	}

	$
			.ajax({
				url : context
						+ (rowTrickId == null || rowTrickId == undefined
								|| rowTrickId < 1 ? "/Scenario/Add"
								: "/Scenario/Edit/" + rowTrickId),
				contentType : "application/json",
				async : true,
				success : function(response) {
					var parser = new DOMParser();
					var doc = parser.parseFromString(response, "text/html");
					if ((addScenarioModal = doc
							.getElementById("addScenarioModal")) == null)
						return false;
					if ($("#addScenarioModal").length)
						$("#addScenarioModal").html($(addScenarioModal).html());
					else
						$(addScenarioModal).appendTo($("#widget"));
					$('#addScenarioModal').on('hidden.bs.modal', function() {
						$('#addScenarioModal').remove();
					});
					$("#addScenarioModal").modal("toggle");
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
			result = data == "" ? true : showError(document.getElementById(form), data);
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
/**
 * 
 * @param form
 * @returns
 */
function savePhase(form) {
	$.ajax({
		url : context + "/Phase/Save",
		type : "post",
		async : true,
		data : serializeAssetForm(form),
		contentType : "application/json",
		success : function(response) {
			var previewError = $("#addPhaseModel .alert");
			if (previewError.length)
				previewError.remove();
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addPhaseModel").modal("hide");
				reloadSection("section_phase");
			}
			return result;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
	});
	return false;
}

function deletePhase(idPhase) {

	if (idPhase == null || idPhase == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_phase"));
		if (selectedScenario.length != 1)
			return false;
		idPhase = selectedScenario[0];
	}
	$("#confirm-dialog .modal-body").text(
			MessageResolver("confirm.delete.phase",
					"Are you sure, you want to delete this phase"));
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Phase/Delete/" + idPhase,
			contentType : "application/json",
			async : true,
			success : function(response) {
				if (response["success"] != undefined) {
					reloadSection("section_phase");
					$("#info-dialog .modal-body").html(response["success"]);
					$("#info-dialog").modal("toggle");
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				}
				return false;
			}
		});
	});
	$("#confirm-dialog").modal("toggle");
	return false;
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

function contextMenuHide(context) {
	var elements = $(context).find("li[name]");
	for (var i = 0; i < elements.length; i++) {
		if ($(elements[i]).attr("class") == "divider")
			$(elements[i]).attr("hidden", false);
		else
			$(elements[i]).attr("hidden", true);
	}
	return true;
}

$(function() {
	var $contextMenu = $("#contextMenu");

	if ($contextMenu == null || $contextMenu == undefined)
		return false;
	var select = $contextMenu.find("li[name='select'] a");
	var unSelect = $contextMenu.find("li[name='unselect'] a");
	var editRow = $contextMenu.find("li[name='edit_row'] a");
	var deleteElement = $contextMenu.find("li[name='delete'] a");
	var showMeasures = $contextMenu.find("li[name='show_measures'] a");

	$("#section_analysis").on("contextmenu", "table tbody tr", function(e) {

		// get rights values
		var deleteRight = $("#deleteRight").text();
		var calcRickRegisterRight = $("#calcRickRegisterRight").text();
		var calcActionPlanRight = $("#calcActionPlanRight").text();
		var modifyRight = $("#modifyRight").text();
		var exportRight = $("#exportRight").text();
		var readRight = $("#readRight").text();

		// get missing elements

		var duplicateanalysis = $contextMenu.find("li[name='duplicate'] a");

		var computeactionplan = $contextMenu.find("li[name='cActionPlan'] a");
		var cactionplandivider = $contextMenu.find("li[name='divider_1']");

		var computeriskregister = $contextMenu.find("li[name='cRiskRegister'] a");
		var criskregisterdivider = $contextMenu.find("li[name='divider_2']");

		var exportanalysis = $contextMenu.find("li[name='export'] a");
		var exportanalysisdivider = $contextMenu.find("li[name='divider_3']");

		var rowTrickId = $(e.currentTarget).attr('trick-id');
		var data = $(e.currentTarget).attr('data');

		var rowTrickVersion = $(e.currentTarget).find("td[trick-version]").attr("trick-version");
		var rowRights = $(e.currentTarget).attr('trick-rights-id');
		$contextMenu.attr("trick-selected-id", rowTrickId);

		// select

		if (rowRights <= readRight) {

			select.parent().removeAttr("hidden");
			select.attr("onclick", "javascript:return selectAnalysis(" + rowTrickId + ");");
		} else {
			select.parent().attr("hidden", "true");
			select.removeAttr("onclick");
		}

		// edit

		if (rowRights <= modifyRight) {
			editRow.parent().removeAttr("hidden");
			editRow.attr("onclick", "javascript:return editSingleAnalysis(" + rowTrickId + ");");
			duplicateanalysis.parent().removeAttr("hidden");
			duplicateanalysis.attr("onclick", "javascript:return addHistory(" + rowTrickId + ", '" + rowTrickVersion + "')");
		} else {
			editRow.parent().attr("hidden", "true");
			editRow.removeAttr("onclick");
			duplicateanalysis.parent().attr("hidden", "true");
			duplicateanalysis.removeAttr("onclick");
		}

		// compute action plan

		if (rowRights <= calcActionPlanRight && data == "true") {

			computeactionplan.parent().removeAttr("hidden");
			computeactionplan.attr("onclick", "javascript:return calculateActionPlan(" + rowTrickId + ");");
			cactionplandivider.removeAttr("hidden", "true");
		} else {
			computeactionplan.parent().attr("hidden", "true");
			computeactionplan.removeAttr("onclick");
			cactionplandivider.attr("hidden", "true");
		}

		// compute risk register

		if (rowRights <= calcRickRegisterRight && data == "true") {

			computeriskregister.parent().removeAttr("hidden");
			computeriskregister.attr("onclick", "javascript:return calculateRiskRegister(" + rowTrickId + ");");
			criskregisterdivider.removeAttr("hidden", "true");
		} else {
			computeriskregister.parent().attr("hidden", "true");
			computeriskregister.removeAttr("onclick");
			criskregisterdivider.attr("hidden", "true");
		}

		// export

		if (rowRights <= exportRight) {

			exportanalysis.parent().removeAttr("hidden");
			exportanalysis.attr("onclick", "javascript:return exportAnalysis(" + rowTrickId + ");");
			exportanalysisdivider.removeAttr("hidden", "true");
		} else {
			exportanalysis.parent().attr("hidden", "true");
			exportanalysis.removeAttr("onclick");
			exportanalysisdivider.parent().attr("hidden", "true");
		}

		// delete

		if (rowRights <= deleteRight) {

			deleteElement.parent().removeAttr("hidden");
			deleteElement.attr("onclick", "javascript:return deleteAnalysis(" + rowTrickId + ");");
		} else {
			deleteElement.parent().attr("hidden", "true");
			deleteElement.removeAttr("onclick");
		}

		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_asset").on("contextmenu", "table tbody tr", function(e) {
		contextMenuHide($contextMenu);
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		$contextMenu.attr("trick-selected-id", rowTrickId);
		select.attr("onclick", "return selectAsset('" + rowTrickId + "','true');");
		unSelect.attr("onclick", "return selectAsset('" + rowTrickId + "','false');");
		editRow.attr("onclick", "return editAsset('" + rowTrickId + "');");
		deleteElement.attr("onclick", "return deleteAsset('" + rowTrickId + "');");
		editRow.attr("href", "#addAssetModel");

		if ($(e.currentTarget).attr('trick-selected') == "true") {
			unSelect.parent().attr("hidden", false);
			var assessment = $contextMenu.find("li[name='assessment'] a");
			$(assessment).parent().attr("hidden", false);
			$(assessment).unbind();
			var assessmentViewer = new AssessmentAssetViewer(rowTrickId);
			application.modal["AssessmentViewer"] = assessmentViewer;
			$(assessment).click(function() {
				assessmentViewer.Show();
				$($contextMenu).hide();
				return false;
			});
		} else
			select.parent().attr("hidden", false);

		$(editRow).parent().attr("hidden", false);

		$(deleteElement).parent().attr("hidden", false);

		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_scenario").on("contextmenu", "table tbody tr", function(e) {
		contextMenuHide($contextMenu);
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		select.attr("onclick", "return selectScenario('" + rowTrickId + "',true);");
		unSelect.attr("onclick", "return selectScenario('" + rowTrickId + "',false);");
		editRow.attr("onclick", "return editScenario('" + rowTrickId + "');");
		deleteElement.attr("onclick", "return deleteScenario('" + rowTrickId + "');");
		editRow.attr("href", "#addScenarioModel");
		if ($(e.currentTarget).attr('trick-selected') == "true") {
			unSelect.parent().attr("hidden", false);
			var assessment = $contextMenu.find("li[name='assessment'] a");
			$(assessment).parent().attr("hidden", false);
			$(assessment).unbind();
			var assessmentViewer = new AssessmentScenarioViewer(rowTrickId);
			application.modal["AssessmentViewer"] = assessmentViewer;
			$(assessment).click(function() {
				assessmentViewer.Show();
				$($contextMenu).hide();
				return false;
			});
		} else
			select.parent().attr("hidden", false);

		$(editRow).parent().attr("hidden", false);

		$(deleteElement).parent().attr("hidden", false);

		$contextMenu.attr("trick-selected-id", rowTrickId);
		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_customer").on("contextmenu", "table tbody tr", function(e) {
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		var organisation = $(e.currentTarget).children(":first").text();
		$contextMenu.attr("trick-selected-id", rowTrickId);
		editRow.attr("onclick", "javascript:return editSingleCustomer(" + rowTrickId + ");");
		deleteElement.attr("onclick", "javascript:return deleteCustomer(" + rowTrickId + ",'" + organisation + "');");
		showMeasures.parent().attr("hidden", true);
		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_language").on("contextmenu", "table tbody tr", function(e) {
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		var langname = $(e.currentTarget).children(":eq(1)").text();
		$contextMenu.attr("trick-selected-id", rowTrickId);
		editRow.attr("onclick", "javascript:return editSingleLanguage(" + rowTrickId + ");");
		deleteElement.attr("onclick", "javascript:return deleteLanguage(" + rowTrickId + ",'" + langname + "');");
		showMeasures.parent().attr("hidden", true);
		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_norm").on("contextmenu", "table tbody tr", function(e) {
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		var normname = $(e.currentTarget).children(":first").text();
		$contextMenu.attr("trick-selected-id", rowTrickId);
		editRow.attr("onclick", "javascript:return editSingleNorm(" + rowTrickId + ");");
		deleteElement.attr("onclick", "javascript:return deleteNorm(" + rowTrickId + ",'" + normname + "');");
		showMeasures.attr("onclick", "javascript:return showMeasures(" + rowTrickId + ", 1);");
		showMeasures.parent().attr("hidden", false);
		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_user").on("contextmenu", "table tbody tr", function(e) {
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		var user = $(e.currentTarget).children(":first").text();
		$contextMenu.attr("trick-selected-id", rowTrickId);
		editRow.attr("onclick", "javascript:return editSingleUser(" + rowTrickId + ");");
		deleteElement.attr("onclick", "javascript:return deleteUser(" + rowTrickId + ",'" + user + "');");
		showMeasures.parent().attr("hidden", true);
		$contextMenu.css({
			display : "block",
			left : e.pageX,
			top : $(e.target).position().top + 20
		});
		return false;
	});

	$("#section_phase").on("contextmenu", "table tbody tr", function(e) {
		contextMenuHide($contextMenu);
		$contextMenu.find("li[name='divider_0']").attr("hidden", true);
		var rowTrickId = $(e.currentTarget).attr('trick-id');
		$contextMenu.attr("trick-selected-id", rowTrickId);
		deleteElement.attr("onclick", "return deletePhase('" + rowTrickId + "');");
		$(deleteElement).parent().attr("hidden", false);
		$contextMenu.css({
			display : "block",
			left : e.pageX,
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
			if ($($lis[i]).attr("trick-selectable") == undefined
					|| $($lis[i]).attr("trick-selectable") === "multi")
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

function navToogled(section, navSelected) {
	var currentMenu = $("#" + section + " *[trick-nav-control='" + navSelected
			+ "']");
	if (!currentMenu.length || $(currentMenu).hasClass("disabled"))
		return false;
	var controls = $("#" + section + " *[trick-nav-control]");
	var data = $("#" + section + " *[trick-nav-data]");
	for (var i = 0; i < controls.length; i++) {
		if ($(controls[i]).attr("trick-nav-control") == navSelected)
			$(controls[i]).addClass("disabled");
		else
			$(controls[i]).removeClass("disabled");
		if ($(data[i]).attr("trick-nav-data") != navSelected)
			$(data[i]).hide();
		else
			$(data[i]).show();
	}
	return false;
}

function toggleDisplayAsctionPlanAssets(actionplantable, menu) {
	$(actionplantable + " .actionplanasset").toggleClass("actionplanassethidden");
	if ($(actionplantable + " .actionplanasset").hasClass("actionplanassethidden")) {
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;"+MessageResolver("action.actionplanassets.show", "Show Assets"));	
	} else {
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-up'></span>&nbsp;"+MessageResolver("action.actionplanassets.hide", "Hide Assets"));
	}
	
	return false;	
}

/**
 * Serialize form fields into JSON
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
 * Content Navigation
 */
$(function() {

	if ($('#confirm-dialog').length)
		$('#confirm-dialog').on('hidden.bs.modal', function() {
			$("#confirm-dialog .btn-danger").unbind("click");
		});

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
		} else if (scrollTop < startPosition && scrollTop < previewScrollTop
				&& scrollTop < 50)
			$(".navbar-custom").removeClass("affix");
		previewPosition = currentPosition;
		previewScrollTop = scrollTop;

	});

});

function reloadMeasureRow(idMeasure, norm) {
	$.ajax({
		url : context + "/Measure/Section/" + norm,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var $measure = $(doc).find("#section_measure_" + norm + " tr[trick-id='" + idMeasure + "']");
			if (!$measure.length)
				return false;
			$("#section_measure_" + norm + " tr[trick-id='" + idMeasure + "']").html($measure.html());
			return false;
		}
	});
	return false;
}

function reloadActionPlanEntryRow(idActionPlanEntry, type, idMeasure, norm) {
	$.ajax({
		url : context + "/ActionPlan/RetrieveSingleEntry/" + idActionPlanEntry,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			if (!response.length)
				return false;
			$("#section_actionplan_" + type + " tr[trick-id='" + idActionPlanEntry + "']").replaceWith(response);
			return false;
		}
	});
	reloadMeasureRow(idMeasure, norm);
	return false;
}

function reloadMeausreAndCompliance(norm, idMeasure) {
	reloadMeasureRow(idMeasure, norm);
	compliance(norm);
	return false;
}

function compliance(norm) {
	if (!$('#chart_compliance_' + norm).length)
		return false;
	return $.ajax({
		url : context + "/Measure/Compliance/" + norm,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			$('#chart_compliance_' + norm).highcharts(JSON.parse(response));

		}
	});
}

function evolutionProfitabilityComplianceByActionPlanType(actionPlanType) {
	if (!$('#chart_evolution_profitability_compliance_' + actionPlanType).length)
		return false;
	return $.ajax({
		url : context + "/ActionPlanSummary/Evolution/" + actionPlanType,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			$('#chart_evolution_profitability_compliance_' + actionPlanType)
					.highcharts(response);
		}
	});
}

function budgetByActionPlanType(actionPlanType) {
	if (!$('#chart_budget_' + actionPlanType).length)
		return false;
	return $.ajax({
		url : context + "/ActionPlanSummary/Budget/" + actionPlanType,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			$('#chart_budget_' + actionPlanType).highcharts(response);
		}
	});
}

function summaryCharts() {
	var actionPlanTypes = $("#section_summary *[trick-nav-control]");
	for (var i = 0; i < actionPlanTypes.length; i++) {
		try {
			actionPlanType = $(actionPlanTypes[i]).attr("trick-nav-control");
			evolutionProfitabilityComplianceByActionPlanType(actionPlanType);
			budgetByActionPlanType(actionPlanType);
		} catch (e) {
			console.log(e);
		}

	}
	return false;
}

function reloadCharts() {
	chartALE();
	compliance('27001');
	compliance('27002');
	summaryCharts();

};

function reloadActionPlansAndCharts(){
	reloadSection('section_actionplans');
	compliance('27001');
	compliance('27002');
	summaryCharts();
}

function chartALE() {
	if ($('#chart_ale_scenario_type').length) {
		$.ajax({
			url : context + "/Scenario/Chart/Type/Ale",
			type : "get",
			async : true,
			contentType : "application/json",
			async : true,
			success : function(response) {
				$('#chart_ale_scenario_type').highcharts(JSON.parse(response));
			}
		});
	}
	if ($('#chart_ale_scenario').length) {
		$.ajax({
			url : context + "/Scenario/Chart/Ale",
			type : "get",
			async : true,
			contentType : "application/json",
			async : true,
			success : function(response) {
				$('#chart_ale_scenario').highcharts(JSON.parse(response));
			}
		});
	}

	if ($('#chart_ale_asset').length) {
		$.ajax({
			url : context + "/Asset/Chart/Ale",
			type : "get",
			async : true,
			contentType : "application/json",
			async : true,
			success : function(response) {
				$('#chart_ale_asset').highcharts(JSON.parse(response));
			}
		});
	}
	if ($('#chart_ale_asset_type').length) {
		$.ajax({
			url : context + "/Asset/Chart/AssetType/Ale",
			type : "get",
			async : true,
			contentType : "application/json",
			async : true,
			success : function(response) {
				$('#chart_ale_asset_type').highcharts(JSON.parse(response));
			}
		});
	}
}
