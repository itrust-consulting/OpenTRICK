var previous;

$(document).ready(function() {
	$("input[type='checkbox']").removeAttr("checked");
	application["settings-fixed-header"] = {
		fixedOffset : $(".nav-tab"),
		marginTop : application.fixedOffset
	};
	setTimeout(() => fixTableHeader("#tab-container table"), 300);
	
	$(window).scroll(function(e) {
		if (($(window).scrollTop() + $(window).height()) >= $(document).height()*.98) {
			var $selectedTab = $(".tab-pane.active"), attr = $selectedTab.attr("data-scroll-trigger");
			if ($selectedTab.attr("data-update-required") === "false" && typeof attr !== typeof undefined && attr !== false)
				window[$selectedTab.attr("data-scroll-trigger")].apply();
		}
	});
	
	$("#btn-add-notification").on("click", notificationForm);
	$("#btn-clear-notification").on("click", clearNotification);
});

function installTrickService() {
	$.ajax({
		url : context + "/Install",
		type : "POST",
		async : true,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["error"] != undefined)
				showDialog("#alert-dialog",response["error"]);
			else if (response["idTask"] != undefined)
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
	var $progress = $("#loading-indicator").show(), idAnalysis = selectedAnalysis[0];
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
					$progress.show();
					$.ajax({
						url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Customer/" + $content.find("select").val(),
						type : "post",
						contentType : "application/json;charset=UTF-8",
						success : function(response, textStatus, jqXHR) {
							if (response["success"] != undefined) {
								adminCustomerChange($("#tab-analyses").find("select"));
								$content.modal("hide");
							} else if (response["error"] != undefined)
								$("<label class='label label-danger' />").text(response["error"]).appendTo($content.find("select").parent());
							else
								unknowError();
						},
						error : unknowError
					}).complete(function() {
						$progress.hide();
					});
				});
				$content.modal("show");
			} else
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function switchOwner(section) {
	var selectedAnalysis = findSelectItemIdBySection(section);
	if (!isProfile("#" + section) || selectedAnalysis.length != 1)
		return false;
	var $progress = $("#loading-indicator").show(), idAnalysis = selectedAnalysis[0];
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
						$progress.show();
						$.ajax({
							url : context + "/Admin/Analysis/" + idAnalysis + "/Switch/Owner/" + $content.find("select").val(),
							type : "post",
							contentType : "application/json;charset=UTF-8",
							success : function(response, textStatus, jqXHR) {
								if (response["success"] != undefined) {
									$("#tab-analyses").find("select").trigger("change");
									$content.modal("hide");
								} else if (response["error"] != undefined)
									$("<label class='label label-danger'>" + response["error"] + "</label>").appendTo($content.find("select").parent());
								else
									unknowError();
							},
							error : unknowError
						}).complete(function() {
							$progress.hide();
						});
					});
					$content.modal("show");
				} else
					unknowError();
			}
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
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
						showDialog("success", response.success);
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
	if(!isAnalysisType("QUANTITATIVE", "#"+section))
		return false;
	var selectedAnalysis = findSelectItemIdBySection(section);
	if (selectedAnalysis.length != 1)
		return false;
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/Manage/IDS/" + selectedAnalysis[0],
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
							url : context + "/Admin/Manage/IDS/" + selectedAnalysis[0] + "/Update",
							type : "post",
							data : JSON.stringify(data),
							contentType : "application/json;charset=UTF-8",
							success : function(response, textStatus, jqXHR) {
								if (response.error != undefined)
									showDialog("#alert-dialog", response.error);
								else if (response.success != undefined)
									showDialog("success", response.success);
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
	if (element && element.length == 1)
		if ($(element).attr("data-trick-is-profile") != undefined)
			return $(element).attr("data-trick-is-profile");
		else
			return findTrickisProfile($(element).closest("[data-trick-is-profile]"));
	else
		return null;
}

function isProfile(section) {
	return findTrickisProfile($(section + " tbody :checked")) !== "true";
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
	
	var $modal = showDialog("#deleteAnalysisModel", MessageResolver("label.analysis.question.delete", "Are you sure that you want to delete the analysis?"));
	$("button[name='delete']", $modal).unbind().one("click", function () {
		var $progress = $("#loading-indicator").show();
		$.ajax(
				{
					url : context + "/Admin/Analysis/Delete",
					type : "post",
					contentType : "application/json;charset=UTF-8",
					data : JSON.stringify(selectedAnalysis),
					success : function(response, textStatus, jqXHR) {
						if (response === true)
							$("#section_admin_analysis select").change();
						else if (response === false)
							showDialog("#alert-dialog", selectedAnalysis.length == 1 ? MessageResolver("failed.delete.analysis", "Analysis cannot be deleted!")
									: MessageResolver("failed.delete.analyses", "Analyses cannot be deleted!"));
						else
							unknowError();
						return false;
					},
					error : unknowError
				}).complete(function() {
			$progress.hide();
		});
		$modal.modal("hide");
		return false;
	});
	
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
				showDialog("#alert-dialog",response["error"]);
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function loadSystemLog() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/Log/Section",
		async : false,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $section = $("#section_log", new DOMParser().parseFromString(response, "text/html"));
			if ($section.length) {
				$("#section_log").replaceWith($section);
				fixTableHeader($("table.table-fixed-header-analysis", $section));
			} else
				unknowError();
		},
		error : unknowError
		
	}).complete( () => {
		$progress.hide();
	});
	return true;
}

