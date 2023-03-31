function fixAllScenarioCategories() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Patch/Update/ScenarioCategoryValue",
		contentType: "application/json;charset=UTF-8",
		type: 'POST',
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				showDialog("success", response["success"]);
				setTimeout(() => location.reload(), 5000);
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else 
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		}
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function updateMeasureAssetTypeValue() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Patch/Update/Measure/MeasureAssetTypeValues",
		contentType: "application/json;charset=UTF-8",
		type: 'POST',
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				showDialog("success", response["success"]);
				setTimeout(() => location.reload(), 5000);
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function restoreAnalysisRights() {
	var $confirmDialog = $("#confirm-dialog");
	$confirmDialog.find('.modal-body').text(MessageResolver("confirm.restore.analysis.right", "Are you sure, you want to restore analysis rights?"));
	$confirmDialog.find("button[name='yes']").one("click", function () {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Patch/Restore/Analysis/Right",
			contentType: "application/json;charset=UTF-8",
			type: 'POST',
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					showDialog("success", response["success"]);
					setTimeout(() => location.reload(), 5000);
				} else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else 
					showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			}
		}).complete(function () {
			$progress.hide();
		});
		$confirmDialog.modal("hide");
	});
	$confirmDialog.modal("show");
	return false;
}

function updateAnalysesScopes() {
	var $confirmDialog = $("#confirm-dialog");
	$confirmDialog.find('.modal-body').text(MessageResolver("confirm.update.analyses.scopes", "Are you sure, you want to update missing scopes of analyses?"));
	$confirmDialog.find("button[name='yes']").one("click", function () {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Patch/Update/Analyses/Scopes",
			contentType: "application/json;charset=UTF-8",
			type: 'POST',
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					showDialog("success", response["success"]);
					setTimeout(() => location.reload(), 5000);
				} else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else 
					showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			}
		}).complete(function () {
			$progress.hide();
		});
		$confirmDialog.modal("hide");
	});
	$confirmDialog.modal("show");
	return false;
}

function updateAnalysesRiskAndItemInformation() {
	var $confirmDialog = $("#confirm-dialog");
	$confirmDialog.find('.modal-body').text(MessageResolver("confirm.update.analyses.risk_item.information", "Are you sure, you want to update missing risk and item information of analyses?"));
	$confirmDialog.find("button[name='yes']").one("click", function () {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Patch/Update/Analyses/Risk-item-information",
			contentType: "application/json;charset=UTF-8",
			type: 'POST',
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					showDialog("success", response["success"]);
					setTimeout(() => location.reload(), 5000);
				} else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else 
					showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			}
		}).complete(function () {
			$progress.hide();
		});
		$confirmDialog.modal("hide");
	});
	$confirmDialog.modal("show");
	return false;
}

function fixAllAssessments() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Patch/Update/Assessments",
		contentType: "application/json;charset=UTF-8",
		type: 'POST',
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				showDialog("success", response["success"]);
				setTimeout(() => location.reload(), 5000);
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else 
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		}
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function addCSSFParameters() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Patch/Add-CSSF-Parameters",
		contentType: "application/json;charset=UTF-8",
		type: 'POST',
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				showDialog("success", response["success"]);
				setTimeout(() => location.reload(), 5000);
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else 
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		}
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function fixQualitativeImpactParameter() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Patch/Fix-qualitative-impact-parameter",
		contentType: "application/json;charset=UTF-8",
		type: 'POST',
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined) {
				showDialog("success", response["success"]);
				setTimeout(() => location.reload(), 5000);
			} else if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else 
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		},
		error: function (jqXHR, textStatus, errorThrown) {
			showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
		}
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function synchroniseAnalysesMeasureCollection() {
	var $confirmDialog = $("#confirm-dialog");
	$confirmDialog.find('.modal-body').text(MessageResolver("confirm.synchronise.analyses.measure.collection", "Are you sure, you want to synchronise.analyses.measure.collection?"));
	$confirmDialog.find("button[name='yes']").one("click", function () {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Patch/Synchronise/Analyses/Measure-collection",
			contentType: "application/json;charset=UTF-8",
			type: 'POST',
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					showDialog("success", response["success"]);
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else 
					showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			},
			error: function (jqXHR, textStatus, errorThrown) {
				showDialog("#alert-dialog",MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
			}
		}).complete(function () {
			$progress.hide();
		});
		$confirmDialog.modal("hide");
	});
	$confirmDialog.modal("show");
	return false;
}
