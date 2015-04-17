var previous;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	$("#tab-container table").stickyTableHeaders({
		cssTopOffset : ".nav-tab",
		fixedOffset : 6
	});
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
		type : "GET",
		async : true,
		contentType : "application/json",
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
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#switchCustomerModal");
			if ($content.length) {
				$content.appendTo($("#widget"))
				$content.find(".modal-footer>button").on("click", function() {
					$content.find(".label-error").remove();
					$.ajax({
						url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Customer/" + $content.find("select").val(),
						type : "get",
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							if (response["success"] != undefined) {
								adminCustomerChange($("#tab_analyses").find("select"));
								$content.modal("hide");
							} else if (response["error"] != undefined)
								$("<label class='label label-error'>" + response["error"] + "</label>").appendTo($content.find(".modal-body"));
							else
								unknowError();
						},
						error : unknowError
					});
				});
				new Modal($content).Show();
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function manageAnalysisAccess(analysisId, section_analysis) {
	if (!isProfile(section_analysis))
		return false;
	if (analysisId == null || analysisId == undefined) {
		var selectedAnalysis = findSelectItemIdBySection(section_analysis);
		if (selectedAnalysis.length != 1)
			return false;
		analysisId = selectedAnalysis[0];
	}

	$.ajax({
		url : context + "/Admin/Analysis/" + analysisId + "/ManageAccess",
		type : "get",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $newSection = $(new DOMParser().parseFromString(response, "text/html")).find("#manageAnalysisAccessModel");
			if ($newSection.length) {
				$("#manageAnalysisAccessModel").replaceWith($newSection);
				$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
				$("#manageAnalysisAccessModel").modal('toggle');
				$("#userselect").one('focus', function() {
					previous = this.value;
				}).change(function() {
					$("#user_" + previous).attr("hidden", true);
					$("#user_" + this.value).removeAttr("hidden");
					previous = this.value;
				});
			} else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function updatemanageAnalysisAccess(analysisId, userrightsform) {
	$.ajax({
		url : context + "/Admin/Analysis/" + analysisId + "/ManageAccess/Update",
		type : "post",
		data : serializeForm(userrightsform),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var $newSection = $(doc).find(".modal-content");
			if ($newSection.length) {
				$("#manageAnalysisAccessModel .modal-content").replaceWith($newSection);
				$("#manageAnalysisAccessModelButton").attr("onclick", "updatemanageAnalysisAccess(" + analysisId + ",'userrightsform')");
				$("#userselect").one('focus', function() {
					previous = this.value;
				}).change(function() {
					$("#user_" + previous).attr("hidden", true);
					$("#user_" + this.value).removeAttr("hidden");
					previous = this.value;
				});
			} else
				unknowError();
		},
		error : unknowError
	});
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
			var parser = new DOMParser();
			var doc = parser.parseFromString(response, "text/html");
			var $newSection = $(doc).find("#section_admin_analysis");
			if ($newSection.length) {
				$("#section_admin_analysis").replaceWith($newSection);
				$("#section_admin_analysis table").stickyTableHeaders({
					cssTopOffset : ".nav-tab",
					fixedOffset : 6
				});
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

function updateLogFilter(element){
	if (element != undefined && !$(element).is(":checked"))
		return false;
	var data = $("#logFilterForm").serializeJSON();
	if(data["level"]==="ALL")
		delete data["level"];
	if(data["type"] ==="ALL")
		delete data["type"];
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
			var section = $(new DOMParser().parseFromString(response, "text/html")).find("#section_log");
			if(section.length){
				$("#section_log").replaceWith(section);
				$("#section_log table.table-fixed-header-analysis").stickyTableHeaders({
					cssTopOffset : ".nav-tab",
					fixedOffset : 6
				});
			}
			else unknowError();
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

