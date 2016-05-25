function importRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = findAnalysisId();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$
				.ajax({
					url : context + "/Analysis/RRF/Import",
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
						var $content = $("#importMeasureCharacteristics", new DOMParser().parseFromString(response, "text/html"));
						if ($content.length) {
						
							var modal = new Modal($content.appendTo($("#widgets"))), $modalBody = modal.modal_body, $customers = $modalBody.find("select[name='customer']"), $analyses = $modalBody
									.find("select[name='analysis']"), $standards = $modalBody.find("select[name='standards']");
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
							
							var $modalFooter = modal.modal_footer, $closeButton = modal.modal_header.find("button"), $importButton = $modalFooter.find("button[name='import']"), $cancelButton = $modalFooter
									.find("button[name='cancel']"), $progressBar = $modalBody.find(".progress"), $buttons = modal.modal.find("button");
							
							$importButton.click(function() {
								if ($importButton.is(":disabled"))
									return false;
								$progressBar.show();
								$buttons.prop("disabled", true);
								$modalBody.find(".alert").remove();
								$.ajax({
									url : context + "/Analysis/RRF/Import/Save",
									type : "post",
									data : $modalBody.find("form").serialize(),
									success : function(response, textStatus, jqXHR) {
										if (response.success != undefined)
											showSuccess($modalBody[0], response.success);
										else {
											if (response.error != undefined)
												showError($modalBody[0], response.error);
											else
												unknowError();
										}
									},
									error : function(jqXHR, textStatus, errorThrown) {
										$progressBar.hide();
										$buttons.prop("disabled", false);
										showError($modalBody[0], MessageResolver("error.unknown.occurred", "An unknown error occurred"));
									}
								}).done(function() {
									$progressBar.hide();
									$buttons.prop("disabled", false);
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
		idAnalysis = findAnalysisId();
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
		idAnalysis = findAnalysisId();
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
				else
					unknowError();
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