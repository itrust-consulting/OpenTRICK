function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Export/" + analysisId,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				 else
					unknowError();
			},
			error: unknowError
		}).complete(()  => $progress.hide());
	}
	return false;
}

function exportAnalysisSOA(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/SOA/Export?idAnalysis=" + analysisId,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide());
	}
	return false;
}

function exportRawActionPlan(analysisId, type) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if(type == null || type == undefined)
		type = findAnalysisType("#section_analysis", analysisId);
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		if(ANALYSIS_TYPE.isHybrid(type)){
			var $dialogModal = $("#analysis-export-raw-action-plan-dialog");
			$dialogModal.modal("show").find("button[name='export']").unbind("click").one("click", (e) => {
				$dialogModal.modal("hide");
				window.location = context + "/Analysis/Export/Raw-Action-plan/"+analysisId+"/"+e.currentTarget.getAttribute("data-trick-type");
			});
		}else window.location = context + "/Analysis/Export/Raw-Action-plan/"+analysisId+"/"+type;
	}
	return false;
}


function exportAnalysisReport(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Export/Report/" + analysisId,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $modal = $("div#analysis-export-dialog", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length){
					$modal.appendTo("#dialog-body").modal("show").on("hidden.bs.modal", () => $modal.remove());
					
					var $btnBrowse = $("button[name='browse']", $modal), $inputFile = $("input[name='file']", $modal), $template =  $("select[name='template']", $modal), $exportBtn = $("button[name='export']", $modal),
					$fileInfo = $("input[name='filename']", $modal), $form =  $("form", $modal), $type = $("input[name='type']", $modal),$btnSubmit = $("button[name='submit']", $modal);
					
					$exportBtn.on("click", (e) => $btnSubmit.trigger("click"));
					
					$btnBrowse.on("click", (e)=> $inputFile.trigger("click"));
				
					var updateExportButtonState = () => {
						var fileValue = $fileInfo.val(), templateValue = $template.val();
						$exportBtn.prop("disabled", (templateValue === "-1"  || !templateValue) &&  (fileValue ==="" || !fileValue));
					}
					
					$type.on("change", (e) => {
						if(e.currentTarget.checked){
							$("option[data-trick-type][data-trick-type='"+e.currentTarget.value+"']", $template).show();
							$("option[data-trick-type][data-trick-type!='"+e.currentTarget.value+"']", $template.val("-1")).hide();
							updateExportButtonState();
						}
					}).trigger("change");
					
					$template.on("change", (e) => {
						if(e.currentTarget.value!="-1"){
							$inputFile[0].value = "";
							$inputFile.trigger("change");
						}else updateExportButtonState();
							
					});
					
					$inputFile.on("change", (e) => {
						var value = $inputFile.val();
						if(value.trim() === '')
							updateExportButtonState();
						else {
							var size = parseInt($inputFile.attr("maxlength"))
							$template.prop("required", false).val("-1");
							if($inputFile[0].files[0].size > size){
								showDialog("error", MessageResolver("error.file.too.large",undefined, size ));
								return false;
							}else if(!checkExtention(value,".docx", $exportBtn))
								return false
						}
						$fileInfo.val(value);
					});
					
					updateExportButtonState();
					
					$form.on("submit", (e) => {
						$progress.show();
						$.ajax({
							url: context + "/Analysis/Export/Report/" + analysisId ,
							type: 'POST',
							data: new FormData($form[0]),
							cache: false,
							contentType: false,
							processData: false,
							success: function (response, textStatus, jqXHR) {
								if (response.success){
									application["taskManager"].Start();
									$modal.modal("hide");
								}
								else if (response.error)
									showDialog("#alert-dialog", response.error);
								else
									unknowError();
							},
							error: unknowError

						}).complete(function () {
							$progress.hide();
						});
						
						return false;
						
					});
					
					
				}
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide())
	}
	return false;
}

function exportRiskSheet(idAnalysis, report) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/RiskRegister/RiskSheet/Form/Export?type=" + report,
			type: "GET",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $modal = $("div#exportRiskSheetForm", new DOMParser().parseFromString(response, "text/html"));
				if ($modal.length) {
					$("button[name='export']", $modal).on("click", function () {
						var data = $("form", $modal).serializeJSON();
						data.filter = {};
						for (var field in data) {
							if (field.indexOf("filter.") != -1) {
								data['filter'][field.replace("filter.", "")] = data[field];
								delete data[field];
							}
						}

						$.ajax({
							url: context + "/Analysis/RiskRegister/RiskSheet/Export",
							type: "post",
							data: JSON.stringify(data),
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								$(".label-danger", $modal).remove();
								if (response["success"] != undefined) {
									$modal.modal("hide");
									application["taskManager"].Start();
								} else if (response["error"])
									showDialog("#alert-dialog", response["error"]);
								 else {
									for (var error in response) {
										var errorElement = document.createElement("label");
										errorElement.setAttribute("class", "label label-danger");
										$(errorElement).text(response[error]);
										switch (error) {
											case "impact":
											case "probability":
											case "direct":
											case "indirect":
											case "cia":
											case "owner":
												$(errorElement).appendTo($("select[name='" + error + "']", $modal).parent());
												break;
										}
									}

								}
							},
							error: unknowError
						});
					});

					$modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", () => $modal.remove())
				} else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide());
	}
	return false;
}

function exportRiskRegister(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/RiskRegister/Export",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined) 
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(()  =>$progress.hide());
	}
	return false;
}
