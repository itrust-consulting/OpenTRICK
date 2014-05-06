/* Asset */
function serializeAssetForm(formId) {
	var form = $("#" + formId);
	var data = form.serializeJSON();
	data["assetType"] = {
		"id" : parseInt(data["assetType"]),
		"type" : $("#asset_assettype_id option:selected").text()
	};
	data["value"] = parseFloat(data["value"]);
	data["selected"] = data["selected"] == "on";
	return JSON.stringify(data);
}

function selectAsset(assetId, value) {
	if (assetId == undefined) {
		var selectedItem = findSelectItemIdBySection("section_asset");
		if (!selectedItem.length)
			return false;
		var requiredUpdate = [];
		for (var i = 0; i < selectedItem.length; i++) {
			var selected = $("#section_asset tbody tr[trick-id='" + selectedItem[i] + "']").attr("trick-selected");
			if (value != selected)
				requiredUpdate.push(selectedItem[i]);
		}
		$.ajax({
			url : context + "/Asset/Select",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(requiredUpdate, null, 2),
			type : 'post',
			success : function(reponse) {
				reloadSection('section_asset');
				return false;
			}
		});
	} else {
		$.ajax({
			url : context + "/Asset/Select/" + assetId,
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				reloadSection("section_asset");
				return false;
			}
		});
	}
	return false;
}

function deleteAsset(assetId) {
	if (assetId == null || assetId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_asset"));
		if (!selectedScenario.length)
			return false;
		while (selectedScenario.length) {
			rowTrickId = selectedScenario.pop();
			$.ajax({
				url : context + "/Asset/Delete/" + rowTrickId,
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response) {
					var trickSelect = parseJson(response);
					if (trickSelect != undefined && trickSelect["success"] != undefined) {
						var row = $("#section_asset tr[trick-id='" + rowTrickId + "']");
						var checked = $("#section_asset tr[trick-id='" + rowTrickId + "'] :checked");
						if (checked.length)
							$(checked).removeAttr("checked");
						if (row.length)
							$(row).remove();
					}
					return false;
				}
			});
		}
		setTimeout("reloadSection('section_asset')", 100);
		return false;
	}

	$("#confirm-dialog .modal-body").text(MessageResolver("confirm.delete.asset", "Are you sure, you want to delete this asset"));
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Asset/Delete/" + assetId,
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(reponse) {
				reloadSection("section_asset");
				return false;
			}
		});
	});
	$("#confirm-dialog").modal("toggle");
	return false;

}

function editAsset(rowTrickId, isAdd) {
	if (isAdd)
		rowTrickId = undefined;
	else if (rowTrickId == null || rowTrickId == undefined) {
		var selectedScenario = $("#section_asset :checked");
		if (selectedScenario.length != 1)
			return false;
		rowTrickId = findTrickID(selectedScenario[0]);
	}
	$.ajax({
		url : context + ((rowTrickId == null || rowTrickId == undefined || rowTrickId < 1) ? "/Asset/Add" : "/Asset/Edit/" + rowTrickId),
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			if ((addAssetModal = doc.getElementById("addAssetModal")) == null)
				return false;
			if ($("#addAssetModal").length)
				$("#addAssetModal").html($(addAssetModal).html());
			else
				$(addAssetModal).appendTo($("#widget"));
			$('#addAssetModal').on('hidden.bs.modal', function() {
				$('#addAssetModal').remove();
			});
			$("#addAssetModal").modal("toggle");
			return false;
		}
	});
	return false;
}

function saveAsset(form) {
	return $.ajax({
		url : context + "/Asset/Save",
		type : "post",
		async : true,
		data : serializeAssetForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var previewError = $("#addAssetModal .alert");
			if (previewError.length)
				previewError.remove();
			var data = "";
			for ( var error in response)
				data += response[error][1] + "\n";
			result = data == "" ? true : showError(document.getElementById(form), data);
			if (result) {
				$("#addAssetModal").modal("hide");
				reloadSection("section_asset");
			}
			return result;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return result;
		},
	});
}