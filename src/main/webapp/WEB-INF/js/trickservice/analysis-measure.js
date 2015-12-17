function loadMeasureData(id) {
	var $currentUI = $("#measure-ui");
	if ($currentUI.attr("data-trick-id") == id)
		return false;
	$.ajax({
		url : context + "/Analysis/Standard/Measure/" + id + "/Form",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var $measureUI = $("div#measure-ui", new DOMParser().parseFromString(response, "text/html"));
			if ($measureUI.length)
				$currentUI.replaceWith($measureUI);
			else unknowError();
				
		},
		error : unknowError
	});
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