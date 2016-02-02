function findDefaultLanguageId() {
	var $language =  $("#section_kb_measure #languageselect").val();
	if($.isNumeric($language))
		return $language;
	$language = $("#section_language tbody tr[data-trick-id]:first-child");
	return $language.length ? $language.attr('data-trick-id') : 1;
}

function rebuildMeasureLanguage() {
	var $languageSelect = $("#languageselect"), selected = $languageSelect.val();
	$languageSelect.empty();
	$("#section_language tbody tr[data-trick-id]").each(function() {
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
	var $tab = $("#tab_measure"), $section = $("#section_kb_measure");
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
	$("#progress-dialog").modal("show");
	$.ajax({
		url : context + "/KnowledgeBase/Standard/" + idStandard + "/Language/" + languageId + "/Measures",
		type : "POST",
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $newSection = $("#section_kb_measure", new DOMParser().parseFromString(response, "text/html"));
			if ($newSection.length) {
				$section = $("#section_kb_measure");
				var sectionSmartUpdate = new SectionSmartUpdate("section_kb_measure", $newSection);
				if (reloadBody || sectionSmartUpdate.Update()) {
					$("tbody", $section).replaceWith($("tbody", $newSection));
					console.log("here");
				}
				$("#hidden-standard-data", $section).replaceWith($("#hidden-standard-data", $newSection));
				$("#section_title_measure", $section).text($("#section_title_measure", $newSection).text());
				$section.attr("data-standard-id", idStandard);
				$section.attr("data-language-id", languageId);

			} else
				unknowError();

		},
		error : unknowError,
		complete : function() {
			$("#progress-dialog").modal("hide");
		}
	});
	return false;
}

function newMeasure(idStandard) {
	if (findSelectItemIdBySection("section_kb_measure").length)
		return false;
	if (idStandard == null || idStandard == undefined)
		idStandard = $("#section_kb_measure #idStandard").val();
	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	$("#addMeasureModel #addmeasurebutton").prop("disabled", false);
	$("#addMeasureModel #measure_id").prop("value", "-1");
	$("#addMeasureModel #measure_reference").prop("value", "");
	$("#addMeasureModel #measure_level").prop("value", "");
	$("#addMeasureModel #measure_computable").prop("checked", "false");

	$("#addMeasureModel #measure_form").prop("action", context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Save");
	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure"));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.add", "Add"));

	$.ajax({
		url : context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Add",
		type : "get",
		async : true,
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $content = $("#measurelanguageselect", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				var language = $("#measures_body #languageselect").val();
				$("#addMeasureModel #measurelanguages").html(response);
				$("#addMeasureModel #measurelanguageselect").change(function() {
					var language = parseInt($(this).find("option:selected").attr("value"));
					$("#addMeasureModel #measurelanguages div[data-trick-id][data-trick-id!='" + language + "']").css("display", "none");
					$("#addMeasureModel #measurelanguages div[data-trick-id][data-trick-id='" + language + "']").css("display", "block");
				});
				$("#addMeasureModel #measurelanguageselect option[value='" + language + "']").prop("selected", true);
				$("#addMeasureModel #measurelanguageselect").change();
				$("#addMeasureModel").modal("show");
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});

	return false;
}

function editSingleMeasure(measureId, idStandard) {

	if (idStandard == null || idStandard == undefined)
		idStandard = $("#section_kb_measure #idStandard").val();

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_kb_measure");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
	}
	var measure = $("#section_kb_measure tbody tr[data-trick-id='" + measureId + "'] td:not(:first-child)");

	$("#addMeasureModel #measure_id").prop("value", measureId);
	$("#addMeasureModel #measure_reference").prop("value", $(measure[1]).text());
	$("#addMeasureModel #measure_level").prop("value", $(measure[0]).text());
	$("#addMeasureModel #measure_computable").prop("checked", $(measure[4]).attr("data-trick-computable") == "true");

	$("#addMeasureModel #measure_form").prop("action", context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Save");
	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.measure.update", "Update Measure"));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.edit", "Update"));
	$.ajax({
		url : context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/" + measureId + "/Edit",
		type : "GET",
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			if ($(doc).find("#measurelanguageselect").length) {
				var language = $("#section_kb_measure #languageselect").val();
				$("#addMeasureModel #measurelanguages").html(response);
				$("#addMeasureModel #measurelanguageselect").change(function() {
					var language = parseInt($(this).find("option:selected").attr("value"));
					$("#addMeasureModel #measurelanguages div[data-trick-id][data-trick-id!='" + language + "']").css("display", "none");
					$("#addMeasureModel #measurelanguages div[data-trick-id][data-trick-id='" + language + "']").css("display", "block");
				});
				$("#addMeasureModel #measurelanguageselect option[value='" + language + "']").prop("selected", true);
				$("#addMeasureModel #measurelanguageselect").change();
				$("#addMeasureModel").modal("show");
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});

	return false;
}

