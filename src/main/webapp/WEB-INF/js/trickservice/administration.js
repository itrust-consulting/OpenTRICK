var previous;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	$("#tab-container table").stickyTableHeaders({
		cssTopOffset : ".nav-tab",
		fixedOffset : 6
	});
});

function installTrickService() {

	$.ajax({
		url : context + "/Install",
		type : "GET",
		async : true,
		contentType : "application/json",
		success : function(response) {
			if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			} else if (response["idTask"] != undefined) 
				application['taskManager'].Start();
		},
		error : unknowError
	});

	return false;
}

function manageAnalysisAccess(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
		var profile = $("#" + section_analysis + " [data-trick-id='" + analysisId + "']");
		if (profile.length && $(profile).attr("data-trick-isprofile") == "true")
			return false;
	}

	$.ajax({
		url : context + "/Admin/Analysis/" + analysisId + "/ManageAccess",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {

			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("* div#manageAnalysisAccessModel");
			$("div#manageAnalysisAccessModel").replaceWith(newSection);
			$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
			$("#manageAnalysisAccessModel").modal('toggle');
			$("#userselect").one('focus', function() {
				previous = this.value;
			}).change(function() {

				$("#user_" + previous).attr("hidden", true);

				$("#user_" + this.value).removeAttr("hidden");

				previous = this.value;
			});
		},
		error : unknowError
	});
	return false;
}

function updatemanageAnalysisAccess(analysisId, userrightsform) {
	$.ajax({
		url : context + "/Admin/Analysis/" + analysisId + "/ManageAccess/Update",
		type : "post",
		data : serializeForm(userrightsform),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("* div.modal-content");
			$("div#manageAnalysisAccessModel div.modal-content").html(newSection);
			$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
			$("#userselect").one('focus', function() {
				previous = this.value;
			}).change(function() {

				$("#user_" + previous).attr("hidden", true);

				$("#user_" + this.value).removeAttr("hidden");

				previous = this.value;
			});
		},
		error : unknowError
	});
}

function findTrickisProfile(element) {
	if (element != undefined && element != null && element.length > 0 && element.length < 2)
		if ($(element).attr("data-trick-is-profile") != undefined)
			return $(element).attr("data-trick-is-profile");
		else if ($(element).parent().prop("tagName") != "BODY")
			return findTrickisProfile($(element).parent());
		else
			return null;
	else
		return null;
}

function isProfile(section) {
	return findTrickisProfile($(section + " tbody :checked")) != "true";
}

function adminCustomerChange(selector) {
	var customer = $(selector).find("option:selected").val();
	$.ajax({
		url : context + "/Admin/Analysis/DisplayByCustomer/" + customer,
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("*[id ='section_admin_analysis']");
			$("#section_admin_analysis").replaceWith(newSection);
			$("#section_admin_analysis table").stickyTableHeaders({
				cssTopOffset : ".nav-tab",
				fixedOffset : 6
			});

		},
		error : unknowError
	});
	return false;
}

function deleteAdminAnalysis(analysisId, section_analysis) {
	var selectedAnalysis = [];
	if (analysisId == null || analysisId == undefined) {
		selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (!selectedAnalysis.length)
			return false;
	} else if (!Array.isArray(analysisId))
		selectedAnalysis[selectedAnalysis.length] = analysisId;
	else
		selectedAnalysis = analysisId;

	var modal = new Modal($("#deleteAnalysisModel").clone()).setBody(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));
	$(modal.modal).find("#deleteanalysisbuttonNo").click(function() {
		modal.Destroy();
	});
	$(modal.modal).find("#deleteanalysisbuttonYes").click(function() {
		$(modal.modal).find("#deleteprogressbar").show();
		$(modal.modal).find(".btn").prop("disabled", true);
		$.ajax({
			url : context + "/Admin/Analysis/Delete",
			type : "post",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(selectedAnalysis),
			success : function(response) {
				if (response === true)
					$("#section_admin_analysis select").change();
				else if (response === false) {
					var error = new Modal($("#alert-dialog").clone())
					if (selectedAnalysis.length == 1)
						error.setBody(MessageResolver("failed.delete.analysis", "Analysis cannot be deleted!"));
					else
						error.setBody(MessageResolver("failed.delete.analyses", "Analyses cannot be deleted!"));
					error.Show();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
		modal.Destroy();
		return false;
	});
	$(modal.modal).find("#deleteanalysisbuttonYes").prop("disabled", false);
	modal.Show();
	return false;
}