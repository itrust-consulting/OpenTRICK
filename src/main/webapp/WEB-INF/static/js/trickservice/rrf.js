
/**
 * Imports RRF (Risk and Reliability Framework) for the specified analysis.
 * If the analysis ID is not provided, it will try to find the analysis ID.
 * Only users with MODIFY permission for the analysis can import RRF.
 * This function makes an AJAX request to the server to import the RRF.
 * @param {number} idAnalysis - The ID of the analysis.
 * @returns {boolean} - Returns false.
 */
function importRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = findAnalysisId();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$
			.ajax({
				url: context + "/Analysis/RRF/Import",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					var $content = $("#importMeasureCharacteristics", new DOMParser().parseFromString(response, "text/html"));
					if ($content.length) {

						var modal = new Modal($content.appendTo($("#widgets"))), $modalBody = modal.modal_body, $customers = $modalBody.find("select[name='customer']"), $analyses = $modalBody
							.find("select[name='analysis']"), $standards = $modalBody.find("select[name='standards']");
						$customers.change(function () {
							var value = $(this).val();
							$analyses.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
							$($analyses.find("option[data-trick-id='" + value + "']").show()[0]).prop("selected", true);
							$analyses.change();
						});

						$analyses.on("change", function (e) {
							var value = $(e.target).val();
							if (value == undefined)
								value = 0;
							$standards.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
							$standards.find("option[data-trick-id='" + value + "']").show();
						});

						var $modalFooter = modal.modal_footer, $closeButton = modal.modal_header.find("button"), $importButton = $modalFooter.find("button[name='import']"), $cancelButton = $modalFooter
							.find("button[name='cancel']"), $buttons = modal.modal.find("button");

						$importButton.click(function () {
							if ($importButton.is(":disabled"))
								return false;
							$progress.show();
							$buttons.prop("disabled", true);
							$modalBody.find(".alert").remove();
							$.ajax({
								url: context + "/Analysis/RRF/Import/Save",
								type: "post",
								data: $modalBody.find("form").serialize(),
								success: function (response, textStatus, jqXHR) {
									if (response.success != undefined) {
										showDialog("success", response.success);
										modal.Destroy();
									}
									else {
										if (response.error != undefined)
											showDialog("error", response.error);
										else
											unknowError();
									}
								},
								error: unknowError

							}).complete(function () {
								$buttons.prop("disabled", false);
								$progress.hide();
							});
							return false;
						});

						$cancelButton.click(function () {
							if (!$cancelButton.is(":disabled"))
								modal.Destroy();
							return false;
						});

						$closeButton.click(function () {
							if (!$closeButton.is(":disabled"))
								modal.Destroy();
							return false;
						});

						$customers.change();

						modal.Show();
					} else
						unknowError();
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
	} else
		permissionError();
	return false;
}

/**
 * Imports the raw RRF form for the specified analysis.
 * If the analysis ID is not provided, it will try to find the analysis ID.
 * Only users with MODIFY permission for the analysis can import the raw RRF form.
 * This function makes an AJAX request to the server to import the raw RRF form.
 * @param {number} idAnalysis - The ID of the analysis.
 * @returns {boolean} - Returns false.
 */
function importRawRRFForm(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = findAnalysisId();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/RRF/Form/Import/Raw/" + idAnalysis,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $view = $("#import_raw_rrf_modal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					var $old = $("#import_raw_rrf_modal");
					if ($old.length)
						$old.replaceWith($view);
					else
						$view.appendTo("#widgets");
					$view.modal("show");
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

/**
 * Imports the raw RRF data for the specified analysis.
 * If the analysis ID is not provided, it will try to find the analysis ID.
 * Only users with MODIFY permission for the analysis can import the raw RRF data.
 * This function makes an AJAX request to the server to import the raw RRF data.
 * @param {number} idAnalysis - The ID of the analysis.
 * @returns {boolean} - Returns false.
 */
function importDataRawRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = findAnalysisId();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$("#import_raw_rrf_modal .modal-footer .btn").prop("disabled", true);
		$("#import_raw_rrf_modal .modal-header .close").prop("disabled", true);
		$("#import_raw_rrf_modal .alert").remove();
		$.ajax({
			url: context + "/Analysis/RRF/Import/Raw/" + idAnalysis,
			type: 'POST',
			data: new FormData($('#raw_rrf_form')[0]),
			cache: false,
			contentType: false,
			processData: false,
			async: false,
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					showDialog("success", response["success"]);
					$("#import_raw_rrf_modal").modal("hide");
				}
				else if (response["error"] != undefined)
					showDialog("error", response["error"]);
				else
					unknowError();
			},
			error: unknowError,
			complete: function () {
				$("#import_raw_rrf_modal .modal-footer .btn").prop("disabled", false);
				$("#import_raw_rrf_modal .modal-header .close").prop("disabled", false);
				$progress.hide();
			}
		});
	}
	return false;
}