function loadSystemLogScrolling() {
	var currentSize = $("#section_log table>tbody>tr").length, size = parseInt($("#logFilterPageSize").val());
	if (currentSize >= size && currentSize % size === 0) {
		var $progress = $("#loading-indicator").show();
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
			error : unknowError
		}).complete(() => {
			$progress.hide();
		});
	}
	return true;
}

function updateSetting(idForm, sender) {
	var $sender = $(sender), olvalue = $sender.attr("placeholder"),value = $sender.val();
	if ($sender.attr("type") != "radio" && value!== olvalue  || $sender.is(":checked")) {
		$.ajax({
			url : context + "/Admin/TSSetting/Update",
			async : false,
			type : "post",
			data : JSON.stringify($(idForm).serializeJSON()),
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response !== true)
					unknowError();
				else if ($sender.attr("type") != "radio"){
					$sender.parent().addClass("has-success");
					if($sender.hasAttr("placeholder"))
						$sender.attr("placeholder", value);
					setTimeout(() => {
						$sender.parent().removeClass("has-success")
					}, 5000);
				}
				return false;
			},
			error : unknowError
		});
	}
	return false;
}

function loadNotification() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Admin/Notification/ALL",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			 if (Array.isArray(response) && response.length) {
				 var $container = $("#notification-content");
				 for (let notification of response)
					insertOrUpdateNotification(notification,$container);
			 }
			return false;
		},
		error : unknowError
	}).complete(() => {
		$progress.hide();
	});

	return false;
}

function insertOrUpdateNotification(notification, $container){
	if(!$container)
		$container = $("#notification-content");
	var $current = $("[data-trick-id='"+notification.id+"'][data-role='notification']",$container);
	if(!$current.length){
		$current = $("<div class='col-lg-4 col-md-6' data-role='notification' ><div class='panel'><div class='panel-heading'><h3 class='panel-title'><span style='display:block; margin-bottom:8px;'><span data-role='type' class='col-xs-6' style='padding-left:0'/><span data-role='control' class='col-xs-6 text-right' style='padding-right: 0;'><span class='btn-group'><button class='btn btn-xs btn-warning' name='edit'><i class='fa fa-edit'></i></button><button class='btn btn-xs btn-danger' name='delete'><i class='fa fa-remove'></i></button></span></span><span class='clearfix'/></span><span style='display:block;'><span class='col-xs-6' data-role='startDate' style='padding-left:0'/><span class='col-xs-6 text-right' data-role='endDate'/> <span class='clearfix'/></span></h3></div><div class='panel-body'><fieldset><legend>Français</legend><div lang='fr' data-trick-content='text'></div></fieldset><fieldset><legend>English</legend><div lang='en' data-trick-content='text'></div></fieldset></div><div class='panel-footer'><span data-role='created' /></div></div></div>");
		$current.attr("data-trick-id", notification.id);
		$("button[name='delete']", $current).on("click", (e) => {deleteNotification(notification.id);});
		$("button[name='edit']", $current).on("click", (e) => {notificationForm(e,notification.id);});
	}
	
	var locale = application.language=='en'? 'en-GB' : 'fr-FR', options = {weekday: "long", year: "numeric", month: "long", day: "numeric", hour:"numeric", minute : "numeric"};
	var type = notification.type.toLowerCase(),  $panel = $("div.panel", $current), $body = $("div.panel-body", $panel);
	
	$panel.attr("class", "panel panel-"+(type ==='error'? 'danger':type) );
	
	$("[data-role='type']", $panel).text(MessageResolver("label.log.level."+type,type.capitalize()));
	
	$("[data-role='created']",$panel).text(MessageResolver("label.created.date","Created at: ") + new Date(notification.created).toLocaleString(locale,options));
	
	if(notification.startDate)
		$("[data-role='startDate']", $panel).text(MessageResolver("label.notification.date.from","From at: ") + new Date(notification.startDate).toLocaleString(locale,options));
	else $("[data-role='startDate']", $panel).text(MessageResolver("label.notification.date.from","From at: ") + "-");
	
	if(notification.endDate)
		$("[data-role='endDate']", $panel).text(MessageResolver("label.notification.date.until","Until: ") + new Date(notification.endDate).toLocaleString(locale,options));
	else $("[data-role='endDate']", $panel).text(MessageResolver("label.notification.date.until","Until: ") + "-");
	
	for ( var lang in notification.messages) {
		var $language = $("[lang='"+lang+"']", $body);
		if($language.length)
			$language.text(notification.messages[lang]);
	}
	
	$current.appendTo($container);
	
	return false;
}

