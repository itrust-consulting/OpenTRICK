function fixAllScenarioCategories() {
	$.ajax({
		url : context + "/Patch/Update/ScenarioCategoryValue",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick", "location.reload();");
				$("#info-dialog").modal("toggle");
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			} else {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			$("#alert-dialog").modal("toggle");
		}
	});
	return false;
}

function updateMeasureAssetTypeValue() {
	$.ajax({
		url : context + "/Patch/Update/Measure/MeasureAssetTypeValues",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick", "location.reload();");
				$("#info-dialog").modal("toggle");
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function restoreAnalysisRights() {
	var $confirmDialog = $("#confirm-dialog");
	$confirmDialog.find('.modal-body').text(MessageResolver("confirm.restore.analysis.right", "Are you sure, you want to restore analysis rights?"));
	$confirmDialog.find("button[name='yes']").click(function() {
		$(this).unbind();
		$.ajax({
			url : context + "/Patch/Restore/Analysis/Right",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application['taskManager'].Start();
				else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("show");
				} else {
					$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
					$("#alert-dialog").modal("show");
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("show");
			}
		}).complete(function() {
			$confirmDialog.modal("hide");
		});
	});
	$confirmDialog.modal("show");
	return false;
}

function updateAnalysesScopes() {
	var $confirmDialog = $("#confirm-dialog");
	$confirmDialog.find('.modal-body').text(MessageResolver("confirm.update.analyses.scopes", "Are you sure, you want to update scopes of analyses?"));
	$confirmDialog.find("button[name='yes']").click(function() {
		$(this).unbind();
		$.ajax({
			url : context + "/Patch/Update/Analyses/Scopes",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if(response["success"] != undefined){
					$("#info-dialog .modal-body").text(response["success"]);
					$("#info-dialog").modal("show");
				}
				else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("show");
				} else {
					$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
					$("#alert-dialog").modal("show");
				}
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("show");
			}
		}).complete(function() {
			$confirmDialog.modal("hide");
		});
	});
	$confirmDialog.modal("show");
	return false;
}

function fixAllAssessments() {
	$.ajax({
		url : context + "/Patch/Update/Assessments",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick", "location.reload();");
				$("#info-dialog").modal("toggle");
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			} else {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			$("#alert-dialog").modal("toggle");
		}
	});
}