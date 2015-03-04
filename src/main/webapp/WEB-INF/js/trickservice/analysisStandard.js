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
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	var locale = $("#nav-container").attr("data-trick-language");
	var alert = $("#standardModal .alert");
	if (alert.length)
		alert.remove();
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Standard/Manage",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(response, textStatus, jqXHR) {
				if (response["error"] != undefined)
					showError($("#standardModal .modal-body")[0], response["error"]);
				else {
					var forms = $(new DOMParser().parseFromString(response, "text/html")).find("#section_manage_standards");
					if (!forms.length)
						showError($("#standardModal .modal-footer")[0], MessageResolver("error.unknown.load.data", "An unknown error occurred during loading data", null, locale));
					else
						$("#section_manage_standards").replaceWith(forms);
				}
				$("#standardModal").modal("show");
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

	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {

		var alert = $("#createStandardModal .label-danger");
		if (alert.length)
			alert.remove();

		var locale = $("#nav-container").attr("data-trick-language");

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
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
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

		var idItem = $(selectedItem).attr("data-trick-id");

		var idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
		if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {

			var alert = $("#createStandardModal .label-danger");
			if (alert.length)
				alert.remove();

			var locale = $("#nav-container").attr("data-trick-language");

			var label = $(selectedItem).find("td:nth-child(2)").text();
			var description = $(selectedItem).find("td:nth-child(4)").text();
			var type = $(selectedItem).attr("data-trick-type");
			var computable = $(selectedItem).attr("data-trick-computable");

			$("#createStandardModal #createstandardbutton").prop("disabled", false);
			$("#createStandardModal #standard_label").prop("value", label);
			$("#createStandardModal #standard_description").prop("value", description);
			$("#createStandardModal #standard_form input[name='type']").removeProp("checked");
			$("#createStandardModal #standard_form input[name='type'][value='" + type + "']").prop("checked", true);
			if (computable === "true")
				$("#createStandardModal #standard_computable").prop("checked", true);
			else
				$("#createStandardModal #standard_computable").removeProp("checked");

			$("#createStandardModal #createstandardtitle").text(MessageResolver("label.title.analysis.manage_standard.edit", "Edit standard", null, locale));
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
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
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
							var $table = new DOMParser().parseFromString(response, "text/html").find("#section_manage_standards table.table");
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

	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
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

						var lang = $("#nav-container").attr("data-trick-language");

						text += '</select></div><div class="col-sm-2">';
						text += '<button type="button" class="btn btn-primary" onclick="return doAddStandard(\'addStandardModal\');">'
								+ MessageResolver("label.action.add", "add", null, lang) + '</button></div>';

						$("#addStandardModal .modal-body").html(text);

					} else {

						var lang = $("#nav-container").attr("data-trick-language");
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
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
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

	var lang = $("#nav-container").attr("data-trick-language");
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

function addMeasure(idStandard) {
	if (idStandard == undefined || idStandard == null) {
		idStandard = $(".tab-pane.active").attr("data-trick-id");
		if (idStandard == undefined || idStandard == null)
			return false;
	}
	$.ajax({
		url : context + "/Analysis/Standard/" + idStandard + "/Measure/New",
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
						$assetTab.find("li[data-trick-type='" + selected + "']").show();
						$assetTab.find("li[data-trick-type!='" + selected + "']").hide();
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
			} else
				unknowError()
		},
		error : unknowError
	});
	return false;
}

