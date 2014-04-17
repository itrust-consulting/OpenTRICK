var el = null;

$(document).ready(function() {

	// ******************************************************************************************************************
	// * load charts
	// ******************************************************************************************************************
	// reloadCharts();

	// ******************************************************************************************************************
	// * uncheck checked checkboxes
	// ******************************************************************************************************************

	$("input[type='checkbox']").removeAttr("checked");

	// ******************************************************************************************************************
	// * fixed header tables
	// ******************************************************************************************************************

	$("div[class='panel-body panelbodydefinition']").click(function() {
		initialisefixheadertables($(this).find("table:visible"));
	});

	$("div[class='panel-body panelbodydefinition']").scroll(function() {

		// initialisefixheadertables($(this).find("table:visible"));
	});

	// ******************************************************************************************************************
	// * measure description in popover
	// ******************************************************************************************************************

	initmeasuredescriptionpopover();

});
function loadDefaultParameter(idAnalysis) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Parameter/Load/Default",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["success"] != undefined) {
					$("#alert-dialog .modal-body").html(response["success"]);
					setTimeout(function() {
						location.reload();
					}, 3000);
				} else if (response["error"] != undefined)
					$("#alert-dialog .modal-body").html(response["error"]);
				else
					$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");

				return true;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
			}
		});
	} else
		permissionError();
	return false;
}
function loadDefaultRiskInformation(idAnalysis) {
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/RiskInformation/Load/Default",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response["success"] != undefined){
					$("#alert-dialog .modal-body").html(response["success"]);
					setTimeout(function() {
						location.reload();
					}, 3000);
				}
				else if (response["error"] != undefined)
					$("#alert-dialog .modal-body").html(response["error"]);
				else
					$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
				return true;
			},
			error : function(jqXHR, textStatus, errorThrown) {
				$("#alert-dialog .modal-body").text(MessageResolver("error.unknown.save.data", "An unknown error occurred during processing"));
				$("#alert-dialog").modal("toggle");
			}
		});
	} else
		permissionError();
	return false;
}

function resetfixedheadertables() {
	// console.log("ola");
	// $("div#section_actionplans").find("table:visible").
	$(parent).fixedHeaderTable('destroy');
	var parentt = $(parent).parent();
	$(parentt).find("table:visible").not("[id]").remove();
	initialisefixheadertables(parent);
	return false;
}

function initialisefixheadertables(parent) {
	// console.log(parent);
	if (parent.length !== 0) {
		// initialise fixedheader table with parameters
		// $(parent).fixedHeaderTable("destroy");
		$(parent).fixedHeaderTable({
			footer : false,
			cloneHeadToFoot : false,
			fixedColumn : false,
			width : "100%",
			themeClass : 'table table-hover'
		});

		// first data row has wrong margin top
		// $('.headertofixtable').css("margin-top", "-49px");

		// remove small scrolling which causes scrolling inside panel
		$('div [class="fht-table-wrapper table table-hover"]').css("margin", "0");
		$('div [class="fht-table-wrapper table table-hover"]').css("padding", "0");
	}
}

function initmeasuredescriptionpopover() {
	// tooltip / popover click on reference
	$('.descriptiontooltip').click(function() {

		// check if the same reference had been clicked to hide
		if (el != null && el.attr("data-original-title") != $(this).attr("data-original-title")) {

			// hide opened popover
			el.popover("hide");

			// set watcher to null (no popover active)
			el = null;
		}

		// set current popover as active
		el = $(this);

		// initialise popover and toggle visablility
		$(this).popover({
			trigger : 'manual',
			placement : 'bottom',
			html : true,
			container : ".panelbodydefinition"
		}).popover('toggle');

		// avoid scroll top
		return false;
	});

	// when table is scrolled, hide popover
	$("div [class='fht-tbody']").scroll(function() {
		// console.log("ohe");

		// check if a popover is active
		if (el != null) {

			// hide popover
			el.popover('hide');

			// remove popover from dom (to avoid unclickable references)
			$('.popover').remove();

			// set watcher to null (no popover is active)
			el = null;
		}
	});
}