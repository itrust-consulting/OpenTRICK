/**
 * This file contains the DataManagerImport object and the importDataManager function.
 * The DataManagerImport object defines different setup and process functions for importing data in different scenarios.
 * The importDataManager function is responsible for making an AJAX request to import data using the DataManagerImport object.
 */
/**
 * DataManagerImport is an object that contains setup and process functions for different data import scenarios.
 * Each scenario has a setup function that initializes the view and a process function that handles the import process.
 * The object has the following structure:
 * {
 *    "default": {
 *        setup: Function,
 *        process: Function
 *    },
 *    "asset": {
 *        setup: Function,
 *        process: Function
 *    },
 *    "risk-information": {
 *        setup: Function,
 *        process: Function
 *    },
 *    ...
 * }
 * @typedef {Object} DataManagerImport
 * @property {Object} default - The default scenario setup and process functions.
 * @property {Function} default.setup - The setup function for the default scenario.
 * @property {Function} default.process - The process function for the default scenario.
 * @property {Object} asset - The asset scenario setup and process functions.
 * @property {Function} asset.setup - The setup function for the asset scenario.
 * @property {Function} asset.process - The process function for the asset scenario.
 * @property {Object} risk-information - The risk-information scenario setup and process functions.
 * @property {Function} risk-information.setup - The setup function for the risk-information scenario.
 * @property {Function} risk-information.process - The process function for the risk-information scenario.
 * ...
 */
var DataManagerImport = {
	"default": {
		setup: ($view, $tab) => {
			$("form", $tab).find("input[type='file']").trigger("change");
		},
		process: ($view, $from, $tab) => {
			var $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-process-url"), $btnImport = $("button[name='import']", $view).prop("disabled", true);
			$.ajax({
				url: context + url,
				type: 'POST',
				data: new FormData($from[0]),
				cache: false,
				contentType: false,
				processData: false,
				success: function (response, textStatus, jqXHR) {
					if (response.success) {
						showDialog("success", response.success);
						application["taskManager"].Start();
						$view.modal("hide");
					}
					else if (response.error)
						showDialog("#alert-dialog", response.error);
					else
						showDialog("#alert-dialog", MessageResolver("error.unknown.file.uploading", "An unknown error occurred during file uploading"));
				},
				error: unknowError

			}).complete(function () {
				$progress.hide();
				$btnImport.prop("disabled", false);
			});
		}
	},
	"asset": {
		setup: ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process: ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"risk-information": {
		setup: ($view, $tab) => {
			if($tab.attr("data-option-init")!=="true"){
				var $btns = $("input[type='radio'][name='overwrite']",$tab),$alerts = $("div[data-alert].alert",$tab);
				$btns.on("change", (e)=> {
					if(e.currentTarget.checked){
						if(e.currentTarget.value==="true"){
							$alerts.filter("div[data-alert='warning']").hide();
							$alerts.filter("div[data-alert='danger']").show();
						}else {
							$alerts.filter("div[data-alert='danger']").hide();
							$alerts.filter("div[data-alert='warning']").show();
						}
					}
				}).trigger("change");
				$tab.attr("data-option-init",true);
			}
			DataManagerImport["default"].setup($view, $tab);
		},
		process: ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"risk-estimation": {
		setup: ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process: ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"scenario": {
		setup: ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process: ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"raw-rrf": {
		setup: ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process: ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"rrf-knowledge-base": {
		setup: ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process: ($view, $from, $tab) => {
			var $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-process-url"), $btnImport = $("button[name='import']", $view).prop("disabled", true);
			$.ajax({
				url: context + url,
				type: "post",
				data: $from.serialize(),
				success: function (response, textStatus, jqXHR) {
					if (response.success != undefined) {
						showDialog("success", response.success);
						$view.modal("hide");
					}
					else {
						if (response.error != undefined)
							showDialog("error", response.error);
						else
							unknowError();
					}
				},
				error: unknowError
			}).complete(() => {
				$progress.hide();
				$btnImport.prop("disabled", false);
			});
		}
	},
	"measure": {
		setup: ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process: ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"rrf": {
		setup: ($view, $tab) => {
			var $currentUI = $("[data-view-content-name='rrf']", $tab);
			if($currentUI.length)
				$('ul.nav>li.active>a[data-toggle="tab"]', $currentUI).trigger("show.bs.tab");
			else {
				var $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-url"), $btnImport = $("button[name='import']", $view);
				$.ajax({
					url: context + url,
					contentType: "application/json;charset=UTF-8",
					success: function (response, textStatus, jqXHR) {
						var $content = $("[data-view-content-name='rrf']", new DOMParser().parseFromString(response, "text/html"));
						if ($content.length) {
							var $tabs = $(".tab-pane", $tab);
							if ($tabs.length) {
								$tabs.each((i, e) => {
									var $this = $(e), $newTab = $("#" + e.id, $content), active = $this.hasClass('active');
									$this.replaceWith($newTab);
									if (active)
										$newTab.addClass("active");
									else $newTab.removeClass("active");
								});
								$content = $tab;
							} else 
								$('a[data-toggle="tab"]', $content.appendTo($tab)).on('show.bs.tab', (e) => 
								$("input[name='file'],select[name='standards']",".tab-pane" + e.currentTarget.getAttribute("href"), $tab).trigger("change"))
								.first().trigger("show.bs.tab");
	
							var $customers = $("select[name='customer']", $content),
								$analyses = $("select[name='analysis']", $content),
								$standards = $("select[name='standards']", $content), $selectedTab = $("ul.nav>li.active>a", $tab);
							$customers.change(function () {
								var value = $(this).val();
								$analyses.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
								$($analyses.find("option[data-trick-id='" + value + "']").show()[0]).prop("selected", true);
								$analyses.trigger("change");
							});
							$analyses.on("change", function (e) {
								var value = $(e.target).val();
								if (value == undefined)
									value = 0;
								$standards.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
								$standards.find("option[data-trick-id='" + value + "']").show();
								$standards.trigger("change");
							});
	
							$standards.on("change", (e) => {
								$btnImport.prop("disabled", $standards.val() === null);
							});
							$customers.trigger("change");
	
							if ($selectedTab.length)
								$("ul.nav>li>a[href='" + $selectedTab.attr("href") + "']", $content).tab("show");
							
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
		process: ($view, $from, $mainTab) => { }
	},
};

/**
 * Imports data manager for a given analysis.
 * @param {number} idAnalysis - The ID of the analysis.
 * @returns {boolean} - Returns false.
 */
function importDataManager(idAnalysis) {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/Data-manager/Import" + ($.isNumeric(idAnalysis) ? "?analysisId=" + idAnalysis : ""),
		type: "GET",
		contentType: "application/json;charset=UTF-8",
		success: function (response) {
			var $view = $("#import-modal", new DOMParser().parseFromString(response, "text/html"));
			if ($view.length) {
				var $importBtn = $("button[name='import']", $view);

				$view.appendTo("#dialog-body").modal("show").on("hidden.bs.modal", () => $view.remove());

				$('a[data-toggle="tab"]', $view).on('show.bs.tab', function (e) {
					var $tab = $(".tab-pane" + e.currentTarget.getAttribute("href"), $view), manager = DataManagerImport[$tab.attr("data-view-name")];
					if (manager !== undefined)
						manager.setup($view, $tab);

				}).first().trigger("show.bs.tab");

				$importBtn.on("click", function (e) {
					var $tab = $("div.tab-pane.active:visible:last", $view), $form = $("form:visible", $tab), manager = DataManagerImport[$tab.attr("data-view-name")];
					if (manager !== undefined)
						manager.process($view, $form, $tab);
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