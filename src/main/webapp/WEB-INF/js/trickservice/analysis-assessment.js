var activeSelector = undefined;

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

function saveMeasureData(e) {
	var $target = $(e.currentTarget), value = $target.val(), id = $("#measure-ui").attr('data-trick-id'), name = $target.attr('name'), type = $target.attr('data-trick-type'), oldValue = $target
			.hasAttr("placeholder") ? $target.attr("placeholder") : $target.attr("data-trick-value");
	if (value == oldValue)
		$target.parent().removeClass('has-error');
	else {
		$.ajax({
			url : context + "/Analysis/EditField/Measure/" + id + "/Update",
			async : false,
			type : "post",
			data : '{"id":' + id + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response.message == undefined)
					unknowError();
				else {
					var $parent = $target.parent();
					if (response.error) {
						$parent.addClass("has-error");
						$parent.removeClass("has-success");
						$parent.attr("title", response.message);
					} else {
						$parent.removeAttr("title");
						$parent.removeClass("has-error");
						$parent.addClass("has-success");
						if (response.empty) {
							$target.attr($target.hasAttr("placeholder") ? "placeholder" : "data-trick-value", value);
						} else {
							for (var i = 0; i < response.fields.length; i++) {
								var field = response.fields[i], $element = name == field.name ? $target : $("#measure-ui [name='" + field.name + "'].form-control");
								for ( var fieldName in field) {
									switch (fieldName) {
									case "value":
										$element.attr("placeholder", field[fieldName]);
										$element.val(field[fieldName]);
										break;
									case "title":
										$element.attr(fieldName, field[fieldName]);
										break;
									}
								}
							}
						}
						var status = name == 'status' ? value : $("#measure-ui select[name='status']").val(), $cost = $("#measure-ui input[name='cost']"), cost = $cost
								.attr("title");
						if (status != "NA" && cost == "0€")
							$cost.parent().addClass("has-error");
						else
							$cost.parent().removeClass("has-error");
					}
				}
			},
			error : unknowError
		});
	}
	return false;
}

function loadAssessmentData(id) {
	var $currentUI = $("#estimation-ui"), idCurrent = $("select[name='" + activeSelector + "']").val();
	/*
	 * if ($currentUI.attr("data-trick-id") == id &&
	 * $currentUI.attr("data-trick-content") == activeSelector) return false;
	 */
	var url = (activeSelector == "asset" ? context + "/Analysis/Assessment/Asset/" + idCurrent + "/Load?idScenario=" : context + "/Analysis/Assessment/Scenario/" + idCurrent
			+ "/Load?idAsset=");
	$.ajax({
		url : url + id,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var $assessmentUI = $("div#estimation-ui", new DOMParser().parseFromString(response, "text/html"));
			if ($assessmentUI.length) {
				// backupDescriptionHeight();
				$currentUI.replaceWith($assessmentUI);
				
				
				
				// restoreDescriptionHeight();
				/*
				 * $("select", $assessmentUI).on("change", saveMeasureData);
				 * $("input[name!='cost'],textarea", $assessmentUI).on("blur",
				 * saveMeasureData); $("input[type='number']",
				 * $assessmentUI).on("change", function(e) { var $target =
				 * $(e.currentTarget); if (!$target.is(":focus"))
				 * $target.focus(); return this; });
				 */
			} else
				unknowError();
		},
		error : unknowError
	}).complete(function(){
		fixTableHeader(".table-fixed-header-analysis");
	});
	return false;
}

function backupDescriptionHeight() {
	var $description = $("#description");
	if ($description.length) {
		var height = $description.outerHeight(), defaultHeight = application["measure-description-default-size"];
		if (defaultHeight != undefined && Math.abs(height - defaultHeight) > 8) {
			application["measure-description-size-prev"] = application["measure-description-size"];
			application["measure-description-size"] = $description.outerHeight();
		} else if (application["measure-description-size"] && application["measure-description-size"] != height && application["measure-description-size-prev"] != height) {
			delete application["measure-description-size"];
			delete application["measure-description-size-prev"]
		}
	}
	return false;
}

function restoreDescriptionHeight() {
	var $description = $("#description");
	if ($description.length) {
		application["measure-description-default-size"] = $description.outerHeight();
		var height = application["measure-description-size"];
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
		element.focus();// update scroll
		currentActive.focus();
	}
	return false;
}

function updateAssessmentUI() {
	var $assessment = $("div.list-group:visible>.list-group-item.active");
	if (!$assessment.is(":focus"))
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
	}
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

	updateContent : function() {
		var type = this.getCurrent(activeSelector).find("option:selected").attr("data-trick-type"), $elements = $("div[data-trick-content]:visible a[data-trick-id!='-1']");
		if (activeSelector == "asset") {
			$elements.each(function(i) {
				var $this = $(this);
				if ($this.attr("data-trick-type").contains(type))
					$this.show();
				else
					$this.hide();
			});
		} else {
			$elements.each(function() {
				var $this = $(this);
				if (type.contains($this.attr("data-trick-type")))
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
	

	var helper = new AssessmentHelder(), $nav = $("ul.nav.nav-pills[data-trick-role='nav-estimation']").on("trick.update.nav", updateNavigation), $previousSelector = $("[data-trick-nav='previous-selector']"), $nextSelector = $("[data-trick-nav='next-selector']"), $previousAssessment = $("[data-trick-nav='previous-assessment']"), $nextAssessment = $("[data-trick-nav='next-assessment']"), val;

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

	activeSelector = $("div[data-trick-content]:hidden").attr("data-trick-content");

	var updateSelector = function(e) {
		var $target = $(e.currentTarget), value = $target.val(), name = $target.attr("name"), other = helper.getOtherName(name);
		if (value == '-1') {
			$("div[data-trick-content]").toggle();
			helper.getCurrent(activeSelector = other).find("option[value='" + helper.getLastSelected(other) + "']:first").prop('selected', true);
		} else if (helper.getCurrent(other).val() != "-1") {
			activeSelector = name;
			$("div[data-trick-content]").toggle();
			helper.getCurrent(other).find("option[value='-1']").prop('selected', true);
		} else
			helper.setLastSelected(name, value);

		helper.updateContent();

		$nav.trigger("trick.update.nav");
		return false;
	};

	for ( var i in helper.names)
		helper.getCurrent(helper.names[i]).on('change', updateSelector);
	$("div.list-group>.list-group-item").on("click", changeAssessment);
	updateNavigation();
	updateAssessmentUI();
});