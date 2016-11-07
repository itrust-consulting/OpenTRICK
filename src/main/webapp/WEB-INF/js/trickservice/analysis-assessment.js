var activeSelector = undefined, helper = undefined, scales = [];

function escape(key, val) {
	if (typeof (val) != "string")
		return val;
	return val.replace(/[\\]/g, '\\\\').replace(/[\/]/g, '\\/').replace(/[\b]/g, '\\b').replace(/[\f]/g, '\\f').replace(/[\n]/g, '\\n').replace(/[\r]/g, '\\r').replace(/[\t]/g,
			'\\t').replace(/[\"]/g, '\\"').replace(/\\'/g, "\\'");
}

function defaultValueByType(value, type) {
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

function saveAssessmentData(e) {
	var $assessmentUI = $("#estimation-ui"), $target = $(e.currentTarget), value = $target.val(), idScenario = $assessmentUI.attr('data-trick-scenario-id'), idAsset = $assessmentUI
			.attr('data-trick-asset-id'), name = $target.attr('name'), type = $target.attr('data-trick-type'), oldValue = $target.hasAttr("placeholder") ? $target
			.attr("placeholder") : $target.attr("data-trick-value");
	if (value == oldValue)
		$target.parent().removeClass('has-error').removeAttr("title");
	else {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/EditField/Estimation/Update?asset=" + idAsset + "&scenario=" + idScenario,
			type : "post",
			data : '{"id":' + idAsset + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response.message == undefined)
					unknowError();
				else {
					var $parent = $target.parent();
					if (response.error)
						$parent.addClass("has-error").removeClass("has-success").attr("title", response.message);
					else {
						$parent.removeAttr("title").removeClass("has-error");
						if ($target.attr("readonly"))
							$target.removeClass("has-success");
						else
							$parent.addClass("has-success");
						var updated = false;
						for (var i = 0; i < response.fields.length; i++) {
							var field = response.fields[i], $element = $target;
							if (name == field.name)
								updated = true;
							else {
								$element = $("[name='" + field.name + "'].form-control", $assessmentUI);
								if (field.name == "computedNextImportance") {
									$element.attr("placeholder", field.value).val(field.value);
									continue;
								}
							}
							for ( var fieldName in field) {
								switch (fieldName) {
								case "value":
									$element.attr("placeholder", field[fieldName]).val(field[fieldName]);
									break;
								case "title":
									$element.attr(fieldName, field[fieldName]);
									break;
								}
							}
						}
						if (!updated) {
							$target.attr($target.hasAttr("placeholder") ? "placeholder" : "data-trick-value", $target.val());
							if ($target.hasAttr("list"))
								$target.attr("title", $("option[value='" + value + "']", "#" + $target.attr("list")).attr("title"));
							else if ($target.tagName = 'SELECT')
								$target.attr("title", $target.find("option:selected").attr('title'));
						}
					}
				}
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}

function loadAssessmentData(id) {
	var $currentUI = $("#estimation-ui"), idAsset = -3, idScenario = -3, url = context + "/Analysis/Assessment/";
	if (activeSelector == "asset") {
		idAsset = $("select[name='asset']").val();
		url += "Asset/" + idAsset + "/Load?idScenario=" + (idScenario = id);
	} else {
		idScenario = $("select[name='scenario']").val();
		url += "Scenario/" + idScenario + "/Load?idAsset=" + (idAsset = id);
	}

	if (idAsset == -1 && idScenario == -1 || $currentUI.attr("data-trick-asset-id") == idAsset && $currentUI.attr("data-trick-scenario-id") == idScenario
			&& $currentUI.attr("data-trick-content") == activeSelector)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : url,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var $assessmentUI = $("div#estimation-ui", new DOMParser().parseFromString(response, "text/html"));
			if ($assessmentUI.length) {
				backupDescriptionHeight();
				$currentUI.replaceWith($assessmentUI);
				restoreDescriptionHeight();
				if (OPEN_MODE.isReadOnly()) {
					$("select:not([disabled])", $assessmentUI).prop("disabled", true);
					$("input:not([disabled]),textarea:not([disabled])", $assessmentUI).attr("readOnly", true);
					$("a[data-controller]", $assessmentUI).remove();
				} else {
					$("select", $assessmentUI).on("change", saveAssessmentData);
					$("textarea,input:not([disabled])", $assessmentUI).on("blur", saveAssessmentData);

					$("a[data-controller]", $assessmentUI).on("click", function() {
						var $description = $("#description", $assessmentUI), $control = $("i.fa", this);
						if ($control.hasClass("fa-lock")) {
							$control.removeClass("fa-lock").addClass("fa-unlock")
							$description.removeAttr("readonly");
						} else {
							$control.removeClass("fa-unlock").addClass("fa-lock")
							$description.attr("readonly", true).parent().removeAttr("title").removeClass("has-success has-error");
						}
						return false;
					});
				}

				$("button[data-scale-modal]", $assessmentUI).click(function() {
					var name = this.getAttribute("data-scale-modal");
					if (scales[name] == undefined)
						scales[name] = $("#" + name);
					scales[name].modal("show");
				});

			} else
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		if (idAsset == -1 || idScenario == -1)
			fixTableHeader(".table-fixed-header-analysis");
		$progress.hide()
	});
	return false;
}