function newAssetMeasure(idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	var selectedItem = $("#section_standard_" + idStandard + " tbody :checked");
	if (selectedItem.length != 0)
		return false;

	$("#manageAssetMeasureModel #assetTabs li").removeClass("active");
	$("#manageAssetMeasureModel #assetTabs li a#group_1").parent().addClass("active");

	var lang = $("#nav-container").attr("data-trick-language");

	$("#manageAssetMeasureModel #addMeasureModel-title").text(MessageResolver("title.measure.add", "Add a new Measure", null, lang));

	$.ajax({
		url : context + "/Analysis/Standard/" + idStandard + "/AssetMeasure/New",
		type : "get",
		async : false,
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $form = $(new DOMParser().parseFromString(response, "text/html")).find("form#manageAssetMeasure_form");
			if ($form.length)
				$("#manageAssetMeasureModel #manageAssetMeasure_form").replaceWith($form)
			else
				unknowError()
		},
		error : unknowError
	});

	$("#manageAssetMeasureModel #assetTabs a").click(function() {

		$("#manageAssetMeasureModel #assetTabs li").removeClass("active");
		$(this).parent().addClass("active");
		var location = $(this).attr("id");
		$("#manageAssetMeasureModel #manageAssetMeasure_form div[id^='group_']").css("display", "none");
		$("#manageAssetMeasureModel #manageAssetMeasure_form div[id='" + location + "']").css("display", "block");
		return false;
	});

	if ($("#manageAssetMeasure_form #measure_computable:checked").length == 1)
		$("#assetTabs li:nth-child(2)").css("display", "block");
	else
		$("#assetTabs li:nth-child(2)").css("display", "none");

	$('#manageAssetMeasure_form #measure_computable').change(function() {
		if (this.checked) {
			$("#assetTabs li:nth-child(2)").css("display", "block");
			$("#manageAssetMeasure_form div#group_3 [data-trick-class='MeasureAssetValue']").css("display", "");
		} else {
			$("#assetTabs li:nth-child(2)").css("display", "none");
			$("#manageAssetMeasure_form div#group_3 [data-trick-class='MeasureAssetValue']").css("display", "none");
		}
	});

	$("#manageAssetMeasure_form #group_2 li").each(function() {
		$(this).click(function(event) {
			manageAssetLiClick($(this));
		});
	});
	$("#manageAssetMeasure_form div#group_3 .slider").slider().each(function() {
		$(this).on("slideStop", function(event) {
			var field = event.target.name;
			var fieldValue = event.value;

			$("#manageAssetMeasure_form div#group_3 input[id='measure_" + field + "_value']").attr("value", fieldValue);

		});
	});

	$("#manageAssetMeasureModel").modal("show");
	initialiseAssetmanagement();
	return false;
}

function manageAssetLiClick(event) {
	var asset = $(event).attr("opt");
	var assetname = $(event).text();
	if ($(event).parent().attr("data-trick-type") == 'available') {
		$("select#availableAssets option[value='" + asset + "']").clone().appendTo("select#measureAssets");
		$("select#availableAssets option[value='" + asset + "']").remove();
		$(event).clone().appendTo("ul[data-trick-type='measure']");
		$("li[opt='" + asset + "']").click(function(ev) {
			manageAssetLiClick($(this));
		});
		$(event).remove();

		var assetexists = $("#group_3 #tableheaderrow th[data-trick-class='MeasureAssetValue'][data-trick-name='" + assetname + "']");
		if (assetexists.length == 0) {
			$("#group_3 #tableheaderrow").append('<th data-trick-class="MeasureAssetValue" data-trick-name="' + assetname + '">' + assetname + "</th>");
			$("#group_3 #tablesliderrow").append(
					'<td data-trick-class="MeasureAssetValue"><input type="text" class="slider" id="measure_' + assetname
							+ '" value="0" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="0" name="' + assetname
							+ '" data-slider-orientation="vertical" data-slider-selection="after" data-slider-tooltip="show"></td>');
			$("#manageAssetMeasure_form div#group_3 input[id='measure_" + assetname + "']").slider().on("slideStop", function(event) {
				var field = event.target.name;
				var fieldValue = event.value;
				$("#manageAssetMeasure_form div#group_3 input[id='measure_" + field + "_value']").attr("value", fieldValue);
			});
			$("#group_3 #tabledatarow").append(
					'<td data-trick-class="MeasureAssetValue"><input type="text" name="' + assetname
							+ '" value="0" class="form-control" readonly="readonly" style="min-width: 50px;" id="measure_' + assetname + '_value"></td>');
		}

	} else if ($(event).parent().attr("data-trick-type") == 'measure') {
		$("select#measureAssets option[value='" + asset + "']").clone().appendTo("select#availableAssets");
		$("select#measureAssets option[value='" + asset + "']").remove();
		$(event).clone().appendTo("ul[data-trick-type='available']");
		$("li[opt='" + asset + "']").click(function(ev) {
			manageAssetLiClick($(this));
		});
		$(event).remove();

		var assetexists = $("#group_3 #tableheaderrow th[data-trick-class='MeasureAssetValue'][data-trick-name='" + assetname + "']");
		if (assetexists.length == 1) {
			$("#group_3 #tableheaderrow th[data-trick-class='MeasureAssetValue'][data-trick-name='" + assetname + "']").remove();
			$("#group_3 #tablesliderrow input[id='measure_" + assetname + "']").closest("td[data-trick-class='MeasureAssetValue']").remove();
			$("#group_3 #tabledatarow td[data-trick-class='MeasureAssetValue'] input[id='measure_" + assetname + "_value']").closest("td").remove();
		}

	}

	verifyListItemDesign();
}

