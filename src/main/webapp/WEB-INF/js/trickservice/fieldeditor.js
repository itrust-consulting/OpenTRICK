function escape(key, val) {
	if (typeof (val) != "string")
		return val;
	return val.replace(/[\\]/g, '\\\\').replace(/[\/]/g, '\\/').replace(/[\b]/g, '\\b').replace(/[\f]/g, '\\f').replace(/[\n]/g, '\\n').replace(/[\r]/g, '\\r').replace(/[\t]/g,
			'\\t').replace(/[\"]/g, '\\"').replace(/\\'/g, "\\'");
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
	return escape(undefined, value);
}

function updateFieldValue(element, value, type) {
	$(element).parent().text(defaultValueByType(value, type));
}

function FieldValidator() {
	FieldValidator.prototype.Validate = function() {
		throw "Not implemented";
	};
}

FieldBoundedValidator.prototype = new FieldValidator();

function FieldBoundedValidator(min, max) {
	FieldValidator.call(this);
	this.minValue = $.isNumeric(min) ? parseFloat(min) : undefined;
	this.maxValue = $.isNumeric(max) ? parseFloat(max) : undefined;

	FieldBoundedValidator.prototype.Validate = function(element) {
		var $element = $(element), value = parseFloat($(element).val().replace(",","."));
		if (!$.isNumeric(value))
			return false;
		if (this.minValue != undefined && value < this.minValue)
			return false;
		if (this.maxValue != undefined && value > this.maxValue)
			return false;
		return true;
	};

}

function FieldEditor(element, validator) {
	this.element = element;
	this.validator = validator;
	this.controllor = null;
	this.defaultValue = $(element).text().trim();
	this.choose = [];
	this.chooseTranslate = [];
	this.fieldEditor = null;
	this.realValue = null;
	this.fieldName = null;
	this.classId = null;
	this.fieldType = null;
	this.callback = null;
	this.async = true;
	this.backupData = {
		orginalStyle : undefined,
		parentClass : undefined
	};

	FieldEditor.prototype.GeneratefieldEditor = function() {
		var $element = $(this.element);
		if ($element.find("input").length || $element.find("select").length || $element.find("textarea").length)
			return true;
		if (!this.LoadData())
			return true;
		this.backupData.orginalStyle = $element.attr("style");
		if (!this.choose.length) {
			var height = 0, rows = 2;
			if ($element.is("td")) {
				$element.css("width", $element.outerWidth());
				$element.css("height", height = $element.outerHeight());
			} else {
				var $td = $element.closest("td");
				this.backupData.orginalStyle = $td.attr("style");
				$td.css("width", $td.outerWidth());
				$td.css("height", height = $td.outerHeight());
				rows = $element.text().split(/\n/).length;
				if (rows == 1)
					rows = 2;
			}
			if (this.defaultValue.length > 100 || $element.attr("data-trick-content") == "text") {
				this.fieldEditor = document.createElement("textarea");
				this.fieldEditor.setAttribute("style", "width:100%; height:" + (height - 8) + "px; padding:2px;");
			} else {
				this.fieldEditor = document.createElement("input");
				this.realValue = this.element.hasAttribute("data-real-value") ? $element.attr("data-real-value") : null;
				this.fieldEditor.setAttribute("style", "width:100%; height:34px; padding:2px;");
				var minValue = $element.attr("data-trick-min-value"), maxValue = $element.attr("data-trick-max-value");
				if (minValue != undefined || maxValue != undefined)
					this.validator = new FieldBoundedValidator(minValue, maxValue);
			}
		} else {
			$element.css("min-width", "40px");
			$element.css("height", "34px");
			this.fieldEditor = document.createElement("select");
			this.fieldEditor.setAttribute("style", "width:100%; height:34px; padding:2px;");
			for (var i = 0; i < this.choose.length; i++) {
				var option = document.createElement("option");
				option.setAttribute("value", this.choose[i]);
				if (this.chooseTranslate.length) {
					$(option).text(this.chooseTranslate[i]);
					if (this.chooseTranslate[i] == this.defaultValue)
						option.setAttribute("selected", true);
				} else {
					$(option).text(this.choose[i]);
					if (this.choose[i] == this.defaultValue)
						option.setAttribute("selected", true);
				}
				$(option).appendTo($(this.fieldEditor));
			}
		}

		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);
		if (!application.editMode || $element.attr("data-trick-content") != "text") {
			var that = this, $fieldEditor = $(this.fieldEditor);
			$fieldEditor.blur(function() {
				if (that.Validate())
					return that.Save(that);
				else
					$fieldEditor.parent().addClass("has-error").focus();
			});
		}
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
			this.fieldName = $(this.element).attr("data-trick-field");
			this.fieldType = $(this.element).attr("data-trick-field-type");
			return false;
		}
		return true;
	};

	FieldEditor.prototype.__findChoose = function(element) {
		if ($(element).attr("data-trick-choose") != undefined)
			return $(element).attr("data-trick-choose").split(",");
		return [];
	};

	FieldEditor.prototype.__findChooseTranslate = function(element) {
		if ($(element).attr("data-trick-choose-translate") != undefined)
			return $(element).attr("data-trick-choose-translate").split(",");
		return [];
	};

	FieldEditor.prototype.__findControllor = function(element) {
		if ($(element).attr("data-trick-class") != undefined)
			return $(element).attr("data-trick-class");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findControllor($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findClassId = function(element) {
		if ($(element).attr("data-trick-id") != undefined)
			return $(element).attr("data-trick-id");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findClassId($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findCallback = function(element) {
		if ($(element).attr("data-trick-callback") != undefined)
			return $(element).attr("data-trick-callback");
		else if ($(element).parent().prop("tagName") != "BODY")
			return this.__findCallback($(element).parent());
		else
			return null;
	};

	FieldEditor.prototype.__findCallbackPreExec = function(element) {
		if ($(element).attr("data-trick-callback-pre") != undefined)
			return $(element).attr("data-trick-callback-pre");
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
		var $fieldEditor = $(this.fieldEditor), $element = $(this.element), style = $fieldEditor.attr("style");
		$fieldEditor.prop("value", this.realValue != null ? this.realValue : $element.text().trim());
		$fieldEditor.attr("style", style + (style.endsWith(";") ? ";" : "") + "position: relative;")

		$element.html(this.fieldEditor);

		if (!$element.is("td"))
			$element.closest("td").css("padding", "3px");
		else
			$element.css("padding", "3px");
		
		this.backupData.parentClass = $fieldEditor.parent().attr("class")
		if (!application.editMode || $(this.element).attr("data-trick-content") != "text")
			$fieldEditor.focus();
		
		return false;
	};

	FieldEditor.prototype.Validate = function() {
		if (this.validator != null)
			return this.validator.Validate(this.fieldEditor);
		return true;
	};

	FieldEditor.prototype.LoadData = function() {
		var callback = this.__findCallbackPreExec(this.element);
		if (callback != null)
			eval(callback);
		if (!this.choose.length)
			this.choose = this.__findChoose(this.element);
		if (this.choose.length && !this.chooseTranslate.length)
			this.chooseTranslate = this.__findChooseTranslate(element);
		return true;
	};

	FieldEditor.prototype.HasChanged = function() {
		if (this.realValue != null && this.realValue != undefined)
			return $(this.fieldEditor).prop("value") != this.realValue;
		return $(this.fieldEditor).prop("value") != this.defaultValue;
	};

	FieldEditor.prototype.UpdateUI = function() {
		return this.Restore();
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
					url : context + "/Analysis/EditField/" + that.controllor + "/" + that.classId,
					type : "post",
					async : that.async,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
							+ '", "type": "' + that.fieldType + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
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
		return this.Restore(true);
	};

	FieldEditor.prototype.Restore = function(rollback) {
		var $element = $(this.element), $td = $element.is("td") ? $element : $element.closest("td");
		if (this.backupData.orginalStyle)
			$td.attr("style", this.orginalStyle);
		else
			$td.removeAttr("style");

		if (this.backupData.parentClass)
			$(this.fieldEditor).parent().attr("class", this.backupData.parentClass);
		else
			$(this.fieldEditor).parent().removeAttr("class");

		if (rollback)
			$element.text(this.defaultValue);
		else {
			var value = $(this.fieldEditor).prop("value");
			if (this.choose.length && this.chooseTranslate.length) {
				for (var i = 0; i < this.choose.length; i++) {
					if (this.choose[i] == value) {
						$element.text(this.chooseTranslate[i]);
						break;
					}
				}
			} else
				$element.text(value);
		}
		return this;
	};

}

PhaseFieldEditor.prototype = new FieldEditor();

function PhaseFieldEditor(element) {
	FieldEditor.call(this, element);
	PhaseFieldEditor.prototype.GeneratefieldEditor = function() {
		var result = FieldEditor.prototype.GeneratefieldEditor.apply(this);
		if (!result) {

			var l_lang;
			if (navigator.userLanguage) // Explorer
				l_lang = navigator.userLanguage;
			else if (navigator.language) // FF
				l_lang = navigator.language;
			else
				l_lang = "en";

			if (l_lang == "en-US") {
				l_lang = "en";
			}

			var that = this;

			$(this.fieldEditor).unbind("blur");

			$(this.fieldEditor).css({
				'z-index' : 1000
			});

			$(this.fieldEditor).attr("readonly", "true");

			$(this.fieldEditor).datepicker({
				format : "yyyy-mm-dd",
				language : l_lang,
				autoclose : true,
				weekStart : 1,
				todayHighlight : true,
			}).on("hide", function() {
				if ($(that.fieldEditor).val() == "")
					that.Rollback();
				else
					that.Save(that);
			});
		}
		return result;
	}

}

ExtendedFieldEditor.prototype = new FieldEditor();

function ExtendedFieldEditor(element) {

	FieldEditor.call(this, element);
	this.controllor = "ExtendedParameter";

	ExtendedFieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/Analysis/EditField/" + that.controllor + "/" + that.classId,
					type : "post",
					async : that.async,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
							+ '", "type": "' + that.fieldType + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
						if (response["success"] != undefined) {
							reloadSection("section_parameter");
							if (that.fieldName == "value" || that.fieldName == "acronym")
								updateAssessmentAle(true);
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
	FieldEditor.call(this, element);

	this.implementations = [];

	MaturityMeasureFieldEditor.prototype.LoadData = function() {
		var $implementationRate = $("#Maturity_implementation_rate tr[data-trick-class='Parameter']");
		if (!$implementationRate.length)
			return true;
		for (var i = 0; i < $implementationRate.length; i++)
			this.implementations[i] = {
				'id' : $($implementationRate[i]).attr('data-trick-id'),
				'value' : $($implementationRate[i]).find("td[data-trick-field='value']").text()
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
			option.setAttribute("data-trick-id", this.implementations[i].id);
			$(option).text(this.implementations[i].value);
			$(option).appendTo($(this.fieldEditor));
			if (this.defaultValue == this.implementations[i].value)
				$(option).prop("selected", true);
		}

		var that = this;
		this.realValue = this.element.hasAttribute("data-real-value") ? $(this.element).attr("data-real-value") : null;
		if (!application.editMode) {
			$(this.fieldEditor).blur(function() {
				return that.Save(that);
			});
		}
		return false;
	};

}

AssessmentFieldEditor.prototype = new FieldEditor();

function AssessmentFieldEditor(element) {

	FieldEditor.call(this, element);

	AssessmentFieldEditor.prototype.Save = function(that) {
		if (!that.Validate()) {
			that.Rollback();
		} else {
			if (that.HasChanged()) {
				$.ajax({
					url : context + "/Analysis/EditField/" + that.controllor + "/" + that.classId,
					type : "post",
					async : that.async,
					data : '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
							+ '", "type": "' + that.fieldType + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
						if (response["success"] != undefined) {
							if (application.modal["AssessmentViewer"] != undefined)
								application.modal["AssessmentViewer"].Update();
						} else {
							that.Rollback();
							application.modal["AssessmentViewer"].ShowError(response["error"]);
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						that.Rollback();
						application.modal["AssessmentViewer"].ShowError(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
					}
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

	FieldEditor.call(this, element);

	this.acromym = [];

	AssessmentExtendedParameterEditor.prototype.GeneratefieldEditor = function() {
		if ($(this.element).find("select").length || $(this.element).find("input").length)
			return true;
		if (!this.LoadData())
			return true;
		if ($(this.element).attr("data-real-value") != undefined)
			this.realValue = $(this.element).attr("data-real-value").trim();

		var indexOf = this.acromym.indexOf(this.defaultValue);
		var value = indexOf >= 0 ? this.choose[indexOf] : this.realValue != null ? this.realValue : this.defaultValue;

		var width = $(this.element).outerWidth();

		var height = $(this.element).outerHeight();

		$(this.element).css("width", width);

		$(this.element).css("height", height);

		this.fieldEditor = document.createElement("input");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", value);
		this.fieldEditor.setAttribute("value", value);
		this.fieldEditor.setAttribute("style", "width:100%; height:100%;min-width:80px;");

		var that = this;

		if (!application.editMode) {
			$(this.fieldEditor).blur(function() {
				return that.Save(that);
			});
		}

		return false;
	};

	AssessmentExtendedParameterEditor.prototype.Show = function() {
		if (this.fieldEditor == null || this.fieldEditor == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		$(this.element).html(this.fieldEditor);
		$(this.element).css("padding", "3px");
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
		if (this.defaultValue == '')
			this.defaultValue = 0;
		$(this.element).html(this.defaultValue);
		$(this.element).css("padding", "5px");
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

	FieldEditor.call(this, element);

	AssessmentImpactFieldEditor.prototype.LoadData = function() {
		var $impactAcronyms = $("#Scale_Impact td[data-trick-field='acronym']");
		var $impactValue = $("#Scale_Impact td[data-trick-field='value']");
		for (var i = 0; i < $impactAcronyms.length; i++) {
			this.acromym[i] = $($impactAcronyms[i]).text();
			this.choose[i] = this.acromym[i] + " (" + $($impactValue[i]).text() + ")";
		}
		return this.choose.length;
	};
}

AssessmentProbaFieldEditor.prototype = new FieldEditor();

function AssessmentProbaFieldEditor(element) {

	FieldEditor.call(this, element);

	this.acromym = [];

	AssessmentProbaFieldEditor.prototype.LoadData = function() {
		var $probAcronyms = $("#Scale_Probability td[data-trick-field='acronym']");
		var $probaAcronymsValues = $("#Scale_Probability td[data-trick-field='value']");
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

		var width = $(this.element).outerWidth();

		var height = $(this.element).outerHeight();

		$(this.element).css("width", width);

		$(this.element).css("height", height);

		this.fieldEditor = document.createElement("select");
		this.fieldEditor.setAttribute("style", "width:100%;height:36px;min-width:90px;padding:2px;");
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
		if (!application.editMode) {
			$(this.fieldEditor).blur(function() {
				return that.Save(that);
			});
		}
		return false;
	};

	AssessmentProbaFieldEditor.prototype.Save = function(that) {
		return new AssessmentFieldEditor().Save(that);
	};

}

function SelectText(element) {
	var doc = document, text = doc.getElementById(element), range, selection;
	if (doc.body.createTextRange) {
		range = document.body.createTextRange();
		range.moveToElementText(text);
		range.select();
	} else if (window.getSelection) {
		selection = window.getSelection();
		range = document.createRange();
		range.selectNodeContents(text);
		selection.removeAllRanges();
		selection.addRange(range);
	}
}

function disableEditMode() {
	if (!application.editMode)
		return false;
	application.editMode = false
	$("li[role='enterEditMode']").removeClass("disabled");
	$("li[role='leaveEditMode']").addClass("disabled");

	$(application.fieldEditors).each(function() {
		this.async = false;
		this.Save(this);
	});
	return false;
}

function enableEditMode() {
	if (application.editMode)
		return false;
	application.editMode = true;
	$("li[role='leaveEditMode']").removeClass("disabled");
	$("li[role='enterEditMode']").addClass("disabled");
	application.fieldEditors = [];
	var $data = application.modal["AssessmentViewer"] ? $(application.modal["AssessmentViewer"].modal_body).find("[data-trick-content='text']")
			: $(".tab-pane.active [data-trick-content='text']");
	$data.each(function() {
		var fieldEditor = editField(this);
		if (fieldEditor != null)
			application.fieldEditors.push(fieldEditor);
	});
	return false;
}

function editField(element, controller, id, field, type) {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	var fieldEditor = null;
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		if (controller == null || controller == undefined)
			controller = FieldEditor.prototype.__findControllor(element);
		if (controller == "ExtendedParameter")
			fieldEditor = new ExtendedFieldEditor(element);
		else if (controller == "Assessment") {
			field = $(element).attr("data-trick-field");
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
		else if (controller == "Phase")
			fieldEditor = new PhaseFieldEditor(element);
		else
			fieldEditor = new FieldEditor(element);
		if (!fieldEditor.Initialise())
			fieldEditor.Show();
	}
	return fieldEditor;
}
