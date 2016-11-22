function displayActionPlanOptions(analysisId) {
	$.ajax({
		url : context + "/Analysis/ActionPlan/ComputeOptions",
		type : "GET",
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#actionplancomputeoptions");
			if ($content.length)
				new Modal($content.clone()).Show();
			else
				unknowError();
		},
		error : unknowError
	});
	return false;
}

function calculateActionPlanWithOptions(form) {
	var $form = $("#" + form), data = {
		"id" : $form.find("input[name='id']").val()
	};
	$form.find("input[name^='standard_']").each(function() {
		data[this.name] = this.checked;
	});
	return calculateAction(data);
}

function calculateAction(data) {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/ActionPlan/Compute",
		type : "post",
		data : JSON.stringify(data),
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				application["taskManager"].SetTitle(MessageResolver("title.actionplan.compute", "Compute Action Plan")).Start();
			else if (response["error"])
				showDialog("#alert-dialog", response['error']);
			else
				unknowError();
		},
		error : unknowError
	}).complete(function() {
		$progress.hide();
	});
	return false;
}

function hideActionplanAssets(sectionactionplan, menu) {
	var actionplantype = $(sectionactionplan).find(".disabled[data-trick-nav-control]").attr("data-trick-nav-control");
	if (!$("#actionplantable_" + actionplantype + " .actionplanasset").hasClass("actionplanassethidden")) {
		$("#actionplantable_" + actionplantype + " .actionplanasset").toggleClass("actionplanassethidden");
		$(menu + " a#actionplanassetsmenulink").html(
				"<span class='glyphicon glyphicon-chevron-down'></span>&nbsp;" + MessageResolver("action.actionplanassets.show", "Show Assets"));
	}
	return false;

}

function reloadActionPlanEntryRow(idActionPlanEntry, type, idMeasure, standard) {
	$.ajax({
		url : context + "/Analyis/ActionPlan/RetrieveSingleEntry/" + idActionPlanEntry,
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("tr[data-trick-id='" + idActionPlanEntry + "']");
			if ($content.length)
				$("#section_actionplan_" + type + " tr[data-trick-id='" + idActionPlanEntry + "']").replaceWith($content);
			else
				unknowError();
		},
		error : unknowError
	});
	reloadMeasureRow(idMeasure, standard);
	return false;
}

function displayActionPlanAssets() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url : context + "/Analysis/ActionPlan/Assets",
		data : {
			'selectedApt' : $("#menu_actionplan>li.disabled[data-trick-nav-control]").attr('data-trick-nav-control')
		},
		contentType : "application/json;charset=UTF-8",
		success : function(response, textStatus, jqXHR) {
			var $content = $(new DOMParser().parseFromString(response, "text/html")).find("#actionPlanAssets");
			if ($content.length) {
				var $oldView = $("#actionPlanAssets");
				if (!$oldView.length)
					$content.appendTo($("#widgets"))
				else
					$oldView.replaceWith($content);
				var $body = $content.find(".modal-body");
				var resizer = function() {
					var height = $(window).height();
					var multi = height < 200 ? 0.50 : height < 520 ? 0.60 : height < 600 ? 0.65 : height < 770 ? 0.72 : height < 820 ? 0.77 : height < 900 ? 0.78 : 0.80;
					$body.css({
						'max-height' : (height * multi) + 'px',
						'overflow' : 'auto'
					});
				}
				$(window).on('resize.actionPlanAssets', resizer);
				resizer.apply(resizer, null);
				$content.find(".modal").on("hidden.bs.modal", function() {
					$(window).off('resize.actionPlanAssets', resizer)
				});
				$content.modal("show");
			} else
				unknowError();
		},
		error : unknowError,
		complete : function() {
			$progress.hide();
		}
	});
	return false;
}