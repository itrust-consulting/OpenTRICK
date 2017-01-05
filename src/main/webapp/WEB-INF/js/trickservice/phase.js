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

		if (!(l_lang=="en" || l_lang=="en-GB" || l_lang == "en-US"))
			$.getScript(context + "/js/bootstrap/locales/bootstrap-datepicker." + l_lang + ".js");
		$("#addPhaseModel").on("hidden.bs.modal", function() {
			$("#addPhaseModel .label").remove();
			clearPhaseInterval();
		});
	}
});

function clearPhaseInterval() {
	$("#addPhaseModel .modal-footer .btn-danger").hide();
	$("#addPhaseModel .modal-footer .btn-danger").unbind();
	$("#addPhaseModel #phase-Modal-title-info").text("");
	if (application["phase_interval"] != undefined)
		clearInterval(application["phase_interval"]);
	application["phase_interval"] = undefined;
}

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
			startDate : $("#addPhaseﬁModel #phase_begin_date").prop("value")
		}).on('changeDate', beginDateChanged);

		$("#addPhaseModel #phase_end_date").datepicker({
			format : "yyyy-mm-dd",
			language : l_lang,
			autoclose : true,
			weekStart : 1,
			todayHighlight : true,
			startDate : $("#addPhaseModel #phase_begin_date").prop("value")
		}).on('changeDate', endDateChanged);
		$("#addPhaseModel #phaseNewModal-title").html(MessageResolver("label.title.phase.add", "Add new phase"));
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

		var currentrow = $("#section_phase tr[data-trick-id='" + phaseid + "']");

		var previousphaserow = null;
		var nextphaserow = null;

		var phasestartlimit = null;
		var phaseendlimit = null;

		previousphaserow = $("#section_phase tr[data-trick-id='" + phaseid + "']").prev();

		if (previousphaserow.length)
			phasestartlimit = $(previousphaserow).find("td:last").text();

		nextphaserow = $("#section_phase tr[data-trick-id='" + phaseid + "']").next();

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
			/* startDate : phasestartlimit != null ? phasestartlimit : '', */
			endDate : phaseendlimit != null ? phaseendlimit : ''
		}).on('changeDate', beginDateChanged);

		$("#addPhaseModel #phase_end_date").datepicker({
			format : "yyyy-mm-dd",
			language : l_lang,
			autoclose : true,
			weekStart : 1,
			todayHighlight : true,
			/* startDate : $("#addPhaseModel #phase_begin_date").prop("value"), */
			endDate : phaseendlimit != null ? phaseendlimit : ''
		}).on('changeDate', endDateChanged);

		$("#addPhaseModel #phaseid").prop("value", phaseid);

		$("#addPhaseModel #phaseNewModal-title").html(MessageResolver("label.title.phase.edit", "Edit phase") + " #" + $(currentrow).find("td:eq(1)").text());

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

	/*
	 * if (dt2 < dt1) $("#addPhaseModel #phase_end_date").datepicker('setDate',
	 * dt1);
	 */
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
	clearPhaseInterval();
	$.ajax({
		url : context + "/Analysis/Phase/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#addPhaseModel .label").remove();
			var data = parseJson(response);
			if (data === undefined) {
				unknowError();
				$("#addPhaseModel").modal("hide");
			} else {
				for ( var error in data) {
					console.log(error);
					switch (error) {
					case "endDate":
					case "beginDate":
						if ($("#addPhaseModel #phaseid").val() != -1)
							$("<label class='label label-warning' style='margin-left:10px;'>" + response[error] + "</label>").appendTo("#addPhaseModel .modal-body");
						break;
					default:
						$("<label class='label label-danger' style='margin-left:10px;'>" + response[error] + "</label>").appendTo("#addPhaseModel .modal-body");
					}
				}

				if (!$("#addPhaseModel .label-danger").length) {
					reloadSection("section_phase");
					if (!$("#addPhaseModel .label-warning").length)
						$("#addPhaseModel").modal("hide");
					else {
						var phaseInterval = {
							max : 11,
							current : 0,
							iteration : 1000,
							text : $("#addPhaseModel #phase-Modal-title-info").attr("data-lang-text")
						}
						application["phase_interval"] = setInterval(function(e) {
							phaseInterval["worker"]();
						}, phaseInterval.iteration);

						phaseInterval["worker"] = function() {
							phaseInterval.current++;
							if (phaseInterval.current > phaseInterval.max)
								$("#addPhaseModel").modal("hide");
							else
								$("#addPhaseModel #phase-Modal-title-info").text(phaseInterval.text.replace("%d", phaseInterval.max - phaseInterval.current));
						}
						$("#addPhaseModel .modal-footer .btn-danger").click(clearPhaseInterval);
						$("#addPhaseModel .modal-footer .btn-danger").show();
					}
				}
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
	
	$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.phase", "Are you sure, you want to delete this phase"));
	$("#confirm-dialog .btn-danger").click(function() {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Phase/Delete/" + idPhase,
			contentType : "application/json;charset=UTF-8",
			type : 'POST',
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					setTimeout(function() {
						reloadSection("section_phase");
					}, 200);
				} else if (response["error"] != undefined) {
					setTimeout(function() {
						$("#alert-dialog .modal-body").html(response["error"]);
						$("#alert-dialog").modal("toggle");
					}, 200);
				} else
					unknowError();
				return false;
			},
			error : unknowError
		}).complete(function(){
			$progress.hide();
		});
	});
	$("#confirm-dialog").modal("show");
	return false;
}

// phase

function extractPhase(that,defaultPhase) {
	if(that.choose.length)
		return false;
	var $phases = $("#section_phase *[data-trick-class='Phase']>*:nth-child(2)");
	if(defaultPhase)
		that.choose.push("0");
	for (var i = 0; i < $phases.length; i++)
		that.choose.push($($phases[i]).text());
	return false;
}