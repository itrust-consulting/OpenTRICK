var $customer = undefined, $analyses = undefined, $versions = undefined;

$(document).ready(function() {
	$customer = $("#customer-selector"), $analyses = $("select[name='analysis']"), $versions = $("select[name='version']");

	application["risk-evolution"] = {
		customer : $customer.val(),
		analyses : [],
		size : $versions.length
	};

	$customer.on("change", function(e) {
		var $progress = $("#loading-indicator").show(), value = $customer.val();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Customer/" + value,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				$versions.find("option[value!='-']").remove();
				$analyses.find("option[value!='-']").remove();
				application["risk-evolution"].analyses = [];
				for (var i = 0; i < response.length; i++) {
					for (var j = 0; j < $analyses.length; j++)
						$("<option />").attr("value", response[i].identifier).text(response[i].label).appendTo($($analyses[j]));
				}
				application["risk-evolution"].customer = value;
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});

	});

	$analyses.on("change", onAnalysesChange);

	$versions.on("change", realoadCharts);

});

function onAnalysesChange() {
	var $this = $(this), value = $this.val(), $version = $($this.attr("data-target"));
	$version.find("option[value!='-']").remove();
	if (value != '-') {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Customer/" + $customer.val() + "/Identifier/" + value,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				for (var i = 0; i < response.length; i++)
					$("<option />").attr("value", response[i].id).text(response[i].version).appendTo($version);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
}

function realoadCharts() {
	var analyses = application["risk-evolution"].analyses = [];
	$versions.each(function() {
		var $this = $(this), value = $this.val();
		if (value != "-" && !analyses.includes(value))
			analyses.push(value);
	});
	loadTotalALE();
}

function loadTotalALE() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Risk-evolution/Chart/Total-ALE",
		data : {
			"customerId" : application["risk-evolution"].customer,
			"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
		},
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("<div />").appendTo($("#tabTotalALE").empty()).highcharts(response);
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
}

function loadAleByAssetType() {

	
}

function loadAleByScenario() {

}

function loadAleByScenarioType() {

}

function loadAleByAsset() {

}
