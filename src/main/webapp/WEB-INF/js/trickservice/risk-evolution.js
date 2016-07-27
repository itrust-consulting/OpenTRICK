var $customer = undefined, $analyses = undefined, $versions = undefined;

$(document).ready(function() {
	$customer = $("#customer-selector"), $analyses = $("select[name='analysis']"), $versions = $("select[name='version']");

	application["risk-evolution"] = {
		customer : $customer.val(),
		analyses : [],
		size : $versions.length
	};

	application.shownScrollTop = false;

	$customer.on("change", function(e) {
		var $progress = $("#loading-indicator").show(), value = $customer.val();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Customer/" + value,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				$versions.find("option[value!='-']").remove();
				$analyses.find("option[value!='-']").remove();
				$versions.filter("[data-index!='1']").prop("disabled", true);
				$analyses.filter("[data-index!='1']").prop("disabled", true);
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

	$versions.on("change", onVersionChange);

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
				$version.trigger("change");
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else $version.trigger("change");
}

function onVersionChange(e) {
	var analyses = application["risk-evolution"].analyses = [], $target = $(e.currentTarget), index = parseInt($target.attr("data-index")), currentValue = $target.val();
	
	if (currentValue == "-") {
		$analyses.filter(":visible").filter(function() {
			return this.getAttribute("data-index") > index;
		}).closest("[data-role='form-container']").hide();
	} else {
		$analyses.filter("[data-index='" + (index + 1) + "']").closest("[data-role='form-container']").show();
		$versions.filter("[data-index='" + (index + 1) + "']")
	}
	
	$versions.each(function() {
		var $this = $(this), value = $this.val();
		if ($this.is(":visible") && !(value == "-" || analyses.includes(value)))
			analyses.push(value);
	});

	

	loadTotalALE();
	loadAleByScenario();
	loadAleByAssetType();
}

function loadTotalALE() {
	var $tab = $("#tabTotalALE");
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", 'loadTotalALE');
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Chart/Total-ALE",
			data : {
				"customerId" : application["risk-evolution"].customer,
				"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				// response.chart.height = "700";
				$("<div class='max-height' />").appendTo($tab.empty()).highcharts(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
}

function loadAleByAssetType() {
	var $tab = $("#tabAleByAssetType");
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", 'loadAleByAssetType');
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Chart/ALE-by-asset-type",
			data : {
				"customerId" : application["risk-evolution"].customer,
				"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				// response.chart.height = "700";
				$("<div class='max-height' />").appendTo($tab.empty()).highcharts(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
}

function loadAleByScenario() {
	var $tab = $("#tabAleByScenarioType");
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", 'loadAleByScenario');
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Chart/ALE-by-scenario-type",
			data : {
				"customerId" : application["risk-evolution"].customer,
				"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				// response.chart.height = "700";
				$("<div class='max-height' />").appendTo($tab.empty()).highcharts(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
}

function loadAleByScenarioType() {

}

function loadAleByAsset() {

}
