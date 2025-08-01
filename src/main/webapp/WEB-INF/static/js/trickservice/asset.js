
/**
 * Serializes the form data into an asset object.
 *
 * @param {jQuery} $form - The jQuery object representing the form.
 * @returns {Object} - The serialized asset object.
 */
function serializeAsset($form) {
	var data = $form.serializeJSON();
	data["assetType"] = {
		"id": parseInt(data["assetType"])
	};
	return data;
}

/**
 * Selects an asset and updates the corresponding analysis.
 * 
 * @param {string} assetId - The ID of the asset to be selected.
 * @param {string} value - The value to be assigned to the selected asset.
 * @returns {boolean} Returns false if the asset selection was not successful, otherwise returns true.
 */
function selectAsset(assetId, value) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (assetId == undefined) {
			var selectedItem = findSelectItemIdBySection("section_asset");
			if (!selectedItem.length)
				return false;
			var requiredUpdates = [];
			for (var i = 0; i < selectedItem.length; i++) {
				var selected = $("#section_asset tbody tr[data-trick-id='" + selectedItem[i] + "']").attr("data-trick-selected");
				if (value != selected)
					requiredUpdates.push(selectedItem[i]);
			}
			if (requiredUpdates.length) {
				var $progress = $("#loading-indicator").show();
				$.ajax({
					url: context + "/Analysis/Asset/Select",
					contentType: "application/json;charset=UTF-8",
					data: JSON.stringify(requiredUpdates, null, 2),
					type: 'post',
					success: function (reponse) {
						reloadSection('section_asset');
						updateEstimationSelect("asset", requiredUpdates, value);
						return false;
					},
					error: unknowError
				}).complete(() => $progress.hide());
			}
		} else {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/Analysis/Asset/Select/" + assetId,
				contentType: "application/json;charset=UTF-8",
				success: function (reponse) {
					reloadSection("section_asset");
					updateEstimationSelect("asset", [assetId], value);
					return false;
				},
				error: unknowError
			}).complete(() => $progress.hide());
		}
	}
	return false;
}

/**
 * Deletes an asset.
 * 
 * @returns {boolean} Returns false.
 */
function deleteAsset() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var selectedAsset = findSelectItemIdBySection("section_asset");
		if (!selectedAsset.length)
			return false;
		if (selectedAsset.length == 1) {
			var assetname = $("#section_asset tr[data-trick-id='" + selectedAsset[0] + "'] td:nth-child(3)").text();
			$("#confirm-dialog .modal-body").html(
				MessageResolver("confirm.delete.asset", "Are you sure, you want to delete the asset <b>" + assetname
					+ "</b>?<br/><b>ATTENTION:</b> This will delete all <b>Assessments</b> and complete <b>Action Plans</b> that depend on this asset!", assetname));
		} else
			$("#confirm-dialog .modal-body")
				.html(
					MessageResolver(
						"confirm.delete.selected.asset",
						"Are you sure, you want to delete the selected assets?<br/><b>ATTENTION:</b> This will delete all <b>Assessments</b> and complete <b>Action Plans</b> that depend on these assets!",
						assetname));
		$("#confirm-dialog .btn-danger").one("click", function () {
			$("#confirm-dialog").modal("hide");
			let $progress = $("#loading-indicator").show(), hasChange = false;
			$.ajax({
				url: context + "/Analysis/Asset/Delete",
				type: "DELETE",
				data: JSON.stringify(selectedAsset),
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					let deletedIds = response["ids"];
					if (Array.isArray(deletedIds)) {
						for (const assetId of deletedIds) {
							// Remove the asset from the table
							hasChange |= $("tr[data-trick-id='" + assetId + "']", "#section_asset").remove().length > 0;
							// Remove the estimation for the asset
							removeEstimation("asset", [assetId]);
						}
					}
					else if (response["error"] != undefined)
						showDialog("#alert-dialog", response["error"]);
					else
						showDialog("#alert-dialog", MessageResolver("error.delete.asset.unkown", "Unknown error occoured while deleting the asset"));
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
				if (hasChange)
					reloadSection("section_asset");
			});
		});
		$("#confirm-dialog").modal("show");
	}
	return false;

}

/**
 * Edits an asset.
 * 
 * @param {number} rowTrickId - The ID of the row trick.
 * @param {boolean} isAdding - Indicates whether the asset is being added.
 * @returns {boolean} - Returns false if the asset cannot be edited, otherwise returns true.
 */
function editAsset(rowTrickId, isAdding) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (!isAdding && (rowTrickId == null || rowTrickId == undefined)) {
			var selectedScenario = $("#section_asset :checked");
			if (selectedScenario.length != 1)
				return false;
			rowTrickId = findTrickID(selectedScenario[0]);
		}
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + ((rowTrickId == null || rowTrickId == undefined || rowTrickId < 1) ? "/Analysis/Asset/Add" : "/Analysis/Asset/Edit/" + rowTrickId),
			async: true,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $addAssetModal = $(new DOMParser().parseFromString(response, "text/html")).find("#addAssetModal");
				if ($addAssetModal.length)
					$addAssetModal.appendTo("#widgets").modal("show").on("hidden.bs.modal", (e) => $addAssetModal.remove());
				else
					unknowError();
				return false;
			},
			error: unknowError
		}).complete(() => $progress.hide());
	}
	return false;
}

/**
 * Saves an asset.
 *
 * @param {string} form - The ID of the form containing the asset data.
 * @returns {boolean} - Returns false to prevent the default form submission.
 */
function saveAsset(form) {
	let $progress = $("#loading-indicator").show(), $assetModal = $("#addAssetModal"), $form = $("#" + form), data = serializeAsset($form);
	$(".label-danger,.alert", $assetModal).remove();
	$.ajax({
		url: context + "/Analysis/Asset/Save",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			for (let error in response.errors) {
				if (error != "asset") {
					let $errorElement = $("<label class='label label-danger'/>").text(response.errors[error]);
					switch (error) {
						case "name":
							$errorElement.appendTo($("#asset_name", $form).parent());
							break;
						case "assetType":
							$errorElement.appendTo($("#asset_assettype_id", $form).parent());
							break;
						case "value":
							$errorElement.appendTo($("#asset_value", $form).parent().parent());
							break;
						case "selected":
							$errorElement.appendTo($("#asset_selected", $form).parent());
							break;
						case "comment":
							$errorElement.appendTo($("#asset_comment", $form).parent());
							break;
						case "hiddenComment":
							$errorElement.appendTo($("#asset_hiddenComment", $form).parent());
							break;
						case "relatedName":
							$errorElement.appendTo($("#asset_relatedName", $form).parent());
							break;

					}
				} else
					showError($form.parent()[0], response.errors[error]);

			}
			if (!$(".label-danger,.alert", "#addAssetModal").length) {
				$assetModal.modal("hide");
				reloadSection("section_asset");
				data.id = response.id;
				data['type'] = data["assetType"].id;
				updateEstimationIteam("asset", data);
			}
		},
		error: function (jqXHR, textStatus, errorThrown) {
			$("<label class='label label-danger'>").text(MessageResolver("error.unknown.add.asset", "An unknown error occurred during adding asset")).appendTo($(".modal-body", $assetModal));
		}
	}).complete(() => $progress.hide());
	return false;
}
