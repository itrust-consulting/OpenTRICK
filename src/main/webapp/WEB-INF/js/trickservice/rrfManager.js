function loadRRF() {
	$.ajax({
		url : context + "/Analysis/RRF",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			newSection = $(doc).find("*[id ='rrfEditor']");
			if (!newSection.length) {
				unknowError();
				return false;
			} else {
				$("#rrfEditor").replaceWith(newSection);

				initialiseMeasureSliders();

				initialiseMeasuresClick();

				initialiseScenariosClick();

				$("#rrfEditor").modal("show");
				setTimeout(function() {
					loadMeasureChart();
				}, 500);
			}
		},
		error : unknowError
	});

	return false;
}

function initialiseMeasureSliders() {
	$("#rrfEditor #control_rrf_measure .slider").slider().each(function() {
		$(this).on("slideStop", function(event) {
			var field = event.target.name;
			var fieldValue = event.value;
			var previousValue = $("#rrfEditor #control_rrf_measure #measure_" + field + "_value").attr("value");
			$("#rrfEditor #control_rrf_measure input[id='measure_" + field + "_value']").attr("value", fieldValue);
			return updateMeasureProperty(field, fieldValue, previousValue, $(this));
		})
	});
}

function updateMeasureProperty(property, value, previousValue, slider) {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
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
		success : function(response) {
			if (response.error != undefined) {
				$("#rrfEditor #control_rrf_measure #measure_" + property + "_value").attr("value", previousValue);
				console.log("error: " + response.error);
				$(slider).slider('setValue', previousValue);
			} else {
				loadMeasureChart();
			}

		},
		error : unknowError
	});
}

function initialiseScenarioSliders() {
	$("#rrfEditor #control_rrf_scenario .slider").slider().each(function() {
		$(this).on("slideStop", function(event) {
			var field = event.target.name;
			var fieldValue = event.value;
			var previousValue = $("#rrfEditor #control_rrf_scenario #scenario_" + field + "_value").attr("value");
			var displayvalue = fieldValue;
			if (field == "preventive" || field == "detective" || field == "limitative" || field == "corrective")
				displayvalue = fieldValue.toFixed(1);
			$("#rrfEditor #control_rrf_scenario #scenario_" + field + "_value").attr("value", displayvalue);
			return updateScenarioProperty(field, fieldValue, previousValue, $(this));
		})
	});
}