function saveMeasure() {
	var $form = $("#addMeasureModel #measure_form"), $progressBar = $("#addMeasureModel #save-measure-progress-bar"), $buttonSubmit = $("#addMeasureModel #addmeasurebutton");
	$buttonSubmit.prop("disabled", true);
	$progressBar.show();
	$.ajax({
		url : $form.prop("action"),
		type : "post",
		data : serializeForm($form),
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var alert = $("#addMeasureModel").find(".label-danger");
			if (alert.length)
				alert.remove();
			var languages = $form.find("select option");
			var languageDataValidation = {};
			for (var i = 0; i < languages.length; i++) {
				var idLanguage = $(languages[i]).val();
				languageDataValidation["measureDescriptionText.domain_" + idLanguage] = "#measure_domain_" + idLanguage;
				languageDataValidation["measureDescriptionText.description_" + idLanguage] = "#measure_description_" + idLanguage;
			}
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				var languageData = languageDataValidation[error];
				if (languageData != undefined) {
					$(errorElement).appendTo($form.find(languageData).parent());
					continue;
				}
				switch (error) {
				case "measuredescription.reference":
					$(errorElement).appendTo($form.find("#measure_reference").parent());
					break;
				case "measuredescription.level":
					$(errorElement).appendTo($form.find("#measure_level").parent());
					break;
				case "measureDescription":
					$(errorElement).appendTo($form.parent());
					break;
				}
			}
			if (!$("#addMeasureModel").find(".label-danger").length) {
				$("#addMeasureModel").modal("hide");
				var language = $("#section_kb_measure #languageselect").val();
				var idStandard = $("#section_kb_measure #idStandard").val();
				showMeasures(idStandard, language, $("#measure_id", $form).val() < 1);
			} else
				$("#progress-dialog").modal("hide");
			return false;
		},
		error : unknowError,
		complete : function() {
			$buttonSubmit.prop("disabled", false);
			$progressBar.hide();
		}
	});

	return false;
}

function deleteMeasure(measureId, reference, standard) {

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (measureId == null || measureId == undefined) {
		var selectedMeasure = findSelectItemIdBySection("section_kb_measure");
		if (selectedMeasure.length != 1)
			return false;
		measureId = selectedMeasure[0];
	}

	idStandard = $("#section_kb_measure #idStandard").val();

	if (standard == null || standard == undefined)
		standard = $("#section_kb_measure #standardLabel").val();

	var measure = $("#section_kb_measure tbody tr[data-trick-id='" + measureId + "'] td:not(:first-child)");
	reference = $(measure[1]).text();

	var deleteModal = new Modal();
	deleteModal.FromContent($("#deleteMeasureModel").clone());
	deleteModal.setBody(MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: <strong>" + reference
			+ "</strong> from the standard <strong>" + standard + " </strong>?", [ reference, standard ]));
	$(deleteModal.modal_header).find("button").click(function() {
		delete deleteModal;
	});
	$(deleteModal.modal_footer).find("#deletemeasurebuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Delete/" + measureId,
			type : "POST",
			contentType : "application/json",
			async : false,
			success : function(response, textStatus, jqXHR) {
				if (response.success) {
					var language = $("#section_kb_measure #languageselect").val();
					return showMeasures(idStandard, language);
				} else if (response.error) {
					var error = new Modal();
					error.FromContent($("#alert-dialog").clone());
					error.setBody(response.error);
					error.Show();
				} else
					unknowError();
				return true;
			},
			error : unknowError
		});
		delete deleteModal;
		return true;
	});
	deleteModal.Show();
	return false;
}

function forceDeleteMeasure(measureId, reference, standard) {

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (measureId == null || measureId == undefined) {
		var selectedMeasure = findSelectItemIdBySection("section_kb_measure");
		if (selectedMeasure.length != 1)
			return false;
		measureId = selectedMeasure[0];
	}

	idStandard = $("#section_kb_measure #idStandard").val();

	if (standard == null || standard == undefined)
		standard = $("#section_kb_measure #standardLabel").val();

	var measure = $("#section_kb_measure tbody tr[data-trick-id='" + measureId + "'] td:not(:first-child)");
	reference = $(measure[1]).text();
	var deleteModal = new Modal($("#deleteMeasureModel").clone());
	deleteModal.setBody(MessageResolver("label.measure.question.force.delete", "Are you sure that you want to force deleting of the measure with the Reference: <strong>"
			+ reference + "</strong> from the standard <strong>" + standard + " </strong>?", [ reference, standard ]));
	$(deleteModal.modal_footer).find("#deletemeasurebuttonYes").click(function() {
		$(this).attr("disabled", true);
		$(this).unbind();
		$.ajax({
			url : context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Force/Delete/" + measureId,
			type : "POST",
			contentType : "application/json",
			async : false,
			success : function(response, textStatus, jqXHR) {
				if (response.success != undefined)
					showMeasures(idStandard, $("#section_kb_measure #languageselect").val());
				else if (response.error != undefined)
					showDialog("#alert-dialog", response.error)
				else
					unknowError();
				return true;
			},
			error : unknowError
		}).complete(function() {
			deleteModal.Destroy();
		});
		return true;
	});
	deleteModal.Show();
	return false;
}