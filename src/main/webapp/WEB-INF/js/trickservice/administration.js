var previous;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	analysisTableSortable();
});

function installTrickService() {

	$.ajax({
		url : context + "/Install",
		type : "GET",
		async : true,
		contentType : "application/json",
		success : function(response) {

			if (response["error"]!=undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			} else if (response["success"]!=undefined) {
				$("#success-dialog .modal-body").html(response["success"]);
				$("#success-dialog").modal("toggle");
				location.reload(true);
			}
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
		if ($(element).attr("data-trick-isProfile") != undefined)
			return $(element).attr("data-trick-isProfile");
		else if ($(element).parent().prop("tagName") != "BODY")
			return findTrickisProfile($(element).parent());
		else
			return null;
	else
		return null;
}

function isProfile(section){
	return findTrickisProfile($(section + " tbody :checked"))!="true";
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
			analysisTableSortable();
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
	$(modal.modal).find("#deleteanalysisbuttonNo").click(function() {modal.Destroy();});
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

function analysisTableSortable() {

	// check if datatable has to be initialised
	var tables = $("#section_admin_analysis table");

	if (!tables.length)
		return false;

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
				width : "0.5%"
			},
			1 : {
				sorter : "text",
				filter : false,
				width : "10%",
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
			6 : {
				sorter : "text",
				filter : false,
			},
			7 : {
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
	// $("th[class~='tablesorter-header'][data-column='2']").css({'width':'2px'});
	$("th[class~='tablesorter-header'][data-column='3']").css({
		'width' : '250px'
	});
	$("th[class~='tablesorter-header'][data-column='4']").css({
		'width' : '150px'
	});
	$("th[class~='tablesorter-header'][data-column='5']").css({
		'width' : '250px'
	});
	$("th[class~='tablesorter-header'][data-column='6']").css({
		'width' : '150px'
	});
	$("th[class~='tablesorter-header'][data-column='7']").css({
		'width' : '50px'
	});
	return false;
}