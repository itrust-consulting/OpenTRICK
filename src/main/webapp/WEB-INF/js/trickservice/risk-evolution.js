$(document).ready(function() {
	var $customer = $("#customer-selector"), $analyses = $("select[name='analysis']"), $versions = $("select[name='version']");

	application["risk-evolution"] = {
		customer : $customer.val(),
		analyses : [],
		size : $versions.length
	};

	$customer.on("change", function(e) {
		var $progress = $("#loading-indicator").show(), value = $customer.val();
		$.ajax({
			url : context + "/Analysis/Build/Customer/" + value,
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

	$analyses.on("change", function() {
		var $this = $(this), value = $this.val(), $version = $($this.attr("data-target"));
		$version.find("option[value!='-']").remove();
		if (value != '-') {
			var $progress = $("#loading-indicator").show();
			$.ajax({
				url : context + "/Analysis/Build/Customer/" + $customer.val() + "/Identifier/" + value,
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
	});

	$versions.on("change", function() {
		var $this = $(this), value = $this.val(), index = $this.attr("data-index") - 1;
		if (value != '-')
			application["risk-evolution"].analyses[index] = value;
		else
			delete application["risk-evolution"].analyses[index];
		realoadCharts();
	});

});

function realoadCharts() {
	loadTotalALE();
}

function loadTotalALE() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Risk-evolution/Chart/Total-ALE",
		data : {
			"customerId" : application["risk-evolution"].customer,
			"analyses" : (application["risk-evolution"].analyses+"").replace("=[]","")
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
