function findDefaultLanguageId() {
	var $language = $("#section_kb_measure #languageselect").val();
	if ($.isNumeric($language))
		return $language;
	$language = $("tbody tr[data-trick-id]:first-child", "#section_language");
	return $language.length ? $language.attr('data-trick-id') : 1;
}

function rebuildMeasureLanguage() {
	var $languageSelect = $("#languageselect"), selected = $languageSelect.val();
	$languageSelect.empty();
	$("tbody tr[data-trick-id]", "#section_language").each(function () {
		var $this = $(this), $option = $("<option />"), id = $this.attr("data-trick-id");
		$option.text($("td[data-field-name='name']", $this).text());
		$option.attr("value", id);
		$option.appendTo($languageSelect);
		if (id == selected)
			$option.prop("selected", true);
	});
}

function showTabMeasure(idStandard, idLanguage) {
	if (idLanguage == undefined || idLanguage == null)
		idLanguage = findDefaultLanguageId();
	var $measureTab = $("#control_tab_measure");
	if ($("#section_kb_standard input:checked").length)
		$measureTab.show();
	else
		$measureTab.hide();
	var $tab = $("#tab-measure"), $section = $("#section_kb_measure");
	$tab.attr("data-update-required", !($section.attr("data-standard-id") == idStandard && $section.attr("data-language-id") == idLanguage));
}

function showMeasures(idStandard, languageId, reloadBody) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_kb_standard");
		if (selectedScenario.length != 1)
			return false;
		idStandard = selectedScenario[0];
	}
	if (languageId == undefined || languageId == null)
		languageId = findDefaultLanguageId();
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/KnowledgeBase/Standard/" + idStandard + "/Language/" + languageId + "/Measures",
		type: "POST",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $newSection = $("#section_kb_measure", new DOMParser().parseFromString(response, "text/html"));
			if ($newSection.length) {
				$section = $("#section_kb_measure");
				var sectionSmartUpdate = new SectionSmartUpdate("section_kb_measure", $newSection);
				if (reloadBody || sectionSmartUpdate.Update())
					$("tbody", $section).replaceWith($("tbody", $newSection));
				$("#hidden-standard-data", $section).replaceWith($("#hidden-standard-data", $newSection));
				$("#section_title_measure", $section).text($("#section_title_measure", $newSection).text());
				$section.attr("data-standard-id", idStandard);
				$section.attr("data-language-id", languageId);
				updateMenu(undefined, "#section_kb_measure", "#menu_measure_description");
			} else
				unknowError();

		},
		error: unknowError,
		complete: function () {
			$progress.hide();
		}
	});
	return false;
}

function newMeasure(idStandard) {
	if (idStandard == null || idStandard == undefined)
		idStandard = $("#idStandard", "#section_kb_measure").val();
	if(idStandard == null || idStandard == undefined)
		return false;
	var $modal = $("#addMeasureModel"), $progress = $("#loading-indicator").show();
	$(".label-danger", $modal).remove();
	$("#addmeasurebutton", $modal).prop("disabled", false);
	$("#measure_id", $modal).prop("value", "0");
	$("#measure_reference", $modal).prop("value", "");
	$("#measure_computable input[value='true']", $modal).parent().button("toggle");
	$("#measure_form", $modal).prop("action", context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Save");
	$("#addMeasureModel-title", $modal).text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure"));
	$("#addmeasurebutton", $modal).text(MessageResolver("label.action.save", "Save"));

	$.ajax({
		url: context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Add",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $content = $("#measurelanguageselect", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				var language = $("#languageselect", "#measures_body").val();
				$("#measurelanguages", $modal).html(response);
				$("#measurelanguageselect", $modal).change(function () {
					var language = parseInt($(this).find("option:selected").attr("value"));
					$("#measurelanguages div[data-trick-id][data-trick-id!='" + language + "']", $modal).css("display", "none");
					$("#measurelanguages div[data-trick-id][data-trick-id='" + language + "']", $modal).css("display", "block");
				});
				$("#measurelanguageselect option[value='" + language + "']", $modal).prop("selected", true);
				$("#measurelanguageselect", $modal).change();
				generateHelper(undefined,$modal.modal("show"));
			} else
				unknowError();
			return false;
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});

	return false;
}

