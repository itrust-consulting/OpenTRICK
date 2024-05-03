
/**
 * Escapes special characters in a string.
 *
 * @param {string} key - The key of the value being escaped.
 * @param {string} val - The value to be escaped.
 * @returns {string} - The escaped value.
 */
function escape(key, val) {
	if (typeof (val) != "string")
		return val;
	return val.replace(/[\\]/g, '\\\\').replace(/[\/]/g, '\\/').replace(/[\b]/g, '\\b').replace(/[\f]/g, '\\f').replace(/[\n]/g, '\\n').replace(/[\r]/g, '\\r').replace(/[\t]/g,
		'\\t').replace(/[\"]/g, '\\"').replace(/\\'/g, "\\'");
}

/**
 * Sets a default value based on the given type.
 *
 * @param {any} value - The value to check.
 * @param {string} type - The type of the value.
 * @param {boolean} protect - Whether to protect the value.
 * @returns {any} - The default value based on the type.
 */
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

/**
 * Updates the value of a field element.
 *
 * @param {HTMLElement} element - The field element to update.
 * @param {any} value - The new value for the field.
 * @param {string} type - The type of the field.
 */
function updateFieldValue(element, value, type) {
	$(element).parent().text(defaultValueByType(value, type));
}

/**
 * Represents a field validator.
 * @constructor
 */
function FieldValidator() {
	/**
	 * Validates the field.
	 */
	FieldValidator.prototype.Validate = function () {
		throw "Not implemented";
	};
}

/**
 * Represents a field validator with bounds.
 * @constructor
 * @param {number} min - The minimum value allowed.
 * @param {number} max - The maximum value allowed.
 */
function FieldBoundedValidator(min, max) {
	FieldValidator.call(this);
	this.minValue = $.isNumeric(min) ? parseFloat(min) : undefined;
	this.maxValue = $.isNumeric(max) ? parseFloat(max) : undefined;
}

FieldBoundedValidator.prototype = new FieldValidator();

/**
 * Validates the field element.
 * @param {HTMLElement} element - The field element to validate.
 * @returns {boolean} - True if the field is valid, false otherwise.
 */
FieldBoundedValidator.prototype.Validate = function (element) {
	var $element = $(element), value = parseFloat($(element).val().replace(",", "."));
	if (!$.isNumeric(value))
		return false;
	if (this.minValue != undefined && value < this.minValue)
		return false;
	if (this.maxValue != undefined && value > this.maxValue)
		return false;
	return true;
};

/**
 * Represents a field editor.
 * @constructor
 * @param {HTMLElement} element - The field element.
 * @param {FieldValidator} validator - The field validator.
 */
function FieldEditor(element, validator) {
	this.element = element;
	this.validator = validator;
	this.controllor = null;
	this.defaultValue = $(element).text().trim();
	/**
	 * Options values
	 * @type {Array<string>}
	 */
	this.choose = [];
	/**
	 * Options labels
	 * @type {Array<string>}
	 */
	this.chooseTranslate = [];
	/**
	 * Value to display
	 * @type {Array<string>}
	 */
	this.chooseValue = [];
	/**
	 * Options titles
	 * @type {Array<string>}
	 */
	this.chooseTitle = [];

	this.fieldEditor = null;
	this.realValue = null;
	this.fieldName = null;
	this.classId = null;
	this.fieldType = null;
	this.callback = null;
	this.async = true;
	this.isText = false;
	this.tabPress = undefined;
	this.backupData = {
		orginalStyle: undefined,
		parentClass: undefined,
		width: undefined
	};
}

/**
 * Generates the field editor.
 * @returns {boolean} - True if the field editor was generated successfully, false otherwise.
 */
FieldEditor.prototype.GeneratefieldEditor = function () {
	var $element = $(this.element);
	if ($element.find("input,select,textarea").length)
		return true;
	if (!this.LoadData())
		return true;
	var $fieldEditor, height = 0, width = 0, minWidth = 0, rows = 2, $td, minValue = $element.attr("data-trick-min-value"), maxValue = $element.attr("data-trick-max-value"), stepValue = $element
		.attr("data-trick-step-value"), type = $element.attr("data-trick-content");
	this.isText = type === "text";
	if (!(stepValue === undefined || maxValue === undefined || minValue === undefined)) {
		stepValue = parseInt(stepValue), maxValue = parseInt(maxValue), minValue = parseInt(minValue);
		for (var i = minValue; i <= maxValue; i += stepValue)
			this.choose.push(i.toString());
		this.chooseTranslate = this.__findChooseTranslate(this.element);
	}
	if (!this.choose.length) {
		if ($element.is("td"))
			$td = $element;
		else {
			$td = $element.closest("td");
			rows = $element.text().split("\n").length;
			if (rows == 1)
				rows = 2;
		}
		width = $td.outerWidth();
		height = this.isText || type === "color" ? $td.outerHeight() - 0.5 : 0;
		if (this.defaultValue.length > 100 || this.isText)
			this.fieldEditor = document.createElement("textarea");
		else {
			this.fieldEditor = document.createElement("input");
			if (this.element.hasAttribute("data-real-value"))
				this.realValue = this.element.getAttribute("data-real-value");
			if (minValue != undefined || maxValue != undefined)
				this.validator = new FieldBoundedValidator(minValue, maxValue);
			var dataList = $element.attr("data-trick-list-value");
			if (dataList) {
				this.fieldEditor.setAttribute("list", dataList);
				if (width < 80)
					width = '80';
			} else if (type === "color") {
				this.fieldEditor.setAttribute("type", type);
			}
		}
		$fieldEditor = $(this.fieldEditor);
	} else {
		$td = $element;
		this.fieldEditor = document.createElement("select");
		$fieldEditor = $(this.fieldEditor);
		for (var i = 0; i < this.choose.length; i++) {
			var option = document.createElement("option"), $option = $(option);
			option.setAttribute("value", this.choose[i]);
			if (this.chooseTranslate.length > i) {
				$option.text(this.chooseTranslate[i]);
				if (this.chooseTranslate[i] == this.defaultValue || this.chooseValue[i] === this.defaultValue)
					option.setAttribute("selected", true);
			} else {
				$option.text(this.choose[i]);
				if (this.choose[i] == this.defaultValue)
					option.setAttribute("selected", true);
				if (this.chooseTranslate.length)
					this.chooseTranslate.push(this.choose[i].toString());
			}
			if (this.chooseTitle.length)
				$option.attr("title", this.chooseTitle[i]);
			$option.appendTo($fieldEditor);
		}
		width = $td.outerWidth();
		if (width < 80)
			width = '80';
	}

	this.backupData.width = $td.width();
	this.backupData.orginalStyle = $td.attr("style");
	this.fieldEditor.setAttribute("class", "form-control");
	this.fieldEditor.setAttribute("style", "padding: 4px; position:absolute; z-index:2; width:" + (width ? width : minWidth ? minWidth : '80')
		+ "px;  margin-left:auto; margin-right:auto; height:" + (height == 0 ? "auto" : (height + "px;")));
	this.fieldEditor.setAttribute("placeholder", this.realValue == null || this.realValue == undefined ? this.defaultValue : this.realValue);

	$td.css({
		"padding": 0,
		"width": $td.outerWidth(),
		"height": height == 0 ? undefined : height
	});

	if (!application.editMode || !this.isText) {
		var that = this, $fieldEditor = $(this.fieldEditor);
		if (type == "color") {
			$fieldEditor.change(function () {
				if (that.Validate())
					return that.Save(that);
				else
					$fieldEditor.parent().addClass("has-error").focus();
			});
			//fix issue the color dialog. delay 100ms
			setTimeout(() => {
				$fieldEditor.blur(function () {
					if (that.Validate())
						return that.Save(that);
					else
						$fieldEditor.parent().addClass("has-error").focus();
				});
			}, 300);
		} else {
			$fieldEditor.blur(function () {
				if (that.Validate())
					return that.Save(that);
				else
					$fieldEditor.parent().addClass("has-error").focus();
			});
		}

	}
	return false;
};

/**
 * Checks if the element has the specified attribute.
 * @param {HTMLElement} element - The element to check.
 * @param {string} attribute - The attribute to check for.
 * @returns {boolean} - True if the element has the attribute, false otherwise.
 */
FieldEditor.prototype.HasAttr = function (element, attribute) {
	var attr = $(element).attr(attribute);
	return typeof attr !== 'undefined' && attr !== false;
};

/**
 * Initializes the field editor.
 * @returns {boolean} - True if the initialization was successful, false otherwise.
 */
FieldEditor.prototype.Initialise = function () {

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

/**
 * Finds the choose values for the element.
 * @param {HTMLElement} element - The element to find the choose values for.
 * @returns {Array<string>} - The choose values.
 */
FieldEditor.prototype.__findChoose = function (element) {
	var content = $(element).attr("data-trick-choose");
	if (content != undefined)
		return content.split(",");
	return [];
};

/**
 * Finds the choose translations for the element.
 * @param {HTMLElement} element - The element to find the choose translations for.
 * @returns {Array<string>} - The choose translations.
 */
FieldEditor.prototype.__findChooseTranslate = function (element) {
	var content = $(element).attr("data-trick-choose-translate");
	if (content != undefined)
		return content.split(",");
	return [];
};

/**
 * Finds the choose titles for the element.
 * @param {HTMLElement} element - The element to find the choose titles for.
 * @returns {Array<string>} - The choose titles.
 */
FieldEditor.prototype.__findChooseTitle = function (element) {
	var content = $(element).attr("data-trick-choose-title");
	if (content != undefined)
		return content.split(",");
	return [];
};

/**
 * Finds the controllor for the element.
 * @param {HTMLElement} element - The element to find the controllor for.
 * @returns {string} - The controllor.
 */
FieldEditor.prototype.__findControllor = function (element) {
	return this.__finder(element, "data-trick-class");
};

/**
 * Finds the class ID for the element.
 * @param {HTMLElement} element - The element to find the class ID for.
 * @returns {string} - The class ID.
 */
FieldEditor.prototype.__findClassId = function (element) {
	return this.__finder(element, "data-trick-id");
};

/**
 * Finds the callback for the element.
 * @param {HTMLElement} element - The element to find the callback for.
 * @returns {string} - The callback.
 */
FieldEditor.prototype.__findCallback = function (element) {
	return this.__finder(element, "data-trick-callback");
};

/**
 * Finds the pre-execution callback for the element.
 * @param {HTMLElement} element - The element to find the pre-execution callback for.
 * @returns {string} - The pre-execution callback.
 */
FieldEditor.prototype.__findCallbackPreExec = function (element) {
	return this.__finder(element, "data-trick-callback-pre");
};

/**
 * Helper function to find an attribute in the element or its ancestors.
 * @param {HTMLElement} element - The element to start the search from.
 * @param {string} attr - The attribute to find.
 * @returns {string|null} - The value of the attribute, or null if not found.
 */
FieldEditor.prototype.__finder = function (element, attr) {
	var $element = $(element);
	if (!$element.length)
		return null;
	var content = $element.attr(attr);
	if (typeof content === "undefined")
		return this.__finder($element.closest("[" + attr + "]"), attr);
	return content;
}

/**
 * Finds the next editable field element.
 * @param {jQuery} $tr - The current table row.
 * @param {boolean} isNext - Whether to find the next or previous editable field.
 * @returns {jQuery|null} - The next editable field element, or null if not found.
 */
FieldEditor.prototype.__findNextEditable = function ($tr, isNext) {
	var $nextTr = isNext ? $tr.nextAll("tr[data-trick-id]:first") : $tr.prevAll("tr[data-trick-id]:first")
	if (!$nextTr.length)
		return $nextTr;
	var $next = $nextTr.find("[onclick*='editField']" + (isNext ? ":first" : ":last"))
	if (!$next.length)
		return this.__findNextEditable($nextTr, isNext)
	return $next;
};

/**
 * Checks if the field editor supports tab navigation.
 * @returns {this} 
 */
FieldEditor.prototype.__supportTabNav = function () {
	var that = this;
	$(this.fieldEditor).keydown(function (e) {
		if (e.keyCode == 9) {
			if (e.shiftKey)
				that.tabPress = "prev";
			else
				that.tabPress = "next";
		}
		if (e.keyCode == 27)
			that.Rollback();
		else if (!that.isText && e.keyCode == 13){
			that.Save(that);
		}
	});
	return this;
};

FieldEditor.prototype.__save = function () {
	var that = this;
	$.ajax({
		url: context + "/Analysis/EditField/" + that.controllor + "/" + that.classId,
		type: "post",
		async: that.async,
		data: '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
			+ '", "type": "' + that.fieldType + '"}',
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				that.UpdateUI();
				if (that.callback != null && that.callback != undefined)
					setTimeout(that.callback, 1);
			} else if (response["error"] != undefined) {
				showNotifcation("danger", response["error"]);
			} else
				showNotifcation("danger", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));

			return true;
		},
		error: (jqXHR, textStatus, errorThrown) => that.Error(jqXHR, textStatus, errorThrown)
	});
	return this;
}

