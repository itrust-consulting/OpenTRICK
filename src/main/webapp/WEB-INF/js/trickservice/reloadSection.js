// load sections
function loadPanelBodiesOfSection(section, refreshOnly) {

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
			if (!refreshOnly) {
				var callback = callbackBySection(section);
				if ($.isFunction(callback))
					callback();
			}
			return false;
		},
		error : unknowError
	});
	return false;
}

// reload sections
function reloadSection(section, subSection, refreshOnly) {
	if (Array.isArray(section)) {
		for (var int = 0; int < section.length; int++) {
			if (Array.isArray(section[int]))
				reloadSection(section[int][0], section[int][1], refreshOnly);
			else
				reloadSection(section[int], subSection, refreshOnly);
		}
	} else {
		var controller = controllerBySection(section, subSection);
		if (controller == null || controller == undefined)
			return false;

		if (section == "section_standard"){
			location.reload();
			return false;
		}

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
				if (!refreshOnly) {
					var callback = callbackBySection(section);
					if ($.isFunction(callback))
						callback();
				}
				return false;
			},
			error : unknowError
		});
	}
	return false;
}

function controllerBySection(section, subSection) {
	var controllers = {
		"section_asset" : "/Analysis/Asset/Section",
		"section_parameter" : "/Analysis/Parameter/Section",
		"section_scenario" : "/Analysis/Scenario/Section",
		"section_phase" : "/Analysis/Phase/Section",
		"section_analysis" : "/Analysis/Section",
		"section_profile_analysis" : "/AnalysisProfile/Section",
		"section_standard" : "/Analysis/Standard/Section",
		"section_customer" : "/KnowledgeBase/Customer/Section",
		"section_language" : "/KnowledgeBase/Language/Section",
		"section_kb_standard" : "/KnowledgeBase/Standard/Section",
		"section_user" : "/Admin/User/Section",
		"section_actionplans" : "/Analysis/ActionPlan/Section",
		"section_summary" : "/Analysis/ActionPlanSummary/Section",
		"section_riskregister" : "/Analysis/RiskRegister/Section"
	};

	if (section.match("^section_standard_"))
		return "/Analysis/Standard/Section/" + section.substr(17, section.length);

	if (subSection == null || subSection == undefined)
		return controllers[section];
	else
		return controllers[section] + "/" + subSection;
}

function callbackBySection(section) {
	var callbacks = {
		"section_asset" : function() {
			reloadSection("section_scenario", undefined, true /*
																 * prevent
																 * propagation
																 */);
			chartALE();
			return false;
		},
		"section_scenario" : function() {
			reloadSection("section_asset", undefined, true /*
															 * prevent
															 * propagation
															 */);
			chartALE();
			return false;
		},
		"section_analysis" : function() {
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
		},
		"section_standard" : function() {

			$("#standardmenu a[href^='#anchorMeasure_']").closest("li").remove();

			var text = "";

			$("#" + section + " div[id^='section_standard_']").each(function() {
				var standard = $(this).attr("trick-label");
				var standardid = $(this).attr("trick-id");
				var link = "#anchorMeasure_" + standardid;
				text += "<li><a href='" + link + "'>" + standard + "</a></li>";
			});

			$("#standardmenu").prepend(text);

			$("#" + section + " td.popover-element").popover("hide");
		}

	};

	if (section.match("^section_standard_")) {
		$("#" + section + " td.popover-element").popover('hide');
	}

	return callbacks[section];
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
		case "section_user":
		case "section_standard":
		case "section_language":
		case "section_customer":
		case "section_profile_analysis":
			return this.__generic_update(this.data, "#" + this.sectionName, -1);
		default:
			break;
		}
		return true;
	},
	__generic_update : function(src, dest, indexColnum) {
		try {
			var tableDestTrs = $(dest).find("tbody tr");
			if (!tableDestTrs.length)
				throw "tbody cannot be found";

			if ($(tableDestTrs[0]).find("td").length != $(src).find("tbody>tr:first>td").length)
				throw "Table header has been changed";

			for (var i = 0; i < tableDestTrs.length; i++) {
				var trickId = $(tableDestTrs[i]).attr("trick-id");
				if (trickId == undefined)
					throw "trick-id cannot be found";
				var $check = $(tableDestTrs[i]).find("td:first-child>input:checked");
				var $tr = $(src).find("tbody tr[trick-id='" + trickId + "']");
				if ($tr.length) {
					if ($check.length)
						$($tr).find("td:first-child>input").prop("checked", true);
					$(tableDestTrs[i]).replaceWith($tr);
				} else {
					if ($check.length)
						$check.attr("checked", false).change();
					$(tableDestTrs[i]).remove();
				}
			}
			var $tbody = $(dest).find("tbody");
			var tableSourceTrs = $(src).find("tbody tr[trick-id]");
			for (var i = 0; i < tableSourceTrs.length; i++) {
				var trickId = $(tableSourceTrs[i]).attr("trick-id");
				var $tr = $(dest).find("tbody tr[trick-id='" + trickId + "']");
				if (!$tr.length)
					$(tableSourceTrs[i]).appendTo($tbody);
			}

			var $tfooter = $(dest).find("tfoot");
			if ($tfooter.length) {
				// replaceWith does not work, fix with this following code
				var $parent = $tfooter.parent();
				$tfooter.remove();
				$(src).find("tfoot").appendTo($parent);
			}

			var checked = $($tbody).find("td:first-child>input:checked");
			if (checked.length)
				$(checked).change();
			if (indexColnum >= 0) {
				var tableDestTrs = $(dest).find("tbody tr");
				for (var i = 0; i < tableDestTrs.length; i++) {
					var $td = $(tableDestTrs[i]).find("td");
					if (!$td.length || $td.length < indexColnum)
						throw "Index out of bound";
					$($td[indexColnum]).html(i + 1);
				}
			}
			return false;
		} catch (e) {
			console.log(e);
			return true;
		}
	}

}

/*
 */