function editSingleMeasure(measureId, idStandard) {

	if (idStandard == null || idStandard == undefined)
		idStandard = $("#idStandard", "#section_kb_measure").val();
	var $modal = $("#addMeasureModel");
	$(".label-danger", $modal).remove();
	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_kb_measure");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
	}

	var $progress = $("#loading-indicator").show(), $measure = $("tbody>tr[data-trick-id='" + measureId + "']>td[data-trick-field]", "#section_kb_measure"),
	reference = $measure.filter("td[data-trick-field='reference']").text(), 
	computable =  $measure.filter("td[data-trick-field='computable']").attr("data-trick-real-value");
	$("#measure_id", $modal).prop("value", measureId);
	$("#measure_reference", $modal).prop("value", reference);
	$("#measure_computable input[value='"+computable+"']", $modal).parent().button("toggle");
	$("#measure_form", $modal).prop("action", context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Save");
	$("#addMeasureModel-title", $modal).text(MessageResolver("title.knowledgebase.measure.update", "Update Measure"));
	$("#addmeasurebutton", $modal).text(MessageResolver("label.action.save", "Save"));
	$.ajax({
		url: context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/" + measureId + "/Edit",
		type: "GET",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var content = new DOMParser().parseFromString(response, "text/html");
			if (content.getElementById("measurelanguageselect") != null) {
				var language = $("#languageselect", "#section_kb_measure").val();
				$("#measurelanguages", $modal).html(response);
				$("#measurelanguageselect", $modal).change(function () {
					var language = parseInt($(this).find("option:selected").attr("value"));
					$("#measurelanguages div[data-trick-id][data-trick-id!='" + language + "']", $modal).css("display", "none");
					$("#measurelanguages div[data-trick-id][data-trick-id='" + language + "']", $modal).css("display", "block");
				});
				$("#measurelanguageselect option[value='" + language + "']", $modal).prop("selected", true);
				$("#measurelanguageselect", $modal).trigger("change");
				generateHelper(undefined,$modal.modal("show"));
			} else
				unknowError();
			return false;
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});

	return false;
}

function saveMeasure() {
	var $progress = $("#loading-indicator").show(), $modal = $("#addMeasureModel"), $form = $("#measure_form", $modal), $progressBar = $("#save-measure-progress-bar", $modal), $buttonSubmit = $("#addmeasurebutton", $modal);
	$modal.find(".label-danger").remove();
	$.ajax({
		url: $form.prop("action"),
		type: "post",
		data: serializeForm($form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var languages = $form.find("select option"), languageDataValidation = {};
			for (var i = 0; i < languages.length; i++) {
				var idLanguage = $(languages[i]).val();
				languageDataValidation["measureDescriptionText.domain_" + idLanguage] = "#measure_domain_" + idLanguage;
				languageDataValidation["measureDescriptionText.description_" + idLanguage] = "#measure_description_" + idLanguage;
			}
			for (var error in response) {
				var $errorElement = $("<label class='label label-danger' />").text(response[error]), languageData = languageDataValidation[error];
				if (languageData != undefined) {
					$errorElement.appendTo($form.find(languageData).parent());
					continue;
				}
				switch (error) {
					case "measuredescription.reference":
						$errorElement.appendTo($form.find("#measure_reference").parent());
						break;
					case "measuredescription.level":
						$errorElement.appendTo($form.find("#measure_level").parent());
						break;
					case "measureDescription":
						$errorElement.appendTo($form.parent());
						break;
				}
			}
			if (!$modal.find(".label-danger").length) {
				$modal.modal("hide");
				var language = $("#languageselect", "#section_kb_measure").val(), idStandard = $("#idStandard", "#section_kb_measure").val();
				showMeasures(idStandard, language, $("#measure_id", $form).val() < 1);
			}
			return false;
		},
		error: unknowError,
		complete: function () {
			$buttonSubmit.prop("disabled", false);
			$progress.hide();
		}
	});

	return false;
}

function deleteMeasure(force) {
	$(".label-danger", "#addMeasureModel").remove();
	var selectedMeasures = findSelectItemIdBySection("section_kb_measure");
	if (!selectedMeasures || selectedMeasures.length != 1)
		return false;
	var $deleteModal = $("#deleteMeasureModel"), $measureSection = $("#section_kb_measure"), idStandard = idStandard = $("#idStandard", $measureSection).val(), standard = $("#standardLabel", $measureSection).val(), reference = $(
		"tbody tr[data-trick-id='" + selectedMeasures[0] + "'] td:not(:first-child)", $measureSection).first().text(), url, message;
	if (force) {
		url = context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Force/Delete/" + selectedMeasures[0];
		message = MessageResolver("label.measure.question.force.delete", "Are you sure that you want to force deleting of the measure with the Reference: <strong>" + reference
			+ "</strong> from the standard <strong>" + standard + " </strong>?", [reference, standard]);
	} else {
		url = context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Delete/" + selectedMeasures[0];
		message = MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: <strong>" + reference
			+ "</strong> from the standard <strong>" + standard + " </strong>?", [reference, standard]);
	}
	$deleteModal.find(".modal-body").html(message);
	$deleteModal.find("#deletemeasurebuttonYes").unbind("click.delete").one("click.delete", function () {
		$deleteModal.modal("hide");
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: url,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.success != undefined)
					showMeasures(idStandard, $("#languageselect", "#section_kb_measure").val());
				else if (response.error != undefined)
					showDialog("#alert-dialog", response.error)
				else
					unknowError();
				return true;
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
		return true;
	});
	$deleteModal.modal("show");
	return false;
}