FieldEditor.prototype.Show = function () {
	if (this.fieldEditor == null || this.fieldEditor == undefined)
		return false;
	if (this.element == null || this.element == undefined)
		return false;
	var $fieldEditor = $(this.fieldEditor), $element = $(this.element), type = $element.attr("data-trick-content");
	if (!$fieldEditor.is("select"))
		$fieldEditor.val(this.realValue == null ? $element.text().trim() : this.realValue);
	$element.html($fieldEditor);
	this.backupData.parentClass = $fieldEditor.parent().attr("class")
	if (!application.editMode || type !== "text") {
		this.__supportTabNav();
		$fieldEditor.focus();
	}

	if (type === "color")
		$fieldEditor.trigger("click");
	return false;
};

FieldEditor.prototype.Validate = function () {
	if (this.validator != null)
		return this.validator.Validate(this.fieldEditor);
	return true;
};

FieldEditor.prototype.LoadData = function () {
	var callback = this.__findCallbackPreExec(this.element);
	if (callback != null)
		eval(callback);
	if (!this.choose.length)
		this.choose = this.__findChoose(this.element);
	if (this.choose.length) {
		if (!this.chooseTranslate.length)
			this.chooseTranslate = this.__findChooseTranslate(this.element);
		if (!this.chooseTitle.length)
			this.chooseTitle = this.__findChooseTitle(this.element);
	}
	return true;
};

