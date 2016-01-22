// add new standard to analysis

$(document).ready(function() {
	$('#standardModal').on('hidden.bs.modal', function() {
		reloadSection("section_standard");
	})
});

function isAnalysisOnlyStandard(section) {
	var selectedStandard = $(section + " tbody :checked").parent().parent().attr("data-trick-analysisOnly");
	if (selectedStandard === "true")
		return true;
	else
		return false;
}

function manageStandard() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var $modal = $("#standardModal"), locale = findAnalysisLocale();
		$modal.find(".alert").remove();
		$.ajax({
			url : context + "/Analysis/Standard/Manage",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(response, textStatus, jqXHR) {
				if (response["error"] != undefined)
					showError($(".modal-body", $modal)[0], response["error"]);
				else {
					var forms = $("#section_manage_standards", new DOMParser().parseFromString(response, "text/html"));
					if (!forms.length)
						showError($(".modal-footer", $modal)[0], MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data", null, locale));
					else
						$("#section_manage_standards").replaceWith(forms);
				}
				$modal.modal("show");
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

// manage analysis standards

function createStandard() {

	var selectedItem = $("#section_manage_standards tbody :checked").parent().parent();
	if (selectedItem.length != 0)
		return false;

	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {

		var alert = $("#createStandardModal .label-danger");
		if (alert.length)
			alert.remove();

		var locale = findAnalysisLocale();

		$("#createStandardModal #createstandardbutton").prop("disabled", false);
		$("#createStandardModal #standard_label").prop("value", "");
		$("#createStandardModal #standard_version").prop("value", "");
		$("#createStandardModal #standard_description").prop("value", "");
		$("#createStandardModal #standard_form input[name='type'][value='NORMAL']").prop("checked", "checked");
		$("#createStandardModal #standard_computable").prop("checked", "checked");
		$("#createStandardModal #createstandardtitle").text(MessageResolver("label.title.analysis.manage_standard.create", "Create new standard", null, locale));
		$("#createstandardbutton").text(MessageResolver("label.action.create", "Create", null, locale));
		$("#createstandardbutton").attr("onclick", "doCreateStandard('standard_form')");
		$("#createStandardModal").modal('show');

	} else
		permissionError();
	return false;
}

function doCreateStandard(form) {
	$("#createStandardModal #createstandardbutton").prop("disabled", true);
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Create",
			type : "post",
			data : serializeForm(form),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				$("#createStandardModal #createstandardbutton").prop("disabled", false);
				var alert = $("#createStandardModal .label-danger");
				if (alert.length)
					alert.remove();
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");

					$(errorElement).text(response[error]);
					switch (error) {
					case "label":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_label").parent());
						break;
					case "description":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_description").parent());
						break;
					case "standard":
						showError($("#createStandardModal .modal-footer")[0], response["error"]);
						$("#createStandardModal .modal-footer div[class='alert alert-danger']").css("margin-bottom", "0");
						break;
					}
				}
				if (!$("#createStandardModal .label-danger").length) {
					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var $table = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_standards table.table");
							if ($table.length) {
								$("#section_manage_standards table.table").replaceWith($table);
								updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
							} else
								unknowError();
						},
						error : unknowError
					});
					$("#createStandardModal").modal("hide");
				}
				return false;

			},
			error : function() {
				unknowError();
				$("#createStandardModal #createstandardbutton").prop("disabled", false);
			}
		});
	} else
		permissionError();
	return false;
}

// manage analysis standards

