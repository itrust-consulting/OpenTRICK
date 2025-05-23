let activeSelector = undefined, helper = undefined, standardCaching = undefined;

/**
 * Saves the assessment data.
 *
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns false.
 */
function saveAssessmentData(e) {
	let $assessmentUI = $("#estimation-ui"), $target = $(e.currentTarget), value = $target.val(), idScenario = $assessmentUI.attr('data-trick-scenario-id'), idAsset = $assessmentUI
		.attr('data-trick-asset-id'), name = $target.attr('name'), type = $target.attr('data-trick-type'), oldValue = $target.hasAttr("placeholder") ? $target
			.attr("placeholder") : $target.attr("data-trick-value");
	if (value == oldValue || value == '' && oldValue == $target.attr("title"))
		$target.parent().removeClass('has-error').removeAttr("title");
	else {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/EditField/Estimation/Update?asset=" + idAsset + "&scenario=" + idScenario,
			type: "post",
			data: '{"id":' + idAsset + ', "fieldName":"' + name + '", "value":"' + defaultValueByType(value, type) + '", "type": "' + type + '"}',
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				if (response.message == undefined)
					unknowError();
				else {
					let $parent = $target.parent();
					if (response.error) {
						$parent.on("show.bs.popover", function () {
							let popover = $parent.data('bs.popover');
							setTimeout(function () {
								popover.destroy();
							}, 3000);
						});
						$parent.addClass("has-error").removeClass("has-success").popover({ "content": response.message, 'triger': 'manual', "container": 'body', 'placement': 'auto', 'template': application.errorTemplate }).attr('title', response.message).popover("show");
					}
					else {
						$parent.removeAttr("title").removeClass("has-error");
						if ($target.attr("readonly"))
							$target.removeClass("has-success");
						else
							$parent.addClass("has-success");

						let updated = false;
						for (let i = 0; i < response.fields.length; i++) {
							let field = response.fields[i], $element = $target;
							if (name == field.name)
								updated = true;
							else {
								$element = $("[name='" + field.name + "'].form-control", $assessmentUI);
								if (!$element.length && (field.name.startsWith("ALE") || field.name.startsWith("ILR-VALUE-"))) {
									$("[data-name='" + field.name + "']", $assessmentUI).text(field.value).attr("title", field.title);
									continue;
								}
							}
							for (let fieldName in field) {
								switch (fieldName) {
									case "value":
										$element.attr("placeholder", field[fieldName]).val(field[fieldName]);
										break;
									case "title":
										$element.attr(fieldName, field[fieldName]);
										break;
									case "color":
										$element.css({ "border-color": field.color });
										break;
								}
							}
						}
						if (!updated) {
							$target.attr($target.hasAttr("placeholder") ? "placeholder" : "data-trick-value", $target.val());
							if ($target.hasAttr("list"))
								$target.attr("title", $("option[value='" + value + "']", "#" + $target.attr("list")).attr("title"));
							else if ($target.tagName = 'SELECT')
								$target.attr("title", $target.find("option:selected").attr('title'));
						}
						if (application.analysisType.isQualitative())
							reloadSection("section_riskregister", undefined, true);
						if (application.analysisType.isQuantitative())
							reloadAssetScenario();
						reloadAssetScenarioChart();
						setTimeout(function () {
							if ($target.attr("readonly"))
								$target.removeClass("has-success");
							else
								$parent.removeClass("has-success");
						}, 3000);
					}
				}
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

/**
 * Toggles the visibility of the additional action plan textarea.
 * 
 * @param {Event} e - The event object (optional).
 * @returns {boolean} Returns false if the additional action plan textarea is not found.
 */
function toggleAdditionalActionPlan(e) {
	let $additional = $("textarea[name='riskProfile.actionPlan']");
	if (!$additional.length)
		return false;
	if (e == undefined) {
		if (helper['toggleAdditionalActionPlan'] == undefined) {
			helper['toggleAdditionalActionPlan'] = $additional.is(":visible") ? 'show' : 'hide';
		}
		else {
			let $show = $("#menu_estimation_action_plan a[data-action='show']").parent(), $hide = $("#menu_estimation_action_plan a[data-action='hide']").parent();
			if (helper['toggleAdditionalActionPlan'] == 'hide') {
				$additional.hide();
				$show.show();
				$hide.hide();
			} else {
				$additional.css({ "display": "inline-block" });
				$show.hide();
				$hide.show();
			}
		}
	} else {
		$("a[data-action='hide'],a[data-action='show']", "#menu_estimation_action_plan").parent().toggle();
		if (helper["toggleAdditionalActionPlan"] == 'hide') {
			$additional.css({ "display": "inline-block" });
			helper["toggleAdditionalActionPlan"] = 'show';
		}
		else {
			$additional.css({ "display": "none" });
			helper["toggleAdditionalActionPlan"] = 'hide';
		}
	}
}

/**
 * Loads assessment data for a given ID.
 *
 * @param {string} id - The ID of the assessment data to load.
 * @returns {boolean} - Returns true if the assessment data was loaded successfully, false otherwise.
 */
function loadAssessmentData(id) {
	return application["estimation-helper"].reload(id) == undefined;
}

/**
 * Updates the user interface for the assessment.
 */
function updateAssessmentUI() {
	let $assessment = $("#tab-risk-estimation div.list-group:visible>.list-group-item.active");
	if (!$assessment.is(":focus") && $("div[role='left-menu']").css("position") == "fixed")
		updateScroll($assessment);
	loadAssessmentData($assessment.attr("data-trick-id"));
}

/**
 * Refreshes the estimation based on the specified type.
 * 
 * @param {string} type - The type of estimation to refresh.
 */
function refreshEstimation(type) {
	let $current = $("#tab-risk-estimation div[data-trick-content='" + type + "'] div.list-group > a.list-group-item.active");
	if (!$current.length)
		$current = $("#tab-risk-estimation div[data-trick-content='" + type + "'] div.list-group > a.list-group-item:first");
	$current.trigger("click")
}

/**
 * Updates the estimation select based on the provided type, elements, and status.
 * @param {string} type - The type of the select element.
 * @param {Array} elements - The elements to update.
 * @param {string} status - The status to set for the elements.
 * @returns {boolean} - Returns false.
 */
function updateEstimationSelect(type, elements, status) {
	let $selections = $("select[name='" + type + "']>option[data-trick-type][data-trick-selected!='" + status + "'],div[data-trick-content='" + type + "'] a[data-trick-type][data-trick-id][data-trick-selected!='" + status + "']", "#tab-risk-estimation").filter(function () {
		return this.tagName == "OPTION" ? elements.indexOf(this.getAttribute("value")) != "-1" : elements.indexOf(this.getAttribute("data-trick-id")) != "-1";
	}).attr("data-trick-selected", status);
	if (status == "true")
		$selections.show();
	else $selections.hide();

	refreshEstimation(type);
	return false;
}

/**
 * Updates the estimation item based on the provided parameters.
 *
 * @param {string} type - The type of the estimation item.
 * @param {object} item - The item to be updated.
 * @param {string} status - The status of the item.
 * @returns {boolean} - Returns false.
 */
function updateEstimationIteam(type, item, status) {
	let $tabSection = $("#tab-risk-estimation"), $selector = $("select[name='" + type + "']", $tabSection), $option = $("option[data-trick-type][value='" + item.id + "']", $selector), $link = undefined;
	if ($option.length)
		$link = $("div[data-trick-content='" + type + "'] a[data-trick-type][data-trick-id='" + item.id + "'][data-trick-selected!='" + status + "']", $tabSection);
	else {
		$option = $("<option/>");
		$link = $("<a href='#' class='list-group-item' style='white-space: nowrap; overflow: hidden; text-overflow: ellipsis;' />");
	}
	for (let key in item) {
		switch (key) {
			case "id":
				$option.attr("value", item[key]);
				$link.attr("data-trick-id", item[key]);
				break;
			case "name":
				$option.text(item[key]).attr("title", item[key]);
				$link.text(item[key]).attr("title", item[key]);
				break;
			case "type":
				$option.attr("data-trick-type", item[key]);
				$link.attr("data-trick-type", item[key]);
				break;
			case "assetLinked":
				$option.attr("data-trick-linked", item[key]);
				$link.attr("data-trick-linked", item[key]);
				break;
			case "assetValues":
			case "assetTypeValues":
				let types = item[key].join("|");
				$option.attr("data-trick-type", types);
				$link.attr("data-trick-type", types);
				break;
			case "selected":
				$option.attr("data-trick-selected", item[key]);
				$link.attr("data-trick-selected", item[key]);
				if (item[key] == "true")
					$option.removeClass("hidden");
				else {
					$option.addClass("hidden");
					$link.hide();
				}
		}
	}
	if (!$option.parent().length) {
		$option.appendTo($selector);
		$link.appendTo("div[data-trick-content='" + type + "']>div.list-group", $tabSection).on("click", changeAssessment);
	}

	if ($tabSection.is(":visible"))
		helper.invalidate = true;
	refreshEstimation(type);
	return false;
}

/**
 * Removes the specified elements from the risk estimation.
 * 
 * @param {string} type - The type of elements to remove.
 * @param {Array} elements - The elements to remove.
 * @returns {boolean} - Returns false.
 */
function removeEstimation(type, elements) {
	let $selections = $("select[name='" + type + "']>option[data-trick-type],div[data-trick-content='" + type + "'] a[data-trick-type][data-trick-id]", "#tab-risk-estimation").filter(function () {
		return this.tagName == "OPTION" ? elements.indexOf(this.getAttribute("value")) != "-1" : elements.indexOf(this.getAttribute("data-trick-id")) != "-1";
	}).remove();
	refreshEstimation(type);
	return false;
}

/**
 * Updates the risk estimation.
 * 
 * @param {boolean} force - Indicates whether to force the update.
 */
function riskEstimationUpdate(force) {
	if (helper == undefined)
		initialiseRiskEstimation();
	if (helper.$tabSection.is(":visible")) {
		if (force === true)
			helper.invalidate = true;
		updateRiskEstimationNavigation();
		helper.updateContent();
	} else
		helper.$tabSection.attr("data-update-required", helper.invalidate = true);
}

/**
 * Updates the risk estimation navigation based on the current assessment and selector.
 * @returns {boolean} Returns false.
 */
function updateRiskEstimationNavigation() {
	let $tabSection = $("#tab-risk-estimation"), $currentAssessment = $("div[data-trick-content]:visible .list-group-item.active", $tabSection);

	if (activeSelector == undefined)
		activeSelector = $currentAssessment.closest("[data-trick-content]").attr("data-trick-content") == "scenario" ? "asset" : "scenario";

	let $currentSelector = $("select[name='" + activeSelector + "']:visible>option:selected", $tabSection), $previousSelector = $(
		"[data-trick-nav='previous-selector']", $tabSection).parent(), $nextSelector = $("[data-trick-nav='next-selector']", $tabSection).parent();

	if ($currentSelector.next("[data-trick-selected='true']:first").length)
		$nextSelector.removeClass("disabled");
	else
		$nextSelector.addClass("disabled");

	if ($currentSelector.prev("[data-trick-selected='true']:last").length)
		$previousSelector.removeClass("disabled");
	else
		$previousSelector.addClass("disabled");
	return false;

}

/**
 * Handles the change event for the assessment.
 * @param {Event} e - The event object.
 * @returns {boolean} - Returns false to prevent default behavior.
 */
function changeAssessment(e) {
	let $target = $(e.currentTarget);
	if (!$target.hasClass('active')) {
		$(".list-group-item.active", $target.closest(".list-group")).removeClass("active");
		$target.addClass('active');
	}
	riskEstimationUpdate();
	return false;
}

/**
 * Represents an AssessmentHelder object.
 * @constructor
 */
function AssessmentHelder() {
	this.id = 0;
	this.invalidate = true;
	this.updateLocked = false;
	this.names = ["asset", "scenario"];
	this.$tabSection = $("#tab-risk-estimation");
	this.section = "div[data-view-name='estimation-ui']";
	this.asset = $("select[name='asset']", this.$tabSection);
	this.scenario = $("select[name='scenario']", this.$tabSection);
	this.isReadOnly = application.openMode.value.startsWith("read-only");
	this.lastSelected = {
		asset: this.asset.find("option[data-trick-selected='true']:first").val(),
		scenario: this.scenario.find("option[data-trick-selected='true']:first").val()
	};
	this.switchControl(this.asset.val() == "0" ? "scenario" : "asset");

}

AssessmentHelder.prototype = {

	getCurrent: function (name) {
		return name == "asset" ? this.asset : this.scenario;
	},
	getOther: function (name) {
		return name == "asset" ? this.scenario : this.asset;
	},
	getOtherName: function (name) {
		return name == "asset" ? 'scenario' : 'asset';
	},
	setLastSelected: function (name, id) {
		if (id == "0")
			return false;
		this.lastSelected[name] = id;
	}, isValid: function (idAsset, idScanerio) {
		return activeSelector == "asset" ? parseInt(idAsset) > 0 : activeSelector == 'scenario' ? parseInt(idScanerio) > 0 : false;
	},
	getLastSelected: function (name) {
		return this.lastSelected[name];
	},
	getURL: function (idAsset, idScenario) {
		return context + "/Analysis/Assessment/" + (activeSelector == "asset" ? "Asset/" + idAsset + "/Load?idScenario=" + idScenario : "Scenario/" + idScenario + "/Load?idAsset=" + idAsset);
	},
	switchControl: function (name) {
		activeSelector = name;
		$("div[data-trick-content]", this.$tabSection).each(function () {
			if (this.getAttribute("data-trick-content") == name)
				$(this).hide();
			else
				$(this).show();
		});
		return this;
	}, updateContent: function () {
		let $current = this.getCurrent(activeSelector).find("option:selected"), type = $current.attr("data-trick-type"), $elements = $("div[data-trick-content]:visible a[data-trick-selected='true']", this.$tabSection);
		if (type == undefined) {
			if (this.getOther(activeSelector).find("option[data-trick-type]:not(.hidden)").length)
				this.getCurrent(activeSelector).trigger("change");
		}
		else {
			if (activeSelector == "asset") {
				let id = $current.val();
				$elements.each(function () {
					let $this = $(this), scenarioType = $this.attr("data-trick-type"), linked = $this.attr("data-trick-linked");
					let scenarioTypeArray = scenarioType.split("|");
					if (linked === "true") {
						if (scenarioType && scenarioType.length && (id === scenarioType || scenarioTypeArray.includes(id)))
							$this.show();
						else
							$this.hide();
					} else {
						if (scenarioType && scenarioType.length && (type === scenarioType || scenarioTypeArray.includes(type)))
							$this.show();
						else
							$this.hide();
					}
				});
			} else {
				let linked = $current.attr("data-trick-linked") === "true";
				let typeArray = type.split("|");;
				$elements.each(function () {
					let $this = $(this);
					let assetType = linked ? $this.attr("data-trick-id") : $this.attr("data-trick-type");
					if (type.length && (type === assetType || typeArray.includes(assetType)))
						$this.show();
					else
						$this.hide();
				});
			}
			$elements.removeClass("last").filter(":visible:last").addClass("last");
			if ($elements.filter(".active").is(":hidden"))
				$elements.filter(":visible:first").click();
			else
				updateAssessmentUI();
		}
		return this;
	}, updateTableViewContent: function ($section, idAsset, idScenario, content) {
		let $content = $(this.section, new DOMParser().parseFromString(content, "text/html")), isFullUpdate = true;
		if ($content.length) {
			this.$tabSection.attr("data-update-required", false);
			if ($content.attr("data-trick-content") == $section.attr("data-trick-content") && $section.attr("data-trick-asset-id") == idAsset && $section.attr("data-trick-scenario-id") == idScenario) {
				if (isFullUpdate === this.smartUpdate($content))
					$section.replaceWith($content).length > 0;
			} else
				$section.replaceWith($content).length > 0;

			if (isFullUpdate) {
				$section.attr('data-trick-asset-id', idAsset).attr('data-trick-scenario-id', idScenario);
				fixTableHeader($("table", $content))
			}
			this.invalidate = false;
		} else
			unknowError();
		return false;
	},
	tryUpdate: function (id) {
		let $section = $(this.section), $fields = $("input,select,textarea", $section);
		if ($fields.length) {
			this.invalidate = true;
			let $tr = $("tr[data-trick-id='" + id + "']", $section), $trTotal = $("tr.panel-footer", $section);
			$trTotal.addClass('warning').attr("title",
				MessageResolver("error.ui.update.wait.editing", 'Data was saved but user interface was not updated, it will be updated after edition'));
			$tr.addClass('warning').attr("title",
				MessageResolver("error.ui.update.wait.editing", 'Data was saved but user interface was not updated, it will be updated after edition'));
		} else if (this.invalidate || id != undefined)
			this.load($section, $section.attr("data-trick-asset-id"), $section.attr("data-trick-scenario-id"));
		return this;
	},
	smartUpdate: function (assessments) {
		try {
			let $section = $(this.section), tableDestTrs = $("tbody tr", $section);
			if (!(tableDestTrs.length && $("td", tableDestTrs[0]).length == $("tbody>tr:first>td", assessments).length))
				return true;

			for (let i = 0; i < tableDestTrs.length; i++) {
				let trickId = $(tableDestTrs[i]).attr("data-trick-id");
				if (trickId == undefined && $(tableDestTrs[i]).hasClass("panel-footer")) {
					let $tr = $("tbody tr.panel-footer", assessments);
					if ($tr.length)
						$(tableDestTrs[i]).replaceWith($tr);
					else
						$(tableDestTrs[i]).appendTo($("tbody", $section));
				} else {
					let $tr = $("tbody tr[data-trick-id='" + trickId + "']", assessments);
					if (!$tr.length)
						$(tableDestTrs[i]).remove();
					else
						$(tableDestTrs[i]).replaceWith($tr);
				}
			}
			let $tbody = $("tbody", $section), $footer = $("tbody tr.panel-footer", $section), tableSourceTrs = $("tbody tr[data-trick-id]", assessments)
			if (!$footer.length) {
				$footer = $("tbody tr.panel-footer", assessments);
				if ($footer.length)
					$footer.appendTo($tbody);
			}
			for (let i = 0; i < tableSourceTrs.length; i++) {
				let trickId = $(tableSourceTrs[i]).attr("data-trick-id"), $tr = $("tbody tr[data-trick-id='" + trickId + "']", $section);
				if (!$tr.length) {
					if ($footer.length)
						$(tableSourceTrs[i]).before($footer);
					else
						$(tableSourceTrs[i]).appendTo($tbody);
				}
			}
			return false;
		} catch (e) {
			console.log(e);
			return true;
		}
	}, reload: function (id) {
		if (id == undefined)
			id = "0";
		if (this.$tabSection.is(":visible")) {
			let $currentUI = $(this.section), idAsset = -3, idScenario = -3;
			if (activeSelector == "asset") {
				idAsset = $("select[name='asset']", this.$tabSection).val();
				idScenario = id;
			} else {
				idScenario = $("select[name='scenario']", this.$tabSection).val();
				idAsset = id;
			}
			return (!this.invalidate && $currentUI.attr("data-trick-asset-id") == idAsset && $currentUI.attr("data-trick-scenario-id") == idScenario
				&& $currentUI.attr("data-trick-content") == activeSelector) ? this : this.load($currentUI, idAsset, idScenario);
		}
		this.$tabSection.attr("data-update-required", this.invalidate = true);
		return this;

	}, load: function ($section, idAsset, idScenario) {
		return this.isValid(idAsset, idScenario) ? (idAsset == -1 || idScenario == -1 ? this.loadTableView($section, idAsset, idScenario) : this.loadSheetView($section, idAsset, idScenario)) : this.reset($section);
	}, loadTableView: function ($currentUI, idAsset, idScenario) {
		let $progress = $("#loading-indicator").show(), instance = this;
		$.ajax({
			url: this.getURL(idAsset, idScenario),
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				instance.updateTableViewContent($currentUI, idAsset, idScenario, response);
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
		return this;
	}, error: function (message) {
		return showDialog("#alert-dialog", message);
	}, loadSheetView: function ($currentUI, idAsset, idScenario) {
		let $progress = $("#loading-indicator").show(), instance = this;
		$.ajax({
			url: this.getURL(idAsset, idScenario),
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				let $assessmentUI = $(instance.section, new DOMParser().parseFromString(response, "text/html"));
				if ($assessmentUI.length) {

					backupFieldHeight("assesment", ["assessment-comment", "assessment-riskTreatment", "assessment-hiddenComment", "assessment-actionPlan", "assessment-cockpit"], $currentUI);

					restoreFieldHeight("assesment", ["assessment-comment", "assessment-riskTreatment", "assessment-hiddenComment", "assessment-actionPlan", "assessment-cockpit"], $assessmentUI);

					$currentUI.replaceWith($assessmentUI);

					toggleAdditionalActionPlan();

					if (OPEN_MODE.isReadOnly()) {
						$("select:not([disabled])", $assessmentUI).prop("disabled", true);
						$("input:not([disabled]),textarea:not([disabled])", $assessmentUI).attr("readOnly", true);
						$("a[data-controller]", $assessmentUI).remove();
					} else {
						$("select", $assessmentUI).on("change", saveAssessmentData);
						$("textarea,input:not([disabled]):not([data-menu-controller])", $assessmentUI).on("blur", saveAssessmentData);
						$("a[data-controller]", $assessmentUI).on("click", function () {
							let $description = $("#description", $assessmentUI), $control = $("i.fa", this);
							if ($control.hasClass("fa-lock")) {
								$control.removeClass("fa-lock").addClass("fa-unlock")
								$description.removeAttr("readonly");
							} else {
								$control.removeClass("fa-unlock").addClass("fa-lock")
								$description.attr("readonly", true).parent().removeAttr("title").removeClass("has-success has-error");
							}
							return false;
						});

						$("a[data-action='manage']", $assessmentUI).on("click", function (e) {
							if ($(this).parent().hasClass("disabled"))
								e.preventDefault();
							else {
								forceCloseToolTips();
								manageRiskProfileMeasure(idAsset, idScenario, e);
							}
						});

						$("thead input[type='checkbox']", $assessmentUI).trigger('change');

						$("a[data-action='delete']", $assessmentUI).on("click", function (e) {
							if ($(this).parent().hasClass("disabled"))
								e.preventDefault();
							else {
								forceCloseToolTips();
								deleteEstimationMeasures(idAsset, idScenario, $assessmentUI, $progress);
							}
						});

						$("input[list]").each(function () {
							generateDataList(this.getAttribute("list"));
						});

					}

					$("a[data-action='hide'],a[data-action='show']", $assessmentUI).on("click", toggleAdditionalActionPlan)

					$('[data-toggle="tooltip"]', $assessmentUI).tooltip().on('show.bs.tooltip', toggleToolTip);

					$("button[data-scale-modal]", $assessmentUI).on("click", function () {
						forceCloseToolTips();
						displayParameters(this.getAttribute("data-scale-modal"));
					});

					//ILR 

					$("select[name='riskProfile.riskStrategy']", $assessmentUI).on("change", (e) => $("select[name='riskProfile.expProbaImpact.vulnerability']", $assessmentUI).prop("disabled", e.currentTarget.value === "ACCEPT"));

					$("select[name='vulnerability']", $assessmentUI).on("change", (e) => {
						let $expVulnerability = $("select[name='riskProfile.expProbaImpact.vulnerability']", $assessmentUI);
						let value = parseInt(e.currentTarget.value);
						let expValue = parseInt($expVulnerability.val());
						if (expValue > value)
							$expVulnerability.val(value);
						$expVulnerability.find("option").each((i, ie) => {
							let val = parseInt(ie.value);
							if (val > value)
								$(ie).attr("disabled", "disabled").attr("hidden", "hidden");
							else $(ie).removeAttr("disabled").removeAttr("hidden");
						});


					});

				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
		return this;
	}, reset: function ($section) {
		if (!$section)
			$section = $(this.section);
		if (!this.$tabSection.is(":visible")) {
			$section.attr("data-trick-asset-id", -2).attr("data-trick-scenario-id", -2);
			this.$tabSection.attr("data-update-required", helper.invalidate = true);
		}
		return this;
	}
}

/**
 * Generates a datalist element based on the provided id.
 * 
 * @param {string} id - The id of the datalist element to be generated.
 * @returns {boolean} Returns false if an element with the provided id already exists, otherwise returns true.
 */
function generateDataList(id) {
	if (document.getElementById(id) != null)
		return false;
	let selector = id.endsWith("impact") ? "#Scale_Impact" : "#Scale_Probability,#DynamicParameters", $acronyms = $("td[data-trick-field='acronym']", selector), $values = $("td[data-trick-field='value']", selector), dataList = document.createElement("datalist");
	dataList.setAttribute("id", id);
	for (let i = 0; i < $acronyms.length; i++) {
		let option = document.createElement("option"), value = $values[i].innerText.trim();
		option.setAttribute("value", value);
		option.innerText = value + " (" + $acronyms[i].innerText.trim() + ")";
		dataList.appendChild(option);
	}
	$(dataList).hide().appendTo("#widgets");

}

/**
 * Displays parameters in a modal dialog.
 *
 * @param {string} name - The name of the element to display.
 * @param {string} title - The title of the modal dialog.
 * @returns {boolean} - Returns false.
 */
function displayParameters(name, title) {
	let html = $(name).map(function () { return this.innerHTML; }).get().join("");
	let view = new Modal(undefined, html), $modalBody = $(view.modal_body), $legend = $modalBody.find("legend").remove().slice(0, 1);
	$(view.modal_footer).remove();
	$(view.modal_dialog).addClass("modal-mdl");
	$modalBody.css({ "overflow-y": "auto", "max-height": "800px" }).find("th[data-th-name='qualification']").css({
		"text-align": "center"
	});
	$modalBody.find("th[data-name='action'],td[data-name='action'],tr[data-trick-class='ExcludeDynamicParameter']").remove();
	$modalBody.find("tbody").css({
		"text-align": "center"
	}).find("td").removeAttributes();
	if (!title)
		title = $legend.text();
	view.setTitle(title);

	view.Show();
	return false;
}

/**
 * Deletes estimation measures for a given asset and scenario.
 * 
 * @param {string} idAsset - The ID of the asset.
 * @param {string} idScenario - The ID of the scenario.
 * @param {jQuery} $assessmentUI - The jQuery object representing the assessment UI.
 * @param {jQuery} $progress - The jQuery object representing the progress element.
 */
function deleteEstimationMeasures(idAsset, idScenario, $assessmentUI, $progress) {

	let $toDelete = [], measures = [];

	$progress.show();

	$("tbody tr[data-trick-id]>td>input[type='checkbox']", $assessmentUI).each(function () {
		let $this = $(this), $tr = $this.closest("tr[data-trick-id]");
		if ($this.is(":checked"))
			$toDelete.push($tr);
		else measures.push($tr.attr("data-trick-id"));
	});

	$.ajax({
		url: context + "/Analysis/Assessment/RiskProfile/Update/Measure?idAsset=" + idAsset + "&idScenario=" + idScenario,
		type: "POST",
		data: JSON.stringify(measures),
		contentType: "application/json;charset=UTF-8",
		success: function (response) {
			if (response.success) {
				for (let item of $toDelete)
					item.remove();
				$("thead input[type='checkbox']", $assessmentUI).trigger('change');
			}
			else if (response.error)
				showDialog("#alert-dialog", response.error);
			else showDialog("#alert-dialog", MessageResolver("error.saving.measures", 'An unknown error occurred while saving measures'));
		},
		error: unknowError
	}).complete(function () { $progress.hide(); });
}

/**
 * Computes the assessment and updates the analysis.
 * 
 * @param {boolean} silent - Indicates whether to show dialogs or not.
 * @returns {boolean} - Returns false.
 */
function computeAssessment(silent) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Assessment/Update",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response['error'] != undefined)
					showDialog("#alert-dialog", response['error']);
				else if (response['success'] != undefined) {
					if (!silent)
						showDialog("#info-dialog", response['success']);
					riskEstimationUpdate();
					reloadAssetScenario();
					reloadAssetScenarioChart();
					reloadSection("section_riskregister");
				} else
					unknowError();
				return false;
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

/**
 * Refreshes the assessment by rebuilding all assessments.
 * 
 * @returns {boolean} Returns false.
 */
function refreshAssessment() {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		$("#confirm-dialog .modal-body").html(MessageResolver("confirm.refresh.assessment", "Are you sure, you want to rebuild all assessments"));
		$("#confirm-dialog .btn-danger").click(function () {
			let $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/Analysis/Assessment/Refresh",
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					if (response['error'] != undefined)
						showDialog("#alert-dialog", response['error']);
					else if (response['success'] != undefined) {
						showDialog("#info-dialog", response['success']);
						riskEstimationUpdate();
						reloadAssetScenario();
						reloadAssetScenarioChart();
						reloadSection("section_riskregister");
					} else
						unknowError();
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
		});
		$("#confirm-dialog").modal("show");
	} else
		permissionError();
	return false;
}

/**
 * Updates the Assessment ALE (Annual Loss Expectancy).
 * 
 * @param {boolean} silent - Indicates whether to show dialogs or not.
 * @returns {boolean} - Returns false.
 */
function updateAssessmentAle(silent) {
	if (userCan(findAnalysisId(), ANALYSIS_RIGHT.MODIFY)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Assessment/Update/ALE",
			type: "POST",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response['error'] != undefined)
					showDialog("#alert-dialog", response['error']);
				else if (response['success'] != undefined) {
					if (!silent)
						showDialog("#info-dialog", response['success']);
					riskEstimationUpdate();
					reloadAssetScenario();
					reloadAssetScenarioChart();
				} else
					unknowError();
				return false;
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		permissionError();

	return false;
}


/**
 * Manages the risk profile measure.
 * 
 * @param {string} idAsset - The ID of the asset.
 * @param {string} idScenario - The ID of the scenario.
 * @param {Event} e - The event object.
 */
function manageRiskProfileMeasure(idAsset, idScenario, e) {
	let $progress = $("#loading-indicator").show();
	$.ajax(
		{
			url: context + "/Analysis/Assessment/RiskProfile/Manage-measure?idAsset=" + idAsset + "&idScenario=" + idScenario,
			contentType: "application/json;charset=UTF-8",
			success: function (response) {
				let $measureManager = $("div#riskProfileMeasureManager", new DOMParser().parseFromString(response, "text/html"));
				if ($measureManager.length) {
					$measureManager.appendTo("#widgets");
					let $standardSelector = $("#riskProfileStandardSelector", $measureManager), $messageContainer = $("#riskProfileMessageContainer", $measureManager),
						$selectedMeasures = $("table#riskProfileSelectedMeasureContainer", $measureManager), $standardMeasures = $("table#riskProfileStandardMeasureContainer", $measureManager);
					if (standardCaching == undefined) {
						application["standard-caching"] = standardCaching = {
							standards: {},
							measures: {},
							phaseEndDate: {},
							$standardSelector: $standardSelector,
							$measureManager: $measureManager,
							$selectedMeasures: $selectedMeasures,
							$standardMeasures: $standardMeasures,
							$messageContainer: $messageContainer,
							load: function (standard) {
								this.$messageContainer.empty();
								if (this.hasStandard(standard))
									return this.clearStandardMeasureUI().updateStandardUI(standard);
								let instance = this;
								$progress.show();
								$.ajax(
									{
										url: context + "/Analysis/Standard/Measures?idStandard=" + standard,
										contentType: "application/json;charset=UTF-8",
										success: function (response) {
											if ($.isArray(response)) {
												standardCaching.addStandard(standard, response).clearStandardMeasureUI().updateStandardUI(standard);
											} else if (response.error)
												$("<label class='label label-danger' style='font-size:12px; display: block; margin-bottom: 15px;padding: 10px;' />").text(response.error).appendTo(instance.$messageContainer.empty());
											else
												$("<label class='label label-danger' style='font-size:12px; display: block; margin-bottom: 15px;padding: 10px;'/>").text(
													MessageResolver("error.loading.measures", 'An unknown error occurred while loading measures')).appendTo(
														instance.$messageContainer.empty());
										}, error: unknowError
									}).complete(function () {
										$progress.hide();
									});
								return this;
							},
							hasStandard: function (standard) {
								return this.standards[standard] != undefined;
							},
							addStandard: function (standard, measures) {
								if (!this.hasStandard(standard))
									this.standards[standard] = { measures: {} }
								let computableMeasure = this.standards[standard].measures = {};
								for (let measure of measures) {
									if (measure.computable)
										computableMeasure[measure.id] = measure;
								}
								return this;
							}, getStandardMeasures: function (standard) {
								let selected = this.standards[standard];
								return selected == undefined ? [] : selected.measures;
							}, getMeasure: function (idStandard, idMeasure) {
								return this.getStandardMeasures(idStandard)[idMeasure];
							},
							addMeasure: function (measure) {
								if (!this.measures[measure.id]) {
									this.measures[measure.id] = measure.id;
									let $measure = $("tbody tr[data-trick-id='" + measure.id + "']", this.$standardMeasures), $clone = $measure.clone();
									if (!$measure.length) {
										let $tr = $("<tr data-trick-id='" + measure.id + "' data-trick-class='Measure'>");
										let standardName = $("option[value='" + measure.idStandard + "']", this.$standardSelector).text();
										let status = application.measureStatus[measure.status];
										$("<td />").appendTo($tr);
										$("<td data-real-value='" + measure.idStandard + "'>" + standardName + "</td>").appendTo($tr);
										$("<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;'>" + measure.reference + "</td>").attr("data-title", measure.description).appendTo($tr).tooltip().on('show.bs.tooltip', toggleToolTip);
										$("<td />").text(measure.domain).appendTo($tr);
										$("<td data-real-value='" + measure.status + "'>" + status.value + "</td>").attr("title", status.title).appendTo($tr);
										$("<td>" + measure.implementationRate + "</td>").appendTo($tr);
										$("<td title='" + this.phaseEndDate[measure.phase] + "'>" + measure.phase + "</td>").appendTo($tr);
										$("<td />").text(measure.responsible).appendTo($tr);
										$clone = $tr;
									} else {
										$("button.btn.btn-xs.btn-primary", $clone).remove();
										$measure.find(".btn").removeClass("btn-primary").addClass("btn-danger").find(".fa").removeClass("fa-plus").addClass("fa-remove");
										if (measure.status == 'NA')
											$measure.addClass("danger");
										else if (measure.implementationRate >= 100)
											$measure.addClass("warning");
										else $measure.addClass("info");
									}

									if (measure.status == 'NA')
										$clone.addClass("danger");
									else if (measure.implementationRate >= 100)
										$clone.addClass("warning");

									$clone.appendTo($("tbody", this.$selectedMeasures));
								}
								return this;
							},
							removeMeasure: function (idMeasure) {
								if (this.measures[idMeasure])
									delete this.measures[idMeasure];
								$("tbody tr[data-trick-id='" + idMeasure + "']", this.$standardMeasures).removeClass("info").removeClass("warning").removeClass("danger").find(".btn").removeClass("btn-danger").addClass("btn-primary").find(".fa").removeClass("fa-remove").addClass("fa-plus");
								$("tbody tr[data-trick-id='" + idMeasure + "']", this.$selectedMeasures).remove();
								return this;
							},
							updateStandardUI: function (standard) {
								let that = this, measures = this.getStandardMeasures(standard), $tbody = $("tbody", this.$standardMeasures), standardName = $("option:selected", this.$standardSelector).text(), isCustomed = $("option:selected", this.$standardSelector).attr("data-trick-custom"), $btnAddMeasure = $("button[name='add-measure']", this.$measureManager);
								for (let idMeasure in measures) {
									let measure = measures[idMeasure];
									let $tr = $("<tr data-trick-id='" + measure.id + "' data-trick-class='Measure'>");
									let $button = $("<button class='btn btn-xs'></button>");
									let status = application.measureStatus[measure.status];

									$button.appendTo($("<td />").appendTo($tr));

									$("<td data-real-value='" + measure.idStandard + "' >" + standardName + "</td>").appendTo($tr);
									$("<td data-toggle='tooltip' data-container='body' data-trigger='click' data-placement='right' style='cursor: pointer;'>" + measure.reference + "</td>").attr("data-title", measure.description).appendTo($tr).tooltip().on('show.bs.tooltip', toggleToolTip);
									$("<td />").text(measure.domain).appendTo($tr);
									$("<td data-real-value='" + measure.status + "' >" + status.value + "</td>").attr("title", status.title).appendTo($tr);
									$("<td>" + measure.implementationRate + "</td>").appendTo($tr);
									$("<td title='" + this.phaseEndDate[measure.phase] + "' >" + measure.phase + "</td>").appendTo($tr);
									$("<td />").text(measure.responsible).appendTo($tr);
									if (this.measures[measure.id] != undefined) {
										if (measure.status == 'NA')
											$tr.addClass("danger");
										else if (measure.implementationRate >= 100)
											$tr.addClass("warning");
										else $tr.addClass("info");
										$("<i class='fa fa-remove' aria-hidden='true'></i>").appendTo($button.addClass("btn-danger"));
									} else $("<i class='fa fa-plus' aria-hidden='true'></i>").appendTo($button.addClass("btn-primary"));

									$button.on("click", function () {
										let $this = $(this), id = $this.closest("tr[data-trick-id]").attr("data-trick-id");
										if (standardCaching.measures[id])
											standardCaching.removeMeasure(id);
										else standardCaching.addMeasure(standardCaching.getMeasure(standard, id));
									}).attr("title", MessageResolver("label.action.add", "Add"));

									$tr.appendTo($tbody);
								}
								if (isCustomed == "true") {
									$btnAddMeasure.show().unbind("click").on("click", function () {
										$progress.show();
										$.ajax(
											{
												url: context + "/Analysis/Standard/" + standard + "/Measure/New",
												contentType: "application/json;charset=UTF-8",
												success: function (response) {
													let $container = $(new DOMParser().parseFromString(response, "text/html")).find("#measure-form-container"), $modal = that.$measureManager;
													if ($container.length) {
														$("#measure-form-container", $modal).replaceWith($container);
														$(".modal-body", $modal).attr('style', "padding-top:5px");
														$("div[id^='risk-profile-measure-manager-']", $modal).hide();
														setupMeasureManager($container);
														generateHelper(undefined, "#measure-form-container");
														$("div[id^='measure-form-']", $modal).show();
													} else showDialog("#alert-dialog", MessageResolver("error.unknown.occurred", "An unknown error occurred"));
												}, error: unknowError
											}).complete(function () {
												$progress.hide();
											});
										return false;
									});
								} else $btnAddMeasure.hide().unbind("click");

								return this;
							},
							clearStandardMeasureUI: function () {
								this.$standardMeasures.find("tbody").empty();
								return this;
							}, update: function ($standardSelector, $measureManager, $selectedMeasures, $standardMeasures, $messageContainer) {
								this.$standardSelector = $standardSelector;
								this.$measureManager = $measureManager;
								this.$selectedMeasures = $selectedMeasures;
								this.$standardMeasures = $standardMeasures;
								this.$messageContainer = $messageContainer;
							}, updateView: function () {
								let measures = this.measures, $finalBody = $("tbody", "#riskProfileMeasure"), $currentTrs = $("tr[data-trick-id]", $finalBody), $selectedTrs = $("tbody>tr[data-trick-id]", this.$selectedMeasures);
								$currentTrs.each(function () {
									let $this = $(this), measureId = $this.attr("data-trick-id");
									if (!measures[measureId])
										$this.remove();
									else delete measures[measureId];
								});

								$selectedTrs.each(function () {
									let measureId = this.getAttribute("data-trick-id");
									if (measures[measureId]) {
										let $this = $(this);
										$("<input type='checkbox' class='checkbox' data-menu-controller='menu_estimation_action_plan' onchange=\"return updateMenu(this,'#section_estimation_action_plan','#menu_estimation_action_plan')\" > ").appendTo($("td:first", $this));
										$("td[data-toggle='tooltip']", $this).tooltip().on('show.bs.tooltip', toggleToolTip);
										$this.appendTo($finalBody);
									}
								});

								$("thead input[type='checkbox']", application["estimation-helper"].section).trigger('change');

								return this;
							}, clear: function (standard) {
								if (standard == undefined)
									this.phaseEndDate = {};
								else delete this.standards[standard];
								return this;
							}, reloadStandard: function () {
								$("button[name='back']:first", this.$measureManager).trigger("click");
								this.clear(this.$standardSelector.val());
								this.$standardSelector.trigger("change");
								return this;
							}
						};
					} else standardCaching.update($standardSelector, $measureManager, $selectedMeasures, $standardMeasures, $messageContainer);

					$standardSelector.on("change", function () {
						if (this.value == "0")
							standardCaching.clearStandardMeasureUI();
						else
							standardCaching.load(this.value);
						forceCloseToolTips();
					});

					standardCaching.measures = [];

					if (!standardCaching.phaseEndDate.length) {
						$("#section_phase table>tbody>tr[data-trick-id][data-trick-index]").each(function () {
							standardCaching.phaseEndDate[this.getAttribute("data-trick-index")] = $("td[data-trick-field='endDate']", this).text();
						});
					}

					$("tbody>tr[data-trick-id]", $selectedMeasures).each(function () {
						let idMeasure = $(this).attr("data-trick-id");
						standardCaching.measures[idMeasure] = idMeasure;
					});

					$('a[data-toggle="tab"]', $measureManager).on('shown.bs.tab', function (e) {
						forceCloseToolTips();
					});

					$("button[name='back']", $measureManager).on("click", function () {
						$("div[id^='measure-form-']", $measureManager).hide();
						$("div[id^='risk-profile-measure-manager-']", $measureManager).show();
						$(".modal-body", $measureManager).removeAttr('style');
					});

					$("button[name='save-measure']", $measureManager).on("click", function () {
						saveMeasure($("form", $measureManager), function () {
							standardCaching.reloadStandard();
						});
					});

					$measureManager.modal("show").on("hide.bs.modal", function () {
						closeToolTips();
					}).on("hidden.bs.modal", function () {
						$measureManager.remove();
					});

					$("button[name='save']").on("click", function () {
						let measures = [];
						for (let id of standardCaching.measures) {
							if (id > 0)
								measures.push(id);
						}
						$progress.show();
						$messageContainer.empty();
						$.ajax({
							url: context + "/Analysis/Assessment/RiskProfile/Update/Measure?idAsset=" + idAsset + "&idScenario=" + idScenario,
							type: "POST",
							data: JSON.stringify(measures),
							contentType: "application/json;charset=UTF-8",
							success: function (response) {
								if (response.success) {
									$("<label class='label label-success' style='font-size:12px; display: block; margin-bottom: 15px;padding: 10px;' />").text(response.success).appendTo($messageContainer);
									standardCaching.updateView().$measureManager.modal("hide");
								}
								else if (response.error)
									$("<label class='label label-danger' style='font-size:12px; display: block; margin-bottom: 15px;padding: 10px;' />").text(response.error).appendTo($messageContainer);
								else $("<label class='label label-danger' style='font-size:12px; display: block; margin-bottom: 15px;padding: 10px;' />").text(MessageResolver("error.saving.measures", 'An unknown error occurred while saving measures')).appendTo($messageContainer);

							},
							error: unknowError
						}).complete(function () { $progress.hide(); });
					});

				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide()
		});
	return false;
}

/**
 * Initializes the risk estimation functionality.
 * This function sets up event listeners and handlers for various elements in the application.
 * It also initializes the assessment helper and assigns it to the application object.
 */
function initialiseRiskEstimation() {

	application["estimation-helper"] = helper = new AssessmentHelder();

	let $previousSelector = $("[data-trick-nav='previous-selector']", application["estimation-helper"].$tabSection), $nextSelector = $("[data-trick-nav='next-selector']", application["estimation-helper"].$tabSection), $previousAssessment = $("[data-trick-nav='previous-assessment']", application["estimation-helper"].$tabSection), $nextAssessment = $("[data-trick-nav='next-assessment']", application["estimation-helper"].$tabSection);

	$previousSelector.on("click", function () {
		$("select[name='" + activeSelector + "']>option:selected", application["estimation-helper"].$tabSection).prev("[data-trick-selected='true']:last").prop('selected', true).parent().change();
		return false;
	});

	$nextSelector.on("click", function () {
		$("select[name='" + activeSelector + "']>option:selected", application["estimation-helper"].$tabSection).next("[data-trick-selected='true']:first").prop('selected', true).parent().change();
		return false;
	});

	$previousAssessment.on("click", function () {
		$("div[data-trick-content]:visible .list-group-item.active", application["estimation-helper"].$tabSection).prevAll(":visible:first").click();
		return false;
	});

	$nextAssessment.on("click", function () {
		$("div[data-trick-content]:visible .list-group-item.active", application["estimation-helper"].$tabSection).nextAll(":visible:first").click();
		return false;
	});

	let updateSelector = function (e) {
		let $target = $(e.currentTarget), value = $target.val(), name = $target.attr("name"), other = application["estimation-helper"].getOtherName(name);
		if (value == '0')
			application["estimation-helper"].switchControl(other).getCurrent(other).find("option[data-trick-selected='true'][value='" + helper.getLastSelected(other) + "']:first").prop('selected', true);
		else if (helper.getCurrent(other).val() != "0")
			application["estimation-helper"].switchControl(name).getCurrent(other).find("option[value='0']").prop('selected', true);
		else
			application["estimation-helper"].setLastSelected(name, value);

		application["estimation-helper"].updateContent();

		return false;
	};

	$("div.list-group>.list-group-item", application["estimation-helper"].$tabSection).on("click", changeAssessment);

	for (let tmpName of helper.names)
		application["estimation-helper"].getCurrent(tmpName).on('change', updateSelector)

	$("button[name='add-scenario']", application["estimation-helper"].$tabSection).on("click", function () {
		return editScenario(undefined, true);
	});

	$("button[name='add-asset']", application["estimation-helper"].$tabSection).on("click", function () {
		return editAsset(undefined, true);
	});
}

/**
 * Imports risk estimation data.
 * 
 * @returns {boolean} Returns false to prevent the default form submission behavior.
 */
function importRiskEstimation() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Data-manager/Risk-estimation/Import-form",
		success: function (response, textStatus, jqXHR) {
			let $modal = $("#import-risk-estimation-modal", new DOMParser().parseFromString(response, "text/html"));
			if ($modal.length) {
				$("button[name='import']", $modal).on("click", importRiskEstimationProcess);
				$modal.appendTo("#widgets").modal("show").on("hidden.bs.modal", e => $modal.remove());
			} else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error: unknowError

	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Imports the risk estimation process.
 * 
 * @returns {boolean} Returns false if the upload file is not selected, otherwise returns true.
 */
function importRiskEstimationProcess() {
	let $modal = $("#import-risk-estimation-modal"), $uploadFile = $("#upload-file-info-risk-estimation", $modal), $progress = $("#loading-indicator"), $measureNotification = $("#riskEstimationNotification", $modal);
	if (!$uploadFile.length)
		return false;
	else if ($uploadFile.val() == "") {
		$measureNotification.text(MessageResolver("error.import.risk_estimation.no_select.file", "Please select file to import!"));
		return false;
	}
	try {
		$progress.show();
		$.ajax({
			url: context + "/Analysis/Data-manager/Risk-estimation/Import-process",
			type: 'POST',
			data: new FormData($('#importRiskEstimationForm', $modal)[0]),
			cache: false,
			contentType: false,
			processData: false,
			success: function (response, textStatus, jqXHR) {
				if (response.success)
					application["taskManager"].SetTitle(MessageResolver("label.title.import.risk.estimation", "Import risk estimation")).Start();
				else if (response.error)
					showDialog("#alert-dialog", response.error);
				else
					showDialog("#alert-dialog", MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
			},
			error: unknowError

		}).complete(function () {
			$progress.hide();
		});
	} finally {
		$modal.modal("hide");
	}
	return false;
}