function backupDescriptionHeight() {
	var $description = $("#description");
	if ($description.length) {
		var height = $description.outerHeight(), defaultHeight = application["estimation-description-default-size"];
		if (defaultHeight != undefined && Math.abs(height - defaultHeight) > 8) {
			application["estimation-description-size-prev"] = application["estimation-description-size"];
			application["estimation-description-size"] = $description.outerHeight();
		} else if (application["estimation-description-size"] && application["estimation-description-size"] != height && application["estimation-description-size-prev"] != height) {
			delete application["estimation-description-size"];
			delete application["estimation-description-size-prev"]
		}
	}
	return false;
}

function restoreDescriptionHeight() {
	var $description = $("#description");
	if ($description.length) {
		application["estimation-description-default-size"] = $description.outerHeight();
		var height = application["estimation-description-size"];
		if (height != undefined) {
			$("#description").css({
				"height" : height
			});
		}
	}
	return false;
}

function updateScroll(element) {
	var currentActive = document.activeElement;
	if (element != currentActive) {
		element.focus();// / / update scroll
		currentActive.focus();
	}
	return false;
}

function updateAssessmentUI() {
	var $assessment = $("div.list-group:visible>.list-group-item.active");
	if (!$assessment.is(":focus") && $("div[role='left-menu']").css("position") == "fixed")
		updateScroll($assessment);
	loadAssessmentData($assessment.attr("data-trick-id"));

}

function updateNavigation() {
	var $currentSelector = $("select[name='" + activeSelector + "']:visible>option:selected"), $currentAssessment = $("div[data-trick-content]:visible .list-group-item.active"), $previousSelector = $(
			"[data-trick-nav='previous-selector']").parent(), $nextSelector = $("[data-trick-nav='next-selector']").parent(), $previousAssessment = $(
			"[data-trick-nav='previous-assessment']").parent(), $nextAssessment = $("[data-trick-nav='next-assessment']").parent();

	if ($currentSelector.next(":first").length)
		$nextSelector.removeClass("disabled");
	else
		$nextSelector.addClass("disabled");

	if ($currentSelector.prev("[value!='-1']:last").length)
		$previousSelector.removeClass("disabled");
	else
		$previousSelector.addClass("disabled");

	if ($currentAssessment.prevAll(".list-group-item:visible").length)
		$previousAssessment.removeClass("disabled");
	else
		$previousAssessment.addClass("disabled");

	if ($currentAssessment.nextAll(".list-group-item:visible").length)
		$nextAssessment.removeClass("disabled");
	else
		$nextAssessment.addClass("disabled");
	return false;

}

function changeAssessment(e) {
	var $target = $(e.currentTarget);
	if (!$target.hasClass('active')) {
		var $parent = $target.closest(".list-group");
		$(".list-group-item.active", $parent).removeClass("active");
		$target.addClass('active');
	}
	updateAssessmentUI();
	updateNavigation();
	return false;
}

