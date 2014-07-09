function fixAllScenarioCategories() {
	$.ajax({
		url : context + "/Patch/Update/ScenarioCategoryValue",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick","location.reload();");
				$("#info-dialog").modal("toggle");
			}
			else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			else {
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

function fixMeasureMaintenance() {
	$.ajax({
		url : context + "/Patch/Update/MeasureMaintenance",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick","location.reload();");
				$("#info-dialog").modal("toggle");
			}
			else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			else {
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

function fixImplementationScaleParameterDescription() {
	$.ajax({
		url : context + "/Patch/Update/ParameterImplementationScale",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick","location.reload();");
				$("#info-dialog").modal("toggle");
			}
			else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			else {
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

function fixMaturityParameterStructure() {
	$.ajax({
		url : context + "/Patch/Update/ParameterMaturityILPS",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog .modal-footer button").attr("onclick","location.reload();");
				$("#info-dialog").modal("toggle");
			}
			else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			else {
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
				$("#info-dialog .modal-footer button").attr("onclick","location.reload();");
				$("#info-dialog").modal("toggle");
			}
			else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			else {
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