function editStandard(standardrowobject) {

	var selectedItem = undefined;

	if (standardrowobject == undefined || standardrowobject == null) {

		selectedItem = $("#section_manage_standards tbody :checked").parent().parent();
		if (selectedItem.length != 1)
			return false;
	} else {
		selectedItem = standardrowobject;
	}
	var canbeedited = $(selectedItem).attr("data-trick-analysisOnly");

	if (canbeedited === "true") {

		if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {

			var id = $(selectedItem).attr("data-trick-id"), $model = $("#createStandardModal"), locale = findAnalysisLocale(), label = $(selectedItem).find("td:nth-child(2)").text(), description = $(selectedItem)
					.find("td:nth-child(4)").text(), type = $(selectedItem).attr("data-trick-type"), computable = $(selectedItem).attr("data-trick-computable");
			$("#createStandardModal .label-danger").remove();

			$("#createstandardbutton", $model).prop("disabled", false);
			$("#standard_label",$model).prop("value", label);
			$("#standard_description",$model).prop("value", description);
			$("#standard_form input[name='type']",$model).removeProp("checked");
			$("#standard_form input[name='type'][value='" + type + "']",$model).prop("checked", true);
			$("#id",$model).val(id);
			if (computable === "true")
				$("#standard_computable",$model).prop("checked", true);
			else
				$("#standard_computable",$model).removeProp("checked");
			$("#createstandardtitle", $model).text(MessageResolver("label.title.analysis.manage_standard.edit", "Edit standard", null, locale));
			$("#createstandardbutton").text(MessageResolver("label.action.edit", "Edit", null, locale));
			$("#createstandardbutton").attr("onclick", "doEditStandard('standard_form')");
			$("#createStandardModal").modal('show');

		} else
			permissionError();
	}

	return false;
}

function doEditStandard(form) {
	$("#createStandardModal #createstandardbutton").prop("disabled", true);
	idAnalysis = findAnalysisId();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Save",
			type : "post",
			data : serializeForm(form),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {

				$("#createStandardModal #createstandardbutton").prop("disabled", false);
				var alert = $("#createStandardModal .label-danger");
				if (alert.length)
					alert.remove();
				for ( var error in response) {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");

					$(errorElement).text(response[error]);
					switch (error) {
					case "label":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_label").parent());
						break;
					case "description":
						$(errorElement).appendTo($("#createStandardModal #standard_form #standard_description").parent());
						break;
					case "type":
						$(errorElement).css({
							"display" : "inline-block",
							"margin-top" : "5px"
						});
						$(errorElement).appendTo($("#createStandardModal #standard_form .panel-body"));
						break;
					case "standard":
						showError($("#createStandardModal .modal-footer")[0], response["error"]);
						$("#createStandardModal .modal-footer div[class='alert alert-danger']").css("margin-bottom", "0");
						break;
					}
				}
				if (!$("#createStandardModal .label-danger").length) {
					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var $table = $("#section_manage_standards table.table", new DOMParser().parseFromString(response, "text/html"));
							if ($table.length) {
								$("#section_manage_standards table.table").replaceWith($table);
								updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
							} else
								unknowError();
						},
						error : unknowError
					});
					$("#createStandardModal").modal("hide");
				}
				return false;

			},
			error : function(jqXHR, textStatus, errorThrown) {
				unknowError(jqXHR, textStatus, errorThrown);
				$("#createStandardModal #createstandardbutton").prop("disabled", false);
			}
		});
	} else
		permissionError();
	return false;
}

