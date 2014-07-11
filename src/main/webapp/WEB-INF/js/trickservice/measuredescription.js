function showMeasures(normId, languageId, modal) {
	if (normId == null || normId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_norm"));
		if (selectedScenario.length != 1)
			return false;
		normId = selectedScenario[0];
		var language = $("#section_language tbody tr[trick-id]:first-child");
		languageId = language.length ? $(language).attr('trick-id') : 1;
	}
	$.ajax({
		url : context + "/KnowledgeBase/Norm/" + normId + "/language/" + languageId + "/Measures",
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var isNew = false;
			if (modal == null || modal == undefined) {
				modal = new Modal();
				modal.Intialise();
				$(modal.modal).attr('id', 'showMeasuresModel');
				$(modal.modal_dialog).prop("style", "width: 95%; min-width:1170px;");
				$(modal.modal_footer).remove();
				application["modal-measure"] = modal;
				modal.setBody($(doc).find("#measures_body"));
				$(modal.modal_title).html($(doc).find("#Measures").html());
				$(modal.modal_body).find("#languageselect").change(function(e) {
					showMeasures($(modal.modal_header).find("#normId").val(), $(e.target).val(), modal);
				});
				modal.Show();
				setTimeout(function() {
					fixedTableHeader($(modal.modal).find(".table-fixed-header"));
				}, 400);
			} else
				$(modal.modal_body).find("tbody").replaceWith($(doc).find("tbody"));
		},
		error : unknowError
	});
	return false;
}

function saveMeasure() {
	var modalMeasureForm = application["modal-measure-form"];
	var form = $(modalMeasureForm.modal_body).find("form");
	$.ajax({
		url : form.prop("action"),
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var alert = $(modalMeasureForm.modal_body).find(".label-danger");
			if (alert.length)
				alert.remove();
			var languages = form.find("select option");
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
					$(errorElement).appendTo(form.find(languageData).parent());
					continue;
				}
				switch (error) {
				case "measuredescription.reference":
					$(errorElement).appendTo(form.find("#measure_reference").parent());
					break;
				case "measuredescription.level":
					$(errorElement).appendTo(form.find("#measure_level").parent());
					break;
				case "measureDescription":
					$(errorElement).appendTo(form.parent());
					break;
				}
			}
			if (!$(modalMeasureForm.modal_body).find(".label-danger").length) {
				modalMeasureForm.Destroy();
				var language = $(application["modal-measure"].modal).find("#languageselect").val();
				var normId = $(application["modal-measure"].modal).find("#normId").val();
				return showMeasures(normId, language, application["modal-measure"]);
			}
			return false;

		},
		error : unknowError
	});
	return false;
}

