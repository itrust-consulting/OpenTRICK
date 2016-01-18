function displayParameters(name, title) {
	var view = new Modal();
	view.Intialise();
	$(view.modal_footer).remove();
	view.setTitle(title);
	view.setBody($(name).find(".panel-body").html());
	$(view.modal_body).find("td").removeAttributes();
	view.Show();
	return false;
}

function EstimationHelper(name, id) {
	this.name = name;
	this.id = id;
	this.navSelector = "tr[data-trick-selected='true']:first";
}

EstimationHelper.prototype = {

	tabMenu : function() {
		return $("li[data-menu='estimation'][data-type='" + this.name + "']");
	},
	tabContent : function() {
		return $("li[data-menu='estimation'][data-type='" + this.name + "']>a").attr("href");
	},
	hiddenTabMenu : function() {
		return $("li[data-menu='estimation'][data-type!='" + this.name + "']");
	},
	hiddenTabContent : function() {
		return $("li[data-menu='estimation'][data-type!='" + this.name + "']>a").attr("href");
	},
	section : function() {
		return "#" + this.sectionName();
	},
	sectionName : function() {
		if (this.name === "asset")
			return "section_asset_assessment";
		if (this.name === "scenario")
			return "section_scenario_assessment";
		throw "Unsupported name";
	},

	loadUrl : function() {
		if (this.name === "asset")
			return context + "/Analysis/Assessment/Asset/" + this.id;
		if (this.name === "scenario")
			return context + "/Analysis/Assessment/Scenario/" + this.id;
		throw "Unsupported name";
	},
	error : function(message) {
		$("#alert-dialog .modal-body").text(message);
		return $("#alert-dialog").modal("show")
	},
	updateNav : function() {
		var $current = $(this.current()), $section = $(this.tabContent()), $next = $current.nextAll(this.navSelector), $prev = $current.prevAll(this.navSelector), $nextNav = $(
				"ul.nav>li[data-role='nav-next']", $section), $prevNav = $("ul.nav>li[data-role='nav-prev']", $section);
		if ($next.length)
			$nextNav.removeClass("disabled");
		else
			$nextNav.addClass("disabled");

		if ($prev.length)
			$prevNav.removeClass("disabled");
		else
			$prevNav.addClass("disabled");
		return this;

	},
	updateUrl : function() {
		return this.loadUrl() + "/Update";
	},
	updateContent : function(content) {
		var section = this.section(), $section = $(section), $content = $(section, new DOMParser().parseFromString(content, "text/html"));
		if ($content.length) {
			if ($section.attr("data-trick-id") != this.id || this.SmartUpdate($content)) {
				var $tbody = $section.find("tbody");
				if ($tbody.length)
					$tbody.replaceWith($content.find("tbody"));
				else {
					$section.replaceWith($content);
					fixTableHeader($("table", $content));
				}
				$("h3[role='title']", this.tabContent()).text($content.attr("data-trick-name"));
			}
		} else
			unknowError();
		return false;
	},
	current : function() {
		return $("tbody input:checked", "#section_" + this.name).closest("tr[data-trick-selected='true']");
	},
	hasNext : function() {
		return $(this.current()).nextAll(this.navSelector).length > 0;
	},
	hasPrev : function() {
		return $(this.current()).prevAll(this.navSelector).length > 0;
	},
	next : function() {
		var $current = $(this.current()), $next = $current.nextAll(this.navSelector);
		if ($next.length) {
			$current.find("input:checked").prop("checked", false).trigger("change");
			$next.find("input[type='checkbox']").prop("checked", true).trigger("change");
		}
		return this;

	},
	prev : function() {
		var $current = $(this.current()), $prev = $current.prevAll(this.navSelector);
		if ($prev.length) {
			$current.find("input:checked").prop("checked", false).trigger("change");
			$prev.find("input[type='checkbox']").prop("checked", true).trigger("change");
		}
		return this;
	},
	swithTo : function(id, name) {
		if (id == this.id && name == this.name)
			return this.update();
		else
			return this.select(id, name, true).load();

	},
	planReload : function() {
		$(this.tabContent()).attr("data-update-required", true).attr("data-trigger", 'showEstimation').attr("data-parameters", [ this.name, this.id ]);
		return this;
	},
	select : function(id, name, noUpdate) {
		this.id = id;
		this.name = name;
		if (!noUpdate)
			this.planReload();
		return this;
	},
	display : function() {
		$(this.tabMenu()).show()
		$(this.hiddenTabMenu()).hide();
		return this;
	},
	show : function() {
		this.display();
		$("a[data-toggle='tab']", this.tabMenu()).tab("show");
		return this;
	},
	load : function() {
		var instance = this;
		$.ajax({
			url : instance.loadUrl(),
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				return instance.updateContent(reponse);
			},
			error : unknowError
		});
		return this;
	},
	update : function() {
		var instance = this;
		$.ajax({
			url : instance.updateUrl(),
			contentType : "application/json;charset=UTF-8",
			async : false,
			success : function(reponse) {
				return instance.updateContent(reponse);
			},
			error : unknowError
		});
		return this;
	},
	SmartUpdate : function(assessments) {
		var $section = $(this.section()), tableDestTrs = $("tbody tr", $section);
		if (!(tableDestTrs.length && $("td", tableDestTrs[0]).length == $("tbody>tr:first>td", assessments).length))
			return true;
		for (var i = 0; i < tableDestTrs.length; i++) {
			var trickId = $(tableDestTrs[i]).attr("data-trick-id");
			if (trickId == undefined && $(tableDestTrs[i]).hasClass("panel-footer")) {
				var $tr = $(assessments).find("tbody tr.panel-footer");
				if ($tr.length)
					$(tableDestTrs[i]).replaceWith($tr);
				else
					$(tableDestTrs[i]).appendTo($("tbody", $section));
			} else {
				var $tr = $(assessments).find("tbody tr[data-trick-id='" + trickId + "']");
				if (!$tr.length)
					$(tableDestTrs[i]).remove();
				else
					$(tableDestTrs[i]).replaceWith($tr);
			}
		}
		var $tbody = $("tbody", $section);
		var $footer = $("tbody tr.panel-footer", $section);
		if (!$footer.length) {
			$footer = $("tbody tr.panel-footer", assessments);
			if ($footer.length)
				$footer.appendTo($tbody);
		}
		var tableSourceTrs = $("tbody tr[data-trick-id]", assessments);
		for (var i = 0; i < tableSourceTrs.length; i++) {
			var trickId = $(tableSourceTrs[i]).attr("data-trick-id");
			var $tr = $("tbody tr[data-trick-id='" + trickId + "']", $section);
			if (!$tr.length) {
				if ($footer.length)
					$tr.before($footer);
				else
					$tr.appendTo($tbody);
			}
		}
		return false;
	}
}

