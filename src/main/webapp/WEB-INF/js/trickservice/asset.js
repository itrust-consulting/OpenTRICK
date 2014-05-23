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
			var selectedScenario = $("#section_asset :checked");
			if (selectedScenario.length != 1)
				return false;
			assetId = findTrickID(selectedScenario[0]);
	}

	var assetname = $("#section_asset tr[trick-id='" + assetId + "'] td:nth-child(3)").text();

	$("#confirm-dialog .modal-body").html(MessageResolver("confirm.delete.asset", "Are you sure, you want to delete the asset ")+ "\"<b>"+assetname+"\"</b>?<br/><b>ATTENTION:</b> This will delete all <b>Assessments</b> and complete <b>Action Plans</b> that depend on this asset!");
	$("#confirm-dialog .btn-danger").click(function() {
		$.ajax({
			url : context + "/Asset/Delete/" + assetId,
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["success"] != undefined) {
					var row = $("#section_asset tr[trick-id='" + assetId + "']");
					var checked = $("#section_asset tr[trick-id='" + assetId + "'] :checked");
					if (checked.length)
						$(checked).removeAttr("checked");
					if (row.length)
						$(row).remove();
					reloadSection("section_actionplans");
					reloadSection("section_summary");
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else {
					$("#alert-dialog .modal-body").html(MessageResolver("error.delete.asset.unkown", "Unknown error occoured while deleting the asset"));
					$("#alert-dialog").modal("toggle");
				}
				
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
			var addAssetModal = $(doc).find("* div#addAssetModal");
			if ($("#addAssetModal").length)
				$("#addAssetModal").html($(addAssetModal).html());
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
			var alert = $("#addAssetModal .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
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
			}
			if (!$("#addAssetModal .label-danger").length) {
				$("#addAssetModal").modal("toggle");

				var id = $("#" + form + " input[name='id']").attr("value");
				setTimeout(reloadAsset(id), 10);

			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			var alert = $("#addAssetModal .label-danger");
			if (alert.length)
				alert.remove();
			var errorElement = document.createElement("label");
			errorElement.setAttribute("class", "label label-danger");
			$(errorElement).text(MessageResolver("error.unknown.add.asset", "An unknown error occurred during adding asset"));
			$(errorElement).appendTo($("#addAssetModal .modal-body"));
			return false;
		},
	});
}

function reloadAsset(id) {
	if (id == -1) {
		reloadSection("section_asset");
		return false;
	}
	$.ajax({
		url : context + "/Asset/SingleAsset/" + id,
		type : "get",
		dataType : "html",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {

			var element = document.createElement("div");

			$(element).html(response);

			if ($(element).find("tr[trick-id='" + id + "']").length) {
				var replacedValue = $(element).find("td:nth-child(5)").text();

				if (replacedValue == "")
					return;

				var replaced = false;

				$("#assetTable tr td:nth-child(5)").each(function() {
					
					var thistrickid = $(this).parent().attr("trick-id");
					
					if (id == thistrickid) {
						$(this).parent().remove();
					}
				});
				
				var tmprow = undefined;
				
				$("#assetTable tr td:nth-child(5)").each(function() {
						
				
					var thisValue = $(this).text();

					if(parseInt(replacedValue) > parseInt(thisValue)) {
					
						// the asset to replace has bigger value than the
						// current

						if (!replaced) {

							// get tr element
							var parent = $(this).parent().get(0);

							// add the row to before the current
							$(parent).before($(element).find("tr"));

							replaced = true;
						}
						
					} else {
					
					tmprow = $(this).parent().get(0);
					
					}
					
				});

				if(tmprow != undefined)
					$(tmprow).after($(element).find("tr"));
				
				$("#assetTable tr td:nth-child(2)").each(function(i, obj) {
				
					$(obj).text(i+1);
					
				});
				
				
			}
		}
	});
	return false;
}