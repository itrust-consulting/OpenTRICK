
/**
 * This file contains JavaScript code for managing analysis standards.
 * It includes functions for handling modals, managing standards, importing standards from file and knowledge base,
 * updating the import file form, and reloading the standard table.
 * 
 * @since 24/05/2016
 * @see importStandard, editStandard, addStandard, saveStandard, and removeStandard
 */
let $deleteModal = $("#deleteStandardModal");
let $standardModal = $("#standardModal");

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


/**
 * Checks if the given section is set to analysis only standard.
 *
 * @param {string} section - The section to check.
 * @returns {boolean} - Returns true if the section is set to analysis only standard, otherwise false.
 */
function isAnalysisOnlyStandard(section) {
	return $(section + " tbody :checked").parent().parent().attr("data-trick-analysisOnly") === "true";
}
/**
 * 
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
				success: function (response) {
					if (response["error"] != undefined)
						showDialog("error", response["error"]);
					else {
						let $forms = $("#section_manage_standards", new DOMParser().parseFromString(response, "text/html"));
						if (!$forms.length)
							showError($("#error-standard-modal", $standardModal)[0],
								MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data"));
						else
							$("#section_manage_standards").replaceWith($forms);

						let $tabs = $standardModal.find("#menu_manage_standards a[data-toggle='tab']");
						let $cancelBtn = $standardModal.find(".modal-footer button[name='cancel']");
						let $backBtn = $standardModal.find(".modal-footer a.btn");
						let $saveBtn = $standardModal.find(".modal-footer button[name='save']");
						let $importBtn = $standardModal.find(".modal-footer button[name='import']");

						$saveBtn.on("click", saveStandard);

						$importBtn.on("click", importStandardFromFile);

						$tabs.on('shown.bs.tab', function () {
							let role = this.getAttribute("role");
							let $this = $(this);

							$this.parent().removeClass("active");

							if (!role.includes("import")) {
								$saveBtn.show();
							} else if (role.includes("import-file")) {
								$importBtn.show();
								$standardModal.find("#importStandardFromFileInputFile").trigger("change");
							}

							$backBtn.show();
							$cancelBtn.hide();

						}).each(function () {
							let $this = $(this);
							switch (this.getAttribute("role")) {
								case "import-kb":
									$this.on('show.bs.tab', importStandardFromKb)
									break;
								case "edit":
									$this.on('show.bs.tab', editStandard)
									break;
								case "import-file":
									$this.on('show.bs.tab', () => {
										$standardModal.find("#importStandardFromFile")[0].reset();
										$standardModal.find("#importStandardFromFile input[name='id']").prop("value", "0");
										$standardModal.find("#importStandardFromFile select[name='type']").prop("disabled", false);
										$standardModal.find("#importStandardFromFile input[name='name']").prop("value", name).prop("disabled", false);
									});
									break;
								case "import-file-update":
									$this.on('show.bs.tab', updateImportFileForm)
									break;
								case "add":
									$this.on('show.bs.tab', addStandard)
									break;
								default:
									break;
							}
						});

						$backBtn.on('click', function () {
							$saveBtn.hide();
							$backBtn.hide();
							$importBtn.hide();
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

/**
 * Load data from measure table. 
 */
function updateImportFileForm(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	let $tr = $standardModal.find("input:checked").closest("tr");
	let id = $tr.attr("data-trick-id");
	let name = $tr.find('td[data-name="name"]').text();
	let type = $tr.find('td[data-name="type"]').attr("data-real-value");
	$standardModal.find("#importStandardFromFile")[0].reset();
	$standardModal.find("#importStandardFromFile input[name='id']").prop("value", id);
	$standardModal.find("#importStandardFromFile select[name='type']").prop("value", type).prop("disabled", true);
	$standardModal.find("#importStandardFromFile input[name='name']").prop("value", name).prop("disabled", true);
}

/**
 * Imports a standard from a file.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} Returns false.
 */