function clearNotification(e){
	var $confirmModal = showDialog("#confirm-dialog", MessageResolver("confirm.clear.notification", "Are you sure, you want to clear notification?"));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function(e) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Admin/Notification/Clear",
			type : "delete",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if(response.success){
					$("#notification-content").empty();
					showDialog("success", response.success);
				}else if(response.error)
					showDialog("error", response.error);
				else unknowError();
			},
			error : unknowError
		}).complete(() => {
			$progress.hide();
		});
	});
	return false;
}

function deleteNotification(id){
	var $confirmModal = showDialog("#confirm-dialog", MessageResolver("confirm.delete.notification", "Are you sure, you want to delete this notification?"));
	$confirmModal.find(".modal-footer>button[name='yes']").one("click", function(e) {
		var $progress = $("#loading-indicator").show();
		$.ajax({
			url : context + "/Admin/Notification/"+id+"/Delete",
			type : "delete",
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if(response.success){
					$('#notification-content div[data-role="notification"][data-trick-id="'+id+'"]').remove();
					showDialog("success", response.success);
				}else if(response.error)
					showDialog("error", response.error);
				else unknowError();
			},
			error : unknowError
		}).complete(() => {
			$progress.hide();
		});
	});
	return false;
}

function notificationForm(e, id){
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + (id? "/Admin/Notification/"+id+"/Edit" :"/Admin/Notification/Add" ) ,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			 $view = $("#modal-add-notification",new DOMParser().parseFromString(response, "text/html"));
			 if(!$view.length)
				 unknowError();
			 else {
				 $view.appendTo("#widget");
				 $view.modal("show").on('hidden.bs.modal', () => $view.remove());
				 $("button[name='save']", $view).on("click",saveNotification);
			 }
			return false;
		},
		error : unknowError
	}).complete(() => {
		$progress.hide();
	});

	return false;
}


function parseDate(date, time){
	if(date.length && time.length)
		return new Date(date+"T"+time);
	else if(date.length)
		return new Date(date+"T00:00");
	else{
		var current = new Date(), times = time.split(":");
		if(times.length!=2)
			return undefined;
		current.setHours(times[0]);
		current.setMinutes(times[1])
		return current;
	}
}

function saveNotification(e){
	var $progress = $("#loading-indicator").show(), $view = $("#modal-add-notification"), $form = $("form", $view), data = $form.serializeJSON(), keys = Object.keys(data);
	
	data["messages"]={};
	
	for (let key of keys) {
		if(key.startsWith("messages[")){
			data["messages"][key.replace(/messages\[|\]/g,'')] = data[key];
			delete data[key];
		}
	}
	
	data.startDate =  parseDate(data.startDate, data.startDateTime);
	data.endDate = parseDate(data.endDate, data.endDateTime);
	
	if(!data.startDate || typeof data.startDate ==="invalid date")
		delete data["startDate"];
	
	if(!data.endDate || typeof data.endDate ==="invalid date")
		delete data["endDate"];
	
	delete data["startDateTime"];
	
	delete data["endDateTime"];
	
	$.ajax({
		url : context + "/Admin/Notification/Save",
		type : "post",
		data : JSON.stringify({data:data}),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response.error)
				showDialog("error", response.error);
			else if (response.length == 2){
				$view.modal("hide");
				showDialog("success", response[0]);
				insertOrUpdateNotification(response[1]);
			}
			else unknowError();
			return false;
		},
		error : unknowError
	}).complete(() => {
		$progress.hide();
	});
}


function manageCustomerAccess(customerID) {
	if (!isNotCustomerProfile())
		return false;
	if (customerID == null || customerID == undefined) {
		var selectedScenario = findSelectItemIdBySection("section_customer");
		if (selectedScenario.length != 1)
			return false;
		customerID = selectedScenario[0];
	}
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Admin/Customer/" + customerID + "/Manage-access",
		type: "get",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			var $view = $(new DOMParser().parseFromString(response, "text/html")).find("#manageCustomerUserModel");
			if ($view.length) {
				$view.appendTo("#widget").modal("show").on("hidden.bs.modal", () => $view.remove());
				$("button[name='save']", $view).on("click" , e => updateCustomerAccess(e,$view,$progress,customerID));
			} else
				unknowError();
			return false;
		},
		error: unknowError
	}).complete(() => $progress.hide());
	return false;
}

function isNotCustomerProfile() {
	return isCustomerProfile();
}

function isCustomerProfile() {
	return !isProfile("#section_customer");
}

function updateCustomerAccess(e,$view,$progress,customerID) {
	var data = {};
	$view.find(".form-group[data-trick-id][data-default-value]").each(function () {
		var $this = $(this), newRight = $this.find("input[type='radio']:checked").val(), oldRight = $this.attr("data-default-value");
		if (newRight != oldRight)
			data[$this.attr("data-trick-id")] = newRight;
	});
	if (Object.keys(data).length) {
		$progress.show();
		$.ajax({
			url: context + "/Admin/Customer/" + customerID + "/Manage-access/Update",
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
	}
}
