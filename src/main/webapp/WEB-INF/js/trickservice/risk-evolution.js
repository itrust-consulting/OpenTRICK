var $customer = undefined, $analyses = undefined, $versions = undefined;

$(document).ready(function() {
	$customer = $("#customer-selector"), $analyses = $("select[name='analysis']"), $versions = $("select[name='version']");

	application["risk-evolution"] = {
		customer : $customer.val(),
		analyses : [],
		size : $versions.length
	};

	application.shownScrollTop = false;

	application.sataticPadding = 2;

	application.scrollBarWith = getScrollbarWidth() + application.sataticPadding + "px";

	$customer.on("change", function(e) {
		var $progress = $("#loading-indicator").show(), value = $customer.val();
		$.ajax({
			url : context + "/Analysis/Risk-evolution/Customer/" + value,
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

	});

	$analyses.on("change", onAnalysesChange);

	$versions.on("change", onVersionChange);

	$(".affixNav").mouseenter(function() {
		var $this = $(this);
		if ($this.hasScrollBar()) {
			application.scrollBarWith = ((this.offsetWidth - this.clientWidth) + application.sataticPadding) + "px";
			$this.css({
				"padding-right" : application.sataticPadding + "px"
			});
		}
	}).mouseleave(function() {
		var $this = $(this);
		if ($this.css("padding-right") == (application.sataticPadding + "px")) {
			$this.css({
				"padding-right" : application.scrollBarWith
			});
		}
	}).css({
		"padding-right" : application.scrollBarWith
	});

	$("button[data-control]").on("click", function() {
		$analyses.filter("[data-index='" + (this.getAttribute("data-control")) + "']").val("-").change();
		$versions.filter(":visible").filter(function() {
			return this.value != '-';
		}).trigger("change");
	});

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
			loadCompliance();
		}
	}
}

function loadALEChart(tab, trigger, url) {
	var $tab = $(tab).empty();
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
				if (Array.isArray(response)) {
					for (var i = 0; i < response.length; i++)
						$("<div />").appendTo($tab).highcharts(response[i]);
				} else
					$("<div class='max-height' />").appendTo($tab).highcharts(response);
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
}

function loadTotalALE() {
	loadALEChart("#tabTotalALE", 'loadTotalALE', "/Analysis/Risk-evolution/Chart/Total-ALE");
}

function loadAleByAssetType() {
	loadALEChart("#tabAleByAssetType", 'loadAleByAssetType', "/Analysis/Risk-evolution/Chart/ALE-by-asset-type");
}

function loadAleByScenario() {
	loadALEChart("#tabAleByScenario", 'loadAleByScenario', "/Analysis/Risk-evolution/Chart/ALE-by-scenario");
}

function loadAleByScenarioType() {
	loadALEChart("#tabAleByScenarioType", 'loadAleByScenarioType', "/Analysis/Risk-evolution/Chart/ALE-by-scenario-type");
}

function loadAleByAsset() {
	loadALEChart("#tabAleByAsset", 'loadAleByAsset', "/Analysis/Risk-evolution/Chart/ALE-by-asset");
}

function loadCompliance() {
	loadALEChart("#tabCompliance", 'loadCompliance', "/Analysis/Risk-evolution/Chart/Compliance");
}
