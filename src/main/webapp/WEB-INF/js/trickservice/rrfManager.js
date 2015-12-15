function rrfError(message) {
	if (message == undefined)
		message = MessageResolver("error.unknown.occurred", "An unknown error occurred");
	$("#rrfEditor #rrf-error .label").remove();
	var alert = $("<label class='label label-danger'>" + message
			+ " <a href='#' style='margin-left:10px;color:#fff;'   onclick='return $(this).parent().remove();'>&times;</a></label>");
	alert.appendTo($("#rrfEditor #rrf-error"));
	setTimeout(function() {
		alert.remove();
	}, 10000);
	return false;
}

function loadRRF() {
	var $progress = $("#progress-dialog");
	$progress.modal("show");
	$
			.ajax(
					{
						url : context + "/Analysis/RRF",
						type : "get",
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var $rrfUI = $("#rrfEditor", new DOMParser().parseFromString(response, "text/html"));

							if (!$rrfUI.length) {
								unknowError();
								return false;
							} else {
								$("#rrfEditor").replaceWith($rrfUI);

								initialiseMeasureSliders();

								initialiseMeasuresClick();

								initialiseScenariosClick();

								initialiseStandardFilter();

								if (application.openMode!==OPEN_MODE.READ) {

									var $controlApplySubChapter = $("#measure-control-apply-sub-chapter", $rrfUI), $selectetiveControlApplySubChapter = $(
											"#measure-control-apply-selective-sub-chapter", $rrfUI), applyMeasureCharacteristics = function(data, idMeasure) {
										if (data.length) {
											$.ajax({
												url : context + "/Analysis/RRF/Measure/" + idMeasure + "/Update-child",
												type : "post",
												data : JSON.stringify(data),
												contentType : "application/json;charset=UTF-8",
												success : function(response, textStatus, jqXHR) {
													if (response.success == undefined)
														rrfError(response.error == undefined ? undefined : response.error);
												},
												error : function() {
													rrfError();
												},

											});
										}
									};
									if ($controlApplySubChapter.length) {
										$controlApplySubChapter.on("click", function() {
											var $selectedMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']"), $parent = $selectedMeasure
													.parent(), level = $selectedMeasure.attr("data-trick-value"), data = [];
											$("[data-trick-value^='" + level + ".']", $parent).each(function() {
												data.push($(this).attr("data-trick-id"));
											});
											applyMeasureCharacteristics(data, $selectedMeasure.attr("data-trick-id"));
										});
									}

									if ($selectetiveControlApplySubChapter.length) {
										$selectetiveControlApplySubChapter
												.on(
														"click",
														function() {
															var $selectedMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']"), $parent = $selectedMeasure
																	.parent(), level = $selectedMeasure.attr("data-trick-value"), $form = $("<form class='form-horizontal'></form>");
															$("[data-trick-value^='" + level + ".']", $parent)
																	.each(
																			function() {
																				var $this = $(this);
																				var $formGroup = $("<div class='form-group'><div class='col-md-1'><input style='margin-top:-5px' type='checkbox' class='form-control'></div><label class='col-md-11'></label></div>");
																				$("label", $formGroup).text($this.text())
																				$("input", $formGroup).attr("name", $this.attr("data-trick-id"));
																				$formGroup.appendTo($form);
																			});
															var $input = $("input", $form);
															if ($input.length) {
																var parentText = $selectedMeasure.text().replace("\t", "").replace("\n", " ").trim(), modal = new Modal(undefined,
																		$form).setTitle(MessageResolver("label.title.rrf.apply.measure.characteristics",
																		"RRF: Apply measure ({0})  characteristics".replace("{0}", parentText), parentText));

																$("button[data-control-type='ok']", modal.modal_footer).on("click", function() {
																	var data = []
																	$("input:checked", $form).each(function() {
																		data.push($(this).attr("name"));
																	});
																	applyMeasureCharacteristics(data, $selectedMeasure.attr("data-trick-id"));
																}).text(MessageResolver("label.action.apply", "Apply"));
																modal.Show();
															}
														});
									}
								}
								$rrfUI.modal("show");
								setTimeout(function() {
									loadMeasureChart();
									$('#rrfEditor #chart-container-pending').remove();
								}, 500);
							}
						},
						error : unknowError
					}).complete(function() {
				$("#progress-dialog").modal("hide");
			});

	return false;
}

