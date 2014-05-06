$(function(){
if ($("#addPhaseModel").length) {
		
		var l_lang;
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
			$.getScript(context + "/js/locales/bootstrap-datepicker." + l_lang + ".js");
		$('#addPhaseModel').on('show.bs.modal', function() {
			var lastDate = $("#section_phase td").last();
			if (lastDate.length) {
				var beginDate = lastDate.text();
				if (beginDate.match("\\d{4}-\\d{2}-\\d{2}")) {
					var endDate = beginDate.split("-");
					endDate[0]++;
					$("#addPhaseModel #phase_begin_date").prop("value", beginDate);
					$("#addPhaseModel #phase_endDate").prop("value", endDate[0] + "-" + endDate[1] + "-" + endDate[2]);
				}
			}
			$("#addPhaseModel input").datepicker({
				format : "yyyy-mm-dd",
				language : l_lang
			});
		});
	}
});

/**
 * 
 * @param form
 * @returns
 */
function savePhase(form) {
	$.ajax({
		url : context + "/Phase/Save",
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
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addPhaseModel").modal("hide");
				reloadSection("section_phase");
			}
			return result;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return false;
		},
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
		$.ajax({
			url : context + "/Phase/Delete/" + idPhase,
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response) {
				if (response["success"] != undefined) {
					reloadSection("section_phase");
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				}
				return false;
			}
		});
	});
	$("#confirm-dialog").modal("toggle");
	return false;
}

//phase

function extractPhase(that) {
	var phases = $("#section_phase *[trick-class='Phase']>*:nth-child(2)");
	if (!$(phases).length)
		return true;
	that.choose.push("NA");
	for (var i = 0; i < phases.length; i++)
		that.choose.push($(phases[i]).text());
	return false;
}