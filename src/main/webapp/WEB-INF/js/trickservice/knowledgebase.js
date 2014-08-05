$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
});

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
				$("#addAnalysisModel-title").text(MessageResolver("title.analysis.update", "Update an Analysis"));
				$("#addAnalysisButton").text(MessageResolver("label.action.edit", "Edit"));
				$("#analysis_form").prop("action", "/update");
				$("#addAnalysisModel").modal('toggle');
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