function calculateRiskRegister() {
	var $progress = $("#loading-indicator").show();
	$.ajax({
		url: context + "/Analysis/RiskRegister/Compute",
		type: "post",
		contentType: "application/json;charset=UTF-8",
		success: function (response, textStatus, jqXHR) {
			if (response["success"] != undefined)
				new TaskManager().Start();
			else if (response["error"])
				showDialog("#alert-dialog",response["error"] );
			else
				unknowError()
		},
		error: unknowError
	}).complete(function () {
		$progress.hide();
	});
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
	var $li = $("li[data-trick-role^='menu-risk-register-control']:not(.disabled)");
	$("li[data-trick-role^='menu-risk-register-control']").removeClass("disabled");
	$li.addClass("disabled");
	$(window).resize();
	$(window).scroll();
	return false;
}