function editSingleMeasure(measureId, idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	if (measureId == null || measureId == undefined)
		measureId = $("#section_standard_" + idStandard + " tbody :checked").parent().parent();
	else {
		measureId = $("#section_standard_" + idStandard + " tr[data-trick-id='" + measureId + "']");
	}
	if (measureId.length != 1)
		return false;

	var alert = $("#addMeasureModel .label-danger");
	if (alert.length)
		alert.remove();

	var measure = $(measureId).find("td:not(:first-child)");

	$("#addMeasureModel #measure_id").prop("value", $(measureId).attr("data-trick-id"));
	$("#addMeasureModel #measure_reference").prop("value", $(measure[0]).text());
	$("#addMeasureModel #measure_level").prop("value", $(measureId).attr("data-trick-level"));
	$("#addMeasureModel #measure_computable").prop("checked", $(measureId).attr("data-trick-computable") === "true");

	$("#addMeasureModel #measure_form").prop("action", context + "/Analysis/Standard/" + idStandard + "/Measure/Save");

	var lang = $("#nav-container").attr("data-trick-language");

	$("#addMeasureModel #addMeasureModel-title").text(MessageResolver("title.knowledgebase.measure.update", "Update Measure", null, lang));
	$("#addMeasureModel #addmeasurebutton").text(MessageResolver("label.action.edit", "Update", null, lang));

	$("#addMeasureModel #addmeasurebutton").attr("onclick", "saveMeasure('" + idStandard + "')");

	var domain = $(measure[1]).text();

	var description = $(measureId).attr("data-trick-description") == undefined ? '' : $(measureId).attr("data-trick-description");

	var text = '<div style="display: block;"><div class="form-group"><label class="col-sm-2 control-label" for="domain">'
			+ MessageResolver("label.measure.domain", "Domain", null, lang) + '</label><div class="col-sm-10"><input type="text" class="form-control" id="measure_domain" value="'
			+ domain + '" name="domain"></div></div>';
	text = text + '<div class="form-group"><label class="col-sm-2 control-label" for="description">' + MessageResolver("label.measure.description", "Description", null, lang)
			+ '</label><div class="col-sm-10"><textarea class="form-control" id="measure_description" name="description">' + description + '</textarea></div></div></div>';

	$("#addMeasureModel #measurelanguages").html(text);

	$("#addMeasureModel").modal("show");

	return false;
}

