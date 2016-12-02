var activeSelector = undefined, helper = undefined, scales = [], standardCaching = undefined;

function removeMeasure() {
	var $tr = $(this).closest("tr[data-trick-id]"), id = $tr.attr('data-trick-id');
	$tr.find("td[data-toggle='tooltip']").tooltip("destroy");
	standardCaching.removeMeasure(id);
	$tr.remove();
}

function saveAssessmentData(e) {
	var $assessmentUI = $("#estimation-ui"), $target = $(e.currentTarget), value = $target.val(), idScenario = $assessmentUI.attr('data-trick-scenario-id'), idAsset = $assessmentUI
		.attr('data-trick-asset-id'), name = $target.attr('name'), type = $target.attr('data-trick-type'), oldValue = $target.hasAttr("placeholder") ? $target
			.attr("placeholder") : $target.attr("data-trick-value");
	if (value == oldValue)
		$target.parent().removeClass('has-error').removeAttr("title");
	else {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/EditField/Estimation/Update?asset=" + idAsset + "&scenario=" + idScenario,
			type: "post",
			data: '{"id":' + idAsset + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				if (response.message == undefined)
					unknowError();
				else {
					var $parent = $target.parent();
					if (response.error)
						$parent.addClass("has-error").removeClass("has-success").attr("title", response.message);
					else {
						$parent.removeAttr("title").removeClass("has-error");
						if ($target.attr("readonly"))
							$target.removeClass("has-success");
						else
							$parent.addClass("has-success");
						var updated = false;
						for (var i = 0; i < response.fields.length; i++) {
							var field = response.fields[i], $element = $target;
							if (name == field.name)
								updated = true;
							else {
								$element = $("[name='" + field.name + "'].form-control", $assessmentUI);
								if (field.name == "computedNextImportance") {
									$element.attr("placeholder", field.value).val(field.value);
									continue;
								}
							}
							for (var fieldName in field) {
								switch (fieldName) {
									case "value":
										$element.attr("placeholder", field[fieldName]).val(field[fieldName]);
										break;
									case "title":
										$element.attr(fieldName, field[fieldName]);
										break;
								}
							}
						}
						if (!updated) {
							$target.attr($target.hasAttr("placeholder") ? "placeholder" : "data-trick-value", $target.val());
							if ($target.hasAttr("list"))
								$target.attr("title", $("option[value='" + value + "']", "#" + $target.attr("list")).attr("title"));
							else if ($target.tagName = 'SELECT')
								$target.attr("title", $target.find("option:selected").attr('title'));
						}
						
						if (application.analysisType == "QUALITATIVE")
							reloadSection("section_riskregister", undefined, true);
					}
				}
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

function toggleAdditionalActionPlan(e) {
	var $additional = $("textarea[name='riskProfile.actionPlan']");
	if(!$additional.length)
		return false;
	if (e == undefined) {
		if (helper['toggleAdditionalActionPlan'] == undefined){
			helper['toggleAdditionalActionPlan'] = $additional.is(":visible") ? 'show' : 'hide';
		}
		else {
			var $show = $("#measureManagementAdvance a[data-action='show']").parent(), $hide = $("#measureManagementAdvance a[data-action='hide']").parent();
			if (helper['toggleAdditionalActionPlan'] == 'hide') {
				$additional.hide();
				$show.show();
				$hide.hide();
			} else {
				$additional.css({ "display": "inline-block" });
				$show.hide();
				$hide.show();
			}
		}
	} else {
		$("#measureManagementAdvance a[data-action][data-action!='manage']").parent().toggle();
		if (helper["toggleAdditionalActionPlan"] == 'hide') {
			$additional.css({ "display": "inline-block" });
			helper["toggleAdditionalActionPlan"] = 'show';
		}
		else {
			$additional.css({ "display": "none" });
			helper["toggleAdditionalActionPlan"] = 'hide';
		}
	}
}

function loadAssessmentData(id) {
	var $currentUI = $("div[data-view='estimation-ui']"), idAsset = -3, idScenario = -3, url = context + "/Analysis/Assessment/", invalidate = helper !=undefined && helper.invalidate;
	if (activeSelector == "asset") {
		idAsset = $("select[name='asset']").val();
		url += "Asset/" + idAsset + "/Load?idScenario=" + (idScenario = id);
	} else {
		idScenario = $("select[name='scenario']").val();
		url += "Scenario/" + idScenario + "/Load?idAsset=" + (idAsset = id);
	}
	
	if (idAsset == -1 && idScenario == -1 || !invalidate && $currentUI.attr("data-trick-asset-id") == idAsset && $currentUI.attr("data-trick-scenario-id") == idScenario
		&& $currentUI.attr("data-trick-content") == activeSelector)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: url,
		contentType: "application/json;charset=UTF-8",
		success: function (response) {
			var $assessmentUI = $("div[data-view='estimation-ui']", new DOMParser().parseFromString(response, "text/html"));
			if ($assessmentUI.length) {
				$currentUI.replaceWith($assessmentUI);
				if (OPEN_MODE.isReadOnly()) {
					$("select:not([disabled])", $assessmentUI).prop("disabled", true);
					$("input:not([disabled]),textarea:not([disabled])", $assessmentUI).attr("readOnly", true);
					$("a[data-controller]", $assessmentUI).remove();
					$("#measureManagementAdvance a[data-action='manage']", $assessmentUI).remove();
				} else {
					$("select", $assessmentUI).on("change", saveAssessmentData);
					$("textarea,input:not([disabled])", $assessmentUI).on("blur", saveAssessmentData);
					$("a[data-controller]", $assessmentUI).on("click", function () {
						var $description = $("#description", $assessmentUI), $control = $("i.fa", this);
						if ($control.hasClass("fa-lock")) {
							$control.removeClass("fa-lock").addClass("fa-unlock")
							$description.removeAttr("readonly");
						} else {
							$control.removeClass("fa-unlock").addClass("fa-lock")
							$description.attr("readonly", true).parent().removeAttr("title").removeClass("has-success has-error");
						}
						return false;
					});

					$("#measureManagementAdvance a[data-action='manage']", $assessmentUI).on("click", function (e) {
						forceCloseToolTips();
						manageRiskProfileMeasure(idAsset, idScenario, e);
					});
				}

				$("#measureManagementAdvance a[data-action][data-action!='manage']", $assessmentUI).on("click", toggleAdditionalActionPlan)
				
				toggleAdditionalActionPlan();

				$('[data-toggle="tooltip"]', $assessmentUI).tooltip().on('show.bs.tooltip', toggleToolTip);

				$("button[data-scale-modal]", $assessmentUI).on("click", function () {
					forceCloseToolTips();
					var name = this.getAttribute("data-scale-modal");
					if (scales[name] == undefined)
						scales[name] = $("#" + name);
					scales[name].modal("show");
				});

			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		if (idAsset == -1 || idScenario == -1)
			fixTableHeader(".table-fixed-header-analysis");
		$progress.hide()
	});
	return false;
}


function backupDescriptionHeight() {
	var $description = $("#description");
	if ($description.length) {
		var height = $description.outerHeight(), defaultHeight = application["estimation-description-default-size"];
		if (defaultHeight != undefined && Math.abs(height - defaultHeight) > 8) {
			application["estimation-description-size-prev"] = application["estimation-description-size"];
			application["estimation-description-size"] = $description.outerHeight();
		} else if (application["estimation-description-size"] && application["estimation-description-size"] != height && application["estimation-description-size-prev"] != height) {
			delete application["estimation-description-size"];
			delete application["estimation-description-size-prev"]
		}
	}
	return false;
}

function restoreDescriptionHeight() {
	var $description = $("#description");
	if ($description.length) {
		application["estimation-description-default-size"] = $description.outerHeight();
		var height = application["estimation-description-size"];
		if (height != undefined) {
			$("#description").css({
				"height": height
			});
		}
	}
	return false;
}

function updateScroll(element) {
	var currentActive = document.activeElement;
	if (element != currentActive) {
		element.focus();// / / update scroll
		currentActive.focus();
	}
	return false;
}

function updateAssessmentUI() {
	var $assessment = $("div.list-group:visible>.list-group-item.active");
	if (!$assessment.is(":focus") && $("div[role='left-menu']").css("position") == "fixed")
		updateScroll($assessment);
	loadAssessmentData($assessment.attr("data-trick-id"));
}

function riskEstimationUpdate(){
	var $view = $("#tabRiskEstimation");
	if($view.is(":visible")){
		if(helper == undefined)
			initialiseRiskEstimation();
		updateNavigation();
		updateAssessmentUI();
	}else if(helper!=undefined)
		$view.attr("data-update-required",helper.invalidate = true);
}

function updateNavigation() {
	var $currentAssessment = $("div[data-trick-content]:visible .list-group-item.active");
	
	if(activeSelector == undefined)
		activeSelector = $currentAssessment.closest("[data-trick-content]").attr("data-trick-content") == "scenario"? "asset" : "scenario";
	
	var $currentSelector = $("select[name='" + activeSelector + "']:visible>option:selected"), $previousSelector = $(
		"[data-trick-nav='previous-selector']").parent(), $nextSelector = $("[data-trick-nav='next-selector']").parent(), $previousAssessment = $(
			"[data-trick-nav='previous-assessment']").parent(), $nextAssessment = $("[data-trick-nav='next-assessment']").parent();

	if ($currentSelector.next(":first").length)
		$nextSelector.removeClass("disabled");
	else
		$nextSelector.addClass("disabled");

	if ($currentSelector.prev("[value!='-1']:last").length)
		$previousSelector.removeClass("disabled");
	else
		$previousSelector.addClass("disabled");

	if ($currentAssessment.prevAll(".list-group-item:visible").length)
		$previousAssessment.removeClass("disabled");
	else
		$previousAssessment.addClass("disabled");

	if ($currentAssessment.nextAll(".list-group-item:visible").length)
		$nextAssessment.removeClass("disabled");
	else
		$nextAssessment.addClass("disabled");
	return false;

}

function changeAssessment(e) {
	var $target = $(e.currentTarget);
	if (!$target.hasClass('active')) {
		var $parent = $target.closest(".list-group");
		$(".list-group-item.active", $parent).removeClass("active");
		$target.addClass('active');
	}
	riskEstimationUpdate();
	return false;
}

function AssessmentHelder() {
	this.names = ["asset", "scenario"];
	this.asset = $("select[name='asset']");
	this.scenario = $("select[name='scenario']");
	this.lastSelected = {
		asset: this.asset.find("option[value!='-1']:first").val(),
		scenario: this.scenario.find("option[value!='-1']:first").val()
	};
	this.switchControl(this.asset.val() == "-1" ? "scenario" : "asset");
}

AssessmentHelder.prototype = {

	invalidate : true,
	
	getCurrent: function (name) {
		return name == "asset" ? this.asset : this.scenario;
	},
	getOther: function (name) {
		return name == "asset" ? this.scenario : this.asset;
	},
	getOtherName: function (name) {
		return name == "asset" ? 'scenario' : 'asset';
	},
	setLastSelected: function (name, id) {
		if (id == "-1")
			return false;
		this.lastSelected[name] = id;
	},
	getLastSelected: function (name) {
		return this.lastSelected[name];
	},

	switchControl: function (name) {
		if (name == activeSelector)
			return this;
		activeSelector = name;
		$("div[data-trick-content]").each(function () {
			if (this.getAttribute("data-trick-content") == name)
				$(this).hide();
			else
				$(this).show();
		});
		return this;
	},
	updateContent: function () {
		var type = this.getCurrent(activeSelector).find("option:selected").attr("data-trick-type"), $elements = $("div[data-trick-content]:visible a[data-trick-id!='-1']");
		if (activeSelector == "asset") {
			$elements.each(function () {
				var $this = $(this), scenarioType = $this.attr("data-trick-type");
				if (scenarioType && scenarioType.search(type) != -1)
					$this.show();
				else
					$this.hide();
			});
		} else {
			$elements.each(function () {
				var $this = $(this);
				if (type.search($this.attr("data-trick-type")) != -1)
					$this.show();
				else
					$this.hide();
			});
		}

		if ($elements.filter(".active").is(":hidden"))
			$elements.filter(":visible:first").click();
		else
			updateAssessmentUI();
		return this;
	}
}

function displayParameters(name, title) {
	var view = new Modal(undefined, $(name).html()), $legend = $(view.modal_body).find("legend").remove();
	$(view.modal_footer).remove();
	$(view.modal_body).find("tbody").css({
		"text-align" : "center"
	}).find("td").removeAttributes();
	if (!title)
		title = $legend.text();
	view.setTitle(title);
	view.Show();
	return false;
}

function computeAssessment(silent) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Assessment/Update",
			type : "POST",
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
					chartALE();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

function refreshAssessment() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$("#confirm-dialog .modal-body").html(MessageResolver("confirm.refresh.assessment", "Are you sure, you want to rebuild all assessments"));
		$("#confirm-dialog .btn-danger").click(function() {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Assessment/Refresh",
				type : "POST",
				contentType : "application/json;charset=UTF-8",
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
			}).complete(function() {
				$progress.hide();
			});
		});
		$("#confirm-dialog").modal("show");
	} else
		permissionError();
	return false;
}

