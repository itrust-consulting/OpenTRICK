var  helpers = Chart.helpers, $customer = undefined, $analyses = undefined, $versions = undefined, analysisType = undefined;
$(document).ready(function() {
	$customer = $("#customer-selector"), $analyses = $("select[name='analysis']"), $versions = $("select[name='version']");

	application["risk-evolution"] = {
		customer : $customer.val(),
		analyses : [],
		size : $versions.length
	};

	$("#type-selector").on("change keyup", (e)=> {
		analysisType = e.target.value;
		if(application["risk-evolution"].analyses)
			application["risk-evolution"].analyses = [];
		
		$(".tab-pane").empty();
		
		$customer.trigger('change');
		
		$(".risk-evolution li[data-type!='"+analysisType+"'][data-type!='ALL']").addClass('hidden');
		
		$(".risk-evolution li[data-type='"+analysisType+"']").removeClass('hidden');
		
		if($(".risk-evolution li.active:hidden").length)
			$(".risk-evolution li:visible:first>a").trigger("click");
			
	}).trigger("change");
	
	$customer.on("change keyup", function(e) {
		var $progress = $("#loading-indicator").show(), value = $customer.val();
		if (value == "-") {
			$analyses.find("option[value!='-']").remove();
			$versions.find("option[value!='-']").remove();
			if(application["risk-evolution"].analyses){
				application["risk-evolution"].analyses=[];
				application["risk-evolution"].customer = -1;
			}
			$versions.first().trigger("change");
			$progress.hide();
		} else {
			$.ajax({
				url : context + "/Analysis/Risk-evolution/Type/"+analysisType+"/Customer/" + value,
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					if (!Array.isArray(response))
						unknowError();
					else {
						$versions.find("option[value!='-']").remove();
						$analyses.find("option[value!='-']").remove();
						application["risk-evolution"].analyses = [];
						for (var i = 0; i < response.length; i++) {
							for (var j = 0; j < $analyses.length; j++)
								$("<option />").attr("value", response[i].identifier).text(response[i].label).appendTo($($analyses[j]));
						}
						$versions.first().trigger("change");
						application["risk-evolution"].customer = value;
					}
				},
				error : unknowError
			}).complete(function() {
				$progress.hide();
			});
		}
		return false;
	});

	$analyses.on("change keyup", onAnalysesChange);

	$versions.on("change keyup", onVersionChange);

	$("button[data-control]").on("click", function() {
		var index = this.getAttribute("data-control");
		$analyses.filter("[data-index='" + index + "']").val("-").change();
		$analyses.filter(function() {
			return this.getAttribute("data-index") > index;
		}).val("-");

		$versions.filter(function() {
			return this.getAttribute("data-index") > index;
		}).val("-");

		var length = $versions.filter(":visible").filter(function() {
			return this.value != '-';
		}).last().trigger("change").length;

		if (!length)
			$(".tab-pane").empty();
	});
	
	Chart.defaults.global.defaultFontColor = "#333";
	Chart.defaults.global.defaultFontFamily = 'Corbel', 'Lucida Grande', 'Lucida Sans Unicode', 'Verdana', 'Arial', 'Helvetica', 'sans-serif';
	Chart.defaults.global.defaultFontSize = 13;
});

function aleChartOption(title) {
	return {
		title: {
			display: title != undefined,
			fontSize: 16,
			text: title
		},
		legend: {
			position: "bottom"
		},
		tooltips: {
			callbacks: {
				label: function (item, data) {
					return application.currencyFormat.format(item.yLabel).replace("€", "k€");
				}
			}
		},
		scales: {
			xAxes: [{
				stacked: true
			}],
			yAxes: [{
				stacked: true,
				ticks: {
					userCallback: function (value, index, values) {
						return application.currencyFormat.format(value.toString()).replace("€", "k€");
					}
				}
			}]
		}
	};
}

