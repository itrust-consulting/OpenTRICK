function showMeasures(normId, languageId) {
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
			var header = $(doc).find("#measures_header");
			var body = $(doc).find("#measures_body");
			oldHeader = $("#showMeasuresModel-title");
			oldBody = $("#showmeasuresbody");
			$(oldHeader).html(header.html());
			$(oldBody).html(body.html());
			measureSortTable($("#showmeasuresbody")[0]);
			$("#languageselect").change(function() {
				var language = $(this).find("option:selected").attr("value");
				var normId = $("#normId").attr("value");
				showMeasures(normId, language);
			});

			$("#showMeasuresModel").modal("show");

		}
	});
	return false;
}

function saveMeasure(form) {
	$.ajax({
		url : $("#measure_form").prop("action"),
		type : "post",
		data : serializeForm(form),
		contentType : "application/json",
		success : function(response) {
			var $formParent = $("#" + form).parent();
			var alert = $formParent.find(".label-danger");
			if (alert.length)
				alert.remove();
			var languages = $("#" + form).find("#measurelanguageselect option");
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
					$(errorElement).appendTo($("#" + form).find(languageData).parent());
					continue;
				}
				switch (error) {
				case "measuredescription.reference":
					$(errorElement).appendTo($("#" + form).find("#measure_reference").parent());
					break;
				case "measuredescription.level":
					$(errorElement).appendTo($("#" + form).find("#measure_level").parent());
					break;
				case "measureDescription":
					$(errorElement).appendTo($("#" + form).parent());
					break;
				}
			}
			if (!$formParent.find(".label-danger").length) {
				$("#addMeasureModel").modal("hide");
				var language = $("#languageselect").find("option:selected").attr("value");
				var normId = $("#normId").attr("value");
				var measureId = $("#" + form).find("#measure_id").val();
				return refreshMeasure(normId, measureId, language);
			}
			return false;

		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		}
	});
	return false;
}

function refreshMeasure(normId, measureId, languageId) {
	if (normId == null || normId == undefined) {
		normId = $("#normId");
	}
	if (!languageId == null || languageId == undefined) {
		languageId = $("#languageselect option[selected='selected']").value();
	}

	if (measureId == -1) {
		$.ajax({
			url : context + "/KnowledgeBase/Norm/" + normId + "/language/" + languageId + "/Measures",
			type : "POST",
			contentType : "application/json",
			success : function(response) {

				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				var header = $(doc).find("#measures_header");
				var body = $(doc).find("#measures_body");
				oldHeader = $("#showMeasuresModel-title");
				oldBody = $("#showmeasuresbody");
				$(oldHeader).html(header.html());
				$(oldBody).html(body.html());
				measureSortTable($("#showmeasuresbody")[0]);
				$("#languageselect").change(function() {
					var language = $(this).find("option:selected").attr("value");
					var normId = $("#normId").attr("value");
					showMeasures(normId, language);
				});
			}
		});
	} else {
		$.ajax({
			url : context + "/KnowledgeBase/Norm/" + normId + "/language/" + languageId + "/Measures/" + measureId,
			type : "GET",
			contentType : "application/json",
			success : function(response) {

				oldBody = $("#showmeasuresbody").find("[trick-id='" + measureId + "']");
				$(oldBody).replaceWith(response);
				measureSortTable($("#showmeasuresbody")[0]);
			}
		});
	}
	return false;
}

function deleteMeasure(measureId, reference, norm) {
	if (measureId == null || measureId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_measure_description");
		if (selectedScenario.length != 1)
			return false;
		measureId = selectedScenario[0];
		norm = $("#normLabel").prop("value");
		reference = $("#section_measure_description tbody tr[trick-id='" + measureId + "'] td:nth-child(3)").text();
	}
	$("#deleteMeasureBody").html(
			MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: ") + "&nbsp;<strong>" + reference
					+ "</strong> from the norm <strong>" + norm + " </strong>?");
	var normId = $("#normId").attr("value");
	$("#deletemeasurebuttonYes").click(function() {
		$.ajax({
			url : context + "/KnowledgeBase/Norm/" + normId + "/Measures/Delete/" + measureId,
			type : "POST",
			contentType : "application/json",
			success : function(response) {
				var language = $("#languageselect").find("option:selected").attr("value");
				var normId = $("#normId").attr("value");
				showMeasures(normId, language);
				return false;
			}
		});
		$("#deleteMeasureModel").modal('toggle');
		return false;
	});
	$("#deleteMeasureModel").attr("style", "z-index:1060");
	$("#deleteMeasureModel").modal('toggle');
	return false;
}

function newMeasure(normId) {
	if (normId == null || normId == undefined)
		normId = $("#normId").prop("value");
	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();
	$("#measure_id").prop("value", "-1");

	$("#measure_reference").prop("value", "");

	$("#measure_level").prop("value", "");

	$("#measure_computable").prop("checked", "false");

	$.ajax({
		url : context + "/KnowledgeBase/Norm/" + normId + "/Measures/Add",
		type : "get",
		async : true,
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
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});

	$("#addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Add", "Add a new Measure"));
	$("#addmeasurebutton").text(MessageResolver("label.action.add", "Add"));

	$("#addMeasureModel").modal('toggle');
	$("#addMeasureModel").children(":first").attr("style", "z-index:1060");
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
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});

	$("#addMeasureModel-title").text(MessageResolver("title.knowledgebase.Measure.Update", "Update new Measure"));
	$("#addmeasurebutton").text(MessageResolver("label.action.edit", "Edit"));

	$("#addMeasureModel").modal('toggle');
	$("#addMeasureModel").children(":first").attr("style", "z-index:1060");
	return false;
}