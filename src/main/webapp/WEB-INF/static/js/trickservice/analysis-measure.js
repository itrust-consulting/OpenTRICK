
/**
 * Saves the measure data to the server.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns false to prevent the default form submission.
 */
function saveMeasureData(e) {
	var $target = $(e.currentTarget), value = $target.val(), id = $("#measure-ui").attr('data-trick-id'), name = $target.attr('name'), type = $target.attr('data-trick-type'), oldValue = $target
		.hasAttr("placeholder") ? $target.attr("placeholder") : $target.attr("data-trick-value");
	if (value == oldValue)
		$target.parent().removeClass('has-error');
	else {
		var $progress = $("#loading-indicator").show();
		$.ajax(
			{
				url: context + "/Analysis/EditField/Measure/" + id + "/Update",
				type: "post",
				data: '{"id":' + id + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
				contentType: "application/json;charset=UTF-8",
				success: function (response) {
					if (response.message == undefined)
						unknowError();
					else {
						var $parent = $target.parent();
						if (response.error) {
							$parent.on("show.bs.popover", function () {
								var popover = $parent.data('bs.popover');
								setTimeout(function () {
									popover.destroy();
								}, 3000);
							});
							$parent.addClass("has-error").removeClass("has-success").popover({
								"content": response.message,
								'triger': 'manual',
								"container": 'body',
								'placement': 'auto',
								'template': application.errorTemplate
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
									for (var fieldName in field) {
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

							setTimeout(function () {
								$parent.removeClass("has-success");
							}, 3000);
						}

					}
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
	}
	return false;
}

/**
 * Loads measure data for a given ID.
 * @param {number} id - The ID of the measure.
 * @returns {boolean} - Returns false.
 */
function loadMeasureData(id) {
	var $currentUI = $("#measure-ui");
	if (id == undefined)
		$currentUI.attr("data-trick-id", "0").empty();
	else if ($currentUI.attr("data-trick-id") != id || application["measure-view-invalidate"]) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/Measure/" + id + "/Load",
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				var $measureUI = $("div#measure-ui", new DOMParser().parseFromString(response, "text/html"));
				if ($measureUI.length) {

					backupFieldHeight("measure", ["description", "measure-tocheck", "measure-comment", "measure-todo"], $currentUI);
					restoreFieldHeight("measure", ["description", "measure-tocheck", "measure-comment", "measure-todo"], $measureUI);

					$currentUI.replaceWith($measureUI);

					$("#description-switch-language .btn", $measureUI).on("click", e => {
						return updateDescription($(e.currentTarget), id, $("#description", $measureUI), $progress);
					});

					if (OPEN_MODE.isReadOnly()) {
						$("select:not([disabled])", $measureUI).prop("disabled", true);
						$("input:not([disabled]),textarea:not([disabled])", $measureUI).attr("readOnly", true);
					} else {
						$("select", $measureUI).on("change", saveMeasureData);
						$("input[name!='cost'],textarea", $measureUI).on("blur", saveMeasureData);
						$("input[type='number']", $measureUI).on("change", function (e) {
							var $target = $(e.currentTarget);
							if (!$target.is(":focus"))
								$target.focus();
							return this;
						});
					}
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
			application["measure-view-invalidate"] = false;
		});
	}
	return false;
}

/**
 * Updates the description of an element.
 *
 * @param {jQuery} $element - The jQuery element to update.
 * @param {string} id - The ID of the element.
 * @param {jQuery} $description - The jQuery element representing the description.
 * @param {jQuery} $progress - The jQuery element representing the progress indicator.
 * @returns {boolean} - Returns false.
 */
function updateDescription($element, id, $description, $progress) {
	$progress.show();
	$.ajax({
		url: context + "/Analysis/Standard/Measure/" + id + "/Description/" + $element.attr("lang"),
		contentType: "application/json;charset=UTF-8",
		success: function (response) {
			if (response["error"])
				showDialog("#alert-dialog", response["error"]);
			else if (response["description"] != undefined) {
				$description.attr("lang", language).text(response["description"]);
				$element.attr("disabled", true).find("img").attr("src", $element.attr("data-flag-disabled"));
				var lang = $element.attr("lang"), $other = $element.parent().find(".btn[lang!='" + lang + "']");
				$other.removeAttr('disabled').find("img").attr("src", $other.attr("data-flag-enabled"));
			}
			else unknowError();
		}, error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Updates the Measure UI based on the selected measure.
 */
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

/**
 * Updates the measure navigation based on the current selected chapter and measure.
 * @returns {boolean} Returns false.
 */
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

/**
 * Changes the measure based on the event target.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns false to prevent default behavior.
 */
function changeMeasure(e) {
	var $target = $(e.currentTarget);
	if (!$target.hasClass('active')) {
		$(".list-group-item.active", $target.closest(".list-group")).removeClass("active");
		$target.addClass('active');
	}
	updateMeasureView();
	return false;
}

/**
 * Updates the measure view.
 * @returns {boolean} Returns false.
 */
function updateMeasureView() {
	var $section = $("#tab-measure-edition");
	if ($section.is(":visible")) {
		if (!application["measure-view-init"])
			initiliseMeasureView();
		updateMeasureUI();
		updateMeasureNavigation();
	} else
		$section.attr("data-update-required", application["measure-view-invalidate"] = true);// force
	// update
	return false;
}

/**
 * Extracts the chapter from a given reference.
 * @param {string} reference - The reference string.
 * @returns {string} The extracted chapter.
 */
function extractChapter(reference) {
	return reference.split(/\.|\s|;|-|,/, (reference.toUpperCase().startsWith("A.")) || (reference.toUpperCase().startsWith("M.")) ? 2 : 1).join('.');
}

/**
 * Finds the measures chapter with the given name and performs necessary operations.
 *
 * @param {string} chapter - The name of the chapter to find.
 * @param {jQuery} $measureContainer - The container element for measures.
 * @param {jQuery} $chapterSelector - The selector element for chapters.
 * @returns {jQuery} - The jQuery object representing the found chapter.
 */
function findMeasuresChapter(chapter, $measureContainer, $chapterSelector) {
	let $chapter = $("[data-trick-chapter-name='" + chapter + "']", $measureContainer);
	if (!$chapter.length) {
		const sorter = natsort({ insensitive: true });
		$chapter = $("<div hidden='hidden' class='list-group' data-trick-chapter-name='" + chapter + "' />").appendTo($measureContainer);
		$("<option value='" + chapter + "' />").text(MessageResolver("label.index.chapter", "Chapter {0}").replace("{0}", chapter)).appendTo($chapterSelector);

		$("option", $chapterSelector).sort(function (a, b) {
			return sorter(a.getAttribute("value"), b.getAttribute("value"));
		}).detach().appendTo($chapterSelector);
	}
	return $chapter;
}

/**
 * Sorts the measure chapters by reference.
 *
 * @param {jQuery} $chapter - The jQuery object representing the measure chapter.
 * @returns {jQuery} - The sorted jQuery object representing the measure chapter.
 */
function sortMeasureChapterByReference($chapter) {
	const sorter = natsort({ insensitive: true });
	return $("[data-trick-reference][data-trick-id]", $chapter).sort(function (a, b) {
		return sorter(a.getAttribute("data-trick-reference"), b.getAttribute("data-trick-reference"));
	}).detach().appendTo($chapter).removeClass("active").filter(":first").addClass("active");
}

/**
 * Updates the measure navigation control based on the provided measure.
 * 
 * @param {Object} measure - The measure object.
 * @returns {boolean} - Returns false if the measure reference is undefined, otherwise returns true.
 */
function updateMeasureNavigationControl(measure) {
	if (measure.reference == undefined)
		return false;
	var $tabSection = $("#tab-measure-edition"), $measureContainer = $("div[data-trick-id='" + measure.idStandard + "'][data-trick-content='measure']", $tabSection), $chapterSelector = $(
		"div[data-trick-id='" + measure.idStandard + "'][data-trick-content='chapter'] select[name='chapter']", $tabSection), chapter = extractChapter(measure.reference), $selectedMeasure = $("[data-trick-reference][data-trick-id='"
			+ measure.id + "']");
	if ($selectedMeasure.length) {
		var $chapter = $selectedMeasure.closest("[data-trick-chapter-name]");
		if ($chapter.attr("data-trick-chapter-name") != chapter)
			$chapter = findMeasuresChapter(chapter, $measureContainer, $chapterSelector);

		if ($selectedMeasure.attr("data-trick-reference") != measure.reference) {
			$selectedMeasure.attr("data-trick-reference", measure.reference).appendTo($chapter);
			sortMeasureChapterByReference($chapter);
		}

		$selectedMeasure.attr("title", measure.domain).text(measure.reference + " - " + measure.domain);
	} else {
		var $chapter = findMeasuresChapter(chapter, $measureContainer, $chapterSelector);
		$("<a href='#' style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis;' class='list-group-item' />").attr("title", measure.domain).attr(
			"data-trick-reference", measure.reference).attr("data-trick-id", measure.id).text(
				measure.reference + " - " + measure.domain).appendTo($chapter).on("click", changeMeasure);
		sortMeasureChapterByReference($chapter);
	}
	return updateMeasureView();
}

/**
 * Removes a measure from the measure navigation.
 *
 * @param {string} idStandard - The ID of the standard.
 * @param {string} idMeasure - The ID of the measure.
 * @returns {boolean} - Returns false.
 */
function removeFromMeasureNavigation(idStandard, idMeasure) {
	let $tabSection = $("#tab-measure-edition"), $measure = $("div[data-trick-id='" + idStandard
		+ "'][data-trick-content='measure'] [data-trick-reference][data-trick-id='" + idMeasure + "']", $tabSection), $chapter = $measure
			.closest("[data-trick-chapter-name]");
	if ($("[data-trick-reference]", $chapter).length == 1) {
		$("div[data-trick-id='" + idStandard + "'][data-trick-content='chapter'] select[name='chapter']>option[value='" + $chapter.attr("data-trick-chapter-name") + "']",
			$tabSection).remove();
		$chapter.remove();
	}
	$measure.remove();
	$tabSection.attr("data-update-required", true);// update if required
	return false;
}

/**
 * Initializes the measure view.
 * This function sets up event handlers and initializes the UI elements for the measure view.
 * @returns {boolean} Returns false.
 */
function initiliseMeasureView() {

	let $nav = $("#tab-measure-edition ul.nav.nav-pills[data-trick-role='nav-measure']").on("trick.update.nav", updateMeasureNavigation), $returnAnalysis = $("a[data-base-url]",
		$nav), $standardSelector = $("select[name='standard']"), $previousChatper = $("[data-trick-nav='previous-chapter']"), $nextChapter = $("[data-trick-nav='next-chapter']"), $previousMeasure = $("[data-trick-nav='previous-measure']"), $nextMeasure = $("[data-trick-nav='next-measure']"), $chapterSelector = $("#tab-measure-edition select[name='chapter']");

	$previousChatper.on("click", function () {
		$("#tab-measure-edition select[name='chapter']:visible>option:selected").prev(":last").prop('selected', true).parent().change();
		return false;
	});

	$nextChapter.on("click", function () {
		$("#tab-measure-edition select[name='chapter']:visible>option:selected").next(":first").prop('selected', true).parent().change();
		return false;
	});

	$previousMeasure.on("click", function () {
		$("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active").prev(":visible:last")
			.click();
		return false;
	});

	$nextMeasure.on("click", function () {
		$("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id]:visible div.list-group .list-group-item.active").next(":visible:first")
			.click();
		return false;
	});

	$standardSelector.on('change', function (e) {
		$("#tab-measure-edition div[data-trick-standard-name][data-trick-id!='" + this.value + "']:visible").hide();
		$("#tab-measure-edition div[data-trick-standard-name][data-trick-id='" + this.value + "']:hidden").show().find("select[name='chapter']").trigger("change");
		return false;
	});

	$("#tab-measure-edition div.list-group[data-trick-chapter-name]>.list-group-item").on("click", changeMeasure);

	$chapterSelector
		.on(
			'change',
			function (e) {
				let $target = $(e.currentTarget), $parent = $target.closest("div[data-trick-standard-name]"), standardId = $parent.attr('data-trick-id'), $measuresContainer = $("#tab-measure-edition div[data-trick-content='measure'][data-trick-standard-name][data-trick-id='"
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