FieldEditor.prototype.HasChanged = function () {
	if (this.choose.length && this.chooseValue.length)
		return this.choose.indexOf(this.GetValue()) != this.chooseValue.indexOf(this.defaultValue);
	else if (this.choose.length && this.chooseTranslate.length)
		return this.choose.indexOf(this.GetValue()) != this.chooseTranslate.indexOf(this.defaultValue);
	else if (this.realValue == null || this.realValue == undefined)
		return this.GetValue() != this.defaultValue;
	else
		return this.GetValue() != this.realValue;
};

FieldEditor.prototype.UpdateUI = function () {
	return this.Restore();
};

FieldEditor.prototype.GetValue = function () {
	return $(this.fieldEditor).val();
};

FieldEditor.prototype.Save = function (that) {
	if (!that.Validate()) {
		that.Rollback();
	} else {
		if (that.HasChanged())
			that.__save();
		else {
			that.Rollback();
			return false;
		}
	}
	return false;
};

FieldEditor.prototype.Rollback = function () {
	return this.Restore(true);
};

FieldEditor.prototype.Restore = function (rollback) {
	var taht = this, $element = $(this.element), $td = $element.is("td") ? $element : $element.closest("td"), type = $element.attr("data-trick-content");
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
			setTimeout(this.callback, 1);
	} else {
		var value = this.GetValue();
		if (this.choose.length && (this.chooseTranslate.length || this.chooseValue.length)) {
			if (this.chooseValue.length) {
				for (var i = 0; i < this.choose.length; i++) {
					if (this.choose[i] == value) {
						$element.text(this.chooseValue[i]);
						break;
					}
				}
			} else {
				for (var i = 0; i < this.choose.length; i++) {
					if (this.choose[i] == value) {
						$element.text(this.chooseTranslate[i]);
						break;
					}
				}
			}
		} else if (type === "color") {
			$element.css({ "background-color": value });
			$element.attr("data-real-value", value);
			$element.empty();
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
	delete that;
	return null;
};

FieldEditor.prototype.Error = function (jqXHR, textStatus, errorThrown) {
	this.Rollback();
	if (jqXHR.status === 403)
		showDialog("error", MessageResolver("error.forbidden"));
	else unknowError(jqXHR, textStatus, errorThrown);
	return this;
}

ExtendedFieldEditor.prototype = new FieldEditor();

/**
 * Represents an extended field editor.
 *
 * @param {string} section - The section of the field editor.
 * @param {HTMLElement} element - The HTML element of the field editor.
 * @constructor
 * @extends FieldEditor
 */
function ExtendedFieldEditor(section, element) {
	FieldEditor.call(this, element);
	this.section = section;
}

ExtendedFieldEditor.prototype.__save = function () {
	var that = this;
	$.ajax({
		url: context + "/Analysis/EditField/" + that.controllor + "/" + that.classId,
		type: "post",
		async: that.async,
		data: '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
			+ '", "type": "' + that.fieldType + '"}',
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				try {
					that.UpdateUI();
					if (that.callback != null && that.callback != undefined)
						setTimeout(that.callback, 1);
				} finally {
					if (that.fieldName == "value") {
						updateAssessmentAle(true);
						$("datalist[id^='dataList-parameter-']").remove();
						reloadSection([that.section, "section_asset", "section_scenario"]);
					}
				}
			} else if (response["error"] != undefined)
				showNotifcation("danger", MessageResolver("error.unknown.save.data", response["error"]));
			else
				showNotifcation("danger", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));
		},
		error: (jqXHR, textStatus, errorThrown) => that.Error(jqXHR, textStatus, errorThrown)
	});
	return this;
};

