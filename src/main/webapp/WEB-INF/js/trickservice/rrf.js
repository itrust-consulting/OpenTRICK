RRFView.prototype = new Modal();

function RRFView() {

	this.controller = {};

	this.controllers = {};

	this.chart = {};

	this.filter = {
		series : []
	};

	RRFView.prototype.constructor = RRFView;

	RRFView.prototype.Intialise = function() {
		Modal.prototype.Intialise.call(this);
		$(this.modal_dialog).attr("style", "width: 98%; min-width:1170px;");
		$(this.modal_body).attr("style", "max-height: 885px;");
		$(this.modal_footer).remove();
		return false;
	};

	RRFView.prototype.UpdateChartView = function(response) {
		if (response.chart != null && response.chart != undefined)
			this.chart = $($(this.modal_body).find("#chart_rrf").highcharts(response)).highcharts();
		else if (response.error != undefined) {
			$(this.modal_body).find("#chart_rrf").html('<div style="width: 1151px; height: 400px; padding-top: 12%"></div>');
			showError($(this.modal_body).find("#chart_rrf div")[0], response.error);
		}
		return false;
	};

	RRFView.prototype.UpdateChart = function(fiedName, value) {
		return this.controller.UpdateChart(fiedName, value);
	};

	RRFView.prototype.ForceSelectOneFirstItem = function(controller) {
		for ( var i in this.controllers)
			if (this.controllers[i] != controller)
				this.controllers[i].SelectFirstItem();
		return false;
	};

	RRFView.prototype.SwitchController = function(controller) {
		if (this.controller == controller)
			return true;
		else if (controller == null || controller == undefined)
			return false;
		if (this.controllers[controller.name] != controller)
			this.controllers[controller.name] = controller;
		if (this.controller != undefined && Object.keys(this.controller).length)
			this.controller.Hide();
		this.controller = controller;
		this.controller.Show();
		return true;
	};

	RRFView.prototype.ReloadChart = function() {
		return this.controller.ReloadChart();
	};

	RRFView.prototype.LoadData = function() {
		var that = this;
		$.ajax({
			url : context + "/Scenario/RRF",
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				var parser = new DOMParser();
				var doc = parser.parseFromString(response, "text/html");
				newSection = $(doc).find("*[id ='section_rrf']");
				that.setBody($(newSection)[0].outerHTML);
				// initialise controllers
				that.controllers = {
					"scenario" : new ScenarioRRFController(that, $(that.modal_body).find("#control_rrf_scenario"), "scenario"),
					"measure" : new MeasureRRFController(that, $(that.modal_body).find("#control_rrf_measure"), "measure")
				};
				
				for ( var controller in that.controllers)
					that.controllers[controller].Initialise();

				that.controllers["measure"].SelectFirstItem();
				that.SwitchController(that.controllers["measure"]);

				that.ReloadChart();
				return false;
			},error : unknowError
		});
		return true;
	};

	RRFView.prototype.GenerateFilter = function() {
		if (this.chart == undefined || this.chart.series == undefined)
			return;
		this.filter['series'] = [];
		for (var i = 0; i < this.chart.series.length; i++) {
			if (!this.chart.series[i].visible)
				this.filter['series'].push(this.chart.series[i].name);
		}
		return false;
	};

	RRFView.prototype.Onclick = function(element) {
		var parent = $(element).parent();
		$(parent).find(".list-group-item").removeClass("active");
		$(element).addClass("active");
		this.ReloadControls();
		return this.ReloadChart();
	};

	RRFView.prototype.DefaultFooterButton = function() {
		return false;
	};

	RRFView.prototype.OnSliderChange = function(event) {
		$(this.controller.container).find("#" + event.target.id + "_value").prop("value", event.value);
		return this.UpdateChart(event.target.name, event.value);
	};

	RRFView.prototype.Show = function() {
		try {
			if (this.modal_dialog == null || this.modal_dialog == undefined)
				this.Intialise();
			if ($(this.modal_body).empty() && !this.LoadData())
				return false;
			return Modal.prototype.Show.call(this);
		} catch (e) {
			console.log(e);
			return false;
		}
	};
}