function addStandard() {

	var selectedItem = $("#section_manage_standards tbody :checked").parent().parent();
	if (selectedItem.length != 0)
		return false;

	idAnalysis = findAnalysisId();
	var alert = $("#addStandardModal .alert");
	if (alert.length)
		alert.remove();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#add_standard_progressbar").css("display", "none");
		$.ajax({
			url : context + "/Analysis/Standard/Available",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["0"] != undefined) {
					$("#add_standard_progressbar").css("display", "none");
					showError($("#addStandardModal .modal-footer")[0], response["0"]);
				} else {

					if (!$.isEmptyObject(response)) {

						var text = '<div class="col-sm-6"><select name="idNorm" class="form-control">';

						for ( var standard in response) {

							// alert(standard + ' ' + response[standard]);
							text += '<option value="' + standard + '">' + response[standard] + '</option>';

						}

						var lang = findAnalysisLocale();

						text += '</select></div><div class="col-sm-2">';
						text += '<button type="button" class="btn btn-primary" onclick="return doAddStandard(\'addStandardModal\');">'
								+ MessageResolver("label.action.add", "add", null, lang) + '</button></div>';

						$("#addStandardModal .modal-body").html(text);

					} else {

						var lang = findAnalysisLocale();
						var text = '<div class="col-sm-12"><b>' + MessageResolver("label.no_standards_available", "No standards available", null, lang) + '</b></div>';
						$("#addStandardModal .modal-body").html(text);
					}

					$("#addStandardModal").modal("show");
				}
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function doAddStandard(form) {
	var alert = $("#standardModal .alert");
	if (alert.length)
		alert.remove();
	idAnalysis = findAnalysisId();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#add_standard_progressbar").css("display", "inline-block");
		var idStandard = $("#" + form + " select").val();
		$.ajax({
			url : context + "/Analysis/Standard/Add/" + idStandard,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["error"] != undefined) {
					$("#add_standard_progressbar").css("display", "none");
					showError($("#addStandardModal .modal-footer")[0], response["error"]);
					$("#addStandardModal .modal-footer div[class='alert alert-danger']").css("margin-bottom", "0");
				} else if (response["success"] != undefined) {
					$("#add_standard_progressbar").css("display", "none");
					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var parser = new DOMParser();
							var doc = parser.parseFromString(response, "text/html");
							$("#section_manage_standards table.table").replaceWith($(doc).find("#section_manage_standards table.table"));
							updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
						},
						error : unknowError
					});
					$("#addStandardModal").modal("hide");

				}
			},
			error : function() {
				unknowError();
				$("#add_standard_progressbar").css("display", "none");
			}
		});
	} else
		permissionError();
	return false;
}

function removeStandard() {

	var lang = findAnalysisLocale();
	var selectedStandard = $("#section_manage_standards :checked");
	if (selectedStandard.length != 1)
		return false;
	selectedStandard = findTrickID(selectedStandard[0]);

	var deleteModal = new Modal();
	deleteModal.FromContent($("#deleteStandardModal").clone());

	deleteModal.setBody(MessageResolver("confirm.delete.analysis.norm", "Are you sure, you want to remove this standard from this analysis?", null, lang));

	$(deleteModal.modal_footer).find("#deletestandardbuttonYes").click(function() {
		var alert = $("#standardModal .alert");
		if (alert.length)
			alert.remove();
		$(deleteModal.modal_footer).find("#delete_standard_progressbar").css("display", "inline-block");
		$.ajax({
			url : context + "/Analysis/Standard/Delete/" + selectedStandard,
			type : "get",
			async : false,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["error"] != undefined) {
					$(deleteModal.modal_footer).find("#delete_standard_progressbar").css("display", "none");
					showError($("#standardModal .modal-footer")[0], response["error"]);
					$("#standardModal .modal-footer").find("div[class='alert alert-danger']").css("margin-bottom", "0");
				} else if (response["success"] != undefined) {
					$(deleteModal.modal_footer).find("#delete_standard_progressbar").css("display", "none");
					$.ajax({
						url : context + "/Analysis/Standard/Manage",
						type : "get",
						async : false,
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var $table = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_standards table.table");
							if ($table.length) {
								$("#section_manage_standards table.table").replaceWith($table);
								updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
							} else
								unknowError();
						},
						error : unknowError
					});

				} else
					unknowError();
			},
			error : unknowError
		});
	});
	deleteModal.Show();
	return false;
}

// management of measures of analysis only standards
function addMeasure(element, idStandard) {
	if ($(element).parent().hasClass("disabled") || idStandard == undefined || idStandard == null || !$.isNumeric(idStandard))
		return false;
	return manageMeasure(context + "/Analysis/Standard/" + idStandard + "/Measure/New");
}

