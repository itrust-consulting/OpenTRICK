var el = null;

$(document).ready(function() {

	//******************************************************************************************************************
	// * load charts
	//******************************************************************************************************************
	reloadCharts();

	//******************************************************************************************************************
	// * uncheck checked checkboxes
	//******************************************************************************************************************
	
	$("input[type='checkbox']").removeAttr("checked");

	//******************************************************************************************************************
	// * fixed header tables
	//******************************************************************************************************************
	
	// initialise fixedheader table with parameters
	$('.headertofixtable').fixedHeaderTable({
		footer : false,
		cloneHeadToFoot : false,
		fixedColumn : false,
		width : "100%",
		themeClass : 'table table-hover'
	});
	
	// first data row has wrong margin top
	$('#table_SOA_27002').css("margin-top", "-49px");
	
	// remove small scrolling which causes scrolling inside panel
	$('div [class="fht-table-wrapper table table-hover"]').css("margin", "0");
	$('div [class="fht-table-wrapper table table-hover"]').css("padding", "0");

	//******************************************************************************************************************
	// * measure description in popover
	//******************************************************************************************************************
	
	
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
			container: ".panelbodydefinition"
		}).popover('toggle');
		
		// avoid scroll top
		return false;
	});

	// when table is scrolled, hide popover
	$("div [class='fht-tbody']").scroll(function() {
		//console.log("ohe");
		
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
});