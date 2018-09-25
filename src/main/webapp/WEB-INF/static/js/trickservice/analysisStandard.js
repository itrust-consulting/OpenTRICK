// add new standard to analysis, updated by @eomar 24/05/2016
var $deleteModal = $("#deleteStandardModal"), $standardModal = $("#standardModal");

$(document).ready(function () {
	$standardModal.on('hidden.bs.modal', function () {
		$standardModal.find(".modal-footer button[name='save']").off("click");
		$standardModal.find(".modal-footer a.btn").trigger("click");
		if (application["standard-change"]) {
			$("#loading-indicator").show()
			setTimeout(function () {
				location.reload();
			}, 10);
		}
	});

	$deleteModal.on('hidden.bs.modal', function () {
		if ($(".modal-backdrop").length)
			$("body").addClass("modal-open");
		$deleteModal.find("#deletestandardbuttonYes").off("click.delete")
	});

	$("#measure-collection-selector").on("change", function () {
		$("[id^='tab-standard-'][id!='" + this.value + "']:visible").hide();
		triggerCaller($("#" + this.value).show());
		disableEditMode();
	}).trigger("change");

});

function isAnalysisOnlyStandard(section) {
	var selectedStandard = $(section + " tbody :checked").parent().parent().attr("data-trick-analysisOnly");
	if (selectedStandard === "true")
		return true;
	else
		return false;
}
/**
 * @author eomar
 * @since 24/05/2016
 * @see importStandard, editStandard, addStandard, saveStandard and
 *      removeStandard
 * @returns {Boolean}
 */
function manageStandard() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$
			.ajax({
				url: context + "/Analysis/Standard/Manage",
				type: "get",
				contentType: "application/json;charset=UTF-8",
				async: false,
				success: function (response, textStatus, jqXHR) {
					if (response["error"] != undefined)
						showDialog("error", response["error"]);
					else {
						var $forms = $("#section_manage_standards", new DOMParser().parseFromString(response, "text/html"));
						if (!$forms.length)
							showError($("#error-standard-modal", $standardModal)[0],
								MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data"));
						else
							$("#section_manage_standards").replaceWith($forms);

						var $tabs = $standardModal.find("#menu_manage_standards a[data-toggle='tab']"), $cancelBtn = $standardModal.find(".modal-footer button[name='cancel']"), $backBtn = $standardModal
							.find(".modal-footer a.btn"), $saveBtn = $standardModal.find(".modal-footer button[name='save']");

						$saveBtn.on("click", saveStandard);

						$tabs.on('shown.bs.tab', function () {
							$(this).parent().removeClass("active");
							if (this.getAttribute("role") != "import")
								$saveBtn.show();
							$backBtn.show();
							$cancelBtn.hide();
						}).each(function () {
							switch (this.getAttribute("role")) {
								case "import":
									$(this).on('show.bs.tab', importStandard)
									break;
								case "edit":
									$(this).on('show.bs.tab', editStandard)
									break;
								case "add":
									$(this).on('show.bs.tab', addStandard)
									break;
								default:
									break;
							}
						});

						$backBtn.on('click', function () {
							$saveBtn.hide();
							$backBtn.hide();
							$cancelBtn.show();
							$standardModal.find(".label-danger,.alert-danger").remove();
						});
						$standardModal.modal("show");
					}

				},
				error: unknowError
			});
	} else
		permissionError();
	return false;
}