MaturityMeasureFieldEditor.prototype = new FieldEditor();

/**
 * Represents a field editor for the MaturityMeasure class.
 *
 * @param {HTMLElement} element - The HTML element to bind the field editor to.
 * @constructor
 * @extends FieldEditor
 */
function MaturityMeasureFieldEditor(element) {
	FieldEditor.call(this, element);
	this.implementations = [];
}

MaturityMeasureFieldEditor.prototype.LoadData = function () {
	this.implementations = [];
	var $implementationRates = $("#Maturity_implementation_rate tr[data-trick-class='SimpleParameter']");
	if (!$implementationRates.length)
		return true;
	for (var i = 0; i < $implementationRates.length; i++) {
		var $implementationRate = $($implementationRates[i]);
		this.implementations.push({
			'id': $implementationRate.attr('data-trick-id'),
			'value': $implementationRate.find("td[data-trick-field='value']").text()
		});
	}
	return !this.implementations.length;
};

MaturityMeasureFieldEditor.prototype.GeneratefieldEditor = function () {
	var $element = $(this.element);
	if ($element.find("select").length)
		return true;
	if (this.LoadData())
		return true;

	this.fieldEditor = document.createElement("select");

	var that = this, $fieldEditor = $(this.fieldEditor);

	this.fieldEditor.setAttribute("class", "form-control");
	this.realValue = this.element.hasAttribute("data-real-value") ? $element.attr("data-real-value") : null;
	this.fieldEditor.setAttribute("style", "padding: 4px; margin-left:auto; width:80px; position:absolute; z-index:2; margin-right:auto;");
	this.fieldEditor.setAttribute("placeholder", this.realValue != null && this.realValue != undefined ? this.realValue : this.defaultValue);

	for (let item of this.implementations) {
		var option = document.createElement("option"), $option = $(option);
		option.setAttribute("value", item.value);
		option.setAttribute("data-trick-id", item.id);
		if (this.defaultValue === item.value)
			option.setAttribute("selected", true);
		$option.text(item.value);
		$option.appendTo($fieldEditor);
	}

	this.backupData.width = $element.width();
	this.backupData.orginalStyle = $element.attr("style");
	$element.css({
		"padding": 0,
		"width": this.backupData.width
	});
	$fieldEditor.blur(function () {
		return that.Save(that);
	});
	return false;
};


