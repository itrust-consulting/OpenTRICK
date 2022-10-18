let DataManagerExport = {
	"default": {
		setup: ($view, $tab) => {
			$("button[name='export']", $view).prop("disabled", !$("[download]", $tab).length);
			DataManagerExport["default"].postSetup($view, $tab);
		},
		postSetup: ($view, $tab) => generateHelper(),
		process: ($view, $tab) => downloadProgress($("#loading-indicator"), $tab.attr("data-view-token"), () => $("[download]", $tab)[0].click())
	},
	"asset": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => DataManagerExport["default"].process($view, $tab)
	},
	"risk-information": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => DataManagerExport["default"].process($view, $tab)
	},
	"risk-estimation": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => DataManagerExport["default"].process($view, $tab)
	},
	"scenario": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => DataManagerExport["default"].process($view, $tab)
	},
	"rrf-raw": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => DataManagerExport["default"].process($view, $tab)
	},
	"ilr": {
		setup: ($view, $tab) => {
			let $currentUI = $("[data-view-content-name='ilr']", $tab);
			$("button[name='export']", $view).prop("disabled", !$("#tab-ilr").length);
			if (!$currentUI.length) {
				let $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-url"), $btnExport = $("button[name='export']", $view).prop("disabled", true), $oldContent = $("[data-view-content-name='measure']", $tab);
				$.ajax({
					url: context + url,
					type: "GET",
					contentType: "application/json;charset=UTF-8",
					success: function (response, textStatus, jqXHR) {
						let $content = $("[data-view-content-name='ilr']", new DOMParser().parseFromString(response, "text/html"));
						if ($content.length) {
							if ($oldContent.length)
								$oldContent.replaceWith($content);
							else $content.appendTo($tab);

							let $ilrDataFile = $("#ilr-json-file", $tab), $ilrDataInfo = $("#ilr-json-file-info", $tab), $ilrDatabrowsebtn = $("#ilr-json-file-browse-button", $tab);
							let $ilrMappingFile = $("#mapping-cvs-file", $tab), $ilrMappingInfo = $("#mapping-cvs-file-info", $tab), $ilrMappingbrowsebtn = $("#mapping-cvs-file-browse-button", $tab);

							setupBrowseButton($ilrDataFile, $ilrDataInfo, $ilrDatabrowsebtn, $btnExport, ".json", () => { });
							setupBrowseButton($ilrMappingFile, $ilrMappingInfo, $ilrMappingbrowsebtn, $btnExport, ".csv", () => { });

							$("form", $content).on("submit", (e) => downloadProgress($progress, $content.attr("data-view-token"), () => $view.modal("hide")));

							DataManagerExport["default"].postSetup($view, $tab);
						}
						else {
							if (response["error"] != undefined)
								showDialog("#alert-dialog", response["error"]);
							else
								unknowError();

							$tab.empty();
						}
					},
					error: (jqXHR, textStatus, errorThrown) => {
						$tab.empty();
						unknowError(jqXHR, textStatus, errorThrown);
					}
				}).complete(() => $progress.hide());
			}
		},
		process: ($view, $tab) =>  $("button[type='submit']",$tab).trigger("click")
	},
	"measure": {
		setup: ($view, $tab) => {
			let $currentUI = $("[data-view-content-name='measure']", $tab);
			if ($currentUI.length)
				$("select[name='standards']", $currentUI).trigger("change");
			else {
				let $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-url"), $btnExport = $("button[name='export']", $view).prop("disabled", true), $oldContent = $("[data-view-content-name='measure']", $tab);
				$.ajax({
					url: context + url,
					type: "GET",
					contentType: "application/json;charset=UTF-8",
					success: function (response, textStatus, jqXHR) {
						let $content = $("[data-view-content-name='measure']", new DOMParser().parseFromString(response, "text/html"));
						if ($content.length) {
							if ($oldContent.length)
								$oldContent.replaceWith($content);
							else $content.appendTo($tab);

							$("select[name='standards']", $content).on("change", (e) => {
								let $current = $(e.currentTarget);
								$btnExport.prop("disabled", !$current.val());
							}).trigger("change");

							$("form", $content).on("submit", (e) => downloadProgress($progress, $content.attr("data-view-token"), () => $view.modal("hide")));

							DataManagerExport["default"].postSetup($view, $tab);
						}
						else {
							if (response["error"] != undefined)
								showDialog("#alert-dialog", response["error"]);
							else
								unknowError();

							$tab.empty();
						}
					},
					error: (jqXHR, textStatus, errorThrown) => {
						$tab.empty();
						unknowError(jqXHR, textStatus, errorThrown);
					}
				}).complete(() => $progress.hide());
			}
		},
		process: ($view, $tab) => $("button[type='submit']",$tab).trigger("click")
	},
	"action-plan-raw": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => DataManagerExport["default"].process($view, $tab)
	},
	"async-export": {
		setup: ($view, $tab) => DataManagerExport["default"].setup($view, $tab),
		process: ($view, $tab) => {
			let $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-process-url"), $btnExport = $("button[name='export']", $view).prop("disabled", true);
			$.ajax({
				url: context + url,
				type: "GET",
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					if (response["success"] != undefined) {
						application["taskManager"].Start();
						$view.modal("hide");
					}
					else if (response["error"] != undefined)
						showDialog("#alert-dialog", response["error"]);
					else
						unknowError();
				},
				error: unknowError
			}).complete(() => {
				$progress.hide();
				$btnExport.prop("disabled", false);
			});
		}
	},
	"risk-register": {
		setup: ($view, $tab) => $("button[name='export']", $view).prop("disabled", !$("#tab-risk-register").length),
		process: ($view, $tab) => DataManagerExport["async-export"].process($view, $tab)
	},
	"soa": {
		setup: ($view, $tab) => $("button[name='export']", $view).prop("disabled", !$("#tab-soa").length),
		process: ($view, $tab) => DataManagerExport["async-export"].process($view, $tab)
	}, "risk-sheet": {
		setup: ($view, $tab) => {
			let $currentUI = $("[data-view-content-name='risk-sheet']", $tab);
			if ($currentUI.length)
				$("button[name='export']", $view).prop("disabled", false)
			else {
				let $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-url"), $selectedTab = $("ul.nav>li.active>a", $tab), $btnExport = $("button[name='export']", $view).prop("disabled", true);
				$.ajax({
					url: context + url,
					contentType: "application/json;charset=UTF-8",
					success: function (response, textStatus, jqXHR) {
						let $content = $("[data-view-content-name='risk-sheet']", new DOMParser().parseFromString(response, "text/html"));
						if ($content.length) {
							let $tabs = $(".tab-pane", $tab);
							if ($tabs.length) {
								$tabs.each((i, e) => {
									let $this = $(e), $newTab = $("#" + e.id, $content), active = $this.hasClass('active');
									$this.replaceWith($newTab);
									if (active)
										$newTab.addClass("active");
									else $newTab.removeClass("active");
								});
								$content = $tab;
							} else $content.appendTo($tab);
							if ($selectedTab.length)
								$("ul.nav>li>a[href='" + $selectedTab.attr("href") + "']", $content).tab("show");
							DataManagerExport["default"].postSetup($view, $tab);
							$btnExport.prop("disabled", false);
						} else {
							$(".tab-pane", $tab).empty();
							unknowError();
						}
					}, error: (jqXHR, textStatus, errorThrown) => {
						$(".tab-pane", $tab).empty();
						unknowError(jqXHR, textStatus, errorThrown);
					}
				}).complete(() => $progress.hide());
			}
		},
		process: ($view, $tab) => {
			let $progress = $("#loading-indicator").show(), data = $("form", $tab).serializeJSON(), $btnExport = $("button[name='export']", $view).prop("disabled", true);
			data.filter = {};

			for (let field in data) {
				if (field.indexOf("filter.") != -1) {
					data['filter'][field.replace("filter.", "")] = data[field];
					delete data[field];
				}
			}

			$.ajax({
				url: context + $tab.attr("data-view-process-url"),
				type: "post",
				data: JSON.stringify(data),
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					$(".label-danger", $tab).remove();
					if (response["success"] != undefined) {
						$view.modal("hide");
						application["taskManager"].Start();
					} else if (response["error"])
						showDialog("#alert-dialog", response["error"]);
					else {
						for (let error in response) {
							let errorElement = document.createElement("label");
							errorElement.setAttribute("class", "label label-danger");
							$(errorElement).text(response[error]);
							switch (error) {
								case "impact":
								case "probability":
								case "direct":
								case "indirect":
								case "cia":
								case "owner":
									$(errorElement).appendTo($("select[name='" + error + "']", $tab).parent());
									break;
							}
						}

					}
				},
				error: unknowError
			}).complete(function () {
				$progress.hide();
				$btnExport.prop("disabled", false);
			});
		}

	}, "risk-sheet-raw": {
		setup: ($view, $tab) => { },
		process: ($view, $tab) => DataManagerExport["risk-sheet"].process($view, $tab)
	}, "risk-sheet-report": {
		setup: ($view, $tab) => { },
		process: ($view, $tab) => DataManagerExport["risk-sheet"].process($view, $tab)
	}, "word-report": {
		setup: ($view, $tab) => {
			let $currentUI = $("[data-view-content-name='word-report']", $tab);
			if ($currentUI.length) {
				let $tabs = $(".tab-pane.active", $currentUI);
				DataManagerExport["report"].updateStatus($view, $tabs.length ? $tabs : $currentUI);
			}
			else {
				let $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-url"), $btnExport = $("button[name='export']", $view).prop("disabled", true), $selectedTab = $("ul.nav>li.active>a", $tab);
				$.ajax({
					url: context + url,
					contentType: "application/json;charset=UTF-8",
					success: function (response, textStatus, jqXHR) {
						let $content = $("[data-view-content-name='word-report']", new DOMParser().parseFromString(response, "text/html"));
						if ($content.length) {
							let $tabs = $(".tab-pane", $tab);
							if ($tabs.length) {
								$tabs.each((i, e) => {
									let $this = $(e), $newTab = $("#" + e.id, $content), active = $this.hasClass('active');
									$this.replaceWith($newTab);
									if (active)
										$newTab.addClass("active");
									else $newTab.removeClass("active");
								});
								$content = $tab;
							} else {
								$content.appendTo($tab.empty());
								$('a[data-toggle="tab"]', $tab).on('show.bs.tab', function (e) {
									DataManagerExport["report"].updateStatus($view, $(".tab-pane" + e.currentTarget.getAttribute("href"), $tab));
								}).first().trigger("show.bs.tab");
							}

							if ($selectedTab.length)
								$("ul.nav>li>a[href='" + $selectedTab.attr("href") + "']", $content).tab("show");

							$tabs = $(".tab-pane", $tab);

							if ($tabs.length)
								$tabs.each((i, e) => DataManagerExport["report"].setup($view, $(e)));
							else DataManagerExport["report"].setup($view, $tab);

							DataManagerExport["default"].postSetup($view, $tab);

						} else {

							if ($selectedTab.length)
								$(".tab-pane", $tab).empty();
							else $tab.empty();

							unknowError();
						}
					}, error: (jqXHR, textStatus, errorThrown) => {

						if ($selectedTab.length)
							$(".tab-pane", $tab).empty();
						else $tab.empty();

						unknowError(jqXHR, textStatus, errorThrown);
					}
				}).complete(() => $progress.hide());
			}
		},
		process: ($view, $tab) => DataManagerExport["report"].process($view, $tab)
	},
	//see exportAnalysisReport and DataManagerExport['word-report']
	"report": {
		setup: ($view, $tab) => {
			let $inputFile = $("input[name='file']", $tab), $template = $("select[name='template']", $tab), $exportBtn = $("button[name='export']", $view);
			let $form = $("form", $tab), $type = $("input[name='type']", $tab), $fileInfo = $("input[name='filename']", $tab);
			if ($type.length > 1) {
				$type.on("change", (e) => {
					if (e.currentTarget.checked) {
						$("option[data-trick-type][data-trick-type='" + e.currentTarget.value + "']", $template).show();
						$("option[data-trick-type][data-trick-type!='" + e.currentTarget.value + "']", $template.val("-1")).hide();
						DataManagerExport["report"].updateStatus($view, $tab);
					}
				}).trigger("change");
			}

			$template.on("change", (e) => {
				if (e.currentTarget.value != "-1" && $inputFile[0].value !== "") {
					$inputFile[0].value = "";
					$inputFile.trigger("change");
				} else DataManagerExport["report"].updateStatus($view, $tab);
			});

			$inputFile.on("change", (e) => {
				let value = $inputFile.val();
				if (value.trim() === '')
					DataManagerExport["report"].updateStatus($view, $tab);
				else {
					let size = parseInt($inputFile.attr("maxlength"))
					$template.prop("required", false).val("-1");
					if ($inputFile[0].files[0].size > size) {
						showDialog("error", MessageResolver("error.file.too.large", undefined, size));
						return false;
					}
					if (!checkExtention(value, ".docx", $exportBtn))
						return false
				}
				$fileInfo.val(value);
			});

			$("button[name='browse']", $tab).on("click", (e) => $inputFile.click());
			$form.on("submit", (e) => DataManagerExport["report"].postProcess($view, $tab));
			DataManagerExport["report"].updateStatus($view, $tab);
			DataManagerExport["default"].postSetup($view, $tab);
		},
		process: ($view, $tab) => $("button[name='submit']", $tab).click(),
		updateStatus: ($view, $tab) => {
			let fileValue = $("input[name='filename']", $tab).val(), templateValue = $("select[name='template']", $tab).val();
			$("button[name='export']", $view).prop("disabled", (templateValue === "-1" || !templateValue) && (fileValue === "" || !fileValue));
		},
		postProcess: ($view, $tab) => {
			let $progress = $("#loading-indicator").show(), $form = $("form", $tab);
			$.ajax({
				url: context + "/Analysis/Data-manager/Report/Export-process",
				type: 'POST',
				data: new FormData($form[0]),
				cache: false,
				contentType: false,
				processData: false,
				success: function (response, textStatus, jqXHR) {
					if (response.success) {
						application["taskManager"].Start();
						$view.modal("hide");
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
		}
	}
};

function setupBrowseButton($inputFile, $fileInfo, $browseBtn, $exportBtn, extension, callback) {
	$inputFile.on("change", (e) => {
		let value = $inputFile.val();
		if (value.trim() === '')
			callback();
		else {
			let size = parseInt($inputFile.attr("maxlength"))
			if ($inputFile[0].files[0].size > size) {
				showDialog("error", MessageResolver("error.file.too.large", undefined, size));
				return false;
			}
			if (!checkExtention(value, extension, $exportBtn))
				return false
		}
		$fileInfo.val(value);
	});
	$browseBtn.on("click", (e) => $inputFile.click());
}

function downloadProgress($progress, token, caller) {
	if (token) {
		let cookiePattern = new RegExp(token + "=1", "i"), taskId = "";
		taskId = setInterval(() => {
			if (document.cookie.search(cookiePattern) >= 0) {
				$progress.hide();
				clearInterval(taskId);
			}
		}, 300);
		$progress.show();
	}
	if (caller)
		caller();
}


function exportDataManager(idAnalysis) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Data-manager/Export" + ($.isNumeric(idAnalysis) ? "?analysisId=" + idAnalysis : ""),
		type: "GET",
		contentType: "application/json;charset=UTF-8",
		success: function (response) {
			let $view = $("#export-modal", new DOMParser().parseFromString(response, "text/html"));
			if ($view.length) {
				let $exportBtn = $("button[name='export']", $view);
				$view.appendTo("#dialog-body").modal("show").on("hidden.bs.modal", () => $view.remove());
				$('a[data-toggle="tab"]', $view).on('show.bs.tab', function (e) {
					let $tab = $(".tab-pane" + e.currentTarget.getAttribute("href"), $view), manager = DataManagerExport[$tab.attr("data-view-name")];
					if (manager !== undefined)
						manager.setup($view, $tab);
					else $exportBtn.prop("disabled", true);

				}).first().trigger("show.bs.tab");

				$exportBtn.on("click", function (e) {
					let $tab = $("div.tab-pane.active:visible:last", $view), manager = DataManagerExport[$tab.attr("data-view-name")];
					if (manager !== undefined)
						manager.process($view, $tab);
				});
				$("[download]", $view).on("click", (e) => $view.modal('hide'));
			} else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

function exportAnalysis(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Data-manager/Sqlite/Export-process?idAnalysis=" + analysisId,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					application["taskManager"].Start();
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(() => $progress.hide());
	}
	return false;
}

function exportAnalysisReport(analysisId) {
	if (analysisId == null || analysisId == undefined) {
		let selectedScenario = findSelectItemIdBySection("section_analysis");
		if (selectedScenario.length != 1)
			return false;
		analysisId = selectedScenario[0];
	}
	if (userCan(analysisId, ANALYSIS_RIGHT.EXPORT)) {
		let $progress = $("#loading-indicator").show();
		$.ajax({
			url: context + "/Analysis/Data-manager/Report/Export-form/" + analysisId,
			contentType: "application/json;charset=UTF-8",
			success: function (response, textStatus, jqXHR) {
				let $view = $("div#analysis-export-dialog", new DOMParser().parseFromString(response, "text/html"));
				if ($view.length) {
					$view.appendTo("#dialog-body").modal("show").on("hidden.bs.modal", () => $view.remove());
					$("button[name='export']", $view).on("click", (e) => DataManagerExport["report"].process($view, $view));
					DataManagerExport["report"].setup($view, $view);
				}
				else if (response["error"] != undefined)
					showDialog("#alert-dialog", response["error"]);
				else
					unknowError();
			},
			error: unknowError
		}).complete(() => $progress.hide())
	}
	return false;
}