function updateAssessmentAle(silent) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Assessment/Update/ALE",
			type : "POST",
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
		}).complete(function() {
			$progress.hide();
		});
	} else
		permissionError();

	return false;
}


function manageRiskProfileMeasure(idAsset, idScenario, e) {
	var $progress = $("#loading-indicator").show();
	$.ajax(
		{
			url: context + "/Analysis/Assessment/RiskProfile/Manage-measure?idAsset=" + idAsset + "&idScenario=" + idScenario,
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				var $measureManager = $("div#riskProfileMeasureManager", new DOMParser().parseFromString(response, "text/html"));
				if ($measureManager.length) {
					$measureManager.appendTo("#widgets");
					var $standardSelector = $("#riskProfileStandardSelector", $measureManager), $messageContainer = $("#riskProfileMessageContainer", $measureManager),
						$selectedMeasures = $("table#riskProfileSelectedMeasureContainer", $measureManager), $standardMeasures = $("table#riskProfileStandardMeasureContainer", $measureManager);
					if (standardCaching == undefined) {
						standardCaching = {
							standards: {},
							measures: {},
							$standardSelector: $standardSelector,
							$measureManager: $measureManager,
							$selectedMeasures: $selectedMeasures,
							$standardMeasures: $standardMeasures,
							$messageContainer: $messageContainer,

							load: function (standard) {
								this.$messageContainer.empty();
								if (this.hasStandard(standard))
									return this.clearStandardMeasureUI().updateStandardUI(standard);
								var instance = this;
								$progress.show();
								$.ajax(
									{
										url: context + "/Analysis/Standard/Measures?idStandard=" + standard,
										contentType: "application/json;charset=UTF-8",
										success: function (response) {
											if ($.isArray(response)) {
												standardCaching.addStandard(standard, response).clearStandardMeasureUI().updateStandardUI(standard);
											} else if (response.error)
												$("<label class='label label-danger' />").text(response.error).appendTo(instance.$messageContainer.empty());
											else
												$("<label class='label label-danger' />").text(
													MessageResolver("error.loading.measures", 'An unknown error occurred while loading measures')).appendTo(
													instance.$messageContainer.empty());
										}, error: unknowError
									}).complete(function () {
										$progress.hide();
									});
								return this;
							},
							hasStandard: function (standard) {
								return this.standards[standard] != undefined;
							},
							addStandard: function (standard, measures) {
								if (!this.hasStandard(standard))
									this.standards[standard] = { measures: {} }
								var computableMeasure = this.standards[standard].measures = {};
								for (let measure of measures) {
									if (measure.computable)
										computableMeasure[measure.id] = measure;
								}
								return this;
							}, getStandardMeasures: function (standard) {
								var selected = this.standards[standard];
								return selected == undefined ? [] : selected.measures;
							}, getMeasure: function (idStandard, idMeasure) {
								return this.getStandardMeasures(idStandard)[idMeasure];
							},
							addMeasure: function (measure) {
								if (!this.measures[measure.id]) {
									this.measures[measure.id] = measure.id;
									var $measure = $("tbody tr[data-trick-id='" + measure.id + "']", this.$standardMeasures), $clone = $measure.clone();
									if (!$measure.length) {
										var $tr = $("<tr data-trick-id='" + measure.id + "' data-trick-class='Measure'>"), standardName = $("option[value='" + measure.idStandard + "']", this.$standardSelector).text(),
											$button = $("<button class='btn btn-xs btn-danger'><i class='fa fa-times' aria-hidden='true'></i></button>"), status = application.measureStatus[measure.status];
										$button.appendTo($("<td />").appendTo($tr));
										$("<td data-real-value='" + measure.idStandard + "'>" + standardName + "</td>").appendTo($tr);
										$("<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;'>" + measure.reference + "</td>").attr("data-title", measure.description).appendTo($tr).tooltip().on('show.bs.tooltip', toggleToolTip);
										$("<td data-real-value='" + measure.status + "'>" + status.value + "</td>").attr("title", status.title).appendTo($tr);
										$("<td>" + measure.implementationRate + "</td>").appendTo($tr);
										$("<td>" + measure.phase + "</td>").appendTo($tr);
										$("<td>" + measure.domain + "</td>").appendTo($tr);
										$clone = $tr;
										$button.on("click", removeMeasure).attr("title", MessageResolver("label.action.remove","Remove"));
										
									} else {
										$("button.btn.btn-xs.btn-primary", $clone).removeClass("btn-primary").addClass("btn-danger").off("click").on("click", removeMeasure).attr("title", MessageResolver("label.action.remove","Remove")).find("i.fa-plus").removeClass("fa-plus").addClass("fa-times");
										$clone.find("[data-toggle='tooltip']").tooltip().on('show.bs.tooltip', toggleToolTip);
										$measure.addClass("info").find(".btn").prop("disabled", true);
									}
									
									if(measure.status == 'NA')
										$clone.addClass("danger");
									else if(measure.implementationRate>=100)
										$clone.addClass("warning");
									
									$clone.appendTo($("tbody", this.$selectedMeasures));
								}
								return this;
							},
							removeMeasure: function (idMeasure) {
								if (this.measures[idMeasure])
									delete this.measures[idMeasure];
								$("tbody tr[data-trick-id='" + idMeasure + "']", this.$standardMeasures).removeClass("info").find(".btn").prop("disabled", false);
								return this;
							},
							updateStandardUI: function (standard) {
								var measures = this.getStandardMeasures(standard), $tbody = $("tbody", this.$standardMeasures), standardName = $("option:selected", this.$standardSelector).text();
								for (var idMeasure in measures) {
									var measure = measures[idMeasure], $tr = $("<tr data-trick-id='" + measure.id + "' data-trick-class='Measure'>"),
										$button = $("<button class='btn btn-xs btn-primary'><i class='fa fa-plus' aria-hidden='true'></i></button>"), status = application.measureStatus[measure.status];
									$button.appendTo($("<td />").appendTo($tr));
									$("<td data-real-value='" + measure.idStandard + "' >" + standardName + "</td>").appendTo($tr);
									$("<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;'>" + measure.reference + "</td>").attr("data-title", measure.description).appendTo($tr).tooltip().on('show.bs.tooltip', toggleToolTip);
									$("<td data-real-value='" + measure.status + "' >" + status.value + "</td>").attr("title", status.title).appendTo($tr);
									$("<td>" + measure.implementationRate + "</td>").appendTo($tr);
									$("<td>" + measure.phase + "</td>").appendTo($tr);
									$("<td>" + measure.domain + "</td>").appendTo($tr);
									
									if (this.measures[measure.id] != undefined) {
										$tr.addClass("info");
										$button.prop("disabled", true);
									}
									
									$button.on("click", function () {
										standardCaching.addMeasure(standardCaching.getMeasure(standard, $(this).closest("tr[data-trick-id]").attr("data-trick-id")));
									}).attr("title", MessageResolver("label.action.add","Add"));
									
									$tr.appendTo($tbody);
								}
								return this;
							},
							clearStandardMeasureUI: function () {
								this.$standardMeasures.find("tbody").empty();
								return this;
							}, update: function ($standardSelector, $measureManager, $selectedMeasures, $standardMeasures, $messageContainer) {
								this.$standardSelector = $standardSelector;
								this.$measureManager = $measureManager;
								this.$selectedMeasures = $selectedMeasures;
								this.$standardMeasures = $standardMeasures;
								this.$messageContainer = $messageContainer;
							}, updateView: function () {
								var measures = this.measures, $finalBody = $("tbody", "#riskProfileMeasure"), $currentTrs = $("tr[data-trick-id]", $finalBody), $selectedTrs = $("tbody>tr[data-trick-id]", this.$selectedMeasures);
								$currentTrs.each(function () {
									var $this = $(this), measureId = $this.attr("data-trick-id");
									if (!measures[measureId])
										$this.remove();
									else delete measures[measureId];
								});

								$selectedTrs.each(function () {
									var measureId = this.getAttribute("data-trick-id");
									if (measures[measureId]) {
										var $clone = $(this).clone();
										$("td:first", $clone).remove();
										$("td[data-toggle='tooltip']", $clone).tooltip().on('show.bs.tooltip', toggleToolTip);
										$clone.appendTo($finalBody);
									}
								});

								return this;
							}
						};
					} else standardCaching.update($standardSelector, $measureManager, $selectedMeasures, $standardMeasures, $messageContainer);

					$standardSelector.on("change", function () {
						if (this.value == "-1")
							standardCaching.clearStandardMeasureUI();
						else
							standardCaching.load(this.value);
						forceCloseToolTips();
					});

					standardCaching.measures = [];

					$("tbody>tr[data-trick-id]", $selectedMeasures).each(function () {
						var $this = $(this), idMeasure = $this.attr("data-trick-id");
						$("button", $this).on("click", removeMeasure);
						standardCaching.measures[idMeasure] = idMeasure;
						$('[data-toggle="tooltip"]', $this).tooltip().on('show.bs.tooltip', toggleToolTip)
					});

					$('a[data-toggle="tab"]', $measureManager).on('shown.bs.tab', function (e) {
						forceCloseToolTips();
					});

					$measureManager.modal("show").on("hide.bs.modal", function () {
						closeToolTips();
					}).on("hidden.bs.modal", function () {
						$measureManager.remove();
					});

					$("button[name='save']").on("click", function () {
						var measures = [];
						for (let id of standardCaching.measures) {
							if (id > 0)
								measures.push(id);
						}
						$progress.show();
						$messageContainer.empty();
						$.ajax({
							url: context + "/Analysis/Assessment/RiskProfile/Update/Measure?idAsset=" + idAsset + "&idScenario=" + idScenario,
							type: "POST",
							data: JSON.stringify(measures),
							contentType: "application/json;charset=UTF-8",
							success: function (response) {
								if (response.success) {
									$("<label class='label label-success' />").text(response.success).appendTo($messageContainer);
									setTimeout(function() {
										standardCaching.updateView().$measureManager.modal("hide");
									}, 2000);
									
								} else if (response.error)
									$("<label class='label label-error' />").text(response.error).appendTo($messageContainer);
								else $("<label class='label label-error' />").text(MessageResolver("error.saving.measures", 'An unknown error occurred while saving measures')).appendTo($messageContainer);

							},
							error: unknowError
						}).complete(function () { $progress.hide();});
					});

				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide()
		});
	return false;
}

function initialiseRiskEstimation () {

	helper = new AssessmentHelder();

	var $nav = $("ul.nav.nav-pills[data-trick-role='nav-estimation']").on("trick.update.nav", updateNavigation), $openAnalysis = $("a[data-base-ul]", $nav), $previousSelector = $("[data-trick-nav='previous-selector']"), $nextSelector = $("[data-trick-nav='next-selector']"), $previousAssessment = $("[data-trick-nav='previous-assessment']"), $nextAssessment = $("[data-trick-nav='next-assessment']");
	
	$previousSelector.on("click", function () {
		$("select[name='" + activeSelector + "']>option:selected").prev("[value!='-1']:last").prop('selected', true).parent().change();
		return false;
	});

	$nextSelector.on("click", function () {
		$("select[name='" + activeSelector + "']>option:selected").next(":first").prop('selected', true).parent().change();
		return false;
	});

	$previousAssessment.on("click", function () {
		$("div[data-trick-content]:visible .list-group-item.active").prevAll(":visible:first").click();
		return false;
	});

	$nextAssessment.on("click", function () {
		$("div[data-trick-content]:visible .list-group-item.active").nextAll(":visible:first").click();
		return false;
	});

	var updateSelector = function (e) {
		var $target = $(e.currentTarget), value = $target.val(), name = $target.attr("name"), other = helper.getOtherName(name);
		if (value == '-1')
			helper.switchControl(other).getCurrent(other).find("option[value='" + helper.getLastSelected(other) + "']:first").prop('selected', true);
		else if (helper.getCurrent(other).val() != "-1")
			helper.switchControl(name).getCurrent(other).find("option[value='-1']").prop('selected', true);
		else
			helper.setLastSelected(name, value);

		helper.updateContent();

		$nav.trigger("trick.update.nav");
		return false;
	};

	$("div.list-group>.list-group-item").on("click", changeAssessment);

	for (var i in helper.names)
		helper.getCurrent(helper.names[i]).on('change', updateSelector)
}