AssessmentFieldEditor.prototype = new FieldEditor();

/**
 * Creates a new AssessmentFieldEditor.
 *
 * @param {HTMLElement} element - The element to bind the field editor to.
 * @constructor
 * @extends FieldEditor
 */
function AssessmentFieldEditor(element) {

	FieldEditor.call(this, element);
}

AssessmentFieldEditor.prototype.Rollback = function () {
	FieldEditor.prototype.Rollback.call(this);
	if (application["estimation-helper"] != undefined)
		application["estimation-helper"].tryUpdate();
	return this;
};

AssessmentFieldEditor.prototype.__save = function () {
	var that = this;
	$.ajax({
		url: context + "/Analysis/EditField/" + that.controllor + "/" + that.classId,
		type: "post",
		async: that.async,
		data: '{"id":' + that.classId + ', "fieldName":"' + that.fieldName + '", "value":"' + defaultValueByType(that.GetValue(), that.fieldType, true)
			+ '", "type": "' + that.fieldType + '"}',
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				that.UpdateUI();
				application["estimation-helper"].tryUpdate(that.classId);
				reloadSection(["section_asset", "section_scenario", "section_riskregister"], undefined, true);
				reloadAssetScenarioChart();
			} else {
				that.Rollback();
				application["estimation-helper"].error(response["error"]);
			}
			return true;
		},
		error: (jqXHR, textStatus, errorThrown) => that.Error(jqXHR, textStatus, errorThrown)
	});
	return this;
};