function initialiseMeasureSliders() {

	var $selectedMeasure = $("#selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']"), level = $selectedMeasure.attr("data-trick-value")
	if ($("[data-trick-value^='" + level + ".']", $selectedMeasure.parent()).length)
		$("[id^='measure-control-apply']").show();
	else
		$("[id^='measure-control-apply']").hide();

	$("#rrfEditor #control_rrf_measure .slider").slider().each(function() {
		if (application.openMode === OPEN_MODE.READ) {
			$(this).prop("disabled", true);
			$(this).addClass("disabled");
		} else {
			$(this).on("slideStop", function(event) {
				var field = event.target.name;
				var fieldValue = event.value;
				var previousValue = $("#rrfEditor #control_rrf_measure #measure_" + field + "_value").attr("value");
				$("#rrfEditor #control_rrf_measure input[id='measure_" + field + "_value']").attr("value", fieldValue);
				return updateMeasureProperty(field, fieldValue, previousValue, $(this));
			});
		}
	});
}

function updateMeasureProperty(property, value, previousValue, slider) {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']").attr("data-trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	$.ajax({
		url : context + "/Analysis/EditField/Measure/" + idMeasure,
		type : "post",
		data : JSON.stringify({
			"id" : idMeasure,
			"fieldName" : property,
			"value" : value,
			"type" : "numeric"
		}),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response.success == undefined) {
				$("#rrfEditor #control_rrf_measure #measure_" + property + "_value").attr("value", previousValue);
				$(slider).slider('setValue', previousValue);
				rrfError(response.error == undefined ? undefined : response.error);
			} else
				loadMeasureChart();
		},
		error : function() {
			rrfError();
		}
	});
}

function initialiseScenarioSliders() {
	$("#rrfEditor #control_rrf_scenario .slider").slider().each(function() {
		if (application.openMode === OPEN_MODE.READ) {
			$(this).prop("disabled", true);
			$(this).addClass("disabled");
		} else {
			$(this).on("slideStop", function(event) {
				var field = event.target.name;
				var fieldValue = event.value;
				var previousValue = $("#rrfEditor #control_rrf_scenario #scenario_" + field + "_value").attr("value");
				var displayvalue = fieldValue;
				if (field == "preventive" || field == "detective" || field == "limitative" || field == "corrective")
					displayvalue = fieldValue.toFixed(2);
				$("#rrfEditor #control_rrf_scenario #scenario_" + field + "_value").attr("value", displayvalue);
				return updateScenarioProperty(field, fieldValue, previousValue, $(this));
			});
		}
	});
}

function updateScenarioProperty(property, value, previousValue, slider) {
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='Scenario']").attr("data-trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	if (property == "preventive" || property == "detective" || property == "limitative" || property == "corrective")
		value = value.toFixed(2);
	$.ajax({
		url : context + "/Analysis/EditField/Scenario/" + idScenario,
		type : "post",
		data : JSON.stringify({
			"id" : idScenario,
			"fieldName" : property,
			"value" : value,
			"type" : "numeric"
		}),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response.success == undefined) {
				$("#rrfEditor #control_rrf_scenario #scenario_" + property + "_value").attr("value", previousValue);
				rrfError(response.error == undefined ? undefined : response.error);
				$(slider).slider('setValue', previousValue);
			} else {

				if (property == "preventive" || property == "detective" || property == "limitative" || property == "corrective") {
					var result = +$("#control_rrf_scenario #scenario_preventive_value").val() + +$("#control_rrf_scenario #scenario_detective_value").val()
							+ +$("#control_rrf_scenario #scenario_limitative_value").val() + +$("#control_rrf_scenario #scenario_corrective_value").val();
					result = result.toFixed(2);
					$("#control_rrf_scenario .pdlc").removeClass("success");
					$("#control_rrf_scenario .pdlc").removeClass("danger");
					if (result == 1)
						$("#control_rrf_scenario .pdlc").addClass("success");
					else
						$("#control_rrf_scenario .pdlc").addClass("danger");
				}

				loadScenarioChart();
			}

		},
		error : function() {
			rrfError();
		}
	});
}

function initialiseScenariosClick() {

	$("#rrfEditor #selectable_rrf_scenario_controls a").click(function() {

		// remove previous selection
		$("#rrfEditor #selectable_rrf_scenario_controls a.active").removeClass("active");

		// get current element data
		var classname = $(this).attr("data-trick-class");
		var trickid = $(this).attr("data-trick-id");

		// select current
		$(this).addClass("active");

		if (classname === "ScenarioType") {
			var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']").attr("data-trick-id");
			if (idMeasure == null || idMeasure == undefined) {
				$("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']").parent().parent().find("div.list-group a:first").addClass("active");
				idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']").attr("data-trick-id");
				if (idMeasure == null || idMeasure == undefined)
					return false;
			}
			loadMeasure();
			loadMeasureChart();
		} else if (classname === "Scenario") {

			// select measure parent
			$(this).parent().parent().find("h4 a").addClass("active");
			loadScenario();
			loadScenarioChart();
		}
	});
}

