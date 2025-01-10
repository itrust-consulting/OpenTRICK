/**
 * This file contains functions related to managing analysis access and profiles.
 */
var taskManager = undefined;
var analysesCaching = undefined;

$(document).ready(function () {
	application["settings-fixed-header"] = {
		fixedOffset: $(".navbar-fixed-top"),
		scrollStartFixMulti: 1.02
	};
	setTimeout(() => fixTableHeader("#section_analysis table"), 300);
});

/**
 * Manages the access for an analysis.
 * 
 * @param {number} analysisId - The ID of the analysis.
 * @param {string} section_analysis - The section of the analysis.
 * @returns {boolean} - Returns false if the analysis ID is null or undefined, or if the selected analysis length is not equal to 1. Otherwise, returns true.
 */
function manageAnalysisAccess(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		let selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	if (!isArchived(analysisId) && canManageAccess()) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/ManageAccess/" + analysisId,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $content = $("#manageAnalysisAccessModel", new DOMParser().parseFromString(response, "text/html"));
				if ($content.length) {

					$content.appendTo("#widget");

					let instance = this, emails = {};

					let $template = $("#template-invitation div[data-trick-email][data-default-value][data-name]", $content);

					let $bottomTarget = $("#btn-container", $content), $submitBtn = $(".modal-footer button[name='save']", $content);

					let validateSubmitButton = () => {

						let tempEmails = Object.values(emails).sort();

						for (let i = 1; i < tempEmails.length; i++) {
							if (tempEmails[i - 1] == tempEmails[i])
								$("div[data-status='new'][data-trick-email='" + tempEmails[i] + "']", $content).addClass("has-error");
						}

						let count = $("fieldset div.form-group.has-error", $content).length;

						$submitBtn.prop("disabled", count > 0);
					};

					$("button[name='invite']", $bottomTarget).on('click', () => {

						let $entry = $template.clone(), index = $("div[data-index][data-trick-email]:last").attr("data-index");

						if (index === undefined)
							index = 1;
						else index = parseInt(index) + 1;

						$entry.attr("data-index", index).attr("data-name", "guest-" + index);

						$("input[type='radio']", $entry).attr("name", "guest-" + index).last().on('change', (e) => {
							if (e.currentTarget.checked && e.currentTarget.value === "") {
								if (emails[e.currentTarget.name])
									delete emails[e.currentTarget.name];
								$entry.remove();
								validateSubmitButton();
							}
						});

						$bottomTarget.before($entry);

						$("input[type='email']", $entry).on("blur", (e) => {
							if (!validateEmail(e.currentTarget.value))
								$entry.addClass("has-error");
							else {
								$entry.removeClass("has-error");
								$entry.attr("data-trick-email", emails[e.currentTarget.name] = e.currentTarget.value);
							}

							validateSubmitButton();

						}).trigger("focus");

					});

					$submitBtn.one("click", updateAnalysisAccess);

					$("div[data-trick-email][data-trick-email!='']", $content).each(function (i) {
						emails[this.getAttribute("data-name")] = this.getAttribute("data-trick-email");
					});

					$content.on('hidden.bs.modal', () => {
						$content.remove();
						delete instance;
					}).modal("show");

				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

/**
 * Updates the analysis access based on the user's input.
 *
 * @param {Event} e - The event object.
 */
function updateAnalysisAccess(e) {

	let $progress = $("#loading-indicator").show(), $modal = $("#manageAnalysisAccessModel"), me = $modal.attr("data-trick-user-id"), data = {
		analysisId: $modal.attr("data-trick-id"),
		userRights: {},
		invitations: {}
	};

	$modal.find(".form-group[data-trick-id][data-default-value]").each(function () {
		let $this = $(this), $selected = $this.find("input[type='radio']:checked"), newRight = $selected.val(), oldRight = $this.attr("data-default-value");
		if ($selected.length && newRight != oldRight) {
			data.userRights[$this.attr("data-trick-id")] = {
				oldRight: oldRight == "" ? undefined : oldRight,
				newRight: newRight == "" ? undefined : newRight
			};
		}
	});


	$modal.find(".form-group[data-trick-email][data-default-value][data-index]:visible").each(function () {
		let $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
		if (newRight != oldRight) {
			data.invitations[$this.attr("data-trick-email")] = {
				oldRight: oldRight === "" ? undefined : oldRight,
				newRight: newRight === "" ? undefined : newRight
			};
		}
	});

	if (Object.keys(data.userRights).length || Object.keys(data.invitations).length) {
		$.ajax({
			url: context + "/Analysis/ManageAccess/Update",
			type: "post",
			data: JSON.stringify(data),
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response.error != undefined)
					showDialog("#alert-dialog", response.error);
				else if (response.success != undefined) {
					if (data.userRights[me] != undefined && data.userRights[me].oldRight != data.userRights[me].newRight)
						reloadSection("section_analysis");
					else
						showDialog("success", response.success);
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	} else
		$progress.hide();
}



/**
 * Manages the IDS access for a selected analysis.
 * 
 * @param {string} section - The section of the analysis.
 * @returns {boolean} - Returns false if the selected analysis is not found or if the conditions are not met, otherwise returns true.
 */
function manageAnalysisIDSAccess(section) {
	let selectedAnalysis = findSelectItemIdBySection(section);
	if (selectedAnalysis.length != 1)
		return false;
	if (!isArchived(selectedAnalysis[0]) && canManageAccess() && isAnalysisType("QUANTITATIVE")) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Manage/IDS/" + selectedAnalysis[0],
			type: "GET",
			success: function (response, textStatus, jqXHR) {
				let $content = $("#manageAnalysisIDSAccessModel", new DOMParser().parseFromString(response, "text/html"));
				if ($content.length) {
					$content.appendTo("#widget").on("hidden.bs.modal", function () {
						$content.remove();
					}).modal("show").find(".modal-footer button[name='save']").one("click", function () {
						let data = {};
						$content.find(".form-group[data-trick-id][data-default-value]").each(function () {
							let $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
							if (newRight != oldRight)
								data[this.getAttribute("data-trick-id")] = newRight;
						});
						if (Object.keys(data).length) {
							$.ajax({
								url: context + "/Analysis/Manage/IDS/" + selectedAnalysis[0] + "/Update",
								type: "post",
								data: JSON.stringify(data),
								contentType: "application/json;charset=UTF-8",
								success: function (response, textStatus, jqXHR) {
									if (response.error != undefined)
										showDialog("#alert-dialog", response.error);
									else if (response.success != undefined)
										showDialog("success", response.success);
									else
										unknowError();
								},
								error: unknowError
							}).complete(function () {
								$progress.hide();
							});
						} else
							$progress.hide();
					});
				} else
					unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;

}

/**
 * Finds the value of the "data-trick-is-profile" attribute for the given element or its parent.
 * @param {HTMLElement} element - The element to search for the "data-trick-is-profile" attribute.
 * @returns {string|null} - The value of the "data-trick-is-profile" attribute, or null if not found.
 */
function findTrickisProfile(element) {
	if (element != undefined && element != null && element.length > 0 && element.length < 2)
		if ($(element).attr("data-trick-is-profile") != undefined)
			return $(element).attr("data-trick-is-profile");
		else if ($(element).parent().prop("tagName") != "BODY")
			return findTrickisProfile($(element).parent());
		else
			return null;
	else
		return null;
}

/**
 * Disables the specified menu item if the profile is selected.
 *
 * @param {string} section - The section selector.
 * @param {string} menu - The menu selector.
 */
function disableifprofile(section, menu) {
	let element = $(menu + " li[class='profilemenu']");
	element.addClass("disabled");
	let isProfile = findTrickisProfile($(section + " tbody :checked"));
	if (isProfile != undefined && isProfile != null) {
		if (isProfile == "true")
			element.addClass("disabled");
		else
			element.removeClass("disabled");
	}
}

/**
 * Deletes an analysis.
 * 
 * @param {number} analysisId - The ID of the analysis to delete.
 * @returns {boolean} Returns false if the analysis ID is null or undefined, or if the user does not have the necessary permissions to delete the analysis. Otherwise, returns true.
 */
function deleteAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		let selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (isOwner(analysisId) || !isArchived(analysisId) && userCan(analysisId, ANALYSIS_RIGHT.ALL)) {
		let $modal = showDialog("#deleteAnalysisModel", MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?", 1));
		$("button[name='delete']", $modal).unbind().one("click", function () {
			let $progress = $("#loading-indicator").show();
			$.ajax({
				url: context + "/Analysis/Delete/" + analysisId,
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					if (response.success != undefined) {
						reloadSection("section_analysis");
					} else if (response.error != undefined)
						showDialog("#alert-dialog", response.error)
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
			$modal.modal("hide");
			return false;
		});

	}
	return false;
}

/**
 * Creates an analysis profile for the given analysis ID and section.
 * If the analysis ID is not provided, it will be retrieved from the selected item in the section.
 * 
 * @param {string} analysisId - The ID of the analysis.
 * @param {string} section_analysis - The section containing the analysis.
 * @returns {boolean} Returns false if the analysis ID is not found or if the user does not have the required permissions.
 */
function createAnalysisProfile(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		let selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}
	if (!isArchived(analysisId) && userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/AnalysisProfile/Add/" + analysisId,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("#analysisProfileModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$view.appendTo("#widget").modal("show").on('hidden.bs.modal', () => $view.remove());
					$("button[name='save']").on("click", e => saveAnalysisProfile(e, $view, $progress, analysisId));
				} else unknowError();
			},
			error: unknowError
		}).complete(function () {
			$progress.hide();
		});
	}
	return false;
}

/**
 * Saves the analysis profile.
 *
 * @param {Event} e - The event object.
 * @param {jQuery} $view - The jQuery object representing the view.
 * @param {jQuery} $progress - The jQuery object representing the progress element.
 * @param {string} analysisId - The ID of the analysis.
 * @returns {boolean} - Returns false.
 */
function saveAnalysisProfile(e, $view, $progress, analysisId) {
	let data = {
		"id": analysisId,
		"description": $("input[name='name']", $view).val()
	};
	$view.find(".form-group[data-trick-id][data-name]").each(function () {
		if ($("input[type='radio'][value='true']:checked", this).length)
			data[this.getAttribute("data-trick-id")] = true;
	});
	$progress.show();
	$.ajax({
		url: context + "/AnalysisProfile/Analysis/" + analysisId + "/Save",
		type: "post",
		data: JSON.stringify(data),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			$(".label-danger", $view).remove();
			if (response['taskid'] == undefined) {
				for (let error in response) {
					let errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");
					$(errorElement).text(response[error]);
					switch (error) {
						case "description":
							$(errorElement).appendTo($("#name", $view).parent());
							break;
						default:
							showDialog("#alert-dialog", response[error]);
							break;
					}
				}
			} else {
				application["taskManager"].Start();
				$view.modal("hide");
			}
			return false;
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

/**
 * Performs a custom analysis based on the provided element.
 * 
 * @param {Element} element - The element to perform the analysis on.
 * @returns {boolean} Returns false if the parent of the element has the "disabled" class, otherwise returns true.
 */
function customAnalysis(element) {
	if ($(element).parent().hasClass("disabled"))
		return false;
	let $progress = $("#loading-indicator").show();
	$
		.ajax(
			{
				url: context + "/Analysis/Build",
				type: "get",
				contentType: "application/json;charset=UTF-8",
				success: function (response) {
					let $modalContent = $("#buildAnalysisModal", new DOMParser().parseFromString(response, "text/html"));
					if (!$modalContent.length)
						unknowError();
					else {
						let $old = $("#buildAnalysisModal");
						if ($old.length)
							$old.replaceWith($modalContent);
						else
							$modalContent.appendTo("#widget");
						let modal = new Modal($modalContent), $modalBody = $(modal.modal_body), $emptyText = $modalBody.find("*[dropzone='true']>div:first").text(), $removeText = MessageResolver(
							"label.action.delete", "Delete"), $lockText = MessageResolver("label.action.lock", "Lock"), $analysisSelector = $("#selector-analysis",
								$modalBody), $impacts = $("select[name='impacts']", $modalBody), $impactLevelMaxValue = $("input#scale_level,input#scale_maxValue", $modalBody), $generalTab = $("#group_1"), $advanceTab = $("#group_2");
						// load data from database and manage caching
						analysesCaching = {
							versions: {},
							identifiers: {},
							customers: {},
							cloneWidth: undefined,
							assessmentDisable: true,
							analysisType: undefined,
							saveVersions: function (identifier, data) {
								for (const version of data)
									this.versions[version.id] = version;
								this.identifiers[identifier] = data;
								return this;
							},
							saveIdentifier: function (idCustomer, data) {
								if (!this.cloneWidth)
									this.cloneWidth = this.cloneWidth = $("#analysis-build-standards").width();
								this.customers[idCustomer] = data;
								return this;
							},
							updateAnalysisVersions: function (idCustomer, identifier) {
								if (this.identifiers[identifier] == undefined)
									this.loadByCustomerIdAndIdentifier(idCustomer, identifier);
								else
									this.updateVersionSelector(identifier);
								return this;
							},
							updateAnalysisIdentifiers: function (idCustomer) {
								if (this.customers[idCustomer] == undefined)
									this.loadByCustomerId(idCustomer);
								else
									this.updateAnalysisSelector(idCustomer);
								return this;
							},
							findAnalysisById: function (idAnalysis) {
								return this.versions[idAnalysis];
							},
							loadByCustomerIdAndIdentifier: function (idCustomer, identifier) {
								let instance = this;
								$progress.show();
								$.ajax({
									url: context + "/Analysis/Build/Customer/" + idCustomer + "/Identifier/" + identifier,
									type: "get",
									contentType: "application/json;charset=UTF-8",
									success: function (response) {
										if (typeof response == 'object')
											instance.saveVersions(identifier, response).updateVersionSelector(identifier);
										else
											unknowError();
									}
								}).complete(function () {
									$progress.hide();
								});
							},
							loadByCustomerId: function (idCustomer) {
								let instance = this;
								$progress.show();
								$.ajax({
									url: context + "/Analysis/Build/Customer/" + idCustomer,
									type: "get",
									contentType: "application/json;charset=UTF-8",
									success: function (response) {
										if (typeof response == 'object')
											instance.saveIdentifier(idCustomer, response).updateAnalysisIdentifiers(idCustomer);
										else
											unknowError();
									}
								}).complete(function () {
									$progress.hide();
								});
							},
							updateVersionSelector: function (identifier) {
								let instance = this;
								let analyses = this.identifiers[identifier];
								let $analysisVersions = $("#analysis-versions", $modalBody);
								for (const analysis of analyses) {
									if (instance.analysisType.isSupported(analysis.type)) {
										let $li = $("<li class='list-group-item' data-trick-id='" + analysis.id + "' data-trick-analysis-type='" + analysis.type
											+ "' title='" + analysis.label + " v." + analysis.version + "'>" + analysis.version + "</li>");
										$li.appendTo($analysisVersions);
									}
								}

								$("#analysis-versions li", $modalBody).hover(function () {
									$(this).css('cursor', 'move');
								}).draggable({
									helper: "clone",
									cancel: "span.glyphicon-remove-sign",
									revert: "invalid",
									containment: "#group_2",
									accept: "*[dropzone='true']",
									cursor: "move",
									start: function (e, ui) {
										$(ui.helper).css({
											'z-index': '1385',
											'min-width': instance.cloneWidth,
											'border-radius': "5px"
										});
									}
								});
								return this;
							},
							updateAnalysisSelector: function (idCustomer) {
								let analyses = this.customers[idCustomer];
								let $analysisSelector = $("#selector-analysis");
								for (const analysis of analyses)
									$("<option value='" + analysis.identifier + "'>" + analysis.label + "</option>").appendTo($analysisSelector);
								return this;
							},

							applyCallback: function (callbacks) {
								if (callbacks == undefined)
									return;
								if ($.isArray(callbacks)) {
									for (const callback of callbacks)
										eval(callback);
								} else
									eval(callbacks);
								return this;
							},
							checkPhase: function () {
								let $phaseInput = $("input[name='phase']", $modalBody);
								let $standards = $("#analysis-build-standards>div>label");
								if (!$standards.length)
									$phaseInput.prop("disabled", true).prop("checked", false);
								else {
									$phaseInput.prop("disabled", false);
									let idAnalysis = 0;
									for (let i = 0; i < $standards.length; i++) {
										let currentIdAnalysis = $($standards[i]).attr("data-trick-id");
										if (i == 0)
											idAnalysis = currentIdAnalysis;
										else if (currentIdAnalysis != idAnalysis) {
											$phaseInput.prop("disabled", true).prop("checked", false);
											break;
										}
									}
								}
								return this;
							},
							checkRiskDependancies: function () {
								let trick_id_asset = $("#analysis-build-assets .well").attr("data-trick-id"), trick_id_parameter = $("#analysis-build-parameters .well")
									.attr("data-trick-id"), trick_id_scenario = $("#analysis-build-scenarios .well").attr("data-trick-id"), estimation = trick_id_parameter == undefined
										|| trick_id_parameter != trick_id_asset || trick_id_asset != trick_id_scenario || trick_id_asset == undefined;
								$modalBody.find("input[name='assessment']").prop("disabled", this.assessmentDisable = estimation).prop("checked", false);
								return this.checkProfile();
							},
							checkProfile: function () {

								let $riskProfile = $modalBody.find("input[name='riskProfile']").prop("checked", false).prop("disabled",
									this.assessmentDisable || !this.analysisType.isQualitative()).closest(".form-group");

								let trick_id_asset = parseInt($("#analysis-build-assets .well").attr("data-trick-id"));
								let $assetDependancyForm = $modalBody.find("input[name='assetDependancy']").prop("checked", false).prop("disabled",
									isNaN(trick_id_asset) || trick_id_asset  < 1 || !this.analysisType.isQualitative()).closest(".form-group");

								if (this.analysisType.isQualitative()) {
									$riskProfile.show();
									$assetDependancyForm.show();
									if (!this.analysisType.isQuantitative())
										$modalBody.find("input[name='uncertainty']").prop("checked", false).prop("disabled", true).closest(".form-group").hide();
									else
										$modalBody.find("input[name='uncertainty']").prop("disabled", false).closest(".form-group").show();
								} else {
									$riskProfile.hide();
									$assetDependancyForm.hide();
									$modalBody.find("input[name='uncertainty']").prop("disabled", false).closest(".form-group").show();
								}

								let type = this.analysisType;

								$modalBody.find("select[name='profile']").val("0").find("option[value!='0']").each(function () {
									if (type.isSupported(this.getAttribute("data-type")))
										this.removeAttribute("hidden");
									else
										this.setAttribute("hidden", "hidden")
								});

								$modalBody.find("div[data-type].form-group").each(function () {
									if (type.isSupported(this.getAttribute("data-type")))
										this.removeAttribute("hidden");
									else {
										this.setAttribute("hidden", "hidden");
										let $input = $("input", this);
										$input.val($input.attr("placeholder"));
									}
								});
								$analysisSelector.trigger("change");
								return this.checkParameter();
							},
							checkAssetStandard: function () {
								let analysisAsset = $("#analysis-build-assets input").val();
								if (analysisAsset == -1)
									return this;
								let $parent = $("#analysis-build-standards"), $label = $("#analysis-build-standards label[data-trick-type='ASSET'][data-trick-id!='"
									+ analysisAsset + "']");
								$label.each(function () {
									let $this = $(this);
									$("[data-trick-id='" + $this.attr("data-trick-id") + "'][data-trick-owner-id='" + $this.attr("data-trick-owner-id") + "']", $parent)
										.remove();
								});
								if ($label.length)
									this.nameAnalysisStandard();
								return this;
							},
							checkParameter: function () {
								let trick_id_parameter = $("#analysis-build-parameters .well").attr("data-trick-id") == undefined;
								if (!this.analysisType.isQualitative())
									this.changeImpactState(true);
								else if (trick_id_parameter) {
									if ($impacts.is(":disabled"))
										this.changeImpactState(false);
								} else if (!$impacts.is(":disabled"))
									this.changeImpactState(true);
								return this;
							},
							changeImpactState: function (enable) {
								$impacts.val('0').prop("disabled", enable).trigger("change");
								return this;
							},
							isCustomSupported: function (value, target) {
								return target == 'HYBRID' || value === target;
							},
							nameAnalysisStandard: function () {
								$("#analysis-build-standards label").each(
									function (i) {
										let $this = $(this);
										$(
											"#analysis-build-standards input[data-trick-id='" + $this.attr("data-trick-id") + "'][data-trick-owner-id='"
											+ $this.attr("data-trick-owner-id") + "']").each(function () {
												let $input = $(this), fieldName = $input.attr('data-trick-field');
												$input.attr("name", "standards[" + i + "]." + fieldName);
												switch (fieldName) {
													case "idAnalysis":
														$input.val($this.attr("data-trick-id"));
														break;
													case "idAnalysisStandard":
														$input.val($this.attr("data-trick-owner-id"));
														break;
													default:
														$input.val($this.attr("data-trick-" + fieldName));
												}
											});
									});
							}
						};

						let $locker = $("<a href='#' style='margin-right:3px;' class='pull-right' title='" + $lockText
							+ "'  ><i class='fa fa-unlock'></i><input hidden class='pull-right' type='checkbox' style='margin-right:3px; margin-left:3px' ></a>"), $analysisType = $(
								"input[name='type']", $modalContent);

						// Event user select a customer
						$("#selector-customer").on("change", function (e) {
							$("#selector-analysis option[value!=0]").remove();
							$("#analysis-versions li[class!='disabled']").remove();
							let idCustomer = $(e.target).val();
							if (idCustomer < 1)
								$("#selector-analysis option[value=0]").prop("selected", true);
							else
								analysesCaching.updateAnalysisIdentifiers(idCustomer);
						});

						$analysisSelector.on("change", function (e) {
							$("#analysis-versions li[class!='disabled']", $modalBody).remove();
							let identifier = $(e.target).val();
							if (identifier != 0)
								analysesCaching.updateAnalysisVersions($("#selector-customer").val(), identifier)

						});

						$analysisType.on("change", function (e) {
							if (e.currentTarget.checked) {
								analysesCaching.analysisType = ANALYSIS_TYPE.valueOf(e.currentTarget.value);
								$("#analysis-build-parameters,#analysis-build-assets,#analysis-build-scenarios", $modalBody).find("div.well[data-supported]").attr("data-supported", e.currentTarget.value);
								$("div.well[data-supported]", $modalBody).each(function () {
									let target = this.getAttribute("data-supported");
									$("a[data-trick-type-control]", this).filter(function () {
										return !analysesCaching.isCustomSupported(this.getAttribute("data-trick-type-control"), target);
									}).click();
								});
								analysesCaching.checkProfile();
							}
						});

						$impacts.on("change", function () {
							let options = this.selectedOptions;
							if (options[0].value == "0") {
								$(options[0]).prop("selected", options.length < 2);
								$impactLevelMaxValue.prop("disabled", options.length < 2);
							} else
								$impactLevelMaxValue.prop("disabled", false);
						});

						$("#analysis-build-parameters").attr("data-trick-callback", "analysesCaching.checkRiskDependancies()");
						$("#analysis-build-scenarios").attr("data-trick-callback", "analysesCaching.checkRiskDependancies()");
						$("#analysis-build-assets").attr("data-trick-callback", "analysesCaching.checkRiskDependancies().checkAssetStandard()");
						$("#analysis-build-standards").attr("data-trick-callback", "analysesCaching.checkPhase().checkAssetStandard()");

						$modalBody.find("*[dropzone='true'][id!='analysis-build-standards']>div").droppable(
							{
								accept: function (ele) {
									return analysesCaching.isCustomSupported(ele.attr('data-trick-analysis-type'), this.getAttribute('data-supported')) && ele.hasClass("list-group-item");
								},
								activeClass: "warning",
								drop: function (event, ui) {
									let $this = $(this), $parent = $this.parent();
									$this.attr("data-trick-id", ui.draggable.attr("data-trick-id"));
									$this.attr("title", ui.draggable.attr("title"));
									$this.text(ui.draggable.attr("title"));
									$this.addClass("success");
									$parent.find('input[name]').attr("value", ui.draggable.attr("data-trick-id"));
									let callback = $parent.attr("data-trick-callback");
									$(
										"<a href='#' data-trick-type-control='" + ui.draggable.attr("data-trick-analysis-type")
										+ "' class='pull-right text-danger' title='" + $removeText
										+ "' style='font-size:18px'><span class='glyphicon glyphicon-remove-circle'></span></a>").appendTo($this).click(
											function () {
												let $newParent = $(this).parent();
												$newParent.removeAttr("data-trick-id");
												$newParent.removeAttr("title");
												$newParent.removeClass("success");
												$newParent.text($emptyText);
												$newParent.parent().find('input[name]').attr("value", '0');
												analysesCaching.applyCallback(callback);
												return false;
											});

									analysesCaching.applyCallback(callback);
								}
							});

						$("#analysis-build-standards div")
							.droppable(
								{
									accept: "li.list-group-item",
									activeClass: "warning",
									drop: function (event, ui) {
										let $this = $(this), $parent = $this.parent();
										let isEmpty = $("input[data-trick-field]", $parent).length;
										let callback = $parent.attr("data-trick-callback");
										if (!isEmpty) {
											$this.empty();
											$this.addClass("success");
										}
										$(analysesCaching.findAnalysisById(ui.draggable.attr("data-trick-id")).analysisStandardBaseInfo)
											.each(
												function () {

													let $selector = $("label[data-trick-id='" + this.idAnalysis + "'][data-trick-owner-id='"
														+ this.idAnalysisStandard + "']", $this), $locked = $("label[data-trick-name='" + this.name
															+ "'] input:checked", $this);

													if ($selector.length || $locked.length)
														return this;

													let data = this, $current = $("label[data-trick-name='" + data.name + "']", $this), $content = $("<label style='width:100%'></label>"), $inputs = $("<input data-trick-field='idAnalysis' hidden>"
														+ "<input data-trick-field='idAnalysisStandard' hidden>"
														+ "<input data-trick-field='name' hidden>"
														+ "<input data-trick-field='version' hidden>"
														+ "<input data-trick-field='type' hidden>");
													$content.attr("data-trick-name", data.name).attr("data-trick-version", data.version).attr(
														"data-trick-id", data.idAnalysis).attr("data-trick-type", data.type).attr(
															"data-trick-owner-id", data.idAnalysisStandard).text(
																ui.draggable.attr("title") + " - " + data.name + " v" + data.version);
													$inputs.attr("data-trick-id", data.idAnalysis).attr("data-trick-owner-id", data.idAnalysisStandard)
														.appendTo($parent);
													if ($current.length) {
														$(
															"input[data-trick-id='" + $current.attr('data-trick-id') + "']" + "[data-trick-owner-id='"
															+ $current.attr('data-trick-owner-id') + "']", $parent).remove();
														$current.replaceWith($content)
													} else
														$content.appendTo($this);
													$(
														"<a href='#' class='pull-right text-danger' data-trick-type-control='"
														+ ui.draggable.attr("data-trick-analysis-type")
														+ "' title='"
														+ $removeText
														+ "' style='font-size:18px'><span class='glyphicon glyphicon-remove-circle'></span></a>")
														.appendTo($content).click(
															function () {
																$(
																	"[data-trick-id='" + data.idAnalysis + "'][data-trick-owner-id='"
																	+ data.idAnalysisStandard + "']", $parent).remove();
																if (!$("input:hidden", $parent).length) {
																	$this.text($emptyText)
																	$this.removeClass("success");
																}

																analysesCaching.applyCallback(callback).nameAnalysisStandard();

																return false;
															});
													$locker.clone().appendTo($content).on("click", function (e) {
														let $this = $(e.currentTarget), $input = $("input", $this), $flag = $(".fa", $this);
														if ($input.is(":checked")) {
															$flag.removeClass('fa-lock');
															$flag.addClass("fa-unlock");
															$input.prop("checked", false);
														} else {
															$flag.removeClass('fa-unlock');
															$flag.addClass("fa-lock");
															$input.prop("checked", true);
														}
														return false;
													});

												});
										analysesCaching.applyCallback(callback).nameAnalysisStandard();
									}
								});

						let $saveButton = $(modal.modal_footer).find("button[name='save']"), $cancelButton = $(modal.modal_footer).find("button[name='cancel']");
						$cancelButton.click(function () {
							if (!$cancelButton.is(":disabled"))
								modal.Destroy();
							return false;
						});

						$saveButton.click(function () {
							$(modal.modal).find(".label-danger, .alert").remove();
							$(modal.modal_dialog).find("button").prop("disabled", true);
							$progress.show();
							$.ajax({
								url: context + "/Analysis/Build/Save",
								type: "post",
								data: $("form", $modalBody).serialize(),
								DataType: "application/json;charset=UTF-8",
								contentType: "application/x-www-form-urlencoded;charset=UTF-8",
								success: function (data) {
									let response = parseJson(data);
									if (typeof response == 'object') {
										if (response.error != undefined)
											showDialog("error", response.error);
										else if (response.success != undefined) {
											updateAnalysisFilter($("form select[name='customer']", $modalBody).val(), "ALL");
											showDialog("success", response.success);
											$saveButton.unbind();
											modal.Destroy();
										} else {
											let errorContainer = document.getElementById("build-analysis-modal-error");
											for (let error in response) {
												let errorElement = document.createElement("label");
												errorElement.setAttribute("class", "label label-danger");
												$(errorElement).text(response[error]);
												switch (error) {
													case "customer":
													case "language":
													case "comment":
													case "author":
													case "version":
													case "assessment":
													case "profile":
													case "impacts":
													case "scale.level":
													case "name":
														$(errorElement).appendTo($("form *[name='" + error + "']", $modalBody).parent());
														break;
													case "riskInformation":
													case "scope":
													case "asset":
													case "scenario":
													case "standards":
													case "parameter":
														$(errorElement).appendTo($("[data-trick-name='" + error + "']", $modalBody));
														break;
													default:
														$(showError(errorContainer, response[error])).css({
															'margin-bottom': '0',
															'padding': '6px 10px'
														});
												}
											}

											if ($(".label-danger", $generalTab).length) {
												if ($generalTab.is(":hidden"))
													$("a[href='#group_1']", $modalContent).tab("show");
											} else if ($(".label-danger", $advanceTab).length) {
												if ($advanceTab.is(":hidden"))
													$("a[href='#group_2']", $modalContent).tab("show");
											}
										}
									} else
										unknowError();
								},
								error: unknowError
							}).complete(function () {
								$(modal.modal_dialog).find("button").prop("disabled", false);
								$progress.hide();
							});
						});
						$analysisType.trigger("change");
						$impacts.trigger("change");
						modal.Show();
					}
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
	return false;
}

/* History */
function addHistory(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		let selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
		//oldVersion = $("#section_analysis tr[data-trick-id='" + analysisId + "']>td:nth-child(6)").text();
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/" + analysisId + "/NewVersion",
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("#addHistoryModal", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$view.appendTo("#widget").modal("show").on("hidden.bs.modal", () => $view.remove());
				} else
					unknowError();
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
 * Edits a single analysis.
 * 
 * @param {number} analysisId - The ID of the analysis to be edited.
 * @returns {boolean} - Returns false if the analysis ID is null or undefined, or if the user does not have the required permissions. Otherwise, returns true.
 */
function editSingleAnalysis(analysisId) {

	if (analysisId == null || analysisId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (!isArchived(analysisId) && userCan(analysisId, ANALYSIS_RIGHT.ALL)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Edit/" + analysisId,
			type: "get",
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("#editAnalysisModel", new DOMParser().parseFromString(response, "text/html"));
				if (!$view.length) {
					showDialog("#alert-dialog", MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
				} else {
					$view.appendTo("#widget").modal('show').on("hidden.bs.modal", () => $view.remove());

					$("button[name='save']", $view).on("click", e => {
						$progress.show();
						$.ajax({
							url: context + "/Analysis/Save",
							type: "post",
							data: serializeForm($("form", $view)),
							contentType: "application/json;charset=UTF-8",
							success: function (response, textStatus, jqXHR) {
								let hasError = false;
								$(".label-danger", $view).remove();
								for (const error in response) {
									let $errorElement = $("<label class='label label-danger' />").text(response[error]);
									switch (error) {
										case "customer":
											$errorElement.appendTo($("#customercontainer", $view));
											break;
										case "language":
											$errorElement.appendTo($("#languagecontainer", $view));
											break;
										case "label":
										case "profile":
										case "owner":
										case "version":
											$errorElement.appendTo($("input[name='" + error + "']", $view).parent());
											break;
										default:
											showDialog("#alert-dialog", response[error]);
											break;
									}
									hasError = true;
								}
								if (!hasError) {
									$view.modal("hide");
									reloadSection("section_analysis");
								}
							},
							error: unknowError
						}).complete(function () {
							$progress.hide();
						});
					});
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
 * Selects an analysis based on the provided analysisId and mode.
 * If analysisId is not provided, it selects the analysis from the "section_analysis" dropdown.
 * 
 * @param {string} analysisId - The ID of the analysis to select.
 * @param {string} mode - The mode of the analysis selection.
 * @returns {boolean} - Returns false if the analysis selection fails, otherwise returns undefined.
 */
function selectAnalysis(analysisId, mode) {
	if (analysisId == null || analysisId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	let open = OPEN_MODE.valueOf(mode);
	if (open === OPEN_MODE.READ || !isArchived() && userCan(analysisId, ANALYSIS_RIGHT.MODIFY)) {
		$("#loading-indicator").show();
		setTimeout(() => {
			window.location.replace(context + "/Analysis/" + analysisId + "/Select?open=" + open.value);
		}, 1);
	}
	return false;
}

/**
 * Duplicates an analysis.
 *
 * @param {Object} form - The form object containing the analysis data.
 * @param {string} analyisId - The ID of the analysis to be duplicated.
 * @returns {boolean} - Returns false.
 */
function duplicateAnalysis(form, analyisId) {
	let $progress = $("#loading-indicator").show(), $modal = $("#addHistoryModal");
	$.ajax({
		url: context + "/Analysis/Duplicate/" + analyisId,
		type: "post",
		data: serializeForm(form),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			$(".label-danger", $modal).remove();
			if (response["analysis_task_id"] == undefined) {
				for (let error in response) {
					let errorElement = document.createElement("label");
					errorElement.setAttribute("class", "label label-danger");
					$(errorElement).text(response[error]);
					switch (error) {
						case "author":
							$(errorElement).appendTo($("#history_author", $modal).parent());
							break;
						case "version":
							$(errorElement).appendTo($("#history_version", $modal).parent());
							break;
						case "comment":
							$(errorElement).appendTo($("#history_comment", $modal).parent());
							break;
						default:
							showDialog("#alert-dialog", response[error]);
							break;
					}
				}
			} else {
				$modal.modal("hide");
				application["taskManager"].Start();
			}
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Archives the analysis if it is not already archived and the user has the permission to manage access.
 * @returns {boolean} Returns false.
 */
function archiveAnalysis() {
	if (!isArchived() && canManageAccess()) {
		let $modal = showDialog("#confirm-dialog", MessageResolver("label.analysis.question.archive", "Are you sure that you want to archive the analysis?"));
		$("button[name='yes']", $modal).unbind().one("click", function () {
			let $progress = $("#loading-indicator").show(), analysisIds = findSelectItemIdBySection(("section_analysis"));
			$.ajax({
				url: context + "/Analysis/Archive/" + analysisIds[0],
				type: "POST",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					if (response.success != undefined)
						reloadSection("section_analysis");
					else if (response.error != undefined)
						showDialog("#alert-dialog", response.error)
					return false;
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
			});
			$modal.modal("hide");
			return false;
		});
	}
	return false;
}

/**
 * Updates the analysis filter based on the provided customer ID and trick name.
 * 
 * @param {number} customerId - The ID of the customer.
 * @param {string} trickName - The name of the trick.
 * @returns {boolean} - Returns false.
 */
function updateAnalysisFilter(customerId, trickName) {
	let $customer = $("#nameSelectorFilter"), $analysis = $("#nameSelectorFilter");
	if ($.isNumeric(customerId))
		$customer.val(customerId);
	if (trickName)
		$analysis.val(trickName);
	else $analysis.val("ALL");
	$customer.trigger("change");
	return false;
}

/**
 * Handles the customer change event and performs an AJAX request to display analysis data.
 * @param {HTMLElement} customer - The customer element.
 * @param {HTMLElement} nameFilter - The name filter element.
 * @returns {boolean} - Returns false to prevent the default form submission.
 */
function customerChange(customer, nameFilter) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/DisplayByCustomer/" + $(customer).val(),
		type: "post",
		async: true,
		contentType: "application/json;charset=UTF-8",
		data: $(nameFilter).val(),
		success: function (response, textStatus, jqXHR) {
			let $newSection = $("#section_analysis", new DOMParser().parseFromString(response, "text/html"));
			if ($newSection.length) {
				$("#section_analysis").replaceWith($newSection);
				$(document).ready(function () {
					$("input[type='checkbox']").removeAttr("checked");
					$("#section_analysis table").stickyTableHeaders({
						cssTopOffset: ".navbar-fixed-top"
					});
				});
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Imports analysis data for a specific customer.
 * 
 * @returns {boolean} Returns false if the customer is not selected or invalid.
 */
function importAnalysis() {
	let customer = parseInt($("#customerSelectorFilter").val());
	if (!customer || customer < 1)
		return false;
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Data-manager/Sqlite/Import-form?idCustomer=" + customer,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $viewModal = $("#analysis-import-dialog", new DOMParser().parseFromString(response, "text/html"));
			if ($viewModal.length) {
				$viewModal.appendTo("#widget").modal("show").on("hidden.bs.modal", (e) => $viewModal.remove());

				let $btnBrowse = $("button[name='browse']", $viewModal), $inputFile = $("input[name='file']", $viewModal), $importBtn = $("button[name='import']", $viewModal),

					$fileInfo = $("input[name='filename']", $viewModal), $form = $("form", $viewModal), $btnSubmit = $("button[name='submit']", $viewModal);

				$importBtn.on("click", (e) => $btnSubmit.trigger("click"));

				$btnBrowse.on("click", (e) => $inputFile.trigger("click"));

				let updateImportButtonState = () => {
					let fileValue = $fileInfo.val();
					$importBtn.prop("disabled", fileValue === "" || !fileValue);
				}

				$inputFile.on("change", (e) => {
					let value = $inputFile.val();
					if (value.trim() === '')
						updateImportButtonState();
					else {
						let size = parseInt($inputFile.attr("maxlength"))
						if ($inputFile[0].files[0].size > size) {
							showDialog("error", MessageResolver("error.file.too.large", undefined, size));
							return false;
						} else if (!checkExtention(value, ".sqlite,.SQLITE,.tsdb,.TSDB", $importBtn))
							return false
					}
					$fileInfo.val(value);
				});

				updateImportButtonState();

				$form.on("submit", (e) => {
					$progress.show();
					$.ajax({
						url: context + "/Analysis/Data-manager/Sqlite/Import-process",
						type: 'POST',
						data: new FormData($form[0]),
						cache: false,
						contentType: false,
						processData: false,
						success: function (response, textStatus, jqXHR) {
							if (response.success) {
								$viewModal.modal("hide");
								showDialog("success", response.success);
								application["taskManager"].Start();
							}
							else if (response.error)
								showDialog("#alert-dialog", response.error);
							else
								unknowError();
						},
						error: unknowError

					}).complete(function () {
						$progress.hide();
					});

					return false;

				});

			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}