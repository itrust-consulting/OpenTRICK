// load sections
function loadPanelBodiesOfSection(section, refreshOnly) {
	if (refreshOnly == "")
		refreshOnly = undefined
	var $section = $("#" + section);
	if ($section.is(":visible")) {
		var controller = findControllerBySection(section);
		if (controller == null || controller == undefined)
			return false;
		$
				.ajax({
					url : context + controller,
					type : "get",
					async : true,
					contentType : "application/json;charset=UTF-8",
					success : function(response, textStatus, jqXHR) {
						var $newSection = $("*[id = '" + section + "']", new DOMParser().parseFromString(response, "text/html")), smartUpdate = new SectionSmartUpdate(section,
								$newSection);
						if (smartUpdate.Update())
							$section.replaceWith($newSection);
						if (!refreshOnly) {
							var callback = callbackBySection(section);
							if ($.isFunction(callback))
								callback();
						}
						return false;
					},
					error : unknowError
				});
	} else 
		$section.closest(".tab-tab-pane").attr("data-update-required", true).attr("data-trigger", 'loadPanelBodiesOfSection').attr("data-parameters", [ section, refreshOnly ]);
	return false;
}

// reload sections
function reloadSection(section, subSection, refreshOnly) {
	if (subSection == "")
		subSection = undefined;
	if (refreshOnly == "")
		refreshOnly = undefined
	if (Array.isArray(section)) {
		for (var i = 0; i < section.length; i++) {
			if (Array.isArray(section[i]))
				reloadSection(section[i][0], section[i][1], refreshOnly);
			else
				reloadSection(section[i], subSection, refreshOnly);
		}
	} else if (section == "section_standard")
		location.reload();
	else {
		var $section = $("#" + section);
		if ($section.is(":visible")) {
			var controller = findControllerBySection(section, subSection);
			if (controller == null || controller == undefined)
				return false;
			$.ajax({
				url : context + controller,
				type : "get",
				async : true,
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					if (subSection != null && subSection != undefined)
						section += "_" + subSection;
					$newSection = $("*[id = '" + section + "']", new DOMParser().parseFromString(response, "text/html"));
					if ($newSection.length) {
						var smartUpdate = new SectionSmartUpdate(section, $newSection);
						if (smartUpdate.Update()) {
							$("#" + section).replaceWith($newSection);
							fixTableHeader($("table.table-fixed-header,table.table-fixed-header-analysis", $newSection));
						}
					} else {
						var $tabsSection = $(doc).find(".tab-pane");
						for (var i = 0; i < $tabsSection.length; i++)
							$("#" + $($tabsSection[i]).attr("id")).html($($tabsSection[i]).html());
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
		} else {
			var $tab = $section.closest(".tab-pane");
			$tab.attr("data-update-required", true);
			$tab.attr("data-trigger", 'reloadSection');
			$tab.attr("data-parameters", [ section, subSection, refreshOnly ]);
		}
	}
	return false;
}

function findControllerBySection(section, subSection) {
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
		"section_riskregister" : "/Analysis/RiskRegister/Section",
		"section_soa" : "/Analysis/Standard/SOA"
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
		"section_actionplans" : function() {
			compliance('27001');
			compliance('27002');
			reloadSection("section_summary");
			reloadSection("section_soa");
			return false;
		},
		"section_summary" : function() {
			summaryCharts();
			return false;
		},
		"section_standard" : function() {
			$("#" + section + " [data-toggle='popover']").popover('hide').on('show.bs.popover', togglePopever);
			$("#" + section + " [data-toggle='tooltip']").tooltip("hide");
		},
		"section_language" : function() {
			rebuildMeasureLanguage();
		}

	};

	if (section.match("^section_standard_"))
		$("#" + section + " td.popover-element").popover('hide');

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
		case "section_kb_measure":
		case "section_soa":
			return this.__generic_update(this.data, "#" + this.sectionName, -1);
		default:
			break;
		}
		return true;
	},
	__generic_update : function(src, dest, indexColnum) {
		try {
			var $dest = $(dest), $src = $(src), $tableDestTrs = $("tbody>tr", dest);
			if (!$tableDestTrs.length)
				throw "tbody cannot be found";
			if ($("td", $tableDestTrs[0]).length != $("tbody>tr:first>td", $src).length)
				throw "Table header has been changed";
			for (var i = 0; i < $tableDestTrs.length; i++) {
				var trickId = $($tableDestTrs[i]).attr("data-trick-id");
				if (trickId == undefined)
					throw "data-trick-id cannot be found";
				var $check = $("td:first-child>input:checked", $tableDestTrs[i]);
				var $tr = $("tbody tr[data-trick-id='" + trickId + "']", $src);
				if ($tr.length) {
					if ($check.length)
						$("td:first-child>input", $tr).prop("checked", true);
					$($tableDestTrs[i]).replaceWith($tr);
				} else {
					if ($check.length)
						$check.attr("checked", false).change();
					$($tableDestTrs[i]).remove();
				}
			}
			var $tbody = $("tbody", $dest);
			var $tableSourceTrs = $("tbody>tr[data-trick-id]", $src);
			for (var i = 0; i < $tableSourceTrs.length; i++) {
				var trickId = $($tableSourceTrs[i]).attr("data-trick-id");
				var $tr = $("tbody>tr[data-trick-id='" + trickId + "']", $dest);
				if (!$tr.length)
					$($tableSourceTrs[i]).appendTo($tbody);
			}

			var $tfooter = $("tfoot", $dest);
			if ($tfooter.length) {
				// replaceWith does not work, fix with this following code
				var $parent = $tfooter.parent();
				$tfooter.remove();
				$("tfoot", $src).appendTo($parent);
			}

			var $checked = $("td:first-child>input:checked", $tbody);
			if ($checked.length)
				$checked.change();
			if (indexColnum >= 0) {
				var $tableDestTrs = $("tbody>tr", $dest);
				for (var i = 0; i < $tableDestTrs.length; i++) {
					var $td = $("td", $tableDestTrs[i]);
					if (!$td.length || $td.length < indexColnum)
						throw "Index out of bound";
					$($td[indexColnum]).html(i + 1);
				}
			}
			return false;
		} catch (e) {
			console.log("reload error: " + e);
			return true;
		}
	}

}

/*
 */