function initialiseMeasuresClick() {

	$("#rrfEditor #selectable_rrf_measures_chapter_controls a").click(function() {

		// remove previous selection
		$("#rrfEditor #selectable_rrf_measures_chapter_controls a.active").removeClass("active");

		// get current element data
		var $this = $(this), classname = $this.attr("data-trick-class");
		var trickid = $this.attr("data-trick-id");

		// select current
		$this.addClass("active");

		if (classname === "Standard") {
			var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='Scenario']").attr("data-trick-id");
			if (idScenario == null || idScenario == undefined) {
				$("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='ScenarioType']").parent().parent().find("div.list-group a:first").addClass("active");
				idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='Scenario']").attr("data-trick-id");
				if (idScenario == null || idScenario == undefined)
					return false;
			}
			loadScenario();
			loadScenarioChart();
		} else if (classname === "Measure") {
			// select measure parent
			$this.parent().parent().find("h4 a").addClass("active");
			loadMeasure();
			loadMeasureChart();
		}
	});
}

function loadMeasure() {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']").attr("data-trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	var idStandard = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']").attr("data-trick-id");
	if (idStandard == null || idStandard == undefined)
		return false;
	$.ajax({
		url : context + "/Analysis/RRF/Standard/" + idStandard + "/Measure/" + idMeasure,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $measurerrf = $("[data-trick-controller-name='measure']", new DOMParser().parseFromString(response, "text/html"));
			if (!$measurerrf.length)
				return true;
			$("[data-trick-controller-name='measure']").replaceWith($measurerrf);
			initialiseMeasureSliders();
			var $controlMeasure = $("#control_rrf_measure");
			if (!$controlMeasure.is(":visible")) {
				$("#control_rrf_scenario").hide();
				$controlMeasure.show();
			}
		},
		error : function() {
			rrfError();
			return false;
		}
	});
	return false;
}

function loadMeasureChart() {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']").attr("data-trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	var idScenarioType = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='ScenarioType']").attr("data-trick-id");
	if (idScenarioType == null || idScenarioType == undefined)
		return false;
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='Scenario']").attr("data-trick-id");
	$.ajax({
		url : context + "/Analysis/RRF/Measure/" + idMeasure + "/Chart",
		type : "POST",
		data : JSON.stringify({
			"scenariotype" : idScenarioType,
			"scenario" : idScenario
		}),
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#rrfEditor #chart_rrf").css("padding-right", "");
			if (response.chart != null && response.chart != undefined) {
				$("#rrfEditor #chart-container").highcharts(response).highcharts();
				$("#rrfEditor #chart_rrf").css("padding-right", "14px");
			} else
				rrfError(response.error == undefined ? undefined : response.error);
			return false;
		},
		error : function() {
			rrfError();
			return false;
		}
	});
	return false;
}

function loadScenario() {
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='Scenario']").attr("data-trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	$.ajax({
		url : context + "/Analysis/RRF/Scenario/" + idScenario,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $scenariorrf = $("[data-trick-controller-name='scenario']", new DOMParser().parseFromString(response, "text/html"))
			if (!$scenariorrf.length)
				return true;
			$("[data-trick-controller-name='scenario']").replaceWith($scenariorrf);
			initialiseScenarioSliders();
			var $scenarioControl = $("#control_rrf_scenario");
			if (!$scenarioControl.is(":visible")) {
				$("#control_rrf_measure").hide();
				$scenarioControl.show();
			}
		},
		error : function() {
			rrfError();
			return false;
		}
	});
	return false;
}

function loadScenarioChart() {

	var idStandard = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']").attr("data-trick-id");
	if (idStandard == null || idStandard == undefined)
		return false;

	var chapter = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Standard']").attr("data-trick-value");
	if (chapter == null || chapter == undefined)
		return false;

	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[data-trick-class='Measure']").attr("data-trick-id");

	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[data-trick-class='Scenario']").attr("data-trick-id");
	if (idScenario == null || idScenario == undefined)
		return null;

	$.ajax({
		url : context + "/Analysis/RRF/Scenario/" + idScenario + "/Chart",
		type : "POST",
		data : JSON.stringify({
			"idStandard" : idStandard,
			"chapter" : chapter,
			"idMeasure" : idMeasure,
		}),
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#rrfEditor #chart_rrf").css("padding-right", "");
			if (response.chart != null && response.chart != undefined) {
				$("#rrfEditor #chart-container").highcharts(response).highcharts();
				$("#rrfEditor #chart_rrf").css("padding-right", "14px");
			} else
				rrfError(response.error == undefined ? undefined : response.error);
			return false;
		},
		error : function() {
			rrfError();
			return false;
		}
	});
	return false;
}

function initialiseStandardFilter() {
	$("#section_rrf [name='chapterselection']").change(function() {
		var filter = $("option:selected", this).attr("value");
		$("#selectable_rrf_measures_chapter_controls [class='list-group'][data-trick-filter-value]").css("display", "none");
		$("#selectable_rrf_measures_chapter_controls [class='list-group'][data-trick-filter-value='" + filter + "']").css("display", "block");
	});
}
