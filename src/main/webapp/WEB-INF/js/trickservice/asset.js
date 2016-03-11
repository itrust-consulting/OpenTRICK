/* Asset */
function serializeAssetForm(formId) {
	var form = $("#" + formId), data = form.serializeJSON();
	data["assetType"] = {
		"id" : parseInt(data["assetType"]),
		"type" : $("#asset_assettype_id option:selected").text()
	};
	data["selected"] = data["selected"] == "on";
	return JSON.stringify(data);
}

function selectAsset(assetId, value) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		if (assetId == undefined) {
			var selectedItem = findSelectItemIdBySection("section_asset");
			if (!selectedItem.length)
				return false;
			var requiredUpdate = [];
			for (var i = 0; i < selectedItem.length; i++) {
				var selected = $("#section_asset tbody tr[data-trick-id='" + selectedItem[i] + "']").attr("data-trick-selected");
				if (value != selected)
					requiredUpdate.push(selectedItem[i]);
			}
			if (requiredUpdate.length) {
				$.ajax({
					url : context + "/Analysis/Asset/Select",
					contentType : "application/json;charset=UTF-8",
					data : JSON.stringify(requiredUpdate, null, 2),
					type : 'post',
					async : false,
					success : function(reponse) {
						reloadSection('section_asset');
						return false;
					},
					error : unknowError
				});
			}
		} else {
			$.ajax({
				url : context + "/Analysis/Asset/Select/" + assetId,
				async : false,
				contentType : "application/json;charset=UTF-8",
				success : function(reponse) {
					reloadSection("section_asset");
					return false;
				},
				error : unknowError
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
		$("#confirm-dialog .btn-danger").click(function() {
			while (selectedAsset.length) {
				var assetId = selectedAsset.pop();
				$.ajax({
					url : context + "/Analysis/Asset/Delete/" + assetId,
					type : "POST",
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
						if (response["success"] != undefined)
							reloadSection("section_asset");
						else if (response["error"] != undefined) {
							$("#alert-dialog .modal-body").text(response["error"]);
							$("#alert-dialog").modal("toggle");
						} else {
							$("#alert-dialog .modal-body").text(MessageResolver("error.delete.asset.unkown", "Unknown error occoured while deleting the asset"));
							$("#alert-dialog").modal("toggle");
						}
						return false;
					},
					error : unknowError
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
		});
	}
	return false;
}

function saveAsset(form) {
	$.ajax({
		url : context + "/Analysis/Asset/Save",
		type : "post",
		data : serializeAssetForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#addAssetModal .label-danger,#addAssetModal .alert").remove();
			for ( var error in response) {
				if (error != "asset") {
					var errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");
					$(errorElement).text(response[error]);
					switch (error) {
					case "name":
						$(errorElement).appendTo($("#asset_form #asset_name").parent());
						break;
					case "assetType":
						$(errorElement).appendTo($("#asset_form #asset_assettype_id").parent());
						break;
					case "value":
						$(errorElement).appendTo($("#asset_form #asset_value").parent());
						break;
					case "selected":
						$(errorElement).appendTo($("#asset_form #asset_selected").parent());
						break;
					case "comment":
						$(errorElement).appendTo($("#asset_form #asset_comment").parent());
						break;
					case "hiddenComment":
						$(errorElement).appendTo($("#asset_form #asset_hiddenComment").parent());
						break;
					}
				} else
					showError($("#asset_form").parent()[0], response[error]);

			}
			if (!$("#addAssetModal .label-danger, #addAssetModal .alert").length) {
				$("#addAssetModal").modal("hide");
				reloadSection("section_asset");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("#addAssetModal .label-danger").remove();
			var errorElement = document.createElement("label");
			errorElement.setAttribute("class", "label label-danger");
			$(errorElement).text(MessageResolver("error.unknown.add.asset", "An unknown error occurred during adding asset"));
			$(errorElement).appendTo($("#addAssetModal .modal-body"));
			return false;
		},
	});
	return false;
}
