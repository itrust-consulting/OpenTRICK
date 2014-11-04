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
				$("#rrfEditor .slider").slider();
				$("#rrfEditor").modal("show");
				loadMeasureChart();
			}
		},
		error : unknowError
	});

	return false;
}

function loadMeasureChart() {
	var idMeasure = $("#rrfEditor #selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
	$.ajax({
		url : context + "/Analysis/RRF/Measure/" + idMeasure + "/Chart",
		type : "get",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			if (response.chart != null && response.chart != undefined)
				$("#rrfEditor #chart_rrf").highcharts(response).highcharts();
			else if (response.error != undefined) {
				$("#rrfEditor #chart_rrf").html('<div style="width: 100%; height: 360px; padding-top: 180px;"></div>');
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