function RRFController(rrfView, container, name) {

	this.rrfView = rrfView;

	this.container = container;

	this.name = name;

	this.sliders = {};

	RRFController.prototype.Initialise = function() {
		var that = this;
		var sliders = $(this.container).find(".slider");
		this.sliders = $(sliders).slider();
		this.sliders.on('slideStop', function(event) {
			return that.rrfView.OnSliderChange(event);
		});
	};

	RRFController.prototype.Show = function() {
		if (!Object.keys(this.sliders).length)
			this.Initialise();
		if (this.rrfView.controller != this)
			this.rrfView.SwitchController(this);
		$(this.container).show();
		this.ReloadControls();
		return false;
	};

	RRFController.prototype.Hide = function() {
		$(this.container).hide();
		return false;
	};

	RRFController.prototype.UpdateChart = function(fiedName, value) {
		return false;
	};

	RRFController.prototype.ReloadControls = function() {
		return false;
	};

	RRFController.prototype.ReloadChart = function() {
		false;
	};

	RRFController.prototype.GenerateFilter = function() {
		return this.rrfView.GenerateFilter();
	};

	RRFController.prototype.Onclick = function(element) {
		this.rrfView.SwitchController(this);
		var parent = $(element).parent();
		$(parent).find(".list-group-item").removeClass("active");
		$(element).addClass("active");
		this.rrfView.GenerateFilter();
		this.ReloadChart();
		return this.ReloadControls();
	};

	RRFController.prototype.OnClickFilter = function(event) {
		return this.rrfView.GenerateFilter();
	};
}

ScenarioRRFController.prototype = new RRFController();

