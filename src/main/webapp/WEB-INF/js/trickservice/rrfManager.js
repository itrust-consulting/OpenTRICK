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
				$("#rrfEditor #control_rrf_measure .slider").slider();

				$("#rrfEditor #selectable_rrf_measures_chapter_controls a").click(function() {

					// remove previous selection
					$("#rrfEditor #selectable_rrf_measures_chapter_controls a.active").removeClass("active");

					// get current element data
					var classname = $(this).attr("trick-class");
					var trickid = $(this).attr("trick-id");

					// select current
					$(this).addClass("active");

					if (classname === "Standard") {

					} else if (classname === "Measure") {

						// select measure parent
						$(this).parent().parent().find("h4 a").addClass("active");

						loadMeasure();
						loadMeasureChart();
					}
					//console.log(classname + "::" + trickid);
				});

				$("#rrfEditor #selectable_rrf_scenario_controls a").click(function() {

					// remove previous selection
					$("#rrfEditor #selectable_rrf_scenario_controls a.active").removeClass("active");

					// get current element data
					var classname = $(this).attr("trick-class");
					var trickid = $(this).attr("trick-id");

					// select current
					$(this).addClass("active");

					if (classname === "ScenarioType") {

					} else if (classname === "Scenario") {

						// select measure parent
						$(this).parent().parent().find("h4 a").addClass("active");

						loadScenario();
						loadScenarioChart();
					}
					//console.log(classname + "::" + trickid);
				});
				
				$("#rrfEditor").modal("show");
				setTimeout(function(){
					loadMeasureChart();
				},2000);
			}
		},
		error : unknowError
	});

	return false;
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

			$("#rrfEditor #control_rrf_measure .slider").slider();

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
	$.ajax({
		url : context + "/Analysis/RRF/Measure/" + idMeasure + "/Chart",
		type : "POST",
		data : JSON.stringify({
			"scenariotype" : idScenarioType
		}),
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#rrfEditor #chart_rrf").css("padding-right","");
			if (response.chart != null && response.chart != undefined) {
				$("#rrfEditor #chart-container").highcharts(response).highcharts();
				$("#rrfEditor #chart_rrf").css("padding-right","14px");	
			}
			else if (response.error != undefined) {
				$("#rrfEditor #chart-container").html('<div style="width: 100%; height: 360px; padding-top: 180px;"></div>');
				showError($("#rrfEditor #chart_rrf div"), response.error);
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

			$("#rrfEditor #control_rrf_scenario .slider").slider();

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
	var idScenario = $("#rrfEditor #selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
	if (idScenario == null || idScenario == undefined)
		return false;
	$.ajax({
		url : context + "/Analysis/RRF/Scenario/" + idScenario + "/Chart",
		type : "POST",
		data : JSON.stringify({
			"idStandard" : idStandard,
			"chapter" : chapter,
		}),
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			$("#rrfEditor #chart_rrf").css("padding-right","");
			if (response.chart != null && response.chart != undefined) {
				$("#rrfEditor #chart-container").highcharts(response).highcharts();
				$("#rrfEditor #chart_rrf").css("padding-right","14px");	
			}
			else if (response.error != undefined) {
				$("#rrfEditor #chart-container").html('<div style="width: 100%; height: 360px; padding-top: 180px;"></div>');
				showError($("#rrfEditor #chart_rrf div"), response.error);
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