function editAssetMeasure(idMeasure, idStandard) {

	if (idStandard == null || idStandard == undefined)
		return false;

	if (idMeasure == null || idMeasure == undefined) {
		selectedScenario = findSelectItemIdBySection("section_standard_" + idStandard);

		if (selectedScenario.length == 1)
			idMeasure = selectedScenario[0];
		else
			return false;
	}

	$("#manageAssetMeasureModel #assetTabs li").removeClass("active");
	$("#manageAssetMeasureModel #assetTabs li a#group_1").parent().addClass("active");

	var lang = $("#nav-container").attr("data-trick-language");

	$("#manageAssetMeasureModel #addMeasureModel-title").text(MessageResolver("title.measure.edit", "Edit Measure", null, lang));

	$.ajax({
		url : context + "/Analysis/Standard/" + idStandard + "/AssetMeasure/" + idMeasure + "/Edit",
		type : "get",
		async : false,
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var $form = $(new DOMParser().parseFromString(response, "text/html")).find("#manageAssetMeasure_form");
			if ($form.length)
				$("#manageAssetMeasureModel #manageAssetMeasure_form").replaceWith($form)
			else
				unknowError();
		},
		error : unknowError
	});

	$("#manageAssetMeasureModel #assetTabs a").click(function() {

		$("#manageAssetMeasureModel #assetTabs li").removeClass("active");
		$(this).parent().addClass("active");
		var location = $(this).attr("id");
		$("#manageAssetMeasureModel #manageAssetMeasure_form div[id^='group_']").css("display", "none");
		$("#manageAssetMeasureModel #manageAssetMeasure_form div[id='" + location + "']").css("display", "block");
		return false;
	});

	if ($("#manageAssetMeasure_form #measure_computable:checked").length == 1)
		$("#assetTabs li:nth-child(2)").css("display", "block");
	else
		$("#assetTabs li:nth-child(2)").css("display", "none");

	$('#manageAssetMeasure_form #measure_computable').change(function() {
		if (this.checked) {
			$("#assetTabs li:nth-child(2)").css("display", "block");
			$("#manageAssetMeasure_form div#group_3 [data-trick-class='MeasureAssetValue']").css("display", "");
		} else {
			$("#assetTabs li:nth-child(2)").css("display", "none");
			$("#manageAssetMeasure_form div#group_3 [data-trick-class='MeasureAssetValue']").css("display", "none");
		}
	});

	$("#manageAssetMeasure_form #group_2 li").each(function() {
		$(this).click(function(event) {
			manageAssetLiClick($(this));
		});
	});
	$("#manageAssetMeasure_form div#group_3 .slider").slider().each(function() {
		$(this).on("slideStop", function(event) {
			var field = event.target.name;
			var fieldValue = event.value;

			$("#manageAssetMeasure_form div#group_3 input[id='measure_" + field + "_value']").attr("value", fieldValue);

		});
	});

	$("#manageAssetMeasureModel").modal("show");
	initialiseAssetmanagement();
	return false;
}

