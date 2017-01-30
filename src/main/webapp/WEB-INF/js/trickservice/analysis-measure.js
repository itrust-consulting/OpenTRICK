function saveMeasureData(e) {
	var $target = $(e.currentTarget), value = $target.val(), id = $("#measure-ui").attr('data-trick-id'), name = $target.attr('name'), type = $target.attr('data-trick-type'), oldValue = $target
			.hasAttr("placeholder") ? $target.attr("placeholder") : $target.attr("data-trick-value");
	if (value == oldValue)
		$target.parent().removeClass('has-error');
	else {
		var $progress = $("#loading-indicator").show();
		$.ajax(
				{
					url : context + "/Analysis/EditField/Measure/" + id + "/Update",
					type : "post",
					data : '{"id":' + id + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
					contentType : "application/json;charset=UTF-8",
					success : function(response) {
						if (response.message == undefined)
							unknowError();
						else {
							var $parent = $target.parent();
							if (response.error) {
								$parent.on("show.bs.popover", function() {
									var popover = $parent.data('bs.popover');
									setTimeout(function() {
										popover.destroy();
									}, 3000);
								});
								$parent.addClass("has-error").removeClass("has-success").popover({
									"content" : response.message,
									'triger' : 'manual',
									"container" : 'body',
									'placement' : 'auto',
									'template' : application.errorTemplate
								}).attr('title', response.message).popover("show");
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

								reloadMeasureRow(id, $("select[name='standard']").val());

								setTimeout(function() {
									$parent.removeClass("has-success");
								}, 3000);
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

function loadMeasureData(id) {
	var $currentUI = $("#measure-ui");
	if (id == undefined)
		$currentUI.attr("data-trick-id", "-1").empty();
	else if ($currentUI.attr("data-trick-id") != id || application["measure-view-invalidate"]) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Standard/Measure/" + id + "/Load",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var $measureUI = $("div#measure-ui", new DOMParser().parseFromString(response, "text/html"));
				if ($measureUI.length) {
					backupDescriptionHeight();
					$currentUI.replaceWith($measureUI);
					restoreDescriptionHeight();
					if (OPEN_MODE.isReadOnly()) {
						$("select:not([disabled])", $measureUI).prop("disabled", true);
						$("input:not([disabled]),textarea:not([disabled])", $measureUI).attr("readOnly", true);
					} else {
						$("select", $measureUI).on("change", saveMeasureData);
						$("input[name!='cost'],textarea", $measureUI).on("blur", saveMeasureData);
						$("input[type='number']", $measureUI).on("change", function(e) {
							var $target = $(e.currentTarget);
							if (!$target.is(":focus"))
								$target.focus();
							return this;
						});
					}
				} else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
			application["measure-view-invalidate"] = false;
		});
	}
	return false;
}

function backupDescriptionHeight() {
	var $description = $("#tab-measure-edition #description");
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
	var $description = $("#tab-measure-edition #description");
	if ($description.length) {
		application["measure-description-default-size"] = $description.outerHeight();
		var height = application["measure-description-size"];
		if (height != undefined) {
			$("#tab-measure-edition #description").css({
				"height" : height
			});
		}
	}
	return false;
}

function updateMeasureUI() {
	var $container = $("#tab-measure-edition div.list-group:visible");
	$measure = $(".list-group-item.active", $container), id = $measure.attr('data-trick-id');
	if ($container.length) {
		if (!$measure.is(":focus") && $("div[role='left-menu']").css("position") == "fixed")
			updateScroll($measure);
		loadMeasureData(id);
	} else {
		var $selector = $("#tab-measure-edition select[name='chapter']:visible>option:first");
		if ($selector.length)
			$selector.parent().trigger("change");
		else
			loadMeasureData();// clean UI
	}
}

function updateMeasureNavigation() {
	var $currentChapter = $("#tab-measure-edition select[name='chapter']:visible>option:selected"), $currentMeasure = $("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active"), $previousChatper = $(
			"#tab-measure-edition [data-trick-nav='previous-chapter']").parent(), $nextChapter = $("#tab-measure-edition [data-trick-nav='next-chapter']").parent(), $previousMeasure = $(
			"#tab-measure-edition [data-trick-nav='previous-measure']").parent(), $nextMeasure = $("#tab-measure-edition [data-trick-nav='next-measure']").parent();
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
		$(".list-group-item.active", $target.closest(".list-group")).removeClass("active");
		$target.addClass('active');
	}
	updateMeasureView();
	return false;
}

function updateMeasureView() {
	var $section = $("#tab-measure-edition");
	if ($section.is(":visible")) {
		if (!application["measure-view-init"])
			initilisateMeasureView();
		updateMeasureUI();
		updateMeasureNavigation();
	} else
		$section.attr("data-update-required", application["measure-view-invalidate"] = true);// force
	// update
	return false;
}

function extractChapter(reference) {
	return reference.split(/\.|\s|;|-|,/, (reference.toUpperCase().startsWith("A.")) || (reference.toUpperCase().startsWith("M.")) ? 2 : 1).join('.');
}

function findMeasuresChapter(chapter, $measureContainer, $chapterSelector) {
	var $chapter = $("[data-trick-chapter-name='" + chapter + "']", $measureContainer);
	if (!$chapter.length) {
		$chapter = $("<div hidden='hidden' class='list-group' data-trick-chapter-name='" + chapter + "' />").appendTo($measureContainer);
		$("<option value='" + chapter + "' />").text(MessageResolver("label.index.chapter", "Chapter {0}").replace("{0}", chapter)).appendTo($chapterSelector);

		$("option", $chapterSelector).sort(function(a, b) {
			return naturalSort(a.getAttribute("value"), b.getAttribute("value"));
		}).detach().appendTo($chapterSelector);
	}
	return $chapter;
}

function sortMeasureChapterByReference($chapter) {
	return $("[data-trick-reference][data-trick-level][data-trick-id]", $chapter).sort(function(a, b) {
		return naturalSort(a.getAttribute("data-trick-reference"), b.getAttribute("data-trick-reference"));
	}).detach().appendTo($chapter).removeClass("active").filter(":first").addClass("active");
}

function updateMeasureNavigationControl(measure) {
	if (measure.reference == undefined)
		return false;
	var $tabSection = $("#tab-measure-edition"), $measureContainer = $("div[data-trick-id='" + measure.idStandard + "'][data-trick-content='measure']", $tabSection), $chapterSelector = $(
			"div[data-trick-id='" + measure.idStandard + "'][data-trick-content='chapter'] select[name='chapter']", $tabSection), chapter = extractChapter(measure.reference), $selectedMeasure = $("[data-trick-reference][data-trick-level][data-trick-id='"
			+ measure.id + "']");
	if ($selectedMeasure.length) {
		var $chapter = $selectedMeasure.closest("[data-trick-chapter-name]");
		if ($chapter.attr("data-trick-chapter-name") != chapter)
			$chapter = findMeasuresChapter(chapter, $measureContainer, $chapterSelector);

		if ($selectedMeasure.attr("data-trick-reference") != measure.reference) {
			$selectedMeasure.attr("data-trick-reference", measure.reference).attr("data-trick-level", measure.level).appendTo($chapter);
			sortMeasureChapterByReference($chapter);
		}

		$selectedMeasure.attr("title", measure.domain).text(measure.reference + " - " + measure.domain);
	} else {
		var $chapter = findMeasuresChapter(chapter, $measureContainer, $chapterSelector);
		$("<a href='#' style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis;' class='list-group-item' />").attr("title", measure.domain).attr(
				"data-trick-reference", measure.reference).attr("data-trick-level", measure.level).attr("data-trick-id", measure.id).text(
				measure.reference + " - " + measure.domain).appendTo($chapter).on("click", changeMeasure);
		sortMeasureChapterByReference($chapter);
	}
	return updateMeasureView();
}

function removeFromMeasureNavigation(idStandard, idMeasure) {
	var $tabSection = $("#tab-measure-edition"), $measure = $("div[data-trick-id='" + idStandard
			+ "'][data-trick-content='measure'] [data-trick-reference][data-trick-level][data-trick-id='" + idMeasure + "']", $tabSection), $chapter = $measure
			.closest("[data-trick-chapter-name]");
	if ($("[data-trick-reference][data-trick-level]", $chapter).length == 1) {
		$("div[data-trick-id='" + idStandard + "'][data-trick-content='chapter'] select[name='chapter']>option[value='" + $chapter.attr("data-trick-chapter-name") + "']",
				$tabSection).remove();
		$chapter.remove();
	}
	$measure.remove();
	$tabSection.attr("data-update-required", true);// update if required
	return false;
}

function initilisateMeasureView() {

	var $nav = $("#tab-measure-edition ul.nav.nav-pills[data-trick-role='nav-measure']").on("trick.update.nav", updateMeasureNavigation), $returnAnalysis = $("a[data-base-url]",
			$nav), $standardSelector = $("select[name='standard']"), $previousChatper = $("[data-trick-nav='previous-chapter']"), $nextChapter = $("[data-trick-nav='next-chapter']"), $previousMeasure = $("[data-trick-nav='previous-measure']"), $nextMeasure = $("[data-trick-nav='next-measure']"), $chapterSelector = $("#tab-measure-edition select[name='chapter']");

	$previousChatper.on("click", function() {
		$("#tab-measure-edition select[name='chapter']:visible>option:selected").prev(":last").prop('selected', true).parent().change();
		return false;
	});

	$nextChapter.on("click", function() {
		$("#tab-measure-edition select[name='chapter']:visible>option:selected").next(":first").prop('selected', true).parent().change();
		return false;
	});

	$previousMeasure.on("click", function() {
		$("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active").prev(":visible:last")
				.click();
		return false;
	});

	$nextMeasure.on("click", function() {
		$("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active").next(":visible:first")
				.click();
		return false;
	});

	$standardSelector.on('change', function(e) {
		$("#tab-measure-edition div[data-trick-standard-name][data-trick-id!='" + this.value + "']:visible").hide();
		$("#tab-measure-edition div[data-trick-standard-name][data-trick-id='" + this.value + "']:hidden").show().find("select[name='chapter']").trigger("change");
		return false;
	});

	$("#tab-measure-edition div.list-group[data-trick-chapter-name]>.list-group-item").on("click", changeMeasure);

	$chapterSelector
			.on(
					'change',
					function(e) {
						var $target = $(e.currentTarget), $parent = $target.closest("div[data-trick-standard-name]"), standardId = $parent.attr('data-trick-id'), $measuresContainer = $("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id='"
								+ standardId + "']:visible");
						$measuresContainer.find("div.list-group[data-trick-chapter-name!='" + this.value + "']:visible").hide();
						$measuresContainer.find("div.list-group[data-trick-chapter-name='" + this.value + "']:hidden").show();
						updateMeasureUI();
						$nav.trigger("trick.update.nav");
						return false;
					});

	application["measure-view-init"] = true;
	$standardSelector.trigger("change");
	$chapterSelector.filter(":visible").trigger("change");
	return false;
}