function deleteMeasure(measureId, reference, norm) {
	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection(undefined, application["modal-measure"].modal);
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
		norm = $(application["modal-measure"].modal).find("#normLabel").val();
		reference = $(application["modal-measure"].modal).find("tbody tr[trick-id='" + measureId + "'] td:nth-child(3)").text();
	}
	var deleteModal = new Modal();
	deleteModal.FromContent($("#deleteMeasureModel").clone());
	deleteModal.setBody(MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: ") + "&nbsp;<strong>" + reference
			+ "</strong> from the norm <strong>" + norm + " </strong>?", [ reference, norm ]);
	var normId = $(application["modal-measure"].modal).find("#normId").val();
	$(deleteModal.modal_header).find("button").click(function() {
		delete deleteModal;
	});
	$(deleteModal.modal_footer).find("#deletemeasurebuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Norm/" + normId + "/Measures/Delete/" + measureId,
			type : "POST",
			contentType : "application/json",
			async : false,
			success : function(response) {
				if (response.success) {
					var language = $(application["modal-measure"].modal).find("#languageselect").val();
					return showMeasures(normId, language, application["modal-measure"]);
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

function newMeasure(normId) {
	if (normId == null || normId == undefined)
		normId = $(application["modal-measure"].modal).find("#normId").val();
	var modalMeasureForm = new Modal();
	modalMeasureForm.FromContent($("#addMeasureModel").clone());
	$(modalMeasureForm.modal).find("#measure_id").prop("value", "-1");
	$(modalMeasureForm.modal).find("#measure_reference").prop("value", "");
	$(modalMeasureForm.modal).find("#measure_level").prop("value", "");
	$(modalMeasureForm.modal).find("#measure_computable").prop("checked", "false");
	$.ajax({
		url : context + "/KnowledgeBase/Norm/" + normId + "/Measures/Add",
		type : "get",
		async : true,
		contentType : "application/json",
		success : function(response) {
			$(modalMeasureForm.modal).find("#measurelanguages").html(response);
			$(modalMeasureForm.modal).find("#measurelanguageselect").change(function() {
				var language = parseInt($(this).find("option:selected").attr("value"));
				$(modalMeasureForm.modal).find("#measurelanguages div[trick-id][trick-id!='" + language + "']").hide();
				$(modalMeasureForm.modal).find("#measurelanguages div[trick-id][trick-id='" + language + "']").show();
			});
			$(modalMeasureForm.modal).find("#measure_form").prop("action", context + "/KnowledgeBase/Norm/" + normId + "/Measures/Save");
			return false;
		},
		error : unknowError
	});
	$(modalMeasureForm.modal).find("#addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure"));
	$(modalMeasureForm.modal).find("#addmeasurebutton").text(MessageResolver("label.action.add", "Add"));
	application["modal-measure-form"] = modalMeasureForm;
	modalMeasureForm.Show();
	return false;
}

function measureSortTable(element) {
	// check if datatable has to be initialised
	var tables = $(element).find("table");
	if (!tables.length)
		return false;
	// define sort order of text
	Array.AlphanumericSortOrder = 'AaÃ�Ã¡BbCcDdÃ�Ã°EeÃ‰Ã©Ä˜Ä™FfGgHhIiÃ�Ã­JjKkLlMmNnOoÃ“Ã³PpQqRrSsTtUuÃšÃºVvWwXxYyÃ�Ã½ZzÃžÃ¾Ã†Ã¦Ã–Ã¶';

	// flag to check for case sensitive comparation
	Array.AlphanumericSortIgnoreCase = true;

	// call the tablesorter plugin and apply the uitheme widget
	$(tables).tablesorter({
		headers : {
			0 : {
				sorter : false,
			}
		},
		textSorter : {
			1 : Array.AlphanumericSort,
			2 : function(a, b, direction, column, table) {
				if (table.config.sortLocaleCompare)
					return a.localeCompare(b);
				return versionComparator(a, b);
			},
			3 : $.tablesorter.sortNatural
		},
		theme : "bootstrap",
		headerTemplate : '{icon} {content}',
		widthFixed : true,
		widgets : [ "uitheme" ]
	});
}

function editSingleMeasure(measureId, normId) {
	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_measure_description"));
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
		normId = $("#normId").prop("value");
	}
	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();
	var rows = $("#measurestable").find("tr[trick-id='" + measureId + "'] td:not(:first-child)");
	$("#measure_id").prop("value", measureId);
	$("#measure_reference").prop("value", $(rows[1]).text());
	$("#measure_level").prop("value", $(rows[0]).text());
	if ($(rows[4]).attr("trick-computable") == "true") {
		$("#measure_computable").prop("checked", true);
	} else {
		$("#measure_computable").prop("checked", false);
	}

	$.ajax({
		url : context + "/KnowledgeBase/Norm/" + normId + "/Measures/" + measureId + "/Edit",
		type : "post",
		contentType : "application/json",
		success : function(response) {
			$("#measurelanguages").html(response);
			$("#measurelanguageselect").change(function() {
				var language = parseInt($(this).find("option:selected").attr("value"));
				$("#measurelanguages div[trick-id][trick-id!='" + language + "']").hide();
				$("#measurelanguages div[trick-id][trick-id='" + language + "']").show();
			});
			$("#measure_form").prop("action", context + "/KnowledgeBase/Norm/" + normId + "/Measures/Save");
			return false;

		},
		error : unknowError
	});

	$("#addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Update", "Update new Measure"));
	$("#addmeasurebutton").text(MessageResolver("label.action.edit", "Edit"));

	$("#addMeasureModel").modal('toggle');
	$("#addMeasureModel").children(":first").attr("style", "z-index:1080");
	return false;
}