/* Asset */
function serializeAsset($form) {
	var data = $form.serializeJSON();
	data["assetType"] = {
		"id" : parseInt(data["assetType"]),
		"type" : $("#asset_assettype_id option:selected").text()
	};
	data["selected"] = data["selected"] == "on";
	return data;
}

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
					url : context + "/Analysis/Asset/Select",
					contentType : "application/json;charset=UTF-8",
					data : JSON.stringify(requiredUpdates, null, 2),
					type : 'post',
					success : function(reponse) {
						reloadSection('section_asset');
						updateEstimationSelect("asset",requiredUpdates,value);
						return false;
					},
					error : unknowError
				}).complete(function() {
					$progress.hide();
				});
			}
		} else {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Asset/Select/" + assetId,
				contentType : "application/json;charset=UTF-8",
				success : function(reponse) {
					reloadSection("section_asset");
					updateEstimationSelect("asset",[assetId],value);
					return false;
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		}
	}
	return false;
}

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
		$("#confirm-dialog .btn-danger").one("click", function() {
			var $progress = $("#loading-indicator").show(), hasChange = false;
			while (selectedAsset.length) {
				var assetId = selectedAsset.pop();
				$.ajax({
					url : context + "/Analysis/Asset/Delete/" + assetId,
					type : "POST",
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
						if (response["success"] != undefined){
							hasChange |= $("tr[data-trick-id='" + assetId + "']", "#section_asset").remove().length > 0;
							removeEstimation("asset",[assetId]);
						}
						else if (response["error"] != undefined)
							showDialog("#alert-dialog", response["error"]);
						else
							showDialog("#alert-dialog", MessageResolver("error.delete.asset.unkown", "Unknown error occoured while deleting the asset"));
					},
					error : unknowError
				}).complete(function() {
					if (!selectedAsset.length) {
						if (hasChange)
							reloadSection("section_asset");
						else
							$progress.hide();
					}
				});
			}

		});
		$("#confirm-dialog").modal("toggle");
	}
	return false;

}

function editAsset(rowTrickId, isAdd) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (isAdd) {
			var selectedScenario = $("#section_asset :checked");
			if (selectedScenario.length != 0)
				return false;
			rowTrickId = undefined;
		} else if (rowTrickId == null || rowTrickId == undefined) {
			var selectedScenario = $("#section_asset :checked");
			if (selectedScenario.length != 1)
				return false;
			rowTrickId = findTrickID(selectedScenario[0]);
		}

		var $progress = $("#loading-indicator").show();

		$.ajax({
			url : context + ((rowTrickId == null || rowTrickId == undefined || rowTrickId < 1) ? "/Analysis/Asset/Add" : "/Analysis/Asset/Edit/" + rowTrickId),
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $addAssetModal = $(new DOMParser().parseFromString(response, "text/html")).find("#addAssetModal");
				if ($addAssetModal.length) {
					$("#addAssetModal").replaceWith($addAssetModal);
					$("#addAssetModal").modal("toggle");
				} else
					unknowError();
				return false;
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}

function saveAsset(form) {
	var $progress = $("#loading-indicator").show(), $assetModal = $("#addAssetModal"), $form = $("#"+form), data = serializeAsset($form);
	$(".label-danger,.alert",$assetModal).remove();
	$.ajax({
		url : context + "/Analysis/Asset/Save",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			for ( var error in response.errors) {
				if (error != "asset") {
					var $errorElement = $("<label class='label label-danger'/>").text(response.errors[error]);
					switch (error) {
					case "name":
						$errorElement.appendTo($("#asset_name",$form).parent());
						break;
					case "assetType":
						$errorElement.appendTo($("#asset_assettype_id",$form).parent());
						break;
					case "value":
						$errorElement.appendTo($("#asset_value",$form).parent());
						break;
					case "selected":
						$errorElement.appendTo($("#asset_selected",$form).parent());
						break;
					case "comment":
						$errorElement.appendTo($("#asset_comment",$form).parent());
						break;
					case "hiddenComment":
						$errorElement.appendTo($("#asset_hiddenComment",$form).parent());
						break;
					}
				} else
					showError($form.parent()[0], response.errors[error]);

			}
			if (!$(".label-danger,.alert","#addAssetModal").length) {
				$assetModal.modal("hide");
				reloadSection("section_asset");
				data.id = response.id;
				data['type'] = data["assetType"].type;
				updateEstimationIteam("asset",data);
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("<label class='label label-danger'>").text(MessageResolver("error.unknown.add.asset", "An unknown error occurred during adding asset")).appendTo($(".modal-body",$assetModal));
		}
	}).complete(function() {
		$progress.hide();
	});
	return false;
}
