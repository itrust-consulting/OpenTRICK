/**
 * 
 */

var DataManagerImport = {
	"default" : {
		setup : ($view, $tab) =>{
			$("form", $view).trigger("reset").find("input[type='file']").trigger("change");
		},
		process : ($view, $from, $tab) => {
			var $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-process-url"), $btnImport = $("button[name='import']", $view).prop("disabled", true);
			$.ajax({
				url: context + url,
				type: 'POST',
				data: new FormData($from[0]),
				cache: false,
				contentType: false,
				processData: false,
				success: function (response, textStatus, jqXHR) {
					if (response.success){
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
	"asset" : {
		setup : ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process : ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"risk-information" : {
		setup : ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process : ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"risk-estimation" : {
		setup : ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process : ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"scenario" : {
		setup : ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process : ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"raw-rrf" : {
		setup : ($view, $tab) => DataManagerImport["default"].setup($view, $tab),
		process : ($view, $from, $tab) => DataManagerImport["default"].process($view, $from, $tab)
	},
	"measure" : {
		setup : ($view, $tab) => {
			DataManagerImport["default"].setup($view, $tab);
		},
		process : ($view, $from, $tab) => {
			
		}
	},
	"rrf" : {
		setup : ($view, $tab) => {
			DataManagerImport["default"].setup($view, $tab);
			var $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-url"), $btnImport = $("button[name='import']", $view);
			$.ajax({
				url: context + url,
				contentType: "application/json;charset=UTF-8",
				success: function (response, textStatus, jqXHR) {
					var $content = $("[data-view-content-name='rrf']", new DOMParser().parseFromString(response, "text/html"));
					if ($content.length) {
						var $customers = $("select[name='customer']", $content), 
						$analyses = $("select[name='analysis']", $content), 
						$standards = $("select[name='standards']", $content);
						$customers.change(function () {
							var value = $(this).val();
							$analyses.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
							$($analyses.find("option[data-trick-id='" + value + "']").show()[0]).prop("selected", true);
							$analyses.trigger("change");
						});
						$analyses.on("change", function (e) {
							var value = $(e.target).val();
							if (value == undefined)
								value = -1;
							$standards.find("option[data-trick-id!='" + value + "']").hide().prop("selected", false);
							$standards.find("option[data-trick-id='" + value + "']").show();
							$standards.trigger("change");
						});
						
						$standards.on("change", (e) => {
							$btnImport.prop("disabled", $standards.val()===null);
						});
						
						$content.appendTo($tab.empty());
						$customers.trigger("change");
					}else {
						$tab.empty();
						unknowError();
					}
				},error: (jqXHR, textStatus, errorThrown) => {
					$tab.empty();
					unknowError(jqXHR, textStatus, errorThrown);
				}
				}).complete(() => $progress.hide());
		},
		process : ($view, $from, $tab) => {
			var $progress = $("#loading-indicator").show(), url = $tab.attr("data-view-process-url"), $btnImport = $("button[name='import']", $view).prop("disabled", true);
			$.ajax({
				url: context + url,
				type: "post",
				data: $from.serialize(),
				success: function (response, textStatus, jqXHR) {
					if (response.success != undefined){
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
				error:unknowError
			}).complete(() => {
				$progress.hide();
				$btnImport.prop("disabled",false);
			});
		}
	},
};

function importDataManager(idAnalysis) {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/Data-manager/Import" + ($.isNumeric(idAnalysis) ? "?analysisId=" + idAnalysis : ""),
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response) {
			var $view = $("#import-modal", new DOMParser().parseFromString(response, "text/html"));
			if($view.length){
				var $exportBtn = $("button[name='import']", $view);
				$view.appendTo("#dialog-body").modal("show").on("hidden.bs.modal", () => $view.remove());
				$('a[data-toggle="tab"]', $view).on('shown.bs.tab', function(e) {
					var $tab = $("div.tab-pane.active", $view), manager = DataManagerImport[$tab.attr("data-view-name")];
					if(manager!==undefined)
						manager.setup($view, $tab);
				});
				$exportBtn.on("click", function(e) {
					var $tab = $("div.tab-pane.active", $view), $form = $("form", $tab),  manager = DataManagerImport[$tab.attr("data-view-name")];
					if(manager!==undefined)
						manager.process($view,$form, $tab);
				});
			}else
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}