function displayActionPlanOptions(analysisId) {

	$.ajax({
		url : context + "/Analysis/ActionPlan/ComputeOptions",
		type : "GET",
		async : true,
		contentType : "application/json",
		success : function(response) {

			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var actionplancomputeoptions = $(doc).find("* div#actionplancomputeoptions");

			if ($("#actionplancomputeoptions").length)
				$("#actionplancomputeoptions").html($(actionplancomputeoptions).html());
			$("#actionplancomputeoptions").modal("toggle");

		},
		error : unknowError
	});
	return false;
}

function calculateActionPlanWithOptions(form) {

	var form = $("#" + form);

	var data = {};

	data["id"] = form.find("input[name='id']").val();

	form.find("input[name^='standard_']").each(function() {

		var name = $(this).attr("name");

		var value = $(this).is(":checked");

		data[name] = value;

	});

	var jsonarray = JSON.stringify(data);

	$.ajax({
		url : context + "/Analysis/ActionPlan/Compute",
		type : "post",
		data : jsonarray,
		async : true,
		contentType : "application/json",
		success : function(response) {
			if (response["success"] != undefined) {
				var language = $("#nav-container").attr("trick-language");
				new TaskManager(MessageResolver("title.actionplan.compute", "Compute Action Plan", null, language)).Start();
			} else if (message["error"]) {
				$("#alert-dialog .modal-body").html(message["error"]);
				$("#alert-dialog").modal("toggle");
			}
		},
		error : unknowError
	});

	return false;

}

function hideActionplanAssets(sectionactionplan, menu) {

	var actionplantype = $(sectionactionplan).find(".disabled[trick-nav-control]").attr("trick-nav-control");

	if (!$("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
		$(menu + " a#actionplanassetsmenulink").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
	}

	return false;

}

function toggleDisplayActionPlanAssets(sectionactionplan, menu) {
	var actionplantype = $(sectionactionplan).find(".disabled[trick-nav-control]").attr("trick-nav-control");
	var table = $("#actionplantable_" + actionplantype);
	$(table).stickyTableHeaders("destroy");
	$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
	if ($("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$(menu + " a#actionplanassetsmenulink").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
		$(table).stickyTableHeaders({
			cssTopOffset : ".nav-analysis",
			fixedOffset : 6
		});
	} else {
		$(menu + " a#actionplanassetsmenulink").html("<span class='glyphicon glyphicon-chevron-up'></span>&nbsp;" + MessageResolver("action.actionplanassets.hide", "Hide Assets"));
	}
	return false;
}

function reloadActionPlanEntryRow(idActionPlanEntry, type, idMeasure, standard) {
	$.ajax({
		url : context + "/Analyis/ActionPlan/RetrieveSingleEntry/" + idActionPlanEntry,
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (!response.length)
				return false;
			$("#section_actionplan_" + type + " tr[trick-id='" + idActionPlanEntry + "']").replaceWith(response);
			return false;
		},
		error : unknowError
	});
	reloadMeasureRow(idMeasure, standard);
	return false;
}