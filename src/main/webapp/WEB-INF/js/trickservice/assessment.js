AssessmentViewer.prototype = new Modal();

function AssessmentViewer() {

	AssessmentViewer.prototype.Intialise = function() {
		Modal.prototype.Intialise.call(this);
		$(this.modal_dialog).prop("style", "width: 95%; min-width:1170px;");
		$(this.modal_footer).hide();
		this.dialogError = $("#alert-dialog").clone();
		$(this.dialogError).removeAttr("id");
		$(this.dialogError).appendTo($(this.modal));
		return false;

	};

	AssessmentViewer.prototype.DefaultFooterButton = function() {
		return false;
	};

	AssessmentViewer.prototype.Show = function() {
		var instance = this;
		return this.Load(function() {
			return Modal.prototype.Show.call(instance);
		});
	};

	AssessmentViewer.prototype.Load = function(callback) {
		throw "Not implemented";
	};

	AssessmentViewer.prototype.Update = function() {
		throw "Not implemented";
	};

	AssessmentViewer.prototype.ShowError = function(message) {
		var error = $('<div class="alert alert-danger alert-dismissable">' + message
				+ '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button></div>');
		error.attr("style", "margin-bottom: 0px;");
		$(error).appendTo(this.modal_title);
		setTimeout(function() {
			$(error).remove();
		}, 5000);
		return false;
	};
}

/**
 * Class AssessmentAssetViewer
 */
AssessmentAssetViewer.prototype = new AssessmentViewer();

function AssessmentAssetViewer(assetId) {
	this.assetId = assetId;

	AssessmentAssetViewer.prototype.constructor = AssessmentAssetViewer;

	AssessmentAssetViewer.prototype.Load = function(callback) {
		if (this.modal_body == null)
			this.Intialise();
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Asset/" + instance.assetId,
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_asset_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				if (callback != null && $.isFunction(callback))
					return callback();
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};

	AssessmentAssetViewer.prototype.Update = function() {
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Asset/" + instance.assetId + "/Update",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_asset_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};
}

AssessmentScenarioViewer.prototype = new AssessmentViewer();

function AssessmentScenarioViewer(scenarioId) {
	this.scenarioId = scenarioId;

	AssessmentScenarioViewer.prototype.constructor = AssessmentScenarioViewer;

	AssessmentScenarioViewer.prototype.Load = function(callback) {
		if (this.modal_body == null)
			this.Intialise();
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Scenario/" + instance.scenarioId,
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_scenario_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				if (callback != null && $.isFunction(callback))
					return callback();
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};

	AssessmentScenarioViewer.prototype.Update = function() {
		var instance = this;
		return $.ajax({
			url : context + "/Assessment/Scenario/" + instance.scenarioId + "/Update",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_scenario_assessment']");
				if (!assessments.length)
					return true;
				$(instance.modal_body).html($(assessments).html());
				instance.setTitle($(assessments).attr("trick-name"));
				return false;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				return true;
			}
		});
	};
}

function displayAssessmentByScenario() {
	var selectedItem = findSelectItemIdBySection("section_scenario");
	if (selectedItem.length != 1)
		return false;
	application.modal["AssessmentViewer"] = new AssessmentScenarioViewer(selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
	return false;
}

function displayAssessmentByAsset() {

	var selectedItem = findSelectItemIdBySection("section_asset");
	if (selectedItem.length != 1)
		return false;
	application.modal["AssessmentViewer"] = new AssessmentAssetViewer(selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
	return false;
}

function updateAssessmentAcronym(idParameter, acronym) {
	$.ajax({
		url : context + "/Assessment/Update/Acronym/" + idParameter + "/" + acronym,
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response["success"] != undefined) {
				$("#info-dialog .modal-body").html(response["success"]);
				$("#info-dialog").modal("toggle");
				setTimeout("updateALE()", 2000);
			} else if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			return true;
		}
	});
	return false;
}

function computeAssessment() {
	$.ajax({
		url : context + "/Assessment/Update",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response['error'] != undefined) {
				$("#info-dialog .modal-body").text(response['error']);
				$("#info-dialog").modal("toggle");
			} else if (response['success'] != undefined) {
				$("#info-dialog .modal-body").text(response['success']);
				$("#info-dialog").modal("toggle");
				chartALE();
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			return false;
		},
	});
	return false;
}

function wipeAssessment() {
	$.ajax({
		url : context + "/Assessment/Wipe",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		async : true,
		success : function(response) {
			if (response['error'] != undefined) {
				$("#info-dialog .modal-body").text(response['error']);
				$("#info-dialog").modal("toggle");
			} else if (response['success'] != undefined) {
				$("#info-dialog .modal-body").text(response['success']);
				$("#info-dialog").modal("toggle");
			}
			return false;
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			return false;
		},
	});
	return false;
}