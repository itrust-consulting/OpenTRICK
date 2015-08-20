function importRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/RRF/Import",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ($(doc).find("#importMeasureCharacteristics").length) {
					var modal = new Modal($(doc).find("#importMeasureCharacteristics").clone());
					var $customers = $(modal.modal_body).find("select[name='customer']");
					var $analyses = $(modal.modal_body).find("select[name='analysis']");
					var $standards = $(modal.modal_body).find("select[name='standards']");

					$customers.change(function() {
						var value = $(this).val();
						$analyses.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
						$($analyses.find("option[data-trick-id='" + value + "']").show()[0]).prop("selected", true);
						$analyses.change();
					});

					$analyses.on("change", function(e) {
						var value = $(e.target).val();
						if (value == undefined)
							value = -1;
						$standards.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
						$standards.find("option[data-trick-id='" + value + "']").show();
					});
					var $closeButton = $(modal.modal_header).find("button");
					var $switchRRFButton = $(modal.modal_footer).find("button[name='show_rrf']");
					var $importButton = $(modal.modal_footer).find("button[name='import']");
					var $cancelButton = $(modal.modal_footer).find("button[name='cancel']");
					var $progressBar = $(modal.modal_body).find(".progress");
					// Rejected by Product owner
					$switchRRFButton.hide();

					$importButton.click(function() {
						if ($importButton.is(":disabled"))
							return false;
						$progressBar.show();
						$(modal.modal).find("button").prop("disabled", true);
						$(modal.modal_body).find(".alert").remove();
						$.ajax({
							url : context + "/Analysis/RRF/Import/Save",
							type : "post",
							data : $(modal.modal_body).find("form").serialize(),
							success : function(response, textStatus, jqXHR) {
								if (response.success != undefined)
									showSuccess($(modal.modal_body)[0], response.success);
								else {
									if (response.error != undefined)
										showError($(modal.modal_body)[0], response.error);
									else
										unknowError();
								}
							},
							error : function(jqXHR, textStatus, errorThrown) {
								$progressBar.hide();
								$(modal.modal).find("button").prop("disabled", false);
								showError($(modal.modal_body)[0], MessageResolver("error.unknown.occurred", "An unknown error occurred"));
							}
						}).done(function() {
							$progressBar.hide();
							$(modal.modal).find("button").prop("disabled", false);
						});
						return false;
					});
					$cancelButton.click(function() {
						if (!$cancelButton.is(":disabled"))
							modal.Destroy();
						return false;
					});

					$closeButton.click(function() {
						if (!$closeButton.is(":disabled"))
							modal.Destroy();
						return false;
					});

					$switchRRFButton.click(function() {
						if (!$switchRRFButton.is(":disabled")) {
							modal.Destroy();
							editRRF(idAnalysis);
						}
						return false;
					});

					$customers.change();

					modal.Show();
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function importRawRRFForm(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/RRF/Form/Import/Raw/" + idAnalysis,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
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
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function importDataRawRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#import_raw_rrf_modal .modal-footer .btn").prop("disabled", true);
		$("#import_raw_rrf_modal .modal-header .close").prop("disabled", true);
		$("#import_raw_rrf_modal .alert").remove();
		$.ajax({
			url : context + "/Analysis/RRF/Import/Raw/" + idAnalysis,
			type : 'POST',
			data : new FormData($('#raw_rrf_form')[0]),
			cache : false,
			contentType : false,
			processData : false,
			async : false,
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					$("#import_raw_rrf_modal").modal("hide");
				else if (response["error"] != undefined)
					$('#raw_rrf_form').before($("<label class='alert alert-danger col-md-12' />").text(response["error"]));
				else unknowError();
			},
			error : unknowError,
			complete : function() {
				$("#import_raw_rrf_modal .modal-footer .btn").prop("disabled", false);
				$("#import_raw_rrf_modal .modal-header .close").prop("disabled", false);
			}
		});
	}
	return false;
}

function exportRawRRF(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		$.fileDownload(context + '/Analysis/RRF/Export/Raw/' + analysisId).fail(unknowError);
		return false;
	} else
		permissionError();
	return false;
}