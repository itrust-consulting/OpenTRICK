var previous;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	application["settings-fixed-header"] = {
		fixedOffset : $(".nav-tab"),
		marginTop : application.fixedOffset
	};
	fixTableHeader("#tab-container table");
});

$(function() {
	$(window).scroll(function(e) {
		if (($(window).scrollTop() + $(window).height()) === $(document).height()) {
			var $selectedTab = $(".tab-pane.active"), attr = $selectedTab.attr("data-scroll-trigger");
			if ($selectedTab.attr("data-update-required") === "false" && typeof attr !== typeof undefined && attr !== false)
				window[$selectedTab.attr("data-scroll-trigger")].apply();
		}
	});
});

function installTrickService() {
	$.ajax({
		url : context + "/Install",
		type : "POST",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["error"] != undefined) {
				$("#alert-dialog .modal-body").html(response["error"]);
				$("#alert-dialog").modal("toggle");
			} else if (response["idTask"] != undefined)
				application['taskManager'].Start();
		},
		error : unknowError
	});
	return false;
}

function switchCustomer(section) {
	var selectedAnalysis = findSelectItemIdBySection(section);
	if (!isProfile("#" + section) || selectedAnalysis.length != 1)
		return false;
	var idAnalysis = selectedAnalysis[0];
	$.ajax({
		url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Customer",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $content = $("#switchCustomerModal", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				if ($("#switchCustomerModal").length)
					$("#switchCustomerModal").replaceWith($content);
				else
					$content.appendTo("#widget");
				$content.find(".modal-footer>button[name='save']").on("click", function() {
					$content.find(".label").remove();
					$.ajax({
						url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Customer/" + $content.find("select").val(),
						type : "post",
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							if (response["success"] != undefined) {
								adminCustomerChange($("#tab_analyses").find("select"));
								$content.modal("hide");
							} else if (response["error"] != undefined)
								$("<label class='label label-danger' />").text(response["error"]).appendTo($content.find("select").parent());
							else
								unknowError();
						},
						error : unknowError
					});
				});
				$content.modal("show");
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function switchOwner(section) {
	var selectedAnalysis = findSelectItemIdBySection(section);
	if (!isProfile("#" + section) || selectedAnalysis.length != 1)
		return false;
	var idAnalysis = selectedAnalysis[0];
	$.ajax({
		url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Owner",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["error"] != undefined)
				showDialog("#alert-dialog", response["error"]);
			else {
				var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#switchOwnerModal");
				if ($content.length) {
					if ($("#switchOwnerModal").length)
						$("#switchOwnerModal").replaceWith($content);
					else
						$content.appendTo($("#widget"));
					$content.find(".modal-footer>button[name='save']").on("click", function() {
						$content.find(".label").remove();
						$.ajax({
							url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Owner/" + $content.find("select").val(),
							type : "post",
							contentType : "application/json;charset=UTF-8",
							success : function(response, textStatus, jqXHR) {
								if (response["success"] != undefined) {
									$("#tab_analyses").find("select").change();
									$content.modal("hide");
								} else if (response["error"] != undefined)
									$("<label class='label label-danger'>" + response["error"] + "</label>").appendTo($content.find("select").parent());
								else
									unknowError();
							},
							error : unknowError
						});
					});
					$content.modal("show");
				} else
					unknowError();
			}
		},
		error : unknowError
	});
	return false;
}

function manageAnalysisAccess(analysisId, section_analysis) {
	if (!isProfile("#" + section_analysis))
		return false;
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/Analysis/" + analysisId + "/ManageAccess",
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
			url : context + "/Admin/Analysis/ManageAccess/Update",
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
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/Manage/IDS/"+selectedAnalysis[0],
		type : "GET",
		success : function(response, textStatus, jqXHR) {
			var $content = $("#manageAnalysisIDSAccessModel", new DOMParser().parseFromString(response, "text/html"));
			if ($content.length) {
				$content.appendTo("#widget").on("hidden.bs.modal", () => $content.remove()).modal("show").find(".modal-footer button[name='save']").one("click", function(){
					var  data = { };
					$content.find(".form-group[data-trick-id][data-default-value]").each(function() {
						var $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
						if (newRight != oldRight)
							data[this.getAttribute("data-trick-id")] = newRight; 
					});
					if (Object.keys(data).length) {
						$.ajax({
							url : context + "/Admin/Manage/IDS/"+selectedAnalysis[0]+"/Update",
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

function isProfile(section) {
	return findTrickisProfile($(section + " tbody :checked")) != "true";
}

function adminCustomerChange(selector) {
	var customer = $(selector).find("option:selected").val();
	$.ajax({
		url : context + "/Admin/Analysis/DisplayByCustomer/" + customer,
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $newSection = $("#section_admin_analysis", new DOMParser().parseFromString(response, "text/html"));
			if ($newSection.length) {
				$("#section_admin_analysis").replaceWith($newSection);
				fixTableHeader($("table", $newSection));
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function deleteAdminAnalysis(analysisId, section_analysis) {
	var selectedAnalysis = [];
	if (analysisId == null || analysisId == undefined) {
		selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (!selectedAnalysis.length)
			return false;
	} else if (!Array.isArray(analysisId))
		selectedAnalysis[selectedAnalysis.length] = analysisId;
	else
		selectedAnalysis = analysisId;

	var modal = new Modal($("#deleteAnalysisModel").clone()).setBody(MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));
	$(modal.modal).find("#deleteanalysisbuttonNo").click(function() {
		modal.Destroy();
	});
	$(modal.modal).find("#deleteanalysisbuttonYes").click(function() {
		$(modal.modal).find("#deleteprogressbar").show();
		$(modal.modal).find(".btn").prop("disabled", true);
		$.ajax({
			url : context + "/Admin/Analysis/Delete",
			type : "post",
			contentType : "application/json;charset=UTF-8",
			data : JSON.stringify(selectedAnalysis),
			success : function(response, textStatus, jqXHR) {
				if (response === true)
					$("#section_admin_analysis select").change();
				else if (response === false) {
					var error = new Modal($("#alert-dialog").clone())
					if (selectedAnalysis.length == 1)
						error.setBody(MessageResolver("failed.delete.analysis", "Analysis cannot be deleted!"));
					else
						error.setBody(MessageResolver("failed.delete.analyses", "Analyses cannot be deleted!"));
					error.Show();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
		modal.Destroy();
		return false;
	});
	$(modal.modal).find("#deleteanalysisbuttonYes").prop("disabled", false);
	modal.Show();
	return false;
}

function updateLogFilter(element) {
	if (element != undefined && !$(element).is(":checked"))
		return false;
	var data = $("#logFilterForm").serializeJSON();
	if (data["level"] === "ALL")
		delete data["level"];
	if (data["type"] === "ALL")
		delete data["type"];
	if (data["author"] === "ALL")
		delete data["author"];
	if (data["action"] === "ALL")
		delete data["action"];
	$.ajax({
		url : context + "/Admin/Log/Filter/Update",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				return loadSystemLog();
			else if (response["error"] != undefined)
				new Modal($("#alert-dialog").clone(), response["error"]).Show();
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function loadSystemLog() {
	$("#progress-trickLog").show();
	$.ajax({
		url : context + "/Admin/Log/Section",
		async : false,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $section = $("#section_log",new DOMParser().parseFromString(response, "text/html"));
			if ($section.length) {
				$("#section_log").replaceWith($section);
				fixTableHeader($("table.table-fixed-header-analysis", $section));
			} else
				unknowError();
		},
		error : unknowError,
		complete : function() {
			$("#progress-trickLog").hide();
		}
	});
	return true;
}

function loadSystemLogScrolling() {
	var currentSize = $("#section_log table>tbody>tr").length, size = parseInt($("#logFilterPageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		$("#progress-trickLog").show();
		$.ajax({
			url : context + "/Admin/Log/Section",
			async : false,
			data : {
				"page" : (currentSize / size) + 1
			},
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				$(new DOMParser().parseFromString(response, "text/html")).find("#section_log>table>tbody>tr").each(function() {
					var $current = $("#section_log>table>tbody>tr[data-trick-id='" + $(this).attr("data-trick-id") + "']");
					if (!$current.length)
						$(this).appendTo($("#section_log>table>tbody"));
				});
				return false;
			},
			error : unknowError,
			complete : function() {
				$("#progress-trickLog").hide();
			}
		});
	}
	return true;
}

function updateSetting(idForm, sender) {
	var $sender = $(sender);
	if ($sender.attr("type") != "radio" || $sender.is(":checked")) {
		$sender.parent().removeClass("has-success");
		$.ajax({
			url : context + "/Admin/TSSetting/Update",
			async : false,
			type : "post",
			data : JSON.stringify($(idForm).serializeJSON()),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response !== true)
					unknowError();
				else if ($sender.attr("type") != "radio")
					$sender.parent().addClass("has-success");
				return false;
			},
			error : unknowError
		});
	}
	return false;
}