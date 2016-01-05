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
	if (value != oldValue) {
		$.ajax({
			url : context + "/Analysis/EditField/Measure/" + id + "/Update",
			type : "post",
			async : false,
			data : '{"id":' + id + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				
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
				$(".form-control[name!='cost']").on("blur", saveMeasureData);
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function backupDescriptionHeight() {
	var $description = $("#description"), height = $description.outerHeight(), defaultHeight = $description.attr('data-default-height');
	if ($description.length) {
		if (Math.abs(height - defaultHeight) > 5) {
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
	var height = application["measure-description-size"];
	if (height != undefined) {
		$("#description").css({
			"height" : height
		});
	}
	return false;
}

function updateScroll(element) {
	var $measure = $(element), $parent = $measure.closest("div[data-trick-standard-name][data-trick-content='measure']"), parentTop = $parent.offset().top, measureTop = $measure
			.offset().top;
	$parent.scrollTop(measureTop > parentTop ? measureTop - parentTop : 0);
	return false;
}

function updateMeasureUI() {
	var $measure = $("div.list-group:visible>.list-group-item.active"), id = $measure.attr('data-trick-id');
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

	var $nav = $("ul.nav.nav-pills[data-trick-role='nav-measure']").on("trick.update.nav", updateNavigation), $previousChatper = $("[data-trick-nav='previous-chapter']"), $nextChapter = $("[data-trick-nav='next-chapter']"), $previousMeasure = $("[data-trick-nav='previous-measure']"), $nextMeasure = $("[data-trick-nav='next-measure']");

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

	$("select[name='standard']").on('change', function(e) {
		var $target = $(e.currentTarget), value = $target.val();
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

	updateNavigation();
	updateMeasureUI();
});