function importStandardFromFile(e) {
	let $uploadFile = $("#upload-file-info", $standardModal), $progress = $("#loading-indicator");
	if (!$uploadFile.length)
		return false;
	else if ($uploadFile.val() == "") {
		showError($("#error-standard-modal", $standardModal)[0], MessageResolver("error.import.standard.no_select.file", "Please select file to import"));
		return false;
	}
	let $importBtn = $(e.currentTarget).prop('disabled', true);
	$progress.show();
	$.ajax({
		url: context + "/Analysis/Standard/Import-from-file",
		type: 'POST',
		data: new FormData($('#importStandardFromFile')[0]),
		cache: false,
		contentType: false,
		processData: false,
		success: function (response) {
			if (response.success) {
				application["standard-change"] = true;
				showDialog("#success-dialog", response.success);
				reloadStandardTable();
			}
			else if (response.error)
				showDialog("#alert-dialog", response.error);
			else
				showDialog("#alert-dialog", MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
		},
		error: unknowError

	}).complete(function () {
		$progress.hide();
		$importBtn.prop('disabled', false);
	});
	return false;
}


/**
 * Imports a standard from the Knowledge Base.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} Returns false if the parent element has the "disabled" class, otherwise returns undefined.
 */
function importStandardFromKb(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	let $progress = $("#loading-indicator").show();
	$standardModal.find(".label-dander").remove();
	$.ajax({
		url: context + "/Analysis/Standard/Available",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		async: false,
		success: function (response) {
			let $content = $(new DOMParser().parseFromString(response, "text/html")).find("#importStandardTable");
			if (!$content.length) {
				showDialog("error", MessageResolver("error.unknown.occurred", "An unknown error occurred"));
				e.preventDefault();
			} else {
				let $tableStandard = $standardModal.find("#table_current_standard");
				$("#importStandardTable", $standardModal).replaceWith($content);
				$content.find("button.btn").on("click", function () {
					let $this = $(this), $tr = $this.closest("tr"), $tbodySource = $tr.closest("tbody");
					$progress.show();
					$.ajax({
						url: context + "/Analysis/Standard/Add/" + $tr.attr("data-trick-id"),
						type: "post",
						contentType: "application/json;charset=UTF-8",
						success: function (data) {
							if (data["error"] != undefined) {
								showDialog("error", data["error"]);
							} else if (data["success"] != undefined) {
								$tableStandard.find("tbody>tr:not([data-trick-id])").remove();
								$tr.find("td:last-child").remove();
								if ($("tbody>tr[data-trick-id='" + $tr.attr("data-trick-id") + "']", $tableStandard).length)
									$tr.remove();
								else
									$tr.appendTo($tableStandard.find("tbody")).find("td:hidden").show();
								$tr.on("click", (ev) => { selectElement(ev.currentTarget); });
								$("tr[data-trick-name='" + $tr.attr("data-trick-name") + "']", $tbodySource).remove();
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

/**
 * Reloads the standard table by making an AJAX request to the server.
 * 
 * @returns {boolean} Returns false to prevent the default form submission behavior.
 */
function reloadStandardTable() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Standard/Manage",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response) {
			let $table = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_standards table.table");
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


/**
 * Edits the standard based on the provided event.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns true if the standard was successfully edited, false otherwise.
 */
function editStandard(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	let $tr = $("#section_manage_standards tbody>tr[data-trick-analysisOnly='true'] :checked").closest("tr"), $form = $("#standard_form"), id = $tr.attr("data-trick-id");
	let label = $tr.find("td[data-name='label']").text(), description = $tr.find("td[data-name='description']").text(), type = $tr.attr("data-trick-type");
	let name = $tr.find("td[data-name='name']").text(), computable = $tr.attr("data-trick-computable");
	$("#standard_formId", $form).val(id);
	$("#standard_name", $form).val(name);
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


/**
 * Adds a new standard.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns true if the standard was added successfully, false otherwise.
 */
function addStandard(e) {
	if ($(e.currentTarget).parent().hasClass("disabled"))
		return false;
	let $form = $("#standard_form")
	$("#standard_formId", $form).prop("value", "0");
	$("#standard_name", $form).prop("value", "");
	$("#standard_label", $form).prop("value", "");
	$("#standard_version", $form).prop("value", "");
	$("#standard_description", $form).prop("value", "");
	$("input[name='type']", $form).prop("disabled", false);
	$("input[name='type'][value='NORMAL']", $form).prop("checked", "checked");
	$("#standard_computable", $form).prop("checked", "checked");
	return true;
}

/**
 * Saves the standard.
 * 
 * @param {Event} e - The event object.
 */
function saveStandard(e) {
	let $btn = $(e.currentTarget).prop("disabled", true), $progress = $("#loading-indicator").show(), $form = $("#standard_form");
	$(".label-danger,.alert-danger", $standardModal).remove();
	$.ajax({
		url: context + "/Analysis/Standard/Save",
		type: "post",
		data: serializeForm($form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			for (let error in response) {
				let $errorElement = $("<label class='label label-danger' />").text(response[error]);
				switch (error) {
					case "label":
						$errorElement.appendTo($form.find("#standard_label").parent());
						break;
					case "name":
						$errorElement.appendTo($form.find("#standard_name").parent());
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

/**
 * Removes a standard from the analysis.
 * 
 * @returns {boolean} Returns false if no standard is selected, otherwise returns true.
 */
function removeStandard() {
	let selectedStandard = $("#section_manage_standards :checked");
	if (selectedStandard.length != 1)
		return false;
	selectedStandard = findTrickID(selectedStandard[0]);
	$deleteModal.find(".modal-body").text(MessageResolver("confirm.delete.analysis.norm", "Are you sure, you want to remove this standard from this analysis?"));
	$deleteModal.find("#deletestandardbuttonYes").one("click.delete", function () {
		$(".label-danger", $standardModal).remove();
		let $progress = $("#loading-indicator").show();
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

/**
 * Exports the selected standard to a file.
 * 
 * @returns {boolean} Returns false if no standard is selected, otherwise returns the download link for the standard.
 */
function exportStandard() {
	if (isAnalysisOnlyStandard('#section_manage_standards')) {
		let selectedStandard = $("#section_manage_standards :checked");
		if (selectedStandard.length == 1)
			download(context + "/Analysis/Standard/Export/" + findTrickID(selectedStandard[0]));
	}
	return false
}

/**
 * Adds a measure for a given element and standard ID.
 * 
 * @param {HTMLElement} element - The element to add the measure to.
 * @param {number} idStandard - The ID of the standard.
 * @returns {boolean} - Returns true if the measure was successfully added, false otherwise.
 */
function addMeasure(element, idStandard) {
	if ($(element).parent().hasClass("disabled") || idStandard == undefined || idStandard == null || !$.isNumeric(idStandard))
		return false;
	return manageMeasure(context + "/Analysis/Standard/" + idStandard + "/Measure/New");
}

/**
 * Edits a measure.
 *
 * @param {HTMLElement} element - The element that triggered the edit action.
 * @param {number} idStandard - The ID of the standard.
 * @param {number} [idMeasure] - The ID of the measure (optional).
 * @returns {boolean} - Returns true if the measure was successfully edited, false otherwise.
 */
function editMeasure(element, idStandard, idMeasure) {
	if ($(element).parent().hasClass("disabled") || idStandard == undefined || idStandard == null || !$.isNumeric(idStandard))
		return false;
	if (idMeasure == null || idMeasure == undefined)
		idMeasure = findSelectItemIdBySection("section_standard_" + idStandard);
	if (idMeasure == null || idMeasure == undefined || !$.isNumeric(idStandard))
		return false;
	return manageMeasure(context + "/Analysis/Standard/Measure/" + idMeasure + "/Edit");
}

/**
 * Manages the measure by making an AJAX request to the specified URL.
 * @param {string} url - The URL to make the AJAX request to.
 * @returns {boolean} - Returns false to prevent the default form submission behavior.
 */
function manageMeasure(url) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: url,
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $content = $(new DOMParser().parseFromString(response, "text/html")).find("#modalMeasureForm");
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
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Sets up the measure manager.
 *
 * @param {jQuery} $content - The jQuery object representing the content.
 * @returns {jQuery} The updated jQuery object representing the content.
 */
function setupMeasureManager($content) {
	$content.find("#measure_form_tabs").tab();
	let $assetTab = $content.find("#tab_asset");
	if ($assetTab.length) {
		let onSelectedAsset = function (asset) {
			let $asset = $(asset), id = $asset.attr("data-trick-id");
			$('<input name="assets" value="' + id + '" hidden="hidden">').appendTo($asset);
			$asset.appendTo($assetTab.find("ul.asset-measure[data-trick-type='measure']")).attr("data-trick-selected", true);
			if (application.analysisType.isQuantitative()) {
				let $header = $('<th data-trick-class="MeasureAssetValue" data-trick-asset-id="' + id + '" >' + $(asset).text() + '</th>'), $data = $('<td data-trick-class="MeasureAssetValue" data-trick-asset-id="'
					+ id
					+ '" ><input type="range" id="asset_slider_'
					+ id
					+ '" value="0" min="0" max="100" step="1" value="0" name="property_asset_'
					+ id
					+ '" orient="vertical" selection="after" tooltip="show"></td>'), $value = $('<td data-trick-class="MeasureAssetValue"  data-trick-asset-id="'
						+ id
						+ '"><input type="text" id="property_asset_'
						+ id
						+ '_value" style="min-width: 50px;" readonly="readonly" class="form-control" value="0" name="'
						+ id
						+ '"></td>');

				$header.appendTo($content.find("#slidersTitle"));
				$data.appendTo($content.find("#sliders"));
				$value.appendTo($content.find("#values"));
				$data.find("input[type='range']").on('change', function (event) {
					event.currentTarget.title = event.target.value;
					$value.find("input").val(event.currentTarget.value);
				});
			}
		};

		let onDeselectedAsset = function (asset) {
			let $asset = $(asset);
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

		let updateAssetUI = function (selected) {
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
		$content.find("input[type='range']").each(function () {
			$(this).on("change", function (event) {
				event.currentTarget.title = event.target.value;
				$content.find("#values input[name='" + event.currentTarget.name + "']").val(event.target.value);
			});
		});
	}
	return $content;
}

/**
 * Saves a measure.
 *
 * @param {HTMLElement} form - The form element containing the measure data.
 * @param {Function} callback - The callback function to be executed after the measure is saved.
 * @returns {boolean} - Returns false to prevent the default form submission behavior.
 */
function saveMeasure(form, callback) {
	let data = {}, $form = $(form), $modalMeasureForm = $form.closest(".modal"), $genearal = $("#tab_general", $form), properties = $("#tab_properties #values", $form)
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

	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Standard/Measure/Save",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			for (let error in response.errors) {
				let $errorElement = $("<label class='label label-danger'/>").text(response.errors[error]);
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
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Deletes a measure from the standard.
 * If `measureId` is not provided, it deletes all selected measures from the standard.
 * 
 * @param {string} measureId - The ID of the measure to delete (optional).
 * @param {string} idStandard - The ID of the standard.
 * @returns {boolean} Returns false if `idStandard` is null or undefined, or if no measures are selected.
 */
function deleteMeasure(measureId, idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	let selectedMeasures = [];

	if (measureId == null || measureId == undefined) {
		selectedMeasures = findSelectItemIdBySection("section_standard_" + idStandard);
		if (!selectedMeasures.length)
			return false;
	} else
		selectedMeasures.push(measureId);

	let standard = $("#section_standard_" + idStandard).attr("data-trick-label");

	if (selectedMeasures.length == 1) {
		let measure = $("#section_standard_" + idStandard + " tr[data-trick-id='" + selectedMeasures[0] + "'] td:not(:first-child)");
		let reference = $(measure[0]).text();
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
		let $progress = $("#loading-indicator").show();
		let hasChange = false;
		$.ajax({
			url: context + "/Analysis/Standard/" + idStandard + "/Measure/Delete",
			type: "DELETE",
			data: JSON.stringify(selectedMeasures),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let ids = response["ids"];
				if (Array.isArray(ids)) {
					ids.forEach(function (idMeasure) {
						// Remove the measure from the table
						hasChange |= $("tr[data-trick-id='" + idMeasure + "']", "#section_standard_" + idStandard).remove().length > 0;
						forceUpdateMenu($("#section_standard_" + idStandard));
						removeFromMeasureNavigation(idStandard, idMeasure);
					});
				} else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					showDialog("#alert-dialog", MessageResolver("error.delete.measure.unkown", "Unknown error occoured while deleting the measure"));
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
			if (hasChange)
				reloadSection("section_standard_" + idStandard);
		});
		return false;
	});
	$("#confirm-dialog").modal("show");
	return false;
}



/**
 * Manages the SOA (Service-Oriented Architecture).
 * @returns {boolean} Returns false.
 */
function manageSOA() {
	let idAnalysis = 0;
	if (userCan(idAnalysis = findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Standard/SOA/Manage",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $content = $(new DOMParser().parseFromString(response, "text/html")).find("#manageSAOModel");
				if ($content.length) {
					if ($("#manageSAOModel").length)
						$("#manageSAOModel").replaceWith($content);
					else
						$content.appendTo($("#widgets"));
					$content.modal("show");
					$content.find("[name='save']").on("click", function () {
						let data = [];
						$content.find(".form-group[data-trick-id][data-default-value]").each(function () {
							let $this = $(this), newState = $this.find("input[type='radio']:checked").val(), oldState = $this.attr("data-default-value");
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
										calculateAction();
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

/**
 * Validates the SOA state for a given standard and measure.
 * @param {string} idStandard - The ID of the standard.
 * @param {string} idMeasure - The ID of the measure.
 * @returns {boolean} - Returns false.
 */
function validateSOAState(idStandard, idMeasure) {
	$("tr[data-trick-id='" + idMeasure + "']>td[data-trick-field!='soaReference'][data-trick-field]", "#table_SOA_" + idStandard).each(function () {
		let $this = $(this);
		if ($this.text().trim() == "")
			$this.addClass("warning");
		else
			$this.removeClass("warning");
	});
	return false;
}