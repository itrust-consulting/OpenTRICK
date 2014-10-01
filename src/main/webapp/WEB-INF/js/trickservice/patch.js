function fixAllScenarioCategories() {
	$.ajax({
		url : context + "/Patch/Update/ScenarioCategoryValue",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
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

function fixMeasureAssetTypeValue() {
	$.ajax({
		url : context + "/Patch/Update/Measure/AssettypeValue",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
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

function fixImplementationScaleParameterDescription() {
	$.ajax({
		url : context + "/Patch/Update/ParameterImplementationScale",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
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

function fixAllAssessments() {
	$.ajax({
		url : context + "/Patch/Update/Assessments",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
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

function fixSummaryCompliance() {
	$.ajax({
		url : context + "/Patch/Update/UpdateCompliances",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
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