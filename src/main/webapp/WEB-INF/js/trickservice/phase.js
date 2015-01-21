var l_lang = null;

var previousbegindate = null;
var previousenddate = null;

var previousphaserow = null;
var nextphaserow = null;

$(function() {
	if ($("#addPhaseModel").length) {

		if (navigator.userLanguage) // Explorer
			l_lang = navigator.userLanguage;
		else if (navigator.language) // FF
			l_lang = navigator.language;
		else
			l_lang = "en";

		if (l_lang == "en-US") {
			l_lang = "en";
		}

		if (l_lang != "en")
			$.getScript(context + "/js/bootstrap/locales/bootstrap-datepicker." + l_lang + ".js");
	}
});

function newPhase() {

	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {

		var selectedScenario = $("#section_phase :checked");
		if (selectedScenario.length != 0)
			return false;

		$('#addPhaseModel #datepicker_container').html($('#datepicker_prototype').html());

		$('#addPhaseModel #datepicker_container input[name="beginDate"]').attr("id", "phase_begin_date");

		$('#addPhaseModel #datepicker_container input[name="endDate"]').attr("id", "phase_end_date");

		var previewError = $("#addPhaseModel .alert");
		if (previewError.length)
			previewError.remove();

		var lastDate = $("#section_phase td").last();
		if (lastDate.length) {
			var beginDate = lastDate.text();
			if (beginDate.match("\\d{4}-\\d{2}-\\d{2}")) {
				var endDateSplitted = beginDate.split("-");
				endDateSplitted[0]++;
				var endDate = endDateSplitted[0] + "-" + endDateSplitted[1] + "-" + endDateSplitted[2];
				$("#addPhaseModel #phase_begin_date").prop("value", beginDate);

				previousbegindate = beginDate;
				$("#addPhaseModel #phase_end_date").prop("value", endDate);

				previousenddate = endDate;
			}
		}

		$("#addPhaseModel #phase_begin_date").datepicker({
			format : "yyyy-mm-dd",
			language : l_lang,
			autoclose : true,
			weekStart : 1,
			todayHighlight : true,
			startDate : $("#addPhaseﬁModel #phase_begin_date").prop("value"),
		}).on('changeDate', beginDateChanged);

		$("#addPhaseModel #phase_end_date").datepicker({
			format : "yyyy-mm-dd",
			language : l_lang,
			autoclose : true,
			weekStart : 1,
			todayHighlight : true,
			startDate : $("#addPhaseModel #phase_begin_date").prop("value"),
		}).on('changeDate', endDateChanged);
		var lang = $("#nav-container").attr("trick-language");
		$("#addPhaseModel #phaseNewModal-title").html(MessageResolver("label.title.phase.add", "Add new phase", null, lang));
		$("#addPhaseModel #phaseid").prop("value", -1);
		$('#addPhaseModel').modal('show');

	}
	return false;

}