function updateScenarioProperty(property, value, previousValue, slider) {
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	if (property == "preventive" || property == "detective" || property == "limitative" || property == "corrective")
		value = value.toFixed(1);
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
		success : function(response) {
			if (response.error != undefined) {
				$("#rrfEditor #control_rrf_scenario #scenario_" + property + "_value").attr("value", previousValue);
				console.log("error: " + response.error);
				$(slider).slider('setValue', previousValue);
			} else {

				if (property == "preventive" || property == "detective" || property == "limitative" || property == "corrective") {
					var result = +$("#control_rrf_scenario #scenario_preventive_value").val() + +$("#control_rrf_scenario #scenario_detective_value").val()
							+ +$("#control_rrf_scenario #scenario_limitative_value").val() + +$("#control_rrf_scenario #scenario_corrective_value").val();
					result = result.toFixed(1);
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
		error : unknowError
	});
}

function initialiseScenariosClick() {

	$("#rrfEditor #selectable_rrf_scenario_controls a").click(function() {

		// remove previous selection
		$("#rrfEditor #selectable_rrf_scenario_controls a.active").removeClass("active");

		// get current element data
		var classname = $(this).attr("trick-class");
		var trickid = $(this).attr("trick-id");

		// select current
		$(this).addClass("active");

		if (classname === "ScenarioType") {
			var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
			if (idMeasure == null || idMeasure == undefined) {
				$("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Standard']").parent().parent().find("div.list-group a:first").addClass("active");
				idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
				if (idMeasure == null || idMeasure == undefined)
					return false;
			}
			loadMeasure();
			loadMeasureChart();
		} else if (classname === "Scenario") {

			// select measure parent
			$(this).parent().parent().find("h4 a").addClass("active");
			// $("#rrfEditor #selectable_rrf_measures_chapter_controls
			// .active[trick-class='Measure']").removeClass("active");
			loadScenario();
			loadScenarioChart();
		}
		// console.log(classname + "::" + trickid);
	});
}

function initialiseMeasuresClick() {

	$("#rrfEditor #selectable_rrf_measures_chapter_controls a").click(function() {

		// remove previous selection
		$("#rrfEditor #selectable_rrf_measures_chapter_controls a.active").removeClass("active");

		// get current element data
		var classname = $(this).attr("trick-class");
		var trickid = $(this).attr("trick-id");

		// select current
		$(this).addClass("active");

		if (classname === "Standard") {
			var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
			if (idScenario == null || idScenario == undefined) {
				$("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='ScenarioType']").parent().parent().find("div.list-group a:first").addClass("active");
				idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
				if (idScenario == null || idScenario == undefined)
					return false;
			}
			loadScenario();
			loadScenarioChart();
		} else if (classname === "Measure") {

			// select measure parent
			$(this).parent().parent().find("h4 a").addClass("active");

			// $("#rrfEditor #selectable_rrf_scenario_controls
			// .active[trick-class='Scenario']").removeClass("active");

			loadMeasure();
			loadMeasureChart();
		}
		// console.log(classname + "::" + trickid);
	});
}

function loadMeasure() {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	var idStandard = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Standard']").attr("trick-id");
	if (idStandard == null || idStandard == undefined)
		return false;
	$.ajax({
		url : context + "/Analysis/RRF/Standard/" + idStandard + "/Measure/" + idMeasure,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {

			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var measurerrf = $(doc).find("*[id='control_rrf_measure']");
			if (!measurerrf.length)
				return true;

			$("#rrfEditor #control_rrf_measure").html($(measurerrf).html());

			// $("#rrfEditor #control_rrf_measure .slider").slider();

			initialiseMeasureSliders();

			$("#rrfEditor #control_rrf_measure").removeAttr("hidden");

			$("#rrfEditor #control_rrf_scenario").attr("hidden", true);

		},
		error : function() {
			unknowError;
			return false;
		}
	});
	return false;
}

function loadMeasureChart() {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
	if (idMeasure == null || idMeasure == undefined)
		return false;
	var idScenarioType = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='ScenarioType']").attr("trick-id");
	if (idScenarioType == null || idScenarioType == undefined)
		return false;
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
	$
			.ajax({
				url : context + "/Analysis/RRF/Measure/" + idMeasure + "/Chart",
				type : "POST",
				data : JSON.stringify({
					"scenariotype" : idScenarioType,
					"scenario" : idScenario
				}),
				async : true,
				contentType : "application/json;charset=UTF-8",
				success : function(response) {
					$("#rrfEditor #chart_rrf").css("padding-right", "");
					if (response.chart != null && response.chart != undefined) {
						$("#rrfEditor #chart-container").highcharts(response).highcharts();
						$("#rrfEditor #chart_rrf").css("padding-right", "14px");
					} else if (response.error != undefined) {

						$("#rrfEditor #chart-container")
								.html(
										'<div style="width: 100%; min-height: 340px; padding-top: 150px;padding-left:15px;padding-right:15px;"><div class="alert alert-danger" role="alert"><a data-dismiss="alert" href="#" class="close">x</a><p style="text-align: left">'
												+ response.error + '</p></div></div>');

					} else
						unknowError();
					return false;
				},
				error : function() {
					unknowError;
					return false;
				}
			});
	return false;
}

function loadScenario() {
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	$.ajax({
		url : context + "/Analysis/RRF/Scenario/" + idScenario,
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {

			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var scenariorrf = $(doc).find("*[id='control_rrf_scenario']");
			if (!scenariorrf.length)
				return true;

			$("#rrfEditor #control_rrf_scenario").html($(scenariorrf).html());

			// $("#rrfEditor #control_rrf_scenario .slider").slider();

			initialiseScenarioSliders();

			$("#rrfEditor #control_rrf_scenario").removeAttr("hidden");

			$("#rrfEditor #control_rrf_measure").attr("hidden", true);

		},
		error : function() {
			unknowError;
			return false;
		}
	});
	return false;
}

function loadScenarioChart() {

	var idStandard = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Standard']").attr("trick-id");
	if (idStandard == null || idStandard == undefined)
		return false;

	var chapter = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Standard']").attr("trick-value");
	if (chapter == null || chapter == undefined)
		return false;

	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");

	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
	if (idScenario == null || idScenario == undefined)
		return null;

	$
			.ajax({
				url : context + "/Analysis/RRF/Scenario/" + idScenario + "/Chart",
				type : "POST",
				data : JSON.stringify({
					"idStandard" : idStandard,
					"chapter" : chapter,
					"idMeasure" : idMeasure,
				}),
				async : true,
				contentType : "application/json;charset=UTF-8",
				success : function(response) {
					$("#rrfEditor #chart_rrf").css("padding-right", "");
					if (response.chart != null && response.chart != undefined) {
						$("#rrfEditor #chart-container").highcharts(response).highcharts();
						$("#rrfEditor #chart_rrf").css("padding-right", "14px");
					} else if (response.error != undefined) {
						$("#rrfEditor #chart-container")
								.html(
										'<div style="width: 100%; min-height: 340px; padding-top: 150px;padding-left:15px;padding-right:15px;"><div class="alert alert-danger" role="alert"><a data-dismiss="alert" href="#" class="close">x</a><p style="text-align: left">'
												+ response.error + '</p></div></div>');
					} else
						unknowError();
					return false;
				},
				error : function() {
					unknowError;
					return false;
				}
			});
	return false;
}
