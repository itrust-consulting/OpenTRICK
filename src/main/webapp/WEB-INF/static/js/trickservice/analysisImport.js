/**
 * 
 */

function importDataManager(idAnalysis) {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Data-manager/Import" + ($.isNumeric(idAnalysis) ? "?analysisId=" + idAnalysis : ""),
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var $view = $("#import-modal", new DOMParser().parseFromString(response, "text/html"));
			if($view.length){
				$view.appendTo("#dialog-body").modal("show").on("hidden.bs.modal", () => $view.remove());
			}else
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}