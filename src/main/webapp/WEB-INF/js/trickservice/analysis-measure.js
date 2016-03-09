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
			var $measureUI = $("div#measure-ui", new DOMParser().parseFromString(response, "text/html"));
			if ($measureUI.length) {
				backupDescriptionHeight();
				$currentUI.replaceWith($measureUI);
				restoreDescriptionHeight();
				$("select", $measureUI).on("change", saveMeasureData);
				$("input[name!='cost'],textarea", $measureUI).on("blur", saveMeasureData);
				$("input[type='number']", $measureUI).on("change", function(e) {
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
	var currentActive = document.activeElement;
	if (element != currentActive) {
		element.focus();// update scroll
		currentActive.focus();
	}
	return false;
}

function updateMeasureUI() {
	var $measure = $("div.list-group:visible>.list-group-item.active"), id = $measure.attr('data-trick-id');
	if (!$measure.is(":focus") && $("div[role='left-menu']").css("position") == "fixed")
		updateScroll($measure);
	loadMeasureData(id);
}

function updateNavigation() {
	var $currentChapter = $("select[name='chapter']:visible>option:selected"), $currentMeasure = $("div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active"), $previousChatper = $(
			"[data-trick-nav='previous-chapter']").parent(), $nextChapter = $("[data-trick-nav='next-chapter']").parent(), $previousMeasure = $(
			"[data-trick-nav='previous-measure']").parent(), $nextMeasure = $("[data-trick-nav='next-measure']").parent();
	if ($currentChapter.next(":first").length)
		$nextChapter.removeClass("disabled");
	else
		$nextChapter.addClass("disabled");

	if ($currentChapter.prev(":last").length)
		$previousChatper.removeClass("disabled");
	else
		$previousChatper.addClass("disabled");

	if ($currentMeasure.prev(".list-group-item:visible").length)
		$previousMeasure.removeClass("disabled");
	else
		$previousMeasure.addClass("disabled");

	if ($currentMeasure.next(".list-group-item:visible").length)
		$nextMeasure.removeClass("disabled");
	else
		$nextMeasure.addClass("disabled");
	return false;

}

function changeMeasure(e) {
	var $target = $(e.currentTarget);
	if (!$target.hasClass('active')) {
		var $parent = $target.closest(".list-group");
		$(".list-group-item.active", $parent).removeClass("active");
		$target.addClass('active');
	}
	updateMeasureUI();
	updateNavigation();
	return false;
}

$(function() {

	var $nav = $("ul.nav.nav-pills[data-trick-role='nav-measure']").on("trick.update.nav", updateNavigation), $returnAnalysis = $("a[data-base-url]", $nav), $standardSelector = $("select[name='standard']"), $previousChatper = $("[data-trick-nav='previous-chapter']"), $nextChapter = $("[data-trick-nav='next-chapter']"), $previousMeasure = $("[data-trick-nav='previous-measure']"), $nextMeasure = $("[data-trick-nav='next-measure']");

	$previousChatper.on("click", function() {
		$("select[name='chapter']:visible>option:selected").prev(":last").prop('selected', true).parent().change();
		return false;
	});

	$nextChapter.on("click", function() {
		$("select[name='chapter']:visible>option:selected").next(":first").prop('selected', true).parent().change();
		return false;
	});

	$previousMeasure.on("click", function() {
		$("div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active").prev(":visible:last").click();
		return false;
	});

	$nextMeasure.on("click", function() {
		$("div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active").next(":visible:first").click();
		return false;
	});

	$standardSelector.on('change', function(e) {
		var value = $standardSelector.val();
		$("div[data-trick-standard-name][data-trick-id!='" + value + "']:visible").hide();
		$("div[data-trick-standard-name][data-trick-id='" + value + "']:hidden").show();
		updateMeasureUI();
		$nav.trigger("trick.update.nav");
		return false;
	});

	$("select[name='chapter']")
			.on(
					'change',
					function(e) {
						var $target = $(e.currentTarget), $parent = $target.closest("div[data-trick-standard-name]"), standardId = $parent.attr('data-trick-id'), value = $target
								.val(), $measuresContainer = $("div[data-trick-content='measure'][data-trick-standard-name][data-trick-id='" + standardId + "']:visible");
						$measuresContainer.find("div.list-group[data-trick-chapter-name!='" + value + "']:visible").hide();
						$measuresContainer.find("div.list-group[data-trick-chapter-name='" + value + "']:hidden").show();
						updateMeasureUI();
						$nav.trigger("trick.update.nav");
						return false;
					});

	$("div.list-group[data-trick-chapter-name]>.list-group-item").on("click", changeMeasure);

	$returnAnalysis.on("click", function() {
		$returnAnalysis.attr("href", $returnAnalysis.attr("data-base-url") + "#tabStandard_" + $standardSelector.val());
	});

	updateNavigation();
	updateMeasureUI();
});