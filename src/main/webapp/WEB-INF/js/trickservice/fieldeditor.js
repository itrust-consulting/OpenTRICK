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
	return escape(undefined, value);
}

function updateFieldValue(element, value, type) {
	$(element).parent().text(defaultValueByType(value, type));
}

function FieldEditor(element, validator) {
	this.element = element;
	this.validator = validator;
	this.controllor = null;
	this.defaultValue = $(element).text().trim();
	this.choose = [];
	this.fieldEditor = null;
	this.realValue = null;
	this.fieldName = null;
	this.classId = null;
	this.fieldType = null;
	this.callback = null;

	FieldEditor.prototype.GeneratefieldEditor = function() {
		if ($(this.element).find("input").length || $(this.element).find("select").length || $(this.element).find("textarea").length)
			return true;
		if (!this.LoadData())
			return true;
		if (!this.choose.length) {
			if (this.defaultValue.length > 100 || $(this.element).attr("trick-content") == "text") {
				this.fieldEditor = document.createElement("textarea");
				this.fieldEditor.setAttribute("style", "min-width:300px;");
			} else {
				this.fieldEditor = document.createElement("input");
				this.realValue = this.element.hasAttribute("real-value") ? $(this.element).attr("real-value") : null;
				this.fieldEditor.setAttribute("style", "min-width:80px;");
			}
		} else {
			this.fieldEditor = document.createElement("select");
			this.fieldEditor.setAttribute("style", "min-width:80px;");
			for (var i = 0; i < this.choose.length; i++) {
				var option = document.createElement("option");
				option.setAttribute("value", this.choose[i]);
				$(option).text(this.choose[i]);
				if (this.choose[i] == this.defaultValue)
					option.setAttribute("selected", true);
				$(option).appendTo($(this.fieldEditor));
			}
		}
		var that = this;
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);
		$(this.fieldEditor).blur(function() {
			return that.Save(that);
		});
		return false;
	};

	FieldEditor.prototype.HasAttr = function(element, attribute) {
		var attr = $(element).attr(attribute);
		return typeof attr !== 'undefined' && attr !== false;
	};

	FieldEditor.prototype.Initialise = function() {

		if (!this.GeneratefieldEditor()) {
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
		if (this.fieldEditor == null || this.fieldEditor == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		$(this.fieldEditor).prop("value", this.realValue != null ? this.realValue : $(this.element).text().trim());
		$(this.element).html(this.fieldEditor);
		$(this.fieldEditor).focus();
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
			return $(this.fieldEditor).prop("value") != this.realValue;
		return $(this.fieldEditor).prop("value") != this.defaultValue;
	};

	FieldEditor.prototype.UpdateUI = function() {
		$(this.element).text($(this.fieldEditor).prop("value"));
		return false;
	};

	FieldEditor.prototype.GetValue = function() {
		return $(this.fieldEditor).val();
	};

	FieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/EditField/" + that.controllor + "/" + that.classId,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
							+ '", "type": "' + that.fieldType + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response) {
						if (response["success"] != undefined) {
							that.UpdateUI();
							if (that.callback != null && that.callback != undefined)
								setTimeout(that.callback, 10);
						} else if (response["error"] != undefined) {
							$("#alert-dialog .modal-body").html(response["error"]);
							$("#alert-dialog").modal("toggle");
						} else {
							$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
							$("#alert-dialog").modal("toggle");
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						that.Rollback();
						$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
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
	this.defaultValue = $(element).text().trim();

	ExtendedFieldEditor.prototype.constructor = ExtendedFieldEditor;

	ExtendedFieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/EditField/" + that.controllor + "/" + that.classId,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
							+ '", "type": "' + that.fieldType + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response) {
						if (response["success"] != undefined) {
							if (that.fieldName == "acronym")
								setTimeout("updateAssessmentAcronym('" + that.classId + "', '" + that.defaultValue + "')", 100);
							return reloadSection("section_parameter");
						} else if (response["error"] != undefined) {
							$("#alert-dialog .modal-body").html(response["error"]);
							$("#alert-dialog").modal("toggle");
						} else {
							$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
							$("#alert-dialog").modal("toggle");
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						that.Rollback();
						$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
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
	this.defaultValue = $(element).text().trim();
	this.implementations = [];

	MaturityMeasureFieldEditor.prototype.constructor = MaturityMeasureFieldEditor;

	MaturityMeasureFieldEditor.prototype.LoadData = function() {
		var $implementationRate = $("#Maturity_implementation_rate tr[trick-class='Parameter']");
		if (!$implementationRate.length)
			return true;
		for (var i = 0; i < $implementationRate.length; i++)
			this.implementations[i] = {
				'id' : $($implementationRate[i]).attr('trick-id'),
				'value' : $($implementationRate[i]).find("td[trick-field='value']").text()
			};
		return !this.implementations.length;
	};

	MaturityMeasureFieldEditor.prototype.GeneratefieldEditor = function() {
		if ($(this.element).find("select").length)
			return true;
		if (this.LoadData())
			return true;
		this.fieldEditor = document.createElement("select");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("style", "min-width:70px;");
		this.fieldEditor.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);
		for ( var i in this.implementations) {
			var option = document.createElement("option");
			option.setAttribute("value", this.implementations[i].value);
			option.setAttribute("trick-id", this.implementations[i].id);
			$(option).text(this.implementations[i].value);
			$(option).appendTo($(this.fieldEditor));
			if (this.defaultValue == this.implementations[i].value)
				$(option).prop("selected", true);
		}

		var that = this;
		this.realValue = this.element.hasAttribute("real-value") ? $(this.element).attr("real-value") : null;
		$(this.fieldEditor).blur(function() {
			return that.Save(that);
		});
		return false;
	};

}

AssessmentFieldEditor.prototype = new FieldEditor();

function AssessmentFieldEditor(element) {

	this.element = element;

	this.defaultValue = $(element).text().trim();

	AssessmentFieldEditor.prototype.constructor = AssessmentFieldEditor;

	AssessmentFieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/EditField/" + that.controllor + "/" + that.classId,
					type : "post",
					async : true,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
							+ '", "type": "' + that.fieldType + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response) {
						if (response["success"] != undefined) {
							if (application.modal["AssessmentViewer"] != undefined)
								application.modal["AssessmentViewer"].Load();
						} else {
							that.Rollback();
							application.modal["AssessmentViewer"].ShowError(response["error"]);
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						that.Rollback();
						application.modal["AssessmentViewer"].ShowError(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
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

AssessmentExtendedParameterEditor.prototype = new FieldEditor();

function AssessmentExtendedParameterEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text().trim();
	this.acromym = [];

	AssessmentExtendedParameterEditor.prototype.constructor = AssessmentExtendedParameterEditor;

	AssessmentExtendedParameterEditor.prototype.GeneratefieldEditor = function() {
		if ($(this.element).find("select").length)
			return true;
		if (!this.LoadData())
			return true;
		if ($(this.element).attr("real-value") != undefined)
			this.realValue = $(this.element).attr("real-value").trim();

		var indexOf = this.acromym.indexOf(this.defaultValue);
		var value = indexOf >= 0 ? this.choose[indexOf] : this.realValue != null ? this.realValue : this.defaultValue;
		this.fieldEditor = document.createElement("input");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", value);
		this.fieldEditor.setAttribute("value", value);
		this.fieldEditor.setAttribute("style", "min-width:80px");
		var that = this;
		$(this.fieldEditor).blur(function() {
			return that.Save(that);
		});

		return false;
	};

	AssessmentExtendedParameterEditor.prototype.Show = function() {
		if (this.fieldEditor == null || this.fieldEditor == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		$(this.element).html(this.fieldEditor);
		var data = [];
		for (var i = 0; i < this.choose.length; i++)
			data.push({
				value : this.choose[i]
			});

		var iteams = new Bloodhound({
			datumTokenizer : function(d) {
				return Bloodhound.tokenizers.whitespace(d.value);
			},
			queryTokenizer : Bloodhound.tokenizers.whitespace,
			limit : this.choose.length,
			local : data
		});
		iteams.initialize();
		$(this.fieldEditor).typeahead(null, {
			displayKey : 'value',
			source : iteams.ttAdapter()
		});
		$(this.fieldEditor).focus();
		return false;
	};

	AssessmentExtendedParameterEditor.prototype.HasChanged = function() {
		return this.defaultValue != this.__extractAcronym(this.GetValue());
	};

	AssessmentExtendedParameterEditor.prototype.__extractAcronym = function(value) {
		if (this.choose.indexOf(value) == -1)
			return value;
		return value.split(" (", 1);
	};

	AssessmentExtendedParameterEditor.prototype.Rollback = function() {
		$(this.element).html(this.defaultValue);
		return false;
	};

	AssessmentExtendedParameterEditor.prototype.GetValue = function() {
		return this.__extractAcronym(FieldEditor.prototype.GetValue.call(this));
	};

	AssessmentExtendedParameterEditor.prototype.Save = function(that) {
		return new AssessmentFieldEditor().Save(that);
	};
}

AssessmentImpactFieldEditor.prototype = new AssessmentExtendedParameterEditor();

function AssessmentImpactFieldEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text().trim();
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

AssessmentProbaFieldEditor.prototype = new FieldEditor();

function AssessmentProbaFieldEditor(element) {
	this.element = element;
	this.defaultValue = $(element).text().trim();
	this.acromym = [];

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

	AssessmentProbaFieldEditor.prototype.GeneratefieldEditor = function() {
		if ($(this.element).find("input").length || $(this.element).find("select").length)
			return true;
		if (!this.LoadData())
			return true;
		this.fieldEditor = document.createElement("select");
		this.fieldEditor.setAttribute("style", "min-width:80px;");
		for (var i = 0; i < this.choose.length; i++) {
			var option = document.createElement("option");
			option.setAttribute("value", this.acromym[i]);
			$(option).text(this.choose[i]);
			if (this.acromym[i] == this.defaultValue)
				option.setAttribute("selected", true);
			$(option).appendTo($(this.fieldEditor));
		}
		var that = this;
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);
		$(this.fieldEditor).blur(function() {
			return that.Save(that);
		});
		return false;
	};

	AssessmentProbaFieldEditor.prototype.Save = function(that) {
		return new AssessmentFieldEditor().Save(that);
	};

}

function editField(element, controller, id, field, type) {
	idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
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
			else if (field == fieldProba)
				fieldEditor = new AssessmentProbaFieldEditor(element);
			else
				fieldEditor = new AssessmentFieldEditor(element);
		} else if (controller == "MaturityMeasure")
			fieldEditor = new MaturityMeasureFieldEditor(element);
		else
			fieldEditor = new FieldEditor(element);

		if (!fieldEditor.Initialise())
			fieldEditor.Show();
	} else
		permissionError();
}
