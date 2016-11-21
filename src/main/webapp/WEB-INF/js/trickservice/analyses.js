var taskManager = undefined, analysesCaching = undefined;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	application["settings-fixed-header"] = {
		fixedOffset : $(".navbar-fixed-top"),
		scrollStartFixMulti : 1.02
	};
	fixTableHeader("#section_analysis table");
});

function manageAnalysisAccess(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	if (canManageAccess()) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/ManageAccess/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $content = $("#manageAnalysisAccessModel", new DOMParser().parseFromString(response, "text/html"));
				if ($content.length) {
					$("#manageAnalysisAccessModel").replaceWith($content);
					$content.modal("show").find(".modal-footer button[name='save']").one("click", updateAnalysisAccess);
				} else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		permissionError();
	return false;

}

function updateAnalysisAccess(e) {

	var $progress = $("#loading-indicator").show(), $modal = $("#manageAnalysisAccessModel"), me = $modal.attr("data-trick-user-id"), data = {
		analysisId : $modal.attr("data-trick-id"),
		userRights : {}
	};

	$modal.find(".form-group[data-trick-id][data-default-value]").each(function() {
		var $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
		if (newRight != oldRight) {
			data.userRights[$this.attr("data-trick-id")] = {
				oldRight : oldRight == "" ? undefined : oldRight,
				newRight : newRight == "" ? undefined : newRight
			};
		}
	});

	if (Object.keys(data.userRights).length) {
		$.ajax({
			url : context + "/Analysis/ManageAccess/Update",
			type : "post",
			data : JSON.stringify(data),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response.error != undefined)
					showDialog("#alert-dialog", response.error);
				else if (response.success != undefined) {
					if (data.userRights[me] != undefined && data.userRights[me].oldRight != data.userRights[me].newRight)
						reloadSection("section_analysis");
					else
						showDialog("#info-dialog", response.success);
				} else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		$progress.hide();
}

function manageAnalysisIDSAccess(section) {
	var selectedAnalysis = findSelectItemIdBySection(section);
	if (selectedAnalysis.length != 1)
		return false;
	if (canManageAccess()) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Manage/IDS/" + selectedAnalysis[0],
			type : "GET",
			success : function(response, textStatus, jqXHR) {
				var $content = $("#manageAnalysisIDSAccessModel", new DOMParser().parseFromString(response, "text/html"));
				if ($content.length) {
					$content.appendTo("#widget").on("hidden.bs.modal", function() {
						$content.remove();
					}).modal("show").find(".modal-footer button[name='save']").one("click", function() {
						var data = {};
						$content.find(".form-group[data-trick-id][data-default-value]").each(function() {
							var $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
							if (newRight != oldRight)
								data[this.getAttribute("data-trick-id")] = newRight;
						});
						if (Object.keys(data).length) {
							$.ajax({
								url : context + "/Analysis/Manage/IDS/" + selectedAnalysis[0] + "/Update",
								type : "post",
								data : JSON.stringify(data),
								contentType : "application/json;charset=UTF-8",
								success : function(response, textStatus, jqXHR) {
									if (response.error != undefined)
										showDialog("#alert-dialog", response.error);
									else if (response.success != undefined)
										showDialog("#info-dialog", response.success);
									else
										unknowError();
								},
								error : unknowError
							}).complete(function() {
								$progress.hide();
							});
						} else
							$progress.hide();
					});
				} else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		permissionError();
	return false;

}

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

function disableifprofile(section, menu) {
	var element = $(menu + " li[class='profilemenu']");
	element.addClass("disabled");
	var isProfile = findTrickisProfile($(section + " tbody :checked"));
	if (isProfile != undefined && isProfile != null) {
		if (isProfile == "true")
			element.addClass("disabled");
		else
			element.removeClass("disabled");
	}
}

function saveAnalysis(form, reloadaction) {
	$("#editAnalysisModel .progress").show();
	$("#editAnalysisModel #editAnalysisButton").prop("disabled", true);
	$.ajax({
		url : context + "/Analysis/Save",
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#editAnalysisModel .progress").hide();
			$("#editAnalysisModel #editAnalysisButton").prop("disabled", false);
			var alert = $("#editAnalysisModel .label-danger");
			if (alert.length)
				alert.remove();
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "analysiscustomer":
					$(errorElement).appendTo($("#analysiscustomercontainer"));
					break;

				case "analysislanguage":
					$(errorElement).appendTo($("#analysislanguagecontainer"));
					break;

				case "comment":
					$(errorElement).appendTo($("#analysis_label").parent());

					break;

				case "profile":
					$(errorElement).appendTo($("#analysis_form select[name='profile']").parent());
					break;

				case "author":
					$(errorElement).appendTo($("#analysis_form input[name='author']").parent());
					break;

				case "version":
					$(errorElement).appendTo($("#analysis_version").parent());
					break;

				case "analysis":
					$(errorElement).appendTo($("#editAnalysisModel .modal-body"));
					break;
				}
			}
			if (!$("#editAnalysisModel .label-danger").length) {
				$("#editAnalysisModel").modal("hide");
				reloadSection("section_analysis");
			}
			return false;
		},
		error : unknowError
	});
	return false;
}

function deleteAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection(("section_analysis"));
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.MODIFY)) {
		var $modal = showDialog("#deleteAnalysisModel", MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));
		$("button[data-action='delete']", $modal).one("click", function() {
			var $progress = $("#loading-indicator").show(), $buttons = $(".btn", $modal).prop("disabled", true);
			$.ajax({
				url : context + "/Analysis/Delete/" + analysisId,
				type : "POST",
				contentType : "application/json;charset=UTF-8",
				success : function(response, textStatus, jqXHR) {
					if (response.success != undefined) {
						reloadSection("section_analysis");
					} else if (response.error != undefined)
						showDialog("#alert-dialog", response.error)
					return false;
				},
				error : unknowError
			}).complete(function() {
				$buttons.prop("disabled", false);
				$modal.modal("hide");
				$progress.hide();
			});
			return false;
		});

	} else
		permissionError();
	return false;
}

function createAnalysisProfile(analysisId, section_analysis) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection((section_analysis));
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.READ)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/AnalysisProfile/Add/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				if ((analysisProfile = doc.getElementById("analysisProfileModal")) == null)
					return false;
				$(analysisProfile).appendTo("#wrap");
				$(analysisProfile).on('hidden.bs.modal', function() {
					$(analysisProfile).remove();
				});

				var allVal = new Array();

				$('#analysisProfileform .list-group-item.active').each(function() {
					allVal.push($(this).attr("data-trick-opt"));
				});

				$('#standards').val(allVal);

				$('#analysisProfileform .list-group-item').on('click', function() {
					$(this).toggleClass('active');
					if ($(this).hasClass("active"))
						$(this).css("border", "1px solid #dddddd");
					else
						$(this).css("border", "");
					allVal = new Array();
					$('#analysisProfileform .list-group-item.active').each(function() {
						allVal.push($(this).attr("data-trick-opt"));
					});
					$('#standards').val(allVal);
				});

				$(analysisProfile).modal("toggle");

			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}

