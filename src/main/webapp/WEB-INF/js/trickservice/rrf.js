function importRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/RRF/Import",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				if ($(doc).find("#importMeasureCharacteristics").length) {
					var modal = new Modal($(doc).find("#importMeasureCharacteristics").clone());
					var $standards = $(modal.modal_body).find("select[name='standards']");
					var $profileSelector = $(modal.modal_body).find("select[name='profile']");
					$profileSelector.on("change", function(e) {
						var value = $(e.target).val();
						if (value == undefined)
							value = -1;
						$standards.find("option[name!='" + value + "']").hide().prop("selected", false);
						$standards.find("option[name='" + value + "']").show();
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
							success : function(response) {
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

					$profileSelector.change();

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