function ScenarioRRFController(rrfView, container, name) {

	RRFController.call(this, rrfView, container, name);

	this.idScenario = -1;

	this.DependencyFields = {
		"preventive" : 0.0,
		"limitative" : 0.0,
		"detective" : 0.0,
		"corrective" : 0.0
	};

	ScenarioRRFController.prototype.constructor = ScenarioRRFController;

	ScenarioRRFController.prototype.Initialise = function() {
		var that = this;
		// controllerScenario
		$(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .list-group-item").on("click", function(event) {
			return that.Onclick(event.target);
		});

		// controllerMeasure
		$(that.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .list-group-item").on("click", function(event) {
			return that.OnClickFilter(event);
		});
		return RRFController.prototype.Initialise.call(this);
	};

	ScenarioRRFController.prototype.SelectFirstItem = function() {
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active");
		if (element.length == 1) {
			var item = $(element.parent().parent()).find("a[trick-class='Scenario']:first");
			$(item).addClass("active");
			this.idScenario = parseInt($(item).attr("trick-id"));
			this.rrfView.filter["scenarios"] = [ this.idScenario ];
		}
		return false;
	};

	ScenarioRRFController.prototype.CheckTypeValue = function() {
		var sum = 0;
		for ( var field in this.DependencyFields) 
			sum+=this.DependencyFields[field];
		var types = $(this.container).find("*[trick-type='type']");
		if (Math.abs(1-sum)>0.01) {
			$(types).removeClass("success");
			$(types).addClass("danger");
		} else {
			$(types).removeClass("danger");
			$(types).addClass("success");
		}
		//console.log(Math.abs(1-sum));
		//console.log(sum);
		return false;
	};

	ScenarioRRFController.prototype.UpdateChart = function(fiedName, value) {
		var that = this;
		if (this.idScenario < 1 || this.idScenario == undefined)
			this.idScenario = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
		if (this.DependencyFields[fiedName] != undefined){
			this.DependencyFields[fiedName] = value;
			this.CheckTypeValue();
		}
		$.ajax({
			url : context + "/Scenario/RRF/Update",
			type : "post",
			data : '{"id":' + that.idScenario + ', "fieldName":"' + fiedName + '", "value":' + value + ', "type": "numeric","filter":' + JSON.stringify(that.rrfView.filter) + '}',
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				return that.rrfView.UpdateChartView(response);
			},error : unknowError
		});
		return false;
	};

	ScenarioRRFController.prototype.ReloadControls = function() {
		var that = this;
		if (this.idScenario < 1 || this.idScenario == undefined)
			this.idScenario = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
		$.ajax({
			url : context + "/Scenario/" + that.idScenario,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response.scenarioType != null && response.scenarioType != undefined) {
					$(that.container).find(".slider").unbind("slideStop");
					for (var i = 0; i < that.sliders.length; i++) {
						var clone = $(that.sliders[i]).clone();
						var field = $(clone).prop("name");
						var fieldValue = response[field];
						if (fieldValue == undefined) {
							for (var j = 0; j < response.assetTypeValues.length; j++) {
								if (response.assetTypeValues[j].assetType.type == field) {
									fieldValue = response.assetTypeValues[j].value;
									break;
								}
							}
							if (fieldValue == undefined)
								continue;
						}
						if (that.DependencyFields[field] != undefined)//update preventive, limitative, detective and corrective
							that.DependencyFields[field] = fieldValue;
						$(that.container).find("#" + $(clone).prop("id") + "_value").prop("value", fieldValue);
						$(clone).attr("value", fieldValue);
						$(clone).attr("data-slider-value", fieldValue);
						$(that.sliders[i]).parent().replaceWith($(clone));
						that.sliders[i] = $(clone).slider();
						that.sliders[i].on("slideStop", function(event) {
							return that.rrfView.OnSliderChange(event);
						});
					}
					that.CheckTypeValue();
				}
				return false;
			},error : unknowError
		});
	};

	ScenarioRRFController.prototype.Onclick = function(element) {
		var trickClass = $(element).attr("trick-class");
		if (trickClass != "Scenario") {
			var item = $(element).attr("trick-class") == undefined ? $(element).parent() : $(element);
			$(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active").removeClass("active");
			$(item).addClass('active');
			return false;
		} else {
			var idScenario = $(element).attr("trick-id");
			if (idScenario != this.idScenario || this.rrfView.controller != this) {
				if (idScenario != this.idScenario) {
					var idScenarioType = $(element).parent().attr('trick-id');
					var scenarioType = $(element).parent().attr('trick-value');
					$(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active").removeClass("active");
					$(this.rrfView.modal_body).find(
							"#selectable_rrf_scenario_controls a[trick-class='ScenarioType'][trick-id='" + idScenarioType + "'][trick-value='" + scenarioType + "']").addClass(
							'active');
				} else if (!$(element).hasClass("active"))
					$(element).addClass("active");
				this.idScenario = idScenario;
				return RRFController.prototype.Onclick.call(this, element);
			} else if (!$(element).hasClass("active"))
				$(element).addClass("active");
		}
		return false;
	};

	ScenarioRRFController.prototype.GenerateFilter = function() {
		RRFController.prototype.GenerateFilter.call(this);
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active");
		if (element.length == 1) {
			this.rrfView.filter["measures"] = $.makeArray($(element.parent()).find("a[trick-class='Measure']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
		} else {
			this.rrfView.filter["measures"] = [];
			for (var i = 0; i < element.length; i++)
				this.rrfView.filter["measures"].push($(element[i]).attr('trick-id'));
		}
		return false;
	};

	ScenarioRRFController.prototype.OnClickFilter = function(event) {
		var element = $(event.target).attr("trick-class") == undefined ? $(event.target).parent() : $(event.target);
		var trickClass = $(element).attr("trick-class");
		var trickId = $(element).attr("trick-id");
		if (trickClass == "Norm") {
			this.rrfView.filter["measures"] = $.makeArray($(element).parent().find("a[trick-class='Measure']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
			this.SelectFirstItem();
			this.rrfView.SwitchController(this);
			return this.ReloadChart();
		}
		this.rrfView.filter["measures"] = [ parseInt(trickId) ];
		return false;
	};

	ScenarioRRFController.prototype.ReloadChart = function() {
		var that = this;
		if (this.idScenario < 1 || this.idScenario == undefined)
			this.idScenario = $(that.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active[trick-class='Scenario']").attr("trick-id");
		if (this.rrfView.filter == undefined || this.rrfView.filter.measures == undefined || !this.rrfView.filter.measures.length)
			this.GenerateFilter();
		$.ajax({
			url : context + "/Scenario/RRF/" + that.idScenario + "/Load",
			type : "post",
			data : JSON.stringify(that.rrfView.filter),
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				return that.rrfView.UpdateChartView(response);
			},error : unknowError
		});
	};
}

MeasureRRFController.prototype = new RRFController();

function MeasureRRFController(rrfView, container, name) {

	RRFController.call(this, rrfView, container, name);

	this.idMeasure = -1;

	this.FieldToCategory = {
		direct1 : "Direct1",
		direct2 : "Direct2",
		direct3 : "Direct3",
		direct4 : "Direct4",
		direct5 : "Direct5",
		direct6 : "Direct6",
		direct61 : "Direct6.1",
		direct62 : "Direct6.2",
		direct63 : "Direct6.3",
		direct64 : "Direct6.4",
		direct7 : "Direct7",
		indirect1 : "Indirect1",
		indirect2 : "Indirect2",
		indirect3 : "Indirect3",
		indirect4 : "Indirect4",
		indirect5 : "Indirect5",
		indirect6 : "Indirect6",
		indirect7 : "Indirect7",
		indirect8 : "Indirect8",
		indirect81 : "Indirect8.1",
		indirect82 : "Indirect8.2",
		indirect83 : "Indirect8.3",
		indirect84 : "Indirect8.4",
		indirect9 : "Indirect9",
		indirect10 : "Indirect10",
		confidentiality : "Confidentiality",
		integrity : "Integrity",
		availability : "Availability"
	};

	this.CategoryToField = {
		"Direct1" : "direct1",
		"Direct2" : "direct2",
		"Direct3" : "direct3",
		"Direct4" : "direct4",
		"Direct5" : "direct5",
		"Direct6" : "direct6",
		"Direct6.1" : "direct61",
		"Direct6.2" : "direct62",
		"Direct6.3" : "direct63",
		"Direct6.4" : "direct64",
		"Direct7" : "direct7",
		"Indirect1" : "indirect1",
		"Indirect2" : "indirect2",
		"Indirect3" : "indirect3",
		"Indirect4" : "indirect4",
		"Indirect5" : "indirect5",
		"Indirect6" : "indirect6",
		"Indirect7" : "indirect7",
		"Indirect8" : "indirect8",
		"Indirect8.1" : "indirect81",
		"Indirect8.2" : "indirect82",
		"Indirect8.3" : "indirect83",
		"Indirect8.4" : "indirect84",
		"Indirect9" : "indirect9",
		"Indirect10" : "indirect10",
		"Confidentiality" : "confidentiality",
		"Integrity" : "integrity",
		"Availability" : "availability"
	};

	MeasureRRFController.prototype.constructor = MeasureRRFController;

	MeasureRRFController.prototype.Initialise = function() {
		var that = this;
		// controllerScenario
		$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .list-group-item").on("click", function(event) {
			return that.Onclick(event.target);
		});

		// controllerMeasure
		$(that.rrfView.modal_body).find("#selectable_rrf_scenario_controls .list-group-item").on("click", function(event) {
			return that.OnClickFilter(event);
		});
		return RRFController.prototype.Initialise.call(this);
	};

	MeasureRRFController.prototype.SelectFirstItem = function() {
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active");
		if (element.length == 1) {
			var item = $(element.parent().parent()).find("a[trick-class='Measure']:first");
			$(item).addClass("active");
			this.idMeasure = parseInt($(item).attr("trick-id"));
			this.rrfView.filter["measures"] = [ this.idMeasure ];
		}
		return false;
	};

	MeasureRRFController.prototype.UpdateChart = function(fiedName, value) {
		var that = this;
		if (this.idMeasure < 1 || this.idMeasure == undefined)
			this.idMeasure = $(this.modal_body).find("#selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
		$.ajax({
			url : context + "/Measure/RRF/Update",
			type : "post",
			data : '{"id":' + that.idMeasure + ', "fieldName":"' + fiedName + '", "value":' + value + ', "type": "numeric","filter":' + JSON.stringify(that.rrfView.filter) + '}',
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				return that.rrfView.UpdateChartView(response);
			},error : unknowError
		});
	};

	MeasureRRFController.prototype.ReloadControls = function() {
		var that = this;
		if (this.idMeasure < 1 || this.idMeasure == undefined)
			this.idMeasure = $(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
		$.ajax({
			url : context + "/Measure/" + that.idMeasure,
			type : "get",
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				if (response.measurePropertyList != undefined && response.measurePropertyList != null) {
					// that.SynchronizeSlider(); rejected by product owner
					$(that.container).find(".slider").unbind("slideStop");
					for (var i = 0; i < that.sliders.length; i++) {
						var clone = $(that.sliders[i]).clone();
						var field = that.CategoryToField[$(clone).prop("name")] || $(clone).prop("name");
						var fieldValue = response.measurePropertyList[field];
						if (fieldValue == undefined) {
							for (var j = 0; j < response.assetTypeValues.length; j++) {
								if (response.assetTypeValues[j].assetType.type == field) {
									fieldValue = response.assetTypeValues[j].value;
									break;
								}
							}
							if (fieldValue == undefined)
								continue;
						}
						$(that.container).find("#" + $(clone).prop("id") + "_value").prop("value", fieldValue);
						$(clone).attr("value", fieldValue);
						$(clone).attr("data-slider-value", fieldValue);
						$(that.sliders[i]).parent().replaceWith($(clone));
						that.sliders[i] = $(clone).slider();
						that.sliders[i].on("slideStop", function(event) {
							return that.rrfView.OnSliderChange(event);
						});
					}
				}
				return false;
			},error : unknowError
		});
	};

	MeasureRRFController.prototype.Onclick = function(element) {
		var trickClass = $(element).attr("trick-class");
		if (trickClass != "Measure") {
			var item = $(element).attr("trick-class") == undefined ? $(element).parent() : $(element);
			$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active").removeClass("active");
			$(item).addClass('active');
			return false;
		} else {
			var idMeasure = $(element).attr("trick-id");
			if (idMeasure != this.idMeasure || this.rrfView.controller != this) {
				if (idMeasure != this.idMeasure) {
					var idNorm = $(element).parent().attr('trick-id');
					var chapter = $(element).parent().attr('trick-value');
					$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active").removeClass("active");
					$(this.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls a[trick-class='Norm'][trick-id='" + idNorm + "'][trick-value='" + chapter + "']")
							.addClass('active');
				} else if (!$(element).hasClass("active"))
					$(element).addClass("active");
				this.idMeasure = idMeasure;
				return RRFController.prototype.Onclick.call(this, element);
			} else if (!$(element).hasClass("active"))
				$(element).addClass("active");
		}
		return false;
	};

	MeasureRRFController.prototype.GenerateFilter = function() {
		RRFController.prototype.GenerateFilter.apply(this);
		var element = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active");
		if (element.length == 1) {
			this.rrfView.filter["scenarios"] = $.makeArray($(element.parent()).find("a[trick-class='Scenario']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
		} else {
			this.rrfView.filter["scenarios"] = [];
			for (var i = 0; i < element.length; i++)
				this.rrfView.filter["scenarios"].push($(element[i]).attr('trick-id'));
		}
		return false;
	};

	MeasureRRFController.prototype.SynchronizeSlider = function() {
		var category = $(this.rrfView.modal_body).find("#selectable_rrf_scenario_controls .active :first").attr("trick-value");
		$(this.container).find("*[trick-class='Category'][trick-value!='" + category + "']").hide();
		$(this.container).find("*[trick-class='Category'][trick-value='" + category + "']").show();
		return false;
	};

	MeasureRRFController.prototype.OnClickFilter = function(event) {
		var element = $(event.target).attr("trick-class") == undefined ? $(event.target).parent() : $(event.target);
		var trickClass = $(element).attr("trick-class");
		var trickId = $(element).attr("trick-id");
		// this.SynchronizeSlider(); rejected by product owner
		if (trickClass == "ScenarioType") {
			this.rrfView.filter["scenarios"] = $.makeArray($(element).parent().find("a[trick-class='Scenario']")).map(function(item) {
				return parseInt($(item).attr('trick-id'));
			});
			this.SelectFirstItem();
			this.rrfView.SwitchController(this);
			return this.ReloadChart();
		}
		this.rrfView.filter["scenarios"] = [ parseInt(trickId) ];
		return false;
	};

	MeasureRRFController.prototype.ReloadChart = function() {
		var that = this;
		if (this.idMeasure < 1 || this.idMeasure == undefined)
			this.idMeasure = $(that.rrfView.modal_body).find("#selectable_rrf_measures_chapter_controls .active[trick-class='Measure']").attr("trick-id");
		if (this.rrfView.filter == undefined || this.rrfView.filter.scenarios == undefined || !this.rrfView.filter.scenarios.length)
			this.GenerateFilter();
		$.ajax({
			url : context + "/Measure/RRF/" + that.idMeasure + "/Load",
			type : "post",
			data : JSON.stringify(that.rrfView.filter),
			contentType : "application/json;charset=UTF-8",
			success : function(response) {
				return that.rrfView.UpdateChartView(response);
			},error : unknowError
		});
	};
	return false;
}

function editRRF(idAnalysis) {
	if (idAnalysis == null || idAnalysis == undefined)
		idAnalysis = $("*[trick-rights-id][trick-id]").attr("trick-id");
	if (userCan(idAnalysis, ANALYSIS_RIGHT.READ)) {
		var modal = new RRFView();
		modal.Show();
	} else
		permissionError();
	return false;
}