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

function log(msg) {
	setTimeout(function() {
		throw new Error(msg);
	}, 0);
}

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

AssessmentExtendedParameterEditor.prototype = new AssessmentFieldEditor();

function AssessmentExtendedParameterEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text();
	this.acromym = [];

	AssessmentExtendedParameterEditor.prototype.constructor = AssessmentExtendedParameterEditor;

	AssessmentExtendedParameterEditor.prototype.GenerateInputField = function() {
		if ($(this.element).find("select").length)
			return true;
		if (!this.LoadData())
			return true;

		var indexOf = this.acromym.indexOf(this.defaultValue);
		var value = indexOf >= 0 ? this.choose[indexOf] : this.defaultValue;
		this.inputField = document.createElement("input");
		this.inputField.setAttribute("class", "form-control");
		this.inputField.setAttribute("id", "tag_impact");
		this.inputField.setAttribute("placeholder", value);
		this.inputField.setAttribute("value", value);
		this.realValue = this.defaultValue;
		var that = this;
		$(this.inputField).blur(function() {
			return that.Save(that);
		});

		return false;
	};

	AssessmentExtendedParameterEditor.prototype.Show = function() {
		if (this.inputField == null || this.inputField == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		$(this.element).html(this.inputField);
		$("#tag_impact").autocomplete({
			source : this.choose
		});
		$(this.inputField).focus();
		return false;
	};

	AssessmentExtendedParameterEditor.prototype.HasChanged = function() {
		return this.realValue != this.__extractAcronym($(this.inputField).prop("value"));
	};

	AssessmentExtendedParameterEditor.prototype.__extractAcronym = function(value) {
		if (this.choose.indexOf(value) == -1)
			return value;
		return value.split(" (", 1);
	};

	AssessmentExtendedParameterEditor.prototype.Rollback = function() {
		$(this.element).html(this.realValue);
		return false;
	};

	AssessmentExtendedParameterEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/EditField/" + that.controllor,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"'
							+ defaultValueByType(that.__extractAcronym($(that.inputField).prop("value")), that.fieldType, true) + '", "type": "' + that.fieldType + '"}',
					contentType : "application/json",
					success : function(response) {
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

AssessmentImpactFieldEditor.prototype = new AssessmentExtendedParameterEditor();

function AssessmentImpactFieldEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text();
	AssessmentImpactFieldEditor.prototype.constructor = AssessmentImpactFieldEditor;

	AssessmentImpactFieldEditor.prototype.LoadData = function() {
		var $impactAcronyms = $("#Scale_Impact td[trick-field='acronym']");
		var $impactValue = $("#Scale_Impact td[trick-field='value']");
		for (var i = 0; i < $impactAcronyms.length; i++) {
			this.acromym[i] = $($impactAcronyms[i]).text();
			this.choose[i] = this.acromym[i] + " (" + $($impactValue[i]).text() + ")";

		}
		return this.choose.length;
	};
}

AssessmentProbaFieldEditor.prototype = new AssessmentExtendedParameterEditor();

function AssessmentProbaFieldEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text();
	AssessmentProbaFieldEditor.prototype.constructor = AssessmentProbaFieldEditor;

	AssessmentProbaFieldEditor.prototype.LoadData = function() {
		var $probAcronyms = $("#Scale_Probability td[trick-field='acronym']");
		var $probaAcronymsValues = $("#Scale_Probability td[trick-field='value']");
		for (var i = 0; i < $probAcronyms.length; i++) {
			this.acromym[i] = $($probAcronyms[i]).text();
			this.choose[i] = this.acromym[i] + " (" + $($probaAcronymsValues[i]).text() + ")";
		}
		return this.choose.length;
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

	Modal.prototype.DefaultHeaderButton = function() {
		var button_head_close = document.createElement("button");
		button_head_close.setAttribute("class", "close");
		button_head_close.setAttribute("data-dismiss", "modal");
		$(button_head_close).html("&times;");
		this.modal_header.insertBefore(button_head_close, this.modal_header.firstChild);
		return false;
	};

	Modal.prototype.DefaultFooterButton = function() {
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
		return false;
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
		this.modal.setAttribute("data-backdrop", "static");
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

		if (!this.Size(this.modal_head_buttons))
			this.DefaultHeaderButton();
		else
			this.__addHeadButton();

		if (!this.Size(this.modal_footer_buttons))
			this.DefaultFooterButton();
		else
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

function downloadExportedSqLite(idFile) {
	$.fileDownload(context + '/Analysis/Download/' + idFile).fail(function() {
		alert('File download failed!');
	});
	return false;
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
					}, 1500);
				} else {
					setTimeout(function() {
						instance.Remove(taskId);
					}, 3000);
					if (reponse.asyncCallback != undefined && reponse.asyncCallback != null)
						eval(reponse.asyncCallback.action);
					else if (reponse.taskName != null && reponse.taskName != undefined)
						eval(reponse.taskName.action);
				}
				return false;
			}
		});
	};
};

