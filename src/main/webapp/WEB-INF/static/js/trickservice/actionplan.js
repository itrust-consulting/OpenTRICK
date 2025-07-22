
/**
 * Displays the action plan options by making an AJAX request to the server.
 * @returns {boolean} Returns false to prevent the default form submission behavior.
 */
function displayActionPlanOptions() {
	$.ajax({
		url: context + "/Analysis/ActionPlan/ComputeOptions",
		type: "GET",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $content = $(new DOMParser().parseFromString(response, "text/html")).find("#actionplancomputeoptions");
			if ($content.length)
				new Modal($content.clone()).Show();
			else
				unknowError();
		},
		error: unknowError
	});
	return false;
}

/**
 * Calculates the action plan with the given options.
 *
 * @param {string} form - The ID of the form element.
 * @returns {number[]} - An array of calculated action data.
 */
function calculateActionPlanWithOptions(form) {
	let $form = $("#" + form), data = [];
	let length = 'standard_'.length;
	$form.find("input[name^='standard_']").filter((i,e) => e.checked).each(function () {
		data.push(parseInt(this.name.substring(length)));
	});
	return calculateAction(data);
}

/**
 * Calculates the action plan based on the provided data.
 * 
 * @param {Array} data - The data used for calculating the action plan.
 * @returns {boolean} - Returns false.
 */
function calculateAction(data) {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/ActionPlan/Compute",
		type: "post",
		data: JSON.stringify(data || []),
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				application["taskManager"].SetTitle(MessageResolver("title.actionplan.compute", "Compute Action Plan")).Start();
			else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
	return false;
}

/**
 * Hides the action plan assets in the specified section and updates the menu link text.
 * 
 * @param {string} sectionactionplan - The selector for the section containing the action plan.
 * @param {string} menu - The selector for the menu containing the action plan assets link.
 * @returns {boolean} - Returns false.
 */
function hideActionplanAssets(sectionactionplan, menu) {
	let actionplantype = $(sectionactionplan).find(".disabled[data-trick-nav-control]").attr("data-trick-nav-control");
	if (!$("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
		$(menu + " a#actionplanassetsmenulink").html(
			"<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
	}
	return false;

}

/**
 * Reloads an action plan entry row.
 *
 * @param {number} idActionPlanEntry - The ID of the action plan entry.
 * @param {string} type - The type of the action plan entry.
 * @param {number} idMeasure - The ID of the measure.
 * @param {string} standard - The standard.
 * @returns {boolean} - Returns false.
 */
function reloadActionPlanEntryRow(idActionPlanEntry, type, idMeasure, standard) {
	$.ajax({
		url: context + "/Analyis/ActionPlan/RetrieveSingleEntry/" + idActionPlanEntry,
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $content = $(new DOMParser().parseFromString(response, "text/html")).find("tr[data-trick-id='" + idActionPlanEntry + "']");
			if ($content.length)
				$("#section_actionplan_" + type + " tr[data-trick-id='" + idActionPlanEntry + "']").replaceWith($content);
			else
				unknowError();
		},
		error: unknowError
	});
	reloadMeasureRow(idMeasure, standard);
	return false;
}

/**
 * Displays the action plan assets.
 * @returns {boolean} Returns false.
 */
function displayActionPlanAssets() {
	let $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/ActionPlan/Assets",
		data: {
			'selectedApt': $("#menu_actionplan>li.disabled[data-trick-nav-control]").attr('data-trick-nav-control')
		},
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			let $content = $(new DOMParser().parseFromString(response, "text/html")).find("#actionPlanAssets");
			if ($content.length) {
				let $oldView = $("#actionPlanAssets");
				if (!$oldView.length)
					$content.appendTo($("#widgets"))
				else
					$oldView.replaceWith($content);
				let $body = $content.find(".modal-body");
				let resizer = function () {
					let height = $(window).height();
					let multi = height < 200 ? 0.50 : height < 520 ? 0.60 : height < 600 ? 0.65 : height < 770 ? 0.72 : height < 820 ? 0.77 : height < 900 ? 0.78 : 0.80;
					$body.css({
						'max-height': (height * multi) + 'px',
						'overflow': 'auto'
					});
				}
				$(window).on('resize.actionPlanAssets', resizer);
				resizer.apply(resizer, null);
				$content.find(".modal").on("hidden.bs.modal", function () {
					$(window).off('resize.actionPlanAssets', resizer)
				});
				$content.modal("show");
			} else
				unknowError();
		},
		error: unknowError,
		complete: function () {
			$progress.hide();
		}
	});
	return false;
}