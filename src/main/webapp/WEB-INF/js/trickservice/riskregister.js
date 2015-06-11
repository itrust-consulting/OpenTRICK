function calculateRiskRegister(analysisId) {

	var analysisID = -1;

	if (analysisId == null || analysisId == undefined) {

		var selectedAnalysis = findSelectItemIdBySection("section_analysis");
		if (!selectedAnalysis.length)
			return false;
		while (selectedAnalysis.length) {
			rowTrickId = selectedAnalysis.pop();
			if (userCan(rowTrickId, ANALYSIS_RIGHT.READ)) {
				analysisID = rowTrickId;
			} else
				permissionError();
		}

	} else {
		analysisID = analysisId;
	}

	if (userCan(analysisID, ANALYSIS_RIGHT.READ)) {
		$.ajax({
			url : context + "/Analysis/RiskRegister/Compute",
			type : "post",
			data : JSON.stringify({id:analysisID }),
			async : true,
			contentType : "application/json;charset=UTF-8",
			success : function(response, textStatus, jqXHR) {
				if (response["success"] != undefined)
					new TaskManager().Start();
				else if (message["error"]) {
					$("#alert-dialog .modal-body").html(message["error"]);
					$("#alert-dialog").modal("toggle");
				}
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function riskRegisterSwitchData(element) {
	var $li = $(element);
	if ($li.hasClass("disabled"))
		return false;
	var type = $li.attr("data-trick-role");
	var tds = $("#section_riskregister table>tbody td[data-scale-value]");
	for (var i = 0; i < tds.length; i++) {
		if (type == "menu-risk-register-control-value")
			$(tds[i]).text($(tds[i]).attr("data-scale-value"));
		else
			$(tds[i]).text($(tds[i]).attr("data-scale-level"));
	}
	var $li =  $("li[data-trick-role^='menu-risk-register-control']:not(.disabled)");
	$("li[data-trick-role^='menu-risk-register-control']").removeClass("disabled");
	$li.addClass("disabled");
	$(window).resize();
	$(window).scroll();
	return false;
}