// rewritten by @eomar 24/05/2016
function importStandard(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	var $progress = $("#loading-indicator").show();
	$standardModal.find(".label-dander").remove();
	$.ajax({
		url: context + "/Analysis/Standard/Available",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		async: false,
		success: function (response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#importStandardTable");
			if (!$content.length) {
				$("<label class='label label-danger' />").text(MessageResolver("error.unknown.occurred", "An unknown error occurred")).appendTo("#error-standard-modal");
				e.preventDefault();
			} else {
				var $tableStandard = $standardModal.find("#table_current_standard");
				$("#importStandardTable", $standardModal).replaceWith($content);
				$content.find("button.btn").on("click", function (e) {
					var $this = $(this), $tr = $this.closest("tr");
					$progress.show();
					$.ajax({
						url: context + "/Analysis/Standard/Add/" + $tr.attr("data-trick-id"),
						type: "post",
						contentType: "application/json;charset=UTF-8",
						success: function (response, textStatus, jqXHR) {
							if (response["error"] != undefined) {
								$("<label class='label label-danger' />").text(response["error"]).appendTo("#error-standard-modal");
							} else if (response["success"] != undefined) {
								$tableStandard.find("tbody>tr:not([data-trick-id])").remove();
								$tr.find("td:last-child").remove();
								if ($tableStandard.find("tr[data-trick-id='" + $tr.attr("data-trick-id") + "']").length)
									$tr.remove();
								else
									$tr.appendTo($tableStandard.find("tbody")).find("td:hidden").show();
								$tr.on("click",(e) => {selectElement(e.currentTarget);});
								application["standard-change"] = true;
							}
						},
						error: unknowError
					}).complete(function () {
						$progress.hide();
					});
				});
			}
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
}

function reloadStandardTable() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Standard/Manage",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $table = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_standards table.table");
			if ($table.length) {
				$("#section_manage_standards table.table").replaceWith($table);
				updateMenu(undefined, '#section_manage_standards', '#menu_manage_standards');
				$(".modal-footer a.btn", $standardModal).trigger("click");
				application["standard-change"] = true;
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

// rewritten by @eomar 24/05/2016
function editStandard(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	var $tr = $("#section_manage_standards tbody>tr[data-trick-analysisOnly='true'] :checked").closest("tr"), $form = $("#standard_form"), id = $tr.attr("data-trick-id"), label = $tr
		.find("td:nth-child(2)").text(), description = $tr.find("td:nth-child(4)").text(), type = $tr.attr("data-trick-type"), computable = $tr.attr("data-trick-computable");
	$("#id", $form).val(id);
	$("#standard_label", $form).val(label);
	$("#standard_description", $form).val(description);
	$("input[name='type']", $form).removeProp("checked").prop("disabled", true);
	$("input[name='type'][value='" + type + "']", $form).prop("checked", true);
	if (computable === "true")
		$("#standard_computable", $form).prop("checked", true);
	else
		$("#standard_computable", $form).removeProp("checked");
	return true;
}

// added by @eomar 24/05/2016
function addStandard(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	var $form = $("#standard_form")
	$("#id", $form).prop("value", "-1");
	$("#standard_label", $form).prop("value", "");
	$("#standard_version", $form).prop("value", "");
	$("#standard_description", $form).prop("value", "");
	$("input[name='type']", $form).prop("disabled", false);
	$("input[name='type'][value='NORMAL']", $form).prop("checked", "checked");
	$("#standard_computable", $form).prop("checked", "checked");
	return true;
}

// added by @eomar 24/05/2016
function saveStandard(e) {
	var $btn = $(e.currentTarget).prop("disabled", true), $progress = $("#loading-indicator").show(), $form = $("#standard_form");
	$(".label-danger,.alert-danger", $standardModal).remove();
	$.ajax({
		url: context + "/Analysis/Standard/Save",
		type: "post",
		data: serializeForm($form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			for (var error in response) {
				var $errorElement = $("<label class='label label-danger' />").text(response[error]);
				switch (error) {
					case "label":
						$errorElement.appendTo($form.find("#standard_label").parent());
						break;
					case "description":
					case "standard":
						$errorElement.appendTo($form.find("#standard_description").parent());
						break;
				}
			}
			if (!$(".label-danger", $standardModal).length)
				reloadStandardTable();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
		$btn.prop("disabled", false);
	});
}

function removeStandard() {
	var selectedStandard = $("#section_manage_standards :checked");
	if (selectedStandard.length != 1)
		return false;
	selectedStandard = findTrickID(selectedStandard[0]);
	$deleteModal.find(".modal-body").text(MessageResolver("confirm.delete.analysis.norm", "Are you sure, you want to remove this standard from this analysis?"));
	$deleteModal.find("#deletestandardbuttonYes").one("click.delete", function () {
		$(".label-danger", $standardModal).remove();
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/Delete/" + selectedStandard,
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["error"] != undefined)
					$("<label class='label label-danger' />").text(response["error"]).appendTo("#error-standard-modal");
				else if (response["success"] != undefined)
					reloadStandardTable();
				else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	});
	$deleteModal.modal("show");
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
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: url,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#modalMeasureForm");
			if ($content.length) {
				$content.appendTo($("#widgets"));
				setupMeasureManager($content).modal("show").on("hidden.bs.modal", function () {
					$content.remove();
				});
			} else if (response["error"] != undefined)
				showDialog("#info-dialog", response["error"]);
			else
				unknowError()
		},
		error: unknowError
	}).complete( () => $progress.hide());
	return false;
}