function AssessmentHelder() {
	this.names = [ "asset", "scenario" ];
	this.asset = $("select[name='asset']");
	this.scenario = $("select[name='scenario']");
	this.lastSelected = {
		asset : this.asset.find("option[value!='-1']:first").val(),
		scenario : this.scenario.find("option[value!='-1']:first").val()
	};
	this.switchControl(this.asset.val() == "-1" ? "scenario" : "asset");
}

AssessmentHelder.prototype = {

	getCurrent : function(name) {
		return name == "asset" ? this.asset : this.scenario;
	},
	getOther : function(name) {
		return name == "asset" ? this.scenario : this.asset;
	},
	getOtherName : function(name) {
		return name == "asset" ? 'scenario' : 'asset';
	},
	setLastSelected : function(name, id) {
		if (id == "-1")
			return false;
		this.lastSelected[name] = id;
	},
	getLastSelected : function(name) {
		return this.lastSelected[name];
	},

	switchControl : function(name) {
		if (name == activeSelector)
			return this;
		activeSelector = name;
		$("div[data-trick-content]").each(function() {
			if (this.getAttribute("data-trick-content") == name)
				$(this).hide();
			else
				$(this).show();
		});
		return this;
	},
	updateContent : function() {
		var type = this.getCurrent(activeSelector).find("option:selected").attr("data-trick-type"), $elements = $("div[data-trick-content]:visible a[data-trick-id!='-1']");
		if (activeSelector == "asset") {
			$elements.each(function(i) {
				var $this = $(this);
				if ($this.attr("data-trick-type").search(type) != -1)
					$this.show();
				else
					$this.hide();
			});
		} else {
			$elements.each(function() {
				var $this = $(this);
				if (type.search($this.attr("data-trick-type")) != -1)
					$this.show();
				else
					$this.hide();
			});
		}

		if ($elements.filter(".active").is(":hidden"))
			$elements.filter(":visible:first").click();
		else
			updateAssessmentUI();
		return this;
	}
}

$(function() {

	application["settings-fixed-header"] = {
		fixedOffset : $(".navbar-fixed-top"),
		scrollStartFixMulti : 1.02
	};

	helper = new AssessmentHelder();

	var $nav = $("ul.nav.nav-pills[data-trick-role='nav-estimation']").on("trick.update.nav", updateNavigation), $openAnalysis = $("a[data-base-ul]", $nav), $previousSelector = $("[data-trick-nav='previous-selector']"), $nextSelector = $("[data-trick-nav='next-selector']"), $previousAssessment = $("[data-trick-nav='previous-assessment']"), $nextAssessment = $("[data-trick-nav='next-assessment']"), val;
	$previousSelector.on("click", function() {
		$("select[name='" + activeSelector + "']>option:selected").prev("[value!='-1']:last").prop('selected', true).parent().change();
		return false;
	});

	$nextSelector.on("click", function() {
		$("select[name='" + activeSelector + "']>option:selected").next(":first").prop('selected', true).parent().change();
		return false;
	});

	$previousAssessment.on("click", function() {
		$("div[data-trick-content]:visible .list-group-item.active").prevAll(":visible:first").click();
		return false;
	});

	$nextAssessment.on("click", function() {
		$("div[data-trick-content]:visible .list-group-item.active").nextAll(":visible:first").click();
		return false;
	});

	var updateSelector = function(e) {
		var $target = $(e.currentTarget), value = $target.val(), name = $target.attr("name"), other = helper.getOtherName(name);
		if (value == '-1')
			helper.switchControl(other).getCurrent(other).find("option[value='" + helper.getLastSelected(other) + "']:first").prop('selected', true);
		else if (helper.getCurrent(other).val() != "-1")
			helper.switchControl(name).getCurrent(other).find("option[value='-1']").prop('selected', true);
		else
			helper.setLastSelected(name, value);

		helper.updateContent();

		$nav.trigger("trick.update.nav");
		return false;
	};

	$("div.list-group>.list-group-item").on("click", changeAssessment);

	for ( var i in helper.names)
		helper.getCurrent(helper.names[i]).on('change', updateSelector)

	$openAnalysis.on("click", function() {
		$openAnalysis.attr("href", $openAnalysis.attr("data-base-ul") + "#tab" + activeSelector.capitalize());
	});

	updateNavigation();
	updateAssessmentUI();
});