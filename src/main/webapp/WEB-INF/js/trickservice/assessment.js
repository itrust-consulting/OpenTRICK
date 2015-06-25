AssessmentViewer.prototype = new Modal();

function AssessmentViewer() {

	AssessmentViewer.prototype.FixHeader = function(container) {
		/*var instance = this;
		setTimeout(function(){
			$("table", instance.modal_body).stickyTableHeaders({
				cssTopOffset : container,
				fixedOffset : application.fixedOffset
			});
		},500);*/
	}
	
	AssessmentViewer.prototype.Intialise = function() {
		Modal.prototype.Intialise.call(this);
		$(this.modal_dialog).css({"width": "100%"});
		var lang = $("#nav-container").attr("data-trick-language");

		var impactScale = MessageResolver("label.menu.show.impact_scale", "Show impact scale", null, lang);
		var probabilityScale = MessageResolver("label.menu.show.probability_scale", "Show probability scale", null, lang);
		var dynamicParametersList = MessageResolver("label.menu.show.dynamic_parameters_list", "List of dynamic parameters", null, lang);
		if (application.isReadOnly !== true) {
			var enableEditModeText = MessageResolver("label.menu.edit_mode.open", "Open edit mode", null, lang);
			var disableEditModeText = MessageResolver("label.menu.edit_mode.close", "Close edit mode", null, lang);
			$(this.modal_title).replaceWith(
					$("<div class='modal-title'><h4 role='title' class=''></h4><ul class='nav nav-pills'><li role='impact_scale'><a href='#'>" + impactScale
							+ "</a></li><li role='probability_scale'><a href='#'>" + probabilityScale
							+ "</a></li><li role='dynamic_parameters_list'><a href='#'>" + dynamicParametersList
							+ "</a></li><li role='enterEditMode'><a href='#' onclick='return enableEditMode()'>" + enableEditModeText
							+ "</a></li><li class='disabled' role='leaveEditMode'><a href='#' onclick='return disableEditMode()'>" + disableEditModeText + "</a></li><ul></div>"));
		} else {
			$(this.modal_title).replaceWith(
					$("<div class='modal-title'><h4 role='title' class=''></h4><ul class='nav nav-pills'><li role='impact_scale'><a href='#'>" + impactScale
							+ "</a></li><li role='probability_scale'><a href='#'>" + probabilityScale + "</a></li><ul></div>"));
		}

		$(this.modal_footer).remove();
		this.dialogError = $("#alert-dialog").clone();
		$(this.dialogError).removeAttr("id");
		$(this.dialogError).appendTo($(this.modal));
		this.setTitle("Assessment");

		$(this.modal).on("hidden.bs.modal", function() {
			disableEditMode();
			reloadSection("section_asset");// it will call reloadSection for
			// scenario
		});

		$(this.modal_header).find("*[role='impact_scale']").on("click", function() {
			var view = new Modal();
			view.Intialise();
			$(view.modal_footer).remove();
			view.setTitle(MessageResolver("label.title.impact_scale", "Impact scale", null, lang));
			view.setBody($("#Scale_Impact .panel-body").html());
			$(view.modal_body).find("td").removeAttributes();
			view.Show();
			return false;
		});

		$(this.modal_header).find("*[role='probability_scale']").on("click", function() {
			var view = new Modal();
			view.Intialise();
			$(view.modal_footer).remove();
			view.setTitle(MessageResolver("label.title.probability_scale", "Probability scale", null, lang));
			view.setBody($("#Scale_Probability .panel-body").html());
			$(view.modal_body).find("td").removeAttributes();
			view.Show();
			return false;
		});

		$(this.modal_header).find("*[role='dynamic_parameters_list']").on("click", function() {
			var view = new Modal();
			view.Intialise();
			$(view.modal_footer).remove();
			view.setTitle(MessageResolver("label.parameter.dynamic.probability", "Dynamic probability parameters", null, lang));
			view.setBody($("#DynamicParameters .panel-body").html());
			$(view.modal_body).find("td").removeAttributes();
			view.Show();
			return false;
		});

		var that = this;
		var resizer = function() {
			var height = $(window).height();
			var multi = height < 200 ? 0.50 : height < 520 ? 0.60 : height < 600 ? 0.65 : height < 770 ? 0.72 : height < 820 ? 0.76 : height < 900 ? 0.77 : 0.79;
			$(that.modal_body).css({
				'max-height' : (height * multi) + 'px',
				'overflow' : 'auto'
			});
		}

		$(window).on('resize.assessment', resizer);
		resizer.apply(resizer, null);
		$(this.modal).on("hidden.bs.modal", function() {
			application.modal["AssessmentViewer"] = undefined;
			$(window).off('resize.assessment', resizer)
		});

		$(this.modal).find(".modal-content").css({
			'padding-bottom' : '20px'
		});

		$(this.modal).on("show.bs.modal", function() {
			disableEditMode();
		});

		return false;

	};

	AssessmentViewer.prototype.setTitle = function(title) {
		var $modalTile = $(this.modal_header).find("*[role='title']");
		if ($modalTile.length) {
			$modalTile.text(title);
			return false;
		}
		return true;
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

	AssessmentViewer.prototype.SmartUpdate = function(assessments) {
		var tableDestTrs = $(this.modal_body).find("tbody tr");
		if (!(tableDestTrs.length && $(tableDestTrs[0]).find("td").length == $(assessments).find("tbody>tr:first>td").length))
			return true;
		for (var i = 0; i < tableDestTrs.length; i++) {
			var trickId = $(tableDestTrs[i]).attr("data-trick-id");
			if (trickId == undefined && $(tableDestTrs[i]).hasClass("panel-footer")) {
				var $tr = $(assessments).find("tbody tr.panel-footer");
				if ($tr.length)
					$(tableDestTrs[i]).replaceWith($tr);
				else
					$(tableDestTrs[i]).appendTo($(this.modal_body).find("tbody"));
			} else {
				var $tr = $(assessments).find("tbody tr[data-trick-id='" + trickId + "']");
				if (!$tr.length)
					$(tableDestTrs[i]).remove();
				else
					$(tableDestTrs[i]).replaceWith($tr);
			}
		}
		var $tbody = $(this.modal_body).find("tbody");
		var $footer = $(this.modal_body).find("tbody tr.panel-footer");
		if (!$footer.length) {
			$footer = $(assessments).find("tbody tr.panel-footer");
			if ($footer.length)
				$footer.appendTo($tbody);
		}
		var tableSourceTrs = $(assessments).find("tbody tr[data-trick-id]");
		for (var i = 0; i < tableSourceTrs.length; i++) {
			var trickId = $(tableSourceTrs[i]).attr("data-trick-id");
			var $tr = $(this.modal_body).find("tbody tr[data-trick-id='" + trickId + "']");
			if (!$tr.length) {
				if ($footer.length)
					$tr.before($footer);
				else
					$tr.appendTo($tbody);
			}
		}
		return false;
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

	AssessmentViewer.call(this);

	this.assetId = assetId;

	AssessmentAssetViewer.prototype.Load = function(callback) {
		if (this.modal_body == null)
			this.Intialise();
		var instance = this;
		$.ajax({
			url : context + "/Analysis/Assessment/Asset/" + instance.assetId,
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("#section_asset_assessment");
				if (assessments.length) {
					$(instance.modal_body).html($(assessments).html());
					instance.setTitle($(assessments).attr("data-trick-name"));
					instance.FixHeader("#section_asset_assessment .modal-body");
					if (callback != null && $.isFunction(callback))
						return callback();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
		return this;
	};

	AssessmentAssetViewer.prototype.Update = function() {
		var instance = this;
		return $.ajax({
			url : context + "/Analysis/Assessment/Asset/" + instance.assetId + "/Update",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("#section_asset_assessment");
				if (assessments.length) {
					if (instance.SmartUpdate.apply(instance, assessments)) {
						instance.setBody(assessments);
						instance.setTitle($(assessments).attr("data-trick-name"));
						instance.FixHeader("#section_asset_assessment .modal-body");
					}
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	};
}

AssessmentScenarioViewer.prototype = new AssessmentViewer();

function AssessmentScenarioViewer(scenarioId) {

	AssessmentViewer.call(this)

	this.scenarioId = scenarioId;

	AssessmentScenarioViewer.prototype.Load = function(callback) {
		if (this.modal_body == null)
			this.Intialise();
		var instance = this;
		return $.ajax({
			url : context + "/Analysis/Assessment/Scenario/" + instance.scenarioId,
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var assessments = $("#section_scenario_assessment",new DOMParser().parseFromString(reponse, "text/html"));
				if (assessments.length) {
					$(instance.modal_body).html($(assessments).html());
					instance.setTitle($(assessments).attr("data-trick-name"));
					instance.FixHeader("#section_scenario_assessment .modal-body");
					if (callback != null && $.isFunction(callback))
						return callback();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	};

	AssessmentScenarioViewer.prototype.Update = function() {
		var instance = this;
		return $.ajax({
			url : context + "/Analysis/Assessment/Scenario/" + instance.scenarioId + "/Update",
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(reponse, "text/html");
				var assessments = $(doc).find("*[id='section_scenario_assessment']");
				if (assessments.length) {
					if (instance.SmartUpdate.apply(instance, assessments)) {
						instance.setBody(assessments);
						instance.setTitle($(assessments).attr("data-trick-name"));
						instance.FixHeader("#section_scenario_assessment .modal-body");
					}
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	};
}

function displayAssessmentByScenario() {
	var selectedItem = findSelectItemIdBySection("section_scenario");
	if (selectedItem.length != 1 || !isSelected("scenario"))
		return false;
	application.modal["AssessmentViewer"] = new AssessmentScenarioViewer(selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
	return false;
}

function displayAssessmentByAsset() {
	var selectedItem = findSelectItemIdBySection("section_asset");
	if (selectedItem.length != 1 || !isSelected("asset"))
		return false;
	application.modal["AssessmentViewer"] = new AssessmentAssetViewer(selectedItem[0]);
	application.modal["AssessmentViewer"].Show();
	return false;
}

function computeAssessment(silent) {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Assessment/Update",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response['error'] != undefined) {
					$("#info-dialog .modal-body").text(response['error']);
					$("#info-dialog").modal("toggle");
				} else if (response['success'] != undefined) {
					if (!silent) {
						$("#info-dialog .modal-body").text(response['success']);
						$("#info-dialog").modal("toggle");
					}
					chartALE();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function refreshAssessment() {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#confirm-dialog .modal-body").html(MessageResolver("confirm.refresh.assessment", "Are you sure, you want to rebuild all assessments"));
		$("#confirm-dialog .btn-danger").click(function() {
			$.ajax({
				url : context + "/Analysis/Assessment/Refresh",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					if (response['error'] != undefined) {
						$("#info-dialog .modal-body").text(response['error']);
						$("#info-dialog").modal("toggle");
					} else if (response['success'] != undefined) {
						$("#info-dialog .modal-body").text(response['success']);
						$("#info-dialog").modal("toggle");
						chartALE();
					} else
						unknowError();
					return false;
				},
				error : unknowError
			});
		});
		$("#confirm-dialog").modal("show");
	} else
		permissionError();
	return false;
}

function updateAssessmentAle(silent) {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Assessment/Update/ALE",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response['error'] != undefined) {
					$("#info-dialog .modal-body").text(response['error']);
					$("#info-dialog").modal("toggle");
				} else if (response['success'] != undefined) {
					if (!silent) {
						$("#info-dialog .modal-body").text(response['success']);
						$("#info-dialog").modal("toggle");
					}
					reloadSection("section_asset");
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	} else
		permissionError();

	return false;
}