function setupMeasureManager($content) {
	$content.find("#measure_form_tabs").tab();
	var $assetTab = $content.find("#tab_asset");
	if ($assetTab.length) {
		var onSelectedAsset = function (asset) {
			var $asset = $(asset), id = $asset.attr("data-trick-id");
			$('<input name="assets" value="' + id + '" hidden="hidden">').appendTo($asset);
			$asset.appendTo($assetTab.find("ul.asset-measure[data-trick-type='measure']")).attr("data-trick-selected", true);
			if (application.analysisType.isQuantitative()) {
				var $header = $('<th data-trick-class="MeasureAssetValue" data-trick-asset-id="' + id + '" >' + $(asset).text() + '</th>'), $data = $('<td data-trick-class="MeasureAssetValue" data-trick-asset-id="'
					+ id
					+ '" ><input type="text" class="slider" id="asset_slider_'
					+ id
					+ '" value="0" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0" name="property_asset_'
					+ id
					+ '" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>'), $value = $('<td data-trick-class="MeasureAssetValue"  data-trick-asset-id="'
						+ id
						+ '"><input type="text" id="property_asset_'
						+ id
						+ '_value" style="min-width: 50px;" readonly="readonly" class="form-control" value="0" name="'
						+ id
						+ '"></td>');

				$header.appendTo($content.find("#slidersTitle"));
				$data.appendTo($content.find("#sliders"));
				$value.appendTo($content.find("#values"));
				$data.find(".slider").slider({
					reversed: true
				}).on('change', function (event) {
					$value.find("input").val(event.value.newValue);
				});
			}
		};

		var onDeselectedAsset = function (asset) {
			var $asset = $(asset);
			$asset.find("input").remove();
			$asset.appendTo($assetTab.find("ul.asset-measure[data-trick-type='available']")).attr("data-trick-selected", false);
			if (application.analysisType.isQuantitative())
				$content.find('[data-trick-class="MeasureAssetValue"][data-trick-asset-id="' + $asset.attr("data-trick-id") + '"]').remove();
		};

		$assetTab.find("li[data-trick-type]").each(function () {
			$(this).on("click", function () {
				if ($(this).attr("data-trick-selected") == "true")
					onDeselectedAsset(this);
				else
					onSelectedAsset(this);
			});
		});

		var updateAssetUI = function (selected) {
			if (selected === 'ALL')
				$assetTab.find("li[data-trick-type]").show();
			else {
				$assetTab.find("li[data-trick-type='" + selected + "']").show();
				$assetTab.find("li[data-trick-type!='" + selected + "']").hide();
			}
		};

		updateAssetUI($assetTab.find("#assettypes").val());

		$assetTab.find("#assettypes").on("change", function () {
			updateAssetUI($(this).val());
		});
	}

	if (application.analysisType.isQuantitative()) {
		$content.find(".slider").slider({
			reversed: true
		}).each(function () {
			$(this).on("change", function (event) {
				$content.find("#values input[name='" + event.target.name + "']").val(event.value.newValue);
			});
		});
	}
	return $content;
}

function saveMeasure(form, callback) {
	var data = {}, $form = $(form), $modalMeasureForm = $form.closest(".modal"), $genearal = $("#tab_general", $form), properties = $("#tab_properties #values", $form)
		.serializeJSON(), $assetTab = $("#tab_asset", $form);
	if ($genearal.length)
		data = $genearal.serializeJSON();
	$(".label-danger", $modalMeasureForm).remove();
	data.id = $("#id", $form).val();
	data.idStandard = $("#idStandard", $form).val();
	data.assetValues = [];
	if ($assetTab.length) {
		data.type = "ASSET";
		if (application.analysisType.isQuantitative()) {
			$("#tab_properties #values input[id^='property_asset']", $form).each(function () {
				data.assetValues.push({
					id: this.name,
					value: properties[this.name]
				});
				delete properties[this.name];
			});
		} else {
			$("li[data-trick-selected='true'][data-trick-type][data-trick-id]", $assetTab).each(function () {
				data.assetValues.push({
					id: this.getAttribute("data-trick-id"),
					value: 100
				});
			});
		}

		if (data.computable && !data.assetValues.length) {
			$("<label class='label label-danger'></label>").text(MessageResolver("error.asset.empty", "Asset cannot be empty")).appendTo($("#error_container", $modalMeasureForm));
			$("a[href='#tab_asset']", $modalMeasureForm).tab("show");
			return false;
		}

	} else
		data.type = "NORMAL";
	
	data.properties = properties;
	data.computable = data.computable === "on";
	
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Standard/Measure/Save",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			for (var error in response.errors) {
				var $errorElement = $("<label class='label label-danger'/>").text(response.errors[error]);
				switch (error) {
					case "reference":
					case "computable":
					case "domain":
					case "description":
						$errorElement.appendTo($("#" + error, $form).parent());
						break;
					default:
						$errorElement.appendTo($("#error_container", $modalMeasureForm))
						break;
				}
			}
			if (response.id != undefined) {
				if (!callback)
					$modalMeasureForm.modal("hide");
				else
					callback();
				if (data.id > 0)
					reloadMeasureRow(data.id, data.idStandard);
				else
					reloadSection("section_standard_" + data.idStandard);
				data.id = response.id;
				updateMeasureNavigationControl(data);
			} else if ($genearal.length)
				$("a[href='#tab_general']", $modalMeasureForm).tab("show");
			return false;
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$('<label class="label label-danger">' + MessageResolver("error.unknown.occurred", "An unknown error occurred") + '</label>').appendTo(
				$("#error_container", $modalMeasureForm));
			return false;
		}
	}).complete( () => $progress.hide());
	return false;
}