AssessmentExtendedParameterEditor.prototype = new AssessmentFieldEditor();

/**
 * Data list must be remove when parameter value change.
 * 
 * @See ExtendedFieldEditor
 */
function AssessmentExtendedParameterEditor(element) {
	AssessmentFieldEditor.call(this, element);
}

AssessmentExtendedParameterEditor.prototype.GeneratefieldEditor = function () {
	var fieldName = this.element.getAttribute("data-trick-field");

	if (application.analysisType.isQuantitative() && (fieldName === 'IMPACT' || fieldName === "likelihood")) {

		var that = this, $element = $(this.element);
		if ($element.find("select,input,textarea").length)
			return true;
		this.dataListName = undefined;
		this.acromyms = [];
		if (!this.LoadData())
			return true;
		this.fieldEditor = document.createElement("input");
		this.fieldEditor.setAttribute("list", this.dataListName);
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("class", "form-control");
		this.fieldEditor.setAttribute("placeholder", this.defaultValue);
		if (this.element.hasAttribute("data-real-value")) {
			this.realValue = this.element.getAttribute("data-real-value");
			this.fieldEditor.setAttribute("value", this.realValue);
		} else
			this.fieldEditor.setAttribute("value", this.defaultValue);
		this.fieldEditor.setAttribute("style", "padding: 4px; width:100px; margin-left:auto; position:absolute; z-index:2; margin-right:auto;");
		this.backupData.width = $element.width();
		this.backupData.orginalStyle = $element.attr("style");
		$element.css({
			"padding": 0,
			"width": this.backupData.width
		});

		$(this.fieldEditor).blur(function () {
			return that.Save(that);
		});
		return false;
	}
	return AssessmentFieldEditor.prototype.GeneratefieldEditor.call(this);
};

AssessmentExtendedParameterEditor.prototype.getLabel = function (i, levels, labels) {
	var level = levels.length >= 0 && levels.length > i ? levels[i].innerText.trim() : "";
	var label = labels.length >= 0 && labels.length > i ? labels[i].innerText.trim() : "";
	if (level === "") {
		level = i === 0 ? MessageResolver("label.status.na", "na") : i;
		if (label === "")
			return level;
		else
			return level + " - " + label;
	} else
		return label === "" ? level : level + " - " + label
}

AssessmentExtendedParameterEditor.prototype.__generateDataList = function () {
	if (!this.dataListName)
		return this;
	var dataList = document.getElementById(this.dataListName);
	if (dataList != null)
		return this;
	dataList = document.createElement("datalist");
	dataList.setAttribute("id", this.dataListName);
	for (var i = 0; i < this.choose.length; i++) {
		var option = document.createElement("option");
		option.setAttribute("value", this.choose[i]);
		option.innerText = this.choose[i] + " (" + this.acromyms[i] + ")";
		dataList.appendChild(option);
	}
	$(dataList).hide().appendTo("#widgets");
};


AssessmentImpactFieldEditor.prototype = new AssessmentExtendedParameterEditor();