function nextSelected() {
	var helper = application["estimation-helper"];
	if (helper)
		helper.next();
	return false;
}

function prevSelected() {
	var helper = application["estimation-helper"];
	if (helper)
		helper.prev();
	return false;
}

function showEstimation(name) {
	if (name == undefined)
		name = $("#section_asset").is(":visible") ? "asset" : $("#section_scenario").is(":visible") ? "scenario" : undefined;
	if (name) {
		var selectedItem = findSelectItemIdBySection("section_" + name);
		if (selectedItem.length == 1 && isSelected(name)) {
			var helper = application["estimation-helper"];
			if (helper === undefined)
				application["estimation-helper"] = helper = new EstimationHelper();
			helper.swithTo(selectedItem[0], name).show();
		}
	}
	return false;
}

function showTabEstimation(name) {
	var selectedItem = findSelectItemIdBySection("section_" + name);
	if (selectedItem.length == 1 && isSelected(name)) {
		var helper = application["estimation-helper"];
		if (helper === undefined)
			application["estimation-helper"] = helper = new EstimationHelper();
		helper.select(selectedItem[0], name).updateNav();
		if ($(helper.tabContent()).is(":visible"))
			$(helper.update().tabMenu()).show();
		else
			helper.display();
	} else
		$("li[data-menu='estimation'][data-type]").hide();
	return false;
}

function computeAssessment(silent) {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Assessment/Update",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response['error'] != undefined) {
					$("#info-dialog .modal-body").text(response['error']);
					$("#info-dialog").modal("toggle");
				} else if (response['success'] != undefined) {
					if (!silent) {
						$("#info-dialog .modal-body").text(response['success']);
						$("#info-dialog").modal("toggle");
					}
					chartALE();
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	} else
		permissionError();
	return false;
}

function refreshAssessment() {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$("#confirm-dialog .modal-body").html(MessageResolver("confirm.refresh.assessment", "Are you sure, you want to rebuild all assessments"));
		$("#confirm-dialog .btn-danger").click(function() {
			$.ajax({
				url : context + "/Analysis/Assessment/Refresh",
				type : "get",
				contentType : "application/json;charset=UTF-8",
				async : true,
				success : function(response, textStatus, jqXHR) {
					if (response['error'] != undefined) {
						$("#info-dialog .modal-body").text(response['error']);
						$("#info-dialog").modal("toggle");
					} else if (response['success'] != undefined) {
						$("#info-dialog .modal-body").text(response['success']);
						$("#info-dialog").modal("toggle");
						chartALE();
					} else
						unknowError();
					return false;
				},
				error : unknowError
			});
		});
		$("#confirm-dialog").modal("show");
	} else
		permissionError();
	return false;
}

function updateAssessmentAle(silent) {
	idAnalysis = $("*[data-trick-rights-id][data-trick-id]").attr("data-trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.MODIFY)) {
		$.ajax({
			url : context + "/Analysis/Assessment/Update/ALE",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			async : true,
			success : function(response, textStatus, jqXHR) {
				if (response['error'] != undefined) {
					$("#info-dialog .modal-body").text(response['error']);
					$("#info-dialog").modal("toggle");
				} else if (response['success'] != undefined) {
					if (!silent) {
						$("#info-dialog .modal-body").text(response['success']);
						$("#info-dialog").modal("toggle");
					}
				} else
					unknowError();
				return false;
			},
			error : unknowError
		});
	} else
		permissionError();

	return false;
}