function deleteMeasure(measureId, idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	var selectedMeasures = [];

	if (measureId == null || measureId == undefined) {
		selectedMeasures = findSelectItemIdBySection("section_standard_" + idStandard);
		if (!selectedMeasures.length)
			return false;
	} else
		selectedMeasures.push(measureId);

	var standard = $("#section_standard_" + idStandard + " #menu_standard_" + idStandard + " li:first-child").text();

	if (selectedMeasures.length == 1) {
		var measure = $("#section_standard_" + idStandard + " tr[data-trick-id='" + selectedMeasures[0] + "'] td:not(:first-child)");
		reference = $(measure[0]).text();
		$("#confirm-dialog .modal-body").html(
			MessageResolver("label.measure.question.delete", "Are you sure that you want to delete the measure with the Reference: <strong>" + reference
				+ "</strong> from the standard <strong>" + standard
				+ " </strong>?<b>ATTENTION:</b> This will delete complete <b>Action Plans</b> that depend on these measures!", [reference, standard]));
	} else {
		$("#confirm-dialog .modal-body").html(
			MessageResolver("label.measure.question.selected.delete", "Are you sure, you want to delete the selected measures from the standard <b>" + standard
				+ "</b>?<br/><b>ATTENTION:</b> This will delete complete <b>Action Plans</b> that depend on these measures!", standard));
	}

	$("#confirm-dialog .btn-danger").click(function () {
		$("#confirm-dialog").modal("hide");
		var $progress = $("#loading-indicator").show();
		while (selectedMeasures.length) {
			if ($progress.is(":hidden"))
				break;
			deleteSingleMeasure($progress, idStandard, selectedMeasures.pop(), selectedMeasures.length == 0);
		}
		return false;
	});
	$("#confirm-dialog").modal("show");
	return false;
}

function deleteSingleMeasure($progress, idStandard, idMeasure, last) {
	var error = false;
	$.ajax({
		url: context + "/Analysis/Standard/" + idStandard + "/Measure/Delete/" + idMeasure,
		type: "POST",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if ((error = response["success"] == undefined)) {
				if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					showDialog("#alert-dialog", MessageResolver("error.delete.measure.unkown", "Unknown error occoured while deleting the measure"));
			} else {
				$("tr[data-trick-id='" + idMeasure + "']", "#section_standard_" + idStandard).remove();
				forceUpdateMenu($("#section_standard_" + idStandard));
				removeFromMeasureNavigation(idStandard, idMeasure);
			}
		},
		error: unknowError
	}).complete(function () {
		if (error || last) {
			$progress.hide();
			if (error)
				reloadSection("section_standard_" + idStandard);
		}
	});
}

function manageSOA() {
	var idAnalysis = -1;
	if (userCan(idAnalysis = findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/SOA/Manage",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#manageSAOModel");
				if ($content.length) {
					if ($("#manageSAOModel").length)
						$("#manageSAOModel").replaceWith($content);
					else
						$content.appendTo($("#widgets"));
					$content.modal("show");
					$content.find("[name='save']").on("click", function () {
						var data = [];
						$content.find(".form-group[data-trick-id][data-default-value]").each(function () {
							var $this = $(this), newState = $this.find("input[type='radio']:checked").val(), oldState = $this.attr("data-default-value");
							if (newState != oldState) {
								data.push({
									"id": $this.attr('data-trick-id'),
									"enabled": newState
								});
							}
						});

						if (data.length) {
							$.ajax({
								url: context + "/Analysis/Standard/SOA/Save",
								type: "post",
								data: JSON.stringify(data),
								contentType: "application/json;charset=UTF-8",
								success: function (response, textStatus, jqXHR) {
									if (response.error != undefined)
										showDialog("#alert-dialog", response.error);
									else if (response.success != undefined) {
										calculateAction({
											"id": idAnalysis
										});

										setTimeout(function () {
											$progress.show();
											setTimeout(function name() {
												location.reload();
											}, 600);
										}, 100);

									} else
										unknowError();
								},
								error: unknowError
							}).complete(function () {
								$progress.hide();
							});
						}

					});

				} else if (response["error"] != undefined)
					showDialog("#info-dialog", response["error"]);
				else
					unknowError()
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

function validateSOAState(idStandard, idMeasure) {
	$("tr[data-trick-id='" + idMeasure + "']>td[data-trick-field!='soaReference'][data-trick-field]", "#table_SOA_" + idStandard).each(function () {
		var $this = $(this);
		if ($this.text().trim() == "")
			$this.addClass("warning");
		else
			$this.removeClass("warning");
	});
	return false;
}