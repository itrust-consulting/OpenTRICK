


// load sections

function loadPanelBodiesOfSection(section, refreshOnly) {
	if (refreshOnly === null || refreshOnly == "")
		refreshOnly = false
	let $section = $("#" + section);
	if ($section.is(":visible")) {
		let controller = findControllerBySection(section);
		if (controller == null || controller == undefined)
			return false;
		$
			.ajax({
				url: context + controller,
				type: "get",
				async: true,
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					let $newSection = $("*[id = '" + section + "']", new DOMParser().parseFromString(response, "text/html")), smartUpdate = new SectionSmartUpdate(section,
						$newSection);
					if (smartUpdate.Update())
						$section.replaceWith($newSection);
					if (!refreshOnly) {
						let callback = callbackBySection(section);
						if ($.isFunction(callback))
							callback();
					}
					return false;
				},
				error: unknowError
			});
	} else
		$section.closest(".tab-pane").attr("data-update-required", true).attr("data-trigger", 'loadPanelBodiesOfSection').attr("data-parameters", [section, refreshOnly]);
	return false;
}

// reload sections
function reloadSection(section, subSection, refreshOnly, prepend) {
	if (subSection === null || subSection == "")
		subSection = undefined;
	if (refreshOnly === null || refreshOnly == "")
		refreshOnly = false
	if (!(prepend === true || prepend === 'true'))
		prepend = undefined;
	if (Array.isArray(section)) {
		for (let i = 0; i < section.length; i++) {
			if (Array.isArray(section[i]))
				reloadSection(section[i][0], section[i][1], refreshOnly, prepend);
			else
				reloadSection(section[i], subSection, refreshOnly, prepend);
		}
	} else if (section == "section_chart")
		reloadCharts();
	else if (!staticReload(section)) {
		let sectionName = section.replace("section_admin", "section"), $section = $("#" + sectionName);
		if ($section.is(":visible")) {
			let controller = findControllerBySection(section, subSection);
			if (controller == null || controller == undefined)
				return false;
			let $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + controller,
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					if (subSection !== undefined)
						sectionName += "_" + subSection;
					let content = new DOMParser().parseFromString(response, "text/html"), $newSection = $("*[id = '" + sectionName + "']", content);
					if ($newSection.length) {
						let smartUpdate = new SectionSmartUpdate(sectionName, $newSection, prepend);
						if (smartUpdate.Update()) {
							$("#" + sectionName).replaceWith($newSection);
							fixTableHeader($("table.table-fixed-header,table.table-fixed-header-analysis", $newSection));
						}
					} else {
						let $tabsSection = $(content).find(".tab-pane");
						for (let i = 0; i < $tabsSection.length; i++)
							$("#" + $($tabsSection[i]).attr("id")).html($($tabsSection[i]).html());
					}
					if (!refreshOnly) {
						let callback = callbackBySection(section);
						if ($.isFunction(callback))
							callback();
					}
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		} else if (section != undefined) {
			let $container = section.startsWith("section_standard_") ? $section : $section.closest(".tab-pane");
			$container.attr("data-update-required", true);
			$container.attr("data-trigger", 'reloadSection');
			$container.attr("data-parameters", [section, subSection, refreshOnly, prepend]);
		}
	}
	return false;
}

function staticReload(key) {
	switch (key) {
		case "section_standard":
		case "SETTING_ALLOWED_TICKETING_SYSTEM_LINK":
			setTimeout(() => location.reload(), 10);
			return true;
		default:
			return false;
	}
}

function findControllerBySection(section, subSection) {
	let controllers = {
		"section_asset": "/Analysis/Asset/Section",
		"section_scenario": "/Analysis/Scenario/Section",
		"section_phase": "/Analysis/Phase/Section",
		"section_analysis": "/Analysis/Section",
		"section_profile_analysis": "/AnalysisProfile/Section",
		"section_standard": "/Analysis/Standard/Section",
		"section_customer": "/KnowledgeBase/Customer/Section",
		"section_admin_customer": "/Admin/Customer/Section",
		"section_language": "/KnowledgeBase/Language/Section",
		"section_kb_standard": "/KnowledgeBase/Standard/Section",
		"section_user": "/Admin/User/Section",
		"section_actionplans": "/Analysis/ActionPlan/Section",
		"section_summary": "/Analysis/ActionPlanSummary/Section",
		"section_riskregister": "/Analysis/RiskRegister/Section",
		"section_soa": "/Analysis/Standard/SOA",
		"section_ids": "/Admin/IDS/Section",
		"section_kb_scale_type": "/KnowledgeBase/ScaleType",
		"section_parameter": "/Analysis/Parameter/Section",
		"section_parameter_impact_probability": "/Analysis/Parameter/Impact-probability/Section",
		"section_risk-information_risk": "/Analysis/Risk-information/Section/Risk",
		"section_risk-information_vul": "/Analysis/Risk-information/Section/Vul",
		"section_risk-information_threat": "/Analysis/Risk-information/Section/Threat",
		"section_credential": "/Account/Credential/Section"

	};

	if (section.match("^section_standard_")) {
		$("[data-toggle='tooltip']", "#" + section).tooltip('destroy');
		return "/Analysis/Standard/Section/" + section.substr(17, section.length);
	}

	if ("section_riskregister" == section && !application.analysisType.isQualitative())
		return undefined;

	if (subSection == null || subSection == undefined)
		return controllers[section];
	else
		return controllers[section] + "/" + subSection;
}

