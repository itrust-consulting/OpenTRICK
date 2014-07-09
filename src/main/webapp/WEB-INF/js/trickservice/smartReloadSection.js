/**
 * reload sections
 */

function reloadSection(section, subSection) {
	if (Array.isArray(section)) {
		for (var int = 0; int < section.length; int++) {
			if (Array.isArray(section[int]))
				reloadSection(section[int][0], section[int][1]);
			else
				reloadSection(section[int]);
		}
	} else {
		var controller = controllerBySection(section, subSection);
		if (controller == null || controller == undefined)
			return false;
		$.ajax({
			url : context + controller,
			type : "get",
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var tag = response.substring(response.indexOf('<'));
				var parser = new DOMParser();
				var doc = parser.parseFromString(tag, "text/html");
				if (subSection != null && subSection != undefined)
					section += "_" + subSection;
				newSection = $(doc).find("*[id = '" + section + "']");
				var smartUpdate = new SectionSmartUpdate(section, newSection);
				if (smartUpdate.Update()) {
					$("#" + section).replaceWith(newSection);
					var tableFixedHeader = $("#" + section).find("table.table-fixed-header");
					if (tableFixedHeader.length) {
						setTimeout(function() {
							fixedTableHeader(tableFixedHeader);
						}, 500);
					}
				}
				var callback = callbackBySection(section);
				if ($.isFunction(callback))
					callback();
				return false;
			},
			error : unknowError
		});
	}
}

function controllerBySection(section, subSection) {
	var controllers = {
		"section_asset" : "/Asset/Section",
		"section_parameter" : "/Parameter/Section",
		"section_scenario" : "/Scenario/Section",
		"section_assessment" : "/Assessment/Section",
		"section_phase" : "/Phase/Section",
		"section_analysis" : "/Analysis/Section",
		"section_profile_analysis" : "/AnalysisProfile/Section",
		"section_measure" : "/Measure/Section",
		"section_customer" : "/KnowledgeBase/Customer/Section",
		"section_language" : "/KnowledgeBase/Language/Section",
		"section_norm" : "/KnowledgeBase/Norm/Section",
		"section_user" : "/Admin/User/Section",
		"section_actionplans" : "/ActionPlan/Section",
		"section_summary" : "/ActionPlanSummary/Section",
		"section_riskregister" : "/RiskRegister/Section"
	};

	if (subSection == null || subSection == undefined)
		return controllers[section];
	else
		return controllers[section] + "/" + subSection;
}

function callbackBySection(section) {
	var callbacks = {
		"section_asset" : function() {
			chartALE();
			return false;
		},
		"section_scenario" : function() {
			chartALE();
			return false;
		},
		"section_analysis" : function() {
			return analysisTableSortable();
		},
		"section_profile_analysis" : function() {
			return analysisTableSortable();
		},
		"section_actionplans" : function() {
			compliance('27001');
			compliance('27002');
			reloadSection("section_summary");
			return false;
		},
		"section_summary" : function() {
			summaryCharts();
			return false;
		}

	};
	return callbacks[section];
}

function sectionPretreatment(section) {
	var pretreatment = {
		"section_assessment" : function(data) {
			var trickCarousel = new TrickCarousel($(data).find("table[trick-table]"));
			trickCarousel.initialise();
		}
	};
	return pretreatment[section];
}

function SectionSmartUpdate(sectionName, data) {
	this.sectionName = sectionName;
	this.data = data;

};

SectionSmartUpdate.prototype = {
	Update : function() {
		switch (this.sectionName) {
		case "section_asset":
		case "section_scenario":
		case "section_phase":
			return this.__generic_update(this.data, "#" + this.sectionName, 1);
		default:
			break;
		}
		return true;
	},
	__generic_update : function(src, dest, indexColnum) {
		try {
			var tableDestTrs = $(dest).find("tbody tr");
			if (!tableDestTrs.length){
				console.log("no tbody")
				return true;
			}
			for (var i = 0; i < tableDestTrs.length; i++) {
				var trickId = $(tableDestTrs[i]).attr("trick-id");
				if (trickId == undefined){
					console.log("update");
					return true;
				}
				var $tr = $(src).find("tbody tr[trick-id='" + trickId + "']");
				if ($tr.length)
					$(tableDestTrs[i]).replaceWith($tr);
				else
					$(tableDestTrs[i]).remove();
			}
			var $tbody = $(dest).find("tbody");
			var tableSourceTrs = $(src).find("tbody tr[trick-id]");
			for (var i = 0; i < tableSourceTrs.length; i++) {
				var trickId = $(tableSourceTrs[i]).attr("trick-id");
				var $tr = $(dest).find("tbody tr[trick-id='" + trickId + "']");
				if (!$tr.length)
					$(tableSourceTrs[i]).appendTo($tbody);
			}
			if (indexColnum >= 0) {
				var tableDestTrs = $(dest).find("tbody tr");
				for (var i = 0; i < tableDestTrs.length; i++) {
					var $td = $(tableDestTrs[i]).find("td");
					if (!$td.length || $td.length < indexColnum)
						return true;
					$($td[indexColnum]).text(i+1);
					console.log(i);
				}
			}
			console.log("success");
			return false;
		} catch (e) {
			console.log(e);
			return true;
		}
	}

}

/*
 */