function saveAnalysisProfile(form) {
	var $modal = $("#analysisProfileModal"), $progress = $("#loading-indicator").show(), $form = $("#" + form), data = {
		"id" : $form.find("#id").val(),
		"description" : $form.find("#name").val()
	};

	$(".label-danger", $form).remove();

	$form.find("select[name='standards'] option").each(function() {
		data[this.value] = $(this).is(":checked");
	});

	$.ajax({
		url : context + "/AnalysisProfile/Save",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			for ( var error in response) {
				if (error === "taskid")
					continue;
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "description":
					$(errorElement).appendTo($("#name", $form).parent());
					break;
				case "analysisprofile":
				default:
					$(errorElement).appendTo($form.parent());
					break;
				}
			}

			if (!$(".label-danger", $form).length) {
				application["taskManager"].Start();
				$modal.modal("hide");
			}
			return false;

		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function customAnalysis(element) {
	if ($(element).parent().hasClass("disabled"))
		return false;
	var $progress = $("#loading-indicator").show();
	$
			.ajax(
					{
						url : context + "/Analysis/Build",
						type : "get",
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							var $modalContent = $("#buildAnalysisModal", new DOMParser().parseFromString(response, "text/html"));
							if (!$modalContent.length)
								unknowError();
							else {
								var $old = $("#buildAnalysisModal");
								if ($old.length)
									$old.replaceWith($modalContent);
								else
									$modalContent.appendTo("#widget");
								var modal = new Modal($modalContent), $modalBody = $(modal.modal_body), $emptyText = $modalBody.find("*[dropzone='true']>div:first").text(), $removeText = MessageResolver(
										"label.action.delete", "Delete"), $lockText = MessageResolver("label.action.lock", "Lock"), $analysisSelector = $("#selector-analysis",
										$modalBody), $impacts = $("select[name='impacts']", $modalBody), $generalTab = $("#group_1"), $advanceTab = $("#group_2");
								// load data from database and manage caching

								analysesCaching = {
									versions : {},
									identifiers : {},
									customers : {},
									cloneWidth : undefined,
									assessmentDisable : true,
									analysisType : undefined,
									saveVersions : function(identifier, data) {
										for (var i = 0; i < data.length; i++)
											this.versions[data[i].id] = data[i];
										this.identifiers[identifier] = data;
										return this;
									},
									saveIdentifier : function(idCustomer, data) {
										if (!this.cloneWidth)
											this.cloneWidth = this.cloneWidth = $("#analysis-build-standards").width();
										this.customers[idCustomer] = data;
										return this;
									},
									updateAnalysisVersions : function(idCustomer, identifier) {
										if (this.identifiers[identifier] == undefined)
											this.loadByCustomerIdAndIdentifier(idCustomer, identifier);
										else
											this.updateVersionSelector(identifier);
										return this;
									},
									updateAnalysisIdentifiers : function(idCustomer) {
										if (this.customers[idCustomer] == undefined)
											this.loadByCustomerId(idCustomer);
										else
											this.updateAnalysisSelector(idCustomer);
										return this;
									},
									findAnalysisById : function(idAnalysis) {
										return this.versions[idAnalysis];
									},
									loadByCustomerIdAndIdentifier : function(idCustomer, identifier) {
										var instance = this;
										$progress.show();
										$.ajax({
											url : context + "/Analysis/Build/Customer/" + idCustomer + "/Identifier/" + identifier,
											type : "get",
											contentType : "application/json;charset=UTF-8",
											success : function(response, textStatus, jqXHR) {
												if (typeof response == 'object')
													instance.saveVersions(identifier, response).updateVersionSelector(identifier);
												else
													unknowError();
											}
										}).complete(function() {
											$progress.hide();
										});
									},
									loadByCustomerId : function(idCustomer) {
										var instance = this;
										$progress.show();
										$.ajax({
											url : context + "/Analysis/Build/Customer/" + idCustomer,
											type : "get",
											contentType : "application/json;charset=UTF-8",
											success : function(response, textStatus, jqXHR) {
												if (typeof response == 'object')
													instance.saveIdentifier(idCustomer, response).updateAnalysisIdentifiers(idCustomer);
												else
													unknowError();
											}
										}).complete(function() {
											$progress.hide();
										});
									},
									updateVersionSelector : function(identifier) {
										var instance = this, versions = this.identifiers[identifier], $analysisVersions = $("#analysis-versions", $modalBody);
										for (var i = 0; i < versions.length; i++) {
											if (versions[i].type == instance.analysisType) {
												var $li = $("<li class='list-group-item' data-trick-id='" + versions[i].id + "' data-trick-analysis-type='" + instance.analysisType
														+ "' title='" + versions[i].label + " v." + versions[i].version + "'>" + versions[i].version + "</li>");
												$li.appendTo($analysisVersions);
											}
										}

										$("#analysis-versions li", $modalBody).hover(function() {
											$(this).css('cursor', 'move');
										}).draggable({
											helper : "clone",
											cancel : "span.glyphicon-remove-sign",
											revert : "invalid",
											containment : "#group_2",
											accept : "*[dropzone='true']",
											cursor : "move",
											start : function(e, ui) {
												$(ui.helper).css({
													'z-index' : '1385',
													'min-width' : instance.cloneWidth,
													'border-radius' : "5px"
												});
											}
										});
										return this;
									},
									updateAnalysisSelector : function(idCustomer) {
										var identifiers = this.customers[idCustomer];
										var $analysisSelector = $("#selector-analysis");
										for (var i = 0; i < identifiers.length; i++)
											$("<option value='" + identifiers[i].identifier + "'>" + identifiers[i].label + "</option>").appendTo($analysisSelector);
										return this;
									},

									applyCallback : function(callback) {
										if (callback == undefined)
											return;
										if ($.isArray(callback)) {
											for (var i = 0; i < callback.length; i++)
												eval(callback[i]);
										} else
											eval(callback);
										return this;
									},
									checkPhase : function() {
										var $phaseInput = $("input[name='phase']", $modalBody), $standards = $("#analysis-build-standards>div>label");
										if (!$standards.length)
											$phaseInput.prop("disabled", true).prop("checked", false);
										else {
											$phaseInput.prop("disabled", false);
											var idAnalysis = -1;
											for (var i = 0; i < $standards.length; i++) {
												var currentIdAnalysis = $($standards[i]).attr("data-trick-id");
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
									checkRiskDependancies : function() {
										var trick_id_asset = $("#analysis-build-assets .well").attr("data-trick-id"), trick_id_parameter = $("#analysis-build-parameters .well")
												.attr("data-trick-id"), trick_id_scenario = $("#analysis-build-scenarios .well").attr("data-trick-id"), estimation = trick_id_parameter == undefined
												|| trick_id_parameter != trick_id_asset || trick_id_asset != trick_id_scenario || trick_id_asset == undefined;
										$modalBody.find("input[name='assessment']").prop("disabled", this.assessmentDisable = estimation).prop("checked", false);
										return this.checkProfile();
									},
									checkProfile : function() {
										$modalBody.find("input[name='riskProfile']").prop("checked", false).prop("disabled",
												this.assessmentDisable || this.analysisType != 'QUALITATIVE');
										$modalBody.find("input[name='uncertainty']").prop("checked", false).prop("disabled", this.analysisType == 'QUALITATIVE');
										$modalBody.find("a[data-trick-type-control][data-trick-type-control!='" + this.analysisType + "']").trigger("click");
										var type = this.analysisType;
										$modalBody.find("select[name='profile']").val("-1").find("option[value!='-1']").each(function() {
											if (this.getAttribute("data-type") == type)
												this.removeAttribute("hidden");
											else
												this.setAttribute("hidden", "hidden")
										});

										$analysisSelector.trigger("change");
										return this.checkParameter();
									},
									checkAssetStandard : function() {
										var analysisAsset = $("#analysis-build-assets input").val();
										if (analysisAsset == -1)
											return this;
										var $parent = $("#analysis-build-standards"), $label = $("#analysis-build-standards label[data-trick-type='ASSET'][data-trick-id!='"
												+ analysisAsset + "']");
										$label.each(function() {
											var $this = $(this);
											$("[data-trick-id='" + $this.attr("data-trick-id") + "'][data-trick-owner-id='" + $this.attr("data-trick-owner-id") + "']", $parent)
													.remove();
										});
										if ($label.length)
											this.nameAnalysisStandard();
										return this;
									},
									checkParameter : function() {
										var trick_id_parameter = $("#analysis-build-parameters .well").attr("data-trick-id") == undefined;
										if (this.analysisType != 'QUALITATIVE')
											this.changeImpactState(true);
										else if (trick_id_parameter) {
											if ($impacts.is(":disabled"))
												this.changeImpactState(false);
										} else if (!$impacts.is(":disabled"))
											this.changeImpactState(true);
										return this;
									},
									changeImpactState : function(enable) {
										$impacts.val('-1').prop("disabled", enable);
										$modalBody.find("input[name='scale.level']").val('11').prop("disabled", enable);
										return this;
									},
									nameAnalysisStandard : function() {
										$("#analysis-build-standards label").each(
												function(i) {
													var $this = $(this);
													$(
															"#analysis-build-standards input[data-trick-id='" + $this.attr("data-trick-id") + "'][data-trick-owner-id='"
																	+ $this.attr("data-trick-owner-id") + "']").each(function() {
														var $input = $(this), fieldName = $input.attr('data-trick-field');
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

								var $locker = $("<a href='#' style='margin-right:3px;' class='pull-right' title='" + $lockText
										+ "'  ><i class='fa fa-unlock'></i><input hidden class='pull-right' type='checkbox' style='margin-right:3px; margin-left:3px' ></a>"), $analysisType = $(
										"input[name='type']", $modalContent);

								// Event user select a customer
								$("#selector-customer").on("change", function(e) {
									$("#selector-analysis option[value!=-1]").remove();
									$("#analysis-versions li[class!='disabled']").remove();
									var idCustomer = $(e.target).val();
									if (idCustomer < 0)
										$("#selector-analysis option[value=-1]").prop("selected", true);
									else
										analysesCaching.updateAnalysisIdentifiers(idCustomer);
								});

								$analysisSelector.on("change", function(e) {
									$("#analysis-versions li[class!='disabled']", $modalBody).remove();
									var identifier = $(e.target).val();
									if (identifier != -1)
										analysesCaching.updateAnalysisVersions($("#selector-customer").val(), identifier)

								});

								$analysisType.on("change", function(e) {
									if (e.currentTarget.checked) {
										analysesCaching.analysisType = e.currentTarget.value;
										analysesCaching.checkProfile();
									}
								});

								$impacts.on("change", function() {
									var options = this.selectedOptions;
									if (options[0].value == "-1" && options.length > 1)
										$(options[0]).prop("selected", false)
								});

								$("#analysis-build-parameters").attr("data-trick-callback", "analysesCaching.checkRiskDependancies()");
								$("#analysis-build-scenarios").attr("data-trick-callback", "analysesCaching.checkRiskDependancies()");
								$("#analysis-build-assets").attr("data-trick-callback", "analysesCaching.checkRiskDependancies().checkAssetStandard()");
								$("#analysis-build-standards").attr("data-trick-callback", "analysesCaching.checkPhase().checkAssetStandard()");

								$modalBody.find("*[dropzone='true'][id!='analysis-build-standards']>div").droppable(
										{
											accept : "li.list-group-item",
											activeClass : "warning",
											drop : function(event, ui) {
												var $this = $(this), $parent = $this.parent();
												$this.attr("data-trick-id", ui.draggable.attr("data-trick-id"));
												$this.attr("title", ui.draggable.attr("title"));
												$this.text(ui.draggable.attr("title"));
												$this.addClass("success");
												$parent.find('input[name]').attr("value", ui.draggable.attr("data-trick-id"));
												var callback = $parent.attr("data-trick-callback");
												$(
														"<a href='#' data-trick-type-control='" + ui.draggable.attr("data-trick-analysis-type")
																+ "' class='pull-right text-danger' title='" + $removeText
																+ "' style='font-size:18px'><span class='glyphicon glyphicon-remove-circle'></span></a>").appendTo($this).click(
														function() {
															var $newParent = $(this).parent();
															$newParent.removeAttr("data-trick-id");
															$newParent.removeAttr("title");
															$newParent.removeClass("success");
															$newParent.text($emptyText);
															$newParent.parent().find('input[name]').attr("value", '-1');
															analysesCaching.applyCallback(callback);
															return false;
														});

												analysesCaching.applyCallback(callback);
											}
										});

								$("#analysis-build-standards div")
										.droppable(
												{
													accept : "li.list-group-item",
													activeClass : "warning",
													drop : function(event, ui) {
														var $this = $(this), $parent = $this.parent();
														var isEmpty = $("input[data-trick-field]", $parent).length;
														var callback = $parent.attr("data-trick-callback");
														if (!isEmpty) {
															$this.empty();
															$this.addClass("success");
														}
														$(analysesCaching.findAnalysisById(ui.draggable.attr("data-trick-id")).analysisStandardBaseInfo)
																.each(
																		function() {

																			var $selector = $("label[data-trick-id='" + this.idAnalysis + "'][data-trick-owner-id='"
																					+ this.idAnalysisStandard + "']", $this), $locked = $("label[data-trick-name='" + this.name
																					+ "'] input:checked", $this);

																			if ($selector.length || $locked.length)
																				return this;

																			var data = this, $current = $("label[data-trick-name='" + data.name + "']", $this), $content = $("<label style='width:100%'></label>"), $inputs = $("<input data-trick-field='idAnalysis' hidden>"
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
																							function() {
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
																			$locker.clone().appendTo($content).on("click", function(e) {
																				var $this = $(e.currentTarget), $input = $("input", $this), $flag = $(".fa", $this);
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

								var $saveButton = $(modal.modal_footer).find("button[name='save']"), $cancelButton = $(modal.modal_footer).find("button[name='cancel']"), $progress_bar = $modalBody
										.find(".progress");
								$cancelButton.click(function() {
									if (!$cancelButton.is(":disabled"))
										modal.Destroy();
									return false;
								});

								$saveButton.click(function() {
									$(modal.modal).find(".label-danger, .alert").remove();
									$(modal.modal_dialog).find("button").prop("disabled", true);
									$progress.show();
									$.ajax({
										url : context + "/Analysis/Build/Save",
										type : "post",
										data : $("form", $modalBody).serialize(),
										contentType : "application/x-www-form-urlencoded;charset=UTF-8",
										async : false,
										success : function(data, textStatus, jqXHR) {
											var response = parseJson(data);
											if (typeof response == 'object') {
												if (response.error != undefined)
													$(showError($("#build-analysis-modal-error", $modalBody)[0], response.error)).css({
														'margin-bottom' : '0',
														'padding' : '6px 10px'
													});
												else if (response.success != undefined) {
													$(showSuccess($("#build-analysis-modal-error", $modalBody)[0], response.success)).css({
														'margin-bottom' : '0',
														'padding' : '6px 10px'
													});
													setTimeout(function() {
														modal.Destroy();
														reloadSection("section_analysis");
													}, 3000);
													$saveButton.unbind();
												} else {
													var errorContainer = document.getElementById("build-analysis-modal-error");
													for ( var error in response) {
														var errorElement = document.createElement("label");
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
																'margin-bottom' : '0',
																'padding' : '6px 10px'
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
										error : unknowError
									}).complete(function() {
										$(modal.modal_dialog).find("button").prop("disabled", false);
										$progress.hide();
									});
								})
								$analysisType.trigger("change");
								modal.Show();
							}
							return false;
						},
						error : unknowError
					}).complete(function() {
				$progress.hide();
			});
	return false;
}

/* History */
function addHistory(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
		oldVersion = $("#section_analysis tr[data-trick-id='" + analysisId + "']>td:nth-child(6)").text();
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/" + analysisId + "/NewVersion",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#addHistoryModal");
				if ($content.length) {
					$("#addHistoryModal").replaceWith(response);
					$('#addHistoryModal').modal("toggle");
				} else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

function editSingleAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}

	if (userCan(analysisId, ANALYSIS_RIGHT.MODIFY)) {
		$("#editAnalysisModel .progress").hide();
		$("#editAnalysisModel #editAnalysisButton").prop("disabled", false);
		$.ajax({
			url : context + "/Analysis/Edit/" + analysisId,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var doc = new DOMParser().parseFromString(response, "text/html");
				if ((form = doc.getElementById("form_edit_analysis")) == null) {
					$("#alert-dialog .modal-body").html(MessageResolver("error.unknown.data.loading", "An unknown error occurred during data loading"));
					$("#alert-dialog").modal("toggle");
				} else {
					$("#analysis_form").html($(form).html());
					$("#editAnalysisModel-title").text(MessageResolver("title.analysis.Update", "Update an Analysis"));
					$("#editAnalysisButton").text(MessageResolver("label.action.save", "Save"));
					$("#analysis_form").prop("action", "/Save");
					$("#editAnalysisModel").modal('toggle');
				}
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function selectAnalysis(analysisId, mode) {
	if (analysisId == null || analysisId == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	var open = OPEN_MODE.valueOf(mode), right = open === OPEN_MODE.READ ? ANALYSIS_RIGHT.READ : ANALYSIS_RIGHT.MODIFY;
	if (userCan(analysisId, right))
		window.location.replace(context + "/Analysis/" + analysisId + "/Select?open=" + open.value + "");
	return false;
}

function calculateActionPlan(analysisId) {
	var analysisID = -1;
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (!selectedAnalysis.length)
			return false;
		while (selectedAnalysis.length) {
			rowTrickId = selectedAnalysis.pop();
			if (userCan(rowTrickId, ANALYSIS_RIGHT.READ)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}

	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.READ)) {
		$.ajax({
			url : context + "/Analyis/ActionPlan/Compute",
			type : "post",
			data : JSON.stringify({
				"id" : analysisID
			}),
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					application["taskManager"].Start();
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function calculateRiskRegister(analysisId) {

	var analysisID = -1;

	if (analysisId == null || analysisId == undefined) {

		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (!selectedAnalysis.length)
			return false;
		while (selectedAnalysis.length) {
			rowTrickId = selectedAnalysis.pop();
			if (userCan(rowTrickId, ANALYSIS_RIGHT.READ)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}

	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.READ)) {
		$.ajax({
			url : context + "/Analyis/RiskRegister/Compute",
			type : "post",
			data : JSON.stringify({
				"id" : analysisID
			}),
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined) {
					application["taskManager"].Start();
				} else if (response["error"] != undefined) {
					$("#alert-dialog .modal-body").html(response["error"]);
					$("#alert-dialog").modal("toggle");
				} else
					unknowError();
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function duplicateAnalysis(form, analyisId) {
	var $progress = $("#loading-indicator").show(), $modal = $("#addHistoryModal"), oldVersion = $("#history_oldVersion", $modal).prop("value"), $buttons = $modal.find(
			"[data-action!='cancel']").prop("disabled", true);
	$(".label-danger", $modal).remove();
	$("[class='alert alert-warning']", $modal);
	$.ajax({
		url : context + "/Analysis/Duplicate/" + analyisId,
		type : "post",
		data : serializeForm(form),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			$("#history_oldVersion", $modal).attr("value", oldVersion);
			var errorcounter = 0;
			for ( var error in response) {
				var errorElement = document.createElement("label");
				errorElement.setAttribute("class", "label label-danger");
				$(errorElement).text(response[error]);
				switch (error) {
				case "author":
					errorcounter++;
					$(errorElement).appendTo($("#history_author", $modal).parent());
					break;
				case "version":
					$(errorElement).appendTo($("#history_version", $modal).parent());
					errorcounter++;
					break;
				case "comment":
					errorcounter++;
					$(errorElement).appendTo($("#history_comment", $modal).parent());
					break;
				case "analysis":
					errorcounter++;
					var alertElement = document.createElement("div");
					alertElement.setAttribute("class", "alert alert-warning");
					$(alertElement).text($(errorElement).text());
					$(".modal-body", $modal).prepend($(alertElement));
					break;
				}
			}
			if (errorcounter == 0) {
				$modal.modal("hide");
				application["taskManager"].Start();
			}
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
		$buttons.prop("disabled", false);
	});
	return false;
}

function customerChange(customer, nameFilter) {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/DisplayByCustomer/" + $(customer).val(),
		type : "post",
		async : true,
		contentType : "application/json;charset=UTF-8",
		data : $(nameFilter).val(),
		success : function(response, textStatus, jqXHR) {
			var $newSection = $("#section_analysis", new DOMParser().parseFromString(response, "text/html"));
			if ($newSection.length) {
				$("#section_analysis").replaceWith($newSection);
				$(document).ready(function() {
					$("input[type='checkbox']").removeAttr("checked");
					$("#section_analysis table").stickyTableHeaders({
						cssTopOffset : ".navbar-fixed-top"
					});
				});
			} else
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function linkToProject() {
	var idAnalysis = findSelectItemIdBySection("section_analysis")
	if (idAnalysis.length != 1)
		return false;
	if (userCan(idAnalysis[0], ANALYSIS_RIGHT.ALL)) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/" + idAnalysis[0] + "/Ticketing/Load",
			type : "GET",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				var $modal = $("#modal-ticketing-project-linker", new DOMParser().parseFromString(response, "text/html")), updateRequired = false;
				if (!$modal.length)
					unknowError();
				else {
					$modal.appendTo("#widget").modal("show").on("hidden.bs.modal", function() {
						if (updateRequired)
							reloadSection("section_analysis");
						$modal.remove();
					});

					var $selector = $modal.find("select[name='project']");
					$modal.find("button[name='save']").on("click", function() {
						var project = $selector.val();
						if (project != undefined && project.length) {
							$.ajax({
								url : context + "/Analysis/" + idAnalysis[0] + "/Ticketing/Link",
								type : "POST",
								async : false,
								data : $selector.val(),
								contentType : "application/json;charset=UTF-8",
								success : function(response, textStatus, jqXHR) {
									if (response.error)
										showDialog("#alert-dialog", response.error);
									else if (response.success) {
										reloadSection("section_analysis");
										$modal.modal("hide");
									} else
										unknowError();
								},
								error : unknowError
							});
						}
						return false;
					})
				}
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	} else
		permissionError();
	return false;
}

function unLinkToProject() {
	var analyses = [];

	$("tbody>tr input:checked", "#section_analysis").closest("tr").each(function() {
		var idAnalysis = this.getAttribute("data-trick-id");
		if (this.getAttribute("data-is-linked") === "true" && userCan(idAnalysis, ANALYSIS_RIGHT.ALL))
			analyses.push(idAnalysis);
	});
	if (analyses.length) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Analysis/Ticketing/UnLink",
			type : "POST",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(analyses),
			success : function(response, textStatus, jqXHR) {
				if (response.error)
					showDialog("#alert-dialog", response.error);
				else if (response.success) {
					reloadSection("section_analysis");
					showDialog("#info-dialog", response.success);
				} else
					unknowError();
			},
			error : unknowError
		}).complete(function() {
			$progress.hide();
		});
	}
	return false;
}

function isLinked() {
	return $("tbody>tr input:checked", "#section_analysis").closest("tr").attr("data-is-linked") === "true";
}