function editPhase(phaseid) {

	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$('#addPhaseModel #datepicker_container').html($('#datepicker_prototype').html());

		$('#addPhaseModel #datepicker_container input[name="beginDate"]').attr("id", "phase_begin_date");

		$('#addPhaseModel #datepicker_container input[name="endDate"]').attr("id", "phase_end_date");

		if (phaseid == null || phaseid == undefined) {
			var selectedPhase = $("#section_phase :checked");
			if (selectedPhase.length != 1)
				return false;
			phaseid = findTrickID(selectedPhase[0]);
		}

		var previewError = $("#addPhaseModel .alert");
		if (previewError.length)
			previewError.remove();

		var currentrow = $("#section_phase tr[trick-id='" + phaseid + "']");

		var previousphaserow = null;
		var nextphaserow = null;

		var phasestartlimit = null;
		var phaseendlimit = null;

		previousphaserow = $("#section_phase tr[trick-id='" + phaseid + "']").prev();

		if (previousphaserow.length)
			phasestartlimit = $(previousphaserow).find("td:last").text();

		nextphaserow = $("#section_phase tr[trick-id='" + phaseid + "']").next();

		if (nextphaserow.length)
			phaseendlimit = $(nextphaserow).find("td:eq(2)").text();

		if (phasestartlimit == null || phasestartlimit.match("\\d{4}-\\d{2}-\\d{2}")) {

			$("#addPhaseModel #phase_begin_date").prop("value", $(currentrow).find("td:eq(2)").text());
			previousbegindate = $(currentrow).find("td:eq(2)").text();
			$("#addPhaseModel #phase_end_date").prop("value", $(currentrow).find("td:eq(3)").text());
			previousenddate = $(currentrow).find("td:eq(3)").text();
		}

		$("#addPhaseModel #phase_begin_date").datepicker({
			format : "yyyy-mm-dd",
			language : l_lang,
			autoclose : true,
			weekStart : 1,
			todayHighlight : true,
			startDate : phasestartlimit != null ? phasestartlimit : '',
			endDate : phaseendlimit != null ? phaseendlimit : '',
		}).on('changeDate', beginDateChanged);

		$("#addPhaseModel #phase_end_date").datepicker({
			format : "yyyy-mm-dd",
			language : l_lang,
			autoclose : true,
			weekStart : 1,
			todayHighlight : true,
			startDate : $("#addPhaseModel #phase_begin_date").prop("value"),
			endDate : phaseendlimit != null ? phaseendlimit : '',
		}).on('changeDate', endDateChanged);

		$("#addPhaseModel #phaseid").prop("value", phaseid);

		var lang = $("#nav-container").attr("trick-language");
		$("#addPhaseModel #phaseNewModal-title").html(MessageResolver("label.title.phase.edit", "Edit phase", null, lang) + " #" + $(currentrow).find("td:eq(1)").text());

		$('#addPhaseModel').modal('show');
	}
	return false;

}

function beginDateChanged() {
	var dt1 = $("#addPhaseModel #phase_begin_date").datepicker('getDate');
	var dt2 = $("#addPhaseModel #phase_end_date").datepicker('getDate');

	if (dt1 == 'Invalid Date') {
		dt1 = new Date(previousbegindate);
		$("#addPhaseModel #phase_begin_date").datepicker('setDate', dt1);
	}

	previousbegindate = $("#addPhaseModel #phase_begin_date").prop("value");

	$("#addPhaseModel #phase_end_date").datepicker('setStartDate', dt1);

	if (dt2 < dt1)
		$("#addPhaseModel #phase_end_date").datepicker('setDate', dt1);
}

function endDateChanged() {
	var dt2 = $("#addPhaseModel #phase_end_date").datepicker('getDate');

	if (dt2 == 'Invalid Date') {
		dt2 = new Date(previousenddate);
		$("#addPhaseModel #phase_end_date").datepicker('setDate', dt2);
	}

	previousenddate = $("#addPhaseModel #phase_end_date").prop("value");

}

/**
 * 
 * @param form
 * @returns
 */
function savePhase(form) {
	$.ajax({
		url : context + "/Analysis/Phase/Save",
		type : "post",
		async : true,
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var previewError = $("#addPhaseModel .alert");
			if (previewError.length)
				previewError.remove();
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";

			result = data == "" ? true : false;
			if (result) {
				$("#addPhaseModel").modal("hide");
				reloadSection("section_phase");
			} else {
				showError(document.getElementById(form), data);
			}
		},
		error : unknowError
	});
	return false;
}

function deletePhase(idPhase) {

	if (idPhase == null || idPhase == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_phase"));
		if (selectedScenario.length != 1)
			return false;
		idPhase = selectedScenario[0];
	}
	var lang = $("#nav-container").attr("trick-language");
	$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.phase", "Are you sure, you want to delete this phase", null, lang));
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Analysis/Phase/Delete/" + idPhase,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response) {
				if (response["success"] != undefined) {
					setTimeout(function() {
						reloadSection("section_phase");
					}, 400);
				} else if (response["error"] != undefined) {
					setTimeout(function() {
						$("#alert-dialog .modal-body").html(response["error"]);
						$("#alert-dialog").modal("toggle");
					}, 400);
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	});
	$("#confirm-dialog").modal("toggle");
	return false;
}

// phase

function extractPhase(that) {
	var phases = $("#section_phase *[trick-class='Phase']>*:nth-child(2)");
	if (!$(phases).length)
		return true;
	for (var i = 0; i < phases.length; i++)
		that.choose.push($(phases[i]).text());
	return false;
}