/**
 * Represents an Assessment Impact Field Editor.
 * @constructor
 * @param {HTMLElement} element - The HTML element to bind the editor to.
 * @extends AssessmentExtendedParameterEditor
 */
function AssessmentImpactFieldEditor(element) {
	AssessmentExtendedParameterEditor.call(this, element);
}

AssessmentExtendedParameterEditor.prototype.__qualitativeDataLoader = function (name) {
	var id = "#Scale_Impact_" + name, $acronyms = $("td[data-trick-acronym-value]", id), $values = $("td[data-trick-field='level']", id), $title = $(
		"td[data-trick-field='description']", id);
	for (var i = 0; i < $values.length; i++) {
		this.choose[i] = $acronyms[i].getAttribute("data-trick-acronym-value");
		this.chooseTranslate[i] = this.getLabel(i, $values, $acronyms);
		this.chooseTitle[i] = $title[i].innerText.trim();
		this.chooseValue[i] = $values[i].innerText.trim();
	}
	return this.choose.length;
};

AssessmentExtendedParameterEditor.prototype.__quantitativeDataLoader = function (name) {
	var id = "#Scale_Impact,#DynamicParameters", $acronyms = $("td[data-trick-field='acronym']", id), $values = $("td[data-trick-field='value']", id);
	this.dataListName = "dataList-parameter-impact";
	for (var i = 0; i < $values.length; i++) {
		this.acromyms[i] = $acronyms[i].innerText.trim();
		this.choose[i] = $values[i].innerText.trim();
	}
	this.__generateDataList();
	return this.choose.length;
};

AssessmentImpactFieldEditor.prototype.LoadData = function () {
	var name = this.element.getAttribute("data-trick-field");
	return name === "IMPACT" ? this.__quantitativeDataLoader(name) : this.__qualitativeDataLoader(name);
};

AssessmentProbaFieldEditor.prototype = new AssessmentExtendedParameterEditor();

/**
 * Creates a new AssessmentProbaFieldEditor.
 * @param {HTMLElement} element - The element to bind the field editor to.
 * @constructor
 * @extends AssessmentExtendedParameterEditor
 */
function AssessmentProbaFieldEditor(element) {
	AssessmentExtendedParameterEditor.call(this, element);
}

AssessmentProbaFieldEditor.prototype.LoadData = function () {
	if (application.analysisType.isQuantitative()) {
		this.dataListName = "dataList-parameter-probability";
		var $acronyms = $("td[data-trick-field='acronym']", "#Scale_Probability,#DynamicParameters"), $values = $("td[data-trick-field='value']",
			"#Scale_Probability,#DynamicParameters");
		for (var i = 0; i < $values.length; i++) {
			this.acromyms[i] = $acronyms[i].innerText.trim();
			this.choose[i] = $values[i].innerText.trim();
		}
		this.__generateDataList();
	} else {
		var id = "#Scale_Probability", $acronyms = $("td[data-trick-acronym-value]", id), $values = $("td[data-trick-field='level']", id), $title = $(
			"td[data-trick-field='description']", id);
		for (var i = 0; i < $values.length; i++) {
			this.choose[i] = $acronyms[i].getAttribute("data-trick-acronym-value");
			this.chooseTranslate[i] = this.getLabel(i, $values, $acronyms);
			this.chooseTitle[i] = $title[i].innerText.trim();
			this.chooseValue[i] = $values[i].innerText.trim();
		}
	}
	return this.choose.length;
};

ReportSettingEditor.prototype = new FieldEditor();

/**
 * Represents a ReportSettingEditor.
 * @param {HTMLElement} element - The element to initialize the editor on.
 * @constructor
 * @extends FieldEditor
 */
function ReportSettingEditor(element) {
	FieldEditor.call(this, element);
}

FieldEditor.prototype.__save = function () {
	var that = this;
	$.ajax({
		url: context + "/Analysis/EditField/" + that.controllor,
		type: "post",
		async: that.async,
		data: '{"fieldName":"' + that.classId + '", "value":"' + that.GetValue() + '", "type": "string"}',
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				that.UpdateUI();
				if (that.callback != null && that.callback != undefined)
					setTimeout(that.callback, 1);
			} else if (response["error"] != undefined) {
				showNotifcation("danger", response["error"]);
			} else
				showNotifcation("danger", MessageResolver("error.unknown.save.data", "An unknown error occurred when saving data"));

			return true;
		},
		error: (jqXHR, textStatus, errorThrown) => that.Error(jqXHR, textStatus, errorThrown)
	});
	return this;
};