function editMeasure(element, idStandard, idMeasure) {
	if ($(element).parent().hasClass("disabled") || idStandard == undefined || idStandard == null || !$.isNumeric(idStandard))
		return false;
	if (idMeasure == null || idMeasure == undefined)
		idMeasure = findSelectItemIdBySection("section_standard_" + idStandard);
	if (idMeasure == null || idMeasure == undefined || !$.isNumeric(idStandard))
		return false;
	return manageMeasure(context + "/Analysis/Standard/Measure/" + idMeasure + "/Edit");
}

function manageMeasure(url) {
	$.ajax({
		url : url,
		type : "get",
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#modalMeasureForm");
			if ($content.length) {
				if ($("#modalMeasureForm").length)
					$("#modalMeasureForm").replaceWith($content);
				else
					$content.appendTo($("#widgets"));
				$content.find("#measure_form_tabs").tab();
				var $assetTab = $content.find("#tab_asset");
				if ($assetTab.length) {

					var onSelectedAsset = function(asset) {
						var id = $(asset).attr("data-trick-id");
						$('<input name="assets" value="' + id + '" hidden="hidden">').appendTo($(asset));
						$(asset).appendTo($assetTab.find("ul.asset-measure[data-trick-type='measure']"));
						$(asset).attr("data-trick-selected", true);
						var $header = $('<th data-trick-class="MeasureAssetValue" data-trick-asset-id="' + id + '" >' + $(asset).text() + '</th>')
						var $data = $('<td data-trick-class="MeasureAssetValue" data-trick-asset-id="' + id + '" ><input type="text" class="slider" id="asset_slider_' + id
								+ '" value="0" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0" name="property_asset_' + id
								+ '" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>');
						var $value = $('<td data-trick-class="MeasureAssetValue"  data-trick-asset-id="' + id + '"><input type="text" id="property_asset_' + id
								+ '_value" style="min-width: 50px;" readonly="readonly" class="form-control" value="0" name="' + id + '"></td>');
						$header.appendTo($content.find("#slidersTitle"));
						$data.appendTo($content.find("#sliders"));
						$value.appendTo($content.find("#values"));
						$data.find(".slider").slider().on('slide', function(event) {
							$value.find("input").val(event.value);
						});
					};

					var onDeselectedAsset = function(asset) {
						var id = $(asset).attr("data-trick-id");
						$(asset).find("input").remove();
						$(asset).appendTo($assetTab.find("ul.asset-measure[data-trick-type='available']"));
						$(asset).attr("data-trick-selected", false);
						$content.find('[data-trick-class="MeasureAssetValue"][data-trick-asset-id="' + id + '"]').remove();
					};

					$assetTab.find("li[data-trick-type]").each(function() {
						$(this).on("click", function() {
							if ($(this).attr("data-trick-selected") == "true")
								onDeselectedAsset(this);
							else
								onSelectedAsset(this);
						});
					});

					var updateAssetUI = function(selected) {
						if (selected === 'ALL')
							$assetTab.find("li[data-trick-type]").show();
						else {
							$assetTab.find("li[data-trick-type='" + selected + "']").show();
							$assetTab.find("li[data-trick-type!='" + selected + "']").hide();
						}
					};

					updateAssetUI($assetTab.find("#assettypes").val());

					$assetTab.find("#assettypes").on("change", function() {
						updateAssetUI($(this).val());
					});
				}
				$($content.find(".slider")).slider().each(function() {
					$(this).on("slide", function(event) {
						$content.find("#values input[name='" + event.target.name + "']").val(event.value);
					})
				});
				new Modal($content).Show();
			} else if (response["error"] != undefined) {
				$("#info-dialog .modal-body").text(response["error"]);
				$("#info-dialog").modal("show");
			} else
				unknowError()
		},
		error : unknowError
	});
	return false;
}

