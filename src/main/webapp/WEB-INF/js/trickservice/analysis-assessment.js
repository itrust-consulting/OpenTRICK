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

function loadMeasureData(id) {
	var $currentUI = $("#measure-ui");
	if ($currentUI.attr("data-trick-id") == id)
		return false;
	$.ajax({
		url : context + "/Analysis/Standard/Measure/" + id + "/Form",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var $assessmentUI = $("div#measure-ui", new DOMParser().parseFromString(response, "text/html"));
			if ($assessmentUI.length) {
				backupDescriptionHeight();
				$currentUI.replaceWith($assessmentUI);
				restoreDescriptionHeight();
				$("select", $assessmentUI).on("change", saveMeasureData);
				$("input[name!='cost'],textarea", $assessmentUI).on("blur", saveMeasureData);
				$("input[type='number']", $assessmentUI).on("change", function(e) {
					var $target = $(e.currentTarget);
					if (!$target.is(":focus"))
						$target.focus();
					return this;
				});
			} else
				unknowError();
		},
		error : unknowError
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
	var $assessment = $(element), $parent = $assessment.closest("div[data-trick-content]"), parentTop = $parent.offset().top, assessmentTop = $assessment
			.offset().top;
	$parent.scrollTop(assessmentTop > parentTop ? assessmentTop - parentTop : 0);
	return false;
}

function updateMeasureUI() {
	var $assessment = $("div.list-group:visible>.list-group-item.active"), id = $assessment.attr('data-trick-id');
	if (!$assessment.is(":focus"))
		updateScroll($assessment);
	loadMeasureData(id);
}

function updateNavigation() {
	var $currentSelector = $("select[name='" + activeSelector + "']:visible>option:selected"), $currentAssessment = $("div[data-trick-content]:visible .list-group-item.active"), $previousSelector = $(
			"[data-trick-nav='previous-selector']").parent(), $nextSelector = $("[data-trick-nav='next-selector']").parent(), $previousAssessment = $(
			"[data-trick-nav='previous-assessment']").parent(), $nextAssessment = $("[data-trick-nav='next-assessment']").parent();
	if ($currentSelector.next(":first").length)
		$nextSelector.removeClass("disabled");
	else
		$nextSelector.addClass("disabled");

	if ($currentSelector.prev(":last").length)
		$previousSelector.removeClass("disabled");
	else
		$previousSelector.addClass("disabled");

	if ($currentAssessment.prev(".list-group-item:visible").length)
		$previousAssessment.removeClass("disabled");
	else
		$previousAssessment.addClass("disabled");

	if ($currentAssessment.next(".list-group-item:visible").length)
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
	//updateMeasureUI();
	updateNavigation();
	return false;
}

$(function() {

	var $nav = $("ul.nav.nav-pills[data-trick-role='nav-estimation']").on("trick.update.nav", updateNavigation), $previousSelector = $("[data-trick-nav='previous-selector']"), $nextSelector = $("[data-trick-nav='next-selector']"), $previousAssessment = $("[data-trick-nav='previous-assessment']"), $nextAssessment = $("[data-trick-nav='next-assessment']");

	$previousSelector.on("click", function() {
		$("select[name='" + activeSelector + "']:visible>option:selected").prev(":last").prop('selected', true).parent().change();
		return false;
	});

	$nextSelector.on("click", function() {
		$("select[name='" + activeSelector + "']:visible>option:selected").next(":first").prop('selected', true).parent().change();
		return false;
	});

	$previousAssessment.on("click", function() {
		$("div[data-trick-content]:visisble .list-group-item.active").prev(":visible:last").click();
		return false;
	});

	$nextAssessment.on("click", function() {
		$("div[data-trick-content]:visible .list-group-item.active").next(":visible:first").click();
		return false;
	});

	$("select[name='scenario']").on('change', function(e) {
		/*
		 * var $target = $(e.currentTarget), value = $target.val();
		 * $("div[data-trick-standard-name][data-trick-id!='" + value +
		 * "']:visible").hide();
		 * $("div[data-trick-standard-name][data-trick-id='" + value +
		 * "']:hidden").show(); updateMeasureUI();
		 * $nav.trigger("trick.update.nav");
		 */
		return false;
	});

	$("select[name='asset']").on('change', function(e) {
		/*
		 * var $target = $(e.currentTarget), $parent =
		 * $target.closest("div[data-trick-standard-name]"), standardId =
		 * $parent.attr('data-trick-id'), value = $target .val(),
		 * $assessmentsContainer =
		 * $("div[data-trick-content='measure'][data-trick-standard-name][data-trick-id='" +
		 * standardId + "']:visible");
		 * $assessmentsContainer.find("div.list-group[data-trick-chapter-name!='" +
		 * value + "']:visible").hide();
		 * $assessmentsContainer.find("div.list-group[data-trick-chapter-name='" +
		 * value + "']:hidden").show(); updateMeasureUI();
		 * $nav.trigger("trick.update.nav");
		 */
		return false;
	});

	$("div.list-group>.list-group-item").on("click", changeAssessment);

	activeSelector = $("div[data-trick-content]:visible:first").attr("data-trick-content")

	updateNavigation();
	//updateMeasureUI();
});