function callbackBySection(section) {
	let callbacks = {
		"section_asset": function () {
			reloadSection("section_scenario", undefined, true);
			if (application.analysisType.isQualitative())
				reloadSection("section_riskregister", undefined, true);
			reloadAssetScenarioChart();
			return false;
		},
		"section_scenario": function () {
			reloadSection("section_asset", undefined, true);
			if (application.analysisType.isQualitative())
				reloadSection("section_riskregister", undefined, true);
			reloadAssetScenarioChart();
			return false;
		},
		"section_actionplans": function () {
			compliance('27001');
			compliance('27002');
			reloadSection("section_summary");
			reloadSection("section_soa");
			return false;
		},
		"section_summary": function () {
			summaryCharts();
			return false;
		},
		"section_standard": function () {
			$("[data-toggle='tooltip']", "#" + section).tooltip().on('show.bs.tooltip', toggleToolTip)
		},
		"section_language": function () {
			rebuildMeasureLanguage();
			reloadSection("section_kb_scale_type");

		},
		"section_phase": function () {
			if (application["estimation-helper"]) {// See risk-estimation
				application["estimation-helper"].$tabSection.attr("data-update-required", application["estimation-helper"].invalidate = true);
				if (application["standard-caching"])// See risk-estimation ->
					// manage measure
					application["standard-caching"].clear();
			}
		}, "section_parameter": () => {
			if (application.analysisType.isQualitative())
				reloadRiskChart(true);
		}, "section_parameter_impact_probability": () => {
			if (application.analysisType.isQualitative())
				reloadRiskChart(true);
		}
	};

	if (section.match("^section_standard_"))
		$("[data-toggle='tooltip']", "#" + section).tooltip().on('show.bs.tooltip', toggleToolTip);

	return callbacks[section];
}

function SectionSmartUpdate(sectionName, data, prepend) {
	this.sectionName = sectionName;
	this.data = data;
	this.prepend = prepend;
};

SectionSmartUpdate.prototype = {
	Update: function () {
		switch (this.sectionName) {
			case "section_asset":
			case "section_phase":
			case "section_scenario":
				return this.__generic_update(this.data, "#" + this.sectionName, 1);
			case "section_soa":
				return this.__multiTab_update(this.data, "#" + this.sectionName, -1, { type: "string", index: 0 });
			case "section_ids":
			case "section_user":
			case "section_analysis":
			case "section_standard":
			case "section_language":
			case "section_customer":
			case "section_credential":
			case "section_kb_measure":
			case "section_profile_analysis":
				return this.__generic_update(this.data, "#" + this.sectionName, -1);
			default:
				if (this.sectionName.match("^section_standard_"))
					return this.__generic_update(this.data, "#" + this.sectionName, -1);
				break;
		}
		return true;
	},
	__multiTab_update: function (data, section, indexColnum, sort) {
		let $sources = $("table", data);
		let $targets = $("table", section);
		if ($sources.length != $targets.length)
			return true;
		let result = false;
		for (let i = 0; i < $sources.length; i++) {
			result |= this.__generic_update($($sources[i]).parent(), $($targets[i]).parent(), indexColnum, sort);
		}

		return result;
	}
	,
	__generic_update: function (src, dest, indexColnum, sort) {
		try {
			let $dest = $(dest), $src = $(src), $tableDestTrs = $("tbody>tr", dest);
			if (!$tableDestTrs.length)
				throw "tbody cannot be found";
			if ($("td", $tableDestTrs[0]).length != $("tbody>tr:first>td", $src).length)
				throw "Table header has been changed";
			for (let i = 0; i < $tableDestTrs.length; i++) {
				let trickId = $($tableDestTrs[i]).attr("data-trick-id");
				if (trickId == undefined)
					throw "data-trick-id cannot be found";
				let $check = $("td:first-child>input:checked", $tableDestTrs[i]);
				let $tr = $("tbody tr[data-trick-id='" + trickId + "']", $src);
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
			let hasNewTr = false;
			let $tbody = $("tbody", $dest);
			let $tableSourceTrs = $("tbody>tr[data-trick-id]", $src);
			for (let i = 0; i < $tableSourceTrs.length; i++) {
				let trickId = $($tableSourceTrs[i]).attr("data-trick-id");
				let $tr = $("tbody>tr[data-trick-id='" + trickId + "']", $dest);
				if (!$tr.length) {
					if (this.prepend)
						$($tableSourceTrs[i]).prependTo($tbody);
					else $($tableSourceTrs[i]).appendTo($tbody);
					hasNewTr |= true;
				}
			}

			if (hasNewTr && sort !== undefined) {
				let index = sort["index"];
				let selector = "td:eq(" + index + ")";
				let compare = natsort();
				$tbody.find("tr").sort((e1, e2) => {
					let value1 = $($(e1).find(selector)).text();
					let value2 = $($(e2).find(selector)).text();
					return compare(value1, value2);
				}).detach().appendTo($tbody);
			}

			let $tfooter = $("tfoot", $dest);
			if ($tfooter.length) {
				// replaceWith does not work, fix with this following code
				let $parent = $tfooter.parent();
				$tfooter.remove();
				$("tfoot", $src).appendTo($parent);
			}

			let $checked = $("td:first-child>input", $tbody);
			if ($checked.length)
				$checked.change();
			if (indexColnum >= 0) {
				let $tableDestTrs = $("tbody>tr", $dest);
				for (let i = 0; i < $tableDestTrs.length; i++) {
					let $td = $("td", $tableDestTrs[i]);
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
