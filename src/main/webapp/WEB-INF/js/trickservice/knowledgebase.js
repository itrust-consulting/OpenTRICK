$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	analysisTableSortable();
});

function editSingleAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	$("#editAnalysisModel .progress").hide();
	$("#editAnalysisModel #editAnalysisButton").prop("disabled", false);
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
				$("#editAnalysisModel").modal('toggle');
			}
		},
		error : unknowError
	});
	return false;
}

function setAsDefaultProfile(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_profile_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	$.ajax({
		url : context + "/Analysis/SetDefaultProfile/" + analysisId,
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			reloadSection("section_profile_analysis");
		},
		error : unknowError
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

	var element = document.createElement("a");
	element.setAttribute("href", context + "/Analysis/" + analysisId + "/Select");
	$(element).appendTo("body");
	element.click();

	return false;
}

function saveAnalysis(form, reloadaction) {
	$("#editAnalysisModel .progress").show();
	$("#editAnalysisModel #editAnalysisButton").prop("disabled", true);
	$.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#editAnalysisModel .progress").hide();
			$("#editAnalysisModel #editAnalysisButton").prop("disabled", false);
			var alert = $("#editAnalysisModel .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				
				case "analysislanguage":
					$(errorElement).appendTo($("#analysislanguagecontainer"));
					break;

				case "comment":
					$(errorElement).appendTo($("#analysis_label").parent());

					break;

				case "analysis":
					$(errorElement).appendTo($("#editAnalysisModel .modal-body"));
					break;
				}
			}
			if (!$("#editAnalysisModel .label-danger").length) {
				$("#editAnalysisModel").modal("hide");
				reloadSection("section_profile_analysis");
			}
			return false;
		},
		error : unknowError
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
	$("#deleteAnalysisBody").html(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));

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
					reloadSection("section_profile_analysis");
				} else if (response.error != undefined) {
					$("#alert-dialog .modal-body").html(response.error);
					$("#alert-dialog").modal("toggle");
				}
				return false;
			},error : unknowError
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
	
	// define sort order of text
	Array.AlphanumericSortOrder = 'AaÁáBbCcDdÐðEeÉéĘęFfGgHhIiÍíJjKkLlMmNnOoÓóPpQqRrSsTtUuÚúVvWwXxYyÝýZzÞþÆæÖö';

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
		'width' : '25%'
	});

	return false;
}