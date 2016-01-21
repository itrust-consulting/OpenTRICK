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
		var $element = $(element), value = parseFloat($(element).val().replace(",", "."));
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
	this.tabPress = undefined;
	this.backupData = {
		orginalStyle : undefined,
		parentClass : undefined,
		width : undefined
	};

	FieldEditor.prototype.GeneratefieldEditor = function() {
		var $element = $(this.element);
		if ($element.find("input,select,textarea").length)
			return true;
		if (!this.LoadData())
			return true;
		var $fieldEditor, height = 0, width = 0, minWidth = 0, rows = 2, $td;
		if (!this.choose.length) {
			if ($element.is("td"))
				$td = $element;
			else {
				$td = $element.closest("td");
				rows = $element.text().split(/\n/).length;
				if (rows == 1)
					rows = 2;
			}
			width = $td.outerWidth();
			height = $td.outerHeight();
			if (this.defaultValue.length > 100 || $element.attr("data-trick-content") == "text")
				this.fieldEditor = document.createElement("textarea");
			else {
				this.fieldEditor = document.createElement("input");
				if (this.element.hasAttribute("data-real-value"))
					this.realValue = this.element.getAttribute("data-real-value");
				var minValue = $element.attr("data-trick-min-value"), maxValue = $element.attr("data-trick-max-value");
				if (minValue != undefined || maxValue != undefined)
					this.validator = new FieldBoundedValidator(minValue, maxValue);
			}
			$fieldEditor = $(this.fieldEditor)

		} else {
			$td = $element;
			minWidth = 50;
			height = $td.outerHeight();
			this.fieldEditor = document.createElement("select");
			$fieldEditor = $(this.fieldEditor);
			for (var i = 0; i < this.choose.length; i++) {
				var option = document.createElement("option"), $option = $(option);
				option.setAttribute("value", this.choose[i]);
				if (this.chooseTranslate.length) {
					$option.text(this.chooseTranslate[i]);
					if (this.chooseTranslate[i] == this.defaultValue)
						option.setAttribute("selected", true);
				} else {
					$option.text(this.choose[i]);
					if (this.choose[i] == this.defaultValue)
						option.setAttribute("selected", true);
				}
				$option.appendTo($fieldEditor);
			}
		}
		this.backupData.width = $td.width();
		this.backupData.orginalStyle = $td.attr("style");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("style", "padding: 4px; margin-left:auto; margin-right:auto; height:" + (height - 2) + "px;");
		this.fieldEditor.setAttribute("placeholder", this.realValue == null || this.realValue == undefined ? this.defaultValue : this.realValue);
		$td.css({
			"height" : height,
			"width" : width ? width : "auto",
			"min-width" : minWidth ? minWidth : 'auto',
			"padding" : 0
		});
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
		var content = $(element).attr("data-trick-choose");
		if (content != undefined)
			return content.split(",");
		return [];
	};

	FieldEditor.prototype.__findChooseTranslate = function(element) {
		var content = $(element).attr("data-trick-choose-translate");
		if (content != undefined)
			return content.split(",");
		return [];
	};

	FieldEditor.prototype.__findControllor = function(element) {
		return this.__finder(element, "data-trick-class");
	};

	FieldEditor.prototype.__findClassId = function(element) {
		return this.__finder(element, "data-trick-id");
	};

	FieldEditor.prototype.__findCallback = function(element) {
		return this.__finder(element, "data-trick-callback");
	};

	FieldEditor.prototype.__findCallbackPreExec = function(element) {
		return this.__finder(element, "data-trick-callback-pre");
	};

	FieldEditor.prototype.__finder = function(element, attr) {
		var $element = $(element);
		if (!$element.length)
			return null;
		var content = $element.attr(attr);
		if (typeof content === "undefined")
			return this.__finder($element.closest("[" + attr + "]"), attr);
		return content;
	}

	FieldEditor.prototype.__findNextEditable = function($tr, isNext) {
		var $nextTr = isNext ? $tr.nextAll("tr[data-trick-id]:first") : $tr.prevAll("tr[data-trick-id]:first")
		if (!$nextTr.length)
			return $nextTr;
		var $next = $nextTr.find("[onclick*='editField']" + (isNext ? ":first" : ":last"))
		if (!$next.length)
			return this.__findNextEditable($nextTr, isNext)
		return $next;
	};

	FieldEditor.prototype.__supportTabNav = function() {
		var that = this;
		$(this.fieldEditor).keypress(function(e) {
			if (e.keyCode == 9) {
				if (e.shiftKey)
					that.tabPress = "prev";
				else
					that.tabPress = "next";
			}
		});
		return this;
	}

	FieldEditor.prototype.Show = function() {
		if (this.fieldEditor == null || this.fieldEditor == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;
		var $fieldEditor = $(this.fieldEditor), $element = $(this.element);
		if (!$fieldEditor.is("select"))
			$fieldEditor.val(this.realValue == null ? $element.text().trim() : this.realValue);
		$element.html($fieldEditor);
		this.backupData.parentClass = $fieldEditor.parent().attr("class")
		if (!application.editMode || $element.attr("data-trick-content") != "text") {
			this.__supportTabNav();
			$fieldEditor.focus();
		}
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
		if (this.realValue == null || this.realValue == undefined)
			return this.GetValue() != this.defaultValue;
		else
			return this.GetValue() != this.realValue;
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
							var callback = that.callback;
							that.Restore();
							if (callback != null && callback != undefined)
								setTimeout(callback, 0);
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

		if (rollback) {
			$element.text(this.defaultValue);
			if ($td.parent().attr("data-force-callback"))
				setTimeout(this.callback, 0);
		} else {
			var value = this.GetValue();
			if (this.choose.length && this.chooseTranslate.length) {
				for (var i = 0; i < this.choose.length; i++) {
					if (this.choose[i] == value) {
						$element.text(this.chooseTranslate[i]);
						break;
					}
				}
			} else
				$element.text(value);

			if ($td.width != this.backupData.width)
				window.dispatchEvent(new Event('resize'));
		}

		if (this.tabPress) {
			var isNext = this.tabPress == "next", $next = isNext ? $td.nextAll("[onclick*='editField']:first") : $td.prevAll("[onclick*='editField']:first");
			if (!$next.length)
				$next = this.__findNextEditable($td.parent(), isNext);
			$next.click();
		}
		delete this;
		return null;
	};

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
							var computeAle = that.fieldName == "value" || that.fieldName == "acronym";
							that.UpdateUI();
							reloadSection("section_parameter");
							if (computeAle)
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
						$("#alert-dialog").modal("show");
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
		var $implementationRates = $("#Maturity_implementation_rate tr[data-trick-class='Parameter']");
		if (!$implementationRates.length)
			return true;
		for (var i = 0; i < $implementationRates.length; i++) {
			var $implementationRate = $($implementationRates[i]);
			this.implementations[i] = {
				'id' : $implementationRate.attr('data-trick-id'),
				'value' : $implementationRate.find("td[data-trick-field='value']").text()
			};
		}
		return !this.implementations.length;
	};

	MaturityMeasureFieldEditor.prototype.GeneratefieldEditor = function() {
		var $element = $(this.element);
		if ($element.find("select").length)
			return true;
		if (this.LoadData())
			return true;

		this.fieldEditor = document.createElement("select");

		var that = this, height = $element.outerHeight(), minWidth = 45, $fieldEditor = $(this.fieldEditor);

		this.fieldEditor.setAttribute("class", "form-control");
		this.realValue = this.element.hasAttribute("data-real-value") ? $element.attr("data-real-value") : null;
		this.fieldEditor.setAttribute("style", "padding: 4px; margin-left:auto; margin-right:auto; height:" + (height - 2) + "px;");
		this.fieldEditor.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);

		for ( var i in this.implementations) {
			var option = document.createElement("option"), $option = $(option);
			option.setAttribute("value", this.implementations[i].value);
			option.setAttribute("data-trick-id", this.implementations[i].id);

			if (this.defaultValue == this.implementations[i].value)
				option.setAttribute("selected", true);

			$option.text(this.implementations[i].value);
			$option.appendTo($fieldEditor);
		}

		this.backupData.width = $element.width();
		this.backupData.orginalStyle = $element.attr("style");

		$element.css({
			"height" : height,
			"min-width" : minWidth,
			"padding" : 0
		});

		if (!application.editMode) {
			$fieldEditor.blur(function() {
				return that.Save(that);
			});
		}
		return false;
	};

}