AssessmentViewer.prototype = new Modal();

function AssessmentViewer() {

	AssessmentViewer.prototype.Intialise = function() {
		Modal.prototype.Intialise.call(this);
		$(this.modal_dialog).prop("style", "width: 95%; min-width:1170px; max-width:1300px;");
		return false;

	};

	AssessmentViewer.prototype.DefaultFooterButton = function() {
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

function updateAssessmentAcronym(idParameter, acronym) {
	$.ajax({
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
	return false;
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

function editField(element, controller, id, field, type) {
	var fieldEditor = null;
	if (controller == null || controller == undefined)
		controller = FieldEditor.prototype.__findControllor(element);
	if (controller == "ExtendedParameter")
		fieldEditor = new ExtendedFieldEditor(element);
	else if (controller == "Assessment") {
		field = $(element).attr("trick-field");
		var fieldImpact = [ "impactRep", "impactLeg", "impactOp", "impactFin" ];
		var fieldProba = "likelihood";

		if (fieldImpact.indexOf(field) != -1)
			fieldEditor = new AssessmentImpactFieldEditor(element);
		else if (field == fieldProba) {

			fieldEditor = new AssessmentProbaFieldEditor(element);
		} else
			fieldEditor = new AssessmentFieldEditor(element);
	} else if (controller == "MaturityMeasure")
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
		"section_analysis" : "/Analysis/Section",
		"section_profile_analysis" : "/AnalysisProfile/Section",
		"section_measure" : "/Measure/Section",
		"section_customer" : "/KnowledgeBase/Customer/Section",
		"section_language" : "/KnowledgeBase/Language/Section",
		"section_norm" : "/KnowledgeBase/Norm/Section",
		"section_user" : "/Admin/User/Section",
		"section_actionplans" : "/ActionPlan/Section",
		"section_summary" : "/ActionPlanSummary/Section",
		"section_riskregister" : "/RiskRegister/Section"
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
		},
		"section_analysis" : function() {
			return analysisTableSortable();
		},
		"section_actionplans" : function() {
			compliance('27001');
			compliance('27002');
			reloadSection("section_summary");
			return false;
		},
		"section_summary" : function() {
			summaryCharts();
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
				$("#" + section).replaceWith(newSection);
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
			var selected = $("#section_asset tbody tr[trick-id='" + selectedItem[i] + "']").attr("trick-selected");
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
					if (trickSelect != undefined && trickSelect["success"] != undefined) {
						var row = $("#section_asset tr[trick-id='" + rowTrickId + "']");
						var checked = $("#section_asset tr[trick-id='" + rowTrickId + "'] :checked");
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

	$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.asset", "Are you sure, you want to delete this asset"));
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
	$.ajax({
		url : context + ((rowTrickId == null || rowTrickId == undefined || rowTrickId < 1) ? "/Asset/Add" : "/Asset/Edit/" + rowTrickId),
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
			var selected = $("#section_scenario tbody tr[trick-id='" + selectedItem[i] + "']").attr("trick-selected");
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
	application.modal["AssessmentViewer"] = new AssessmentScenarioViewer(selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
	return false;
}

function displayAssessmentByAsset() {

	var selectedItem = findSelectItemIdBySection("section_asset");
	if (selectedItem.length != 1)
		return false;
	application.modal["AssessmentViewer"] = new AssessmentAssetViewer(selectedItem[0]);
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
					if (trickSelect != undefined && trickSelect["success"] != undefined) {
						var row = $("#section_scenario tr[trick-id='" + rowTrickId + "']");
						var checked = $("#section_scenario tr[trick-id='" + rowTrickId + "'] :checked");
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

	$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.scenario", "Are you sure, you want to delete this scenario"));
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

	$.ajax({
		url : context + (rowTrickId == null || rowTrickId == undefined || rowTrickId < 1 ? "/Scenario/Add" : "/Scenario/Edit/" + rowTrickId),
		contentType : "application/json",
		async : true,
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((addScenarioModal = doc.getElementById("addScenarioModal")) == null)
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
		data : serializeForm(form),
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
	$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.phase", "Are you sure, you want to delete this phase"));
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

/**
 * 
 * @param version1
 * @param version2
 * @param order
 * @returns
 */
function versionComparator(version1, version2, order) {

	// splite versions by "."
	var v1 = version1.split(".", 1);
	var v2 = version2.split(".", 1);

	if (v1 == v2) {
		var index = version1.indexOf(".");
		if (index != -1)
			version1 = version1.substring(index + 2);

		var index2 = version2.indexOf(".");
		if (index2 != -1)
			version2 = version2.substring(index2 + 2);

		if (!version1.length && !version2.length)
			return 0;
		else if (!version1.length)
			return -1 * (order ? 1 : -1);
		else if (!version2.length)
			return 1 * (order ? 1 : -1);

		return versionComparator(version1, version2, order);
	}
	return v1 > v2 ? 1 : -1 * (order ? 1 : -1);

}

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

function navToogled(section, navSelected) {
	var currentMenu = $("#" + section + " *[trick-nav-control='" + navSelected + "']");
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

function hideActionplanAssets(sectionactionplan, menu) {

	var actionplantype = $(sectionactionplan).find(".disabled[trick-nav-control]").attr("trick-nav-control");

	if (!$("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
	}

	initialiseTableFixedHeaderRows('#actionplantable_' + actionplantype);

}

function toggleDisplayActionPlanAssets(sectionactionplan, menu) {

	var actionplantype = $(sectionactionplan).find(".disabled[trick-nav-control]").attr("trick-nav-control");

	$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
	if ($("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
	} else {
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-up'></span>&nbsp;" + MessageResolver("action.actionplanassets.hide", "Hide Assets"));
	}

	initialiseTableFixedHeaderRows("#actionplantable_" + actionplantype);

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

	var l_lang;
	if (navigator.userLanguage) // Explorer
		l_lang = navigator.userLanguage;
	else if (navigator.language) // FF
		l_lang = navigator.language;
	else
		l_lang = "en";
	
	console.log(l_lang);

	if ($('#confirm-dialog').length)
		$('#confirm-dialog').on('hidden.bs.modal', function() {
			$("#confirm-dialog .btn-danger").unbind("click");
		});

	if ($("#addPhaseModel").length) {
		$.getScript(context + "/js/locales/bootstrap-datepicker." + l_lang + ".js");
		$('#addPhaseModel').on('show.bs.modal', function() {
			var lastDate = $("#section_phase td").last();
			if(lastDate.length){
				var beginDate = lastDate.text();
				if(beginDate.match("\\d{4}-\\d{2}-\\d{2}")){
					var endDate = beginDate.split("-");
					endDate[0]++;
					$("#addPhaseModel #phase_begin_date").prop("value", beginDate);
					$("#addPhaseModel #phase_endDate").prop("value", endDate[0]+"-"+endDate[1]+"-"+endDate[2]);
				}
			}
			$("#addPhaseModel input").datepicker({
				format : "yyyy-mm-dd",
				language : l_lang
			});
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

function initialiseTableFixedHeaderRows(con) {
	if (con == undefined || con == null)
		con = "";
	$(con + '.fixedheadertable').stickyRows({
		container : '.panel-body',
		containersToSynchronize : "body"
	});
	$("body").scroll();
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
	$.ajax({
		url : context + "/Measure/Compliance/" + norm,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			$('#chart_compliance_' + norm).highcharts(JSON.parse(response));

		}
	});
	return false;
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
			$('#chart_evolution_profitability_compliance_' + actionPlanType).highcharts(response);
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
	return false;
};

function reloadActionPlansAndCharts() {
	reloadSection('section_actionplans');
}

function updateStatus(progressBar, idTask, callback, status) {
	if (status == null || status == undefined) {
		$.ajax({
			url : context + "/Task/Status/" + idTask,
			async : true,
			contentType : "application/json",
			success : function(reponse) {
				if (reponse.flag == undefined) {
					eval(callback.failed);
					return false;
				}
				return updateStatus(progressBar, idTask, callback, reponse);
			}
		});
	} else {
		if (status.message != null)
			progressBar.Update(status.progress, status.message);
		if (status.flag == 3) {
			setTimeout(function() {
				updateStatus(progressBar, idTask, callback);
			}, 1500);
		} else {
			setTimeout(function() {
				progressBar.Distroy();
			}, 3000);
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

function customerChange(selector) {
	var customer = $(selector).find("option:selected").val();
	$.ajax({
		url : context + "/Analysis/DisplayByCustomer/" + customer,
		type : "get",
		async : true,
		contentType : "application/json",
		async : true,
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("*[id ='section_analysis']");
			$("#section_analysis").replaceWith(newSection);
			analysisTableSortable();
		}
	});
	return false;
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
				$('#chart_ale_scenario_type').highcharts(response);
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
				$('#chart_ale_scenario').highcharts(response);
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
				$('#chart_ale_asset').highcharts(response);
			}
		});
	}
	if ($('#chart_ale_asset_type').length) {
		$.ajax({
			url : context + "/Asset/Chart/Type/Ale",
			type : "get",
			async : true,
			contentType : "application/json",
			async : true,
			success : function(response) {
				$('#chart_ale_asset_type').highcharts(response);
			}
		});
	}
	return false;
}

RRFView.prototype = new Modal();

function RRFView() {

	this.controller = {};

	this.controllers = {};

	this.chart = {};

	this.filter = {
		series : []
	};

	RRFView.prototype.constructor = RRFView;

	RRFView.prototype.Intialise = function() {
		Modal.prototype.Intialise.call(this);
		$(this.modal_dialog).attr("style", "width: 98%; min-width:1170px;");
		$(this.modal_body).attr("style", "max-height: 885px;");
		$(this.modal_footer).remove();
		return false;
	};

	RRFView.prototype.UpdateChart = function(fiedName, value) {
		return this.controller.UpdateChart(fiedName, value);
	};

	RRFView.prototype.ForceSelectOneFirstItem = function(controller) {
		for ( var i in this.controllers)
			if (this.controllers[i] != controller)
				this.controllers[i].SelectFirstItem();
		return false;
	};

	RRFView.prototype.SwitchController = function(controller) {
		if (this.controller == controller)
			return true;
		else if (controller == null || controller == undefined)
			return false;
		if (this.controllers[controller.name] != controller)
			this.controllers[controller.name] = controller;
		if (this.controller != undefined && Object.keys(this.controller).length)
			this.controller.Hide();
		this.controller = controller;
		this.controller.Show();
		return true;
	};

	RRFView.prototype.ReloadChart = function() {
		return this.controller.ReloadChart();
	};

	RRFView.prototype.LoadData = function() {
		var that = this;
		$.ajax({
			url : context + "/Scenario/RRF",
			type : "get",
			contentType : "application/json",
			success : function(response) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				newSection = $(doc).find("*[id ='section_rrf']");
				that.setBody($(newSection)[0].outerHTML);
				// initialise controllers
				that.controllers = {
					"scenario" : new ScenarioRRFController(that, $(that.modal_body).find("#control_rrf_scenario"), "scenario"),
					"measure" : new MeasureRRFController(that, $(that.modal_body).find("#control_rrf_measure"), "measure")
				};

				for ( var controller in that.controllers)
					that.controllers[controller].Initialise();

				that.SwitchController(that.controllers["scenario"]);
				that.ReloadChart();
				return false;
			}
		});
		return true;
	};

	RRFView.prototype.GenerateFilter = function() {
		if (this.chart == undefined || this.chart.series == undefined)
			return;
		this.filter['series'] = [];
		for (var i = 0; i < this.chart.series.length; i++) {
			if (!this.chart.series[i].visible)
				this.filter['series'].push(this.chart.series[i].name);
		}
		return false;
	};

	RRFView.prototype.Onclick = function(element) {
		var parent = $(element).parent();
		$(parent).find(".list-group-item").removeClass("active");
		$(element).addClass("active");
		this.ReloadControls();
		return this.ReloadChart();
	};

	RRFView.prototype.DefaultFooterButton = function() {
		return false;
	};

	RRFView.prototype.OnSliderChange = function(event) {
		$(this.controller.container).find("#" + event.target.id + "_value").prop("value", event.value);
		return this.UpdateChart(event.target.name, event.value);
	};

	RRFView.prototype.Show = function() {
		try {
			if (this.modal_dialog == null || this.modal_dialog == undefined)
				this.Intialise();
			if ($(this.modal_body).empty() && !this.LoadData())
				return false;
			return Modal.prototype.Show.call(this);
		} catch (e) {
			console.log(e);
			return false;
		}
	};
}

function RRFController(rrfView, container, name) {

	this.rrfView = rrfView;

	this.container = container;

	this.name = name;

	this.sliders = {};

	RRFController.prototype.Initialise = function() {
		var that = this;
		var sliders = $(this.container).find(".slider");
		this.sliders = $(sliders).slider();
		this.sliders.on('slideStop', function(event) {
			return that.rrfView.OnSliderChange(event);
		});
	};

	RRFController.prototype.Show = function() {
		if (!Object.keys(this.sliders).length)
			this.Initialise();
		if (this.rrfView.controller != this)
			this.rrfView.SwitchController(this);
		$(this.container).show();
		this.ReloadControls();
		return false;
	};

	RRFController.prototype.Hide = function() {
		$(this.container).hide();
		return false;
	};

	RRFController.prototype.UpdateChart = function(fiedName, value) {
		return false;
	};

	RRFController.prototype.ReloadControls = function() {
		return false;
	};

	RRFController.prototype.ReloadChart = function() {
		false;
	};

	RRFController.prototype.GenerateFilter = function() {
		return this.rrfView.GenerateFilter();
	};

	RRFController.prototype.Onclick = function(element) {
		this.rrfView.SwitchController(this);
		var parent = $(element).parent();
		$(parent).find(".list-group-item").removeClass("active");
		$(element).addClass("active");
		this.rrfView.GenerateFilter();
		this.ReloadChart();
		return this.ReloadControls();
	};

	RRFController.prototype.OnClickFilter = function(event) {
		return this.rrfView.GenerateFilter();
	};
}

ScenarioRRFController.prototype = new RRFController();

function ScenarioRRFController(rrfView, container, name) {

	RRFController.call(this, rrfView, container, name);

	this.idScenario = -1;

	this.DependencyFields = {
		"preventive" : 0.0,
		"limitative" : 0.0,
		"detective" : 0.0,
		"corrective" : 0.0
	};

	ScenarioRRFController.prototype.constructor = ScenarioRRFController;

	ScenarioRRFController.prototype.Initialise = function() {
		var that = this;
		// controllerScenario
		$(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .list-group-item").on("click", function(event) {
			return that.Onclick(event.target);
		});

		// controllerMeasure
		$(that.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .list-group-item").on("click", function(event) {
			return that.OnClickFilter(event);
		});
		return RRFController.prototype.Initialise.call(this);
	};

	ScenarioRRFController.prototype.SelectFirstItem = function() {
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active");
		if (element.length == 1) {
			var item = $(element.parent()).find("a[trick-class='Scenario']:first");
			$(item).addClass("active");
			this.idScenario = parseInt($(item).attr("trick-id"));
			this.rrfView.filter["scenarios"] = [ this.idScenario ];
		}
		return false;
	};

	ScenarioRRFController.prototype.CheckTypeValue = function() {
		var sum = 0;
		for (var i = 0; i < this.sliders.length; i++) {
			if (this.DependencyFields[this.sliders[i].prop("name")] != undefined) {
				var slider = $(this.sliders[i]).slider();
				sum += parseFloat(slider.prop("value"));
			}

		}
		var types = $(this.container).find("*[trick-type='type']");
		if (sum != 1) {
			if ($(types).hasClass("success")) {
				$(types).removeClass("success");
				$(types).addClass("danger");
			}

		} else {
			if ($(types).hasClass("danger")) {
				$(types).removeClass("danger");
				$(types).addClass("success");
			}
		}
		return false;
	};

	ScenarioRRFController.prototype.UpdateChart = function(fiedName, value) {
		var that = this;
		if (this.idScenario < 1 || this.idScenario == undefined)
			this.idScenario = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
		if (this.DependencyFields[fiedName] != undefined)
			this.CheckTypeValue();
		$.ajax({
			url : context + "/Scenario/RRF/Update",
			type : "post",
			data : '{"id":' + that.idScenario + ', "fieldName":"' + fiedName + '", "value":' + value + ', "type": "numeric","filter":' + JSON.stringify(that.rrfView.filter) + '}',
			contentType : "application/json",
			success : function(response) {
				if (response.chart != null && response.chart != undefined)
					that.rrfView.chart = $($(that.rrfView.modal_body).find("#chart_rrf").highcharts(response)).highcharts();
				return false;
			}
		});
		return false;
	};

	ScenarioRRFController.prototype.ReloadControls = function() {
		var that = this;
		if (this.idScenario < 1 || this.idScenario == undefined)
			this.idScenario = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
		$.ajax({
			url : context + "/Scenario/" + that.idScenario,
			type : "get",
			contentType : "application/json",
			success : function(response) {
				if (response.scenarioType != null && response.scenarioType != undefined) {
					$(that.container).find(".slider").unbind("slideStop");
					for (var i = 0; i < that.sliders.length; i++) {
						var clone = $(that.sliders[i]).clone();
						var field = $(clone).prop("name");
						$(that.container).find("#" + $(clone).prop("id") + "_value").prop("value", response[field]);
						$(clone).attr("value", response[field]);
						$(clone).attr("data-slider-value", response[field]);
						$(that.sliders[i]).parent().replaceWith($(clone));
						that.sliders[i] = $(clone).slider();
						that.sliders[i].on("slideStop", function(event) {
							return that.rrfView.OnSliderChange(event);
						});
					}
					that.CheckTypeValue();
				}
				return false;
			}
		});
	};

	ScenarioRRFController.prototype.Onclick = function(element) {
		var trickClass = $(element).attr("trick-class");
		if (trickClass != "Scenario") {
			var item = $(element).attr("trick-class") == undefined ? $(element).parent() : $(element);
			$(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active").removeClass("active");
			$(item).addClass('active');
			return false;
		} else {
			var idScenario = $(element).attr("trick-id");
			if (idScenario != this.idScenario || this.rrfView.controller != this) {
				if (idScenario != this.idScenario) {
					var idScenarioType = $(element).parent().attr('trick-id');
					var scenarioType = $(element).parent().attr('trick-value');
					$(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active").removeClass("active");
					$(this.rrfView.modal_body).find(
							"#selectable_rrf_scenario_controls a[trick-class='ScenarioType'][trick-id='" + idScenarioType + "'][trick-value='" + scenarioType + "']").addClass(
							'active');
				} else if (!$(element).hasClass("active"))
					$(element).addClass("active");
				this.idScenario = idScenario;
				return RRFController.prototype.Onclick.call(this, element);
			} else if (!$(element).hasClass("active"))
				$(element).addClass("active");
		}
		return false;
	};

	ScenarioRRFController.prototype.GenerateFilter = function() {
		RRFController.prototype.GenerateFilter.call(this);
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active");
		if (element.length == 1) {
			this.rrfView.filter["measures"] = $.makeArray($(element.parent()).find("a[trick-class='Measure']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
		} else {
			this.rrfView.filter["measures"] = [];
			for (var i = 0; i < element.length; i++)
				this.rrfView.filter["measures"].push($(element[i]).attr('trick-id'));
		}
		return false;
	};

	ScenarioRRFController.prototype.OnClickFilter = function(event) {
		var element = $(event.target).attr("trick-class") == undefined ? $(event.target).parent() : $(event.target);
		var trickClass = $(element).attr("trick-class");
		var trickId = $(element).attr("trick-id");
		if (trickClass == "Norm") {
			this.rrfView.filter["measures"] = $.makeArray($(element).parent().find("a[trick-class='Measure']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
			this.SelectFirstItem();
			this.rrfView.SwitchController(this);
			return this.ReloadChart();
		}
		this.rrfView.filter["measures"] = [ parseInt(trickId) ];
		return false;
	};

	ScenarioRRFController.prototype.ReloadChart = function() {
		var that = this;
		if (this.idScenario < 1 || this.idScenario == undefined)
			this.idScenario = $(that.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
		if (this.rrfView.filter == undefined || this.rrfView.filter.measures == undefined || !this.rrfView.filter.measures.length)
			this.GenerateFilter();
		$.ajax({
			url : context + "/Scenario/RRF/" + that.idScenario + "/Load",
			type : "post",
			data : JSON.stringify(that.rrfView.filter),
			contentType : "application/json",
			success : function(response) {
				if (response.chart != null && response.chart != undefined)
					that.rrfView.chart = $($(that.rrfView.modal_body).find("#chart_rrf").highcharts(response)).highcharts();
				return false;
			}
		});
	};
}

MeasureRRFController.prototype = new RRFController();

function MeasureRRFController(rrfView, container, name) {

	RRFController.call(this, rrfView, container, name);

	this.idMeasure = -1;

	this.FieldToCategory = {
		direct1 : "Direct1",
		direct2 : "Direc2",
		direct3 : "Direct3",
		direct4 : "Direct4",
		direct5 : "Direct5",
		direct6 : "Direct6",
		direct61 : "Direct6.1",
		direct62 : "Direct6.2",
		direct63 : "Direct6.3",
		direct64 : "Direct6.4",
		direct7 : "Direct7",
		indirect1 : "Indirect1",
		indirect2 : "Indirect2",
		indirect3 : "Indirect3",
		indirect4 : "Indirect4",
		indirect5 : "Indirect5",
		indirect6 : "Indirect6",
		indirect7 : "Indirect7",
		indirect8 : "Indirect8",
		indirect81 : "Indirect8.1",
		indirect82 : "Indirect8.2",
		indirect83 : "Indirect8.3",
		indirect84 : "Indirect8.4",
		indirect9 : "Indirect9",
		indirect10 : "Indirect10",
		confidentiality : "Confidentiality",
		integrity : "Integrity",
		availability : "Availability"
	};

	this.CategoryToField = {
		"Direct1" : "direct1",
		"Direc2" : "direct2",
		"Direct3" : "direct3",
		"Direct4" : "direct4",
		"Direct5" : "direct5",
		"Direct6" : "direct6",
		"Direct6.1" : "direct61",
		"Direct6.2" : "direct62",
		"Direct6.3" : "direct63",
		"Direct6.4" : "direct64",
		"Direct7" : "direct7",
		"Indirect1" : "indirect1",
		"Indirect2" : "indirect2",
		"Indirect3" : "indirect3",
		"Indirect4" : "indirect4",
		"Indirect5" : "indirect5",
		"Indirect6" : "indirect6",
		"Indirect7" : "indirect7",
		"Indirect8" : "indirect8",
		"Indirect8.1" : "indirect81",
		"Indirect8.2" : "indirect82",
		"Indirect8.3" : "indirect83",
		"Indirect8.4" : "indirect84",
		"Indirect9" : "indirect9",
		"Indirect10" : "indirect10",
		"Confidentiality" : "confidentiality",
		"Integrity" : "integrity",
		"Availability" : "availability"
	};

	MeasureRRFController.prototype.constructor = MeasureRRFController;

	MeasureRRFController.prototype.Initialise = function() {
		var that = this;
		// controllerScenario
		$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .list-group-item").on("click", function(event) {
			return that.Onclick(event.target);
		});

		// controllerMeasure
		$(that.rrfView.modal_body).find("#selectable_rrf_scenario_controls .list-group-item").on("click", function(event) {
			return that.OnClickFilter(event);
		});
		return RRFController.prototype.Initialise.call(this);
	};

	MeasureRRFController.prototype.SelectFirstItem = function() {
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active");
		if (element.length == 1) {
			var item = $(element.parent()).find("a[trick-class='Measure']:first");
			$(item).addClass("active");
			this.idMeasure = parseInt($(item).attr("trick-id"));
			this.rrfView.filter["measures"] = [ this.idMeasure ];
		}
		return false;
	};

	MeasureRRFController.prototype.UpdateChart = function(fiedName, value) {
		var that = this;
		if (this.idMeasure < 1 || this.idMeasure == undefined)
			this.idMeasure = $(this.modal_body).find("#selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
		$.ajax({
			url : context + "/Measure/RRF/Update",
			type : "post",
			data : '{"id":' + that.idMeasure + ', "fieldName":"' + fiedName + '", "value":' + value + ', "type": "numeric","filter":' + JSON.stringify(that.rrfView.filter) + '}',
			contentType : "application/json",
			success : function(response) {
				if (response.chart != null && response.chart != undefined)
					that.rrfView.chart = $($(that.rrfView.modal_body).find("#chart_rrf").highcharts(response)).highcharts();
				return false;
			}
		});
	};

	MeasureRRFController.prototype.ReloadControls = function() {
		var that = this;
		if (this.idMeasure < 1 || this.idMeasure == undefined)
			this.idMeasure = $(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
		$.ajax({
			url : context + "/Measure/" + that.idMeasure,
			type : "get",
			contentType : "application/json",
			success : function(response) {
				if (response.measurePropertyList != undefined && response.measurePropertyList != null) {
					// that.SynchronizeSlider(); rejected by product owner
					$(that.container).find(".slider").unbind("slideStop");
					for (var i = 0; i < that.sliders.length; i++) {
						var clone = $(that.sliders[i]).clone();
						var field = that.CategoryToField[$(clone).prop("name")] || $(clone).prop("name");
						var fieldValue = response.measurePropertyList[field];
						if (fieldValue == undefined) {
							for (var j = 0; j < response.assetTypeValues.length; j++) {
								if (response.assetTypeValues[j].assetType.type == field) {
									fieldValue = response.assetTypeValues[j].value;
									break;
								}
							}
							if (fieldValue == undefined)
								continue;
						}
						$(that.container).find("#" + $(clone).prop("id") + "_value").prop("value", fieldValue);
						$(clone).attr("value", fieldValue);
						$(clone).attr("data-slider-value", fieldValue);
						$(that.sliders[i]).parent().replaceWith($(clone));
						that.sliders[i] = $(clone).slider();
						that.sliders[i].on("slideStop", function(event) {
							return that.rrfView.OnSliderChange(event);
						});
					}
				}
				return false;
			}
		});
	};

	MeasureRRFController.prototype.Onclick = function(element) {
		var trickClass = $(element).attr("trick-class");
		if (trickClass != "Measure") {
			var item = $(element).attr("trick-class") == undefined ? $(element).parent() : $(element);
			$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active").removeClass("active");
			$(item).addClass('active');
			return false;
		} else {
			var idMeasure = $(element).attr("trick-id");
			if (idMeasure != this.idMeasure || this.rrfView.controller != this) {
				if (idMeasure != this.idMeasure) {
					var idNorm = $(element).parent().attr('trick-id');
					var chapter = $(element).parent().attr('trick-value');
					$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active").removeClass("active");
					$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls a[trick-class='Norm'][trick-id='" + idNorm + "'][trick-value='" + chapter + "']")
							.addClass('active');
				} else if (!$(element).hasClass("active"))
					$(element).addClass("active");
				this.idMeasure = idMeasure;
				return RRFController.prototype.Onclick.call(this, element);
			} else if (!$(element).hasClass("active"))
				$(element).addClass("active");
		}
		return false;
	};

	MeasureRRFController.prototype.GenerateFilter = function() {
		RRFController.prototype.GenerateFilter.apply(this);
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active");
		if (element.length == 1) {
			this.rrfView.filter["scenarios"] = $.makeArray($(element.parent()).find("a[trick-class='Scenario']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
		} else {
			this.rrfView.filter["scenarios"] = [];
			for (var i = 0; i < element.length; i++)
				this.rrfView.filter["scenarios"].push($(element[i]).attr('trick-id'));
		}
		return false;
	};

	MeasureRRFController.prototype.SynchronizeSlider = function() {
		var category = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active :first").attr("trick-value");
		$(this.container).find("*[trick-class='Category'][trick-value!='" + category + "']").hide();
		$(this.container).find("*[trick-class='Category'][trick-value='" + category + "']").show();
		return false;
	};

	MeasureRRFController.prototype.OnClickFilter = function(event) {
		var element = $(event.target).attr("trick-class") == undefined ? $(event.target).parent() : $(event.target);
		var trickClass = $(element).attr("trick-class");
		var trickId = $(element).attr("trick-id");
		// this.SynchronizeSlider(); rejected by product owner
		if (trickClass == "ScenarioType") {
			this.rrfView.filter["scenarios"] = $.makeArray($(element).parent().find("a[trick-class='Scenario']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
			this.SelectFirstItem();
			this.rrfView.SwitchController(this);
			return this.ReloadChart();
		}
		this.rrfView.filter["scenarios"] = [ parseInt(trickId) ];
		return false;
	};

	MeasureRRFController.prototype.ReloadChart = function() {
		var that = this;
		if (this.idMeasure < 1 || this.idMeasure == undefined)
			this.idMeasure = $(that.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
		if (this.rrfView.filter == undefined || this.rrfView.filter.scenarios == undefined || !this.rrfView.filter.scenarios.length)
			this.GenerateFilter();
		$.ajax({
			url : context + "/Measure/RRF/" + that.idMeasure + "/Load",
			type : "post",
			data : JSON.stringify(that.rrfView.filter),
			contentType : "application/json",
			success : function(response) {
				if (response.chart != null && response.chart != undefined)
					that.rrfView.chart = $($(that.rrfView.modal_body).find("#chart_rrf").highcharts(response)).highcharts();
				return false;
			}
		});
	};
	return false;
}

function editRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.ALL)) {
		var modal = new RRFView();
		modal.Show();
	} else
		permissionError();
	return false;
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

});