/**
 * Extracts phases from the given element and adds them to the `choose` array in the provided object.
 * If a default phase is specified, it is also added to the `choose` array.
 * 
 * @param {Object} self - The object to which the extracted phases will be added.
 * @param {string} defaultPhase - The default phase to be added to the `choose` array.
 * @returns {boolean} - Always returns false.
 */
function extractPhase(self, defaultPhase) {
	if (self.choose.length)
		return false;
	var $phases = $("#section_phase tbody>tr[data-trick-class='Phase']>td[data-trick-field='number']");
	if (defaultPhase)
		self.choose.push("0");
	for (var i = 0; i < $phases.length; i++)
		self.choose.push($phases[i].innerText.trim());
	return false;
}

/**
 * Disables the edit mode and saves the changes made in the field editors.
 * @returns {boolean} Returns false.
 */
function disableEditMode() {
	if (application.editMode) {
		try {
			application.editingModeFroceAbort = application.editMode = false;
			var $progress = $("#loading-indicator").show();
			setTimeout(function () {
				try {
					$("[role='enterEditMode']").removeClass("disabled").removeClass("active");
					$("[role='leaveEditMode']").addClass("disabled").addClass('active');
					$(application.fieldEditors).each(function () {
						this.Save(this);
					});
				} finally {
					$progress.hide();
				}
			}, 250);
		} catch (e) {
			$progress.hide();
		}
	}
	return false;
}

/**
 * Enables the edit mode for the application.
 * @returns {boolean} Returns false.
 */
function enableEditMode() {
	if (!application.editMode) {
		try {
			application.editMode = true;
			var $progress = $("#loading-indicator").show();
			setTimeout(function () {
				try {

					application.fieldEditors = [];
					var $data = $(".tab-pane.active table:visible [data-trick-content='text']");
					if ($data.length) {
						$("[role='leaveEditMode']").removeClass("disabled").removeClass("active");
						$("[role='enterEditMode']").addClass("disabled").addClass('active');
						$data.each(function () {
							var fieldEditor = editField(this);
							if (fieldEditor != null)
								application.fieldEditors.push(fieldEditor);
						});
					} else
						application.editingModeFroceAbort = application.editMode = false;
				} finally {
					$progress.hide()
				}
			}, 250);
		} catch (e) {
			$progress.hide()
		}
	}
	return false;
}

/**
 * Edits a field based on the provided parameters.
 *
 * @param {HTMLElement} element - The HTML element representing the field.
 * @param {string} controller - The controller for the field.
 * @param {string} id - The ID of the field.
 * @param {string} field - The field to be edited.
 * @param {string} type - The type of the field.
 * @returns {FieldEditor} - The field editor instance.
 */
function editField(element, controller, id, field, type) {
	var fieldEditor = null;
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (controller === null || controller === undefined)
			controller = FieldEditor.prototype.__findControllor(element);
		if (controller === "LikelihoodParameter" || controller === "ImpactParameter") {
			fieldEditor = new ExtendedFieldEditor("section_parameter_impact_probability", element);
		} else if (controller == "Assessment") {
			field = element.getAttribute("data-trick-field");
			var fieldImpact = ["comment", "hiddenComment", "uncertainty", "owner"];
			if (fieldImpact.indexOf(field) != -1)
				fieldEditor = new AssessmentFieldEditor(element);
			else if (field === "likelihood")
				fieldEditor = new AssessmentProbaFieldEditor(element);
			else
				fieldEditor = new AssessmentImpactFieldEditor(element);
		} else if (controller === "MaturityMeasure")
			fieldEditor = new MaturityMeasureFieldEditor(element);
		else if (controller === "ReportSetting" || controller === "ExportFileName")
			fieldEditor = new ReportSettingEditor(element);
		else
			fieldEditor = new FieldEditor(element);

		if (!fieldEditor.Initialise())
			fieldEditor.Show();
	}
	return fieldEditor;
}