function onAnalysesChange() {
	var $this = $(this), value = $this.val(), $version = $($this.attr("data-target"));
	$version.find("option[value!='-']").remove();
	if (value != '-') {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Type/"+analysisType+"/Customer/" + $customer.val() + "/Identifier/" + value,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (!Array.isArray(response))
					unknowError();
				else {
					for (var i = 0; i < response.length; i++)
						$("<option />").attr("value", response[i].id).text(response[i].version).appendTo($version);
					$version.trigger("change");
				}
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		$version.trigger("change");
	return false;
}

function onVersionChange(e) {
	var analyses = application["risk-evolution"].analyses = [], $target = $(e.currentTarget), index = parseInt($target.attr("data-index")), currentValue = $target.val(), updateChart = true;
	if ($target.is(":visible")) {
		if (currentValue == "-") {
			$analyses.filter(":visible").filter(function() {
				return this.getAttribute("data-index") > index;
			}).closest("[data-role='form-container']").hide().find("button[data-control]").prop("disabled", true);
			updateChart = false;
		} else {
			$analyses.filter("[data-index='" + (index + 1) + "']").closest("[data-role='form-container']").show();
			var $nextVersion = $versions.filter("[data-index='" + (index + 1) + "']");
			if ($nextVersion.length && $nextVersion.val() != "-") {
				$nextVersion.trigger('change');
				updateChart = false;
			}
			$versions.each(function(i) {
				var value = this.value, index = this.getAttribute("data-index");
				if (value != '-') {
					$versions.filter(function() {
						return this.value == value && this.getAttribute("data-index") > index;
					}).val("-");
				}
			});
		}

		$versions.find("option:hidden").prop("hidden", false);
		var $visibleVersions = $versions.filter(":visible");
		$visibleVersions.filter(":visible").each(function(i) {
			var value = this.value, index = this.getAttribute("data-index");
			if (value != "-") {
				if (updateChart && analyses.indexOf(value) == -1)
					analyses.push(value);
				$versions.filter("[data-index!='" + index + "']").find("option[value='" + value + "']").prop("hidden", true);
				$("button[data-control='" + index + "']").prop("disabled", false);
			} else
				$("button[data-control='" + index + "']").prop("disabled", true);
		});

		if (updateChart) {
			loadTotalALE();
			loadAleByAsset();
			loadAleByScenario();
			loadAleByAssetType();
			loadAleByScenarioType();
			
			loadTotalRisk();
			loadRiskByAsset();
			loadRiskByScenario();
			loadRiskByAssetType();
			loadRiskByScenarioType();
			loadCompliance();
			if (!analyses.length)
				$(".tab-pane").empty();
		}

	}
	return false;
}

function loadALEChart(tab,name, trigger, url) {
	var $tab = $(tab);
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", trigger);
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + url,
			data : {
				"customerId" : application["risk-evolution"].customer,
				"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				$tab.empty();
				if (window[name] != undefined)
					window[name].map(chart => chart.destroy());
				if (!helpers.isArray(response))
					response = [response];
				window[name] = [];
				response.map(chart => {
					var $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($tab);
					window[name].push(new Chart($canvas[0].getContext("2d"), {
						type: "bar",
						data: chart,
						options: aleChartOption(chart.title)
					}));

				});
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}

function loadRiskChart(tab,name, trigger, url) {
	var $tab = $(tab);
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", trigger);
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + url,
			data : {
				"customerId" : application["risk-evolution"].customer,
				"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				$tab.empty();
				if (window[name] != undefined)
					window[name].map(chart => chart.destroy());
				if (!helpers.isArray(response))
					response = [response];
				window[name] = [];
				response.map(chart => {
					var $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($tab);
					window[name].push(new Chart($canvas[0].getContext("2d"), {
						type: "bar",
						data: chart,
						options: {
							scales: {
								xAxes: [{
									stacked: true
								}],
								yAxes: [{
									stacked: true
								}]
							}
						}
					}));

				});
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}


function loadTotalALE() {
	return loadALEChart("#tab-total-ale","total-ale-chart", 'loadTotalALE', "/Analysis/Risk-evolution/Chart/Total-ALE");
}

function loadAleByAssetType() {
	return loadALEChart("#tab-ale-asset-type","ale-asset-type-chart", 'loadAleByAssetType', "/Analysis/Risk-evolution/Chart/ALE-by-asset-type");
}

function loadAleByScenario() {
	return loadALEChart("#tab-ale-scenario","ale-scenario-chart", 'loadAleByScenario', "/Analysis/Risk-evolution/Chart/ALE-by-scenario");
}

function loadAleByScenarioType() {
	return loadALEChart("#tab-ale-scenario-type", "ale-scenario-type-chart",'loadAleByScenarioType', "/Analysis/Risk-evolution/Chart/ALE-by-scenario-type");
}

function loadAleByAsset() {
	return loadALEChart("#tab-ale-asset","ale-asset-chart", 'loadAleByAsset', "/Analysis/Risk-evolution/Chart/ALE-by-asset");
}

function loadTotalRisk() {
	return loadRiskChart("#tab-total-risk","total-risk-chart", 'loadTotalRisk', "/Analysis/Risk-evolution/Chart/Total-Risk");
}

function loadRiskByAssetType() {
	return loadRiskChart("#tab-risk-asset-type","risk-asset-type-chart", 'loadRiskByAssetType', "/Analysis/Risk-evolution/Chart/Risk-by-asset-type");
}

function loadRiskByScenario() {
	return loadRiskChart("#tab-risk-scenario","risk-scenario-chart", 'loadRiskByScenario', "/Analysis/Risk-evolution/Chart/Risk-by-scenario");
}

function loadRiskByScenarioType() {
	return loadRiskChart("#tab-risk-scenario-type", "risk-scenario-type-chart",'loadRiskByScenarioType', "/Analysis/Risk-evolution/Chart/Risk-by-scenario-type");
}

function loadRiskByAsset() {
	return loadRiskChart("#tab-risk-asset","risk-asset-chart", 'loadAleByAsset', "/Analysis/Risk-evolution/Chart/Risk-by-asset");
}

function loadCompliance() {
	var $tab = $("#tab-compliance");
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", 'loadCompliance');
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show(), name = "compliancesChart", canvas = "chart_canvas_compliance_";
		try {
			$.ajax({
				url: context + "/Analysis/Risk-evolution/Chart/Compliance",
				data : {
					"customerId" : application["risk-evolution"].customer,
					"analyses" : (application["risk-evolution"].analyses + "").replace("=[]", "")
				},
				contentType : "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
					if (window[name] == undefined)
						window[name] = new Map();
					else {
						window[name].forEach((chart,key)=> {
							chart.destroy();
							$("[id='chart_compliance_" +key+"']").remove();
						});
					}
					
					charts.map(chart => {
						if (chart.datasets && chart.datasets.length) {
							chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
							var $parent = $("<div class='col-sm-6 col-lg-4' id='chart_compliance_" + chart.trickId + "' />").appendTo($tab), $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($parent);
							window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
								type: "radar",
								data: chart,
								options: {
									legend: {
										position: 'bottom',
									},
									title: {
										display: true,
										text: chart.title,
										fontSize: 16
									},
									scale: {
										ticks: {
											beginAtZero: true,
											max: 100,
											stepSize:20
										}
									}
								}
							}));
						
						}
					});
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		} catch (e) {
			$progress.hide();
		}
	}
	return false;
}
