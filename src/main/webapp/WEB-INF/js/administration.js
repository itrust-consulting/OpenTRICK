function manageAnalysisAccess(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
		var profile = $("#" + section_analysis + " [trick-id='" + analysisId + "']");
		if (profile.length && $(profile).attr("trick-isprofile") == "true")
			return false;
	}

	$.ajax({
		url : context + "/Admin/Analysis/" + analysisId + "/ManageAccess",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#manageAnalysisAccessModelBody").html(response);
			$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
			$("#manageAnalysisAccessModel").modal('toggle');
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
	});
	return false;
}

function generateDefaultRiskInformation(section) {
	var idAnalysis = findSelectItemIdBySection(section);
	if (idAnalysis.length != 1)
		return false;
	$.ajax({
		url : context + "/RiskInformation/Generate/Default/From/Analysis/" + idAnalysis[0],
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response["success"] != undefined)
				$("#alert-dialog .modal-body").html(response["success"]);
			else if (response["error"] != undefined)
				$("#alert-dialog .modal-body").html(response["error"]);
			else
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			$("#alert-dialog").modal("toggle");
			return true;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			$("#alert-dialog").modal("toggle");
		}
	});
	return false;
}

function generateDefaultParameter(section) {
	var idAnalysis = findSelectItemIdBySection(section);
	if (idAnalysis.length != 1)
		return false;
	$.ajax({
		url : context + "/Parameter/Generate/Default/From/Analysis/" + idAnalysis[0],
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response["success"] != undefined)
				$("#alert-dialog .modal-body").html(response["success"]);
			else if (response["error"] != undefined)
				$("#alert-dialog .modal-body").html(response["error"]);
			else
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			$("#alert-dialog").modal("toggle");
			return true;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			$("#alert-dialog").modal("toggle");
		}
	});
}

function updatemanageAnalysisAccess(analysisid, userrightsform) {
	$.ajax({
		url : context + "/Admin/Analysis/" + analysisid + "/ManageAccess/Update",
		type : "post",
		data : serializeForm(userrightsform),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#manageAnalysisAccessModelBody").html(response);
			$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisid + ",'userrightsform')");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
	});
}

function findTrickisProfile(element) {
	if (element != undefined && element != null && element.length > 0 && element.length < 2)
		if ($(element).attr("trick-isProfile") != undefined)
			return $(element).attr("trick-isProfile");
		else if ($(element).parent().prop("tagName") != "BODY")
			return findTrickisProfile($(element).parent());
		else
			return null;
	else
		return null;
}

function disableifprofile(section, menu) {
	var element = $(menu + " li[class='profilemenu']");
	element.addClass("disabled");
	var isProfile = findTrickisProfile($(section + " tbody :checked"));
	if (isProfile != undefined && isProfile != null) {
		if (isProfile == "true")
			element.addClass("disabled");
		else
			element.removeClass("disabled");
	}
}

function adminCustomerChange(selector) {
	var customer = $(selector).find("option:selected").val();
	$.ajax({
		url : context + "/Admin/Analysis/DisplayByCustomer/" + customer,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("*[id ='section_admin_analysis']");
			$("#section_admin_analysis").replaceWith(newSection);
			analysisTableSortable();
		}
	});
	return false;
}

function analysisTableSortable() {

	// check if datatable has to be initialised
	var tables = $("#section_admin_analysis table");

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