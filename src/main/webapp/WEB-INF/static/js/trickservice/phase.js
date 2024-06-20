/**
 * Adds a phase to the analysis.
 * 
 * @returns {boolean} Returns false.
 */
function addPhase() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
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

/**
 * Processes the phase form response and performs necessary actions.
 *
 * @param {any} response - The response from the server.
 * @param {string} textStatus - The status of the request.
 * @param {XMLHttpRequest} jqXHR - The XMLHttpRequest object.
 * @returns {Object} - The current object.
 */
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
	return this;
}

/**
 * Edits a phase.
 * 
 * @param {number} phaseId - The ID of the phase to edit.
 * @returns {boolean} - Returns false if the selected phase is not found or if the user does not have the MODIFY right, otherwise returns true.
 */
function editPhase(phaseId) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if(phaseId===undefined || phaseId === null){
			var selectedPhase = findSelectItemIdBySection("section_phase");
			if (selectedPhase.length!=1)
				return false;
			phaseId = selectedPhase[0];
		}
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Phase/"+phaseId+"/Edit",
			contentType : "application/json;charset=UTF-8",
			success : processPhaseForm,
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}


/**
 * Saves the phase data using an AJAX request.
 * 
 * @param {Event} e - The event object.
 * @returns {boolean} Returns false to prevent the default form submission.
 */
function savePhase(e) {
	var $progress = $("#loading-indicator").show(), form = e.currentTarget, view = form.closest(".modal");
	$.ajax({
		url : context + "/Analysis/Phase/Save",
		type : "post",
		data: new FormData(form),
		cache: false,
		contentType: false,
		processData: false,
		success : function(response, textStatus, jqXHR) {
			if(response.success || response.warning){
				reloadSection("section_phase");
				if(response.success)
					showDialog("success", response.success);
				else showDialog("warning", response.warning);
				$(view).modal("hide");
			}else if(response.error || response.begin || response.end){
				for ( var field in response) 
					showDialog("error", response[field]);
			}
			else 
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

/**
 * Deletes a phase.
 * 
 * @param {number} idPhase - The ID of the phase to be deleted.
 * @returns {boolean} Returns false if the ID is null or undefined, otherwise returns true.
 */
function deletePhase(idPhase) {

	if (idPhase == null || idPhase == undefined) {
		var selectedPhase = findSelectItemIdBySection("section_phase");
		if (selectedPhase.length!=1)
			return false;
		idPhase = selectedPhase[0];
	}
	var $confirmDialog = showDialog("#confirm-dialog", MessageResolver("confirm.delete.phase", "Are you sure, you want to delete this phase"));
	$(".btn-danger", $confirmDialog).click(function() {
		$confirmDialog.modal("hide");
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Phase/" +idPhase+"/Delete",
			contentType : "application/json;charset=UTF-8",
			type : 'POST',
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined){
					reloadSection("section_phase");
					showDialog("success", response["success"]);
				}
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

