var helpers = Chart.helpers, $customer = undefined, $analyses = undefined, $versions = undefined, analysisType = undefined, timerSaveSettings = undefined, restoring = false, openingTag = window.location.hash;
$(document).ready(function () {
	$customer = $("#customer-selector"), $analyses = $("select[name='analysis']"), $versions = $("select[name='version']");

	application["risk-evolution"] = {
		customer: $customer.val(),
		analyses: [],
		size: $versions.length
	};

	$("#type-selector").on("change keyup", (e) => {
		analysisType = e.target.value;
		if (application["risk-evolution"].analyses)
			application["risk-evolution"].analyses = [];

		$(".tab-pane").empty();

		$customer.trigger('change');

		$(".risk-evolution li:not([data-type~='" + analysisType + "'])").addClass('hidden');

		$(".risk-evolution li[data-type~='" + analysisType + "']").removeClass('hidden');

		if ($(".risk-evolution li.active:hidden").length)
			$(".risk-evolution li:visible:first>a").trigger("click");

	}).trigger("change");

	$customer.on("change keyup", function (e) {
		var $progress = $("#loading-indicator").show(), value = $customer.val();
		if (value == "-") {
			$analyses.find("option[value!='-']").remove();
			$versions.find("option[value!='-']").remove();
			if (application["risk-evolution"].analyses) {
				application["risk-evolution"].analyses = [];
				application["risk-evolution"].customer = -1;
			}
			$versions.first().trigger("change");
			$progress.hide();
		} else {
			$.ajax({
				url: context + "/Analysis/Risk-evolution/Type/" + analysisType + "/Customer/" + value,
				contentType: "application/json;charset=UTF-8",
				async: !restoring,
				success: function (response, textStatus, jqXHR) {
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
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		}
		return false;
	});

	$analyses.on("change keyup", onAnalysesChange);

	$versions.on("change keyup", onVersionChange);

	$("button[data-control]").on("click", function () {
		var index = this.getAttribute("data-control");
		$analyses.filter("[data-index='" + index + "']").val("-").change();
		$analyses.filter(function () {
			return this.getAttribute("data-index") > index;
		}).val("-");

		$versions.filter(function () {
			return this.getAttribute("data-index") > index;
		}).val("-");

		var length = $versions.filter(":visible").filter(function () {
			return this.value != '-';
		}).last().trigger("change").length;

		if (!length)
			$(".tab-pane").empty();

		resetSaveSettingsTimeout();
	});

	restoreSettings();

});



function onAnalysesChange() {
	var $this = $(this), value = $this.val(), $version = $($this.attr("data-target"));
	$version.find("option[value!='-']").remove();
	if (value != '-') {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Risk-evolution/Type/" + analysisType + "/Customer/" + $customer.val() + "/Identifier/" + value,
			contentType: "application/json;charset=UTF-8",
			async: !restoring,
			success: function (response, textStatus, jqXHR) {
				if (!Array.isArray(response))
					unknowError();
				else {
					for (var i = 0; i < response.length; i++)
						$("<option />").attr("value", response[i].id).text(response[i].version).appendTo($version);
					$version.trigger("change");
				}
			},
			error: unknowError
		}).complete(function () {
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
			$analyses.filter(":visible").filter(function () {
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
			$versions.each(function (i) {
				var value = this.value, index = this.getAttribute("data-index");
				if (value != '-') {
					$versions.filter(function () {
						return this.value == value && this.getAttribute("data-index") > index;
					}).val("-");
				}
			});
		}

		$versions.find("option:hidden").prop("hidden", false);
		var $visibleVersions = $versions.filter(":visible");
		$visibleVersions.filter(":visible").each(function (i) {
			var value = this.value, index = this.getAttribute("data-index");
			if (value != "-") {
				if (updateChart && analyses.indexOf(value) == -1)
					analyses.push(value);
				$versions.filter("[data-index!='" + index + "']").find("option[value='" + value + "']").prop("hidden", true);
				$("button[data-control='" + index + "']").prop("disabled", false);
			} else
				$("button[data-control='" + index + "']").prop("disabled", true);
		});

		if (updateChart && !restoring) {
			updateCharts();
			if (!analyses.length)
				$(".tab-pane").empty();
			resetSaveSettingsTimeout();

		}

	}
	return false;
}

function updateCharts() {
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
}

function resetSaveSettingsTimeout() {
	clearTimeout(timerSaveSettings);
	timerSaveSettings = setTimeout(() => saveSettings(), 5000);
}

function restoreSettings() {
	try {
		restoring = true;
		if (!application["settings"] || !application["settings"].type)
			return false;
		$("#type-selector").val(application["settings"].type).change();
		if (!application["settings"].idCustomer)
			return false;
		
		if(application["settings"].currentTab){
			if(!openingTag)
				openingTag = application["settings"].currentTab;
		}
		
		if(openingTag)	
			$("a[href='"+openingTag+"']:visible").tab("show");
		
		$customer.val(application["settings"].idCustomer).change();
		if (!application["settings"].analyses || !application["settings"].versions)
			return false;
		var analyses = application["settings"].analyses, versions = application["settings"].versions;
		for (var i = 0; i < analyses.length && i < versions.length; i++) {
			$($analyses[i]).val(analyses[i]).change();
			$($versions[i]).val(versions[i]).change();
		}
		if (versions.length)
			updateCharts();
		return true
	} finally {
		restoring = false;
	}
}

function saveSettings() {
	var data = {
		type: analysisType,
		idCustomer: $customer.val(),
		currentTab : window.location.hash,
		analyses: [],
		versions: []
	};

	$analyses.filter(":visible").each(i => {
		var value = $($analyses[i]).val();
		if (value != '-')
			data.analyses.push(value);
	});

	$versions.filter(":visible").each(i => {
		var value = $($versions[i]).val();
		if (value != '-')
			data.versions.push(value);
	});

	$.ajax({
		url: context + "/Analysis/Risk-evolution/Save-settings",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: (response) => {
			if (response.success)
				showDialog("info", response.success);
		}

	});
}


function updateRiskCriteria(settings, $table) {
	if (!settings)
		return false;
	$("tbody>tr.warning", $table).remove();
	var $body = $("tbody", $table)
	for (var i = 0; i < settings.length; i++) {
		var $tr = $("<tr />");
		$("<td class='textaligncenter'/>").text(settings[i].label).appendTo($tr);
		if(i==0)
			$("<td class='textaligncenter'/>").text("[ 0 ; "+application.numberFormatNoDecimal.format(settings[i].value)+" ]").appendTo($tr);
		else if(i==settings.length-1)
			$("<td class='textaligncenter'/>").text('] '+application.numberFormatNoDecimal.format(settings[i-1].value)+'; +∞ [').appendTo($tr);
		else $("<td class='textaligncenter'/>").text('] '+application.numberFormatNoDecimal.format(settings[i-1].value)+'; '+application.numberFormatNoDecimal.format(settings[i].value)+' ]').appendTo($tr);
		$("<td class='textaligncenter' />").text(settings[i].description).appendTo($tr);
		$("<td/>").css({ "background-color": settings[i].color }).appendTo($tr);
		$tr.appendTo($body);
	}
}

function loadTotalALE() {
	return loadALEChart("#tab-total-ale", "total-ale-chart", 'loadTotalALE', "/Analysis/Risk-evolution/Chart/Total-ALE");
}

function loadAleByAssetType() {
	return loadALEChart("#tab-ale-asset-type", "ale-asset-type-chart", 'loadAleByAssetType', "/Analysis/Risk-evolution/Chart/ALE-by-asset-type");
}

function loadAleByScenario() {
	return loadALEChart("#tab-ale-scenario", "ale-scenario-chart", 'loadAleByScenario', "/Analysis/Risk-evolution/Chart/ALE-by-scenario");
}

function loadAleByScenarioType() {
	return loadALEChart("#tab-ale-scenario-type", "ale-scenario-type-chart", 'loadAleByScenarioType', "/Analysis/Risk-evolution/Chart/ALE-by-scenario-type");
}

function loadAleByAsset() {
	return loadALEChart("#tab-ale-asset", "ale-asset-chart", 'loadAleByAsset', "/Analysis/Risk-evolution/Chart/ALE-by-asset");
}

function loadTotalRisk() {
	return loadRiskChart("#tab-total-risk", "total-risk-chart", 'loadTotalRisk', "/Analysis/Risk-evolution/Chart/Total-Risk");
}

function loadRiskByAssetType() {
	return loadRiskChart("#tab-risk-asset-type", "risk-asset-type-chart", 'loadRiskByAssetType', "/Analysis/Risk-evolution/Chart/Risk-by-asset-type");
}

function loadRiskByScenario() {
	return loadRiskChart("#tab-risk-scenario", "risk-scenario-chart", 'loadRiskByScenario', "/Analysis/Risk-evolution/Chart/Risk-by-scenario");
}

function loadRiskByScenarioType() {
	return loadRiskChart("#tab-risk-scenario-type", "risk-scenario-type-chart", 'loadRiskByScenarioType', "/Analysis/Risk-evolution/Chart/Risk-by-scenario-type");
}

function loadRiskByAsset() {
	return loadRiskChart("#tab-risk-asset", "risk-asset-chart", 'loadRiskByAsset', "/Analysis/Risk-evolution/Chart/Risk-by-asset");
}

function loadALEChart(tab, name, trigger, url) {
	var $tab = $(tab);
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", trigger);
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + url,
			data: {
				"customerId": application["risk-evolution"].customer,
				"analyses": (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
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
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

function loadRiskChart(tab, name, trigger, url) {
	var $tab = $(tab);
	if (!$tab.is(":visible"))
		$tab.attr("data-update-required", true).attr("data-trigger", trigger);
	else if (application["risk-evolution"].analyses && application["risk-evolution"].analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + url,
			data: {
				"customerId": application["risk-evolution"].customer,
				"analyses": (application["risk-evolution"].analyses + "").replace("=[]", "")
			},
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				var $containers = $("<div class='col-sm-7 col-lg-8'/>").appendTo($tab.empty()), $criteria = $("#view-helper fieldset").clone().appendTo($("<div class='col-sm-5 col-lg-4' />").appendTo($tab)), $table = $("table", $criteria).removeAttr("id");
				if (window[name] != undefined)
					window[name].map(chart => chart.destroy());
				if (!helpers.isArray(response))
					response = [response];
				window[name] = [];
				response.map(chart => {
					var $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($containers);
					updateRiskCriteria(chart.settings, $table);
					window[name].push(new Chart($canvas[0].getContext("2d"), {
						type: "bar",
						data: chart,
						options: riskOptions(chart.title)
					}));

				});
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
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
				data: {
					"customerId": application["risk-evolution"].customer,
					"analyses": (application["risk-evolution"].analyses + "").replace("=[]", "")
				},
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					var color = Chart.helpers.color, charts = helpers.isArray(response) ? response : [response];
					if (window[name] == undefined)
						window[name] = new Map();
					else {
						window[name].forEach((chart, key) => {
							chart.destroy();
							$("[id='chart_compliance_" + key + "']").remove();
						});
					}

					charts.map(chart => {
						if (chart.datasets && chart.datasets.length) {
							chart.datasets.map(dataset => dataset.backgroundColor = color(dataset.backgroundColor).alpha(0.1).rgbString());
							var $parent = $("<div class='col-md-6' id='chart_compliance_" + chart.trickId + "' />").appendTo($tab), $canvas = $("<canvas style='max-width: 1000px; margin-left: auto; margin-right: auto;' />").appendTo($parent);
							window[name].set(chart.trickId, new Chart($canvas[0].getContext("2d"), {
								type: "radar",
								data: chart,
								options: complianceOptions(chart.title)
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
