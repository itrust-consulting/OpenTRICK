function addPhase() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var selectedScenario = $("#section_phase :checked");
		if (selectedScenario.length != 0)
			return false;
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Phase/Add",
			contentType : "application/json;charset=UTF-8",
			success : processPhaseForm,
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;

}

function processPhaseForm(response, textStatus, jqXHR){
	var $view = $("#phase-modal-form", new DOMParser().parseFromString(response, "text/html"));
	if ($view.length) {
		$view.appendTo("#widgets").modal("show").on('hidden.bs.modal', () => $view.remove());
		var $begin = $("input[name='begin']", $view), $end = $("input[name='end']", $view), $saveBtn = $("button[name='save']", $view), 
		$submitBtn = $("button[name='submit']", $view);
		if($begin.is(":disabled"))
			$end.attr("min", $begin.val());
		else {
			var checkDateValidity = (e) => {
				try {
					var begin = new Date($begin[0].value), end = new Date($end[0].value);
					$saveBtn.prop("disabled", begin.getTime()> end.getTime());
				} catch (e) {
					$saveBtn.prop("disabled", true);
				}
			};
			$begin.on("change", checkDateValidity);
			$end.on("change", checkDateValidity);
		}
		$saveBtn.on("click", (e) => $submitBtn.trigger("click"));
		$("form", $view).on("submit", savePhase);
	}else unknowError();
	
	
}

function editPhase(phaseid) {

	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {

	}
	return false;
}

/**
 * 
 * @param form
 * @returns
 */
function savePhase(e) {
	var $progress = $("#loading-indicator").show(), form = e.currentTarget, view = form.closest(".modal");
	$.ajax({
		url : context + "/Analysis/Phase/Save",
		type : "post",
		type: 'POST',
		data: new FormData(form),
		cache: false,
		contentType: false,
		processData: false,
		success : function(response, textStatus, jqXHR) {
			if(response.success){
				showDialog("success", response.success);
				$(view).modal("hide");
			}else if(response.error)
				showDialog("error", response.error);
			else 
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
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
	var $confirmDialog = showDialog("#confirm-dialog", MessageResolver("confirm.delete.phase", "Are you sure, you want to delete this phase"));
	$(".btn-danger", $confirmDialog).click(function() {
		$confirmDialog.modal("hide");
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Phase/Delete/" + idPhase,
			contentType : "application/json;charset=UTF-8",
			type : 'POST',
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					reloadSection("section_phase");
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
				return false;
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	});
	return false;
}

// phase