function saveMeasure() {
	var data = {};
	var form = $("#modalMeasureForm #measure_form");
	var $genearal = form.find("#tab_general");
	var properties = form.find("#tab_properties #values").serializeJSON();
	var $assetTab = form.find("#tab_asset");
	if ($genearal.length)
		data = $genearal.serializeJSON();
	$(".label-danger", "#modalMeasureForm").remove();
	data.id = form.find("#id").val();
	data.idStandard = form.find("#idStandard").val();
	data.assetValues = [];

	form.find("#tab_properties #values input[id^='property_asset']").each(function() {
		data.assetValues.push({
			id : this.name,
			value : properties[this.name],
		});
		delete properties[this.name];
	});

	data.properties = properties;
	data.computable = data.computable === "on";

	if ($assetTab.length) {
		data.type = "ASSET";
		if (data.computable && !data.assetValues.length) {
			$("<label class='label label-danger'></label>").text(MessageResolver("error.asset.empty", "Asset cannot be empty")).appendTo($("#modalMeasureForm #error_container"));
			return false;
		}
	} else
		data.type = "NORMAL";

	$.ajax({
		url : context + "/Analysis/Standard/Measure/Save",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "reference":
				case "level":
				case "computable":
				case "domain":
				case "description":
					$(errorElement).appendTo(form.find("#" + error).parent());
					break;
				default:
					$(errorElement).appendTo($("#modalMeasureForm #error_container"))
					break;
				}
			}
			if (!$("#modalMeasureForm").find(".label-danger").length) {
				$("#modalMeasureForm").modal("hide");
				if (data.id > 0)
					reloadMeasureRow(data.id, data.idStandard);
				else
					reloadSection("section_standard_" + data.idStandard);
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$('<label class="label label-danger">' + MessageResolver("error.unknown.occurred", "An unknown error occurred") + '</label>').appendTo(
					$("#modalMeasureForm #error_container"));
			return false;
		}
	});

	return false;
}

function deleteMeasure(measureId, standardid) {

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	if (standardid == null || standardid == undefined)
		return false;

	if (measureId == null || measureId == undefined) {
		var selectedMeasures = findSelectItemIdBySection("section_standard_" + standardid);
		if (!selectedMeasures.length)
			return false;
	}

	var lang = findAnalysisLocale();

	var standard = $("#section_standard_" + standardid + " #menu_standard_" + standardid + " li:first-child").text();

	if (selectedMeasures.length == 1) {
		measureId = selectedMeasures[0];
		var measure = $("#section_standard_" + standardid + " tr[data-trick-id='" + measureId + "'] td:not(:first-child)");
		reference = $(measure[0]).text();

		$("#confirm-dialog .modal-body").html(
				MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: <strong>" + reference
						+ "</strong> from the standard <strong>" + standard
						+ " </strong>?<b>ATTENTION:</b> This will delete complete <b>Action Plans</b> that depend on these measures!", [ reference, standard ], lang));
	} else {
		$("#confirm-dialog .modal-body").html(
				MessageResolver("label.measure.question.selected.delete", "Are you sure, you want to delete the selected measures from the standard <b>" + standard
						+ "</b>?<br/><b>ATTENTION:</b> This will delete complete <b>Action Plans</b> that depend on these measures!", standard, lang));
	}

	$("#confirm-dialog .btn-danger").click(function() {

		var errors = false;

		while (selectedMeasures.length) {

			if (errors)
				break;

			rowTrickId = selectedMeasures.pop();

			$.ajax({
				url : context + "/Analysis/Standard/" + standardid + "/Measure/Delete/" + rowTrickId,
				async : false,
				contentType : "application/json",
				success : function(response, textStatus, jqXHR) {
					if (response["success"] == undefined) {
						errors = true;
						if (response["error"] != undefined) {
							$("#alert-dialog .modal-body").html(response["error"]);
						} else {
							$("#alert-dialog .modal-body").html(MessageResolver("error.delete.measure.unkown", "Unknown error occoured while deleting the measure", null, lang));
						}
						$("#alert-dialog").modal("show");
					}
				},
				error : unknowError
			});
		}
		$("#confirm-dialog").modal("hide");
		reloadSection("section_standard_" + standardid);
		return false;
	});
	$("#confirm-dialog").modal("show");
	return false;
}