function saveMeasure(idStandard) {
	var form = $("#modalMeasureForm #measure_form");
	var data = $("#modalMeasureForm #measure_form #tab_general").serializeJSON();
	var properties = $("#modalMeasureForm #measure_form #tab_properties #values").serializeJSON();
	var $assetTab = $("#modalMeasureForm #measure_form #tab_asset");
	if ($assetTab.length) {
		data.type = "ASSET";
		data.assetValues = [];
		$assetTab.find("li>input[name='assets']").each(function() {
			data.assetValues.push({
				id : this.value,
				value : properties[this.value],
			});
			delete properties[this.value];
		});
	} else
		data.type = "NORMAL";
	data.properties = properties;
	data.computable = data.computable === "on";

	$.ajax({
		url : context + "/Analysis/Standard/Measure/Save",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var alert = $("#modalMeasureForm").find(".label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "reference":
					$(errorElement).appendTo(form.find("#reference").parent());
					break;
				case "level":
					$(errorElement).appendTo(form.find("#level").parent());
					break;
				case "computable":
					$(errorElement).appendTo(form.find("#computable").parent());
					break;
				case "domain":
					$(errorElement).appendTo(form.find("#domain").parent());
					break;
				case "description":
					$(errorElement).appendTo(form.find("#description").parent());
					break;
				case "norm":
				case "measureDescription":
					$(errorElement).appendTo(form.parent());
					break;
				}
			}
			if (!$("#modalMeasureForm").find(".label-danger").length) {
				$("#modalMeasureForm").modal("hide");
				reloadSection("section_standard_" + idStandard);
			}
			return false;
		},
		error : unknowError
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
		var selectedScenario = findSelectItemIdBySection("section_standard_" + standardid);
		if (!selectedScenario.length)
			return false;
	}

	var lang = $("#nav-container").attr("data-trick-language");

	var standard = $("#section_standard_" + standardid + " #menu_standard_" + standardid + " li:first-child").text();

	if (selectedScenario.length == 1) {
		measureId = selectedScenario[0];
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

		while (selectedScenario.length) {

			if (errors)
				break;

			rowTrickId = selectedScenario.pop();

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

function saveAssetMeasure(form) {

	var idStandard = $(form + " #standard_id").val();

	if (idStandard == null || idStandard == undefined)
		return false;

	var idMeasure = $(form + " #measure_id").val();

	var data = {};

	var categories = {};

	var measureassetvalues = {};

	var measureProperties = {};

	$(form).find("input:not(.slider)").each(function() {
		var name = $(this).attr("name");
		var value = $(this).prop("value");
		if (value == null || value == undefined)
			if ($(this).attr("type") == "checkbox")
				value = this.checked ? "on" : "";
			else
				value = "";
		var trickclass = $(this).parent().attr("data-trick-class");
		if (trickclass == undefined || trickclass == null)
			data[name] = value;
		else {
			if (trickclass === "Category")
				categories[name] = value;
			else if (trickclass === "MeasureAssetValue")
				measureassetvalues[name] = value;
			else if (trickclass === "MeasureProperties")
				measureProperties[name] = value;
		}

	});

	var text = $(form).find("textarea[name='description']").val();

	data['description'] = text;

	data["properties"] = measureProperties;

	measureProperties["categories"] = categories;

	data["measureassetvalues"] = measureassetvalues;

	var jsonarray = JSON.stringify(data);

	$.ajax({
		url : context + "/Analysis/Standard/" + idStandard + "/AssetMeasure/Save",
		async : true,
		type : "post",
		data : jsonarray,
		contentType : "application/json",
		success : function(response, textStatus, jqXHR) {
			var alert = $("#manageAssetMeasure_form").find(".label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "reference":
					$(errorElement).appendTo($("#manageAssetMeasure_form input#measure_reference").parent());
					break;
				case "level":
					$(errorElement).appendTo($("#manageAssetMeasure_form #measure_level").parent());
					break;
				case "computable":
					$(errorElement).appendTo($("#manageAssetMeasure_form #measure_computable").parent());
					break;
				case "domain":
					$(errorElement).appendTo($("#manageAssetMeasure_form #measure_domain").parent());
					break;
				case "description":
					$(errorElement).appendTo($("#manageAssetMeasure_form #measure_description").parent());
					break;
				default:
					if (error === "success") {
						reloadSection("section_standard_" + idStandard);
						$("#manageAssetMeasureModel").modal("hide");
					} else {
						$("#alert-dialog .modal-body").text(response[error]);
						$("#alert-dialog").modal("toggle");
					}
					break;
				}
			}
		},
		error : unknowError
	});
}

function initialiseAssetmanagement() {
	verifyListItemDesign();
	$("#group_2 #assettypes").change(function() {
		var type = $("option:selected", this).attr("data-trick-type");
		$("#group_2 ul[data-trick-type='available'] li").css("display", "none");
		$("#group_2 ul[data-trick-type='available'] li[data-trick-type='" + type + "']").css("display", "block");
		$("#group_2 ul[data-trick-type='measure'] li").css("display", "none");
		$("#group_2 ul[data-trick-type='measure'] li[data-trick-type='" + type + "']").css("display", "block");
		verifyListItemDesign();
	});
}
function verifyListItemDesign() {

	var first = null;

	var last = null;

	$("#group_2 ul[data-trick-type='available'] li").each(function() {
		$(this).css("border", "1px solid #dddddd");
		$(this).css('border-top-left-radius', '0px');
		$(this).css('border-top-right-radius', '0px');
		$(this).css('border-bottom-left-radius', '0px');
		$(this).css('border-bottom-right-radius', '0px');
		$(this).css('margin-bottom', '-1px');

		if ($(this).css("display") === "block" && first == null)
			first = $(this);
		if ($(this).css("display") === "block")
			last = $(this);
	});

	$(first).css('border-top-left-radius', '4px');
	$(first).css('border-top-right-radius', '4px');

	$(last).css('border-bottom-left-radius', '4px');
	$(last).css('border-bottom-right-radius', '4px');
	$(last).css("margin-bottom", "0");

	first = null;

	last = null;

	$("#group_2 ul[data-trick-type='measure'] li").each(function() {
		$(this).css("border", "1px solid #dddddd");
		$(this).css('border-top-left-radius', '0px');
		$(this).css('border-top-right-radius', '0px');
		$(this).css('border-bottom-left-radius', '0px');
		$(this).css('border-bottom-right-radius', '0px');
		$(this).css('margin-bottom', '-1px');

		if ($(this).css("display") === "block" && first == null)
			first = $(this);
		if ($(this).css("display") === "block")
			last = $(this);
	});

	$(first).css('border-top-left-radius', '4px');
	$(first).css('border-top-right-radius', '4px');

	$(last).css('border-bottom-left-radius', '4px');
	$(last).css('border-bottom-right-radius', '4px');
	$(last).css("margin-bottom", "0");

}