AssessmentFieldEditor.prototype = new FieldEditor();

function AssessmentFieldEditor(element) {

	FieldEditor.call(this, element);

	AssessmentFieldEditor.prototype.Rollback = function() {
		FieldEditor.prototype.Rollback.call(this);
		if (application["estimation-helper"] != undefined)
			application["estimation-helper"].tryUpdate();
		return this;
	};

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
							that.UpdateUI();
							if (application["estimation-helper"] != undefined) {
								application["estimation-helper"].tryUpdate(that.classId);
								reloadSection([ "section_asset", "section_scenario" ], undefined, true);
								chartALE();
							}
						} else {
							that.Rollback();
							application["estimation-helper"].error(response["error"]);
						}
						return true;
					},
					error : function(jqXHR, textStatus, errorThrown) {
						that.Rollback();
						application["estimation-helper"].error(MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
					}
				});

			} else
				that.Rollback();

		}
		return false;
	};
}

AssessmentExtendedParameterEditor.prototype = new AssessmentFieldEditor();

function AssessmentExtendedParameterEditor(element) {

	AssessmentFieldEditor.call(this, element);

	this.acromym = [];

	AssessmentExtendedParameterEditor.prototype.GeneratefieldEditor = function() {
		var $element = $(this.element);
		if ($element.find("select,input,textarea").length)
			return true;
		if (!this.LoadData())
			return true;
		if (this.element.hasAttribute("data-real-value"))
			this.realValue = $element.attr("data-real-value").trim();

		var that = this, indexOf = this.acromym.indexOf(this.defaultValue), value = indexOf >= 0 ? this.choose[indexOf] : this.realValue != null ? this.realValue
				: this.defaultValue, height = $element.outerHeight();

		this.fieldEditor = document.createElement("input");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", value);
		this.fieldEditor.setAttribute("value", value);
		this.fieldEditor.setAttribute("style", "padding: 4px; margin-left:auto; margin-right:auto; height:" + (height - 2) + "px;");

		this.backupData.width = $element.width();
		this.backupData.orginalStyle = $element.attr("style");

		$element.css({
			"min-width" : 50,
			"height" : height,
			"padding" : 0
		});

		if (!application.editMode) {
			$(this.fieldEditor).blur(function() {
				return that.Save(that);
			});
		}

		return false;
	};

	AssessmentExtendedParameterEditor.prototype.__extractAcronym = function(value) {
		if (this.choose.indexOf(value) == -1)
			return value;
		return value.split(" (", 1)[0];
	};

	AssessmentExtendedParameterEditor.prototype.GetValue = function() {
		return this.__extractAcronym(FieldEditor.prototype.GetValue.call(this));
	};
}

