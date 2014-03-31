function editSingleAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	$("#addAnalysisModel .progress").hide();
	$("#addAnalysisModel #addAnalysisButton").prop("disabled", false);
	$.ajax({
		url : context + "/Analysis/Edit/" + analysisId,
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((form = doc.getElementById("form_edit_analysis")) == null) {
				$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
				$("#alert-dialog").modal("toggle");
			} else {
				$("#analysis_form").html($(form).html());
				$("#addAnalysisModel-title").text(MessageResolver("title.analysis.Update", "Update an Analysis"));
				$("#addAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
				$("#analysis_form").prop("action", "/update");
				$("#addAnalysisModel").modal('toggle');
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
	});
	return false;
}

function selectAnalysis(analysisId) {

	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	window.location.replace(context + "/Analysis/" + analysisId + "/Select");
}

function saveAnalysis(form, reloadaction) {
	$("#addAnalysisModel .progress").show();
	$("#addAnalysisModel #addAnalysisButton").prop("disabled", true);
	$.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#addAnalysisModel .progress").hide();
			$("#addAnalysisModel #addAnalysisButton").prop("disabled", false);
			var alert = $("#addAnalysisModel .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "analysiscustomer":
					$(errorElement).appendTo($("#analysiscustomercontainer"));
					break;

				case "analysislanguage":
					$(errorElement).appendTo($("#analysislanguagecontainer"));
					break;

				case "comment":
					$(errorElement).appendTo($("#analysis_label").parent());

					break;

				case "profile":
					$(errorElement).appendTo($("#analysis_form select[name='profile']").parent());
					break;

				case "author":
					$(errorElement).appendTo($("#analysis_form input[name='author']").parent());
					break;

				case "version":
					$(errorElement).appendTo($("#analysis_version").parent());
					break;

				case "analysis":
					$(errorElement).appendTo($("#addAnalysisModel .modal-body"));
					break;
				}
			}
			if (!$("#addAnalysisModel .label-danger").length) {
				$("#addAnalysisModel").modal("hide");
				reloadSection("section_profile_analysis");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
	});
	return false;
}

function deleteAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_profile_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	$("#deleteAnalysisBody").html(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis") + "?");

	$("#deleteanalysisbuttonYes").click(function() {
		$("#deleteAnalysisModel .modal-header > .close").hide();
		$("#deleteprogressbar").show();
		$("#deleteanalysisbuttonYes").prop("disabled", true);
		$.ajax({
			url : context + "/Analysis/Delete/" + analysisId,
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				$("#deleteprogressbar").hide();
				$("#deleteanalysisbuttonYes").prop("disabled", false);
				$("#deleteAnalysisModel").modal('toggle');
				if (response.success != undefined) {
					reloadSection("section_analysis");
				} else if (response.error != undefined) {
					$("#alert-dialog .modal-body").html(response.error);
					$("#alert-dialog").modal("toggle");
				}
				return false;
			}
		});
		$("#deleteanalysisbuttonYes").unbind();
		return false;
	});
	$("#deleteanalysisbuttonYes").prop("disabled", false);
	$("#deleteAnalysisModel .modal-header > .close").show();
	$("#deleteAnalysisModel").modal('show');
	return false;
}

function analysisTableSortable() {

	// check if datatable has to be initialised
	var tables = $("#section_profile_analysis table");
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
				filter : false,
			},
			1 : {
				sorter : "text",
				filter : false,
			},
			2 : {
				sorter : "text",
				filter : false,
			},
			3 : {
				sorter : "text",
				filter : false,
			},
			4 : {
				sorter : "text",
				filter : false,
			},
			5 : {
				sorter : "text",
				filter : false,
			},
		},
		textSorter : {
			1 : Array.AlphanumericSort,
			2 : function(a, b, direction, column, table) {
				if (table.config.sortLocaleCompare)
					return a.localeCompare(b);
				return versionComparator(a, b, direction);
			},
			3 : $.tablesorter.sortNatural,
		},
		theme : "bootstrap",
		dateFormat : "yyyymmdd",
		widthFixed : false,
		headerTemplate : '{content} {icon}',
		widgets : [ "uitheme", "filter", "zebra" ],
		widgetOptions : {
			zebra : [ "even", "odd" ],
			filter_reset : ".reset"
		}
	});
	$("th[class~='tablesorter-header'][data-column='0']").css({
		'width' : '2px'
	});
	$("th[class~='tablesorter-header'][data-column='1']").css({
		'width' : '250px'
	});
	// $("th[class~='tablesorter-header'][data-column='2']").css({'width':'250px'});
	$("th[class~='tablesorter-header'][data-column='3']").css({
		'width' : '250px'
	});
	$("th[class~='tablesorter-header'][data-column='4']").css({
		'width' : '250px'
	});
	$("th[class~='tablesorter-header'][data-column='5']").css({
		'width' : '150px'
	});
	return false;
}