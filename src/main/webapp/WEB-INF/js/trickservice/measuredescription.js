function showMeasures(idStandard, languageId) {
	if (idStandard == null || idStandard == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_kb_standard"));
		if (selectedScenario.length != 1)
			return false;
		idStandard = selectedScenario[0];
	}
	
	if(languageId == undefined || languageId == null) {
		var language = $("#section_language tbody tr[trick-id]:first-child");
		languageId = language.length ? $(language).attr('trick-id') : 1;
	}
	
	$.ajax({
		url : context + "/KnowledgeBase/Standard/" + idStandard + "/Language/" + languageId + "/Measures",
		type : "POST",
		contentType : "application/json",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			
			$("#section_measure_description #measures_header").html($(doc).find("#measures_header").html());
			
			$("#section_measure_description #measures_body").html($(doc).find("#measures_body").html());
			
			updateMenu(undefined, "#section_measure_description", "#menu_measure_description", undefined);
			
			$("#languageselect").change(function(e) {
				showMeasures($("#section_measure_description #measures_header #idStandard").val(), $(e.target).val());
			});
			
			$("#section_measure_description").modal("show");
			
		},
		error : unknowError
	});
	return false;
}

function newMeasure(idStandard) {
	if (findSelectItemIdBySection("section_measure_description").length)
		return false;
	if (idStandard == null || idStandard == undefined)
		idStandard = $("#section_measure_description #measures_header #idStandard").val();
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
		success : function(response) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			if ($(doc).find("#measurelanguageselect").length) {
				var language = $("#measures_body #languageselect").val();
				$("#addMeasureModel #measurelanguages").html(response);
				$("#addMeasureModel #measurelanguageselect").change(function() {
					var language = parseInt($(this).find("option:selected").attr("value"));
					$("#addMeasureModel #measurelanguages div[trick-id][trick-id!='" + language + "']").css("display","none");
					$("#addMeasureModel #measurelanguages div[trick-id][trick-id='" + language + "']").css("display","block");
				});
				$("#addMeasureModel #measurelanguageselect option[value='" + language + "']").prop("selected", true);
				$("#addMeasureModel #measurelanguageselect").change();
				
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});

	$("#addMeasureModel").modal("show");
	
	return false;
}

function editSingleMeasure(measureId, idStandard) {
	
	if (idStandard == null || idStandard == undefined)
		idStandard = $("#section_measure_description #measures_header #idStandard").val();
	
	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();
	
	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_measure_description");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
	}
	var measure = $("#section_measure_description #measures_body tr[trick-id='" + measureId + "'] td:not(:first-child)");
	
	$("#addMeasureModel #measure_id").prop("value", measureId);
	$("#addMeasureModel #measure_reference").prop("value", $(measure[1]).text());
	$("#addMeasureModel #measure_level").prop("value", $(measure[0]).text());
	$("#addMeasureModel #measure_computable").prop("checked", $(measure[4]).attr("trick-computable") == "true");
	
	$("#addMeasureModel #measure_form").prop("action", context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/Save");
	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.measure.update", "Update Measure"));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.edit", "Update"));
	
	$.ajax({
		url : context + "/KnowledgeBase/Standard/" + idStandard + "/Measures/" + measureId + "/Edit",
		type : "post",
		contentType : "application/json",
		success : function(response) {
			var doc = new DOMParser().parseFromString(response, "text/html");
			if ($(doc).find("#measurelanguageselect").length) {
				var language = $("#measures_body #languageselect").val();
				$("#addMeasureModel #measurelanguages").html(response);
				$("#addMeasureModel #measurelanguageselect").change(function() {
					var language = parseInt($(this).find("option:selected").attr("value"));
					$("#addMeasureModel #measurelanguages div[trick-id][trick-id!='" + language + "']").css("display","none");
					$("#addMeasureModel #measurelanguages div[trick-id][trick-id='" + language + "']").css("display","block");
				});
				$("#addMeasureModel #measurelanguageselect option[value='" + language + "']").prop("selected", true);
				$("#addMeasureModel #measurelanguageselect").change();
			} else
				unknowError();
			return false;
		},
		error : unknowError
	});
	
	$("#addMeasureModel").modal("show");
	
	return false;
}

function saveMeasure() {
	var form = $("#addMeasureModel #measure_form");
	$.ajax({
		url : form.prop("action"),
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var alert = $("#addMeasureModel").find(".label-danger");
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
			if (!$("#addMeasureModel").find(".label-danger").length) {
				var language = $("#measures_body #languageselect").val();
				var idStandard = $("#section_measure_description #measures_header #idStandard").val();
				return showMeasures(idStandard, language);
			}
			return false;

		},
		error : unknowError
	});
	
	$("#addMeasureModel").modal("hide");
	
	return false;
}

function deleteMeasure(measureId, reference, standard) {
	
	
	
	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();
	
	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_measure_description");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
	}

	idStandard = $("#section_measure_description #measures_header #idStandard").val();
	
	if (standard == null || standard == undefined)
		standard = $("#section_measure_description #measures_header #standardLabel").val();
	
	var measure = $("#section_measure_description #measures_body tr[trick-id='" + measureId + "'] td:not(:first-child)");
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
			success : function(response) {
				if (response.success) {
					var language = $("#measures_body #languageselect").val();
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