AssessmentImpactFieldEditor.prototype = new AssessmentExtendedParameterEditor();

function AssessmentImpactFieldEditor(element) {

	AssessmentExtendedParameterEditor.call(this, element);

	AssessmentImpactFieldEditor.prototype.LoadData = function() {
		var $impactAcronyms = $("#Scale_Impact td[data-trick-field='acronym']"), $impactValue = $("#Scale_Impact td[data-trick-field='value']");
		for (var i = 0; i < $impactAcronyms.length; i++) {
			this.acromym[i] = $($impactAcronyms[i]).text();
			this.choose[i] = this.acromym[i] + " (" + $($impactValue[i]).text() + ")";
		}
		return this.choose.length;
	};

	AssessmentImpactFieldEditor.prototype.Show = function() {
		if (this.fieldEditor == null || this.fieldEditor == undefined)
			return false;
		if (this.element == null || this.element == undefined)
			return false;

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
		$(this.element).html(this.fieldEditor);
		$(this.fieldEditor).typeahead(null, {
			displayKey : 'value',
			source : iteams.ttAdapter()
		}).focus();
		this.__supportTabNav();
		return false;
	};
}

AssessmentProbaFieldEditor.prototype = new AssessmentExtendedParameterEditor();

function AssessmentProbaFieldEditor(element) {

	AssessmentExtendedParameterEditor.call(this, element);

	AssessmentProbaFieldEditor.prototype.LoadData = function() {
		var $probAcronyms = $("#Scale_Probability td[data-trick-field='acronym']"), $probaAcronymsValues = $("#Scale_Probability td[data-trick-field='value']");
		for (var i = 0; i < $probAcronyms.length; i++) {
			this.acromym[i] = $($probAcronyms[i]).text();
			this.choose[i] = this.acromym[i] + " (" + $($probaAcronymsValues[i]).text() + ")";
		}
		return this.choose.length;
	};

	AssessmentProbaFieldEditor.prototype.GeneratefieldEditor = function() {
		var $element = $(this.element);
		if ($element.find("input,select,textarea").length)
			return true;
		if (!this.LoadData())
			return true;
		if (this.element.hasAttribute("data-real-value"))
			this.realValue = this.element.getAttribute("data-real-value");
		this.fieldEditor = document.createElement("select");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", this.realValue == null || this.realValue == undefined ? this.defaultValue : this.realValue == '0' ? this.acromym[0]
				: this.realValue);
		var that = this, height = $element.outerHeight(), $fieldEditor = $(this.fieldEditor);
		this.fieldEditor.setAttribute("style", "padding: 4px; margin-left:auto; margin-right:auto; height:" + (height - 2) + "px;");
		for (var i = 0; i < this.choose.length; i++) {
			var option = document.createElement("option"), $option = $(option);
			option.setAttribute("value", this.acromym[i]);
			if (this.acromym[i] == this.defaultValue)
				option.setAttribute("selected", "selected");
			$option.text(this.choose[i]).appendTo($fieldEditor);
		}

		this.backupData.width = $element.width();
		this.backupData.orginalStyle = $element.attr("style");

		$element.css({
			"min-width" : 60,
			"height" : height,
			"padding" : 0
		});

		if (!application.editMode) {
			$fieldEditor.blur(function() {
				return that.Save(that);
			});
		}
		return false;
	};

	AssessmentProbaFieldEditor.prototype.__extractAcronym = function(value) {
		var value = AssessmentExtendedParameterEditor.prototype.__extractAcronym.call(this, value);
		if (value == this.acromym[0])
			return '0';
		return value;
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
	try {
		$("#progress-dialog").modal("show");
		application.editMode = false
		$("li[role='enterEditMode']").removeClass("disabled");
		$("li[role='leaveEditMode']").addClass("disabled");
		$(application.fieldEditors).each(function() {
			this.Save(this);
		});
		return false;
	} finally {
		$("#progress-dialog").modal("hide");
	}
}

function enableEditMode() {
	if (application.editMode)
		return false;
	try {
		$("#progress-dialog").modal("show");
		application.editMode = true;
		$("li[role='leaveEditMode']").removeClass("disabled");
		$("li[role='enterEditMode']").addClass("disabled");
		application.fieldEditors = [];
		var $data = $(".tab-pane.active [data-trick-content='text']");
		$data.each(function() {
			var that = this;
			var fieldEditor = editField(that);
			if (fieldEditor != null)
				application.fieldEditors.push(fieldEditor);
		});
	} finally {
		$("#progress-dialog").modal("hide");
	}
	return false;
}

function editField(element, controller, id, field, type) {
	idAnalysis = $("[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
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
		else
			fieldEditor = new FieldEditor(element);
		if (!fieldEditor.Initialise())
			fieldEditor.Show();
	}
	return fieldEditor;
}
