function displayActionPlanOptions(analysisId) {

	$.ajax({
		url : context + "/ActionPlan/" + analysisId + "/ComputeOptions",
		type : "GET",
		async : true,
		contentType : "application/json",
		success : function(response) {

			dialog = new Modal();

			var button_footer_compute = document.createElement("button");
			var button_footer_cancel = document.createElement("button");
			button_footer_compute.setAttribute("onclick", "return calculateActionPlanWithOptions(" + analysisId + " ,'#modalBox')");
			button_footer_compute.setAttribute("class", "btn btn-default");
			button_footer_compute.setAttribute("data-dismiss", "modal");
			button_footer_cancel.setAttribute("class", "btn btn-default");
			button_footer_cancel.setAttribute("data-dismiss", "modal");
			$(button_footer_compute).html(MessageResolver("label.actionplan.compute", "Compute"));
			$(button_footer_cancel).html("Cancel");

			dialog.modal_footer_buttons = [ button_footer_compute, button_footer_cancel ];

			dialog.Intialise();

			dialog.setTitle(MessageResolver("title.actionplan.compute.options", "Compute Action Plan: Options"));

			dialog.setBody(response);

			dialog.Show();

		},
		error : unknowError
	});
	return false;
}

function removeModal(modalpopup) {
	$("*[class='modal-backdrop fade in']*").remove();

	$(modalpopup).remove();
}

function calculateActionPlanWithOptions(analysisId, modalBox) {

	var form = $(modalBox + " #actionplancomputationoptionsform");

	var data = {};

	data["id"] = analysisId;

	var uncertainty = form.find(" input[name='uncertainty']").is(":checked");

	data["uncertainty"] = uncertainty;

	form.find("input[name^='norm_']").each(function() {

		var name = $(this).attr("name");

		var value = $(this).is(":checked");

		data[name] = value;

	});

	var jsonarray = JSON.stringify(data);

	// removeModal(modalBox);

	$.ajax({
		url : context + "/ActionPlan/Compute",
		type : "post",
		data : jsonarray,
		async : true,
		contentType : "application/json",
		success : function(response) {
			if (response["success"] != undefined) {
				var taskManager = new TaskManager(MessageResolver("title.actionplan.compute", "Compute Action Plan"));
				taskManager.Start();
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
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
	}

	return false;

}

function toggleDisplayActionPlanAssets(sectionactionplan, menu) {
	var actionplantype = $(sectionactionplan).find(".disabled[trick-nav-control]").attr("trick-nav-control");
	var table = $("#actionplantable_" + actionplantype);
	table.floatThead('destroy');
	$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
	if ($("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
		fixedTableHeader(table);
	} else {
		$(menu + " a").html("<span class='glyphicon glyphicon-chevron-up'></span>&nbsp;" + MessageResolver("action.actionplanassets.hide", "Hide Assets"));
	}
	return false;
}

function reloadActionPlanEntryRow(idActionPlanEntry, type, idMeasure, norm) {
	$.ajax({
		url : context + "/ActionPlan/RetrieveSingleEntry/" + idActionPlanEntry,
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
	reloadMeasureRow(idMeasure, norm);
	return false;
}